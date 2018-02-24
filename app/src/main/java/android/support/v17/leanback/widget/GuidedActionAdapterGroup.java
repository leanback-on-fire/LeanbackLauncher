package android.support.v17.leanback.widget;

import android.content.Context;
import android.support.annotation.RestrictTo;
import android.util.Pair;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import java.util.ArrayList;

@RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
public class GuidedActionAdapterGroup
{
  private static final boolean DEBUG_EDIT = false;
  private static final String TAG_EDIT = "EditableAction";
  ArrayList<Pair<GuidedActionAdapter, GuidedActionAdapter>> mAdapters = new ArrayList();
  private GuidedActionAdapter.EditListener mEditListener;
  private boolean mImeOpened;
  
  private void updateTextIntoAction(GuidedActionsStylist.ViewHolder paramViewHolder, TextView paramTextView)
  {
    GuidedAction localGuidedAction = paramViewHolder.getAction();
    if (paramTextView == paramViewHolder.getDescriptionView()) {
      if (localGuidedAction.getEditDescription() != null) {
        localGuidedAction.setEditDescription(paramTextView.getText());
      }
    }
    while (paramTextView != paramViewHolder.getTitleView())
    {
      return;
      localGuidedAction.setDescription(paramTextView.getText());
      return;
    }
    if (localGuidedAction.getEditTitle() != null)
    {
      localGuidedAction.setEditTitle(paramTextView.getText());
      return;
    }
    localGuidedAction.setTitle(paramTextView.getText());
  }
  
  public void addAdpter(GuidedActionAdapter paramGuidedActionAdapter1, GuidedActionAdapter paramGuidedActionAdapter2)
  {
    this.mAdapters.add(new Pair(paramGuidedActionAdapter1, paramGuidedActionAdapter2));
    if (paramGuidedActionAdapter1 != null) {
      paramGuidedActionAdapter1.mGroup = this;
    }
    if (paramGuidedActionAdapter2 != null) {
      paramGuidedActionAdapter2.mGroup = this;
    }
  }
  
  public void closeIme(View paramView)
  {
    if (this.mImeOpened)
    {
      this.mImeOpened = false;
      ((InputMethodManager)paramView.getContext().getSystemService("input_method")).hideSoftInputFromWindow(paramView.getWindowToken(), 0);
      this.mEditListener.onImeClose();
    }
  }
  
  public void fillAndGoNext(GuidedActionAdapter paramGuidedActionAdapter, TextView paramTextView)
  {
    boolean bool2 = false;
    GuidedActionsStylist.ViewHolder localViewHolder = paramGuidedActionAdapter.findSubChildViewHolder(paramTextView);
    updateTextIntoAction(localViewHolder, paramTextView);
    paramGuidedActionAdapter.performOnActionClick(localViewHolder);
    long l = this.mEditListener.onGuidedActionEditedAndProceed(localViewHolder.getAction());
    paramGuidedActionAdapter.getGuidedActionsStylist().setEditingMode(localViewHolder, false);
    boolean bool1 = bool2;
    if (l != -3L)
    {
      bool1 = bool2;
      if (l != localViewHolder.getAction().getId()) {
        bool1 = focusToNextAction(paramGuidedActionAdapter, localViewHolder.getAction(), l);
      }
    }
    if (!bool1)
    {
      closeIme(paramTextView);
      localViewHolder.itemView.requestFocus();
    }
  }
  
  public void fillAndStay(GuidedActionAdapter paramGuidedActionAdapter, TextView paramTextView)
  {
    GuidedActionsStylist.ViewHolder localViewHolder = paramGuidedActionAdapter.findSubChildViewHolder(paramTextView);
    updateTextIntoAction(localViewHolder, paramTextView);
    this.mEditListener.onGuidedActionEditCanceled(localViewHolder.getAction());
    paramGuidedActionAdapter.getGuidedActionsStylist().setEditingMode(localViewHolder, false);
    closeIme(paramTextView);
    localViewHolder.itemView.requestFocus();
  }
  
  boolean focusToNextAction(GuidedActionAdapter paramGuidedActionAdapter, GuidedAction paramGuidedAction, long paramLong)
  {
    int i = 0;
    GuidedActionAdapter localGuidedActionAdapter = paramGuidedActionAdapter;
    if (paramLong == -2L)
    {
      i = paramGuidedActionAdapter.indexOf(paramGuidedAction);
      if (i < 0) {
        return false;
      }
      i += 1;
      localGuidedActionAdapter = paramGuidedActionAdapter;
    }
    for (;;)
    {
      int m = localGuidedActionAdapter.getCount();
      int j = i;
      int k;
      if (paramLong == -2L) {
        for (;;)
        {
          k = i;
          if (i >= m) {
            break;
          }
          k = i;
          if (localGuidedActionAdapter.getItem(i).isFocusable()) {
            break;
          }
          i += 1;
        }
      }
      for (;;)
      {
        k = j;
        if (j >= m) {
          break;
        }
        k = j;
        if (localGuidedActionAdapter.getItem(j).getId() == paramLong) {
          break;
        }
        j += 1;
      }
      if (k < m)
      {
        paramGuidedActionAdapter = (GuidedActionsStylist.ViewHolder)localGuidedActionAdapter.getGuidedActionsStylist().getActionsGridView().findViewHolderForPosition(k);
        if (paramGuidedActionAdapter == null) {
          break;
        }
        if (paramGuidedActionAdapter.getAction().hasTextEditable()) {
          openIme(localGuidedActionAdapter, paramGuidedActionAdapter);
        }
        for (;;)
        {
          return true;
          closeIme(paramGuidedActionAdapter.itemView);
          paramGuidedActionAdapter.itemView.requestFocus();
        }
      }
      localGuidedActionAdapter = getNextAdapter(localGuidedActionAdapter);
      if (localGuidedActionAdapter == null) {
        break;
      }
      i = 0;
    }
  }
  
  public GuidedActionAdapter getNextAdapter(GuidedActionAdapter paramGuidedActionAdapter)
  {
    int i = 0;
    while (i < this.mAdapters.size())
    {
      Pair localPair = (Pair)this.mAdapters.get(i);
      if (localPair.first == paramGuidedActionAdapter) {
        return (GuidedActionAdapter)localPair.second;
      }
      i += 1;
    }
    return null;
  }
  
  public void openIme(GuidedActionAdapter paramGuidedActionAdapter, GuidedActionsStylist.ViewHolder paramViewHolder)
  {
    paramGuidedActionAdapter.getGuidedActionsStylist().setEditingMode(paramViewHolder, true);
    paramGuidedActionAdapter = paramViewHolder.getEditingView();
    if ((paramGuidedActionAdapter == null) || (!paramViewHolder.isInEditingText())) {}
    do
    {
      return;
      paramViewHolder = (InputMethodManager)paramGuidedActionAdapter.getContext().getSystemService("input_method");
      paramGuidedActionAdapter.setFocusable(true);
      paramGuidedActionAdapter.requestFocus();
      paramViewHolder.showSoftInput(paramGuidedActionAdapter, 0);
    } while (this.mImeOpened);
    this.mImeOpened = true;
    this.mEditListener.onImeOpen();
  }
  
  public void setEditListener(GuidedActionAdapter.EditListener paramEditListener)
  {
    this.mEditListener = paramEditListener;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/GuidedActionAdapterGroup.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */