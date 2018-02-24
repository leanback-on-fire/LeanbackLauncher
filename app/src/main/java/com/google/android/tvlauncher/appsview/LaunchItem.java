package com.google.android.tvlauncher.appsview;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import java.util.Objects;

public class LaunchItem
  implements Comparable<LaunchItem>
{
  static final int PROGRESS_UNKNOWN = -1;
  static final int STATE_UNKNOWN = 0;
  private Drawable mBanner;
  private String mComponentName;
  private Context mContext;
  private Drawable mIcon;
  private int mInstallProgressPercent;
  private int mInstallStateStringResourceId;
  private Intent mIntent;
  private boolean mIsGame;
  private boolean mIsInitialInstall;
  private CharSequence mLabel;
  private String mPkgName;
  
  LaunchItem(Context paramContext, ResolveInfo paramResolveInfo)
  {
    set(paramContext, paramResolveInfo);
    this.mIsInitialInstall = true;
  }
  
  LaunchItem(Context paramContext, CharSequence paramCharSequence, String paramString, Intent paramIntent, Drawable paramDrawable)
  {
    init(paramContext, paramCharSequence, paramString);
    this.mIntent = paramIntent;
    this.mBanner = paramDrawable;
  }
  
  @VisibleForTesting
  LaunchItem(Context paramContext, CharSequence paramCharSequence, String paramString1, String paramString2)
  {
    init(paramContext, paramCharSequence, paramString2);
    this.mComponentName = paramString1;
  }
  
  public LaunchItem(Context paramContext, CharSequence paramCharSequence, String paramString1, String paramString2, Intent paramIntent, boolean paramBoolean, final InstallingLaunchItemListener paramInstallingLaunchItemListener)
  {
    init(paramContext, paramCharSequence, paramString2);
    if (paramIntent != null)
    {
      this.mIntent = paramIntent.addFlags(270532608);
      if (this.mIntent.getComponent() != null) {
        this.mComponentName = this.mIntent.getComponent().flattenToString();
      }
    }
    this.mIsGame = paramBoolean;
    this.mIsInitialInstall = true;
    if (!TextUtils.isEmpty(paramString1))
    {
      int i = paramContext.getResources().getDimensionPixelSize(R.dimen.banner_icon_size);
      paramCharSequence = new SimpleTarget(i, i)
      {
        public void onResourceReady(Drawable paramAnonymousDrawable, Transition<? super Drawable> paramAnonymousTransition)
        {
          if ((paramAnonymousDrawable != null) && (paramInstallingLaunchItemListener != null))
          {
            LaunchItem.access$302(LaunchItem.this, paramAnonymousDrawable);
            paramInstallingLaunchItemListener.onInstallingLaunchItemChanged(LaunchItem.this);
          }
        }
      };
      Glide.with(paramContext).asDrawable().apply(RequestOptions.skipMemoryCacheOf(true)).apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE)).load(paramString1).into(paramCharSequence);
    }
    this.mBanner = null;
  }
  
  private void clear()
  {
    this.mInstallProgressPercent = -1;
    this.mInstallStateStringResourceId = 0;
    this.mIntent = null;
    this.mBanner = null;
    this.mIcon = null;
    this.mLabel = null;
    this.mPkgName = null;
    this.mComponentName = null;
    this.mIsInitialInstall = false;
    this.mIsGame = false;
  }
  
  private static Intent getLaunchIntent(ResolveInfo paramResolveInfo)
  {
    paramResolveInfo = new ComponentName(paramResolveInfo.activityInfo.applicationInfo.packageName, paramResolveInfo.activityInfo.name);
    Intent localIntent = new Intent("android.intent.action.MAIN");
    localIntent.addCategory("android.intent.category.LAUNCHER");
    localIntent.setComponent(paramResolveInfo);
    localIntent.addFlags(270532608);
    return localIntent;
  }
  
  private static Bitmap getSizeCappedBitmap(Bitmap paramBitmap, int paramInt1, int paramInt2)
  {
    Object localObject;
    if (paramBitmap == null) {
      localObject = null;
    }
    Bitmap localBitmap;
    do
    {
      int i;
      int j;
      float f1;
      do
      {
        do
        {
          do
          {
            do
            {
              return (Bitmap)localObject;
              i = paramBitmap.getWidth();
              j = paramBitmap.getHeight();
              if (i > paramInt1) {
                break;
              }
              localObject = paramBitmap;
            } while (j <= paramInt2);
            localObject = paramBitmap;
          } while (i <= 0);
          localObject = paramBitmap;
        } while (j <= 0);
        f1 = Math.min(1.0F, paramInt2 / j);
        if (f1 < 1.0D) {
          break;
        }
        localObject = paramBitmap;
      } while (i <= paramInt1);
      float f2 = Math.max(Math.round(i * f1) - paramInt1, 0) / f1;
      localObject = new Matrix();
      ((Matrix)localObject).postScale(f1, f1);
      localBitmap = Bitmap.createBitmap(paramBitmap, (int)(f2 / 2.0F), 0, (int)(i - f2), j, (Matrix)localObject, true);
      localObject = paramBitmap;
    } while (localBitmap == null);
    return localBitmap;
  }
  
  private boolean hasImage(Drawable paramDrawable)
  {
    if (paramDrawable != null) {}
    for (boolean bool = true;; bool = false)
    {
      if ((paramDrawable instanceof BitmapDrawable))
      {
        if (((BitmapDrawable)paramDrawable).getBitmap() == null) {
          break;
        }
        bool = true;
      }
      return bool;
    }
    return false;
  }
  
  private void init(Context paramContext, CharSequence paramCharSequence, String paramString)
  {
    this.mContext = paramContext;
    this.mLabel = paramCharSequence;
    this.mPkgName = paramString;
  }
  
  public static boolean isSystemApp(Context paramContext, String paramString)
  {
    boolean bool = false;
    try
    {
      int i = paramContext.getPackageManager().getApplicationInfo(paramString, 0).flags;
      if ((i & 0x1) != 0) {
        bool = true;
      }
      return bool;
    }
    catch (PackageManager.NameNotFoundException paramContext) {}
    return false;
  }
  
  public boolean areContentsTheSame(LaunchItem paramLaunchItem)
  {
    return (Objects.equals(paramLaunchItem.getBanner(), getBanner())) && (Objects.equals(paramLaunchItem.getIcon(), getIcon())) && (Objects.equals(paramLaunchItem.getLabel(), getLabel()));
  }
  
  public int compareTo(@NonNull LaunchItem paramLaunchItem)
  {
    Object localObject2 = getLabel();
    CharSequence localCharSequence = paramLaunchItem.getLabel();
    Object localObject1 = localObject2;
    if (localObject2 == null) {
      localObject1 = getComponentName();
    }
    localObject2 = localCharSequence;
    if (localCharSequence == null) {
      localObject2 = paramLaunchItem.getComponentName();
    }
    if ((localObject1 == null) && (localObject2 == null)) {
      return 0;
    }
    if (localObject1 == null) {
      return 1;
    }
    if (localObject2 == null) {
      return -1;
    }
    return ((CharSequence)localObject1).toString().compareToIgnoreCase(((CharSequence)localObject2).toString());
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject instanceof LaunchItem))
    {
      String str = ((LaunchItem)paramObject).getPackageName();
      paramObject = ((LaunchItem)paramObject).getComponentName();
      if ((!TextUtils.equals(this.mPkgName, str)) || (!TextUtils.equals(this.mComponentName, (CharSequence)paramObject))) {
        break label58;
      }
    }
    label58:
    for (boolean bool = true;; bool = false)
    {
      return bool;
      return false;
    }
  }
  
  public Drawable getBanner()
  {
    return this.mBanner;
  }
  
  public String getComponentName()
  {
    return this.mComponentName;
  }
  
  public Drawable getIcon()
  {
    return this.mIcon;
  }
  
  public int getInstallProgressPercent()
  {
    return this.mInstallProgressPercent;
  }
  
  public String getInstallProgressString(Context paramContext)
  {
    if (this.mInstallProgressPercent == -1) {
      return "";
    }
    return paramContext.getString(2131493066, new Object[] { Integer.valueOf(this.mInstallProgressPercent) });
  }
  
  public String getInstallStateString(Context paramContext)
  {
    return paramContext.getString(this.mInstallStateStringResourceId);
  }
  
  public int getInstallStateStringResId()
  {
    return this.mInstallStateStringResourceId;
  }
  
  public Intent getIntent()
  {
    return this.mIntent;
  }
  
  public Drawable getItemDrawable()
  {
    if (isInstalling())
    {
      if (!hasImage(this.mIcon)) {
        return this.mContext.getResources().getDrawable(2130837649, null);
      }
      return this.mIcon;
    }
    if (hasImage(this.mBanner)) {
      return this.mBanner;
    }
    return this.mIcon;
  }
  
  public CharSequence getLabel()
  {
    return this.mLabel;
  }
  
  public String getPackageName()
  {
    return this.mPkgName;
  }
  
  public boolean hasSamePackageName(ResolveInfo paramResolveInfo)
  {
    String str = null;
    Object localObject = null;
    Intent localIntent = getLaunchIntent(paramResolveInfo);
    paramResolveInfo = (ResolveInfo)localObject;
    if (localIntent.getComponent() != null)
    {
      str = localIntent.getComponent().getPackageName();
      paramResolveInfo = localIntent.getComponent().flattenToString();
    }
    return (TextUtils.equals(this.mPkgName, str)) && (TextUtils.equals(this.mComponentName, paramResolveInfo));
  }
  
  public int hashCode()
  {
    return this.mPkgName.hashCode();
  }
  
  public boolean isGame()
  {
    return this.mIsGame;
  }
  
  public boolean isInitialInstall()
  {
    return this.mIsInitialInstall;
  }
  
  public boolean isInstalling()
  {
    return this.mInstallStateStringResourceId != 0;
  }
  
  public LaunchItem set(Context paramContext, ResolveInfo paramResolveInfo)
  {
    clear();
    this.mContext = paramContext;
    this.mIntent = Intent.makeMainActivity(new ComponentName(paramResolveInfo.activityInfo.packageName, paramResolveInfo.activityInfo.name));
    this.mIntent.addFlags(268435456);
    PackageManager localPackageManager = paramContext.getPackageManager();
    this.mIcon = paramResolveInfo.loadIcon(localPackageManager);
    this.mLabel = paramResolveInfo.loadLabel(localPackageManager);
    this.mPkgName = this.mIntent.getComponent().getPackageName();
    this.mComponentName = this.mIntent.getComponent().flattenToString();
    if ((paramResolveInfo.activityInfo.applicationInfo.flags & 0x2000000) != 0) {}
    int i;
    int j;
    for (boolean bool = true;; bool = false)
    {
      this.mIsGame = bool;
      paramContext = paramContext.getResources();
      this.mBanner = paramResolveInfo.activityInfo.loadBanner(localPackageManager);
      if ((this.mBanner instanceof BitmapDrawable))
      {
        i = Math.round(paramContext.getDimensionPixelOffset(R.dimen.banner_width) * paramContext.getFraction(2131886084, 1, 1));
        j = Math.round(paramContext.getDimensionPixelOffset(R.dimen.banner_height) * paramContext.getFraction(2131886084, 1, 1));
        if (Looper.getMainLooper().getThread() != Thread.currentThread()) {
          break;
        }
        new BitmapResizeTask(((BitmapDrawable)this.mBanner).getBitmap(), i, j, true).execute(new Void[0]);
      }
      return this;
    }
    this.mBanner = new BitmapDrawable(paramContext, getSizeCappedBitmap(((BitmapDrawable)this.mBanner).getBitmap(), i, j));
    return this;
  }
  
  public void setInstallProgressPercent(int paramInt)
  {
    this.mInstallProgressPercent = paramInt;
  }
  
  public void setInstallStateStringResourceId(int paramInt)
  {
    this.mInstallStateStringResourceId = paramInt;
  }
  
  public LaunchItem setInstallationState(LaunchItem paramLaunchItem)
  {
    this.mInstallProgressPercent = paramLaunchItem.getInstallProgressPercent();
    this.mInstallStateStringResourceId = paramLaunchItem.getInstallStateStringResId();
    return this;
  }
  
  public String toDebugString()
  {
    return "Label: " + this.mLabel + " Intent: " + this.mIntent + " Banner: " + this.mBanner + " Icon: " + this.mIcon;
  }
  
  public String toString()
  {
    return this.mLabel + " -- " + getComponentName();
  }
  
  private class BitmapResizeTask
    extends AsyncTask<Void, Void, Bitmap>
  {
    private Bitmap mImageToResize;
    private boolean mIsBanner;
    private int mMaxHeight;
    private int mMaxWidth;
    
    BitmapResizeTask(Bitmap paramBitmap, int paramInt1, int paramInt2, boolean paramBoolean)
    {
      this.mImageToResize = paramBitmap;
      this.mMaxWidth = paramInt1;
      this.mMaxHeight = paramInt2;
      this.mIsBanner = paramBoolean;
    }
    
    protected Bitmap doInBackground(Void... paramVarArgs)
    {
      return LaunchItem.getSizeCappedBitmap(this.mImageToResize, this.mMaxWidth, this.mMaxHeight);
    }
    
    protected void onPostExecute(Bitmap paramBitmap)
    {
      if (this.mIsBanner)
      {
        LaunchItem.access$102(LaunchItem.this, new BitmapDrawable(LaunchItem.this.mContext.getResources(), paramBitmap));
        return;
      }
      LaunchItem.access$302(LaunchItem.this, new BitmapDrawable(LaunchItem.this.mContext.getResources(), paramBitmap));
    }
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/appsview/LaunchItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */