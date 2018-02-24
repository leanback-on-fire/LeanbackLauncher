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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.tvlauncher.R;
import com.google.android.tvlauncher.analytics.EventLogger;
import com.google.android.tvlauncher.analytics.LogEvents;
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

class AppItemsAdapter extends Adapter<AppItemsAdapter.BaseViewHolder> {
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

    class BaseViewHolder extends ViewHolder {
        final ScaleFocusHandler mFocusHandler;
        ImageView mImageView;
        TextView mTitleView;

        BaseViewHolder(View v) {
            super(v);
            this.mTitleView = (TextView) v.findViewById(R.id.app_title);
            this.mImageView = (ImageView) v.findViewById(R.id.app_banner_image);
            this.mFocusHandler = new ScaleFocusHandler(AppItemsAdapter.this.mFocusHandlerTemplate);
            this.mFocusHandler.setView(v);
            this.mFocusHandler.setOnFocusChangeListener(new OnFocusChangeListener() {
                public void onFocusChange(View v, boolean hasFocus) {
                    BaseViewHolder.this.handleFocusChange(hasFocus);
                }
            });
            this.mFocusHandler.setPivotVerticalShift((-v.getResources().getDimensionPixelSize(R.dimen.banner_app_title_height)) / 2);
        }

        protected void handleFocusChange(boolean hasFocus) {
            if (this.mTitleView != null) {
                this.mTitleView.setSelected(this.itemView.hasFocus());
                this.itemView.postDelayed(new Runnable() {
                    public void run() {
                        BaseViewHolder.this.mTitleView.animate().alpha(BaseViewHolder.this.itemView.hasFocus() ? AppItemsAdapter.this.mBannerTitleAlpha : 0.0f).setDuration((long) AppItemsAdapter.this.mFocusHandlerTemplate.getAnimationDuration()).setListener(new AnimatorListenerAdapter() {
                            public void onAnimationStart(Animator animation) {
                                BaseViewHolder.this.mTitleView.setVisibility(0);
                            }

                            public void onAnimationEnd(Animator animation) {
                                if (BaseViewHolder.this.mTitleView.getAlpha() == 0.0f) {
                                    BaseViewHolder.this.mTitleView.setVisibility(4);
                                }
                            }
                        });
                    }
                }, 60);
            }
        }

        void updateSize() {
            MarginLayoutParams lp = (MarginLayoutParams) this.itemView.getLayoutParams();
            switch (AppItemsAdapter.this.mAppState) {
                case 0:
                    lp.setMargins(0, AppItemsAdapter.this.mDefaultTopMargin, 0, AppItemsAdapter.this.mDefaultBottomMargin);
                    lp.setMarginEnd(AppItemsAdapter.this.mDefaultHorizontalMargin);
                    break;
                case 1:
                    lp.setMargins(0, AppItemsAdapter.this.mDefaultTopMargin, 0, AppItemsAdapter.this.mDefaultAboveSelectedBottomMargin);
                    lp.setMarginEnd(AppItemsAdapter.this.mDefaultHorizontalMargin);
                    break;
                case 2:
                    lp.setMargins(0, AppItemsAdapter.this.mSelectedTopMargin, 0, AppItemsAdapter.this.mSelectedBottomMargin);
                    lp.setMarginEnd(AppItemsAdapter.this.mDefaultHorizontalMargin);
                    break;
                case 3:
                    lp.setMargins(0, AppItemsAdapter.this.mZoomedOutVerticalMargin, 0, AppItemsAdapter.this.mZoomedOutVerticalMargin);
                    lp.setMarginEnd(AppItemsAdapter.this.mZoomedOutHorizontalMargin);
                    break;
            }
            this.itemView.setLayoutParams(lp);
        }

        public void setItem(LaunchItem item) {
        }
    }

    private class AddMoreViewHolder extends BaseViewHolder implements OnClickListener {
        private Intent mIntent;

        AddMoreViewHolder(View v) {
            super(v);
            this.itemView.setOnClickListener(this);
            final int cornerRadius = this.itemView.getResources().getDimensionPixelSize(R.dimen.card_rounded_corner_radius);
            this.mImageView.setOutlineProvider(new ViewOutlineProvider() {
                public void getOutline(View view, Outline outline) {
                    outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), (float) cornerRadius);
                }
            });
            this.mImageView.setClipToOutline(true);
            this.itemView.setOutlineProvider(new ViewOutlineProvider() {
                public void getOutline(View view, Outline outline) {
                    outline.setRoundRect(0, 0, view.getResources().getDimensionPixelSize(R.dimen.home_app_banner_width), view.getResources().getDimensionPixelSize(R.dimen.home_app_banner_image_height), (float) cornerRadius);
                }
            });
            this.mFocusHandler.setPivotProvider(new PivotProvider() {
                public int getPivot() {
                    return AddMoreViewHolder.this.getAdapterPosition() == 0 ? 1 : 0;
                }

                public boolean shouldAnimate() {
                    return false;
                }
            });
        }

        public void setItem(LaunchItem item) {
            super.setItem(item);
            this.mIntent = item.getIntent();
            this.mImageView.setImageDrawable(item.getItemDrawable());
        }

        public void onClick(View v) {
            try {
                this.itemView.getContext().startActivity(this.mIntent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this.itemView.getContext(), R.string.failed_launch, 0).show();
                Log.e(AppItemsAdapter.TAG, "Cannot start activity : " + e);
            }
        }
    }

    private class AppViewHolder extends BaseViewHolder implements OnAppsViewActionListener {
        private final FavoriteAppBannerView mBanner;
        private LaunchItem mItem;

        AppViewHolder(View v) {
            super(v);
            this.mBanner = (FavoriteAppBannerView) v;
            this.mFocusHandler.setPivotProvider(new PivotProvider() {
                public int getPivot() {
                    return AppViewHolder.this.getAdapterPosition() == 0 ? 1 : 0;
                }

                public boolean shouldAnimate() {
                    return AppViewHolder.this.mBanner.isBeingMoved() && AppViewHolder.this.getAdapterPosition() <= 1;
                }
            });
        }

        public void setItem(LaunchItem item) {
            super.setItem(item);
            this.mItem = item;
            this.mBanner.setAppBannerItems(this.mItem, false, this);
        }

        public void onShowEditModeView(int editModeType, int focusedAppPosition) {
            AppItemsAdapter.this.mEditModeCallbacks.onEnterEditMode();
            this.mBanner.setIsBeingMoved(true);
        }

        public void onExitEditModeView() {
            AppItemsAdapter.this.mEditModeCallbacks.onExitEditMode();
            this.mBanner.setIsBeingMoved(false);
        }

        public void onShowAppInfo(String packageName) {
            AppItemsAdapter.this.mEventLogger.log(new UserActionEvent(LogEvents.GET_APP_INFO).putParameter("package_name", packageName));
            Intent intent = new Intent();
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.parse("package:" + packageName));
            this.mBanner.getContext().startActivity(intent);
        }

        public void onUninstallApp(String packageName) {
            AppItemsAdapter.this.mEventLogger.log(new UserActionEvent(LogEvents.UNINSTALL_APP).putParameter("package_name", packageName));
            Intent intent = new Intent();
            intent.setAction("android.intent.action.UNINSTALL_PACKAGE");
            intent.setData(Uri.parse("package:" + packageName));
            this.mBanner.getContext().startActivity(intent);
        }

        public void onLaunchApp(Intent intent) {
            try {
                AppItemsAdapter.this.mEventLogger.log(new UserActionEvent(LogEvents.START_APP).putParameter("placement", LogEvents.APP_PLACEMENT_HOME_APPS_ROW).putParameter("package_name", LogUtils.getPackage(this.mItem.getIntent())).putParameter(LogEvents.PARAMETER_INDEX, getAdapterPosition()));
                this.mBanner.getContext().startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this.mBanner.getContext(), R.string.failed_launch, 0).show();
                Log.e(AppItemsAdapter.TAG, "Cannot start activity : " + e);
            }
        }

        public void onToggleFavorite(LaunchItem item) {
            AppItemsAdapter.this.mEventLogger.log(new UserActionEvent(LogEvents.UNFAVORITE_APP).putParameter("package_name", item.getPackageName()));
            AppItemsAdapter.this.mDataManager.removeFromFavorites(item);
        }

        public void onStoreLaunch(Intent intent) {
        }

        protected void handleFocusChange(boolean hasFocus) {
            super.handleFocusChange(hasFocus);
            if (this.mBanner.isBeingMoved() && !hasFocus) {
                onExitEditModeView();
            }
            ContextMenu menu = this.mBanner.getAppMenu();
            if (menu != null && menu.isShowing()) {
                menu.forceDismiss();
            }
        }
    }

    private static String stateToString(int state) {
        String stateString;
        switch (state) {
            case 0:
                stateString = "DEFAULT";
                break;
            case 1:
                stateString = "DEFAULT_ABOVE_SELECTED";
                break;
            case 2:
                stateString = "DEFAULT_SELECTED";
                break;
            case 3:
                stateString = "ZOOMED_OUT";
                break;
            default:
                stateString = "STATE_UNKNOWN";
                break;
        }
        return stateString + " (" + state + ")";
    }

    AppItemsAdapter(Context context, EventLogger eventLogger) {
        this.mDataManager = AppsManager.getInstance(context);
        this.mEventLogger = eventLogger;
        this.mDataManager.setHomeScreenItemsChangeListener(new HomeScreenItemsChangeListener() {
            public void onHomeScreenItemsLoaded() {
                AppItemsAdapter.this.notifyDataSetChanged();
            }

            public void onHomeScreenItemsChanged(List<LaunchItem> list) {
                AppItemsAdapter.this.notifyDataSetChanged();
            }

            public void onHomeScreenItemsSwapped(int from, int to) {
                AppItemsAdapter.this.notifyItemMoved(from, to);
            }
        });
        Resources resources = context.getResources();
        this.mFocusHandlerTemplate = new ScaleFocusHandler(resources.getInteger(R.integer.home_app_banner_focused_animation_duration_ms), resources.getFraction(R.fraction.home_app_banner_focused_scale, 1, 1), resources.getDimension(R.dimen.home_app_banner_focused_elevation));
        this.mDefaultTopMargin = resources.getDimensionPixelSize(R.dimen.home_app_banner_default_margin_top);
        this.mDefaultBottomMargin = resources.getDimensionPixelSize(R.dimen.home_app_banner_default_margin_bottom);
        this.mDefaultAboveSelectedBottomMargin = resources.getDimensionPixelSize(R.dimen.home_app_banner_default_above_selected_margin_bottom);
        this.mDefaultHorizontalMargin = resources.getDimensionPixelSize(R.dimen.home_app_banner_default_margin_horizontal);
        this.mSelectedTopMargin = resources.getDimensionPixelSize(R.dimen.home_app_banner_selected_margin_top);
        this.mSelectedBottomMargin = resources.getDimensionPixelSize(R.dimen.home_app_banner_selected_margin_bottom);
        this.mZoomedOutHorizontalMargin = resources.getDimensionPixelSize(R.dimen.home_app_banner_zoomed_out_margin_horizontal);
        this.mZoomedOutVerticalMargin = resources.getDimensionPixelSize(R.dimen.home_app_banner_zoomed_out_margin_vertical);
        this.mBannerTitleAlpha = resources.getFraction(R.fraction.banner_app_title_alpha, 1, 1);
        setHasStableIds(true);
        if (!this.mDataManager.areItemsLoaded()) {
            this.mDataManager.refreshLaunchItems();
        }
    }

    int getAppState() {
        return this.mAppState;
    }

    void setAppState(int state) {
        if (this.mAppState != state) {
            this.mAppState = state;
            notifyItemRangeChanged(0, getItemCount(), PAYLOAD_STATE);
        }
    }

    void setAppsRowEditModeActionCallbacks(AppsRowEditModeActionCallbacks callbacks) {
        this.mEditModeCallbacks = callbacks;
    }

    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            return new AppViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_home_app, parent, false));
        }
        if (viewType == 1) {
            return new AddMoreViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_home_add_more_app, parent, false));
        }
        Log.e(TAG, "Trying to add a view type that does not exist to the Favorites row : " + viewType);
        return null;
    }

    public int getItemViewType(int position) {
        if (this.mDataManager.isFavorite((LaunchItem) this.mDataManager.getHomeScreenItems().get(position))) {
            return 0;
        }
        return 1;
    }

    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.setItem((LaunchItem) this.mDataManager.getHomeScreenItems().get(position));
        holder.updateSize();
        holder.mFocusHandler.resetFocusedState();
    }

    public void onBindViewHolder(BaseViewHolder holder, int position, List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            holder.updateSize();
        }
    }

    public int getItemCount() {
        if (this.mDataManager.areItemsLoaded()) {
            return this.mDataManager.getHomeScreenItems().size();
        }
        return 0;
    }

    public long getItemId(int position) {
        return (long) ((LaunchItem) this.mDataManager.getHomeScreenItems().get(position)).getPackageName().hashCode();
    }
}
