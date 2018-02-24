package android.support.v17.leanback.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.RestrictTo;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

@RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
public class ThumbsBar
  extends LinearLayout
{
  static final int DEFAULT_NUM_OF_THUMBS = 7;
  final SparseArray<Bitmap> mBitmaps = new SparseArray();
  int mHeroThumbHeight = 240;
  int mHeroThumbWidth = 240;
  int mMeasuredMargin;
  int mMinimalMargin = 16;
  int mNumOfThumbs;
  int mThumbHeight = 160;
  int mThumbWidth = 160;
  
  public ThumbsBar(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public ThumbsBar(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    setNumberOfThumbs(7);
  }
  
  public void clearThumbBitmaps()
  {
    int i = 0;
    while (i < getChildCount())
    {
      setThumbBitmap(i, null);
      i += 1;
    }
    this.mBitmaps.clear();
  }
  
  protected View createThumbView(ViewGroup paramViewGroup)
  {
    return new ImageView(paramViewGroup.getContext());
  }
  
  public int getHeroIndex()
  {
    return getChildCount() / 2;
  }
  
  public Bitmap getThumbBitmap(int paramInt)
  {
    return (Bitmap)this.mBitmaps.get(paramInt);
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    int i = getHeroIndex();
    View localView = getChildAt(i);
    paramInt2 = getWidth() / 2 - localView.getMeasuredWidth() / 2;
    paramInt3 = getWidth() / 2 + localView.getMeasuredWidth() / 2;
    localView.layout(paramInt2, getPaddingTop(), paramInt3, getPaddingTop() + localView.getMeasuredHeight());
    paramInt4 = getPaddingTop() + localView.getMeasuredHeight() / 2;
    paramInt1 = i - 1;
    while (paramInt1 >= 0)
    {
      paramInt2 -= this.mMeasuredMargin;
      localView = getChildAt(paramInt1);
      localView.layout(paramInt2 - localView.getMeasuredWidth(), paramInt4 - localView.getMeasuredHeight() / 2, paramInt2, localView.getMeasuredHeight() / 2 + paramInt4);
      paramInt2 -= localView.getMeasuredWidth();
      paramInt1 -= 1;
    }
    paramInt1 = i + 1;
    paramInt2 = paramInt3;
    while (paramInt1 < this.mNumOfThumbs)
    {
      paramInt2 += this.mMeasuredMargin;
      localView = getChildAt(paramInt1);
      localView.layout(paramInt2, paramInt4 - localView.getMeasuredHeight() / 2, localView.getMeasuredWidth() + paramInt2, localView.getMeasuredHeight() / 2 + paramInt4);
      paramInt2 += localView.getMeasuredWidth();
      paramInt1 += 1;
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(paramInt1, paramInt2);
    int i = getMeasuredWidth();
    paramInt1 = 0;
    for (;;)
    {
      paramInt2 = paramInt1;
      if (this.mNumOfThumbs <= 1) {
        break;
      }
      paramInt1 = i - this.mHeroThumbWidth - this.mThumbWidth * (this.mNumOfThumbs - 1);
      paramInt2 = paramInt1;
      if (paramInt1 >= this.mMinimalMargin * (this.mNumOfThumbs - 1)) {
        break;
      }
      setNumberOfThumbs(this.mNumOfThumbs - 2);
    }
    if (this.mNumOfThumbs > 0) {}
    for (paramInt1 = paramInt2 / (this.mNumOfThumbs - 1);; paramInt1 = 0)
    {
      this.mMeasuredMargin = paramInt1;
      return;
    }
  }
  
  public void setHeroThumbSize(int paramInt1, int paramInt2)
  {
    this.mHeroThumbHeight = paramInt2;
    this.mHeroThumbWidth = paramInt1;
    int k = getHeroIndex();
    int j = 0;
    while (j < getChildCount())
    {
      if (k == j)
      {
        View localView = getChildAt(j);
        LinearLayout.LayoutParams localLayoutParams = (LinearLayout.LayoutParams)localView.getLayoutParams();
        int i = 0;
        if (localLayoutParams.height != paramInt2)
        {
          localLayoutParams.height = paramInt2;
          i = 1;
        }
        if (localLayoutParams.width != paramInt1)
        {
          localLayoutParams.width = paramInt1;
          i = 1;
        }
        if (i != 0) {
          localView.setLayoutParams(localLayoutParams);
        }
      }
      j += 1;
    }
  }
  
  public void setNumberOfThumbs(int paramInt)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException();
    }
    int i = paramInt;
    if ((paramInt & 0x1) == 0) {
      i = paramInt + 1;
    }
    this.mNumOfThumbs = i;
    while (getChildCount() > this.mNumOfThumbs) {
      removeView(getChildAt(getChildCount() - 1));
    }
    while (getChildCount() < this.mNumOfThumbs) {
      addView(createThumbView(this), new LinearLayout.LayoutParams(this.mThumbWidth, this.mThumbHeight));
    }
    i = getHeroIndex();
    paramInt = 0;
    if (paramInt < getChildCount())
    {
      View localView = getChildAt(paramInt);
      LinearLayout.LayoutParams localLayoutParams = (LinearLayout.LayoutParams)localView.getLayoutParams();
      if (i == paramInt) {
        localLayoutParams.width = this.mHeroThumbWidth;
      }
      for (localLayoutParams.height = this.mHeroThumbHeight;; localLayoutParams.height = this.mThumbHeight)
      {
        localView.setLayoutParams(localLayoutParams);
        paramInt += 1;
        break;
        localLayoutParams.width = this.mThumbWidth;
      }
    }
  }
  
  public void setThumbBitmap(int paramInt, Bitmap paramBitmap)
  {
    this.mBitmaps.put(paramInt, paramBitmap);
    ((ImageView)getChildAt(paramInt)).setImageBitmap(paramBitmap);
  }
  
  public void setThumbSize(int paramInt1, int paramInt2)
  {
    this.mThumbHeight = paramInt2;
    this.mThumbWidth = paramInt1;
    int k = getHeroIndex();
    int j = 0;
    while (j < getChildCount())
    {
      if (k != j)
      {
        View localView = getChildAt(j);
        LinearLayout.LayoutParams localLayoutParams = (LinearLayout.LayoutParams)localView.getLayoutParams();
        int i = 0;
        if (localLayoutParams.height != paramInt2)
        {
          localLayoutParams.height = paramInt2;
          i = 1;
        }
        if (localLayoutParams.width != paramInt1)
        {
          localLayoutParams.width = paramInt1;
          i = 1;
        }
        if (i != 0) {
          localView.setLayoutParams(localLayoutParams);
        }
      }
      j += 1;
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/ThumbsBar.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */