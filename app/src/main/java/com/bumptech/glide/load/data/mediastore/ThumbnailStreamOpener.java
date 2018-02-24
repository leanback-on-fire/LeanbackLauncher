package com.bumptech.glide.load.data.mediastore;

import android.content.ContentResolver;
import android.net.Uri;
import android.util.Log;
import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;
import com.bumptech.glide.load.resource.bitmap.ImageHeaderParser;
import java.io.IOException;
import java.io.InputStream;

class ThumbnailStreamOpener {
    private static final FileService DEFAULT_SERVICE = new FileService();
    private final ArrayPool byteArrayPool;
    private final ContentResolver contentResolver;
    private final ThumbnailQuery query;
    private final FileService service;

    public java.io.InputStream open(android.net.Uri r11) throws java.io.FileNotFoundException {
        /* JADX: method processing error */
/*
Error: java.util.NoSuchElementException
	at java.util.HashMap$HashIterator.nextNode(HashMap.java:1444)
	at java.util.HashMap$KeyIterator.next(HashMap.java:1466)
	at jadx.core.dex.visitors.blocksmaker.BlockFinallyExtract.applyRemove(BlockFinallyExtract.java:535)
	at jadx.core.dex.visitors.blocksmaker.BlockFinallyExtract.extractFinally(BlockFinallyExtract.java:175)
	at jadx.core.dex.visitors.blocksmaker.BlockFinallyExtract.processExceptionHandler(BlockFinallyExtract.java:79)
	at jadx.core.dex.visitors.blocksmaker.BlockFinallyExtract.visit(BlockFinallyExtract.java:51)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:306)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:199)
*/
        /*
        r10 = this;
        r5 = 0;
        r4 = 0;
        r2 = 0;
        r6 = r10.query;
        r0 = r6.query(r11);
        if (r0 == 0) goto L_0x0011;
    L_0x000b:
        r6 = r0.moveToFirst();	 Catch:{ all -> 0x0055 }
        if (r6 != 0) goto L_0x0017;
    L_0x0011:
        if (r0 == 0) goto L_0x0016;
    L_0x0013:
        r0.close();
    L_0x0016:
        return r5;
    L_0x0017:
        r6 = 0;
        r3 = r0.getString(r6);	 Catch:{ all -> 0x0055 }
        r6 = android.text.TextUtils.isEmpty(r3);	 Catch:{ all -> 0x0055 }
        if (r6 == 0) goto L_0x0028;
    L_0x0022:
        if (r0 == 0) goto L_0x0016;
    L_0x0024:
        r0.close();
        goto L_0x0016;
    L_0x0028:
        r5 = r10.service;	 Catch:{ all -> 0x0055 }
        r1 = r5.get(r3);	 Catch:{ all -> 0x0055 }
        r5 = r10.service;	 Catch:{ all -> 0x0055 }
        r5 = r5.exists(r1);	 Catch:{ all -> 0x0055 }
        if (r5 == 0) goto L_0x0046;	 Catch:{ all -> 0x0055 }
    L_0x0036:
        r5 = r10.service;	 Catch:{ all -> 0x0055 }
        r6 = r5.length(r1);	 Catch:{ all -> 0x0055 }
        r8 = 0;	 Catch:{ all -> 0x0055 }
        r5 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));	 Catch:{ all -> 0x0055 }
        if (r5 <= 0) goto L_0x0046;	 Catch:{ all -> 0x0055 }
    L_0x0042:
        r4 = android.net.Uri.fromFile(r1);	 Catch:{ all -> 0x0055 }
    L_0x0046:
        if (r0 == 0) goto L_0x004b;
    L_0x0048:
        r0.close();
    L_0x004b:
        if (r4 == 0) goto L_0x0053;
    L_0x004d:
        r5 = r10.contentResolver;
        r2 = r5.openInputStream(r4);
    L_0x0053:
        r5 = r2;
        goto L_0x0016;
    L_0x0055:
        r5 = move-exception;
        if (r0 == 0) goto L_0x005b;
    L_0x0058:
        r0.close();
    L_0x005b:
        throw r5;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.load.data.mediastore.ThumbnailStreamOpener.open(android.net.Uri):java.io.InputStream");
    }

    public ThumbnailStreamOpener(ThumbnailQuery query, ArrayPool byteArrayPool, ContentResolver contentResolver) {
        this(DEFAULT_SERVICE, query, byteArrayPool, contentResolver);
    }

    public ThumbnailStreamOpener(FileService service, ThumbnailQuery query, ArrayPool byteArrayPool, ContentResolver contentResolver) {
        this.service = service;
        this.query = query;
        this.byteArrayPool = byteArrayPool;
        this.contentResolver = contentResolver;
    }

    public int getOrientation(Uri uri) {
        int orientation = -1;
        InputStream is = null;
        try {
            is = this.contentResolver.openInputStream(uri);
            orientation = new ImageHeaderParser(is, this.byteArrayPool).getOrientation();
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        } catch (IOException e2) {
            if (Log.isLoggable("ThumbStreamOpener", 3)) {
                String valueOf = String.valueOf(uri);
                Log.d("ThumbStreamOpener", new StringBuilder(String.valueOf(valueOf).length() + 20).append("Failed to open uri: ").append(valueOf).toString(), e2);
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e3) {
                }
            }
        } catch (Throwable th) {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e4) {
                }
            }
        }
        return orientation;
    }
}
