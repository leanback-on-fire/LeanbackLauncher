package android.support.v17.leanback.widget;

class WindowAlignment
{
  public final Axis horizontal = new Axis("horizontal");
  private Axis mMainAxis = this.horizontal;
  private int mOrientation = 0;
  private Axis mSecondAxis = this.vertical;
  public final Axis vertical = new Axis("vertical");
  
  public final int getOrientation()
  {
    return this.mOrientation;
  }
  
  public final Axis mainAxis()
  {
    return this.mMainAxis;
  }
  
  public final void reset()
  {
    mainAxis().reset();
  }
  
  public final Axis secondAxis()
  {
    return this.mSecondAxis;
  }
  
  public final void setOrientation(int paramInt)
  {
    this.mOrientation = paramInt;
    if (this.mOrientation == 0)
    {
      this.mMainAxis = this.horizontal;
      this.mSecondAxis = this.vertical;
      return;
    }
    this.mMainAxis = this.vertical;
    this.mSecondAxis = this.horizontal;
  }
  
  public String toString()
  {
    return "horizontal=" + this.horizontal.toString() + "; vertical=" + this.vertical.toString();
  }
  
  public static class Axis
  {
    static final int PF_KEYLINE_OVER_HIGH_EDGE = 2;
    static final int PF_KEYLINE_OVER_LOW_EDGE = 1;
    private int mMaxEdge;
    private int mMaxScroll;
    private int mMinEdge;
    private int mMinScroll;
    private String mName;
    private int mPaddingMax;
    private int mPaddingMin;
    private int mPreferredKeyLine = 2;
    private boolean mReversedFlow;
    private int mSize;
    private int mWindowAlignment = 3;
    private int mWindowAlignmentOffset = 0;
    private float mWindowAlignmentOffsetPercent = 50.0F;
    
    public Axis(String paramString)
    {
      reset();
      this.mName = paramString;
    }
    
    final int calculateKeyline()
    {
      int i;
      if (!this.mReversedFlow)
      {
        if (this.mWindowAlignmentOffset >= 0) {}
        for (j = this.mWindowAlignmentOffset;; j = this.mSize + this.mWindowAlignmentOffset)
        {
          i = j;
          if (this.mWindowAlignmentOffsetPercent != -1.0F) {
            i = j + (int)(this.mSize * this.mWindowAlignmentOffsetPercent / 100.0F);
          }
          return i;
        }
      }
      if (this.mWindowAlignmentOffset >= 0) {}
      for (int j = this.mSize - this.mWindowAlignmentOffset;; j = -this.mWindowAlignmentOffset)
      {
        i = j;
        if (this.mWindowAlignmentOffsetPercent == -1.0F) {
          break;
        }
        return j - (int)(this.mSize * this.mWindowAlignmentOffsetPercent / 100.0F);
      }
    }
    
    final int calculateScrollToKeyLine(int paramInt1, int paramInt2)
    {
      return paramInt1 - paramInt2;
    }
    
    public final int getClientSize()
    {
      return this.mSize - this.mPaddingMin - this.mPaddingMax;
    }
    
    public final int getMaxScroll()
    {
      return this.mMaxScroll;
    }
    
    public final int getMinScroll()
    {
      return this.mMinScroll;
    }
    
    public final int getPaddingMax()
    {
      return this.mPaddingMax;
    }
    
    public final int getPaddingMin()
    {
      return this.mPaddingMin;
    }
    
    public final int getScroll(int paramInt)
    {
      int i = getSize();
      int j = calculateKeyline();
      boolean bool1 = isMinUnknown();
      boolean bool2 = isMaxUnknown();
      int k;
      if (!bool1)
      {
        k = this.mPaddingMin;
        if (!this.mReversedFlow)
        {
          if ((this.mWindowAlignment & 0x1) == 0) {}
        }
        else {
          while ((this.mWindowAlignment & 0x2) != 0)
          {
            if (paramInt - this.mMinEdge > j - k) {
              break;
            }
            i = this.mMinEdge - this.mPaddingMin;
            paramInt = i;
            if (!bool2)
            {
              paramInt = i;
              if (i > this.mMaxScroll) {
                paramInt = this.mMaxScroll;
              }
            }
            return paramInt;
          }
        }
      }
      if (!bool2)
      {
        k = this.mPaddingMax;
        if (!this.mReversedFlow)
        {
          if ((this.mWindowAlignment & 0x2) == 0) {}
        }
        else {
          while ((this.mWindowAlignment & 0x1) != 0)
          {
            if (this.mMaxEdge - paramInt > i - j - k) {
              break;
            }
            i = this.mMaxEdge - (i - this.mPaddingMax);
            paramInt = i;
            if (!bool1)
            {
              paramInt = i;
              if (i < this.mMinScroll) {
                paramInt = this.mMinScroll;
              }
            }
            return paramInt;
          }
        }
      }
      return calculateScrollToKeyLine(paramInt, j);
    }
    
    public final int getSize()
    {
      return this.mSize;
    }
    
    public final int getWindowAlignment()
    {
      return this.mWindowAlignment;
    }
    
    public final int getWindowAlignmentOffset()
    {
      return this.mWindowAlignmentOffset;
    }
    
    public final float getWindowAlignmentOffsetPercent()
    {
      return this.mWindowAlignmentOffsetPercent;
    }
    
    public final void invalidateScrollMax()
    {
      this.mMaxEdge = Integer.MAX_VALUE;
      this.mMaxScroll = Integer.MAX_VALUE;
    }
    
    public final void invalidateScrollMin()
    {
      this.mMinEdge = Integer.MIN_VALUE;
      this.mMinScroll = Integer.MIN_VALUE;
    }
    
    public final boolean isMaxUnknown()
    {
      return this.mMaxEdge == Integer.MAX_VALUE;
    }
    
    public final boolean isMinUnknown()
    {
      return this.mMinEdge == Integer.MIN_VALUE;
    }
    
    final boolean isPreferKeylineOverHighEdge()
    {
      return (this.mPreferredKeyLine & 0x2) != 0;
    }
    
    final boolean isPreferKeylineOverLowEdge()
    {
      return (this.mPreferredKeyLine & 0x1) != 0;
    }
    
    void reset()
    {
      this.mMinEdge = Integer.MIN_VALUE;
      this.mMaxEdge = Integer.MAX_VALUE;
    }
    
    public final void setPadding(int paramInt1, int paramInt2)
    {
      this.mPaddingMin = paramInt1;
      this.mPaddingMax = paramInt2;
    }
    
    final void setPreferKeylineOverHighEdge(boolean paramBoolean)
    {
      if (paramBoolean) {}
      for (int i = this.mPreferredKeyLine | 0x2;; i = this.mPreferredKeyLine & 0xFFFFFFFD)
      {
        this.mPreferredKeyLine = i;
        return;
      }
    }
    
    final void setPreferKeylineOverLowEdge(boolean paramBoolean)
    {
      if (paramBoolean) {}
      for (int i = this.mPreferredKeyLine | 0x1;; i = this.mPreferredKeyLine & 0xFFFFFFFE)
      {
        this.mPreferredKeyLine = i;
        return;
      }
    }
    
    public final void setReversedFlow(boolean paramBoolean)
    {
      this.mReversedFlow = paramBoolean;
    }
    
    public final void setSize(int paramInt)
    {
      this.mSize = paramInt;
    }
    
    public final void setWindowAlignment(int paramInt)
    {
      this.mWindowAlignment = paramInt;
    }
    
    public final void setWindowAlignmentOffset(int paramInt)
    {
      this.mWindowAlignmentOffset = paramInt;
    }
    
    public final void setWindowAlignmentOffsetPercent(float paramFloat)
    {
      if (((paramFloat < 0.0F) || (paramFloat > 100.0F)) && (paramFloat != -1.0F)) {
        throw new IllegalArgumentException();
      }
      this.mWindowAlignmentOffsetPercent = paramFloat;
    }
    
    public String toString()
    {
      return " min:" + this.mMinEdge + " " + this.mMinScroll + " max:" + this.mMaxEdge + " " + this.mMaxScroll;
    }
    
    public final void updateMinMax(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      this.mMinEdge = paramInt1;
      this.mMaxEdge = paramInt2;
      paramInt1 = getClientSize();
      paramInt2 = calculateKeyline();
      boolean bool1 = isMinUnknown();
      boolean bool2 = isMaxUnknown();
      if (!bool1)
      {
        if (this.mReversedFlow) {
          break label161;
        }
        if ((this.mWindowAlignment & 0x1) == 0) {
          break label170;
        }
      }
      label66:
      label87:
      label102:
      label142:
      label160:
      label161:
      label170:
      label192:
      label206:
      label215:
      do
      {
        break label160;
        break label160;
        this.mMinScroll = (this.mMinEdge - this.mPaddingMin);
        if (!bool2)
        {
          if (!this.mReversedFlow)
          {
            if ((this.mWindowAlignment & 0x2) == 0) {
              break label192;
            }
            this.mMaxScroll = (this.mMaxEdge - this.mPaddingMin - paramInt1);
          }
        }
        else if ((!bool2) && (!bool1))
        {
          if (this.mReversedFlow) {
            break label206;
          }
          if ((this.mWindowAlignment & 0x1) == 0) {
            break label215;
          }
        }
        while ((this.mWindowAlignment & 0x2) != 0)
        {
          if (this.mReversedFlow) {
            break label263;
          }
          if (!isPreferKeylineOverLowEdge()) {
            break label270;
          }
          this.mMinScroll = Math.min(this.mMinScroll, calculateScrollToKeyLine(paramInt4, paramInt2));
          return;
          if ((this.mWindowAlignment & 0x2) != 0) {
            break;
          }
          this.mMinScroll = calculateScrollToKeyLine(paramInt3, paramInt2);
          break label66;
          if ((this.mWindowAlignment & 0x1) != 0) {
            break label87;
          }
          this.mMaxScroll = calculateScrollToKeyLine(paramInt4, paramInt2);
          break label102;
        }
        if (this.mReversedFlow) {
          break label286;
        }
      } while ((this.mWindowAlignment & 0x2) == 0);
      label231:
      if (!this.mReversedFlow)
      {
        if (!isPreferKeylineOverHighEdge()) {}
      }
      else {
        label263:
        label270:
        label286:
        while (isPreferKeylineOverLowEdge())
        {
          this.mMaxScroll = Math.max(this.mMaxScroll, calculateScrollToKeyLine(paramInt3, paramInt2));
          return;
          if (isPreferKeylineOverHighEdge()) {
            break label142;
          }
          this.mMaxScroll = Math.max(this.mMinScroll, this.mMaxScroll);
          return;
          if ((this.mWindowAlignment & 0x1) == 0) {
            break;
          }
          break label231;
        }
      }
      this.mMinScroll = Math.min(this.mMinScroll, this.mMaxScroll);
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/WindowAlignment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */