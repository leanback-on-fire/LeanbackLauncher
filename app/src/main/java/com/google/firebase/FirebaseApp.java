package com.google.firebase;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.common.internal.zzaa;
import com.google.android.gms.common.internal.zzaa.zza;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.util.zza;
import com.google.android.gms.common.util.zzc;
import com.google.android.gms.common.util.zzt;
import com.google.android.gms.common.util.zzu;
import com.google.android.gms.internal.zzcbo;
import com.google.android.gms.internal.zzcbp;
import com.google.android.gms.internal.zzcbq;
import com.google.android.gms.internal.zzyq;
import com.google.android.gms.internal.zzyq.zza;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.GetTokenResult;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class FirebaseApp
{
  public static final String DEFAULT_APP_NAME = "[DEFAULT]";
  private static final List<String> ht = Arrays.asList(new String[] { "com.google.firebase.auth.FirebaseAuth", "com.google.firebase.iid.FirebaseInstanceId" });
  private static final List<String> hu = Collections.singletonList("com.google.firebase.crash.FirebaseCrash");
  private static final List<String> hv = Arrays.asList(new String[] { "com.google.android.gms.measurement.AppMeasurement" });
  private static final List<String> hw = Arrays.asList(new String[0]);
  private static final Set<String> hx = Collections.emptySet();
  static final Map<String, FirebaseApp> zzbAC = new ArrayMap();
  private static final Object zzuq = new Object();
  private final AtomicBoolean hA = new AtomicBoolean();
  private final List<zza> hB = new CopyOnWriteArrayList();
  private final List<zzb> hC = new CopyOnWriteArrayList();
  private final List<Object> hD = new CopyOnWriteArrayList();
  private zzcbp hE;
  private final FirebaseOptions hy;
  private final AtomicBoolean hz = new AtomicBoolean(false);
  private final String mName;
  private final Context zzwB;
  
  protected FirebaseApp(Context paramContext, String paramString, FirebaseOptions paramFirebaseOptions)
  {
    this.zzwB = ((Context)zzac.zzC(paramContext));
    this.mName = zzac.zzdc(paramString);
    this.hy = ((FirebaseOptions)zzac.zzC(paramFirebaseOptions));
  }
  
  public static List<FirebaseApp> getApps(Context paramContext)
  {
    zzcbo localzzcbo = zzcbo.zzcG(paramContext);
    ArrayList localArrayList;
    synchronized (zzuq)
    {
      localArrayList = new ArrayList(zzbAC.values());
      Object localObject2 = zzcbo.zzakr().zzaks();
      ((Set)localObject2).removeAll(zzbAC.keySet());
      localObject2 = ((Set)localObject2).iterator();
      if (((Iterator)localObject2).hasNext())
      {
        String str = (String)((Iterator)localObject2).next();
        localzzcbo.zzmv(str);
        localArrayList.add(initializeApp(paramContext, null, str));
      }
    }
    return localArrayList;
  }
  
  @Nullable
  public static FirebaseApp getInstance()
  {
    synchronized (zzuq)
    {
      Object localObject2 = (FirebaseApp)zzbAC.get("[DEFAULT]");
      if (localObject2 == null)
      {
        localObject2 = String.valueOf(zzu.zzBc());
        throw new IllegalStateException(String.valueOf(localObject2).length() + 116 + "Default FirebaseApp is not initialized in this process " + (String)localObject2 + ". Make sure to call FirebaseApp.initializeApp(Context) first.");
      }
    }
    return localFirebaseApp;
  }
  
  public static FirebaseApp getInstance(@NonNull String paramString)
  {
    for (;;)
    {
      synchronized (zzuq)
      {
        localObject1 = (FirebaseApp)zzbAC.get(zzli(paramString));
        if (localObject1 != null) {
          return (FirebaseApp)localObject1;
        }
        localObject1 = zzadn();
        if (((List)localObject1).isEmpty())
        {
          localObject1 = "";
          throw new IllegalStateException(String.format("FirebaseApp with name %s doesn't exist. %s", new Object[] { paramString, localObject1 }));
        }
      }
      Object localObject1 = String.valueOf(TextUtils.join(", ", (Iterable)localObject1));
      if (((String)localObject1).length() != 0) {
        localObject1 = "Available app names: ".concat((String)localObject1);
      } else {
        localObject1 = new String("Available app names: ");
      }
    }
  }
  
  @Nullable
  public static FirebaseApp initializeApp(Context paramContext)
  {
    FirebaseOptions localFirebaseOptions;
    synchronized (zzuq)
    {
      if (zzbAC.containsKey("[DEFAULT]"))
      {
        paramContext = getInstance();
        return paramContext;
      }
      localFirebaseOptions = FirebaseOptions.fromResource(paramContext);
      if (localFirebaseOptions == null) {
        return null;
      }
    }
    paramContext = initializeApp(paramContext, localFirebaseOptions);
    return paramContext;
  }
  
  public static FirebaseApp initializeApp(Context paramContext, FirebaseOptions paramFirebaseOptions)
  {
    return initializeApp(paramContext, paramFirebaseOptions, "[DEFAULT]");
  }
  
  public static FirebaseApp initializeApp(Context paramContext, FirebaseOptions paramFirebaseOptions, String paramString)
  {
    zzcbo localzzcbo = zzcbo.zzcG(paramContext);
    zzcu(paramContext);
    paramString = zzli(paramString);
    if (paramContext.getApplicationContext() == null) {}
    synchronized (zzuq)
    {
      while (!zzbAC.containsKey(paramString))
      {
        bool = true;
        zzac.zza(bool, String.valueOf(paramString).length() + 33 + "FirebaseApp name " + paramString + " already exists!");
        zzac.zzb(paramContext, "Application context cannot be null.");
        paramContext = new FirebaseApp(paramContext, paramString, paramFirebaseOptions);
        zzbAC.put(paramString, paramContext);
        localzzcbo.zzg(paramContext);
        paramContext.zza(FirebaseApp.class, paramContext, ht);
        if (paramContext.zzadl())
        {
          paramContext.zza(FirebaseApp.class, paramContext, hu);
          paramContext.zza(Context.class, paramContext.getApplicationContext(), hv);
        }
        return paramContext;
        paramContext = paramContext.getApplicationContext();
      }
      boolean bool = false;
    }
  }
  
  private <T> void zza(Class<T> paramClass, T paramT, Iterable<String> paramIterable)
  {
    boolean bool = ContextCompat.isDeviceProtectedStorage(this.zzwB);
    if (bool) {
      zzc.zzcw(this.zzwB);
    }
    Iterator localIterator = paramIterable.iterator();
    for (;;)
    {
      if (localIterator.hasNext())
      {
        paramIterable = (String)localIterator.next();
        if (bool) {}
        try
        {
          if (hw.contains(paramIterable))
          {
            Method localMethod = Class.forName(paramIterable).getMethod("getInstance", new Class[] { paramClass });
            int i = localMethod.getModifiers();
            if ((Modifier.isPublic(i)) && (Modifier.isStatic(i))) {
              localMethod.invoke(null, new Object[] { paramT });
            }
          }
        }
        catch (ClassNotFoundException localClassNotFoundException)
        {
          if (hx.contains(paramIterable)) {
            throw new IllegalStateException(String.valueOf(paramIterable).concat(" is missing, but is required. Check if it has been removed by Proguard."));
          }
          Log.d("FirebaseApp", String.valueOf(paramIterable).concat(" is not linked. Skipping initialization."));
        }
        catch (NoSuchMethodException paramClass)
        {
          throw new IllegalStateException(String.valueOf(paramIterable).concat("#getInstance has been removed by Proguard. Add keep rule to prevent it."));
        }
        catch (InvocationTargetException paramIterable)
        {
          Log.wtf("FirebaseApp", "Firebase API initialization failure.", paramIterable);
        }
        catch (IllegalAccessException localIllegalAccessException)
        {
          paramIterable = String.valueOf(paramIterable);
          if (paramIterable.length() != 0) {}
          for (paramIterable = "Failed to initialize ".concat(paramIterable);; paramIterable = new String("Failed to initialize "))
          {
            Log.wtf("FirebaseApp", paramIterable, localIllegalAccessException);
            break;
          }
        }
      }
    }
  }
  
  private void zzadk()
  {
    if (!this.hA.get()) {}
    for (boolean bool = true;; bool = false)
    {
      zzac.zza(bool, "FirebaseApp was deleted");
      return;
    }
  }
  
  private static List<String> zzadn()
  {
    zza localzza = new zza();
    synchronized (zzuq)
    {
      localObject2 = zzbAC.values().iterator();
      if (((Iterator)localObject2).hasNext()) {
        localzza.add(((FirebaseApp)((Iterator)localObject2).next()).getName());
      }
    }
    Object localObject2 = zzcbo.zzakr();
    if (localObject2 != null) {
      localCollection.addAll(((zzcbo)localObject2).zzaks());
    }
    ??? = new ArrayList(localCollection);
    Collections.sort((List)???);
    return (List<String>)???;
  }
  
  private void zzado()
  {
    zza(FirebaseApp.class, this, ht);
    if (zzadl())
    {
      zza(FirebaseApp.class, this, hu);
      zza(Context.class, this.zzwB, hv);
    }
  }
  
  public static void zzas(boolean paramBoolean)
  {
    synchronized (zzuq)
    {
      Iterator localIterator = new ArrayList(zzbAC.values()).iterator();
      while (localIterator.hasNext())
      {
        FirebaseApp localFirebaseApp = (FirebaseApp)localIterator.next();
        if (localFirebaseApp.hz.get()) {
          localFirebaseApp.zzbG(paramBoolean);
        }
      }
    }
  }
  
  private void zzbG(boolean paramBoolean)
  {
    Log.d("FirebaseApp", "Notifying background state change listeners.");
    Iterator localIterator = this.hC.iterator();
    while (localIterator.hasNext()) {
      ((zzb)localIterator.next()).zzas(paramBoolean);
    }
  }
  
  @TargetApi(14)
  private static void zzcu(Context paramContext)
  {
    zzt.zzAR();
    if ((paramContext.getApplicationContext() instanceof Application))
    {
      zzyq.zza((Application)paramContext.getApplicationContext());
      zzyq.zzxn().zza(new zzyq.zza()
      {
        public void zzas(boolean paramAnonymousBoolean)
        {
          FirebaseApp.zzas(paramAnonymousBoolean);
        }
      });
    }
  }
  
  private static String zzli(@NonNull String paramString)
  {
    return paramString.trim();
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof FirebaseApp)) {
      return false;
    }
    return this.mName.equals(((FirebaseApp)paramObject).getName());
  }
  
  @NonNull
  public Context getApplicationContext()
  {
    zzadk();
    return this.zzwB;
  }
  
  @NonNull
  public String getName()
  {
    zzadk();
    return this.mName;
  }
  
  @NonNull
  public FirebaseOptions getOptions()
  {
    zzadk();
    return this.hy;
  }
  
  public Task<GetTokenResult> getToken(boolean paramBoolean)
  {
    zzadk();
    if (this.hE == null) {
      return Tasks.forException(new FirebaseApiNotAvailableException("firebase-auth is not linked, please fall back to unauthenticated mode."));
    }
    return this.hE.zzbI(paramBoolean);
  }
  
  public int hashCode()
  {
    return this.mName.hashCode();
  }
  
  public void setAutomaticResourceManagementEnabled(boolean paramBoolean)
  {
    zzadk();
    AtomicBoolean localAtomicBoolean = this.hz;
    boolean bool;
    if (!paramBoolean)
    {
      bool = true;
      if (localAtomicBoolean.compareAndSet(bool, paramBoolean))
      {
        bool = zzyq.zzxn().zzxo();
        if ((!paramBoolean) || (!bool)) {
          break label50;
        }
        zzbG(true);
      }
    }
    label50:
    while ((paramBoolean) || (!bool))
    {
      return;
      bool = false;
      break;
    }
    zzbG(false);
  }
  
  public String toString()
  {
    return zzaa.zzB(this).zzh("name", this.mName).zzh("options", this.hy).toString();
  }
  
  public void zza(@NonNull zzcbp paramzzcbp)
  {
    this.hE = ((zzcbp)zzac.zzC(paramzzcbp));
  }
  
  @UiThread
  public void zza(@NonNull zzcbq paramzzcbq)
  {
    Log.d("FirebaseApp", "Notifying auth state listeners.");
    Iterator localIterator = this.hB.iterator();
    int i = 0;
    while (localIterator.hasNext())
    {
      ((zza)localIterator.next()).zzb(paramzzcbq);
      i += 1;
    }
    Log.d("FirebaseApp", String.format("Notified %d auth state listeners.", new Object[] { Integer.valueOf(i) }));
  }
  
  public void zza(@NonNull zza paramzza)
  {
    zzadk();
    zzac.zzC(paramzza);
    this.hB.add(paramzza);
  }
  
  public void zza(zzb paramzzb)
  {
    zzadk();
    if ((this.hz.get()) && (zzyq.zzxn().zzxo())) {
      paramzzb.zzas(true);
    }
    this.hC.add(paramzzb);
  }
  
  public boolean zzadl()
  {
    return "[DEFAULT]".equals(getName());
  }
  
  public String zzadm()
  {
    String str1 = String.valueOf(zzc.zzt(getName().getBytes()));
    String str2 = String.valueOf(zzc.zzt(getOptions().getApplicationId().getBytes()));
    return String.valueOf(str1).length() + 1 + String.valueOf(str2).length() + str1 + "+" + str2;
  }
  
  public static abstract interface zza
  {
    public abstract void zzb(@NonNull zzcbq paramzzcbq);
  }
  
  public static abstract interface zzb
  {
    public abstract void zzas(boolean paramBoolean);
  }
  
  @TargetApi(24)
  private static class zzc
    extends BroadcastReceiver
  {
    private static AtomicReference<zzc> hF = new AtomicReference();
    private final Context zzwB;
    
    public zzc(Context paramContext)
    {
      this.zzwB = paramContext;
    }
    
    private static void zzcv(Context paramContext)
    {
      if (hF.get() == null)
      {
        zzc localzzc = new zzc(paramContext);
        if (hF.compareAndSet(null, localzzc)) {
          paramContext.registerReceiver(localzzc, new IntentFilter("android.intent.action.USER_UNLOCKED"));
        }
      }
    }
    
    public void onReceive(Context arg1, Intent paramIntent)
    {
      synchronized ()
      {
        paramIntent = FirebaseApp.zzbAC.values().iterator();
        if (paramIntent.hasNext()) {
          FirebaseApp.zza((FirebaseApp)paramIntent.next());
        }
      }
      unregister();
    }
    
    public void unregister()
    {
      this.zzwB.unregisterReceiver(this);
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/firebase/FirebaseApp.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */