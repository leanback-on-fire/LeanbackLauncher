package com.google.android.gtalkservice;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;

public abstract interface IGroupChatInvitationListener
  extends IInterface
{
  public abstract boolean onInvitationReceived(GroupChatInvitation paramGroupChatInvitation)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IGroupChatInvitationListener
  {
    private static final String DESCRIPTOR = "com.google.android.gtalkservice.IGroupChatInvitationListener";
    static final int TRANSACTION_onInvitationReceived = 1;
    
    public Stub()
    {
      attachInterface(this, "com.google.android.gtalkservice.IGroupChatInvitationListener");
    }
    
    public static IGroupChatInvitationListener asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("com.google.android.gtalkservice.IGroupChatInvitationListener");
      if ((localIInterface != null) && ((localIInterface instanceof IGroupChatInvitationListener))) {
        return (IGroupChatInvitationListener)localIInterface;
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
        paramParcel2.writeString("com.google.android.gtalkservice.IGroupChatInvitationListener");
        return true;
      }
      paramParcel1.enforceInterface("com.google.android.gtalkservice.IGroupChatInvitationListener");
      if (paramParcel1.readInt() != 0)
      {
        paramParcel1 = (GroupChatInvitation)GroupChatInvitation.CREATOR.createFromParcel(paramParcel1);
        boolean bool = onInvitationReceived(paramParcel1);
        paramParcel2.writeNoException();
        if (!bool) {
          break label102;
        }
      }
      label102:
      for (paramInt1 = 1;; paramInt1 = 0)
      {
        paramParcel2.writeInt(paramInt1);
        return true;
        paramParcel1 = null;
        break;
      }
    }
    
    private static class Proxy
      implements IGroupChatInvitationListener
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
        return "com.google.android.gtalkservice.IGroupChatInvitationListener";
      }
      
      public boolean onInvitationReceived(GroupChatInvitation paramGroupChatInvitation)
        throws RemoteException
      {
        boolean bool = true;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        for (;;)
        {
          try
          {
            localParcel1.writeInterfaceToken("com.google.android.gtalkservice.IGroupChatInvitationListener");
            if (paramGroupChatInvitation != null)
            {
              localParcel1.writeInt(1);
              paramGroupChatInvitation.writeToParcel(localParcel1, 0);
              this.mRemote.transact(1, localParcel1, localParcel2, 0);
              localParcel2.readException();
              int i = localParcel2.readInt();
              if (i != 0) {
                return bool;
              }
            }
            else
            {
              localParcel1.writeInt(0);
              continue;
            }
            bool = false;
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
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gtalkservice/IGroupChatInvitationListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */