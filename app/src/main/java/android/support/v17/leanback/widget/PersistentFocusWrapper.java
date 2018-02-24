package android.support.v17.leanback.widget;

import android.content.Context;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.BaseSavedState;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import java.util.ArrayList;

class PersistentFocusWrapper
  extends FrameLayout
{
  private static final boolean DEBUG = false;
  private static final String TAG = "PersistentFocusWrapper";
  private boolean mPersistFocusVertical = true;
  private int mSelectedPosition = -1;
  
  public PersistentFocusWrapper(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  public PersistentFocusWrapper(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
  }
  
  private boolean shouldPersistFocusFromDirection(int paramInt)
  {
    return ((this.mPersistFocusVertical) && ((paramInt == 33) || (paramInt == 130))) || ((!this.mPersistFocusVertical) && ((paramInt == 17) || (paramInt == 66)));
  }
  
  public void addFocusables(ArrayList<View> paramArrayList, int paramInt1, int paramInt2)
  {
    if ((hasFocus()) || (getGrandChildCount() == 0) || (!shouldPersistFocusFromDirection(paramInt1)))
    {
      super.addFocusables(paramArrayList, paramInt1, paramInt2);
      return;
    }
    paramArrayList.add(this);
  }
  
  public void clearSelection()
  {
    this.mSelectedPosition = -1;
    if (hasFocus()) {
      clearFocus();
    }
  }
  
  int getGrandChildCount()
  {
    ViewGroup localViewGroup = (ViewGroup)getChildAt(0);
    if (localViewGroup == null) {
      return 0;
    }
    return localViewGroup.getChildCount();
  }
  
  protected void onRestoreInstanceState(Parcelable paramParcelable)
  {
    if (!(paramParcelable instanceof SavedState))
    {
      super.onRestoreInstanceState(paramParcelable);
      return;
    }
    SavedState localSavedState = (SavedState)paramParcelable;
    this.mSelectedPosition = ((SavedState)paramParcelable).mSelectedPosition;
    super.onRestoreInstanceState(localSavedState.getSuperState());
  }
  
  protected Parcelable onSaveInstanceState()
  {
    SavedState localSavedState = new SavedState(super.onSaveInstanceState());
    localSavedState.mSelectedPosition = this.mSelectedPosition;
    return localSavedState;
  }
  
  public void persistFocusHorizontal()
  {
    this.mPersistFocusVertical = false;
  }
  
  public void persistFocusVertical()
  {
    this.mPersistFocusVertical = true;
  }
  
  public void requestChildFocus(View paramView1, View paramView2)
  {
    super.requestChildFocus(paramView1, paramView2);
    while ((paramView2 != null) && (paramView2.getParent() != paramView1)) {
      paramView2 = (View)paramView2.getParent();
    }
    if (paramView2 == null) {}
    for (int i = -1;; i = ((ViewGroup)paramView1).indexOfChild(paramView2))
    {
      this.mSelectedPosition = i;
      return;
    }
  }
  
  public boolean requestFocus(int paramInt, Rect paramRect)
  {
    ViewGroup localViewGroup = (ViewGroup)getChildAt(0);
    if ((localViewGroup != null) && (this.mSelectedPosition >= 0) && (this.mSelectedPosition < getGrandChildCount()) && (localViewGroup.getChildAt(this.mSelectedPosition).requestFocus(paramInt, paramRect))) {
      return true;
    }
    return super.requestFocus(paramInt, paramRect);
  }
  
  static class SavedState
    extends View.BaseSavedState
  {
    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator()
    {
      public PersistentFocusWrapper.SavedState createFromParcel(Parcel paramAnonymousParcel)
      {
        return new PersistentFocusWrapper.SavedState(paramAnonymousParcel);
      }
      
      public PersistentFocusWrapper.SavedState[] newArray(int paramAnonymousInt)
      {
        return new PersistentFocusWrapper.SavedState[paramAnonymousInt];
      }
    };
    int mSelectedPosition;
    
    SavedState(Parcel paramParcel)
    {
      super();
      this.mSelectedPosition = paramParcel.readInt();
    }
    
    SavedState(Parcelable paramParcelable)
    {
      super();
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      super.writeToParcel(paramParcel, paramInt);
      paramParcel.writeInt(this.mSelectedPosition);
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/PersistentFocusWrapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */