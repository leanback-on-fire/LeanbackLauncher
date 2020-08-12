package com.amazon.tv.leanbacklauncher.inputs;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.ColorMatrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.tv.TvContract;
import android.media.tv.TvContract.Channels;
import android.media.tv.TvInputInfo;
import android.media.tv.TvInputManager;
import android.media.tv.TvInputManager.TvInputCallback;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.amazon.tv.leanbacklauncher.LauncherViewHolder;
import com.amazon.tv.leanbacklauncher.R;
import com.amazon.tv.leanbacklauncher.apps.BannerView;
import com.amazon.tv.leanbacklauncher.util.Partner;
import com.amazon.tv.leanbacklauncher.widget.RowViewAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class InputsAdapter extends RowViewAdapter<InputsAdapter.InputViewHolder> {
    private static final String[] mPhysicalTunerBlackList = new String[]{"com.google.android.videos", "com.google.android.youtube.tv", "com.amazon.avod", "com.amazon.hedwig"}; // TODO add amazon
    private final InputsComparator mComp = new InputsComparator();
    private final Configuration mConfig;
    private final Handler mHandler = new InputsMessageHandler(this);


    private static class InputsMessageHandler extends Handler {
        private InputsAdapter ref;

        private InputsMessageHandler(InputsAdapter ref) {
            this.ref = ref;
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    ref.inputStateUpdated((String) msg.obj, msg.arg1);
                    return;
                case 2:
                    ref.inputAdded((String) msg.obj, false);
                    return;
                case 3:
                    ref.inputRemoved((String) msg.obj);
                    return;
                case 4:
                    ref.inputEntryModified((TvInputInfo) msg.obj);
                    return;
                default:
            }
        }
    }

    private final LayoutInflater mInflater;
    private final HashMap<String, TvInputEntry> mInputs = new HashMap<>();
    private final InputCallback mInputsCallback;
    private boolean mIsBundledTunerVisible = false;
    private final LinkedHashMap<String, TvInputInfo> mPhysicalTunerInputs = new LinkedHashMap<>();
    private final int mTextColorDark;
    private final int mTextColorLight;
    private final TvInputManager mTvManager;
    private Map<Integer, Integer> mTypePriorities;
    private final HashMap<String, TvInputInfo> mVirtualTunerInputs = new HashMap<>();
    private final List<TvInputEntry> mVisibleInputs = new ArrayList<>();

    public static final class Configuration {
        final boolean mDisableDiconnectedInputs;
        final boolean mGetStateIconFromTVInput;
        final boolean mShowPhysicalTunersSeparately;

        public Configuration(boolean showPhysicalTunersSeparately, boolean disableDiconnectedInputs, boolean stateIconFromTVInput) {
            this.mShowPhysicalTunersSeparately = showPhysicalTunersSeparately;
            this.mDisableDiconnectedInputs = disableDiconnectedInputs;
            this.mGetStateIconFromTVInput = stateIconFromTVInput;
        }
    }

    class InputCallback extends TvInputCallback {
        InputCallback() {
        }

        public void onInputStateChanged(String inputId, int state) {
            InputsAdapter.this.mHandler.sendMessage(InputsAdapter.this.mHandler.obtainMessage(1, state, 0, inputId));
        }

        public void onInputAdded(String inputId) {
            InputsAdapter.this.mHandler.sendMessage(InputsAdapter.this.mHandler.obtainMessage(2, inputId));
        }

        public void onInputRemoved(String inputId) {
            InputsAdapter.this.mHandler.sendMessage(InputsAdapter.this.mHandler.obtainMessage(3, inputId));
        }

        public void onTvInputInfoUpdated(TvInputInfo inputInfo) {
            InputsAdapter.this.mHandler.sendMessage(InputsAdapter.this.mHandler.obtainMessage(4, inputInfo));
        }
    }

    class InputViewHolder extends LauncherViewHolder {
        private final Drawable mBackground;
        private final BannerView mBannerView;
        private final ColorMatrix mColorMatrix = new ColorMatrix();
        private boolean mDisconnected = false;
        private boolean mEnabled = true;
        private final ImageView mImageView;
        private final TextView mLabelView;

        InputViewHolder(View v) {
            super(v);
            this.mColorMatrix.setScale(0.5f, 0.5f, 0.5f, 1.0f);
            if (v instanceof BannerView) {
                this.mBannerView = (BannerView) v;
                this.mImageView = v.findViewById(R.id.input_image);
                this.mLabelView = v.findViewById(R.id.input_label);
                this.mBackground = v.getResources().getDrawable(R.drawable.input_banner_background, null);
                return;
            }
            this.mBannerView = null;
            this.mImageView = null;
            this.mLabelView = null;
            this.mBackground = null;
        }

        public void init(TvInputEntry entry) {
            this.itemView.setVisibility(View.VISIBLE);
            if (entry != null) {
                boolean connected = entry.isConnected();
                this.mEnabled = entry.isEnabled();
                this.mDisconnected = entry.isDisconnected();
                this.mBannerView.setEnabled(this.mEnabled);
                if (this.mImageView != null) {
                    Drawable drawable = entry.getImageDrawable(entry.mState);
                    if (!(drawable instanceof BitmapDrawable) || ((BitmapDrawable) drawable).getBitmap().hasAlpha()) {
                        this.mBannerView.setBackground(this.mBackground);
                    } else {
                        this.mBannerView.setBackground(null);
                    }
                    this.mBannerView.getViewDimmer().setConcatMatrix(connected ? null : this.mColorMatrix);
                    this.mImageView.setImageDrawable(drawable);
                }
                if (this.mLabelView != null) {
                    this.mLabelView.setText(entry.getLabel());
                    this.mBannerView.setTextViewColor(this.mLabelView, entry.isLabelDarkColor() ? InputsAdapter.this.mTextColorDark : InputsAdapter.this.mTextColorLight);
                }
                setLaunchIntent(entry.getLaunchIntent());
                setLaunchColor(ContextCompat.getColor(InputsAdapter.this.mContext, R.color.input_banner_launch_ripple_color));
            }
        }

        public void onClick(View v) {
            if (this.mDisconnected) {
                String toastText = Partner.get(InputsAdapter.this.mContext).getDisconnectedInputToastText();
                if (!TextUtils.isEmpty(toastText)) {
                    Toast.makeText(InputsAdapter.this.mContext, toastText, Toast.LENGTH_SHORT).show();
                }
            } else if (this.mEnabled) {
                super.onClick(v);
            }
        }
    }

    private class InputsComparator implements Comparator<TvInputEntry> {
        private InputsComparator() {
        }

        public int compare(TvInputEntry lhs, TvInputEntry rhs) {
            int i = 0;
            int i2 = -1;
            if (rhs == null) {
                if (lhs != null) {
                    i = -1;
                }
                return i;
            } else if (lhs == null) {
                return 1;
            } else {
                if (InputsAdapter.this.mConfig.mDisableDiconnectedInputs) {
                    boolean disconnectedL;
                    boolean disconnectedR;
                    disconnectedL = lhs.mState == 2;
                    disconnectedR = rhs.mState == 2;
                    if (disconnectedL != disconnectedR) {
                        if (disconnectedL) {
                            return 1;
                        }
                        return -1;
                    }
                }
                if (lhs.mPriority != rhs.mPriority) {
                    return lhs.mPriority - rhs.mPriority;
                }
                if (lhs.mType == 0 && rhs.mType == 0) {
                    boolean rIsPhysical = InputsAdapter.isPhysicalTuner(InputsAdapter.this.mContext.getPackageManager(), rhs.mInfo);
                    boolean lIsPhysical = InputsAdapter.isPhysicalTuner(InputsAdapter.this.mContext.getPackageManager(), lhs.mInfo);
                    if (rIsPhysical != lIsPhysical) {
                        if (!lIsPhysical) {
                            i2 = 1;
                        }
                        return i2;
                    }
                }
                if (lhs.mSortKey != rhs.mSortKey) {
                    return rhs.mSortKey - lhs.mSortKey;
                }
                if (TextUtils.equals(lhs.mParentLabel, rhs.mParentLabel) || lhs.mParentLabel == null) {
                    return lhs.mLabel.compareToIgnoreCase(rhs.mLabel);
                }
                return lhs.mParentLabel.compareToIgnoreCase(rhs.mParentLabel);
            }
        }
    }

    class TvInputEntry {
        public Drawable mBanner;
        public int mBannerState;
        public final Object mHdmiInfo; // edited
        public final TvInputInfo mInfo;
        public String mLabel;
        public int mNumChildren;
        public final TvInputEntry mParentEntry;
        public final String mParentLabel;
        public final int mPriority;
        public final int mSortKey;
        public int mState;
        public final int mTextColorOption;
        public final int mType;

        public TvInputEntry(String label, Drawable banner, int colorOption, int type) {
            this.mInfo = null;
            this.mLabel = label;
            this.mParentLabel = null;
            this.mBanner = banner;
            this.mHdmiInfo = null;
            this.mTextColorOption = colorOption;
            this.mType = type;
            this.mPriority = InputsAdapter.this.getPriorityForType(type);
            this.mSortKey = Integer.MAX_VALUE;
            this.mParentEntry = null;
            this.mBannerState = 0;
            this.mState = 0;
        }

        public TvInputEntry(TvInputInfo info, TvInputEntry parent, int state, Context ctx) {
            this.mInfo = info;
            this.mType = this.mInfo.getType();

            this.mHdmiInfo = null;

            CharSequence label = null;

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                label = info.loadCustomLabel(ctx);
            }

            if (TextUtils.isEmpty(label)) {
                label = info.loadLabel(ctx);
            }
            if (label != null) {
                this.mLabel = label.toString();
            } else {
                this.mLabel = "";
            }
            this.mTextColorOption = this.mInfo.getServiceInfo().metaData.getInt("input_banner_label_color_option", 0);
            this.mSortKey = this.mInfo.getServiceInfo().metaData.getInt("input_sort_key", Integer.MAX_VALUE);
            this.mState = state;

            this.mPriority = InputsAdapter.this.getPriorityForType(-6);

            this.mParentEntry = parent;
            if (this.mParentEntry != null) {
                label = this.mParentEntry.mInfo.loadLabel(ctx);

                if (label != null) {
                    this.mParentLabel = label.toString();
                } else {
                    this.mParentLabel = "";
                }
            } else {
                this.mParentLabel = this.mLabel;
            }
            this.mBanner = getImageDrawable(this.mState);
        }

        private Drawable loadIcon() {
            if (InputsAdapter.this.mConfig.mGetStateIconFromTVInput && VERSION.SDK_INT >= 24) {
                // TODO Fix this API incompatibility.
                Drawable icon = null;//this.mInfo.loadIcon(InputsAdapter.this.mContext, this.mState);
                if (icon != null) {
                    return icon;
                }
            }
            return this.mInfo.loadIcon(InputsAdapter.this.mContext);
        }

        public boolean isBundledTuner() {
            return this.mType == -3;
        }

        public boolean isEnabled() {
            return isConnected() || !InputsAdapter.this.mConfig.mDisableDiconnectedInputs;
        }

        public boolean isConnected() {
            return isBundledTuner() || this.mState != 2;
        }

        public boolean isDisconnected() {
            return !isBundledTuner() && this.mState == 2;
        }

        public Drawable getImageDrawable(int newState) {
            if (this.mBanner != null && this.mState == this.mBannerState) {
                return this.mBanner;
            }
            int drawableId;
            this.mBannerState = newState;
            if (this.mInfo != null) {
                this.mBanner = loadIcon();
                if (this.mBanner != null) {
                    return this.mBanner;
                }
            }
            switch (this.mType) {
                case -3:
                case 0:
                    if (this.mState != 2) {
                        if (this.mState != 1) {
                            drawableId = R.drawable.ic_input_tuner;
                            break;
                        }
                        drawableId = R.drawable.ic_input_tuner_standby;
                        break;
                    }
                    drawableId = R.drawable.ic_input_tuner_disconnected;
                    break;
                case 1001:
                    if (this.mState != 2) {
                        if (this.mState != 1) {
                            drawableId = R.drawable.ic_input_composite;
                            break;
                        }
                        drawableId = R.drawable.ic_input_composite_standby;
                        break;
                    }
                    drawableId = R.drawable.ic_input_composite_disconnected;
                    break;
                case 1002:
                    if (this.mState != 2) {
                        if (this.mState != 1) {
                            drawableId = R.drawable.ic_input_svideo;
                            break;
                        }
                        drawableId = R.drawable.ic_input_svideo_standby;
                        break;
                    }
                    drawableId = R.drawable.ic_input_svideo_disconnected;
                    break;
                case 1003:
                    if (this.mState != 2) {
                        if (this.mState != 1) {
                            drawableId = R.drawable.ic_input_scart;
                            break;
                        }
                        drawableId = R.drawable.ic_input_scart_standby;
                        break;
                    }
                    drawableId = R.drawable.ic_input_scart_disconnected;
                    break;
                case 1004:
                    if (this.mState != 2) {
                        if (this.mState != 1) {
                            drawableId = R.drawable.ic_input_component;
                            break;
                        }
                        drawableId = R.drawable.ic_input_component_standby;
                        break;
                    }
                    drawableId = R.drawable.ic_input_component_disconnected;
                    break;
                case 1005:
                    if (this.mState != 2) {
                        if (this.mState != 1) {
                            drawableId = R.drawable.ic_input_vga;
                            break;
                        }
                        drawableId = R.drawable.ic_input_vga_standby;
                        break;
                    }
                    drawableId = R.drawable.ic_input_vga_disconnected;
                    break;
                case 1006:
                    if (this.mState != 2) {
                        if (this.mState != 1) {
                            drawableId = R.drawable.ic_input_dvi;
                            break;
                        }
                        drawableId = R.drawable.ic_input_dvi_standby;
                        break;
                    }
                    drawableId = R.drawable.ic_input_dvi_disconnected;
                    break;
                case 1007:
                    if (this.mHdmiInfo == null) {
                        if (this.mState != 2) {
                            if (this.mState != 1) {
                                drawableId = R.drawable.ic_input_hdmi;
                                break;
                            }
                            drawableId = R.drawable.ic_input_hdmi_standby;
                            break;
                        }
                        drawableId = R.drawable.ic_input_hdmi_disconnected;
                        break;
                    }


                    drawableId = R.drawable.ic_input_tuner;

                    // drawableId = R.drawable.ic_input_mhl;
                    break;

                case 1008:
                    if (this.mState != 2) {
                        if (this.mState != 1) {
                            drawableId = R.drawable.ic_input_display_port;
                            break;
                        }
                        drawableId = R.drawable.ic_input_display_port_standby;
                        break;
                    }
                    drawableId = R.drawable.ic_input_display_port_disconnected;
                    break;
                default:
                    if (this.mState != 2) {
                        if (this.mState != 1) {
                            drawableId = R.drawable.ic_input_hdmi;
                            break;
                        }
                        drawableId = R.drawable.ic_input_hdmi_standby;
                        break;
                    }
                    drawableId = R.drawable.ic_input_hdmi_disconnected;
                    break;
            }
            Drawable drawable = ContextCompat.getDrawable(InputsAdapter.this.mContext, drawableId);
            this.mBanner = drawable;
            return drawable;
        }

        public String getLabel() {

            if (!TextUtils.isEmpty(this.mLabel)) {
                return this.mLabel;
            }
            if (this.mType == -3) {
                return InputsAdapter.this.mContext.getResources().getString(R.string.input_title_bundled_tuner);
            }
            return null;
        }

        public boolean isLabelDarkColor() {
            return this.mTextColorOption == 1;
        }

        public Intent getLaunchIntent() {
            if (this.mInfo != null) {
                if (this.mInfo.isPassthroughInput()) {
                    return new Intent("android.intent.action.VIEW", TvContract.buildChannelUriForPassthroughInput(this.mInfo.getId()));
                }
                return new Intent("android.intent.action.VIEW", TvContract.buildChannelsUriForInput(this.mInfo.getId()));
            } else if (!isBundledTuner()) {
                return null;
            } else {
                Uri uri;
                if (VERSION.SDK_INT < 23) {
                    uri = TvContract.buildChannelUri(0);
                } else {
                    uri = Channels.CONTENT_URI;
                }
                return new Intent("android.intent.action.VIEW", uri);
            }
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof TvInputEntry)) {
                return false;
            }
            TvInputEntry obj = (TvInputEntry) o;
            if (isBundledTuner() && obj.isBundledTuner()) {
                return true;
            }
            return this.mInfo != null && obj.mInfo != null && this.mInfo.equals(obj.mInfo);
        }
    }

    public InputsAdapter(Context context, Configuration config) {
        super(context);
        this.mConfig = config;
        this.mTvManager = (TvInputManager) context.getSystemService(Context.TV_INPUT_SERVICE);
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        setupDeviceTypePriorities();
        this.mTextColorLight = ContextCompat.getColor(this.mContext, R.color.input_banner_label_text_color_light);
        this.mTextColorDark = ContextCompat.getColor(this.mContext, R.color.input_banner_label_text_color_dark);
        refreshInputs();
        this.mInputsCallback = new InputCallback();
        if (this.mTvManager != null) {
            this.mTvManager.registerCallback(this.mInputsCallback, this.mHandler);
        }
    }

    public void unregisterReceivers() {
        if (this.mTvManager != null) {
            this.mTvManager.unregisterCallback(this.mInputsCallback);
        }
    }

    public InputViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new InputViewHolder(this.mInflater.inflate(R.layout.input_banner, parent, false));
    }

    public void onBindViewHolder(InputViewHolder holder, int position) {
        if (position < this.mVisibleInputs.size()) {
            holder.init(this.mVisibleInputs.get(position));
        }
    }

    public int getItemCount() {
        return this.mVisibleInputs.size();
    }

    public void refreshInputsData() {
        refreshInputs();
        notifyDataSetChanged();
    }

    private void refreshInputs() {
        this.mInputs.clear();
        this.mVisibleInputs.clear();
        this.mPhysicalTunerInputs.clear();
        this.mVirtualTunerInputs.clear();
        this.mIsBundledTunerVisible = false;
        // https://stackoverflow.com/questions/45074883/nullpointerexception-in-tvinputmanager-gettvinputlist
        boolean tifSupport = InputsAdapter.this.mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_LIVE_TV);
        if (this.mTvManager != null && tifSupport) {
            List<TvInputInfo> serviceInputs = this.mTvManager.getTvInputList();
            if (serviceInputs != null) {
                for (int i = 0; i < serviceInputs.size(); i++) {
                    inputAdded(serviceInputs.get(i), true);
                }
                Collections.sort(this.mVisibleInputs, this.mComp);
            }
        }
    }

    private void hideBundledTunerInput(boolean isRefresh) {
        if (this.mIsBundledTunerVisible) {
            for (int i = this.mVisibleInputs.size() - 1; i >= 0; i--) {
                if (this.mVisibleInputs.get(i).isBundledTuner()) {
                    this.mVisibleInputs.remove(i);
                    if (!isRefresh) {
                        notifyItemRemoved(i);
                    }
                    this.mIsBundledTunerVisible = false;
                }
            }
        }
    }

    private void showBundledTunerInput(boolean isRefresh) {
        if (!this.mIsBundledTunerVisible) {
            TvInputEntry bundledTuner = new TvInputEntry(Partner.get(this.mContext).getBundledTunerTitle(), Partner.get(this.mContext).getBundledTunerBanner(), Partner.get(this.mContext).getBundledTunerLabelColorOption(0), -3);
            if (isRefresh) {
                this.mVisibleInputs.add(bundledTuner);
            } else {
                notifyItemInserted(insertEntryIntoSortedList(bundledTuner, this.mVisibleInputs));
            }
            this.mIsBundledTunerVisible = true;
        }
    }

    private void addInputEntry(TvInputInfo input, boolean isRefresh) {
        try {
            int state = this.mTvManager.getInputState(input.getId());
            TvInputEntry parentEntry = null;
            if (this.mInputs.get(input.getId()) == null) {
                if (!(input.getParentId() == null)) {
                    TvInputInfo parentInfo = this.mTvManager.getTvInputInfo(input.getParentId());
                    if (parentInfo != null) {
                        parentEntry = this.mInputs.get(parentInfo.getId());
                        if (parentEntry == null) {
                            parentEntry = new TvInputEntry(parentInfo, null, this.mTvManager.getInputState(parentInfo.getId()), this.mContext);
                            this.mInputs.put(parentInfo.getId(), parentEntry);
                        }
                        parentEntry.mNumChildren++;
                    }
                }
                TvInputEntry entry = new TvInputEntry(input, parentEntry, state, this.mContext);
                this.mInputs.put(input.getId(), entry);

                if (VERSION.SDK_INT < Build.VERSION_CODES.N || !entry.mInfo.isHidden(this.mContext)) {
                    if (parentEntry != null && (VERSION.SDK_INT < Build.VERSION_CODES.N || parentEntry.mInfo.isHidden(this.mContext))) {
                        return;
                    }
                    if (isRefresh) {
                        this.mVisibleInputs.add(entry);
                        if (parentEntry != null && parentEntry.mInfo.getParentId() == null && !this.mVisibleInputs.contains(parentEntry)) {
                            this.mVisibleInputs.add(parentEntry);
                            return;
                        }
                        return;
                    }
                    notifyItemInserted(insertEntryIntoSortedList(entry, this.mVisibleInputs));
                    if (parentEntry != null && parentEntry.mInfo.getParentId() == null && !this.mVisibleInputs.contains(parentEntry)) {
                        notifyItemInserted(insertEntryIntoSortedList(parentEntry, this.mVisibleInputs));
                    }
                }

            }
        } catch (IllegalArgumentException e) {
            Log.e("InputsAdapter", "Failed to get state for Input, droppig entry. Id = " + input.getId());
        }
    }

    private int getIndexInVisibleList(String id) {
        for (int i = 0; i < this.mVisibleInputs.size(); i++) {
            TvInputInfo info = this.mVisibleInputs.get(i).mInfo;
            if (info != null && TextUtils.equals(info.getId(), id)) {
                return i;
            }
        }
        return -1;
    }

    private int insertEntryIntoSortedList(TvInputEntry entry, List<TvInputEntry> list) {
        int i = 0;
        while (i < list.size() && this.mComp.compare(entry, list.get(i)) > 0) {
            i++;
        }
        if (!list.contains(entry)) {
            list.add(i, entry);
        }
        return i;
    }

    private void inputStateUpdated(String id, int state) {
        boolean isNowConnected = true;
        TvInputEntry entry = this.mInputs.get(id);
        if (entry != null) {
            boolean wasConnected = entry.mState != 2;
            if (state == 2) {
                isNowConnected = false;
            }
            entry.mState = state;
            int visPos = getIndexInVisibleList(id);
            if (visPos < 0) {
                return;
            }
            if (!this.mConfig.mDisableDiconnectedInputs || wasConnected == isNowConnected) {
                notifyItemChanged(visPos);
                return;
            }
            this.mVisibleInputs.remove(visPos);
            int i = insertEntryIntoSortedList(entry, this.mVisibleInputs);
            notifyItemMoved(visPos, i);
            notifyItemChanged(i);
        }
    }

    private void inputEntryModified(TvInputInfo inputInfo) {
        // TODO Removed custom label support...
    }

    private void inputAdded(String id, boolean isRefresh) {
        if (this.mTvManager != null) {
            inputAdded(this.mTvManager.getTvInputInfo(id), isRefresh);
        }
    }

    private void inputAdded(TvInputInfo info, boolean isRefresh) {
        if (info == null) {
            return;
        }
        if (info.isPassthroughInput()) {
            addInputEntry(info, isRefresh);
        } else if (isPhysicalTuner(this.mContext.getPackageManager(), info)) {
            this.mPhysicalTunerInputs.put(info.getId(), info);
            if (this.mConfig.mShowPhysicalTunersSeparately) {
                addInputEntry(info, isRefresh);
            } else if (VERSION.SDK_INT < Build.VERSION_CODES.N || !info.isHidden(this.mContext)) {
                showBundledTunerInput(isRefresh);
            }
        } else {
            this.mVirtualTunerInputs.put(info.getId(), info);

            if (VERSION.SDK_INT < Build.VERSION_CODES.N || !info.isHidden(this.mContext)) {
                showBundledTunerInput(isRefresh);
            }

        }
    }

    private void inputRemoved(String id) {
        TvInputEntry entry = this.mInputs.get(id);
        if (entry == null || entry.mInfo == null || !entry.mInfo.isPassthroughInput()) {
            removeTuner(id);
        } else {
            removeEntry(id);
        }
    }

    private void removeTuner(String id) {
        removeEntry(id);
        this.mVirtualTunerInputs.remove(id);
        this.mPhysicalTunerInputs.remove(id);
        if (!this.mVirtualTunerInputs.isEmpty()) {
            return;
        }
        if (this.mPhysicalTunerInputs.isEmpty() || this.mConfig.mShowPhysicalTunersSeparately) {
            hideBundledTunerInput(false);
        }
    }

    private void removeEntry(String id) {
        TvInputEntry entry = this.mInputs.get(id);
        if (entry != null) {
            this.mInputs.remove(id);
            for (int i = this.mVisibleInputs.size() - 1; i >= 0; i--) {
                TvInputEntry anEntry = this.mVisibleInputs.get(i);
                if (!(anEntry.mParentEntry == null || anEntry.mParentEntry.mInfo == null || !TextUtils.equals(anEntry.mParentEntry.mInfo.getId(), id))) {
                    this.mInputs.remove(anEntry.mInfo.getId());
                    this.mVisibleInputs.remove(i);
                    notifyItemRemoved(i);
                }
            }
            int index = getIndexInVisibleList(id);
            if (index != -1) {
                this.mVisibleInputs.remove(index);
                TvInputEntry parent = entry.mParentEntry;
                if (parent != null) {
                    parent.mNumChildren = Math.max(0, parent.mNumChildren - 1);
                    if (parent.mNumChildren == 0) {
                        int parentIndex = this.mVisibleInputs.indexOf(parent);
                        if (parentIndex != -1) {
                            notifyItemRemoved(index);
                            this.mVisibleInputs.remove(parentIndex);
                        } else {
                            parentIndex = index;
                        }
                        int newPos = insertEntryIntoSortedList(parent, this.mVisibleInputs);
                        notifyItemMoved(parentIndex, newPos);
                        notifyItemChanged(newPos);
                        return;
                    }
                    notifyItemRemoved(index);
                    return;
                }
                notifyItemRemoved(index);
            }
        }
    }

    private static boolean isPhysicalTuner(PackageManager pkgMan, TvInputInfo input) {
        boolean isPhysicalTuner = true;
        if (Arrays.asList(mPhysicalTunerBlackList).contains(input.getServiceInfo().packageName)) {
            return false;
        }
        if (input.createSetupIntent() == null) {
            isPhysicalTuner = false;
        } else {
            boolean mayBeTunerInput = pkgMan.checkPermission("com.android.providers.tv.permission.ACCESS_ALL_EPG_DATA", input.getServiceInfo().packageName) == PackageManager.PERMISSION_GRANTED;

            if (!mayBeTunerInput) {
                try {
                    if ((pkgMan.getApplicationInfo(input.getServiceInfo().packageName, 0).flags & 129) == 0) {
                        isPhysicalTuner = false;
                    }
                } catch (NameNotFoundException e) {
                    isPhysicalTuner = false;
                }
            }
        }
        // Log.d("InputsAdapter", "isPhysicalTuner: " + isPhysicalTuner + " for " + input.getServiceInfo().packageName);
        return isPhysicalTuner;
    }

    private int getPriorityForType(int type) {
        if (this.mTypePriorities != null) {
            Integer priority = this.mTypePriorities.get(type);
            if (priority != null) {
                return priority;
            }
        }
        return Integer.MAX_VALUE;
    }

    private int addToPriorityIfMissing(int key, int priority) {
        if (this.mTypePriorities.containsKey(key)) {
            return priority;
        }
        int priority2 = priority + 1;
        this.mTypePriorities.put(key, priority);
        return priority2;
    }

    private void setupDeviceTypePriorities() {
        this.mTypePriorities = Partner.get(this.mContext).getInputsOrderMap();
        addToPriorityIfMissing(1000, addToPriorityIfMissing(1003, addToPriorityIfMissing(1005, addToPriorityIfMissing(1008, addToPriorityIfMissing(1001, addToPriorityIfMissing(1002, addToPriorityIfMissing(1004, addToPriorityIfMissing(1006, addToPriorityIfMissing(1007, addToPriorityIfMissing(-6, addToPriorityIfMissing(-5, addToPriorityIfMissing(-4, addToPriorityIfMissing(-2, addToPriorityIfMissing(0, addToPriorityIfMissing(-3, this.mTypePriorities.size())))))))))))))));
    }
}
