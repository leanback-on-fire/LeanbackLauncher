package android.support.v17.leanback.widget;

import android.support.annotation.RestrictTo;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import java.util.ArrayList;
import java.util.List;

@RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
public class GuidedActionAdapter
  extends RecyclerView.Adapter
{
  static final boolean DEBUG = false;
  static final boolean DEBUG_EDIT = false;
  static final String TAG = "GuidedActionAdapter";
  static final String TAG_EDIT = "EditableAction";
  private final ActionEditListener mActionEditListener;
  private final ActionOnFocusListener mActionOnFocusListener;
  private final ActionOnKeyListener mActionOnKeyListener;
  private final List<GuidedAction> mActions;
  private ClickListener mClickListener;
  GuidedActionAdapterGroup mGroup;
  private final boolean mIsSubAdapter;
  private final View.OnClickListener mOnClickListener = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      GuidedAction localGuidedAction;
      if ((paramAnonymousView != null) && (paramAnonymousView.getWindowToken() != null) && (GuidedActionAdapter.this.getRecyclerView() != null))
      {
        paramAnonymousView = (GuidedActionsStylist.ViewHolder)GuidedActionAdapter.this.getRecyclerView().getChildViewHolder(paramAnonymousView);
        localGuidedAction = paramAnonymousView.getAction();
        if (!localGuidedAction.hasTextEditable()) {
          break label64;
        }
        GuidedActionAdapter.this.mGroup.openIme(GuidedActionAdapter.this, paramAnonymousView);
      }
      label64:
      do
      {
        return;
        if (localGuidedAction.hasEditableActivatorView())
        {
          GuidedActionAdapter.this.performOnActionClick(paramAnonymousView);
          return;
        }
        GuidedActionAdapter.this.handleCheckedActions(paramAnonymousView);
      } while ((!localGuidedAction.isEnabled()) || (localGuidedAction.infoOnly()));
      GuidedActionAdapter.this.performOnActionClick(paramAnonymousView);
    }
  };
  final GuidedActionsStylist mStylist;
  
  public GuidedActionAdapter(List<GuidedAction> paramList, ClickListener paramClickListener, FocusListener paramFocusListener, GuidedActionsStylist paramGuidedActionsStylist, boolean paramBoolean)
  {
    if (paramList == null) {}
    for (paramList = new ArrayList();; paramList = new ArrayList(paramList))
    {
      this.mActions = paramList;
      this.mClickListener = paramClickListener;
      this.mStylist = paramGuidedActionsStylist;
      this.mActionOnKeyListener = new ActionOnKeyListener();
      this.mActionOnFocusListener = new ActionOnFocusListener(paramFocusListener);
      this.mActionEditListener = new ActionEditListener();
      this.mIsSubAdapter = paramBoolean;
      return;
    }
  }
  
  private void setupListeners(EditText paramEditText)
  {
    if (paramEditText != null)
    {
      paramEditText.setPrivateImeOptions("EscapeNorth=1;");
      paramEditText.setOnEditorActionListener(this.mActionEditListener);
      if ((paramEditText instanceof ImeKeyMonitor)) {
        ((ImeKeyMonitor)paramEditText).setImeKeyListener(this.mActionEditListener);
      }
    }
  }
  
  public GuidedActionsStylist.ViewHolder findSubChildViewHolder(View paramView)
  {
    if (getRecyclerView() == null) {}
    View localView;
    do
    {
      return null;
      ViewParent localViewParent = paramView.getParent();
      localView = paramView;
      for (paramView = localViewParent; (paramView != getRecyclerView()) && (paramView != null) && (localView != null); paramView = paramView.getParent()) {
        localView = (View)paramView;
      }
    } while ((paramView == null) || (localView == null));
    return (GuidedActionsStylist.ViewHolder)getRecyclerView().getChildViewHolder(localView);
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public List<GuidedAction> getActions()
  {
    return new ArrayList(this.mActions);
  }
  
  public int getCount()
  {
    return this.mActions.size();
  }
  
  public GuidedActionsStylist getGuidedActionsStylist()
  {
    return this.mStylist;
  }
  
  public GuidedAction getItem(int paramInt)
  {
    return (GuidedAction)this.mActions.get(paramInt);
  }
  
  public int getItemCount()
  {
    return this.mActions.size();
  }
  
  public int getItemViewType(int paramInt)
  {
    return this.mStylist.getItemViewType((GuidedAction)this.mActions.get(paramInt));
  }
  
  RecyclerView getRecyclerView()
  {
    if (this.mIsSubAdapter) {
      return this.mStylist.getSubActionsGridView();
    }
    return this.mStylist.getActionsGridView();
  }
  
  public void handleCheckedActions(GuidedActionsStylist.ViewHolder paramViewHolder)
  {
    GuidedAction localGuidedAction = paramViewHolder.getAction();
    int j = localGuidedAction.getCheckSetId();
    if ((getRecyclerView() != null) && (j != 0))
    {
      if (j != -1)
      {
        int i = 0;
        int k = this.mActions.size();
        while (i < k)
        {
          Object localObject = (GuidedAction)this.mActions.get(i);
          if ((localObject != localGuidedAction) && (((GuidedAction)localObject).getCheckSetId() == j) && (((GuidedAction)localObject).isChecked()))
          {
            ((GuidedAction)localObject).setChecked(false);
            localObject = (GuidedActionsStylist.ViewHolder)getRecyclerView().findViewHolderForPosition(i);
            if (localObject != null) {
              this.mStylist.onAnimateItemChecked((GuidedActionsStylist.ViewHolder)localObject, false);
            }
          }
          i += 1;
        }
      }
      if (localGuidedAction.isChecked()) {
        break label151;
      }
      localGuidedAction.setChecked(true);
      this.mStylist.onAnimateItemChecked(paramViewHolder, true);
    }
    label151:
    while (j != -1) {
      return;
    }
    localGuidedAction.setChecked(false);
    this.mStylist.onAnimateItemChecked(paramViewHolder, false);
  }
  
  public int indexOf(GuidedAction paramGuidedAction)
  {
    return this.mActions.indexOf(paramGuidedAction);
  }
  
  public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
  {
    if (paramInt >= this.mActions.size()) {
      return;
    }
    paramViewHolder = (GuidedActionsStylist.ViewHolder)paramViewHolder;
    GuidedAction localGuidedAction = (GuidedAction)this.mActions.get(paramInt);
    this.mStylist.onBindViewHolder(paramViewHolder, localGuidedAction);
  }
  
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
  {
    paramViewGroup = this.mStylist.onCreateViewHolder(paramViewGroup, paramInt);
    View localView = paramViewGroup.itemView;
    localView.setOnKeyListener(this.mActionOnKeyListener);
    localView.setOnClickListener(this.mOnClickListener);
    localView.setOnFocusChangeListener(this.mActionOnFocusListener);
    setupListeners(paramViewGroup.getEditableTitleView());
    setupListeners(paramViewGroup.getEditableDescriptionView());
    return paramViewGroup;
  }
  
  public void performOnActionClick(GuidedActionsStylist.ViewHolder paramViewHolder)
  {
    if (this.mClickListener != null) {
      this.mClickListener.onGuidedActionClicked(paramViewHolder.getAction());
    }
  }
  
  public void setActions(List<GuidedAction> paramList)
  {
    if (!this.mIsSubAdapter) {
      this.mStylist.collapseAction(false);
    }
    this.mActionOnFocusListener.unFocus();
    this.mActions.clear();
    this.mActions.addAll(paramList);
    notifyDataSetChanged();
  }
  
  public void setClickListener(ClickListener paramClickListener)
  {
    this.mClickListener = paramClickListener;
  }
  
  public void setFocusListener(FocusListener paramFocusListener)
  {
    this.mActionOnFocusListener.setFocusListener(paramFocusListener);
  }
  
  private class ActionEditListener
    implements TextView.OnEditorActionListener, ImeKeyMonitor.ImeKeyListener
  {
    ActionEditListener() {}
    
    public boolean onEditorAction(TextView paramTextView, int paramInt, KeyEvent paramKeyEvent)
    {
      boolean bool = false;
      if ((paramInt == 5) || (paramInt == 6))
      {
        GuidedActionAdapter.this.mGroup.fillAndGoNext(GuidedActionAdapter.this, paramTextView);
        bool = true;
      }
      while (paramInt != 1) {
        return bool;
      }
      GuidedActionAdapter.this.mGroup.fillAndStay(GuidedActionAdapter.this, paramTextView);
      return true;
    }
    
    public boolean onKeyPreIme(EditText paramEditText, int paramInt, KeyEvent paramKeyEvent)
    {
      if ((paramInt == 4) && (paramKeyEvent.getAction() == 1))
      {
        GuidedActionAdapter.this.mGroup.fillAndStay(GuidedActionAdapter.this, paramEditText);
        return true;
      }
      if ((paramInt == 66) && (paramKeyEvent.getAction() == 1))
      {
        GuidedActionAdapter.this.mGroup.fillAndGoNext(GuidedActionAdapter.this, paramEditText);
        return true;
      }
      return false;
    }
  }
  
  private class ActionOnFocusListener
    implements View.OnFocusChangeListener
  {
    private GuidedActionAdapter.FocusListener mFocusListener;
    private View mSelectedView;
    
    ActionOnFocusListener(GuidedActionAdapter.FocusListener paramFocusListener)
    {
      this.mFocusListener = paramFocusListener;
    }
    
    public void onFocusChange(View paramView, boolean paramBoolean)
    {
      if (GuidedActionAdapter.this.getRecyclerView() == null) {
        return;
      }
      GuidedActionsStylist.ViewHolder localViewHolder = (GuidedActionsStylist.ViewHolder)GuidedActionAdapter.this.getRecyclerView().getChildViewHolder(paramView);
      if (paramBoolean)
      {
        this.mSelectedView = paramView;
        if (this.mFocusListener != null) {
          this.mFocusListener.onGuidedActionFocused(localViewHolder.getAction());
        }
      }
      for (;;)
      {
        GuidedActionAdapter.this.mStylist.onAnimateItemFocused(localViewHolder, paramBoolean);
        return;
        if (this.mSelectedView == paramView)
        {
          GuidedActionAdapter.this.mStylist.onAnimateItemPressedCancelled(localViewHolder);
          this.mSelectedView = null;
        }
      }
    }
    
    public void setFocusListener(GuidedActionAdapter.FocusListener paramFocusListener)
    {
      this.mFocusListener = paramFocusListener;
    }
    
    public void unFocus()
    {
      if ((this.mSelectedView != null) && (GuidedActionAdapter.this.getRecyclerView() != null))
      {
        Object localObject = GuidedActionAdapter.this.getRecyclerView().getChildViewHolder(this.mSelectedView);
        if (localObject != null)
        {
          localObject = (GuidedActionsStylist.ViewHolder)localObject;
          GuidedActionAdapter.this.mStylist.onAnimateItemFocused((GuidedActionsStylist.ViewHolder)localObject, false);
        }
      }
      else
      {
        return;
      }
      Log.w("GuidedActionAdapter", "RecyclerView returned null view holder", new Throwable());
    }
  }
  
  private class ActionOnKeyListener
    implements View.OnKeyListener
  {
    private boolean mKeyPressed = false;
    
    ActionOnKeyListener() {}
    
    public boolean onKey(View paramView, int paramInt, KeyEvent paramKeyEvent)
    {
      if ((paramView == null) || (paramKeyEvent == null) || (GuidedActionAdapter.this.getRecyclerView() == null)) {}
      do
      {
        do
        {
          return false;
          switch (paramInt)
          {
          default: 
            return false;
          }
          paramView = (GuidedActionsStylist.ViewHolder)GuidedActionAdapter.this.getRecyclerView().getChildViewHolder(paramView);
          GuidedAction localGuidedAction = paramView.getAction();
          if ((!localGuidedAction.isEnabled()) || (localGuidedAction.infoOnly()))
          {
            if (paramKeyEvent.getAction() == 0) {}
            return true;
          }
          switch (paramKeyEvent.getAction())
          {
          default: 
            return false;
          }
        } while (this.mKeyPressed);
        this.mKeyPressed = true;
        GuidedActionAdapter.this.mStylist.onAnimateItemPressed(paramView, this.mKeyPressed);
        return false;
      } while (!this.mKeyPressed);
      this.mKeyPressed = false;
      GuidedActionAdapter.this.mStylist.onAnimateItemPressed(paramView, this.mKeyPressed);
      return false;
    }
  }
  
  public static abstract interface ClickListener
  {
    public abstract void onGuidedActionClicked(GuidedAction paramGuidedAction);
  }
  
  public static abstract interface EditListener
  {
    public abstract void onGuidedActionEditCanceled(GuidedAction paramGuidedAction);
    
    public abstract long onGuidedActionEditedAndProceed(GuidedAction paramGuidedAction);
    
    public abstract void onImeClose();
    
    public abstract void onImeOpen();
  }
  
  public static abstract interface FocusListener
  {
    public abstract void onGuidedActionFocused(GuidedAction paramGuidedAction);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/GuidedActionAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */