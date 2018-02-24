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
import android.view.ViewTreeObserver.OnGlobalFocusChangeListener;
import android.widget.LinearLayout;

import com.google.android.tvlauncher.R;
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

public class HomeTopRowView extends LinearLayout implements OnFocusChangeListener, HomeRow {
    private Context mContext;
    private int mDefaultItemsContainerHeight;
    private int mDefaultItemsContainerTopMargin;
    private int mDuration;
    private float mFocusedElevation;
    private float mFocusedZoom;
    private final OnGlobalFocusChangeListener mGlobalFocusChangeListener = new OnGlobalFocusChangeListener() {
        public void onGlobalFocusChanged(View oldFocus, View newFocus) {
            if (HomeTopRowView.this.findFocus() == newFocus) {
                HomeTopRowView.this.mOnHomeRowSelectedListener.onHomeRowSelected(HomeTopRowView.this);
            }
            HomeTopRowView.this.mSearch.animateKeyboardOrb(HomeTopRowView.this.mItemsContainer.findFocus() == newFocus);
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

    private class InputsCallback extends TvInputCallback {
        private InputsCallback() {
        }

        public void onInputAdded(String inputId) {
            super.onInputAdded(inputId);
            HomeTopRowView.this.mInputs.setVisibility(View.VISIBLE);
        }

        public void onInputRemoved(String inputId) {
            super.onInputRemoved(inputId);
            if (HomeTopRowView.this.mTvInputManager != null && HomeTopRowView.this.mTvInputManager.getTvInputList().isEmpty() && !InputsManager.hasInputs(HomeTopRowView.this.mContext)) {
                HomeTopRowView.this.mInputs.setVisibility(View.GONE);
            }
        }
    }

    public interface OnActionListener {
        void onShowInputs();

        void onStartSettings();
    }

    public HomeTopRowView(Context context) {
        super(context);
        init(context);
    }

    public HomeTopRowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HomeTopRowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        Resources resources = context.getResources();
        this.mFocusedElevation = resources.getDimension(R.dimen.top_row_item_focused_z);
        this.mUnfocusedElevation = resources.getDimension(R.dimen.top_row_item_unfocused_z);
        this.mFocusedZoom = resources.getFraction(R.fraction.top_row_item_focused_zoom, 1, 1);
        this.mDuration = resources.getInteger(R.integer.top_row_scale_duration_ms);
        this.mDefaultItemsContainerHeight = resources.getDimensionPixelSize(R.dimen.top_row_items_container_height);
        this.mDefaultItemsContainerTopMargin = resources.getDimensionPixelSize(R.dimen.top_row_items_container_margin_top);
        this.mSelectedItemsContainerHeight = resources.getDimensionPixelSize(R.dimen.top_row_selected_items_container_height);
        this.mSelectedItemsContainerTopMargin = resources.getDimensionPixelSize(R.dimen.top_row_selected_items_container_margin_top);
    }

    public void setOnActionListener(OnActionListener listener) {
        this.mOnActionListener = listener;
    }

    public void setNotificationsTrayAdapter(NotificationsTrayAdapter adapter) {
        this.mNotificationsTray.setTrayAdapter(adapter);
    }

    public void updateNotificationsTrayVisibility() {
        this.mNotificationsTray.updateVisibility();
    }

    public void setNotificationsPanelController(NotificationsPanelController controller) {
        this.mPanelController = controller;
        this.mPanelController.setView((NotificationsPanelButtonView) findViewById(R.id.notification_panel_button));
    }

    public NotificationsTrayAdapter getNotificationsTrayAdapter() {
        return this.mNotificationsTray.getTrayAdapter();
    }

    public NotificationsPanelController getNotificationsPanelController() {
        return this.mPanelController;
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        ViewOutlineProvider outlineProvider = new ViewOutlineProvider() {
            public void getOutline(View view, Outline outline) {
                outline.setOval(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
            }
        };
        this.mItemsContainer = findViewById(R.id.items_container);
        this.mSearch = (SearchOrbView) findViewById(R.id.search_view);
        this.mSearch.setOutlineProvider(outlineProvider);
        AppsManager.getInstance(getContext()).setSearchPackageChangeListener(this.mSearch, this.mSearch.getSearchPackageName());
        this.mInputs = findViewById(R.id.inputs);
        this.mInputs.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (HomeTopRowView.this.mOnActionListener != null) {
                    HomeTopRowView.this.mOnActionListener.onShowInputs();
                }
            }
        });
        this.mInputs.setOutlineProvider(outlineProvider);
        this.mInputs.setClipToOutline(true);
        this.mTvInputManager = (TvInputManager) getContext().getSystemService(Context.TV_INPUT_SERVICE);
        if (InputsManager.hasInputs(this.mContext)) {
            this.mInputs.setVisibility(View.VISIBLE);
        } else {
            this.mInputs.setVisibility(View.GONE);
        }
        this.mInputsCallback = new InputsCallback();
        this.mInputsHandler = new Handler();
        View settings = findViewById(R.id.settings);
        settings.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (HomeTopRowView.this.mOnActionListener != null) {
                    HomeTopRowView.this.mOnActionListener.onStartSettings();
                }
            }
        });
        settings.setOutlineProvider(outlineProvider);
        settings.setClipToOutline(true);
        this.mNotificationsTray = (NotificationsTrayView) findViewById(R.id.notifications_tray);
        updateNotificationsTrayVisibility();
    }

    public void onFocusChange(View v, boolean hasFocus) {
        float scale = hasFocus ? this.mFocusedZoom : 1.0f;
        v.animate().z(hasFocus ? this.mFocusedElevation : this.mUnfocusedElevation).scaleX(scale).scaleY(scale).setDuration((long) this.mDuration);
    }

    public void setState(boolean zoomedOut, boolean selected) {
        MarginLayoutParams lp = (MarginLayoutParams) this.mSearch.getLayoutParams();
        lp.setMarginStart(getResources().getDimensionPixelOffset(zoomedOut ? R.dimen.search_orb_zoomed_out_margin_start : R.dimen.search_orb_margin_start));
        this.mSearch.setLayoutParams(lp);
        updateItemsSelectedState(selected);
    }

    private void updateItemsSelectedState(boolean newItemsSelected) {
        if (this.mItemsSelected != newItemsSelected) {
            this.mItemsSelected = newItemsSelected;
            MarginLayoutParams lp = (MarginLayoutParams) this.mItemsContainer.getLayoutParams();
            if (this.mItemsSelected) {
                lp.height = this.mSelectedItemsContainerHeight;
                lp.topMargin = this.mSelectedItemsContainerTopMargin;
            } else {
                lp.height = this.mDefaultItemsContainerHeight;
                lp.topMargin = this.mDefaultItemsContainerTopMargin;
            }
            this.mItemsContainer.setLayoutParams(lp);
        }
    }

    public void setOnHomeStateChangeListener(OnHomeStateChangeListener listener) {
    }

    public void setOnHomeRowSelectedListener(OnHomeRowSelectedListener listener) {
        this.mOnHomeRowSelectedListener = listener;
    }

    public void setOnHomeRowRemovedListener(OnHomeRowRemovedListener listener) {
    }

    public View getView() {
        return this;
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mTvInputManager.unregisterCallback(this.mInputsCallback);
        getViewTreeObserver().removeOnGlobalFocusChangeListener(this.mGlobalFocusChangeListener);
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mTvInputManager.registerCallback(this.mInputsCallback, this.mInputsHandler);
        getViewTreeObserver().addOnGlobalFocusChangeListener(this.mGlobalFocusChangeListener);
    }
}
