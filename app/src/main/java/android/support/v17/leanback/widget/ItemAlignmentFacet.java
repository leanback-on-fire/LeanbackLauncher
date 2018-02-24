package android.support.v17.leanback.widget;

public final class ItemAlignmentFacet
{
  public static final float ITEM_ALIGN_OFFSET_PERCENT_DISABLED = -1.0F;
  private ItemAlignmentDef[] mAlignmentDefs = { new ItemAlignmentDef() };
  
  public ItemAlignmentDef[] getAlignmentDefs()
  {
    return this.mAlignmentDefs;
  }
  
  public boolean isMultiAlignment()
  {
    return this.mAlignmentDefs.length > 1;
  }
  
  public void setAlignmentDefs(ItemAlignmentDef[] paramArrayOfItemAlignmentDef)
  {
    if ((paramArrayOfItemAlignmentDef == null) || (paramArrayOfItemAlignmentDef.length < 1)) {
      throw new IllegalArgumentException();
    }
    this.mAlignmentDefs = paramArrayOfItemAlignmentDef;
  }
  
  public static class ItemAlignmentDef
  {
    private boolean mAlignToBaseline;
    int mFocusViewId = -1;
    int mOffset = 0;
    float mOffsetPercent = 50.0F;
    boolean mOffsetWithPadding = false;
    int mViewId = -1;
    
    public final int getItemAlignmentFocusViewId()
    {
      if (this.mFocusViewId != -1) {
        return this.mFocusViewId;
      }
      return this.mViewId;
    }
    
    public final int getItemAlignmentOffset()
    {
      return this.mOffset;
    }
    
    public final float getItemAlignmentOffsetPercent()
    {
      return this.mOffsetPercent;
    }
    
    public final int getItemAlignmentViewId()
    {
      return this.mViewId;
    }
    
    public boolean isAlignedToTextViewBaseLine()
    {
      return this.mAlignToBaseline;
    }
    
    public final boolean isItemAlignmentOffsetWithPadding()
    {
      return this.mOffsetWithPadding;
    }
    
    public final void setAlignedToTextViewBaseline(boolean paramBoolean)
    {
      this.mAlignToBaseline = paramBoolean;
    }
    
    public final void setItemAlignmentFocusViewId(int paramInt)
    {
      this.mFocusViewId = paramInt;
    }
    
    public final void setItemAlignmentOffset(int paramInt)
    {
      this.mOffset = paramInt;
    }
    
    public final void setItemAlignmentOffsetPercent(float paramFloat)
    {
      if (((paramFloat < 0.0F) || (paramFloat > 100.0F)) && (paramFloat != -1.0F)) {
        throw new IllegalArgumentException();
      }
      this.mOffsetPercent = paramFloat;
    }
    
    public final void setItemAlignmentOffsetWithPadding(boolean paramBoolean)
    {
      this.mOffsetWithPadding = paramBoolean;
    }
    
    public final void setItemAlignmentViewId(int paramInt)
    {
      this.mViewId = paramInt;
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/ItemAlignmentFacet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */