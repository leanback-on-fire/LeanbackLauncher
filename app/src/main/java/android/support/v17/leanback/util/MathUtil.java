package android.support.v17.leanback.util;

public final class MathUtil
{
  public static int safeLongToInt(long paramLong)
  {
    if ((int)paramLong != paramLong) {
      throw new ArithmeticException("Input overflows int.\n");
    }
    return (int)paramLong;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/util/MathUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */