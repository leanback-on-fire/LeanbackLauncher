package android.support.v4.view.accessibility;

import android.graphics.Rect;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction;
import android.view.accessibility.AccessibilityNodeInfo.CollectionInfo;
import android.view.accessibility.AccessibilityNodeInfo.CollectionItemInfo;
import android.view.accessibility.AccessibilityNodeInfo.RangeInfo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class AccessibilityNodeInfoCompat
{
  public static final int ACTION_ACCESSIBILITY_FOCUS = 64;
  public static final String ACTION_ARGUMENT_COLUMN_INT = "android.view.accessibility.action.ARGUMENT_COLUMN_INT";
  public static final String ACTION_ARGUMENT_EXTEND_SELECTION_BOOLEAN = "ACTION_ARGUMENT_EXTEND_SELECTION_BOOLEAN";
  public static final String ACTION_ARGUMENT_HTML_ELEMENT_STRING = "ACTION_ARGUMENT_HTML_ELEMENT_STRING";
  public static final String ACTION_ARGUMENT_MOVEMENT_GRANULARITY_INT = "ACTION_ARGUMENT_MOVEMENT_GRANULARITY_INT";
  public static final String ACTION_ARGUMENT_PROGRESS_VALUE = "android.view.accessibility.action.ARGUMENT_PROGRESS_VALUE";
  public static final String ACTION_ARGUMENT_ROW_INT = "android.view.accessibility.action.ARGUMENT_ROW_INT";
  public static final String ACTION_ARGUMENT_SELECTION_END_INT = "ACTION_ARGUMENT_SELECTION_END_INT";
  public static final String ACTION_ARGUMENT_SELECTION_START_INT = "ACTION_ARGUMENT_SELECTION_START_INT";
  public static final String ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE = "ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE";
  public static final int ACTION_CLEAR_ACCESSIBILITY_FOCUS = 128;
  public static final int ACTION_CLEAR_FOCUS = 2;
  public static final int ACTION_CLEAR_SELECTION = 8;
  public static final int ACTION_CLICK = 16;
  public static final int ACTION_COLLAPSE = 524288;
  public static final int ACTION_COPY = 16384;
  public static final int ACTION_CUT = 65536;
  public static final int ACTION_DISMISS = 1048576;
  public static final int ACTION_EXPAND = 262144;
  public static final int ACTION_FOCUS = 1;
  public static final int ACTION_LONG_CLICK = 32;
  public static final int ACTION_NEXT_AT_MOVEMENT_GRANULARITY = 256;
  public static final int ACTION_NEXT_HTML_ELEMENT = 1024;
  public static final int ACTION_PASTE = 32768;
  public static final int ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY = 512;
  public static final int ACTION_PREVIOUS_HTML_ELEMENT = 2048;
  public static final int ACTION_SCROLL_BACKWARD = 8192;
  public static final int ACTION_SCROLL_FORWARD = 4096;
  public static final int ACTION_SELECT = 4;
  public static final int ACTION_SET_SELECTION = 131072;
  public static final int ACTION_SET_TEXT = 2097152;
  public static final int FOCUS_ACCESSIBILITY = 2;
  public static final int FOCUS_INPUT = 1;
  static final AccessibilityNodeInfoBaseImpl IMPL = new AccessibilityNodeInfoBaseImpl();
  public static final int MOVEMENT_GRANULARITY_CHARACTER = 1;
  public static final int MOVEMENT_GRANULARITY_LINE = 4;
  public static final int MOVEMENT_GRANULARITY_PAGE = 16;
  public static final int MOVEMENT_GRANULARITY_PARAGRAPH = 8;
  public static final int MOVEMENT_GRANULARITY_WORD = 2;
  private final AccessibilityNodeInfo mInfo;
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public int mParentVirtualDescendantId = -1;
  
  static
  {
    if (Build.VERSION.SDK_INT >= 24)
    {
      IMPL = new AccessibilityNodeInfoApi24Impl();
      return;
    }
    if (Build.VERSION.SDK_INT >= 23)
    {
      IMPL = new AccessibilityNodeInfoApi23Impl();
      return;
    }
    if (Build.VERSION.SDK_INT >= 22)
    {
      IMPL = new AccessibilityNodeInfoApi22Impl();
      return;
    }
    if (Build.VERSION.SDK_INT >= 21)
    {
      IMPL = new AccessibilityNodeInfoApi21Impl();
      return;
    }
    if (Build.VERSION.SDK_INT >= 19)
    {
      IMPL = new AccessibilityNodeInfoApi19Impl();
      return;
    }
    if (Build.VERSION.SDK_INT >= 18)
    {
      IMPL = new AccessibilityNodeInfoApi18Impl();
      return;
    }
    if (Build.VERSION.SDK_INT >= 17)
    {
      IMPL = new AccessibilityNodeInfoApi17Impl();
      return;
    }
    if (Build.VERSION.SDK_INT >= 16)
    {
      IMPL = new AccessibilityNodeInfoApi16Impl();
      return;
    }
  }
  
  private AccessibilityNodeInfoCompat(AccessibilityNodeInfo paramAccessibilityNodeInfo)
  {
    this.mInfo = paramAccessibilityNodeInfo;
  }
  
  @Deprecated
  public AccessibilityNodeInfoCompat(Object paramObject)
  {
    this.mInfo = ((AccessibilityNodeInfo)paramObject);
  }
  
  private static String getActionSymbolicName(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return "ACTION_UNKNOWN";
    case 1: 
      return "ACTION_FOCUS";
    case 2: 
      return "ACTION_CLEAR_FOCUS";
    case 4: 
      return "ACTION_SELECT";
    case 8: 
      return "ACTION_CLEAR_SELECTION";
    case 16: 
      return "ACTION_CLICK";
    case 32: 
      return "ACTION_LONG_CLICK";
    case 64: 
      return "ACTION_ACCESSIBILITY_FOCUS";
    case 128: 
      return "ACTION_CLEAR_ACCESSIBILITY_FOCUS";
    case 256: 
      return "ACTION_NEXT_AT_MOVEMENT_GRANULARITY";
    case 512: 
      return "ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY";
    case 1024: 
      return "ACTION_NEXT_HTML_ELEMENT";
    case 2048: 
      return "ACTION_PREVIOUS_HTML_ELEMENT";
    case 4096: 
      return "ACTION_SCROLL_FORWARD";
    case 8192: 
      return "ACTION_SCROLL_BACKWARD";
    case 65536: 
      return "ACTION_CUT";
    case 16384: 
      return "ACTION_COPY";
    case 32768: 
      return "ACTION_PASTE";
    }
    return "ACTION_SET_SELECTION";
  }
  
  public static AccessibilityNodeInfoCompat obtain()
  {
    return wrap(AccessibilityNodeInfo.obtain());
  }
  
  public static AccessibilityNodeInfoCompat obtain(AccessibilityNodeInfoCompat paramAccessibilityNodeInfoCompat)
  {
    return wrap(AccessibilityNodeInfo.obtain(paramAccessibilityNodeInfoCompat.mInfo));
  }
  
  public static AccessibilityNodeInfoCompat obtain(View paramView)
  {
    return wrap(AccessibilityNodeInfo.obtain(paramView));
  }
  
  public static AccessibilityNodeInfoCompat obtain(View paramView, int paramInt)
  {
    return wrapNonNullInstance(IMPL.obtain(paramView, paramInt));
  }
  
  public static AccessibilityNodeInfoCompat wrap(@NonNull AccessibilityNodeInfo paramAccessibilityNodeInfo)
  {
    return new AccessibilityNodeInfoCompat(paramAccessibilityNodeInfo);
  }
  
  static AccessibilityNodeInfoCompat wrapNonNullInstance(Object paramObject)
  {
    if (paramObject != null) {
      return new AccessibilityNodeInfoCompat(paramObject);
    }
    return null;
  }
  
  public void addAction(int paramInt)
  {
    this.mInfo.addAction(paramInt);
  }
  
  public void addAction(AccessibilityActionCompat paramAccessibilityActionCompat)
  {
    IMPL.addAction(this.mInfo, paramAccessibilityActionCompat.mAction);
  }
  
  public void addChild(View paramView)
  {
    this.mInfo.addChild(paramView);
  }
  
  public void addChild(View paramView, int paramInt)
  {
    IMPL.addChild(this.mInfo, paramView, paramInt);
  }
  
  public boolean canOpenPopup()
  {
    return IMPL.canOpenPopup(this.mInfo);
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {}
    do
    {
      do
      {
        return true;
        if (paramObject == null) {
          return false;
        }
        if (getClass() != paramObject.getClass()) {
          return false;
        }
        paramObject = (AccessibilityNodeInfoCompat)paramObject;
        if (this.mInfo != null) {
          break;
        }
      } while (((AccessibilityNodeInfoCompat)paramObject).mInfo == null);
      return false;
    } while (this.mInfo.equals(((AccessibilityNodeInfoCompat)paramObject).mInfo));
    return false;
  }
  
  public List<AccessibilityNodeInfoCompat> findAccessibilityNodeInfosByText(String paramString)
  {
    ArrayList localArrayList = new ArrayList();
    paramString = this.mInfo.findAccessibilityNodeInfosByText(paramString);
    int j = paramString.size();
    int i = 0;
    while (i < j)
    {
      localArrayList.add(wrap((AccessibilityNodeInfo)paramString.get(i)));
      i += 1;
    }
    return localArrayList;
  }
  
  public List<AccessibilityNodeInfoCompat> findAccessibilityNodeInfosByViewId(String paramString)
  {
    paramString = IMPL.findAccessibilityNodeInfosByViewId(this.mInfo, paramString);
    if (paramString != null)
    {
      ArrayList localArrayList = new ArrayList();
      Iterator localIterator = paramString.iterator();
      for (;;)
      {
        paramString = localArrayList;
        if (!localIterator.hasNext()) {
          break;
        }
        localArrayList.add(wrap((AccessibilityNodeInfo)localIterator.next()));
      }
    }
    paramString = Collections.emptyList();
    return paramString;
  }
  
  public AccessibilityNodeInfoCompat findFocus(int paramInt)
  {
    return wrapNonNullInstance(IMPL.findFocus(this.mInfo, paramInt));
  }
  
  public AccessibilityNodeInfoCompat focusSearch(int paramInt)
  {
    return wrapNonNullInstance(IMPL.focusSearch(this.mInfo, paramInt));
  }
  
  public List<AccessibilityActionCompat> getActionList()
  {
    List localList = IMPL.getActionList(this.mInfo);
    if (localList != null)
    {
      ArrayList localArrayList = new ArrayList();
      int j = localList.size();
      int i = 0;
      for (;;)
      {
        localObject = localArrayList;
        if (i >= j) {
          break;
        }
        localArrayList.add(new AccessibilityActionCompat(localList.get(i)));
        i += 1;
      }
    }
    Object localObject = Collections.emptyList();
    return (List<AccessibilityActionCompat>)localObject;
  }
  
  public int getActions()
  {
    return this.mInfo.getActions();
  }
  
  public void getBoundsInParent(Rect paramRect)
  {
    this.mInfo.getBoundsInParent(paramRect);
  }
  
  public void getBoundsInScreen(Rect paramRect)
  {
    this.mInfo.getBoundsInScreen(paramRect);
  }
  
  public AccessibilityNodeInfoCompat getChild(int paramInt)
  {
    return wrapNonNullInstance(this.mInfo.getChild(paramInt));
  }
  
  public int getChildCount()
  {
    return this.mInfo.getChildCount();
  }
  
  public CharSequence getClassName()
  {
    return this.mInfo.getClassName();
  }
  
  public CollectionInfoCompat getCollectionInfo()
  {
    Object localObject = IMPL.getCollectionInfo(this.mInfo);
    if (localObject == null) {
      return null;
    }
    return new CollectionInfoCompat(localObject);
  }
  
  public CollectionItemInfoCompat getCollectionItemInfo()
  {
    Object localObject = IMPL.getCollectionItemInfo(this.mInfo);
    if (localObject == null) {
      return null;
    }
    return new CollectionItemInfoCompat(localObject);
  }
  
  public CharSequence getContentDescription()
  {
    return this.mInfo.getContentDescription();
  }
  
  public int getDrawingOrder()
  {
    return IMPL.getDrawingOrder(this.mInfo);
  }
  
  public CharSequence getError()
  {
    return IMPL.getError(this.mInfo);
  }
  
  public Bundle getExtras()
  {
    return IMPL.getExtras(this.mInfo);
  }
  
  @Deprecated
  public Object getInfo()
  {
    return this.mInfo;
  }
  
  public int getInputType()
  {
    return IMPL.getInputType(this.mInfo);
  }
  
  public AccessibilityNodeInfoCompat getLabelFor()
  {
    return wrapNonNullInstance(IMPL.getLabelFor(this.mInfo));
  }
  
  public AccessibilityNodeInfoCompat getLabeledBy()
  {
    return wrapNonNullInstance(IMPL.getLabeledBy(this.mInfo));
  }
  
  public int getLiveRegion()
  {
    return IMPL.getLiveRegion(this.mInfo);
  }
  
  public int getMaxTextLength()
  {
    return IMPL.getMaxTextLength(this.mInfo);
  }
  
  public int getMovementGranularities()
  {
    return IMPL.getMovementGranularities(this.mInfo);
  }
  
  public CharSequence getPackageName()
  {
    return this.mInfo.getPackageName();
  }
  
  public AccessibilityNodeInfoCompat getParent()
  {
    return wrapNonNullInstance(this.mInfo.getParent());
  }
  
  public RangeInfoCompat getRangeInfo()
  {
    Object localObject = IMPL.getRangeInfo(this.mInfo);
    if (localObject == null) {
      return null;
    }
    return new RangeInfoCompat(localObject);
  }
  
  @Nullable
  public CharSequence getRoleDescription()
  {
    return IMPL.getRoleDescription(this.mInfo);
  }
  
  public CharSequence getText()
  {
    return this.mInfo.getText();
  }
  
  public int getTextSelectionEnd()
  {
    return IMPL.getTextSelectionEnd(this.mInfo);
  }
  
  public int getTextSelectionStart()
  {
    return IMPL.getTextSelectionStart(this.mInfo);
  }
  
  public AccessibilityNodeInfoCompat getTraversalAfter()
  {
    return wrapNonNullInstance(IMPL.getTraversalAfter(this.mInfo));
  }
  
  public AccessibilityNodeInfoCompat getTraversalBefore()
  {
    return wrapNonNullInstance(IMPL.getTraversalBefore(this.mInfo));
  }
  
  public String getViewIdResourceName()
  {
    return IMPL.getViewIdResourceName(this.mInfo);
  }
  
  public AccessibilityWindowInfoCompat getWindow()
  {
    return AccessibilityWindowInfoCompat.wrapNonNullInstance(IMPL.getWindow(this.mInfo));
  }
  
  public int getWindowId()
  {
    return this.mInfo.getWindowId();
  }
  
  public int hashCode()
  {
    if (this.mInfo == null) {
      return 0;
    }
    return this.mInfo.hashCode();
  }
  
  public boolean isAccessibilityFocused()
  {
    return IMPL.isAccessibilityFocused(this.mInfo);
  }
  
  public boolean isCheckable()
  {
    return this.mInfo.isCheckable();
  }
  
  public boolean isChecked()
  {
    return this.mInfo.isChecked();
  }
  
  public boolean isClickable()
  {
    return this.mInfo.isClickable();
  }
  
  public boolean isContentInvalid()
  {
    return IMPL.isContentInvalid(this.mInfo);
  }
  
  public boolean isContextClickable()
  {
    return IMPL.isContextClickable(this.mInfo);
  }
  
  public boolean isDismissable()
  {
    return IMPL.isDismissable(this.mInfo);
  }
  
  public boolean isEditable()
  {
    return IMPL.isEditable(this.mInfo);
  }
  
  public boolean isEnabled()
  {
    return this.mInfo.isEnabled();
  }
  
  public boolean isFocusable()
  {
    return this.mInfo.isFocusable();
  }
  
  public boolean isFocused()
  {
    return this.mInfo.isFocused();
  }
  
  public boolean isImportantForAccessibility()
  {
    return IMPL.isImportantForAccessibility(this.mInfo);
  }
  
  public boolean isLongClickable()
  {
    return this.mInfo.isLongClickable();
  }
  
  public boolean isMultiLine()
  {
    return IMPL.isMultiLine(this.mInfo);
  }
  
  public boolean isPassword()
  {
    return this.mInfo.isPassword();
  }
  
  public boolean isScrollable()
  {
    return this.mInfo.isScrollable();
  }
  
  public boolean isSelected()
  {
    return this.mInfo.isSelected();
  }
  
  public boolean isVisibleToUser()
  {
    return IMPL.isVisibleToUser(this.mInfo);
  }
  
  public boolean performAction(int paramInt)
  {
    return this.mInfo.performAction(paramInt);
  }
  
  public boolean performAction(int paramInt, Bundle paramBundle)
  {
    return IMPL.performAction(this.mInfo, paramInt, paramBundle);
  }
  
  public void recycle()
  {
    this.mInfo.recycle();
  }
  
  public boolean refresh()
  {
    return IMPL.refresh(this.mInfo);
  }
  
  public boolean removeAction(AccessibilityActionCompat paramAccessibilityActionCompat)
  {
    return IMPL.removeAction(this.mInfo, paramAccessibilityActionCompat.mAction);
  }
  
  public boolean removeChild(View paramView)
  {
    return IMPL.removeChild(this.mInfo, paramView);
  }
  
  public boolean removeChild(View paramView, int paramInt)
  {
    return IMPL.removeChild(this.mInfo, paramView, paramInt);
  }
  
  public void setAccessibilityFocused(boolean paramBoolean)
  {
    IMPL.setAccessibilityFocused(this.mInfo, paramBoolean);
  }
  
  public void setBoundsInParent(Rect paramRect)
  {
    this.mInfo.setBoundsInParent(paramRect);
  }
  
  public void setBoundsInScreen(Rect paramRect)
  {
    this.mInfo.setBoundsInScreen(paramRect);
  }
  
  public void setCanOpenPopup(boolean paramBoolean)
  {
    IMPL.setCanOpenPopup(this.mInfo, paramBoolean);
  }
  
  public void setCheckable(boolean paramBoolean)
  {
    this.mInfo.setCheckable(paramBoolean);
  }
  
  public void setChecked(boolean paramBoolean)
  {
    this.mInfo.setChecked(paramBoolean);
  }
  
  public void setClassName(CharSequence paramCharSequence)
  {
    this.mInfo.setClassName(paramCharSequence);
  }
  
  public void setClickable(boolean paramBoolean)
  {
    this.mInfo.setClickable(paramBoolean);
  }
  
  public void setCollectionInfo(Object paramObject)
  {
    IMPL.setCollectionInfo(this.mInfo, ((CollectionInfoCompat)paramObject).mInfo);
  }
  
  public void setCollectionItemInfo(Object paramObject)
  {
    IMPL.setCollectionItemInfo(this.mInfo, ((CollectionItemInfoCompat)paramObject).mInfo);
  }
  
  public void setContentDescription(CharSequence paramCharSequence)
  {
    this.mInfo.setContentDescription(paramCharSequence);
  }
  
  public void setContentInvalid(boolean paramBoolean)
  {
    IMPL.setContentInvalid(this.mInfo, paramBoolean);
  }
  
  public void setContextClickable(boolean paramBoolean)
  {
    IMPL.setContextClickable(this.mInfo, paramBoolean);
  }
  
  public void setDismissable(boolean paramBoolean)
  {
    IMPL.setDismissable(this.mInfo, paramBoolean);
  }
  
  public void setDrawingOrder(int paramInt)
  {
    IMPL.setDrawingOrder(this.mInfo, paramInt);
  }
  
  public void setEditable(boolean paramBoolean)
  {
    IMPL.setEditable(this.mInfo, paramBoolean);
  }
  
  public void setEnabled(boolean paramBoolean)
  {
    this.mInfo.setEnabled(paramBoolean);
  }
  
  public void setError(CharSequence paramCharSequence)
  {
    IMPL.setError(this.mInfo, paramCharSequence);
  }
  
  public void setFocusable(boolean paramBoolean)
  {
    this.mInfo.setFocusable(paramBoolean);
  }
  
  public void setFocused(boolean paramBoolean)
  {
    this.mInfo.setFocused(paramBoolean);
  }
  
  public void setImportantForAccessibility(boolean paramBoolean)
  {
    IMPL.setImportantForAccessibility(this.mInfo, paramBoolean);
  }
  
  public void setInputType(int paramInt)
  {
    IMPL.setInputType(this.mInfo, paramInt);
  }
  
  public void setLabelFor(View paramView)
  {
    IMPL.setLabelFor(this.mInfo, paramView);
  }
  
  public void setLabelFor(View paramView, int paramInt)
  {
    IMPL.setLabelFor(this.mInfo, paramView, paramInt);
  }
  
  public void setLabeledBy(View paramView)
  {
    IMPL.setLabeledBy(this.mInfo, paramView);
  }
  
  public void setLabeledBy(View paramView, int paramInt)
  {
    IMPL.setLabeledBy(this.mInfo, paramView, paramInt);
  }
  
  public void setLiveRegion(int paramInt)
  {
    IMPL.setLiveRegion(this.mInfo, paramInt);
  }
  
  public void setLongClickable(boolean paramBoolean)
  {
    this.mInfo.setLongClickable(paramBoolean);
  }
  
  public void setMaxTextLength(int paramInt)
  {
    IMPL.setMaxTextLength(this.mInfo, paramInt);
  }
  
  public void setMovementGranularities(int paramInt)
  {
    IMPL.setMovementGranularities(this.mInfo, paramInt);
  }
  
  public void setMultiLine(boolean paramBoolean)
  {
    IMPL.setMultiLine(this.mInfo, paramBoolean);
  }
  
  public void setPackageName(CharSequence paramCharSequence)
  {
    this.mInfo.setPackageName(paramCharSequence);
  }
  
  public void setParent(View paramView)
  {
    this.mInfo.setParent(paramView);
  }
  
  public void setParent(View paramView, int paramInt)
  {
    this.mParentVirtualDescendantId = paramInt;
    IMPL.setParent(this.mInfo, paramView, paramInt);
  }
  
  public void setPassword(boolean paramBoolean)
  {
    this.mInfo.setPassword(paramBoolean);
  }
  
  public void setRangeInfo(RangeInfoCompat paramRangeInfoCompat)
  {
    IMPL.setRangeInfo(this.mInfo, paramRangeInfoCompat.mInfo);
  }
  
  public void setRoleDescription(@Nullable CharSequence paramCharSequence)
  {
    IMPL.setRoleDescription(this.mInfo, paramCharSequence);
  }
  
  public void setScrollable(boolean paramBoolean)
  {
    this.mInfo.setScrollable(paramBoolean);
  }
  
  public void setSelected(boolean paramBoolean)
  {
    this.mInfo.setSelected(paramBoolean);
  }
  
  public void setSource(View paramView)
  {
    this.mInfo.setSource(paramView);
  }
  
  public void setSource(View paramView, int paramInt)
  {
    IMPL.setSource(this.mInfo, paramView, paramInt);
  }
  
  public void setText(CharSequence paramCharSequence)
  {
    this.mInfo.setText(paramCharSequence);
  }
  
  public void setTextSelection(int paramInt1, int paramInt2)
  {
    IMPL.setTextSelection(this.mInfo, paramInt1, paramInt2);
  }
  
  public void setTraversalAfter(View paramView)
  {
    IMPL.setTraversalAfter(this.mInfo, paramView);
  }
  
  public void setTraversalAfter(View paramView, int paramInt)
  {
    IMPL.setTraversalAfter(this.mInfo, paramView, paramInt);
  }
  
  public void setTraversalBefore(View paramView)
  {
    IMPL.setTraversalBefore(this.mInfo, paramView);
  }
  
  public void setTraversalBefore(View paramView, int paramInt)
  {
    IMPL.setTraversalBefore(this.mInfo, paramView, paramInt);
  }
  
  public void setViewIdResourceName(String paramString)
  {
    IMPL.setViewIdResourceName(this.mInfo, paramString);
  }
  
  public void setVisibleToUser(boolean paramBoolean)
  {
    IMPL.setVisibleToUser(this.mInfo, paramBoolean);
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(super.toString());
    Rect localRect = new Rect();
    getBoundsInParent(localRect);
    localStringBuilder.append("; boundsInParent: " + localRect);
    getBoundsInScreen(localRect);
    localStringBuilder.append("; boundsInScreen: " + localRect);
    localStringBuilder.append("; packageName: ").append(getPackageName());
    localStringBuilder.append("; className: ").append(getClassName());
    localStringBuilder.append("; text: ").append(getText());
    localStringBuilder.append("; contentDescription: ").append(getContentDescription());
    localStringBuilder.append("; viewId: ").append(getViewIdResourceName());
    localStringBuilder.append("; checkable: ").append(isCheckable());
    localStringBuilder.append("; checked: ").append(isChecked());
    localStringBuilder.append("; focusable: ").append(isFocusable());
    localStringBuilder.append("; focused: ").append(isFocused());
    localStringBuilder.append("; selected: ").append(isSelected());
    localStringBuilder.append("; clickable: ").append(isClickable());
    localStringBuilder.append("; longClickable: ").append(isLongClickable());
    localStringBuilder.append("; enabled: ").append(isEnabled());
    localStringBuilder.append("; password: ").append(isPassword());
    localStringBuilder.append("; scrollable: " + isScrollable());
    localStringBuilder.append("; [");
    int i = getActions();
    while (i != 0)
    {
      int k = 1 << Integer.numberOfTrailingZeros(i);
      int j = i & (k ^ 0xFFFFFFFF);
      localStringBuilder.append(getActionSymbolicName(k));
      i = j;
      if (j != 0)
      {
        localStringBuilder.append(", ");
        i = j;
      }
    }
    localStringBuilder.append("]");
    return localStringBuilder.toString();
  }
  
  public AccessibilityNodeInfo unwrap()
  {
    return this.mInfo;
  }
  
  public static class AccessibilityActionCompat
  {
    public static final AccessibilityActionCompat ACTION_ACCESSIBILITY_FOCUS;
    public static final AccessibilityActionCompat ACTION_CLEAR_ACCESSIBILITY_FOCUS;
    public static final AccessibilityActionCompat ACTION_CLEAR_FOCUS;
    public static final AccessibilityActionCompat ACTION_CLEAR_SELECTION;
    public static final AccessibilityActionCompat ACTION_CLICK;
    public static final AccessibilityActionCompat ACTION_COLLAPSE;
    public static final AccessibilityActionCompat ACTION_CONTEXT_CLICK = new AccessibilityActionCompat(AccessibilityNodeInfoCompat.IMPL.getActionContextClick());
    public static final AccessibilityActionCompat ACTION_COPY;
    public static final AccessibilityActionCompat ACTION_CUT;
    public static final AccessibilityActionCompat ACTION_DISMISS;
    public static final AccessibilityActionCompat ACTION_EXPAND;
    public static final AccessibilityActionCompat ACTION_FOCUS = new AccessibilityActionCompat(1, null);
    public static final AccessibilityActionCompat ACTION_LONG_CLICK;
    public static final AccessibilityActionCompat ACTION_NEXT_AT_MOVEMENT_GRANULARITY;
    public static final AccessibilityActionCompat ACTION_NEXT_HTML_ELEMENT;
    public static final AccessibilityActionCompat ACTION_PASTE;
    public static final AccessibilityActionCompat ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY;
    public static final AccessibilityActionCompat ACTION_PREVIOUS_HTML_ELEMENT;
    public static final AccessibilityActionCompat ACTION_SCROLL_BACKWARD;
    public static final AccessibilityActionCompat ACTION_SCROLL_DOWN;
    public static final AccessibilityActionCompat ACTION_SCROLL_FORWARD;
    public static final AccessibilityActionCompat ACTION_SCROLL_LEFT;
    public static final AccessibilityActionCompat ACTION_SCROLL_RIGHT;
    public static final AccessibilityActionCompat ACTION_SCROLL_TO_POSITION;
    public static final AccessibilityActionCompat ACTION_SCROLL_UP;
    public static final AccessibilityActionCompat ACTION_SELECT;
    public static final AccessibilityActionCompat ACTION_SET_PROGRESS = new AccessibilityActionCompat(AccessibilityNodeInfoCompat.IMPL.getActionSetProgress());
    public static final AccessibilityActionCompat ACTION_SET_SELECTION;
    public static final AccessibilityActionCompat ACTION_SET_TEXT;
    public static final AccessibilityActionCompat ACTION_SHOW_ON_SCREEN;
    final Object mAction;
    
    static
    {
      ACTION_CLEAR_FOCUS = new AccessibilityActionCompat(2, null);
      ACTION_SELECT = new AccessibilityActionCompat(4, null);
      ACTION_CLEAR_SELECTION = new AccessibilityActionCompat(8, null);
      ACTION_CLICK = new AccessibilityActionCompat(16, null);
      ACTION_LONG_CLICK = new AccessibilityActionCompat(32, null);
      ACTION_ACCESSIBILITY_FOCUS = new AccessibilityActionCompat(64, null);
      ACTION_CLEAR_ACCESSIBILITY_FOCUS = new AccessibilityActionCompat(128, null);
      ACTION_NEXT_AT_MOVEMENT_GRANULARITY = new AccessibilityActionCompat(256, null);
      ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY = new AccessibilityActionCompat(512, null);
      ACTION_NEXT_HTML_ELEMENT = new AccessibilityActionCompat(1024, null);
      ACTION_PREVIOUS_HTML_ELEMENT = new AccessibilityActionCompat(2048, null);
      ACTION_SCROLL_FORWARD = new AccessibilityActionCompat(4096, null);
      ACTION_SCROLL_BACKWARD = new AccessibilityActionCompat(8192, null);
      ACTION_COPY = new AccessibilityActionCompat(16384, null);
      ACTION_PASTE = new AccessibilityActionCompat(32768, null);
      ACTION_CUT = new AccessibilityActionCompat(65536, null);
      ACTION_SET_SELECTION = new AccessibilityActionCompat(131072, null);
      ACTION_EXPAND = new AccessibilityActionCompat(262144, null);
      ACTION_COLLAPSE = new AccessibilityActionCompat(524288, null);
      ACTION_DISMISS = new AccessibilityActionCompat(1048576, null);
      ACTION_SET_TEXT = new AccessibilityActionCompat(2097152, null);
      ACTION_SHOW_ON_SCREEN = new AccessibilityActionCompat(AccessibilityNodeInfoCompat.IMPL.getActionShowOnScreen());
      ACTION_SCROLL_TO_POSITION = new AccessibilityActionCompat(AccessibilityNodeInfoCompat.IMPL.getActionScrollToPosition());
      ACTION_SCROLL_UP = new AccessibilityActionCompat(AccessibilityNodeInfoCompat.IMPL.getActionScrollUp());
      ACTION_SCROLL_LEFT = new AccessibilityActionCompat(AccessibilityNodeInfoCompat.IMPL.getActionScrollLeft());
      ACTION_SCROLL_DOWN = new AccessibilityActionCompat(AccessibilityNodeInfoCompat.IMPL.getActionScrollDown());
      ACTION_SCROLL_RIGHT = new AccessibilityActionCompat(AccessibilityNodeInfoCompat.IMPL.getActionScrollRight());
    }
    
    public AccessibilityActionCompat(int paramInt, CharSequence paramCharSequence)
    {
      this(AccessibilityNodeInfoCompat.IMPL.newAccessibilityAction(paramInt, paramCharSequence));
    }
    
    AccessibilityActionCompat(Object paramObject)
    {
      this.mAction = paramObject;
    }
    
    public int getId()
    {
      return AccessibilityNodeInfoCompat.IMPL.getAccessibilityActionId(this.mAction);
    }
    
    public CharSequence getLabel()
    {
      return AccessibilityNodeInfoCompat.IMPL.getAccessibilityActionLabel(this.mAction);
    }
  }
  
  @RequiresApi(16)
  static class AccessibilityNodeInfoApi16Impl
    extends AccessibilityNodeInfoCompat.AccessibilityNodeInfoBaseImpl
  {
    public void addChild(AccessibilityNodeInfo paramAccessibilityNodeInfo, View paramView, int paramInt)
    {
      paramAccessibilityNodeInfo.addChild(paramView, paramInt);
    }
    
    public Object findFocus(AccessibilityNodeInfo paramAccessibilityNodeInfo, int paramInt)
    {
      return paramAccessibilityNodeInfo.findFocus(paramInt);
    }
    
    public Object focusSearch(AccessibilityNodeInfo paramAccessibilityNodeInfo, int paramInt)
    {
      return paramAccessibilityNodeInfo.focusSearch(paramInt);
    }
    
    public int getMovementGranularities(AccessibilityNodeInfo paramAccessibilityNodeInfo)
    {
      return paramAccessibilityNodeInfo.getMovementGranularities();
    }
    
    public boolean isAccessibilityFocused(AccessibilityNodeInfo paramAccessibilityNodeInfo)
    {
      return paramAccessibilityNodeInfo.isAccessibilityFocused();
    }
    
    public boolean isVisibleToUser(AccessibilityNodeInfo paramAccessibilityNodeInfo)
    {
      return paramAccessibilityNodeInfo.isVisibleToUser();
    }
    
    public AccessibilityNodeInfo obtain(View paramView, int paramInt)
    {
      return AccessibilityNodeInfo.obtain(paramView, paramInt);
    }
    
    public boolean performAction(AccessibilityNodeInfo paramAccessibilityNodeInfo, int paramInt, Bundle paramBundle)
    {
      return paramAccessibilityNodeInfo.performAction(paramInt, paramBundle);
    }
    
    public void setAccessibilityFocused(AccessibilityNodeInfo paramAccessibilityNodeInfo, boolean paramBoolean)
    {
      paramAccessibilityNodeInfo.setAccessibilityFocused(paramBoolean);
    }
    
    public void setMovementGranularities(AccessibilityNodeInfo paramAccessibilityNodeInfo, int paramInt)
    {
      paramAccessibilityNodeInfo.setMovementGranularities(paramInt);
    }
    
    public void setParent(AccessibilityNodeInfo paramAccessibilityNodeInfo, View paramView, int paramInt)
    {
      paramAccessibilityNodeInfo.setParent(paramView, paramInt);
    }
    
    public void setSource(AccessibilityNodeInfo paramAccessibilityNodeInfo, View paramView, int paramInt)
    {
      paramAccessibilityNodeInfo.setSource(paramView, paramInt);
    }
    
    public void setVisibleToUser(AccessibilityNodeInfo paramAccessibilityNodeInfo, boolean paramBoolean)
    {
      paramAccessibilityNodeInfo.setVisibleToUser(paramBoolean);
    }
  }
  
  @RequiresApi(17)
  static class AccessibilityNodeInfoApi17Impl
    extends AccessibilityNodeInfoCompat.AccessibilityNodeInfoApi16Impl
  {
    public Object getLabelFor(AccessibilityNodeInfo paramAccessibilityNodeInfo)
    {
      return paramAccessibilityNodeInfo.getLabelFor();
    }
    
    public Object getLabeledBy(AccessibilityNodeInfo paramAccessibilityNodeInfo)
    {
      return paramAccessibilityNodeInfo.getLabeledBy();
    }
    
    public void setLabelFor(AccessibilityNodeInfo paramAccessibilityNodeInfo, View paramView)
    {
      paramAccessibilityNodeInfo.setLabelFor(paramView);
    }
    
    public void setLabelFor(AccessibilityNodeInfo paramAccessibilityNodeInfo, View paramView, int paramInt)
    {
      paramAccessibilityNodeInfo.setLabelFor(paramView, paramInt);
    }
    
    public void setLabeledBy(AccessibilityNodeInfo paramAccessibilityNodeInfo, View paramView)
    {
      paramAccessibilityNodeInfo.setLabeledBy(paramView);
    }
    
    public void setLabeledBy(AccessibilityNodeInfo paramAccessibilityNodeInfo, View paramView, int paramInt)
    {
      paramAccessibilityNodeInfo.setLabeledBy(paramView, paramInt);
    }
  }
  
  @RequiresApi(18)
  static class AccessibilityNodeInfoApi18Impl
    extends AccessibilityNodeInfoCompat.AccessibilityNodeInfoApi17Impl
  {
    public List<AccessibilityNodeInfo> findAccessibilityNodeInfosByViewId(AccessibilityNodeInfo paramAccessibilityNodeInfo, String paramString)
    {
      return paramAccessibilityNodeInfo.findAccessibilityNodeInfosByViewId(paramString);
    }
    
    public int getTextSelectionEnd(AccessibilityNodeInfo paramAccessibilityNodeInfo)
    {
      return paramAccessibilityNodeInfo.getTextSelectionEnd();
    }
    
    public int getTextSelectionStart(AccessibilityNodeInfo paramAccessibilityNodeInfo)
    {
      return paramAccessibilityNodeInfo.getTextSelectionStart();
    }
    
    public String getViewIdResourceName(AccessibilityNodeInfo paramAccessibilityNodeInfo)
    {
      return paramAccessibilityNodeInfo.getViewIdResourceName();
    }
    
    public boolean isEditable(AccessibilityNodeInfo paramAccessibilityNodeInfo)
    {
      return paramAccessibilityNodeInfo.isEditable();
    }
    
    public boolean refresh(AccessibilityNodeInfo paramAccessibilityNodeInfo)
    {
      return paramAccessibilityNodeInfo.refresh();
    }
    
    public void setEditable(AccessibilityNodeInfo paramAccessibilityNodeInfo, boolean paramBoolean)
    {
      paramAccessibilityNodeInfo.setEditable(paramBoolean);
    }
    
    public void setTextSelection(AccessibilityNodeInfo paramAccessibilityNodeInfo, int paramInt1, int paramInt2)
    {
      paramAccessibilityNodeInfo.setTextSelection(paramInt1, paramInt2);
    }
    
    public void setViewIdResourceName(AccessibilityNodeInfo paramAccessibilityNodeInfo, String paramString)
    {
      paramAccessibilityNodeInfo.setViewIdResourceName(paramString);
    }
  }
  
  @RequiresApi(19)
  static class AccessibilityNodeInfoApi19Impl
    extends AccessibilityNodeInfoCompat.AccessibilityNodeInfoApi18Impl
  {
    private static final String ROLE_DESCRIPTION_KEY = "AccessibilityNodeInfo.roleDescription";
    
    public boolean canOpenPopup(AccessibilityNodeInfo paramAccessibilityNodeInfo)
    {
      return paramAccessibilityNodeInfo.canOpenPopup();
    }
    
    public Object getCollectionInfo(AccessibilityNodeInfo paramAccessibilityNodeInfo)
    {
      return paramAccessibilityNodeInfo.getCollectionInfo();
    }
    
    public int getCollectionInfoColumnCount(Object paramObject)
    {
      return ((AccessibilityNodeInfo.CollectionInfo)paramObject).getColumnCount();
    }
    
    public int getCollectionInfoRowCount(Object paramObject)
    {
      return ((AccessibilityNodeInfo.CollectionInfo)paramObject).getRowCount();
    }
    
    public int getCollectionItemColumnIndex(Object paramObject)
    {
      return ((AccessibilityNodeInfo.CollectionItemInfo)paramObject).getColumnIndex();
    }
    
    public int getCollectionItemColumnSpan(Object paramObject)
    {
      return ((AccessibilityNodeInfo.CollectionItemInfo)paramObject).getColumnSpan();
    }
    
    public Object getCollectionItemInfo(AccessibilityNodeInfo paramAccessibilityNodeInfo)
    {
      return paramAccessibilityNodeInfo.getCollectionItemInfo();
    }
    
    public int getCollectionItemRowIndex(Object paramObject)
    {
      return ((AccessibilityNodeInfo.CollectionItemInfo)paramObject).getRowIndex();
    }
    
    public int getCollectionItemRowSpan(Object paramObject)
    {
      return ((AccessibilityNodeInfo.CollectionItemInfo)paramObject).getRowSpan();
    }
    
    public Bundle getExtras(AccessibilityNodeInfo paramAccessibilityNodeInfo)
    {
      return paramAccessibilityNodeInfo.getExtras();
    }
    
    public int getInputType(AccessibilityNodeInfo paramAccessibilityNodeInfo)
    {
      return paramAccessibilityNodeInfo.getInputType();
    }
    
    public int getLiveRegion(AccessibilityNodeInfo paramAccessibilityNodeInfo)
    {
      return paramAccessibilityNodeInfo.getLiveRegion();
    }
    
    public Object getRangeInfo(AccessibilityNodeInfo paramAccessibilityNodeInfo)
    {
      return paramAccessibilityNodeInfo.getRangeInfo();
    }
    
    public CharSequence getRoleDescription(AccessibilityNodeInfo paramAccessibilityNodeInfo)
    {
      return getExtras(paramAccessibilityNodeInfo).getCharSequence("AccessibilityNodeInfo.roleDescription");
    }
    
    public boolean isCollectionInfoHierarchical(Object paramObject)
    {
      return ((AccessibilityNodeInfo.CollectionInfo)paramObject).isHierarchical();
    }
    
    public boolean isCollectionItemHeading(Object paramObject)
    {
      return ((AccessibilityNodeInfo.CollectionItemInfo)paramObject).isHeading();
    }
    
    public boolean isContentInvalid(AccessibilityNodeInfo paramAccessibilityNodeInfo)
    {
      return paramAccessibilityNodeInfo.isContentInvalid();
    }
    
    public boolean isDismissable(AccessibilityNodeInfo paramAccessibilityNodeInfo)
    {
      return paramAccessibilityNodeInfo.isDismissable();
    }
    
    public boolean isMultiLine(AccessibilityNodeInfo paramAccessibilityNodeInfo)
    {
      return paramAccessibilityNodeInfo.isMultiLine();
    }
    
    public Object obtainCollectionInfo(int paramInt1, int paramInt2, boolean paramBoolean)
    {
      return AccessibilityNodeInfo.CollectionInfo.obtain(paramInt1, paramInt2, paramBoolean);
    }
    
    public Object obtainCollectionInfo(int paramInt1, int paramInt2, boolean paramBoolean, int paramInt3)
    {
      return AccessibilityNodeInfo.CollectionInfo.obtain(paramInt1, paramInt2, paramBoolean);
    }
    
    public Object obtainCollectionItemInfo(int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean)
    {
      return AccessibilityNodeInfo.CollectionItemInfo.obtain(paramInt1, paramInt2, paramInt3, paramInt4, paramBoolean);
    }
    
    public Object obtainCollectionItemInfo(int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean1, boolean paramBoolean2)
    {
      return AccessibilityNodeInfo.CollectionItemInfo.obtain(paramInt1, paramInt2, paramInt3, paramInt4, paramBoolean1);
    }
    
    public Object obtainRangeInfo(int paramInt, float paramFloat1, float paramFloat2, float paramFloat3)
    {
      return AccessibilityNodeInfo.RangeInfo.obtain(paramInt, paramFloat1, paramFloat2, paramFloat3);
    }
    
    public void setCanOpenPopup(AccessibilityNodeInfo paramAccessibilityNodeInfo, boolean paramBoolean)
    {
      paramAccessibilityNodeInfo.setCanOpenPopup(paramBoolean);
    }
    
    public void setCollectionInfo(AccessibilityNodeInfo paramAccessibilityNodeInfo, Object paramObject)
    {
      paramAccessibilityNodeInfo.setCollectionInfo((AccessibilityNodeInfo.CollectionInfo)paramObject);
    }
    
    public void setCollectionItemInfo(AccessibilityNodeInfo paramAccessibilityNodeInfo, Object paramObject)
    {
      paramAccessibilityNodeInfo.setCollectionItemInfo((AccessibilityNodeInfo.CollectionItemInfo)paramObject);
    }
    
    public void setContentInvalid(AccessibilityNodeInfo paramAccessibilityNodeInfo, boolean paramBoolean)
    {
      paramAccessibilityNodeInfo.setContentInvalid(paramBoolean);
    }
    
    public void setDismissable(AccessibilityNodeInfo paramAccessibilityNodeInfo, boolean paramBoolean)
    {
      paramAccessibilityNodeInfo.setDismissable(paramBoolean);
    }
    
    public void setInputType(AccessibilityNodeInfo paramAccessibilityNodeInfo, int paramInt)
    {
      paramAccessibilityNodeInfo.setInputType(paramInt);
    }
    
    public void setLiveRegion(AccessibilityNodeInfo paramAccessibilityNodeInfo, int paramInt)
    {
      paramAccessibilityNodeInfo.setLiveRegion(paramInt);
    }
    
    public void setMultiLine(AccessibilityNodeInfo paramAccessibilityNodeInfo, boolean paramBoolean)
    {
      paramAccessibilityNodeInfo.setMultiLine(paramBoolean);
    }
    
    public void setRangeInfo(AccessibilityNodeInfo paramAccessibilityNodeInfo, Object paramObject)
    {
      paramAccessibilityNodeInfo.setRangeInfo((AccessibilityNodeInfo.RangeInfo)paramObject);
    }
    
    public void setRoleDescription(AccessibilityNodeInfo paramAccessibilityNodeInfo, CharSequence paramCharSequence)
    {
      getExtras(paramAccessibilityNodeInfo).putCharSequence("AccessibilityNodeInfo.roleDescription", paramCharSequence);
    }
  }
  
  @RequiresApi(21)
  static class AccessibilityNodeInfoApi21Impl
    extends AccessibilityNodeInfoCompat.AccessibilityNodeInfoApi19Impl
  {
    public void addAction(AccessibilityNodeInfo paramAccessibilityNodeInfo, Object paramObject)
    {
      paramAccessibilityNodeInfo.addAction((AccessibilityNodeInfo.AccessibilityAction)paramObject);
    }
    
    public int getAccessibilityActionId(Object paramObject)
    {
      return ((AccessibilityNodeInfo.AccessibilityAction)paramObject).getId();
    }
    
    public CharSequence getAccessibilityActionLabel(Object paramObject)
    {
      return ((AccessibilityNodeInfo.AccessibilityAction)paramObject).getLabel();
    }
    
    public List<Object> getActionList(AccessibilityNodeInfo paramAccessibilityNodeInfo)
    {
      return (List)paramAccessibilityNodeInfo.getActionList();
    }
    
    public int getCollectionInfoSelectionMode(Object paramObject)
    {
      return ((AccessibilityNodeInfo.CollectionInfo)paramObject).getSelectionMode();
    }
    
    public CharSequence getError(AccessibilityNodeInfo paramAccessibilityNodeInfo)
    {
      return paramAccessibilityNodeInfo.getError();
    }
    
    public int getMaxTextLength(AccessibilityNodeInfo paramAccessibilityNodeInfo)
    {
      return paramAccessibilityNodeInfo.getMaxTextLength();
    }
    
    public Object getWindow(AccessibilityNodeInfo paramAccessibilityNodeInfo)
    {
      return paramAccessibilityNodeInfo.getWindow();
    }
    
    public boolean isCollectionItemSelected(Object paramObject)
    {
      return ((AccessibilityNodeInfo.CollectionItemInfo)paramObject).isSelected();
    }
    
    public Object newAccessibilityAction(int paramInt, CharSequence paramCharSequence)
    {
      return new AccessibilityNodeInfo.AccessibilityAction(paramInt, paramCharSequence);
    }
    
    public Object obtainCollectionInfo(int paramInt1, int paramInt2, boolean paramBoolean, int paramInt3)
    {
      return AccessibilityNodeInfo.CollectionInfo.obtain(paramInt1, paramInt2, paramBoolean, paramInt3);
    }
    
    public Object obtainCollectionItemInfo(int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean1, boolean paramBoolean2)
    {
      return AccessibilityNodeInfo.CollectionItemInfo.obtain(paramInt1, paramInt2, paramInt3, paramInt4, paramBoolean1, paramBoolean2);
    }
    
    public boolean removeAction(AccessibilityNodeInfo paramAccessibilityNodeInfo, Object paramObject)
    {
      return paramAccessibilityNodeInfo.removeAction((AccessibilityNodeInfo.AccessibilityAction)paramObject);
    }
    
    public boolean removeChild(AccessibilityNodeInfo paramAccessibilityNodeInfo, View paramView)
    {
      return paramAccessibilityNodeInfo.removeChild(paramView);
    }
    
    public boolean removeChild(AccessibilityNodeInfo paramAccessibilityNodeInfo, View paramView, int paramInt)
    {
      return paramAccessibilityNodeInfo.removeChild(paramView, paramInt);
    }
    
    public void setError(AccessibilityNodeInfo paramAccessibilityNodeInfo, CharSequence paramCharSequence)
    {
      paramAccessibilityNodeInfo.setError(paramCharSequence);
    }
    
    public void setMaxTextLength(AccessibilityNodeInfo paramAccessibilityNodeInfo, int paramInt)
    {
      paramAccessibilityNodeInfo.setMaxTextLength(paramInt);
    }
  }
  
  @RequiresApi(22)
  static class AccessibilityNodeInfoApi22Impl
    extends AccessibilityNodeInfoCompat.AccessibilityNodeInfoApi21Impl
  {
    public Object getTraversalAfter(AccessibilityNodeInfo paramAccessibilityNodeInfo)
    {
      return paramAccessibilityNodeInfo.getTraversalAfter();
    }
    
    public Object getTraversalBefore(AccessibilityNodeInfo paramAccessibilityNodeInfo)
    {
      return paramAccessibilityNodeInfo.getTraversalBefore();
    }
    
    public void setTraversalAfter(AccessibilityNodeInfo paramAccessibilityNodeInfo, View paramView)
    {
      paramAccessibilityNodeInfo.setTraversalAfter(paramView);
    }
    
    public void setTraversalAfter(AccessibilityNodeInfo paramAccessibilityNodeInfo, View paramView, int paramInt)
    {
      paramAccessibilityNodeInfo.setTraversalAfter(paramView, paramInt);
    }
    
    public void setTraversalBefore(AccessibilityNodeInfo paramAccessibilityNodeInfo, View paramView)
    {
      paramAccessibilityNodeInfo.setTraversalBefore(paramView);
    }
    
    public void setTraversalBefore(AccessibilityNodeInfo paramAccessibilityNodeInfo, View paramView, int paramInt)
    {
      paramAccessibilityNodeInfo.setTraversalBefore(paramView, paramInt);
    }
  }
  
  @RequiresApi(23)
  static class AccessibilityNodeInfoApi23Impl
    extends AccessibilityNodeInfoCompat.AccessibilityNodeInfoApi22Impl
  {
    public Object getActionContextClick()
    {
      return AccessibilityNodeInfo.AccessibilityAction.ACTION_CONTEXT_CLICK;
    }
    
    public Object getActionScrollDown()
    {
      return AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_DOWN;
    }
    
    public Object getActionScrollLeft()
    {
      return AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_LEFT;
    }
    
    public Object getActionScrollRight()
    {
      return AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_RIGHT;
    }
    
    public Object getActionScrollToPosition()
    {
      return AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_TO_POSITION;
    }
    
    public Object getActionScrollUp()
    {
      return AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_UP;
    }
    
    public Object getActionShowOnScreen()
    {
      return AccessibilityNodeInfo.AccessibilityAction.ACTION_SHOW_ON_SCREEN;
    }
    
    public boolean isContextClickable(AccessibilityNodeInfo paramAccessibilityNodeInfo)
    {
      return paramAccessibilityNodeInfo.isContextClickable();
    }
    
    public void setContextClickable(AccessibilityNodeInfo paramAccessibilityNodeInfo, boolean paramBoolean)
    {
      paramAccessibilityNodeInfo.setContextClickable(paramBoolean);
    }
  }
  
  @RequiresApi(24)
  static class AccessibilityNodeInfoApi24Impl
    extends AccessibilityNodeInfoCompat.AccessibilityNodeInfoApi23Impl
  {
    public Object getActionSetProgress()
    {
      return AccessibilityNodeInfo.AccessibilityAction.ACTION_SET_PROGRESS;
    }
    
    public int getDrawingOrder(AccessibilityNodeInfo paramAccessibilityNodeInfo)
    {
      return paramAccessibilityNodeInfo.getDrawingOrder();
    }
    
    public boolean isImportantForAccessibility(AccessibilityNodeInfo paramAccessibilityNodeInfo)
    {
      return paramAccessibilityNodeInfo.isImportantForAccessibility();
    }
    
    public void setDrawingOrder(AccessibilityNodeInfo paramAccessibilityNodeInfo, int paramInt)
    {
      paramAccessibilityNodeInfo.setDrawingOrder(paramInt);
    }
    
    public void setImportantForAccessibility(AccessibilityNodeInfo paramAccessibilityNodeInfo, boolean paramBoolean)
    {
      paramAccessibilityNodeInfo.setImportantForAccessibility(paramBoolean);
    }
  }
  
  static class AccessibilityNodeInfoBaseImpl
  {
    public void addAction(AccessibilityNodeInfo paramAccessibilityNodeInfo, Object paramObject) {}
    
    public void addChild(AccessibilityNodeInfo paramAccessibilityNodeInfo, View paramView, int paramInt) {}
    
    public boolean canOpenPopup(AccessibilityNodeInfo paramAccessibilityNodeInfo)
    {
      return false;
    }
    
    public List<AccessibilityNodeInfo> findAccessibilityNodeInfosByViewId(AccessibilityNodeInfo paramAccessibilityNodeInfo, String paramString)
    {
      return Collections.emptyList();
    }
    
    public Object findFocus(AccessibilityNodeInfo paramAccessibilityNodeInfo, int paramInt)
    {
      return null;
    }
    
    public Object focusSearch(AccessibilityNodeInfo paramAccessibilityNodeInfo, int paramInt)
    {
      return null;
    }
    
    public int getAccessibilityActionId(Object paramObject)
    {
      return 0;
    }
    
    public CharSequence getAccessibilityActionLabel(Object paramObject)
    {
      return null;
    }
    
    public Object getActionContextClick()
    {
      return null;
    }
    
    public List<Object> getActionList(AccessibilityNodeInfo paramAccessibilityNodeInfo)
    {
      return null;
    }
    
    public Object getActionScrollDown()
    {
      return null;
    }
    
    public Object getActionScrollLeft()
    {
      return null;
    }
    
    public Object getActionScrollRight()
    {
      return null;
    }
    
    public Object getActionScrollToPosition()
    {
      return null;
    }
    
    public Object getActionScrollUp()
    {
      return null;
    }
    
    public Object getActionSetProgress()
    {
      return null;
    }
    
    public Object getActionShowOnScreen()
    {
      return null;
    }
    
    public Object getCollectionInfo(AccessibilityNodeInfo paramAccessibilityNodeInfo)
    {
      return null;
    }
    
    public int getCollectionInfoColumnCount(Object paramObject)
    {
      return 0;
    }
    
    public int getCollectionInfoRowCount(Object paramObject)
    {
      return 0;
    }
    
    public int getCollectionInfoSelectionMode(Object paramObject)
    {
      return 0;
    }
    
    public int getCollectionItemColumnIndex(Object paramObject)
    {
      return 0;
    }
    
    public int getCollectionItemColumnSpan(Object paramObject)
    {
      return 0;
    }
    
    public Object getCollectionItemInfo(AccessibilityNodeInfo paramAccessibilityNodeInfo)
    {
      return null;
    }
    
    public int getCollectionItemRowIndex(Object paramObject)
    {
      return 0;
    }
    
    public int getCollectionItemRowSpan(Object paramObject)
    {
      return 0;
    }
    
    public int getDrawingOrder(AccessibilityNodeInfo paramAccessibilityNodeInfo)
    {
      return 0;
    }
    
    public CharSequence getError(AccessibilityNodeInfo paramAccessibilityNodeInfo)
    {
      return null;
    }
    
    public Bundle getExtras(AccessibilityNodeInfo paramAccessibilityNodeInfo)
    {
      return new Bundle();
    }
    
    public int getInputType(AccessibilityNodeInfo paramAccessibilityNodeInfo)
    {
      return 0;
    }
    
    public Object getLabelFor(AccessibilityNodeInfo paramAccessibilityNodeInfo)
    {
      return null;
    }
    
    public Object getLabeledBy(AccessibilityNodeInfo paramAccessibilityNodeInfo)
    {
      return null;
    }
    
    public int getLiveRegion(AccessibilityNodeInfo paramAccessibilityNodeInfo)
    {
      return 0;
    }
    
    public int getMaxTextLength(AccessibilityNodeInfo paramAccessibilityNodeInfo)
    {
      return -1;
    }
    
    public int getMovementGranularities(AccessibilityNodeInfo paramAccessibilityNodeInfo)
    {
      return 0;
    }
    
    public Object getRangeInfo(AccessibilityNodeInfo paramAccessibilityNodeInfo)
    {
      return null;
    }
    
    public CharSequence getRoleDescription(AccessibilityNodeInfo paramAccessibilityNodeInfo)
    {
      return null;
    }
    
    public int getTextSelectionEnd(AccessibilityNodeInfo paramAccessibilityNodeInfo)
    {
      return -1;
    }
    
    public int getTextSelectionStart(AccessibilityNodeInfo paramAccessibilityNodeInfo)
    {
      return -1;
    }
    
    public Object getTraversalAfter(AccessibilityNodeInfo paramAccessibilityNodeInfo)
    {
      return null;
    }
    
    public Object getTraversalBefore(AccessibilityNodeInfo paramAccessibilityNodeInfo)
    {
      return null;
    }
    
    public String getViewIdResourceName(AccessibilityNodeInfo paramAccessibilityNodeInfo)
    {
      return null;
    }
    
    public Object getWindow(AccessibilityNodeInfo paramAccessibilityNodeInfo)
    {
      return null;
    }
    
    public boolean isAccessibilityFocused(AccessibilityNodeInfo paramAccessibilityNodeInfo)
    {
      return false;
    }
    
    public boolean isCollectionInfoHierarchical(Object paramObject)
    {
      return false;
    }
    
    public boolean isCollectionItemHeading(Object paramObject)
    {
      return false;
    }
    
    public boolean isCollectionItemSelected(Object paramObject)
    {
      return false;
    }
    
    public boolean isContentInvalid(AccessibilityNodeInfo paramAccessibilityNodeInfo)
    {
      return false;
    }
    
    public boolean isContextClickable(AccessibilityNodeInfo paramAccessibilityNodeInfo)
    {
      return false;
    }
    
    public boolean isDismissable(AccessibilityNodeInfo paramAccessibilityNodeInfo)
    {
      return false;
    }
    
    public boolean isEditable(AccessibilityNodeInfo paramAccessibilityNodeInfo)
    {
      return false;
    }
    
    public boolean isImportantForAccessibility(AccessibilityNodeInfo paramAccessibilityNodeInfo)
    {
      return true;
    }
    
    public boolean isMultiLine(AccessibilityNodeInfo paramAccessibilityNodeInfo)
    {
      return false;
    }
    
    public boolean isVisibleToUser(AccessibilityNodeInfo paramAccessibilityNodeInfo)
    {
      return false;
    }
    
    public Object newAccessibilityAction(int paramInt, CharSequence paramCharSequence)
    {
      return null;
    }
    
    public AccessibilityNodeInfo obtain(View paramView, int paramInt)
    {
      return null;
    }
    
    public Object obtainCollectionInfo(int paramInt1, int paramInt2, boolean paramBoolean)
    {
      return null;
    }
    
    public Object obtainCollectionInfo(int paramInt1, int paramInt2, boolean paramBoolean, int paramInt3)
    {
      return null;
    }
    
    public Object obtainCollectionItemInfo(int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean)
    {
      return null;
    }
    
    public Object obtainCollectionItemInfo(int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean1, boolean paramBoolean2)
    {
      return null;
    }
    
    public Object obtainRangeInfo(int paramInt, float paramFloat1, float paramFloat2, float paramFloat3)
    {
      return null;
    }
    
    public boolean performAction(AccessibilityNodeInfo paramAccessibilityNodeInfo, int paramInt, Bundle paramBundle)
    {
      return false;
    }
    
    public boolean refresh(AccessibilityNodeInfo paramAccessibilityNodeInfo)
    {
      return false;
    }
    
    public boolean removeAction(AccessibilityNodeInfo paramAccessibilityNodeInfo, Object paramObject)
    {
      return false;
    }
    
    public boolean removeChild(AccessibilityNodeInfo paramAccessibilityNodeInfo, View paramView)
    {
      return false;
    }
    
    public boolean removeChild(AccessibilityNodeInfo paramAccessibilityNodeInfo, View paramView, int paramInt)
    {
      return false;
    }
    
    public void setAccessibilityFocused(AccessibilityNodeInfo paramAccessibilityNodeInfo, boolean paramBoolean) {}
    
    public void setCanOpenPopup(AccessibilityNodeInfo paramAccessibilityNodeInfo, boolean paramBoolean) {}
    
    public void setCollectionInfo(AccessibilityNodeInfo paramAccessibilityNodeInfo, Object paramObject) {}
    
    public void setCollectionItemInfo(AccessibilityNodeInfo paramAccessibilityNodeInfo, Object paramObject) {}
    
    public void setContentInvalid(AccessibilityNodeInfo paramAccessibilityNodeInfo, boolean paramBoolean) {}
    
    public void setContextClickable(AccessibilityNodeInfo paramAccessibilityNodeInfo, boolean paramBoolean) {}
    
    public void setDismissable(AccessibilityNodeInfo paramAccessibilityNodeInfo, boolean paramBoolean) {}
    
    public void setDrawingOrder(AccessibilityNodeInfo paramAccessibilityNodeInfo, int paramInt) {}
    
    public void setEditable(AccessibilityNodeInfo paramAccessibilityNodeInfo, boolean paramBoolean) {}
    
    public void setError(AccessibilityNodeInfo paramAccessibilityNodeInfo, CharSequence paramCharSequence) {}
    
    public void setImportantForAccessibility(AccessibilityNodeInfo paramAccessibilityNodeInfo, boolean paramBoolean) {}
    
    public void setInputType(AccessibilityNodeInfo paramAccessibilityNodeInfo, int paramInt) {}
    
    public void setLabelFor(AccessibilityNodeInfo paramAccessibilityNodeInfo, View paramView) {}
    
    public void setLabelFor(AccessibilityNodeInfo paramAccessibilityNodeInfo, View paramView, int paramInt) {}
    
    public void setLabeledBy(AccessibilityNodeInfo paramAccessibilityNodeInfo, View paramView) {}
    
    public void setLabeledBy(AccessibilityNodeInfo paramAccessibilityNodeInfo, View paramView, int paramInt) {}
    
    public void setLiveRegion(AccessibilityNodeInfo paramAccessibilityNodeInfo, int paramInt) {}
    
    public void setMaxTextLength(AccessibilityNodeInfo paramAccessibilityNodeInfo, int paramInt) {}
    
    public void setMovementGranularities(AccessibilityNodeInfo paramAccessibilityNodeInfo, int paramInt) {}
    
    public void setMultiLine(AccessibilityNodeInfo paramAccessibilityNodeInfo, boolean paramBoolean) {}
    
    public void setParent(AccessibilityNodeInfo paramAccessibilityNodeInfo, View paramView, int paramInt) {}
    
    public void setRangeInfo(AccessibilityNodeInfo paramAccessibilityNodeInfo, Object paramObject) {}
    
    public void setRoleDescription(AccessibilityNodeInfo paramAccessibilityNodeInfo, CharSequence paramCharSequence) {}
    
    public void setSource(AccessibilityNodeInfo paramAccessibilityNodeInfo, View paramView, int paramInt) {}
    
    public void setTextSelection(AccessibilityNodeInfo paramAccessibilityNodeInfo, int paramInt1, int paramInt2) {}
    
    public void setTraversalAfter(AccessibilityNodeInfo paramAccessibilityNodeInfo, View paramView) {}
    
    public void setTraversalAfter(AccessibilityNodeInfo paramAccessibilityNodeInfo, View paramView, int paramInt) {}
    
    public void setTraversalBefore(AccessibilityNodeInfo paramAccessibilityNodeInfo, View paramView) {}
    
    public void setTraversalBefore(AccessibilityNodeInfo paramAccessibilityNodeInfo, View paramView, int paramInt) {}
    
    public void setViewIdResourceName(AccessibilityNodeInfo paramAccessibilityNodeInfo, String paramString) {}
    
    public void setVisibleToUser(AccessibilityNodeInfo paramAccessibilityNodeInfo, boolean paramBoolean) {}
  }
  
  public static class CollectionInfoCompat
  {
    public static final int SELECTION_MODE_MULTIPLE = 2;
    public static final int SELECTION_MODE_NONE = 0;
    public static final int SELECTION_MODE_SINGLE = 1;
    final Object mInfo;
    
    CollectionInfoCompat(Object paramObject)
    {
      this.mInfo = paramObject;
    }
    
    public static CollectionInfoCompat obtain(int paramInt1, int paramInt2, boolean paramBoolean)
    {
      return new CollectionInfoCompat(AccessibilityNodeInfoCompat.IMPL.obtainCollectionInfo(paramInt1, paramInt2, paramBoolean));
    }
    
    public static CollectionInfoCompat obtain(int paramInt1, int paramInt2, boolean paramBoolean, int paramInt3)
    {
      return new CollectionInfoCompat(AccessibilityNodeInfoCompat.IMPL.obtainCollectionInfo(paramInt1, paramInt2, paramBoolean, paramInt3));
    }
    
    public int getColumnCount()
    {
      return AccessibilityNodeInfoCompat.IMPL.getCollectionInfoColumnCount(this.mInfo);
    }
    
    public int getRowCount()
    {
      return AccessibilityNodeInfoCompat.IMPL.getCollectionInfoRowCount(this.mInfo);
    }
    
    public int getSelectionMode()
    {
      return AccessibilityNodeInfoCompat.IMPL.getCollectionInfoSelectionMode(this.mInfo);
    }
    
    public boolean isHierarchical()
    {
      return AccessibilityNodeInfoCompat.IMPL.isCollectionInfoHierarchical(this.mInfo);
    }
  }
  
  public static class CollectionItemInfoCompat
  {
    final Object mInfo;
    
    CollectionItemInfoCompat(Object paramObject)
    {
      this.mInfo = paramObject;
    }
    
    public static CollectionItemInfoCompat obtain(int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean)
    {
      return new CollectionItemInfoCompat(AccessibilityNodeInfoCompat.IMPL.obtainCollectionItemInfo(paramInt1, paramInt2, paramInt3, paramInt4, paramBoolean));
    }
    
    public static CollectionItemInfoCompat obtain(int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean1, boolean paramBoolean2)
    {
      return new CollectionItemInfoCompat(AccessibilityNodeInfoCompat.IMPL.obtainCollectionItemInfo(paramInt1, paramInt2, paramInt3, paramInt4, paramBoolean1, paramBoolean2));
    }
    
    public int getColumnIndex()
    {
      return AccessibilityNodeInfoCompat.IMPL.getCollectionItemColumnIndex(this.mInfo);
    }
    
    public int getColumnSpan()
    {
      return AccessibilityNodeInfoCompat.IMPL.getCollectionItemColumnSpan(this.mInfo);
    }
    
    public int getRowIndex()
    {
      return AccessibilityNodeInfoCompat.IMPL.getCollectionItemRowIndex(this.mInfo);
    }
    
    public int getRowSpan()
    {
      return AccessibilityNodeInfoCompat.IMPL.getCollectionItemRowSpan(this.mInfo);
    }
    
    public boolean isHeading()
    {
      return AccessibilityNodeInfoCompat.IMPL.isCollectionItemHeading(this.mInfo);
    }
    
    public boolean isSelected()
    {
      return AccessibilityNodeInfoCompat.IMPL.isCollectionItemSelected(this.mInfo);
    }
  }
  
  public static class RangeInfoCompat
  {
    public static final int RANGE_TYPE_FLOAT = 1;
    public static final int RANGE_TYPE_INT = 0;
    public static final int RANGE_TYPE_PERCENT = 2;
    final Object mInfo;
    
    RangeInfoCompat(Object paramObject)
    {
      this.mInfo = paramObject;
    }
    
    public static RangeInfoCompat obtain(int paramInt, float paramFloat1, float paramFloat2, float paramFloat3)
    {
      return new RangeInfoCompat(AccessibilityNodeInfoCompat.IMPL.obtainRangeInfo(paramInt, paramFloat1, paramFloat2, paramFloat3));
    }
    
    public float getCurrent()
    {
      return AccessibilityNodeInfoCompatKitKat.RangeInfo.getCurrent(this.mInfo);
    }
    
    public float getMax()
    {
      return AccessibilityNodeInfoCompatKitKat.RangeInfo.getMax(this.mInfo);
    }
    
    public float getMin()
    {
      return AccessibilityNodeInfoCompatKitKat.RangeInfo.getMin(this.mInfo);
    }
    
    public int getType()
    {
      return AccessibilityNodeInfoCompatKitKat.RangeInfo.getType(this.mInfo);
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v4/view/accessibility/AccessibilityNodeInfoCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */