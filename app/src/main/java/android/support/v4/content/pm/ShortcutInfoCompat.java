package android.support.v4.content.pm;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutInfo.Builder;
import android.graphics.Bitmap;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.graphics.drawable.IconCompat;
import android.text.TextUtils;
import java.util.Arrays;

public class ShortcutInfoCompat
{
  private ComponentName mActivity;
  private Context mContext;
  private CharSequence mDisabledMessage;
  private IconCompat mIcon;
  private String mId;
  private Intent[] mIntents;
  private CharSequence mLabel;
  private CharSequence mLongLabel;
  
  Intent addToIntent(Intent paramIntent)
  {
    paramIntent.putExtra("android.intent.extra.shortcut.INTENT", this.mIntents[(this.mIntents.length - 1)]).putExtra("android.intent.extra.shortcut.NAME", this.mLabel.toString());
    if (this.mIcon != null) {
      this.mIcon.addToShortcutIntent(paramIntent);
    }
    return paramIntent;
  }
  
  @Nullable
  public ComponentName getActivity()
  {
    return this.mActivity;
  }
  
  @Nullable
  public CharSequence getDisabledMessage()
  {
    return this.mDisabledMessage;
  }
  
  @NonNull
  public String getId()
  {
    return this.mId;
  }
  
  @NonNull
  public Intent getIntent()
  {
    return this.mIntents[(this.mIntents.length - 1)];
  }
  
  @NonNull
  public Intent[] getIntents()
  {
    return (Intent[])Arrays.copyOf(this.mIntents, this.mIntents.length);
  }
  
  @Nullable
  public CharSequence getLongLabel()
  {
    return this.mLongLabel;
  }
  
  @NonNull
  public CharSequence getShortLabel()
  {
    return this.mLabel;
  }
  
  @RequiresApi(26)
  ShortcutInfo toShortcutInfo()
  {
    ShortcutInfo.Builder localBuilder = new ShortcutInfo.Builder(this.mContext, this.mId).setShortLabel(this.mLabel).setIntents(this.mIntents);
    if (this.mIcon != null) {
      localBuilder.setIcon(this.mIcon.toIcon());
    }
    if (!TextUtils.isEmpty(this.mLongLabel)) {
      localBuilder.setLongLabel(this.mLongLabel);
    }
    if (!TextUtils.isEmpty(this.mDisabledMessage)) {
      localBuilder.setDisabledMessage(this.mDisabledMessage);
    }
    if (this.mActivity != null) {
      localBuilder.setActivity(this.mActivity);
    }
    return localBuilder.build();
  }
  
  public static class Builder
  {
    private final ShortcutInfoCompat mInfo = new ShortcutInfoCompat(null);
    
    public Builder(@NonNull Context paramContext, @NonNull String paramString)
    {
      ShortcutInfoCompat.access$102(this.mInfo, paramContext);
      ShortcutInfoCompat.access$202(this.mInfo, paramString);
    }
    
    @NonNull
    public ShortcutInfoCompat build()
    {
      if (TextUtils.isEmpty(this.mInfo.mLabel)) {
        throw new IllegalArgumentException("Shortcut much have a non-empty label");
      }
      if ((this.mInfo.mIntents == null) || (this.mInfo.mIntents.length == 0)) {
        throw new IllegalArgumentException("Shortcut much have an intent");
      }
      return this.mInfo;
    }
    
    @NonNull
    public Builder setActivity(@NonNull ComponentName paramComponentName)
    {
      ShortcutInfoCompat.access$802(this.mInfo, paramComponentName);
      return this;
    }
    
    @NonNull
    public Builder setDisabledMessage(@NonNull CharSequence paramCharSequence)
    {
      ShortcutInfoCompat.access$502(this.mInfo, paramCharSequence);
      return this;
    }
    
    @NonNull
    public Builder setIcon(@DrawableRes int paramInt)
    {
      return setIcon(IconCompat.createWithResource(this.mInfo.mContext, paramInt));
    }
    
    @NonNull
    public Builder setIcon(@NonNull Bitmap paramBitmap)
    {
      return setIcon(IconCompat.createWithBitmap(paramBitmap));
    }
    
    @NonNull
    public Builder setIcon(IconCompat paramIconCompat)
    {
      ShortcutInfoCompat.access$702(this.mInfo, paramIconCompat);
      return this;
    }
    
    @NonNull
    public Builder setIntent(@NonNull Intent paramIntent)
    {
      return setIntents(new Intent[] { paramIntent });
    }
    
    @NonNull
    public Builder setIntents(@NonNull Intent[] paramArrayOfIntent)
    {
      ShortcutInfoCompat.access$602(this.mInfo, paramArrayOfIntent);
      return this;
    }
    
    @NonNull
    public Builder setLongLabel(@NonNull CharSequence paramCharSequence)
    {
      ShortcutInfoCompat.access$402(this.mInfo, paramCharSequence);
      return this;
    }
    
    @NonNull
    public Builder setShortLabel(@NonNull CharSequence paramCharSequence)
    {
      ShortcutInfoCompat.access$302(this.mInfo, paramCharSequence);
      return this;
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v4/content/pm/ShortcutInfoCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */