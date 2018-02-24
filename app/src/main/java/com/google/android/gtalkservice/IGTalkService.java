package com.google.android.gtalkservice;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import java.util.ArrayList;
import java.util.List;

public abstract interface IGTalkService
  extends IInterface
{
  public abstract void createGTalkConnection(String paramString, IGTalkConnectionListener paramIGTalkConnectionListener)
    throws RemoteException;
  
  public abstract void dismissAllNotifications()
    throws RemoteException;
  
  public abstract void dismissNotificationFor(String paramString, long paramLong)
    throws RemoteException;
  
  public abstract void dismissNotificationsForAccount(long paramLong)
    throws RemoteException;
  
  public abstract List getActiveConnections()
    throws RemoteException;
  
  public abstract IGTalkConnection getConnectionForUser(String paramString)
    throws RemoteException;
  
  public abstract IGTalkConnection getDefaultConnection()
    throws RemoteException;
  
  public abstract boolean getDeviceStorageLow()
    throws RemoteException;
  
  public abstract IImSession getImSessionForAccountId(long paramLong)
    throws RemoteException;
  
  public abstract String printDiagnostics()
    throws RemoteException;
  
  public abstract void setTalkForegroundState()
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IGTalkService
  {
    private static final String DESCRIPTOR = "com.google.android.gtalkservice.IGTalkService";
    static final int TRANSACTION_createGTalkConnection = 1;
    static final int TRANSACTION_dismissAllNotifications = 6;
    static final int TRANSACTION_dismissNotificationFor = 8;
    static final int TRANSACTION_dismissNotificationsForAccount = 7;
    static final int TRANSACTION_getActiveConnections = 2;
    static final int TRANSACTION_getConnectionForUser = 3;
    static final int TRANSACTION_getDefaultConnection = 4;
    static final int TRANSACTION_getDeviceStorageLow = 9;
    static final int TRANSACTION_getImSessionForAccountId = 5;
    static final int TRANSACTION_printDiagnostics = 10;
    static final int TRANSACTION_setTalkForegroundState = 11;
    
    public Stub()
    {
      attachInterface(this, "com.google.android.gtalkservice.IGTalkService");
    }
    
    public static IGTalkService asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("com.google.android.gtalkservice.IGTalkService");
      if ((localIInterface != null) && ((localIInterface instanceof IGTalkService))) {
        return (IGTalkService)localIInterface;
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
      IGTalkConnection localIGTalkConnection = null;
      Object localObject2 = null;
      Object localObject1 = null;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("com.google.android.gtalkservice.IGTalkService");
        return true;
      case 1: 
        paramParcel1.enforceInterface("com.google.android.gtalkservice.IGTalkService");
        createGTalkConnection(paramParcel1.readString(), IGTalkConnectionListener.Stub.asInterface(paramParcel1.readStrongBinder()));
        return true;
      case 2: 
        paramParcel1.enforceInterface("com.google.android.gtalkservice.IGTalkService");
        paramParcel1 = getActiveConnections();
        paramParcel2.writeNoException();
        paramParcel2.writeList(paramParcel1);
        return true;
      case 3: 
        paramParcel1.enforceInterface("com.google.android.gtalkservice.IGTalkService");
        localIGTalkConnection = getConnectionForUser(paramParcel1.readString());
        paramParcel2.writeNoException();
        paramParcel1 = (Parcel)localObject1;
        if (localIGTalkConnection != null) {
          paramParcel1 = localIGTalkConnection.asBinder();
        }
        paramParcel2.writeStrongBinder(paramParcel1);
        return true;
      case 4: 
        paramParcel1.enforceInterface("com.google.android.gtalkservice.IGTalkService");
        localObject1 = getDefaultConnection();
        paramParcel2.writeNoException();
        paramParcel1 = localIGTalkConnection;
        if (localObject1 != null) {
          paramParcel1 = ((IGTalkConnection)localObject1).asBinder();
        }
        paramParcel2.writeStrongBinder(paramParcel1);
        return true;
      case 5: 
        paramParcel1.enforceInterface("com.google.android.gtalkservice.IGTalkService");
        localObject1 = getImSessionForAccountId(paramParcel1.readLong());
        paramParcel2.writeNoException();
        paramParcel1 = (Parcel)localObject2;
        if (localObject1 != null) {
          paramParcel1 = ((IImSession)localObject1).asBinder();
        }
        paramParcel2.writeStrongBinder(paramParcel1);
        return true;
      case 6: 
        paramParcel1.enforceInterface("com.google.android.gtalkservice.IGTalkService");
        dismissAllNotifications();
        return true;
      case 7: 
        paramParcel1.enforceInterface("com.google.android.gtalkservice.IGTalkService");
        dismissNotificationsForAccount(paramParcel1.readLong());
        return true;
      case 8: 
        paramParcel1.enforceInterface("com.google.android.gtalkservice.IGTalkService");
        dismissNotificationFor(paramParcel1.readString(), paramParcel1.readLong());
        return true;
      case 9: 
        paramParcel1.enforceInterface("com.google.android.gtalkservice.IGTalkService");
        boolean bool = getDeviceStorageLow();
        paramParcel2.writeNoException();
        if (bool) {}
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          paramParcel2.writeInt(paramInt1);
          return true;
        }
      case 10: 
        paramParcel1.enforceInterface("com.google.android.gtalkservice.IGTalkService");
        paramParcel1 = printDiagnostics();
        paramParcel2.writeNoException();
        paramParcel2.writeString(paramParcel1);
        return true;
      }
      paramParcel1.enforceInterface("com.google.android.gtalkservice.IGTalkService");
      setTalkForegroundState();
      return true;
    }
    
    private static class Proxy
      implements IGTalkService
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
      
      public void createGTalkConnection(String paramString, IGTalkConnectionListener paramIGTalkConnectionListener)
        throws RemoteException
      {
        Object localObject = null;
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("com.google.android.gtalkservice.IGTalkService");
          localParcel.writeString(paramString);
          paramString = (String)localObject;
          if (paramIGTalkConnectionListener != null) {
            paramString = paramIGTalkConnectionListener.asBinder();
          }
          localParcel.writeStrongBinder(paramString);
          this.mRemote.transact(1, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void dismissAllNotifications()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("com.google.android.gtalkservice.IGTalkService");
          this.mRemote.transact(6, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void dismissNotificationFor(String paramString, long paramLong)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("com.google.android.gtalkservice.IGTalkService");
          localParcel.writeString(paramString);
          localParcel.writeLong(paramLong);
          this.mRemote.transact(8, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void dismissNotificationsForAccount(long paramLong)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("com.google.android.gtalkservice.IGTalkService");
          localParcel.writeLong(paramLong);
          this.mRemote.transact(7, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public List getActiveConnections()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.google.android.gtalkservice.IGTalkService");
          this.mRemote.transact(2, localParcel1, localParcel2, 0);
          localParcel2.readException();
          ArrayList localArrayList = localParcel2.readArrayList(getClass().getClassLoader());
          return localArrayList;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public IGTalkConnection getConnectionForUser(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.google.android.gtalkservice.IGTalkService");
          localParcel1.writeString(paramString);
          this.mRemote.transact(3, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramString = IGTalkConnection.Stub.asInterface(localParcel2.readStrongBinder());
          return paramString;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public IGTalkConnection getDefaultConnection()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.google.android.gtalkservice.IGTalkService");
          this.mRemote.transact(4, localParcel1, localParcel2, 0);
          localParcel2.readException();
          IGTalkConnection localIGTalkConnection = IGTalkConnection.Stub.asInterface(localParcel2.readStrongBinder());
          return localIGTalkConnection;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public boolean getDeviceStorageLow()
        throws RemoteException
      {
        boolean bool = false;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.google.android.gtalkservice.IGTalkService");
          this.mRemote.transact(9, localParcel1, localParcel2, 0);
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
      
      public IImSession getImSessionForAccountId(long paramLong)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.google.android.gtalkservice.IGTalkService");
          localParcel1.writeLong(paramLong);
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
      
      public String getInterfaceDescriptor()
      {
        return "com.google.android.gtalkservice.IGTalkService";
      }
      
      public String printDiagnostics()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.google.android.gtalkservice.IGTalkService");
          this.mRemote.transact(10, localParcel1, localParcel2, 0);
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
      
      public void setTalkForegroundState()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("com.google.android.gtalkservice.IGTalkService");
          this.mRemote.transact(11, localParcel, null, 1);
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


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gtalkservice/IGTalkService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */