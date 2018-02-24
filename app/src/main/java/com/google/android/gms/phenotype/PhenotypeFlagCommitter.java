package com.google.android.gms.phenotype;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public abstract class PhenotypeFlagCommitter
{
  public static final String SHARED_PREFS_SERVER_TOKEN_KEY_NAME = "__phenotype_server_token";
  public static final String SHARED_PREFS_SNAPSHOT_TOKEN_KEY_NAME = "__phenotype_snapshot_token";
  public static final String TAG = "PhenotypeFlagCommitter";
  protected final PhenotypeClient mClient;
  protected final String mPackageName;
  private long zzVd;
  
  @Deprecated
  public PhenotypeFlagCommitter(GoogleApiClient paramGoogleApiClient, PhenotypeApi paramPhenotypeApi, String paramString)
  {
    this(Phenotype.getInstance(paramGoogleApiClient.getContext()), paramString);
  }
  
  @Deprecated
  public PhenotypeFlagCommitter(GoogleApiClient paramGoogleApiClient, String paramString)
  {
    this(Phenotype.getInstance(paramGoogleApiClient.getContext()), paramString);
  }
  
  public PhenotypeFlagCommitter(PhenotypeClient paramPhenotypeClient, String paramString)
  {
    this.mClient = paramPhenotypeClient;
    this.mPackageName = paramString;
    this.zzVd = 2000L;
  }
  
  public static void writeToSharedPrefs(SharedPreferences paramSharedPreferences, Configurations paramConfigurations)
  {
    paramSharedPreferences = paramSharedPreferences.edit();
    if (!paramConfigurations.isDelta) {
      paramSharedPreferences.clear();
    }
    Configuration[] arrayOfConfiguration = paramConfigurations.configurations;
    int j = arrayOfConfiguration.length;
    int i = 0;
    while (i < j)
    {
      zza(paramSharedPreferences, arrayOfConfiguration[i]);
      i += 1;
    }
    paramSharedPreferences.putString("__phenotype_server_token", paramConfigurations.serverToken);
    paramSharedPreferences.putString("__phenotype_snapshot_token", paramConfigurations.snapshotToken);
    if (!paramSharedPreferences.commit()) {
      Log.w("PhenotypeFlagCommitter", "Failed to commit Phenotype configs to SharedPreferences!");
    }
  }
  
  public static void writeToSharedPrefs(SharedPreferences paramSharedPreferences, Configuration... paramVarArgs)
  {
    paramSharedPreferences = paramSharedPreferences.edit();
    int j = paramVarArgs.length;
    int i = 0;
    while (i < j)
    {
      zza(paramSharedPreferences, paramVarArgs[i]);
      i += 1;
    }
    if (!paramSharedPreferences.commit()) {
      Log.w("PhenotypeFlagCommitter", "Failed to commit Phenotype configs to SharedPreferences!");
    }
  }
  
  /* Error */
  private boolean zzG(String paramString, int paramInt)
  {
    // Byte code:
    //   0: iload_2
    //   1: ifgt +47 -> 48
    //   4: aload_0
    //   5: getfield 54	com/google/android/gms/phenotype/PhenotypeFlagCommitter:mPackageName	Ljava/lang/String;
    //   8: invokestatic 122	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   11: astore_1
    //   12: aload_1
    //   13: invokevirtual 126	java/lang/String:length	()I
    //   16: ifeq +19 -> 35
    //   19: ldc -128
    //   21: aload_1
    //   22: invokevirtual 132	java/lang/String:concat	(Ljava/lang/String;)Ljava/lang/String;
    //   25: astore_1
    //   26: ldc 21
    //   28: aload_1
    //   29: invokestatic 107	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   32: pop
    //   33: iconst_0
    //   34: ireturn
    //   35: new 118	java/lang/String
    //   38: dup
    //   39: ldc -128
    //   41: invokespecial 135	java/lang/String:<init>	(Ljava/lang/String;)V
    //   44: astore_1
    //   45: goto -19 -> 26
    //   48: aload_0
    //   49: getfield 52	com/google/android/gms/phenotype/PhenotypeFlagCommitter:mClient	Lcom/google/android/gms/phenotype/PhenotypeClient;
    //   52: aload_0
    //   53: getfield 54	com/google/android/gms/phenotype/PhenotypeFlagCommitter:mPackageName	Ljava/lang/String;
    //   56: aload_1
    //   57: aload_0
    //   58: invokevirtual 139	com/google/android/gms/phenotype/PhenotypeFlagCommitter:getSnapshotToken	()Ljava/lang/String;
    //   61: invokevirtual 145	com/google/android/gms/phenotype/PhenotypeClient:getConfigurationSnapshot	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Lcom/google/android/gms/tasks/Task;
    //   64: astore_3
    //   65: aload_3
    //   66: aload_0
    //   67: getfield 58	com/google/android/gms/phenotype/PhenotypeFlagCommitter:zzVd	J
    //   70: getstatic 151	java/util/concurrent/TimeUnit:MILLISECONDS	Ljava/util/concurrent/TimeUnit;
    //   73: invokestatic 157	com/google/android/gms/tasks/Tasks:await	(Lcom/google/android/gms/tasks/Task;JLjava/util/concurrent/TimeUnit;)Ljava/lang/Object;
    //   76: pop
    //   77: aload_0
    //   78: aload_3
    //   79: invokevirtual 163	com/google/android/gms/tasks/Task:getResult	()Ljava/lang/Object;
    //   82: checkcast 68	com/google/android/gms/phenotype/Configurations
    //   85: invokevirtual 167	com/google/android/gms/phenotype/PhenotypeFlagCommitter:handleConfigurations	(Lcom/google/android/gms/phenotype/Configurations;)V
    //   88: aload_0
    //   89: getfield 52	com/google/android/gms/phenotype/PhenotypeFlagCommitter:mClient	Lcom/google/android/gms/phenotype/PhenotypeClient;
    //   92: aload_3
    //   93: invokevirtual 163	com/google/android/gms/tasks/Task:getResult	()Ljava/lang/Object;
    //   96: checkcast 68	com/google/android/gms/phenotype/Configurations
    //   99: getfield 95	com/google/android/gms/phenotype/Configurations:snapshotToken	Ljava/lang/String;
    //   102: invokevirtual 171	com/google/android/gms/phenotype/PhenotypeClient:commitToConfiguration	(Ljava/lang/String;)Lcom/google/android/gms/tasks/Task;
    //   105: astore_3
    //   106: aload_3
    //   107: aload_0
    //   108: getfield 58	com/google/android/gms/phenotype/PhenotypeFlagCommitter:zzVd	J
    //   111: getstatic 151	java/util/concurrent/TimeUnit:MILLISECONDS	Ljava/util/concurrent/TimeUnit;
    //   114: invokestatic 157	com/google/android/gms/tasks/Tasks:await	(Lcom/google/android/gms/tasks/Task;JLjava/util/concurrent/TimeUnit;)Ljava/lang/Object;
    //   117: pop
    //   118: iconst_1
    //   119: ireturn
    //   120: astore_1
    //   121: aload_0
    //   122: getfield 54	com/google/android/gms/phenotype/PhenotypeFlagCommitter:mPackageName	Ljava/lang/String;
    //   125: astore_3
    //   126: ldc 21
    //   128: new 173	java/lang/StringBuilder
    //   131: dup
    //   132: aload_3
    //   133: invokestatic 122	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   136: invokevirtual 126	java/lang/String:length	()I
    //   139: bipush 31
    //   141: iadd
    //   142: invokespecial 176	java/lang/StringBuilder:<init>	(I)V
    //   145: ldc -78
    //   147: invokevirtual 182	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   150: aload_3
    //   151: invokevirtual 182	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   154: ldc -72
    //   156: invokevirtual 182	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   159: invokevirtual 187	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   162: aload_1
    //   163: invokestatic 191	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   166: pop
    //   167: iconst_0
    //   168: ireturn
    //   169: astore_3
    //   170: aload_0
    //   171: getfield 54	com/google/android/gms/phenotype/PhenotypeFlagCommitter:mPackageName	Ljava/lang/String;
    //   174: astore 4
    //   176: ldc 21
    //   178: new 173	java/lang/StringBuilder
    //   181: dup
    //   182: aload 4
    //   184: invokestatic 122	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   187: invokevirtual 126	java/lang/String:length	()I
    //   190: bipush 41
    //   192: iadd
    //   193: invokespecial 176	java/lang/StringBuilder:<init>	(I)V
    //   196: ldc -63
    //   198: invokevirtual 182	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   201: aload 4
    //   203: invokevirtual 182	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   206: ldc -61
    //   208: invokevirtual 182	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   211: invokevirtual 187	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   214: aload_3
    //   215: invokestatic 197	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   218: pop
    //   219: aload_0
    //   220: aload_1
    //   221: iload_2
    //   222: iconst_1
    //   223: isub
    //   224: invokespecial 199	com/google/android/gms/phenotype/PhenotypeFlagCommitter:zzG	(Ljava/lang/String;I)Z
    //   227: ireturn
    //   228: astore_3
    //   229: goto -59 -> 170
    //   232: astore_3
    //   233: goto -63 -> 170
    //   236: astore_1
    //   237: goto -116 -> 121
    //   240: astore_1
    //   241: goto -120 -> 121
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	244	0	this	PhenotypeFlagCommitter
    //   0	244	1	paramString	String
    //   0	244	2	paramInt	int
    //   64	87	3	localObject	Object
    //   169	46	3	localInterruptedException	InterruptedException
    //   228	1	3	localExecutionException	java.util.concurrent.ExecutionException
    //   232	1	3	localTimeoutException	java.util.concurrent.TimeoutException
    //   174	28	4	str	String
    // Exception table:
    //   from	to	target	type
    //   65	77	120	java/lang/InterruptedException
    //   106	118	169	java/lang/InterruptedException
    //   106	118	228	java/util/concurrent/ExecutionException
    //   106	118	232	java/util/concurrent/TimeoutException
    //   65	77	236	java/util/concurrent/ExecutionException
    //   65	77	240	java/util/concurrent/TimeoutException
  }
  
  private static void zza(SharedPreferences.Editor paramEditor, Configuration paramConfiguration)
  {
    int j = 0;
    if (paramConfiguration == null) {}
    int k;
    int i;
    do
    {
      return;
      arrayOfString = paramConfiguration.deleteFlags;
      k = arrayOfString.length;
      i = 0;
      while (i < k)
      {
        paramEditor.remove(arrayOfString[i]);
        i += 1;
      }
      paramConfiguration = paramConfiguration.flags;
      k = paramConfiguration.length;
      i = j;
    } while (i >= k);
    String[] arrayOfString = paramConfiguration[i];
    switch (arrayOfString.flagValueType)
    {
    }
    for (;;)
    {
      i += 1;
      break;
      paramEditor.putLong(arrayOfString.name, arrayOfString.getLong());
      continue;
      paramEditor.putBoolean(arrayOfString.name, arrayOfString.getBoolean());
      continue;
      paramEditor.putFloat(arrayOfString.name, (float)arrayOfString.getDouble());
      continue;
      paramEditor.putString(arrayOfString.name, arrayOfString.getString());
      continue;
      String str = Base64.encodeToString(arrayOfString.getBytesValue(), 3);
      paramEditor.putString(arrayOfString.name, str);
    }
  }
  
  private void zza(final String paramString, final Callback paramCallback, final int paramInt)
  {
    if (paramInt <= 0)
    {
      paramString = String.valueOf(this.mPackageName);
      if (paramString.length() != 0) {}
      for (paramString = "No more attempts remaining, giving up for ".concat(paramString);; paramString = new String("No more attempts remaining, giving up for "))
      {
        Log.e("PhenotypeFlagCommitter", paramString);
        if (paramCallback != null) {
          paramCallback.onFinish(false);
        }
        return;
      }
    }
    this.mClient.getConfigurationSnapshot(this.mPackageName, paramString, getSnapshotToken()).addOnCompleteListener(new OnCompleteListener()
    {
      public void onComplete(@NonNull Task<Configurations> paramAnonymousTask)
      {
        if (!paramAnonymousTask.isSuccessful())
        {
          paramAnonymousTask = PhenotypeFlagCommitter.this.mPackageName;
          Log.e("PhenotypeFlagCommitter", String.valueOf(paramAnonymousTask).length() + 31 + "Retrieving snapshot for " + paramAnonymousTask + " failed");
          if (paramCallback != null) {
            paramCallback.onFinish(false);
          }
          return;
        }
        PhenotypeFlagCommitter.this.handleConfigurations((Configurations)paramAnonymousTask.getResult());
        PhenotypeFlagCommitter.this.mClient.commitToConfiguration(((Configurations)paramAnonymousTask.getResult()).snapshotToken).addOnCompleteListener(new OnCompleteListener()
        {
          public void onComplete(@NonNull Task<Void> paramAnonymous2Task)
          {
            if (!paramAnonymous2Task.isSuccessful())
            {
              paramAnonymous2Task = PhenotypeFlagCommitter.this.mPackageName;
              Log.w("PhenotypeFlagCommitter", String.valueOf(paramAnonymous2Task).length() + 41 + "Committing snapshot for " + paramAnonymous2Task + " failed, retrying");
              PhenotypeFlagCommitter.zza(PhenotypeFlagCommitter.this, PhenotypeFlagCommitter.1.this.zzcfk, PhenotypeFlagCommitter.1.this.zzcfI, PhenotypeFlagCommitter.1.this.zzcfJ - 1);
            }
            while (PhenotypeFlagCommitter.1.this.zzcfI == null) {
              return;
            }
            PhenotypeFlagCommitter.1.this.zzcfI.onFinish(true);
          }
        });
      }
    });
  }
  
  public boolean commitForUser(String paramString)
  {
    zzac.zzC(paramString);
    return zzG(paramString, 3);
  }
  
  public final void commitForUserAsync(String paramString, Callback paramCallback)
  {
    zzac.zzC(paramString);
    zza(paramString, paramCallback, 3);
  }
  
  protected String getSnapshotToken()
  {
    return null;
  }
  
  protected abstract void handleConfigurations(Configurations paramConfigurations);
  
  public void setTimeoutMillis(long paramLong)
  {
    this.zzVd = paramLong;
  }
  
  public static abstract interface Callback
  {
    public abstract void onFinish(boolean paramBoolean);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/phenotype/PhenotypeFlagCommitter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */