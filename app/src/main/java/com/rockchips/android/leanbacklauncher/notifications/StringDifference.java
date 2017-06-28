package com.rockchips.android.leanbacklauncher.notifications;

import android.util.Log;
import com.rockchips.android.leanbacklauncher.tvrecommendations.TvRecommendation;
import java.lang.reflect.Array;
import java.util.ArrayList;

final class StringDifference {
    private static  /* synthetic */ int[] f10xf9b83482 ;

    public static final class EditItem {
        TvRecommendation mItem;
        Op mOp;
        int mSrcIndex;

        public enum Op {
            INSERT,
            DELETE,
            SUB,
            UPDATE
        }
    }

    public static class ExtractDeleteAndUpdateResult {
        ArrayList<EditItem> mItems;
        int mRemainingEditItems;
    }

    private static class LevenshteinDistance {
        private int[][] f9D;
        private ArrayList<TvRecommendation> mPendingList;
        private final int mPendingListDim;
        private final int mVisibleListDim;

        LevenshteinDistance(ArrayList<TvRecommendation> willView, ArrayList<TvRecommendation> nowView) {
            int i;
            this.mPendingListDim = willView.size();
            this.mVisibleListDim = nowView.size();
            this.mPendingList = willView;
            this.f9D = (int[][]) Array.newInstance(Integer.TYPE, new int[]{this.mPendingListDim + 1, this.mVisibleListDim + 1});
            for (i = 1; i <= this.mPendingListDim; i++) {
                this.f9D[i][0] = 536870912 | i;
            }
            for (i = 1; i <= this.mVisibleListDim; i++) {
                this.f9D[0][i] = 1073741824 | i;
            }
            for (i = 1; i <= this.mPendingListDim; i++) {
                for (int j = 1; j <= this.mVisibleListDim; j++) {
                    int min;
                    if (NotificationUtils.equals((TvRecommendation) willView.get(i - 1), (TvRecommendation) nowView.get(j - 1))) {
                        min = (this.f9D[i - 1][j - 1] & 536870911) | Integer.MIN_VALUE;
                    } else {
                        int del = (this.f9D[i - 1][j] & 536870911) + 1;
                        int ins = (this.f9D[i][j - 1] & 536870911) + 1;
                        int sub = (this.f9D[i - 1][j - 1] & 536870911) + 2;
                        if (del < ins) {
                            if (del <= sub) {
                                min = del | 1073741824;
                            } else {
                                min = sub | 1610612736;
                            }
                        } else if (ins <= sub) {
                            min = ins | 536870912;
                        } else {
                            min = sub | 1610612736;
                        }
                    }
                    this.f9D[i][j] = min;
                }
            }
        }

        private ArrayList<EditItem> getChangeList() {
            int[] changeList = new int[((this.mPendingListDim + this.mVisibleListDim) + 1)];
            int i = this.mPendingListDim;
            int j = this.mVisibleListDim;
            int ndx = 0;
            while (true) {
                int change;
                if (i == 0 && j == 0) {
                    i = 0;
                    for (j = ndx - 1; i < j; j--) {
                        int t = changeList[i];
                        changeList[i] = changeList[j];
                        changeList[j] = t;
                        i++;
                    }
                    ArrayList<EditItem> changeItems = new ArrayList();
                    for (int k = 0; k < ndx; k++) {
                        EditItem changeItem = new EditItem();
                        change = changeList[k];
                        int op = change & -536870912;
                        switch (op) {
                            case 536870912:
                                changeItem.mOp = EditItem.Op.INSERT;
                                break;
                            case 1073741824:
                                changeItem.mOp = EditItem.Op.DELETE;
                                break;
                            case 1610612736:
                                changeItem.mOp = EditItem.Op.SUB;
                                break;
                        }
                        changeItem.mSrcIndex = 536870911 & change;
                        if (op == 536870912 || op == 1610612736) {
                            changeItem.mItem = (TvRecommendation) this.mPendingList.get(536870911 & change);
                        }
                        changeItems.add(changeItem);
                    }
                    return changeItems;
                }
                int ndx2;
                switch (this.f9D[i][j] & -536870912) {
                    case Integer.MIN_VALUE:
                        i--;
                        j--;
                        ndx2 = ndx;
                        break;
                    case 536870912:
                    case 1073741824:
                    case 1610612736:
                        int ins;
                        int del;
                        int sub;
                        int dir;
                        if (i > 0) {
                            ins = this.f9D[i - 1][j];
                        } else {
                            ins = Integer.MAX_VALUE;
                        }
                        if (j > 0) {
                            del = this.f9D[i][j - 1];
                        } else {
                            del = Integer.MAX_VALUE;
                        }
                        if (i <= 0 || j <= 0) {
                            sub = Integer.MAX_VALUE;
                        } else {
                            sub = this.f9D[i - 1][j - 1];
                        }
                        if (del < ins) {
                            if (del <= sub) {
                                dir = 1073741824;
                            } else {
                                dir = 1610612736;
                            }
                        } else if (ins <= sub) {
                            dir = 536870912;
                        } else {
                            dir = 1610612736;
                        }
                        change = 0;
                        switch (dir) {
                            case 536870912:
                                change = (i - 1) | 536870912;
                                i--;
                                break;
                            case 1073741824:
                                change = i | 1073741824;
                                j--;
                                break;
                            case 1610612736:
                                change = (i - 1) | 1610612736;
                                i--;
                                j--;
                                break;
                        }
                        ndx2 = ndx + 1;
                        changeList[ndx] = change;
                        break;
                    default:
                        ndx2 = ndx;
                        break;
                }
                ndx = ndx2;
            }
        }
    }

    private static /* synthetic */ int[] m1989x4e27365e() {
        if (f10xf9b83482 != null) {
            return f10xf9b83482;
        }
        int[] iArr = new int[EditItem.Op.values().length];
        try {
            iArr[EditItem.Op.DELETE.ordinal()] = 1;
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[EditItem.Op.INSERT.ordinal()] = 2;
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[EditItem.Op.SUB.ordinal()] = 3;
        } catch (NoSuchFieldError e3) {
        }
        try {
            iArr[EditItem.Op.UPDATE.ordinal()] = 4;
        } catch (NoSuchFieldError e4) {
        }
        f10xf9b83482 = iArr;
        return iArr;
    }

    private StringDifference() {
    }

    public static ArrayList<EditItem> calculateStringAlignment(ArrayList<TvRecommendation> src, ArrayList<TvRecommendation> dst) {
        ArrayList<EditItem> editItems = new LevenshteinDistance(src, dst).getChangeList();
        int itemNdx = 0;
        int itemDim = editItems.size();
        int srcNdx = 0;
        int srcDim = src.size();
        int dstNdx = 0;
        int dstDim = dst.size();
        int cumulativeAdjustment = 0;
        while (itemNdx < itemDim && srcNdx < srcDim && dstNdx < dstDim) {
            EditItem item = (EditItem) editItems.get(itemNdx);
            int itemSrcNdx = item.mSrcIndex;
            while (srcNdx < srcDim && dstNdx < dstDim && dstNdx + cumulativeAdjustment < itemSrcNdx) {
                TvRecommendation srcRec = (TvRecommendation) src.get(srcNdx);
                if (srcRec.getPostTime() != ((TvRecommendation) dst.get(dstNdx)).getPostTime()) {
                    EditItem updItem = new EditItem();
                    updItem.mOp = EditItem.Op.UPDATE;
                    updItem.mSrcIndex = dstNdx + cumulativeAdjustment;
                    updItem.mItem = srcRec;
                    editItems.add(itemNdx, updItem);
                    itemNdx++;
                    itemDim++;
                }
                srcNdx++;
                dstNdx++;
            }
            switch (m1989x4e27365e()[item.mOp.ordinal()]) {
                case android.support.v7.recyclerview.R.styleable.RecyclerView_android_descendantFocusability /*1*/:
                    cumulativeAdjustment--;
                    dstNdx++;
                    break;
                case android.support.v7.recyclerview.R.styleable.RecyclerView_layoutManager /*2*/:
                    cumulativeAdjustment++;
                    srcNdx++;
                    break;
                case android.support.v7.preference.R.styleable.Preference_android_layout /*3*/:
                    srcNdx++;
                    dstNdx++;
                    break;
                default:
                    break;
            }
            itemNdx++;
        }
        while (srcNdx < srcDim && dstNdx < dstDim) {
            //TvRecommendation srcRec = (TvRecommendation) src.get(srcNdx);
            TvRecommendation srcRec = (TvRecommendation) src.get(srcNdx);
            if (srcRec.getPostTime() != ((TvRecommendation) dst.get(dstNdx)).getPostTime()) {
                EditItem updItem = new EditItem();
                updItem.mOp = EditItem.Op.UPDATE;
                updItem.mSrcIndex = dstNdx + cumulativeAdjustment;
                updItem.mItem = srcRec;
                editItems.add(updItem);
            }
            srcNdx++;
            dstNdx++;
        }
        return editItems;
    }

    public static ExtractDeleteAndUpdateResult extractDeleteAndUpdateItems(ArrayList<EditItem> editItems, ArrayList<TvRecommendation> targetList) {
        ExtractDeleteAndUpdateResult extractDeleteAndUpdateResult = new ExtractDeleteAndUpdateResult();
        extractDeleteAndUpdateResult.mItems = new ArrayList();
        int cumulativeAdjustment = 0;
        int n = editItems.size();
        for (int i = 0; i < n; i++) {
            EditItem item = (EditItem) editItems.get(i);
            switch (m1989x4e27365e()[item.mOp.ordinal()]) {
                case android.support.v7.recyclerview.R.styleable.RecyclerView_android_descendantFocusability /*1*/:
                    item.mSrcIndex += cumulativeAdjustment;
                    extractDeleteAndUpdateResult.mItems.add(item);
                    break;
                case android.support.v7.recyclerview.R.styleable.RecyclerView_layoutManager /*2*/:
                    cumulativeAdjustment--;
                    extractDeleteAndUpdateResult.mRemainingEditItems++;
                    break;
                case android.support.v7.preference.R.styleable.Preference_android_layout /*3*/:
                case android.support.v7.preference.R.styleable.Preference_android_title /*4*/:
                    item.mSrcIndex += cumulativeAdjustment;
                    if (item.mSrcIndex >= 0) {
                        if (!NotificationUtils.isUpdate(item.mItem, (TvRecommendation) targetList.get(item.mSrcIndex))) {
                            item.mOp = EditItem.Op.DELETE;
                            cumulativeAdjustment--;
                        }
                        extractDeleteAndUpdateResult.mItems.add(item);
                        extractDeleteAndUpdateResult.mRemainingEditItems++;
                        break;
                    }
                    Log.d("CMD", "StringDifference::extractDeleteAndUpdateItems  fail su " + item.mSrcIndex);
                    break;
                default:
                    break;
            }
        }
        return extractDeleteAndUpdateResult;
    }
}
