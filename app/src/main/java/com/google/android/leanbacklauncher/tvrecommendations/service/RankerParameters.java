package com.google.android.leanbacklauncher.tvrecommendations.service;

import android.content.ContentResolver;
import android.content.Context;
import com.google.android.leanbacklauncher.gsf.Gservices;

final class RankerParameters {
    private static float SPREAD_FACTOR_DEFAULT;
    private float mBonusFadePeriodDays;
    private ContentResolver mContentResolver;
    private float mGroupStarterScore;
    private float mInstallBonus;
    private Object mLastVersionToken;
    private float mOutOfBoxBonus;
    private float mSpreadFactor;

    static {
        SPREAD_FACTOR_DEFAULT = 1.0f;
    }

    public RankerParameters(Context ctx) {
        this.mLastVersionToken = null;
        this.mContentResolver = ctx.getContentResolver();
    }

    private final void CheckUpdateGservicesFlags() {
        Object versionToken = Gservices.getVersionToken(this.mContentResolver);
        if (!versionToken.equals(this.mLastVersionToken)) {
            this.mLastVersionToken = versionToken;
            this.mSpreadFactor = Gservices.getFloat(this.mContentResolver, "rec_ranker_spread_factor", SPREAD_FACTOR_DEFAULT);
            this.mGroupStarterScore = Gservices.getFloat(this.mContentResolver, "rec_ranker_group_starter_score", 0.001f);
            this.mInstallBonus = Gservices.getFloat(this.mContentResolver, "rec_ranker_install_bonus", 0.3f);
            this.mOutOfBoxBonus = Gservices.getFloat(this.mContentResolver, "rec_ranker_out_of_box_bonus", 0.005f);
            this.mBonusFadePeriodDays = Gservices.getFloat(this.mContentResolver, "bonus_fade_period_days", 0.5f);
        }
    }

    public final float getOutOfBoxBonus() {
        CheckUpdateGservicesFlags();
        return this.mOutOfBoxBonus;
    }

    public final float getGroupStarterScore() {
        CheckUpdateGservicesFlags();
        return this.mGroupStarterScore;
    }

    public final float getInstallBonus() {
        CheckUpdateGservicesFlags();
        return this.mInstallBonus;
    }

    public final float getSpreadFactor() {
        CheckUpdateGservicesFlags();
        return this.mSpreadFactor;
    }

    public final float getBonusFadePeriodDays() {
        CheckUpdateGservicesFlags();
        return this.mBonusFadePeriodDays;
    }
}
