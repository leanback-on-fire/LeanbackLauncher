package com.google.android.gms.internal;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import java.util.List;

public abstract interface zzatw
  extends IInterface
{
  public abstract List<zzaut> zza(zzatg paramzzatg, boolean paramBoolean)
    throws RemoteException;
  
  public abstract List<zzatj> zza(String paramString1, String paramString2, zzatg paramzzatg)
    throws RemoteException;
  
  public abstract List<zzaut> zza(String paramString1, String paramString2, String paramString3, boolean paramBoolean)
    throws RemoteException;
  
  public abstract List<zzaut> zza(String paramString1, String paramString2, boolean paramBoolean, zzatg paramzzatg)
    throws RemoteException;
  
  public abstract void zza(long paramLong, String paramString1, String paramString2, String paramString3)
    throws RemoteException;
  
  public abstract void zza(zzatg paramzzatg)
    throws RemoteException;
  
  public abstract void zza(zzatj paramzzatj, zzatg paramzzatg)
    throws RemoteException;
  
  public abstract void zza(zzatt paramzzatt, zzatg paramzzatg)
    throws RemoteException;
  
  public abstract void zza(zzatt paramzzatt, String paramString1, String paramString2)
    throws RemoteException;
  
  public abstract void zza(zzaut paramzzaut, zzatg paramzzatg)
    throws RemoteException;
  
  public abstract byte[] zza(zzatt paramzzatt, String paramString)
    throws RemoteException;
  
  public abstract void zzb(zzatg paramzzatg)
    throws RemoteException;
  
  public abstract void zzb(zzatj paramzzatj)
    throws RemoteException;
  
  public abstract String zzc(zzatg paramzzatg)
    throws RemoteException;
  
  public abstract List<zzatj> zzl(String paramString1, String paramString2, String paramString3)
    throws RemoteException;
  
  public static abstract class zza
    extends Binder
    implements zzatw
  {
    public zza()
    {
      attachInterface(this, "com.google.android.gms.measurement.internal.IMeasurementService");
    }
    
    public static zzatw zzgP(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("com.google.android.gms.measurement.internal.IMeasurementService");
      if ((localIInterface != null) && ((localIInterface instanceof zzatw))) {
        return (zzatw)localIInterface;
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
      boolean bool2 = false;
      boolean bool3 = false;
      boolean bool1 = false;
      Object localObject;
      label232:
      label303:
      label709:
      String str1;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("com.google.android.gms.measurement.internal.IMeasurementService");
        return true;
      case 1: 
        paramParcel1.enforceInterface("com.google.android.gms.measurement.internal.IMeasurementService");
        if (paramParcel1.readInt() != 0)
        {
          localObject = (zzatt)zzatt.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label232;
          }
        }
        for (paramParcel1 = (zzatg)zzatg.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          zza((zzatt)localObject, paramParcel1);
          paramParcel2.writeNoException();
          return true;
          localObject = null;
          break;
        }
      case 2: 
        paramParcel1.enforceInterface("com.google.android.gms.measurement.internal.IMeasurementService");
        if (paramParcel1.readInt() != 0)
        {
          localObject = (zzaut)zzaut.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label303;
          }
        }
        for (paramParcel1 = (zzatg)zzatg.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          zza((zzaut)localObject, paramParcel1);
          paramParcel2.writeNoException();
          return true;
          localObject = null;
          break;
        }
      case 4: 
        paramParcel1.enforceInterface("com.google.android.gms.measurement.internal.IMeasurementService");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (zzatg)zzatg.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          zza(paramParcel1);
          paramParcel2.writeNoException();
          return true;
        }
      case 5: 
        paramParcel1.enforceInterface("com.google.android.gms.measurement.internal.IMeasurementService");
        if (paramParcel1.readInt() != 0) {}
        for (localObject = (zzatt)zzatt.CREATOR.createFromParcel(paramParcel1);; localObject = null)
        {
          zza((zzatt)localObject, paramParcel1.readString(), paramParcel1.readString());
          paramParcel2.writeNoException();
          return true;
        }
      case 6: 
        paramParcel1.enforceInterface("com.google.android.gms.measurement.internal.IMeasurementService");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (zzatg)zzatg.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          zzb(paramParcel1);
          paramParcel2.writeNoException();
          return true;
        }
      case 7: 
        paramParcel1.enforceInterface("com.google.android.gms.measurement.internal.IMeasurementService");
        if (paramParcel1.readInt() != 0) {}
        for (localObject = (zzatg)zzatg.CREATOR.createFromParcel(paramParcel1);; localObject = null)
        {
          if (paramParcel1.readInt() != 0) {
            bool1 = true;
          }
          paramParcel1 = zza((zzatg)localObject, bool1);
          paramParcel2.writeNoException();
          paramParcel2.writeTypedList(paramParcel1);
          return true;
        }
      case 9: 
        paramParcel1.enforceInterface("com.google.android.gms.measurement.internal.IMeasurementService");
        if (paramParcel1.readInt() != 0) {}
        for (localObject = (zzatt)zzatt.CREATOR.createFromParcel(paramParcel1);; localObject = null)
        {
          paramParcel1 = zza((zzatt)localObject, paramParcel1.readString());
          paramParcel2.writeNoException();
          paramParcel2.writeByteArray(paramParcel1);
          return true;
        }
      case 10: 
        paramParcel1.enforceInterface("com.google.android.gms.measurement.internal.IMeasurementService");
        zza(paramParcel1.readLong(), paramParcel1.readString(), paramParcel1.readString(), paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 11: 
        paramParcel1.enforceInterface("com.google.android.gms.measurement.internal.IMeasurementService");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (zzatg)zzatg.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          paramParcel1 = zzc(paramParcel1);
          paramParcel2.writeNoException();
          paramParcel2.writeString(paramParcel1);
          return true;
        }
      case 12: 
        paramParcel1.enforceInterface("com.google.android.gms.measurement.internal.IMeasurementService");
        if (paramParcel1.readInt() != 0)
        {
          localObject = (zzatj)zzatj.CREATOR.createFromParcel(paramParcel1);
          if (paramParcel1.readInt() == 0) {
            break label709;
          }
        }
        for (paramParcel1 = (zzatg)zzatg.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          zza((zzatj)localObject, paramParcel1);
          paramParcel2.writeNoException();
          return true;
          localObject = null;
          break;
        }
      case 13: 
        paramParcel1.enforceInterface("com.google.android.gms.measurement.internal.IMeasurementService");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (zzatj)zzatj.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          zzb(paramParcel1);
          paramParcel2.writeNoException();
          return true;
        }
      case 14: 
        paramParcel1.enforceInterface("com.google.android.gms.measurement.internal.IMeasurementService");
        localObject = paramParcel1.readString();
        str1 = paramParcel1.readString();
        bool1 = bool2;
        if (paramParcel1.readInt() != 0) {
          bool1 = true;
        }
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (zzatg)zzatg.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          paramParcel1 = zza((String)localObject, str1, bool1, paramParcel1);
          paramParcel2.writeNoException();
          paramParcel2.writeTypedList(paramParcel1);
          return true;
        }
      case 15: 
        paramParcel1.enforceInterface("com.google.android.gms.measurement.internal.IMeasurementService");
        localObject = paramParcel1.readString();
        str1 = paramParcel1.readString();
        String str2 = paramParcel1.readString();
        bool1 = bool3;
        if (paramParcel1.readInt() != 0) {
          bool1 = true;
        }
        paramParcel1 = zza((String)localObject, str1, str2, bool1);
        paramParcel2.writeNoException();
        paramParcel2.writeTypedList(paramParcel1);
        return true;
      case 16: 
        paramParcel1.enforceInterface("com.google.android.gms.measurement.internal.IMeasurementService");
        localObject = paramParcel1.readString();
        str1 = paramParcel1.readString();
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (zzatg)zzatg.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          paramParcel1 = zza((String)localObject, str1, paramParcel1);
          paramParcel2.writeNoException();
          paramParcel2.writeTypedList(paramParcel1);
          return true;
        }
      }
      paramParcel1.enforceInterface("com.google.android.gms.measurement.internal.IMeasurementService");
      paramParcel1 = zzl(paramParcel1.readString(), paramParcel1.readString(), paramParcel1.readString());
      paramParcel2.writeNoException();
      paramParcel2.writeTypedList(paramParcel1);
      return true;
    }
    
    private static class zza
      implements zzatw
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
      
      public List<zzaut> zza(zzatg paramzzatg, boolean paramBoolean)
        throws RemoteException
      {
        int i = 1;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("com.google.android.gms.measurement.internal.IMeasurementService");
            if (paramzzatg != null)
            {
              localParcel1.writeInt(1);
              paramzzatg.writeToParcel(localParcel1, 0);
              break label115;
              localParcel1.writeInt(i);
              this.zzrj.transact(7, localParcel1, localParcel2, 0);
              localParcel2.readException();
              paramzzatg = localParcel2.createTypedArrayList(zzaut.CREATOR);
              return paramzzatg;
            }
            else
            {
              localParcel1.writeInt(0);
            }
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
          label115:
          do
          {
            i = 0;
            break;
          } while (!paramBoolean);
        }
      }
      
      /* Error */
      public List<zzatj> zza(String paramString1, String paramString2, zzatg paramzzatg)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 4
        //   5: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 5
        //   10: aload 4
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload 4
        //   19: aload_1
        //   20: invokevirtual 75	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   23: aload 4
        //   25: aload_2
        //   26: invokevirtual 75	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   29: aload_3
        //   30: ifnull +59 -> 89
        //   33: aload 4
        //   35: iconst_1
        //   36: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   39: aload_3
        //   40: aload 4
        //   42: iconst_0
        //   43: invokevirtual 46	com/google/android/gms/internal/zzatg:writeToParcel	(Landroid/os/Parcel;I)V
        //   46: aload_0
        //   47: getfield 18	com/google/android/gms/internal/zzatw$zza$zza:zzrj	Landroid/os/IBinder;
        //   50: bipush 16
        //   52: aload 4
        //   54: aload 5
        //   56: iconst_0
        //   57: invokeinterface 52 5 0
        //   62: pop
        //   63: aload 5
        //   65: invokevirtual 55	android/os/Parcel:readException	()V
        //   68: aload 5
        //   70: getstatic 78	com/google/android/gms/internal/zzatj:CREATOR	Landroid/os/Parcelable$Creator;
        //   73: invokevirtual 65	android/os/Parcel:createTypedArrayList	(Landroid/os/Parcelable$Creator;)Ljava/util/ArrayList;
        //   76: astore_1
        //   77: aload 5
        //   79: invokevirtual 68	android/os/Parcel:recycle	()V
        //   82: aload 4
        //   84: invokevirtual 68	android/os/Parcel:recycle	()V
        //   87: aload_1
        //   88: areturn
        //   89: aload 4
        //   91: iconst_0
        //   92: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   95: goto -49 -> 46
        //   98: astore_1
        //   99: aload 5
        //   101: invokevirtual 68	android/os/Parcel:recycle	()V
        //   104: aload 4
        //   106: invokevirtual 68	android/os/Parcel:recycle	()V
        //   109: aload_1
        //   110: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	111	0	this	zza
        //   0	111	1	paramString1	String
        //   0	111	2	paramString2	String
        //   0	111	3	paramzzatg	zzatg
        //   3	102	4	localParcel1	Parcel
        //   8	92	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	29	98	finally
        //   33	46	98	finally
        //   46	77	98	finally
        //   89	95	98	finally
      }
      
      public List<zzaut> zza(String paramString1, String paramString2, String paramString3, boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.google.android.gms.measurement.internal.IMeasurementService");
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          localParcel1.writeString(paramString3);
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
          this.zzrj.transact(15, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramString1 = localParcel2.createTypedArrayList(zzaut.CREATOR);
          return paramString1;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public List<zzaut> zza(String paramString1, String paramString2, boolean paramBoolean, zzatg paramzzatg)
        throws RemoteException
      {
        // Byte code:
        //   0: iconst_1
        //   1: istore 5
        //   3: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   6: astore 6
        //   8: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   11: astore 7
        //   13: aload 6
        //   15: ldc 32
        //   17: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   20: aload 6
        //   22: aload_1
        //   23: invokevirtual 75	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   26: aload 6
        //   28: aload_2
        //   29: invokevirtual 75	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   32: iload_3
        //   33: ifeq +72 -> 105
        //   36: aload 6
        //   38: iload 5
        //   40: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   43: aload 4
        //   45: ifnull +66 -> 111
        //   48: aload 6
        //   50: iconst_1
        //   51: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   54: aload 4
        //   56: aload 6
        //   58: iconst_0
        //   59: invokevirtual 46	com/google/android/gms/internal/zzatg:writeToParcel	(Landroid/os/Parcel;I)V
        //   62: aload_0
        //   63: getfield 18	com/google/android/gms/internal/zzatw$zza$zza:zzrj	Landroid/os/IBinder;
        //   66: bipush 14
        //   68: aload 6
        //   70: aload 7
        //   72: iconst_0
        //   73: invokeinterface 52 5 0
        //   78: pop
        //   79: aload 7
        //   81: invokevirtual 55	android/os/Parcel:readException	()V
        //   84: aload 7
        //   86: getstatic 61	com/google/android/gms/internal/zzaut:CREATOR	Landroid/os/Parcelable$Creator;
        //   89: invokevirtual 65	android/os/Parcel:createTypedArrayList	(Landroid/os/Parcelable$Creator;)Ljava/util/ArrayList;
        //   92: astore_1
        //   93: aload 7
        //   95: invokevirtual 68	android/os/Parcel:recycle	()V
        //   98: aload 6
        //   100: invokevirtual 68	android/os/Parcel:recycle	()V
        //   103: aload_1
        //   104: areturn
        //   105: iconst_0
        //   106: istore 5
        //   108: goto -72 -> 36
        //   111: aload 6
        //   113: iconst_0
        //   114: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   117: goto -55 -> 62
        //   120: astore_1
        //   121: aload 7
        //   123: invokevirtual 68	android/os/Parcel:recycle	()V
        //   126: aload 6
        //   128: invokevirtual 68	android/os/Parcel:recycle	()V
        //   131: aload_1
        //   132: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	133	0	this	zza
        //   0	133	1	paramString1	String
        //   0	133	2	paramString2	String
        //   0	133	3	paramBoolean	boolean
        //   0	133	4	paramzzatg	zzatg
        //   1	106	5	i	int
        //   6	121	6	localParcel1	Parcel
        //   11	111	7	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   13	32	120	finally
        //   36	43	120	finally
        //   48	62	120	finally
        //   62	93	120	finally
        //   111	117	120	finally
      }
      
      public void zza(long paramLong, String paramString1, String paramString2, String paramString3)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.google.android.gms.measurement.internal.IMeasurementService");
          localParcel1.writeLong(paramLong);
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          localParcel1.writeString(paramString3);
          this.zzrj.transact(10, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      /* Error */
      public void zza(zzatg paramzzatg)
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
        //   15: ifnull +41 -> 56
        //   18: aload_2
        //   19: iconst_1
        //   20: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   23: aload_1
        //   24: aload_2
        //   25: iconst_0
        //   26: invokevirtual 46	com/google/android/gms/internal/zzatg:writeToParcel	(Landroid/os/Parcel;I)V
        //   29: aload_0
        //   30: getfield 18	com/google/android/gms/internal/zzatw$zza$zza:zzrj	Landroid/os/IBinder;
        //   33: iconst_4
        //   34: aload_2
        //   35: aload_3
        //   36: iconst_0
        //   37: invokeinterface 52 5 0
        //   42: pop
        //   43: aload_3
        //   44: invokevirtual 55	android/os/Parcel:readException	()V
        //   47: aload_3
        //   48: invokevirtual 68	android/os/Parcel:recycle	()V
        //   51: aload_2
        //   52: invokevirtual 68	android/os/Parcel:recycle	()V
        //   55: return
        //   56: aload_2
        //   57: iconst_0
        //   58: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   61: goto -32 -> 29
        //   64: astore_1
        //   65: aload_3
        //   66: invokevirtual 68	android/os/Parcel:recycle	()V
        //   69: aload_2
        //   70: invokevirtual 68	android/os/Parcel:recycle	()V
        //   73: aload_1
        //   74: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	75	0	this	zza
        //   0	75	1	paramzzatg	zzatg
        //   3	67	2	localParcel1	Parcel
        //   7	59	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	64	finally
        //   18	29	64	finally
        //   29	47	64	finally
        //   56	61	64	finally
      }
      
      public void zza(zzatj paramzzatj, zzatg paramzzatg)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("com.google.android.gms.measurement.internal.IMeasurementService");
            if (paramzzatj != null)
            {
              localParcel1.writeInt(1);
              paramzzatj.writeToParcel(localParcel1, 0);
              if (paramzzatg != null)
              {
                localParcel1.writeInt(1);
                paramzzatg.writeToParcel(localParcel1, 0);
                this.zzrj.transact(12, localParcel1, localParcel2, 0);
                localParcel2.readException();
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            localParcel1.writeInt(0);
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      public void zza(zzatt paramzzatt, zzatg paramzzatg)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("com.google.android.gms.measurement.internal.IMeasurementService");
            if (paramzzatt != null)
            {
              localParcel1.writeInt(1);
              paramzzatt.writeToParcel(localParcel1, 0);
              if (paramzzatg != null)
              {
                localParcel1.writeInt(1);
                paramzzatg.writeToParcel(localParcel1, 0);
                this.zzrj.transact(1, localParcel1, localParcel2, 0);
                localParcel2.readException();
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            localParcel1.writeInt(0);
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      /* Error */
      public void zza(zzatt paramzzatt, String paramString1, String paramString2)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore 4
        //   5: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   8: astore 5
        //   10: aload 4
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload_1
        //   18: ifnull +60 -> 78
        //   21: aload 4
        //   23: iconst_1
        //   24: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   27: aload_1
        //   28: aload 4
        //   30: iconst_0
        //   31: invokevirtual 95	com/google/android/gms/internal/zzatt:writeToParcel	(Landroid/os/Parcel;I)V
        //   34: aload 4
        //   36: aload_2
        //   37: invokevirtual 75	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   40: aload 4
        //   42: aload_3
        //   43: invokevirtual 75	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   46: aload_0
        //   47: getfield 18	com/google/android/gms/internal/zzatw$zza$zza:zzrj	Landroid/os/IBinder;
        //   50: iconst_5
        //   51: aload 4
        //   53: aload 5
        //   55: iconst_0
        //   56: invokeinterface 52 5 0
        //   61: pop
        //   62: aload 5
        //   64: invokevirtual 55	android/os/Parcel:readException	()V
        //   67: aload 5
        //   69: invokevirtual 68	android/os/Parcel:recycle	()V
        //   72: aload 4
        //   74: invokevirtual 68	android/os/Parcel:recycle	()V
        //   77: return
        //   78: aload 4
        //   80: iconst_0
        //   81: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   84: goto -50 -> 34
        //   87: astore_1
        //   88: aload 5
        //   90: invokevirtual 68	android/os/Parcel:recycle	()V
        //   93: aload 4
        //   95: invokevirtual 68	android/os/Parcel:recycle	()V
        //   98: aload_1
        //   99: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	100	0	this	zza
        //   0	100	1	paramzzatt	zzatt
        //   0	100	2	paramString1	String
        //   0	100	3	paramString2	String
        //   3	91	4	localParcel1	Parcel
        //   8	81	5	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   10	17	87	finally
        //   21	34	87	finally
        //   34	67	87	finally
        //   78	84	87	finally
      }
      
      public void zza(zzaut paramzzaut, zzatg paramzzatg)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("com.google.android.gms.measurement.internal.IMeasurementService");
            if (paramzzaut != null)
            {
              localParcel1.writeInt(1);
              paramzzaut.writeToParcel(localParcel1, 0);
              if (paramzzatg != null)
              {
                localParcel1.writeInt(1);
                paramzzatg.writeToParcel(localParcel1, 0);
                this.zzrj.transact(2, localParcel1, localParcel2, 0);
                localParcel2.readException();
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            localParcel1.writeInt(0);
          }
          finally
          {
            localParcel2.recycle();
            localParcel1.recycle();
          }
        }
      }
      
      /* Error */
      public byte[] zza(zzatt paramzzatt, String paramString)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 32
        //   12: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_1
        //   16: ifnull +57 -> 73
        //   19: aload_3
        //   20: iconst_1
        //   21: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   24: aload_1
        //   25: aload_3
        //   26: iconst_0
        //   27: invokevirtual 95	com/google/android/gms/internal/zzatt:writeToParcel	(Landroid/os/Parcel;I)V
        //   30: aload_3
        //   31: aload_2
        //   32: invokevirtual 75	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   35: aload_0
        //   36: getfield 18	com/google/android/gms/internal/zzatw$zza$zza:zzrj	Landroid/os/IBinder;
        //   39: bipush 9
        //   41: aload_3
        //   42: aload 4
        //   44: iconst_0
        //   45: invokeinterface 52 5 0
        //   50: pop
        //   51: aload 4
        //   53: invokevirtual 55	android/os/Parcel:readException	()V
        //   56: aload 4
        //   58: invokevirtual 103	android/os/Parcel:createByteArray	()[B
        //   61: astore_1
        //   62: aload 4
        //   64: invokevirtual 68	android/os/Parcel:recycle	()V
        //   67: aload_3
        //   68: invokevirtual 68	android/os/Parcel:recycle	()V
        //   71: aload_1
        //   72: areturn
        //   73: aload_3
        //   74: iconst_0
        //   75: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   78: goto -48 -> 30
        //   81: astore_1
        //   82: aload 4
        //   84: invokevirtual 68	android/os/Parcel:recycle	()V
        //   87: aload_3
        //   88: invokevirtual 68	android/os/Parcel:recycle	()V
        //   91: aload_1
        //   92: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	93	0	this	zza
        //   0	93	1	paramzzatt	zzatt
        //   0	93	2	paramString	String
        //   3	85	3	localParcel1	Parcel
        //   7	76	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	15	81	finally
        //   19	30	81	finally
        //   30	62	81	finally
        //   73	78	81	finally
      }
      
      /* Error */
      public void zzb(zzatg paramzzatg)
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
        //   18: aload_2
        //   19: iconst_1
        //   20: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   23: aload_1
        //   24: aload_2
        //   25: iconst_0
        //   26: invokevirtual 46	com/google/android/gms/internal/zzatg:writeToParcel	(Landroid/os/Parcel;I)V
        //   29: aload_0
        //   30: getfield 18	com/google/android/gms/internal/zzatw$zza$zza:zzrj	Landroid/os/IBinder;
        //   33: bipush 6
        //   35: aload_2
        //   36: aload_3
        //   37: iconst_0
        //   38: invokeinterface 52 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 55	android/os/Parcel:readException	()V
        //   48: aload_3
        //   49: invokevirtual 68	android/os/Parcel:recycle	()V
        //   52: aload_2
        //   53: invokevirtual 68	android/os/Parcel:recycle	()V
        //   56: return
        //   57: aload_2
        //   58: iconst_0
        //   59: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   62: goto -33 -> 29
        //   65: astore_1
        //   66: aload_3
        //   67: invokevirtual 68	android/os/Parcel:recycle	()V
        //   70: aload_2
        //   71: invokevirtual 68	android/os/Parcel:recycle	()V
        //   74: aload_1
        //   75: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	76	0	this	zza
        //   0	76	1	paramzzatg	zzatg
        //   3	68	2	localParcel1	Parcel
        //   7	60	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	65	finally
        //   18	29	65	finally
        //   29	48	65	finally
        //   57	62	65	finally
      }
      
      /* Error */
      public void zzb(zzatj paramzzatj)
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
        //   18: aload_2
        //   19: iconst_1
        //   20: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   23: aload_1
        //   24: aload_2
        //   25: iconst_0
        //   26: invokevirtual 91	com/google/android/gms/internal/zzatj:writeToParcel	(Landroid/os/Parcel;I)V
        //   29: aload_0
        //   30: getfield 18	com/google/android/gms/internal/zzatw$zza$zza:zzrj	Landroid/os/IBinder;
        //   33: bipush 13
        //   35: aload_2
        //   36: aload_3
        //   37: iconst_0
        //   38: invokeinterface 52 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 55	android/os/Parcel:readException	()V
        //   48: aload_3
        //   49: invokevirtual 68	android/os/Parcel:recycle	()V
        //   52: aload_2
        //   53: invokevirtual 68	android/os/Parcel:recycle	()V
        //   56: return
        //   57: aload_2
        //   58: iconst_0
        //   59: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   62: goto -33 -> 29
        //   65: astore_1
        //   66: aload_3
        //   67: invokevirtual 68	android/os/Parcel:recycle	()V
        //   70: aload_2
        //   71: invokevirtual 68	android/os/Parcel:recycle	()V
        //   74: aload_1
        //   75: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	76	0	this	zza
        //   0	76	1	paramzzatj	zzatj
        //   3	68	2	localParcel1	Parcel
        //   7	60	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	65	finally
        //   18	29	65	finally
        //   29	48	65	finally
        //   57	62	65	finally
      }
      
      /* Error */
      public String zzc(zzatg paramzzatg)
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
        //   15: ifnull +48 -> 63
        //   18: aload_2
        //   19: iconst_1
        //   20: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   23: aload_1
        //   24: aload_2
        //   25: iconst_0
        //   26: invokevirtual 46	com/google/android/gms/internal/zzatg:writeToParcel	(Landroid/os/Parcel;I)V
        //   29: aload_0
        //   30: getfield 18	com/google/android/gms/internal/zzatw$zza$zza:zzrj	Landroid/os/IBinder;
        //   33: bipush 11
        //   35: aload_2
        //   36: aload_3
        //   37: iconst_0
        //   38: invokeinterface 52 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 55	android/os/Parcel:readException	()V
        //   48: aload_3
        //   49: invokevirtual 111	android/os/Parcel:readString	()Ljava/lang/String;
        //   52: astore_1
        //   53: aload_3
        //   54: invokevirtual 68	android/os/Parcel:recycle	()V
        //   57: aload_2
        //   58: invokevirtual 68	android/os/Parcel:recycle	()V
        //   61: aload_1
        //   62: areturn
        //   63: aload_2
        //   64: iconst_0
        //   65: invokevirtual 40	android/os/Parcel:writeInt	(I)V
        //   68: goto -39 -> 29
        //   71: astore_1
        //   72: aload_3
        //   73: invokevirtual 68	android/os/Parcel:recycle	()V
        //   76: aload_2
        //   77: invokevirtual 68	android/os/Parcel:recycle	()V
        //   80: aload_1
        //   81: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	82	0	this	zza
        //   0	82	1	paramzzatg	zzatg
        //   3	74	2	localParcel1	Parcel
        //   7	66	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	71	finally
        //   18	29	71	finally
        //   29	53	71	finally
        //   63	68	71	finally
      }
      
      public List<zzatj> zzl(String paramString1, String paramString2, String paramString3)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.google.android.gms.measurement.internal.IMeasurementService");
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          localParcel1.writeString(paramString3);
          this.zzrj.transact(17, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramString1 = localParcel2.createTypedArrayList(zzatj.CREATOR);
          return paramString1;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzatw.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */