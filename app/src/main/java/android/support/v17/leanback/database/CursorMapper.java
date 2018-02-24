package android.support.v17.leanback.database;

import android.database.Cursor;

public abstract class CursorMapper
{
  private Cursor mCursor;
  
  protected abstract Object bind(Cursor paramCursor);
  
  protected abstract void bindColumns(Cursor paramCursor);
  
  public Object convert(Cursor paramCursor)
  {
    if (paramCursor != this.mCursor)
    {
      this.mCursor = paramCursor;
      bindColumns(this.mCursor);
    }
    return bind(this.mCursor);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/database/CursorMapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */