package com.google.android.gms.internal;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzg;
import com.google.android.gms.common.internal.zzl;
import com.google.android.gms.usagereporting.UsageReportingApi.OptInOptionsChangedListener;
import com.google.android.gms.usagereporting.UsageReportingApi.OptInOptionsResult;
import com.google.android.gms.usagereporting.UsageReportingOptInOptions;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class zzbry
  extends zzl<zzbrv>
{
  private final AtomicReference<zzf> zzcFR = new AtomicReference();
  
  public zzbry(Context paramContext, Looper paramLooper, zzg paramzzg, GoogleApiClient.ConnectionCallbacks paramConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener)
  {
    super(paramContext, paramLooper, 41, paramzzg, paramConnectionCallbacks, paramOnConnectionFailedListener);
  }
  
  public static boolean isUsageReportingServiceAvailable(Context paramContext)
  {
    boolean bool = false;
    if (!paramContext.getPackageManager().queryIntentServices(new Intent("com.google.android.gms.usagereporting.service.START").setPackage("com.google.android.gms"), 0).isEmpty()) {
      bool = true;
    }
    return bool;
  }
  
  public void disconnect()
  {
    try
    {
      zzf localzzf = (zzf)this.zzcFR.getAndSet(null);
      if (localzzf != null)
      {
        zzb localzzb = new zzb(null);
        ((zzbrv)zzzw()).zzb(localzzf, localzzb);
      }
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        Log.e("UsageReportingClientImpl", "disconnect(): Could not unregister listener from remote:", localRemoteException);
      }
    }
    super.disconnect();
  }
  
  public void zzM(zzyr.zzb<UsageReportingApi.OptInOptionsResult> paramzzb)
    throws RemoteException
  {
    ((zzbrv)zzzw()).zza(new zzc(paramzzb));
  }
  
  public void zza(zzzw<UsageReportingApi.OptInOptionsChangedListener> paramzzzw, zzyr.zzb<Status> paramzzb)
    throws RemoteException
  {
    if (paramzzzw == null) {}
    zze localzze;
    for (paramzzzw = null;; paramzzzw = new zzf(paramzzzw))
    {
      localzze = new zze((zzbrv)zzzw(), paramzzb, this.zzcFR, paramzzzw);
      zzf localzzf = (zzf)this.zzcFR.get();
      if (localzzf == null) {
        break;
      }
      ((zzbrv)zzzw()).zzb(localzzf, localzze);
      return;
    }
    if (paramzzzw == null)
    {
      paramzzb.setResult(Status.zzaLc);
      return;
    }
    this.zzcFR.set(paramzzzw);
    ((zzbrv)zzzw()).zza(paramzzzw, localzze);
  }
  
  public void zza(UsageReportingOptInOptions paramUsageReportingOptInOptions, zzyr.zzb<Status> paramzzb)
    throws RemoteException
  {
    ((zzbrv)zzzw()).zza(paramUsageReportingOptInOptions, new zzd(paramzzb));
  }
  
  protected String zzeJ()
  {
    return "com.google.android.gms.usagereporting.service.START";
  }
  
  protected String zzeK()
  {
    return "com.google.android.gms.usagereporting.internal.IUsageReportingService";
  }
  
  protected zzbrv zzjb(IBinder paramIBinder)
  {
    return zzbrv.zza.zzja(paramIBinder);
  }
  
  private static class zza
    extends zzbrt.zza
  {
    public void zza(Status paramStatus, UsageReportingOptInOptions paramUsageReportingOptInOptions)
      throws RemoteException
    {
      throw new IllegalStateException("Not implemented.");
    }
    
    public void zzff(Status paramStatus)
      throws RemoteException
    {
      throw new IllegalStateException("Not implemented.");
    }
    
    public void zzfg(Status paramStatus)
      throws RemoteException
    {
      throw new IllegalStateException("Not implemented.");
    }
    
    public void zzfh(Status paramStatus)
      throws RemoteException
    {
      throw new IllegalStateException("Not implemented.");
    }
  }
  
  private static final class zzb
    extends zzbry.zza
  {
    private zzb()
    {
      super();
    }
    
    public void zzfh(Status paramStatus)
    {
      if (!paramStatus.isSuccess())
      {
        paramStatus = String.valueOf(paramStatus);
        Log.e("UsageReportingClientImpl", String.valueOf(paramStatus).length() + 52 + "disconnect(): Could not unregister listener: status=" + paramStatus);
      }
    }
  }
  
  private static final class zzc
    extends zzbry.zza
  {
    private final zzyr.zzb<UsageReportingApi.OptInOptionsResult> zzaOS;
    
    public zzc(zzyr.zzb<UsageReportingApi.OptInOptionsResult> paramzzb)
    {
      super();
      this.zzaOS = paramzzb;
    }
    
    public void zza(Status paramStatus, UsageReportingOptInOptions paramUsageReportingOptInOptions)
      throws RemoteException
    {
      if (!paramStatus.isSuccess())
      {
        this.zzaOS.setResult(new zzbrw(paramStatus, null));
        return;
      }
      this.zzaOS.setResult(new zzbrw(Status.zzaLc, paramUsageReportingOptInOptions));
    }
  }
  
  private static final class zzd
    extends zzbry.zza
  {
    private final zzyr.zzb<Status> zzaOS;
    
    public zzd(zzyr.zzb<Status> paramzzb)
    {
      super();
      this.zzaOS = paramzzb;
    }
    
    public void zzff(Status paramStatus)
      throws RemoteException
    {
      this.zzaOS.setResult(paramStatus);
    }
  }
  
  private static final class zze
    extends zzbry.zza
  {
    private final zzyr.zzb<Status> zzaOS;
    private final zzbrv zzcFS;
    private final AtomicReference<zzbry.zzf> zzcFT;
    private final zzbry.zzf zzcFU;
    
    public zze(zzbrv paramzzbrv, zzyr.zzb<Status> paramzzb, AtomicReference<zzbry.zzf> paramAtomicReference, zzbry.zzf paramzzf)
    {
      super();
      this.zzcFS = paramzzbrv;
      this.zzaOS = paramzzb;
      this.zzcFT = paramAtomicReference;
      this.zzcFU = paramzzf;
    }
    
    public void zzfg(Status paramStatus)
    {
      if (!paramStatus.isSuccess())
      {
        this.zzcFT.set(null);
        this.zzaOS.setResult(paramStatus);
        return;
      }
      this.zzaOS.setResult(Status.zzaLc);
    }
    
    public void zzfh(Status paramStatus)
      throws RemoteException
    {
      this.zzcFT.set(null);
      if (!paramStatus.isSuccess())
      {
        this.zzaOS.setResult(paramStatus);
        return;
      }
      if (this.zzcFU == null)
      {
        this.zzaOS.setResult(Status.zzaLc);
        return;
      }
      this.zzcFT.set(this.zzcFU);
      this.zzcFS.zza(this.zzcFU, this);
    }
  }
  
  private static final class zzf
    extends zzbru.zza
  {
    private final zzzw<UsageReportingApi.OptInOptionsChangedListener> zzaOL;
    
    public zzf(zzzw<UsageReportingApi.OptInOptionsChangedListener> paramzzzw)
    {
      this.zzaOL = paramzzzw;
    }
    
    public void onOptInOptionsChanged()
      throws RemoteException
    {
      this.zzaOL.zza(new zzzw.zzc()
      {
        public void zza(UsageReportingApi.OptInOptionsChangedListener paramAnonymousOptInOptionsChangedListener)
        {
          paramAnonymousOptInOptionsChangedListener.onOptInOptionsChanged();
        }
        
        public void zzxO() {}
      });
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzbry.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */