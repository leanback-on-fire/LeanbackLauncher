package com.rockon999.android.tvrecommendations.service;

public abstract class RankerParameters {
    static final float BONUS_FADE_PERIOD_DAYS_DEFAULT = 0.5f;
    static final float GROUP_STARTER_SCORE_DEFAULT = 0.001f;
    static final float INSTALL_BONUS_DEFAULT = 0.3f;
    static final float OUT_OF_BOX_BONUS_DEFAULT = 0.005f;
    static final float SPREAD_FACTOR_DEFAULT = 1.0f;
    private float mBonusFadePeriodDays;
    private float mGroupStarterScore;
    private float mInstallBonus;
    private Object mLastVersionToken = null;
    private float mOutOfBoxBonus;
    private float mSpreadFactor;

    protected abstract float getFloat(String str, float f);

    protected abstract Object getVersionToken();

    private void checkUpdateGservicesFlags() {
        Object versionToken = getVersionToken();
        if (!versionToken.equals(this.mLastVersionToken)) {
            this.mLastVersionToken = versionToken;
            this.mSpreadFactor = getFloat("rec_ranker_spread_factor", SPREAD_FACTOR_DEFAULT);
            this.mGroupStarterScore = getFloat("rec_ranker_group_starter_score", GROUP_STARTER_SCORE_DEFAULT);
            this.mInstallBonus = getFloat("rec_ranker_install_bonus", INSTALL_BONUS_DEFAULT);
            this.mOutOfBoxBonus = getFloat("rec_ranker_out_of_box_bonus", OUT_OF_BOX_BONUS_DEFAULT);
            this.mBonusFadePeriodDays = getFloat("bonus_fade_period_days", BONUS_FADE_PERIOD_DAYS_DEFAULT);
        }
    }

    public final float getOutOfBoxBonus() {
        checkUpdateGservicesFlags();
        return this.mOutOfBoxBonus;
    }

    public final float getGroupStarterScore() {
        checkUpdateGservicesFlags();
        return this.mGroupStarterScore;
    }

    public final float getInstallBonus() {
        checkUpdateGservicesFlags();
        return this.mInstallBonus;
    }

    public final float getSpreadFactor() {
        checkUpdateGservicesFlags();
        return this.mSpreadFactor;
    }

    public final float getBonusFadePeriodDays() {
        checkUpdateGservicesFlags();
        return this.mBonusFadePeriodDays;
    }
}
