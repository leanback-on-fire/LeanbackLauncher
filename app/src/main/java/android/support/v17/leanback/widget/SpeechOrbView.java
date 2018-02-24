package android.support.v17.leanback.widget;

import android.content.Context;
import android.content.res.Resources;
import android.support.v17.leanback.R.color;
import android.support.v17.leanback.R.drawable;
import android.support.v17.leanback.R.fraction;
import android.support.v17.leanback.R.layout;
import android.util.AttributeSet;

public class SpeechOrbView
  extends SearchOrbView
{
  private int mCurrentLevel = 0;
  private boolean mListening = false;
  private SearchOrbView.Colors mListeningOrbColors;
  private SearchOrbView.Colors mNotListeningOrbColors;
  private final float mSoundLevelMaxZoom;
  
  public SpeechOrbView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public SpeechOrbView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public SpeechOrbView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    paramContext = paramContext.getResources();
    this.mSoundLevelMaxZoom = paramContext.getFraction(R.fraction.lb_search_bar_speech_orb_max_level_zoom, 1, 1);
    this.mNotListeningOrbColors = new SearchOrbView.Colors(paramContext.getColor(R.color.lb_speech_orb_not_recording), paramContext.getColor(R.color.lb_speech_orb_not_recording_pulsed), paramContext.getColor(R.color.lb_speech_orb_not_recording_icon));
    this.mListeningOrbColors = new SearchOrbView.Colors(paramContext.getColor(R.color.lb_speech_orb_recording), paramContext.getColor(R.color.lb_speech_orb_recording), 0);
    showNotListening();
  }
  
  int getLayoutResourceId()
  {
    return R.layout.lb_speech_orb;
  }
  
  public void setListeningOrbColors(SearchOrbView.Colors paramColors)
  {
    this.mListeningOrbColors = paramColors;
  }
  
  public void setNotListeningOrbColors(SearchOrbView.Colors paramColors)
  {
    this.mNotListeningOrbColors = paramColors;
  }
  
  public void setSoundLevel(int paramInt)
  {
    if (!this.mListening) {
      return;
    }
    if (paramInt > this.mCurrentLevel) {}
    for (this.mCurrentLevel += (paramInt - this.mCurrentLevel) / 2;; this.mCurrentLevel = ((int)(this.mCurrentLevel * 0.7F)))
    {
      scaleOrbViewOnly(1.0F + (this.mSoundLevelMaxZoom - getFocusedZoom()) * this.mCurrentLevel / 100.0F);
      return;
    }
  }
  
  public void showListening()
  {
    setOrbColors(this.mListeningOrbColors);
    setOrbIcon(getResources().getDrawable(R.drawable.lb_ic_search_mic));
    animateOnFocus(true);
    enableOrbColorAnimation(false);
    scaleOrbViewOnly(1.0F);
    this.mCurrentLevel = 0;
    this.mListening = true;
  }
  
  public void showNotListening()
  {
    setOrbColors(this.mNotListeningOrbColors);
    setOrbIcon(getResources().getDrawable(R.drawable.lb_ic_search_mic_out));
    animateOnFocus(hasFocus());
    scaleOrbViewOnly(1.0F);
    this.mListening = false;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/SpeechOrbView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */