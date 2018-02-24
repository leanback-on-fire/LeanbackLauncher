package android.support.v17.leanback.app;

import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v17.leanback.R.color;
import android.support.v17.leanback.R.dimen;
import android.support.v17.leanback.R.id;
import android.support.v17.leanback.R.layout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ErrorFragment
  extends BrandedFragment
{
  private Drawable mBackgroundDrawable;
  private Button mButton;
  private View.OnClickListener mButtonClickListener;
  private String mButtonText;
  private Drawable mDrawable;
  private ViewGroup mErrorFrame;
  private ImageView mImageView;
  private boolean mIsBackgroundTranslucent = true;
  private CharSequence mMessage;
  private TextView mTextView;
  
  private static Paint.FontMetricsInt getFontMetricsInt(TextView paramTextView)
  {
    Paint localPaint = new Paint(1);
    localPaint.setTextSize(paramTextView.getTextSize());
    localPaint.setTypeface(paramTextView.getTypeface());
    return localPaint.getFontMetricsInt();
  }
  
  private static void setTopMargin(TextView paramTextView, int paramInt)
  {
    ViewGroup.MarginLayoutParams localMarginLayoutParams = (ViewGroup.MarginLayoutParams)paramTextView.getLayoutParams();
    localMarginLayoutParams.topMargin = paramInt;
    paramTextView.setLayoutParams(localMarginLayoutParams);
  }
  
  private void updateBackground()
  {
    if (this.mErrorFrame != null)
    {
      if (this.mBackgroundDrawable != null) {
        this.mErrorFrame.setBackground(this.mBackgroundDrawable);
      }
    }
    else {
      return;
    }
    ViewGroup localViewGroup = this.mErrorFrame;
    Resources localResources = this.mErrorFrame.getResources();
    if (this.mIsBackgroundTranslucent) {}
    for (int i = R.color.lb_error_background_color_translucent;; i = R.color.lb_error_background_color_opaque)
    {
      localViewGroup.setBackgroundColor(localResources.getColor(i));
      return;
    }
  }
  
  private void updateButton()
  {
    Button localButton;
    if (this.mButton != null)
    {
      this.mButton.setText(this.mButtonText);
      this.mButton.setOnClickListener(this.mButtonClickListener);
      localButton = this.mButton;
      if (!TextUtils.isEmpty(this.mButtonText)) {
        break label61;
      }
    }
    label61:
    for (int i = 8;; i = 0)
    {
      localButton.setVisibility(i);
      this.mButton.requestFocus();
      return;
    }
  }
  
  private void updateImageDrawable()
  {
    ImageView localImageView;
    if (this.mImageView != null)
    {
      this.mImageView.setImageDrawable(this.mDrawable);
      localImageView = this.mImageView;
      if (this.mDrawable != null) {
        break label39;
      }
    }
    label39:
    for (int i = 8;; i = 0)
    {
      localImageView.setVisibility(i);
      return;
    }
  }
  
  private void updateMessage()
  {
    TextView localTextView;
    if (this.mTextView != null)
    {
      this.mTextView.setText(this.mMessage);
      localTextView = this.mTextView;
      if (!TextUtils.isEmpty(this.mMessage)) {
        break label42;
      }
    }
    label42:
    for (int i = 8;; i = 0)
    {
      localTextView.setVisibility(i);
      return;
    }
  }
  
  public Drawable getBackgroundDrawable()
  {
    return this.mBackgroundDrawable;
  }
  
  public View.OnClickListener getButtonClickListener()
  {
    return this.mButtonClickListener;
  }
  
  public String getButtonText()
  {
    return this.mButtonText;
  }
  
  public Drawable getImageDrawable()
  {
    return this.mDrawable;
  }
  
  public CharSequence getMessage()
  {
    return this.mMessage;
  }
  
  public boolean isBackgroundTranslucent()
  {
    return this.mIsBackgroundTranslucent;
  }
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    View localView = paramLayoutInflater.inflate(R.layout.lb_error_fragment, paramViewGroup, false);
    this.mErrorFrame = ((ViewGroup)localView.findViewById(R.id.error_frame));
    updateBackground();
    installTitleView(paramLayoutInflater, this.mErrorFrame, paramBundle);
    this.mImageView = ((ImageView)localView.findViewById(R.id.image));
    updateImageDrawable();
    this.mTextView = ((TextView)localView.findViewById(R.id.message));
    updateMessage();
    this.mButton = ((Button)localView.findViewById(R.id.button));
    updateButton();
    paramLayoutInflater = getFontMetricsInt(this.mTextView);
    int i = paramViewGroup.getResources().getDimensionPixelSize(R.dimen.lb_error_under_image_baseline_margin);
    setTopMargin(this.mTextView, paramLayoutInflater.ascent + i);
    i = paramViewGroup.getResources().getDimensionPixelSize(R.dimen.lb_error_under_message_baseline_margin);
    setTopMargin(this.mButton, i - paramLayoutInflater.descent);
    return localView;
  }
  
  public void onStart()
  {
    super.onStart();
    this.mErrorFrame.requestFocus();
  }
  
  public void setBackgroundDrawable(Drawable paramDrawable)
  {
    this.mBackgroundDrawable = paramDrawable;
    if (paramDrawable != null)
    {
      int i = paramDrawable.getOpacity();
      if ((i != -3) && (i != -2)) {
        break label42;
      }
    }
    label42:
    for (boolean bool = true;; bool = false)
    {
      this.mIsBackgroundTranslucent = bool;
      updateBackground();
      updateMessage();
      return;
    }
  }
  
  public void setButtonClickListener(View.OnClickListener paramOnClickListener)
  {
    this.mButtonClickListener = paramOnClickListener;
    updateButton();
  }
  
  public void setButtonText(String paramString)
  {
    this.mButtonText = paramString;
    updateButton();
  }
  
  public void setDefaultBackground(boolean paramBoolean)
  {
    this.mBackgroundDrawable = null;
    this.mIsBackgroundTranslucent = paramBoolean;
    updateBackground();
    updateMessage();
  }
  
  public void setImageDrawable(Drawable paramDrawable)
  {
    this.mDrawable = paramDrawable;
    updateImageDrawable();
  }
  
  public void setMessage(CharSequence paramCharSequence)
  {
    this.mMessage = paramCharSequence;
    updateMessage();
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/app/ErrorFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */