package com.rockchips.android.leanbacklauncher.inputs;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
///import android.hardware.hdmi.HdmiDeviceInfo;
import android.media.tv.TvContract;
import android.media.tv.TvContract.Channels;
import android.media.tv.TvInputInfo;
import android.media.tv.TvInputManager;
import android.media.tv.TvInputManager.TvInputCallback;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.rockchips.android.leanbacklauncher.LauncherViewHolder;
import com.rockchips.android.leanbacklauncher.R;
import com.rockchips.android.leanbacklauncher.apps.BannerView;
import com.rockchips.android.leanbacklauncher.util.Partner;
import com.rockchips.android.leanbacklauncher.widget.RowViewAdapter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class InputsAdapter extends RowViewAdapter<InputsAdapter.InputViewHolder> {
    private static final String[] PHYSICAL_TUNER_BLACK_LIST;
    private final InputsComparator mComp;
    private final Configuration mConfig;
    private final Handler mHandler;
    private final float mImageAlphaConnected;
    private final float mImageAlphaDisconnected;
    private final LayoutInflater mInflater;
    private final HashMap<String, TvInputEntry> mInputs;
    private final InputCallback mInputsCallback;
    private boolean mIsBundledTunerVisible;
    private final LinkedHashMap<String, TvInputInfo> mPhysicalTunerInputs;
    private final float mTextAlphaConnected;
    private final float mTextAlphaDisconnected;
    private final int mTextColorDark;
    private final int mTextColorLight;
    private final TvInputManager mTvManager;
    private Map<Integer, Integer> mTypePriorities;
    private final HashMap<String, TvInputInfo> mVirtualTunerInputs;
    private final List<TvInputEntry> mVisibleInputs;

    /* renamed from: InputsAdapter.1 */
    class C01891 extends Handler {
        C01891() {
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case android.support.v7.recyclerview.R.styleable.RecyclerView_android_descendantFocusability /*1*/:
                    InputsAdapter.this.inputStateUpdated((String) msg.obj, msg.arg1);
                case android.support.v7.recyclerview.R.styleable.RecyclerView_layoutManager /*2*/:
                    InputsAdapter.this.inputAdded((String) msg.obj, false);
                case android.support.v7.preference.R.styleable.Preference_android_layout /*3*/:
                    InputsAdapter.this.inputRemoved((String) msg.obj);
                default:
            }
        }
    }

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

    public class InputCallback extends TvInputCallback {
        public void onInputStateChanged(String inputId, int state) {
            InputsAdapter.this.mHandler.sendMessage(InputsAdapter.this.mHandler.obtainMessage(1, state, 0, inputId));
        }

        public void onInputAdded(String inputId) {
            InputsAdapter.this.mHandler.sendMessage(InputsAdapter.this.mHandler.obtainMessage(2, inputId));
        }

        public void onInputRemoved(String inputId) {
            InputsAdapter.this.mHandler.sendMessage(InputsAdapter.this.mHandler.obtainMessage(3, inputId));
        }
    }

    class InputViewHolder extends LauncherViewHolder {
        private final BannerView mBannerView;
        private boolean mDisconnected;
        private boolean mEnabled;
        private final ImageView mImageView;
        private final TextView mLabelView;

        public InputViewHolder(View v) {
            super(v);
            this.mEnabled = true;
            this.mDisconnected = false;
            if (v instanceof BannerView) {
                this.mBannerView = (BannerView) v;
                this.mImageView = (ImageView) v.findViewById(R.id.input_image);
                this.mLabelView = (TextView) v.findViewById(R.id.input_label);
                return;
            }
            this.mBannerView = null;
            this.mImageView = null;
            this.mLabelView = null;
        }

        public void init(TvInputEntry entry) {
            this.itemView.setVisibility(0);
            if (entry != null) {
                boolean connected = entry.isConnected();
                this.mEnabled = entry.isEnabled();
                this.mDisconnected = entry.isDisconnected();
                this.mBannerView.setEnabled(this.mEnabled);
                if (this.mImageView != null) {
                    this.mImageView.setImageDrawable(entry.getImageDrawable(entry.mState));
                    this.mImageView.setAlpha(connected ? InputsAdapter.this.mImageAlphaConnected : InputsAdapter.this.mImageAlphaDisconnected);
                }
                if (this.mLabelView != null) {
                    this.mLabelView.setText(entry.getLabel());
                    this.mLabelView.setAlpha(connected ? InputsAdapter.this.mTextAlphaConnected : InputsAdapter.this.mTextAlphaDisconnected);
                    this.mBannerView.setTextViewColor(this.mLabelView, entry.isLabelDarkColor() ? InputsAdapter.this.mTextColorDark : InputsAdapter.this.mTextColorLight);
                }
                setLaunchIntent(entry.getLaunchIntent());
                setLaunchColor(InputsAdapter.this.mContext.getResources().getColor(R.color.input_banner_launch_ripple_color));
            }
        }

        public void onClick(View v) {
            if (this.mDisconnected) {
                String toastText = Partner.get(InputsAdapter.this.mContext).getDisconnectedInputToastText();
                if (!TextUtils.isEmpty(toastText)) {
                    Toast.makeText(InputsAdapter.this.mContext, toastText, 0).show();
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
            int i = -1;
            int i2 = 1;
            int i3 = 0;
            if (rhs == null) {
                if (lhs != null) {
                    i3 = -1;
                }
                return i3;
            } else if (lhs == null) {
                if (rhs != null) {
                    i3 = 1;
                }
                return i3;
            } else {
                if (InputsAdapter.this.mConfig.mDisableDiconnectedInputs) {
                    boolean disconnectedR;
                    boolean disconnectedL = lhs.mState == 2;
                    if (rhs.mState == 2) {
                        disconnectedR = true;
                    } else {
                        disconnectedR = false;
                    }
                    if (disconnectedL != disconnectedR) {
                        if (!disconnectedL) {
                            i2 = -1;
                        }
                        return i2;
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
                            i = 1;
                        }
                        return i;
                    }
                }
                if (lhs.mSortKey != rhs.mSortKey) {
                    return rhs.mSortKey - lhs.mSortKey;
                }
                if (TextUtils.equals(lhs.mParentLabel, rhs.mParentLabel)) {
                    return lhs.mLabel.compareToIgnoreCase(rhs.mLabel);
                }
                return lhs.mParentLabel.compareToIgnoreCase(rhs.mParentLabel);
            }
        }
    }

    private class TvInputEntry {
        public Drawable mBanner;
        public int mBannerState;
        public final Object mHdmiInfo;
        public final TvInputInfo mInfo;
        public final String mLabel;
        public int mNumChildren;
        public final TvInputEntry mParentEntry;
        public final String mParentLabel;
        public int mPriority;
        public final int mSortKey;
        public int mState;
        public final int mTextColorOption;
        public final int mType;

        public TvInputEntry(String label, Drawable banner, int colorOption, int type) {
            this.mInfo = null;
            this.mHdmiInfo = null;
            this.mLabel = label;
            this.mParentLabel = null;
            this.mBanner = banner;
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
            if (this.mType == 1007) {
                this.mHdmiInfo = invokeMethod("getHdmiDeviceInfo", mInfo, new Class[]{}, new Object[]{});
                //this.mHdmiInfo = this.mInfo.getHdmiDeviceInfo();
            } else {
                this.mHdmiInfo = null;
            }
            CharSequence label = info.loadCustomLabel(ctx);
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
            Object deviceInfo = invokeMethod("getHdmiDeviceInfo", info, new Class[]{}, new Object[] {});
            //isCecDevice
            if (deviceInfo != null && (Boolean) invokeMethod("isCecDevice", deviceInfo, new Class[]{}, new Object[]{})) {
               /* switch (info.getHdmiDeviceInfo().getDeviceType()) {
                    case android.support.v7.recyclerview.R.styleable.RecyclerView_android_descendantFocusability *//*1*//*:
                        this.mPriority = InputsAdapter.this.getPriorityForType(-4);
                        break;
                    case android.support.v7.preference.R.styleable.Preference_android_title *//*4*//*:
                        this.mPriority = InputsAdapter.this.getPriorityForType(-5);
                        break;
                    default:
                        this.mPriority = InputsAdapter.this.getPriorityForType(-2);
                        break;
                }*/
            } else if (deviceInfo == null || !(Boolean) invokeMethod("isMhlDevice", deviceInfo, new Class[]{}, new Object[]{})) {
                this.mPriority = InputsAdapter.this.getPriorityForType(this.mType);
            } else {
                this.mPriority = InputsAdapter.this.getPriorityForType(-6);
            }
            this.mParentEntry = parent;
            if (this.mParentEntry != null) {
                label = this.mParentEntry.mInfo.loadCustomLabel(ctx);
                if (TextUtils.isEmpty(label)) {
                    label = this.mParentEntry.mInfo.loadLabel(ctx);
                }
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

                Drawable icon = this.mInfo.loadIcon(InputsAdapter.this.mContext);
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
                case android.support.v7.preference.R.styleable.Preference_android_icon /*0*/:
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

                    switch ((int)invokeMethod("getDeviceType", mHdmiInfo, new Class[]{}, new Object[]{})) {
                        case android.support.v7.preference.R.styleable.Preference_android_icon /*0*/:
                            drawableId = R.drawable.ic_input_livetv;
                            break;
                        case android.support.v7.recyclerview.R.styleable.RecyclerView_android_descendantFocusability /*1*/:
                            drawableId = R.drawable.ic_input_recording;
                            break;
                        case android.support.v7.preference.R.styleable.Preference_android_layout /*3*/:
                            drawableId = R.drawable.ic_input_tuner;
                            break;
                        case android.support.v7.preference.R.styleable.Preference_android_title /*4*/:
                            drawableId = R.drawable.ic_input_playback;
                            break;
                        case android.support.v7.preference.R.styleable.Preference_android_selectable /*5*/:
                            drawableId = R.drawable.ic_input_audio;
                            break;
                        default:
                            boolean isMhl = (boolean)invokeMethod("isMhlDevice", mHdmiInfo, new Class[]{}, new Object[]{});
                            if (!isMhl) {
                                drawableId = R.drawable.ic_input_tuner;
                                break;
                            }
                            drawableId = R.drawable.ic_input_mhl;
                            break;
                    }
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
            String displayName = (String) invokeMethod("getDisplayName", mHdmiInfo, new Class[]{}, new Object[]{});
            if (this.mHdmiInfo != null && !TextUtils.isEmpty(displayName)) {
                return displayName;
            }
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
            boolean z = false;
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
            if (!(this.mInfo == null || obj.mInfo == null)) {
                z = this.mInfo.equals(obj.mInfo);
            }
            return z;
        }
    }

    static {
        PHYSICAL_TUNER_BLACK_LIST = new String[]{"com.rockchips.android.videos", "com.rockchips.android.youtube.tv"};
    }

    public InputsAdapter(Context context, Configuration config) {
        super(context);
        this.mIsBundledTunerVisible = false;
        this.mHandler = new C01891();
        this.mComp = new InputsComparator();
        this.mInputs = new HashMap();
        this.mVisibleInputs = new ArrayList();
        this.mPhysicalTunerInputs = new LinkedHashMap();
        this.mVirtualTunerInputs = new HashMap();
        this.mConfig = config;
        this.mTvManager = (TvInputManager) context.getSystemService("tv_input");
        this.mInflater = (LayoutInflater) context.getSystemService("layout_inflater");
        setupDeviceTypePriorities();
        TypedValue out = new TypedValue();
        context.getResources().getValue(R.raw.input_banner_connected_text_alpha, out, true);
        this.mTextAlphaConnected = out.getFloat();
        context.getResources().getValue(R.raw.input_banner_disconnected_text_alpha, out, true);
        this.mTextAlphaDisconnected = out.getFloat();
        context.getResources().getValue(R.raw.input_banner_connected_image_alpha, out, true);
        this.mImageAlphaConnected = out.getFloat();
        context.getResources().getValue(R.raw.input_banner_disconnected_image_alpha, out, true);
        this.mImageAlphaDisconnected = out.getFloat();
        this.mTextColorLight = this.mContext.getResources().getColor(R.color.input_banner_label_text_color_light);
        this.mTextColorDark = this.mContext.getResources().getColor(R.color.input_banner_label_text_color_dark);
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
            holder.init((TvInputEntry) this.mVisibleInputs.get(position));
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
        if (this.mTvManager != null) {
            List<TvInputInfo> serviceInputs = this.mTvManager.getTvInputList();
            if (serviceInputs != null) {
                for (int i = 0; i < serviceInputs.size(); i++) {
                    inputAdded((TvInputInfo) serviceInputs.get(i), true);
                }
                Collections.sort(this.mVisibleInputs, this.mComp);
            }
        }
    }

    private void hideBundledTunerInput(boolean isRefresh) {
        if (this.mIsBundledTunerVisible) {
            for (int i = this.mVisibleInputs.size() - 1; i >= 0; i--) {
                if (((TvInputEntry) this.mVisibleInputs.get(i)).isBundledTuner()) {
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
            if (((TvInputEntry) this.mInputs.get(input.getId())) == null) {
                boolean isConnected = (boolean)invokeMethod("isConnectedToHdmiSwitch", input, new Class[]{}, new Object[]{});
                if (!(input.getParentId() == null || isConnected)) {
                    TvInputInfo parentInfo = this.mTvManager.getTvInputInfo(input.getParentId());
                    if (parentInfo != null) {
                        parentEntry = (TvInputEntry) this.mInputs.get(parentInfo.getId());
                        if (parentEntry == null) {
                            parentEntry = new TvInputEntry(parentInfo, null, this.mTvManager.getInputState(parentInfo.getId()), this.mContext);
                            this.mInputs.put(parentInfo.getId(), parentEntry);
                        }
                        parentEntry.mNumChildren++;
                    }
                }
                TvInputEntry entry = new TvInputEntry(input, parentEntry, state, this.mContext);
                this.mInputs.put(input.getId(), entry);
                if (!entry.mInfo.isHidden(this.mContext) && (parentEntry == null || !parentEntry.mInfo.isHidden(this.mContext))) {
                    if (isRefresh) {
                        this.mVisibleInputs.add(entry);
                        if (!(parentEntry == null || parentEntry.mInfo.getParentId() != null || this.mVisibleInputs.contains(parentEntry))) {
                            this.mVisibleInputs.add(parentEntry);
                        }
                    } else {
                        notifyItemInserted(insertEntryIntoSortedList(entry, this.mVisibleInputs));
                        if (!(parentEntry == null || parentEntry.mInfo.getParentId() != null || this.mVisibleInputs.contains(parentEntry))) {
                            notifyItemInserted(insertEntryIntoSortedList(parentEntry, this.mVisibleInputs));
                        }
                    }
                }
            }
        } catch (IllegalArgumentException e) {
            Log.e("InputsAdapter", "Failed to get state for Input, droppig entry. Id = " + input.getId());
        }
    }

    private int getIndexInVisibleList(String id) {
        for (int i = 0; i < this.mVisibleInputs.size(); i++) {
            TvInputInfo info = ((TvInputEntry) this.mVisibleInputs.get(i)).mInfo;
            if (info != null && TextUtils.equals(info.getId(), id)) {
                return i;
            }
        }
        return -1;
    }

    private int insertEntryIntoSortedList(TvInputEntry entry, List<TvInputEntry> list) {
        int i = 0;
        while (i < list.size() && this.mComp.compare(entry, (TvInputEntry) list.get(i)) > 0) {
            i++;
        }
        if (!list.contains(entry)) {
            list.add(i, entry);
        }
        return i;
    }

    private void inputStateUpdated(String id, int state) {
        TvInputEntry entry = (TvInputEntry) this.mInputs.get(id);
        if (entry != null) {
            boolean wasConnected = entry.mState != 2;
            boolean isNowConnected = state != 2;
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
            } else if (!info.isHidden(this.mContext)) {
                showBundledTunerInput(isRefresh);
            }
        } else {
            this.mVirtualTunerInputs.put(info.getId(), info);
            if (!info.isHidden(this.mContext)) {
                showBundledTunerInput(isRefresh);
            }
        }
    }

    private void inputRemoved(String id) {
        TvInputEntry entry = (TvInputEntry) this.mInputs.get(id);
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
        TvInputEntry entry = (TvInputEntry) this.mInputs.get(id);
        if (entry != null) {
            this.mInputs.remove(id);
            for (int i = this.mVisibleInputs.size() - 1; i >= 0; i--) {
                TvInputEntry anEntry = (TvInputEntry) this.mVisibleInputs.get(i);
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
                    } else {
                        notifyItemRemoved(index);
                    }
                } else {
                    notifyItemRemoved(index);
                }
            }
        }
    }

    private static boolean isPhysicalTuner(PackageManager pkgMan, TvInputInfo input) {
        boolean mayBeTunerInput = false;
        boolean isPhysicalTuner = true;
        if (Arrays.asList(PHYSICAL_TUNER_BLACK_LIST).contains(input.getServiceInfo().packageName)) {
            return false;
        }
        if (input.createSetupIntent() == null) {
            isPhysicalTuner = false;
        } else {
            if (pkgMan.checkPermission("com.android.providers.tv.permission.ACCESS_ALL_EPG_DATA", input.getServiceInfo().packageName) == 0) {
                mayBeTunerInput = true;
            }
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
        return isPhysicalTuner;
    }

    private int getPriorityForType(int type) {
        if (this.mTypePriorities != null) {
            Integer priority = (Integer) this.mTypePriorities.get(Integer.valueOf(type));
            if (priority != null) {
                return priority.intValue();
            }
        }
        return Integer.MAX_VALUE;
    }

    private int addToPriorityIfMissing(int key, int priority) {
        if (this.mTypePriorities.containsKey(Integer.valueOf(key))) {
            return priority;
        }
        int priority2 = priority + 1;
        this.mTypePriorities.put(Integer.valueOf(key), Integer.valueOf(priority));
        return priority2;
    }

    private void setupDeviceTypePriorities() {
        this.mTypePriorities = Partner.get(this.mContext).getInputsOrderMap();
        int priority = addToPriorityIfMissing(1000, addToPriorityIfMissing(1003, addToPriorityIfMissing(1005, addToPriorityIfMissing(1008, addToPriorityIfMissing(1001, addToPriorityIfMissing(1002, addToPriorityIfMissing(1004, addToPriorityIfMissing(1006, addToPriorityIfMissing(1007, addToPriorityIfMissing(-6, addToPriorityIfMissing(-5, addToPriorityIfMissing(-4, addToPriorityIfMissing(-2, addToPriorityIfMissing(0, addToPriorityIfMissing(-3, this.mTypePriorities.size())))))))))))))));
    }

    public Object invokeMethod(String methodName, Object object, Class[] methodParams, Object[] paramValues){
        try{
            Method method = object.getClass().getMethod(methodName, methodParams);
            method.setAccessible(true);
            return method.invoke(object, paramValues);
        }catch (Exception e){
            try{
                Method method2 =  object.getClass().getSuperclass().getMethod(methodName, methodParams);
                method2.setAccessible(true);
                return  method2.invoke(object, paramValues);
            }catch (Exception e2){

            }
        }
        return  null;

    }

    public Object getFieldValue(Object object, String fieldName){
        try{
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return  field.get(object);
        }catch (Exception e){
            try{
                Field field = object.getClass().getSuperclass().getDeclaredField(fieldName);
                field.setAccessible(true);
                return  field.get(fieldName);
            }catch (Exception e2){

            }
        }
        return  null;

    }
}
