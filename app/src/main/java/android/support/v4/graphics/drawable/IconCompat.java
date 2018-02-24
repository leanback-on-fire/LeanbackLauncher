package android.support.v4.graphics.drawable;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build.VERSION;
import android.support.annotation.DrawableRes;
import android.support.annotation.RestrictTo;
import android.support.annotation.VisibleForTesting;

public class IconCompat
{
  private static final float ADAPTIVE_ICON_INSET_FACTOR = 0.25F;
  private static final int AMBIENT_SHADOW_ALPHA = 30;
  private static final float BLUR_FACTOR = 0.010416667F;
  private static final float DEFAULT_VIEW_PORT_SCALE = 0.6666667F;
  private static final float ICON_DIAMETER_FACTOR = 0.9166667F;
  private static final int KEY_SHADOW_ALPHA = 61;
  private static final float KEY_SHADOW_OFFSET_FACTOR = 0.020833334F;
  private static final int TYPE_ADAPTIVE_BITMAP = 5;
  private static final int TYPE_BITMAP = 1;
  private static final int TYPE_DATA = 3;
  private static final int TYPE_RESOURCE = 2;
  private static final int TYPE_URI = 4;
  private int mInt1;
  private int mInt2;
  private Object mObj1;
  private final int mType;
  
  private IconCompat(int paramInt)
  {
    this.mType = paramInt;
  }
  
  @VisibleForTesting
  static Bitmap createLegacyIconFromAdaptiveIcon(Bitmap paramBitmap)
  {
    int i = (int)(0.6666667F * Math.min(paramBitmap.getWidth(), paramBitmap.getHeight()));
    Bitmap localBitmap = Bitmap.createBitmap(i, i, Bitmap.Config.ARGB_8888);
    Canvas localCanvas = new Canvas(localBitmap);
    Paint localPaint = new Paint(3);
    float f1 = i * 0.5F;
    float f2 = f1 * 0.9166667F;
    float f3 = 0.010416667F * i;
    localPaint.setColor(0);
    localPaint.setShadowLayer(f3, 0.0F, 0.020833334F * i, 1023410176);
    localCanvas.drawCircle(f1, f1, f2, localPaint);
    localPaint.setShadowLayer(f3, 0.0F, 0.0F, 503316480);
    localCanvas.drawCircle(f1, f1, f2, localPaint);
    localPaint.clearShadowLayer();
    localPaint.setColor(-16777216);
    BitmapShader localBitmapShader = new BitmapShader(paramBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
    Matrix localMatrix = new Matrix();
    localMatrix.setTranslate(-(paramBitmap.getWidth() - i) / 2, -(paramBitmap.getHeight() - i) / 2);
    localBitmapShader.setLocalMatrix(localMatrix);
    localPaint.setShader(localBitmapShader);
    localCanvas.drawCircle(f1, f1, f2, localPaint);
    localCanvas.setBitmap(null);
    return localBitmap;
  }
  
  public static IconCompat createWithAdaptiveBitmap(Bitmap paramBitmap)
  {
    if (paramBitmap == null) {
      throw new IllegalArgumentException("Bitmap must not be null.");
    }
    IconCompat localIconCompat = new IconCompat(5);
    localIconCompat.mObj1 = paramBitmap;
    return localIconCompat;
  }
  
  public static IconCompat createWithBitmap(Bitmap paramBitmap)
  {
    if (paramBitmap == null) {
      throw new IllegalArgumentException("Bitmap must not be null.");
    }
    IconCompat localIconCompat = new IconCompat(1);
    localIconCompat.mObj1 = paramBitmap;
    return localIconCompat;
  }
  
  public static IconCompat createWithContentUri(Uri paramUri)
  {
    if (paramUri == null) {
      throw new IllegalArgumentException("Uri must not be null.");
    }
    return createWithContentUri(paramUri.toString());
  }
  
  public static IconCompat createWithContentUri(String paramString)
  {
    if (paramString == null) {
      throw new IllegalArgumentException("Uri must not be null.");
    }
    IconCompat localIconCompat = new IconCompat(4);
    localIconCompat.mObj1 = paramString;
    return localIconCompat;
  }
  
  public static IconCompat createWithData(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    if (paramArrayOfByte == null) {
      throw new IllegalArgumentException("Data must not be null.");
    }
    IconCompat localIconCompat = new IconCompat(3);
    localIconCompat.mObj1 = paramArrayOfByte;
    localIconCompat.mInt1 = paramInt1;
    localIconCompat.mInt2 = paramInt2;
    return localIconCompat;
  }
  
  public static IconCompat createWithResource(Context paramContext, @DrawableRes int paramInt)
  {
    if (paramContext == null) {
      throw new IllegalArgumentException("Context must not be null.");
    }
    IconCompat localIconCompat = new IconCompat(2);
    localIconCompat.mInt1 = paramInt;
    localIconCompat.mObj1 = paramContext;
    return localIconCompat;
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public void addToShortcutIntent(Intent paramIntent)
  {
    switch (this.mType)
    {
    case 3: 
    case 4: 
    default: 
      throw new IllegalArgumentException("Icon type not supported for intent shortcuts");
    case 1: 
      paramIntent.putExtra("android.intent.extra.shortcut.ICON", (Bitmap)this.mObj1);
      return;
    case 5: 
      paramIntent.putExtra("android.intent.extra.shortcut.ICON", createLegacyIconFromAdaptiveIcon((Bitmap)this.mObj1));
      return;
    }
    paramIntent.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", Intent.ShortcutIconResource.fromContext((Context)this.mObj1, this.mInt1));
  }
  
  @TargetApi(26)
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public Icon toIcon()
  {
    switch (this.mType)
    {
    default: 
      throw new IllegalArgumentException("Unknown type");
    case 1: 
      return Icon.createWithBitmap((Bitmap)this.mObj1);
    case 5: 
      if (Build.VERSION.SDK_INT >= 26) {
        return Icon.createWithAdaptiveBitmap((Bitmap)this.mObj1);
      }
      return Icon.createWithBitmap(createLegacyIconFromAdaptiveIcon((Bitmap)this.mObj1));
    case 2: 
      return Icon.createWithResource((Context)this.mObj1, this.mInt1);
    case 3: 
      return Icon.createWithData((byte[])this.mObj1, this.mInt1, this.mInt2);
    }
    return Icon.createWithContentUri((String)this.mObj1);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v4/graphics/drawable/IconCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */