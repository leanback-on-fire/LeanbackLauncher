package com.bumptech.glide.load.resource.transcode;

import com.bumptech.glide.load.engine.Resource;

public interface ResourceTranscoder<Z, R> {
    Resource<R> transcode(Resource<Z> resource);
}
