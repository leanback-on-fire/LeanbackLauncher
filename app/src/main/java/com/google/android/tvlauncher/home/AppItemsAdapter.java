package com.google.android.tvlauncher.home;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Outline;
import android.net.Uri;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewOutlineProvider;
import android.view.ViewPropertyAnimator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.tvlauncher.analytics.EventLogger;
import com.google.android.tvlauncher.analytics.LogEvent;
import com.google.android.tvlauncher.analytics.LogUtils;
import com.google.android.tvlauncher.analytics.UserActionEvent;
import com.google.android.tvlauncher.appsview.AppsManager;
import com.google.android.tvlauncher.appsview.AppsManager.HomeScreenItemsChangeListener;
import com.google.android.tvlauncher.appsview.LaunchItem;
import com.google.android.tvlauncher.appsview.OnAppsViewActionListener;
import com.google.android.tvlauncher.util.ContextMenu;
import com.google.android.tvlauncher.util.ScaleFocusHandler;
import com.google.android.tvlauncher.util.ScaleFocusHandler.PivotProvider;
import java.util.List;

class AppItemsAdapter
  extends RecyclerView.Adapter<BaseViewHolder>
{
  private static final boolean DEBUG = false;
  private static final String PAYLOAD_STATE = "PAYLOAD_STATE";
  private static final String TAG = "AppsRVAdapter";
  private static final int TYPE_APP = 0;
  private static final int TYPE_MORE = 1;
  private int mAppState = 0;
  private final float mBannerTitleAlpha;
  private final AppsManager mDataManager;
  private final int mDefaultAboveSelectedBottomMargin;
  private final int mDefaultBottomMargin;
  private final int mDefaultHorizontalMargin;
  private final int mDefaultTopMargin;
  private AppsRowEditModeActionCallbacks mEditModeCallbacks;
  private final EventLogger mEventLogger;
  private final ScaleFocusHandler mFocusHandlerTemplate;
  private final int mSelectedBottomMargin;
  private final int mSelectedTopMargin;
  private final int mZoomedOutHorizontalMargin;
  private final int mZoomedOutVerticalMargin;
  
  AppItemsAdapter(Context paramContext, EventLogger paramEventLogger)
  {
    this.mDataManager = AppsManager.getInstance(paramContext);
    this.mEventLogger = paramEventLogger;
    this.mDataManager.setHomeScreenItemsChangeListener(new AppsManager.HomeScreenItemsChangeListener()
    {
      public void onHomeScreenItemsChanged(List<LaunchItem> paramAnonymousList)
      {
        AppItemsAdapter.this.notifyDataSetChanged();
      }
      
      public void onHomeScreenItemsLoaded()
      {
        AppItemsAdapter.this.notifyDataSetChanged();
      }
      
      public void onHomeScreenItemsSwapped(int paramAnonymousInt1, int paramAnonymousInt2)
      {
        AppItemsAdapter.this.notifyItemMoved(paramAnonymousInt1, paramAnonymousInt2);
      }
    });
    paramContext = paramContext.getResources();
    this.mFocusHandlerTemplate = new ScaleFocusHandler(paramContext.getInteger(2131689484), paramContext.getFraction(2131886084, 1, 1), paramContext.getDimension(2131558592));
    this.mDefaultTopMargin = paramContext.getDimensionPixelSize(2131558591);
    this.mDefaultBottomMargin = paramContext.getDimensionPixelSize(2131558589);
    this.mDefaultAboveSelectedBottomMargin = paramContext.getDimensionPixelSize(2131558588);
    this.mDefaultHorizontalMargin = paramContext.getDimensionPixelSize(2131558590);
    this.mSelectedTopMargin = paramContext.getDimensionPixelSize(2131558596);
    this.mSelectedBottomMargin = paramContext.getDimensionPixelSize(2131558595);
    this.mZoomedOutHorizontalMargin = paramContext.getDimensionPixelSize(2131558598);
    this.mZoomedOutVerticalMargin = paramContext.getDimensionPixelSize(2131558599);
    this.mBannerTitleAlpha = paramContext.getFraction(2131886080, 1, 1);
    setHasStableIds(true);
    if (!this.mDataManager.areItemsLoaded()) {
      this.mDataManager.refreshLaunchItems();
    }
  }
  
  private static String stateToString(int paramInt)
  {
    String str;
    switch (paramInt)
    {
    default: 
      str = "STATE_UNKNOWN";
    }
    for (;;)
    {
      return str + " (" + paramInt + ")";
      str = "DEFAULT";
      continue;
      str = "DEFAULT_ABOVE_SELECTED";
      continue;
      str = "DEFAULT_SELECTED";
      continue;
      str = "ZOOMED_OUT";
    }
  }
  
  int getAppState()
  {
    return this.mAppState;
  }
  
  public int getItemCount()
  {
    if (this.mDataManager.areItemsLoaded()) {
      return this.mDataManager.getHomeScreenItems().size();
    }
    return 0;
  }
  
  public long getItemId(int paramInt)
  {
    return ((LaunchItem)this.mDataManager.getHomeScreenItems().get(paramInt)).getPackageName().hashCode();
  }
  
  public int getItemViewType(int paramInt)
  {
    LaunchItem localLaunchItem = (LaunchItem)this.mDataManager.getHomeScreenItems().get(paramInt);
    if (this.mDataManager.isFavorite(localLaunchItem)) {
      return 0;
    }
    return 1;
  }
  
  public void onBindViewHolder(BaseViewHolder paramBaseViewHolder, int paramInt)
  {
    paramBaseViewHolder.setItem((LaunchItem)this.mDataManager.getHomeScreenItems().get(paramInt));
    paramBaseViewHolder.updateSize();
    paramBaseViewHolder.mFocusHandler.resetFocusedState();
  }
  
  public void onBindViewHolder(BaseViewHolder paramBaseViewHolder, int paramInt, List<Object> paramList)
  {
    if (paramList.isEmpty())
    {
      onBindViewHolder(paramBaseViewHolder, paramInt);
      return;
    }
    paramBaseViewHolder.updateSize();
  }
  
  public BaseViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
  {
    if (paramInt == 0) {
      return new AppViewHolder(LayoutInflater.from(paramViewGroup.getContext()).inflate(2130968743, paramViewGroup, false));
    }
    if (paramInt == 1) {
      return new AddMoreViewHolder(LayoutInflater.from(paramViewGroup.getContext()).inflate(2130968742, paramViewGroup, false));
    }
    Log.e("AppsRVAdapter", "Trying to add a view type that does not exist to the Favorites row : " + paramInt);
    return null;
  }
  
  void setAppState(int paramInt)
  {
    if (this.mAppState != paramInt)
    {
      this.mAppState = paramInt;
      notifyItemRangeChanged(0, getItemCount(), "PAYLOAD_STATE");
    }
  }
  
  void setAppsRowEditModeActionCallbacks(AppsRowEditModeActionCallbacks paramAppsRowEditModeActionCallbacks)
  {
    this.mEditModeCallbacks = paramAppsRowEditModeActionCallbacks;
  }
  
  private class AddMoreViewHolder
    extends AppItemsAdapter.BaseViewHolder
    implements View.OnClickListener
  {
    private Intent mIntent;
    
    AddMoreViewHolder(View paramView)
    {
      super(paramView);
      this.itemView.setOnClickListener(this);
      final int i = this.itemView.getResources().getDimensionPixelSize(2131558510);
      this.mImageView.setOutlineProvider(new ViewOutlineProvider()
      {
        public void getOutline(View paramAnonymousView, Outline paramAnonymousOutline)
        {
          paramAnonymousOutline.setRoundRect(0, 0, paramAnonymousView.getWidth(), paramAnonymousView.getHeight(), i);
        }
      });
      this.mImageView.setClipToOutline(true);
      this.itemView.setOutlineProvider(new ViewOutlineProvider()
      {
        public void getOutline(View paramAnonymousView, Outline paramAnonymousOutline)
        {
          paramAnonymousOutline.setRoundRect(0, 0, paramAnonymousView.getResources().getDimensionPixelSize(2131558597), paramAnonymousView.getResources().getDimensionPixelSize(2131558594), i);
        }
      });
      this.mFocusHandler.setPivotProvider(new ScaleFocusHandler.PivotProvider()
      {
        public int getPivot()
        {
          if (AppItemsAdapter.AddMoreViewHolder.this.getAdapterPosition() == 0) {
            return 1;
          }
          return 0;
        }
        
        public boolean shouldAnimate()
        {
          return false;
        }
      });
    }
    
    public void onClick(View paramView)
    {
      try
      {
        this.itemView.getContext().startActivity(this.mIntent);
        return;
      }
      catch (ActivityNotFoundException paramView)
      {
        Toast.makeText(this.itemView.getContext(), 2131492990, 0).show();
        Log.e("AppsRVAdapter", "Cannot start activity : " + paramView);
      }
    }
    
    public void setItem(LaunchItem paramLaunchItem)
    {
      super.setItem(paramLaunchItem);
      this.mIntent = paramLaunchItem.getIntent();
      this.mImageView.setImageDrawable(paramLaunchItem.getItemDrawable());
    }
  }
  
  private class AppViewHolder
    extends AppItemsAdapter.BaseViewHolder
    implements OnAppsViewActionListener
  {
    private final FavoriteAppBannerView mBanner;
    private LaunchItem mItem;
    
    AppViewHolder(View paramView)
    {
      super(paramView);
      this.mBanner = ((FavoriteAppBannerView)paramView);
      this.mFocusHandler.setPivotProvider(new ScaleFocusHandler.PivotProvider()
      {
        public int getPivot()
        {
          if (AppItemsAdapter.AppViewHolder.this.getAdapterPosition() == 0) {
            return 1;
          }
          return 0;
        }
        
        public boolean shouldAnimate()
        {
          return (AppItemsAdapter.AppViewHolder.this.mBanner.isBeingMoved()) && (AppItemsAdapter.AppViewHolder.this.getAdapterPosition() <= 1);
        }
      });
    }
    
    protected void handleFocusChange(boolean paramBoolean)
    {
      super.handleFocusChange(paramBoolean);
      if ((this.mBanner.isBeingMoved()) && (!paramBoolean)) {
        onExitEditModeView();
      }
      ContextMenu localContextMenu = this.mBanner.getAppMenu();
      if ((localContextMenu != null) && (localContextMenu.isShowing())) {
        localContextMenu.forceDismiss();
      }
    }
    
    public void onExitEditModeView()
    {
      AppItemsAdapter.this.mEditModeCallbacks.onExitEditMode();
      this.mBanner.setIsBeingMoved(false);
    }
    
    public void onLaunchApp(Intent paramIntent)
    {
      try
      {
        AppItemsAdapter.this.mEventLogger.log(new UserActionEvent("start_app").putParameter("placement", "home_apps_row").putParameter("package_name", LogUtils.getPackage(this.mItem.getIntent())).putParameter("index", getAdapterPosition()));
        this.mBanner.getContext().startActivity(paramIntent);
        return;
      }
      catch (ActivityNotFoundException paramIntent)
      {
        Toast.makeText(this.mBanner.getContext(), 2131492990, 0).show();
        Log.e("AppsRVAdapter", "Cannot start activity : " + paramIntent);
      }
    }
    
    public void onShowAppInfo(String paramString)
    {
      AppItemsAdapter.this.mEventLogger.log(new UserActionEvent("get_app_info").putParameter("package_name", paramString));
      Intent localIntent = new Intent();
      localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
      localIntent.setData(Uri.parse("package:" + paramString));
      this.mBanner.getContext().startActivity(localIntent);
    }
    
    public void onShowEditModeView(int paramInt1, int paramInt2)
    {
      AppItemsAdapter.this.mEditModeCallbacks.onEnterEditMode();
      this.mBanner.setIsBeingMoved(true);
    }
    
    public void onStoreLaunch(Intent paramIntent) {}
    
    public void onToggleFavorite(LaunchItem paramLaunchItem)
    {
      AppItemsAdapter.this.mEventLogger.log(new UserActionEvent("unfavorite_app").putParameter("package_name", paramLaunchItem.getPackageName()));
      AppItemsAdapter.this.mDataManager.removeFromFavorites(paramLaunchItem);
    }
    
    public void onUninstallApp(String paramString)
    {
      AppItemsAdapter.this.mEventLogger.log(new UserActionEvent("uninstall_app").putParameter("package_name", paramString));
      Intent localIntent = new Intent();
      localIntent.setAction("android.intent.action.UNINSTALL_PACKAGE");
      localIntent.setData(Uri.parse("package:" + paramString));
      this.mBanner.getContext().startActivity(localIntent);
    }
    
    public void setItem(LaunchItem paramLaunchItem)
    {
      super.setItem(paramLaunchItem);
      this.mItem = paramLaunchItem;
      this.mBanner.setAppBannerItems(this.mItem, false, this);
    }
  }
  
  class BaseViewHolder
    extends RecyclerView.ViewHolder
  {
    final ScaleFocusHandler mFocusHandler;
    ImageView mImageView;
    TextView mTitleView;
    
    BaseViewHolder(View paramView)
    {
      super();
      this.mTitleView = ((TextView)paramView.findViewById(2131951785));
      this.mImageView = ((ImageView)paramView.findViewById(2131951784));
      this.mFocusHandler = new ScaleFocusHandler(AppItemsAdapter.this.mFocusHandlerTemplate);
      this.mFocusHandler.setView(paramView);
      this.mFocusHandler.setOnFocusChangeListener(new View.OnFocusChangeListener()
      {
        public void onFocusChange(View paramAnonymousView, boolean paramAnonymousBoolean)
        {
          AppItemsAdapter.BaseViewHolder.this.handleFocusChange(paramAnonymousBoolean);
        }
      });
      int i = paramView.getResources().getDimensionPixelSize(2131558498);
      this.mFocusHandler.setPivotVerticalShift(-i / 2);
    }
    
    protected void handleFocusChange(boolean paramBoolean)
    {
      if (this.mTitleView != null)
      {
        this.mTitleView.setSelected(this.itemView.hasFocus());
        this.itemView.postDelayed(new Runnable()
        {
          public void run()
          {
            ViewPropertyAnimator localViewPropertyAnimator = AppItemsAdapter.BaseViewHolder.this.mTitleView.animate();
            if (AppItemsAdapter.BaseViewHolder.this.itemView.hasFocus()) {}
            for (float f = AppItemsAdapter.this.mBannerTitleAlpha;; f = 0.0F)
            {
              localViewPropertyAnimator.alpha(f).setDuration(AppItemsAdapter.this.mFocusHandlerTemplate.getAnimationDuration()).setListener(new AnimatorListenerAdapter()
              {
                public void onAnimationEnd(Animator paramAnonymous2Animator)
                {
                  if (AppItemsAdapter.BaseViewHolder.this.mTitleView.getAlpha() == 0.0F) {
                    AppItemsAdapter.BaseViewHolder.this.mTitleView.setVisibility(4);
                  }
                }
                
                public void onAnimationStart(Animator paramAnonymous2Animator)
                {
                  AppItemsAdapter.BaseViewHolder.this.mTitleView.setVisibility(0);
                }
              });
              return;
            }
          }
        }, 60L);
      }
    }
    
    public void setItem(LaunchItem paramLaunchItem) {}
    
    void updateSize()
    {
      ViewGroup.MarginLayoutParams localMarginLayoutParams = (ViewGroup.MarginLayoutParams)this.itemView.getLayoutParams();
      switch (AppItemsAdapter.this.mAppState)
      {
      }
      for (;;)
      {
        this.itemView.setLayoutParams(localMarginLayoutParams);
        return;
        localMarginLayoutParams.setMargins(0, AppItemsAdapter.this.mDefaultTopMargin, 0, AppItemsAdapter.this.mDefaultBottomMargin);
        localMarginLayoutParams.setMarginEnd(AppItemsAdapter.this.mDefaultHorizontalMargin);
        continue;
        localMarginLayoutParams.setMargins(0, AppItemsAdapter.this.mDefaultTopMargin, 0, AppItemsAdapter.this.mDefaultAboveSelectedBottomMargin);
        localMarginLayoutParams.setMarginEnd(AppItemsAdapter.this.mDefaultHorizontalMargin);
        continue;
        localMarginLayoutParams.setMargins(0, AppItemsAdapter.this.mSelectedTopMargin, 0, AppItemsAdapter.this.mSelectedBottomMargin);
        localMarginLayoutParams.setMarginEnd(AppItemsAdapter.this.mDefaultHorizontalMargin);
        continue;
        localMarginLayoutParams.setMargins(0, AppItemsAdapter.this.mZoomedOutVerticalMargin, 0, AppItemsAdapter.this.mZoomedOutVerticalMargin);
        localMarginLayoutParams.setMarginEnd(AppItemsAdapter.this.mZoomedOutHorizontalMargin);
      }
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/home/AppItemsAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */