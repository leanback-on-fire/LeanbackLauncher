package android.support.v4.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.util.Log;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class RemoteInput
  extends RemoteInputCompatBase.RemoteInput
{
  private static final String EXTRA_DATA_TYPE_RESULTS_DATA = "android.remoteinput.dataTypeResultsData";
  public static final String EXTRA_RESULTS_DATA = "android.remoteinput.resultsData";
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public static final RemoteInputCompatBase.RemoteInput.Factory FACTORY;
  private static final Impl IMPL;
  public static final String RESULTS_CLIP_LABEL = "android.remoteinput.results";
  private static final String TAG = "RemoteInput";
  private final boolean mAllowFreeFormTextInput;
  private final Set<String> mAllowedDataTypes;
  private final CharSequence[] mChoices;
  private final Bundle mExtras;
  private final CharSequence mLabel;
  private final String mResultKey;
  
  static
  {
    if (Build.VERSION.SDK_INT >= 20) {
      IMPL = new ImplApi20();
    }
    for (;;)
    {
      FACTORY = new RemoteInputCompatBase.RemoteInput.Factory()
      {
        public RemoteInput build(String paramAnonymousString, CharSequence paramAnonymousCharSequence, CharSequence[] paramAnonymousArrayOfCharSequence, boolean paramAnonymousBoolean, Bundle paramAnonymousBundle, Set<String> paramAnonymousSet)
        {
          return new RemoteInput(paramAnonymousString, paramAnonymousCharSequence, paramAnonymousArrayOfCharSequence, paramAnonymousBoolean, paramAnonymousBundle, paramAnonymousSet);
        }
        
        public RemoteInput[] newArray(int paramAnonymousInt)
        {
          return new RemoteInput[paramAnonymousInt];
        }
      };
      return;
      if (Build.VERSION.SDK_INT >= 16) {
        IMPL = new ImplJellybean();
      } else {
        IMPL = new ImplBase();
      }
    }
  }
  
  RemoteInput(String paramString, CharSequence paramCharSequence, CharSequence[] paramArrayOfCharSequence, boolean paramBoolean, Bundle paramBundle, Set<String> paramSet)
  {
    this.mResultKey = paramString;
    this.mLabel = paramCharSequence;
    this.mChoices = paramArrayOfCharSequence;
    this.mAllowFreeFormTextInput = paramBoolean;
    this.mExtras = paramBundle;
    this.mAllowedDataTypes = paramSet;
  }
  
  public static void addDataResultToIntent(RemoteInput paramRemoteInput, Intent paramIntent, Map<String, Uri> paramMap)
  {
    IMPL.addDataResultToIntent(paramRemoteInput, paramIntent, paramMap);
  }
  
  public static void addResultsToIntent(RemoteInput[] paramArrayOfRemoteInput, Intent paramIntent, Bundle paramBundle)
  {
    IMPL.addResultsToIntent(paramArrayOfRemoteInput, paramIntent, paramBundle);
  }
  
  public static Map<String, Uri> getDataResultsFromIntent(Intent paramIntent, String paramString)
  {
    return IMPL.getDataResultsFromIntent(paramIntent, paramString);
  }
  
  public static Bundle getResultsFromIntent(Intent paramIntent)
  {
    return IMPL.getResultsFromIntent(paramIntent);
  }
  
  public boolean getAllowFreeFormInput()
  {
    return this.mAllowFreeFormTextInput;
  }
  
  public Set<String> getAllowedDataTypes()
  {
    return this.mAllowedDataTypes;
  }
  
  public CharSequence[] getChoices()
  {
    return this.mChoices;
  }
  
  public Bundle getExtras()
  {
    return this.mExtras;
  }
  
  public CharSequence getLabel()
  {
    return this.mLabel;
  }
  
  public String getResultKey()
  {
    return this.mResultKey;
  }
  
  public boolean isDataOnly()
  {
    return (!getAllowFreeFormInput()) && ((getChoices() == null) || (getChoices().length == 0)) && (getAllowedDataTypes() != null) && (!getAllowedDataTypes().isEmpty());
  }
  
  public static final class Builder
  {
    private boolean mAllowFreeFormTextInput = true;
    private final Set<String> mAllowedDataTypes = new HashSet();
    private CharSequence[] mChoices;
    private Bundle mExtras = new Bundle();
    private CharSequence mLabel;
    private final String mResultKey;
    
    public Builder(String paramString)
    {
      if (paramString == null) {
        throw new IllegalArgumentException("Result key can't be null");
      }
      this.mResultKey = paramString;
    }
    
    public Builder addExtras(Bundle paramBundle)
    {
      if (paramBundle != null) {
        this.mExtras.putAll(paramBundle);
      }
      return this;
    }
    
    public RemoteInput build()
    {
      return new RemoteInput(this.mResultKey, this.mLabel, this.mChoices, this.mAllowFreeFormTextInput, this.mExtras, this.mAllowedDataTypes);
    }
    
    public Bundle getExtras()
    {
      return this.mExtras;
    }
    
    public Builder setAllowDataType(String paramString, boolean paramBoolean)
    {
      if (paramBoolean)
      {
        this.mAllowedDataTypes.add(paramString);
        return this;
      }
      this.mAllowedDataTypes.remove(paramString);
      return this;
    }
    
    public Builder setAllowFreeFormInput(boolean paramBoolean)
    {
      this.mAllowFreeFormTextInput = paramBoolean;
      return this;
    }
    
    public Builder setChoices(CharSequence[] paramArrayOfCharSequence)
    {
      this.mChoices = paramArrayOfCharSequence;
      return this;
    }
    
    public Builder setLabel(CharSequence paramCharSequence)
    {
      this.mLabel = paramCharSequence;
      return this;
    }
  }
  
  static abstract interface Impl
  {
    public abstract void addDataResultToIntent(RemoteInput paramRemoteInput, Intent paramIntent, Map<String, Uri> paramMap);
    
    public abstract void addResultsToIntent(RemoteInput[] paramArrayOfRemoteInput, Intent paramIntent, Bundle paramBundle);
    
    public abstract Map<String, Uri> getDataResultsFromIntent(Intent paramIntent, String paramString);
    
    public abstract Bundle getResultsFromIntent(Intent paramIntent);
  }
  
  @RequiresApi(20)
  static class ImplApi20
    implements RemoteInput.Impl
  {
    public void addDataResultToIntent(RemoteInput paramRemoteInput, Intent paramIntent, Map<String, Uri> paramMap)
    {
      RemoteInputCompatApi20.addDataResultToIntent(paramRemoteInput, paramIntent, paramMap);
    }
    
    public void addResultsToIntent(RemoteInput[] paramArrayOfRemoteInput, Intent paramIntent, Bundle paramBundle)
    {
      RemoteInputCompatApi20.addResultsToIntent(paramArrayOfRemoteInput, paramIntent, paramBundle);
    }
    
    public Map<String, Uri> getDataResultsFromIntent(Intent paramIntent, String paramString)
    {
      return RemoteInputCompatApi20.getDataResultsFromIntent(paramIntent, paramString);
    }
    
    public Bundle getResultsFromIntent(Intent paramIntent)
    {
      return RemoteInputCompatApi20.getResultsFromIntent(paramIntent);
    }
  }
  
  static class ImplBase
    implements RemoteInput.Impl
  {
    public void addDataResultToIntent(RemoteInput paramRemoteInput, Intent paramIntent, Map<String, Uri> paramMap)
    {
      Log.w("RemoteInput", "RemoteInput is only supported from API Level 16");
    }
    
    public void addResultsToIntent(RemoteInput[] paramArrayOfRemoteInput, Intent paramIntent, Bundle paramBundle)
    {
      Log.w("RemoteInput", "RemoteInput is only supported from API Level 16");
    }
    
    public Map<String, Uri> getDataResultsFromIntent(Intent paramIntent, String paramString)
    {
      Log.w("RemoteInput", "RemoteInput is only supported from API Level 16");
      return null;
    }
    
    public Bundle getResultsFromIntent(Intent paramIntent)
    {
      Log.w("RemoteInput", "RemoteInput is only supported from API Level 16");
      return null;
    }
  }
  
  @RequiresApi(16)
  static class ImplJellybean
    implements RemoteInput.Impl
  {
    public void addDataResultToIntent(RemoteInput paramRemoteInput, Intent paramIntent, Map<String, Uri> paramMap)
    {
      RemoteInputCompatJellybean.addDataResultToIntent(paramRemoteInput, paramIntent, paramMap);
    }
    
    public void addResultsToIntent(RemoteInput[] paramArrayOfRemoteInput, Intent paramIntent, Bundle paramBundle)
    {
      RemoteInputCompatJellybean.addResultsToIntent(paramArrayOfRemoteInput, paramIntent, paramBundle);
    }
    
    public Map<String, Uri> getDataResultsFromIntent(Intent paramIntent, String paramString)
    {
      return RemoteInputCompatJellybean.getDataResultsFromIntent(paramIntent, paramString);
    }
    
    public Bundle getResultsFromIntent(Intent paramIntent)
    {
      return RemoteInputCompatJellybean.getResultsFromIntent(paramIntent);
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v4/app/RemoteInput.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */