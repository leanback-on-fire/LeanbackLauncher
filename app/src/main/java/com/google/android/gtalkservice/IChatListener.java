package com.google.android.gtalkservice;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract interface IChatListener
  extends IInterface
{
  public abstract void callEnded()
    throws RemoteException;
  
  public abstract void chatClosed(String paramString)
    throws RemoteException;
  
  public abstract void chatRead(String paramString)
    throws RemoteException;
  
  public abstract void convertedToGroupChat(String paramString1, String paramString2, long paramLong)
    throws RemoteException;
  
  public abstract void missedCall()
    throws RemoteException;
  
  public abstract void newMessageReceived(String paramString1, String paramString2, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void newMessageSent(String paramString)
    throws RemoteException;
  
  public abstract void participantJoined(String paramString1, String paramString2)
    throws RemoteException;
  
  public abstract void participantLeft(String paramString1, String paramString2)
    throws RemoteException;
  
  public abstract boolean useLightweightNotify()
    throws RemoteException;
  
  public abstract void willConvertToGroupChat(String paramString1, String paramString2, long paramLong)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IChatListener
  {
    private static final String DESCRIPTOR = "com.google.android.gtalkservice.IChatListener";
    static final int TRANSACTION_callEnded = 4;
    static final int TRANSACTION_chatClosed = 6;
    static final int TRANSACTION_chatRead = 5;
    static final int TRANSACTION_convertedToGroupChat = 8;
    static final int TRANSACTION_missedCall = 3;
    static final int TRANSACTION_newMessageReceived = 1;
    static final int TRANSACTION_newMessageSent = 2;
    static final int TRANSACTION_participantJoined = 9;
    static final int TRANSACTION_participantLeft = 10;
    static final int TRANSACTION_useLightweightNotify = 11;
    static final int TRANSACTION_willConvertToGroupChat = 7;
    
    public Stub()
    {
      attachInterface(this, "com.google.android.gtalkservice.IChatListener");
    }
    
    public static IChatListener asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("com.google.android.gtalkservice.IChatListener");
      if ((localIInterface != null) && ((localIInterface instanceof IChatListener))) {
        return (IChatListener)localIInterface;
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
      int i = 0;
      boolean bool = false;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("com.google.android.gtalkservice.IChatListener");
        return true;
      case 1: 
        paramParcel1.enforceInterface("com.google.android.gtalkservice.IChatListener");
        String str1 = paramParcel1.readString();
        String str2 = paramParcel1.readString();
        if (paramParcel1.readInt() != 0) {
          bool = true;
        }
        newMessageReceived(str1, str2, bool);
        paramParcel2.writeNoException();
        return true;
      case 2: 
        paramParcel1.enforceInterface("com.google.android.gtalkservice.IChatListener");
        newMessageSent(paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 3: 
        paramParcel1.enforceInterface("com.google.android.gtalkservice.IChatListener");
        missedCall();
        paramParcel2.writeNoException();
        return true;
      case 4: 
        paramParcel1.enforceInterface("com.google.android.gtalkservice.IChatListener");
        callEnded();
        paramParcel2.writeNoException();
        return true;
      case 5: 
        paramParcel1.enforceInterface("com.google.android.gtalkservice.IChatListener");
        chatRead(paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 6: 
        paramParcel1.enforceInterface("com.google.android.gtalkservice.IChatListener");
        chatClosed(paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 7: 
        paramParcel1.enforceInterface("com.google.android.gtalkservice.IChatListener");
        willConvertToGroupChat(paramParcel1.readString(), paramParcel1.readString(), paramParcel1.readLong());
        paramParcel2.writeNoException();
        return true;
      case 8: 
        paramParcel1.enforceInterface("com.google.android.gtalkservice.IChatListener");
        convertedToGroupChat(paramParcel1.readString(), paramParcel1.readString(), paramParcel1.readLong());
        paramParcel2.writeNoException();
        return true;
      case 9: 
        paramParcel1.enforceInterface("com.google.android.gtalkservice.IChatListener");
        participantJoined(paramParcel1.readString(), paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 10: 
        paramParcel1.enforceInterface("com.google.android.gtalkservice.IChatListener");
        participantLeft(paramParcel1.readString(), paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      }
      paramParcel1.enforceInterface("com.google.android.gtalkservice.IChatListener");
      bool = useLightweightNotify();
      paramParcel2.writeNoException();
      paramInt1 = i;
      if (bool) {
        paramInt1 = 1;
      }
      paramParcel2.writeInt(paramInt1);
      return true;
    }
    
    private static class Proxy
      implements IChatListener
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
      
      public void callEnded()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.google.android.gtalkservice.IChatListener");
          this.mRemote.transact(4, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void chatClosed(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.google.android.gtalkservice.IChatListener");
          localParcel1.writeString(paramString);
          this.mRemote.transact(6, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void chatRead(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.google.android.gtalkservice.IChatListener");
          localParcel1.writeString(paramString);
          this.mRemote.transact(5, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void convertedToGroupChat(String paramString1, String paramString2, long paramLong)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.google.android.gtalkservice.IChatListener");
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          localParcel1.writeLong(paramLong);
          this.mRemote.transact(8, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public String getInterfaceDescriptor()
      {
        return "com.google.android.gtalkservice.IChatListener";
      }
      
      public void missedCall()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.google.android.gtalkservice.IChatListener");
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
      
      /* Error */
      public void newMessageReceived(String paramString1, String paramString2, boolean paramBoolean)
        throws RemoteException
      {
        // Byte code:
        //   0: iconst_1
        //   1: istore 4
        //   3: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   6: astore 5
        //   8: invokestatic 31	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   11: astore 6
        //   13: aload 5
        //   15: ldc 33
        //   17: invokevirtual 37	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   20: aload 5
        //   22: aload_1
        //   23: invokevirtual 54	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   26: aload 5
        //   28: aload_2
        //   29: invokevirtual 54	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   32: iload_3
        //   33: ifeq +42 -> 75
        //   36: aload 5
        //   38: iload 4
        //   40: invokevirtual 70	android/os/Parcel:writeInt	(I)V
        //   43: aload_0
        //   44: getfield 19	com/google/android/gtalkservice/IChatListener$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   47: iconst_1
        //   48: aload 5
        //   50: aload 6
        //   52: iconst_0
        //   53: invokeinterface 43 5 0
        //   58: pop
        //   59: aload 6
        //   61: invokevirtual 46	android/os/Parcel:readException	()V
        //   64: aload 6
        //   66: invokevirtual 49	android/os/Parcel:recycle	()V
        //   69: aload 5
        //   71: invokevirtual 49	android/os/Parcel:recycle	()V
        //   74: return
        //   75: iconst_0
        //   76: istore 4
        //   78: goto -42 -> 36
        //   81: astore_1
        //   82: aload 6
        //   84: invokevirtual 49	android/os/Parcel:recycle	()V
        //   87: aload 5
        //   89: invokevirtual 49	android/os/Parcel:recycle	()V
        //   92: aload_1
        //   93: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	94	0	this	Proxy
        //   0	94	1	paramString1	String
        //   0	94	2	paramString2	String
        //   0	94	3	paramBoolean	boolean
        //   1	76	4	i	int
        //   6	82	5	localParcel1	Parcel
        //   11	72	6	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   13	32	81	finally
        //   36	64	81	finally
      }
      
      public void newMessageSent(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.google.android.gtalkservice.IChatListener");
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
      
      public void participantJoined(String paramString1, String paramString2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.google.android.gtalkservice.IChatListener");
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          this.mRemote.transact(9, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void participantLeft(String paramString1, String paramString2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.google.android.gtalkservice.IChatListener");
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          this.mRemote.transact(10, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public boolean useLightweightNotify()
        throws RemoteException
      {
        boolean bool = false;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.google.android.gtalkservice.IChatListener");
          this.mRemote.transact(11, localParcel1, localParcel2, 0);
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
      
      public void willConvertToGroupChat(String paramString1, String paramString2, long paramLong)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.google.android.gtalkservice.IChatListener");
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          localParcel1.writeLong(paramLong);
          this.mRemote.transact(7, localParcel1, localParcel2, 0);
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


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gtalkservice/IChatListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */