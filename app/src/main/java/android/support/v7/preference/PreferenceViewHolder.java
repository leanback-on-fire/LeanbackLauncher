package android.support.v7.preference;

import android.support.annotation.IdRes;
import android.support.annotation.RestrictTo;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.SparseArray;
import android.view.View;

public class PreferenceViewHolder
  extends RecyclerView.ViewHolder
{
  private final SparseArray<View> mCachedViews = new SparseArray(4);
  private boolean mDividerAllowedAbove;
  private boolean mDividerAllowedBelow;
  
  PreferenceViewHolder(View paramView)
  {
    super(paramView);
    this.mCachedViews.put(16908310, paramView.findViewById(16908310));
    this.mCachedViews.put(16908304, paramView.findViewById(16908304));
    this.mCachedViews.put(16908294, paramView.findViewById(16908294));
    this.mCachedViews.put(R.id.icon_frame, paramView.findViewById(R.id.icon_frame));
    this.mCachedViews.put(16908350, paramView.findViewById(16908350));
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.TESTS})
  public static PreferenceViewHolder createInstanceForTests(View paramView)
  {
    return new PreferenceViewHolder(paramView);
  }
  
  public View findViewById(@IdRes int paramInt)
  {
    View localView = (View)this.mCachedViews.get(paramInt);
    if (localView != null) {
      return localView;
    }
    localView = this.itemView.findViewById(paramInt);
    if (localView != null) {
      this.mCachedViews.put(paramInt, localView);
    }
    return localView;
  }
  
  public boolean isDividerAllowedAbove()
  {
    return this.mDividerAllowedAbove;
  }
  
  public boolean isDividerAllowedBelow()
  {
    return this.mDividerAllowedBelow;
  }
  
  public void setDividerAllowedAbove(boolean paramBoolean)
  {
    this.mDividerAllowedAbove = paramBoolean;
  }
  
  public void setDividerAllowedBelow(boolean paramBoolean)
  {
    this.mDividerAllowedBelow = paramBoolean;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v7/preference/PreferenceViewHolder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */