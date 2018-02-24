package com.google.firebase.iid;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.VisibleForTesting;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import com.google.android.gms.iid.MessengerCompat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class zzb
  extends Service
{
  MessengerCompat zzbAJ = new MessengerCompat(new Handler(Looper.getMainLooper())
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      int i = MessengerCompat.zzc(paramAnonymousMessage);
      Rpc.findAppIDPackage(zzb.this);
      zzb.this.getPackageManager();
      if ((i != Rpc.zzbAU) && (i != Rpc.zzbAT))
      {
        int j = Rpc.zzbAT;
        int k = Rpc.zzbAU;
        Log.w("FirebaseInstanceId", 77 + "Message from unexpected caller " + i + " mine=" + j + " appid=" + k);
        return;
      }
      zzb.this.zzp((Intent)paramAnonymousMessage.obj);
    }
  });
  @VisibleForTesting
  final ExecutorService zzbOE = Executors.newSingleThreadExecutor();
  private int zzbxb;
  private int zzbxc = 0;
  private final Object zzrU = new Object();
  
  private void zzQ(Intent arg1)
  {
    if (??? != null) {
      WakefulBroadcastReceiver.completeWakefulIntent(???);
    }
    synchronized (this.zzrU)
    {
      this.zzbxc -= 1;
      if (this.zzbxc == 0) {
        zzmZ(this.zzbxb);
      }
      return;
    }
  }
  
  public final IBinder onBind(Intent paramIntent)
  {
    if ((paramIntent != null) && ("com.google.firebase.INSTANCE_ID_EVENT".equals(paramIntent.getAction()))) {
      return this.zzbAJ.getBinder();
    }
    return null;
  }
  
  public final int onStartCommand(final Intent paramIntent, int paramInt1, int paramInt2)
  {
    synchronized (this.zzrU)
    {
      this.zzbxb = paramInt2;
      this.zzbxc += 1;
      ??? = zzP(paramIntent);
      if (??? == null)
      {
        zzQ(paramIntent);
        return 2;
      }
    }
    if (zzR((Intent)???))
    {
      zzQ(paramIntent);
      return 2;
    }
    this.zzbOE.execute(new Runnable()
    {
      public void run()
      {
        zzb.this.zzp(localObject);
        zzb.zza(zzb.this, paramIntent);
      }
    });
    return 3;
  }
  
  protected abstract Intent zzP(Intent paramIntent);
  
  public boolean zzR(Intent paramIntent)
  {
    return false;
  }
  
  boolean zzmZ(int paramInt)
  {
    return stopSelfResult(paramInt);
  }
  
  public abstract void zzp(Intent paramIntent);
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/firebase/iid/zzb.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */