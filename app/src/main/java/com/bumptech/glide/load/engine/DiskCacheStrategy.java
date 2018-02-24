package com.bumptech.glide.load.engine;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.EncodeStrategy;

public abstract class DiskCacheStrategy {
    public static final DiskCacheStrategy ALL = new DiskCacheStrategy() {
        public boolean isDataCacheable(DataSource dataSource) {
            return dataSource == DataSource.REMOTE;
        }

        public boolean isResourceCacheable(boolean isFromAlternateCacheKey, DataSource dataSource, EncodeStrategy encodeStrategy) {
            return (dataSource == DataSource.RESOURCE_DISK_CACHE || dataSource == DataSource.MEMORY_CACHE) ? false : true;
        }

        public boolean decodeCachedResource() {
            return true;
        }

        public boolean decodeCachedData() {
            return true;
        }
    };
    public static final DiskCacheStrategy AUTOMATIC = new DiskCacheStrategy() {
        public boolean isDataCacheable(DataSource dataSource) {
            return dataSource == DataSource.REMOTE;
        }

        public boolean isResourceCacheable(boolean isFromAlternateCacheKey, DataSource dataSource, EncodeStrategy encodeStrategy) {
            return ((isFromAlternateCacheKey && dataSource == DataSource.DATA_DISK_CACHE) || dataSource == DataSource.LOCAL) && encodeStrategy == EncodeStrategy.TRANSFORMED;
        }

        public boolean decodeCachedResource() {
            return true;
        }

        public boolean decodeCachedData() {
            return true;
        }
    };
    public static final DiskCacheStrategy DATA = new DiskCacheStrategy() {
        public boolean isDataCacheable(DataSource dataSource) {
            return (dataSource == DataSource.DATA_DISK_CACHE || dataSource == DataSource.MEMORY_CACHE) ? false : true;
        }

        public boolean isResourceCacheable(boolean isFromAlternateCacheKey, DataSource dataSource, EncodeStrategy encodeStrategy) {
            return false;
        }

        public boolean decodeCachedResource() {
            return false;
        }

        public boolean decodeCachedData() {
            return true;
        }
    };
    public static final DiskCacheStrategy NONE = new DiskCacheStrategy() {
        public boolean isDataCacheable(DataSource dataSource) {
            return false;
        }

        public boolean isResourceCacheable(boolean isFromAlternateCacheKey, DataSource dataSource, EncodeStrategy encodeStrategy) {
            return false;
        }

        public boolean decodeCachedResource() {
            return false;
        }

        public boolean decodeCachedData() {
            return false;
        }
    };
    public static final DiskCacheStrategy RESOURCE = new DiskCacheStrategy() {
        public boolean isDataCacheable(DataSource dataSource) {
            return false;
        }

        public boolean isResourceCacheable(boolean isFromAlternateCacheKey, DataSource dataSource, EncodeStrategy encodeStrategy) {
            return (dataSource == DataSource.RESOURCE_DISK_CACHE || dataSource == DataSource.MEMORY_CACHE) ? false : true;
        }

        public boolean decodeCachedResource() {
            return true;
        }

        public boolean decodeCachedData() {
            return false;
        }
    };

    public abstract boolean decodeCachedData();

    public abstract boolean decodeCachedResource();

    public abstract boolean isDataCacheable(DataSource dataSource);

    public abstract boolean isResourceCacheable(boolean z, DataSource dataSource, EncodeStrategy encodeStrategy);
}
