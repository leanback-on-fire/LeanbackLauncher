package com.rockon999.android.leanbacklauncher.recline.util;

import android.content.Context;
import android.content.Intent.ShortcutIconResource;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ImageView;

import com.rockon999.android.leanbacklauncher.recline.util.RefcountObject.RefcountListener;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;

class DrawableLoader extends AsyncTask<CachedTaskPool.TaskOption, Void, Object> {
    private WeakReference<ImageView> mImageView;
    private int mOriginalHeight;
    private int mOriginalWidth;
    private PostProc<Bitmap> mPostProc;
    private RecycleBitmapPool mRecycledBitmaps;
    private RefcountListener mRefcountListener;

    /* renamed from: com.rockon999.android.recline.util.DrawableLoader.1 */
    class C02221 implements RefcountListener {
        C02221() {
        }

        public void onRefcountZero(RefcountObject object) {
            DrawableLoader.this.mRecycledBitmaps.addRecycledBitmap((Bitmap) object.getObject());
        }
    }

    DrawableLoader(ImageView imageView, RecycleBitmapPool recycledBitmapPool, PostProc<Bitmap> postProc) {
        this.mRefcountListener = new C02221();
        this.mImageView = new WeakReference<>(imageView);
        this.mRecycledBitmaps = recycledBitmapPool;
        this.mPostProc = postProc;
    }

    public int getOriginalWidth() {
        return this.mOriginalWidth;
    }

    public int getOriginalHeight() {
        return this.mOriginalHeight;
    }

    protected Drawable doInBackground(CachedTaskPool.TaskOption... params) {
        return retrieveDrawable((BitmapWorkerOptions) params[0]);
    }

    protected Drawable retrieveDrawable(BitmapWorkerOptions workerOptions) {
        try {
            if (workerOptions.getIconResource() != null) {
                return getBitmapFromResource(workerOptions.getIconResource(), workerOptions);
            }
            if (workerOptions.getResourceUri() == null) {
                Log.e("DrawableLoader", "Error loading bitmap - no source!");
            } else if (UriUtils.isAndroidResourceUri(workerOptions.getResourceUri()) || UriUtils.isShortcutIconResourceUri(workerOptions.getResourceUri())) {
                return getBitmapFromResource(UriUtils.getIconResource(workerOptions.getResourceUri()), workerOptions);
            } else {
                if (UriUtils.isWebUri(workerOptions.getResourceUri())) {
                    return getBitmapFromHttp(workerOptions);
                }
                if (UriUtils.isContentUri(workerOptions.getResourceUri())) {
                    return getBitmapFromContent(workerOptions);
                }
                Log.e("DrawableLoader", "Error loading bitmap - unknown resource URI! " + workerOptions.getResourceUri());
            }
            return null;
        } catch (IOException e) {
            Log.e("DrawableLoader", "Error loading url " + workerOptions.getResourceUri(), e);
            return null;
        } catch (RuntimeException e2) {
            Log.e("DrawableLoader", "Critical Error loading url " + workerOptions.getResourceUri(), e2);
            return null;
        }
    }

    protected void onPostExecute(Object bitmap) {
        if (this.mImageView != null) {
            ImageView imageView = this.mImageView.get();
            if (imageView != null) {
                imageView.setImageDrawable((Drawable) bitmap);
            }
        }
    }

    protected void onCancelled(Object result) {
        if (result instanceof RefcountBitmapDrawable) {
            ((RefcountBitmapDrawable) result).getRefcountObject().releaseRef();
        }
    }

    private Drawable getBitmapFromResource(ShortcutIconResource iconResource, BitmapWorkerOptions outputOptions) throws IOException {
        String packageName = iconResource.packageName;
        String resourceName = iconResource.resourceName;
        try {
            Object drawable = loadDrawable(outputOptions.getContext(), iconResource);
            if (drawable instanceof InputStream) {
                return decodeBitmap((InputStream) drawable, outputOptions);
            }
            if (drawable instanceof Drawable) {
                Drawable d = (Drawable) drawable;
                this.mOriginalWidth = d.getIntrinsicWidth();
                this.mOriginalHeight = d.getIntrinsicHeight();
                return d;
            }
            Log.w("DrawableLoader", "getBitmapFromResource failed, unrecognized resource: " + drawable);
            return null;
        } catch (NameNotFoundException e) {
            Log.w("DrawableLoader", "Could not load package: " + iconResource.packageName + "! NameNotFound");
            return null;
        } catch (NotFoundException e2) {
            Log.w("DrawableLoader", "Could not load resource: " + iconResource.resourceName + "! NotFound");
            return null;
        }
    }

    private Drawable decodeBitmap(InputStream in, BitmapWorkerOptions options) throws IOException {
        Throwable th;
        CachedInputStream bufferedStream = null;
        Options options2 = null;
        try {
            CachedInputStream bufferedStream2 = new CachedInputStream(in);
            try {
                Bitmap bitmap;
                bufferedStream2.setOverrideMarkLimit(Integer.MAX_VALUE);
                Options bitmapOptions = new Options();
                try {
                    int scale;
                    bitmapOptions.inJustDecodeBounds = true;
                    if (options.getBitmapConfig() != null) {
                        bitmapOptions.inPreferredConfig = options.getBitmapConfig();
                    }
                    bitmapOptions.inTempStorage = ByteArrayPool.get16KBPool().allocateChunk();
                    bufferedStream2.mark(Integer.MAX_VALUE);
                    BitmapFactory.decodeStream(bufferedStream2, null, bitmapOptions);
                    this.mOriginalWidth = bitmapOptions.outWidth;
                    this.mOriginalHeight = bitmapOptions.outHeight;
                    int heightScale = 1;
                    int height = options.getHeight();
                    if (height > 0) {
                        heightScale = bitmapOptions.outHeight / height;
                    }
                    int widthScale = 1;
                    int width = options.getWidth();
                    if (width > 0) {
                        widthScale = bitmapOptions.outWidth / width;
                    }
                    if (heightScale > widthScale) {
                        scale = heightScale;
                    } else {
                        scale = widthScale;
                    }
                    if (scale <= 1) {
                        scale = 1;
                    } else {
                        int shift = 0;
                        do {
                            scale >>= 1;
                            shift++;
                        } while (scale != 0);
                        scale = 1 << (shift - 1);
                    }
                    bufferedStream2.reset();
                    bufferedStream2.setOverrideMarkLimit(0);
                    bitmapOptions.inJustDecodeBounds = false;
                    bitmapOptions.inSampleSize = scale;
                    bitmapOptions.inMutable = true;
                    bitmapOptions.inBitmap = this.mRecycledBitmaps.getRecycledBitmap(this.mOriginalWidth / scale, this.mOriginalHeight / scale);
                    bitmap = BitmapFactory.decodeStream(bufferedStream2, null, bitmapOptions);
                } catch (RuntimeException ex) {
                    Log.e("DrawableLoader", "RuntimeException" + ex + ", trying decodeStream again");
                    bufferedStream2.reset();
                    bufferedStream2.setOverrideMarkLimit(0);
                    bitmapOptions.inBitmap = null;
                    bitmap = BitmapFactory.decodeStream(bufferedStream2, null, bitmapOptions);
                } catch (Throwable th2) {
                    th = th2;
                    options2 = bitmapOptions;
                    bufferedStream = bufferedStream2;
                    if (options2 != null) {
                        ByteArrayPool.get16KBPool().releaseChunk(options2.inTempStorage);
                    }
                    if (bufferedStream != null) {
                        bufferedStream.close();
                    }
                    throw th;
                }
                if (bitmap == null) {
                    Log.v("DrawableLoader", "bitmap was null");
                    if (bitmapOptions != null) {
                        ByteArrayPool.get16KBPool().releaseChunk(bitmapOptions.inTempStorage);
                    }
                    if (bufferedStream2 != null) {
                        bufferedStream2.close();
                    }
                    return null;
                }
                if (this.mPostProc != null) {
                    bitmap = this.mPostProc.postProcess(bitmap);
                }
                RefcountObject object = new RefcountObject(bitmap);
                object.addRef();
                object.setRefcountListener(this.mRefcountListener);
                RefcountBitmapDrawable d = new RefcountBitmapDrawable(options.getContext().getResources(), object);
                if (bitmapOptions != null) {
                    ByteArrayPool.get16KBPool().releaseChunk(bitmapOptions.inTempStorage);
                }
                if (bufferedStream2 != null) {
                    bufferedStream2.close();
                }
                return d;
            } catch (Throwable th3) {
                th = th3;
                bufferedStream = bufferedStream2;
                if (options2 != null) {
                    ByteArrayPool.get16KBPool().releaseChunk(options2.inTempStorage);
                }
                if (bufferedStream != null) {
                    bufferedStream.close();
                }
                throw th;
            }
        } catch (Throwable th4) {
            th = th4;
            if (options2 != null) {
                ByteArrayPool.get16KBPool().releaseChunk(options2.inTempStorage);
            }
            if (bufferedStream != null) {
                bufferedStream.close();
            }
            // throw th;
        }
        return null;
    }

    private Drawable getBitmapFromHttp(BitmapWorkerOptions options) throws IOException {
        URL url = new URL(options.getResourceUri().toString());
        try {
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            return decodeBitmap(connection.getInputStream(), options);
        } catch (SocketTimeoutException e) {
            Log.e("DrawableLoader", "loading " + url + " timed out");
            return null;
        }
    }

    private Drawable getBitmapFromContent(BitmapWorkerOptions options) throws IOException {
        Uri resourceUri = options.getResourceUri();
        if (resourceUri != null) {
            try {
                InputStream bitmapStream = options.getContext().getContentResolver().openInputStream(resourceUri);
                if (bitmapStream != null) {
                    return decodeBitmap(bitmapStream, options);
                }
                Log.w("DrawableLoader", "Content provider returned a null InputStream when trying to open resource.");
                return null;
            } catch (FileNotFoundException e) {
                Log.e("DrawableLoader", "FileNotFoundException during openInputStream for uri: " + resourceUri.toString());
                return null;
            }
        }
        Log.w("DrawableLoader", "Get null resourceUri from BitmapWorkerOptions.");
        return null;
    }

    private static Object loadDrawable(Context context, ShortcutIconResource r) throws NameNotFoundException {
        Resources resources = context.getPackageManager().getResourcesForApplication(r.packageName);
        if (resources == null) {
            return null;
        }
        int id = resources.getIdentifier(r.resourceName, null, null);
        if (id == 0) {
            Log.e("DrawableLoader", "Couldn't get resource " + r.resourceName + " in resources of " + r.packageName);
            return null;
        }
        TypedValue value = new TypedValue();
        resources.getValue(id, value, true);
        if ((value.type != 3 || !value.string.toString().endsWith(".xml")) && (value.type < 28 || value.type > 31)) {
            return resources.openRawResource(id, value);
        }
        return resources.getDrawable(id, context.getTheme()); // todo added theme
    }
}
