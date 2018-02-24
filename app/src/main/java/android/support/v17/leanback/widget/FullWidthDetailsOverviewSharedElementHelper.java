package android.support.v17.leanback.widget;

import android.app.Activity;
import android.os.Handler;
import android.support.v17.leanback.transition.TransitionHelper;
import android.support.v17.leanback.transition.TransitionListener;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.view.ViewGroup;
import java.lang.ref.WeakReference;

public class FullWidthDetailsOverviewSharedElementHelper
  extends FullWidthDetailsOverviewRowPresenter.Listener
{
  static final boolean DEBUG = false;
  private static final long DEFAULT_TIMEOUT = 5000L;
  static final String TAG = "DetailsTransitionHelper";
  Activity mActivityToRunTransition;
  private boolean mAutoStartSharedElementTransition = true;
  String mSharedElementName;
  private boolean mStartedPostpone;
  FullWidthDetailsOverviewRowPresenter.ViewHolder mViewHolder;
  
  public boolean getAutoStartSharedElementTransition()
  {
    return this.mAutoStartSharedElementTransition;
  }
  
  public void onBindLogo(FullWidthDetailsOverviewRowPresenter.ViewHolder paramViewHolder)
  {
    this.mViewHolder = paramViewHolder;
    if (!this.mAutoStartSharedElementTransition) {
      return;
    }
    if (this.mViewHolder != null) {
      ViewCompat.setTransitionName(this.mViewHolder.getLogoViewHolder().view, null);
    }
    this.mViewHolder.getDetailsDescriptionFrame().postOnAnimation(new Runnable()
    {
      public void run()
      {
        ViewCompat.setTransitionName(FullWidthDetailsOverviewSharedElementHelper.this.mViewHolder.getLogoViewHolder().view, FullWidthDetailsOverviewSharedElementHelper.this.mSharedElementName);
        Object localObject = TransitionHelper.getSharedElementEnterTransition(FullWidthDetailsOverviewSharedElementHelper.this.mActivityToRunTransition.getWindow());
        if (localObject != null) {
          TransitionHelper.addTransitionListener(localObject, new TransitionListener()
          {
            public void onTransitionEnd(Object paramAnonymous2Object)
            {
              if (FullWidthDetailsOverviewSharedElementHelper.this.mViewHolder.getActionsRow().isFocused()) {
                FullWidthDetailsOverviewSharedElementHelper.this.mViewHolder.getActionsRow().requestFocus();
              }
              TransitionHelper.removeTransitionListener(paramAnonymous2Object, this);
            }
          });
        }
        FullWidthDetailsOverviewSharedElementHelper.this.startPostponedEnterTransitionInternal();
      }
    });
  }
  
  public void setAutoStartSharedElementTransition(boolean paramBoolean)
  {
    this.mAutoStartSharedElementTransition = paramBoolean;
  }
  
  public void setSharedElementEnterTransition(Activity paramActivity, String paramString)
  {
    setSharedElementEnterTransition(paramActivity, paramString, 5000L);
  }
  
  public void setSharedElementEnterTransition(Activity paramActivity, String paramString, long paramLong)
  {
    if (((paramActivity == null) && (!TextUtils.isEmpty(paramString))) || ((paramActivity != null) && (TextUtils.isEmpty(paramString)))) {
      throw new IllegalArgumentException();
    }
    if ((paramActivity == this.mActivityToRunTransition) && (TextUtils.equals(paramString, this.mSharedElementName))) {
      return;
    }
    this.mActivityToRunTransition = paramActivity;
    this.mSharedElementName = paramString;
    if (TransitionHelper.getSharedElementEnterTransition(paramActivity.getWindow()) != null) {}
    for (boolean bool = true;; bool = false)
    {
      setAutoStartSharedElementTransition(bool);
      ActivityCompat.postponeEnterTransition(this.mActivityToRunTransition);
      if (paramLong <= 0L) {
        break;
      }
      new Handler().postDelayed(new TransitionTimeOutRunnable(this), paramLong);
      return;
    }
  }
  
  public void startPostponedEnterTransition()
  {
    new Handler().post(new Runnable()
    {
      public void run()
      {
        FullWidthDetailsOverviewSharedElementHelper.this.startPostponedEnterTransitionInternal();
      }
    });
  }
  
  void startPostponedEnterTransitionInternal()
  {
    if ((!this.mStartedPostpone) && (this.mViewHolder != null))
    {
      ActivityCompat.startPostponedEnterTransition(this.mActivityToRunTransition);
      this.mStartedPostpone = true;
    }
  }
  
  static class TransitionTimeOutRunnable
    implements Runnable
  {
    WeakReference<FullWidthDetailsOverviewSharedElementHelper> mHelperRef;
    
    TransitionTimeOutRunnable(FullWidthDetailsOverviewSharedElementHelper paramFullWidthDetailsOverviewSharedElementHelper)
    {
      this.mHelperRef = new WeakReference(paramFullWidthDetailsOverviewSharedElementHelper);
    }
    
    public void run()
    {
      FullWidthDetailsOverviewSharedElementHelper localFullWidthDetailsOverviewSharedElementHelper = (FullWidthDetailsOverviewSharedElementHelper)this.mHelperRef.get();
      if (localFullWidthDetailsOverviewSharedElementHelper == null) {
        return;
      }
      localFullWidthDetailsOverviewSharedElementHelper.startPostponedEnterTransition();
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/FullWidthDetailsOverviewSharedElementHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */