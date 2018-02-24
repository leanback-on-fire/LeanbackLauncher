package com.google.android.gtalkservice;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IGTalkConnection
  extends IInterface
{
  public abstract void clearConnectionStatistics()
    throws RemoteException;
  
  public abstract IImSession createImSession()
    throws RemoteException;
  
  public abstract int getConnectionUptime()
    throws RemoteException;
  
  public abstract IImSession getDefaultImSession()
    throws RemoteException;
  
  public abstract String getDeviceId()
    throws RemoteException;
  
  public abstract IImSession getImSessionForAccountId(long paramLong)
    throws RemoteException;
  
  public abstract String getJid()
    throws RemoteException;
  
  public abstract long getLastActivityFromServerTime()
    throws RemoteException;
  
  public abstract long getLastActivityToServerTime()
    throws RemoteException;
  
  public abstract int getNumberOfConnectionsAttempted()
    throws RemoteException;
  
  public abstract int getNumberOfConnectionsMade()
    throws RemoteException;
  
  public abstract String getUsername()
    throws RemoteException;
  
  public abstract boolean isConnected()
    throws RemoteException;
  
  public abstract void sendHeartbeat()
    throws RemoteException;
  
  public abstract void sendHttpRequest(byte[] paramArrayOfByte, IHttpRequestCallback paramIHttpRequestCallback)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IGTalkConnection
  {
    private static final String DESCRIPTOR = "com.google.android.gtalkservice.IGTalkConnection";
    static final int TRANSACTION_clearConnectionStatistics = 13;
    static final int TRANSACTION_createImSession = 5;
    static final int TRANSACTION_getConnectionUptime = 12;
    static final int TRANSACTION_getDefaultImSession = 7;
    static final int TRANSACTION_getDeviceId = 3;
    static final int TRANSACTION_getImSessionForAccountId = 6;
    static final int TRANSACTION_getJid = 2;
    static final int TRANSACTION_getLastActivityFromServerTime = 8;
    static final int TRANSACTION_getLastActivityToServerTime = 9;
    static final int TRANSACTION_getNumberOfConnectionsAttempted = 11;
    static final int TRANSACTION_getNumberOfConnectionsMade = 10;
    static final int TRANSACTION_getUsername = 1;
    static final int TRANSACTION_isConnected = 4;
    static final int TRANSACTION_sendHeartbeat = 15;
    static final int TRANSACTION_sendHttpRequest = 14;
    
    public Stub()
    {
      attachInterface(this, "com.google.android.gtalkservice.IGTalkConnection");
    }
    
    public static IGTalkConnection asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("com.google.android.gtalkservice.IGTalkConnection");
      if ((localIInterface != null) && ((localIInterface instanceof IGTalkConnection))) {
        return (IGTalkConnection)localIInterface;
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
      IImSession localIImSession2 = null;
      Object localObject = null;
      IImSession localIImSession1 = null;
      long l;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("com.google.android.gtalkservice.IGTalkConnection");
        return true;
      case 1: 
        paramParcel1.enforceInterface("com.google.android.gtalkservice.IGTalkConnection");
        paramParcel1 = getUsername();
        paramParcel2.writeNoException();
        paramParcel2.writeString(paramParcel1);
        return true;
      case 2: 
        paramParcel1.enforceInterface("com.google.android.gtalkservice.IGTalkConnection");
        paramParcel1 = getJid();
        paramParcel2.writeNoException();
        paramParcel2.writeString(paramParcel1);
        return true;
      case 3: 
        paramParcel1.enforceInterface("com.google.android.gtalkservice.IGTalkConnection");
        paramParcel1 = getDeviceId();
        paramParcel2.writeNoException();
        paramParcel2.writeString(paramParcel1);
        return true;
      case 4: 
        paramParcel1.enforceInterface("com.google.android.gtalkservice.IGTalkConnection");
        boolean bool = isConnected();
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 5: 
        paramParcel1.enforceInterface("com.google.android.gtalkservice.IGTalkConnection");
        localIImSession2 = createImSession();
        paramParcel2.writeNoException();
        paramParcel1 = localIImSession1;
        if (localIImSession2 != null) {
          paramParcel1 = localIImSession2.asBinder();
        }
        paramParcel2.writeStrongBinder(paramParcel1);
        return true;
      case 6: 
        paramParcel1.enforceInterface("com.google.android.gtalkservice.IGTalkConnection");
        localIImSession1 = getImSessionForAccountId(paramParcel1.readLong());
        paramParcel2.writeNoException();
        paramParcel1 = localIImSession2;
        if (localIImSession1 != null) {
          paramParcel1 = localIImSession1.asBinder();
        }
        paramParcel2.writeStrongBinder(paramParcel1);
        return true;
      case 7: 
        paramParcel1.enforceInterface("com.google.android.gtalkservice.IGTalkConnection");
        localIImSession1 = getDefaultImSession();
        paramParcel2.writeNoException();
        paramParcel1 = (Parcel)localObject;
        if (localIImSession1 != null) {
          paramParcel1 = localIImSession1.asBinder();
        }
        paramParcel2.writeStrongBinder(paramParcel1);
        return true;
      case 8: 
        paramParcel1.enforceInterface("com.google.android.gtalkservice.IGTalkConnection");
        l = getLastActivityFromServerTime();
        paramParcel2.writeNoException();
        paramParcel2.writeLong(l);
        return true;
      case 9: 
        paramParcel1.enforceInterface("com.google.android.gtalkservice.IGTalkConnection");
        l = getLastActivityToServerTime();
        paramParcel2.writeNoException();
        paramParcel2.writeLong(l);
        return true;
      case 10: 
        paramParcel1.enforceInterface("com.google.android.gtalkservice.IGTalkConnection");
        paramInt1 = getNumberOfConnectionsMade();
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 11: 
        paramParcel1.enforceInterface("com.google.android.gtalkservice.IGTalkConnection");
        paramInt1 = getNumberOfConnectionsAttempted();
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 12: 
        paramParcel1.enforceInterface("com.google.android.gtalkservice.IGTalkConnection");
        paramInt1 = getConnectionUptime();
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        return true;
      case 13: 
        paramParcel1.enforceInterface("com.google.android.gtalkservice.IGTalkConnection");
        clearConnectionStatistics();
        return true;
      case 14: 
        paramParcel1.enforceInterface("com.google.android.gtalkservice.IGTalkConnection");
        sendHttpRequest(paramParcel1.createByteArray(), IHttpRequestCallback.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      }
      paramParcel1.enforceInterface("com.google.android.gtalkservice.IGTalkConnection");
      sendHeartbeat();
      paramParcel2.writeNoException();
      return true;
    }
    
    private static class Proxy
      implements IGTalkConnection
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
      
      public void clearConnectionStatistics()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("com.google.android.gtalkservice.IGTalkConnection");
          this.mRemote.transact(13, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public IImSession createImSession()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.google.android.gtalkservice.IGTalkConnection");
          this.mRemote.transact(5, localParcel1, localParcel2, 0);
          localParcel2.readException();
          IImSession localIImSession = IImSession.Stub.asInterface(localParcel2.readStrongBinder());
          return localIImSession;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public int getConnectionUptime()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.google.android.gtalkservice.IGTalkConnection");
          this.mRemote.transact(12, localParcel1, localParcel2, 0);
          localParcel2.readException();
          int i = localParcel2.readInt();
          return i;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public IImSession getDefaultImSession()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.google.android.gtalkservice.IGTalkConnection");
          this.mRemote.transact(7, localParcel1, localParcel2, 0);
          localParcel2.readException();
          IImSession localIImSession = IImSession.Stub.asInterface(localParcel2.readStrongBinder());
          return localIImSession;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public String getDeviceId()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.google.android.gtalkservice.IGTalkConnection");
          this.mRemote.transact(3, localParcel1, localParcel2, 0);
          localParcel2.readException();
          String str = localParcel2.readString();
          return str;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public IImSession getImSessionForAccountId(long paramLong)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.google.android.gtalkservice.IGTalkConnection");
          localParcel1.writeLong(paramLong);
          this.mRemote.transact(6, localParcel1, localParcel2, 0);
          localParcel2.readException();
          IImSession localIImSession = IImSession.Stub.asInterface(localParcel2.readStrongBinder());
          return localIImSession;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public String getInterfaceDescriptor()
      {
        return "com.google.android.gtalkservice.IGTalkConnection";
      }
      
      public String getJid()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.google.android.gtalkservice.IGTalkConnection");
          this.mRemote.transact(2, localParcel1, localParcel2, 0);
          localParcel2.readException();
          String str = localParcel2.readString();
          return str;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public long getLastActivityFromServerTime()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.google.android.gtalkservice.IGTalkConnection");
          this.mRemote.transact(8, localParcel1, localParcel2, 0);
          localParcel2.readException();
          long l = localParcel2.readLong();
          return l;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public long getLastActivityToServerTime()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.google.android.gtalkservice.IGTalkConnection");
          this.mRemote.transact(9, localParcel1, localParcel2, 0);
          localParcel2.readException();
          long l = localParcel2.readLong();
          return l;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public int getNumberOfConnectionsAttempted()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.google.android.gtalkservice.IGTalkConnection");
          this.mRemote.transact(11, localParcel1, localParcel2, 0);
          localParcel2.readException();
          int i = localParcel2.readInt();
          return i;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public int getNumberOfConnectionsMade()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.google.android.gtalkservice.IGTalkConnection");
          this.mRemote.transact(10, localParcel1, localParcel2, 0);
          localParcel2.readException();
          int i = localParcel2.readInt();
          return i;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public String getUsername()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.google.android.gtalkservice.IGTalkConnection");
          this.mRemote.transact(1, localParcel1, localParcel2, 0);
          localParcel2.readException();
          String str = localParcel2.readString();
          return str;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public boolean isConnected()
        throws RemoteException
      {
        boolean bool = false;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.google.android.gtalkservice.IGTalkConnection");
          this.mRemote.transact(4, localParcel1, localParcel2, 0);
          localParcel2.readException();
          int i = localParcel2.readInt();
          if (i != 0) {
            bool = true;
          }
          return bool;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void sendHeartbeat()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.google.android.gtalkservice.IGTalkConnection");
          this.mRemote.transact(15, localParcel1, localParcel2, 0);
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
      public void sendHttpRequest(byte[] paramArrayOfByte, IHttpRequestCallback paramIHttpRequestCallback)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_3
        //   4: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore 4
        //   9: aload_3
        //   10: ldc 33
        //   12: invokevirtual 37	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload_3
        //   16: aload_1
        //   17: invokevirtual 98	android/os/Parcel:writeByteArray	([B)V
        //   20: aload_2
        //   21: ifnull +46 -> 67
        //   24: aload_2
        //   25: invokeinterface 102 1 0
        //   30: astore_1
        //   31: aload_3
        //   32: aload_1
        //   33: invokevirtual 105	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   36: aload_0
        //   37: getfield 19	com/google/android/gtalkservice/IGTalkConnection$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   40: bipush 14
        //   42: aload_3
        //   43: aload 4
        //   45: iconst_0
        //   46: invokeinterface 43 5 0
        //   51: pop
        //   52: aload 4
        //   54: invokevirtual 52	android/os/Parcel:readException	()V
        //   57: aload 4
        //   59: invokevirtual 46	android/os/Parcel:recycle	()V
        //   62: aload_3
        //   63: invokevirtual 46	android/os/Parcel:recycle	()V
        //   66: return
        //   67: aconst_null
        //   68: astore_1
        //   69: goto -38 -> 31
        //   72: astore_1
        //   73: aload 4
        //   75: invokevirtual 46	android/os/Parcel:recycle	()V
        //   78: aload_3
        //   79: invokevirtual 46	android/os/Parcel:recycle	()V
        //   82: aload_1
        //   83: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	84	0	this	Proxy
        //   0	84	1	paramArrayOfByte	byte[]
        //   0	84	2	paramIHttpRequestCallback	IHttpRequestCallback
        //   3	76	3	localParcel1	Parcel
        //   7	67	4	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   9	20	72	finally
        //   24	31	72	finally
        //   31	57	72	finally
      }
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gtalkservice/IGTalkConnection.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */