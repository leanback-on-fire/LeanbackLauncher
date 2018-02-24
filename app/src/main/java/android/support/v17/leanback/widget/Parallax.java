package android.support.v17.leanback.widget;

import android.support.annotation.CallSuper;
import android.util.Property;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Parallax<PropertyT extends Property>
{
  private final List<ParallaxEffect> mEffects = new ArrayList(4);
  private float[] mFloatValues = new float[4];
  final List<PropertyT> mProperties = new ArrayList();
  final List<PropertyT> mPropertiesReadOnly = Collections.unmodifiableList(this.mProperties);
  private int[] mValues = new int[4];
  
  public ParallaxEffect addEffect(PropertyMarkerValue... paramVarArgs)
  {
    if ((paramVarArgs[0].getProperty() instanceof IntProperty)) {}
    for (Object localObject = new ParallaxEffect.IntEffect();; localObject = new ParallaxEffect.FloatEffect())
    {
      ((ParallaxEffect)localObject).setPropertyRanges(paramVarArgs);
      this.mEffects.add(localObject);
      return (ParallaxEffect)localObject;
    }
  }
  
  public final PropertyT addProperty(String paramString)
  {
    int j = this.mProperties.size();
    paramString = createProperty(paramString, j);
    int k;
    Object localObject;
    int i;
    if ((paramString instanceof IntProperty))
    {
      k = this.mValues.length;
      if (k == j)
      {
        localObject = new int[k * 2];
        i = 0;
        while (i < k)
        {
          localObject[i] = this.mValues[i];
          i += 1;
        }
        this.mValues = ((int[])localObject);
      }
      this.mValues[j] = Integer.MAX_VALUE;
    }
    for (;;)
    {
      this.mProperties.add(paramString);
      return paramString;
      if (!(paramString instanceof FloatProperty)) {
        break;
      }
      k = this.mFloatValues.length;
      if (k == j)
      {
        localObject = new float[k * 2];
        i = 0;
        while (i < k)
        {
          localObject[i] = this.mFloatValues[i];
          i += 1;
        }
        this.mFloatValues = ((float[])localObject);
      }
      this.mFloatValues[j] = Float.MAX_VALUE;
    }
    throw new IllegalArgumentException("Invalid Property type");
  }
  
  public abstract PropertyT createProperty(String paramString, int paramInt);
  
  public List<ParallaxEffect> getEffects()
  {
    return this.mEffects;
  }
  
  final float getFloatPropertyValue(int paramInt)
  {
    return this.mFloatValues[paramInt];
  }
  
  final int getIntPropertyValue(int paramInt)
  {
    return this.mValues[paramInt];
  }
  
  public abstract float getMaxValue();
  
  public final List<PropertyT> getProperties()
  {
    return this.mPropertiesReadOnly;
  }
  
  public void removeAllEffects()
  {
    this.mEffects.clear();
  }
  
  public void removeEffect(ParallaxEffect paramParallaxEffect)
  {
    this.mEffects.remove(paramParallaxEffect);
  }
  
  final void setFloatPropertyValue(int paramInt, float paramFloat)
  {
    if (paramInt >= this.mProperties.size()) {
      throw new ArrayIndexOutOfBoundsException();
    }
    this.mFloatValues[paramInt] = paramFloat;
  }
  
  final void setIntPropertyValue(int paramInt1, int paramInt2)
  {
    if (paramInt1 >= this.mProperties.size()) {
      throw new ArrayIndexOutOfBoundsException();
    }
    this.mValues[paramInt1] = paramInt2;
  }
  
  @CallSuper
  public void updateValues()
  {
    int i = 0;
    while (i < this.mEffects.size())
    {
      ((ParallaxEffect)this.mEffects.get(i)).performMapping(this);
      i += 1;
    }
  }
  
  final void verifyFloatProperties()
    throws IllegalStateException
  {
    if (this.mProperties.size() < 2) {}
    for (;;)
    {
      return;
      float f1 = getFloatPropertyValue(0);
      int i = 1;
      while (i < this.mProperties.size())
      {
        float f2 = getFloatPropertyValue(i);
        if (f2 < f1) {
          throw new IllegalStateException(String.format("Parallax Property[%d]\"%s\" is smaller than Property[%d]\"%s\"", new Object[] { Integer.valueOf(i), ((Property)this.mProperties.get(i)).getName(), Integer.valueOf(i - 1), ((Property)this.mProperties.get(i - 1)).getName() }));
        }
        if ((f1 == -3.4028235E38F) && (f2 == Float.MAX_VALUE)) {
          throw new IllegalStateException(String.format("Parallax Property[%d]\"%s\" is UNKNOWN_BEFORE and Property[%d]\"%s\" is UNKNOWN_AFTER", new Object[] { Integer.valueOf(i - 1), ((Property)this.mProperties.get(i - 1)).getName(), Integer.valueOf(i), ((Property)this.mProperties.get(i)).getName() }));
        }
        f1 = f2;
        i += 1;
      }
    }
  }
  
  void verifyIntProperties()
    throws IllegalStateException
  {
    if (this.mProperties.size() < 2) {}
    for (;;)
    {
      return;
      int j = getIntPropertyValue(0);
      int i = 1;
      while (i < this.mProperties.size())
      {
        int k = getIntPropertyValue(i);
        if (k < j) {
          throw new IllegalStateException(String.format("Parallax Property[%d]\"%s\" is smaller than Property[%d]\"%s\"", new Object[] { Integer.valueOf(i), ((Property)this.mProperties.get(i)).getName(), Integer.valueOf(i - 1), ((Property)this.mProperties.get(i - 1)).getName() }));
        }
        if ((j == Integer.MIN_VALUE) && (k == Integer.MAX_VALUE)) {
          throw new IllegalStateException(String.format("Parallax Property[%d]\"%s\" is UNKNOWN_BEFORE and Property[%d]\"%s\" is UNKNOWN_AFTER", new Object[] { Integer.valueOf(i - 1), ((Property)this.mProperties.get(i - 1)).getName(), Integer.valueOf(i), ((Property)this.mProperties.get(i)).getName() }));
        }
        j = k;
        i += 1;
      }
    }
  }
  
  public static class FloatProperty
    extends Property<Parallax, Float>
  {
    public static final float UNKNOWN_AFTER = Float.MAX_VALUE;
    public static final float UNKNOWN_BEFORE = -3.4028235E38F;
    private final int mIndex;
    
    public FloatProperty(String paramString, int paramInt)
    {
      super(paramString);
      this.mIndex = paramInt;
    }
    
    public final Parallax.PropertyMarkerValue at(float paramFloat1, float paramFloat2)
    {
      return new Parallax.FloatPropertyMarkerValue(this, paramFloat1, paramFloat2);
    }
    
    public final Parallax.PropertyMarkerValue atAbsolute(float paramFloat)
    {
      return new Parallax.FloatPropertyMarkerValue(this, paramFloat, 0.0F);
    }
    
    public final Parallax.PropertyMarkerValue atFraction(float paramFloat)
    {
      return new Parallax.FloatPropertyMarkerValue(this, 0.0F, paramFloat);
    }
    
    public final Parallax.PropertyMarkerValue atMax()
    {
      return new Parallax.FloatPropertyMarkerValue(this, 0.0F, 1.0F);
    }
    
    public final Parallax.PropertyMarkerValue atMin()
    {
      return new Parallax.FloatPropertyMarkerValue(this, 0.0F);
    }
    
    public final Float get(Parallax paramParallax)
    {
      return Float.valueOf(paramParallax.getFloatPropertyValue(this.mIndex));
    }
    
    public final int getIndex()
    {
      return this.mIndex;
    }
    
    public final float getValue(Parallax paramParallax)
    {
      return paramParallax.getFloatPropertyValue(this.mIndex);
    }
    
    public final void set(Parallax paramParallax, Float paramFloat)
    {
      paramParallax.setFloatPropertyValue(this.mIndex, paramFloat.floatValue());
    }
    
    public final void setValue(Parallax paramParallax, float paramFloat)
    {
      paramParallax.setFloatPropertyValue(this.mIndex, paramFloat);
    }
  }
  
  static class FloatPropertyMarkerValue
    extends Parallax.PropertyMarkerValue<Parallax.FloatProperty>
  {
    private final float mFactionOfMax;
    private final float mValue;
    
    FloatPropertyMarkerValue(Parallax.FloatProperty paramFloatProperty, float paramFloat)
    {
      this(paramFloatProperty, paramFloat, 0.0F);
    }
    
    FloatPropertyMarkerValue(Parallax.FloatProperty paramFloatProperty, float paramFloat1, float paramFloat2)
    {
      super();
      this.mValue = paramFloat1;
      this.mFactionOfMax = paramFloat2;
    }
    
    final float getMarkerValue(Parallax paramParallax)
    {
      if (this.mFactionOfMax == 0.0F) {
        return this.mValue;
      }
      return this.mValue + paramParallax.getMaxValue() * this.mFactionOfMax;
    }
  }
  
  public static class IntProperty
    extends Property<Parallax, Integer>
  {
    public static final int UNKNOWN_AFTER = Integer.MAX_VALUE;
    public static final int UNKNOWN_BEFORE = Integer.MIN_VALUE;
    private final int mIndex;
    
    public IntProperty(String paramString, int paramInt)
    {
      super(paramString);
      this.mIndex = paramInt;
    }
    
    public final Parallax.PropertyMarkerValue at(int paramInt, float paramFloat)
    {
      return new Parallax.IntPropertyMarkerValue(this, paramInt, paramFloat);
    }
    
    public final Parallax.PropertyMarkerValue atAbsolute(int paramInt)
    {
      return new Parallax.IntPropertyMarkerValue(this, paramInt, 0.0F);
    }
    
    public final Parallax.PropertyMarkerValue atFraction(float paramFloat)
    {
      return new Parallax.IntPropertyMarkerValue(this, 0, paramFloat);
    }
    
    public final Parallax.PropertyMarkerValue atMax()
    {
      return new Parallax.IntPropertyMarkerValue(this, 0, 1.0F);
    }
    
    public final Parallax.PropertyMarkerValue atMin()
    {
      return new Parallax.IntPropertyMarkerValue(this, 0);
    }
    
    public final Integer get(Parallax paramParallax)
    {
      return Integer.valueOf(paramParallax.getIntPropertyValue(this.mIndex));
    }
    
    public final int getIndex()
    {
      return this.mIndex;
    }
    
    public final int getValue(Parallax paramParallax)
    {
      return paramParallax.getIntPropertyValue(this.mIndex);
    }
    
    public final void set(Parallax paramParallax, Integer paramInteger)
    {
      paramParallax.setIntPropertyValue(this.mIndex, paramInteger.intValue());
    }
    
    public final void setValue(Parallax paramParallax, int paramInt)
    {
      paramParallax.setIntPropertyValue(this.mIndex, paramInt);
    }
  }
  
  static class IntPropertyMarkerValue
    extends Parallax.PropertyMarkerValue<Parallax.IntProperty>
  {
    private final float mFactionOfMax;
    private final int mValue;
    
    IntPropertyMarkerValue(Parallax.IntProperty paramIntProperty, int paramInt)
    {
      this(paramIntProperty, paramInt, 0.0F);
    }
    
    IntPropertyMarkerValue(Parallax.IntProperty paramIntProperty, int paramInt, float paramFloat)
    {
      super();
      this.mValue = paramInt;
      this.mFactionOfMax = paramFloat;
    }
    
    final int getMarkerValue(Parallax paramParallax)
    {
      if (this.mFactionOfMax == 0.0F) {
        return this.mValue;
      }
      return this.mValue + Math.round(paramParallax.getMaxValue() * this.mFactionOfMax);
    }
  }
  
  public static class PropertyMarkerValue<PropertyT>
  {
    private final PropertyT mProperty;
    
    public PropertyMarkerValue(PropertyT paramPropertyT)
    {
      this.mProperty = paramPropertyT;
    }
    
    public PropertyT getProperty()
    {
      return (PropertyT)this.mProperty;
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/Parallax.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */