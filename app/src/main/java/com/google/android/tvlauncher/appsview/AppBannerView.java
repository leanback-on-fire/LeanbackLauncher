package com.google.android.tvlauncher.appsview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Outline;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnLongClickListener;
import android.view.ViewOutlineProvider;
import android.view.ViewPropertyAnimator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.tvlauncher.util.ContextMenu;
import com.google.android.tvlauncher.util.ContextMenu.OnItemClickListener;
import com.google.android.tvlauncher.util.ContextMenuItem;
import com.google.android.tvlauncher.util.Util;

public class AppBannerView
  extends BaseBannerView
  implements View.OnFocusChangeListener, View.OnClickListener, View.OnLongClickListener, ContextMenu.OnItemClickListener
{
  protected static final int MENU_FAVORITE = 2;
  protected static final int MENU_INFO = 3;
  protected static final int MENU_MOVE = 1;
  protected static final int MENU_PRIMARY_ACTION = 0;
  protected static final int MENU_UNINSTALL = 4;
  private static final String TAG = "BannerView";
  protected ContextMenu mAppMenu;
  protected final AppsManager mAppsManager;
  protected final float mBannerTitleAlpha;
  private Drawable mFavoriteIcon;
  private final String mFavoriteText;
  private final Handler mHandler;
  protected boolean mIsOemRowBanner;
  protected OnAppsViewActionListener mOnAppsViewActionListener;
  private InstallStateOverlayHelper mOverlayHelper;
  protected TextView mTitleView;
  private Drawable mUnfavoriteIcon;
  private final String mUnfavoriteText;
  
  public AppBannerView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public AppBannerView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public AppBannerView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    setOnFocusChangeListener(this);
    setOnClickListener(this);
    setOnLongClickListener(this);
    this.mFavoriteText = getContext().getString(2131492898);
    this.mUnfavoriteText = getContext().getString(2131492902);
    this.mFavoriteIcon = getContext().getDrawable(2130837641);
    this.mUnfavoriteIcon = getContext().getDrawable(2130837646);
    this.mAppsManager = AppsManager.getInstance(getContext());
    this.mHandler = new Handler();
    this.mBannerTitleAlpha = getContext().getResources().getFraction(2131886080, 1, 1);
  }
  
  private void onFavorite()
  {
    if (this.mOnAppsViewActionListener != null) {
      this.mOnAppsViewActionListener.onToggleFavorite(this.mItem);
    }
  }
  
  private void onPrimaryAction()
  {
    if ((this.mItem != null) && (this.mItem.getIntent() != null) && (this.mOnAppsViewActionListener != null)) {
      this.mOnAppsViewActionListener.onLaunchApp(this.mItem.getIntent());
    }
    do
    {
      return;
      Toast.makeText(getContext(), 2131492990, 0).show();
      if (this.mItem == null)
      {
        Log.e("BannerView", "Cannot start activity: item was null");
        return;
      }
      if (this.mItem.getIntent() == null)
      {
        Log.e("BannerView", "Cannot start activity: intent was null for " + this.mItem);
        return;
      }
    } while (this.mOnAppsViewActionListener != null);
    Log.e("BannerView", "Cannot start activity: no listener for item " + this.mItem);
  }
  
  private void onShowInfoView()
  {
    if (this.mOnAppsViewActionListener != null) {
      this.mOnAppsViewActionListener.onShowAppInfo(this.mItem.getPackageName());
    }
  }
  
  private void onShowUninstall()
  {
    if (this.mOnAppsViewActionListener != null) {
      this.mOnAppsViewActionListener.onUninstallApp(this.mItem.getPackageName());
    }
  }
  
  public ContextMenu getAppMenu()
  {
    return this.mAppMenu;
  }
  
  public void onClick(View paramView)
  {
    if (Util.isAccessibilityEnabled(getContext()))
    {
      onLongClick(paramView);
      return;
    }
    onPrimaryAction();
  }
  
  protected void onEnterEditMode()
  {
    OnAppsViewActionListener localOnAppsViewActionListener;
    if ((!this.mIsOemRowBanner) && (this.mItem != null) && (this.mOnAppsViewActionListener != null))
    {
      localOnAppsViewActionListener = this.mOnAppsViewActionListener;
      if (!this.mItem.isGame()) {
        break label60;
      }
    }
    label60:
    for (int i = 1;; i = 0)
    {
      localOnAppsViewActionListener.onShowEditModeView(i, AppsManager.getInstance(getContext()).getOrderedPosition(this.mItem));
      return;
    }
  }
  
  protected void onFinishInflate()
  {
    super.onFinishInflate();
    this.mTitleView = ((TextView)findViewById(2131951785));
  }
  
  public void onFocusChange(View paramView, boolean paramBoolean)
  {
    float f3 = 0.0F;
    if ((this.mAppMenu != null) && (this.mAppMenu.isShowing())) {
      this.mAppMenu.forceDismiss();
    }
    float f1;
    if (paramBoolean)
    {
      f1 = this.mFocusedScale;
      if (!paramBoolean) {
        break label128;
      }
    }
    label128:
    for (float f2 = this.mFocusedZDelta;; f2 = 0.0F)
    {
      if (paramBoolean) {
        f3 = this.mBannerTitleAlpha;
      }
      this.mTitleView.setSelected(paramBoolean);
      paramView.animate().z(f2).scaleX(f1).scaleY(f1).setDuration(this.mAnimDuration);
      this.mTitleView.animate().alpha(f3).setDuration(this.mAnimDuration).setListener(new AnimatorListenerAdapter()
      {
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          if (AppBannerView.this.mTitleView.getAlpha() == 0.0F) {
            AppBannerView.this.mTitleView.setVisibility(4);
          }
        }
        
        public void onAnimationStart(Animator paramAnonymousAnimator)
        {
          AppBannerView.this.mTitleView.setVisibility(0);
        }
      });
      return;
      f1 = 1.0F;
      break;
    }
  }
  
  public void onItemClick(ContextMenuItem paramContextMenuItem)
  {
    switch (paramContextMenuItem.getId())
    {
    default: 
      return;
    case 0: 
      onPrimaryAction();
      return;
    case 3: 
      onShowInfoView();
      return;
    case 2: 
      onFavorite();
      return;
    case 1: 
      onEnterEditMode();
      return;
    }
    onShowUninstall();
  }
  
  public boolean onLongClick(View paramView)
  {
    boolean bool = this.mAppsManager.isFavorite(this.mItem);
    this.mAppMenu = new ContextMenu((Activity)paramView.getContext(), this.mBannerImage, this.mCornerRadius, getScaleX(), getScaleY());
    this.mAppMenu.addItem(new ContextMenuItem(0, paramView.getContext().getString(2131492901), paramView.getContext().getDrawable(2130837645)));
    this.mAppMenu.addItem(new ContextMenuItem(1, paramView.getContext().getString(2131492900), paramView.getContext().getDrawable(2130837643)));
    ContextMenu localContextMenu = this.mAppMenu;
    String str;
    if (bool)
    {
      str = this.mUnfavoriteText;
      if (!bool) {
        break label256;
      }
    }
    label256:
    for (Drawable localDrawable = this.mUnfavoriteIcon;; localDrawable = this.mFavoriteIcon)
    {
      localContextMenu.addItem(new ContextMenuItem(2, str, localDrawable));
      this.mAppMenu.addItem(new ContextMenuItem(3, paramView.getContext().getString(2131492899), paramView.getContext().getDrawable(2130837642)));
      this.mAppMenu.addItem(new ContextMenuItem(4, paramView.getContext().getString(2131492903), paramView.getContext().getDrawable(2130837647)));
      this.mAppMenu.setOnMenuItemClickListener(this);
      setMenuItems();
      this.mAppMenu.show();
      return true;
      str = this.mFavoriteText;
      break;
    }
  }
  
  public void setAppBannerItems(LaunchItem paramLaunchItem, boolean paramBoolean, OnAppsViewActionListener paramOnAppsViewActionListener)
  {
    setLaunchItem(paramLaunchItem);
    this.mTitleView.setText(paramLaunchItem.getLabel());
    this.mIsOemRowBanner = paramBoolean;
    this.mOnAppsViewActionListener = paramOnAppsViewActionListener;
    if (this.mItem.isInstalling())
    {
      if (this.mOverlayHelper == null) {
        this.mOverlayHelper = new InstallStateOverlayHelper(this);
      }
      this.mOverlayHelper.updateOverlay(this.mItem);
    }
    while (this.mOverlayHelper == null) {
      return;
    }
    this.mOverlayHelper.removeOverlay();
  }
  
  protected void setMenuItems()
  {
    boolean bool2 = true;
    ContextMenuItem localContextMenuItem;
    if (this.mItem.isGame())
    {
      bool1 = this.mAppsManager.isOnlyGame(this.mItem);
      localContextMenuItem = this.mAppMenu.findItem(1);
      if ((this.mIsOemRowBanner) || (bool1)) {
        break label176;
      }
      bool1 = true;
      label46:
      localContextMenuItem.setEnabled(bool1);
      localContextMenuItem = this.mAppMenu.findItem(4);
      if ((LaunchItem.isSystemApp(getContext(), this.mItem.getPackageName())) || (this.mItem.isInstalling())) {
        break label181;
      }
      bool1 = true;
      label89:
      localContextMenuItem.setEnabled(bool1);
      localContextMenuItem = this.mAppMenu.findItem(2);
      if ((this.mAppsManager.isFavoritesFull()) && (!this.mAppsManager.isFavorite(this.mItem))) {
        break label186;
      }
      bool1 = true;
      label129:
      localContextMenuItem.setEnabled(bool1);
      localContextMenuItem = this.mAppMenu.findItem(3);
      if (this.mItem.isInstalling()) {
        break label191;
      }
    }
    label176:
    label181:
    label186:
    label191:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      localContextMenuItem.setEnabled(bool1);
      return;
      bool1 = this.mAppsManager.isOnlyApp(this.mItem);
      break;
      bool1 = false;
      break label46;
      bool1 = false;
      break label89;
      bool1 = false;
      break label129;
    }
  }
  
  private static class InstallStateOverlayHelper
  {
    private final AppBannerView mBanner;
    private final ImageView mIconView;
    private final View mOverlay;
    private final ProgressBar mProgressBar;
    private final TextView mProgressView;
    private final TextView mStateView;
    
    InstallStateOverlayHelper(AppBannerView paramAppBannerView)
    {
      this.mOverlay = LayoutInflater.from(paramAppBannerView.getContext()).inflate(2130968625, paramAppBannerView, false);
      final float f = paramAppBannerView.getResources().getDimensionPixelSize(2131558510);
      this.mOverlay.setOutlineProvider(new ViewOutlineProvider()
      {
        public void getOutline(View paramAnonymousView, Outline paramAnonymousOutline)
        {
          paramAnonymousOutline.setRoundRect(0, 0, paramAnonymousView.getWidth(), paramAnonymousView.getHeight(), f);
        }
      });
      this.mOverlay.setClipToOutline(true);
      this.mStateView = ((TextView)this.mOverlay.findViewById(2131951847));
      this.mProgressView = ((TextView)this.mOverlay.findViewById(2131951848));
      this.mProgressBar = ((ProgressBar)this.mOverlay.findViewById(2131951849));
      this.mIconView = ((ImageView)this.mOverlay.findViewById(2131951846));
      this.mBanner = paramAppBannerView;
    }
    
    void removeOverlay()
    {
      if (this.mOverlay.getParent() != null) {
        this.mBanner.removeView(this.mOverlay);
      }
    }
    
    void updateOverlay(LaunchItem paramLaunchItem)
    {
      this.mStateView.setText(paramLaunchItem.getInstallStateString(this.mStateView.getContext()));
      this.mProgressView.setText(paramLaunchItem.getInstallProgressString(this.mProgressView.getContext()));
      int i = paramLaunchItem.getInstallProgressPercent();
      if (i == -1) {
        this.mProgressBar.setIndeterminate(true);
      }
      for (;;)
      {
        this.mIconView.setImageDrawable(paramLaunchItem.getItemDrawable());
        if (this.mOverlay.getParent() == null) {
          this.mBanner.addView(this.mOverlay);
        }
        return;
        this.mProgressBar.setProgress(i);
        this.mProgressBar.setIndeterminate(false);
      }
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/appsview/AppBannerView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */