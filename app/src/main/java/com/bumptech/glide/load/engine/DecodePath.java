package com.bumptech.glide.load.engine;

import android.support.v4.util.Pools.Pool;
import android.util.Log;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.data.DataRewinder;
import com.bumptech.glide.load.resource.transcode.ResourceTranscoder;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DecodePath<DataType, ResourceType, Transcode> {
    private final Class<DataType> dataClass;
    private final List<? extends ResourceDecoder<DataType, ResourceType>> decoders;
    private final String failureMessage;
    private final Pool<List<Exception>> listPool;
    private final ResourceTranscoder<ResourceType, Transcode> transcoder;

    interface DecodeCallback<ResourceType> {
        Resource<ResourceType> onResourceDecoded(Resource<ResourceType> resource);
    }

    public DecodePath(Class<DataType> dataClass, Class<ResourceType> resourceClass, Class<Transcode> transcodeClass, List<? extends ResourceDecoder<DataType, ResourceType>> decoders, ResourceTranscoder<ResourceType, Transcode> transcoder, Pool<List<Exception>> listPool) {
        this.dataClass = dataClass;
        this.decoders = decoders;
        this.transcoder = transcoder;
        this.listPool = listPool;
        String valueOf = String.valueOf(dataClass.getSimpleName());
        String valueOf2 = String.valueOf(resourceClass.getSimpleName());
        String valueOf3 = String.valueOf(transcodeClass.getSimpleName());
        this.failureMessage = new StringBuilder(((String.valueOf(valueOf).length() + 23) + String.valueOf(valueOf2).length()) + String.valueOf(valueOf3).length()).append("Failed DecodePath{").append(valueOf).append("->").append(valueOf2).append("->").append(valueOf3).append("}").toString();
    }

    public Resource<Transcode> decode(DataRewinder<DataType> rewinder, int width, int height, Options options, DecodeCallback<ResourceType> callback) throws GlideException {
        return this.transcoder.transcode(callback.onResourceDecoded(decodeResource(rewinder, width, height, options)));
    }

    private Resource<ResourceType> decodeResource(DataRewinder<DataType> rewinder, int width, int height, Options options) throws GlideException {
        List<Exception> exceptions = (List) this.listPool.acquire();
        try {
            Resource<ResourceType> decodeResourceWithList = decodeResourceWithList(rewinder, width, height, options, exceptions);
            return decodeResourceWithList;
        } finally {
            this.listPool.release(exceptions);
        }
    }

    private Resource<ResourceType> decodeResourceWithList(DataRewinder<DataType> rewinder, int width, int height, Options options, List<Exception> exceptions) throws GlideException {
        Resource<ResourceType> result = null;
        int size = this.decoders.size();
        for (int i = 0; i < size; i++) {
            ResourceDecoder<DataType, ResourceType> decoder = (ResourceDecoder) this.decoders.get(i);
            try {
                if (decoder.handles(rewinder.rewindAndGet(), options)) {
                    result = decoder.decode(rewinder.rewindAndGet(), width, height, options);
                }
            } catch (IOException e) {
                if (Log.isLoggable("DecodePath", 2)) {
                    String valueOf = String.valueOf(decoder);
                    Log.v("DecodePath", new StringBuilder(String.valueOf(valueOf).length() + 26).append("Failed to decode data for ").append(valueOf).toString(), e);
                }
                exceptions.add(e);
            }
            if (result != null) {
                break;
            }
        }
        if (result != null) {
            return result;
        }
        throw new GlideException(this.failureMessage, new ArrayList(exceptions));
    }

    public String toString() {
        String valueOf = String.valueOf(this.dataClass);
        String valueOf2 = String.valueOf(this.decoders);
        String valueOf3 = String.valueOf(this.transcoder);
        return new StringBuilder(((String.valueOf(valueOf).length() + 47) + String.valueOf(valueOf2).length()) + String.valueOf(valueOf3).length()).append("DecodePath{ dataClass=").append(valueOf).append(", decoders=").append(valueOf2).append(", transcoder=").append(valueOf3).append("}").toString();
    }
}
