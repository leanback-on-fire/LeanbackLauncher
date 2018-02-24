package android.support.v17.leanback.app;

import android.os.Bundle;
import android.support.v17.leanback.widget.ItemBridgeAdapter;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ObjectAdapter;
import android.support.v17.leanback.widget.OnChildViewHolderSelectedListener;
import android.support.v17.leanback.widget.PresenterSelector;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.VerticalGridView;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.AdapterDataObserver;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

abstract class BaseRowSupportFragment
  extends Fragment
{
  private static final String CURRENT_SELECTED_POSITION = "currentSelectedPosition";
  private ObjectAdapter mAdapter;
  final ItemBridgeAdapter mBridgeAdapter = new ItemBridgeAdapter();
  private LateSelectionObserver mLateSelectionObserver = new LateSelectionObserver();
  private boolean mPendingTransitionPrepare;
  private PresenterSelector mPresenterSelector;
  private final OnChildViewHolderSelectedListener mRowSelectedListener = new OnChildViewHolderSelectedListener()
  {
    public void onChildViewHolderSelected(RecyclerView paramAnonymousRecyclerView, RecyclerView.ViewHolder paramAnonymousViewHolder, int paramAnonymousInt1, int paramAnonymousInt2)
    {
      if (!BaseRowSupportFragment.this.mLateSelectionObserver.mIsLateSelection)
      {
        BaseRowSupportFragment.this.mSelectedPosition = paramAnonymousInt1;
        BaseRowSupportFragment.this.onRowSelected(paramAnonymousRecyclerView, paramAnonymousViewHolder, paramAnonymousInt1, paramAnonymousInt2);
      }
    }
  };
  int mSelectedPosition = -1;
  VerticalGridView mVerticalGridView;
  
  VerticalGridView findGridViewFromRoot(View paramView)
  {
    return (VerticalGridView)paramView;
  }
  
  public final ObjectAdapter getAdapter()
  {
    return this.mAdapter;
  }
  
  public final ItemBridgeAdapter getBridgeAdapter()
  {
    return this.mBridgeAdapter;
  }
  
  Object getItem(Row paramRow, int paramInt)
  {
    if ((paramRow instanceof ListRow)) {
      return ((ListRow)paramRow).getAdapter().get(paramInt);
    }
    return null;
  }
  
  abstract int getLayoutResourceId();
  
  public final PresenterSelector getPresenterSelector()
  {
    return this.mPresenterSelector;
  }
  
  public int getSelectedPosition()
  {
    return this.mSelectedPosition;
  }
  
  public final VerticalGridView getVerticalGridView()
  {
    return this.mVerticalGridView;
  }
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    paramLayoutInflater = paramLayoutInflater.inflate(getLayoutResourceId(), paramViewGroup, false);
    this.mVerticalGridView = findGridViewFromRoot(paramLayoutInflater);
    if (this.mPendingTransitionPrepare)
    {
      this.mPendingTransitionPrepare = false;
      onTransitionPrepare();
    }
    return paramLayoutInflater;
  }
  
  public void onDestroyView()
  {
    super.onDestroyView();
    this.mLateSelectionObserver.clear();
    this.mVerticalGridView = null;
  }
  
  void onRowSelected(RecyclerView paramRecyclerView, RecyclerView.ViewHolder paramViewHolder, int paramInt1, int paramInt2) {}
  
  public void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    paramBundle.putInt("currentSelectedPosition", this.mSelectedPosition);
  }
  
  public void onTransitionEnd()
  {
    if (this.mVerticalGridView != null)
    {
      this.mVerticalGridView.setLayoutFrozen(false);
      this.mVerticalGridView.setAnimateChildLayout(true);
      this.mVerticalGridView.setPruneChild(true);
      this.mVerticalGridView.setFocusSearchDisabled(false);
      this.mVerticalGridView.setScrollEnabled(true);
    }
  }
  
  public boolean onTransitionPrepare()
  {
    if (this.mVerticalGridView != null)
    {
      this.mVerticalGridView.setAnimateChildLayout(false);
      this.mVerticalGridView.setScrollEnabled(false);
      return true;
    }
    this.mPendingTransitionPrepare = true;
    return false;
  }
  
  public void onTransitionStart()
  {
    if (this.mVerticalGridView != null)
    {
      this.mVerticalGridView.setPruneChild(false);
      this.mVerticalGridView.setLayoutFrozen(true);
      this.mVerticalGridView.setFocusSearchDisabled(true);
    }
  }
  
  public void onViewCreated(View paramView, Bundle paramBundle)
  {
    if (paramBundle != null) {
      this.mSelectedPosition = paramBundle.getInt("currentSelectedPosition", -1);
    }
    setAdapterAndSelection();
    this.mVerticalGridView.setOnChildViewHolderSelectedListener(this.mRowSelectedListener);
  }
  
  public final void setAdapter(ObjectAdapter paramObjectAdapter)
  {
    this.mAdapter = paramObjectAdapter;
    updateAdapter();
  }
  
  void setAdapterAndSelection()
  {
    if (this.mAdapter == null) {}
    do
    {
      return;
      if (this.mVerticalGridView.getAdapter() != this.mBridgeAdapter) {
        this.mVerticalGridView.setAdapter(this.mBridgeAdapter);
      }
      if ((this.mBridgeAdapter.getItemCount() == 0) && (this.mSelectedPosition >= 0)) {}
      for (int i = 1; i != 0; i = 0)
      {
        this.mLateSelectionObserver.startLateSelection();
        return;
      }
    } while (this.mSelectedPosition < 0);
    this.mVerticalGridView.setSelectedPosition(this.mSelectedPosition);
  }
  
  public void setAlignment(int paramInt)
  {
    if (this.mVerticalGridView != null)
    {
      this.mVerticalGridView.setItemAlignmentOffset(0);
      this.mVerticalGridView.setItemAlignmentOffsetPercent(-1.0F);
      this.mVerticalGridView.setWindowAlignmentOffset(paramInt);
      this.mVerticalGridView.setWindowAlignmentOffsetPercent(-1.0F);
      this.mVerticalGridView.setWindowAlignment(0);
    }
  }
  
  public final void setPresenterSelector(PresenterSelector paramPresenterSelector)
  {
    this.mPresenterSelector = paramPresenterSelector;
    updateAdapter();
  }
  
  public void setSelectedPosition(int paramInt)
  {
    setSelectedPosition(paramInt, true);
  }
  
  public void setSelectedPosition(int paramInt, boolean paramBoolean)
  {
    if (this.mSelectedPosition == paramInt) {}
    do
    {
      return;
      this.mSelectedPosition = paramInt;
    } while ((this.mVerticalGridView == null) || (this.mLateSelectionObserver.mIsLateSelection));
    if (paramBoolean)
    {
      this.mVerticalGridView.setSelectedPositionSmooth(paramInt);
      return;
    }
    this.mVerticalGridView.setSelectedPosition(paramInt);
  }
  
  void updateAdapter()
  {
    this.mBridgeAdapter.setAdapter(this.mAdapter);
    this.mBridgeAdapter.setPresenter(this.mPresenterSelector);
    if (this.mVerticalGridView != null) {
      setAdapterAndSelection();
    }
  }
  
  private class LateSelectionObserver
    extends RecyclerView.AdapterDataObserver
  {
    boolean mIsLateSelection = false;
    
    LateSelectionObserver() {}
    
    void clear()
    {
      if (this.mIsLateSelection)
      {
        this.mIsLateSelection = false;
        BaseRowSupportFragment.this.mBridgeAdapter.unregisterAdapterDataObserver(this);
      }
    }
    
    public void onChanged()
    {
      performLateSelection();
    }
    
    public void onItemRangeInserted(int paramInt1, int paramInt2)
    {
      performLateSelection();
    }
    
    void performLateSelection()
    {
      clear();
      if (BaseRowSupportFragment.this.mVerticalGridView != null) {
        BaseRowSupportFragment.this.mVerticalGridView.setSelectedPosition(BaseRowSupportFragment.this.mSelectedPosition);
      }
    }
    
    void startLateSelection()
    {
      this.mIsLateSelection = true;
      BaseRowSupportFragment.this.mBridgeAdapter.registerAdapterDataObserver(this);
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/app/BaseRowSupportFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */