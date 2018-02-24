package android.support.v17.leanback.widget;

public final class SinglePresenterSelector
  extends PresenterSelector
{
  private final Presenter mPresenter;
  
  public SinglePresenterSelector(Presenter paramPresenter)
  {
    this.mPresenter = paramPresenter;
  }
  
  public Presenter getPresenter(Object paramObject)
  {
    return this.mPresenter;
  }
  
  public Presenter[] getPresenters()
  {
    return new Presenter[] { this.mPresenter };
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/SinglePresenterSelector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */