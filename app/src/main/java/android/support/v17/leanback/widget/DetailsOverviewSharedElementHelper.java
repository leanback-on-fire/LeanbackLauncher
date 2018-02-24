package android.support.v17.leanback.widget;

import android.app.Activity;
import android.graphics.Matrix;
import android.os.Handler;
import android.support.v17.leanback.transition.TransitionHelper;
import android.support.v17.leanback.transition.TransitionListener;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnLayoutChangeListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import java.lang.ref.WeakReference;
import java.util.List;

final class DetailsOverviewSharedElementHelper
  extends SharedElementCallback
{
  static final boolean DEBUG = false;
  static final String TAG = "DetailsTransitionHelper";
  Activity mActivityToRunTransition;
  int mRightPanelHeight;
  int mRightPanelWidth;
  private Matrix mSavedMatrix;
  private ImageView.ScaleType mSavedScaleType;
  String mSharedElementName;
  boolean mStartedPostpone;
  DetailsOverviewRowPresenter.ViewHolder mViewHolder;
  
  private void changeImageViewScale(View paramView)
  {
    paramView = (ImageView)paramView;
    ImageView localImageView = this.mViewHolder.mImageView;
    localImageView.setScaleType(paramView.getScaleType());
    if (paramView.getScaleType() == ImageView.ScaleType.MATRIX) {
      localImageView.setImageMatrix(paramView.getImageMatrix());
    }
    updateImageViewAfterScaleTypeChange(localImageView);
  }
  
  private boolean hasImageViewScaleChange(View paramView)
  {
    return paramView instanceof ImageView;
  }
  
  private void restoreImageViewScale()
  {
    if (this.mSavedScaleType != null)
    {
      ImageView localImageView = this.mViewHolder.mImageView;
      localImageView.setScaleType(this.mSavedScaleType);
      if (this.mSavedScaleType == ImageView.ScaleType.MATRIX) {
        localImageView.setImageMatrix(this.mSavedMatrix);
      }
      this.mSavedScaleType = null;
      updateImageViewAfterScaleTypeChange(localImageView);
    }
  }
  
  private void saveImageViewScale()
  {
    if (this.mSavedScaleType == null)
    {
      localObject = this.mViewHolder.mImageView;
      this.mSavedScaleType = ((ImageView)localObject).getScaleType();
      if (this.mSavedScaleType != ImageView.ScaleType.MATRIX) {
        break label44;
      }
    }
    label44:
    for (Object localObject = ((ImageView)localObject).getMatrix();; localObject = null)
    {
      this.mSavedMatrix = ((Matrix)localObject);
      return;
    }
  }
  
  private static void updateImageViewAfterScaleTypeChange(ImageView paramImageView)
  {
    paramImageView.measure(View.MeasureSpec.makeMeasureSpec(paramImageView.getMeasuredWidth(), 1073741824), View.MeasureSpec.makeMeasureSpec(paramImageView.getMeasuredHeight(), 1073741824));
    paramImageView.layout(paramImageView.getLeft(), paramImageView.getTop(), paramImageView.getRight(), paramImageView.getBottom());
  }
  
  void onBindToDrawable(DetailsOverviewRowPresenter.ViewHolder paramViewHolder)
  {
    if (this.mViewHolder != null) {
      ViewCompat.setTransitionName(this.mViewHolder.mOverviewFrame, null);
    }
    this.mViewHolder = paramViewHolder;
    this.mViewHolder.mRightPanel.addOnLayoutChangeListener(new View.OnLayoutChangeListener()
    {
      public void onLayoutChange(View paramAnonymousView, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, int paramAnonymousInt4, int paramAnonymousInt5, int paramAnonymousInt6, int paramAnonymousInt7, int paramAnonymousInt8)
      {
        DetailsOverviewSharedElementHelper.this.mViewHolder.mRightPanel.removeOnLayoutChangeListener(this);
        DetailsOverviewSharedElementHelper.this.mRightPanelWidth = DetailsOverviewSharedElementHelper.this.mViewHolder.mRightPanel.getWidth();
        DetailsOverviewSharedElementHelper.this.mRightPanelHeight = DetailsOverviewSharedElementHelper.this.mViewHolder.mRightPanel.getHeight();
      }
    });
    this.mViewHolder.mRightPanel.postOnAnimation(new Runnable()
    {
      public void run()
      {
        ViewCompat.setTransitionName(DetailsOverviewSharedElementHelper.this.mViewHolder.mOverviewFrame, DetailsOverviewSharedElementHelper.this.mSharedElementName);
        Object localObject = TransitionHelper.getSharedElementEnterTransition(DetailsOverviewSharedElementHelper.this.mActivityToRunTransition.getWindow());
        if (localObject != null) {
          TransitionHelper.addTransitionListener(localObject, new TransitionListener()
          {
            public void onTransitionEnd(Object paramAnonymous2Object)
            {
              if (DetailsOverviewSharedElementHelper.this.mViewHolder.mActionsRow.isFocused()) {
                DetailsOverviewSharedElementHelper.this.mViewHolder.mActionsRow.requestFocus();
              }
              TransitionHelper.removeTransitionListener(paramAnonymous2Object, this);
            }
          });
        }
        DetailsOverviewSharedElementHelper.this.startPostponedEnterTransition();
      }
    });
  }
  
  public void onSharedElementEnd(List<String> paramList, List<View> paramList1, List<View> paramList2)
  {
    if (paramList1.size() < 1) {}
    do
    {
      return;
      paramList = (View)paramList1.get(0);
    } while ((this.mViewHolder == null) || (this.mViewHolder.mOverviewFrame != paramList));
    restoreImageViewScale();
    this.mViewHolder.mActionsRow.setDescendantFocusability(131072);
    this.mViewHolder.mActionsRow.setVisibility(0);
    this.mViewHolder.mActionsRow.setDescendantFocusability(262144);
    this.mViewHolder.mDetailsDescriptionFrame.setVisibility(0);
  }
  
  public void onSharedElementStart(List<String> paramList, List<View> paramList1, List<View> paramList2)
  {
    if (paramList1.size() < 1) {}
    do
    {
      return;
      paramList = (View)paramList1.get(0);
    } while ((this.mViewHolder == null) || (this.mViewHolder.mOverviewFrame != paramList));
    paramList1 = (View)paramList2.get(0);
    if (hasImageViewScaleChange(paramList1))
    {
      saveImageViewScale();
      changeImageViewScale(paramList1);
    }
    paramList1 = this.mViewHolder.mImageView;
    int i = paramList.getWidth();
    int j = paramList.getHeight();
    paramList1.measure(View.MeasureSpec.makeMeasureSpec(i, 1073741824), View.MeasureSpec.makeMeasureSpec(j, 1073741824));
    paramList1.layout(0, 0, i, j);
    paramList = this.mViewHolder.mRightPanel;
    if ((this.mRightPanelWidth != 0) && (this.mRightPanelHeight != 0))
    {
      paramList.measure(View.MeasureSpec.makeMeasureSpec(this.mRightPanelWidth, 1073741824), View.MeasureSpec.makeMeasureSpec(this.mRightPanelHeight, 1073741824));
      paramList.layout(i, paramList.getTop(), this.mRightPanelWidth + i, paramList.getTop() + this.mRightPanelHeight);
    }
    for (;;)
    {
      this.mViewHolder.mActionsRow.setVisibility(4);
      this.mViewHolder.mDetailsDescriptionFrame.setVisibility(4);
      return;
      paramList.offsetLeftAndRight(i - paramList.getLeft());
    }
  }
  
  void setSharedElementEnterTransition(Activity paramActivity, String paramString, long paramLong)
  {
    if (((paramActivity == null) && (!TextUtils.isEmpty(paramString))) || ((paramActivity != null) && (TextUtils.isEmpty(paramString)))) {
      throw new IllegalArgumentException();
    }
    if ((paramActivity == this.mActivityToRunTransition) && (TextUtils.equals(paramString, this.mSharedElementName))) {}
    do
    {
      return;
      if (this.mActivityToRunTransition != null) {
        ActivityCompat.setEnterSharedElementCallback(this.mActivityToRunTransition, null);
      }
      this.mActivityToRunTransition = paramActivity;
      this.mSharedElementName = paramString;
      ActivityCompat.setEnterSharedElementCallback(this.mActivityToRunTransition, this);
      ActivityCompat.postponeEnterTransition(this.mActivityToRunTransition);
    } while (paramLong <= 0L);
    new Handler().postDelayed(new TransitionTimeOutRunnable(this), paramLong);
  }
  
  void startPostponedEnterTransition()
  {
    if (!this.mStartedPostpone)
    {
      ActivityCompat.startPostponedEnterTransition(this.mActivityToRunTransition);
      this.mStartedPostpone = true;
    }
  }
  
  static class TransitionTimeOutRunnable
    implements Runnable
  {
    WeakReference<DetailsOverviewSharedElementHelper> mHelperRef;
    
    TransitionTimeOutRunnable(DetailsOverviewSharedElementHelper paramDetailsOverviewSharedElementHelper)
    {
      this.mHelperRef = new WeakReference(paramDetailsOverviewSharedElementHelper);
    }
    
    public void run()
    {
      DetailsOverviewSharedElementHelper localDetailsOverviewSharedElementHelper = (DetailsOverviewSharedElementHelper)this.mHelperRef.get();
      if (localDetailsOverviewSharedElementHelper == null) {
        return;
      }
      localDetailsOverviewSharedElementHelper.startPostponedEnterTransition();
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/DetailsOverviewSharedElementHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */