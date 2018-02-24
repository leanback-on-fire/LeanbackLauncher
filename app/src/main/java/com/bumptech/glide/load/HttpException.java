package com.bumptech.glide.load;

import java.io.IOException;

public final class HttpException extends IOException {
    private final int statusCode;

    public HttpException(int statusCode) {
        this("Http request failed with status code: " + statusCode, statusCode);
    }

    public HttpException(String message) {
        this(message, -1);
    }

    public HttpException(String message, int statusCode) {
        this(message, statusCode, null);
    }

    public HttpException(String message, int statusCode, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }
}
