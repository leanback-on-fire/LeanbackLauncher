package android.support.v7.preference;

import android.os.Bundle;
import android.support.annotation.RestrictTo;
import android.support.v4.view.AccessibilityDelegateCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerViewAccessibilityDelegate;
import android.view.View;

@RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
public class PreferenceRecyclerViewAccessibilityDelegate
  extends RecyclerViewAccessibilityDelegate
{
  final AccessibilityDelegateCompat mDefaultItemDelegate = super.getItemDelegate();
  final AccessibilityDelegateCompat mItemDelegate = new AccessibilityDelegateCompat()
  {
    public void onInitializeAccessibilityNodeInfo(View paramAnonymousView, AccessibilityNodeInfoCompat paramAnonymousAccessibilityNodeInfoCompat)
    {
      PreferenceRecyclerViewAccessibilityDelegate.this.mDefaultItemDelegate.onInitializeAccessibilityNodeInfo(paramAnonymousView, paramAnonymousAccessibilityNodeInfoCompat);
      int i = PreferenceRecyclerViewAccessibilityDelegate.this.mRecyclerView.getChildAdapterPosition(paramAnonymousView);
      paramAnonymousView = PreferenceRecyclerViewAccessibilityDelegate.this.mRecyclerView.getAdapter();
      if (!(paramAnonymousView instanceof PreferenceGroupAdapter)) {}
      do
      {
        return;
        paramAnonymousView = ((PreferenceGroupAdapter)paramAnonymousView).getItem(i);
      } while (paramAnonymousView == null);
      paramAnonymousView.onInitializeAccessibilityNodeInfo(paramAnonymousAccessibilityNodeInfoCompat);
    }
    
    public boolean performAccessibilityAction(View paramAnonymousView, int paramAnonymousInt, Bundle paramAnonymousBundle)
    {
      return PreferenceRecyclerViewAccessibilityDelegate.this.mDefaultItemDelegate.performAccessibilityAction(paramAnonymousView, paramAnonymousInt, paramAnonymousBundle);
    }
  };
  final RecyclerView mRecyclerView;
  
  public PreferenceRecyclerViewAccessibilityDelegate(RecyclerView paramRecyclerView)
  {
    super(paramRecyclerView);
    this.mRecyclerView = paramRecyclerView;
  }
  
  public AccessibilityDelegateCompat getItemDelegate()
  {
    return this.mItemDelegate;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v7/preference/PreferenceRecyclerViewAccessibilityDelegate.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */