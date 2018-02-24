package com.google.android.gms.internal;

abstract class zzauk
  extends zzauj
{
  private boolean zzady;
  
  zzauk(zzauh paramzzauh)
  {
    super(paramzzauh);
    this.zzbLa.zzb(this);
  }
  
  public final void initialize()
  {
    if (this.zzady) {
      throw new IllegalStateException("Can't initialize twice");
    }
    onInitialize();
    this.zzbLa.zzOF();
    this.zzady = true;
  }
  
  boolean isInitialized()
  {
    return this.zzady;
  }
  
  protected abstract void onInitialize();
  
  protected void zznA()
  {
    if (!isInitialized()) {
      throw new IllegalStateException("Not initialized");
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzauk.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */