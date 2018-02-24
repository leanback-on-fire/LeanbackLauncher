package com.google.android.tvlauncher.home;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Outline;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver.OnGlobalFocusChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.google.android.exoplayer2.extractor.ts.TsExtractor;
import com.google.android.tvlauncher.R;
import com.google.android.tvlauncher.home.util.ChannelStateSettings;
import com.google.android.tvlauncher.util.ScaleFocusHandler;
import com.google.android.tvlauncher.util.Util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

public class ChannelView extends LinearLayout {
    private static final boolean DEBUG = false;
    public static final int STATE_ACTIONS_NOT_SELECTED = 7;
    public static final int STATE_ACTIONS_SELECTED = 6;
    public static final int STATE_DEFAULT_ABOVE_SELECTED = 2;
    public static final int STATE_DEFAULT_BELOW_SELECTED = 3;
    public static final int STATE_DEFAULT_NOT_SELECTED = 1;
    public static final int STATE_DEFAULT_SELECTED = 0;
    public static final int STATE_MOVE_NOT_SELECTED = 9;
    public static final int STATE_MOVE_SELECTED = 8;
    public static final int STATE_ZOOMED_OUT_NOT_SELECTED = 5;
    public static final int STATE_ZOOMED_OUT_SELECTED = 4;
    private static final String TAG = "ChannelView";
    private Drawable mActionMoveDownIcon;
    private Drawable mActionMoveUpDownIcon;
    private Drawable mActionMoveUpIcon;
    private View mActionsHint;
    private boolean mAllowMoving = true;
    private boolean mAllowRemoving = true;
    private boolean mAllowZoomOut = true;
    private View mChannelActionsPaddingView;
    private ImageView mChannelLogo;
    private View mChannelLogoContainer;
    private int mChannelLogoDefaultSize;
    private int mChannelLogoZoomedOutMargin;
    private int mChannelLogoZoomedOutSize;
    private TextView mChannelTitle;
    private boolean mIsRtl = false;
    private View mItemMetaContainer;
    private HorizontalGridView mItemsList;
    private FadingEdgeContainer mItemsListContainer;
    private int mItemsListMarginStart;
    private int mItemsListZoomedOutMarginStart;
    private ImageView mMoveButton;
    private View mMoveChannelPaddingView;
    private View mMovingChannelBackground;
    private View mNoMoveActionPaddingView;
    private OnMoveChannelDownListener mOnMoveChannelDownListener;
    private OnMoveChannelUpListener mOnMoveChannelUpListener;
    private OnPerformMainActionListener mOnPerformMainActionListener;
    private OnRemoveListener mOnRemoveListener;
    private OnStateChangeGesturePerformedListener mOnStateChangeGesturePerformedListener;
    private ImageView mRemoveButton;
    private boolean mShowItemMeta = true;
    private int mState = 1;
    private SparseArray<ChannelStateSettings> mStateSettings;
    private TextView mZoomedOutChannelTitle;
    private View mZoomedOutPaddingView;

    public interface OnMoveChannelDownListener {
        void onMoveChannelDown(ChannelView channelView);
    }

    public interface OnMoveChannelUpListener {
        void onMoveChannelUp(ChannelView channelView);
    }

    public interface OnPerformMainActionListener {
        void onPerformMainAction(ChannelView channelView);
    }

    public interface OnRemoveListener {
        void onRemove(ChannelView channelView);
    }

    public interface OnStateChangeGesturePerformedListener {
        void onStateChangeGesturePerformed(ChannelView channelView, int i);
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface State {
    }

    public static String stateToString(int state) {
        String stateString;
        switch (state) {
            case 0:
                stateString = "STATE_DEFAULT_SELECTED";
                break;
            case 1:
                stateString = "STATE_DEFAULT_NOT_SELECTED";
                break;
            case 2:
                stateString = "STATE_DEFAULT_ABOVE_SELECTED";
                break;
            case 3:
                stateString = "STATE_DEFAULT_BELOW_SELECTED";
                break;
            case 4:
                stateString = "STATE_ZOOMED_OUT_SELECTED";
                break;
            case 5:
                stateString = "STATE_ZOOMED_OUT_NOT_SELECTED";
                break;
            case 6:
                stateString = "STATE_ACTIONS_SELECTED";
                break;
            case 7:
                stateString = "STATE_ACTIONS_NOT_SELECTED";
                break;
            case 8:
                stateString = "STATE_MOVE_SELECTED";
                break;
            case 9:
                stateString = "STATE_MOVE_NOT_SELECTED";
                break;
            default:
                stateString = "STATE_UNKNOWN";
                break;
        }
        return stateString + " (" + state + ")";
    }

    public static String directionToString(int direction) {
        String directionString;
        switch (direction) {
            case 17:
                directionString = "FOCUS_LEFT";
                break;
            case 33:
                directionString = "FOCUS_UP";
                break;
            case 66:
                directionString = "FOCUS_RIGHT";
                break;
            case TsExtractor.TS_STREAM_TYPE_HDMV_DTS /*130*/:
                directionString = "FOCUS_DOWN";
                break;
            default:
                directionString = "FOCUS_UNKNOWN";
                break;
        }
        return directionString + " (" + direction + ")";
    }

    public ChannelView(Context context) {
        super(context);
    }

    public ChannelView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ChannelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mIsRtl = Util.isRtl(getContext());
        Resources r = getResources();
        this.mChannelLogoDefaultSize = r.getDimensionPixelSize(R.dimen.channel_logo_size);
        this.mChannelLogoZoomedOutSize = r.getDimensionPixelSize(R.dimen.channel_action_button_size);
        this.mChannelLogoZoomedOutMargin = r.getDimensionPixelOffset(R.dimen.channel_logo_zoomed_out_margin);
        this.mItemsListMarginStart = r.getDimensionPixelOffset(R.dimen.channel_items_list_margin_start);
        this.mItemsListZoomedOutMarginStart = r.getDimensionPixelOffset(R.dimen.channel_items_list_zoomed_out_margin_start);
        setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (ChannelView.this.mState == 8 && ChannelView.this.mOnStateChangeGesturePerformedListener != null) {
                    ChannelView.this.mOnStateChangeGesturePerformedListener.onStateChangeGesturePerformed(ChannelView.this, 4);
                }
            }
        });
        setFocusable(false);
        this.mChannelTitle = (TextView) findViewById(R.id.channel_title);
        this.mZoomedOutChannelTitle = (TextView) findViewById(R.id.channel_title_zoomed_out);
        this.mActionsHint = findViewById(R.id.actions_hint);
        this.mZoomedOutPaddingView = findViewById(R.id.zoomed_out_padding);
        this.mChannelActionsPaddingView = findViewById(R.id.channel_actions_padding);
        this.mMoveChannelPaddingView = findViewById(R.id.move_channel_padding);
        this.mNoMoveActionPaddingView = findViewById(R.id.no_move_action_padding);
        this.mMovingChannelBackground = findViewById(R.id.moving_channel_background);
        final int movingChannelBackgroundCornerRadius = getResources().getDimensionPixelSize(R.dimen.moving_channel_background_corner_radius);
        this.mMovingChannelBackground.setOutlineProvider(new ViewOutlineProvider() {
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth() + movingChannelBackgroundCornerRadius, view.getHeight(), (float) movingChannelBackgroundCornerRadius);
            }
        });
        this.mMovingChannelBackground.setClipToOutline(true);
        this.mChannelLogo = (ImageView) findViewById(R.id.channel_logo);
        this.mChannelLogo.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (ChannelView.this.mOnPerformMainActionListener != null) {
                    ChannelView.this.mOnPerformMainActionListener.onPerformMainAction(ChannelView.this);
                }
            }
        });
        this.mChannelLogo.setOutlineProvider(new ViewOutlineProvider() {
            public void getOutline(View view, Outline outline) {
                outline.setOval(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
            }
        });
        this.mChannelLogo.setClipToOutline(true);
        if (!isInEditMode()) {
            float focusedScale = r.getFraction(R.fraction.channel_logo_focused_scale, 1, 1);
            final float f = focusedScale;
            new ScaleFocusHandler(r.getInteger(R.integer.channel_logo_focused_animation_duration_ms), focusedScale, r.getDimension(R.dimen.channel_logo_focused_elevation)) {
                public void onFocusChange(View v, boolean hasFocus) {
                    setFocusedScale(Util.isAccessibilityEnabled(ChannelView.this.getContext()) ? 1.0f : f);
                    super.onFocusChange(v, hasFocus);
                }
            }.setView(this.mChannelLogo);
        }
        this.mChannelLogoContainer = findViewById(R.id.channel_logo_container);
        this.mRemoveButton = (ImageView) findViewById(R.id.remove);
        this.mRemoveButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (ChannelView.this.mOnRemoveListener != null) {
                    ChannelView.this.mOnRemoveListener.onRemove(ChannelView.this);
                }
            }
        });
        translateNextFocusForRtl(this.mRemoveButton);
        this.mMoveButton = (ImageView) findViewById(R.id.move);
        this.mMoveButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (ChannelView.this.mOnStateChangeGesturePerformedListener != null) {
                    ChannelView.this.mOnStateChangeGesturePerformedListener.onStateChangeGesturePerformed(ChannelView.this, 8);
                }
            }
        });
        this.mItemsList = (HorizontalGridView) findViewById(R.id.items_list);
        this.mItemsListContainer = (FadingEdgeContainer) findViewById(R.id.items_list_container);
        this.mItemMetaContainer = findViewById(R.id.item_meta_container);
        getViewTreeObserver().addOnGlobalFocusChangeListener(new OnGlobalFocusChangeListener() {
            public void onGlobalFocusChanged(View oldFocus, View newFocus) {
                boolean oldFocusedIsChild = ChannelView.this.isFocusableChild(oldFocus);
                boolean newFocusedIsChild = ChannelView.this.isFocusableChild(newFocus);
                if (newFocusedIsChild != oldFocusedIsChild) {
                    ChannelView.this.onChannelSelected(newFocusedIsChild);
                }
            }
        });
        this.mActionMoveUpDownIcon = getContext().getDrawable(R.drawable.ic_action_move_up_down_black);
        this.mActionMoveUpIcon = getContext().getDrawable(R.drawable.ic_action_move_up_black);
        this.mActionMoveDownIcon = getContext().getDrawable(R.drawable.ic_action_move_down_black);
    }

    private int translateFocusDirectionForRtl(int direction) {
        if (!this.mIsRtl) {
            return direction;
        }
        if (direction == 17) {
            return 66;
        }
        if (direction == 66) {
            return 17;
        }
        return direction;
    }

    private void translateNextFocusForRtl(View view) {
        if (this.mIsRtl) {
            int temp = view.getNextFocusLeftId();
            view.setNextFocusLeftId(view.getNextFocusRightId());
            view.setNextFocusRightId(temp);
        }
    }

    private boolean isFocusableChild(View v) {
        if (v == null) {
            return false;
        }
        Object parent = v.getParent();
        if (parent == this.mItemsList || parent == this.mChannelLogo.getParent() || parent == this) {
            return true;
        }
        return false;
    }

    private void onChannelSelected(boolean selected) {
        Integer newState = null;
        if (!selected) {
            switch (this.mState) {
                case 0:
                    newState = Integer.valueOf(1);
                    break;
                case 4:
                    newState = Integer.valueOf(5);
                    break;
                default:
                    break;
            }
        }
        switch (this.mState) {
            case 1:
            case 2:
            case 3:
                newState = Integer.valueOf(0);
                break;
            case 5:
                newState = Integer.valueOf(4);
                break;
        }
        if (newState != null && this.mOnStateChangeGesturePerformedListener != null) {
            this.mOnStateChangeGesturePerformedListener.onStateChangeGesturePerformed(this, newState.intValue());
        }
    }

    protected boolean onRequestFocusInDescendants(int direction, Rect previouslyFocusedRect) {
        if (this.mState == 5 || this.mState == 4) {
            this.mChannelLogo.requestFocus();
        } else {
            this.mItemsList.requestFocus();
        }
        return true;
    }

    public void addFocusables(ArrayList<View> views, int direction, int focusableMode) {
        if (this.mState == 5) {
            this.mChannelLogo.addFocusables(views, direction, focusableMode);
        } else if (this.mState == 1 || this.mState == 2 || this.mState == 3) {
            this.mItemsList.addFocusables(views, direction, focusableMode);
        } else {
            super.addFocusables(views, direction, focusableMode);
        }
    }

    public View focusSearch(int direction) {
        switch (direction) {
            case 33:
                if (this.mOnMoveChannelUpListener != null) {
                    this.mOnMoveChannelUpListener.onMoveChannelUp(this);
                    break;
                }
                break;
            case TsExtractor.TS_STREAM_TYPE_HDMV_DTS /*130*/:
                if (this.mOnMoveChannelDownListener != null) {
                    this.mOnMoveChannelDownListener.onMoveChannelDown(this);
                    break;
                }
                break;
        }
        return this;
    }

    public View focusSearch(View focused, int direction) {
        int originalDirection = direction;
        direction = translateFocusDirectionForRtl(direction);
        Integer newState = null;
        if (focused.getParent() != this.mItemsList || !this.mAllowZoomOut) {
            if (focused != this.mChannelLogo) {
                if (focused == this.mMoveButton || (focused == this.mRemoveButton && !this.mAllowMoving)) {
                    switch (direction) {
                        case 66:
                            newState = Integer.valueOf(4);
                            break;
                        default:
                            break;
                    }
                }
            }
            switch (direction) {
                case 17:
                    if (this.mAllowMoving || this.mAllowRemoving) {
                        newState = 6;
                        break;
                    }
                    return focused;
                case 66:
                    newState = 0;
                    break;
                default:
                    break;
            }
        }
        switch (direction) {
            case 17:
                newState = Integer.valueOf(4);
                break;
        }
        if (newState == null || this.mOnStateChangeGesturePerformedListener == null) {
            return super.focusSearch(focused, originalDirection);
        }
        this.mOnStateChangeGesturePerformedListener.onStateChangeGesturePerformed(this, newState.intValue());
        return focused;
    }

    private void updateUi(int oldState, int newState) {
        if (newState != oldState) {
            View view;
            int i;
            switch (newState) {
                case 0:
                    this.mChannelTitle.setVisibility(View.VISIBLE);
                    this.mZoomedOutChannelTitle.setVisibility(View.GONE);
                    this.mActionsHint.setVisibility(View.GONE);
                    this.mRemoveButton.setVisibility(View.GONE);
                    this.mMoveButton.setVisibility(View.GONE);
                    this.mZoomedOutPaddingView.setVisibility(View.GONE);
                    this.mChannelActionsPaddingView.setVisibility(View.GONE);
                    this.mMoveChannelPaddingView.setVisibility(View.GONE);
                    this.mNoMoveActionPaddingView.setVisibility(View.GONE);
                    this.mItemMetaContainer.setVisibility(this.mShowItemMeta ? View.VISIBLE : View.GONE);
                    this.mMovingChannelBackground.setVisibility(View.GONE);
                    this.mItemsListContainer.setFadeEnabled(true);
                    setFocusable(false);
                    if (!Util.isAccessibilityEnabled(getContext())) {
                        this.mItemsList.requestFocus();
                        break;
                    }
                    break;
                case 1:
                case 2:
                case 3:
                    this.mChannelTitle.setVisibility(View.VISIBLE);
                    this.mZoomedOutChannelTitle.setVisibility(View.GONE);
                    this.mActionsHint.setVisibility(View.GONE);
                    this.mRemoveButton.setVisibility(View.GONE);
                    this.mMoveButton.setVisibility(View.GONE);
                    this.mZoomedOutPaddingView.setVisibility(View.GONE);
                    this.mChannelActionsPaddingView.setVisibility(View.GONE);
                    this.mMoveChannelPaddingView.setVisibility(View.GONE);
                    this.mNoMoveActionPaddingView.setVisibility(View.GONE);
                    this.mItemMetaContainer.setVisibility(View.GONE);
                    this.mMovingChannelBackground.setVisibility(View.GONE);
                    this.mItemsListContainer.setFadeEnabled(false);
                    setFocusable(false);
                    break;
                case 4:
                    this.mChannelTitle.setVisibility(View.GONE);
                    this.mZoomedOutChannelTitle.setVisibility(View.VISIBLE);
                    view = this.mActionsHint;
                    i = (this.mAllowMoving || this.mAllowRemoving) ? View.VISIBLE : View.GONE;
                    view.setVisibility(i);
                    this.mRemoveButton.setVisibility(View.GONE);
                    this.mMoveButton.setVisibility(View.GONE);
                    view = this.mZoomedOutPaddingView;
                    if (this.mAllowMoving || this.mAllowRemoving) {
                        i = 8;
                    } else {
                        i = 0;
                    }
                    view.setVisibility(i);
                    this.mChannelActionsPaddingView.setVisibility(View.GONE);
                    this.mMoveChannelPaddingView.setVisibility(View.GONE);
                    this.mNoMoveActionPaddingView.setVisibility(View.GONE);
                    this.mItemMetaContainer.setVisibility(View.GONE);
                    this.mMovingChannelBackground.setVisibility(View.GONE);
                    this.mItemsListContainer.setFadeEnabled(false);
                    setFocusable(false);
                    this.mChannelLogo.requestFocus();
                    break;
                case 5:
                    this.mChannelTitle.setVisibility(View.GONE);
                    this.mZoomedOutChannelTitle.setVisibility(View.VISIBLE);
                    this.mActionsHint.setVisibility(View.GONE);
                    this.mRemoveButton.setVisibility(View.GONE);
                    this.mMoveButton.setVisibility(View.GONE);
                    this.mZoomedOutPaddingView.setVisibility(View.VISIBLE);
                    this.mChannelActionsPaddingView.setVisibility(View.GONE);
                    this.mMoveChannelPaddingView.setVisibility(View.GONE);
                    this.mNoMoveActionPaddingView.setVisibility(View.GONE);
                    this.mItemMetaContainer.setVisibility(View.GONE);
                    this.mMovingChannelBackground.setVisibility(View.GONE);
                    this.mItemsListContainer.setFadeEnabled(false);
                    setFocusable(false);
                    break;
                case 6:
                    this.mChannelTitle.setVisibility(View.GONE);
                    this.mZoomedOutChannelTitle.setVisibility(View.VISIBLE);
                    this.mActionsHint.setVisibility(View.GONE);
                    this.mRemoveButton.setVisibility(View.VISIBLE);
                    this.mMoveButton.setVisibility(this.mAllowMoving ? View.VISIBLE : View.GONE);
                    this.mZoomedOutPaddingView.setVisibility(View.GONE);
                    this.mChannelActionsPaddingView.setVisibility(View.GONE);
                    this.mMoveChannelPaddingView.setVisibility(View.GONE);
                    view = this.mNoMoveActionPaddingView;
                    if (this.mAllowMoving) {
                        i = 8;
                    } else {
                        i = 0;
                    }
                    view.setVisibility(i);
                    this.mItemMetaContainer.setVisibility(View.GONE);
                    this.mMovingChannelBackground.setVisibility(View.GONE);
                    this.mItemsListContainer.setFadeEnabled(false);
                    setFocusable(false);
                    if (!this.mAllowMoving) {
                        this.mRemoveButton.requestFocus();
                        break;
                    } else {
                        this.mMoveButton.requestFocus();
                        break;
                    }
                case 7:
                    this.mChannelTitle.setVisibility(View.GONE);
                    this.mZoomedOutChannelTitle.setVisibility(View.VISIBLE);
                    this.mActionsHint.setVisibility(View.GONE);
                    this.mRemoveButton.setVisibility(View.GONE);
                    this.mMoveButton.setVisibility(View.GONE);
                    this.mZoomedOutPaddingView.setVisibility(View.GONE);
                    this.mChannelActionsPaddingView.setVisibility(View.VISIBLE);
                    this.mMoveChannelPaddingView.setVisibility(View.GONE);
                    this.mNoMoveActionPaddingView.setVisibility(View.GONE);
                    this.mItemMetaContainer.setVisibility(View.GONE);
                    this.mMovingChannelBackground.setVisibility(View.GONE);
                    this.mItemsListContainer.setFadeEnabled(false);
                    setFocusable(false);
                    break;
                case 8:
                    this.mChannelTitle.setVisibility(View.GONE);
                    this.mZoomedOutChannelTitle.setVisibility(View.VISIBLE);
                    this.mActionsHint.setVisibility(View.GONE);
                    this.mRemoveButton.setVisibility(View.INVISIBLE);
                    this.mMoveButton.setVisibility(View.VISIBLE);
                    this.mZoomedOutPaddingView.setVisibility(View.GONE);
                    this.mChannelActionsPaddingView.setVisibility(View.GONE);
                    this.mMoveChannelPaddingView.setVisibility(View.GONE);
                    this.mNoMoveActionPaddingView.setVisibility(View.GONE);
                    this.mItemMetaContainer.setVisibility(View.GONE);
                    this.mMovingChannelBackground.setVisibility(View.VISIBLE);
                    this.mItemsListContainer.setFadeEnabled(false);
                    setFocusable(true);
                    requestFocus();
                    break;
                case 9:
                    this.mChannelTitle.setVisibility(View.GONE);
                    this.mZoomedOutChannelTitle.setVisibility(View.VISIBLE);
                    this.mActionsHint.setVisibility(View.GONE);
                    this.mRemoveButton.setVisibility(View.GONE);
                    this.mMoveButton.setVisibility(View.GONE);
                    this.mZoomedOutPaddingView.setVisibility(View.GONE);
                    this.mChannelActionsPaddingView.setVisibility(View.GONE);
                    this.mMoveChannelPaddingView.setVisibility(View.VISIBLE);
                    this.mNoMoveActionPaddingView.setVisibility(View.GONE);
                    this.mItemMetaContainer.setVisibility(View.GONE);
                    this.mMovingChannelBackground.setVisibility(View.GONE);
                    this.mItemsListContainer.setFadeEnabled(false);
                    setFocusable(false);
                    break;
            }
            if (this.mStateSettings != null) {
                ChannelStateSettings settings = (ChannelStateSettings) this.mStateSettings.get(this.mState);
                boolean isZoomedOut = isZoomedOutState(this.mState);
                MarginLayoutParams channelLayoutParams = (MarginLayoutParams) getLayoutParams();
                channelLayoutParams.setMargins(0, settings.getMarginTop(), 0, settings.getMarginBottom());
                setLayoutParams(channelLayoutParams);
                MarginLayoutParams itemsListContainerLayoutParams = (MarginLayoutParams) this.mItemsListContainer.getLayoutParams();
                itemsListContainerLayoutParams.setMarginStart(isZoomedOut ? this.mItemsListZoomedOutMarginStart : this.mItemsListMarginStart);
                this.mItemsListContainer.setLayoutParams(itemsListContainerLayoutParams);
                MarginLayoutParams itemListLayoutParams = (MarginLayoutParams) this.mItemsList.getLayoutParams();
                itemListLayoutParams.height = (settings.getItemHeight() + settings.getItemMarginTop()) + settings.getItemMarginBottom();
                this.mItemsList.setLayoutParams(itemListLayoutParams);
                LayoutParams channelLogoContainerLayoutParams = (LayoutParams) this.mChannelLogoContainer.getLayoutParams();
                if (this.mState == 2) {
                    channelLogoContainerLayoutParams.gravity = 80;
                    channelLogoContainerLayoutParams.bottomMargin = settings.getChannelLogoAlignmentOriginMargin();
                } else {
                    channelLogoContainerLayoutParams.gravity = 48;
                    channelLogoContainerLayoutParams.bottomMargin = 0;
                }
                this.mChannelLogoContainer.setLayoutParams(channelLogoContainerLayoutParams);
                MarginLayoutParams channelLogoLayoutParams = (MarginLayoutParams) this.mChannelLogo.getLayoutParams();
                int topMargin = settings.getChannelLogoAlignmentOriginMargin();
                if (isZoomedOut) {
                    channelLogoLayoutParams.height = this.mChannelLogoZoomedOutSize;
                    channelLogoLayoutParams.width = this.mChannelLogoZoomedOutSize;
                    channelLogoLayoutParams.setMargins(this.mChannelLogoZoomedOutMargin, this.mChannelLogoZoomedOutMargin + topMargin, this.mChannelLogoZoomedOutMargin, this.mChannelLogoZoomedOutMargin);
                } else {
                    channelLogoLayoutParams.height = this.mChannelLogoDefaultSize;
                    channelLogoLayoutParams.width = this.mChannelLogoDefaultSize;
                    channelLogoLayoutParams.setMargins(0, topMargin, 0, 0);
                }
                this.mChannelLogo.setLayoutParams(channelLogoLayoutParams);
            }
        }
    }

    private boolean isZoomedOutState(int state) {
        switch (state) {
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
                return true;
            default:
                return false;
        }
    }

    public void setAllowMoving(boolean allowMoving) {
        this.mAllowMoving = allowMoving;
    }

    public void setAllowRemoving(boolean allowRemoving) {
        this.mAllowRemoving = allowRemoving;
    }

    public void setShowItemMeta(boolean showItemMeta) {
        this.mShowItemMeta = showItemMeta;
    }

    public void setAllowZoomOut(boolean allowZoomOut) {
        this.mAllowZoomOut = allowZoomOut;
    }

    public void setStateSettings(SparseArray<ChannelStateSettings> stateSettings) {
        this.mStateSettings = stateSettings;
    }

    public void setChannelTitle(String title) {
        this.mChannelTitle.setText(title);
        this.mZoomedOutChannelTitle.setText(title);
    }

    public ImageView getChannelLogoImageView() {
        return this.mChannelLogo;
    }

    public HorizontalGridView getItemsListView() {
        return this.mItemsList;
    }

    public View getItemMetadataView() {
        return this.mItemMetaContainer;
    }

    public int getState() {
        return this.mState;
    }

    public void setState(int state) {
        if (state != this.mState) {
            int oldState = this.mState;
            this.mState = state;
            updateUi(oldState, state);
        }
    }

    public void updateChannelMoveAction(boolean canMoveUp, boolean canMoveDown) {
        setAllowMoving(true);
        if (canMoveUp && canMoveDown) {
            this.mMoveButton.setImageDrawable(this.mActionMoveUpDownIcon);
        } else if (canMoveUp) {
            this.mMoveButton.setImageDrawable(this.mActionMoveUpIcon);
        } else if (canMoveDown) {
            this.mMoveButton.setImageDrawable(this.mActionMoveDownIcon);
        } else {
            setAllowMoving(false);
        }
    }

    public void setOnPerformMainActionListener(OnPerformMainActionListener listener) {
        this.mOnPerformMainActionListener = listener;
    }

    public void setOnMoveUpListener(OnMoveChannelUpListener moveUpListener) {
        this.mOnMoveChannelUpListener = moveUpListener;
    }

    public void setOnMoveDownListener(OnMoveChannelDownListener moveDownListener) {
        this.mOnMoveChannelDownListener = moveDownListener;
    }

    public void setOnRemoveListener(OnRemoveListener onRemoveListener) {
        this.mOnRemoveListener = onRemoveListener;
    }

    public void setOnStateChangeGesturePerformedListener(OnStateChangeGesturePerformedListener onStateChangeGesturePerformedListener) {
        this.mOnStateChangeGesturePerformedListener = onStateChangeGesturePerformedListener;
    }

    public String toString() {
        return '{' + super.toString() + ", title='" + this.mChannelTitle.getText() + '\'' + '}';
    }
}
