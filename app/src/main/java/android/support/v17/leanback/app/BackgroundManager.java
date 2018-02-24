package android.support.v17.leanback.app;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.ConstantState;
import android.graphics.drawable.LayerDrawable;
import android.os.Build.VERSION;
import android.os.Handler;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v17.leanback.R.color;
import android.support.v17.leanback.R.drawable;
import android.support.v17.leanback.R.id;
import android.support.v17.leanback.widget.BackgroundHelper;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import java.lang.ref.WeakReference;

public final class BackgroundManager
{
  private static final int CHANGE_BG_DELAY_MS = 500;
  static final boolean DEBUG = false;
  private static final int FADE_DURATION = 500;
  private static final String FRAGMENT_TAG = BackgroundManager.class.getCanonicalName();
  static final int FULL_ALPHA = 255;
  static final String TAG = "BackgroundManager";
  private final Interpolator mAccelerateInterpolator;
  private final Animator.AnimatorListener mAnimationListener = new Animator.AnimatorListener()
  {
    final Runnable mRunnable = new Runnable()
    {
      public void run()
      {
        BackgroundManager.this.postChangeRunnable();
      }
    };
    
    public void onAnimationCancel(Animator paramAnonymousAnimator) {}
    
    public void onAnimationEnd(Animator paramAnonymousAnimator)
    {
      if (BackgroundManager.this.mLayerDrawable != null) {
        BackgroundManager.this.mLayerDrawable.clearDrawable(R.id.background_imageout, BackgroundManager.this.mContext);
      }
      BackgroundManager.this.mHandler.post(this.mRunnable);
    }
    
    public void onAnimationRepeat(Animator paramAnonymousAnimator) {}
    
    public void onAnimationStart(Animator paramAnonymousAnimator) {}
  };
  private final ValueAnimator.AnimatorUpdateListener mAnimationUpdateListener = new ValueAnimator.AnimatorUpdateListener()
  {
    public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
    {
      int i = ((Integer)paramAnonymousValueAnimator.getAnimatedValue()).intValue();
      if (BackgroundManager.this.mImageInWrapperIndex != -1) {
        BackgroundManager.this.mLayerDrawable.setWrapperAlpha(BackgroundManager.this.mImageInWrapperIndex, i);
      }
    }
  };
  final ValueAnimator mAnimator;
  private boolean mAttached;
  private boolean mAutoReleaseOnStop = true;
  int mBackgroundColor;
  Drawable mBackgroundDrawable;
  private View mBgView;
  ChangeBackgroundRunnable mChangeRunnable;
  private boolean mChangeRunnablePending;
  Activity mContext;
  private final Interpolator mDecelerateInterpolator;
  private BackgroundFragment mFragmentState;
  Handler mHandler;
  private int mHeightPx;
  int mImageInWrapperIndex;
  int mImageOutWrapperIndex;
  private long mLastSetTime;
  TranslucentLayerDrawable mLayerDrawable;
  private BackgroundContinuityService mService;
  private int mThemeDrawableResourceId;
  private int mWidthPx;
  
  private BackgroundManager(Activity paramActivity)
  {
    this.mContext = paramActivity;
    this.mService = BackgroundContinuityService.getInstance();
    this.mHeightPx = this.mContext.getResources().getDisplayMetrics().heightPixels;
    this.mWidthPx = this.mContext.getResources().getDisplayMetrics().widthPixels;
    this.mHandler = new Handler();
    Object localObject = new FastOutLinearInInterpolator();
    this.mAccelerateInterpolator = AnimationUtils.loadInterpolator(this.mContext, 17432581);
    this.mDecelerateInterpolator = AnimationUtils.loadInterpolator(this.mContext, 17432582);
    this.mAnimator = ValueAnimator.ofInt(new int[] { 0, 255 });
    this.mAnimator.addListener(this.mAnimationListener);
    this.mAnimator.addUpdateListener(this.mAnimationUpdateListener);
    this.mAnimator.setInterpolator((TimeInterpolator)localObject);
    localObject = paramActivity.getTheme().obtainStyledAttributes(new int[] { 16842836 });
    this.mThemeDrawableResourceId = ((TypedArray)localObject).getResourceId(0, -1);
    if (this.mThemeDrawableResourceId < 0) {}
    ((TypedArray)localObject).recycle();
    createFragment(paramActivity);
  }
  
  static Drawable createEmptyDrawable(Context paramContext)
  {
    return new EmptyDrawable(paramContext.getResources());
  }
  
  private void createFragment(Activity paramActivity)
  {
    BackgroundFragment localBackgroundFragment = (BackgroundFragment)paramActivity.getFragmentManager().findFragmentByTag(FRAGMENT_TAG);
    if (localBackgroundFragment == null)
    {
      localBackgroundFragment = new BackgroundFragment();
      paramActivity.getFragmentManager().beginTransaction().add(localBackgroundFragment, FRAGMENT_TAG).commit();
      paramActivity = localBackgroundFragment;
    }
    do
    {
      paramActivity.setBackgroundManager(this);
      this.mFragmentState = paramActivity;
      return;
      paramActivity = localBackgroundFragment;
    } while (localBackgroundFragment.getBackgroundManager() == null);
    throw new IllegalStateException("Created duplicated BackgroundManager for same activity, please use getInstance() instead");
  }
  
  public static BackgroundManager getInstance(Activity paramActivity)
  {
    Object localObject = (BackgroundFragment)paramActivity.getFragmentManager().findFragmentByTag(FRAGMENT_TAG);
    if (localObject != null)
    {
      localObject = ((BackgroundFragment)localObject).getBackgroundManager();
      if (localObject != null) {
        return (BackgroundManager)localObject;
      }
    }
    return new BackgroundManager(paramActivity);
  }
  
  private long getRunnableDelay()
  {
    return Math.max(0L, this.mLastSetTime + 500L - System.currentTimeMillis());
  }
  
  private Drawable getThemeDrawable()
  {
    Drawable localDrawable1 = null;
    if (this.mThemeDrawableResourceId != -1) {
      localDrawable1 = this.mService.getThemeDrawable(this.mContext, this.mThemeDrawableResourceId);
    }
    Drawable localDrawable2 = localDrawable1;
    if (localDrawable1 == null) {
      localDrawable2 = createEmptyDrawable(this.mContext);
    }
    return localDrawable2;
  }
  
  private void lazyInit()
  {
    if (this.mLayerDrawable != null) {
      return;
    }
    this.mLayerDrawable = createTranslucentLayerDrawable((LayerDrawable)ContextCompat.getDrawable(this.mContext, R.drawable.lb_background).mutate());
    this.mImageInWrapperIndex = this.mLayerDrawable.findWrapperIndexById(R.id.background_imagein);
    this.mImageOutWrapperIndex = this.mLayerDrawable.findWrapperIndexById(R.id.background_imageout);
    BackgroundHelper.setBackgroundPreservingAlpha(this.mBgView, this.mLayerDrawable);
  }
  
  private void setDrawableInternal(Drawable paramDrawable)
  {
    if (!this.mAttached) {
      throw new IllegalStateException("Must attach before setting background drawable");
    }
    if (this.mChangeRunnable != null)
    {
      if (sameDrawable(paramDrawable, this.mChangeRunnable.mDrawable)) {
        return;
      }
      this.mHandler.removeCallbacks(this.mChangeRunnable);
      this.mChangeRunnable = null;
    }
    this.mChangeRunnable = new ChangeBackgroundRunnable(paramDrawable);
    this.mChangeRunnablePending = true;
    postChangeRunnable();
  }
  
  private void syncWithService()
  {
    int i = this.mService.getColor();
    Drawable localDrawable = this.mService.getDrawable();
    this.mBackgroundColor = i;
    if (localDrawable == null) {}
    for (localDrawable = null;; localDrawable = localDrawable.getConstantState().newDrawable().mutate())
    {
      this.mBackgroundDrawable = localDrawable;
      updateImmediate();
      return;
    }
  }
  
  private void updateImmediate()
  {
    if (!this.mAttached) {
      return;
    }
    lazyInit();
    if (this.mBackgroundDrawable == null) {
      this.mLayerDrawable.updateDrawable(R.id.background_imagein, getDefaultDrawable());
    }
    for (;;)
    {
      this.mLayerDrawable.clearDrawable(R.id.background_imageout, this.mContext);
      return;
      this.mLayerDrawable.updateDrawable(R.id.background_imagein, this.mBackgroundDrawable);
    }
  }
  
  public void attach(Window paramWindow)
  {
    attachToViewInternal(paramWindow.getDecorView());
  }
  
  public void attachToView(View paramView)
  {
    attachToViewInternal(paramView);
    View localView = this.mContext.getWindow().getDecorView();
    if (Build.VERSION.SDK_INT >= 26) {}
    for (paramView = null;; paramView = new ColorDrawable(0))
    {
      localView.setBackground(paramView);
      return;
    }
  }
  
  void attachToViewInternal(View paramView)
  {
    if (this.mAttached) {
      throw new IllegalStateException("Already attached to " + this.mBgView);
    }
    this.mBgView = paramView;
    this.mAttached = true;
    syncWithService();
  }
  
  public void clearDrawable()
  {
    setDrawable(null);
  }
  
  TranslucentLayerDrawable createTranslucentLayerDrawable(LayerDrawable paramLayerDrawable)
  {
    int j = paramLayerDrawable.getNumberOfLayers();
    Object localObject = new Drawable[j];
    int i = 0;
    while (i < j)
    {
      localObject[i] = paramLayerDrawable.getDrawable(i);
      i += 1;
    }
    localObject = new TranslucentLayerDrawable(this, (Drawable[])localObject);
    i = 0;
    while (i < j)
    {
      ((TranslucentLayerDrawable)localObject).setId(i, paramLayerDrawable.getId(i));
      i += 1;
    }
    return (TranslucentLayerDrawable)localObject;
  }
  
  void detach()
  {
    release();
    this.mBgView = null;
    this.mAttached = false;
    if (this.mService != null)
    {
      this.mService.unref();
      this.mService = null;
    }
  }
  
  @ColorInt
  public final int getColor()
  {
    return this.mBackgroundColor;
  }
  
  @Deprecated
  public Drawable getDefaultDimLayer()
  {
    return ContextCompat.getDrawable(this.mContext, R.color.lb_background_protection);
  }
  
  Drawable getDefaultDrawable()
  {
    if (this.mBackgroundColor != 0) {
      return new ColorDrawable(this.mBackgroundColor);
    }
    return getThemeDrawable();
  }
  
  @Deprecated
  public Drawable getDimLayer()
  {
    return null;
  }
  
  public Drawable getDrawable()
  {
    return this.mBackgroundDrawable;
  }
  
  DrawableWrapper getImageInWrapper()
  {
    if (this.mLayerDrawable == null) {
      return null;
    }
    return this.mLayerDrawable.mWrapper[this.mImageInWrapperIndex];
  }
  
  DrawableWrapper getImageOutWrapper()
  {
    if (this.mLayerDrawable == null) {
      return null;
    }
    return this.mLayerDrawable.mWrapper[this.mImageOutWrapperIndex];
  }
  
  public boolean isAttached()
  {
    return this.mAttached;
  }
  
  public boolean isAutoReleaseOnStop()
  {
    return this.mAutoReleaseOnStop;
  }
  
  void onActivityStart()
  {
    updateImmediate();
  }
  
  void onResume()
  {
    postChangeRunnable();
  }
  
  void onStop()
  {
    if (isAutoReleaseOnStop()) {
      release();
    }
  }
  
  void postChangeRunnable()
  {
    if ((this.mChangeRunnable == null) || (!this.mChangeRunnablePending)) {}
    while ((this.mAnimator.isStarted()) || (!this.mFragmentState.isResumed()) || (this.mLayerDrawable.getAlpha() < 255)) {
      return;
    }
    long l = getRunnableDelay();
    this.mLastSetTime = System.currentTimeMillis();
    this.mHandler.postDelayed(this.mChangeRunnable, l);
    this.mChangeRunnablePending = false;
  }
  
  public void release()
  {
    if (this.mChangeRunnable != null)
    {
      this.mHandler.removeCallbacks(this.mChangeRunnable);
      this.mChangeRunnable = null;
    }
    if (this.mAnimator.isStarted()) {
      this.mAnimator.cancel();
    }
    if (this.mLayerDrawable != null)
    {
      this.mLayerDrawable.clearDrawable(R.id.background_imagein, this.mContext);
      this.mLayerDrawable.clearDrawable(R.id.background_imageout, this.mContext);
      this.mLayerDrawable = null;
    }
    this.mBackgroundDrawable = null;
  }
  
  boolean sameDrawable(Drawable paramDrawable1, Drawable paramDrawable2)
  {
    if ((paramDrawable1 == null) || (paramDrawable2 == null)) {
      return false;
    }
    if (paramDrawable1 == paramDrawable2) {
      return true;
    }
    if (((paramDrawable1 instanceof BitmapDrawable)) && ((paramDrawable2 instanceof BitmapDrawable)) && (((BitmapDrawable)paramDrawable1).getBitmap().sameAs(((BitmapDrawable)paramDrawable2).getBitmap()))) {
      return true;
    }
    return ((paramDrawable1 instanceof ColorDrawable)) && ((paramDrawable2 instanceof ColorDrawable)) && (((ColorDrawable)paramDrawable1).getColor() == ((ColorDrawable)paramDrawable2).getColor());
  }
  
  public void setAutoReleaseOnStop(boolean paramBoolean)
  {
    this.mAutoReleaseOnStop = paramBoolean;
  }
  
  public void setBitmap(Bitmap paramBitmap)
  {
    if (paramBitmap == null) {
      setDrawable(null);
    }
    while ((paramBitmap.getWidth() <= 0) || (paramBitmap.getHeight() <= 0)) {
      return;
    }
    Matrix localMatrix = null;
    int i;
    int j;
    if ((paramBitmap.getWidth() != this.mWidthPx) || (paramBitmap.getHeight() != this.mHeightPx))
    {
      i = paramBitmap.getWidth();
      j = paramBitmap.getHeight();
      if (this.mHeightPx * i <= this.mWidthPx * j) {
        break label155;
      }
    }
    label155:
    for (float f = this.mHeightPx / j;; f = this.mWidthPx / i)
    {
      i = Math.max(0, (i - Math.min((int)(this.mWidthPx / f), i)) / 2);
      localMatrix = new Matrix();
      localMatrix.setScale(f, f);
      localMatrix.preTranslate(-i, 0.0F);
      setDrawable(new BitmapDrawable(this.mContext.getResources(), paramBitmap, localMatrix));
      return;
    }
  }
  
  public void setColor(@ColorInt int paramInt)
  {
    this.mService.setColor(paramInt);
    this.mBackgroundColor = paramInt;
    this.mBackgroundDrawable = null;
    if (this.mLayerDrawable == null) {
      return;
    }
    setDrawableInternal(getDefaultDrawable());
  }
  
  @Deprecated
  public void setDimLayer(Drawable paramDrawable) {}
  
  public void setDrawable(Drawable paramDrawable)
  {
    this.mService.setDrawable(paramDrawable);
    this.mBackgroundDrawable = paramDrawable;
    if (this.mLayerDrawable == null) {
      return;
    }
    if (paramDrawable == null)
    {
      setDrawableInternal(getDefaultDrawable());
      return;
    }
    setDrawableInternal(paramDrawable);
  }
  
  public void setThemeDrawableResourceId(int paramInt)
  {
    this.mThemeDrawableResourceId = paramInt;
  }
  
  private static class BackgroundContinuityService
  {
    private static boolean DEBUG = false;
    private static final String TAG = "BackgroundContinuity";
    private static BackgroundContinuityService sService = new BackgroundContinuityService();
    private int mColor;
    private int mCount;
    private Drawable mDrawable;
    private int mLastThemeDrawableId;
    private WeakReference<Drawable.ConstantState> mLastThemeDrawableState;
    
    private BackgroundContinuityService()
    {
      reset();
    }
    
    public static BackgroundContinuityService getInstance()
    {
      BackgroundContinuityService localBackgroundContinuityService = sService;
      int i = localBackgroundContinuityService.mCount;
      localBackgroundContinuityService.mCount = (i + 1);
      if (DEBUG) {
        Log.v("BackgroundContinuity", "Returning instance with new count " + i);
      }
      return sService;
    }
    
    private void reset()
    {
      this.mColor = 0;
      this.mDrawable = null;
    }
    
    public int getColor()
    {
      return this.mColor;
    }
    
    public Drawable getDrawable()
    {
      return this.mDrawable;
    }
    
    public Drawable getThemeDrawable(Context paramContext, int paramInt)
    {
      Object localObject2 = null;
      Object localObject1 = localObject2;
      if (this.mLastThemeDrawableState != null)
      {
        localObject1 = localObject2;
        if (this.mLastThemeDrawableId == paramInt)
        {
          Drawable.ConstantState localConstantState = (Drawable.ConstantState)this.mLastThemeDrawableState.get();
          if (DEBUG) {
            Log.v("BackgroundContinuity", "got cached theme drawable state " + localConstantState);
          }
          localObject1 = localObject2;
          if (localConstantState != null) {
            localObject1 = localConstantState.newDrawable();
          }
        }
      }
      localObject2 = localObject1;
      if (localObject1 == null)
      {
        localObject2 = ContextCompat.getDrawable(paramContext, paramInt);
        if (DEBUG) {
          Log.v("BackgroundContinuity", "loaded theme drawable " + localObject2);
        }
        this.mLastThemeDrawableState = new WeakReference(((Drawable)localObject2).getConstantState());
        this.mLastThemeDrawableId = paramInt;
      }
      return (Drawable)localObject2;
    }
    
    public void setColor(int paramInt)
    {
      this.mColor = paramInt;
      this.mDrawable = null;
    }
    
    public void setDrawable(Drawable paramDrawable)
    {
      this.mDrawable = paramDrawable;
    }
    
    public void unref()
    {
      if (this.mCount <= 0) {
        throw new IllegalStateException("Can't unref, count " + this.mCount);
      }
      int i = this.mCount - 1;
      this.mCount = i;
      if (i == 0)
      {
        if (DEBUG) {
          Log.v("BackgroundContinuity", "mCount is zero, resetting");
        }
        reset();
      }
    }
  }
  
  static class BitmapDrawable
    extends Drawable
  {
    boolean mMutated;
    ConstantState mState;
    
    BitmapDrawable(Resources paramResources, Bitmap paramBitmap)
    {
      this(paramResources, paramBitmap, null);
    }
    
    BitmapDrawable(Resources paramResources, Bitmap paramBitmap, Matrix paramMatrix)
    {
      this.mState = new ConstantState(paramBitmap, paramMatrix);
    }
    
    BitmapDrawable(ConstantState paramConstantState)
    {
      this.mState = paramConstantState;
    }
    
    public void draw(Canvas paramCanvas)
    {
      if (this.mState.mBitmap == null) {
        return;
      }
      if ((this.mState.mPaint.getAlpha() < 255) && (this.mState.mPaint.getColorFilter() != null)) {
        throw new IllegalStateException("Can't draw with translucent alpha and color filter");
      }
      paramCanvas.drawBitmap(this.mState.mBitmap, this.mState.mMatrix, this.mState.mPaint);
    }
    
    Bitmap getBitmap()
    {
      return this.mState.mBitmap;
    }
    
    public ColorFilter getColorFilter()
    {
      return this.mState.mPaint.getColorFilter();
    }
    
    public ConstantState getConstantState()
    {
      return this.mState;
    }
    
    public int getOpacity()
    {
      return -3;
    }
    
    @NonNull
    public Drawable mutate()
    {
      if (!this.mMutated)
      {
        this.mMutated = true;
        this.mState = new ConstantState(this.mState);
      }
      return this;
    }
    
    public void setAlpha(int paramInt)
    {
      mutate();
      if (this.mState.mPaint.getAlpha() != paramInt)
      {
        this.mState.mPaint.setAlpha(paramInt);
        invalidateSelf();
      }
    }
    
    public void setColorFilter(ColorFilter paramColorFilter)
    {
      mutate();
      this.mState.mPaint.setColorFilter(paramColorFilter);
      invalidateSelf();
    }
    
    static final class ConstantState
      extends Drawable.ConstantState
    {
      final Bitmap mBitmap;
      final Matrix mMatrix;
      final Paint mPaint = new Paint();
      
      ConstantState(Bitmap paramBitmap, Matrix paramMatrix)
      {
        this.mBitmap = paramBitmap;
        if (paramMatrix != null) {}
        for (;;)
        {
          this.mMatrix = paramMatrix;
          this.mPaint.setFilterBitmap(true);
          return;
          paramMatrix = new Matrix();
        }
      }
      
      ConstantState(ConstantState paramConstantState)
      {
        this.mBitmap = paramConstantState.mBitmap;
        if (paramConstantState.mMatrix != null) {}
        for (Matrix localMatrix = new Matrix(paramConstantState.mMatrix);; localMatrix = new Matrix())
        {
          this.mMatrix = localMatrix;
          if (paramConstantState.mPaint.getAlpha() != 255) {
            this.mPaint.setAlpha(paramConstantState.mPaint.getAlpha());
          }
          if (paramConstantState.mPaint.getColorFilter() != null) {
            this.mPaint.setColorFilter(paramConstantState.mPaint.getColorFilter());
          }
          this.mPaint.setFilterBitmap(true);
          return;
        }
      }
      
      public int getChangingConfigurations()
      {
        return 0;
      }
      
      public Drawable newDrawable()
      {
        return new BackgroundManager.BitmapDrawable(this);
      }
    }
  }
  
  final class ChangeBackgroundRunnable
    implements Runnable
  {
    final Drawable mDrawable;
    
    ChangeBackgroundRunnable(Drawable paramDrawable)
    {
      this.mDrawable = paramDrawable;
    }
    
    private void runTask()
    {
      if (BackgroundManager.this.mLayerDrawable == null) {}
      BackgroundManager.DrawableWrapper localDrawableWrapper;
      do
      {
        return;
        localDrawableWrapper = BackgroundManager.this.getImageInWrapper();
        if (localDrawableWrapper == null) {
          break;
        }
      } while (BackgroundManager.this.sameDrawable(this.mDrawable, localDrawableWrapper.getDrawable()));
      BackgroundManager.this.mLayerDrawable.clearDrawable(R.id.background_imagein, BackgroundManager.this.mContext);
      BackgroundManager.this.mLayerDrawable.updateDrawable(R.id.background_imageout, localDrawableWrapper.getDrawable());
      applyBackgroundChanges();
    }
    
    void applyBackgroundChanges()
    {
      if (!BackgroundManager.this.mAttached) {
        return;
      }
      if ((BackgroundManager.this.getImageInWrapper() == null) && (this.mDrawable != null))
      {
        BackgroundManager.this.mLayerDrawable.updateDrawable(R.id.background_imagein, this.mDrawable);
        BackgroundManager.this.mLayerDrawable.setWrapperAlpha(BackgroundManager.this.mImageInWrapperIndex, 0);
      }
      BackgroundManager.this.mAnimator.setDuration(500L);
      BackgroundManager.this.mAnimator.start();
    }
    
    public void run()
    {
      runTask();
      BackgroundManager.this.mChangeRunnable = null;
    }
  }
  
  static final class DrawableWrapper
  {
    int mAlpha = 255;
    final Drawable mDrawable;
    
    public DrawableWrapper(Drawable paramDrawable)
    {
      this.mDrawable = paramDrawable;
    }
    
    public DrawableWrapper(DrawableWrapper paramDrawableWrapper, Drawable paramDrawable)
    {
      this.mDrawable = paramDrawable;
      this.mAlpha = paramDrawableWrapper.mAlpha;
    }
    
    public Drawable getDrawable()
    {
      return this.mDrawable;
    }
    
    public void setColor(int paramInt)
    {
      ((ColorDrawable)this.mDrawable).setColor(paramInt);
    }
  }
  
  static class EmptyDrawable
    extends BackgroundManager.BitmapDrawable
  {
    EmptyDrawable(Resources paramResources)
    {
      super((Bitmap)null);
    }
  }
  
  static final class TranslucentLayerDrawable
    extends LayerDrawable
  {
    int mAlpha = 255;
    WeakReference<BackgroundManager> mManagerWeakReference;
    boolean mSuspendInvalidation;
    BackgroundManager.DrawableWrapper[] mWrapper;
    
    TranslucentLayerDrawable(BackgroundManager paramBackgroundManager, Drawable[] paramArrayOfDrawable)
    {
      super();
      this.mManagerWeakReference = new WeakReference(paramBackgroundManager);
      int j = paramArrayOfDrawable.length;
      this.mWrapper = new BackgroundManager.DrawableWrapper[j];
      int i = 0;
      while (i < j)
      {
        this.mWrapper[i] = new BackgroundManager.DrawableWrapper(paramArrayOfDrawable[i]);
        i += 1;
      }
    }
    
    public void clearDrawable(int paramInt, Context paramContext)
    {
      int i = 0;
      for (;;)
      {
        if (i < getNumberOfLayers())
        {
          if (getId(i) != paramInt) {
            break label48;
          }
          this.mWrapper[i] = null;
          if (!(getDrawable(i) instanceof BackgroundManager.EmptyDrawable)) {
            super.setDrawableByLayerId(paramInt, BackgroundManager.createEmptyDrawable(paramContext));
          }
        }
        return;
        label48:
        i += 1;
      }
    }
    
    public void draw(Canvas paramCanvas)
    {
      int m = 0;
      if (m < this.mWrapper.length)
      {
        Drawable localDrawable;
        if (this.mWrapper[m] != null)
        {
          localDrawable = this.mWrapper[m].getDrawable();
          if (localDrawable != null) {
            if (Build.VERSION.SDK_INT < 19) {
              break label148;
            }
          }
        }
        int j;
        int k;
        int n;
        label148:
        for (int i = DrawableCompat.getAlpha(localDrawable);; i = 255)
        {
          j = 0;
          k = i;
          if (this.mAlpha < 255)
          {
            k = i * this.mAlpha;
            j = 0 + 1;
          }
          int i1 = k;
          k = i1;
          n = j;
          if (this.mWrapper[m].mAlpha < 255)
          {
            k = i1 * this.mWrapper[m].mAlpha;
            n = j + 1;
          }
          if (n != 0) {
            break label155;
          }
          localDrawable.draw(paramCanvas);
          m += 1;
          break;
        }
        label155:
        if (n == 1) {
          j = k / 255;
        }
        for (;;)
        {
          try
          {
            this.mSuspendInvalidation = true;
            localDrawable.setAlpha(j);
            localDrawable.draw(paramCanvas);
            localDrawable.setAlpha(i);
            this.mSuspendInvalidation = false;
          }
          finally
          {
            this.mSuspendInvalidation = false;
          }
          j = k;
          if (n == 2) {
            j = k / 65025;
          }
        }
      }
    }
    
    public int findWrapperIndexById(int paramInt)
    {
      int i = 0;
      while (i < getNumberOfLayers())
      {
        if (getId(i) == paramInt) {
          return i;
        }
        i += 1;
      }
      return -1;
    }
    
    public int getAlpha()
    {
      return this.mAlpha;
    }
    
    public int getOpacity()
    {
      return -3;
    }
    
    public void invalidateDrawable(Drawable paramDrawable)
    {
      if (!this.mSuspendInvalidation) {
        super.invalidateDrawable(paramDrawable);
      }
    }
    
    public Drawable mutate()
    {
      Drawable localDrawable = super.mutate();
      int j = getNumberOfLayers();
      int i = 0;
      while (i < j)
      {
        if (this.mWrapper[i] != null) {
          this.mWrapper[i] = new BackgroundManager.DrawableWrapper(this.mWrapper[i], getDrawable(i));
        }
        i += 1;
      }
      return localDrawable;
    }
    
    public void setAlpha(int paramInt)
    {
      if (this.mAlpha != paramInt)
      {
        this.mAlpha = paramInt;
        invalidateSelf();
        BackgroundManager localBackgroundManager = (BackgroundManager)this.mManagerWeakReference.get();
        if (localBackgroundManager != null) {
          localBackgroundManager.postChangeRunnable();
        }
      }
    }
    
    public boolean setDrawableByLayerId(int paramInt, Drawable paramDrawable)
    {
      return updateDrawable(paramInt, paramDrawable) != null;
    }
    
    void setWrapperAlpha(int paramInt1, int paramInt2)
    {
      if (this.mWrapper[paramInt1] != null)
      {
        this.mWrapper[paramInt1].mAlpha = paramInt2;
        invalidateSelf();
      }
    }
    
    public BackgroundManager.DrawableWrapper updateDrawable(int paramInt, Drawable paramDrawable)
    {
      super.setDrawableByLayerId(paramInt, paramDrawable);
      int i = 0;
      while (i < getNumberOfLayers())
      {
        if (getId(i) == paramInt)
        {
          this.mWrapper[i] = new BackgroundManager.DrawableWrapper(paramDrawable);
          invalidateSelf();
          return this.mWrapper[i];
        }
        i += 1;
      }
      return null;
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/app/BackgroundManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */