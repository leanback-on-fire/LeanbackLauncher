package android.support.v7.preference;

import android.support.annotation.Nullable;
import java.util.Set;

public abstract class PreferenceDataStore
{
  public boolean getBoolean(String paramString, boolean paramBoolean)
  {
    return paramBoolean;
  }
  
  public float getFloat(String paramString, float paramFloat)
  {
    return paramFloat;
  }
  
  public int getInt(String paramString, int paramInt)
  {
    return paramInt;
  }
  
  public long getLong(String paramString, long paramLong)
  {
    return paramLong;
  }
  
  @Nullable
  public String getString(String paramString1, @Nullable String paramString2)
  {
    return paramString2;
  }
  
  @Nullable
  public Set<String> getStringSet(String paramString, @Nullable Set<String> paramSet)
  {
    return paramSet;
  }
  
  public void putBoolean(String paramString, boolean paramBoolean)
  {
    throw new UnsupportedOperationException("Not implemented on this data store");
  }
  
  public void putFloat(String paramString, float paramFloat)
  {
    throw new UnsupportedOperationException("Not implemented on this data store");
  }
  
  public void putInt(String paramString, int paramInt)
  {
    throw new UnsupportedOperationException("Not implemented on this data store");
  }
  
  public void putLong(String paramString, long paramLong)
  {
    throw new UnsupportedOperationException("Not implemented on this data store");
  }
  
  public void putString(String paramString1, @Nullable String paramString2)
  {
    throw new UnsupportedOperationException("Not implemented on this data store");
  }
  
  public void putStringSet(String paramString, @Nullable Set<String> paramSet)
  {
    throw new UnsupportedOperationException("Not implemented on this data store");
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v7/preference/PreferenceDataStore.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */