package android.support.v17.leanback.widget;

import android.content.Context;
import android.os.Bundle;
import java.util.Calendar;

public class GuidedDatePickerAction
  extends GuidedAction
{
  long mDate;
  String mDatePickerFormat;
  long mMaxDate = Long.MAX_VALUE;
  long mMinDate = Long.MIN_VALUE;
  
  public long getDate()
  {
    return this.mDate;
  }
  
  public String getDatePickerFormat()
  {
    return this.mDatePickerFormat;
  }
  
  public long getMaxDate()
  {
    return this.mMaxDate;
  }
  
  public long getMinDate()
  {
    return this.mMinDate;
  }
  
  public void onRestoreInstanceState(Bundle paramBundle, String paramString)
  {
    setDate(paramBundle.getLong(paramString, getDate()));
  }
  
  public void onSaveInstanceState(Bundle paramBundle, String paramString)
  {
    paramBundle.putLong(paramString, getDate());
  }
  
  public void setDate(long paramLong)
  {
    this.mDate = paramLong;
  }
  
  public static final class Builder
    extends GuidedDatePickerAction.BuilderBase<Builder>
  {
    public Builder(Context paramContext)
    {
      super();
    }
    
    public GuidedDatePickerAction build()
    {
      GuidedDatePickerAction localGuidedDatePickerAction = new GuidedDatePickerAction();
      applyDatePickerValues(localGuidedDatePickerAction);
      return localGuidedDatePickerAction;
    }
  }
  
  public static abstract class BuilderBase<B extends BuilderBase>
    extends GuidedAction.BuilderBase<B>
  {
    private long mDate = Calendar.getInstance().getTimeInMillis();
    private String mDatePickerFormat;
    private long mMaxDate = Long.MAX_VALUE;
    private long mMinDate = Long.MIN_VALUE;
    
    public BuilderBase(Context paramContext)
    {
      super();
      hasEditableActivatorView(true);
    }
    
    protected final void applyDatePickerValues(GuidedDatePickerAction paramGuidedDatePickerAction)
    {
      super.applyValues(paramGuidedDatePickerAction);
      paramGuidedDatePickerAction.mDatePickerFormat = this.mDatePickerFormat;
      paramGuidedDatePickerAction.mDate = this.mDate;
      if (this.mMinDate > this.mMaxDate) {
        throw new IllegalArgumentException("MinDate cannot be larger than MaxDate");
      }
      paramGuidedDatePickerAction.mMinDate = this.mMinDate;
      paramGuidedDatePickerAction.mMaxDate = this.mMaxDate;
    }
    
    public B date(long paramLong)
    {
      this.mDate = paramLong;
      return this;
    }
    
    public B datePickerFormat(String paramString)
    {
      this.mDatePickerFormat = paramString;
      return this;
    }
    
    public B maxDate(long paramLong)
    {
      this.mMaxDate = paramLong;
      return this;
    }
    
    public B minDate(long paramLong)
    {
      this.mMinDate = paramLong;
      return this;
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/GuidedDatePickerAction.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */