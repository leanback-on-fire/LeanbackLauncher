package com.google.android.tvlauncher.trace;

public final class AppTrace
{
  private static final String APP_TRACE_IMPL_CLASS_NAME = "com.google.android.tvlauncher.trace.TraceAdapterImpl";
  private static TraceAdapter sInstance;
  
  public static TraceTag beginAsyncSection(String paramString)
  {
    return null;
  }
  
  public static void beginSection(String paramString) {}
  
  public static void endAsyncSection(TraceTag paramTraceTag) {}
  
  public static void endSection() {}
  
  protected static abstract interface TraceAdapter
  {
    public abstract AppTrace.TraceTag beginAsyncSection(String paramString);
    
    public abstract void beginSection(String paramString);
    
    public abstract void endAsyncSection(AppTrace.TraceTag paramTraceTag);
    
    public abstract void endSection();
  }
  
  public static abstract interface TraceTag {}
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/trace/AppTrace.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */