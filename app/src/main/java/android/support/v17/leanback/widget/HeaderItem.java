package android.support.v17.leanback.widget;

public class HeaderItem
{
  private CharSequence mContentDescription;
  private CharSequence mDescription;
  private final long mId;
  private final String mName;
  
  public HeaderItem(long paramLong, String paramString)
  {
    this.mId = paramLong;
    this.mName = paramString;
  }
  
  public HeaderItem(String paramString)
  {
    this(-1L, paramString);
  }
  
  public CharSequence getContentDescription()
  {
    return this.mContentDescription;
  }
  
  public CharSequence getDescription()
  {
    return this.mDescription;
  }
  
  public final long getId()
  {
    return this.mId;
  }
  
  public final String getName()
  {
    return this.mName;
  }
  
  public void setContentDescription(CharSequence paramCharSequence)
  {
    this.mContentDescription = paramCharSequence;
  }
  
  public void setDescription(CharSequence paramCharSequence)
  {
    this.mDescription = paramCharSequence;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/HeaderItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */