package com.google.android.tvlauncher.home;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Outline;
import android.support.v17.leanback.widget.FacetProvider;
import android.support.v17.leanback.widget.ItemAlignmentFacet;
import android.support.v17.leanback.widget.ItemAlignmentFacet.ItemAlignmentDef;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.tvlauncher.R;
import com.google.android.tvlauncher.analytics.EventLogger;
import com.google.android.tvlauncher.analytics.LogEvent;
import com.google.android.tvlauncher.analytics.LogEvents;
import com.google.android.tvlauncher.data.ChannelProgramsObserver;
import com.google.android.tvlauncher.data.TvDataManager;
import com.google.android.tvlauncher.home.util.ProgramSettings;
import com.google.android.tvlauncher.home.util.ProgramUtil;
import com.google.android.tvlauncher.model.Program;
import com.google.android.tvlauncher.util.ColorUtils;
import com.google.android.tvlauncher.util.ScaleFocusHandler;

import java.util.List;

class ChannelItemsAdapter extends Adapter<ViewHolder> {
    private static final boolean DEBUG = false;
    private static final int EMPTY_CHANNEL_PROGRAM_ID = -3;
    private static final float LAST_PROGRAM_FOCUSED_COLOR_DARKEN_FACTOR = 0.8f;
    private static final int LAST_PROGRAM_ID = -2;
    private static final int NO_CHANNEL_ID = -1;
    private static final String PAYLOAD_STATE = "PAYLOAD_STATE";
    private static final String TAG = "ChannelItemsAdapter";
    private static final int TYPE_EMPTY_CHANNEL_PROGRAM = 2;
    private static final int TYPE_LAST_PROGRAM = 1;
    private static final int TYPE_PROGRAM = 0;
    private boolean mCanAddToWatchNext;
    private boolean mCanRemoveProgram;
    private long mChannelId = -1;
    private Bitmap mChannelLogo;
    private final TvDataManager mDataManager;
    private final EventLogger mEventLogger;
    private boolean mIsLegacy;
    private int mLastProgramFocusedColor;
    private OnPerformLastProgramActionListener mOnPerformLastProgramActionListener;
    private OnProgramSelectedListener mOnProgramSelectedListener;
    private String mPackageName;
    private int mPrimaryColor;
    private int mProgramState = 0;
    private final ChannelProgramsObserver mProgramsObserver = new ChannelProgramsObserver() {
        public void onProgramsChange(long channelId) {
            ChannelItemsAdapter.this.notifyDataSetChanged();
        }
    };
    private boolean mStarted;

    private class BaseSpecialProgramViewHolder extends ViewHolder {
        final ProgramSettings mProgramSettings;

        private final ScaleFocusHandler mFocusHandler;

        protected void handleFocusChange(View v, boolean hasFocus) {
            if (ChannelItemsAdapter.this.mOnProgramSelectedListener != null && hasFocus && getAdapterPosition() != -1) {
                ChannelItemsAdapter.this.mOnProgramSelectedListener.onProgramSelected(null, null);
            }
        }

        BaseSpecialProgramViewHolder(View v) {
            super(v);
            this.mProgramSettings = ProgramUtil.getProgramSettings(v.getContext());
            this.mFocusHandler = new ScaleFocusHandler(this.mProgramSettings.focusedAnimationDuration, this.mProgramSettings.focusedScale, this.mProgramSettings.focusedElevation, 1);
            this.mFocusHandler.setView(v);
            this.mFocusHandler.setOnFocusChangeListener(new OnFocusChangeListener() {
                public void onFocusChange(View v, boolean hasFocus) {
                    BaseSpecialProgramViewHolder.this.handleFocusChange(v, hasFocus);
                }
            });
            v.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    if (ChannelItemsAdapter.this.mOnPerformLastProgramActionListener != null) {
                        ChannelItemsAdapter.this.mOnPerformLastProgramActionListener.onPerformLastProgramAction();
                    }
                }
            });
        }

        void bind() {
            bindState();
            this.mFocusHandler.resetFocusedState();
        }

        void bindState() {
            ProgramUtil.updateSize(this.itemView, ChannelItemsAdapter.this.mProgramState, ProgramUtil.ASPECT_RATIO_16_9, this.mProgramSettings);
        }

        void setRoundedCornerClipOutline(View v) {
            v.setOutlineProvider(new ViewOutlineProvider() {
                public void getOutline(View view, Outline outline) {
                    outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), (float) view.getResources().getDimensionPixelSize(R.dimen.card_rounded_corner_radius));
                }
            });
            v.setClipToOutline(true);
        }
    }

    private class ProgramViewHolder extends ViewHolder implements OnProgramViewFocusedListener, EventLogger {
        private final EventLogger mEventLogger;
        final ProgramController mProgramController;

        ProgramViewHolder(View v, EventLogger eventLogger) {
            super(v);
            this.mEventLogger = eventLogger;
            this.mProgramController = new ProgramController(v, this);
            this.mProgramController.setOnProgramViewFocusedListener(this);
            this.mProgramController.setIsWatchNextProgram(false);
        }

        public void onProgramViewFocused() {
            if (ChannelItemsAdapter.this.mOnProgramSelectedListener != null) {
                int position = getAdapterPosition();
                if (position != -1) {
                    ChannelItemsAdapter.this.mOnProgramSelectedListener.onProgramSelected(ChannelItemsAdapter.this.mDataManager.getProgram(ChannelItemsAdapter.this.mChannelId, position), this.mProgramController);
                }
            }
        }

        public void log(LogEvent event) {
            event.putParameter(LogEvents.PARAMETER_INDEX, getAdapterPosition()).putParameter("count", ChannelItemsAdapter.this.getItemCount());
            this.mEventLogger.log(event);
        }

        ProgramController getProgramController() {
            return this.mProgramController;
        }

        void bind(Program program) {
            this.mProgramController.bind(program, ChannelItemsAdapter.this.mPackageName, ChannelItemsAdapter.this.mProgramState, ChannelItemsAdapter.this.mCanAddToWatchNext, ChannelItemsAdapter.this.mCanRemoveProgram, ChannelItemsAdapter.this.mIsLegacy);
            if (ChannelItemsAdapter.this.mOnProgramSelectedListener != null && this.mProgramController.isViewFocused()) {
                ChannelItemsAdapter.this.mOnProgramSelectedListener.onProgramSelected(program, this.mProgramController);
            }
        }
    }

    private class EmptyChannelProgramViewHolder extends BaseSpecialProgramViewHolder {
        private static final float DARKEN_FACTOR_BOTTOM_LAYER = 0.269f;
        private static final float DARKEN_FACTOR_MIDDLE_LAYER = 0.376f;
        private final View mBottomLayer;
        private final int mIconSize;
        private final View mMiddleLayer;
        private final ImageView mTopLayer;
        private final int mTopLayerMarginEnd;

        EmptyChannelProgramViewHolder(View v) {
            super(v);
            this.mIconSize = v.getResources().getDimensionPixelSize(R.dimen.empty_channel_program_channel_logo_size);
            this.mTopLayerMarginEnd = v.getResources().getDimensionPixelSize(R.dimen.empty_channel_program_top_layer_margin_end);
            this.mBottomLayer = v.findViewById(R.id.bottom_layer);
            this.mMiddleLayer = v.findViewById(R.id.middle_layer);
            this.mTopLayer = (ImageView) v.findViewById(R.id.top_layer);
            setRoundedCornerClipOutline(this.mBottomLayer);
            setRoundedCornerClipOutline(this.mMiddleLayer);
            setRoundedCornerClipOutline(this.mTopLayer);
            v.setOutlineProvider(new ViewOutlineProvider() {
                public void getOutline(View view, Outline outline) {
                    // todo check
                    if (view.getLayoutDirection() == View.LAYOUT_DIRECTION_LTR) {
                        outline.setRoundRect(0, 0, view.getWidth() - EmptyChannelProgramViewHolder.this.mTopLayerMarginEnd, view.getHeight(), (float) view.getResources().getDimensionPixelSize(R.dimen.card_rounded_corner_radius));
                        return;
                    }
                    outline.setRoundRect(EmptyChannelProgramViewHolder.this.mTopLayerMarginEnd, 0, view.getWidth(), view.getHeight(), (float) view.getResources().getDimensionPixelSize(R.dimen.card_rounded_corner_radius));
                }
            });
        }

        void bind() {
            updateLogoAndColor();
            super.bind();
        }

        void bindState() {
            MarginLayoutParams topLayerLayoutParams = (MarginLayoutParams) this.mTopLayer.getLayoutParams();
            MarginLayoutParams containerLayoutParams = (MarginLayoutParams) this.itemView.getLayoutParams();
            switch (ChannelItemsAdapter.this.mProgramState) {
                case 0:
                    topLayerLayoutParams.height = this.mProgramSettings.defaultHeight;
                    containerLayoutParams.setMargins(0, this.mProgramSettings.defaultTopMargin, 0, this.mProgramSettings.defaultBottomMargin);
                    containerLayoutParams.setMarginEnd(this.mProgramSettings.defaultHorizontalMargin);
                    break;
                case 1:
                    topLayerLayoutParams.height = this.mProgramSettings.selectedHeight;
                    containerLayoutParams.setMargins(0, this.mProgramSettings.selectedVerticalMargin, 0, this.mProgramSettings.selectedVerticalMargin);
                    containerLayoutParams.setMarginEnd(this.mProgramSettings.defaultHorizontalMargin);
                    break;
                case 2:
                    topLayerLayoutParams.height = this.mProgramSettings.zoomedOutHeight;
                    containerLayoutParams.setMargins(0, this.mProgramSettings.zoomedOutVerticalMargin, 0, this.mProgramSettings.zoomedOutVerticalMargin);
                    containerLayoutParams.setMarginEnd(this.mProgramSettings.zoomedOutHorizontalMargin);
                    break;
            }
            topLayerLayoutParams.width = (int) Math.round(((double) topLayerLayoutParams.height) * ProgramUtil.ASPECT_RATIO_16_9);
            this.mTopLayer.setLayoutParams(topLayerLayoutParams);
            int verticalPadding = (topLayerLayoutParams.height - this.mIconSize) / 2;
            int horizontalPadding = (topLayerLayoutParams.width - this.mIconSize) / 2;
            this.mTopLayer.setPadding(horizontalPadding, verticalPadding, horizontalPadding, verticalPadding);
        }

        void updateLogoAndColor() {
            this.mTopLayer.setImageBitmap(ChannelItemsAdapter.this.mChannelLogo);
            this.mTopLayer.setBackgroundColor(ChannelItemsAdapter.this.mPrimaryColor);
            this.mMiddleLayer.setBackgroundColor(ColorUtils.darkenColor(ChannelItemsAdapter.this.mPrimaryColor, DARKEN_FACTOR_MIDDLE_LAYER));
            this.mBottomLayer.setBackgroundColor(ColorUtils.darkenColor(ChannelItemsAdapter.this.mPrimaryColor, DARKEN_FACTOR_BOTTOM_LAYER));
        }
    }

    private class LastProgramViewHolder extends BaseSpecialProgramViewHolder implements FacetProvider {
        private final int mDefaultTextSize;
        private ItemAlignmentFacet mFacet;
        private ItemAlignmentDef mItemAlignmentDef = new ItemAlignmentDef();
        private final TextView mText;
        private final int mUnfocusedBackgroundColor;
        private final int mZoomedOutTextSize;

        LastProgramViewHolder(View v) {
            super(v);
            this.mText = (TextView) v.findViewById(R.id.text);
            setRoundedCornerClipOutline(v);
            this.mUnfocusedBackgroundColor = ContextCompat.getColor(v.getContext(), R.color.channel_last_item_unfocused_background);
            this.mDefaultTextSize = v.getContext().getResources().getDimensionPixelSize(R.dimen.channel_last_item_default_text_size);
            this.mZoomedOutTextSize = v.getContext().getResources().getDimensionPixelSize(R.dimen.channel_last_item_zoomed_out_text_size);
            this.mItemAlignmentDef.setItemAlignmentOffsetPercent(-1.0f);
            this.mFacet = new ItemAlignmentFacet();
            this.mFacet.setAlignmentDefs(new ItemAlignmentDef[]{this.mItemAlignmentDef});
        }

        protected void handleFocusChange(View v, boolean hasFocus) {
            super.handleFocusChange(v, hasFocus);
            v.setBackgroundColor(hasFocus ? ChannelItemsAdapter.this.mLastProgramFocusedColor : this.mUnfocusedBackgroundColor);
        }

        void bindState() {
            super.bindState();
            switch (ChannelItemsAdapter.this.mProgramState) {
                case 0:
                case 1:
                    this.mText.setTextSize(0, (float) this.mDefaultTextSize);
                    return;
                case 2:
                    this.mText.setTextSize(0, (float) this.mZoomedOutTextSize);
                    return;
                default:
                    return;
            }
        }

        public Object getFacet(Class<?> cls) {
            int position = getAdapterPosition();
            if (position == -1 || position == 0) {
                return null;
            }
            int previousCardEndMargin;
            double ratio = ProgramUtil.getAspectRatio(ChannelItemsAdapter.this.mDataManager.getProgram(ChannelItemsAdapter.this.mChannelId, position - 1).getPreviewImageAspectRatio());
            int cardHeight = 0;
            switch (ChannelItemsAdapter.this.mProgramState) {
                case 0:
                    cardHeight = this.mProgramSettings.defaultHeight;
                    break;
                case 1:
                    cardHeight = this.mProgramSettings.selectedHeight;
                    break;
                case 2:
                    cardHeight = this.mProgramSettings.zoomedOutHeight;
                    break;
            }
            int previousCardWidth = (int) (((double) cardHeight) * ratio);
            if (ChannelItemsAdapter.this.mProgramState == 2) {
                previousCardEndMargin = this.mProgramSettings.zoomedOutHorizontalMargin;
            } else {
                previousCardEndMargin = this.mProgramSettings.defaultHorizontalMargin;
            }
            this.mItemAlignmentDef.setItemAlignmentOffset(-(previousCardWidth + previousCardEndMargin));
            return this.mFacet;
        }
    }

    ChannelItemsAdapter(Context context, EventLogger eventLogger) {
        this.mDataManager = TvDataManager.getInstance(context);
        this.mEventLogger = eventLogger;
        setHasStableIds(true);
    }

    void setOnPerformLastProgramActionListener(OnPerformLastProgramActionListener listener) {
        this.mOnPerformLastProgramActionListener = listener;
    }

    void setOnProgramSelectedListener(OnProgramSelectedListener listener) {
        this.mOnProgramSelectedListener = listener;
    }

    void setChannelLogoAndPrimaryColor(Bitmap channelLogo, int primaryColor) {
        this.mChannelLogo = channelLogo;
        this.mPrimaryColor = primaryColor;
        this.mLastProgramFocusedColor = ColorUtils.darkenColor(this.mPrimaryColor, LAST_PROGRAM_FOCUSED_COLOR_DARKEN_FACTOR);
        int itemCount = getItemCount();
        if (itemCount > 0) {
            notifyItemChanged(itemCount - 1);
        }
    }

    void bind(long channelId, String packageName, int programState, boolean canAddToWatchNext, boolean canRemoveProgram, boolean isLegacy) {
        this.mPackageName = packageName;
        this.mCanAddToWatchNext = canAddToWatchNext;
        this.mCanRemoveProgram = canRemoveProgram;
        this.mIsLegacy = isLegacy;
        int oldProgramState = this.mProgramState;
        this.mProgramState = programState;
        if (channelId != this.mChannelId) {
            if (this.mChannelId != -1) {
                this.mDataManager.unregisterChannelProgramsObserver(this.mChannelId, this.mProgramsObserver);
            }
            this.mChannelId = channelId;
            if (this.mChannelId != -1) {
                if (!registerObserverAndUpdateDataIfNeeded()) {
                    notifyDataSetChanged();
                }
                this.mStarted = true;
                return;
            }
            this.mStarted = false;
        } else if (oldProgramState != programState) {
            notifyItemRangeChanged(0, getItemCount(), PAYLOAD_STATE);
        }
    }

    int getProgramState() {
        return this.mProgramState;
    }

    void setProgramState(int programState) {
        if (this.mProgramState != programState) {
            this.mProgramState = programState;
            notifyItemRangeChanged(0, getItemCount(), PAYLOAD_STATE);
        }
    }

    private boolean registerObserverAndUpdateDataIfNeeded() {
        this.mDataManager.registerChannelProgramsObserver(this.mChannelId, this.mProgramsObserver);
        if (this.mDataManager.isProgramDataLoaded(this.mChannelId) && !this.mDataManager.isProgramDataStale(this.mChannelId)) {
            return false;
        }
        this.mDataManager.loadProgramData(this.mChannelId);
        return true;
    }

    public void onStart() {
        if (!(this.mStarted || this.mChannelId == -1)) {
            registerObserverAndUpdateDataIfNeeded();
        }
        this.mStarted = true;
    }

    public void onStop() {
        if (this.mStarted && this.mChannelId != -1) {
            this.mDataManager.unregisterChannelProgramsObserver(this.mChannelId, this.mProgramsObserver);
        }
        this.mStarted = false;
    }

    void recycle() {
        bind(-1, null, this.mProgramState, false, false, false);
        this.mStarted = false;
        notifyDataSetChanged();
    }

    public int getItemCount() {
        if (this.mDataManager.isProgramDataLoaded(this.mChannelId)) {
            return this.mDataManager.getProgramCount(this.mChannelId) + 1;
        }
        return 1;
    }

    public int getItemViewType(int position) {
        int itemCount = getItemCount();
        if (itemCount == 1) {
            return 2;
        }
        if (position != itemCount - 1) {
            return 0;
        }
        return 1;
    }

    public long getItemId(int position) {
        int itemViewType = getItemViewType(position);
        if (itemViewType == 1) {
            return -2;
        }
        if (itemViewType == 2) {
            return -3;
        }
        return this.mDataManager.getProgram(this.mChannelId, position).getId();
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            return new ProgramViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_program, parent, false), this.mEventLogger);
        }
        if (viewType == 1) {
            return new LastProgramViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_last_program, parent, false));
        }
        if (viewType == 2) {
            return new EmptyChannelProgramViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_empty_channel_program, parent, false));
        }
        return null;
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder instanceof ProgramViewHolder) {
            ((ProgramViewHolder) holder).bind(this.mDataManager.getProgram(this.mChannelId, position));
        } else if (holder instanceof LastProgramViewHolder) {
            ((LastProgramViewHolder) holder).bind();
        } else if (holder instanceof EmptyChannelProgramViewHolder) {
            ((EmptyChannelProgramViewHolder) holder).bind();
        }
    }

    public void onBindViewHolder(ViewHolder holder, int position, List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else if (holder instanceof ProgramViewHolder) {
            ((ProgramViewHolder) holder).getProgramController().bindState(this.mProgramState);
        } else if (holder instanceof LastProgramViewHolder) {
            ((LastProgramViewHolder) holder).bindState();
        } else if (holder instanceof EmptyChannelProgramViewHolder) {
            ((EmptyChannelProgramViewHolder) holder).bindState();
        }
    }
}
