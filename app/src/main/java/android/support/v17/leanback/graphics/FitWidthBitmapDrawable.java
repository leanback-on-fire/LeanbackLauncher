package android.support.v17.leanback.graphics;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.ConstantState;
import android.os.Build.VERSION;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.IntProperty;
import android.util.Property;

public class FitWidthBitmapDrawable
  extends Drawable
{
  public static final Property<FitWidthBitmapDrawable, Integer> PROPERTY_VERTICAL_OFFSET = new Property(Integer.class, "verticalOffset")
  {
    public Integer get(FitWidthBitmapDrawable paramAnonymousFitWidthBitmapDrawable)
    {
      return Integer.valueOf(paramAnonymousFitWidthBitmapDrawable.getVerticalOffset());
    }
    
    public void set(FitWidthBitmapDrawable paramAnonymousFitWidthBitmapDrawable, Integer paramAnonymousInteger)
    {
      paramAnonymousFitWidthBitmapDrawable.setVerticalOffset(paramAnonymousInteger.intValue());
    }
  };
  BitmapState mBitmapState;
  final Rect mDest = new Rect();
  boolean mMutated = false;
  
  static
  {
    if (Build.VERSION.SDK_INT >= 24)
    {
      PROPERTY_VERTICAL_OFFSET = getVerticalOffsetIntProperty();
      return;
    }
  }
  
  public FitWidthBitmapDrawable()
  {
    this.mBitmapState = new BitmapState();
  }
  
  FitWidthBitmapDrawable(BitmapState paramBitmapState)
  {
    this.mBitmapState = paramBitmapState;
  }
  
  @RequiresApi(24)
  static IntProperty<FitWidthBitmapDrawable> getVerticalOffsetIntProperty()
  {
    new IntProperty("verticalOffset")
    {
      public Integer get(FitWidthBitmapDrawable paramAnonymousFitWidthBitmapDrawable)
      {
        return Integer.valueOf(paramAnonymousFitWidthBitmapDrawable.getVerticalOffset());
      }
      
      public void setValue(FitWidthBitmapDrawable paramAnonymousFitWidthBitmapDrawable, int paramAnonymousInt)
      {
        paramAnonymousFitWidthBitmapDrawable.setVerticalOffset(paramAnonymousInt);
      }
    };
  }
  
  private Rect validateSource()
  {
    if (this.mBitmapState.mSource == null) {
      return this.mBitmapState.mDefaultSource;
    }
    return this.mBitmapState.mSource;
  }
  
  public void draw(Canvas paramCanvas)
  {
    if (this.mBitmapState.mBitmap != null)
    {
      Rect localRect1 = getBounds();
      this.mDest.left = 0;
      this.mDest.top = this.mBitmapState.mOffset;
      this.mDest.right = localRect1.width();
      Rect localRect2 = validateSource();
      float f = localRect1.width() / localRect2.width();
      this.mDest.bottom = (this.mDest.top + (int)(localRect2.height() * f));
      int i = paramCanvas.save();
      paramCanvas.clipRect(localRect1);
      paramCanvas.drawBitmap(this.mBitmapState.mBitmap, localRect2, this.mDest, this.mBitmapState.mPaint);
      paramCanvas.restoreToCount(i);
    }
  }
  
  public int getAlpha()
  {
    return this.mBitmapState.mPaint.getAlpha();
  }
  
  public Bitmap getBitmap()
  {
    return this.mBitmapState.mBitmap;
  }
  
  public Drawable.ConstantState getConstantState()
  {
    return this.mBitmapState;
  }
  
  public int getOpacity()
  {
    Bitmap localBitmap = this.mBitmapState.mBitmap;
    if ((localBitmap == null) || (localBitmap.hasAlpha()) || (this.mBitmapState.mPaint.getAlpha() < 255)) {
      return -3;
    }
    return -1;
  }
  
  public Rect getSource()
  {
    return this.mBitmapState.mSource;
  }
  
  public int getVerticalOffset()
  {
    return this.mBitmapState.mOffset;
  }
  
  public Drawable mutate()
  {
    if ((!this.mMutated) && (super.mutate() == this))
    {
      this.mBitmapState = new BitmapState(this.mBitmapState);
      this.mMutated = true;
    }
    return this;
  }
  
  public void setAlpha(int paramInt)
  {
    if (paramInt != this.mBitmapState.mPaint.getAlpha())
    {
      this.mBitmapState.mPaint.setAlpha(paramInt);
      invalidateSelf();
    }
  }
  
  public void setBitmap(Bitmap paramBitmap)
  {
    this.mBitmapState.mBitmap = paramBitmap;
    if (paramBitmap != null) {
      this.mBitmapState.mDefaultSource.set(0, 0, paramBitmap.getWidth(), paramBitmap.getHeight());
    }
    for (;;)
    {
      this.mBitmapState.mSource = null;
      return;
      this.mBitmapState.mDefaultSource.set(0, 0, 0, 0);
    }
  }
  
  public void setColorFilter(ColorFilter paramColorFilter)
  {
    this.mBitmapState.mPaint.setColorFilter(paramColorFilter);
    invalidateSelf();
  }
  
  public void setSource(Rect paramRect)
  {
    this.mBitmapState.mSource = paramRect;
  }
  
  public void setVerticalOffset(int paramInt)
  {
    this.mBitmapState.mOffset = paramInt;
    invalidateSelf();
  }
  
  static class BitmapState
    extends Drawable.ConstantState
  {
    Bitmap mBitmap;
    final Rect mDefaultSource = new Rect();
    int mOffset;
    Paint mPaint;
    Rect mSource;
    
    BitmapState()
    {
      this.mPaint = new Paint();
    }
    
    BitmapState(BitmapState paramBitmapState)
    {
      this.mBitmap = paramBitmapState.mBitmap;
      this.mPaint = new Paint(paramBitmapState.mPaint);
      if (paramBitmapState.mSource != null) {}
      for (Rect localRect = new Rect(paramBitmapState.mSource);; localRect = null)
      {
        this.mSource = localRect;
        this.mDefaultSource.set(paramBitmapState.mDefaultSource);
        this.mOffset = paramBitmapState.mOffset;
        return;
      }
    }
    
    public int getChangingConfigurations()
    {
      return 0;
    }
    
    @NonNull
    public Drawable newDrawable()
    {
      return new FitWidthBitmapDrawable(this);
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/graphics/FitWidthBitmapDrawable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */