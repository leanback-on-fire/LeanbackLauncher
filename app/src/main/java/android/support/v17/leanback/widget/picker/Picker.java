package android.support.v17.leanback.widget.picker;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.support.v17.leanback.R.dimen;
import android.support.v17.leanback.R.id;
import android.support.v17.leanback.R.layout;
import android.support.v17.leanback.widget.OnChildViewHolderSelectedListener;
import android.support.v17.leanback.widget.VerticalGridView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class Picker
  extends FrameLayout
{
  private Interpolator mAccelerateInterpolator;
  private int mAlphaAnimDuration;
  private final OnChildViewHolderSelectedListener mColumnChangeListener = new OnChildViewHolderSelectedListener()
  {
    public void onChildViewHolderSelected(RecyclerView paramAnonymousRecyclerView, RecyclerView.ViewHolder paramAnonymousViewHolder, int paramAnonymousInt1, int paramAnonymousInt2)
    {
      Picker.PickerScrollArrayAdapter localPickerScrollArrayAdapter = (Picker.PickerScrollArrayAdapter)paramAnonymousRecyclerView.getAdapter();
      paramAnonymousInt2 = Picker.this.mColumnViews.indexOf(paramAnonymousRecyclerView);
      Picker.this.updateColumnAlpha(paramAnonymousInt2, true);
      if (paramAnonymousViewHolder != null)
      {
        int i = ((PickerColumn)Picker.this.mColumns.get(paramAnonymousInt2)).getMinValue();
        Picker.this.onColumnValueChanged(paramAnonymousInt2, i + paramAnonymousInt1);
      }
    }
  };
  final List<VerticalGridView> mColumnViews = new ArrayList();
  ArrayList<PickerColumn> mColumns;
  private Interpolator mDecelerateInterpolator;
  private float mFocusedAlpha;
  private float mInvisibleColumnAlpha;
  private ArrayList<PickerValueListener> mListeners;
  private int mPickerItemLayoutId = R.layout.lb_picker_item;
  private int mPickerItemTextViewId = 0;
  private ViewGroup mPickerView;
  private ViewGroup mRootView;
  private int mSelectedColumn = 0;
  private CharSequence mSeparator;
  private float mUnfocusedAlpha;
  private float mVisibleColumnAlpha;
  private float mVisibleItems = 1.0F;
  private float mVisibleItemsActivated = 3.0F;
  
  public Picker(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    setEnabled(true);
    setDescendantFocusability(262144);
    this.mFocusedAlpha = 1.0F;
    this.mUnfocusedAlpha = 1.0F;
    this.mVisibleColumnAlpha = 0.5F;
    this.mInvisibleColumnAlpha = 0.0F;
    this.mAlphaAnimDuration = 200;
    this.mDecelerateInterpolator = new DecelerateInterpolator(2.5F);
    this.mAccelerateInterpolator = new AccelerateInterpolator(2.5F);
    this.mRootView = ((ViewGroup)LayoutInflater.from(getContext()).inflate(R.layout.lb_picker, this, true));
    this.mPickerView = ((ViewGroup)this.mRootView.findViewById(R.id.picker));
  }
  
  private float getFloat(int paramInt)
  {
    TypedValue localTypedValue = new TypedValue();
    getContext().getResources().getValue(paramInt, localTypedValue, true);
    return localTypedValue.getFloat();
  }
  
  private void notifyValueChanged(int paramInt)
  {
    if (this.mListeners != null)
    {
      int i = this.mListeners.size() - 1;
      while (i >= 0)
      {
        ((PickerValueListener)this.mListeners.get(i)).onValueChanged(this, paramInt);
        i -= 1;
      }
    }
  }
  
  private void setOrAnimateAlpha(View paramView, boolean paramBoolean, float paramFloat1, float paramFloat2, Interpolator paramInterpolator)
  {
    paramView.animate().cancel();
    if (!paramBoolean)
    {
      paramView.setAlpha(paramFloat1);
      return;
    }
    if (paramFloat2 >= 0.0F) {
      paramView.setAlpha(paramFloat2);
    }
    paramView.animate().alpha(paramFloat1).setDuration(this.mAlphaAnimDuration).setInterpolator(paramInterpolator).start();
  }
  
  private void updateColumnSize()
  {
    int i = 0;
    while (i < getColumnsCount())
    {
      updateColumnSize((VerticalGridView)this.mColumnViews.get(i));
      i += 1;
    }
  }
  
  private void updateColumnSize(VerticalGridView paramVerticalGridView)
  {
    ViewGroup.LayoutParams localLayoutParams = paramVerticalGridView.getLayoutParams();
    if (isActivated()) {}
    for (float f = getActivatedVisibleItemCount();; f = getVisibleItemCount())
    {
      localLayoutParams.height = ((int)(getPickerItemHeightPixels() * f + paramVerticalGridView.getVerticalSpacing() * (f - 1.0F)));
      paramVerticalGridView.setLayoutParams(localLayoutParams);
      return;
    }
  }
  
  private void updateItemFocusable()
  {
    boolean bool = isActivated();
    int i = 0;
    while (i < getColumnsCount())
    {
      VerticalGridView localVerticalGridView = (VerticalGridView)this.mColumnViews.get(i);
      int j = 0;
      while (j < localVerticalGridView.getChildCount())
      {
        localVerticalGridView.getChildAt(j).setFocusable(bool);
        j += 1;
      }
      i += 1;
    }
  }
  
  public void addOnValueChangedListener(PickerValueListener paramPickerValueListener)
  {
    if (this.mListeners == null) {
      this.mListeners = new ArrayList();
    }
    this.mListeners.add(paramPickerValueListener);
  }
  
  public boolean dispatchKeyEvent(KeyEvent paramKeyEvent)
  {
    boolean bool = true;
    if (isActivated())
    {
      switch (paramKeyEvent.getKeyCode())
      {
      default: 
        bool = super.dispatchKeyEvent(paramKeyEvent);
      }
      do
      {
        return bool;
      } while (paramKeyEvent.getAction() != 1);
      performClick();
      return true;
    }
    return super.dispatchKeyEvent(paramKeyEvent);
  }
  
  public float getActivatedVisibleItemCount()
  {
    return this.mVisibleItemsActivated;
  }
  
  public PickerColumn getColumnAt(int paramInt)
  {
    if (this.mColumns == null) {
      return null;
    }
    return (PickerColumn)this.mColumns.get(paramInt);
  }
  
  public int getColumnsCount()
  {
    if (this.mColumns == null) {
      return 0;
    }
    return this.mColumns.size();
  }
  
  protected int getPickerItemHeightPixels()
  {
    return getContext().getResources().getDimensionPixelSize(R.dimen.picker_item_height);
  }
  
  public final int getPickerItemLayoutId()
  {
    return this.mPickerItemLayoutId;
  }
  
  public final int getPickerItemTextViewId()
  {
    return this.mPickerItemTextViewId;
  }
  
  public int getSelectedColumn()
  {
    return this.mSelectedColumn;
  }
  
  public final CharSequence getSeparator()
  {
    return this.mSeparator;
  }
  
  public float getVisibleItemCount()
  {
    return 1.0F;
  }
  
  public void onColumnValueChanged(int paramInt1, int paramInt2)
  {
    PickerColumn localPickerColumn = (PickerColumn)this.mColumns.get(paramInt1);
    if (localPickerColumn.getCurrentValue() != paramInt2)
    {
      localPickerColumn.setCurrentValue(paramInt2);
      notifyValueChanged(paramInt1);
    }
  }
  
  protected boolean onRequestFocusInDescendants(int paramInt, Rect paramRect)
  {
    int i = getSelectedColumn();
    if (i < this.mColumnViews.size()) {
      return ((VerticalGridView)this.mColumnViews.get(i)).requestFocus(paramInt, paramRect);
    }
    return false;
  }
  
  public void removeOnValueChangedListener(PickerValueListener paramPickerValueListener)
  {
    if (this.mListeners != null) {
      this.mListeners.remove(paramPickerValueListener);
    }
  }
  
  public void requestChildFocus(View paramView1, View paramView2)
  {
    super.requestChildFocus(paramView1, paramView2);
    int i = 0;
    while (i < this.mColumnViews.size())
    {
      if (((VerticalGridView)this.mColumnViews.get(i)).hasFocus()) {
        setSelectedColumn(i);
      }
      i += 1;
    }
  }
  
  public void setActivated(boolean paramBoolean)
  {
    if (paramBoolean == isActivated())
    {
      super.setActivated(paramBoolean);
      return;
    }
    super.setActivated(paramBoolean);
    boolean bool = hasFocus();
    int j = getSelectedColumn();
    setDescendantFocusability(131072);
    if ((!paramBoolean) && (bool) && (isFocusable())) {
      requestFocus();
    }
    int i = 0;
    while (i < getColumnsCount())
    {
      ((VerticalGridView)this.mColumnViews.get(i)).setFocusable(paramBoolean);
      i += 1;
    }
    updateColumnSize();
    updateItemFocusable();
    if ((paramBoolean) && (bool) && (j >= 0)) {
      ((VerticalGridView)this.mColumnViews.get(j)).requestFocus();
    }
    setDescendantFocusability(262144);
  }
  
  public void setActivatedVisibleItemCount(float paramFloat)
  {
    if (paramFloat <= 0.0F) {
      throw new IllegalArgumentException();
    }
    if (this.mVisibleItemsActivated != paramFloat)
    {
      this.mVisibleItemsActivated = paramFloat;
      if (isActivated()) {
        updateColumnSize();
      }
    }
  }
  
  public void setColumnAt(int paramInt, PickerColumn paramPickerColumn)
  {
    this.mColumns.set(paramInt, paramPickerColumn);
    VerticalGridView localVerticalGridView = (VerticalGridView)this.mColumnViews.get(paramInt);
    PickerScrollArrayAdapter localPickerScrollArrayAdapter = (PickerScrollArrayAdapter)localVerticalGridView.getAdapter();
    if (localPickerScrollArrayAdapter != null) {
      localPickerScrollArrayAdapter.notifyDataSetChanged();
    }
    localVerticalGridView.setSelectedPosition(paramPickerColumn.getCurrentValue() - paramPickerColumn.getMinValue());
  }
  
  public void setColumnValue(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    Object localObject = (PickerColumn)this.mColumns.get(paramInt1);
    if (((PickerColumn)localObject).getCurrentValue() != paramInt2)
    {
      ((PickerColumn)localObject).setCurrentValue(paramInt2);
      notifyValueChanged(paramInt1);
      localObject = (VerticalGridView)this.mColumnViews.get(paramInt1);
      if (localObject != null)
      {
        paramInt1 = paramInt2 - ((PickerColumn)this.mColumns.get(paramInt1)).getMinValue();
        if (!paramBoolean) {
          break label81;
        }
        ((VerticalGridView)localObject).setSelectedPositionSmooth(paramInt1);
      }
    }
    return;
    label81:
    ((VerticalGridView)localObject).setSelectedPosition(paramInt1);
  }
  
  public void setColumns(List<PickerColumn> paramList)
  {
    this.mColumnViews.clear();
    this.mPickerView.removeAllViews();
    this.mColumns = new ArrayList(paramList);
    if (this.mSelectedColumn > this.mColumns.size() - 1) {
      this.mSelectedColumn = (this.mColumns.size() - 1);
    }
    paramList = LayoutInflater.from(getContext());
    int j = getColumnsCount();
    int i = 0;
    while (i < j)
    {
      VerticalGridView localVerticalGridView = (VerticalGridView)paramList.inflate(R.layout.lb_picker_column, this.mPickerView, false);
      updateColumnSize(localVerticalGridView);
      localVerticalGridView.setWindowAlignment(0);
      localVerticalGridView.setHasFixedSize(false);
      localVerticalGridView.setFocusable(isActivated());
      localVerticalGridView.setItemViewCacheSize(0);
      this.mColumnViews.add(localVerticalGridView);
      this.mPickerView.addView(localVerticalGridView);
      if ((i != j - 1) && (getSeparator() != null))
      {
        TextView localTextView = (TextView)paramList.inflate(R.layout.lb_picker_separator, this.mPickerView, false);
        localTextView.setText(getSeparator());
        this.mPickerView.addView(localTextView);
      }
      localVerticalGridView.setAdapter(new PickerScrollArrayAdapter(getContext(), getPickerItemLayoutId(), getPickerItemTextViewId(), i));
      localVerticalGridView.setOnChildViewHolderSelectedListener(this.mColumnChangeListener);
      i += 1;
    }
  }
  
  void setOrAnimateAlpha(View paramView, boolean paramBoolean1, int paramInt, boolean paramBoolean2)
  {
    if ((paramInt == this.mSelectedColumn) || (!hasFocus())) {
      paramInt = 1;
    }
    while (paramBoolean1) {
      if (paramInt != 0)
      {
        setOrAnimateAlpha(paramView, paramBoolean2, this.mFocusedAlpha, -1.0F, this.mDecelerateInterpolator);
        return;
        paramInt = 0;
      }
      else
      {
        setOrAnimateAlpha(paramView, paramBoolean2, this.mUnfocusedAlpha, -1.0F, this.mDecelerateInterpolator);
        return;
      }
    }
    if (paramInt != 0)
    {
      setOrAnimateAlpha(paramView, paramBoolean2, this.mVisibleColumnAlpha, -1.0F, this.mDecelerateInterpolator);
      return;
    }
    setOrAnimateAlpha(paramView, paramBoolean2, this.mInvisibleColumnAlpha, -1.0F, this.mDecelerateInterpolator);
  }
  
  public final void setPickerItemTextViewId(int paramInt)
  {
    this.mPickerItemTextViewId = paramInt;
  }
  
  public void setSelectedColumn(int paramInt)
  {
    if (this.mSelectedColumn != paramInt)
    {
      this.mSelectedColumn = paramInt;
      paramInt = 0;
      while (paramInt < this.mColumnViews.size())
      {
        updateColumnAlpha(paramInt, true);
        paramInt += 1;
      }
    }
  }
  
  public final void setSeparator(CharSequence paramCharSequence)
  {
    this.mSeparator = paramCharSequence;
  }
  
  public void setVisibleItemCount(float paramFloat)
  {
    if (paramFloat <= 0.0F) {
      throw new IllegalArgumentException();
    }
    if (this.mVisibleItems != paramFloat)
    {
      this.mVisibleItems = paramFloat;
      if (!isActivated()) {
        updateColumnSize();
      }
    }
  }
  
  void updateColumnAlpha(int paramInt, boolean paramBoolean)
  {
    VerticalGridView localVerticalGridView = (VerticalGridView)this.mColumnViews.get(paramInt);
    int j = localVerticalGridView.getSelectedPosition();
    int i = 0;
    if (i < localVerticalGridView.getAdapter().getItemCount())
    {
      View localView = localVerticalGridView.getLayoutManager().findViewByPosition(i);
      if (localView != null) {
        if (j != i) {
          break label78;
        }
      }
      label78:
      for (boolean bool = true;; bool = false)
      {
        setOrAnimateAlpha(localView, bool, paramInt, paramBoolean);
        i += 1;
        break;
      }
    }
  }
  
  class PickerScrollArrayAdapter
    extends RecyclerView.Adapter<Picker.ViewHolder>
  {
    private final int mColIndex;
    private PickerColumn mData;
    private final int mResource;
    private final int mTextViewResourceId;
    
    PickerScrollArrayAdapter(Context paramContext, int paramInt1, int paramInt2, int paramInt3)
    {
      this.mResource = paramInt1;
      this.mColIndex = paramInt3;
      this.mTextViewResourceId = paramInt2;
      this.mData = ((PickerColumn)Picker.this.mColumns.get(this.mColIndex));
    }
    
    public int getItemCount()
    {
      if (this.mData == null) {
        return 0;
      }
      return this.mData.getCount();
    }
    
    public void onBindViewHolder(Picker.ViewHolder paramViewHolder, int paramInt)
    {
      if ((paramViewHolder.textView != null) && (this.mData != null)) {
        paramViewHolder.textView.setText(this.mData.getLabelFor(this.mData.getMinValue() + paramInt));
      }
      Picker localPicker = Picker.this;
      paramViewHolder = paramViewHolder.itemView;
      if (((VerticalGridView)Picker.this.mColumnViews.get(this.mColIndex)).getSelectedPosition() == paramInt) {}
      for (boolean bool = true;; bool = false)
      {
        localPicker.setOrAnimateAlpha(paramViewHolder, bool, this.mColIndex, false);
        return;
      }
    }
    
    public Picker.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      View localView = LayoutInflater.from(paramViewGroup.getContext()).inflate(this.mResource, paramViewGroup, false);
      if (this.mTextViewResourceId != 0) {}
      for (paramViewGroup = (TextView)localView.findViewById(this.mTextViewResourceId);; paramViewGroup = (TextView)localView) {
        return new Picker.ViewHolder(localView, paramViewGroup);
      }
    }
    
    public void onViewAttachedToWindow(Picker.ViewHolder paramViewHolder)
    {
      paramViewHolder.itemView.setFocusable(Picker.this.isActivated());
    }
  }
  
  public static abstract interface PickerValueListener
  {
    public abstract void onValueChanged(Picker paramPicker, int paramInt);
  }
  
  static class ViewHolder
    extends RecyclerView.ViewHolder
  {
    final TextView textView;
    
    ViewHolder(View paramView, TextView paramTextView)
    {
      super();
      this.textView = paramTextView;
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/picker/Picker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */