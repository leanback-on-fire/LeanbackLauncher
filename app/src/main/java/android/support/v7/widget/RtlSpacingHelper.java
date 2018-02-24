package android.support.v7.widget;

class RtlSpacingHelper
{
  public static final int UNDEFINED = Integer.MIN_VALUE;
  private int mEnd = Integer.MIN_VALUE;
  private int mExplicitLeft = 0;
  private int mExplicitRight = 0;
  private boolean mIsRelative = false;
  private boolean mIsRtl = false;
  private int mLeft = 0;
  private int mRight = 0;
  private int mStart = Integer.MIN_VALUE;
  
  public int getEnd()
  {
    if (this.mIsRtl) {
      return this.mLeft;
    }
    return this.mRight;
  }
  
  public int getLeft()
  {
    return this.mLeft;
  }
  
  public int getRight()
  {
    return this.mRight;
  }
  
  public int getStart()
  {
    if (this.mIsRtl) {
      return this.mRight;
    }
    return this.mLeft;
  }
  
  public void setAbsolute(int paramInt1, int paramInt2)
  {
    this.mIsRelative = false;
    if (paramInt1 != Integer.MIN_VALUE)
    {
      this.mExplicitLeft = paramInt1;
      this.mLeft = paramInt1;
    }
    if (paramInt2 != Integer.MIN_VALUE)
    {
      this.mExplicitRight = paramInt2;
      this.mRight = paramInt2;
    }
  }
  
  public void setDirection(boolean paramBoolean)
  {
    if (paramBoolean == this.mIsRtl) {
      return;
    }
    this.mIsRtl = paramBoolean;
    if (this.mIsRelative)
    {
      if (paramBoolean)
      {
        if (this.mEnd != Integer.MIN_VALUE)
        {
          i = this.mEnd;
          this.mLeft = i;
          if (this.mStart == Integer.MIN_VALUE) {
            break label72;
          }
        }
        label72:
        for (i = this.mStart;; i = this.mExplicitRight)
        {
          this.mRight = i;
          return;
          i = this.mExplicitLeft;
          break;
        }
      }
      if (this.mStart != Integer.MIN_VALUE)
      {
        i = this.mStart;
        this.mLeft = i;
        if (this.mEnd == Integer.MIN_VALUE) {
          break label127;
        }
      }
      label127:
      for (int i = this.mEnd;; i = this.mExplicitRight)
      {
        this.mRight = i;
        return;
        i = this.mExplicitLeft;
        break;
      }
    }
    this.mLeft = this.mExplicitLeft;
    this.mRight = this.mExplicitRight;
  }
  
  public void setRelative(int paramInt1, int paramInt2)
  {
    this.mStart = paramInt1;
    this.mEnd = paramInt2;
    this.mIsRelative = true;
    if (this.mIsRtl)
    {
      if (paramInt2 != Integer.MIN_VALUE) {
        this.mLeft = paramInt2;
      }
      if (paramInt1 != Integer.MIN_VALUE) {
        this.mRight = paramInt1;
      }
    }
    do
    {
      return;
      if (paramInt1 != Integer.MIN_VALUE) {
        this.mLeft = paramInt1;
      }
    } while (paramInt2 == Integer.MIN_VALUE);
    this.mRight = paramInt2;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v7/widget/RtlSpacingHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */