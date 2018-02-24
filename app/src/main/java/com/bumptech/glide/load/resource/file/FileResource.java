package com.bumptech.glide.load.resource.file;

import com.bumptech.glide.load.resource.SimpleResource;
import java.io.File;

public class FileResource extends SimpleResource<File> {
    public FileResource(File file) {
        super(file);
    }
}
