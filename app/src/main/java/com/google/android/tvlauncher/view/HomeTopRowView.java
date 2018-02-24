package com.google.android.tvlauncher.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Outline;
import android.media.tv.TvInputManager;
import android.media.tv.TvInputManager.TvInputCallback;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewOutlineProvider;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalFocusChangeListener;
import android.widget.LinearLayout;
import com.google.android.tvlauncher.appsview.AppsManager;
import com.google.android.tvlauncher.home.HomeRow;
import com.google.android.tvlauncher.home.OnHomeRowRemovedListener;
import com.google.android.tvlauncher.home.OnHomeRowSelectedListener;
import com.google.android.tvlauncher.home.OnHomeStateChangeListener;
import com.google.android.tvlauncher.inputs.InputsManager;
import com.google.android.tvlauncher.notifications.NotificationsPanelButtonView;
import com.google.android.tvlauncher.notifications.NotificationsPanelController;
import com.google.android.tvlauncher.notifications.NotificationsTrayAdapter;
import com.google.android.tvlauncher.notifications.NotificationsTrayView;
import java.util.List;

public class HomeTopRowView
  extends LinearLayout
  implements View.OnFocusChangeListener, HomeRow
{
  private Context mContext;
  private int mDefaultItemsContainerHeight;
  private int mDefaultItemsContainerTopMargin;
  private int mDuration;
  private float mFocusedElevation;
  private float mFocusedZoom;
  private final ViewTreeObserver.OnGlobalFocusChangeListener mGlobalFocusChangeListener = new ViewTreeObserver.OnGlobalFocusChangeListener()
  {
    public void onGlobalFocusChanged(View paramAnonymousView1, View paramAnonymousView2)
    {
      if (HomeTopRowView.this.findFocus() == paramAnonymousView2) {
        HomeTopRowView.this.mOnHomeRowSelectedListener.onHomeRowSelected(HomeTopRowView.this);
      }
      if (HomeTopRowView.this.mItemsContainer.findFocus() == paramAnonymousView2) {}
      for (boolean bool = true;; bool = false)
      {
        HomeTopRowView.this.mSearch.animateKeyboardOrb(bool);
        return;
      }
    }
  };
  private View mInputs;
  private InputsCallback mInputsCallback;
  private Handler mInputsHandler;
  private View mItemsContainer;
  private boolean mItemsSelected = false;
  private NotificationsTrayView mNotificationsTray;
  private OnActionListener mOnActionListener;
  private OnHomeRowSelectedListener mOnHomeRowSelectedListener;
  private NotificationsPanelController mPanelController = null;
  private SearchOrbView mSearch;
  private int mSelectedItemsContainerHeight;
  private int mSelectedItemsContainerTopMargin;
  private TvInputManager mTvInputManager;
  private float mUnfocusedElevation;
  
  public HomeTopRowView(Context paramContext)
  {
    super(paramContext);
    init(paramContext);
  }
  
  public HomeTopRowView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    init(paramContext);
  }
  
  public HomeTopRowView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    init(paramContext);
  }
  
  private void init(Context paramContext)
  {
    this.mContext = paramContext;
    paramContext = paramContext.getResources();
    this.mFocusedElevation = paramContext.getDimension(2131559040);
    this.mUnfocusedElevation = paramContext.getDimension(2131559044);
    this.mFocusedZoom = paramContext.getFraction(2131886099, 1, 1);
    this.mDuration = paramContext.getInteger(2131689528);
    this.mDefaultItemsContainerHeight = paramContext.getDimensionPixelSize(2131559045);
    this.mDefaultItemsContainerTopMargin = paramContext.getDimensionPixelSize(2131559046);
    this.mSelectedItemsContainerHeight = paramContext.getDimensionPixelSize(2131559051);
    this.mSelectedItemsContainerTopMargin = paramContext.getDimensionPixelSize(2131559052);
  }
  
  private void updateItemsSelectedState(boolean paramBoolean)
  {
    ViewGroup.MarginLayoutParams localMarginLayoutParams;
    if (this.mItemsSelected != paramBoolean)
    {
      this.mItemsSelected = paramBoolean;
      localMarginLayoutParams = (ViewGroup.MarginLayoutParams)this.mItemsContainer.getLayoutParams();
      if (!this.mItemsSelected) {
        break label56;
      }
      localMarginLayoutParams.height = this.mSelectedItemsContainerHeight;
    }
    for (localMarginLayoutParams.topMargin = this.mSelectedItemsContainerTopMargin;; localMarginLayoutParams.topMargin = this.mDefaultItemsContainerTopMargin)
    {
      this.mItemsContainer.setLayoutParams(localMarginLayoutParams);
      return;
      label56:
      localMarginLayoutParams.height = this.mDefaultItemsContainerHeight;
    }
  }
  
  public NotificationsPanelController getNotificationsPanelController()
  {
    return this.mPanelController;
  }
  
  public NotificationsTrayAdapter getNotificationsTrayAdapter()
  {
    return this.mNotificationsTray.getTrayAdapter();
  }
  
  public View getView()
  {
    return this;
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    this.mTvInputManager.registerCallback(this.mInputsCallback, this.mInputsHandler);
    getViewTreeObserver().addOnGlobalFocusChangeListener(this.mGlobalFocusChangeListener);
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    this.mTvInputManager.unregisterCallback(this.mInputsCallback);
    getViewTreeObserver().removeOnGlobalFocusChangeListener(this.mGlobalFocusChangeListener);
  }
  
  protected void onFinishInflate()
  {
    super.onFinishInflate();
    ViewOutlineProvider local2 = new ViewOutlineProvider()
    {
      public void getOutline(View paramAnonymousView, Outline paramAnonymousOutline)
      {
        paramAnonymousOutline.setOval(0, 0, paramAnonymousView.getMeasuredWidth(), paramAnonymousView.getMeasuredHeight());
      }
    };
    this.mItemsContainer = findViewById(2131951836);
    this.mSearch = ((SearchOrbView)findViewById(2131951828));
    this.mSearch.setOutlineProvider(local2);
    AppsManager.getInstance(getContext()).setSearchPackageChangeListener(this.mSearch, this.mSearch.getSearchPackageName());
    this.mInputs = findViewById(2131951840);
    this.mInputs.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        if (HomeTopRowView.this.mOnActionListener != null) {
          HomeTopRowView.this.mOnActionListener.onShowInputs();
        }
      }
    });
    this.mInputs.setOutlineProvider(local2);
    this.mInputs.setClipToOutline(true);
    this.mTvInputManager = ((TvInputManager)getContext().getSystemService("tv_input"));
    if (!InputsManager.hasInputs(this.mContext)) {
      this.mInputs.setVisibility(8);
    }
    for (;;)
    {
      this.mInputsCallback = new InputsCallback(null);
      this.mInputsHandler = new Handler();
      View localView = findViewById(2131951841);
      localView.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          if (HomeTopRowView.this.mOnActionListener != null) {
            HomeTopRowView.this.mOnActionListener.onStartSettings();
          }
        }
      });
      localView.setOutlineProvider(local2);
      localView.setClipToOutline(true);
      this.mNotificationsTray = ((NotificationsTrayView)findViewById(2131951843));
      updateNotificationsTrayVisibility();
      return;
      this.mInputs.setVisibility(0);
    }
  }
  
  public void onFocusChange(View paramView, boolean paramBoolean)
  {
    float f1;
    if (paramBoolean)
    {
      f1 = this.mFocusedZoom;
      if (!paramBoolean) {
        break label51;
      }
    }
    label51:
    for (float f2 = this.mFocusedElevation;; f2 = this.mUnfocusedElevation)
    {
      paramView.animate().z(f2).scaleX(f1).scaleY(f1).setDuration(this.mDuration);
      return;
      f1 = 1.0F;
      break;
    }
  }
  
  public void setNotificationsPanelController(NotificationsPanelController paramNotificationsPanelController)
  {
    this.mPanelController = paramNotificationsPanelController;
    paramNotificationsPanelController = (NotificationsPanelButtonView)findViewById(2131951837);
    this.mPanelController.setView(paramNotificationsPanelController);
  }
  
  public void setNotificationsTrayAdapter(NotificationsTrayAdapter paramNotificationsTrayAdapter)
  {
    this.mNotificationsTray.setTrayAdapter(paramNotificationsTrayAdapter);
  }
  
  public void setOnActionListener(OnActionListener paramOnActionListener)
  {
    this.mOnActionListener = paramOnActionListener;
  }
  
  public void setOnHomeRowRemovedListener(OnHomeRowRemovedListener paramOnHomeRowRemovedListener) {}
  
  public void setOnHomeRowSelectedListener(OnHomeRowSelectedListener paramOnHomeRowSelectedListener)
  {
    this.mOnHomeRowSelectedListener = paramOnHomeRowSelectedListener;
  }
  
  public void setOnHomeStateChangeListener(OnHomeStateChangeListener paramOnHomeStateChangeListener) {}
  
  public void setState(boolean paramBoolean1, boolean paramBoolean2)
  {
    ViewGroup.MarginLayoutParams localMarginLayoutParams = (ViewGroup.MarginLayoutParams)this.mSearch.getLayoutParams();
    Resources localResources = getResources();
    if (paramBoolean1) {}
    for (int i = 2131559014;; i = 2131559012)
    {
      localMarginLayoutParams.setMarginStart(localResources.getDimensionPixelOffset(i));
      this.mSearch.setLayoutParams(localMarginLayoutParams);
      updateItemsSelectedState(paramBoolean2);
      return;
    }
  }
  
  public void updateNotificationsTrayVisibility()
  {
    this.mNotificationsTray.updateVisibility();
  }
  
  private class InputsCallback
    extends TvInputManager.TvInputCallback
  {
    private InputsCallback() {}
    
    public void onInputAdded(String paramString)
    {
      super.onInputAdded(paramString);
      HomeTopRowView.this.mInputs.setVisibility(0);
    }
    
    public void onInputRemoved(String paramString)
    {
      super.onInputRemoved(paramString);
      if ((HomeTopRowView.this.mTvInputManager != null) && (HomeTopRowView.this.mTvInputManager.getTvInputList().isEmpty()) && (!InputsManager.hasInputs(HomeTopRowView.this.mContext))) {
        HomeTopRowView.this.mInputs.setVisibility(8);
      }
    }
  }
  
  public static abstract interface OnActionListener
  {
    public abstract void onShowInputs();
    
    public abstract void onStartSettings();
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/view/HomeTopRowView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */