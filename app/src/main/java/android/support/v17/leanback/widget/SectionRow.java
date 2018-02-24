package android.support.v17.leanback.widget;

public class SectionRow
  extends Row
{
  public SectionRow(long paramLong, String paramString)
  {
    super(new HeaderItem(paramLong, paramString));
  }
  
  public SectionRow(HeaderItem paramHeaderItem)
  {
    super(paramHeaderItem);
  }
  
  public SectionRow(String paramString)
  {
    super(new HeaderItem(paramString));
  }
  
  public final boolean isRenderedAsRowView()
  {
    return false;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/SectionRow.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */