package com.bumptech.glide.load.engine.cache;

import android.support.v4.util.Pools.Pool;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.util.LruCache;
import com.bumptech.glide.util.Util;
import com.bumptech.glide.util.pool.FactoryPools;
import com.bumptech.glide.util.pool.FactoryPools.Factory;
import com.bumptech.glide.util.pool.FactoryPools.Poolable;
import com.bumptech.glide.util.pool.StateVerifier;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SafeKeyGenerator {
    private final Pool<PoolableDigestContainer> digestPool = FactoryPools.threadSafe(10, new Factory<PoolableDigestContainer>() {
        public PoolableDigestContainer create() {
            try {
                return new PoolableDigestContainer(MessageDigest.getInstance("SHA-256"));
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }
    });
    private final LruCache<Key, String> loadIdToSafeHash = new LruCache(1000);

    private static final class PoolableDigestContainer implements Poolable {
        private final MessageDigest messageDigest;
        private final StateVerifier stateVerifier = StateVerifier.newInstance();

        PoolableDigestContainer(MessageDigest messageDigest) {
            this.messageDigest = messageDigest;
        }

        public StateVerifier getVerifier() {
            return this.stateVerifier;
        }
    }

    public String getSafeKey(Key key) {
        String safeKey;
        synchronized (this.loadIdToSafeHash) {
            safeKey = (String) this.loadIdToSafeHash.get(key);
        }
        if (safeKey == null) {
            safeKey = calculateHexStringDigest(key);
        }
        synchronized (this.loadIdToSafeHash) {
            this.loadIdToSafeHash.put(key, safeKey);
        }
        return safeKey;
    }

    private String calculateHexStringDigest(Key key) {
        PoolableDigestContainer container = (PoolableDigestContainer) this.digestPool.acquire();
        try {
            key.updateDiskCacheKey(container.messageDigest);
            String sha256BytesToHex = Util.sha256BytesToHex(container.messageDigest.digest());
            return sha256BytesToHex;
        } finally {
            this.digestPool.release(container);
        }
    }
}
