package android.support.v17.leanback.widget;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.RecyclerView.LayoutManager.Properties;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.view.View.OnLayoutChangeListener;
import java.util.Iterator;
import java.util.List;

public class RecyclerViewParallax
  extends Parallax<ChildPositionProperty>
{
  boolean mIsVertical;
  View.OnLayoutChangeListener mOnLayoutChangeListener = new View.OnLayoutChangeListener()
  {
    public void onLayoutChange(View paramAnonymousView, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, int paramAnonymousInt4, int paramAnonymousInt5, int paramAnonymousInt6, int paramAnonymousInt7, int paramAnonymousInt8)
    {
      RecyclerViewParallax.this.updateValues();
    }
  };
  RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener()
  {
    public void onScrolled(RecyclerView paramAnonymousRecyclerView, int paramAnonymousInt1, int paramAnonymousInt2)
    {
      RecyclerViewParallax.this.updateValues();
    }
  };
  RecyclerView mRecylerView;
  
  public ChildPositionProperty createProperty(String paramString, int paramInt)
  {
    return new ChildPositionProperty(paramString, paramInt);
  }
  
  public float getMaxValue()
  {
    if (this.mRecylerView == null) {
      return 0.0F;
    }
    if (this.mIsVertical) {
      return this.mRecylerView.getHeight();
    }
    return this.mRecylerView.getWidth();
  }
  
  public RecyclerView getRecyclerView()
  {
    return this.mRecylerView;
  }
  
  public void setRecyclerView(RecyclerView paramRecyclerView)
  {
    boolean bool = true;
    if (this.mRecylerView == paramRecyclerView) {}
    do
    {
      return;
      if (this.mRecylerView != null)
      {
        this.mRecylerView.removeOnScrollListener(this.mOnScrollListener);
        this.mRecylerView.removeOnLayoutChangeListener(this.mOnLayoutChangeListener);
      }
      this.mRecylerView = paramRecyclerView;
    } while (this.mRecylerView == null);
    this.mRecylerView.getLayoutManager();
    if (RecyclerView.LayoutManager.getProperties(this.mRecylerView.getContext(), null, 0, 0).orientation == 1) {}
    for (;;)
    {
      this.mIsVertical = bool;
      this.mRecylerView.addOnScrollListener(this.mOnScrollListener);
      this.mRecylerView.addOnLayoutChangeListener(this.mOnLayoutChangeListener);
      return;
      bool = false;
    }
  }
  
  public void updateValues()
  {
    Iterator localIterator = getProperties().iterator();
    while (localIterator.hasNext()) {
      ((ChildPositionProperty)localIterator.next()).updateValue(this);
    }
    super.updateValues();
  }
  
  public static final class ChildPositionProperty
    extends Parallax.IntProperty
  {
    int mAdapterPosition;
    float mFraction;
    int mOffset;
    int mViewId;
    
    ChildPositionProperty(String paramString, int paramInt)
    {
      super(paramInt);
    }
    
    public ChildPositionProperty adapterPosition(int paramInt)
    {
      this.mAdapterPosition = paramInt;
      return this;
    }
    
    public ChildPositionProperty fraction(float paramFloat)
    {
      this.mFraction = paramFloat;
      return this;
    }
    
    public int getAdapterPosition()
    {
      return this.mAdapterPosition;
    }
    
    public float getFraction()
    {
      return this.mFraction;
    }
    
    public int getOffset()
    {
      return this.mOffset;
    }
    
    public int getViewId()
    {
      return this.mViewId;
    }
    
    public ChildPositionProperty offset(int paramInt)
    {
      this.mOffset = paramInt;
      return this;
    }
    
    void updateValue(RecyclerViewParallax paramRecyclerViewParallax)
    {
      RecyclerView localRecyclerView = paramRecyclerViewParallax.mRecylerView;
      Object localObject;
      if (localRecyclerView == null)
      {
        localObject = null;
        if (localObject != null) {
          break label106;
        }
        if ((localRecyclerView != null) && (localRecyclerView.getLayoutManager().getChildCount() != 0)) {
          break label60;
        }
        paramRecyclerViewParallax.setIntPropertyValue(getIndex(), Integer.MAX_VALUE);
      }
      label60:
      label106:
      do
      {
        return;
        localObject = localRecyclerView.findViewHolderForAdapterPosition(this.mAdapterPosition);
        break;
        if (localRecyclerView.findContainingViewHolder(localRecyclerView.getLayoutManager().getChildAt(0)).getAdapterPosition() < this.mAdapterPosition)
        {
          paramRecyclerViewParallax.setIntPropertyValue(getIndex(), Integer.MAX_VALUE);
          return;
        }
        paramRecyclerViewParallax.setIntPropertyValue(getIndex(), Integer.MIN_VALUE);
        return;
        localObject = ((RecyclerView.ViewHolder)localObject).itemView.findViewById(this.mViewId);
      } while (localObject == null);
      Rect localRect = new Rect(0, 0, ((View)localObject).getWidth(), ((View)localObject).getHeight());
      localRecyclerView.offsetDescendantRectToMyCoords((View)localObject, localRect);
      float f3 = 0.0F;
      float f2;
      for (float f1 = 0.0F; (localObject != localRecyclerView) && (localObject != null); f1 = f2)
      {
        float f4;
        if (((View)localObject).getParent() == localRecyclerView)
        {
          f4 = f3;
          f2 = f1;
          if (localRecyclerView.isAnimating()) {}
        }
        else
        {
          f4 = f3 + ((View)localObject).getTranslationX();
          f2 = f1 + ((View)localObject).getTranslationY();
        }
        localObject = (View)((View)localObject).getParent();
        f3 = f4;
      }
      localRect.offset((int)f3, (int)f1);
      if (paramRecyclerViewParallax.mIsVertical)
      {
        paramRecyclerViewParallax.setIntPropertyValue(getIndex(), localRect.top + this.mOffset + (int)(this.mFraction * localRect.height()));
        return;
      }
      paramRecyclerViewParallax.setIntPropertyValue(getIndex(), localRect.left + this.mOffset + (int)(this.mFraction * localRect.width()));
    }
    
    public ChildPositionProperty viewId(int paramInt)
    {
      this.mViewId = paramInt;
      return this;
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/RecyclerViewParallax.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */