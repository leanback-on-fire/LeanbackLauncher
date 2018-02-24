package com.google.android.tvlauncher.home;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Outline;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;
import com.google.android.tvlauncher.util.ScaleFocusHandler;

public class CircularActionButtonView
  extends AppCompatImageView
{
  public CircularActionButtonView(Context paramContext)
  {
    super(paramContext);
  }
  
  public CircularActionButtonView(Context paramContext, @Nullable AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  public CircularActionButtonView(Context paramContext, @Nullable AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
  }
  
  protected void onFinishInflate()
  {
    super.onFinishInflate();
    if (!isInEditMode())
    {
      Resources localResources = getResources();
      new ScaleFocusHandler(localResources.getInteger(2131689478), localResources.getFraction(2131886082, 1, 1), localResources.getDimension(2131558511)).setView(this);
    }
    setOutlineProvider(new ViewOutlineProvider()
    {
      public void getOutline(View paramAnonymousView, Outline paramAnonymousOutline)
      {
        paramAnonymousOutline.setOval(0, 0, paramAnonymousView.getMeasuredWidth(), paramAnonymousView.getMeasuredHeight());
      }
    });
    setClipToOutline(true);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/home/CircularActionButtonView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */