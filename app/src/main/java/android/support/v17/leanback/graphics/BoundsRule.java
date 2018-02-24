package android.support.v17.leanback.graphics;

import android.graphics.Rect;

public class BoundsRule
{
  public ValueRule bottom;
  public ValueRule left;
  public ValueRule right;
  public ValueRule top;
  
  public BoundsRule() {}
  
  public BoundsRule(BoundsRule paramBoundsRule)
  {
    if (paramBoundsRule.left != null)
    {
      localObject1 = new ValueRule(paramBoundsRule.left);
      this.left = ((ValueRule)localObject1);
      if (paramBoundsRule.right == null) {
        break label110;
      }
      localObject1 = new ValueRule(paramBoundsRule.right);
      label49:
      this.right = ((ValueRule)localObject1);
      if (paramBoundsRule.top == null) {
        break label115;
      }
    }
    label110:
    label115:
    for (Object localObject1 = new ValueRule(paramBoundsRule.top);; localObject1 = null)
    {
      this.top = ((ValueRule)localObject1);
      localObject1 = localObject2;
      if (paramBoundsRule.bottom != null) {
        localObject1 = new ValueRule(paramBoundsRule.bottom);
      }
      this.bottom = ((ValueRule)localObject1);
      return;
      localObject1 = null;
      break;
      localObject1 = null;
      break label49;
    }
  }
  
  private int doCalculate(int paramInt1, ValueRule paramValueRule, int paramInt2)
  {
    return paramValueRule.mAbsoluteValue + paramInt1 + (int)(paramValueRule.mFraction * paramInt2);
  }
  
  public void calculateBounds(Rect paramRect1, Rect paramRect2)
  {
    if (this.left == null)
    {
      paramRect2.left = paramRect1.left;
      if (this.right != null) {
        break label84;
      }
      paramRect2.right = paramRect1.right;
      label30:
      if (this.top != null) {
        break label107;
      }
    }
    label84:
    label107:
    for (paramRect2.top = paramRect1.top;; paramRect2.top = doCalculate(paramRect1.top, this.top, paramRect1.height()))
    {
      if (this.bottom != null) {
        break label130;
      }
      paramRect2.bottom = paramRect1.bottom;
      return;
      paramRect2.left = doCalculate(paramRect1.left, this.left, paramRect1.width());
      break;
      paramRect2.right = doCalculate(paramRect1.left, this.right, paramRect1.width());
      break label30;
    }
    label130:
    paramRect2.bottom = doCalculate(paramRect1.top, this.bottom, paramRect1.height());
  }
  
  public static final class ValueRule
  {
    int mAbsoluteValue;
    float mFraction;
    
    ValueRule(int paramInt, float paramFloat)
    {
      this.mAbsoluteValue = paramInt;
      this.mFraction = paramFloat;
    }
    
    ValueRule(ValueRule paramValueRule)
    {
      this.mFraction = paramValueRule.mFraction;
      this.mAbsoluteValue = paramValueRule.mAbsoluteValue;
    }
    
    public static ValueRule absoluteValue(int paramInt)
    {
      return new ValueRule(paramInt, 0.0F);
    }
    
    public static ValueRule inheritFromParent(float paramFloat)
    {
      return new ValueRule(0, paramFloat);
    }
    
    public static ValueRule inheritFromParentWithOffset(float paramFloat, int paramInt)
    {
      return new ValueRule(paramInt, paramFloat);
    }
    
    public int getAbsoluteValue()
    {
      return this.mAbsoluteValue;
    }
    
    public float getFraction()
    {
      return this.mFraction;
    }
    
    public void setAbsoluteValue(int paramInt)
    {
      this.mAbsoluteValue = paramInt;
    }
    
    public void setFraction(float paramFloat)
    {
      this.mFraction = paramFloat;
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/graphics/BoundsRule.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */