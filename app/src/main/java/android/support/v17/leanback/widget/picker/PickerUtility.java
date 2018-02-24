package android.support.v17.leanback.widget.picker;

import android.content.res.Resources;
import android.support.v17.leanback.R.string;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Locale;

class PickerUtility
{
  public static String[] createStringIntArrays(int paramInt1, int paramInt2, String paramString)
  {
    String[] arrayOfString = new String[paramInt2 - paramInt1 + 1];
    int i = paramInt1;
    if (i <= paramInt2)
    {
      if (paramString != null) {
        arrayOfString[(i - paramInt1)] = String.format(paramString, new Object[] { Integer.valueOf(i) });
      }
      for (;;)
      {
        i += 1;
        break;
        arrayOfString[(i - paramInt1)] = String.valueOf(i);
      }
    }
    return arrayOfString;
  }
  
  public static Calendar getCalendarForLocale(Calendar paramCalendar, Locale paramLocale)
  {
    if (paramCalendar == null) {
      return Calendar.getInstance(paramLocale);
    }
    long l = paramCalendar.getTimeInMillis();
    paramCalendar = Calendar.getInstance(paramLocale);
    paramCalendar.setTimeInMillis(l);
    return paramCalendar;
  }
  
  public static DateConstant getDateConstantInstance(Locale paramLocale, Resources paramResources)
  {
    return new DateConstant(paramLocale, paramResources, null);
  }
  
  public static TimeConstant getTimeConstantInstance(Locale paramLocale, Resources paramResources)
  {
    return new TimeConstant(paramLocale, paramResources, null);
  }
  
  public static class DateConstant
  {
    public final String dateSeparator;
    public final String[] days;
    public final Locale locale;
    public final String[] months;
    
    private DateConstant(Locale paramLocale, Resources paramResources)
    {
      this.locale = paramLocale;
      this.months = DateFormatSymbols.getInstance(paramLocale).getShortMonths();
      paramLocale = Calendar.getInstance(paramLocale);
      this.days = PickerUtility.createStringIntArrays(paramLocale.getMinimum(5), paramLocale.getMaximum(5), "%02d");
      this.dateSeparator = paramResources.getString(R.string.lb_date_separator);
    }
  }
  
  public static class TimeConstant
  {
    public final String[] ampm;
    public final String[] hours12;
    public final String[] hours24;
    public final Locale locale;
    public final String[] minutes;
    public final String timeSeparator;
    
    private TimeConstant(Locale paramLocale, Resources paramResources)
    {
      this.locale = paramLocale;
      paramLocale = DateFormatSymbols.getInstance(paramLocale);
      this.hours12 = PickerUtility.createStringIntArrays(1, 12, "%02d");
      this.hours24 = PickerUtility.createStringIntArrays(0, 23, "%02d");
      this.minutes = PickerUtility.createStringIntArrays(0, 59, "%02d");
      this.ampm = paramLocale.getAmPmStrings();
      this.timeSeparator = paramResources.getString(R.string.lb_time_separator);
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/picker/PickerUtility.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */