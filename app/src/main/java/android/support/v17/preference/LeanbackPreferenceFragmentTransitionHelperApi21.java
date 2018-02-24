package android.support.v17.preference;

import android.app.Fragment;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.support.v17.leanback.transition.FadeAndShortSlide;

@RequiresApi(21)
@RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
public class LeanbackPreferenceFragmentTransitionHelperApi21
{
  public static void addTransitions(Fragment paramFragment)
  {
    FadeAndShortSlide localFadeAndShortSlide1 = new FadeAndShortSlide(8388611);
    FadeAndShortSlide localFadeAndShortSlide2 = new FadeAndShortSlide(8388613);
    paramFragment.setEnterTransition(localFadeAndShortSlide2);
    paramFragment.setExitTransition(localFadeAndShortSlide1);
    paramFragment.setReenterTransition(localFadeAndShortSlide1);
    paramFragment.setReturnTransition(localFadeAndShortSlide2);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/preference/LeanbackPreferenceFragmentTransitionHelperApi21.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */