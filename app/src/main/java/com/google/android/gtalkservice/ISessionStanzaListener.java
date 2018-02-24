package com.google.android.gtalkservice;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface ISessionStanzaListener
  extends IInterface
{
  public abstract long getAccountId()
    throws RemoteException;
  
  public abstract void onStanzaReceived(String paramString1, String paramString2)
    throws RemoteException;
  
  public abstract void onStanzaResponse(String paramString1, String paramString2, String paramString3)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements ISessionStanzaListener
  {
    private static final String DESCRIPTOR = "com.google.android.gtalkservice.ISessionStanzaListener";
    static final int TRANSACTION_getAccountId = 3;
    static final int TRANSACTION_onStanzaReceived = 1;
    static final int TRANSACTION_onStanzaResponse = 2;
    
    public Stub()
    {
      attachInterface(this, "com.google.android.gtalkservice.ISessionStanzaListener");
    }
    
    public static ISessionStanzaListener asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("com.google.android.gtalkservice.ISessionStanzaListener");
      if ((localIInterface != null) && ((localIInterface instanceof ISessionStanzaListener))) {
        return (ISessionStanzaListener)localIInterface;
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
        paramParcel2.writeString("com.google.android.gtalkservice.ISessionStanzaListener");
        return true;
      case 1: 
        paramParcel1.enforceInterface("com.google.android.gtalkservice.ISessionStanzaListener");
        onStanzaReceived(paramParcel1.readString(), paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 2: 
        paramParcel1.enforceInterface("com.google.android.gtalkservice.ISessionStanzaListener");
        onStanzaResponse(paramParcel1.readString(), paramParcel1.readString(), paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      }
      paramParcel1.enforceInterface("com.google.android.gtalkservice.ISessionStanzaListener");
      long l = getAccountId();
      paramParcel2.writeNoException();
      paramParcel2.writeLong(l);
      return true;
    }
    
    private static class Proxy
      implements ISessionStanzaListener
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
      
      public long getAccountId()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.google.android.gtalkservice.ISessionStanzaListener");
          this.mRemote.transact(3, localParcel1, localParcel2, 0);
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
      
      public String getInterfaceDescriptor()
      {
        return "com.google.android.gtalkservice.ISessionStanzaListener";
      }
      
      public void onStanzaReceived(String paramString1, String paramString2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.google.android.gtalkservice.ISessionStanzaListener");
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          this.mRemote.transact(1, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void onStanzaResponse(String paramString1, String paramString2, String paramString3)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.google.android.gtalkservice.ISessionStanzaListener");
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          localParcel1.writeString(paramString3);
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


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gtalkservice/ISessionStanzaListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */