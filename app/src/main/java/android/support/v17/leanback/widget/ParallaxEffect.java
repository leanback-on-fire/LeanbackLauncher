package android.support.v17.leanback.widget;

import android.animation.PropertyValuesHolder;
import android.util.Property;
import java.util.ArrayList;
import java.util.List;

public abstract class ParallaxEffect
{
  final List<Parallax.PropertyMarkerValue> mMarkerValues = new ArrayList(2);
  final List<ParallaxTarget> mTargets = new ArrayList(4);
  final List<Float> mTotalWeights = new ArrayList(2);
  final List<Float> mWeights = new ArrayList(2);
  
  public final void addTarget(ParallaxTarget paramParallaxTarget)
  {
    this.mTargets.add(paramParallaxTarget);
  }
  
  abstract Number calculateDirectValue(Parallax paramParallax);
  
  abstract float calculateFraction(Parallax paramParallax);
  
  final float getFractionWithWeightAdjusted(float paramFloat, int paramInt)
  {
    float f1 = paramFloat;
    int i;
    float f2;
    if (this.mMarkerValues.size() >= 3)
    {
      if (this.mWeights.size() != this.mMarkerValues.size() - 1) {
        break label131;
      }
      i = 1;
      if (i == 0) {
        break label137;
      }
      f2 = ((Float)this.mTotalWeights.get(this.mTotalWeights.size() - 1)).floatValue();
      paramFloat = ((Float)this.mWeights.get(paramInt - 1)).floatValue() * paramFloat / f2;
      f1 = paramFloat;
      if (paramInt >= 2) {
        f1 = paramFloat + ((Float)this.mTotalWeights.get(paramInt - 2)).floatValue() / f2;
      }
    }
    label131:
    label137:
    do
    {
      return f1;
      i = 0;
      break;
      f2 = this.mMarkerValues.size() - 1;
      paramFloat /= f2;
      f1 = paramFloat;
    } while (paramInt < 2);
    return paramFloat + (paramInt - 1) / f2;
  }
  
  public final List<Parallax.PropertyMarkerValue> getPropertyRanges()
  {
    return this.mMarkerValues;
  }
  
  public final List<ParallaxTarget> getTargets()
  {
    return this.mTargets;
  }
  
  public final List<Float> getWeights()
  {
    return this.mWeights;
  }
  
  public final void performMapping(Parallax paramParallax)
  {
    if (this.mMarkerValues.size() < 2) {
      return;
    }
    label25:
    int i;
    float f;
    int k;
    label35:
    ParallaxTarget localParallaxTarget;
    if ((this instanceof IntEffect))
    {
      paramParallax.verifyIntProperties();
      i = 0;
      f = 0.0F;
      Object localObject1 = null;
      k = 0;
      if (k < this.mTargets.size())
      {
        localParallaxTarget = (ParallaxTarget)this.mTargets.get(k);
        if (!localParallaxTarget.isDirectMapping()) {
          break label116;
        }
        Object localObject2 = localObject1;
        if (localObject1 == null) {
          localObject2 = calculateDirectValue(paramParallax);
        }
        localParallaxTarget.directUpdate((Number)localObject2);
        localObject1 = localObject2;
      }
    }
    for (;;)
    {
      k += 1;
      break label35;
      break;
      paramParallax.verifyFloatProperties();
      break label25;
      label116:
      int j = i;
      if (i == 0)
      {
        j = 1;
        f = calculateFraction(paramParallax);
      }
      localParallaxTarget.update(f);
      i = j;
    }
  }
  
  public final void removeTarget(ParallaxTarget paramParallaxTarget)
  {
    this.mTargets.remove(paramParallaxTarget);
  }
  
  public final void setPropertyRanges(Parallax.PropertyMarkerValue... paramVarArgs)
  {
    this.mMarkerValues.clear();
    int j = paramVarArgs.length;
    int i = 0;
    while (i < j)
    {
      Parallax.PropertyMarkerValue localPropertyMarkerValue = paramVarArgs[i];
      this.mMarkerValues.add(localPropertyMarkerValue);
      i += 1;
    }
  }
  
  public final void setWeights(float... paramVarArgs)
  {
    int j = 0;
    int k = paramVarArgs.length;
    int i = 0;
    while (i < k)
    {
      if (paramVarArgs[i] <= 0.0F) {
        throw new IllegalArgumentException();
      }
      i += 1;
    }
    this.mWeights.clear();
    this.mTotalWeights.clear();
    float f1 = 0.0F;
    k = paramVarArgs.length;
    i = j;
    while (i < k)
    {
      float f2 = paramVarArgs[i];
      this.mWeights.add(Float.valueOf(f2));
      f1 += f2;
      this.mTotalWeights.add(Float.valueOf(f1));
      i += 1;
    }
  }
  
  public final ParallaxEffect target(ParallaxTarget paramParallaxTarget)
  {
    this.mTargets.add(paramParallaxTarget);
    return this;
  }
  
  public final ParallaxEffect target(Object paramObject, PropertyValuesHolder paramPropertyValuesHolder)
  {
    this.mTargets.add(new ParallaxTarget.PropertyValuesHolderTarget(paramObject, paramPropertyValuesHolder));
    return this;
  }
  
  public final <T, V extends Number> ParallaxEffect target(T paramT, Property<T, V> paramProperty)
  {
    this.mTargets.add(new ParallaxTarget.DirectPropertyTarget(paramT, paramProperty));
    return this;
  }
  
  public final ParallaxEffect weights(float... paramVarArgs)
  {
    setWeights(paramVarArgs);
    return this;
  }
  
  static final class FloatEffect
    extends ParallaxEffect
  {
    Number calculateDirectValue(Parallax paramParallax)
    {
      if (this.mMarkerValues.size() != 2) {
        throw new RuntimeException("Must use two marker values for direct mapping");
      }
      if (((Parallax.PropertyMarkerValue)this.mMarkerValues.get(0)).getProperty() != ((Parallax.PropertyMarkerValue)this.mMarkerValues.get(1)).getProperty()) {
        throw new RuntimeException("Marker value must use same Property for direct mapping");
      }
      float f3 = ((Parallax.FloatPropertyMarkerValue)this.mMarkerValues.get(0)).getMarkerValue(paramParallax);
      float f1 = ((Parallax.FloatPropertyMarkerValue)this.mMarkerValues.get(1)).getMarkerValue(paramParallax);
      float f4 = f3;
      float f2 = f1;
      if (f3 > f1)
      {
        f2 = f3;
        f4 = f1;
      }
      Float localFloat = ((Parallax.FloatProperty)((Parallax.PropertyMarkerValue)this.mMarkerValues.get(0)).getProperty()).get(paramParallax);
      if (localFloat.floatValue() < f4) {
        paramParallax = Float.valueOf(f4);
      }
      do
      {
        return paramParallax;
        paramParallax = localFloat;
      } while (localFloat.floatValue() <= f2);
      return Float.valueOf(f2);
    }
    
    float calculateFraction(Parallax paramParallax)
    {
      int j = 0;
      float f2 = 0.0F;
      float f1 = 0.0F;
      int i = 0;
      while (i < this.mMarkerValues.size())
      {
        Parallax.FloatPropertyMarkerValue localFloatPropertyMarkerValue = (Parallax.FloatPropertyMarkerValue)this.mMarkerValues.get(i);
        int k = ((Parallax.FloatProperty)localFloatPropertyMarkerValue.getProperty()).getIndex();
        float f3 = localFloatPropertyMarkerValue.getMarkerValue(paramParallax);
        float f4 = paramParallax.getFloatPropertyValue(k);
        if (i == 0)
        {
          if (f4 >= f3) {
            return 0.0F;
          }
        }
        else
        {
          if ((j == k) && (f1 < f3)) {
            throw new IllegalStateException("marker value of same variable must be descendant order");
          }
          if (f4 == Float.MAX_VALUE) {
            return getFractionWithWeightAdjusted((f1 - f2) / paramParallax.getMaxValue(), i);
          }
          if (f4 >= f3)
          {
            if (j == k) {
              f1 = (f1 - f4) / (f1 - f3);
            }
            for (;;)
            {
              return getFractionWithWeightAdjusted(f1, i);
              if (f2 != -3.4028235E38F)
              {
                f1 += f4 - f2;
                f1 = (f1 - f4) / (f1 - f3);
              }
              else
              {
                f1 = 1.0F - (f4 - f3) / paramParallax.getMaxValue();
              }
            }
          }
        }
        f2 = f4;
        j = k;
        f1 = f3;
        i += 1;
      }
      return 1.0F;
    }
  }
  
  static final class IntEffect
    extends ParallaxEffect
  {
    Number calculateDirectValue(Parallax paramParallax)
    {
      if (this.mMarkerValues.size() != 2) {
        throw new RuntimeException("Must use two marker values for direct mapping");
      }
      if (((Parallax.PropertyMarkerValue)this.mMarkerValues.get(0)).getProperty() != ((Parallax.PropertyMarkerValue)this.mMarkerValues.get(1)).getProperty()) {
        throw new RuntimeException("Marker value must use same Property for direct mapping");
      }
      int k = ((Parallax.IntPropertyMarkerValue)this.mMarkerValues.get(0)).getMarkerValue(paramParallax);
      int i = ((Parallax.IntPropertyMarkerValue)this.mMarkerValues.get(1)).getMarkerValue(paramParallax);
      int m = k;
      int j = i;
      if (k > i)
      {
        j = k;
        m = i;
      }
      Integer localInteger = ((Parallax.IntProperty)((Parallax.PropertyMarkerValue)this.mMarkerValues.get(0)).getProperty()).get(paramParallax);
      if (localInteger.intValue() < m) {
        paramParallax = Integer.valueOf(m);
      }
      do
      {
        return paramParallax;
        paramParallax = localInteger;
      } while (localInteger.intValue() <= j);
      return Integer.valueOf(j);
    }
    
    float calculateFraction(Parallax paramParallax)
    {
      int j = 0;
      int m = 0;
      int k = 0;
      int i = 0;
      while (i < this.mMarkerValues.size())
      {
        Parallax.IntPropertyMarkerValue localIntPropertyMarkerValue = (Parallax.IntPropertyMarkerValue)this.mMarkerValues.get(i);
        int i1 = ((Parallax.IntProperty)localIntPropertyMarkerValue.getProperty()).getIndex();
        int n = localIntPropertyMarkerValue.getMarkerValue(paramParallax);
        int i2 = paramParallax.getIntPropertyValue(i1);
        if (i == 0)
        {
          if (i2 >= n) {
            return 0.0F;
          }
        }
        else
        {
          if ((j == i1) && (k < n)) {
            throw new IllegalStateException("marker value of same variable must be descendant order");
          }
          if (i2 == Integer.MAX_VALUE) {
            return getFractionWithWeightAdjusted((k - m) / paramParallax.getMaxValue(), i);
          }
          if (i2 >= n)
          {
            float f;
            if (j == i1) {
              f = (k - i2) / (k - n);
            }
            for (;;)
            {
              return getFractionWithWeightAdjusted(f, i);
              if (m != Integer.MIN_VALUE)
              {
                j = k + (i2 - m);
                f = (j - i2) / (j - n);
              }
              else
              {
                f = 1.0F - (i2 - n) / paramParallax.getMaxValue();
              }
            }
          }
        }
        m = i2;
        j = i1;
        k = n;
        i += 1;
      }
      return 1.0F;
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/ParallaxEffect.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */