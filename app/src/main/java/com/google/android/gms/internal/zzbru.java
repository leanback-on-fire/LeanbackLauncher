package com.google.android.gms.internal;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface zzbru
  extends IInterface
{
  public abstract void onOptInOptionsChanged()
    throws RemoteException;
  
  public static abstract class zza
    extends Binder
    implements zzbru
  {
    public zza()
    {
      attachInterface(this, "com.google.android.gms.usagereporting.internal.IUsageReportingOptInOptionsChangedListener");
    }
    
    public static zzbru zziZ(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("com.google.android.gms.usagereporting.internal.IUsageReportingOptInOptionsChangedListener");
      if ((localIInterface != null) && ((localIInterface instanceof zzbru))) {
        return (zzbru)localIInterface;
      }
      return new zza(paramIBinder);
    }
    
    public IBinder asBinder()
    {
      return this;
    }
    
    public boolean onTransact(int paramInt1, Parcel paramParcel1, Parcel paramParcel2, int paramInt2)
      throws RemoteException
    {
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("com.google.android.gms.usagereporting.internal.IUsageReportingOptInOptionsChangedListener");
        return true;
      }
      paramParcel1.enforceInterface("com.google.android.gms.usagereporting.internal.IUsageReportingOptInOptionsChangedListener");
      onOptInOptionsChanged();
      return true;
    }
    
    private static class zza
      implements zzbru
    {
      private IBinder zzrj;
      
      zza(IBinder paramIBinder)
      {
        this.zzrj = paramIBinder;
      }
      
      public IBinder asBinder()
      {
        return this.zzrj;
      }
      
      public void onOptInOptionsChanged()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("com.google.android.gms.usagereporting.internal.IUsageReportingOptInOptionsChangedListener");
          this.zzrj.transact(2, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzbru.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */