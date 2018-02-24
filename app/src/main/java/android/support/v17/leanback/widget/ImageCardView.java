package android.support.v17.leanback.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.v17.leanback.R.attr;
import android.support.v17.leanback.R.id;
import android.support.v17.leanback.R.layout;
import android.support.v17.leanback.R.style;
import android.support.v17.leanback.R.styleable;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewPropertyAnimator;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class ImageCardView
  extends BaseCardView
{
  public static final int CARD_TYPE_FLAG_CONTENT = 2;
  public static final int CARD_TYPE_FLAG_ICON_LEFT = 8;
  public static final int CARD_TYPE_FLAG_ICON_RIGHT = 4;
  public static final int CARD_TYPE_FLAG_IMAGE_ONLY = 0;
  public static final int CARD_TYPE_FLAG_TITLE = 1;
  private boolean mAttachedToWindow;
  private ImageView mBadgeImage;
  private TextView mContentView;
  private ImageView mImageView;
  private ViewGroup mInfoArea;
  private TextView mTitleView;
  
  public ImageCardView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  @Deprecated
  public ImageCardView(Context paramContext, int paramInt)
  {
    this(new ContextThemeWrapper(paramContext, paramInt));
  }
  
  public ImageCardView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, R.attr.imageCardViewStyle);
  }
  
  public ImageCardView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    buildImageCardView(paramAttributeSet, paramInt, R.style.Widget_Leanback_ImageCardView);
  }
  
  private void buildImageCardView(AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    setFocusable(true);
    setFocusableInTouchMode(true);
    Object localObject = LayoutInflater.from(getContext());
    ((LayoutInflater)localObject).inflate(R.layout.lb_image_card_view, this);
    paramAttributeSet = getContext().obtainStyledAttributes(paramAttributeSet, R.styleable.lbImageCardView, paramInt1, paramInt2);
    int k = paramAttributeSet.getInt(R.styleable.lbImageCardView_lbImageCardViewType, 0);
    label70:
    int i;
    label81:
    int j;
    if (k == 0)
    {
      paramInt2 = 1;
      if ((k & 0x1) != 1) {
        break label178;
      }
      paramInt1 = 1;
      if ((k & 0x2) != 2) {
        break label183;
      }
      i = 1;
      if ((k & 0x4) != 4) {
        break label189;
      }
      j = 1;
      label92:
      if ((j != 0) || ((k & 0x8) != 8)) {
        break label195;
      }
    }
    label178:
    label183:
    label189:
    label195:
    for (k = 1;; k = 0)
    {
      this.mImageView = ((ImageView)findViewById(R.id.main_image));
      if (this.mImageView.getDrawable() == null) {
        this.mImageView.setVisibility(4);
      }
      this.mInfoArea = ((ViewGroup)findViewById(R.id.info_field));
      if (paramInt2 == 0) {
        break label201;
      }
      removeView(this.mInfoArea);
      paramAttributeSet.recycle();
      return;
      paramInt2 = 0;
      break;
      paramInt1 = 0;
      break label70;
      i = 0;
      break label81;
      j = 0;
      break label92;
    }
    label201:
    if (paramInt1 != 0)
    {
      this.mTitleView = ((TextView)((LayoutInflater)localObject).inflate(R.layout.lb_image_card_view_themed_title, this.mInfoArea, false));
      this.mInfoArea.addView(this.mTitleView);
    }
    if (i != 0)
    {
      this.mContentView = ((TextView)((LayoutInflater)localObject).inflate(R.layout.lb_image_card_view_themed_content, this.mInfoArea, false));
      this.mInfoArea.addView(this.mContentView);
    }
    if ((j != 0) || (k != 0))
    {
      paramInt2 = R.layout.lb_image_card_view_themed_badge_right;
      if (k != 0) {
        paramInt2 = R.layout.lb_image_card_view_themed_badge_left;
      }
      this.mBadgeImage = ((ImageView)((LayoutInflater)localObject).inflate(paramInt2, this.mInfoArea, false));
      this.mInfoArea.addView(this.mBadgeImage);
    }
    if ((paramInt1 != 0) && (i == 0) && (this.mBadgeImage != null))
    {
      localObject = (RelativeLayout.LayoutParams)this.mTitleView.getLayoutParams();
      if (k != 0)
      {
        ((RelativeLayout.LayoutParams)localObject).addRule(17, this.mBadgeImage.getId());
        this.mTitleView.setLayoutParams((ViewGroup.LayoutParams)localObject);
      }
    }
    else
    {
      if (i != 0)
      {
        localObject = (RelativeLayout.LayoutParams)this.mContentView.getLayoutParams();
        if (paramInt1 == 0) {
          ((RelativeLayout.LayoutParams)localObject).addRule(10);
        }
        if (k != 0)
        {
          ((RelativeLayout.LayoutParams)localObject).removeRule(16);
          ((RelativeLayout.LayoutParams)localObject).removeRule(20);
          ((RelativeLayout.LayoutParams)localObject).addRule(17, this.mBadgeImage.getId());
        }
        this.mContentView.setLayoutParams((ViewGroup.LayoutParams)localObject);
      }
      if (this.mBadgeImage != null)
      {
        localObject = (RelativeLayout.LayoutParams)this.mBadgeImage.getLayoutParams();
        if (i == 0) {
          break label565;
        }
        ((RelativeLayout.LayoutParams)localObject).addRule(8, this.mContentView.getId());
      }
    }
    for (;;)
    {
      this.mBadgeImage.setLayoutParams((ViewGroup.LayoutParams)localObject);
      localObject = paramAttributeSet.getDrawable(R.styleable.lbImageCardView_infoAreaBackground);
      if (localObject != null) {
        setInfoAreaBackground((Drawable)localObject);
      }
      if ((this.mBadgeImage != null) && (this.mBadgeImage.getDrawable() == null)) {
        this.mBadgeImage.setVisibility(8);
      }
      paramAttributeSet.recycle();
      return;
      ((RelativeLayout.LayoutParams)localObject).addRule(16, this.mBadgeImage.getId());
      break;
      label565:
      if (paramInt1 != 0) {
        ((RelativeLayout.LayoutParams)localObject).addRule(8, this.mTitleView.getId());
      }
    }
  }
  
  private void fadeIn()
  {
    this.mImageView.setAlpha(0.0F);
    if (this.mAttachedToWindow) {
      this.mImageView.animate().alpha(1.0F).setDuration(this.mImageView.getResources().getInteger(17694720));
    }
  }
  
  public Drawable getBadgeImage()
  {
    if (this.mBadgeImage == null) {
      return null;
    }
    return this.mBadgeImage.getDrawable();
  }
  
  public CharSequence getContentText()
  {
    if (this.mContentView == null) {
      return null;
    }
    return this.mContentView.getText();
  }
  
  public Drawable getInfoAreaBackground()
  {
    if (this.mInfoArea != null) {
      return this.mInfoArea.getBackground();
    }
    return null;
  }
  
  public Drawable getMainImage()
  {
    if (this.mImageView == null) {
      return null;
    }
    return this.mImageView.getDrawable();
  }
  
  public final ImageView getMainImageView()
  {
    return this.mImageView;
  }
  
  public CharSequence getTitleText()
  {
    if (this.mTitleView == null) {
      return null;
    }
    return this.mTitleView.getText();
  }
  
  public boolean hasOverlappingRendering()
  {
    return false;
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    this.mAttachedToWindow = true;
    if (this.mImageView.getAlpha() == 0.0F) {
      fadeIn();
    }
  }
  
  protected void onDetachedFromWindow()
  {
    this.mAttachedToWindow = false;
    this.mImageView.animate().cancel();
    this.mImageView.setAlpha(1.0F);
    super.onDetachedFromWindow();
  }
  
  public void setBadgeImage(Drawable paramDrawable)
  {
    if (this.mBadgeImage == null) {
      return;
    }
    this.mBadgeImage.setImageDrawable(paramDrawable);
    if (paramDrawable != null)
    {
      this.mBadgeImage.setVisibility(0);
      return;
    }
    this.mBadgeImage.setVisibility(8);
  }
  
  public void setContentText(CharSequence paramCharSequence)
  {
    if (this.mContentView == null) {
      return;
    }
    this.mContentView.setText(paramCharSequence);
  }
  
  public void setInfoAreaBackground(Drawable paramDrawable)
  {
    if (this.mInfoArea != null) {
      this.mInfoArea.setBackground(paramDrawable);
    }
  }
  
  public void setInfoAreaBackgroundColor(@ColorInt int paramInt)
  {
    if (this.mInfoArea != null) {
      this.mInfoArea.setBackgroundColor(paramInt);
    }
  }
  
  public void setMainImage(Drawable paramDrawable)
  {
    setMainImage(paramDrawable, true);
  }
  
  public void setMainImage(Drawable paramDrawable, boolean paramBoolean)
  {
    if (this.mImageView == null) {
      return;
    }
    this.mImageView.setImageDrawable(paramDrawable);
    if (paramDrawable == null)
    {
      this.mImageView.animate().cancel();
      this.mImageView.setAlpha(1.0F);
      this.mImageView.setVisibility(4);
      return;
    }
    this.mImageView.setVisibility(0);
    if (paramBoolean)
    {
      fadeIn();
      return;
    }
    this.mImageView.animate().cancel();
    this.mImageView.setAlpha(1.0F);
  }
  
  public void setMainImageAdjustViewBounds(boolean paramBoolean)
  {
    if (this.mImageView != null) {
      this.mImageView.setAdjustViewBounds(paramBoolean);
    }
  }
  
  public void setMainImageDimensions(int paramInt1, int paramInt2)
  {
    ViewGroup.LayoutParams localLayoutParams = this.mImageView.getLayoutParams();
    localLayoutParams.width = paramInt1;
    localLayoutParams.height = paramInt2;
    this.mImageView.setLayoutParams(localLayoutParams);
  }
  
  public void setMainImageScaleType(ImageView.ScaleType paramScaleType)
  {
    if (this.mImageView != null) {
      this.mImageView.setScaleType(paramScaleType);
    }
  }
  
  public void setTitleText(CharSequence paramCharSequence)
  {
    if (this.mTitleView == null) {
      return;
    }
    this.mTitleView.setText(paramCharSequence);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/ImageCardView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */