package android.support.v17.leanback.widget;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.util.Property;
import android.view.animation.LinearInterpolator;

public abstract class ParallaxTarget
{
  public void directUpdate(Number paramNumber) {}
  
  public boolean isDirectMapping()
  {
    return false;
  }
  
  public void update(float paramFloat) {}
  
  public static final class DirectPropertyTarget<T, V extends Number>
    extends ParallaxTarget
  {
    Object mObject;
    Property<T, V> mProperty;
    
    public DirectPropertyTarget(Object paramObject, Property<T, V> paramProperty)
    {
      this.mObject = paramObject;
      this.mProperty = paramProperty;
    }
    
    public void directUpdate(Number paramNumber)
    {
      this.mProperty.set(this.mObject, paramNumber);
    }
    
    public boolean isDirectMapping()
    {
      return true;
    }
  }
  
  public static final class PropertyValuesHolderTarget
    extends ParallaxTarget
  {
    private static final long PSEUDO_DURATION = 1000000L;
    private final ObjectAnimator mAnimator;
    private float mFraction;
    
    public PropertyValuesHolderTarget(Object paramObject, PropertyValuesHolder paramPropertyValuesHolder)
    {
      this.mAnimator = ObjectAnimator.ofPropertyValuesHolder(paramObject, new PropertyValuesHolder[] { paramPropertyValuesHolder });
      this.mAnimator.setInterpolator(new LinearInterpolator());
      this.mAnimator.setDuration(1000000L);
    }
    
    public void update(float paramFloat)
    {
      this.mFraction = paramFloat;
      this.mAnimator.setCurrentPlayTime((1000000.0F * paramFloat));
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/ParallaxTarget.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */