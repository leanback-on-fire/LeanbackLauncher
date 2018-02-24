package android.support.v17.leanback.app;

import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ProgressBar;

public final class ProgressBarManager
{
  private static final long DEFAULT_PROGRESS_BAR_DELAY = 1000L;
  boolean mEnableProgressBar = true;
  private Handler mHandler = new Handler();
  private long mInitialDelay = 1000L;
  boolean mIsShowing;
  View mProgressBarView;
  boolean mUserProvidedProgressBar;
  ViewGroup rootView;
  private Runnable runnable = new Runnable()
  {
    public void run()
    {
      if ((!ProgressBarManager.this.mEnableProgressBar) || ((!ProgressBarManager.this.mUserProvidedProgressBar) && (ProgressBarManager.this.rootView == null))) {}
      do
      {
        do
        {
          return;
        } while (!ProgressBarManager.this.mIsShowing);
        if (ProgressBarManager.this.mProgressBarView == null)
        {
          ProgressBarManager.this.mProgressBarView = new ProgressBar(ProgressBarManager.this.rootView.getContext(), null, 16842874);
          FrameLayout.LayoutParams localLayoutParams = new FrameLayout.LayoutParams(-2, -2);
          localLayoutParams.gravity = 17;
          ProgressBarManager.this.rootView.addView(ProgressBarManager.this.mProgressBarView, localLayoutParams);
          return;
        }
      } while (!ProgressBarManager.this.mUserProvidedProgressBar);
      ProgressBarManager.this.mProgressBarView.setVisibility(0);
    }
  };
  
  public void disableProgressBar()
  {
    this.mEnableProgressBar = false;
  }
  
  public void enableProgressBar()
  {
    this.mEnableProgressBar = true;
  }
  
  public long getInitialDelay()
  {
    return this.mInitialDelay;
  }
  
  public void hide()
  {
    this.mIsShowing = false;
    if (this.mUserProvidedProgressBar) {
      this.mProgressBarView.setVisibility(4);
    }
    for (;;)
    {
      this.mHandler.removeCallbacks(this.runnable);
      return;
      if (this.mProgressBarView != null)
      {
        this.rootView.removeView(this.mProgressBarView);
        this.mProgressBarView = null;
      }
    }
  }
  
  public void setInitialDelay(long paramLong)
  {
    this.mInitialDelay = paramLong;
  }
  
  public void setProgressBarView(View paramView)
  {
    if (paramView.getParent() == null) {
      throw new IllegalArgumentException("Must have a parent");
    }
    this.mProgressBarView = paramView;
    this.mProgressBarView.setVisibility(4);
    this.mUserProvidedProgressBar = true;
  }
  
  public void setRootView(ViewGroup paramViewGroup)
  {
    this.rootView = paramViewGroup;
  }
  
  public void show()
  {
    if (this.mEnableProgressBar)
    {
      this.mIsShowing = true;
      this.mHandler.postDelayed(this.runnable, this.mInitialDelay);
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/app/ProgressBarManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */