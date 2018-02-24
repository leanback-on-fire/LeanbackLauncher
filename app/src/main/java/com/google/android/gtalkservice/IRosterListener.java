package com.google.android.gtalkservice;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IRosterListener
  extends IInterface
{
  public abstract void presenceChanged(String paramString)
    throws RemoteException;
  
  public abstract void rosterChanged()
    throws RemoteException;
  
  public abstract void selfPresenceChanged()
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IRosterListener
  {
    private static final String DESCRIPTOR = "com.google.android.gtalkservice.IRosterListener";
    static final int TRANSACTION_presenceChanged = 2;
    static final int TRANSACTION_rosterChanged = 1;
    static final int TRANSACTION_selfPresenceChanged = 3;
    
    public Stub()
    {
      attachInterface(this, "com.google.android.gtalkservice.IRosterListener");
    }
    
    public static IRosterListener asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("com.google.android.gtalkservice.IRosterListener");
      if ((localIInterface != null) && ((localIInterface instanceof IRosterListener))) {
        return (IRosterListener)localIInterface;
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
        paramParcel2.writeString("com.google.android.gtalkservice.IRosterListener");
        return true;
      case 1: 
        paramParcel1.enforceInterface("com.google.android.gtalkservice.IRosterListener");
        rosterChanged();
        paramParcel2.writeNoException();
        return true;
      case 2: 
        paramParcel1.enforceInterface("com.google.android.gtalkservice.IRosterListener");
        presenceChanged(paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      }
      paramParcel1.enforceInterface("com.google.android.gtalkservice.IRosterListener");
      selfPresenceChanged();
      paramParcel2.writeNoException();
      return true;
    }
    
    private static class Proxy
      implements IRosterListener
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
        return "com.google.android.gtalkservice.IRosterListener";
      }
      
      public void presenceChanged(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.google.android.gtalkservice.IRosterListener");
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
      
      public void rosterChanged()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.google.android.gtalkservice.IRosterListener");
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
      
      public void selfPresenceChanged()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.google.android.gtalkservice.IRosterListener");
          this.mRemote.transact(3, localParcel1, localParcel2, 0);
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


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gtalkservice/IRosterListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */