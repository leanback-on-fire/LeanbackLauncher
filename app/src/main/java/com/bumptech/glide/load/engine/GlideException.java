package com.bumptech.glide.load.engine;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Key;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public final class GlideException
  extends Exception
{
  private static final StackTraceElement[] EMPTY_ELEMENTS = new StackTraceElement[0];
  private final List<Exception> causes;
  private Class<?> dataClass;
  private DataSource dataSource;
  private Key key;
  
  public GlideException(String paramString)
  {
    this(paramString, Collections.emptyList());
  }
  
  public GlideException(String paramString, Exception paramException)
  {
    this(paramString, Collections.singletonList(paramException));
  }
  
  public GlideException(String paramString, List<Exception> paramList)
  {
    super(paramString);
    setStackTrace(EMPTY_ELEMENTS);
    this.causes = paramList;
  }
  
  private void addRootCauses(Exception paramException, List<Exception> paramList)
  {
    if ((paramException instanceof GlideException))
    {
      paramException = ((GlideException)paramException).getCauses().iterator();
      while (paramException.hasNext()) {
        addRootCauses((Exception)paramException.next(), paramList);
      }
    }
    paramList.add(paramException);
  }
  
  private static void appendCauses(List<Exception> paramList, Appendable paramAppendable)
  {
    try
    {
      appendCausesWrapped(paramList, paramAppendable);
      return;
    }
    catch (IOException paramList)
    {
      throw new RuntimeException(paramList);
    }
  }
  
  private static void appendCausesWrapped(List<Exception> paramList, Appendable paramAppendable)
    throws IOException
  {
    int j = paramList.size();
    int i = 0;
    if (i < j)
    {
      paramAppendable.append("Cause (").append(String.valueOf(i + 1)).append(" of ").append(String.valueOf(j)).append("): ");
      Exception localException = (Exception)paramList.get(i);
      if ((localException instanceof GlideException)) {
        ((GlideException)localException).printStackTrace(paramAppendable);
      }
      for (;;)
      {
        i += 1;
        break;
        appendExceptionMessage(localException, paramAppendable);
      }
    }
  }
  
  private static void appendExceptionMessage(Exception paramException, Appendable paramAppendable)
  {
    try
    {
      paramAppendable.append(paramException.getClass().toString()).append(": ").append(paramException.getMessage()).append('\n');
      return;
    }
    catch (IOException paramAppendable)
    {
      throw new RuntimeException(paramException);
    }
  }
  
  private void printStackTrace(Appendable paramAppendable)
  {
    appendExceptionMessage(this, paramAppendable);
    appendCauses(getCauses(), new IndentedAppendable(paramAppendable));
  }
  
  public Throwable fillInStackTrace()
  {
    return this;
  }
  
  public List<Exception> getCauses()
  {
    return this.causes;
  }
  
  public String getMessage()
  {
    String str4 = String.valueOf(super.getMessage());
    String str1;
    String str2;
    if (this.dataClass != null)
    {
      str1 = String.valueOf(this.dataClass);
      str1 = String.valueOf(str1).length() + 2 + ", " + str1;
      if (this.dataSource == null) {
        break label209;
      }
      str2 = String.valueOf(this.dataSource);
      str2 = String.valueOf(str2).length() + 2 + ", " + str2;
      label97:
      if (this.key == null) {
        break label215;
      }
      str3 = String.valueOf(this.key);
    }
    label209:
    label215:
    for (String str3 = String.valueOf(str3).length() + 2 + ", " + str3;; str3 = "")
    {
      return String.valueOf(str4).length() + 0 + String.valueOf(str1).length() + String.valueOf(str2).length() + String.valueOf(str3).length() + str4 + str1 + str2 + str3;
      str1 = "";
      break;
      str2 = "";
      break label97;
    }
  }
  
  public List<Exception> getRootCauses()
  {
    ArrayList localArrayList = new ArrayList();
    addRootCauses(this, localArrayList);
    return localArrayList;
  }
  
  public void logRootCauses(String paramString)
  {
    Object localObject = String.valueOf(getClass());
    String str = String.valueOf(getMessage());
    Log.e(paramString, String.valueOf(localObject).length() + 2 + String.valueOf(str).length() + (String)localObject + ": " + str);
    localObject = getRootCauses();
    int i = 0;
    int j = ((List)localObject).size();
    while (i < j)
    {
      Log.i(paramString, 39 + "Root cause (" + (i + 1) + " of " + j + ")", (Throwable)((List)localObject).get(i));
      i += 1;
    }
  }
  
  public void printStackTrace()
  {
    printStackTrace(System.err);
  }
  
  public void printStackTrace(PrintStream paramPrintStream)
  {
    printStackTrace(paramPrintStream);
  }
  
  public void printStackTrace(PrintWriter paramPrintWriter)
  {
    printStackTrace(paramPrintWriter);
  }
  
  void setLoggingDetails(Key paramKey, DataSource paramDataSource)
  {
    setLoggingDetails(paramKey, paramDataSource, null);
  }
  
  void setLoggingDetails(Key paramKey, DataSource paramDataSource, Class<?> paramClass)
  {
    this.key = paramKey;
    this.dataSource = paramDataSource;
    this.dataClass = paramClass;
  }
  
  private static final class IndentedAppendable
    implements Appendable
  {
    private static final String EMPTY_SEQUENCE = "";
    private static final String INDENT = "  ";
    private final Appendable appendable;
    private boolean printedNewLine = true;
    
    IndentedAppendable(Appendable paramAppendable)
    {
      this.appendable = paramAppendable;
    }
    
    @NonNull
    private CharSequence safeSequence(@Nullable CharSequence paramCharSequence)
    {
      Object localObject = paramCharSequence;
      if (paramCharSequence == null) {
        localObject = "";
      }
      return (CharSequence)localObject;
    }
    
    public Appendable append(char paramChar)
      throws IOException
    {
      boolean bool = false;
      if (this.printedNewLine)
      {
        this.printedNewLine = false;
        this.appendable.append("  ");
      }
      if (paramChar == '\n') {
        bool = true;
      }
      this.printedNewLine = bool;
      this.appendable.append(paramChar);
      return this;
    }
    
    public Appendable append(@Nullable CharSequence paramCharSequence)
      throws IOException
    {
      paramCharSequence = safeSequence(paramCharSequence);
      return append(paramCharSequence, 0, paramCharSequence.length());
    }
    
    public Appendable append(@Nullable CharSequence paramCharSequence, int paramInt1, int paramInt2)
      throws IOException
    {
      boolean bool2 = false;
      paramCharSequence = safeSequence(paramCharSequence);
      if (this.printedNewLine)
      {
        this.printedNewLine = false;
        this.appendable.append("  ");
      }
      boolean bool1 = bool2;
      if (paramCharSequence.length() > 0)
      {
        bool1 = bool2;
        if (paramCharSequence.charAt(paramInt2 - 1) == '\n') {
          bool1 = true;
        }
      }
      this.printedNewLine = bool1;
      this.appendable.append(paramCharSequence, paramInt1, paramInt2);
      return this;
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/engine/GlideException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */