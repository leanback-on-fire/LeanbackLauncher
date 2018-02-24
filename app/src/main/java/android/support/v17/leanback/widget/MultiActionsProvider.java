package android.support.v17.leanback.widget;

import android.graphics.drawable.Drawable;

public abstract interface MultiActionsProvider
{
  public abstract MultiAction[] getActions();
  
  public static class MultiAction
  {
    private Drawable[] mDrawables;
    private long mId;
    private int mIndex;
    
    public MultiAction(long paramLong)
    {
      this.mId = paramLong;
      this.mIndex = 0;
    }
    
    public Drawable getCurrentDrawable()
    {
      return this.mDrawables[this.mIndex];
    }
    
    public Drawable[] getDrawables()
    {
      return this.mDrawables;
    }
    
    public long getId()
    {
      return this.mId;
    }
    
    public int getIndex()
    {
      return this.mIndex;
    }
    
    public void incrementIndex()
    {
      if (this.mIndex < this.mDrawables.length - 1) {}
      for (int i = this.mIndex + 1;; i = 0)
      {
        setIndex(i);
        return;
      }
    }
    
    public void setDrawables(Drawable[] paramArrayOfDrawable)
    {
      this.mDrawables = paramArrayOfDrawable;
      if (this.mIndex > paramArrayOfDrawable.length - 1) {
        this.mIndex = (paramArrayOfDrawable.length - 1);
      }
    }
    
    public void setIndex(int paramInt)
    {
      this.mIndex = paramInt;
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/MultiActionsProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */