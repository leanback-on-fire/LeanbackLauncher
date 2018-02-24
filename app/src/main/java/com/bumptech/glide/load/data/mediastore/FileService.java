package com.bumptech.glide.load.data.mediastore;

import java.io.File;

class FileService {
    FileService() {
    }

    public boolean exists(File file) {
        return file.exists();
    }

    public long length(File file) {
        return file.length();
    }

    public File get(String path) {
        return new File(path);
    }
}
