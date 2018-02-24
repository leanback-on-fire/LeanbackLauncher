package android.support.v17.leanback.widget;

import android.database.Cursor;
import android.support.v17.leanback.database.CursorMapper;
import android.util.LruCache;

public class CursorObjectAdapter
  extends ObjectAdapter
{
  private static final int CACHE_SIZE = 100;
  private Cursor mCursor;
  private final LruCache<Integer, Object> mItemCache = new LruCache(100);
  private CursorMapper mMapper;
  
  public CursorObjectAdapter() {}
  
  public CursorObjectAdapter(Presenter paramPresenter)
  {
    super(paramPresenter);
  }
  
  public CursorObjectAdapter(PresenterSelector paramPresenterSelector)
  {
    super(paramPresenterSelector);
  }
  
  public void changeCursor(Cursor paramCursor)
  {
    if (paramCursor == this.mCursor) {
      return;
    }
    if (this.mCursor != null) {
      this.mCursor.close();
    }
    this.mCursor = paramCursor;
    this.mItemCache.trimToSize(0);
    onCursorChanged();
  }
  
  public void close()
  {
    if (this.mCursor != null)
    {
      this.mCursor.close();
      this.mCursor = null;
    }
  }
  
  public Object get(int paramInt)
  {
    if (this.mCursor == null) {
      localObject1 = null;
    }
    Object localObject2;
    do
    {
      return localObject1;
      if (!this.mCursor.moveToPosition(paramInt)) {
        throw new ArrayIndexOutOfBoundsException();
      }
      localObject2 = this.mItemCache.get(Integer.valueOf(paramInt));
      localObject1 = localObject2;
    } while (localObject2 != null);
    Object localObject1 = this.mMapper.convert(this.mCursor);
    this.mItemCache.put(Integer.valueOf(paramInt), localObject1);
    return localObject1;
  }
  
  public final Cursor getCursor()
  {
    return this.mCursor;
  }
  
  public final CursorMapper getMapper()
  {
    return this.mMapper;
  }
  
  protected final void invalidateCache(int paramInt)
  {
    this.mItemCache.remove(Integer.valueOf(paramInt));
  }
  
  protected final void invalidateCache(int paramInt1, int paramInt2)
  {
    int i = paramInt1;
    while (i < paramInt2 + paramInt1)
    {
      invalidateCache(i);
      i += 1;
    }
  }
  
  public boolean isClosed()
  {
    return (this.mCursor == null) || (this.mCursor.isClosed());
  }
  
  public boolean isImmediateNotifySupported()
  {
    return true;
  }
  
  protected void onCursorChanged()
  {
    notifyChanged();
  }
  
  protected void onMapperChanged() {}
  
  public final void setMapper(CursorMapper paramCursorMapper)
  {
    if (this.mMapper != paramCursorMapper) {}
    for (int i = 1;; i = 0)
    {
      this.mMapper = paramCursorMapper;
      if (i != 0) {
        onMapperChanged();
      }
      return;
    }
  }
  
  public int size()
  {
    if (this.mCursor == null) {
      return 0;
    }
    return this.mCursor.getCount();
  }
  
  public Cursor swapCursor(Cursor paramCursor)
  {
    if (paramCursor == this.mCursor) {
      return this.mCursor;
    }
    Cursor localCursor = this.mCursor;
    this.mCursor = paramCursor;
    this.mItemCache.trimToSize(0);
    onCursorChanged();
    return localCursor;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/CursorObjectAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */