package android.support.v7.preference;

import android.content.Context;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat.CollectionItemInfoCompat;
import android.util.AttributeSet;

public class PreferenceCategory
  extends PreferenceGroup
{
  private static final String TAG = "PreferenceCategory";
  
  public PreferenceCategory(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public PreferenceCategory(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, TypedArrayUtils.getAttr(paramContext, R.attr.preferenceCategoryStyle, 16842892));
  }
  
  public PreferenceCategory(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public PreferenceCategory(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
  }
  
  public boolean isEnabled()
  {
    return false;
  }
  
  public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfoCompat paramAccessibilityNodeInfoCompat)
  {
    super.onInitializeAccessibilityNodeInfo(paramAccessibilityNodeInfoCompat);
    AccessibilityNodeInfoCompat.CollectionItemInfoCompat localCollectionItemInfoCompat = paramAccessibilityNodeInfoCompat.getCollectionItemInfo();
    if (localCollectionItemInfoCompat == null) {
      return;
    }
    paramAccessibilityNodeInfoCompat.setCollectionItemInfo(AccessibilityNodeInfoCompat.CollectionItemInfoCompat.obtain(localCollectionItemInfoCompat.getRowIndex(), localCollectionItemInfoCompat.getRowSpan(), localCollectionItemInfoCompat.getColumnIndex(), localCollectionItemInfoCompat.getColumnSpan(), true, localCollectionItemInfoCompat.isSelected()));
  }
  
  protected boolean onPrepareAddPreference(Preference paramPreference)
  {
    if ((paramPreference instanceof PreferenceCategory)) {
      throw new IllegalArgumentException("Cannot add a PreferenceCategory directly to a PreferenceCategory");
    }
    return super.onPrepareAddPreference(paramPreference);
  }
  
  public boolean shouldDisableDependents()
  {
    return !super.isEnabled();
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v7/preference/PreferenceCategory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */