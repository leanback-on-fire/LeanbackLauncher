package android.support.v7.app;

class TwilightCalculator
{
  private static final float ALTIDUTE_CORRECTION_CIVIL_TWILIGHT = -0.10471976F;
  private static final float C1 = 0.0334196F;
  private static final float C2 = 3.49066E-4F;
  private static final float C3 = 5.236E-6F;
  public static final int DAY = 0;
  private static final float DEGREES_TO_RADIANS = 0.017453292F;
  private static final float J0 = 9.0E-4F;
  public static final int NIGHT = 1;
  private static final float OBLIQUITY = 0.4092797F;
  private static final long UTC_2000 = 946728000000L;
  private static TwilightCalculator sInstance;
  public int state;
  public long sunrise;
  public long sunset;
  
  static TwilightCalculator getInstance()
  {
    if (sInstance == null) {
      sInstance = new TwilightCalculator();
    }
    return sInstance;
  }
  
  public void calculateTwilight(long paramLong, double paramDouble1, double paramDouble2)
  {
    float f1 = (float)(paramLong - 946728000000L) / 8.64E7F;
    float f2 = 6.24006F + 0.01720197F * f1;
    double d = 1.796593063D + (f2 + 0.03341960161924362D * Math.sin(f2) + 3.4906598739326E-4D * Math.sin(2.0F * f2) + 5.236000106378924E-6D * Math.sin(3.0F * f2)) + 3.141592653589793D;
    paramDouble2 = -paramDouble2 / 360.0D;
    paramDouble2 = 9.0E-4F + (float)Math.round(f1 - 9.0E-4F - paramDouble2) + paramDouble2 + 0.0053D * Math.sin(f2) + -0.0069D * Math.sin(2.0D * d);
    d = Math.asin(Math.sin(d) * Math.sin(0.4092797040939331D));
    paramDouble1 *= 0.01745329238474369D;
    paramDouble1 = (Math.sin(-0.10471975803375244D) - Math.sin(paramDouble1) * Math.sin(d)) / (Math.cos(paramDouble1) * Math.cos(d));
    if (paramDouble1 >= 1.0D)
    {
      this.state = 1;
      this.sunset = -1L;
      this.sunrise = -1L;
      return;
    }
    if (paramDouble1 <= -1.0D)
    {
      this.state = 0;
      this.sunset = -1L;
      this.sunrise = -1L;
      return;
    }
    f1 = (float)(Math.acos(paramDouble1) / 6.283185307179586D);
    this.sunset = (Math.round((f1 + paramDouble2) * 8.64E7D) + 946728000000L);
    this.sunrise = (Math.round((paramDouble2 - f1) * 8.64E7D) + 946728000000L);
    if ((this.sunrise < paramLong) && (this.sunset > paramLong))
    {
      this.state = 0;
      return;
    }
    this.state = 1;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v7/app/TwilightCalculator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */