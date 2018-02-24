package com.google.android.tvlauncher.home;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Outline;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalFocusChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import com.google.android.tvlauncher.home.util.ChannelStateSettings;
import com.google.android.tvlauncher.util.ScaleFocusHandler;
import com.google.android.tvlauncher.util.Util;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

public class ChannelView
  extends LinearLayout
{
  private static final boolean DEBUG = false;
  public static final int STATE_ACTIONS_NOT_SELECTED = 7;
  public static final int STATE_ACTIONS_SELECTED = 6;
  public static final int STATE_DEFAULT_ABOVE_SELECTED = 2;
  public static final int STATE_DEFAULT_BELOW_SELECTED = 3;
  public static final int STATE_DEFAULT_NOT_SELECTED = 1;
  public static final int STATE_DEFAULT_SELECTED = 0;
  public static final int STATE_MOVE_NOT_SELECTED = 9;
  public static final int STATE_MOVE_SELECTED = 8;
  public static final int STATE_ZOOMED_OUT_NOT_SELECTED = 5;
  public static final int STATE_ZOOMED_OUT_SELECTED = 4;
  private static final String TAG = "ChannelView";
  private Drawable mActionMoveDownIcon;
  private Drawable mActionMoveUpDownIcon;
  private Drawable mActionMoveUpIcon;
  private View mActionsHint;
  private boolean mAllowMoving = true;
  private boolean mAllowRemoving = true;
  private boolean mAllowZoomOut = true;
  private View mChannelActionsPaddingView;
  private ImageView mChannelLogo;
  private View mChannelLogoContainer;
  private int mChannelLogoDefaultSize;
  private int mChannelLogoZoomedOutMargin;
  private int mChannelLogoZoomedOutSize;
  private TextView mChannelTitle;
  private boolean mIsRtl = false;
  private View mItemMetaContainer;
  private HorizontalGridView mItemsList;
  private FadingEdgeContainer mItemsListContainer;
  private int mItemsListMarginStart;
  private int mItemsListZoomedOutMarginStart;
  private ImageView mMoveButton;
  private View mMoveChannelPaddingView;
  private View mMovingChannelBackground;
  private View mNoMoveActionPaddingView;
  private OnMoveChannelDownListener mOnMoveChannelDownListener;
  private OnMoveChannelUpListener mOnMoveChannelUpListener;
  private OnPerformMainActionListener mOnPerformMainActionListener;
  private OnRemoveListener mOnRemoveListener;
  private OnStateChangeGesturePerformedListener mOnStateChangeGesturePerformedListener;
  private ImageView mRemoveButton;
  private boolean mShowItemMeta = true;
  private int mState = 1;
  private SparseArray<ChannelStateSettings> mStateSettings;
  private TextView mZoomedOutChannelTitle;
  private View mZoomedOutPaddingView;
  
  public ChannelView(Context paramContext)
  {
    super(paramContext);
  }
  
  public ChannelView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  public ChannelView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
  }
  
  public static String directionToString(int paramInt)
  {
    String str;
    switch (paramInt)
    {
    default: 
      str = "FOCUS_UNKNOWN";
    }
    for (;;)
    {
      return str + " (" + paramInt + ")";
      str = "FOCUS_UP";
      continue;
      str = "FOCUS_DOWN";
      continue;
      str = "FOCUS_LEFT";
      continue;
      str = "FOCUS_RIGHT";
    }
  }
  
  private boolean isFocusableChild(View paramView)
  {
    if (paramView == null) {}
    do
    {
      return false;
      paramView = paramView.getParent();
    } while ((paramView != this.mItemsList) && (paramView != this.mChannelLogo.getParent()) && (paramView != this));
    return true;
  }
  
  private boolean isZoomedOutState(int paramInt)
  {
    switch (paramInt)
    {
    case 0: 
    case 1: 
    case 2: 
    case 3: 
    default: 
      return false;
    }
    return true;
  }
  
  private void onChannelSelected(boolean paramBoolean)
  {
    Object localObject2 = null;
    Object localObject1;
    if (paramBoolean)
    {
      localObject1 = localObject2;
      switch (this.mState)
      {
      default: 
        localObject1 = localObject2;
      }
    }
    for (;;)
    {
      if ((localObject1 != null) && (this.mOnStateChangeGesturePerformedListener != null)) {
        this.mOnStateChangeGesturePerformedListener.onStateChangeGesturePerformed(this, ((Integer)localObject1).intValue());
      }
      return;
      localObject1 = Integer.valueOf(0);
      continue;
      localObject1 = Integer.valueOf(4);
      continue;
      switch (this.mState)
      {
      default: 
        localObject1 = localObject2;
        break;
      case 0: 
        localObject1 = Integer.valueOf(1);
        break;
      case 4: 
        localObject1 = Integer.valueOf(5);
      }
    }
  }
  
  public static String stateToString(int paramInt)
  {
    String str;
    switch (paramInt)
    {
    default: 
      str = "STATE_UNKNOWN";
    }
    for (;;)
    {
      return str + " (" + paramInt + ")";
      str = "STATE_DEFAULT_SELECTED";
      continue;
      str = "STATE_DEFAULT_NOT_SELECTED";
      continue;
      str = "STATE_DEFAULT_ABOVE_SELECTED";
      continue;
      str = "STATE_DEFAULT_BELOW_SELECTED";
      continue;
      str = "STATE_ZOOMED_OUT_SELECTED";
      continue;
      str = "STATE_ZOOMED_OUT_NOT_SELECTED";
      continue;
      str = "STATE_ACTIONS_SELECTED";
      continue;
      str = "STATE_ACTIONS_NOT_SELECTED";
      continue;
      str = "STATE_MOVE_SELECTED";
      continue;
      str = "STATE_MOVE_NOT_SELECTED";
    }
  }
  
  private int translateFocusDirectionForRtl(int paramInt)
  {
    if (!this.mIsRtl) {}
    do
    {
      return paramInt;
      if (paramInt == 17) {
        return 66;
      }
    } while (paramInt != 66);
    return 17;
  }
  
  private void translateNextFocusForRtl(View paramView)
  {
    if (!this.mIsRtl) {
      return;
    }
    int i = paramView.getNextFocusLeftId();
    paramView.setNextFocusLeftId(paramView.getNextFocusRightId());
    paramView.setNextFocusRightId(i);
  }
  
  private void updateUi(int paramInt1, int paramInt2)
  {
    if (paramInt2 == paramInt1) {
      return;
    }
    label60:
    Object localObject1;
    Object localObject2;
    switch (paramInt2)
    {
    default: 
      if (this.mStateSettings != null)
      {
        localObject1 = (ChannelStateSettings)this.mStateSettings.get(this.mState);
        boolean bool = isZoomedOutState(this.mState);
        localObject2 = (ViewGroup.MarginLayoutParams)getLayoutParams();
        ((ViewGroup.MarginLayoutParams)localObject2).setMargins(0, ((ChannelStateSettings)localObject1).getMarginTop(), 0, ((ChannelStateSettings)localObject1).getMarginBottom());
        setLayoutParams((ViewGroup.LayoutParams)localObject2);
        localObject2 = (ViewGroup.MarginLayoutParams)this.mItemsListContainer.getLayoutParams();
        if (!bool) {
          break label1388;
        }
        paramInt1 = this.mItemsListZoomedOutMarginStart;
        label145:
        ((ViewGroup.MarginLayoutParams)localObject2).setMarginStart(paramInt1);
        this.mItemsListContainer.setLayoutParams((ViewGroup.LayoutParams)localObject2);
        localObject2 = (ViewGroup.MarginLayoutParams)this.mItemsList.getLayoutParams();
        ((ViewGroup.MarginLayoutParams)localObject2).height = (((ChannelStateSettings)localObject1).getItemHeight() + ((ChannelStateSettings)localObject1).getItemMarginTop() + ((ChannelStateSettings)localObject1).getItemMarginBottom());
        this.mItemsList.setLayoutParams((ViewGroup.LayoutParams)localObject2);
        localObject2 = (LinearLayout.LayoutParams)this.mChannelLogoContainer.getLayoutParams();
        if (this.mState != 2) {
          break label1396;
        }
        ((LinearLayout.LayoutParams)localObject2).gravity = 80;
        ((LinearLayout.LayoutParams)localObject2).bottomMargin = ((ChannelStateSettings)localObject1).getChannelLogoAlignmentOriginMargin();
        label240:
        this.mChannelLogoContainer.setLayoutParams((ViewGroup.LayoutParams)localObject2);
        localObject2 = (ViewGroup.MarginLayoutParams)this.mChannelLogo.getLayoutParams();
        paramInt1 = ((ChannelStateSettings)localObject1).getChannelLogoAlignmentOriginMargin();
        if (!bool) {
          break label1412;
        }
        ((ViewGroup.MarginLayoutParams)localObject2).height = this.mChannelLogoZoomedOutSize;
        ((ViewGroup.MarginLayoutParams)localObject2).width = this.mChannelLogoZoomedOutSize;
        ((ViewGroup.MarginLayoutParams)localObject2).setMargins(this.mChannelLogoZoomedOutMargin, this.mChannelLogoZoomedOutMargin + paramInt1, this.mChannelLogoZoomedOutMargin, this.mChannelLogoZoomedOutMargin);
      }
      break;
    }
    for (;;)
    {
      this.mChannelLogo.setLayoutParams((ViewGroup.LayoutParams)localObject2);
      return;
      this.mChannelTitle.setVisibility(0);
      this.mZoomedOutChannelTitle.setVisibility(8);
      this.mActionsHint.setVisibility(8);
      this.mRemoveButton.setVisibility(8);
      this.mMoveButton.setVisibility(8);
      this.mZoomedOutPaddingView.setVisibility(8);
      this.mChannelActionsPaddingView.setVisibility(8);
      this.mMoveChannelPaddingView.setVisibility(8);
      this.mNoMoveActionPaddingView.setVisibility(8);
      localObject1 = this.mItemMetaContainer;
      if (this.mShowItemMeta) {}
      for (paramInt1 = 0;; paramInt1 = 8)
      {
        ((View)localObject1).setVisibility(paramInt1);
        this.mMovingChannelBackground.setVisibility(8);
        this.mItemsListContainer.setFadeEnabled(true);
        setFocusable(false);
        if (Util.isAccessibilityEnabled(getContext())) {
          break;
        }
        this.mItemsList.requestFocus();
        break;
      }
      this.mChannelTitle.setVisibility(0);
      this.mZoomedOutChannelTitle.setVisibility(8);
      this.mActionsHint.setVisibility(8);
      this.mRemoveButton.setVisibility(8);
      this.mMoveButton.setVisibility(8);
      this.mZoomedOutPaddingView.setVisibility(8);
      this.mChannelActionsPaddingView.setVisibility(8);
      this.mMoveChannelPaddingView.setVisibility(8);
      this.mNoMoveActionPaddingView.setVisibility(8);
      this.mItemMetaContainer.setVisibility(8);
      this.mMovingChannelBackground.setVisibility(8);
      this.mItemsListContainer.setFadeEnabled(false);
      setFocusable(false);
      break label60;
      this.mChannelTitle.setVisibility(8);
      this.mZoomedOutChannelTitle.setVisibility(0);
      localObject1 = this.mActionsHint;
      if ((this.mAllowMoving) || (this.mAllowRemoving))
      {
        paramInt1 = 0;
        label625:
        ((View)localObject1).setVisibility(paramInt1);
        this.mRemoveButton.setVisibility(8);
        this.mMoveButton.setVisibility(8);
        localObject1 = this.mZoomedOutPaddingView;
        if ((!this.mAllowMoving) && (!this.mAllowRemoving)) {
          break label753;
        }
      }
      label753:
      for (paramInt1 = 8;; paramInt1 = 0)
      {
        ((View)localObject1).setVisibility(paramInt1);
        this.mChannelActionsPaddingView.setVisibility(8);
        this.mMoveChannelPaddingView.setVisibility(8);
        this.mNoMoveActionPaddingView.setVisibility(8);
        this.mItemMetaContainer.setVisibility(8);
        this.mMovingChannelBackground.setVisibility(8);
        this.mItemsListContainer.setFadeEnabled(false);
        setFocusable(false);
        this.mChannelLogo.requestFocus();
        break;
        paramInt1 = 8;
        break label625;
      }
      this.mChannelTitle.setVisibility(8);
      this.mZoomedOutChannelTitle.setVisibility(0);
      this.mActionsHint.setVisibility(8);
      this.mRemoveButton.setVisibility(8);
      this.mMoveButton.setVisibility(8);
      this.mZoomedOutPaddingView.setVisibility(0);
      this.mChannelActionsPaddingView.setVisibility(8);
      this.mMoveChannelPaddingView.setVisibility(8);
      this.mNoMoveActionPaddingView.setVisibility(8);
      this.mItemMetaContainer.setVisibility(8);
      this.mMovingChannelBackground.setVisibility(8);
      this.mItemsListContainer.setFadeEnabled(false);
      setFocusable(false);
      break label60;
      this.mChannelTitle.setVisibility(8);
      this.mZoomedOutChannelTitle.setVisibility(0);
      this.mActionsHint.setVisibility(8);
      this.mRemoveButton.setVisibility(0);
      localObject1 = this.mMoveButton;
      if (this.mAllowMoving)
      {
        paramInt1 = 0;
        label920:
        ((ImageView)localObject1).setVisibility(paramInt1);
        this.mZoomedOutPaddingView.setVisibility(8);
        this.mChannelActionsPaddingView.setVisibility(8);
        this.mMoveChannelPaddingView.setVisibility(8);
        localObject1 = this.mNoMoveActionPaddingView;
        if (!this.mAllowMoving) {
          break label1030;
        }
      }
      label1030:
      for (paramInt1 = 8;; paramInt1 = 0)
      {
        ((View)localObject1).setVisibility(paramInt1);
        this.mItemMetaContainer.setVisibility(8);
        this.mMovingChannelBackground.setVisibility(8);
        this.mItemsListContainer.setFadeEnabled(false);
        setFocusable(false);
        if (!this.mAllowMoving) {
          break label1035;
        }
        this.mMoveButton.requestFocus();
        break;
        paramInt1 = 8;
        break label920;
      }
      label1035:
      this.mRemoveButton.requestFocus();
      break label60;
      this.mChannelTitle.setVisibility(8);
      this.mZoomedOutChannelTitle.setVisibility(0);
      this.mActionsHint.setVisibility(8);
      this.mRemoveButton.setVisibility(8);
      this.mMoveButton.setVisibility(8);
      this.mZoomedOutPaddingView.setVisibility(8);
      this.mChannelActionsPaddingView.setVisibility(0);
      this.mMoveChannelPaddingView.setVisibility(8);
      this.mNoMoveActionPaddingView.setVisibility(8);
      this.mItemMetaContainer.setVisibility(8);
      this.mMovingChannelBackground.setVisibility(8);
      this.mItemsListContainer.setFadeEnabled(false);
      setFocusable(false);
      break label60;
      this.mChannelTitle.setVisibility(8);
      this.mZoomedOutChannelTitle.setVisibility(0);
      this.mActionsHint.setVisibility(8);
      this.mRemoveButton.setVisibility(4);
      this.mMoveButton.setVisibility(0);
      this.mZoomedOutPaddingView.setVisibility(8);
      this.mChannelActionsPaddingView.setVisibility(8);
      this.mMoveChannelPaddingView.setVisibility(8);
      this.mNoMoveActionPaddingView.setVisibility(8);
      this.mItemMetaContainer.setVisibility(8);
      this.mMovingChannelBackground.setVisibility(0);
      this.mItemsListContainer.setFadeEnabled(false);
      setFocusable(true);
      requestFocus();
      break label60;
      this.mChannelTitle.setVisibility(8);
      this.mZoomedOutChannelTitle.setVisibility(0);
      this.mActionsHint.setVisibility(8);
      this.mRemoveButton.setVisibility(8);
      this.mMoveButton.setVisibility(8);
      this.mZoomedOutPaddingView.setVisibility(8);
      this.mChannelActionsPaddingView.setVisibility(8);
      this.mMoveChannelPaddingView.setVisibility(0);
      this.mNoMoveActionPaddingView.setVisibility(8);
      this.mItemMetaContainer.setVisibility(8);
      this.mMovingChannelBackground.setVisibility(8);
      this.mItemsListContainer.setFadeEnabled(false);
      setFocusable(false);
      break label60;
      break;
      label1388:
      paramInt1 = this.mItemsListMarginStart;
      break label145;
      label1396:
      ((LinearLayout.LayoutParams)localObject2).gravity = 48;
      ((LinearLayout.LayoutParams)localObject2).bottomMargin = 0;
      break label240;
      label1412:
      ((ViewGroup.MarginLayoutParams)localObject2).height = this.mChannelLogoDefaultSize;
      ((ViewGroup.MarginLayoutParams)localObject2).width = this.mChannelLogoDefaultSize;
      ((ViewGroup.MarginLayoutParams)localObject2).setMargins(0, paramInt1, 0, 0);
    }
  }
  
  public void addFocusables(ArrayList<View> paramArrayList, int paramInt1, int paramInt2)
  {
    if (this.mState == 5)
    {
      this.mChannelLogo.addFocusables(paramArrayList, paramInt1, paramInt2);
      return;
    }
    if ((this.mState == 1) || (this.mState == 2) || (this.mState == 3))
    {
      this.mItemsList.addFocusables(paramArrayList, paramInt1, paramInt2);
      return;
    }
    super.addFocusables(paramArrayList, paramInt1, paramInt2);
  }
  
  public View focusSearch(int paramInt)
  {
    switch (paramInt)
    {
    }
    do
    {
      do
      {
        return this;
      } while (this.mOnMoveChannelUpListener == null);
      this.mOnMoveChannelUpListener.onMoveChannelUp(this);
      return this;
    } while (this.mOnMoveChannelDownListener == null);
    this.mOnMoveChannelDownListener.onMoveChannelDown(this);
    return this;
  }
  
  public View focusSearch(View paramView, int paramInt)
  {
    int i = translateFocusDirectionForRtl(paramInt);
    Object localObject2 = null;
    Object localObject1;
    if ((paramView.getParent() == this.mItemsList) && (this.mAllowZoomOut)) {
      switch (i)
      {
      default: 
        localObject1 = localObject2;
      }
    }
    label52:
    while ((localObject1 != null) && (this.mOnStateChangeGesturePerformedListener != null))
    {
      this.mOnStateChangeGesturePerformedListener.onStateChangeGesturePerformed(this, ((Integer)localObject1).intValue());
      do
      {
        return paramView;
        localObject1 = Integer.valueOf(4);
        break;
        if (paramView != this.mChannelLogo) {
          break label164;
        }
        switch (i)
        {
        default: 
          localObject1 = localObject2;
          break label52;
        }
      } while ((!this.mAllowMoving) && (!this.mAllowRemoving));
      localObject1 = Integer.valueOf(6);
      continue;
      localObject1 = Integer.valueOf(0);
      continue;
      label164:
      if (paramView != this.mMoveButton)
      {
        localObject1 = localObject2;
        if (paramView == this.mRemoveButton)
        {
          localObject1 = localObject2;
          if (this.mAllowMoving) {}
        }
      }
      else
      {
        switch (i)
        {
        default: 
          localObject1 = localObject2;
          break;
        case 66: 
          localObject1 = Integer.valueOf(4);
        }
      }
    }
    return super.focusSearch(paramView, paramInt);
  }
  
  public ImageView getChannelLogoImageView()
  {
    return this.mChannelLogo;
  }
  
  public View getItemMetadataView()
  {
    return this.mItemMetaContainer;
  }
  
  public HorizontalGridView getItemsListView()
  {
    return this.mItemsList;
  }
  
  public int getState()
  {
    return this.mState;
  }
  
  protected void onFinishInflate()
  {
    super.onFinishInflate();
    this.mIsRtl = Util.isRtl(getContext());
    Resources localResources = getResources();
    this.mChannelLogoDefaultSize = localResources.getDimensionPixelSize(R.dimen.channel_logo_size);
    this.mChannelLogoZoomedOutSize = localResources.getDimensionPixelSize(R.dimen.channel_action_button_size);
    this.mChannelLogoZoomedOutMargin = localResources.getDimensionPixelOffset(R.dimen.channel_logo_zoomed_out_margin);
    this.mItemsListMarginStart = localResources.getDimensionPixelOffset(R.dimen.channel_items_list_margin_start);
    this.mItemsListZoomedOutMarginStart = localResources.getDimensionPixelOffset(R.dimen.channel_items_list_zoomed_out_margin_start);
    setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        if ((ChannelView.this.mState == 8) && (ChannelView.this.mOnStateChangeGesturePerformedListener != null)) {
          ChannelView.this.mOnStateChangeGesturePerformedListener.onStateChangeGesturePerformed(ChannelView.this, 4);
        }
      }
    });
    setFocusable(false);
    this.mChannelTitle = ((TextView)findViewById(R.id.channel_title));
    this.mZoomedOutChannelTitle = ((TextView)findViewById(R.id.channel_title_zoomed_out));
    this.mActionsHint = findViewById(R.id.actions_hint);
    this.mZoomedOutPaddingView = findViewById(R.id.zoomed_out_padding);
    this.mChannelActionsPaddingView = findViewById(R.id.channel_actions_padding);
    this.mMoveChannelPaddingView = findViewById(R.id.move_channel_padding);
    this.mNoMoveActionPaddingView = findViewById(R.id.no_move_action_padding);
    this.mMovingChannelBackground = findViewById(R.id.moving_channel_background);
    final int i = getResources().getDimensionPixelSize(R.dimen.moving_channel_background_corner_radius);
    this.mMovingChannelBackground.setOutlineProvider(new ViewOutlineProvider()
    {
      public void getOutline(View paramAnonymousView, Outline paramAnonymousOutline)
      {
        paramAnonymousOutline.setRoundRect(0, 0, paramAnonymousView.getWidth() + i, paramAnonymousView.getHeight(), i);
      }
    });
    this.mMovingChannelBackground.setClipToOutline(true);
    this.mChannelLogo = ((ImageView)findViewById(R.id.channel_logo));
    this.mChannelLogo.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        if (ChannelView.this.mOnPerformMainActionListener != null) {
          ChannelView.this.mOnPerformMainActionListener.onPerformMainAction(ChannelView.this);
        }
      }
    });
    ViewOutlineProvider local4 = new ViewOutlineProvider()
    {
      public void getOutline(View paramAnonymousView, Outline paramAnonymousOutline)
      {
        paramAnonymousOutline.setOval(0, 0, paramAnonymousView.getMeasuredWidth(), paramAnonymousView.getMeasuredHeight());
      }
    };
    this.mChannelLogo.setOutlineProvider(local4);
    this.mChannelLogo.setClipToOutline(true);
    if (!isInEditMode())
    {
      final float f = localResources.getFraction(2131886083, 1, 1);
      new ScaleFocusHandler(localResources.getInteger(R.integer.channel_logo_focused_animation_duration_ms), f, localResources.getDimension(R.dimen.channel_logo_focused_elevation))
      {
        public void onFocusChange(View paramAnonymousView, boolean paramAnonymousBoolean)
        {
          if (Util.isAccessibilityEnabled(ChannelView.this.getContext())) {}
          for (float f = 1.0F;; f = f)
          {
            setFocusedScale(f);
            super.onFocusChange(paramAnonymousView, paramAnonymousBoolean);
            return;
          }
        }
      }.setView(this.mChannelLogo);
    }
    this.mChannelLogoContainer = findViewById(R.id.channel_logo_container);
    this.mRemoveButton = ((ImageView)findViewById(R.id.remove));
    this.mRemoveButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        if (ChannelView.this.mOnRemoveListener != null) {
          ChannelView.this.mOnRemoveListener.onRemove(ChannelView.this);
        }
      }
    });
    translateNextFocusForRtl(this.mRemoveButton);
    this.mMoveButton = ((ImageView)findViewById(R.id.move));
    this.mMoveButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        if (ChannelView.this.mOnStateChangeGesturePerformedListener != null) {
          ChannelView.this.mOnStateChangeGesturePerformedListener.onStateChangeGesturePerformed(ChannelView.this, 8);
        }
      }
    });
    this.mItemsList = ((HorizontalGridView)findViewById(R.id.items_list));
    this.mItemsListContainer = ((FadingEdgeContainer)findViewById(R.id.items_list_container));
    this.mItemMetaContainer = findViewById(R.id.item_meta_container);
    getViewTreeObserver().addOnGlobalFocusChangeListener(new ViewTreeObserver.OnGlobalFocusChangeListener()
    {
      public void onGlobalFocusChanged(View paramAnonymousView1, View paramAnonymousView2)
      {
        boolean bool1 = ChannelView.this.isFocusableChild(paramAnonymousView1);
        boolean bool2 = ChannelView.this.isFocusableChild(paramAnonymousView2);
        if (bool2 != bool1) {
          ChannelView.this.onChannelSelected(bool2);
        }
      }
    });
    this.mActionMoveUpDownIcon = getContext().getDrawable(R.drawable.ic_action_move_up_down_black);
    this.mActionMoveUpIcon = getContext().getDrawable(R.drawable.ic_action_move_up_black);
    this.mActionMoveDownIcon = getContext().getDrawable(R.drawable.ic_action_move_down_black);
  }
  
  protected boolean onRequestFocusInDescendants(int paramInt, Rect paramRect)
  {
    if ((this.mState == 5) || (this.mState == 4)) {
      this.mChannelLogo.requestFocus();
    }
    for (;;)
    {
      return true;
      this.mItemsList.requestFocus();
    }
  }
  
  public void setAllowMoving(boolean paramBoolean)
  {
    this.mAllowMoving = paramBoolean;
  }
  
  public void setAllowRemoving(boolean paramBoolean)
  {
    this.mAllowRemoving = paramBoolean;
  }
  
  public void setAllowZoomOut(boolean paramBoolean)
  {
    this.mAllowZoomOut = paramBoolean;
  }
  
  public void setChannelTitle(String paramString)
  {
    this.mChannelTitle.setText(paramString);
    this.mZoomedOutChannelTitle.setText(paramString);
  }
  
  public void setOnMoveDownListener(OnMoveChannelDownListener paramOnMoveChannelDownListener)
  {
    this.mOnMoveChannelDownListener = paramOnMoveChannelDownListener;
  }
  
  public void setOnMoveUpListener(OnMoveChannelUpListener paramOnMoveChannelUpListener)
  {
    this.mOnMoveChannelUpListener = paramOnMoveChannelUpListener;
  }
  
  public void setOnPerformMainActionListener(OnPerformMainActionListener paramOnPerformMainActionListener)
  {
    this.mOnPerformMainActionListener = paramOnPerformMainActionListener;
  }
  
  public void setOnRemoveListener(OnRemoveListener paramOnRemoveListener)
  {
    this.mOnRemoveListener = paramOnRemoveListener;
  }
  
  public void setOnStateChangeGesturePerformedListener(OnStateChangeGesturePerformedListener paramOnStateChangeGesturePerformedListener)
  {
    this.mOnStateChangeGesturePerformedListener = paramOnStateChangeGesturePerformedListener;
  }
  
  public void setShowItemMeta(boolean paramBoolean)
  {
    this.mShowItemMeta = paramBoolean;
  }
  
  public void setState(int paramInt)
  {
    if (paramInt == this.mState) {
      return;
    }
    int i = this.mState;
    this.mState = paramInt;
    updateUi(i, paramInt);
  }
  
  public void setStateSettings(SparseArray<ChannelStateSettings> paramSparseArray)
  {
    this.mStateSettings = paramSparseArray;
  }
  
  public String toString()
  {
    return '{' + super.toString() + ", title='" + this.mChannelTitle.getText() + '\'' + '}';
  }
  
  public void updateChannelMoveAction(boolean paramBoolean1, boolean paramBoolean2)
  {
    setAllowMoving(true);
    if ((paramBoolean1) && (paramBoolean2))
    {
      this.mMoveButton.setImageDrawable(this.mActionMoveUpDownIcon);
      return;
    }
    if (paramBoolean1)
    {
      this.mMoveButton.setImageDrawable(this.mActionMoveUpIcon);
      return;
    }
    if (paramBoolean2)
    {
      this.mMoveButton.setImageDrawable(this.mActionMoveDownIcon);
      return;
    }
    setAllowMoving(false);
  }
  
  public static abstract interface OnMoveChannelDownListener
  {
    public abstract void onMoveChannelDown(ChannelView paramChannelView);
  }
  
  public static abstract interface OnMoveChannelUpListener
  {
    public abstract void onMoveChannelUp(ChannelView paramChannelView);
  }
  
  public static abstract interface OnPerformMainActionListener
  {
    public abstract void onPerformMainAction(ChannelView paramChannelView);
  }
  
  public static abstract interface OnRemoveListener
  {
    public abstract void onRemove(ChannelView paramChannelView);
  }
  
  public static abstract interface OnStateChangeGesturePerformedListener
  {
    public abstract void onStateChangeGesturePerformed(ChannelView paramChannelView, int paramInt);
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface State {}
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/home/ChannelView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */