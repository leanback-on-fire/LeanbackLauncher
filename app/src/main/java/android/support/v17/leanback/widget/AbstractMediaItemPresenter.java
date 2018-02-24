package android.support.v17.leanback.widget;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.graphics.Rect;
import android.support.v17.leanback.R.attr;
import android.support.v17.leanback.R.id;
import android.support.v17.leanback.R.layout;
import android.support.v4.view.ViewCompat;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewPropertyAnimator;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;
import android.widget.ViewFlipper;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractMediaItemPresenter
  extends RowPresenter
{
  public static final int PLAY_STATE_INITIAL = 0;
  public static final int PLAY_STATE_PAUSED = 1;
  public static final int PLAY_STATE_PLAYING = 2;
  static final Rect sTempRect = new Rect();
  private int mBackgroundColor = 0;
  private boolean mBackgroundColorSet;
  private Presenter mMediaItemActionPresenter = new MediaItemActionPresenter();
  private boolean mMediaRowSeparator;
  private int mThemeId;
  
  public AbstractMediaItemPresenter()
  {
    this(0);
  }
  
  public AbstractMediaItemPresenter(int paramInt)
  {
    this.mThemeId = paramInt;
    setHeaderPresenter(null);
  }
  
  static int calculateMediaItemNumberFlipperIndex(ViewHolder paramViewHolder)
  {
    switch (paramViewHolder.mRowPresenter.getMediaPlayState(paramViewHolder.getRowObject()))
    {
    default: 
      return -1;
    case 0: 
      if (paramViewHolder.mMediaItemNumberView == null) {
        return -1;
      }
      return paramViewHolder.mMediaItemNumberViewFlipper.indexOfChild(paramViewHolder.mMediaItemNumberView);
    case 1: 
      if (paramViewHolder.mMediaItemPausedView == null) {
        return -1;
      }
      return paramViewHolder.mMediaItemNumberViewFlipper.indexOfChild(paramViewHolder.mMediaItemPausedView);
    }
    if (paramViewHolder.mMediaItemPlayingView == null) {
      return -1;
    }
    return paramViewHolder.mMediaItemNumberViewFlipper.indexOfChild(paramViewHolder.mMediaItemPlayingView);
  }
  
  static ValueAnimator updateSelector(final View paramView1, View paramView2, ValueAnimator paramValueAnimator, boolean paramBoolean)
  {
    int i = paramView2.getContext().getResources().getInteger(17694720);
    DecelerateInterpolator localDecelerateInterpolator = new DecelerateInterpolator();
    final int j = ViewCompat.getLayoutDirection(paramView1);
    if (!paramView2.hasFocus())
    {
      paramView1.animate().cancel();
      paramView1.animate().alpha(0.0F).setDuration(i).setInterpolator(localDecelerateInterpolator).start();
      return paramValueAnimator;
    }
    ValueAnimator localValueAnimator = paramValueAnimator;
    if (paramValueAnimator != null)
    {
      paramValueAnimator.cancel();
      localValueAnimator = null;
    }
    float f1 = paramView1.getAlpha();
    paramView1.animate().alpha(1.0F).setDuration(i).setInterpolator(localDecelerateInterpolator).start();
    paramValueAnimator = (ViewGroup.MarginLayoutParams)paramView1.getLayoutParams();
    ViewGroup localViewGroup = (ViewGroup)paramView1.getParent();
    sTempRect.set(0, 0, paramView2.getWidth(), paramView2.getHeight());
    localViewGroup.offsetDescendantRectToMyCoords(paramView2, sTempRect);
    final int k;
    final float f2;
    final float f3;
    if (paramBoolean)
    {
      if (j == 1)
      {
        paramView2 = sTempRect;
        paramView2.right += localViewGroup.getHeight();
        paramView2 = sTempRect;
        paramView2.left -= localViewGroup.getHeight() / 2;
      }
    }
    else
    {
      j = sTempRect.left;
      k = sTempRect.width();
      f2 = paramValueAnimator.width - k;
      f3 = paramValueAnimator.leftMargin - j;
      if ((f3 != 0.0F) || (f2 != 0.0F)) {
        break label293;
      }
    }
    for (;;)
    {
      return localValueAnimator;
      paramView2 = sTempRect;
      paramView2.left -= localViewGroup.getHeight();
      paramView2 = sTempRect;
      paramView2.right += localViewGroup.getHeight() / 2;
      break;
      label293:
      if (f1 == 0.0F)
      {
        paramValueAnimator.width = k;
        paramValueAnimator.leftMargin = j;
        paramView1.requestLayout();
      }
      else
      {
        localValueAnimator = ValueAnimator.ofFloat(new float[] { 0.0F, 1.0F });
        localValueAnimator.setDuration(i);
        localValueAnimator.setInterpolator(localDecelerateInterpolator);
        localValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
          public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
          {
            float f = 1.0F - paramAnonymousValueAnimator.getAnimatedFraction();
            this.val$lp.leftMargin = Math.round(j + f3 * f);
            this.val$lp.width = Math.round(k + f2 * f);
            paramView1.requestLayout();
          }
        });
        localValueAnimator.start();
      }
    }
  }
  
  protected RowPresenter.ViewHolder createRowViewHolder(ViewGroup paramViewGroup)
  {
    Context localContext = paramViewGroup.getContext();
    Object localObject = localContext;
    if (this.mThemeId != 0) {
      localObject = new ContextThemeWrapper(localContext, this.mThemeId);
    }
    paramViewGroup = new ViewHolder(LayoutInflater.from((Context)localObject).inflate(R.layout.lb_row_media_item, paramViewGroup, false));
    paramViewGroup.mRowPresenter = this;
    if (this.mBackgroundColorSet) {
      paramViewGroup.mMediaRowView.setBackgroundColor(this.mBackgroundColor);
    }
    return paramViewGroup;
  }
  
  public Presenter getActionPresenter()
  {
    return this.mMediaItemActionPresenter;
  }
  
  protected int getMediaPlayState(Object paramObject)
  {
    return 0;
  }
  
  public int getThemeId()
  {
    return this.mThemeId;
  }
  
  public boolean hasMediaRowSeparator()
  {
    return this.mMediaRowSeparator;
  }
  
  protected boolean isClippingChildren()
  {
    return true;
  }
  
  public boolean isUsingDefaultSelectEffect()
  {
    return false;
  }
  
  protected abstract void onBindMediaDetails(ViewHolder paramViewHolder, Object paramObject);
  
  public void onBindMediaPlayState(ViewHolder paramViewHolder)
  {
    int i = calculateMediaItemNumberFlipperIndex(paramViewHolder);
    if ((i != -1) && (paramViewHolder.mMediaItemNumberViewFlipper.getDisplayedChild() != i)) {
      paramViewHolder.mMediaItemNumberViewFlipper.setDisplayedChild(i);
    }
  }
  
  protected void onBindRowActions(ViewHolder paramViewHolder)
  {
    paramViewHolder.onBindRowActions();
  }
  
  protected void onBindRowViewHolder(RowPresenter.ViewHolder paramViewHolder, Object paramObject)
  {
    super.onBindRowViewHolder(paramViewHolder, paramObject);
    ViewHolder localViewHolder = (ViewHolder)paramViewHolder;
    onBindRowActions(localViewHolder);
    View localView = localViewHolder.getMediaItemRowSeparator();
    if (hasMediaRowSeparator()) {}
    for (int i = 0;; i = 8)
    {
      localView.setVisibility(i);
      onBindMediaPlayState(localViewHolder);
      onBindMediaDetails((ViewHolder)paramViewHolder, paramObject);
      return;
    }
  }
  
  protected void onUnbindMediaDetails(ViewHolder paramViewHolder) {}
  
  public void onUnbindMediaPlayState(ViewHolder paramViewHolder) {}
  
  public void setActionPresenter(Presenter paramPresenter)
  {
    this.mMediaItemActionPresenter = paramPresenter;
  }
  
  public void setBackgroundColor(int paramInt)
  {
    this.mBackgroundColorSet = true;
    this.mBackgroundColor = paramInt;
  }
  
  public void setHasMediaRowSeparator(boolean paramBoolean)
  {
    this.mMediaRowSeparator = paramBoolean;
  }
  
  public void setThemeId(int paramInt)
  {
    this.mThemeId = paramInt;
  }
  
  public static class ViewHolder
    extends RowPresenter.ViewHolder
  {
    private final List<Presenter.ViewHolder> mActionViewHolders;
    ValueAnimator mFocusViewAnimator;
    private final ViewGroup mMediaItemActionsContainer;
    private final View mMediaItemDetailsView;
    private final TextView mMediaItemDurationView;
    private final TextView mMediaItemNameView;
    final TextView mMediaItemNumberView;
    final ViewFlipper mMediaItemNumberViewFlipper;
    final View mMediaItemPausedView;
    final View mMediaItemPlayingView;
    MultiActionsProvider.MultiAction[] mMediaItemRowActions;
    private final View mMediaItemRowSeparator;
    final View mMediaRowView;
    AbstractMediaItemPresenter mRowPresenter;
    final View mSelectorView;
    
    public ViewHolder(View paramView)
    {
      super();
      this.mSelectorView = paramView.findViewById(R.id.mediaRowSelector);
      this.mMediaRowView = paramView.findViewById(R.id.mediaItemRow);
      this.mMediaItemDetailsView = paramView.findViewById(R.id.mediaItemDetails);
      this.mMediaItemNameView = ((TextView)paramView.findViewById(R.id.mediaItemName));
      this.mMediaItemDurationView = ((TextView)paramView.findViewById(R.id.mediaItemDuration));
      this.mMediaItemRowSeparator = paramView.findViewById(R.id.mediaRowSeparator);
      this.mMediaItemActionsContainer = ((ViewGroup)paramView.findViewById(R.id.mediaItemActionsContainer));
      this.mActionViewHolders = new ArrayList();
      getMediaItemDetailsView().setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          if (AbstractMediaItemPresenter.ViewHolder.this.getOnItemViewClickedListener() != null) {
            AbstractMediaItemPresenter.ViewHolder.this.getOnItemViewClickedListener().onItemClicked(null, null, AbstractMediaItemPresenter.ViewHolder.this, AbstractMediaItemPresenter.ViewHolder.this.getRowObject());
          }
        }
      });
      getMediaItemDetailsView().setOnFocusChangeListener(new View.OnFocusChangeListener()
      {
        public void onFocusChange(View paramAnonymousView, boolean paramAnonymousBoolean)
        {
          AbstractMediaItemPresenter.ViewHolder.this.mFocusViewAnimator = AbstractMediaItemPresenter.updateSelector(AbstractMediaItemPresenter.ViewHolder.this.mSelectorView, paramAnonymousView, AbstractMediaItemPresenter.ViewHolder.this.mFocusViewAnimator, true);
        }
      });
      this.mMediaItemNumberViewFlipper = ((ViewFlipper)paramView.findViewById(R.id.mediaItemNumberViewFlipper));
      TypedValue localTypedValue = new TypedValue();
      boolean bool = paramView.getContext().getTheme().resolveAttribute(R.attr.playbackMediaItemNumberViewFlipperLayout, localTypedValue, true);
      paramView = LayoutInflater.from(paramView.getContext());
      if (bool) {}
      for (int i = localTypedValue.resourceId;; i = R.layout.lb_media_item_number_view_flipper)
      {
        paramView = paramView.inflate(i, this.mMediaItemNumberViewFlipper, true);
        this.mMediaItemNumberView = ((TextView)paramView.findViewById(R.id.initial));
        this.mMediaItemPausedView = paramView.findViewById(R.id.paused);
        this.mMediaItemPlayingView = paramView.findViewById(R.id.playing);
        return;
      }
    }
    
    int findActionIndex(MultiActionsProvider.MultiAction paramMultiAction)
    {
      if (this.mMediaItemRowActions != null)
      {
        int i = 0;
        while (i < this.mMediaItemRowActions.length)
        {
          if (this.mMediaItemRowActions[i] == paramMultiAction) {
            return i;
          }
          i += 1;
        }
      }
      return -1;
    }
    
    public ViewGroup getMediaItemActionsContainer()
    {
      return this.mMediaItemActionsContainer;
    }
    
    public View getMediaItemDetailsView()
    {
      return this.mMediaItemDetailsView;
    }
    
    public TextView getMediaItemDurationView()
    {
      return this.mMediaItemDurationView;
    }
    
    public TextView getMediaItemNameView()
    {
      return this.mMediaItemNameView;
    }
    
    public TextView getMediaItemNumberView()
    {
      return this.mMediaItemNumberView;
    }
    
    public ViewFlipper getMediaItemNumberViewFlipper()
    {
      return this.mMediaItemNumberViewFlipper;
    }
    
    public View getMediaItemPausedView()
    {
      return this.mMediaItemPausedView;
    }
    
    public View getMediaItemPlayingView()
    {
      return this.mMediaItemPlayingView;
    }
    
    public MultiActionsProvider.MultiAction[] getMediaItemRowActions()
    {
      return this.mMediaItemRowActions;
    }
    
    public View getMediaItemRowSeparator()
    {
      return this.mMediaItemRowSeparator;
    }
    
    public View getSelectorView()
    {
      return this.mSelectorView;
    }
    
    public void notifyActionChanged(MultiActionsProvider.MultiAction paramMultiAction)
    {
      Presenter localPresenter = this.mRowPresenter.getActionPresenter();
      if (localPresenter == null) {}
      int i;
      do
      {
        return;
        i = findActionIndex(paramMultiAction);
      } while (i < 0);
      Presenter.ViewHolder localViewHolder = (Presenter.ViewHolder)this.mActionViewHolders.get(i);
      localPresenter.onUnbindViewHolder(localViewHolder);
      localPresenter.onBindViewHolder(localViewHolder, paramMultiAction);
    }
    
    public void notifyDetailsChanged()
    {
      this.mRowPresenter.onUnbindMediaDetails(this);
      this.mRowPresenter.onBindMediaDetails(this, getRowObject());
    }
    
    public void notifyPlayStateChanged()
    {
      this.mRowPresenter.onBindMediaPlayState(this);
    }
    
    public void onBindRowActions()
    {
      final int i = getMediaItemActionsContainer().getChildCount() - 1;
      while (i >= this.mActionViewHolders.size())
      {
        getMediaItemActionsContainer().removeViewAt(i);
        this.mActionViewHolders.remove(i);
        i -= 1;
      }
      this.mMediaItemRowActions = null;
      Object localObject = getRowObject();
      Presenter localPresenter;
      if ((localObject instanceof MultiActionsProvider))
      {
        localObject = ((MultiActionsProvider)localObject).getActions();
        localPresenter = this.mRowPresenter.getActionPresenter();
        if (localPresenter != null) {
          break label89;
        }
      }
      for (;;)
      {
        return;
        label89:
        this.mMediaItemRowActions = ((MultiActionsProvider.MultiAction[])localObject);
        i = this.mActionViewHolders.size();
        final Presenter.ViewHolder localViewHolder;
        while (i < localObject.length)
        {
          localViewHolder = localPresenter.onCreateViewHolder(getMediaItemActionsContainer());
          getMediaItemActionsContainer().addView(localViewHolder.view);
          this.mActionViewHolders.add(localViewHolder);
          localViewHolder.view.setOnFocusChangeListener(new View.OnFocusChangeListener()
          {
            public void onFocusChange(View paramAnonymousView, boolean paramAnonymousBoolean)
            {
              AbstractMediaItemPresenter.ViewHolder.this.mFocusViewAnimator = AbstractMediaItemPresenter.updateSelector(AbstractMediaItemPresenter.ViewHolder.this.mSelectorView, paramAnonymousView, AbstractMediaItemPresenter.ViewHolder.this.mFocusViewAnimator, false);
            }
          });
          localViewHolder.view.setOnClickListener(new View.OnClickListener()
          {
            public void onClick(View paramAnonymousView)
            {
              if (AbstractMediaItemPresenter.ViewHolder.this.getOnItemViewClickedListener() != null) {
                AbstractMediaItemPresenter.ViewHolder.this.getOnItemViewClickedListener().onItemClicked(localViewHolder, AbstractMediaItemPresenter.ViewHolder.this.mMediaItemRowActions[i], AbstractMediaItemPresenter.ViewHolder.this, AbstractMediaItemPresenter.ViewHolder.this.getRowObject());
              }
            }
          });
          i += 1;
        }
        if (this.mMediaItemActionsContainer != null)
        {
          i = 0;
          while (i < localObject.length)
          {
            localViewHolder = (Presenter.ViewHolder)this.mActionViewHolders.get(i);
            localPresenter.onUnbindViewHolder(localViewHolder);
            localPresenter.onBindViewHolder(localViewHolder, this.mMediaItemRowActions[i]);
            i += 1;
          }
        }
      }
    }
    
    public void setSelectedMediaItemNumberView(int paramInt)
    {
      if ((paramInt >= 0) && (paramInt < this.mMediaItemNumberViewFlipper.getChildCount())) {
        this.mMediaItemNumberViewFlipper.setDisplayedChild(paramInt);
      }
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/AbstractMediaItemPresenter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */