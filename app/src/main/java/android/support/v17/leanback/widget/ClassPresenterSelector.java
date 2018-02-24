package android.support.v17.leanback.widget;

import java.util.ArrayList;
import java.util.HashMap;

public final class ClassPresenterSelector
  extends PresenterSelector
{
  private final HashMap<Class<?>, Object> mClassMap = new HashMap();
  private final ArrayList<Presenter> mPresenters = new ArrayList();
  
  public ClassPresenterSelector addClassPresenter(Class<?> paramClass, Presenter paramPresenter)
  {
    this.mClassMap.put(paramClass, paramPresenter);
    if (!this.mPresenters.contains(paramPresenter)) {
      this.mPresenters.add(paramPresenter);
    }
    return this;
  }
  
  public ClassPresenterSelector addClassPresenterSelector(Class<?> paramClass, PresenterSelector paramPresenterSelector)
  {
    this.mClassMap.put(paramClass, paramPresenterSelector);
    paramClass = paramPresenterSelector.getPresenters();
    int i = 0;
    while (i < paramClass.length)
    {
      if (!this.mPresenters.contains(paramClass[i])) {
        this.mPresenters.add(paramClass[i]);
      }
      i += 1;
    }
    return this;
  }
  
  public Presenter getPresenter(Object paramObject)
  {
    Object localObject1 = paramObject.getClass();
    Object localObject3;
    Object localObject2;
    do
    {
      localObject3 = this.mClassMap.get(localObject1);
      if ((localObject3 instanceof PresenterSelector))
      {
        localObject2 = ((PresenterSelector)localObject3).getPresenter(paramObject);
        if (localObject2 != null) {
          return (Presenter)localObject2;
        }
      }
      localObject2 = ((Class)localObject1).getSuperclass();
      if (localObject3 != null) {
        break;
      }
      localObject1 = localObject2;
    } while (localObject2 != null);
    return (Presenter)localObject3;
  }
  
  public Presenter[] getPresenters()
  {
    return (Presenter[])this.mPresenters.toArray(new Presenter[this.mPresenters.size()]);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/ClassPresenterSelector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */