package com.google.android.tvlauncher.inputs;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.hardware.hdmi.HdmiDeviceInfo;
import android.media.tv.TvContract;
import android.media.tv.TvContract.Channels;
import android.media.tv.TvInputInfo;
import android.media.tv.TvInputManager;
import android.media.tv.TvInputManager.TvInputCallback;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
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

public class InputsManager
{
  private static final boolean DEBUG = false;
  private static final String META_LABEL_COLOR_OPTION = "input_banner_label_color_option";
  private static final String META_LABEL_SORT_KEY = "input_sort_key";
  private static final int MSG_INPUT_ADDED = 2;
  private static final int MSG_INPUT_MODIFIED = 4;
  private static final int MSG_INPUT_REMOVED = 3;
  private static final int MSG_INPUT_UPDATED = 1;
  private static final String PERMISSION_ACCESS_ALL_EPG_DATA = "com.android.providers.tv.permission.ACCESS_ALL_EPG_DATA";
  private static final String TAG = "InputsAdapter";
  private static final String[] mPhysicalTunerBlackList = { "com.google.android.videos", "com.google.android.youtube.tv" };
  private final InputsComparator mComp = new InputsComparator(null);
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
  
  InputsManager(Context paramContext, Configuration paramConfiguration)
  {
    this.mConfig = paramConfiguration;
    this.mContext = paramContext;
    this.mTvManager = ((TvInputManager)paramContext.getSystemService("tv_input"));
    setupDeviceTypePriorities();
    refreshInputs();
    this.mInputsCallback = new InputCallback(null);
    if (this.mTvManager != null) {
      this.mTvManager.registerCallback(this.mInputsCallback, this.mHandler);
    }
    if (BuildType.DEBUG.booleanValue())
    {
      Object localObject = (LocalInputs)BuildType.newInstance(LocalInputs.class, "com.google.android.tvlauncher.inputs.DebugLocalInputs", new Object[0]);
      int j = ((LocalInputs)localObject).getInputsNum(this.mContext);
      if (j > 0)
      {
        paramContext = ((LocalInputs)localObject).getNames();
        paramConfiguration = ((LocalInputs)localObject).getTypes();
        localObject = ((LocalInputs)localObject).getStates();
        int i = 0;
        while (i < j)
        {
          TvInputEntry localTvInputEntry = new TvInputEntry(paramContext[i], null, 0, paramConfiguration[i]);
          localTvInputEntry.mState = localObject[i];
          this.mInputs.put(Integer.toString(i) + "-emulated", localTvInputEntry);
          this.mVisibleInputs.add(localTvInputEntry);
          i += 1;
        }
      }
    }
  }
  
  private void addInputEntry(TvInputInfo paramTvInputInfo, boolean paramBoolean)
  {
    do
    {
      TvInputEntry localTvInputEntry;
      do
      {
        try
        {
          int i = this.mTvManager.getInputState(paramTvInputInfo.getId());
          Object localObject = (TvInputEntry)this.mInputs.get(paramTvInputInfo.getId());
          localTvInputEntry = null;
          if (localObject == null)
          {
            localObject = localTvInputEntry;
            if (paramTvInputInfo.getParentId() != null)
            {
              localObject = localTvInputEntry;
              if (!paramTvInputInfo.isConnectedToHdmiSwitch())
              {
                TvInputInfo localTvInputInfo = this.mTvManager.getTvInputInfo(paramTvInputInfo.getParentId());
                localObject = localTvInputEntry;
                if (localTvInputInfo != null)
                {
                  localTvInputEntry = (TvInputEntry)this.mInputs.get(localTvInputInfo.getId());
                  localObject = localTvInputEntry;
                  if (localTvInputEntry == null)
                  {
                    localObject = new TvInputEntry(localTvInputInfo, null, this.mTvManager.getInputState(localTvInputInfo.getId()), this.mContext);
                    this.mInputs.put(localTvInputInfo.getId(), localObject);
                  }
                  ((TvInputEntry)localObject).mNumChildren += 1;
                }
              }
            }
            localTvInputEntry = new TvInputEntry(paramTvInputInfo, (TvInputEntry)localObject, i, this.mContext);
            this.mInputs.put(paramTvInputInfo.getId(), localTvInputEntry);
            if ((!localTvInputEntry.mInfo.isHidden(this.mContext)) && ((localObject == null) || (!((TvInputEntry)localObject).mInfo.isHidden(this.mContext)))) {}
          }
          else
          {
            return;
          }
        }
        catch (IllegalArgumentException localIllegalArgumentException)
        {
          Log.e("InputsAdapter", "Failed to get state for Input, dropping entry. Id = " + paramTvInputInfo.getId());
          return;
        }
        if (!paramBoolean) {
          break;
        }
        this.mVisibleInputs.add(localTvInputEntry);
      } while ((localIllegalArgumentException == null) || (localIllegalArgumentException.mInfo.getParentId() != null) || (this.mVisibleInputs.contains(localIllegalArgumentException)));
      this.mVisibleInputs.add(localIllegalArgumentException);
      return;
      insertEntryIntoSortedList(localTvInputEntry, this.mVisibleInputs);
      notifyInputsChanged();
    } while ((localIllegalArgumentException == null) || (localIllegalArgumentException.mInfo.getParentId() != null) || (this.mVisibleInputs.contains(localIllegalArgumentException)));
    insertEntryIntoSortedList(localIllegalArgumentException, this.mVisibleInputs);
    notifyInputsChanged();
  }
  
  private int addToPriorityIfMissing(int paramInt1, int paramInt2)
  {
    int i = paramInt2;
    if (!this.mTypePriorities.containsKey(Integer.valueOf(paramInt1)))
    {
      this.mTypePriorities.put(Integer.valueOf(paramInt1), Integer.valueOf(paramInt2));
      i = paramInt2 + 1;
    }
    return i;
  }
  
  private int getIndexInVisibleList(String paramString)
  {
    int i = 0;
    while (i < this.mVisibleInputs.size())
    {
      TvInputInfo localTvInputInfo = ((TvInputEntry)this.mVisibleInputs.get(i)).mInfo;
      if ((localTvInputInfo != null) && (TextUtils.equals(localTvInputInfo.getId(), paramString))) {
        return i;
      }
      i += 1;
    }
    return -1;
  }
  
  private int getPriorityForType(int paramInt)
  {
    if (this.mTypePriorities != null)
    {
      Integer localInteger = (Integer)this.mTypePriorities.get(Integer.valueOf(paramInt));
      if (localInteger != null) {
        return localInteger.intValue();
      }
    }
    return Integer.MAX_VALUE;
  }
  
  public static boolean hasInputs(Context paramContext)
  {
    boolean bool = false;
    if (BuildType.DEBUG.booleanValue()) {}
    for (LocalInputs localLocalInputs = (LocalInputs)BuildType.newInstance(LocalInputs.class, "com.google.android.tvlauncher.inputs.DebugLocalInputs", new Object[0]);; localLocalInputs = (LocalInputs)BuildType.newInstance(LocalInputs.class, "com.google.android.tvlauncher.inputs.LocalInputs", new Object[0]))
    {
      if (localLocalInputs.getInputsNum(paramContext) > 0) {
        bool = true;
      }
      return bool;
    }
  }
  
  private void hideBundledTunerInput(boolean paramBoolean)
  {
    if (this.mIsBundledTunerVisible)
    {
      int i = this.mVisibleInputs.size() - 1;
      while (i >= 0)
      {
        if (((TvInputEntry)this.mVisibleInputs.get(i)).isBundledTuner())
        {
          this.mVisibleInputs.remove(i);
          if (!paramBoolean) {
            notifyInputsChanged();
          }
          this.mIsBundledTunerVisible = false;
        }
        i -= 1;
      }
    }
  }
  
  private void inputAdded(TvInputInfo paramTvInputInfo, boolean paramBoolean)
  {
    if (paramTvInputInfo != null)
    {
      if (!paramTvInputInfo.isPassthroughInput()) {
        break label18;
      }
      addInputEntry(paramTvInputInfo, paramBoolean);
    }
    label18:
    do
    {
      do
      {
        return;
        if (!isPhysicalTuner(this.mContext.getPackageManager(), paramTvInputInfo)) {
          break;
        }
        this.mPhysicalTunerInputs.put(paramTvInputInfo.getId(), paramTvInputInfo);
        if (this.mConfig.mShowPhysicalTunersSeparately)
        {
          addInputEntry(paramTvInputInfo, paramBoolean);
          return;
        }
      } while (paramTvInputInfo.isHidden(this.mContext));
      showBundledTunerInput(paramBoolean);
      return;
      this.mVirtualTunerInputs.put(paramTvInputInfo.getId(), paramTvInputInfo);
    } while (paramTvInputInfo.isHidden(this.mContext));
    showBundledTunerInput(paramBoolean);
  }
  
  private void inputAdded(String paramString, boolean paramBoolean)
  {
    if (this.mTvManager != null) {
      inputAdded(this.mTvManager.getTvInputInfo(paramString), paramBoolean);
    }
  }
  
  private void inputEntryModified(TvInputInfo paramTvInputInfo)
  {
    CharSequence localCharSequence = paramTvInputInfo.loadCustomLabel(this.mContext);
    if (localCharSequence != null)
    {
      paramTvInputInfo = paramTvInputInfo.getId();
      TvInputEntry localTvInputEntry = (TvInputEntry)this.mInputs.get(paramTvInputInfo);
      if (localTvInputEntry != null)
      {
        localTvInputEntry.mLabel = localCharSequence.toString();
        if (getIndexInVisibleList(paramTvInputInfo) >= 0) {
          notifyInputsChanged();
        }
      }
    }
  }
  
  private void inputRemoved(String paramString)
  {
    TvInputEntry localTvInputEntry = (TvInputEntry)this.mInputs.get(paramString);
    if ((localTvInputEntry != null) && (localTvInputEntry.mInfo != null) && (localTvInputEntry.mInfo.isPassthroughInput()))
    {
      removeEntry(paramString);
      return;
    }
    removeTuner(paramString);
  }
  
  private void inputStateUpdated(String paramString, int paramInt)
  {
    TvInputEntry localTvInputEntry = (TvInputEntry)this.mInputs.get(paramString);
    if (localTvInputEntry != null)
    {
      localTvInputEntry.mState = paramInt;
      if (getIndexInVisibleList(paramString) >= 0) {
        notifyInputsChanged();
      }
    }
  }
  
  private int insertEntryIntoSortedList(TvInputEntry paramTvInputEntry, List<TvInputEntry> paramList)
  {
    int i = 0;
    while ((i < paramList.size()) && (this.mComp.compare(paramTvInputEntry, (TvInputEntry)paramList.get(i)) > 0)) {
      i += 1;
    }
    if (!paramList.contains(paramTvInputEntry)) {
      paramList.add(i, paramTvInputEntry);
    }
    return i;
  }
  
  private static boolean isPhysicalTuner(PackageManager paramPackageManager, TvInputInfo paramTvInputInfo)
  {
    boolean bool2 = true;
    String str = paramTvInputInfo.getServiceInfo().packageName;
    if (Arrays.asList(mPhysicalTunerBlackList).contains(str)) {
      return false;
    }
    boolean bool1;
    if (paramTvInputInfo.createSetupIntent() == null) {
      bool1 = false;
    }
    for (;;)
    {
      return bool1;
      if (paramPackageManager.checkPermission("com.android.providers.tv.permission.ACCESS_ALL_EPG_DATA", paramTvInputInfo.getServiceInfo().packageName) == 0) {}
      for (int i = 1;; i = 0)
      {
        bool1 = bool2;
        if (i != 0) {
          break;
        }
        try
        {
          i = paramPackageManager.getApplicationInfo(paramTvInputInfo.getServiceInfo().packageName, 0).flags;
          bool1 = bool2;
          if ((i & 0x81) != 0) {
            break;
          }
          bool1 = false;
        }
        catch (PackageManager.NameNotFoundException paramPackageManager)
        {
          bool1 = false;
        }
      }
    }
  }
  
  private void notifyInputsChanged()
  {
    if (this.mListener != null) {
      this.mListener.onInputsChanged();
    }
  }
  
  private void refreshInputs()
  {
    this.mInputs.clear();
    this.mVisibleInputs.clear();
    this.mPhysicalTunerInputs.clear();
    this.mVirtualTunerInputs.clear();
    this.mIsBundledTunerVisible = false;
    if (this.mTvManager != null)
    {
      List localList = this.mTvManager.getTvInputList();
      if (localList != null)
      {
        int i = 0;
        while (i < localList.size())
        {
          inputAdded((TvInputInfo)localList.get(i), true);
          i += 1;
        }
        Collections.sort(this.mVisibleInputs, this.mComp);
      }
    }
  }
  
  private void removeEntry(String paramString)
  {
    TvInputEntry localTvInputEntry1 = (TvInputEntry)this.mInputs.get(paramString);
    if (localTvInputEntry1 == null) {}
    int i;
    do
    {
      return;
      this.mInputs.remove(paramString);
      i = this.mVisibleInputs.size() - 1;
      while (i >= 0)
      {
        TvInputEntry localTvInputEntry2 = (TvInputEntry)this.mVisibleInputs.get(i);
        if ((localTvInputEntry2.mParentEntry != null) && (localTvInputEntry2.mParentEntry.mInfo != null) && (TextUtils.equals(localTvInputEntry2.mParentEntry.mInfo.getId(), paramString)))
        {
          this.mInputs.remove(localTvInputEntry2.mInfo.getId());
          this.mVisibleInputs.remove(i);
          notifyInputsChanged();
        }
        i -= 1;
      }
      i = getIndexInVisibleList(paramString);
    } while (i == -1);
    this.mVisibleInputs.remove(i);
    paramString = localTvInputEntry1.mParentEntry;
    if (paramString != null)
    {
      paramString.mNumChildren = Math.max(0, paramString.mNumChildren - 1);
      if (paramString.mNumChildren == 0)
      {
        i = this.mVisibleInputs.indexOf(paramString);
        if (i != -1) {
          this.mVisibleInputs.remove(i);
        }
        insertEntryIntoSortedList(paramString, this.mVisibleInputs);
      }
    }
    notifyInputsChanged();
  }
  
  private void removeTuner(String paramString)
  {
    removeEntry(paramString);
    this.mVirtualTunerInputs.remove(paramString);
    this.mPhysicalTunerInputs.remove(paramString);
    if ((this.mVirtualTunerInputs.isEmpty()) && ((this.mPhysicalTunerInputs.isEmpty()) || (this.mConfig.mShowPhysicalTunersSeparately))) {
      hideBundledTunerInput(false);
    }
  }
  
  private void setupDeviceTypePriorities()
  {
    this.mTypePriorities = Partner.get(this.mContext).getInputsOrderMap();
    addToPriorityIfMissing(1000, addToPriorityIfMissing(1003, addToPriorityIfMissing(1005, addToPriorityIfMissing(1008, addToPriorityIfMissing(1001, addToPriorityIfMissing(1002, addToPriorityIfMissing(1004, addToPriorityIfMissing(1006, addToPriorityIfMissing(1007, addToPriorityIfMissing(-6, addToPriorityIfMissing(-5, addToPriorityIfMissing(-4, addToPriorityIfMissing(-2, addToPriorityIfMissing(0, addToPriorityIfMissing(-3, this.mTypePriorities.size())))))))))))))));
  }
  
  private void showBundledTunerInput(boolean paramBoolean)
  {
    if (!this.mIsBundledTunerVisible)
    {
      TvInputEntry localTvInputEntry = new TvInputEntry(Partner.get(this.mContext).getBundledTunerTitle(), Partner.get(this.mContext).getBundledTunerBanner(), Partner.get(this.mContext).getBundledTunerLabelColorOption(0), -3);
      if (!paramBoolean) {
        break label70;
      }
      this.mVisibleInputs.add(localTvInputEntry);
    }
    for (;;)
    {
      this.mIsBundledTunerVisible = true;
      return;
      label70:
      notifyInputsChanged();
    }
  }
  
  int getInputState(int paramInt)
  {
    if ((paramInt >= this.mVisibleInputs.size()) || (paramInt < 0)) {}
    TvInputEntry localTvInputEntry;
    do
    {
      return 2;
      localTvInputEntry = (TvInputEntry)this.mVisibleInputs.get(paramInt);
      if (localTvInputEntry.isConnected())
      {
        if (localTvInputEntry.isStandby()) {
          return 1;
        }
        return 0;
      }
    } while (localTvInputEntry.isDisconnected());
    return localTvInputEntry.mState;
  }
  
  int getInputType(int paramInt)
  {
    if ((paramInt >= this.mVisibleInputs.size()) || (paramInt < 0)) {
      return -1;
    }
    return ((TvInputEntry)this.mVisibleInputs.get(paramInt)).mType;
  }
  
  public int getItemCount()
  {
    return this.mVisibleInputs.size();
  }
  
  Drawable getItemDrawable(int paramInt)
  {
    if ((paramInt >= this.mVisibleInputs.size()) || (paramInt < 0)) {
      return null;
    }
    TvInputEntry localTvInputEntry = (TvInputEntry)this.mVisibleInputs.get(paramInt);
    return localTvInputEntry.getImageDrawable(localTvInputEntry.mState);
  }
  
  String getLabel(int paramInt)
  {
    if ((paramInt >= this.mVisibleInputs.size()) || (paramInt < 0)) {
      return null;
    }
    return ((TvInputEntry)this.mVisibleInputs.get(paramInt)).getLabel();
  }
  
  void launchInputActivity(int paramInt)
  {
    if ((paramInt >= this.mVisibleInputs.size()) || (paramInt < 0)) {}
    Object localObject;
    do
    {
      do
      {
        return;
        localObject = (TvInputEntry)this.mVisibleInputs.get(paramInt);
        if (!((TvInputEntry)localObject).isDisconnected()) {
          break;
        }
        localObject = Partner.get(this.mContext).getDisconnectedInputToastText();
      } while (TextUtils.isEmpty((CharSequence)localObject));
      Toast.makeText(this.mContext, (CharSequence)localObject, 0).show();
      return;
    } while (!((TvInputEntry)localObject).isEnabled());
    try
    {
      this.mContext.startActivity(((TvInputEntry)localObject).getLaunchIntent());
      return;
    }
    catch (Throwable localThrowable)
    {
      Log.e("InputsAdapter", "Could not perform launch:", localThrowable);
      Toast.makeText(this.mContext, 2131492990, 0).show();
    }
  }
  
  void setOnChangedListener(OnChangedListener paramOnChangedListener)
  {
    this.mListener = paramOnChangedListener;
  }
  
  public void unregisterReceivers()
  {
    if (this.mTvManager != null) {
      this.mTvManager.unregisterCallback(this.mInputsCallback);
    }
  }
  
  public static final class Configuration
  {
    final boolean mDisableDisconnectedInputs;
    final boolean mGetStateIconFromTVInput;
    final boolean mShowPhysicalTunersSeparately;
    
    Configuration(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
    {
      this.mShowPhysicalTunersSeparately = paramBoolean1;
      this.mDisableDisconnectedInputs = paramBoolean2;
      this.mGetStateIconFromTVInput = paramBoolean3;
    }
  }
  
  private class InputCallback
    extends TvInputManager.TvInputCallback
  {
    private InputCallback() {}
    
    public void onInputAdded(String paramString)
    {
      InputsManager.this.mHandler.sendMessage(InputsManager.this.mHandler.obtainMessage(2, paramString));
    }
    
    public void onInputRemoved(String paramString)
    {
      InputsManager.this.mHandler.sendMessage(InputsManager.this.mHandler.obtainMessage(3, paramString));
    }
    
    public void onInputStateChanged(String paramString, int paramInt)
    {
      InputsManager.this.mHandler.sendMessage(InputsManager.this.mHandler.obtainMessage(1, paramInt, 0, paramString));
    }
    
    public void onTvInputInfoUpdated(TvInputInfo paramTvInputInfo)
    {
      InputsManager.this.mHandler.sendMessage(InputsManager.this.mHandler.obtainMessage(4, paramTvInputInfo));
    }
  }
  
  private class InputsComparator
    implements Comparator<InputsManager.TvInputEntry>
  {
    private InputsComparator() {}
    
    public int compare(InputsManager.TvInputEntry paramTvInputEntry1, InputsManager.TvInputEntry paramTvInputEntry2)
    {
      int i = 0;
      int k = -1;
      int m = 1;
      int j;
      if (paramTvInputEntry2 == null) {
        if (paramTvInputEntry1 == null) {
          j = i;
        }
      }
      do
      {
        return j;
        i = -1;
        break;
        j = m;
      } while (paramTvInputEntry1 == null);
      if (InputsManager.this.mConfig.mDisableDisconnectedInputs)
      {
        if (paramTvInputEntry1.mState == 2)
        {
          i = 1;
          label58:
          if (paramTvInputEntry2.mState != 2) {
            break label90;
          }
        }
        label90:
        for (j = 1;; j = 0)
        {
          if (i == j) {
            break label96;
          }
          j = m;
          if (i != 0) {
            break;
          }
          return -1;
          i = 0;
          break label58;
        }
      }
      label96:
      if (paramTvInputEntry1.mPriority != paramTvInputEntry2.mPriority) {
        return paramTvInputEntry1.mPriority - paramTvInputEntry2.mPriority;
      }
      if ((paramTvInputEntry1.mType == 0) && (paramTvInputEntry2.mType == 0))
      {
        boolean bool1 = InputsManager.isPhysicalTuner(InputsManager.this.mContext.getPackageManager(), paramTvInputEntry2.mInfo);
        boolean bool2 = InputsManager.isPhysicalTuner(InputsManager.this.mContext.getPackageManager(), paramTvInputEntry1.mInfo);
        if (bool1 != bool2)
        {
          if (bool2) {}
          for (i = k;; i = 1) {
            return i;
          }
        }
      }
      if (paramTvInputEntry1.mSortKey != paramTvInputEntry2.mSortKey) {
        return paramTvInputEntry2.mSortKey - paramTvInputEntry1.mSortKey;
      }
      if (!TextUtils.equals(paramTvInputEntry1.mParentLabel, paramTvInputEntry2.mParentLabel)) {
        return paramTvInputEntry1.mParentLabel.compareToIgnoreCase(paramTvInputEntry2.mParentLabel);
      }
      return paramTvInputEntry1.mLabel.compareToIgnoreCase(paramTvInputEntry2.mLabel);
    }
  }
  
  private static class InputsHandler
    extends Handler
  {
    private final WeakReference<InputsManager> mInputsManager;
    
    InputsHandler(InputsManager paramInputsManager)
    {
      this.mInputsManager = new WeakReference(paramInputsManager);
    }
    
    public void handleMessage(Message paramMessage)
    {
      InputsManager localInputsManager = (InputsManager)this.mInputsManager.get();
      if (localInputsManager != null) {}
      switch (paramMessage.what)
      {
      default: 
        return;
      case 1: 
        localInputsManager.inputStateUpdated((String)paramMessage.obj, paramMessage.arg1);
        return;
      case 2: 
        localInputsManager.inputAdded((String)paramMessage.obj, false);
        return;
      case 3: 
        localInputsManager.inputRemoved((String)paramMessage.obj);
        return;
      }
      localInputsManager.inputEntryModified((TvInputInfo)paramMessage.obj);
    }
  }
  
  static abstract interface OnChangedListener
  {
    public abstract void onInputsChanged();
  }
  
  private class TvInputEntry
  {
    Drawable mBanner;
    int mBannerState;
    final HdmiDeviceInfo mHdmiInfo;
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
    
    TvInputEntry(TvInputInfo paramTvInputInfo, TvInputEntry paramTvInputEntry, int paramInt, Context paramContext)
    {
      this.mInfo = paramTvInputInfo;
      this.mType = this.mInfo.getType();
      if (this.mType == 1007)
      {
        this.mHdmiInfo = this.mInfo.getHdmiDeviceInfo();
        CharSequence localCharSequence2 = paramTvInputInfo.loadCustomLabel(paramContext);
        CharSequence localCharSequence1 = localCharSequence2;
        if (TextUtils.isEmpty(localCharSequence2)) {
          localCharSequence1 = paramTvInputInfo.loadLabel(paramContext);
        }
        if (localCharSequence1 == null) {
          break label284;
        }
        this.mLabel = localCharSequence1.toString();
        label90:
        this.mTextColorOption = this.mInfo.getServiceInfo().metaData.getInt("input_banner_label_color_option", 0);
        this.mSortKey = this.mInfo.getServiceInfo().metaData.getInt("input_sort_key", Integer.MAX_VALUE);
        this.mState = paramInt;
        if ((paramTvInputInfo.getHdmiDeviceInfo() == null) || (!paramTvInputInfo.getHdmiDeviceInfo().isCecDevice())) {
          break label319;
        }
        switch (paramTvInputInfo.getHdmiDeviceInfo().getDeviceType())
        {
        case 2: 
        case 3: 
        default: 
          this.mPriority = InputsManager.this.getPriorityForType(-2);
          label202:
          this.mParentEntry = paramTvInputEntry;
          if (this.mParentEntry != null)
          {
            paramTvInputInfo = this.mParentEntry.mInfo.loadCustomLabel(paramContext);
            this$1 = paramTvInputInfo;
            if (TextUtils.isEmpty(paramTvInputInfo)) {
              this$1 = this.mParentEntry.mInfo.loadLabel(paramContext);
            }
            if (InputsManager.this != null) {
              this.mParentLabel = InputsManager.this.toString();
            }
          }
          break;
        }
      }
      for (;;)
      {
        this.mBanner = getImageDrawable(this.mState);
        return;
        this.mHdmiInfo = null;
        break;
        label284:
        this.mLabel = "";
        break label90;
        this.mPriority = InputsManager.this.getPriorityForType(-4);
        break label202;
        this.mPriority = InputsManager.this.getPriorityForType(-5);
        break label202;
        label319:
        if ((paramTvInputInfo.getHdmiDeviceInfo() != null) && (paramTvInputInfo.getHdmiDeviceInfo().isMhlDevice()))
        {
          this.mPriority = InputsManager.this.getPriorityForType(-6);
          break label202;
        }
        this.mPriority = InputsManager.this.getPriorityForType(this.mType);
        break label202;
        this.mParentLabel = "";
        continue;
        this.mParentLabel = this.mLabel;
      }
    }
    
    TvInputEntry(String paramString, Drawable paramDrawable, int paramInt1, int paramInt2)
    {
      this.mInfo = null;
      this.mHdmiInfo = null;
      this.mLabel = paramString;
      this.mParentLabel = null;
      this.mBanner = paramDrawable;
      this.mTextColorOption = paramInt1;
      this.mType = paramInt2;
      this.mPriority = InputsManager.this.getPriorityForType(paramInt2);
      this.mSortKey = Integer.MAX_VALUE;
      this.mParentEntry = null;
      this.mBannerState = 0;
      this.mState = 0;
    }
    
    private Drawable loadIcon()
    {
      if (InputsManager.this.mConfig.mGetStateIconFromTVInput)
      {
        Drawable localDrawable = this.mInfo.loadIcon(InputsManager.this.mContext, this.mState);
        if (localDrawable != null) {
          return localDrawable;
        }
      }
      return this.mInfo.loadIcon(InputsManager.this.mContext);
    }
    
    public boolean equals(Object paramObject)
    {
      boolean bool2 = false;
      boolean bool1;
      if (paramObject == this) {
        bool1 = true;
      }
      do
      {
        do
        {
          do
          {
            do
            {
              return bool1;
              bool1 = bool2;
            } while (!(paramObject instanceof TvInputEntry));
            paramObject = (TvInputEntry)paramObject;
            if ((isBundledTuner()) && (((TvInputEntry)paramObject).isBundledTuner())) {
              break;
            }
            bool1 = bool2;
          } while (this.mInfo == null);
          bool1 = bool2;
        } while (((TvInputEntry)paramObject).mInfo == null);
        bool1 = bool2;
      } while (!this.mInfo.equals(((TvInputEntry)paramObject).mInfo));
      return true;
    }
    
    Drawable getImageDrawable(int paramInt)
    {
      if ((this.mBanner != null) && (this.mState == this.mBannerState)) {
        return this.mBanner;
      }
      this.mBannerState = paramInt;
      if (this.mInfo != null)
      {
        this.mBanner = loadIcon();
        if (this.mBanner != null) {
          return this.mBanner;
        }
      }
      switch (this.mType)
      {
      default: 
        paramInt = 2130837655;
      }
      for (;;)
      {
        Drawable localDrawable = ContextCompat.getDrawable(InputsManager.this.mContext, paramInt);
        this.mBanner = localDrawable;
        return localDrawable;
        if (this.mHdmiInfo == null) {
          paramInt = 2130837655;
        } else {
          switch (this.mHdmiInfo.getDeviceType())
          {
          case 2: 
          default: 
            if (this.mHdmiInfo.isMhlDevice()) {
              paramInt = 2130837657;
            }
            break;
          case 0: 
            paramInt = 2130837656;
            break;
          case 5: 
            paramInt = 2130837650;
            break;
          case 1: 
            paramInt = 2130837659;
            break;
          case 3: 
            paramInt = 2130837662;
            break;
          case 4: 
            paramInt = 2130837658;
            continue;
            paramInt = 2130837662;
            continue;
            paramInt = 2130837662;
            continue;
            paramInt = 2130837654;
            continue;
            paramInt = 2130837651;
            continue;
            paramInt = 2130837661;
            continue;
            paramInt = 2130837652;
            continue;
            paramInt = 2130837653;
            continue;
            paramInt = 2130837663;
            continue;
            paramInt = 2130837660;
          }
        }
      }
    }
    
    public String getLabel()
    {
      if ((this.mHdmiInfo != null) && (!TextUtils.isEmpty(this.mHdmiInfo.getDisplayName()))) {
        return this.mHdmiInfo.getDisplayName();
      }
      if (!TextUtils.isEmpty(this.mLabel)) {
        return this.mLabel;
      }
      if (this.mType == -3) {
        return InputsManager.this.mContext.getResources().getString(R.string.input_title_bundled_tuner);
      }
      return null;
    }
    
    Intent getLaunchIntent()
    {
      if (this.mInfo != null)
      {
        if (this.mInfo.isPassthroughInput()) {
          return new Intent("android.intent.action.VIEW", TvContract.buildChannelUriForPassthroughInput(this.mInfo.getId()));
        }
        return new Intent("android.intent.action.VIEW", TvContract.buildChannelsUriForInput(this.mInfo.getId()));
      }
      if (isBundledTuner()) {
        return new Intent("android.intent.action.VIEW", TvContract.Channels.CONTENT_URI);
      }
      return null;
    }
    
    boolean isBundledTuner()
    {
      return this.mType == -3;
    }
    
    boolean isConnected()
    {
      return (isBundledTuner()) || (this.mState != 2);
    }
    
    boolean isDisconnected()
    {
      return (!isBundledTuner()) && (this.mState == 2);
    }
    
    public boolean isEnabled()
    {
      return (isConnected()) || (!InputsManager.this.mConfig.mDisableDisconnectedInputs);
    }
    
    boolean isStandby()
    {
      return this.mState == 1;
    }
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/inputs/InputsManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */