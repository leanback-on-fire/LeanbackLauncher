package android.support.v17.leanback.widget;

public class ListRow
  extends Row
{
  private final ObjectAdapter mAdapter;
  private CharSequence mContentDescription;
  
  public ListRow(long paramLong, HeaderItem paramHeaderItem, ObjectAdapter paramObjectAdapter)
  {
    super(paramLong, paramHeaderItem);
    this.mAdapter = paramObjectAdapter;
    verify();
  }
  
  public ListRow(HeaderItem paramHeaderItem, ObjectAdapter paramObjectAdapter)
  {
    super(paramHeaderItem);
    this.mAdapter = paramObjectAdapter;
    verify();
  }
  
  public ListRow(ObjectAdapter paramObjectAdapter)
  {
    this.mAdapter = paramObjectAdapter;
    verify();
  }
  
  private void verify()
  {
    if (this.mAdapter == null) {
      throw new IllegalArgumentException("ObjectAdapter cannot be null");
    }
  }
  
  public final ObjectAdapter getAdapter()
  {
    return this.mAdapter;
  }
  
  public CharSequence getContentDescription()
  {
    Object localObject;
    if (this.mContentDescription != null) {
      localObject = this.mContentDescription;
    }
    HeaderItem localHeaderItem;
    CharSequence localCharSequence;
    do
    {
      return (CharSequence)localObject;
      localHeaderItem = getHeaderItem();
      if (localHeaderItem == null) {
        break;
      }
      localCharSequence = localHeaderItem.getContentDescription();
      localObject = localCharSequence;
    } while (localCharSequence != null);
    return localHeaderItem.getName();
    return null;
  }
  
  public void setContentDescription(CharSequence paramCharSequence)
  {
    this.mContentDescription = paramCharSequence;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/ListRow.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */