package android.support.v17.leanback.widget.picker;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build.VERSION;
import android.support.annotation.IntRange;
import android.support.v17.leanback.R.id;
import android.support.v17.leanback.R.styleable;
import android.support.v17.leanback.widget.VerticalGridView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class TimePicker
  extends Picker
{
  private static final int AM_INDEX = 0;
  private static final int HOURS_IN_HALF_DAY = 12;
  private static final int PM_INDEX = 1;
  static final String TAG = "TimePicker";
  PickerColumn mAmPmColumn;
  private View mAmPmSeparatorView;
  int mColAmPmIndex;
  int mColHourIndex;
  int mColMinuteIndex;
  private final PickerUtility.TimeConstant mConstant;
  private int mCurrentAmPmIndex;
  private int mCurrentHour;
  private int mCurrentMinute;
  PickerColumn mHourColumn;
  private boolean mIs24hFormat;
  PickerColumn mMinuteColumn;
  private ViewGroup mPickerView;
  
  public TimePicker(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public TimePicker(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    this.mConstant = PickerUtility.getTimeConstantInstance(Locale.getDefault(), paramContext.getResources());
    setSeparator(this.mConstant.timeSeparator);
    this.mPickerView = ((ViewGroup)findViewById(R.id.picker));
    paramAttributeSet = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.lbTimePicker);
    this.mIs24hFormat = paramAttributeSet.getBoolean(R.styleable.lbTimePicker_is24HourFormat, android.text.format.DateFormat.is24HourFormat(paramContext));
    boolean bool = paramAttributeSet.getBoolean(R.styleable.lbTimePicker_useCurrentTime, true);
    updateColumns(getTimePickerFormat());
    updateHourColumn(false);
    updateMin(this.mMinuteColumn, 0);
    updateMax(this.mMinuteColumn, 59);
    updateMin(this.mAmPmColumn, 0);
    updateMax(this.mAmPmColumn, 1);
    updateAmPmColumn();
    if (bool)
    {
      paramContext = PickerUtility.getCalendarForLocale(null, this.mConstant.locale);
      setHour(paramContext.get(11));
      setMinute(paramContext.get(12));
    }
  }
  
  private String getTimePickerFormat()
  {
    int j;
    label37:
    int i;
    if (Build.VERSION.SDK_INT >= 18)
    {
      str = android.text.format.DateFormat.getBestDateTimePattern(this.mConstant.locale, "hma");
      if (TextUtils.getLayoutDirectionFromLocale(this.mConstant.locale) != 1) {
        break label115;
      }
      j = 1;
      if (str.indexOf('a') < 0) {
        break label125;
      }
      if (str.indexOf("a") <= str.indexOf("m")) {
        break label120;
      }
      i = 1;
      label63:
      if (j == 0) {
        break label130;
      }
    }
    label115:
    label120:
    label125:
    label130:
    for (String str = "mh";; str = "hm")
    {
      if (i == 0) {
        break label136;
      }
      return str + "a";
      str = ((SimpleDateFormat)java.text.DateFormat.getTimeInstance(0, this.mConstant.locale)).toPattern();
      break;
      j = 0;
      break label37;
      i = 0;
      break label63;
      i = 1;
      break label63;
    }
    label136:
    return "a" + str;
  }
  
  private void updateAmPmColumn()
  {
    if (this.mIs24hFormat)
    {
      ((VerticalGridView)this.mColumnViews.get(this.mColAmPmIndex)).setVisibility(8);
      this.mAmPmSeparatorView.setVisibility(8);
      return;
    }
    ((VerticalGridView)this.mColumnViews.get(this.mColAmPmIndex)).setVisibility(0);
    this.mAmPmSeparatorView.setVisibility(0);
    setColumnValue(this.mColAmPmIndex, this.mCurrentAmPmIndex, false);
  }
  
  private void updateColumns(String paramString)
  {
    int j = 1;
    Object localObject = paramString;
    if (TextUtils.isEmpty(paramString)) {
      localObject = "hma";
    }
    paramString = ((String)localObject).toUpperCase();
    this.mAmPmColumn = null;
    this.mMinuteColumn = null;
    this.mHourColumn = null;
    this.mColAmPmIndex = -1;
    this.mColMinuteIndex = -1;
    this.mColHourIndex = -1;
    localObject = new ArrayList(3);
    int i = 0;
    if (i < paramString.length())
    {
      PickerColumn localPickerColumn;
      switch (paramString.charAt(i))
      {
      default: 
        throw new IllegalArgumentException("Invalid time picker format.");
      case 'H': 
        localPickerColumn = new PickerColumn();
        this.mHourColumn = localPickerColumn;
        ((ArrayList)localObject).add(localPickerColumn);
        this.mHourColumn.setStaticLabels(this.mConstant.hours24);
        this.mColHourIndex = i;
      }
      for (;;)
      {
        i += 1;
        break;
        localPickerColumn = new PickerColumn();
        this.mMinuteColumn = localPickerColumn;
        ((ArrayList)localObject).add(localPickerColumn);
        this.mMinuteColumn.setStaticLabels(this.mConstant.minutes);
        this.mColMinuteIndex = i;
        continue;
        localPickerColumn = new PickerColumn();
        this.mAmPmColumn = localPickerColumn;
        ((ArrayList)localObject).add(localPickerColumn);
        this.mAmPmColumn.setStaticLabels(this.mConstant.ampm);
        this.mColAmPmIndex = i;
        updateMin(this.mAmPmColumn, 0);
        updateMax(this.mAmPmColumn, 1);
      }
    }
    setColumns((List)localObject);
    paramString = this.mPickerView;
    if (this.mColAmPmIndex == 0) {}
    for (i = j;; i = this.mColAmPmIndex * 2 - 1)
    {
      this.mAmPmSeparatorView = paramString.getChildAt(i);
      return;
    }
  }
  
  private void updateHourColumn(boolean paramBoolean)
  {
    PickerColumn localPickerColumn = this.mHourColumn;
    if (this.mIs24hFormat)
    {
      i = 0;
      updateMin(localPickerColumn, i);
      localPickerColumn = this.mHourColumn;
      if (!this.mIs24hFormat) {
        break label63;
      }
    }
    label63:
    for (int i = 23;; i = 12)
    {
      updateMax(localPickerColumn, i);
      if (paramBoolean) {
        setColumnAt(this.mColHourIndex, this.mHourColumn);
      }
      return;
      i = 1;
      break;
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
  
  public int getHour()
  {
    if (this.mIs24hFormat) {
      return this.mCurrentHour;
    }
    if (this.mCurrentAmPmIndex == 0) {
      return this.mCurrentHour % 12;
    }
    return this.mCurrentHour % 12 + 12;
  }
  
  public int getMinute()
  {
    return this.mCurrentMinute;
  }
  
  public boolean is24Hour()
  {
    return this.mIs24hFormat;
  }
  
  public boolean isPm()
  {
    return this.mCurrentAmPmIndex == 1;
  }
  
  public void onColumnValueChanged(int paramInt1, int paramInt2)
  {
    if (paramInt1 == this.mColHourIndex)
    {
      this.mCurrentHour = paramInt2;
      return;
    }
    if (paramInt1 == this.mColMinuteIndex)
    {
      this.mCurrentMinute = paramInt2;
      return;
    }
    if (paramInt1 == this.mColAmPmIndex)
    {
      this.mCurrentAmPmIndex = paramInt2;
      return;
    }
    throw new IllegalArgumentException("Invalid column index.");
  }
  
  public void setHour(@IntRange(from=0L, to=23L) int paramInt)
  {
    if ((paramInt < 0) || (paramInt > 23)) {
      throw new IllegalArgumentException("hour: " + paramInt + " is not in [0-23] range in");
    }
    this.mCurrentHour = paramInt;
    if (!this.mIs24hFormat)
    {
      if (this.mCurrentHour < 12) {
        break label108;
      }
      this.mCurrentAmPmIndex = 1;
      if (this.mCurrentHour > 12) {
        this.mCurrentHour -= 12;
      }
    }
    for (;;)
    {
      updateAmPmColumn();
      setColumnValue(this.mColHourIndex, this.mCurrentHour, false);
      return;
      label108:
      this.mCurrentAmPmIndex = 0;
      if (this.mCurrentHour == 0) {
        this.mCurrentHour = 12;
      }
    }
  }
  
  public void setIs24Hour(boolean paramBoolean)
  {
    if (this.mIs24hFormat == paramBoolean) {
      return;
    }
    int i = getHour();
    this.mIs24hFormat = paramBoolean;
    updateHourColumn(true);
    setHour(i);
    updateAmPmColumn();
  }
  
  public void setMinute(@IntRange(from=0L, to=59L) int paramInt)
  {
    if (this.mCurrentMinute == paramInt) {
      return;
    }
    if ((paramInt < 0) || (paramInt > 59)) {
      throw new IllegalArgumentException("minute: " + paramInt + " is not in [0-59] range.");
    }
    this.mCurrentMinute = paramInt;
    setColumnValue(this.mColMinuteIndex, this.mCurrentMinute, false);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/picker/TimePicker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */