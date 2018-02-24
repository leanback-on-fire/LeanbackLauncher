package android.support.v17.leanback.widget;

import android.animation.Animator;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v17.leanback.R.id;
import android.support.v17.leanback.R.layout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

public class GuidanceStylist
  implements FragmentAnimationProvider
{
  private TextView mBreadcrumbView;
  private TextView mDescriptionView;
  private View mGuidanceContainer;
  private ImageView mIconView;
  private TextView mTitleView;
  
  public TextView getBreadcrumbView()
  {
    return this.mBreadcrumbView;
  }
  
  public TextView getDescriptionView()
  {
    return this.mDescriptionView;
  }
  
  public ImageView getIconView()
  {
    return this.mIconView;
  }
  
  public TextView getTitleView()
  {
    return this.mTitleView;
  }
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Guidance paramGuidance)
  {
    paramLayoutInflater = paramLayoutInflater.inflate(onProvideLayoutId(), paramViewGroup, false);
    this.mTitleView = ((TextView)paramLayoutInflater.findViewById(R.id.guidance_title));
    this.mBreadcrumbView = ((TextView)paramLayoutInflater.findViewById(R.id.guidance_breadcrumb));
    this.mDescriptionView = ((TextView)paramLayoutInflater.findViewById(R.id.guidance_description));
    this.mIconView = ((ImageView)paramLayoutInflater.findViewById(R.id.guidance_icon));
    this.mGuidanceContainer = paramLayoutInflater.findViewById(R.id.guidance_container);
    if (this.mTitleView != null) {
      this.mTitleView.setText(paramGuidance.getTitle());
    }
    if (this.mBreadcrumbView != null) {
      this.mBreadcrumbView.setText(paramGuidance.getBreadcrumb());
    }
    if (this.mDescriptionView != null) {
      this.mDescriptionView.setText(paramGuidance.getDescription());
    }
    if (this.mIconView != null)
    {
      if (paramGuidance.getIconDrawable() == null) {
        break label267;
      }
      this.mIconView.setImageDrawable(paramGuidance.getIconDrawable());
    }
    for (;;)
    {
      if ((this.mGuidanceContainer != null) && (TextUtils.isEmpty(this.mGuidanceContainer.getContentDescription())))
      {
        paramViewGroup = new StringBuilder();
        if (!TextUtils.isEmpty(paramGuidance.getBreadcrumb())) {
          paramViewGroup.append(paramGuidance.getBreadcrumb()).append('\n');
        }
        if (!TextUtils.isEmpty(paramGuidance.getTitle())) {
          paramViewGroup.append(paramGuidance.getTitle()).append('\n');
        }
        if (!TextUtils.isEmpty(paramGuidance.getDescription())) {
          paramViewGroup.append(paramGuidance.getDescription()).append('\n');
        }
        this.mGuidanceContainer.setContentDescription(paramViewGroup);
      }
      return paramLayoutInflater;
      label267:
      this.mIconView.setVisibility(8);
    }
  }
  
  public void onDestroyView()
  {
    this.mBreadcrumbView = null;
    this.mDescriptionView = null;
    this.mIconView = null;
    this.mTitleView = null;
  }
  
  public void onImeAppearing(@NonNull List<Animator> paramList) {}
  
  public void onImeDisappearing(@NonNull List<Animator> paramList) {}
  
  public int onProvideLayoutId()
  {
    return R.layout.lb_guidance;
  }
  
  public static class Guidance
  {
    private final String mBreadcrumb;
    private final String mDescription;
    private final Drawable mIconDrawable;
    private final String mTitle;
    
    public Guidance(String paramString1, String paramString2, String paramString3, Drawable paramDrawable)
    {
      this.mBreadcrumb = paramString3;
      this.mTitle = paramString1;
      this.mDescription = paramString2;
      this.mIconDrawable = paramDrawable;
    }
    
    public String getBreadcrumb()
    {
      return this.mBreadcrumb;
    }
    
    public String getDescription()
    {
      return this.mDescription;
    }
    
    public Drawable getIconDrawable()
    {
      return this.mIconDrawable;
    }
    
    public String getTitle()
    {
      return this.mTitle;
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/GuidanceStylist.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */