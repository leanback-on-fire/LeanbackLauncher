package com.bumptech.glide.load.model;

import android.util.Log;
import com.bumptech.glide.load.Encoder;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamEncoder implements Encoder<InputStream> {
    private final ArrayPool byteArrayPool;

    public StreamEncoder(ArrayPool byteArrayPool) {
        this.byteArrayPool = byteArrayPool;
    }

    public boolean encode(InputStream data, File file, Options options) {
        IOException e;
        Throwable th;
        byte[] buffer = (byte[]) this.byteArrayPool.get(65536, byte[].class);
        boolean success = false;
        OutputStream outputStream = null;
        try {
            OutputStream os = new FileOutputStream(file);
            while (true) {
                try {
                    int read = data.read(buffer);
                    if (read == -1) {
                        break;
                    }
                    os.write(buffer, 0, read);
                } catch (IOException e2) {
                    e = e2;
                    outputStream = os;
                } catch (Throwable th2) {
                    th = th2;
                    outputStream = os;
                }
            }
            os.close();
            success = true;
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e3) {
                }
            }
            this.byteArrayPool.put(buffer, byte[].class);
            outputStream = os;
        } catch (IOException e4) {
            e = e4;
            try {
                if (Log.isLoggable("StreamEncoder", 3)) {
                    Log.d("StreamEncoder", "Failed to encode data onto the OutputStream", e);
                }
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e5) {
                    }
                }
                this.byteArrayPool.put(buffer, byte[].class);
                return success;
            } catch (Throwable th3) {
                th = th3;
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e6) {
                    }
                }
                this.byteArrayPool.put(buffer, byte[].class);
                throw th;
            }
        }
        return success;
    }
}
