package android.support.v17.leanback.app;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v17.leanback.R.id;
import android.support.v17.leanback.R.layout;
import android.support.v17.leanback.widget.ClassPresenterSelector;
import android.support.v17.leanback.widget.DividerPresenter;
import android.support.v17.leanback.widget.DividerRow;
import android.support.v17.leanback.widget.FocusHighlightHelper;
import android.support.v17.leanback.widget.ItemBridgeAdapter;
import android.support.v17.leanback.widget.ItemBridgeAdapter.AdapterListener;
import android.support.v17.leanback.widget.ItemBridgeAdapter.ViewHolder;
import android.support.v17.leanback.widget.ItemBridgeAdapter.Wrapper;
import android.support.v17.leanback.widget.Presenter.ViewHolder;
import android.support.v17.leanback.widget.PresenterSelector;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowHeaderPresenter;
import android.support.v17.leanback.widget.RowHeaderPresenter.ViewHolder;
import android.support.v17.leanback.widget.SectionRow;
import android.support.v17.leanback.widget.VerticalGridView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLayoutChangeListener;
import android.widget.FrameLayout;

public class HeadersSupportFragment
  extends BaseRowSupportFragment
{
  private static final PresenterSelector sHeaderPresenter = new ClassPresenterSelector().addClassPresenter(DividerRow.class, new DividerPresenter()).addClassPresenter(SectionRow.class, new RowHeaderPresenter(R.layout.lb_section_header, false)).addClassPresenter(Row.class, new RowHeaderPresenter(R.layout.lb_header));
  static View.OnLayoutChangeListener sLayoutChangeListener = new View.OnLayoutChangeListener()
  {
    public void onLayoutChange(View paramAnonymousView, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, int paramAnonymousInt4, int paramAnonymousInt5, int paramAnonymousInt6, int paramAnonymousInt7, int paramAnonymousInt8)
    {
      if (paramAnonymousView.getLayoutDirection() == 1) {}
      for (float f = paramAnonymousView.getWidth();; f = 0.0F)
      {
        paramAnonymousView.setPivotX(f);
        paramAnonymousView.setPivotY(paramAnonymousView.getMeasuredHeight() / 2);
        return;
      }
    }
  };
  private final ItemBridgeAdapter.AdapterListener mAdapterListener = new ItemBridgeAdapter.AdapterListener()
  {
    public void onCreate(final ItemBridgeAdapter.ViewHolder paramAnonymousViewHolder)
    {
      View localView = paramAnonymousViewHolder.getViewHolder().view;
      localView.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymous2View)
        {
          if (HeadersSupportFragment.this.mOnHeaderClickedListener != null) {
            HeadersSupportFragment.this.mOnHeaderClickedListener.onHeaderClicked((RowHeaderPresenter.ViewHolder)paramAnonymousViewHolder.getViewHolder(), (Row)paramAnonymousViewHolder.getItem());
          }
        }
      });
      if (HeadersSupportFragment.this.mWrapper != null)
      {
        paramAnonymousViewHolder.itemView.addOnLayoutChangeListener(HeadersSupportFragment.sLayoutChangeListener);
        return;
      }
      localView.addOnLayoutChangeListener(HeadersSupportFragment.sLayoutChangeListener);
    }
  };
  private int mBackgroundColor;
  private boolean mBackgroundColorSet;
  private boolean mHeadersEnabled = true;
  private boolean mHeadersGone = false;
  OnHeaderClickedListener mOnHeaderClickedListener;
  private OnHeaderViewSelectedListener mOnHeaderViewSelectedListener;
  final ItemBridgeAdapter.Wrapper mWrapper = new ItemBridgeAdapter.Wrapper()
  {
    public View createWrapper(View paramAnonymousView)
    {
      return new HeadersSupportFragment.NoOverlappingFrameLayout(paramAnonymousView.getContext());
    }
    
    public void wrap(View paramAnonymousView1, View paramAnonymousView2)
    {
      ((FrameLayout)paramAnonymousView1).addView(paramAnonymousView2);
    }
  };
  
  public HeadersSupportFragment()
  {
    setPresenterSelector(sHeaderPresenter);
    FocusHighlightHelper.setupHeaderItemFocusHighlight(getBridgeAdapter());
  }
  
  private void updateFadingEdgeToBrandColor(int paramInt)
  {
    Drawable localDrawable = getView().findViewById(R.id.fade_out_edge).getBackground();
    if ((localDrawable instanceof GradientDrawable))
    {
      localDrawable.mutate();
      ((GradientDrawable)localDrawable).setColors(new int[] { 0, paramInt });
    }
  }
  
  private void updateListViewVisibility()
  {
    VerticalGridView localVerticalGridView = getVerticalGridView();
    View localView;
    if (localVerticalGridView != null)
    {
      localView = getView();
      if (!this.mHeadersGone) {
        break label49;
      }
    }
    label49:
    for (int i = 8;; i = 0)
    {
      localView.setVisibility(i);
      if (!this.mHeadersGone)
      {
        if (!this.mHeadersEnabled) {
          break;
        }
        localVerticalGridView.setChildrenVisibility(0);
      }
      return;
    }
    localVerticalGridView.setChildrenVisibility(4);
  }
  
  VerticalGridView findGridViewFromRoot(View paramView)
  {
    return (VerticalGridView)paramView.findViewById(R.id.browse_headers);
  }
  
  int getLayoutResourceId()
  {
    return R.layout.lb_headers_fragment;
  }
  
  public boolean isScrolling()
  {
    return getVerticalGridView().getScrollState() != 0;
  }
  
  void onRowSelected(RecyclerView paramRecyclerView, RecyclerView.ViewHolder paramViewHolder, int paramInt1, int paramInt2)
  {
    if (this.mOnHeaderViewSelectedListener != null)
    {
      if ((paramViewHolder != null) && (paramInt1 >= 0))
      {
        paramRecyclerView = (ItemBridgeAdapter.ViewHolder)paramViewHolder;
        this.mOnHeaderViewSelectedListener.onHeaderSelected((RowHeaderPresenter.ViewHolder)paramRecyclerView.getViewHolder(), (Row)paramRecyclerView.getItem());
      }
    }
    else {
      return;
    }
    this.mOnHeaderViewSelectedListener.onHeaderSelected(null, null);
  }
  
  public void onTransitionEnd()
  {
    if (this.mHeadersEnabled)
    {
      VerticalGridView localVerticalGridView = getVerticalGridView();
      if (localVerticalGridView != null)
      {
        localVerticalGridView.setDescendantFocusability(262144);
        if (localVerticalGridView.hasFocus()) {
          localVerticalGridView.requestFocus();
        }
      }
    }
    super.onTransitionEnd();
  }
  
  public void onTransitionStart()
  {
    super.onTransitionStart();
    if (!this.mHeadersEnabled)
    {
      VerticalGridView localVerticalGridView = getVerticalGridView();
      if (localVerticalGridView != null)
      {
        localVerticalGridView.setDescendantFocusability(131072);
        if (localVerticalGridView.hasFocus()) {
          localVerticalGridView.requestFocus();
        }
      }
    }
  }
  
  public void onViewCreated(View paramView, Bundle paramBundle)
  {
    super.onViewCreated(paramView, paramBundle);
    paramView = getVerticalGridView();
    if (paramView == null) {
      return;
    }
    if (this.mBackgroundColorSet)
    {
      paramView.setBackgroundColor(this.mBackgroundColor);
      updateFadingEdgeToBrandColor(this.mBackgroundColor);
    }
    for (;;)
    {
      updateListViewVisibility();
      return;
      paramView = paramView.getBackground();
      if ((paramView instanceof ColorDrawable)) {
        updateFadingEdgeToBrandColor(((ColorDrawable)paramView).getColor());
      }
    }
  }
  
  void setBackgroundColor(int paramInt)
  {
    this.mBackgroundColor = paramInt;
    this.mBackgroundColorSet = true;
    if (getVerticalGridView() != null)
    {
      getVerticalGridView().setBackgroundColor(this.mBackgroundColor);
      updateFadingEdgeToBrandColor(this.mBackgroundColor);
    }
  }
  
  void setHeadersEnabled(boolean paramBoolean)
  {
    this.mHeadersEnabled = paramBoolean;
    updateListViewVisibility();
  }
  
  void setHeadersGone(boolean paramBoolean)
  {
    this.mHeadersGone = paramBoolean;
    updateListViewVisibility();
  }
  
  public void setOnHeaderClickedListener(OnHeaderClickedListener paramOnHeaderClickedListener)
  {
    this.mOnHeaderClickedListener = paramOnHeaderClickedListener;
  }
  
  public void setOnHeaderViewSelectedListener(OnHeaderViewSelectedListener paramOnHeaderViewSelectedListener)
  {
    this.mOnHeaderViewSelectedListener = paramOnHeaderViewSelectedListener;
  }
  
  void updateAdapter()
  {
    super.updateAdapter();
    ItemBridgeAdapter localItemBridgeAdapter = getBridgeAdapter();
    localItemBridgeAdapter.setAdapterListener(this.mAdapterListener);
    localItemBridgeAdapter.setWrapper(this.mWrapper);
  }
  
  static class NoOverlappingFrameLayout
    extends FrameLayout
  {
    public NoOverlappingFrameLayout(Context paramContext)
    {
      super();
    }
    
    public boolean hasOverlappingRendering()
    {
      return false;
    }
  }
  
  public static abstract interface OnHeaderClickedListener
  {
    public abstract void onHeaderClicked(RowHeaderPresenter.ViewHolder paramViewHolder, Row paramRow);
  }
  
  public static abstract interface OnHeaderViewSelectedListener
  {
    public abstract void onHeaderSelected(RowHeaderPresenter.ViewHolder paramViewHolder, Row paramRow);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/app/HeadersSupportFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */