package com.google.firebase.iid;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.ConditionVariable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.Process;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.v4.util.SimpleArrayMap;
import android.util.Log;
import com.google.android.gms.iid.MessengerCompat;
import java.io.IOException;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Rpc
{
  public static final String PARAM_APP_VER = "app_ver";
  public static final String PARAM_APP_VER_NAME = "app_ver_name";
  public static final String PARAM_CLIENT_VER = "cliv";
  public static final String PARAM_GMP_APP_ID = "gmp_app_id";
  public static final String PARAM_GMS_VER = "gmsv";
  public static final String PARAM_INSTANCE_ID = "appid";
  public static final String PARAM_OS_VER = "osv";
  public static final String PARAM_PUBLIC_KEY = "pub2";
  public static final String PARAM_SIGNATURE = "sig";
  static String zzbAS = null;
  static int zzbAT = 0;
  static int zzbAU = 0;
  static int zzbAV = 0;
  private final SimpleArrayMap<String, zzb> wp = new SimpleArrayMap();
  Messenger zzbAX;
  MessengerCompat zzbAY;
  long zzbAZ;
  long zzbBa;
  int zzbBb;
  int zzbBc;
  long zzbBd;
  PendingIntent zzbxv;
  Messenger zzbxz;
  Context zzqm;
  
  public Rpc(Context paramContext)
  {
    this.zzqm = paramContext;
  }
  
  public static String findAppIDPackage(Context paramContext)
  {
    if (zzbAS != null) {
      return zzbAS;
    }
    zzbAT = Process.myUid();
    paramContext = paramContext.getPackageManager();
    Object localObject1 = paramContext.queryIntentServices(new Intent("com.google.android.c2dm.intent.REGISTER"), 0).iterator();
    for (;;)
    {
      if (((Iterator)localObject1).hasNext())
      {
        localObject2 = (ResolveInfo)((Iterator)localObject1).next();
        if (paramContext.checkPermission("com.google.android.c2dm.permission.RECEIVE", ((ResolveInfo)localObject2).serviceInfo.packageName) != 0) {}
      }
      try
      {
        localObject3 = paramContext.getApplicationInfo(((ResolveInfo)localObject2).serviceInfo.packageName, 0);
        int i = ((ApplicationInfo)localObject3).uid;
        Log.w("InstanceID/Rpc", 17 + "Found " + i);
        zzbAU = ((ApplicationInfo)localObject3).uid;
        zzbAS = ((ResolveInfo)localObject2).serviceInfo.packageName;
        localObject2 = zzbAS;
        return (String)localObject2;
      }
      catch (PackageManager.NameNotFoundException localNameNotFoundException2) {}
      Object localObject2 = String.valueOf(((ResolveInfo)localObject2).serviceInfo.packageName);
      Object localObject3 = String.valueOf("com.google.android.c2dm.intent.REGISTER");
      Log.w("InstanceID/Rpc", String.valueOf(localObject2).length() + 56 + String.valueOf(localObject3).length() + "Possible malicious package " + (String)localObject2 + " declares " + (String)localObject3 + " without permission");
      continue;
      Log.w("InstanceID/Rpc", "Failed to resolve REGISTER intent, falling back");
      try
      {
        localObject1 = paramContext.getApplicationInfo("com.google.android.gms", 0);
        zzbAS = ((ApplicationInfo)localObject1).packageName;
        zzbAU = ((ApplicationInfo)localObject1).uid;
        localObject1 = zzbAS;
        return (String)localObject1;
      }
      catch (PackageManager.NameNotFoundException localNameNotFoundException1)
      {
        try
        {
          paramContext = paramContext.getApplicationInfo("com.google.android.gsf", 0);
          zzbAS = paramContext.packageName;
          zzbAU = paramContext.uid;
          paramContext = zzbAS;
          return paramContext;
        }
        catch (PackageManager.NameNotFoundException paramContext)
        {
          Log.w("InstanceID/Rpc", "Both Google Play Services and legacy GSF package are missing");
          return null;
        }
      }
    }
  }
  
  public static String nextId()
  {
    try
    {
      int i = zzbAV;
      zzbAV = i + 1;
      String str = Integer.toString(i);
      return str;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  /* Error */
  static String zza(KeyPair paramKeyPair, String... paramVarArgs)
  {
    // Byte code:
    //   0: ldc -38
    //   2: aload_1
    //   3: invokestatic 224	android/text/TextUtils:join	(Ljava/lang/CharSequence;[Ljava/lang/Object;)Ljava/lang/String;
    //   6: ldc -30
    //   8: invokevirtual 230	java/lang/String:getBytes	(Ljava/lang/String;)[B
    //   11: astore_1
    //   12: aload_0
    //   13: invokevirtual 236	java/security/KeyPair:getPrivate	()Ljava/security/PrivateKey;
    //   16: astore_2
    //   17: aload_2
    //   18: instanceof 238
    //   21: ifeq +44 -> 65
    //   24: ldc -16
    //   26: astore_0
    //   27: aload_0
    //   28: invokestatic 246	java/security/Signature:getInstance	(Ljava/lang/String;)Ljava/security/Signature;
    //   31: astore_0
    //   32: aload_0
    //   33: aload_2
    //   34: invokevirtual 250	java/security/Signature:initSign	(Ljava/security/PrivateKey;)V
    //   37: aload_0
    //   38: aload_1
    //   39: invokevirtual 254	java/security/Signature:update	([B)V
    //   42: aload_0
    //   43: invokevirtual 258	java/security/Signature:sign	()[B
    //   46: invokestatic 264	com/google/firebase/iid/FirebaseInstanceId:zzB	([B)Ljava/lang/String;
    //   49: astore_0
    //   50: aload_0
    //   51: areturn
    //   52: astore_0
    //   53: ldc -98
    //   55: ldc_w 266
    //   58: aload_0
    //   59: invokestatic 270	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   62: pop
    //   63: aconst_null
    //   64: areturn
    //   65: ldc_w 272
    //   68: astore_0
    //   69: goto -42 -> 27
    //   72: astore_0
    //   73: ldc -98
    //   75: ldc_w 274
    //   78: aload_0
    //   79: invokestatic 270	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   82: pop
    //   83: aconst_null
    //   84: areturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	85	0	paramKeyPair	KeyPair
    //   0	85	1	paramVarArgs	String[]
    //   16	18	2	localPrivateKey	java.security.PrivateKey
    // Exception table:
    //   from	to	target	type
    //   0	12	52	java/io/UnsupportedEncodingException
    //   12	24	72	java/security/GeneralSecurityException
    //   27	50	72	java/security/GeneralSecurityException
  }
  
  private void zzaN(String paramString1, String paramString2)
  {
    SimpleArrayMap localSimpleArrayMap = this.wp;
    int i;
    if (paramString1 == null) {
      i = 0;
    }
    for (;;)
    {
      try
      {
        if (i < this.wp.size())
        {
          ((zzb)this.wp.valueAt(i)).onError(paramString2);
          i += 1;
          continue;
        }
        this.wp.clear();
        return;
      }
      finally {}
      zzb localzzb = (zzb)this.wp.remove(paramString1);
      if (localzzb == null)
      {
        paramString1 = String.valueOf(paramString1);
        if (paramString1.length() != 0)
        {
          paramString1 = "Missing callback for ".concat(paramString1);
          Log.w("InstanceID/Rpc", paramString1);
          return;
        }
        paramString1 = new String("Missing callback for ");
        continue;
      }
      localzzb.onError(paramString2);
    }
  }
  
  /* Error */
  private Intent zzb(Bundle arg1, KeyPair paramKeyPair)
    throws IOException
  {
    // Byte code:
    //   0: invokestatic 305	com/google/firebase/iid/Rpc:nextId	()Ljava/lang/String;
    //   3: astore_3
    //   4: new 8	com/google/firebase/iid/Rpc$zza
    //   7: dup
    //   8: aconst_null
    //   9: invokespecial 308	com/google/firebase/iid/Rpc$zza:<init>	(Lcom/google/firebase/iid/Rpc$1;)V
    //   12: astore 5
    //   14: aload_0
    //   15: getfield 83	com/google/firebase/iid/Rpc:wp	Landroid/support/v4/util/SimpleArrayMap;
    //   18: astore 4
    //   20: aload 4
    //   22: monitorenter
    //   23: aload_0
    //   24: getfield 83	com/google/firebase/iid/Rpc:wp	Landroid/support/v4/util/SimpleArrayMap;
    //   27: aload_3
    //   28: aload 5
    //   30: invokevirtual 312	android/support/v4/util/SimpleArrayMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   33: pop
    //   34: aload 4
    //   36: monitorexit
    //   37: aload_0
    //   38: aload_1
    //   39: aload_2
    //   40: aload_3
    //   41: invokevirtual 315	com/google/firebase/iid/Rpc:zza	(Landroid/os/Bundle;Ljava/security/KeyPair;Ljava/lang/String;)V
    //   44: aload 5
    //   46: invokevirtual 319	com/google/firebase/iid/Rpc$zza:zzakm	()Landroid/content/Intent;
    //   49: astore_2
    //   50: aload_0
    //   51: getfield 83	com/google/firebase/iid/Rpc:wp	Landroid/support/v4/util/SimpleArrayMap;
    //   54: astore_1
    //   55: aload_1
    //   56: monitorenter
    //   57: aload_0
    //   58: getfield 83	com/google/firebase/iid/Rpc:wp	Landroid/support/v4/util/SimpleArrayMap;
    //   61: aload_3
    //   62: invokevirtual 293	android/support/v4/util/SimpleArrayMap:remove	(Ljava/lang/Object;)Ljava/lang/Object;
    //   65: pop
    //   66: aload_1
    //   67: monitorexit
    //   68: aload_2
    //   69: areturn
    //   70: astore_1
    //   71: aload 4
    //   73: monitorexit
    //   74: aload_1
    //   75: athrow
    //   76: astore_2
    //   77: aload_1
    //   78: monitorexit
    //   79: aload_2
    //   80: athrow
    //   81: astore_2
    //   82: aload_0
    //   83: getfield 83	com/google/firebase/iid/Rpc:wp	Landroid/support/v4/util/SimpleArrayMap;
    //   86: astore_1
    //   87: aload_1
    //   88: monitorenter
    //   89: aload_0
    //   90: getfield 83	com/google/firebase/iid/Rpc:wp	Landroid/support/v4/util/SimpleArrayMap;
    //   93: aload_3
    //   94: invokevirtual 293	android/support/v4/util/SimpleArrayMap:remove	(Ljava/lang/Object;)Ljava/lang/Object;
    //   97: pop
    //   98: aload_1
    //   99: monitorexit
    //   100: aload_2
    //   101: athrow
    //   102: astore_2
    //   103: aload_1
    //   104: monitorexit
    //   105: aload_2
    //   106: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	107	0	this	Rpc
    //   0	107	2	paramKeyPair	KeyPair
    //   3	91	3	str	String
    //   18	54	4	localSimpleArrayMap	SimpleArrayMap
    //   12	33	5	localzza	zza
    // Exception table:
    //   from	to	target	type
    //   23	37	70	finally
    //   71	74	70	finally
    //   57	68	76	finally
    //   77	79	76	finally
    //   44	50	81	finally
    //   89	100	102	finally
    //   103	105	102	finally
  }
  
  private void zzb(String paramString, Intent paramIntent)
  {
    zzb localzzb;
    synchronized (this.wp)
    {
      localzzb = (zzb)this.wp.remove(paramString);
      if (localzzb == null)
      {
        paramString = String.valueOf(paramString);
        if (paramString.length() != 0)
        {
          paramString = "Missing callback for ".concat(paramString);
          Log.w("InstanceID/Rpc", paramString);
          return;
        }
        paramString = new String("Missing callback for ");
      }
    }
    localzzb.zzU(paramIntent);
  }
  
  private void zzeF(String paramString)
  {
    if (!"com.google.android.gsf".equals(zzbAS)) {}
    do
    {
      return;
      this.zzbBb += 1;
    } while (this.zzbBb < 3);
    if (this.zzbBb == 3) {
      this.zzbBc = (new Random().nextInt(1000) + 1000);
    }
    this.zzbBc *= 2;
    this.zzbBd = (SystemClock.elapsedRealtime() + this.zzbBc);
    int i = this.zzbBc;
    Log.w("InstanceID/Rpc", String.valueOf(paramString).length() + 31 + "Backoff due to " + paramString + " for " + i);
  }
  
  public void handleIncomingMessenger(Message paramMessage)
  {
    if (paramMessage == null) {
      return;
    }
    if ((paramMessage.obj instanceof Intent))
    {
      Object localObject = (Intent)paramMessage.obj;
      ((Intent)localObject).setExtrasClassLoader(MessengerCompat.class.getClassLoader());
      if (((Intent)localObject).hasExtra("google.messenger"))
      {
        localObject = ((Intent)localObject).getParcelableExtra("google.messenger");
        if ((localObject instanceof MessengerCompat)) {
          this.zzbAY = ((MessengerCompat)localObject);
        }
        if ((localObject instanceof Messenger)) {
          this.zzbAX = ((Messenger)localObject);
        }
      }
      zzE((Intent)paramMessage.obj);
      return;
    }
    Log.w("InstanceID/Rpc", "Dropping invalid message");
  }
  
  protected void sendRequest(Intent paramIntent, String paramString)
  {
    this.zzbAZ = SystemClock.elapsedRealtime();
    paramIntent.putExtra("kid", String.valueOf(paramString).length() + 5 + "|ID|" + paramString + "|");
    paramIntent.putExtra("X-kid", String.valueOf(paramString).length() + 5 + "|ID|" + paramString + "|");
    boolean bool = "com.google.android.gsf".equals(zzbAS);
    if (Log.isLoggable("InstanceID/Rpc", 3))
    {
      paramString = String.valueOf(paramIntent.getExtras());
      Log.d("InstanceID/Rpc", String.valueOf(paramString).length() + 8 + "Sending " + paramString);
    }
    if (bool)
    {
      this.zzqm.startService(paramIntent);
      return;
    }
    paramIntent.putExtra("google.messenger", this.zzbxz);
    if ((this.zzbAX != null) || (this.zzbAY != null))
    {
      paramString = Message.obtain();
      paramString.obj = paramIntent;
      try
      {
        if (this.zzbAX == null) {
          break label249;
        }
        this.zzbAX.send(paramString);
        return;
      }
      catch (RemoteException paramString)
      {
        if (Log.isLoggable("InstanceID/Rpc", 3)) {
          Log.d("InstanceID/Rpc", "Messenger failed, fallback to startService");
        }
      }
    }
    else
    {
      this.zzqm.startService(paramIntent);
      return;
    }
    label249:
    this.zzbAY.send(paramString);
  }
  
  void zzB(Intent paramIntent)
  {
    try
    {
      if (this.zzbxv == null)
      {
        Intent localIntent = new Intent();
        localIntent.setPackage("com.google.example.invalidpackage");
        this.zzbxv = PendingIntent.getBroadcast(this.zzqm, 0, localIntent, 0);
      }
      paramIntent.putExtra("app", this.zzbxv);
      return;
    }
    finally {}
  }
  
  String zzC(Intent paramIntent)
    throws IOException
  {
    if (paramIntent == null) {
      throw new IOException("SERVICE_NOT_AVAILABLE");
    }
    String str2 = paramIntent.getStringExtra("registration_id");
    String str1 = str2;
    if (str2 == null) {
      str1 = paramIntent.getStringExtra("unregistered");
    }
    if (str1 == null)
    {
      str1 = paramIntent.getStringExtra("error");
      if (str1 != null) {
        throw new IOException(str1);
      }
      paramIntent = String.valueOf(paramIntent.getExtras());
      Log.w("InstanceID/Rpc", String.valueOf(paramIntent).length() + 29 + "Unexpected response from GCM " + paramIntent, new Throwable());
      throw new IOException("SERVICE_NOT_AVAILABLE");
    }
    return str1;
  }
  
  void zzD(Intent paramIntent)
  {
    Object localObject2 = paramIntent.getStringExtra("error");
    if (localObject2 == null)
    {
      paramIntent = String.valueOf(paramIntent.getExtras());
      Log.w("InstanceID/Rpc", String.valueOf(paramIntent).length() + 49 + "Unexpected response, no error or registration id " + paramIntent);
      return;
    }
    Object localObject1;
    label93:
    label160:
    Object localObject3;
    if (Log.isLoggable("InstanceID/Rpc", 3))
    {
      localObject1 = String.valueOf(localObject2);
      if (((String)localObject1).length() != 0)
      {
        localObject1 = "Received InstanceID error ".concat((String)localObject1);
        Log.d("InstanceID/Rpc", (String)localObject1);
      }
    }
    else
    {
      if (!((String)localObject2).startsWith("|")) {
        break label385;
      }
      Object localObject4 = ((String)localObject2).split("\\|");
      if (!"ID".equals(localObject4[1]))
      {
        localObject1 = String.valueOf(localObject2);
        if (((String)localObject1).length() == 0) {
          break label330;
        }
        localObject1 = "Unexpected structured response ".concat((String)localObject1);
        Log.w("InstanceID/Rpc", (String)localObject1);
      }
      if (localObject4.length <= 2) {
        break label345;
      }
      localObject3 = localObject4[2];
      localObject4 = localObject4[3];
      localObject2 = localObject3;
      localObject1 = localObject4;
      if (((String)localObject4).startsWith(":"))
      {
        localObject1 = ((String)localObject4).substring(1);
        localObject2 = localObject3;
      }
      label218:
      paramIntent.putExtra("error", (String)localObject1);
    }
    for (;;)
    {
      zzaN((String)localObject2, (String)localObject1);
      long l = paramIntent.getLongExtra("Retry-After", 0L);
      if (l > 0L)
      {
        this.zzbBa = SystemClock.elapsedRealtime();
        this.zzbBc = ((int)l * 1000);
        this.zzbBd = (SystemClock.elapsedRealtime() + this.zzbBc);
        int i = this.zzbBc;
        Log.w("InstanceID/Rpc", 52 + "Explicit request from server to backoff: " + i);
        return;
        localObject1 = new String("Received InstanceID error ");
        break label93;
        label330:
        localObject1 = new String("Unexpected structured response ");
        break label160;
        label345:
        localObject1 = "UNKNOWN";
        localObject2 = null;
        break label218;
      }
      if ((!"SERVICE_NOT_AVAILABLE".equals(localObject1)) && (!"AUTHENTICATION_FAILED".equals(localObject1))) {
        break;
      }
      zzeF((String)localObject1);
      return;
      label385:
      localObject3 = null;
      localObject1 = localObject2;
      localObject2 = localObject3;
    }
  }
  
  void zzE(Intent paramIntent)
  {
    if (paramIntent == null) {
      if (Log.isLoggable("InstanceID/Rpc", 3)) {
        Log.d("InstanceID/Rpc", "Unexpected response: null");
      }
    }
    do
    {
      return;
      if ("com.google.android.c2dm.intent.REGISTRATION".equals(paramIntent.getAction())) {
        break;
      }
    } while (!Log.isLoggable("InstanceID/Rpc", 3));
    paramIntent = String.valueOf(paramIntent.getAction());
    if (paramIntent.length() != 0) {}
    for (paramIntent = "Unexpected response ".concat(paramIntent);; paramIntent = new String("Unexpected response "))
    {
      Log.d("InstanceID/Rpc", paramIntent);
      return;
    }
    String str = paramIntent.getStringExtra("registration_id");
    Object localObject1 = str;
    if (str == null) {
      localObject1 = paramIntent.getStringExtra("unregistered");
    }
    if (localObject1 == null)
    {
      zzD(paramIntent);
      return;
    }
    this.zzbAZ = SystemClock.elapsedRealtime();
    this.zzbBd = 0L;
    this.zzbBb = 0;
    this.zzbBc = 0;
    Object localObject2;
    if (((String)localObject1).startsWith("|"))
    {
      localObject2 = ((String)localObject1).split("\\|");
      if (!"ID".equals(localObject2[1]))
      {
        localObject1 = String.valueOf(localObject1);
        if (((String)localObject1).length() != 0)
        {
          localObject1 = "Unexpected structured response ".concat((String)localObject1);
          label196:
          Log.w("InstanceID/Rpc", (String)localObject1);
        }
      }
      else
      {
        str = localObject2[2];
        if (localObject2.length > 4)
        {
          if (!"SYNC".equals(localObject2[3])) {
            break label314;
          }
          FirebaseInstanceId.zzbF(this.zzqm);
        }
        label235:
        localObject2 = localObject2[(localObject2.length - 1)];
        localObject1 = localObject2;
        if (((String)localObject2).startsWith(":")) {
          localObject1 = ((String)localObject2).substring(1);
        }
        paramIntent.putExtra("registration_id", (String)localObject1);
      }
    }
    for (localObject1 = str;; localObject1 = null)
    {
      if (localObject1 == null)
      {
        if (!Log.isLoggable("InstanceID/Rpc", 3)) {
          break;
        }
        Log.d("InstanceID/Rpc", "Ignoring response without a request ID");
        return;
        localObject1 = new String("Unexpected structured response ");
        break label196;
        label314:
        if (!"RST".equals(localObject2[3])) {
          break label235;
        }
        FirebaseInstanceId.zza(this.zzqm, zzd.zza(this.zzqm, null).zzakj());
        paramIntent.removeExtra("registration_id");
        zzb(str, paramIntent);
        return;
      }
      zzb((String)localObject1, paramIntent);
      return;
    }
  }
  
  void zzJT()
  {
    if (this.zzbxz != null) {
      return;
    }
    findAppIDPackage(this.zzqm);
    this.zzbxz = new Messenger(new Handler(Looper.getMainLooper())
    {
      public void handleMessage(Message paramAnonymousMessage)
      {
        Rpc.this.handleIncomingMessenger(paramAnonymousMessage);
      }
    });
  }
  
  Intent zza(Bundle paramBundle, KeyPair paramKeyPair)
    throws IOException
  {
    Intent localIntent = zzb(paramBundle, paramKeyPair);
    Object localObject = localIntent;
    if (localIntent != null)
    {
      localObject = localIntent;
      if (localIntent.hasExtra("google.messenger"))
      {
        paramBundle = zzb(paramBundle, paramKeyPair);
        localObject = paramBundle;
        if (paramBundle != null)
        {
          localObject = paramBundle;
          if (paramBundle.hasExtra("google.messenger")) {
            localObject = null;
          }
        }
      }
    }
    return (Intent)localObject;
  }
  
  public void zza(Bundle paramBundle, KeyPair paramKeyPair, String paramString)
    throws IOException
  {
    long l1 = SystemClock.elapsedRealtime();
    if ((this.zzbBd != 0L) && (l1 <= this.zzbBd))
    {
      long l2 = this.zzbBd;
      int i = this.zzbBc;
      Log.w("InstanceID/Rpc", 78 + "Backoff mode, next request attempt: " + (l2 - l1) + " interval: " + i);
      throw new IOException("RETRY_LATER");
    }
    zzJT();
    if (zzbAS == null) {
      throw new IOException("MISSING_INSTANCEID_SERVICE");
    }
    this.zzbAZ = SystemClock.elapsedRealtime();
    Intent localIntent = new Intent("com.google.android.c2dm.intent.REGISTER");
    localIntent.setPackage(zzbAS);
    paramBundle.putString("gmsv", Integer.toString(FirebaseInstanceId.zzY(this.zzqm, findAppIDPackage(this.zzqm))));
    paramBundle.putString("osv", Integer.toString(Build.VERSION.SDK_INT));
    paramBundle.putString("app_ver", Integer.toString(FirebaseInstanceId.zzcB(this.zzqm)));
    paramBundle.putString("app_ver_name", FirebaseInstanceId.zzbE(this.zzqm));
    paramBundle.putString("cliv", "fiid-10298000");
    paramBundle.putString("appid", FirebaseInstanceId.zza(paramKeyPair));
    String str = FirebaseInstanceId.zzcA(this.zzqm);
    if (str != null) {
      paramBundle.putString("gmp_app_id", str);
    }
    str = FirebaseInstanceId.zzB(paramKeyPair.getPublic().getEncoded());
    paramBundle.putString("pub2", str);
    paramBundle.putString("sig", zza(paramKeyPair, new String[] { this.zzqm.getPackageName(), str }));
    localIntent.putExtras(paramBundle);
    zzB(localIntent);
    sendRequest(localIntent, paramString);
  }
  
  private static class zza
    implements Rpc.zzb
  {
    private Intent intent;
    private final ConditionVariable wr = new ConditionVariable();
    private String ws;
    
    public void onError(String paramString)
    {
      this.ws = paramString;
      this.wr.open();
    }
    
    public void zzU(Intent paramIntent)
    {
      this.intent = paramIntent;
      this.wr.open();
    }
    
    public Intent zzakm()
      throws IOException
    {
      if (!this.wr.block(30000L))
      {
        Log.w("InstanceID/Rpc", "No response");
        throw new IOException("TIMEOUT");
      }
      if (this.ws != null) {
        throw new IOException(this.ws);
      }
      return this.intent;
    }
  }
  
  private static abstract interface zzb
  {
    public abstract void onError(String paramString);
    
    public abstract void zzU(Intent paramIntent);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/firebase/iid/Rpc.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */