package android.support.v7.preference.internal;

import android.content.Context;
import android.support.annotation.RestrictTo;
import android.support.v7.preference.DialogPreference;
import android.util.AttributeSet;
import java.util.Set;

@RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
public abstract class AbstractMultiSelectListPreference
  extends DialogPreference
{
  public AbstractMultiSelectListPreference(Context paramContext)
  {
    super(paramContext);
  }
  
  public AbstractMultiSelectListPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  public AbstractMultiSelectListPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
  }
  
  public AbstractMultiSelectListPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
  }
  
  public abstract CharSequence[] getEntries();
  
  public abstract CharSequence[] getEntryValues();
  
  public abstract Set<String> getValues();
  
  public abstract void setValues(Set<String> paramSet);
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v7/preference/internal/AbstractMultiSelectListPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */