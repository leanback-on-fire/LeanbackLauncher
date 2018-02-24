package android.support.v17.leanback.widget;

import android.support.v17.leanback.transition.LeanbackTransitionHelper;
import android.support.v17.leanback.transition.TransitionHelper;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.ViewGroup;

public class TitleHelper
{
  private final BrowseFrameLayout.OnFocusSearchListener mOnFocusSearchListener = new BrowseFrameLayout.OnFocusSearchListener()
  {
    public View onFocusSearch(View paramAnonymousView, int paramAnonymousInt)
    {
      int i = 1;
      if ((paramAnonymousView != TitleHelper.this.mTitleView) && (paramAnonymousInt == 33)) {
        return TitleHelper.this.mTitleView;
      }
      if (ViewCompat.getLayoutDirection(paramAnonymousView) == 1) {
        if (i == 0) {
          break label80;
        }
      }
      label80:
      for (i = 17;; i = 66)
      {
        if ((!TitleHelper.this.mTitleView.hasFocus()) || ((paramAnonymousInt != 130) && (paramAnonymousInt != i))) {
          break label86;
        }
        return TitleHelper.this.mSceneRoot;
        i = 0;
        break;
      }
      label86:
      return null;
    }
  };
  ViewGroup mSceneRoot;
  private Object mSceneWithTitle;
  private Object mSceneWithoutTitle;
  private Object mTitleDownTransition;
  private Object mTitleUpTransition;
  View mTitleView;
  
  public TitleHelper(ViewGroup paramViewGroup, View paramView)
  {
    if ((paramViewGroup == null) || (paramView == null)) {
      throw new IllegalArgumentException("Views may not be null");
    }
    this.mSceneRoot = paramViewGroup;
    this.mTitleView = paramView;
    createTransitions();
  }
  
  private void createTransitions()
  {
    this.mTitleUpTransition = LeanbackTransitionHelper.loadTitleOutTransition(this.mSceneRoot.getContext());
    this.mTitleDownTransition = LeanbackTransitionHelper.loadTitleInTransition(this.mSceneRoot.getContext());
    this.mSceneWithTitle = TransitionHelper.createScene(this.mSceneRoot, new Runnable()
    {
      public void run()
      {
        TitleHelper.this.mTitleView.setVisibility(0);
      }
    });
    this.mSceneWithoutTitle = TransitionHelper.createScene(this.mSceneRoot, new Runnable()
    {
      public void run()
      {
        TitleHelper.this.mTitleView.setVisibility(4);
      }
    });
  }
  
  public BrowseFrameLayout.OnFocusSearchListener getOnFocusSearchListener()
  {
    return this.mOnFocusSearchListener;
  }
  
  public ViewGroup getSceneRoot()
  {
    return this.mSceneRoot;
  }
  
  public View getTitleView()
  {
    return this.mTitleView;
  }
  
  public void showTitle(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      TransitionHelper.runTransition(this.mSceneWithTitle, this.mTitleDownTransition);
      return;
    }
    TransitionHelper.runTransition(this.mSceneWithoutTitle, this.mTitleUpTransition);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/TitleHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */