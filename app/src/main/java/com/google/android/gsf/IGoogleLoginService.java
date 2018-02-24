package com.google.android.gsf;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;

public abstract interface IGoogleLoginService
  extends IInterface
{
  public abstract GoogleLoginCredentialsResult blockingGetCredentials(String paramString1, String paramString2, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void deleteAllAccounts()
    throws RemoteException;
  
  public abstract void deleteOneAccount(String paramString)
    throws RemoteException;
  
  public abstract String getAccount(boolean paramBoolean)
    throws RemoteException;
  
  public abstract String[] getAccounts()
    throws RemoteException;
  
  public abstract long getAndroidId()
    throws RemoteException;
  
  public abstract String getPrimaryAccount()
    throws RemoteException;
  
  public abstract void invalidateAuthToken(String paramString)
    throws RemoteException;
  
  public abstract String peekCredentials(String paramString1, String paramString2)
    throws RemoteException;
  
  public abstract void saveAuthToken(String paramString1, String paramString2, String paramString3)
    throws RemoteException;
  
  public abstract void saveNewAccount(LoginData paramLoginData)
    throws RemoteException;
  
  public abstract void saveUsernameAndPassword(String paramString1, String paramString2, int paramInt)
    throws RemoteException;
  
  public abstract void tryNewAccount(LoginData paramLoginData)
    throws RemoteException;
  
  public abstract void updatePassword(LoginData paramLoginData)
    throws RemoteException;
  
  public abstract boolean verifyStoredPassword(String paramString1, String paramString2)
    throws RemoteException;
  
  public abstract int waitForAndroidId()
    throws RemoteException;
  
  public static abstract class Stub
    extends Binder
    implements IGoogleLoginService
  {
    private static final String DESCRIPTOR = "com.google.android.gsf.IGoogleLoginService";
    static final int TRANSACTION_blockingGetCredentials = 5;
    static final int TRANSACTION_deleteAllAccounts = 15;
    static final int TRANSACTION_deleteOneAccount = 14;
    static final int TRANSACTION_getAccount = 3;
    static final int TRANSACTION_getAccounts = 1;
    static final int TRANSACTION_getAndroidId = 7;
    static final int TRANSACTION_getPrimaryAccount = 2;
    static final int TRANSACTION_invalidateAuthToken = 6;
    static final int TRANSACTION_peekCredentials = 4;
    static final int TRANSACTION_saveAuthToken = 10;
    static final int TRANSACTION_saveNewAccount = 9;
    static final int TRANSACTION_saveUsernameAndPassword = 13;
    static final int TRANSACTION_tryNewAccount = 8;
    static final int TRANSACTION_updatePassword = 11;
    static final int TRANSACTION_verifyStoredPassword = 12;
    static final int TRANSACTION_waitForAndroidId = 16;
    
    public Stub()
    {
      attachInterface(this, "com.google.android.gsf.IGoogleLoginService");
    }
    
    public static IGoogleLoginService asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("com.google.android.gsf.IGoogleLoginService");
      if ((localIInterface != null) && ((localIInterface instanceof IGoogleLoginService))) {
        return (IGoogleLoginService)localIInterface;
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
      boolean bool;
      switch (paramInt1)
      {
      default: 
        return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
      case 1598968902: 
        paramParcel2.writeString("com.google.android.gsf.IGoogleLoginService");
        return true;
      case 1: 
        paramParcel1.enforceInterface("com.google.android.gsf.IGoogleLoginService");
        paramParcel1 = getAccounts();
        paramParcel2.writeNoException();
        paramParcel2.writeStringArray(paramParcel1);
        return true;
      case 2: 
        paramParcel1.enforceInterface("com.google.android.gsf.IGoogleLoginService");
        paramParcel1 = getPrimaryAccount();
        paramParcel2.writeNoException();
        paramParcel2.writeString(paramParcel1);
        return true;
      case 3: 
        paramParcel1.enforceInterface("com.google.android.gsf.IGoogleLoginService");
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          paramParcel1 = getAccount(bool);
          paramParcel2.writeNoException();
          paramParcel2.writeString(paramParcel1);
          return true;
        }
      case 4: 
        paramParcel1.enforceInterface("com.google.android.gsf.IGoogleLoginService");
        paramParcel1 = peekCredentials(paramParcel1.readString(), paramParcel1.readString());
        paramParcel2.writeNoException();
        paramParcel2.writeString(paramParcel1);
        return true;
      case 5: 
        paramParcel1.enforceInterface("com.google.android.gsf.IGoogleLoginService");
        String str1 = paramParcel1.readString();
        String str2 = paramParcel1.readString();
        if (paramParcel1.readInt() != 0) {}
        for (bool = true;; bool = false)
        {
          paramParcel1 = blockingGetCredentials(str1, str2, bool);
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break;
          }
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
          return true;
        }
        paramParcel2.writeInt(0);
        return true;
      case 6: 
        paramParcel1.enforceInterface("com.google.android.gsf.IGoogleLoginService");
        invalidateAuthToken(paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 7: 
        paramParcel1.enforceInterface("com.google.android.gsf.IGoogleLoginService");
        long l = getAndroidId();
        paramParcel2.writeNoException();
        paramParcel2.writeLong(l);
        return true;
      case 8: 
        paramParcel1.enforceInterface("com.google.android.gsf.IGoogleLoginService");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (LoginData)LoginData.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          tryNewAccount(paramParcel1);
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break;
          }
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
          return true;
        }
        paramParcel2.writeInt(0);
        return true;
      case 9: 
        paramParcel1.enforceInterface("com.google.android.gsf.IGoogleLoginService");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (LoginData)LoginData.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          saveNewAccount(paramParcel1);
          paramParcel2.writeNoException();
          return true;
        }
      case 10: 
        paramParcel1.enforceInterface("com.google.android.gsf.IGoogleLoginService");
        saveAuthToken(paramParcel1.readString(), paramParcel1.readString(), paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 11: 
        paramParcel1.enforceInterface("com.google.android.gsf.IGoogleLoginService");
        if (paramParcel1.readInt() != 0) {}
        for (paramParcel1 = (LoginData)LoginData.CREATOR.createFromParcel(paramParcel1);; paramParcel1 = null)
        {
          updatePassword(paramParcel1);
          paramParcel2.writeNoException();
          if (paramParcel1 == null) {
            break;
          }
          paramParcel2.writeInt(1);
          paramParcel1.writeToParcel(paramParcel2, 1);
          return true;
        }
        paramParcel2.writeInt(0);
        return true;
      case 12: 
        paramParcel1.enforceInterface("com.google.android.gsf.IGoogleLoginService");
        bool = verifyStoredPassword(paramParcel1.readString(), paramParcel1.readString());
        paramParcel2.writeNoException();
        paramInt1 = i;
        if (bool) {
          paramInt1 = 1;
        }
        paramParcel2.writeInt(paramInt1);
        return true;
      case 13: 
        paramParcel1.enforceInterface("com.google.android.gsf.IGoogleLoginService");
        saveUsernameAndPassword(paramParcel1.readString(), paramParcel1.readString(), paramParcel1.readInt());
        paramParcel2.writeNoException();
        return true;
      case 14: 
        paramParcel1.enforceInterface("com.google.android.gsf.IGoogleLoginService");
        deleteOneAccount(paramParcel1.readString());
        paramParcel2.writeNoException();
        return true;
      case 15: 
        paramParcel1.enforceInterface("com.google.android.gsf.IGoogleLoginService");
        deleteAllAccounts();
        paramParcel2.writeNoException();
        return true;
      }
      paramParcel1.enforceInterface("com.google.android.gsf.IGoogleLoginService");
      paramInt1 = waitForAndroidId();
      paramParcel2.writeNoException();
      paramParcel2.writeInt(paramInt1);
      return true;
    }
    
    private static class Proxy
      implements IGoogleLoginService
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
      
      /* Error */
      public GoogleLoginCredentialsResult blockingGetCredentials(String paramString1, String paramString2, boolean paramBoolean)
        throws RemoteException
      {
        // Byte code:
        //   0: iconst_0
        //   1: istore 4
        //   3: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   6: astore 5
        //   8: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   11: astore 6
        //   13: aload 5
        //   15: ldc 34
        //   17: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   20: aload 5
        //   22: aload_1
        //   23: invokevirtual 41	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   26: aload 5
        //   28: aload_2
        //   29: invokevirtual 41	android/os/Parcel:writeString	(Ljava/lang/String;)V
        //   32: iload_3
        //   33: ifeq +6 -> 39
        //   36: iconst_1
        //   37: istore 4
        //   39: aload 5
        //   41: iload 4
        //   43: invokevirtual 45	android/os/Parcel:writeInt	(I)V
        //   46: aload_0
        //   47: getfield 19	com/google/android/gsf/IGoogleLoginService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   50: iconst_5
        //   51: aload 5
        //   53: aload 6
        //   55: iconst_0
        //   56: invokeinterface 51 5 0
        //   61: pop
        //   62: aload 6
        //   64: invokevirtual 54	android/os/Parcel:readException	()V
        //   67: aload 6
        //   69: invokevirtual 58	android/os/Parcel:readInt	()I
        //   72: ifeq +29 -> 101
        //   75: getstatic 64	com/google/android/gsf/GoogleLoginCredentialsResult:CREATOR	Landroid/os/Parcelable$Creator;
        //   78: aload 6
        //   80: invokeinterface 70 2 0
        //   85: checkcast 60	com/google/android/gsf/GoogleLoginCredentialsResult
        //   88: astore_1
        //   89: aload 6
        //   91: invokevirtual 73	android/os/Parcel:recycle	()V
        //   94: aload 5
        //   96: invokevirtual 73	android/os/Parcel:recycle	()V
        //   99: aload_1
        //   100: areturn
        //   101: aconst_null
        //   102: astore_1
        //   103: goto -14 -> 89
        //   106: astore_1
        //   107: aload 6
        //   109: invokevirtual 73	android/os/Parcel:recycle	()V
        //   112: aload 5
        //   114: invokevirtual 73	android/os/Parcel:recycle	()V
        //   117: aload_1
        //   118: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	119	0	this	Proxy
        //   0	119	1	paramString1	String
        //   0	119	2	paramString2	String
        //   0	119	3	paramBoolean	boolean
        //   1	41	4	i	int
        //   6	107	5	localParcel1	Parcel
        //   11	97	6	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   13	32	106	finally
        //   39	89	106	finally
      }
      
      public void deleteAllAccounts()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.google.android.gsf.IGoogleLoginService");
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
      
      public void deleteOneAccount(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.google.android.gsf.IGoogleLoginService");
          localParcel1.writeString(paramString);
          this.mRemote.transact(14, localParcel1, localParcel2, 0);
          localParcel2.readException();
          return;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public String getAccount(boolean paramBoolean)
        throws RemoteException
      {
        int i = 0;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.google.android.gsf.IGoogleLoginService");
          if (paramBoolean) {
            i = 1;
          }
          localParcel1.writeInt(i);
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
      
      public String[] getAccounts()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.google.android.gsf.IGoogleLoginService");
          this.mRemote.transact(1, localParcel1, localParcel2, 0);
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
      
      public long getAndroidId()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.google.android.gsf.IGoogleLoginService");
          this.mRemote.transact(7, localParcel1, localParcel2, 0);
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
        return "com.google.android.gsf.IGoogleLoginService";
      }
      
      public String getPrimaryAccount()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.google.android.gsf.IGoogleLoginService");
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
      
      public void invalidateAuthToken(String paramString)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.google.android.gsf.IGoogleLoginService");
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
      
      public String peekCredentials(String paramString1, String paramString2)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.google.android.gsf.IGoogleLoginService");
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          this.mRemote.transact(4, localParcel1, localParcel2, 0);
          localParcel2.readException();
          paramString1 = localParcel2.readString();
          return paramString1;
        }
        finally
        {
          localParcel2.recycle();
          localParcel1.recycle();
        }
      }
      
      public void saveAuthToken(String paramString1, String paramString2, String paramString3)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.google.android.gsf.IGoogleLoginService");
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          localParcel1.writeString(paramString3);
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
      
      /* Error */
      public void saveNewAccount(LoginData paramLoginData)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore_3
        //   8: aload_2
        //   9: ldc 34
        //   11: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload_1
        //   15: ifnull +42 -> 57
        //   18: aload_2
        //   19: iconst_1
        //   20: invokevirtual 45	android/os/Parcel:writeInt	(I)V
        //   23: aload_1
        //   24: aload_2
        //   25: iconst_0
        //   26: invokevirtual 107	com/google/android/gsf/LoginData:writeToParcel	(Landroid/os/Parcel;I)V
        //   29: aload_0
        //   30: getfield 19	com/google/android/gsf/IGoogleLoginService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 9
        //   35: aload_2
        //   36: aload_3
        //   37: iconst_0
        //   38: invokeinterface 51 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 54	android/os/Parcel:readException	()V
        //   48: aload_3
        //   49: invokevirtual 73	android/os/Parcel:recycle	()V
        //   52: aload_2
        //   53: invokevirtual 73	android/os/Parcel:recycle	()V
        //   56: return
        //   57: aload_2
        //   58: iconst_0
        //   59: invokevirtual 45	android/os/Parcel:writeInt	(I)V
        //   62: goto -33 -> 29
        //   65: astore_1
        //   66: aload_3
        //   67: invokevirtual 73	android/os/Parcel:recycle	()V
        //   70: aload_2
        //   71: invokevirtual 73	android/os/Parcel:recycle	()V
        //   74: aload_1
        //   75: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	76	0	this	Proxy
        //   0	76	1	paramLoginData	LoginData
        //   3	68	2	localParcel1	Parcel
        //   7	60	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	65	finally
        //   18	29	65	finally
        //   29	48	65	finally
        //   57	62	65	finally
      }
      
      public void saveUsernameAndPassword(String paramString1, String paramString2, int paramInt)
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.google.android.gsf.IGoogleLoginService");
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
          localParcel1.writeInt(paramInt);
          this.mRemote.transact(13, localParcel1, localParcel2, 0);
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
      public void tryNewAccount(LoginData paramLoginData)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore_3
        //   8: aload_2
        //   9: ldc 34
        //   11: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload_1
        //   15: ifnull +54 -> 69
        //   18: aload_2
        //   19: iconst_1
        //   20: invokevirtual 45	android/os/Parcel:writeInt	(I)V
        //   23: aload_1
        //   24: aload_2
        //   25: iconst_0
        //   26: invokevirtual 107	com/google/android/gsf/LoginData:writeToParcel	(Landroid/os/Parcel;I)V
        //   29: aload_0
        //   30: getfield 19	com/google/android/gsf/IGoogleLoginService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 8
        //   35: aload_2
        //   36: aload_3
        //   37: iconst_0
        //   38: invokeinterface 51 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 54	android/os/Parcel:readException	()V
        //   48: aload_3
        //   49: invokevirtual 58	android/os/Parcel:readInt	()I
        //   52: ifeq +8 -> 60
        //   55: aload_1
        //   56: aload_3
        //   57: invokevirtual 114	com/google/android/gsf/LoginData:readFromParcel	(Landroid/os/Parcel;)V
        //   60: aload_3
        //   61: invokevirtual 73	android/os/Parcel:recycle	()V
        //   64: aload_2
        //   65: invokevirtual 73	android/os/Parcel:recycle	()V
        //   68: return
        //   69: aload_2
        //   70: iconst_0
        //   71: invokevirtual 45	android/os/Parcel:writeInt	(I)V
        //   74: goto -45 -> 29
        //   77: astore_1
        //   78: aload_3
        //   79: invokevirtual 73	android/os/Parcel:recycle	()V
        //   82: aload_2
        //   83: invokevirtual 73	android/os/Parcel:recycle	()V
        //   86: aload_1
        //   87: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	88	0	this	Proxy
        //   0	88	1	paramLoginData	LoginData
        //   3	80	2	localParcel1	Parcel
        //   7	72	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	77	finally
        //   18	29	77	finally
        //   29	60	77	finally
        //   69	74	77	finally
      }
      
      /* Error */
      public void updatePassword(LoginData paramLoginData)
        throws RemoteException
      {
        // Byte code:
        //   0: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   3: astore_2
        //   4: invokestatic 32	android/os/Parcel:obtain	()Landroid/os/Parcel;
        //   7: astore_3
        //   8: aload_2
        //   9: ldc 34
        //   11: invokevirtual 38	android/os/Parcel:writeInterfaceToken	(Ljava/lang/String;)V
        //   14: aload_1
        //   15: ifnull +54 -> 69
        //   18: aload_2
        //   19: iconst_1
        //   20: invokevirtual 45	android/os/Parcel:writeInt	(I)V
        //   23: aload_1
        //   24: aload_2
        //   25: iconst_0
        //   26: invokevirtual 107	com/google/android/gsf/LoginData:writeToParcel	(Landroid/os/Parcel;I)V
        //   29: aload_0
        //   30: getfield 19	com/google/android/gsf/IGoogleLoginService$Stub$Proxy:mRemote	Landroid/os/IBinder;
        //   33: bipush 11
        //   35: aload_2
        //   36: aload_3
        //   37: iconst_0
        //   38: invokeinterface 51 5 0
        //   43: pop
        //   44: aload_3
        //   45: invokevirtual 54	android/os/Parcel:readException	()V
        //   48: aload_3
        //   49: invokevirtual 58	android/os/Parcel:readInt	()I
        //   52: ifeq +8 -> 60
        //   55: aload_1
        //   56: aload_3
        //   57: invokevirtual 114	com/google/android/gsf/LoginData:readFromParcel	(Landroid/os/Parcel;)V
        //   60: aload_3
        //   61: invokevirtual 73	android/os/Parcel:recycle	()V
        //   64: aload_2
        //   65: invokevirtual 73	android/os/Parcel:recycle	()V
        //   68: return
        //   69: aload_2
        //   70: iconst_0
        //   71: invokevirtual 45	android/os/Parcel:writeInt	(I)V
        //   74: goto -45 -> 29
        //   77: astore_1
        //   78: aload_3
        //   79: invokevirtual 73	android/os/Parcel:recycle	()V
        //   82: aload_2
        //   83: invokevirtual 73	android/os/Parcel:recycle	()V
        //   86: aload_1
        //   87: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	88	0	this	Proxy
        //   0	88	1	paramLoginData	LoginData
        //   3	80	2	localParcel1	Parcel
        //   7	72	3	localParcel2	Parcel
        // Exception table:
        //   from	to	target	type
        //   8	14	77	finally
        //   18	29	77	finally
        //   29	60	77	finally
        //   69	74	77	finally
      }
      
      public boolean verifyStoredPassword(String paramString1, String paramString2)
        throws RemoteException
      {
        boolean bool = false;
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.google.android.gsf.IGoogleLoginService");
          localParcel1.writeString(paramString1);
          localParcel1.writeString(paramString2);
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
      
      public int waitForAndroidId()
        throws RemoteException
      {
        Parcel localParcel1 = Parcel.obtain();
        Parcel localParcel2 = Parcel.obtain();
        try
        {
          localParcel1.writeInterfaceToken("com.google.android.gsf.IGoogleLoginService");
          this.mRemote.transact(16, localParcel1, localParcel2, 0);
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
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gsf/IGoogleLoginService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */