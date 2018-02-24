package android.support.v17.leanback.widget.picker;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.RestrictTo;
import android.support.v17.leanback.R.styleable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

@RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
public class DatePicker
  extends Picker
{
  private static int[] DATE_FIELDS = { 5, 2, 1 };
  static final String DATE_FORMAT = "MM/dd/yyyy";
  static final String LOG_TAG = "DatePicker";
  int mColDayIndex;
  int mColMonthIndex;
  int mColYearIndex;
  PickerUtility.DateConstant mConstant;
  Calendar mCurrentDate;
  final java.text.DateFormat mDateFormat = new SimpleDateFormat("MM/dd/yyyy");
  private String mDatePickerFormat;
  PickerColumn mDayColumn;
  Calendar mMaxDate;
  Calendar mMinDate;
  PickerColumn mMonthColumn;
  Calendar mTempDate;
  PickerColumn mYearColumn;
  
  public DatePicker(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public DatePicker(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    updateCurrentLocale();
    setSeparator(this.mConstant.dateSeparator);
    paramAttributeSet = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.lbDatePicker);
    String str1 = paramAttributeSet.getString(R.styleable.lbDatePicker_android_minDate);
    String str2 = paramAttributeSet.getString(R.styleable.lbDatePicker_android_maxDate);
    this.mTempDate.clear();
    if (!TextUtils.isEmpty(str1))
    {
      if (!parseDate(str1, this.mTempDate)) {
        this.mTempDate.set(1900, 0, 1);
      }
      this.mMinDate.setTimeInMillis(this.mTempDate.getTimeInMillis());
      this.mTempDate.clear();
      if (TextUtils.isEmpty(str2)) {
        break label223;
      }
      if (!parseDate(str2, this.mTempDate)) {
        this.mTempDate.set(2100, 0, 1);
      }
    }
    for (;;)
    {
      this.mMaxDate.setTimeInMillis(this.mTempDate.getTimeInMillis());
      str1 = paramAttributeSet.getString(R.styleable.lbDatePicker_datePickerFormat);
      paramAttributeSet = str1;
      if (TextUtils.isEmpty(str1)) {
        paramAttributeSet = new String(android.text.format.DateFormat.getDateFormatOrder(paramContext));
      }
      setDatePickerFormat(paramAttributeSet);
      return;
      this.mTempDate.set(1900, 0, 1);
      break;
      label223:
      this.mTempDate.set(2100, 0, 1);
    }
  }
  
  private boolean isNewDate(int paramInt1, int paramInt2, int paramInt3)
  {
    return (this.mCurrentDate.get(1) != paramInt1) || (this.mCurrentDate.get(2) != paramInt3) || (this.mCurrentDate.get(5) != paramInt2);
  }
  
  private boolean parseDate(String paramString, Calendar paramCalendar)
  {
    try
    {
      paramCalendar.setTime(this.mDateFormat.parse(paramString));
      return true;
    }
    catch (ParseException paramCalendar)
    {
      Log.w("DatePicker", "Date: " + paramString + " not in format: " + "MM/dd/yyyy");
    }
    return false;
  }
  
  private void setDate(int paramInt1, int paramInt2, int paramInt3)
  {
    this.mCurrentDate.set(paramInt1, paramInt2, paramInt3);
    if (this.mCurrentDate.before(this.mMinDate)) {
      this.mCurrentDate.setTimeInMillis(this.mMinDate.getTimeInMillis());
    }
    while (!this.mCurrentDate.after(this.mMaxDate)) {
      return;
    }
    this.mCurrentDate.setTimeInMillis(this.mMaxDate.getTimeInMillis());
  }
  
  private void updateCurrentLocale()
  {
    this.mConstant = PickerUtility.getDateConstantInstance(Locale.getDefault(), getContext().getResources());
    this.mTempDate = PickerUtility.getCalendarForLocale(this.mTempDate, this.mConstant.locale);
    this.mMinDate = PickerUtility.getCalendarForLocale(this.mMinDate, this.mConstant.locale);
    this.mMaxDate = PickerUtility.getCalendarForLocale(this.mMaxDate, this.mConstant.locale);
    this.mCurrentDate = PickerUtility.getCalendarForLocale(this.mCurrentDate, this.mConstant.locale);
    if (this.mMonthColumn != null)
    {
      this.mMonthColumn.setStaticLabels(this.mConstant.months);
      setColumnAt(this.mColMonthIndex, this.mMonthColumn);
    }
  }
  
  private static boolean updateMax(PickerColumn paramPickerColumn, int paramInt)
  {
    if (paramInt != paramPickerColumn.getMaxValue())
    {
      paramPickerColumn.setMaxValue(paramInt);
      return true;
    }
    return false;
  }
  
  private static boolean updateMin(PickerColumn paramPickerColumn, int paramInt)
  {
    if (paramInt != paramPickerColumn.getMinValue())
    {
      paramPickerColumn.setMinValue(paramInt);
      return true;
    }
    return false;
  }
  
  private void updateSpinners(final boolean paramBoolean)
  {
    post(new Runnable()
    {
      public void run()
      {
        DatePicker.this.updateSpinnersImpl(paramBoolean);
      }
    });
  }
  
  public long getDate()
  {
    return this.mCurrentDate.getTimeInMillis();
  }
  
  public String getDatePickerFormat()
  {
    return this.mDatePickerFormat;
  }
  
  public long getMaxDate()
  {
    return this.mMaxDate.getTimeInMillis();
  }
  
  public long getMinDate()
  {
    return this.mMinDate.getTimeInMillis();
  }
  
  public final void onColumnValueChanged(int paramInt1, int paramInt2)
  {
    this.mTempDate.setTimeInMillis(this.mCurrentDate.getTimeInMillis());
    int i = getColumnAt(paramInt1).getCurrentValue();
    if (paramInt1 == this.mColDayIndex) {
      this.mTempDate.add(5, paramInt2 - i);
    }
    for (;;)
    {
      setDate(this.mTempDate.get(1), this.mTempDate.get(2), this.mTempDate.get(5));
      updateSpinners(false);
      return;
      if (paramInt1 == this.mColMonthIndex)
      {
        this.mTempDate.add(2, paramInt2 - i);
      }
      else
      {
        if (paramInt1 != this.mColYearIndex) {
          break;
        }
        this.mTempDate.add(1, paramInt2 - i);
      }
    }
    throw new IllegalArgumentException();
  }
  
  public void setDatePickerFormat(String paramString)
  {
    Object localObject = paramString;
    if (TextUtils.isEmpty(paramString)) {
      localObject = new String(android.text.format.DateFormat.getDateFormatOrder(getContext()));
    }
    paramString = ((String)localObject).toUpperCase();
    if (TextUtils.equals(this.mDatePickerFormat, paramString)) {
      return;
    }
    this.mDatePickerFormat = paramString;
    this.mDayColumn = null;
    this.mMonthColumn = null;
    this.mYearColumn = null;
    this.mColMonthIndex = -1;
    this.mColDayIndex = -1;
    this.mColYearIndex = -1;
    localObject = new ArrayList(3);
    int i = 0;
    if (i < paramString.length())
    {
      PickerColumn localPickerColumn;
      switch (paramString.charAt(i))
      {
      default: 
        throw new IllegalArgumentException("datePicker format error");
      case 'Y': 
        if (this.mYearColumn != null) {
          throw new IllegalArgumentException("datePicker format error");
        }
        localPickerColumn = new PickerColumn();
        this.mYearColumn = localPickerColumn;
        ((ArrayList)localObject).add(localPickerColumn);
        this.mColYearIndex = i;
        this.mYearColumn.setLabelFormat("%d");
      }
      for (;;)
      {
        i += 1;
        break;
        if (this.mMonthColumn != null) {
          throw new IllegalArgumentException("datePicker format error");
        }
        localPickerColumn = new PickerColumn();
        this.mMonthColumn = localPickerColumn;
        ((ArrayList)localObject).add(localPickerColumn);
        this.mMonthColumn.setStaticLabels(this.mConstant.months);
        this.mColMonthIndex = i;
        continue;
        if (this.mDayColumn != null) {
          throw new IllegalArgumentException("datePicker format error");
        }
        localPickerColumn = new PickerColumn();
        this.mDayColumn = localPickerColumn;
        ((ArrayList)localObject).add(localPickerColumn);
        this.mDayColumn.setLabelFormat("%02d");
        this.mColDayIndex = i;
      }
    }
    setColumns((List)localObject);
    updateSpinners(false);
  }
  
  public void setMaxDate(long paramLong)
  {
    this.mTempDate.setTimeInMillis(paramLong);
    if ((this.mTempDate.get(1) == this.mMaxDate.get(1)) && (this.mTempDate.get(6) != this.mMaxDate.get(6))) {
      return;
    }
    this.mMaxDate.setTimeInMillis(paramLong);
    if (this.mCurrentDate.after(this.mMaxDate)) {
      this.mCurrentDate.setTimeInMillis(this.mMaxDate.getTimeInMillis());
    }
    updateSpinners(false);
  }
  
  public void setMinDate(long paramLong)
  {
    this.mTempDate.setTimeInMillis(paramLong);
    if ((this.mTempDate.get(1) == this.mMinDate.get(1)) && (this.mTempDate.get(6) != this.mMinDate.get(6))) {
      return;
    }
    this.mMinDate.setTimeInMillis(paramLong);
    if (this.mCurrentDate.before(this.mMinDate)) {
      this.mCurrentDate.setTimeInMillis(this.mMinDate.getTimeInMillis());
    }
    updateSpinners(false);
  }
  
  public void updateDate(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean)
  {
    if (!isNewDate(paramInt1, paramInt2, paramInt3)) {
      return;
    }
    setDate(paramInt1, paramInt2, paramInt3);
    updateSpinners(paramBoolean);
  }
  
  void updateSpinnersImpl(boolean paramBoolean)
  {
    int[] arrayOfInt = new int[3];
    arrayOfInt[0] = this.mColDayIndex;
    arrayOfInt[1] = this.mColMonthIndex;
    arrayOfInt[2] = this.mColYearIndex;
    int k = 1;
    int j = 1;
    int i = DATE_FIELDS.length - 1;
    while (i >= 0) {
      if (arrayOfInt[i] < 0)
      {
        i -= 1;
      }
      else
      {
        int n = DATE_FIELDS[i];
        PickerColumn localPickerColumn = getColumnAt(arrayOfInt[i]);
        boolean bool;
        label99:
        label122:
        int m;
        if (k != 0)
        {
          bool = false | updateMin(localPickerColumn, this.mMinDate.get(n));
          if (j == 0) {
            break label243;
          }
          bool |= updateMax(localPickerColumn, this.mMaxDate.get(n));
          if (this.mCurrentDate.get(n) != this.mMinDate.get(n)) {
            break label265;
          }
          m = 1;
          label146:
          m = k & m;
          if (this.mCurrentDate.get(n) != this.mMaxDate.get(n)) {
            break label271;
          }
        }
        label243:
        label265:
        label271:
        for (k = 1;; k = 0)
        {
          j &= k;
          if (bool) {
            setColumnAt(arrayOfInt[i], localPickerColumn);
          }
          setColumnValue(arrayOfInt[i], this.mCurrentDate.get(n), paramBoolean);
          k = m;
          break;
          bool = false | updateMin(localPickerColumn, this.mCurrentDate.getActualMinimum(n));
          break label99;
          bool |= updateMax(localPickerColumn, this.mCurrentDate.getActualMaximum(n));
          break label122;
          m = 0;
          break label146;
        }
      }
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/picker/DatePicker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */