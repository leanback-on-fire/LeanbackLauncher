package android.support.v17.leanback.widget;

import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import java.util.ArrayList;

public class ItemBridgeAdapter
  extends RecyclerView.Adapter
  implements FacetProviderAdapter
{
  static final boolean DEBUG = false;
  static final String TAG = "ItemBridgeAdapter";
  private ObjectAdapter mAdapter;
  private AdapterListener mAdapterListener;
  private ObjectAdapter.DataObserver mDataObserver = new ObjectAdapter.DataObserver()
  {
    public void onChanged()
    {
      ItemBridgeAdapter.this.notifyDataSetChanged();
    }
    
    public void onItemRangeChanged(int paramAnonymousInt1, int paramAnonymousInt2)
    {
      ItemBridgeAdapter.this.notifyItemRangeChanged(paramAnonymousInt1, paramAnonymousInt2);
    }
    
    public void onItemRangeInserted(int paramAnonymousInt1, int paramAnonymousInt2)
    {
      ItemBridgeAdapter.this.notifyItemRangeInserted(paramAnonymousInt1, paramAnonymousInt2);
    }
    
    public void onItemRangeRemoved(int paramAnonymousInt1, int paramAnonymousInt2)
    {
      ItemBridgeAdapter.this.notifyItemRangeRemoved(paramAnonymousInt1, paramAnonymousInt2);
    }
  };
  FocusHighlightHandler mFocusHighlight;
  private PresenterSelector mPresenterSelector;
  private ArrayList<Presenter> mPresenters = new ArrayList();
  Wrapper mWrapper;
  
  public ItemBridgeAdapter() {}
  
  public ItemBridgeAdapter(ObjectAdapter paramObjectAdapter)
  {
    this(paramObjectAdapter, null);
  }
  
  public ItemBridgeAdapter(ObjectAdapter paramObjectAdapter, PresenterSelector paramPresenterSelector)
  {
    setAdapter(paramObjectAdapter);
    this.mPresenterSelector = paramPresenterSelector;
  }
  
  public void clear()
  {
    setAdapter(null);
  }
  
  public FacetProvider getFacetProvider(int paramInt)
  {
    return (FacetProvider)this.mPresenters.get(paramInt);
  }
  
  public int getItemCount()
  {
    if (this.mAdapter != null) {
      return this.mAdapter.size();
    }
    return 0;
  }
  
  public long getItemId(int paramInt)
  {
    return this.mAdapter.getId(paramInt);
  }
  
  public int getItemViewType(int paramInt)
  {
    if (this.mPresenterSelector != null) {}
    for (Object localObject = this.mPresenterSelector;; localObject = this.mAdapter.getPresenterSelector())
    {
      localObject = ((PresenterSelector)localObject).getPresenter(this.mAdapter.get(paramInt));
      int i = this.mPresenters.indexOf(localObject);
      paramInt = i;
      if (i < 0)
      {
        this.mPresenters.add(localObject);
        i = this.mPresenters.indexOf(localObject);
        onAddPresenter((Presenter)localObject, i);
        paramInt = i;
        if (this.mAdapterListener != null)
        {
          this.mAdapterListener.onAddPresenter((Presenter)localObject, i);
          paramInt = i;
        }
      }
      return paramInt;
    }
  }
  
  public ArrayList<Presenter> getPresenterMapper()
  {
    return this.mPresenters;
  }
  
  public Wrapper getWrapper()
  {
    return this.mWrapper;
  }
  
  protected void onAddPresenter(Presenter paramPresenter, int paramInt) {}
  
  protected void onAttachedToWindow(ViewHolder paramViewHolder) {}
  
  protected void onBind(ViewHolder paramViewHolder) {}
  
  public final void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
  {
    paramViewHolder = (ViewHolder)paramViewHolder;
    paramViewHolder.mItem = this.mAdapter.get(paramInt);
    paramViewHolder.mPresenter.onBindViewHolder(paramViewHolder.mHolder, paramViewHolder.mItem);
    onBind(paramViewHolder);
    if (this.mAdapterListener != null) {
      this.mAdapterListener.onBind(paramViewHolder);
    }
  }
  
  protected void onCreate(ViewHolder paramViewHolder) {}
  
  public final RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
  {
    Presenter localPresenter = (Presenter)this.mPresenters.get(paramInt);
    Object localObject1;
    Object localObject2;
    if (this.mWrapper != null)
    {
      localObject1 = this.mWrapper.createWrapper(paramViewGroup);
      localObject2 = localPresenter.onCreateViewHolder(paramViewGroup);
      this.mWrapper.wrap((View)localObject1, ((Presenter.ViewHolder)localObject2).view);
      paramViewGroup = (ViewGroup)localObject1;
      localObject1 = localObject2;
    }
    for (;;)
    {
      localObject1 = new ViewHolder(localPresenter, paramViewGroup, (Presenter.ViewHolder)localObject1);
      onCreate((ViewHolder)localObject1);
      if (this.mAdapterListener != null) {
        this.mAdapterListener.onCreate((ViewHolder)localObject1);
      }
      localObject2 = ((ViewHolder)localObject1).mHolder.view;
      if (localObject2 != null)
      {
        ((ViewHolder)localObject1).mFocusChangeListener.mChainedListener = ((View)localObject2).getOnFocusChangeListener();
        ((View)localObject2).setOnFocusChangeListener(((ViewHolder)localObject1).mFocusChangeListener);
      }
      if (this.mFocusHighlight != null) {
        this.mFocusHighlight.onInitializeView(paramViewGroup);
      }
      return (RecyclerView.ViewHolder)localObject1;
      localObject1 = localPresenter.onCreateViewHolder(paramViewGroup);
      paramViewGroup = ((Presenter.ViewHolder)localObject1).view;
    }
  }
  
  protected void onDetachedFromWindow(ViewHolder paramViewHolder) {}
  
  protected void onUnbind(ViewHolder paramViewHolder) {}
  
  public final void onViewAttachedToWindow(RecyclerView.ViewHolder paramViewHolder)
  {
    paramViewHolder = (ViewHolder)paramViewHolder;
    onAttachedToWindow(paramViewHolder);
    if (this.mAdapterListener != null) {
      this.mAdapterListener.onAttachedToWindow(paramViewHolder);
    }
    paramViewHolder.mPresenter.onViewAttachedToWindow(paramViewHolder.mHolder);
  }
  
  public final void onViewDetachedFromWindow(RecyclerView.ViewHolder paramViewHolder)
  {
    paramViewHolder = (ViewHolder)paramViewHolder;
    paramViewHolder.mPresenter.onViewDetachedFromWindow(paramViewHolder.mHolder);
    onDetachedFromWindow(paramViewHolder);
    if (this.mAdapterListener != null) {
      this.mAdapterListener.onDetachedFromWindow(paramViewHolder);
    }
  }
  
  public final void onViewRecycled(RecyclerView.ViewHolder paramViewHolder)
  {
    paramViewHolder = (ViewHolder)paramViewHolder;
    paramViewHolder.mPresenter.onUnbindViewHolder(paramViewHolder.mHolder);
    onUnbind(paramViewHolder);
    if (this.mAdapterListener != null) {
      this.mAdapterListener.onUnbind(paramViewHolder);
    }
    paramViewHolder.mItem = null;
  }
  
  public void setAdapter(ObjectAdapter paramObjectAdapter)
  {
    if (paramObjectAdapter == this.mAdapter) {
      return;
    }
    if (this.mAdapter != null) {
      this.mAdapter.unregisterObserver(this.mDataObserver);
    }
    this.mAdapter = paramObjectAdapter;
    if (this.mAdapter == null)
    {
      notifyDataSetChanged();
      return;
    }
    this.mAdapter.registerObserver(this.mDataObserver);
    if (hasStableIds() != this.mAdapter.hasStableIds()) {
      setHasStableIds(this.mAdapter.hasStableIds());
    }
    notifyDataSetChanged();
  }
  
  public void setAdapterListener(AdapterListener paramAdapterListener)
  {
    this.mAdapterListener = paramAdapterListener;
  }
  
  void setFocusHighlight(FocusHighlightHandler paramFocusHighlightHandler)
  {
    this.mFocusHighlight = paramFocusHighlightHandler;
  }
  
  public void setPresenter(PresenterSelector paramPresenterSelector)
  {
    this.mPresenterSelector = paramPresenterSelector;
    notifyDataSetChanged();
  }
  
  public void setPresenterMapper(ArrayList<Presenter> paramArrayList)
  {
    this.mPresenters = paramArrayList;
  }
  
  public void setWrapper(Wrapper paramWrapper)
  {
    this.mWrapper = paramWrapper;
  }
  
  public static class AdapterListener
  {
    public void onAddPresenter(Presenter paramPresenter, int paramInt) {}
    
    public void onAttachedToWindow(ItemBridgeAdapter.ViewHolder paramViewHolder) {}
    
    public void onBind(ItemBridgeAdapter.ViewHolder paramViewHolder) {}
    
    public void onCreate(ItemBridgeAdapter.ViewHolder paramViewHolder) {}
    
    public void onDetachedFromWindow(ItemBridgeAdapter.ViewHolder paramViewHolder) {}
    
    public void onUnbind(ItemBridgeAdapter.ViewHolder paramViewHolder) {}
  }
  
  final class OnFocusChangeListener
    implements View.OnFocusChangeListener
  {
    View.OnFocusChangeListener mChainedListener;
    
    OnFocusChangeListener() {}
    
    public void onFocusChange(View paramView, boolean paramBoolean)
    {
      View localView = paramView;
      if (ItemBridgeAdapter.this.mWrapper != null) {
        localView = (View)paramView.getParent();
      }
      if (ItemBridgeAdapter.this.mFocusHighlight != null) {
        ItemBridgeAdapter.this.mFocusHighlight.onItemFocused(localView, paramBoolean);
      }
      if (this.mChainedListener != null) {
        this.mChainedListener.onFocusChange(localView, paramBoolean);
      }
    }
  }
  
  public class ViewHolder
    extends RecyclerView.ViewHolder
    implements FacetProvider
  {
    Object mExtraObject;
    final ItemBridgeAdapter.OnFocusChangeListener mFocusChangeListener = new ItemBridgeAdapter.OnFocusChangeListener(ItemBridgeAdapter.this);
    final Presenter.ViewHolder mHolder;
    Object mItem;
    final Presenter mPresenter;
    
    ViewHolder(Presenter paramPresenter, View paramView, Presenter.ViewHolder paramViewHolder)
    {
      super();
      this.mPresenter = paramPresenter;
      this.mHolder = paramViewHolder;
    }
    
    public final Object getExtraObject()
    {
      return this.mExtraObject;
    }
    
    public Object getFacet(Class<?> paramClass)
    {
      return this.mHolder.getFacet(paramClass);
    }
    
    public final Object getItem()
    {
      return this.mItem;
    }
    
    public final Presenter getPresenter()
    {
      return this.mPresenter;
    }
    
    public final Presenter.ViewHolder getViewHolder()
    {
      return this.mHolder;
    }
    
    public void setExtraObject(Object paramObject)
    {
      this.mExtraObject = paramObject;
    }
  }
  
  public static abstract class Wrapper
  {
    public abstract View createWrapper(View paramView);
    
    public abstract void wrap(View paramView1, View paramView2);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/ItemBridgeAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */