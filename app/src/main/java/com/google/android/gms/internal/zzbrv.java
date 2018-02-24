package com.google.android.gms.internal;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import com.google.android.gms.usagereporting.UsageReportingOptInOptions;

public abstract interface zzbrv
  extends IInterface
{
  public abstract void zza(zzbrt paramzzbrt)
    throws RemoteException;
  
  public abstract void zza(zzbru paramzzbru, zzbrt paramzzbrt)
    throws RemoteException;
  
  public abstract void zza(UsageReportingOptInOptions paramUsageReportingOptInOptions, zzbrt paramzzbrt)
    throws RemoteException;
  
  public abstract void zzb(zzbru paramzzbru, zzbrt paramzzbrt)
    throws RemoteException;
  
  public static abstract class zza
    extends Binder
    implements zzbrv
  {
    public static zzbrv zzja(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("com.google.android.gms.usagereporting.internal.IUsageReportingService");
      if ((localIInterface != null) && ((localIInterface instanceof zzbrv))) {
        return (zzbrv)localIInterface;
      }
      return new zza(paramIBinder);
    }
    
    public boolean onTransact(int paramInt1, Parcel paramParcel1, Parcel paramParcel2, int paramInt2)
      throws RemoteException
    {
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("com.google.android.gms.usagereporting.internal.IUsageReportingService");
        return true;
      case 2: 
        paramParcel1.enforceInterface("com.google.android.gms.usagereporting.internal.IUsageReportingService");
        zza(zzbrt.zza.zziY(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 3: 
        paramParcel1.enforceInterface("com.google.android.gms.usagereporting.internal.IUsageReportingService");
        if (paramParcel1.readInt() != 0) {}
        for (UsageReportingOptInOptions localUsageReportingOptInOptions = (UsageReportingOptInOptions)UsageReportingOptInOptions.CREATOR.createFromParcel(paramParcel1);; localUsageReportingOptInOptions = null)
        {
          zza(localUsageReportingOptInOptions, zzbrt.zza.zziY(paramParcel1.readStrongBinder()));
          paramParcel2.writeNoException();
          return true;
        }
      case 4: 
        paramParcel1.enforceInterface("com.google.android.gms.usagereporting.internal.IUsageReportingService");
        zza(zzbru.zza.zziZ(paramParcel1.readStrongBinder()), zzbrt.zza.zziY(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      }
      paramParcel1.enforceInterface("com.google.android.gms.usagereporting.internal.IUsageReportingService");
      zzb(zzbru.zza.zziZ(paramParcel1.readStrongBinder()), zzbrt.zza.zziY(paramParcel1.readStrongBinder()));
      paramParcel2.writeNoException();
      return true;
    }
    
    private static class zza
      implements zzbrv
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
      
      /* Error */
      public void zza(zzbrt paramzzbrt)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore_3
        //   8: aload_2
        //   9: ldc 32
        //   11: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload_1
        //   15: ifnull +42 -> 57
        //   18: aload_1
        //   19: invokeinterface 40 1 0
        //   24: astore_1
        //   25: aload_2
        //   26: aload_1
        //   27: invokevirtual 43	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   30: aload_0
        //   31: getfield 18	com/google/android/gms/internal/zzbrv$zza$zza:zzrj	Landroid/os/IBinder;
        //   34: iconst_2
        //   35: aload_2
        //   36: aload_3
        //   37: iconst_0
        //   38: invokeinterface 49 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 52	android/os/Parcel:readException	()V
        //   48: aload_3
        //   49: invokevirtual 55	android/os/Parcel:recycle	()V
        //   52: aload_2
        //   53: invokevirtual 55	android/os/Parcel:recycle	()V
        //   56: return
        //   57: aconst_null
        //   58: astore_1
        //   59: goto -34 -> 25
        //   62: astore_1
        //   63: aload_3
        //   64: invokevirtual 55	android/os/Parcel:recycle	()V
        //   67: aload_2
        //   68: invokevirtual 55	android/os/Parcel:recycle	()V
        //   71: aload_1
        //   72: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	73	0	this	zza
        //   0	73	1	paramzzbrt	zzbrt
        //   3	65	2	localParcel1	Parcel
        //   7	57	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	62	finally
        //   18	25	62	finally
        //   25	48	62	finally
      }
      
      /* Error */
      public void zza(zzbru paramzzbru, zzbrt paramzzbrt)
        throws RemoteException
      {
        // Byte code:
        //   0: aconst_null
        //   1: astore_3
        //   2: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   5: astore 4
        //   7: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   10: astore 5
        //   12: aload 4
        //   14: ldc 32
        //   16: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   19: aload_1
        //   20: ifnull +67 -> 87
        //   23: aload_1
        //   24: invokeinterface 60 1 0
        //   29: astore_1
        //   30: aload 4
        //   32: aload_1
        //   33: invokevirtual 43	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   36: aload_3
        //   37: astore_1
        //   38: aload_2
        //   39: ifnull +10 -> 49
        //   42: aload_2
        //   43: invokeinterface 40 1 0
        //   48: astore_1
        //   49: aload 4
        //   51: aload_1
        //   52: invokevirtual 43	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   55: aload_0
        //   56: getfield 18	com/google/android/gms/internal/zzbrv$zza$zza:zzrj	Landroid/os/IBinder;
        //   59: iconst_4
        //   60: aload 4
        //   62: aload 5
        //   64: iconst_0
        //   65: invokeinterface 49 5 0
        //   70: pop
        //   71: aload 5
        //   73: invokevirtual 52	android/os/Parcel:readException	()V
        //   76: aload 5
        //   78: invokevirtual 55	android/os/Parcel:recycle	()V
        //   81: aload 4
        //   83: invokevirtual 55	android/os/Parcel:recycle	()V
        //   86: return
        //   87: aconst_null
        //   88: astore_1
        //   89: goto -59 -> 30
        //   92: astore_1
        //   93: aload 5
        //   95: invokevirtual 55	android/os/Parcel:recycle	()V
        //   98: aload 4
        //   100: invokevirtual 55	android/os/Parcel:recycle	()V
        //   103: aload_1
        //   104: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	105	0	this	zza
        //   0	105	1	paramzzbru	zzbru
        //   0	105	2	paramzzbrt	zzbrt
        //   1	36	3	localObject	Object
        //   5	94	4	localParcel1	Parcel
        //   10	84	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   12	19	92	finally
        //   23	30	92	finally
        //   30	36	92	finally
        //   42	49	92	finally
        //   49	76	92	finally
      }
      
      public void zza(UsageReportingOptInOptions paramUsageReportingOptInOptions, zzbrt paramzzbrt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("com.google.android.gms.usagereporting.internal.IUsageReportingService");
            if (paramUsageReportingOptInOptions != null)
            {
              localParcel1.writeInt(1);
              paramUsageReportingOptInOptions.writeToParcel(localParcel1, 0);
              if (paramzzbrt != null)
              {
                paramUsageReportingOptInOptions = paramzzbrt.asBinder();
                localParcel1.writeStrongBinder(paramUsageReportingOptInOptions);
                this.zzrj.transact(3, localParcel1, localParcel2, 0);
                localParcel2.readException();
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            paramUsageReportingOptInOptions = null;
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      /* Error */
      public void zzb(zzbru paramzzbru, zzbrt paramzzbrt)
        throws RemoteException
      {
        // Byte code:
        //   0: aconst_null
        //   1: astore_3
        //   2: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   5: astore 4
        //   7: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   10: astore 5
        //   12: aload 4
        //   14: ldc 32
        //   16: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   19: aload_1
        //   20: ifnull +67 -> 87
        //   23: aload_1
        //   24: invokeinterface 60 1 0
        //   29: astore_1
        //   30: aload 4
        //   32: aload_1
        //   33: invokevirtual 43	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   36: aload_3
        //   37: astore_1
        //   38: aload_2
        //   39: ifnull +10 -> 49
        //   42: aload_2
        //   43: invokeinterface 40 1 0
        //   48: astore_1
        //   49: aload 4
        //   51: aload_1
        //   52: invokevirtual 43	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   55: aload_0
        //   56: getfield 18	com/google/android/gms/internal/zzbrv$zza$zza:zzrj	Landroid/os/IBinder;
        //   59: iconst_5
        //   60: aload 4
        //   62: aload 5
        //   64: iconst_0
        //   65: invokeinterface 49 5 0
        //   70: pop
        //   71: aload 5
        //   73: invokevirtual 52	android/os/Parcel:readException	()V
        //   76: aload 5
        //   78: invokevirtual 55	android/os/Parcel:recycle	()V
        //   81: aload 4
        //   83: invokevirtual 55	android/os/Parcel:recycle	()V
        //   86: return
        //   87: aconst_null
        //   88: astore_1
        //   89: goto -59 -> 30
        //   92: astore_1
        //   93: aload 5
        //   95: invokevirtual 55	android/os/Parcel:recycle	()V
        //   98: aload 4
        //   100: invokevirtual 55	android/os/Parcel:recycle	()V
        //   103: aload_1
        //   104: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	105	0	this	zza
        //   0	105	1	paramzzbru	zzbru
        //   0	105	2	paramzzbrt	zzbrt
        //   1	36	3	localObject	Object
        //   5	94	4	localParcel1	Parcel
        //   10	84	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   12	19	92	finally
        //   23	30	92	finally
        //   30	36	92	finally
        //   42	49	92	finally
        //   49	76	92	finally
      }
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzbrv.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */