package android.support.v7.preference;

import android.content.Context;
import android.support.annotation.RestrictTo;
import android.support.v4.content.res.TypedArrayUtils;
import android.util.AttributeSet;

public final class PreferenceScreen
  extends PreferenceGroup
{
  private boolean mShouldUseGeneratedIds = true;
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public PreferenceScreen(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet, TypedArrayUtils.getAttr(paramContext, R.attr.preferenceScreenStyle, 16842891));
  }
  
  protected boolean isOnSameScreenAsChildren()
  {
    return false;
  }
  
  protected void onClick()
  {
    if ((getIntent() != null) || (getFragment() != null) || (getPreferenceCount() == 0)) {}
    PreferenceManager.OnNavigateToScreenListener localOnNavigateToScreenListener;
    do
    {
      return;
      localOnNavigateToScreenListener = getPreferenceManager().getOnNavigateToScreenListener();
    } while (localOnNavigateToScreenListener == null);
    localOnNavigateToScreenListener.onNavigateToScreen(this);
  }
  
  public void setShouldUseGeneratedIds(boolean paramBoolean)
  {
    if (isAttached()) {
      throw new IllegalStateException("Cannot change the usage of generated IDs while attached to the preference hierarchy");
    }
    this.mShouldUseGeneratedIds = paramBoolean;
  }
  
  public boolean shouldUseGeneratedIds()
  {
    return this.mShouldUseGeneratedIds;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v7/preference/PreferenceScreen.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */