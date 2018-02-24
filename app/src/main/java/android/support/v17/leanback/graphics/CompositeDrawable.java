package android.support.v17.leanback.graphics;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.Callback;
import android.graphics.drawable.Drawable.ConstantState;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.Property;
import java.util.ArrayList;

public class CompositeDrawable
  extends Drawable
  implements Drawable.Callback
{
  boolean mMutated = false;
  CompositeState mState;
  
  public CompositeDrawable()
  {
    this.mState = new CompositeState();
  }
  
  CompositeDrawable(CompositeState paramCompositeState)
  {
    this.mState = paramCompositeState;
  }
  
  public void addChildDrawable(Drawable paramDrawable)
  {
    this.mState.mChildren.add(new ChildDrawable(paramDrawable, this));
  }
  
  public void draw(Canvas paramCanvas)
  {
    ArrayList localArrayList = this.mState.mChildren;
    int i = 0;
    while (i < localArrayList.size())
    {
      ((ChildDrawable)localArrayList.get(i)).mDrawable.draw(paramCanvas);
      i += 1;
    }
  }
  
  public int getAlpha()
  {
    Drawable localDrawable = getFirstNonNullDrawable();
    if (localDrawable != null) {
      return DrawableCompat.getAlpha(localDrawable);
    }
    return 255;
  }
  
  public ChildDrawable getChildAt(int paramInt)
  {
    return (ChildDrawable)this.mState.mChildren.get(paramInt);
  }
  
  public int getChildCount()
  {
    return this.mState.mChildren.size();
  }
  
  public Drawable.ConstantState getConstantState()
  {
    return this.mState;
  }
  
  public Drawable getDrawable(int paramInt)
  {
    return ((ChildDrawable)this.mState.mChildren.get(paramInt)).mDrawable;
  }
  
  final Drawable getFirstNonNullDrawable()
  {
    ArrayList localArrayList = this.mState.mChildren;
    int i = 0;
    int j = localArrayList.size();
    while (i < j)
    {
      Drawable localDrawable = ((ChildDrawable)localArrayList.get(i)).mDrawable;
      if (localDrawable != null) {
        return localDrawable;
      }
      i += 1;
    }
    return null;
  }
  
  public int getOpacity()
  {
    return 0;
  }
  
  public void invalidateDrawable(Drawable paramDrawable)
  {
    invalidateSelf();
  }
  
  public Drawable mutate()
  {
    if ((!this.mMutated) && (super.mutate() == this))
    {
      this.mState = new CompositeState(this.mState, this, null);
      ArrayList localArrayList = this.mState.mChildren;
      int i = 0;
      int j = localArrayList.size();
      while (i < j)
      {
        Drawable localDrawable = ((ChildDrawable)localArrayList.get(i)).mDrawable;
        if (localDrawable != null) {
          localDrawable.mutate();
        }
        i += 1;
      }
      this.mMutated = true;
    }
    return this;
  }
  
  protected void onBoundsChange(Rect paramRect)
  {
    super.onBoundsChange(paramRect);
    updateBounds(paramRect);
  }
  
  public void removeChild(int paramInt)
  {
    this.mState.mChildren.remove(paramInt);
  }
  
  public void removeDrawable(Drawable paramDrawable)
  {
    ArrayList localArrayList = this.mState.mChildren;
    int i = 0;
    for (;;)
    {
      if (i < localArrayList.size())
      {
        if (paramDrawable == ((ChildDrawable)localArrayList.get(i)).mDrawable)
        {
          ((ChildDrawable)localArrayList.get(i)).mDrawable.setCallback(null);
          localArrayList.remove(i);
        }
      }
      else {
        return;
      }
      i += 1;
    }
  }
  
  public void scheduleDrawable(Drawable paramDrawable, Runnable paramRunnable, long paramLong)
  {
    scheduleSelf(paramRunnable, paramLong);
  }
  
  public void setAlpha(int paramInt)
  {
    ArrayList localArrayList = this.mState.mChildren;
    int i = 0;
    while (i < localArrayList.size())
    {
      ((ChildDrawable)localArrayList.get(i)).mDrawable.setAlpha(paramInt);
      i += 1;
    }
  }
  
  public void setChildDrawableAt(int paramInt, Drawable paramDrawable)
  {
    this.mState.mChildren.set(paramInt, new ChildDrawable(paramDrawable, this));
  }
  
  public void setColorFilter(ColorFilter paramColorFilter)
  {
    ArrayList localArrayList = this.mState.mChildren;
    int i = 0;
    while (i < localArrayList.size())
    {
      ((ChildDrawable)localArrayList.get(i)).mDrawable.setColorFilter(paramColorFilter);
      i += 1;
    }
  }
  
  public void unscheduleDrawable(Drawable paramDrawable, Runnable paramRunnable)
  {
    unscheduleSelf(paramRunnable);
  }
  
  void updateBounds(Rect paramRect)
  {
    ArrayList localArrayList = this.mState.mChildren;
    int i = 0;
    while (i < localArrayList.size())
    {
      ((ChildDrawable)localArrayList.get(i)).updateBounds(paramRect);
      i += 1;
    }
  }
  
  public static final class ChildDrawable
  {
    public static final Property<ChildDrawable, Integer> BOTTOM_ABSOLUTE;
    public static final Property<ChildDrawable, Float> BOTTOM_FRACTION = new Property(Float.class, "fractionBottom")
    {
      public Float get(CompositeDrawable.ChildDrawable paramAnonymousChildDrawable)
      {
        if (paramAnonymousChildDrawable.getBoundsRule().bottom == null) {
          return Float.valueOf(1.0F);
        }
        return Float.valueOf(paramAnonymousChildDrawable.getBoundsRule().bottom.getFraction());
      }
      
      public void set(CompositeDrawable.ChildDrawable paramAnonymousChildDrawable, Float paramAnonymousFloat)
      {
        if (paramAnonymousChildDrawable.getBoundsRule().bottom == null) {
          paramAnonymousChildDrawable.getBoundsRule().bottom = BoundsRule.ValueRule.inheritFromParent(paramAnonymousFloat.floatValue());
        }
        for (;;)
        {
          paramAnonymousChildDrawable.recomputeBounds();
          return;
          paramAnonymousChildDrawable.getBoundsRule().bottom.setFraction(paramAnonymousFloat.floatValue());
        }
      }
    };
    public static final Property<ChildDrawable, Integer> LEFT_ABSOLUTE;
    public static final Property<ChildDrawable, Float> LEFT_FRACTION = new Property(Float.class, "fractionLeft")
    {
      public Float get(CompositeDrawable.ChildDrawable paramAnonymousChildDrawable)
      {
        if (paramAnonymousChildDrawable.getBoundsRule().left == null) {
          return Float.valueOf(0.0F);
        }
        return Float.valueOf(paramAnonymousChildDrawable.getBoundsRule().left.getFraction());
      }
      
      public void set(CompositeDrawable.ChildDrawable paramAnonymousChildDrawable, Float paramAnonymousFloat)
      {
        if (paramAnonymousChildDrawable.getBoundsRule().left == null) {
          paramAnonymousChildDrawable.getBoundsRule().left = BoundsRule.ValueRule.inheritFromParent(paramAnonymousFloat.floatValue());
        }
        for (;;)
        {
          paramAnonymousChildDrawable.recomputeBounds();
          return;
          paramAnonymousChildDrawable.getBoundsRule().left.setFraction(paramAnonymousFloat.floatValue());
        }
      }
    };
    public static final Property<ChildDrawable, Integer> RIGHT_ABSOLUTE;
    public static final Property<ChildDrawable, Float> RIGHT_FRACTION = new Property(Float.class, "fractionRight")
    {
      public Float get(CompositeDrawable.ChildDrawable paramAnonymousChildDrawable)
      {
        if (paramAnonymousChildDrawable.getBoundsRule().right == null) {
          return Float.valueOf(1.0F);
        }
        return Float.valueOf(paramAnonymousChildDrawable.getBoundsRule().right.getFraction());
      }
      
      public void set(CompositeDrawable.ChildDrawable paramAnonymousChildDrawable, Float paramAnonymousFloat)
      {
        if (paramAnonymousChildDrawable.getBoundsRule().right == null) {
          paramAnonymousChildDrawable.getBoundsRule().right = BoundsRule.ValueRule.inheritFromParent(paramAnonymousFloat.floatValue());
        }
        for (;;)
        {
          paramAnonymousChildDrawable.recomputeBounds();
          return;
          paramAnonymousChildDrawable.getBoundsRule().right.setFraction(paramAnonymousFloat.floatValue());
        }
      }
    };
    public static final Property<ChildDrawable, Integer> TOP_ABSOLUTE = new Property(Integer.class, "absoluteTop")
    {
      public Integer get(CompositeDrawable.ChildDrawable paramAnonymousChildDrawable)
      {
        if (paramAnonymousChildDrawable.getBoundsRule().top == null) {
          return Integer.valueOf(paramAnonymousChildDrawable.mParent.getBounds().top);
        }
        return Integer.valueOf(paramAnonymousChildDrawable.getBoundsRule().top.getAbsoluteValue());
      }
      
      public void set(CompositeDrawable.ChildDrawable paramAnonymousChildDrawable, Integer paramAnonymousInteger)
      {
        if (paramAnonymousChildDrawable.getBoundsRule().top == null) {
          paramAnonymousChildDrawable.getBoundsRule().top = BoundsRule.ValueRule.absoluteValue(paramAnonymousInteger.intValue());
        }
        for (;;)
        {
          paramAnonymousChildDrawable.recomputeBounds();
          return;
          paramAnonymousChildDrawable.getBoundsRule().top.setAbsoluteValue(paramAnonymousInteger.intValue());
        }
      }
    };
    public static final Property<ChildDrawable, Float> TOP_FRACTION;
    private final Rect adjustedBounds = new Rect();
    private final BoundsRule mBoundsRule;
    private final Drawable mDrawable;
    final CompositeDrawable mParent;
    
    static
    {
      BOTTOM_ABSOLUTE = new Property(Integer.class, "absoluteBottom")
      {
        public Integer get(CompositeDrawable.ChildDrawable paramAnonymousChildDrawable)
        {
          if (paramAnonymousChildDrawable.getBoundsRule().bottom == null) {
            return Integer.valueOf(paramAnonymousChildDrawable.mParent.getBounds().bottom);
          }
          return Integer.valueOf(paramAnonymousChildDrawable.getBoundsRule().bottom.getAbsoluteValue());
        }
        
        public void set(CompositeDrawable.ChildDrawable paramAnonymousChildDrawable, Integer paramAnonymousInteger)
        {
          if (paramAnonymousChildDrawable.getBoundsRule().bottom == null) {
            paramAnonymousChildDrawable.getBoundsRule().bottom = BoundsRule.ValueRule.absoluteValue(paramAnonymousInteger.intValue());
          }
          for (;;)
          {
            paramAnonymousChildDrawable.recomputeBounds();
            return;
            paramAnonymousChildDrawable.getBoundsRule().bottom.setAbsoluteValue(paramAnonymousInteger.intValue());
          }
        }
      };
      LEFT_ABSOLUTE = new Property(Integer.class, "absoluteLeft")
      {
        public Integer get(CompositeDrawable.ChildDrawable paramAnonymousChildDrawable)
        {
          if (paramAnonymousChildDrawable.getBoundsRule().left == null) {
            return Integer.valueOf(paramAnonymousChildDrawable.mParent.getBounds().left);
          }
          return Integer.valueOf(paramAnonymousChildDrawable.getBoundsRule().left.getAbsoluteValue());
        }
        
        public void set(CompositeDrawable.ChildDrawable paramAnonymousChildDrawable, Integer paramAnonymousInteger)
        {
          if (paramAnonymousChildDrawable.getBoundsRule().left == null) {
            paramAnonymousChildDrawable.getBoundsRule().left = BoundsRule.ValueRule.absoluteValue(paramAnonymousInteger.intValue());
          }
          for (;;)
          {
            paramAnonymousChildDrawable.recomputeBounds();
            return;
            paramAnonymousChildDrawable.getBoundsRule().left.setAbsoluteValue(paramAnonymousInteger.intValue());
          }
        }
      };
      RIGHT_ABSOLUTE = new Property(Integer.class, "absoluteRight")
      {
        public Integer get(CompositeDrawable.ChildDrawable paramAnonymousChildDrawable)
        {
          if (paramAnonymousChildDrawable.getBoundsRule().right == null) {
            return Integer.valueOf(paramAnonymousChildDrawable.mParent.getBounds().right);
          }
          return Integer.valueOf(paramAnonymousChildDrawable.getBoundsRule().right.getAbsoluteValue());
        }
        
        public void set(CompositeDrawable.ChildDrawable paramAnonymousChildDrawable, Integer paramAnonymousInteger)
        {
          if (paramAnonymousChildDrawable.getBoundsRule().right == null) {
            paramAnonymousChildDrawable.getBoundsRule().right = BoundsRule.ValueRule.absoluteValue(paramAnonymousInteger.intValue());
          }
          for (;;)
          {
            paramAnonymousChildDrawable.recomputeBounds();
            return;
            paramAnonymousChildDrawable.getBoundsRule().right.setAbsoluteValue(paramAnonymousInteger.intValue());
          }
        }
      };
      TOP_FRACTION = new Property(Float.class, "fractionTop")
      {
        public Float get(CompositeDrawable.ChildDrawable paramAnonymousChildDrawable)
        {
          if (paramAnonymousChildDrawable.getBoundsRule().top == null) {
            return Float.valueOf(0.0F);
          }
          return Float.valueOf(paramAnonymousChildDrawable.getBoundsRule().top.getFraction());
        }
        
        public void set(CompositeDrawable.ChildDrawable paramAnonymousChildDrawable, Float paramAnonymousFloat)
        {
          if (paramAnonymousChildDrawable.getBoundsRule().top == null) {
            paramAnonymousChildDrawable.getBoundsRule().top = BoundsRule.ValueRule.inheritFromParent(paramAnonymousFloat.floatValue());
          }
          for (;;)
          {
            paramAnonymousChildDrawable.recomputeBounds();
            return;
            paramAnonymousChildDrawable.getBoundsRule().top.setFraction(paramAnonymousFloat.floatValue());
          }
        }
      };
    }
    
    public ChildDrawable(Drawable paramDrawable, CompositeDrawable paramCompositeDrawable)
    {
      this.mDrawable = paramDrawable;
      this.mParent = paramCompositeDrawable;
      this.mBoundsRule = new BoundsRule();
      paramDrawable.setCallback(paramCompositeDrawable);
    }
    
    ChildDrawable(ChildDrawable paramChildDrawable, CompositeDrawable paramCompositeDrawable, Resources paramResources)
    {
      Drawable localDrawable = paramChildDrawable.mDrawable;
      Drawable.ConstantState localConstantState;
      if (localDrawable != null)
      {
        localConstantState = localDrawable.getConstantState();
        if (paramResources != null)
        {
          paramResources = localConstantState.newDrawable(paramResources);
          paramResources.setCallback(paramCompositeDrawable);
          DrawableCompat.setLayoutDirection(paramResources, DrawableCompat.getLayoutDirection(localDrawable));
          paramResources.setBounds(localDrawable.getBounds());
          paramResources.setLevel(localDrawable.getLevel());
          label78:
          if (paramChildDrawable.mBoundsRule == null) {
            break label125;
          }
        }
      }
      label125:
      for (this.mBoundsRule = new BoundsRule(paramChildDrawable.mBoundsRule);; this.mBoundsRule = new BoundsRule())
      {
        this.mDrawable = paramResources;
        this.mParent = paramCompositeDrawable;
        return;
        paramResources = localConstantState.newDrawable();
        break;
        paramResources = null;
        break label78;
      }
    }
    
    public BoundsRule getBoundsRule()
    {
      return this.mBoundsRule;
    }
    
    public Drawable getDrawable()
    {
      return this.mDrawable;
    }
    
    public void recomputeBounds()
    {
      updateBounds(this.mParent.getBounds());
    }
    
    void updateBounds(Rect paramRect)
    {
      this.mBoundsRule.calculateBounds(paramRect, this.adjustedBounds);
      this.mDrawable.setBounds(this.adjustedBounds);
    }
  }
  
  static class CompositeState
    extends Drawable.ConstantState
  {
    final ArrayList<CompositeDrawable.ChildDrawable> mChildren;
    
    CompositeState()
    {
      this.mChildren = new ArrayList();
    }
    
    CompositeState(CompositeState paramCompositeState, CompositeDrawable paramCompositeDrawable, Resources paramResources)
    {
      int j = paramCompositeState.mChildren.size();
      this.mChildren = new ArrayList(j);
      int i = 0;
      while (i < j)
      {
        this.mChildren.add(new CompositeDrawable.ChildDrawable((CompositeDrawable.ChildDrawable)paramCompositeState.mChildren.get(i), paramCompositeDrawable, paramResources));
        i += 1;
      }
    }
    
    public int getChangingConfigurations()
    {
      return 0;
    }
    
    @NonNull
    public Drawable newDrawable()
    {
      return new CompositeDrawable(this);
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/graphics/CompositeDrawable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */