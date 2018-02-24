package android.support.v17.leanback.widget;

public class Row
{
  private static final int FLAG_ID_USE_HEADER = 1;
  private static final int FLAG_ID_USE_ID = 0;
  private static final int FLAG_ID_USE_MASK = 1;
  private int mFlags = 1;
  private HeaderItem mHeaderItem;
  private long mId = -1L;
  
  public Row() {}
  
  public Row(long paramLong, HeaderItem paramHeaderItem)
  {
    setId(paramLong);
    setHeaderItem(paramHeaderItem);
  }
  
  public Row(HeaderItem paramHeaderItem)
  {
    setHeaderItem(paramHeaderItem);
  }
  
  final int getFlags()
  {
    return this.mFlags;
  }
  
  public final HeaderItem getHeaderItem()
  {
    return this.mHeaderItem;
  }
  
  public final long getId()
  {
    if ((this.mFlags & 0x1) == 1)
    {
      HeaderItem localHeaderItem = getHeaderItem();
      if (localHeaderItem != null) {
        return localHeaderItem.getId();
      }
      return -1L;
    }
    return this.mId;
  }
  
  public boolean isRenderedAsRowView()
  {
    return true;
  }
  
  final void setFlags(int paramInt1, int paramInt2)
  {
    this.mFlags = (this.mFlags & (paramInt2 ^ 0xFFFFFFFF) | paramInt1 & paramInt2);
  }
  
  public final void setHeaderItem(HeaderItem paramHeaderItem)
  {
    this.mHeaderItem = paramHeaderItem;
  }
  
  public final void setId(long paramLong)
  {
    this.mId = paramLong;
    setFlags(0, 1);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/Row.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */