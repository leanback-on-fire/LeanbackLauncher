package com.google.android.tvlauncher.appsview;

import android.content.Context;
import android.content.res.Resources;
import android.support.v17.leanback.widget.FacetProvider;
import android.support.v17.leanback.widget.ItemAlignmentFacet;
import android.support.v17.leanback.widget.ItemAlignmentFacet.ItemAlignmentDef;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Iterator;

public class EditModeGridAdapter
  extends RecyclerView.Adapter<LaunchItemViewHolder>
  implements AppsManager.AppsViewChangeListener
{
  private static final String TAG = "EditModeGridAdapter";
  private int mBottomKeyline;
  private int mKeylineOffsetThree;
  private int mKeylineOffsetTwo;
  private final ArrayList<LaunchItem> mLaunchItems = new ArrayList();
  private OnShowAccessibilityMenuListener mOnShowAccessibilityMenuListener;
  private int mTopKeyline;
  
  public EditModeGridAdapter(Context paramContext)
  {
    paramContext = paramContext.getResources();
    this.mKeylineOffsetTwo = paramContext.getDimensionPixelSize(2131558494);
    this.mKeylineOffsetThree = paramContext.getDimensionPixelSize(2131558493);
  }
  
  public int getItemCount()
  {
    return this.mLaunchItems.size();
  }
  
  ArrayList<LaunchItem> getLaunchItems()
  {
    return this.mLaunchItems;
  }
  
  void moveLaunchItems(int paramInt1, int paramInt2)
  {
    int i = 1;
    if ((paramInt1 < 0) || (paramInt1 > this.mLaunchItems.size() - 1) || (paramInt2 < 0) || (paramInt2 > this.mLaunchItems.size() - 1)) {}
    int j;
    do
    {
      return;
      LaunchItem localLaunchItem1 = (LaunchItem)this.mLaunchItems.get(paramInt1);
      LaunchItem localLaunchItem2 = (LaunchItem)this.mLaunchItems.get(paramInt2);
      this.mLaunchItems.set(paramInt1, localLaunchItem2);
      this.mLaunchItems.set(paramInt2, localLaunchItem1);
      notifyItemMoved(paramInt1, paramInt2);
      j = paramInt2 - paramInt1;
    } while (Math.abs(j) <= 1);
    if (j > 0) {
      i = -1;
    }
    notifyItemMoved(paramInt2 + i, paramInt1);
  }
  
  public void onBindViewHolder(LaunchItemViewHolder paramLaunchItemViewHolder, int paramInt)
  {
    paramLaunchItemViewHolder.set((LaunchItem)this.mLaunchItems.get(paramInt));
  }
  
  public LaunchItemViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
  {
    return new LaunchItemViewHolder(LayoutInflater.from(paramViewGroup.getContext()).inflate(2130968615, paramViewGroup, false));
  }
  
  public void onEditModeItemOrderChange(ArrayList<LaunchItem> paramArrayList, boolean paramBoolean, Pair<Integer, Integer> paramPair) {}
  
  public void onLaunchItemsAddedOrUpdated(ArrayList<LaunchItem> paramArrayList)
  {
    paramArrayList = paramArrayList.iterator();
    while (paramArrayList.hasNext())
    {
      LaunchItem localLaunchItem = (LaunchItem)paramArrayList.next();
      int i = this.mLaunchItems.indexOf(localLaunchItem);
      if (i == -1)
      {
        if ((this.mLaunchItems.size() <= 0) || (((LaunchItem)this.mLaunchItems.get(0)).isGame() == localLaunchItem.isGame()))
        {
          this.mLaunchItems.add(localLaunchItem);
          notifyItemInserted(this.mLaunchItems.size() - 1);
        }
      }
      else {
        notifyItemChanged(i);
      }
    }
  }
  
  public void onLaunchItemsLoaded() {}
  
  public void onLaunchItemsRemoved(ArrayList<LaunchItem> paramArrayList)
  {
    paramArrayList = paramArrayList.iterator();
    while (paramArrayList.hasNext())
    {
      LaunchItem localLaunchItem = (LaunchItem)paramArrayList.next();
      int i = this.mLaunchItems.indexOf(localLaunchItem);
      if (i != -1)
      {
        this.mLaunchItems.remove(i);
        notifyItemRemoved(i);
      }
    }
  }
  
  public void setBottomKeyline(int paramInt)
  {
    this.mBottomKeyline = paramInt;
  }
  
  void setLaunchItems(ArrayList<LaunchItem> paramArrayList)
  {
    this.mLaunchItems.clear();
    this.mLaunchItems.addAll(paramArrayList);
    notifyDataSetChanged();
  }
  
  void setOnShowAccessibilityMenuListener(OnShowAccessibilityMenuListener paramOnShowAccessibilityMenuListener)
  {
    this.mOnShowAccessibilityMenuListener = paramOnShowAccessibilityMenuListener;
  }
  
  public void setTopKeyline(int paramInt)
  {
    this.mTopKeyline = paramInt;
  }
  
  final class LaunchItemViewHolder
    extends RecyclerView.ViewHolder
    implements FacetProvider
  {
    LaunchItemViewHolder(View paramView)
    {
      super();
    }
    
    private int calculateOffset()
    {
      int i = ((Integer)LaunchItemsHolder.getRowColIndexFromListIndex(getAdapterPosition()).first).intValue();
      int j = LaunchItemsHolder.getRowCount(EditModeGridAdapter.this.getItemCount());
      if ((EditModeGridAdapter.this.mKeylineOffsetThree == EditModeGridAdapter.this.mBottomKeyline) && (i == j - 2) && (j >= 3)) {
        return EditModeGridAdapter.this.mKeylineOffsetTwo;
      }
      if (i == j - 1) {
        return EditModeGridAdapter.this.mBottomKeyline;
      }
      return EditModeGridAdapter.this.mTopKeyline;
    }
    
    public Object getFacet(Class<?> paramClass)
    {
      if (getAdapterPosition() == -1) {
        return null;
      }
      int i = calculateOffset();
      paramClass = new ItemAlignmentFacet.ItemAlignmentDef();
      paramClass.setItemAlignmentOffset(-i);
      paramClass.setItemAlignmentOffsetPercent(50.0F);
      ItemAlignmentFacet localItemAlignmentFacet = new ItemAlignmentFacet();
      localItemAlignmentFacet.setAlignmentDefs(new ItemAlignmentFacet.ItemAlignmentDef[] { paramClass });
      return localItemAlignmentFacet;
    }
    
    public void set(LaunchItem paramLaunchItem)
    {
      ((EditModeBannerView)this.itemView).setAppBannerItems(paramLaunchItem, false, null, EditModeGridAdapter.this.mOnShowAccessibilityMenuListener);
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/appsview/EditModeGridAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */