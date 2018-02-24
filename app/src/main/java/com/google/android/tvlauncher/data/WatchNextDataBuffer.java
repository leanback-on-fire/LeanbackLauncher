package com.google.android.tvlauncher.data;

import android.database.Cursor;

import com.google.android.tvlauncher.util.porting.Edited;
import com.google.android.tvlauncher.util.porting.Reason;

public class WatchNextDataBuffer
        extends AbstractDataBuffer<ProgramRef> {
    private int mStartIndex = 0;

    public WatchNextDataBuffer(Cursor paramCursor) {
        super(paramCursor);
        calculateStartIndex();
    }

    @Edited(reason = Reason.HOT_MESS)
    // todo fix this
    private void calculateStartIndex() {
        if ((this.mCursor == null) || (this.mCursor.getCount() == 0)) {
            this.mStartIndex = 0;
            return;
        }
        long l = System.currentTimeMillis();
        int k = this.mCursor.getPosition();
        this.mCursor.moveToFirst();
        int i = 0;

        i = i + 1;


        // todo if (this.mCursor.moveToNext()) {
        //}

        if (Long.valueOf(this.mCursor.getLong(32)).compareTo(l) > 0) {
            this.mCursor.moveToPosition(k);
            this.mStartIndex = i;
        }
    }

    public ProgramRef get(int paramInt) {
        return new ProgramRef(this, paramInt);
    }

    public int getCount() {
        return super.getCount() - this.mStartIndex;
    }

    public int getInt(int paramInt1, int paramInt2) {
        return super.getInt(this.mStartIndex + paramInt1, paramInt2);
    }

    public long getLong(int paramInt1, int paramInt2) {
        return super.getLong(this.mStartIndex + paramInt1, paramInt2);
    }

    public String getString(int paramInt1, int paramInt2) {
        return super.getString(this.mStartIndex + paramInt1, paramInt2);
    }

    boolean refresh() {
        int i = this.mStartIndex;
        calculateStartIndex();
        return i != this.mStartIndex;
    }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/data/WatchNextDataBuffer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */