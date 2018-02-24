package com.bumptech.glide.request.target;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.WindowManager;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.util.Preconditions;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class ViewTarget<T extends View, Z>
  extends BaseTarget<Z>
{
  private static final String TAG = "ViewTarget";
  private static boolean isTagUsedAtLeastOnce = false;
  @Nullable
  private static Integer tagId = null;
  private final SizeDeterminer sizeDeterminer;
  protected final T view;
  
  public ViewTarget(T paramT)
  {
    this.view = ((View)Preconditions.checkNotNull(paramT));
    this.sizeDeterminer = new SizeDeterminer(paramT);
  }
  
  @Nullable
  private Object getTag()
  {
    if (tagId == null) {
      return this.view.getTag();
    }
    return this.view.getTag(tagId.intValue());
  }
  
  private void setTag(@Nullable Object paramObject)
  {
    if (tagId == null)
    {
      isTagUsedAtLeastOnce = true;
      this.view.setTag(paramObject);
      return;
    }
    this.view.setTag(tagId.intValue(), paramObject);
  }
  
  public static void setTagId(int paramInt)
  {
    if ((tagId != null) || (isTagUsedAtLeastOnce)) {
      throw new IllegalArgumentException("You cannot set the tag id more than once or change the tag id after the first request has been made");
    }
    tagId = Integer.valueOf(paramInt);
  }
  
  @Nullable
  public Request getRequest()
  {
    Object localObject = getTag();
    Request localRequest = null;
    if (localObject != null)
    {
      if ((localObject instanceof Request)) {
        localRequest = (Request)localObject;
      }
    }
    else {
      return localRequest;
    }
    throw new IllegalArgumentException("You must not call setTag() on a view Glide is targeting");
  }
  
  public void getSize(SizeReadyCallback paramSizeReadyCallback)
  {
    this.sizeDeterminer.getSize(paramSizeReadyCallback);
  }
  
  public T getView()
  {
    return this.view;
  }
  
  public void onLoadCleared(Drawable paramDrawable)
  {
    super.onLoadCleared(paramDrawable);
    this.sizeDeterminer.clearCallbacksAndListener();
  }
  
  public void setRequest(@Nullable Request paramRequest)
  {
    setTag(paramRequest);
  }
  
  public String toString()
  {
    String str = String.valueOf(this.view);
    return String.valueOf(str).length() + 12 + "Target for: " + str;
  }
  
  private static class SizeDeterminer
  {
    private static final int PENDING_SIZE = 0;
    private final List<SizeReadyCallback> cbs = new ArrayList();
    @Nullable
    private Point displayDimens;
    @Nullable
    private SizeDeterminerLayoutListener layoutListener;
    private final View view;
    
    public SizeDeterminer(View paramView)
    {
      this.view = paramView;
    }
    
    private void checkCurrentDimens()
    {
      if (this.cbs.isEmpty()) {}
      int i;
      int j;
      do
      {
        return;
        i = getViewWidthOrParam();
        j = getViewHeightOrParam();
      } while ((!isSizeValid(i)) || (!isSizeValid(j)));
      notifyCbs(i, j);
      clearCallbacksAndListener();
    }
    
    @TargetApi(13)
    private Point getDisplayDimens()
    {
      if (this.displayDimens != null) {
        return this.displayDimens;
      }
      Display localDisplay = ((WindowManager)this.view.getContext().getSystemService("window")).getDefaultDisplay();
      if (Build.VERSION.SDK_INT >= 13)
      {
        this.displayDimens = new Point();
        localDisplay.getSize(this.displayDimens);
      }
      for (;;)
      {
        return this.displayDimens;
        this.displayDimens = new Point(localDisplay.getWidth(), localDisplay.getHeight());
      }
    }
    
    private int getSizeForParam(int paramInt, boolean paramBoolean)
    {
      if (paramInt == -2)
      {
        Point localPoint = getDisplayDimens();
        if (paramBoolean) {
          return localPoint.y;
        }
        return localPoint.x;
      }
      return paramInt;
    }
    
    private int getViewHeightOrParam()
    {
      ViewGroup.LayoutParams localLayoutParams = this.view.getLayoutParams();
      if (isSizeValid(this.view.getHeight())) {
        return this.view.getHeight();
      }
      if (localLayoutParams != null) {
        return getSizeForParam(localLayoutParams.height, true);
      }
      return 0;
    }
    
    private int getViewWidthOrParam()
    {
      int i = 0;
      ViewGroup.LayoutParams localLayoutParams = this.view.getLayoutParams();
      if (isSizeValid(this.view.getWidth())) {
        i = this.view.getWidth();
      }
      while (localLayoutParams == null) {
        return i;
      }
      return getSizeForParam(localLayoutParams.width, false);
    }
    
    private boolean isSizeValid(int paramInt)
    {
      return (paramInt > 0) || (paramInt == -2);
    }
    
    private void notifyCbs(int paramInt1, int paramInt2)
    {
      Iterator localIterator = this.cbs.iterator();
      while (localIterator.hasNext()) {
        ((SizeReadyCallback)localIterator.next()).onSizeReady(paramInt1, paramInt2);
      }
    }
    
    void clearCallbacksAndListener()
    {
      ViewTreeObserver localViewTreeObserver = this.view.getViewTreeObserver();
      if (localViewTreeObserver.isAlive()) {
        localViewTreeObserver.removeOnPreDrawListener(this.layoutListener);
      }
      this.layoutListener = null;
      this.cbs.clear();
    }
    
    void getSize(SizeReadyCallback paramSizeReadyCallback)
    {
      int i = getViewWidthOrParam();
      int j = getViewHeightOrParam();
      if ((isSizeValid(i)) && (isSizeValid(j))) {
        if (i == -2)
        {
          if (j != -2) {
            break label68;
          }
          label38:
          paramSizeReadyCallback.onSizeReady(i, j);
        }
      }
      label68:
      do
      {
        return;
        i = i - ViewCompat.getPaddingStart(this.view) - ViewCompat.getPaddingEnd(this.view);
        break;
        j = j - this.view.getPaddingTop() - this.view.getPaddingBottom();
        break label38;
        if (!this.cbs.contains(paramSizeReadyCallback)) {
          this.cbs.add(paramSizeReadyCallback);
        }
      } while (this.layoutListener != null);
      paramSizeReadyCallback = this.view.getViewTreeObserver();
      this.layoutListener = new SizeDeterminerLayoutListener(this);
      paramSizeReadyCallback.addOnPreDrawListener(this.layoutListener);
    }
    
    private static class SizeDeterminerLayoutListener
      implements ViewTreeObserver.OnPreDrawListener
    {
      private final WeakReference<ViewTarget.SizeDeterminer> sizeDeterminerRef;
      
      public SizeDeterminerLayoutListener(ViewTarget.SizeDeterminer paramSizeDeterminer)
      {
        this.sizeDeterminerRef = new WeakReference(paramSizeDeterminer);
      }
      
      public boolean onPreDraw()
      {
        if (Log.isLoggable("ViewTarget", 2))
        {
          localObject = String.valueOf(this);
          Log.v("ViewTarget", String.valueOf(localObject).length() + 39 + "OnGlobalLayoutListener called listener=" + (String)localObject);
        }
        Object localObject = (ViewTarget.SizeDeterminer)this.sizeDeterminerRef.get();
        if (localObject != null) {
          ((ViewTarget.SizeDeterminer)localObject).checkCurrentDimens();
        }
        return true;
      }
    }
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/request/target/ViewTarget.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */