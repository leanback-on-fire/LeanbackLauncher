package com.google.android.tvlauncher.home.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v7.graphics.Palette;
import android.support.v7.graphics.Palette.Builder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.transcode.ResourceTranscoder;

public class ProgramPreviewImageTranscoder
  implements ResourceTranscoder<Bitmap, ProgramPreviewImageData>
{
  private static final float BLURRED_BITMAP_SCALE = 0.5F;
  private static final float BLUR_RADIUS = 8.0F;
  private final BitmapPool mBitmapPool;
  private ScriptIntrinsicBlur mBlur;
  private final Context mContext;
  private RenderScript mRenderScript;
  
  public ProgramPreviewImageTranscoder(Context paramContext)
  {
    this.mContext = paramContext.getApplicationContext();
    this.mBitmapPool = Glide.get(paramContext).getBitmapPool();
  }
  
  private Bitmap generateBlurredBitmap(Bitmap paramBitmap)
  {
    if (this.mRenderScript == null)
    {
      this.mRenderScript = RenderScript.create(this.mContext);
      this.mBlur = ScriptIntrinsicBlur.create(this.mRenderScript, Element.U8_4(this.mRenderScript));
      this.mBlur.setRadius(8.0F);
    }
    Object localObject = new Matrix();
    ((Matrix)localObject).setScale(0.5F, 0.5F);
    localObject = Bitmap.createBitmap(paramBitmap, 0, 0, paramBitmap.getWidth(), paramBitmap.getHeight(), (Matrix)localObject, false);
    paramBitmap = Bitmap.createBitmap((Bitmap)localObject);
    localObject = Allocation.createFromBitmap(this.mRenderScript, (Bitmap)localObject);
    Allocation localAllocation = Allocation.createFromBitmap(this.mRenderScript, paramBitmap);
    this.mBlur.setInput((Allocation)localObject);
    this.mBlur.forEach(localAllocation);
    localAllocation.copyTo(paramBitmap);
    return paramBitmap;
  }
  
  public Resource<ProgramPreviewImageData> transcode(Resource<Bitmap> paramResource)
  {
    paramResource = (Bitmap)paramResource.get();
    return new ProgramPreviewImageResource(new ProgramPreviewImageData(paramResource, generateBlurredBitmap(paramResource), Palette.from(paramResource).generate()), this.mBitmapPool);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/home/util/ProgramPreviewImageTranscoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */