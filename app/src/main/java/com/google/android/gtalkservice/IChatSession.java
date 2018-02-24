package com.google.android.gtalkservice;

import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;

public abstract interface IChatSession
  extends IInterface
{
  public abstract void addRemoteChatListener(IChatListener paramIChatListener)
    throws RemoteException;
  
  public abstract void clearChatHistory(Uri paramUri)
    throws RemoteException;
  
  public abstract void ensureNonZeroLastMessageDate()
    throws RemoteException;
  
  public abstract boolean getLightweightNotify()
    throws RemoteException;
  
  public abstract String[] getParticipants()
    throws RemoteException;
  
  public abstract String getUnsentComposedMessage()
    throws RemoteException;
  
  public abstract void inviteContact(String paramString)
    throws RemoteException;
  
  public abstract boolean isGroupChat()
    throws RemoteException;
  
  public abstract boolean isOffTheRecord()
    throws RemoteException;
  
  public abstract void leave()
    throws RemoteException;
  
  public abstract void markAsRead()
    throws RemoteException;
  
  public abstract void removeRemoteChatListener(IChatListener paramIChatListener)
    throws RemoteException;
  
  public abstract void reportEndCause(String paramString, boolean paramBoolean, int paramInt)
    throws RemoteException;
  
  public abstract void reportMissedCall(String paramString1, String paramString2, boolean paramBoolean1, boolean paramBoolean2)
    throws RemoteException;
  
  public abstract void saveUnsentComposedMessage(String paramString)
    throws RemoteException;
  
  public abstract void sendChatMessage(String paramString)
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IChatSession
  {
    private static final String DESCRIPTOR = "com.google.android.gtalkservice.IChatSession";
    static final int TRANSACTION_addRemoteChatListener = 9;
    static final int TRANSACTION_clearChatHistory = 16;
    static final int TRANSACTION_ensureNonZeroLastMessageDate = 15;
    static final int TRANSACTION_getLightweightNotify = 12;
    static final int TRANSACTION_getParticipants = 3;
    static final int TRANSACTION_getUnsentComposedMessage = 8;
    static final int TRANSACTION_inviteContact = 4;
    static final int TRANSACTION_isGroupChat = 1;
    static final int TRANSACTION_isOffTheRecord = 11;
    static final int TRANSACTION_leave = 5;
    static final int TRANSACTION_markAsRead = 2;
    static final int TRANSACTION_removeRemoteChatListener = 10;
    static final int TRANSACTION_reportEndCause = 13;
    static final int TRANSACTION_reportMissedCall = 14;
    static final int TRANSACTION_saveUnsentComposedMessage = 7;
    static final int TRANSACTION_sendChatMessage = 6;
    
    public Stub()
    {
      attachInterface(this, "com.google.android.gtalkservice.IChatSession");
    }
    
    public static IChatSession asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("com.google.android.gtalkservice.IChatSession");
      if ((localIInterface != null) && ((localIInterface instanceof IChatSession))) {
        return (IChatSession)localIInterface;
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
      int j = 0;
      int k = 0;
      int i = 0;
      boolean bool1;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("com.google.android.gtalkservice.IChatSession");
        return true;
      case 1: 
        paramParcel1.enforceInterface("com.google.android.gtalkservice.IChatSession");
        bool1 = isGroupChat();
        paramParcel2.writeNoException();
        paramInt1 = i;
        if (bool1) {
          paramInt1 = 1;
        }
        paramParcel2.writeInt(paramInt1);
        return true;
      case 2: 
        paramParcel1.enforceInterface("com.google.android.gtalkservice.IChatSession");
        markAsRead();
        return true;
      case 3: 
        paramParcel1.enforceInterface("com.google.android.gtalkservice.IChatSession");
        paramParcel1 = getParticipants();
        paramParcel2.writeNoException();
        paramParcel2.writeStringArray(paramParcel1);
        return true;
      case 4: 
        paramParcel1.enforceInterface("com.google.android.gtalkservice.IChatSession");
        inviteContact(paramParcel1.readString());
        return true;
      case 5: 
        paramParcel1.enforceInterface("com.google.android.gtalkservice.IChatSession");
        leave();
        return true;
      case 6: 
        paramParcel1.enforceInterface("com.google.android.gtalkservice.IChatSession");
        sendChatMessage(paramParcel1.readString());
        return true;
      case 7: 
        paramParcel1.enforceInterface("com.google.android.gtalkservice.IChatSession");
        saveUnsentComposedMessage(paramParcel1.readString());
        return true;
      case 8: 
        paramParcel1.enforceInterface("com.google.android.gtalkservice.IChatSession");
        paramParcel1 = getUnsentComposedMessage();
        paramParcel2.writeNoException();
        paramParcel2.writeString(paramParcel1);
        return true;
      case 9: 
        paramParcel1.enforceInterface("com.google.android.gtalkservice.IChatSession");
        addRemoteChatListener(IChatListener.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 10: 
        paramParcel1.enforceInterface("com.google.android.gtalkservice.IChatSession");
        removeRemoteChatListener(IChatListener.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        return true;
      case 11: 
        paramParcel1.enforceInterface("com.google.android.gtalkservice.IChatSession");
        bool1 = isOffTheRecord();
        paramParcel2.writeNoException();
        paramInt1 = j;
        if (bool1) {
          paramInt1 = 1;
        }
        paramParcel2.writeInt(paramInt1);
        return true;
      case 12: 
        paramParcel1.enforceInterface("com.google.android.gtalkservice.IChatSession");
        bool1 = getLightweightNotify();
        paramParcel2.writeNoException();
        paramInt1 = k;
        if (bool1) {
          paramInt1 = 1;
        }
        paramParcel2.writeInt(paramInt1);
        return true;
      case 13: 
        paramParcel1.enforceInterface("com.google.android.gtalkservice.IChatSession");
        paramParcel2 = paramParcel1.readString();
        if (paramParcel1.readInt() != 0) {}
        for (bool1 = true;; bool1 = false)
        {
          reportEndCause(paramParcel2, bool1, paramParcel1.readInt());
          return true;
        }
      case 14: 
        paramParcel1.enforceInterface("com.google.android.gtalkservice.IChatSession");
        paramParcel2 = paramParcel1.readString();
        String str = paramParcel1.readString();
        if (paramParcel1.readInt() != 0)
        {
          bool1 = true;
          if (paramParcel1.readInt() == 0) {
            break label531;
          }
        }
        for (boolean bool2 = true;; bool2 = false)
        {
          reportMissedCall(paramParcel2, str, bool1, bool2);
          return true;
          bool1 = false;
          break;
        }
      case 15: 
        label531:
        paramParcel1.enforceInterface("com.google.android.gtalkservice.IChatSession");
        ensureNonZeroLastMessageDate();
        return true;
      }
      paramParcel1.enforceInterface("com.google.android.gtalkservice.IChatSession");
      if (paramParcel1.readInt() != 0) {}
      for (paramParcel1 = (Uri)Uri.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
      {
        clearChatHistory(paramParcel1);
        return true;
      }
    }
    
    private static class Proxy
      implements IChatSession
    {
      private IBinder mRemote;
      
      Proxy(IBinder paramIBinder)
      {
        this.mRemote = paramIBinder;
      }
      
      /* Error */
      public void addRemoteChatListener(IChatListener paramIChatListener)
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
        //   15: ifnull +43 -> 58
        //   18: aload_1
        //   19: invokeinterface 42 1 0
        //   24: astore_1
        //   25: aload_2
        //   26: aload_1
        //   27: invokevirtual 45	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   30: aload_0
        //   31: getfield 19	com/google/android/gtalkservice/IChatSession$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   34: bipush 9
        //   36: aload_2
        //   37: aload_3
        //   38: iconst_0
        //   39: invokeinterface 51 5 0
        //   44: pop
        //   45: aload_3
        //   46: invokevirtual 54	android/os/Parcel:readException	()V
        //   49: aload_3
        //   50: invokevirtual 57	android/os/Parcel:recycle	()V
        //   53: aload_2
        //   54: invokevirtual 57	android/os/Parcel:recycle	()V
        //   57: return
        //   58: aconst_null
        //   59: astore_1
        //   60: goto -35 -> 25
        //   63: astore_1
        //   64: aload_3
        //   65: invokevirtual 57	android/os/Parcel:recycle	()V
        //   68: aload_2
        //   69: invokevirtual 57	android/os/Parcel:recycle	()V
        //   72: aload_1
        //   73: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	74	0	this	Proxy
        //   0	74	1	paramIChatListener	IChatListener
        //   3	66	2	localParcel1	Parcel
        //   7	58	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	63	finally
        //   18	25	63	finally
        //   25	49	63	finally
      }
      
      public IBinder asBinder()
      {
        return this.mRemote;
      }
      
      /* Error */
      public void clearChatHistory(Uri paramUri)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: aload_2
        //   5: ldc 32
        //   7: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   10: aload_1
        //   11: ifnull +34 -> 45
        //   14: aload_2
        //   15: iconst_1
        //   16: invokevirtual 64	android/os/Parcel:writeInt	(I)V
        //   19: aload_1
        //   20: aload_2
        //   21: iconst_0
        //   22: invokevirtual 70	android/net/Uri:writeToParcel	(Landroid/os/Parcel;I)V
        //   25: aload_0
        //   26: getfield 19	com/google/android/gtalkservice/IChatSession$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   29: bipush 16
        //   31: aload_2
        //   32: aconst_null
        //   33: iconst_1
        //   34: invokeinterface 51 5 0
        //   39: pop
        //   40: aload_2
        //   41: invokevirtual 57	android/os/Parcel:recycle	()V
        //   44: return
        //   45: aload_2
        //   46: iconst_0
        //   47: invokevirtual 64	android/os/Parcel:writeInt	(I)V
        //   50: goto -25 -> 25
        //   53: astore_1
        //   54: aload_2
        //   55: invokevirtual 57	android/os/Parcel:recycle	()V
        //   58: aload_1
        //   59: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	60	0	this	Proxy
        //   0	60	1	paramUri	Uri
        //   3	52	2	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   4	10	53	finally
        //   14	25	53	finally
        //   25	40	53	finally
        //   45	50	53	finally
      }
      
      public void ensureNonZeroLastMessageDate()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("com.google.android.gtalkservice.IChatSession");
          this.mRemote.transact(15, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public String getInterfaceDescriptor()
      {
        return "com.google.android.gtalkservice.IChatSession";
      }
      
      public boolean getLightweightNotify()
        throws RemoteException
      {
        boolean bool = false;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.google.android.gtalkservice.IChatSession");
          this.mRemote.transact(12, localParcel1, localParcel2, 0);
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
      
      public String[] getParticipants()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.google.android.gtalkservice.IChatSession");
          this.mRemote.transact(3, localParcel1, localParcel2, 0);
          localParcel2.readException();
          String[] arrayOfString = localParcel2.createStringArray();
          return arrayOfString;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public String getUnsentComposedMessage()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.google.android.gtalkservice.IChatSession");
          this.mRemote.transact(8, localParcel1, localParcel2, 0);
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
      
      public void inviteContact(String paramString)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("com.google.android.gtalkservice.IChatSession");
          localParcel.writeString(paramString);
          this.mRemote.transact(4, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      /* Error */
      public boolean isGroupChat()
        throws RemoteException
      {
        // Byte code:
        //   0: iconst_1
        //   1: istore_2
        //   2: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   5: astore_3
        //   6: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   9: astore 4
        //   11: aload_3
        //   12: ldc 32
        //   14: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   17: aload_0
        //   18: getfield 19	com/google/android/gtalkservice/IChatSession$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   21: iconst_1
        //   22: aload_3
        //   23: aload 4
        //   25: iconst_0
        //   26: invokeinterface 51 5 0
        //   31: pop
        //   32: aload 4
        //   34: invokevirtual 54	android/os/Parcel:readException	()V
        //   37: aload 4
        //   39: invokevirtual 79	android/os/Parcel:readInt	()I
        //   42: istore_1
        //   43: iload_1
        //   44: ifeq +14 -> 58
        //   47: aload 4
        //   49: invokevirtual 57	android/os/Parcel:recycle	()V
        //   52: aload_3
        //   53: invokevirtual 57	android/os/Parcel:recycle	()V
        //   56: iload_2
        //   57: ireturn
        //   58: iconst_0
        //   59: istore_2
        //   60: goto -13 -> 47
        //   63: astore 5
        //   65: aload 4
        //   67: invokevirtual 57	android/os/Parcel:recycle	()V
        //   70: aload_3
        //   71: invokevirtual 57	android/os/Parcel:recycle	()V
        //   74: aload 5
        //   76: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	77	0	this	Proxy
        //   42	2	1	i	int
        //   1	59	2	bool	boolean
        //   5	66	3	localParcel1	Parcel
        //   9	57	4	localParcel2	Parcel
        //   63	12	5	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   11	43	63	finally
      }
      
      public boolean isOffTheRecord()
        throws RemoteException
      {
        boolean bool = false;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.google.android.gtalkservice.IChatSession");
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
      
      public void leave()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("com.google.android.gtalkservice.IChatSession");
          this.mRemote.transact(5, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void markAsRead()
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("com.google.android.gtalkservice.IChatSession");
          this.mRemote.transact(2, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      /* Error */
      public void removeRemoteChatListener(IChatListener paramIChatListener)
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
        //   15: ifnull +43 -> 58
        //   18: aload_1
        //   19: invokeinterface 42 1 0
        //   24: astore_1
        //   25: aload_2
        //   26: aload_1
        //   27: invokevirtual 45	android/os/Parcel:writeStrongBinder	(Landroid/os/IBinder;)V
        //   30: aload_0
        //   31: getfield 19	com/google/android/gtalkservice/IChatSession$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   34: bipush 10
        //   36: aload_2
        //   37: aload_3
        //   38: iconst_0
        //   39: invokeinterface 51 5 0
        //   44: pop
        //   45: aload_3
        //   46: invokevirtual 54	android/os/Parcel:readException	()V
        //   49: aload_3
        //   50: invokevirtual 57	android/os/Parcel:recycle	()V
        //   53: aload_2
        //   54: invokevirtual 57	android/os/Parcel:recycle	()V
        //   57: return
        //   58: aconst_null
        //   59: astore_1
        //   60: goto -35 -> 25
        //   63: astore_1
        //   64: aload_3
        //   65: invokevirtual 57	android/os/Parcel:recycle	()V
        //   68: aload_2
        //   69: invokevirtual 57	android/os/Parcel:recycle	()V
        //   72: aload_1
        //   73: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	74	0	this	Proxy
        //   0	74	1	paramIChatListener	IChatListener
        //   3	66	2	localParcel1	Parcel
        //   7	58	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	63	finally
        //   18	25	63	finally
        //   25	49	63	finally
      }
      
      /* Error */
      public void reportEndCause(String paramString, boolean paramBoolean, int paramInt)
        throws RemoteException
      {
        // Byte code:
        //   0: iconst_1
        //   1: istore 4
        //   3: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   6: astore 5
        //   8: aload 5
        //   10: ldc 32
        //   12: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload 5
        //   17: aload_1
        //   18: invokevirtual 92	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   21: iload_2
        //   22: ifeq +38 -> 60
        //   25: aload 5
        //   27: iload 4
        //   29: invokevirtual 64	android/os/Parcel:writeInt	(I)V
        //   32: aload 5
        //   34: iload_3
        //   35: invokevirtual 64	android/os/Parcel:writeInt	(I)V
        //   38: aload_0
        //   39: getfield 19	com/google/android/gtalkservice/IChatSession$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   42: bipush 13
        //   44: aload 5
        //   46: aconst_null
        //   47: iconst_1
        //   48: invokeinterface 51 5 0
        //   53: pop
        //   54: aload 5
        //   56: invokevirtual 57	android/os/Parcel:recycle	()V
        //   59: return
        //   60: iconst_0
        //   61: istore 4
        //   63: goto -38 -> 25
        //   66: astore_1
        //   67: aload 5
        //   69: invokevirtual 57	android/os/Parcel:recycle	()V
        //   72: aload_1
        //   73: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	74	0	this	Proxy
        //   0	74	1	paramString	String
        //   0	74	2	paramBoolean	boolean
        //   0	74	3	paramInt	int
        //   1	61	4	i	int
        //   6	62	5	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	21	66	finally
        //   25	54	66	finally
      }
      
      /* Error */
      public void reportMissedCall(String paramString1, String paramString2, boolean paramBoolean1, boolean paramBoolean2)
        throws RemoteException
      {
        // Byte code:
        //   0: iconst_1
        //   1: istore 6
        //   3: invokestatic 30	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   6: astore 7
        //   8: aload 7
        //   10: ldc 32
        //   12: invokevirtual 36	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   15: aload 7
        //   17: aload_1
        //   18: invokevirtual 92	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   21: aload 7
        //   23: aload_2
        //   24: invokevirtual 92	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   27: iload_3
        //   28: ifeq +51 -> 79
        //   31: iconst_1
        //   32: istore 5
        //   34: aload 7
        //   36: iload 5
        //   38: invokevirtual 64	android/os/Parcel:writeInt	(I)V
        //   41: iload 4
        //   43: ifeq +42 -> 85
        //   46: iload 6
        //   48: istore 5
        //   50: aload 7
        //   52: iload 5
        //   54: invokevirtual 64	android/os/Parcel:writeInt	(I)V
        //   57: aload_0
        //   58: getfield 19	com/google/android/gtalkservice/IChatSession$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   61: bipush 14
        //   63: aload 7
        //   65: aconst_null
        //   66: iconst_1
        //   67: invokeinterface 51 5 0
        //   72: pop
        //   73: aload 7
        //   75: invokevirtual 57	android/os/Parcel:recycle	()V
        //   78: return
        //   79: iconst_0
        //   80: istore 5
        //   82: goto -48 -> 34
        //   85: iconst_0
        //   86: istore 5
        //   88: goto -38 -> 50
        //   91: astore_1
        //   92: aload 7
        //   94: invokevirtual 57	android/os/Parcel:recycle	()V
        //   97: aload_1
        //   98: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	99	0	this	Proxy
        //   0	99	1	paramString1	String
        //   0	99	2	paramString2	String
        //   0	99	3	paramBoolean1	boolean
        //   0	99	4	paramBoolean2	boolean
        //   32	55	5	i	int
        //   1	46	6	j	int
        //   6	87	7	localParcel	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	27	91	finally
        //   34	41	91	finally
        //   50	73	91	finally
      }
      
      public void saveUnsentComposedMessage(String paramString)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("com.google.android.gtalkservice.IChatSession");
          localParcel.writeString(paramString);
          this.mRemote.transact(7, localParcel, null, 1);
          return;
        }
        finally
        {
          localParcel.recycle();
        }
      }
      
      public void sendChatMessage(String paramString)
        throws RemoteException
      {
        Parcel localParcel = Parcel.obtain();
        try
        {
          localParcel.writeInterfaceToken("com.google.android.gtalkservice.IChatSession");
          localParcel.writeString(paramString);
          this.mRemote.transact(6, localParcel, null, 1);
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


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gtalkservice/IChatSession.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */