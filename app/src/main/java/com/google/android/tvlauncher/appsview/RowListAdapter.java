package com.google.android.tvlauncher.appsview;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.support.v17.leanback.widget.FacetProvider;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.support.v17.leanback.widget.ItemAlignmentFacet;
import android.support.v17.leanback.widget.ItemAlignmentFacet.ItemAlignmentDef;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.AdapterDataObserver;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.android.tvlauncher.analytics.EventLogger;
import com.google.android.tvlauncher.analytics.LogEvent;
import com.google.android.tvlauncher.analytics.LogEventParameters;
import com.google.android.tvlauncher.data.PromoChannelObserver;
import com.google.android.tvlauncher.data.TvDataManager;
import com.google.android.tvlauncher.model.Channel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

class RowListAdapter
  extends RecyclerView.Adapter<BaseViewHolder>
  implements AppsManager.AppsViewChangeListener
{
  private static final int ADS_ROW_POSITION = 0;
  private static final int OEM_ROW_TITLE_POSITION = 1;
  private static final int ROW_TYPE_ADS = 4;
  private static final int ROW_TYPE_APPS = 1;
  private static final int ROW_TYPE_GAMES = 2;
  private static final int ROW_TYPE_OEM = 3;
  private static final int ROW_TYPE_STORE = 5;
  static final int ROW_TYPE_TITLE = 6;
  private static final String TAG = "RowListAdapter";
  private final LaunchItemsHolder mAppLaunchItems = new LaunchItemsHolder();
  private final AppsManager mAppsManager;
  private Handler mChangeHandler = new Handler();
  private final TvDataManager mDataManager;
  private final EventLogger mEventLogger;
  private final LaunchItemsHolder mGameLaunchItems = new LaunchItemsHolder();
  private final int mKeylineOffsetOne;
  private final int mKeylineOffsetThree;
  private final int mKeylineOffsetTwo;
  private final ArrayList<LaunchItem> mOemLaunchItems = new ArrayList();
  private OnAppsViewActionListener mOnAppsViewActionListener;
  private AppsViewFragment.OnEditModeOrderChangeCallback mOnEditModeOrderChangeCallback;
  private final PromoChannelObserver mPromoChannelObserver = new PromoChannelObserver()
  {
    public void onChannelChange()
    {
      RowListAdapter.this.notifyItemChanged(0);
    }
  };
  private PromotionRowAdapter mPromotionRowAdapter;
  private final ArrayList<Integer> mRows = new ArrayList();
  private final int mStoreKeylineOffset;
  private final ArrayList<LaunchItem> mStoreLaunchItems = new ArrayList();
  
  public RowListAdapter(Context paramContext, EventLogger paramEventLogger)
  {
    this.mEventLogger = paramEventLogger;
    this.mDataManager = TvDataManager.getInstance(paramContext);
    this.mDataManager.registerPromoChannelObserver(this.mPromoChannelObserver);
    if (!this.mDataManager.isPromoChannelLoaded()) {
      this.mDataManager.loadPromoChannel();
    }
    paramEventLogger = paramContext.getResources();
    this.mKeylineOffsetOne = paramEventLogger.getDimensionPixelOffset(2131558492);
    this.mKeylineOffsetTwo = paramEventLogger.getDimensionPixelOffset(2131558494);
    this.mKeylineOffsetThree = paramEventLogger.getDimensionPixelOffset(2131558493);
    this.mStoreKeylineOffset = paramEventLogger.getDimensionPixelOffset(2131558495);
    this.mAppsManager = AppsManager.getInstance(paramContext);
  }
  
  private void addLaunchItemsToViewHolder(BaseViewHolder paramBaseViewHolder, int paramInt)
  {
    int i = ((Integer)this.mRows.get(paramInt)).intValue();
    paramInt = getPositionRelativeToTitle(paramInt);
    if ((paramInt < 0) || (paramInt >= this.mRows.size())) {
      Log.e("RowListAdapter", "RowListAdapter: Title relative position was out of bounds : " + paramInt + ", in addLauncItemToViewHolder()");
    }
    Object localObject;
    do
    {
      return;
      switch (i)
      {
      default: 
        return;
      case 1: 
        localObject = this.mAppLaunchItems.getRowData(paramInt);
      }
    } while (!(paramBaseViewHolder instanceof AppViewHolder));
    paramBaseViewHolder = (AppViewHolder)paramBaseViewHolder;
    if (i == 3) {}
    for (boolean bool = true;; bool = false)
    {
      paramBaseViewHolder.addAllLaunchItems((List)localObject, bool);
      return;
      localObject = this.mGameLaunchItems.getRowData(paramInt);
      break;
      localObject = this.mOemLaunchItems;
      break;
    }
  }
  
  private int getKeylineForPosition(int paramInt)
  {
    if ((paramInt < 0) || (paramInt > this.mRows.size() - 1)) {
      return this.mKeylineOffsetOne;
    }
    int i = getItemCount();
    int j = ((Integer)this.mRows.get(paramInt)).intValue();
    if ((paramInt == i - 1) && (i > 3)) {
      return this.mKeylineOffsetThree;
    }
    if ((paramInt == i - 1) && (i <= 3)) {
      return this.mKeylineOffsetTwo;
    }
    if ((paramInt == i - 2) && (j != 6)) {
      return this.mKeylineOffsetTwo;
    }
    if ((paramInt == i - 3) && (((Integer)this.mRows.get(paramInt + 1)).intValue() == 6)) {
      return this.mKeylineOffsetTwo;
    }
    return this.mKeylineOffsetOne;
  }
  
  private int getPositionRelativeToTitle(int paramInt)
  {
    int i = ((Integer)this.mRows.get(paramInt)).intValue();
    if ((i == 4) || (i == 3)) {
      return -1;
    }
    i = paramInt;
    while (i >= 0)
    {
      if (((Integer)this.mRows.get(i)).intValue() == 6) {
        return paramInt - i - 1;
      }
      i -= 1;
    }
    return -1;
  }
  
  private void onBindChannel(AdsViewHolder paramAdsViewHolder)
  {
    if (this.mDataManager.isPromoChannelLoaded())
    {
      Channel localChannel = this.mDataManager.getPromoChannel();
      if (localChannel != null)
      {
        paramAdsViewHolder.itemView.setVisibility(0);
        ((PromotionRowAdapter)paramAdsViewHolder.mRecyclerView.getAdapter()).setChannelId(localChannel.getId());
        return;
      }
      paramAdsViewHolder.itemView.setVisibility(8);
      return;
    }
    this.mDataManager.loadPromoChannel();
    paramAdsViewHolder.itemView.setVisibility(8);
  }
  
  int getBottomKeylineForEditMode(int paramInt)
  {
    if (paramInt == 0) {
      return getKeylineForPosition(this.mRows.lastIndexOf(Integer.valueOf(1)));
    }
    return getKeylineForPosition(this.mRows.lastIndexOf(Integer.valueOf(2)));
  }
  
  public int getItemCount()
  {
    return this.mRows.size();
  }
  
  public int getItemViewType(int paramInt)
  {
    return ((Integer)this.mRows.get(paramInt)).intValue();
  }
  
  int getTopKeylineForEditMode(int paramInt)
  {
    if (paramInt == 0) {
      return getKeylineForPosition(this.mRows.indexOf(Integer.valueOf(1)));
    }
    return getKeylineForPosition(this.mRows.indexOf(Integer.valueOf(2)));
  }
  
  void initRows()
  {
    if (!this.mAppsManager.areItemsLoaded()) {
      return;
    }
    this.mRows.clear();
    if (this.mStoreLaunchItems.size() > 0) {
      this.mRows.add(Integer.valueOf(5));
    }
    if (this.mOemLaunchItems.size() > 0)
    {
      this.mRows.add(Integer.valueOf(6));
      this.mRows.add(Integer.valueOf(3));
    }
    int i;
    if (this.mAppLaunchItems.size() > 0)
    {
      this.mRows.add(Integer.valueOf(6));
      i = 0;
      while (i < this.mAppLaunchItems.getNumRows())
      {
        this.mRows.add(Integer.valueOf(1));
        i += 1;
      }
    }
    if (this.mGameLaunchItems.size() > 0)
    {
      this.mRows.add(Integer.valueOf(6));
      i = 0;
      while (i < this.mGameLaunchItems.getNumRows())
      {
        this.mRows.add(Integer.valueOf(2));
        i += 1;
      }
    }
    notifyDataSetChanged();
  }
  
  public void onBindViewHolder(BaseViewHolder paramBaseViewHolder, int paramInt)
  {
    if ((paramBaseViewHolder instanceof StoreViewHolder)) {
      ((StoreViewHolder)paramBaseViewHolder).addStoreItems(this.mStoreLaunchItems);
    }
    if ((paramBaseViewHolder instanceof AppViewHolder)) {
      addLaunchItemsToViewHolder(paramBaseViewHolder, paramInt);
    }
    if ((paramBaseViewHolder instanceof TitleViewHolder))
    {
      paramInt += 1;
      if (paramInt < this.mRows.size()) {
        break label83;
      }
      ((TitleViewHolder)paramBaseViewHolder).setTitle("");
    }
    for (;;)
    {
      if ((paramBaseViewHolder instanceof AdsViewHolder)) {
        onBindChannel((AdsViewHolder)paramBaseViewHolder);
      }
      paramBaseViewHolder.set();
      return;
      label83:
      Resources localResources = paramBaseViewHolder.itemView.getContext().getResources();
      switch (((Integer)this.mRows.get(paramInt)).intValue())
      {
      default: 
        ((TitleViewHolder)paramBaseViewHolder).setTitle("");
        break;
      case 1: 
        ((TitleViewHolder)paramBaseViewHolder).setTitle(localResources.getString(2131492894));
        break;
      case 2: 
        ((TitleViewHolder)paramBaseViewHolder).setTitle(localResources.getString(2131492995));
        break;
      case 3: 
        ((TitleViewHolder)paramBaseViewHolder).setTitle(localResources.getString(2131493051));
      }
    }
  }
  
  public BaseViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
  {
    switch (paramInt)
    {
    default: 
      throw new IllegalArgumentException("Unexpected row type : " + paramInt);
    case 5: 
      return new StoreViewHolder(LayoutInflater.from(paramViewGroup.getContext()).inflate(2130968613, paramViewGroup, false));
    case 4: 
      return new AdsViewHolder(LayoutInflater.from(paramViewGroup.getContext()).inflate(2130968610, paramViewGroup, false));
    case 1: 
    case 2: 
    case 3: 
      return new AppViewHolder(LayoutInflater.from(paramViewGroup.getContext()).inflate(2130968611, paramViewGroup, false));
    }
    return new TitleViewHolder(LayoutInflater.from(paramViewGroup.getContext()).inflate(2130968738, paramViewGroup, false));
  }
  
  public void onEditModeItemOrderChange(ArrayList<LaunchItem> paramArrayList, boolean paramBoolean, Pair<Integer, Integer> paramPair)
  {
    if (paramArrayList != null)
    {
      if (paramBoolean)
      {
        setGameLaunchItems(paramArrayList);
        initRows();
      }
    }
    else
    {
      paramArrayList = this.mRows;
      if (!paramBoolean) {
        break label100;
      }
    }
    label100:
    for (int i = 2;; i = 1)
    {
      i = paramArrayList.indexOf(Integer.valueOf(i));
      if ((paramPair != null) && (i != -1) && (this.mOnEditModeOrderChangeCallback != null))
      {
        int j = ((Integer)paramPair.first).intValue();
        this.mOnEditModeOrderChangeCallback.onEditModeExited(i + j, ((Integer)paramPair.second).intValue());
      }
      return;
      setAppLaunchItems(paramArrayList);
      break;
    }
  }
  
  public void onLaunchItemsAddedOrUpdated(final ArrayList<LaunchItem> paramArrayList)
  {
    this.mChangeHandler.post(new Runnable()
    {
      private boolean checkItemHolders(LaunchItemsHolder paramAnonymousLaunchItemsHolder, LaunchItem paramAnonymousLaunchItem, int paramAnonymousInt)
      {
        Pair localPair = paramAnonymousLaunchItemsHolder.findIndex(paramAnonymousLaunchItem);
        boolean bool2 = false;
        boolean bool3 = paramAnonymousLaunchItem.isGame();
        boolean bool1;
        int i;
        label34:
        label61:
        int j;
        if (paramAnonymousInt == 2)
        {
          bool1 = true;
          if (bool3 != bool1) {
            break label155;
          }
          i = 1;
          if (localPair == null) {
            break label236;
          }
          if (i != 0) {
            break label185;
          }
          i = paramAnonymousLaunchItemsHolder.getNumRows();
          if (paramAnonymousLaunchItemsHolder.removeItem(paramAnonymousLaunchItem) == null) {
            break label161;
          }
          bool1 = true;
          j = RowListAdapter.this.mRows.indexOf(Integer.valueOf(paramAnonymousInt)) + ((Integer)localPair.first).intValue();
          paramAnonymousInt = RowListAdapter.this.mRows.lastIndexOf(Integer.valueOf(paramAnonymousInt));
          if (paramAnonymousLaunchItemsHolder.getNumRows() >= i) {
            break label167;
          }
          RowListAdapter.this.notifyItemRangeChanged(j, paramAnonymousInt - j);
          RowListAdapter.this.mRows.remove(paramAnonymousInt);
          RowListAdapter.this.notifyItemRemoved(paramAnonymousInt);
        }
        for (;;)
        {
          return bool1;
          bool1 = false;
          break;
          label155:
          i = 0;
          break label34;
          label161:
          bool1 = false;
          break label61;
          label167:
          RowListAdapter.this.notifyItemRangeChanged(j, paramAnonymousInt - j + 1);
          continue;
          label185:
          paramAnonymousLaunchItemsHolder.set(localPair, paramAnonymousLaunchItem);
          paramAnonymousLaunchItemsHolder = RowListAdapter.this;
          paramAnonymousInt = RowListAdapter.this.mRows.indexOf(Integer.valueOf(paramAnonymousInt));
          paramAnonymousLaunchItemsHolder.notifyItemChanged(((Integer)localPair.first).intValue() + paramAnonymousInt);
          bool1 = bool2;
          continue;
          label236:
          bool1 = bool2;
          if (i != 0)
          {
            i = ((Integer)paramAnonymousLaunchItemsHolder.addItemAtIndexElseEnd(RowListAdapter.this.mAppsManager.getOrderedPosition(paramAnonymousLaunchItem), paramAnonymousLaunchItem).first).intValue();
            bool1 = true;
            j = RowListAdapter.this.mRows.indexOf(Integer.valueOf(paramAnonymousInt));
            int k = RowListAdapter.this.mRows.lastIndexOf(Integer.valueOf(paramAnonymousInt));
            int m = i + j;
            if (j == -1)
            {
              switch (paramAnonymousInt)
              {
              default: 
                i = j;
              }
              for (;;)
              {
                RowListAdapter.this.mRows.add(i, Integer.valueOf(paramAnonymousInt));
                RowListAdapter.this.notifyItemInserted(i - 1);
                return true;
                RowListAdapter.this.mRows.add(1, Integer.valueOf(6));
                RowListAdapter.this.notifyItemInserted(1);
                i = 2;
                continue;
                if (RowListAdapter.this.mRows.indexOf(Integer.valueOf(2)) == -1)
                {
                  RowListAdapter.this.mRows.add(Integer.valueOf(6));
                  RowListAdapter.this.notifyItemInserted(RowListAdapter.this.mRows.size() - 1);
                  i = RowListAdapter.this.mRows.size();
                }
                else
                {
                  i = RowListAdapter.this.mRows.indexOf(Integer.valueOf(2)) - 1;
                  RowListAdapter.this.mRows.add(i, Integer.valueOf(6));
                  RowListAdapter.this.notifyItemInserted(i);
                  i += 1;
                  continue;
                  RowListAdapter.this.mRows.add(Integer.valueOf(6));
                  RowListAdapter.this.notifyItemInserted(RowListAdapter.this.mRows.size() - 2);
                  i = RowListAdapter.this.mRows.size();
                }
              }
            }
            i = k;
            if (k - j + 1 < paramAnonymousLaunchItemsHolder.getNumRows())
            {
              RowListAdapter.this.mRows.add(k + 1, Integer.valueOf(paramAnonymousInt));
              i = k + 1;
            }
            if (m != i) {
              RowListAdapter.this.notifyItemRangeChanged(m, i - m + 1);
            } else {
              RowListAdapter.this.notifyItemChanged(i);
            }
          }
        }
      }
      
      public void run()
      {
        int i = 0;
        Iterator localIterator = paramArrayList.iterator();
        if (localIterator.hasNext())
        {
          LaunchItem localLaunchItem = (LaunchItem)localIterator.next();
          if ((i != 0) || (checkItemHolders(RowListAdapter.this.mAppLaunchItems, localLaunchItem, 1)) || (checkItemHolders(RowListAdapter.this.mGameLaunchItems, localLaunchItem, 2))) {}
          for (i = 1;; i = 0) {
            break;
          }
        }
        if ((i != 0) && (paramArrayList.size() > 0))
        {
          if (((LaunchItem)paramArrayList.get(0)).isGame()) {
            RowListAdapter.this.mAppsManager.saveOrderSnapshot(RowListAdapter.this.mGameLaunchItems.getData());
          }
        }
        else {
          return;
        }
        RowListAdapter.this.mAppsManager.saveOrderSnapshot(RowListAdapter.this.mAppLaunchItems.getData());
      }
    });
  }
  
  public void onLaunchItemsLoaded()
  {
    this.mAppLaunchItems.setData(this.mAppsManager.getAppLaunchItems());
    this.mGameLaunchItems.setData(this.mAppsManager.getGameLaunchItems());
    this.mOemLaunchItems.clear();
    this.mOemLaunchItems.addAll(this.mAppsManager.getOemLaunchItems());
    this.mStoreLaunchItems.clear();
    this.mStoreLaunchItems.add(this.mAppsManager.getAppStoreLaunchItem());
    this.mStoreLaunchItems.add(this.mAppsManager.getGameStoreLaunchItem());
    this.mStoreLaunchItems.removeAll(Collections.singleton(null));
    initRows();
    this.mEventLogger.log(new LogEventParameters("open_apps_view").putParameter("app_count", this.mAppLaunchItems.size()).putParameter("game_count", this.mGameLaunchItems.size()));
  }
  
  public void onLaunchItemsRemoved(final ArrayList<LaunchItem> paramArrayList)
  {
    this.mChangeHandler.post(new Runnable()
    {
      private void removeItemFromHolder(LaunchItemsHolder paramAnonymousLaunchItemsHolder, LaunchItem paramAnonymousLaunchItem, int paramAnonymousInt)
      {
        int j = paramAnonymousLaunchItemsHolder.getNumRows();
        paramAnonymousLaunchItem = paramAnonymousLaunchItemsHolder.removeItem(paramAnonymousLaunchItem);
        if (paramAnonymousLaunchItem == null) {
          return;
        }
        int i = RowListAdapter.this.mRows.indexOf(Integer.valueOf(paramAnonymousInt)) + ((Integer)paramAnonymousLaunchItem.first).intValue();
        paramAnonymousInt = RowListAdapter.this.mRows.lastIndexOf(Integer.valueOf(paramAnonymousInt));
        if (j > paramAnonymousLaunchItemsHolder.getNumRows())
        {
          RowListAdapter.this.mRows.remove(paramAnonymousInt);
          RowListAdapter.this.notifyItemRemoved(paramAnonymousInt);
          j = RowListAdapter.this.mRows.size() - 1;
          if (((Integer)RowListAdapter.this.mRows.get(j)).intValue() == 6)
          {
            RowListAdapter.this.mRows.remove(j);
            RowListAdapter.this.notifyItemRemoved(j);
          }
          RowListAdapter.this.notifyItemRangeChanged(i, paramAnonymousInt - i);
          return;
        }
        RowListAdapter.this.notifyItemRangeChanged(i, paramAnonymousInt - i + 1);
      }
      
      public void run()
      {
        Iterator localIterator = paramArrayList.iterator();
        while (localIterator.hasNext())
        {
          LaunchItem localLaunchItem = (LaunchItem)localIterator.next();
          if (localLaunchItem.isGame()) {
            removeItemFromHolder(RowListAdapter.this.mGameLaunchItems, localLaunchItem, 2);
          } else {
            removeItemFromHolder(RowListAdapter.this.mAppLaunchItems, localLaunchItem, 1);
          }
        }
      }
    });
  }
  
  public void onStart()
  {
    this.mDataManager.registerPromoChannelObserver(this.mPromoChannelObserver);
    if (this.mPromotionRowAdapter != null) {
      this.mPromotionRowAdapter.onStart();
    }
  }
  
  public void onStop()
  {
    if (this.mPromotionRowAdapter != null) {
      this.mPromotionRowAdapter.onStop();
    }
    this.mDataManager.unregisterPromoChannelObserver(this.mPromoChannelObserver);
  }
  
  void setAppLaunchItems(ArrayList<LaunchItem> paramArrayList)
  {
    this.mAppLaunchItems.setData(paramArrayList);
  }
  
  void setGameLaunchItems(ArrayList<LaunchItem> paramArrayList)
  {
    this.mGameLaunchItems.setData(paramArrayList);
  }
  
  void setOemLaunchItems(ArrayList<LaunchItem> paramArrayList)
  {
    this.mOemLaunchItems.clear();
    this.mOemLaunchItems.addAll(paramArrayList);
  }
  
  void setOnAppsViewActionListener(OnAppsViewActionListener paramOnAppsViewActionListener)
  {
    this.mOnAppsViewActionListener = paramOnAppsViewActionListener;
  }
  
  void setOnEditModeOrderChangeCallback(AppsViewFragment.OnEditModeOrderChangeCallback paramOnEditModeOrderChangeCallback)
  {
    this.mOnEditModeOrderChangeCallback = paramOnEditModeOrderChangeCallback;
  }
  
  private class AdsViewHolder
    extends RowListAdapter.BaseViewHolder
  {
    private final RecyclerView.AdapterDataObserver mDataObserver = new RecyclerView.AdapterDataObserver()
    {
      public void onChanged()
      {
        super.onChanged();
        View localView;
        if (RowListAdapter.AdsViewHolder.this.mRecyclerView != null)
        {
          localView = RowListAdapter.AdsViewHolder.this.itemView;
          if (RowListAdapter.AdsViewHolder.this.mRecyclerView.getAdapter().getItemCount() >= 1) {
            break label48;
          }
        }
        label48:
        for (int i = 8;; i = 0)
        {
          localView.setVisibility(i);
          return;
        }
      }
    };
    private HorizontalGridView mRecyclerView;
    
    AdsViewHolder(View paramView)
    {
      super(paramView);
      this.mRecyclerView = ((HorizontalGridView)paramView.findViewById(2131951790));
      RowListAdapter.access$802(RowListAdapter.this, new PromotionRowAdapter(paramView.getContext()));
      RowListAdapter.this.mPromotionRowAdapter.registerAdapterDataObserver(this.mDataObserver);
      this.mRecyclerView.setAdapter(RowListAdapter.this.mPromotionRowAdapter);
    }
  }
  
  private class AppViewHolder
    extends RowListAdapter.BaseViewHolder
  {
    private boolean mIsOem;
    private final ArrayList<LaunchItem> mLaunchItems = new ArrayList();
    
    AppViewHolder(View paramView)
    {
      super(paramView);
    }
    
    void addAllLaunchItems(List<LaunchItem> paramList, boolean paramBoolean)
    {
      this.mLaunchItems.clear();
      this.mLaunchItems.addAll(paramList);
      this.mIsOem = paramBoolean;
    }
    
    protected int calculateOffset()
    {
      int i = getAdapterPosition();
      return RowListAdapter.this.getKeylineForPosition(i);
    }
    
    public void set()
    {
      super.set();
      ((AppRowView)this.itemView).setOnAppsViewActionListener(RowListAdapter.this.mOnAppsViewActionListener);
      ((AppRowView)this.itemView).addBannerViews(this.mLaunchItems, this.mIsOem);
    }
  }
  
  class BaseViewHolder
    extends RecyclerView.ViewHolder
    implements FacetProvider
  {
    BaseViewHolder(View paramView)
    {
      super();
    }
    
    protected int calculateOffset()
    {
      return 0;
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
    
    public void set() {}
  }
  
  private class StoreViewHolder
    extends RowListAdapter.BaseViewHolder
  {
    private List<LaunchItem> mItems;
    
    StoreViewHolder(View paramView)
    {
      super(paramView);
    }
    
    void addStoreItems(List<LaunchItem> paramList)
    {
      this.mItems = paramList;
    }
    
    protected int calculateOffset()
    {
      return RowListAdapter.this.mStoreKeylineOffset;
    }
    
    public void set()
    {
      super.set();
      StoreRowButtonView localStoreRowButtonView1 = (StoreRowButtonView)this.itemView.findViewById(2131951793);
      StoreRowButtonView localStoreRowButtonView2 = (StoreRowButtonView)this.itemView.findViewById(2131951794);
      Iterator localIterator = this.mItems.iterator();
      while (localIterator.hasNext())
      {
        LaunchItem localLaunchItem = (LaunchItem)localIterator.next();
        if (AppsManager.checkIfAppStore(localLaunchItem.getPackageName()))
        {
          localStoreRowButtonView1.setStoreItem(localLaunchItem, RowListAdapter.this.mOnAppsViewActionListener);
          localStoreRowButtonView1.setVisibility(0);
        }
        else if (AppsManager.checkIfGameStore(localLaunchItem.getPackageName()))
        {
          localStoreRowButtonView2.setStoreItem(localLaunchItem, RowListAdapter.this.mOnAppsViewActionListener);
          localStoreRowButtonView2.setVisibility(0);
        }
        else
        {
          Log.e("RowListAdapter", "Trying to add an app to store button that is not a store.");
        }
      }
    }
  }
  
  private class TitleViewHolder
    extends RowListAdapter.BaseViewHolder
  {
    private String mTitle;
    
    TitleViewHolder(View paramView)
    {
      super(paramView);
    }
    
    public void set()
    {
      ((TextView)this.itemView).setText(this.mTitle);
    }
    
    public void setTitle(String paramString)
    {
      this.mTitle = paramString;
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/appsview/RowListAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */