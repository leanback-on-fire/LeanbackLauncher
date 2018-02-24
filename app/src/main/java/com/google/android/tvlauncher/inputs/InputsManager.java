package com.google.android.tvlauncher.inputs;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.media.tv.TvContract;
import android.media.tv.TvContract.Channels;
import android.media.tv.TvInputInfo;
import android.media.tv.TvInputManager;
import android.media.tv.TvInputManager.TvInputCallback;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PointerIconCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.exoplayer2.extractor.ts.TsExtractor;
import com.google.android.tvlauncher.R;
import com.google.android.tvlauncher.util.BuildType;
import com.google.android.tvlauncher.util.Partner;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class InputsManager {
    private static final boolean DEBUG = false;
    private static final String META_LABEL_COLOR_OPTION = "input_banner_label_color_option";
    private static final String META_LABEL_SORT_KEY = "input_sort_key";
    private static final int MSG_INPUT_ADDED = 2;
    private static final int MSG_INPUT_MODIFIED = 4;
    private static final int MSG_INPUT_REMOVED = 3;
    private static final int MSG_INPUT_UPDATED = 1;
    private static final String PERMISSION_ACCESS_ALL_EPG_DATA = "com.android.providers.tv.permission.ACCESS_ALL_EPG_DATA";
    private static final String TAG = "InputsAdapter";
    private static final String[] mPhysicalTunerBlackList = new String[]{"com.google.android.videos", "com.google.android.youtube.tv"};
    private final InputsComparator mComp = new InputsComparator();
    private final Configuration mConfig;
    private final Context mContext;
    private final InputsHandler mHandler = new InputsHandler(this);
    private final HashMap<String, TvInputEntry> mInputs = new HashMap();
    private final InputCallback mInputsCallback;
    private boolean mIsBundledTunerVisible = false;
    private OnChangedListener mListener;
    private final LinkedHashMap<String, TvInputInfo> mPhysicalTunerInputs = new LinkedHashMap();
    private final TvInputManager mTvManager;
    private Map<Integer, Integer> mTypePriorities;
    private final HashMap<String, TvInputInfo> mVirtualTunerInputs = new HashMap();
    private final List<TvInputEntry> mVisibleInputs = new ArrayList();

    public static final class Configuration {
        final boolean mDisableDisconnectedInputs;
        final boolean mGetStateIconFromTVInput;
        final boolean mShowPhysicalTunersSeparately;

        Configuration(boolean showPhysicalTunersSeparately, boolean disableDisconnectedInputs, boolean stateIconFromTVInput) {
            this.mShowPhysicalTunersSeparately = showPhysicalTunersSeparately;
            this.mDisableDisconnectedInputs = disableDisconnectedInputs;
            this.mGetStateIconFromTVInput = stateIconFromTVInput;
        }
    }

    private class InputCallback extends TvInputCallback {
        private InputCallback() {
        }

        public void onInputStateChanged(String inputId, int state) {
            InputsManager.this.mHandler.sendMessage(InputsManager.this.mHandler.obtainMessage(1, state, 0, inputId));
        }

        public void onInputAdded(String inputId) {
            InputsManager.this.mHandler.sendMessage(InputsManager.this.mHandler.obtainMessage(2, inputId));
        }

        public void onInputRemoved(String inputId) {
            InputsManager.this.mHandler.sendMessage(InputsManager.this.mHandler.obtainMessage(3, inputId));
        }

        public void onTvInputInfoUpdated(TvInputInfo inputInfo) {
            InputsManager.this.mHandler.sendMessage(InputsManager.this.mHandler.obtainMessage(4, inputInfo));
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
                if (InputsManager.this.mConfig.mDisableDisconnectedInputs) {
                    boolean disconnectedL;
                    boolean disconnectedR;
                    if (lhs.mState == 2) {
                        disconnectedL = true;
                    } else {
                        disconnectedL = false;
                    }
                    if (rhs.mState == 2) {
                        disconnectedR = true;
                    } else {
                        disconnectedR = false;
                    }
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
                    boolean rIsPhysical = InputsManager.isPhysicalTuner(InputsManager.this.mContext.getPackageManager(), rhs.mInfo);
                    boolean lIsPhysical = InputsManager.isPhysicalTuner(InputsManager.this.mContext.getPackageManager(), lhs.mInfo);
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
                if (TextUtils.equals(lhs.mParentLabel, rhs.mParentLabel)) {
                    return lhs.mLabel.compareToIgnoreCase(rhs.mLabel);
                }
                return lhs.mParentLabel.compareToIgnoreCase(rhs.mParentLabel);
            }
        }
    }

    private static class InputsHandler extends Handler {
        private final WeakReference<InputsManager> mInputsManager;

        InputsHandler(InputsManager inputsManager) {
            this.mInputsManager = new WeakReference(inputsManager);
        }

        public void handleMessage(Message msg) {
            InputsManager inputsManager = (InputsManager) this.mInputsManager.get();
            if (inputsManager != null) {
                switch (msg.what) {
                    case 1:
                        inputsManager.inputStateUpdated((String) msg.obj, msg.arg1);
                        return;
                    case 2:
                        inputsManager.inputAdded((String) msg.obj, false);
                        return;
                    case 3:
                        inputsManager.inputRemoved((String) msg.obj);
                        return;
                    case 4:
                        inputsManager.inputEntryModified((TvInputInfo) msg.obj);
                        return;
                    default:
                        return;
                }
            }
        }
    }

    interface OnChangedListener {
        void onInputsChanged();
    }

    private class TvInputEntry {
        Drawable mBanner;
        int mBannerState;
        // final HdmiDeviceInfo mHdmiInfo;
        final TvInputInfo mInfo;
        String mLabel;
        int mNumChildren;
        final TvInputEntry mParentEntry;
        final String mParentLabel;
        final int mPriority;
        final int mSortKey;
        int mState;
        final int mTextColorOption;
        final int mType;

        TvInputEntry(String label, Drawable banner, int colorOption, int type) {
            this.mInfo = null;
            //  this.mHdmiInfo = null;
            this.mLabel = label;
            this.mParentLabel = null;
            this.mBanner = banner;
            this.mTextColorOption = colorOption;
            this.mType = type;
            this.mPriority = InputsManager.this.getPriorityForType(type);
            this.mSortKey = Integer.MAX_VALUE;
            this.mParentEntry = null;
            this.mBannerState = 0;
            this.mState = 0;
        }

        TvInputEntry(TvInputInfo info, TvInputEntry parent, int state, Context ctx) {
            this.mInfo = info;
            this.mType = this.mInfo.getType();
            // if (this.mType == PointerIconCompat.TYPE_CROSSHAIR) {
            //     this.mHdmiInfo = this.mInfo.getHdmiDeviceInfo();
            // } else {
            //     this.mHdmiInfo = null;
            // }
            CharSequence label;// = info.loadCustomLabel(ctx);
            //if (TextUtils.isEmpty(label)) {
            label = info.loadLabel(ctx);
            //}
            if (label != null) {
                this.mLabel = label.toString();
            } else {
                this.mLabel = "";
            }
            this.mTextColorOption = this.mInfo.getServiceInfo().metaData.getInt(InputsManager.META_LABEL_COLOR_OPTION, 0);
            this.mSortKey = this.mInfo.getServiceInfo().metaData.getInt(InputsManager.META_LABEL_SORT_KEY, Integer.MAX_VALUE);
            this.mState = state;
            /*if (info.getHdmiDeviceInfo() != null && info.getHdmiDeviceInfo().isCecDevice()) {
                switch (info.getHdmiDeviceInfo().getDeviceType()) {
                    case 1:
                        this.mPriority = InputsManager.this.getPriorityForType(-4);
                        break;
                    case 4:
                        this.mPriority = InputsManager.this.getPriorityForType(-5);
                        break;
                    default:
                        this.mPriority = InputsManager.this.getPriorityForType(-2);
                        break;
                }
            } else if (info.getHdmiDeviceInfo() == null || !info.getHdmiDeviceInfo().isMhlDevice()) {
                this.mPriority = InputsManager.this.getPriorityForType(this.mType);
            } else {*/
            this.mPriority = InputsManager.this.getPriorityForType(-6);
            // todo hdmi }
            this.mParentEntry = parent;
            if (this.mParentEntry != null) {
                // label = this.mParentEntry.mInfo.loadCustomLabel(ctx);
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
            if (InputsManager.this.mConfig.mGetStateIconFromTVInput) {
                Drawable icon = this.mInfo.loadIcon(InputsManager.this.mContext); // , this.mState
                if (icon != null) {
                    return icon;
                }
            }
            return this.mInfo.loadIcon(InputsManager.this.mContext);
        }

        boolean isBundledTuner() {
            return this.mType == -3;
        }

        public boolean isEnabled() {
            return isConnected() || !InputsManager.this.mConfig.mDisableDisconnectedInputs;
        }

        boolean isConnected() {
            return isBundledTuner() || this.mState != 2;
        }

        boolean isStandby() {
            return this.mState == 1;
        }

        boolean isDisconnected() {
            return !isBundledTuner() && this.mState == 2;
        }

        Drawable getImageDrawable(int newState) {
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
                    drawableId = R.drawable.ic_icon_32dp_tuner;
                    break;
                case PointerIconCompat.TYPE_CONTEXT_MENU /*1001*/:
                    drawableId = R.drawable.ic_icon_32dp_composite;
                    break;
                case PointerIconCompat.TYPE_HAND /*1002*/:
                    drawableId = R.drawable.ic_icon_32dp_svideo;
                    break;
                case PointerIconCompat.TYPE_HELP /*1003*/:
                    drawableId = R.drawable.ic_icon_32dp_scart;
                    break;
                case PointerIconCompat.TYPE_WAIT /*1004*/:
                    drawableId = R.drawable.ic_icon_32dp_component;
                    break;
                case 1005:
                    drawableId = R.drawable.ic_icon_32dp_vga;
                    break;
                case PointerIconCompat.TYPE_CELL /*1006*/:
                    drawableId = R.drawable.ic_icon_32dp_dvi;
                    break;
                case PointerIconCompat.TYPE_CROSSHAIR /*1007*/:
                    /*if (this.mHdmiInfo != null) {
                        switch (this.mHdmiInfo.getDeviceType()) {
                            case 0:
                                drawableId = R.drawable.ic_icon_32dp_livetv;
                                break;
                            case 1:
                                drawableId = R.drawable.ic_icon_32dp_recording;
                                break;
                            case 3:
                                drawableId = R.drawable.ic_icon_32dp_tuner;
                                break;
                            case 4:
                                drawableId = R.drawable.ic_icon_32dp_playback;
                                break;
                            case 5:
                                drawableId = R.drawable.ic_icon_32dp_audio;
                                break;
                            default:
                                if (!this.mHdmiInfo.isMhlDevice()) {
                                    drawableId = R.drawable.ic_icon_32dp_tuner;
                                    break;
                                }
                                drawableId = R.drawable.ic_icon_32dp_mhl;
                                break;
                        }
                    }*/
                    // todo hdmi apis inaccessible
                    drawableId = R.drawable.ic_icon_32dp_hdmi;
                    break;
                case PointerIconCompat.TYPE_TEXT /*1008*/:
                    drawableId = R.drawable.ic_icon_32dp_display_port;
                    break;
                default:
                    drawableId = R.drawable.ic_icon_32dp_hdmi;
                    break;
            }
            Drawable drawable = ContextCompat.getDrawable(InputsManager.this.mContext, drawableId);
            this.mBanner = drawable;
            return drawable;
        }

        public String getLabel() {
            // if (this.mHdmiInfo != null && !TextUtils.isEmpty(this.mHdmiInfo.getDisplayName())) {
            //     return this.mHdmiInfo.getDisplayName();
            // }
            if (!TextUtils.isEmpty(this.mLabel)) {
                return this.mLabel;
            }
            if (this.mType == -3) {
                return InputsManager.this.mContext.getResources().getString(R.string.input_title_bundled_tuner);
            }
            return null;
        }

        Intent getLaunchIntent() {
            if (this.mInfo != null) {
                if (this.mInfo.isPassthroughInput()) {
                    return new Intent("android.intent.action.VIEW", TvContract.buildChannelUriForPassthroughInput(this.mInfo.getId()));
                }
                return new Intent("android.intent.action.VIEW", TvContract.buildChannelsUriForInput(this.mInfo.getId()));
            } else if (isBundledTuner()) {
                return new Intent("android.intent.action.VIEW", Channels.CONTENT_URI);
            } else {
                return null;
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
            if ((!isBundledTuner() || !obj.isBundledTuner()) && (this.mInfo == null || obj.mInfo == null || !this.mInfo.equals(obj.mInfo))) {
                return false;
            }
            return true;
        }
    }

    public static boolean hasInputs(Context context) {
        LocalInputs localInputs;
        if (BuildType.DEBUG.booleanValue()) {
            localInputs = (LocalInputs) BuildType.newInstance(LocalInputs.class, "com.google.android.tvlauncher.inputs.DebugLocalInputs", new Object[0]);
        } else {
            localInputs = (LocalInputs) BuildType.newInstance(LocalInputs.class, "com.google.android.tvlauncher.inputs.LocalInputs", new Object[0]);
        }
        if (localInputs.getInputsNum(context) > 0) {
            return true;
        }
        return false;
    }

    InputsManager(Context context, Configuration config) {
        this.mConfig = config;
        this.mContext = context;
        this.mTvManager = (TvInputManager) context.getSystemService("tv_input");
        setupDeviceTypePriorities();
        refreshInputs();
        this.mInputsCallback = new InputCallback();
        if (this.mTvManager != null) {
            this.mTvManager.registerCallback(this.mInputsCallback, this.mHandler);
        }
        if (BuildType.DEBUG.booleanValue()) {
            LocalInputs localInputs = (LocalInputs) BuildType.newInstance(LocalInputs.class, "com.google.android.tvlauncher.inputs.DebugLocalInputs", new Object[0]);
            int inputsNum = localInputs.getInputsNum(this.mContext);
            if (inputsNum > 0) {
                String[] names = localInputs.getNames();
                int[] types = localInputs.getTypes();
                int[] states = localInputs.getStates();
                for (int i = 0; i < inputsNum; i++) {
                    TvInputEntry e = new TvInputEntry(names[i], null, 0, types[i]);
                    e.mState = states[i];
                    this.mInputs.put(Integer.toString(i) + "-emulated", e);
                    this.mVisibleInputs.add(e);
                }
            }
        }
    }

    public void unregisterReceivers() {
        if (this.mTvManager != null) {
            this.mTvManager.unregisterCallback(this.mInputsCallback);
        }
    }

    int getInputType(int position) {
        if (position >= this.mVisibleInputs.size() || position < 0) {
            return -1;
        }
        return ((TvInputEntry) this.mVisibleInputs.get(position)).mType;
    }

    void launchInputActivity(int position) {
        if (position < this.mVisibleInputs.size() && position >= 0) {
            TvInputEntry entry = (TvInputEntry) this.mVisibleInputs.get(position);
            if (entry.isDisconnected()) {
                String toastText = Partner.get(this.mContext).getDisconnectedInputToastText();
                if (!TextUtils.isEmpty(toastText)) {
                    Toast.makeText(this.mContext, toastText, 0).show();
                }
            } else if (entry.isEnabled()) {
                try {
                    this.mContext.startActivity(entry.getLaunchIntent());
                } catch (Throwable e) {
                    Log.e(TAG, "Could not perform launch:", e);
                    Toast.makeText(this.mContext, R.string.failed_launch, 0).show();
                }
            }
        }
    }

    public int getItemCount() {
        return this.mVisibleInputs.size();
    }

    Drawable getItemDrawable(int position) {
        if (position >= this.mVisibleInputs.size() || position < 0) {
            return null;
        }
        TvInputEntry entry = (TvInputEntry) this.mVisibleInputs.get(position);
        return entry.getImageDrawable(entry.mState);
    }

    String getLabel(int position) {
        if (position >= this.mVisibleInputs.size() || position < 0) {
            return null;
        }
        return ((TvInputEntry) this.mVisibleInputs.get(position)).getLabel();
    }

    int getInputState(int position) {
        if (position >= this.mVisibleInputs.size() || position < 0) {
            return 2;
        }
        TvInputEntry entry = (TvInputEntry) this.mVisibleInputs.get(position);
        if (entry.isConnected()) {
            return entry.isStandby() ? 1 : 0;
        } else {
            if (entry.isDisconnected()) {
                return 2;
            }
            return entry.mState;
        }
    }

    void setOnChangedListener(OnChangedListener listener) {
        this.mListener = listener;
    }

    private void notifyInputsChanged() {
        if (this.mListener != null) {
            this.mListener.onInputsChanged();
        }
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
                        notifyInputsChanged();
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
                notifyInputsChanged();
            }
            this.mIsBundledTunerVisible = true;
        }
    }

    private void addInputEntry(TvInputInfo input, boolean isRefresh) {
        try {
            int state = this.mTvManager.getInputState(input.getId());
            TvInputEntry parentEntry = null;
            if (((TvInputEntry) this.mInputs.get(input.getId())) == null) {
                if (!(input.getParentId() == null)) {//|| input.isConnectedToHdmiSwitch())) {
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    if (!entry.mInfo.isHidden(this.mContext)) {
                        if (parentEntry != null && parentEntry.mInfo.isHidden(this.mContext)) {
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
                        insertEntryIntoSortedList(entry, this.mVisibleInputs);
                        notifyInputsChanged();
                        if (parentEntry != null && parentEntry.mInfo.getParentId() == null && !this.mVisibleInputs.contains(parentEntry)) {
                            insertEntryIntoSortedList(parentEntry, this.mVisibleInputs);
                            notifyInputsChanged();
                        }
                    }
                }
            }
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Failed to get state for Input, dropping entry. Id = " + input.getId());
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
            entry.mState = state;
            if (getIndexInVisibleList(id) >= 0) {
                notifyInputsChanged();
            }
        }
    }

    private void inputEntryModified(TvInputInfo inputInfo) {
    /*    CharSequence customLabel = inputInfo.loadCustomLabel(this.mContext);
        if (customLabel != null) {
            String id = inputInfo.getId();
            TvInputEntry entry = (TvInputEntry) this.mInputs.get(id);
            if (entry != null) {
                entry.mLabel = customLabel.toString();
                if (getIndexInVisibleList(id) >= 0) {
                    notifyInputsChanged();
                }
            }
        }*/
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
            } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N || !info.isHidden(this.mContext)) {
                showBundledTunerInput(isRefresh);
            }
        } else {
            this.mVirtualTunerInputs.put(info.getId(), info);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N || !info.isHidden(this.mContext)) {
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
                    notifyInputsChanged();
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
                            this.mVisibleInputs.remove(parentIndex);
                        }
                        insertEntryIntoSortedList(parent, this.mVisibleInputs);
                    }
                }
                notifyInputsChanged();
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
            boolean mayBeTunerInput;
            if (pkgMan.checkPermission(PERMISSION_ACCESS_ALL_EPG_DATA, input.getServiceInfo().packageName) == 0) {
                mayBeTunerInput = true;
            } else {
                mayBeTunerInput = false;
            }
            if (!mayBeTunerInput) {
                try {
                    if ((pkgMan.getApplicationInfo(input.getServiceInfo().packageName, 0).flags & TsExtractor.TS_STREAM_TYPE_AC3) == 0) {
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
        addToPriorityIfMissing(1000, addToPriorityIfMissing(PointerIconCompat.TYPE_HELP, addToPriorityIfMissing(1005, addToPriorityIfMissing(PointerIconCompat.TYPE_TEXT, addToPriorityIfMissing(PointerIconCompat.TYPE_CONTEXT_MENU, addToPriorityIfMissing(PointerIconCompat.TYPE_HAND, addToPriorityIfMissing(PointerIconCompat.TYPE_WAIT, addToPriorityIfMissing(PointerIconCompat.TYPE_CELL, addToPriorityIfMissing(PointerIconCompat.TYPE_CROSSHAIR, addToPriorityIfMissing(-6, addToPriorityIfMissing(-5, addToPriorityIfMissing(-4, addToPriorityIfMissing(-2, addToPriorityIfMissing(0, addToPriorityIfMissing(-3, this.mTypePriorities.size())))))))))))))));
    }
}
