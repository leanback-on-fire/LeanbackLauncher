package com.google.android.gtalkservice;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IGTalkConnectionListener
  extends IInterface
{
  public abstract void onConnectionCreated(IGTalkConnection paramIGTalkConnection)
    throws RemoteException;
  
  public abstract void onConnectionCreationFailed(String paramString)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IGTalkConnectionListener
  {
    private static final String DESCRIPTOR = "com.google.android.gtalkservice.IGTalkConnectionListener";
    static final int TRANSACTION_onConnectionCreated = 1;
    static final int TRANSACTION_onConnectionCreationFailed = 2;
    
    public Stub()
    {
      attachInterface(this, "com.google.android.gtalkservice.IGTalkConnectionListener");
    }
    
    public static IGTalkConnectionListener asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("com.google.android.gtalkservice.IGTalkConnectionListener");
      if ((localIInterface != null) && ((localIInterface instanceof IGTalkConnectionListener))) {
        return (IGTalkConnectionListener)localIInterface;
      }
      return new Proxy(paramIBinder);
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
        paramParcel2.writeString("com.google.android.gtalkservice.IGTalkConnectionListener");
        return true;
      case 1: 
        paramParcel1.enforceInterface("com.google.android.gtalkservice.IGTalkConnectionListener");
        onConnectionCreated(IGTalkConnection.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      }
      paramParcel1.enforceInterface("com.google.android.gtalkservice.IGTalkConnectionListener");
      onConnectionCreationFailed(paramParcel1.readString());
      paramParcel2.writeNoException();
      return true;
    }
    
    private static class Proxy
      implements IGTalkConnectionListener
    {
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      public IBinder asBinder()
      {
        return this.mRemote;
      }
      
      public String getInterfaceDescriptor()
      {
        return "com.google.android.gtalkservice.IGTalkConnectionListener";
      }
      
      /* Error */
      public void onConnectionCreated(IGTalkConnection paramIGTalkConnection)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore_3
        //   8: aload_2
        //   9: ldc 26
        //   11: invokevirtual 40	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload_1
        //   15: ifnull +42 -> 57
        //   18: aload_1
        //   19: invokeinterface 44 1 0
        //   24: astore_1
        //   25: aload_2
        //   26: aload_1
        //   27: invokevirtual 47	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   30: aload_0
        //   31: getfield 19	com/google/android/gtalkservice/IGTalkConnectionListener$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   34: iconst_1
        //   35: aload_2
        //   36: aload_3
        //   37: iconst_0
        //   38: invokeinterface 53 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 56	android/os/Parcel:readException	()V
        //   48: aload_3
        //   49: invokevirtual 59	android/os/Parcel:recycle	()V
        //   52: aload_2
        //   53: invokevirtual 59	android/os/Parcel:recycle	()V
        //   56: return
        //   57: aconst_null
        //   58: astore_1
        //   59: goto -34 -> 25
        //   62: astore_1
        //   63: aload_3
        //   64: invokevirtual 59	android/os/Parcel:recycle	()V
        //   67: aload_2
        //   68: invokevirtual 59	android/os/Parcel:recycle	()V
        //   71: aload_1
        //   72: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	73	0	this	Proxy
        //   0	73	1	paramIGTalkConnection	IGTalkConnection
        //   3	65	2	localParcel1	Parcel
        //   7	57	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	62	finally
        //   18	25	62	finally
        //   25	48	62	finally
      }
      
      public void onConnectionCreationFailed(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.google.android.gtalkservice.IGTalkConnectionListener");
          localParcel1.writeString(paramString);
          this.mRemote.transact(2, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
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


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gtalkservice/IGTalkConnectionListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */