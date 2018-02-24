package android.support.v17.leanback.widget;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.support.v17.leanback.R.attr;
import android.support.v17.leanback.R.id;
import android.support.v17.leanback.R.layout;
import android.support.v17.leanback.R.string;
import android.support.v17.leanback.R.styleable;
import android.support.v17.leanback.transition.TransitionEpicenterCallback;
import android.support.v17.leanback.transition.TransitionHelper;
import android.support.v17.leanback.transition.TransitionListener;
import android.support.v17.leanback.widget.picker.DatePicker;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.AccessibilityDelegate;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Checkable;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class GuidedActionsStylist
  implements FragmentAnimationProvider
{
  private static String TAG = "GuidedActionsStylist";
  public static final int VIEW_TYPE_DATE_PICKER = 1;
  public static final int VIEW_TYPE_DEFAULT = 0;
  static final ItemAlignmentFacet sGuidedActionItemAlignFacet = new ItemAlignmentFacet();
  private VerticalGridView mActionsGridView;
  private boolean mBackToCollapseActivatorView = true;
  private boolean mBackToCollapseSubActions = true;
  private View mBgView;
  private boolean mButtonActions;
  private View mContentView;
  private int mDescriptionMinLines;
  private float mDisabledChevronAlpha;
  private float mDisabledDescriptionAlpha;
  private float mDisabledTextAlpha;
  private int mDisplayHeight;
  private GuidedActionAdapter.EditListener mEditListener;
  private float mEnabledChevronAlpha;
  private float mEnabledDescriptionAlpha;
  private float mEnabledTextAlpha;
  Object mExpandTransition;
  private GuidedAction mExpandedAction = null;
  private float mKeyLinePercent;
  ViewGroup mMainView;
  private View mSubActionsBackground;
  VerticalGridView mSubActionsGridView;
  private int mTitleMaxLines;
  private int mTitleMinLines;
  private int mVerticalPadding;
  
  static
  {
    ItemAlignmentFacet.ItemAlignmentDef localItemAlignmentDef = new ItemAlignmentFacet.ItemAlignmentDef();
    localItemAlignmentDef.setItemAlignmentViewId(R.id.guidedactions_item_title);
    localItemAlignmentDef.setAlignedToTextViewBaseline(true);
    localItemAlignmentDef.setItemAlignmentOffset(0);
    localItemAlignmentDef.setItemAlignmentOffsetWithPadding(true);
    localItemAlignmentDef.setItemAlignmentOffsetPercent(0.0F);
    sGuidedActionItemAlignFacet.setAlignmentDefs(new ItemAlignmentFacet.ItemAlignmentDef[] { localItemAlignmentDef });
  }
  
  private int getDescriptionMaxHeight(Context paramContext, TextView paramTextView)
  {
    return this.mDisplayHeight - this.mVerticalPadding * 2 - this.mTitleMaxLines * 2 * paramTextView.getLineHeight();
  }
  
  private int getDimension(Context paramContext, TypedValue paramTypedValue, int paramInt)
  {
    paramContext.getTheme().resolveAttribute(paramInt, paramTypedValue, true);
    return paramContext.getResources().getDimensionPixelSize(paramTypedValue.resourceId);
  }
  
  private float getFloat(Context paramContext, TypedValue paramTypedValue, int paramInt)
  {
    paramContext.getTheme().resolveAttribute(paramInt, paramTypedValue, true);
    return Float.valueOf(paramContext.getResources().getString(paramTypedValue.resourceId)).floatValue();
  }
  
  private int getInteger(Context paramContext, TypedValue paramTypedValue, int paramInt)
  {
    paramContext.getTheme().resolveAttribute(paramInt, paramTypedValue, true);
    return paramContext.getResources().getInteger(paramTypedValue.resourceId);
  }
  
  private boolean setIcon(ImageView paramImageView, GuidedAction paramGuidedAction)
  {
    boolean bool = false;
    Drawable localDrawable = null;
    if (paramImageView != null)
    {
      localDrawable = paramGuidedAction.getIcon();
      if (localDrawable == null) {
        break label49;
      }
      paramImageView.setImageLevel(localDrawable.getLevel());
      paramImageView.setImageDrawable(localDrawable);
      paramImageView.setVisibility(0);
    }
    for (;;)
    {
      if (localDrawable != null) {
        bool = true;
      }
      return bool;
      label49:
      paramImageView.setVisibility(8);
    }
  }
  
  private static void setMaxLines(TextView paramTextView, int paramInt)
  {
    if (paramInt == 1)
    {
      paramTextView.setSingleLine(true);
      return;
    }
    paramTextView.setSingleLine(false);
    paramTextView.setMaxLines(paramInt);
  }
  
  private void setupNextImeOptions(EditText paramEditText)
  {
    if (paramEditText != null) {
      paramEditText.setImeOptions(5);
    }
  }
  
  private void updateChevronAndVisibility(ViewHolder paramViewHolder)
  {
    if (!paramViewHolder.isSubAction())
    {
      if (this.mExpandedAction != null) {
        break label59;
      }
      paramViewHolder.itemView.setVisibility(0);
      paramViewHolder.itemView.setTranslationY(0.0F);
      if (paramViewHolder.mActivatorView != null) {
        paramViewHolder.setActivated(false);
      }
    }
    for (;;)
    {
      if (paramViewHolder.mChevronView != null) {
        onBindChevronView(paramViewHolder, paramViewHolder.getAction());
      }
      return;
      label59:
      if (paramViewHolder.getAction() == this.mExpandedAction)
      {
        paramViewHolder.itemView.setVisibility(0);
        if (paramViewHolder.getAction().hasSubActions())
        {
          paramViewHolder.itemView.setTranslationY(getKeyLine() - paramViewHolder.itemView.getBottom());
        }
        else if (paramViewHolder.mActivatorView != null)
        {
          paramViewHolder.itemView.setTranslationY(0.0F);
          paramViewHolder.setActivated(true);
        }
      }
      else
      {
        paramViewHolder.itemView.setVisibility(4);
        paramViewHolder.itemView.setTranslationY(0.0F);
      }
    }
  }
  
  public void collapseAction(boolean paramBoolean)
  {
    if ((isInExpandTransition()) || (this.mExpandedAction == null)) {
      return;
    }
    if ((isExpandTransitionSupported()) && (paramBoolean)) {}
    for (paramBoolean = true;; paramBoolean = false)
    {
      int i = ((GuidedActionAdapter)getActionsGridView().getAdapter()).indexOf(this.mExpandedAction);
      if (i < 0) {
        break;
      }
      if (!this.mExpandedAction.hasEditableActivatorView()) {
        break label83;
      }
      setEditingMode((ViewHolder)getActionsGridView().findViewHolderForPosition(i), false, paramBoolean);
      return;
    }
    label83:
    startExpanded(null, paramBoolean);
  }
  
  public void expandAction(GuidedAction paramGuidedAction, boolean paramBoolean)
  {
    if ((isInExpandTransition()) || (this.mExpandedAction != null)) {}
    int j;
    do
    {
      return;
      j = ((GuidedActionAdapter)getActionsGridView().getAdapter()).indexOf(paramGuidedAction);
    } while (j < 0);
    if ((isExpandTransitionSupported()) && (paramBoolean)) {}
    for (int i = 1;; i = 0)
    {
      if (i != 0) {
        break label89;
      }
      getActionsGridView().setSelectedPosition(j, new ViewHolderTask()
      {
        public void run(RecyclerView.ViewHolder paramAnonymousViewHolder)
        {
          paramAnonymousViewHolder = (GuidedActionsStylist.ViewHolder)paramAnonymousViewHolder;
          if (paramAnonymousViewHolder.getAction().hasEditableActivatorView())
          {
            GuidedActionsStylist.this.setEditingMode(paramAnonymousViewHolder, true, false);
            return;
          }
          GuidedActionsStylist.this.onUpdateExpandedViewHolder(paramAnonymousViewHolder);
        }
      });
      if (!paramGuidedAction.hasSubActions()) {
        break;
      }
      onUpdateSubActionsGridView(paramGuidedAction, true);
      return;
    }
    label89:
    getActionsGridView().setSelectedPosition(j, new ViewHolderTask()
    {
      public void run(RecyclerView.ViewHolder paramAnonymousViewHolder)
      {
        paramAnonymousViewHolder = (GuidedActionsStylist.ViewHolder)paramAnonymousViewHolder;
        if (paramAnonymousViewHolder.getAction().hasEditableActivatorView())
        {
          GuidedActionsStylist.this.setEditingMode(paramAnonymousViewHolder, true, true);
          return;
        }
        GuidedActionsStylist.this.startExpanded(paramAnonymousViewHolder, true);
      }
    });
  }
  
  public VerticalGridView getActionsGridView()
  {
    return this.mActionsGridView;
  }
  
  public GuidedAction getExpandedAction()
  {
    return this.mExpandedAction;
  }
  
  public int getItemViewType(GuidedAction paramGuidedAction)
  {
    if ((paramGuidedAction instanceof GuidedDatePickerAction)) {
      return 1;
    }
    return 0;
  }
  
  int getKeyLine()
  {
    return (int)(this.mKeyLinePercent * this.mActionsGridView.getHeight() / 100.0F);
  }
  
  public VerticalGridView getSubActionsGridView()
  {
    return this.mSubActionsGridView;
  }
  
  public final boolean isBackKeyToCollapseActivatorView()
  {
    return this.mBackToCollapseActivatorView;
  }
  
  public final boolean isBackKeyToCollapseSubActions()
  {
    return this.mBackToCollapseSubActions;
  }
  
  public boolean isButtonActions()
  {
    return this.mButtonActions;
  }
  
  public boolean isExpandTransitionSupported()
  {
    return Build.VERSION.SDK_INT >= 21;
  }
  
  public boolean isExpanded()
  {
    return this.mExpandedAction != null;
  }
  
  public boolean isInExpandTransition()
  {
    return this.mExpandTransition != null;
  }
  
  public boolean isSubActionsExpanded()
  {
    return (this.mExpandedAction != null) && (this.mExpandedAction.hasSubActions());
  }
  
  public void onAnimateItemChecked(ViewHolder paramViewHolder, boolean paramBoolean)
  {
    if ((paramViewHolder.mCheckmarkView instanceof Checkable)) {
      ((Checkable)paramViewHolder.mCheckmarkView).setChecked(paramBoolean);
    }
  }
  
  public void onAnimateItemFocused(ViewHolder paramViewHolder, boolean paramBoolean) {}
  
  public void onAnimateItemPressed(ViewHolder paramViewHolder, boolean paramBoolean)
  {
    paramViewHolder.press(paramBoolean);
  }
  
  public void onAnimateItemPressedCancelled(ViewHolder paramViewHolder)
  {
    paramViewHolder.press(false);
  }
  
  public void onBindActivatorView(ViewHolder paramViewHolder, GuidedAction paramGuidedAction)
  {
    if ((paramGuidedAction instanceof GuidedDatePickerAction))
    {
      paramGuidedAction = (GuidedDatePickerAction)paramGuidedAction;
      paramViewHolder = (DatePicker)paramViewHolder.mActivatorView;
      paramViewHolder.setDatePickerFormat(paramGuidedAction.getDatePickerFormat());
      if (paramGuidedAction.getMinDate() != Long.MIN_VALUE) {
        paramViewHolder.setMinDate(paramGuidedAction.getMinDate());
      }
      if (paramGuidedAction.getMaxDate() != Long.MAX_VALUE) {
        paramViewHolder.setMaxDate(paramGuidedAction.getMaxDate());
      }
      Calendar localCalendar = Calendar.getInstance();
      localCalendar.setTimeInMillis(paramGuidedAction.getDate());
      paramViewHolder.updateDate(localCalendar.get(1), localCalendar.get(2), localCalendar.get(5), false);
    }
  }
  
  public void onBindCheckMarkView(ViewHolder paramViewHolder, GuidedAction paramGuidedAction)
  {
    if (paramGuidedAction.getCheckSetId() != 0)
    {
      paramViewHolder.mCheckmarkView.setVisibility(0);
      if (paramGuidedAction.getCheckSetId() == -1) {}
      for (int i = 16843290;; i = 16843289)
      {
        Context localContext = paramViewHolder.mCheckmarkView.getContext();
        Drawable localDrawable = null;
        TypedValue localTypedValue = new TypedValue();
        if (localContext.getTheme().resolveAttribute(i, localTypedValue, true)) {
          localDrawable = ContextCompat.getDrawable(localContext, localTypedValue.resourceId);
        }
        paramViewHolder.mCheckmarkView.setImageDrawable(localDrawable);
        if ((paramViewHolder.mCheckmarkView instanceof Checkable)) {
          ((Checkable)paramViewHolder.mCheckmarkView).setChecked(paramGuidedAction.isChecked());
        }
        return;
      }
    }
    paramViewHolder.mCheckmarkView.setVisibility(8);
  }
  
  public void onBindChevronView(ViewHolder paramViewHolder, GuidedAction paramGuidedAction)
  {
    boolean bool1 = paramGuidedAction.hasNext();
    boolean bool2 = paramGuidedAction.hasSubActions();
    if ((bool1) || (bool2))
    {
      paramViewHolder.mChevronView.setVisibility(0);
      ImageView localImageView = paramViewHolder.mChevronView;
      if (paramGuidedAction.isEnabled())
      {
        f = this.mEnabledChevronAlpha;
        localImageView.setAlpha(f);
        if (!bool1) {
          break label103;
        }
        if ((this.mMainView == null) || (this.mMainView.getLayoutDirection() != 1)) {
          break label98;
        }
      }
      label98:
      for (float f = 180.0F;; f = 0.0F)
      {
        paramViewHolder.mChevronView.setRotation(f);
        return;
        f = this.mDisabledChevronAlpha;
        break;
      }
      label103:
      if (paramGuidedAction == this.mExpandedAction)
      {
        paramViewHolder.mChevronView.setRotation(270.0F);
        return;
      }
      paramViewHolder.mChevronView.setRotation(90.0F);
      return;
    }
    paramViewHolder.mChevronView.setVisibility(8);
  }
  
  public void onBindViewHolder(ViewHolder paramViewHolder, GuidedAction paramGuidedAction)
  {
    paramViewHolder.mAction = paramGuidedAction;
    TextView localTextView;
    float f;
    int i;
    if (paramViewHolder.mTitleView != null)
    {
      paramViewHolder.mTitleView.setInputType(paramGuidedAction.getInputType());
      paramViewHolder.mTitleView.setText(paramGuidedAction.getTitle());
      localTextView = paramViewHolder.mTitleView;
      if (paramGuidedAction.isEnabled())
      {
        f = this.mEnabledTextAlpha;
        localTextView.setAlpha(f);
        paramViewHolder.mTitleView.setFocusable(false);
        paramViewHolder.mTitleView.setClickable(false);
        paramViewHolder.mTitleView.setLongClickable(false);
      }
    }
    else
    {
      if (paramViewHolder.mDescriptionView != null)
      {
        paramViewHolder.mDescriptionView.setInputType(paramGuidedAction.getDescriptionInputType());
        paramViewHolder.mDescriptionView.setText(paramGuidedAction.getDescription());
        localTextView = paramViewHolder.mDescriptionView;
        if (!TextUtils.isEmpty(paramGuidedAction.getDescription())) {
          break label331;
        }
        i = 8;
        label131:
        localTextView.setVisibility(i);
        localTextView = paramViewHolder.mDescriptionView;
        if (!paramGuidedAction.isEnabled()) {
          break label337;
        }
        f = this.mEnabledDescriptionAlpha;
        label156:
        localTextView.setAlpha(f);
        paramViewHolder.mDescriptionView.setFocusable(false);
        paramViewHolder.mDescriptionView.setClickable(false);
        paramViewHolder.mDescriptionView.setLongClickable(false);
      }
      if (paramViewHolder.mCheckmarkView != null) {
        onBindCheckMarkView(paramViewHolder, paramGuidedAction);
      }
      setIcon(paramViewHolder.mIconView, paramGuidedAction);
      if (!paramGuidedAction.hasMultilineDescription()) {
        break label345;
      }
      if (paramViewHolder.mTitleView != null)
      {
        setMaxLines(paramViewHolder.mTitleView, this.mTitleMaxLines);
        if (paramViewHolder.mDescriptionView != null) {
          paramViewHolder.mDescriptionView.setMaxHeight(getDescriptionMaxHeight(paramViewHolder.itemView.getContext(), paramViewHolder.mTitleView));
        }
      }
      label263:
      if (paramViewHolder.mActivatorView != null) {
        onBindActivatorView(paramViewHolder, paramGuidedAction);
      }
      setEditingMode(paramViewHolder, false, false);
      if (!paramGuidedAction.isFocusable()) {
        break label384;
      }
      paramViewHolder.itemView.setFocusable(true);
      ((ViewGroup)paramViewHolder.itemView).setDescendantFocusability(131072);
    }
    for (;;)
    {
      setupImeOptions(paramViewHolder, paramGuidedAction);
      updateChevronAndVisibility(paramViewHolder);
      return;
      f = this.mDisabledTextAlpha;
      break;
      label331:
      i = 0;
      break label131;
      label337:
      f = this.mDisabledDescriptionAlpha;
      break label156;
      label345:
      if (paramViewHolder.mTitleView != null) {
        setMaxLines(paramViewHolder.mTitleView, this.mTitleMinLines);
      }
      if (paramViewHolder.mDescriptionView == null) {
        break label263;
      }
      setMaxLines(paramViewHolder.mDescriptionView, this.mDescriptionMinLines);
      break label263;
      label384:
      paramViewHolder.itemView.setFocusable(false);
      ((ViewGroup)paramViewHolder.itemView).setDescendantFocusability(393216);
    }
  }
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup)
  {
    float f = paramLayoutInflater.getContext().getTheme().obtainStyledAttributes(R.styleable.LeanbackGuidedStepTheme).getFloat(R.styleable.LeanbackGuidedStepTheme_guidedStepKeyline, 40.0F);
    this.mMainView = ((ViewGroup)paramLayoutInflater.inflate(onProvideLayoutId(), paramViewGroup, false));
    paramLayoutInflater = this.mMainView;
    int i;
    if (this.mButtonActions)
    {
      i = R.id.guidedactions_content2;
      this.mContentView = paramLayoutInflater.findViewById(i);
      paramLayoutInflater = this.mMainView;
      if (!this.mButtonActions) {
        break label376;
      }
      i = R.id.guidedactions_list_background2;
      label84:
      this.mBgView = paramLayoutInflater.findViewById(i);
      if (!(this.mMainView instanceof VerticalGridView)) {
        break label384;
      }
      this.mActionsGridView = ((VerticalGridView)this.mMainView);
    }
    for (;;)
    {
      this.mActionsGridView.setFocusable(false);
      this.mActionsGridView.setFocusableInTouchMode(false);
      paramLayoutInflater = this.mMainView.getContext();
      paramViewGroup = new TypedValue();
      this.mEnabledChevronAlpha = getFloat(paramLayoutInflater, paramViewGroup, R.attr.guidedActionEnabledChevronAlpha);
      this.mDisabledChevronAlpha = getFloat(paramLayoutInflater, paramViewGroup, R.attr.guidedActionDisabledChevronAlpha);
      this.mTitleMinLines = getInteger(paramLayoutInflater, paramViewGroup, R.attr.guidedActionTitleMinLines);
      this.mTitleMaxLines = getInteger(paramLayoutInflater, paramViewGroup, R.attr.guidedActionTitleMaxLines);
      this.mDescriptionMinLines = getInteger(paramLayoutInflater, paramViewGroup, R.attr.guidedActionDescriptionMinLines);
      this.mVerticalPadding = getDimension(paramLayoutInflater, paramViewGroup, R.attr.guidedActionVerticalPadding);
      this.mDisplayHeight = ((WindowManager)paramLayoutInflater.getSystemService("window")).getDefaultDisplay().getHeight();
      this.mEnabledTextAlpha = Float.valueOf(paramLayoutInflater.getResources().getString(R.string.lb_guidedactions_item_unselected_text_alpha)).floatValue();
      this.mDisabledTextAlpha = Float.valueOf(paramLayoutInflater.getResources().getString(R.string.lb_guidedactions_item_disabled_text_alpha)).floatValue();
      this.mEnabledDescriptionAlpha = Float.valueOf(paramLayoutInflater.getResources().getString(R.string.lb_guidedactions_item_unselected_description_text_alpha)).floatValue();
      this.mDisabledDescriptionAlpha = Float.valueOf(paramLayoutInflater.getResources().getString(R.string.lb_guidedactions_item_disabled_description_text_alpha)).floatValue();
      this.mKeyLinePercent = GuidanceStylingRelativeLayout.getKeyLinePercent(paramLayoutInflater);
      if ((this.mContentView instanceof GuidedActionsRelativeLayout)) {
        ((GuidedActionsRelativeLayout)this.mContentView).setInterceptKeyEventListener(new GuidedActionsRelativeLayout.InterceptKeyEventListener()
        {
          public boolean onInterceptKeyEvent(KeyEvent paramAnonymousKeyEvent)
          {
            if ((paramAnonymousKeyEvent.getKeyCode() == 4) && (paramAnonymousKeyEvent.getAction() == 1) && (GuidedActionsStylist.this.mExpandedAction != null) && (((GuidedActionsStylist.this.mExpandedAction.hasSubActions()) && (GuidedActionsStylist.this.isBackKeyToCollapseSubActions())) || ((GuidedActionsStylist.this.mExpandedAction.hasEditableActivatorView()) && (GuidedActionsStylist.this.isBackKeyToCollapseActivatorView()))))
            {
              GuidedActionsStylist.this.collapseAction(true);
              return true;
            }
            return false;
          }
        });
      }
      return this.mMainView;
      i = R.id.guidedactions_content;
      break;
      label376:
      i = R.id.guidedactions_list_background;
      break label84;
      label384:
      paramLayoutInflater = this.mMainView;
      if (this.mButtonActions) {}
      for (i = R.id.guidedactions_list2;; i = R.id.guidedactions_list)
      {
        this.mActionsGridView = ((VerticalGridView)paramLayoutInflater.findViewById(i));
        if (this.mActionsGridView != null) {
          break;
        }
        throw new IllegalStateException("No ListView exists.");
      }
      this.mActionsGridView.setWindowAlignmentOffsetPercent(f);
      this.mActionsGridView.setWindowAlignment(0);
      if (!this.mButtonActions)
      {
        this.mSubActionsGridView = ((VerticalGridView)this.mMainView.findViewById(R.id.guidedactions_sub_list));
        this.mSubActionsBackground = this.mMainView.findViewById(R.id.guidedactions_sub_list_background);
      }
    }
  }
  
  public ViewHolder onCreateViewHolder(ViewGroup paramViewGroup)
  {
    boolean bool = false;
    View localView = LayoutInflater.from(paramViewGroup.getContext()).inflate(onProvideItemLayoutId(), paramViewGroup, false);
    if (paramViewGroup == this.mSubActionsGridView) {
      bool = true;
    }
    return new ViewHolder(localView, bool);
  }
  
  public ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
  {
    boolean bool = false;
    if (paramInt == 0) {
      return onCreateViewHolder(paramViewGroup);
    }
    View localView = LayoutInflater.from(paramViewGroup.getContext()).inflate(onProvideItemLayoutId(paramInt), paramViewGroup, false);
    if (paramViewGroup == this.mSubActionsGridView) {
      bool = true;
    }
    return new ViewHolder(localView, bool);
  }
  
  public void onDestroyView()
  {
    this.mExpandedAction = null;
    this.mExpandTransition = null;
    this.mActionsGridView = null;
    this.mSubActionsGridView = null;
    this.mSubActionsBackground = null;
    this.mContentView = null;
    this.mBgView = null;
    this.mMainView = null;
  }
  
  void onEditActivatorView(final ViewHolder paramViewHolder, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramBoolean1)
    {
      startExpanded(paramViewHolder, paramBoolean2);
      paramViewHolder.itemView.setFocusable(false);
      paramViewHolder.mActivatorView.requestFocus();
      paramViewHolder.mActivatorView.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          if (!GuidedActionsStylist.this.isInExpandTransition()) {
            ((GuidedActionAdapter)GuidedActionsStylist.this.getActionsGridView().getAdapter()).performOnActionClick(paramViewHolder);
          }
        }
      });
      return;
    }
    if ((onUpdateActivatorView(paramViewHolder, paramViewHolder.getAction())) && (this.mEditListener != null)) {
      this.mEditListener.onGuidedActionEditedAndProceed(paramViewHolder.getAction());
    }
    paramViewHolder.itemView.setFocusable(true);
    paramViewHolder.itemView.requestFocus();
    startExpanded(null, paramBoolean2);
    paramViewHolder.mActivatorView.setOnClickListener(null);
    paramViewHolder.mActivatorView.setClickable(false);
  }
  
  @Deprecated
  protected void onEditingModeChange(ViewHolder paramViewHolder, GuidedAction paramGuidedAction, boolean paramBoolean) {}
  
  @CallSuper
  protected void onEditingModeChange(ViewHolder paramViewHolder, boolean paramBoolean1, boolean paramBoolean2)
  {
    GuidedAction localGuidedAction = paramViewHolder.getAction();
    TextView localTextView1 = paramViewHolder.getTitleView();
    TextView localTextView2 = paramViewHolder.getDescriptionView();
    if (paramBoolean1)
    {
      CharSequence localCharSequence = localGuidedAction.getEditTitle();
      if ((localTextView1 != null) && (localCharSequence != null)) {
        localTextView1.setText(localCharSequence);
      }
      localCharSequence = localGuidedAction.getEditDescription();
      if ((localTextView2 != null) && (localCharSequence != null)) {
        localTextView2.setText(localCharSequence);
      }
      if (localGuidedAction.isDescriptionEditable())
      {
        if (localTextView2 != null)
        {
          localTextView2.setVisibility(0);
          localTextView2.setInputType(localGuidedAction.getDescriptionEditInputType());
        }
        paramViewHolder.mEditingMode = 2;
      }
      for (;;)
      {
        onEditingModeChange(paramViewHolder, localGuidedAction, paramBoolean1);
        return;
        if (localGuidedAction.isEditable())
        {
          if (localTextView1 != null) {
            localTextView1.setInputType(localGuidedAction.getEditInputType());
          }
          paramViewHolder.mEditingMode = 1;
        }
        else if (paramViewHolder.mActivatorView != null)
        {
          onEditActivatorView(paramViewHolder, paramBoolean1, paramBoolean2);
          paramViewHolder.mEditingMode = 3;
        }
      }
    }
    if (localTextView1 != null) {
      localTextView1.setText(localGuidedAction.getTitle());
    }
    if (localTextView2 != null) {
      localTextView2.setText(localGuidedAction.getDescription());
    }
    int i;
    if (paramViewHolder.mEditingMode == 2) {
      if (localTextView2 != null)
      {
        if (!TextUtils.isEmpty(localGuidedAction.getDescription())) {
          break label249;
        }
        i = 8;
        label224:
        localTextView2.setVisibility(i);
        localTextView2.setInputType(localGuidedAction.getDescriptionInputType());
      }
    }
    for (;;)
    {
      paramViewHolder.mEditingMode = 0;
      break;
      label249:
      i = 0;
      break label224;
      if (paramViewHolder.mEditingMode == 1)
      {
        if (localTextView1 != null) {
          localTextView1.setInputType(localGuidedAction.getInputType());
        }
      }
      else if ((paramViewHolder.mEditingMode == 3) && (paramViewHolder.mActivatorView != null)) {
        onEditActivatorView(paramViewHolder, paramBoolean1, paramBoolean2);
      }
    }
  }
  
  public void onImeAppearing(@NonNull List<Animator> paramList) {}
  
  public void onImeDisappearing(@NonNull List<Animator> paramList) {}
  
  public int onProvideItemLayoutId()
  {
    return R.layout.lb_guidedactions_item;
  }
  
  public int onProvideItemLayoutId(int paramInt)
  {
    if (paramInt == 0) {
      return onProvideItemLayoutId();
    }
    if (paramInt == 1) {
      return R.layout.lb_guidedactions_datepicker_item;
    }
    throw new RuntimeException("ViewType " + paramInt + " not supported in GuidedActionsStylist");
  }
  
  public int onProvideLayoutId()
  {
    if (this.mButtonActions) {
      return R.layout.lb_guidedbuttonactions;
    }
    return R.layout.lb_guidedactions;
  }
  
  public boolean onUpdateActivatorView(ViewHolder paramViewHolder, GuidedAction paramGuidedAction)
  {
    if ((paramGuidedAction instanceof GuidedDatePickerAction))
    {
      paramGuidedAction = (GuidedDatePickerAction)paramGuidedAction;
      paramViewHolder = (DatePicker)paramViewHolder.mActivatorView;
      if (paramGuidedAction.getDate() != paramViewHolder.getDate())
      {
        paramGuidedAction.setDate(paramViewHolder.getDate());
        return true;
      }
    }
    return false;
  }
  
  public void onUpdateExpandedViewHolder(ViewHolder paramViewHolder)
  {
    if (paramViewHolder == null)
    {
      this.mExpandedAction = null;
      this.mActionsGridView.setPruneChild(true);
    }
    for (;;)
    {
      this.mActionsGridView.setAnimateChildLayout(false);
      int j = this.mActionsGridView.getChildCount();
      int i = 0;
      while (i < j)
      {
        updateChevronAndVisibility((ViewHolder)this.mActionsGridView.getChildViewHolder(this.mActionsGridView.getChildAt(i)));
        i += 1;
      }
      if (paramViewHolder.getAction() != this.mExpandedAction)
      {
        this.mExpandedAction = paramViewHolder.getAction();
        this.mActionsGridView.setPruneChild(false);
      }
    }
  }
  
  void onUpdateSubActionsGridView(GuidedAction paramGuidedAction, boolean paramBoolean)
  {
    ViewGroup.MarginLayoutParams localMarginLayoutParams;
    GuidedActionAdapter localGuidedActionAdapter;
    if (this.mSubActionsGridView != null)
    {
      localMarginLayoutParams = (ViewGroup.MarginLayoutParams)this.mSubActionsGridView.getLayoutParams();
      localGuidedActionAdapter = (GuidedActionAdapter)this.mSubActionsGridView.getAdapter();
      if (paramBoolean)
      {
        localMarginLayoutParams.topMargin = -2;
        localMarginLayoutParams.height = -1;
        this.mSubActionsGridView.setLayoutParams(localMarginLayoutParams);
        this.mSubActionsGridView.setVisibility(0);
        this.mSubActionsBackground.setVisibility(0);
        this.mSubActionsGridView.requestFocus();
        localGuidedActionAdapter.setActions(paramGuidedAction.getSubActions());
      }
    }
    else
    {
      return;
    }
    int i = ((GuidedActionAdapter)this.mActionsGridView.getAdapter()).indexOf(paramGuidedAction);
    localMarginLayoutParams.topMargin = this.mActionsGridView.getLayoutManager().findViewByPosition(i).getBottom();
    localMarginLayoutParams.height = 0;
    this.mSubActionsGridView.setVisibility(4);
    this.mSubActionsBackground.setVisibility(4);
    this.mSubActionsGridView.setLayoutParams(localMarginLayoutParams);
    localGuidedActionAdapter.setActions(Collections.EMPTY_LIST);
    this.mActionsGridView.requestFocus();
  }
  
  public void openInEditMode(GuidedAction paramGuidedAction)
  {
    final GuidedActionAdapter localGuidedActionAdapter = (GuidedActionAdapter)getActionsGridView().getAdapter();
    int i = localGuidedActionAdapter.getActions().indexOf(paramGuidedAction);
    if ((i < 0) || (!paramGuidedAction.isEditable())) {
      return;
    }
    getActionsGridView().setSelectedPosition(i, new ViewHolderTask()
    {
      public void run(RecyclerView.ViewHolder paramAnonymousViewHolder)
      {
        paramAnonymousViewHolder = (GuidedActionsStylist.ViewHolder)paramAnonymousViewHolder;
        localGuidedActionAdapter.mGroup.openIme(localGuidedActionAdapter, paramAnonymousViewHolder);
      }
    });
  }
  
  public void setAsButtonActions()
  {
    if (this.mMainView != null) {
      throw new IllegalStateException("setAsButtonActions() must be called before creating views");
    }
    this.mButtonActions = true;
  }
  
  public final void setBackKeyToCollapseActivatorView(boolean paramBoolean)
  {
    this.mBackToCollapseActivatorView = paramBoolean;
  }
  
  public final void setBackKeyToCollapseSubActions(boolean paramBoolean)
  {
    this.mBackToCollapseSubActions = paramBoolean;
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public void setEditListener(GuidedActionAdapter.EditListener paramEditListener)
  {
    this.mEditListener = paramEditListener;
  }
  
  @Deprecated
  public void setEditingMode(ViewHolder paramViewHolder, GuidedAction paramGuidedAction, boolean paramBoolean)
  {
    if ((paramBoolean != paramViewHolder.isInEditing()) && (isInExpandTransition())) {
      onEditingModeChange(paramViewHolder, paramGuidedAction, paramBoolean);
    }
  }
  
  void setEditingMode(ViewHolder paramViewHolder, boolean paramBoolean)
  {
    setEditingMode(paramViewHolder, paramBoolean, true);
  }
  
  void setEditingMode(ViewHolder paramViewHolder, boolean paramBoolean1, boolean paramBoolean2)
  {
    if ((paramBoolean1 != paramViewHolder.isInEditing()) && (!isInExpandTransition())) {
      onEditingModeChange(paramViewHolder, paramBoolean1, paramBoolean2);
    }
  }
  
  @Deprecated
  public void setExpandedViewHolder(ViewHolder paramViewHolder)
  {
    if (paramViewHolder == null) {}
    for (paramViewHolder = null;; paramViewHolder = paramViewHolder.getAction())
    {
      expandAction(paramViewHolder, isExpandTransitionSupported());
      return;
    }
  }
  
  protected void setupImeOptions(ViewHolder paramViewHolder, GuidedAction paramGuidedAction)
  {
    setupNextImeOptions(paramViewHolder.getEditableTitleView());
    setupNextImeOptions(paramViewHolder.getEditableDescriptionView());
  }
  
  void startExpanded(ViewHolder paramViewHolder, boolean paramBoolean)
  {
    Object localObject2 = null;
    int j = this.mActionsGridView.getChildCount();
    int i = 0;
    Object localObject1 = localObject2;
    if (i < j)
    {
      localObject1 = (ViewHolder)this.mActionsGridView.getChildViewHolder(this.mActionsGridView.getChildAt(i));
      if ((paramViewHolder != null) || (((ViewHolder)localObject1).itemView.getVisibility() != 0)) {
        break label68;
      }
    }
    label68:
    boolean bool1;
    boolean bool2;
    label138:
    label218:
    label221:
    label296:
    label313:
    label348:
    label372:
    label515:
    do
    {
      for (;;)
      {
        if (localObject1 == null)
        {
          return;
          if ((paramViewHolder == null) || (((ViewHolder)localObject1).getAction() != paramViewHolder.getAction()))
          {
            i += 1;
            break;
          }
        }
      }
      float f;
      Object localObject3;
      Object localObject4;
      Object localObject5;
      Object localObject6;
      Object localObject7;
      ViewHolder localViewHolder;
      if (paramViewHolder != null)
      {
        bool1 = true;
        bool2 = ((ViewHolder)localObject1).getAction().hasSubActions();
        if (!paramBoolean) {
          break label515;
        }
        localObject2 = TransitionHelper.createTransitionSet(false);
        if (!bool2) {
          break label296;
        }
        f = ((ViewHolder)localObject1).itemView.getHeight();
        localObject3 = TransitionHelper.createFadeAndShortSlide(112, f);
        TransitionHelper.setEpicenterCallback(localObject3, new TransitionEpicenterCallback()
        {
          Rect mRect = new Rect();
          
          public Rect onGetEpicenter(Object paramAnonymousObject)
          {
            int i = GuidedActionsStylist.this.getKeyLine();
            this.mRect.set(0, i, 0, i);
            return this.mRect;
          }
        });
        localObject4 = TransitionHelper.createChangeTransform();
        localObject5 = TransitionHelper.createChangeBounds(false);
        localObject6 = TransitionHelper.createFadeTransition(3);
        localObject7 = TransitionHelper.createChangeBounds(false);
        if (paramViewHolder != null) {
          break label313;
        }
        TransitionHelper.setStartDelay(localObject3, 150L);
        TransitionHelper.setStartDelay(localObject4, 100L);
        TransitionHelper.setStartDelay(localObject5, 100L);
        TransitionHelper.setStartDelay(localObject7, 100L);
        i = 0;
        if (i >= j) {
          break label372;
        }
        localViewHolder = (ViewHolder)this.mActionsGridView.getChildViewHolder(this.mActionsGridView.getChildAt(i));
        if (localViewHolder != localObject1) {
          break label348;
        }
        if (bool2)
        {
          TransitionHelper.include(localObject4, localViewHolder.itemView);
          TransitionHelper.include(localObject5, localViewHolder.itemView);
        }
      }
      for (;;)
      {
        i += 1;
        break label221;
        bool1 = false;
        break;
        f = ((ViewHolder)localObject1).itemView.getHeight() * 0.5F;
        break label138;
        TransitionHelper.setStartDelay(localObject6, 100L);
        TransitionHelper.setStartDelay(localObject7, 50L);
        TransitionHelper.setStartDelay(localObject4, 50L);
        TransitionHelper.setStartDelay(localObject5, 50L);
        break label218;
        TransitionHelper.include(localObject3, localViewHolder.itemView);
        TransitionHelper.exclude(localObject6, localViewHolder.itemView, true);
      }
      TransitionHelper.include(localObject7, this.mSubActionsGridView);
      TransitionHelper.include(localObject7, this.mSubActionsBackground);
      TransitionHelper.addTransition(localObject2, localObject3);
      if (bool2)
      {
        TransitionHelper.addTransition(localObject2, localObject4);
        TransitionHelper.addTransition(localObject2, localObject5);
      }
      TransitionHelper.addTransition(localObject2, localObject6);
      TransitionHelper.addTransition(localObject2, localObject7);
      this.mExpandTransition = localObject2;
      TransitionHelper.addTransitionListener(this.mExpandTransition, new TransitionListener()
      {
        public void onTransitionEnd(Object paramAnonymousObject)
        {
          GuidedActionsStylist.this.mExpandTransition = null;
        }
      });
      if ((bool1) && (bool2))
      {
        i = paramViewHolder.itemView.getBottom();
        this.mSubActionsGridView.offsetTopAndBottom(i - this.mSubActionsGridView.getTop());
        this.mSubActionsBackground.offsetTopAndBottom(i - this.mSubActionsBackground.getTop());
      }
      TransitionHelper.beginDelayedTransition(this.mMainView, this.mExpandTransition);
      onUpdateExpandedViewHolder(paramViewHolder);
    } while (!bool2);
    onUpdateSubActionsGridView(((ViewHolder)localObject1).getAction(), bool1);
  }
  
  @Deprecated
  public void startExpandedTransition(ViewHolder paramViewHolder)
  {
    if (paramViewHolder == null) {}
    for (paramViewHolder = null;; paramViewHolder = paramViewHolder.getAction())
    {
      expandAction(paramViewHolder, isExpandTransitionSupported());
      return;
    }
  }
  
  public static class ViewHolder
    extends RecyclerView.ViewHolder
    implements FacetProvider
  {
    GuidedAction mAction;
    View mActivatorView;
    ImageView mCheckmarkView;
    ImageView mChevronView;
    private View mContentView;
    final View.AccessibilityDelegate mDelegate = new View.AccessibilityDelegate()
    {
      public void onInitializeAccessibilityEvent(View paramAnonymousView, AccessibilityEvent paramAnonymousAccessibilityEvent)
      {
        super.onInitializeAccessibilityEvent(paramAnonymousView, paramAnonymousAccessibilityEvent);
        if ((GuidedActionsStylist.ViewHolder.this.mAction != null) && (GuidedActionsStylist.ViewHolder.this.mAction.isChecked())) {}
        for (boolean bool = true;; bool = false)
        {
          paramAnonymousAccessibilityEvent.setChecked(bool);
          return;
        }
      }
      
      public void onInitializeAccessibilityNodeInfo(View paramAnonymousView, AccessibilityNodeInfo paramAnonymousAccessibilityNodeInfo)
      {
        boolean bool2 = true;
        super.onInitializeAccessibilityNodeInfo(paramAnonymousView, paramAnonymousAccessibilityNodeInfo);
        if ((GuidedActionsStylist.ViewHolder.this.mAction != null) && (GuidedActionsStylist.ViewHolder.this.mAction.getCheckSetId() != 0))
        {
          bool1 = true;
          paramAnonymousAccessibilityNodeInfo.setCheckable(bool1);
          if ((GuidedActionsStylist.ViewHolder.this.mAction == null) || (!GuidedActionsStylist.ViewHolder.this.mAction.isChecked())) {
            break label76;
          }
        }
        label76:
        for (boolean bool1 = bool2;; bool1 = false)
        {
          paramAnonymousAccessibilityNodeInfo.setChecked(bool1);
          return;
          bool1 = false;
          break;
        }
      }
    };
    TextView mDescriptionView;
    int mEditingMode = 0;
    ImageView mIconView;
    private final boolean mIsSubAction;
    Animator mPressAnimator;
    TextView mTitleView;
    
    public ViewHolder(View paramView)
    {
      this(paramView, false);
    }
    
    public ViewHolder(View paramView, boolean paramBoolean)
    {
      super();
      this.mContentView = paramView.findViewById(R.id.guidedactions_item_content);
      this.mTitleView = ((TextView)paramView.findViewById(R.id.guidedactions_item_title));
      this.mActivatorView = paramView.findViewById(R.id.guidedactions_activator_item);
      this.mDescriptionView = ((TextView)paramView.findViewById(R.id.guidedactions_item_description));
      this.mIconView = ((ImageView)paramView.findViewById(R.id.guidedactions_item_icon));
      this.mCheckmarkView = ((ImageView)paramView.findViewById(R.id.guidedactions_item_checkmark));
      this.mChevronView = ((ImageView)paramView.findViewById(R.id.guidedactions_item_chevron));
      this.mIsSubAction = paramBoolean;
      paramView.setAccessibilityDelegate(this.mDelegate);
    }
    
    public GuidedAction getAction()
    {
      return this.mAction;
    }
    
    public ImageView getCheckmarkView()
    {
      return this.mCheckmarkView;
    }
    
    public ImageView getChevronView()
    {
      return this.mChevronView;
    }
    
    public View getContentView()
    {
      return this.mContentView;
    }
    
    public TextView getDescriptionView()
    {
      return this.mDescriptionView;
    }
    
    public EditText getEditableDescriptionView()
    {
      if ((this.mDescriptionView instanceof EditText)) {
        return (EditText)this.mDescriptionView;
      }
      return null;
    }
    
    public EditText getEditableTitleView()
    {
      if ((this.mTitleView instanceof EditText)) {
        return (EditText)this.mTitleView;
      }
      return null;
    }
    
    public View getEditingView()
    {
      switch (this.mEditingMode)
      {
      default: 
        return null;
      case 1: 
        return this.mTitleView;
      case 2: 
        return this.mDescriptionView;
      }
      return this.mActivatorView;
    }
    
    public Object getFacet(Class<?> paramClass)
    {
      if (paramClass == ItemAlignmentFacet.class) {
        return GuidedActionsStylist.sGuidedActionItemAlignFacet;
      }
      return null;
    }
    
    public ImageView getIconView()
    {
      return this.mIconView;
    }
    
    public TextView getTitleView()
    {
      return this.mTitleView;
    }
    
    public boolean isInEditing()
    {
      return this.mEditingMode != 0;
    }
    
    public boolean isInEditingActivatorView()
    {
      return this.mEditingMode == 3;
    }
    
    public boolean isInEditingDescription()
    {
      return this.mEditingMode == 2;
    }
    
    public boolean isInEditingText()
    {
      return (this.mEditingMode == 1) || (this.mEditingMode == 2);
    }
    
    public boolean isInEditingTitle()
    {
      return this.mEditingMode == 1;
    }
    
    public boolean isSubAction()
    {
      return this.mIsSubAction;
    }
    
    void press(boolean paramBoolean)
    {
      if (this.mPressAnimator != null)
      {
        this.mPressAnimator.cancel();
        this.mPressAnimator = null;
      }
      if (paramBoolean) {}
      for (int i = R.attr.guidedActionPressedAnimation;; i = R.attr.guidedActionUnpressedAnimation)
      {
        Context localContext = this.itemView.getContext();
        TypedValue localTypedValue = new TypedValue();
        if (localContext.getTheme().resolveAttribute(i, localTypedValue, true))
        {
          this.mPressAnimator = AnimatorInflater.loadAnimator(localContext, localTypedValue.resourceId);
          this.mPressAnimator.setTarget(this.itemView);
          this.mPressAnimator.addListener(new AnimatorListenerAdapter()
          {
            public void onAnimationEnd(Animator paramAnonymousAnimator)
            {
              GuidedActionsStylist.ViewHolder.this.mPressAnimator = null;
            }
          });
          this.mPressAnimator.start();
        }
        return;
      }
    }
    
    void setActivated(boolean paramBoolean)
    {
      this.mActivatorView.setActivated(paramBoolean);
      GuidedActionItemContainer localGuidedActionItemContainer;
      if ((this.itemView instanceof GuidedActionItemContainer))
      {
        localGuidedActionItemContainer = (GuidedActionItemContainer)this.itemView;
        if (paramBoolean) {
          break label38;
        }
      }
      label38:
      for (paramBoolean = true;; paramBoolean = false)
      {
        localGuidedActionItemContainer.setFocusOutAllowed(paramBoolean);
        return;
      }
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/GuidedActionsStylist.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */