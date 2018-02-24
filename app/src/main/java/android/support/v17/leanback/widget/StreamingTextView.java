package android.support.v17.leanback.widget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.os.Build.VERSION;
import android.support.v17.leanback.R.drawable;
import android.text.SpannableStringBuilder;
import android.text.SpannedString;
import android.text.style.ForegroundColorSpan;
import android.text.style.ReplacementSpan;
import android.util.AttributeSet;
import android.util.Property;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.EditText;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class StreamingTextView
  extends EditText
{
  static final boolean ANIMATE_DOTS_FOR_PENDING = true;
  private static final boolean DEBUG = false;
  private static final boolean DOTS_FOR_PENDING = true;
  private static final boolean DOTS_FOR_STABLE = false;
  private static final Pattern SPLIT_PATTERN = Pattern.compile("\\S+");
  private static final Property<StreamingTextView, Integer> STREAM_POSITION_PROPERTY = new Property(Integer.class, "streamPosition")
  {
    public Integer get(StreamingTextView paramAnonymousStreamingTextView)
    {
      return Integer.valueOf(paramAnonymousStreamingTextView.getStreamPosition());
    }
    
    public void set(StreamingTextView paramAnonymousStreamingTextView, Integer paramAnonymousInteger)
    {
      paramAnonymousStreamingTextView.setStreamPosition(paramAnonymousInteger.intValue());
    }
  };
  private static final long STREAM_UPDATE_DELAY_MILLIS = 50L;
  private static final String TAG = "StreamingTextView";
  private static final float TEXT_DOT_SCALE = 1.3F;
  Bitmap mOneDot;
  final Random mRandom = new Random();
  int mStreamPosition;
  private ObjectAnimator mStreamingAnimation;
  Bitmap mTwoDot;
  
  public StreamingTextView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  public StreamingTextView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
  }
  
  private void addColorSpan(SpannableStringBuilder paramSpannableStringBuilder, int paramInt1, String paramString, int paramInt2)
  {
    paramSpannableStringBuilder.setSpan(new ForegroundColorSpan(paramInt1), paramInt2, paramInt2 + paramString.length(), 33);
  }
  
  private void addDottySpans(SpannableStringBuilder paramSpannableStringBuilder, String paramString, int paramInt)
  {
    Matcher localMatcher = SPLIT_PATTERN.matcher(paramString);
    while (localMatcher.find())
    {
      int i = paramInt + localMatcher.start();
      int j = localMatcher.end();
      paramSpannableStringBuilder.setSpan(new DottySpan(paramString.charAt(localMatcher.start()), i), i, paramInt + j, 33);
    }
  }
  
  private void cancelStreamAnimation()
  {
    if (this.mStreamingAnimation != null) {
      this.mStreamingAnimation.cancel();
    }
  }
  
  private Bitmap getScaledBitmap(int paramInt, float paramFloat)
  {
    Bitmap localBitmap = BitmapFactory.decodeResource(getResources(), paramInt);
    return Bitmap.createScaledBitmap(localBitmap, (int)(localBitmap.getWidth() * paramFloat), (int)(localBitmap.getHeight() * paramFloat), false);
  }
  
  public static boolean isLayoutRtl(View paramView)
  {
    if (Build.VERSION.SDK_INT >= 17) {
      return 1 == paramView.getLayoutDirection();
    }
    return false;
  }
  
  private void startStreamAnimation()
  {
    cancelStreamAnimation();
    int i = getStreamPosition();
    int j = length();
    int k = j - i;
    if (k > 0)
    {
      if (this.mStreamingAnimation == null)
      {
        this.mStreamingAnimation = new ObjectAnimator();
        this.mStreamingAnimation.setTarget(this);
        this.mStreamingAnimation.setProperty(STREAM_POSITION_PROPERTY);
      }
      this.mStreamingAnimation.setIntValues(new int[] { i, j });
      this.mStreamingAnimation.setDuration(50L * k);
      this.mStreamingAnimation.start();
    }
  }
  
  private void updateText(CharSequence paramCharSequence)
  {
    setText(paramCharSequence);
    bringPointIntoView(length());
  }
  
  int getStreamPosition()
  {
    return this.mStreamPosition;
  }
  
  protected void onFinishInflate()
  {
    super.onFinishInflate();
    this.mOneDot = getScaledBitmap(R.drawable.lb_text_dot_one, 1.3F);
    this.mTwoDot = getScaledBitmap(R.drawable.lb_text_dot_two, 1.3F);
    reset();
  }
  
  public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo paramAccessibilityNodeInfo)
  {
    super.onInitializeAccessibilityNodeInfo(paramAccessibilityNodeInfo);
    paramAccessibilityNodeInfo.setClassName(StreamingTextView.class.getCanonicalName());
  }
  
  public void reset()
  {
    this.mStreamPosition = -1;
    cancelStreamAnimation();
    setText("");
  }
  
  public void setFinalRecognizedText(CharSequence paramCharSequence)
  {
    updateText(paramCharSequence);
  }
  
  void setStreamPosition(int paramInt)
  {
    this.mStreamPosition = paramInt;
    invalidate();
  }
  
  public void updateRecognizedText(String paramString1, String paramString2)
  {
    String str = paramString1;
    if (paramString1 == null) {
      str = "";
    }
    paramString1 = new SpannableStringBuilder(str);
    if (paramString2 != null)
    {
      int i = paramString1.length();
      paramString1.append(paramString2);
      addDottySpans(paramString1, paramString2, i);
    }
    this.mStreamPosition = Math.max(str.length(), this.mStreamPosition);
    updateText(new SpannedString(paramString1));
    startStreamAnimation();
  }
  
  public void updateRecognizedText(String paramString, List<Float> paramList) {}
  
  private class DottySpan
    extends ReplacementSpan
  {
    private final int mPosition;
    private final int mSeed;
    
    public DottySpan(int paramInt1, int paramInt2)
    {
      this.mSeed = paramInt1;
      this.mPosition = paramInt2;
    }
    
    public void draw(Canvas paramCanvas, CharSequence paramCharSequence, int paramInt1, int paramInt2, float paramFloat, int paramInt3, int paramInt4, int paramInt5, Paint paramPaint)
    {
      paramInt2 = (int)paramPaint.measureText(paramCharSequence, paramInt1, paramInt2);
      paramInt3 = StreamingTextView.this.mOneDot.getWidth();
      paramInt5 = paramInt3 * 2;
      int i = paramInt2 / paramInt5;
      int j = paramInt2 % paramInt5 / 2;
      boolean bool = StreamingTextView.isLayoutRtl(StreamingTextView.this);
      StreamingTextView.this.mRandom.setSeed(this.mSeed);
      int k = paramPaint.getAlpha();
      paramInt1 = 0;
      if ((paramInt1 >= i) || (this.mPosition + paramInt1 >= StreamingTextView.this.mStreamPosition))
      {
        paramPaint.setAlpha(k);
        return;
      }
      float f = paramInt1 * paramInt5 + j + paramInt3 / 2;
      if (bool)
      {
        f = paramInt2 + paramFloat - f - paramInt3;
        label144:
        paramPaint.setAlpha((StreamingTextView.this.mRandom.nextInt(4) + 1) * 63);
        if (!StreamingTextView.this.mRandom.nextBoolean()) {
          break label224;
        }
        paramCanvas.drawBitmap(StreamingTextView.this.mTwoDot, f, paramInt4 - StreamingTextView.this.mTwoDot.getHeight(), paramPaint);
      }
      for (;;)
      {
        paramInt1 += 1;
        break;
        f = paramFloat + f;
        break label144;
        label224:
        paramCanvas.drawBitmap(StreamingTextView.this.mOneDot, f, paramInt4 - StreamingTextView.this.mOneDot.getHeight(), paramPaint);
      }
    }
    
    public int getSize(Paint paramPaint, CharSequence paramCharSequence, int paramInt1, int paramInt2, Paint.FontMetricsInt paramFontMetricsInt)
    {
      return (int)paramPaint.measureText(paramCharSequence, paramInt1, paramInt2);
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/StreamingTextView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */