package com.google.android.gms.phenotype;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.util.Base64;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.zzaa;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ExperimentTokens
  extends zza
{
  public static final Parcelable.Creator<ExperimentTokens> CREATOR = new zzd();
  public static final ExperimentTokens EMPTY;
  public static final byte[][] EMPTY_BYTES = new byte[0][];
  private static final Charset UTF_8 = Charset.forName("UTF-8");
  private static final zza zzceV;
  private static final zza zzceW;
  private static final zza zzceX;
  private static final zza zzceY;
  public final byte[][] additionalDirectExperimentTokens;
  public final byte[][] alwaysCrossExperimentTokens;
  public final byte[] directExperimentToken;
  public final byte[][] gaiaCrossExperimentTokens;
  public final byte[][] otherCrossExperimentTokens;
  public final byte[][] pseudonymousCrossExperimentTokens;
  public final String user;
  public final int[] weakExperimentIds;
  
  static
  {
    EMPTY = new ExperimentTokens("", null, EMPTY_BYTES, EMPTY_BYTES, EMPTY_BYTES, EMPTY_BYTES, null, null);
    zzceV = new zza()
    {
      public byte[][] zza(ExperimentTokens paramAnonymousExperimentTokens)
      {
        return paramAnonymousExperimentTokens.gaiaCrossExperimentTokens;
      }
    };
    zzceW = new zza()
    {
      public byte[][] zza(ExperimentTokens paramAnonymousExperimentTokens)
      {
        return paramAnonymousExperimentTokens.pseudonymousCrossExperimentTokens;
      }
    };
    zzceX = new zza()
    {
      public byte[][] zza(ExperimentTokens paramAnonymousExperimentTokens)
      {
        return paramAnonymousExperimentTokens.alwaysCrossExperimentTokens;
      }
    };
    zzceY = new zza()
    {
      public byte[][] zza(ExperimentTokens paramAnonymousExperimentTokens)
      {
        return paramAnonymousExperimentTokens.otherCrossExperimentTokens;
      }
    };
  }
  
  public ExperimentTokens(String paramString, byte[] paramArrayOfByte, byte[][] paramArrayOfByte1, byte[][] paramArrayOfByte2, byte[][] paramArrayOfByte3, byte[][] paramArrayOfByte4, int[] paramArrayOfInt)
  {
    this(paramString, paramArrayOfByte, paramArrayOfByte1, paramArrayOfByte2, paramArrayOfByte3, paramArrayOfByte4, paramArrayOfInt, null);
  }
  
  public ExperimentTokens(String paramString, byte[] paramArrayOfByte, byte[][] paramArrayOfByte1, byte[][] paramArrayOfByte2, byte[][] paramArrayOfByte3, byte[][] paramArrayOfByte4, int[] paramArrayOfInt, byte[][] paramArrayOfByte5)
  {
    this.user = paramString;
    this.directExperimentToken = paramArrayOfByte;
    this.gaiaCrossExperimentTokens = paramArrayOfByte1;
    this.pseudonymousCrossExperimentTokens = paramArrayOfByte2;
    this.alwaysCrossExperimentTokens = paramArrayOfByte3;
    this.otherCrossExperimentTokens = paramArrayOfByte4;
    this.weakExperimentIds = paramArrayOfInt;
    this.additionalDirectExperimentTokens = paramArrayOfByte5;
  }
  
  public static ExperimentTokens deserializeFromString(String paramString)
  {
    paramString = new DataInputStream(new ByteArrayInputStream(Base64.decode(paramString, 0)));
    try
    {
      int i = paramString.readInt();
      if (i != 1) {
        throw new RuntimeException(30 + "Unexpected version " + i);
      }
    }
    catch (IOException paramString)
    {
      throw new RuntimeException(paramString);
    }
    paramString = new ExperimentTokens(paramString.readUTF(), zzc(paramString), zzd(paramString), zzd(paramString), zzd(paramString), zzd(paramString), zze(paramString), zzd(paramString));
    return paramString;
  }
  
  public static ExperimentTokens merge(List<ExperimentTokens> paramList)
  {
    if ((paramList != null) && (paramList.size() == 1)) {
      return (ExperimentTokens)paramList.get(0);
    }
    String str;
    if ((sameUser(paramList)) && (paramList != null) && (!paramList.isEmpty()))
    {
      str = ((ExperimentTokens)paramList.get(0)).user;
      if (!sameUser(paramList)) {
        break label120;
      }
    }
    label120:
    for (byte[][] arrayOfByte = zza(paramList, zzceV);; arrayOfByte = EMPTY_BYTES)
    {
      return new ExperimentTokens(str, null, arrayOfByte, zza(paramList, zzceW), zza(paramList, zzceX), zza(paramList, zzceY), zzac(paramList), zzab(paramList));
      str = "";
      break;
    }
  }
  
  public static ExperimentTokens mergeNoCross(List<ExperimentTokens> paramList)
  {
    return new ExperimentTokens("", null, EMPTY_BYTES, EMPTY_BYTES, EMPTY_BYTES, EMPTY_BYTES, zzac(paramList), zzab(paramList));
  }
  
  public static boolean sameUser(List<ExperimentTokens> paramList)
  {
    if ((paramList == null) || (paramList.isEmpty())) {
      return true;
    }
    String str = ((ExperimentTokens)paramList.get(0)).user;
    paramList = paramList.iterator();
    while (paramList.hasNext()) {
      if (!zzaa.equal(str, ((ExperimentTokens)paramList.next()).user)) {
        return false;
      }
    }
    return true;
  }
  
  private byte[] zzQ(byte[] paramArrayOfByte)
  {
    if ((paramArrayOfByte == null) || (paramArrayOfByte.length == 0)) {
      return paramArrayOfByte;
    }
    return Arrays.copyOf(paramArrayOfByte, paramArrayOfByte.length);
  }
  
  private static void zza(StringBuilder paramStringBuilder, String paramString, byte[] paramArrayOfByte)
  {
    paramStringBuilder.append(paramString);
    paramStringBuilder.append("=");
    if (paramArrayOfByte == null)
    {
      paramStringBuilder.append("null");
      return;
    }
    paramStringBuilder.append("'");
    paramStringBuilder.append(new String(paramArrayOfByte, UTF_8));
    paramStringBuilder.append("'");
  }
  
  private static void zza(StringBuilder paramStringBuilder, String paramString, int[] paramArrayOfInt)
  {
    paramStringBuilder.append(paramString);
    paramStringBuilder.append("=");
    if (paramArrayOfInt == null)
    {
      paramStringBuilder.append("null");
      return;
    }
    paramStringBuilder.append("(");
    int k = paramArrayOfInt.length;
    int j = 1;
    int i = 0;
    while (i < k)
    {
      int m = paramArrayOfInt[i];
      if (j == 0) {
        paramStringBuilder.append(", ");
      }
      paramStringBuilder.append(m);
      i += 1;
      j = 0;
    }
    paramStringBuilder.append(")");
  }
  
  private static void zza(StringBuilder paramStringBuilder, String paramString, byte[][] paramArrayOfByte)
  {
    paramStringBuilder.append(paramString);
    paramStringBuilder.append("=");
    if (paramArrayOfByte == null)
    {
      paramStringBuilder.append("null");
      return;
    }
    paramStringBuilder.append("(");
    int k = paramArrayOfByte.length;
    int j = 1;
    int i = 0;
    while (i < k)
    {
      paramString = paramArrayOfByte[i];
      if (j == 0) {
        paramStringBuilder.append(", ");
      }
      paramStringBuilder.append("'");
      paramStringBuilder.append(new String(paramString, UTF_8));
      paramStringBuilder.append("'");
      i += 1;
      j = 0;
    }
    paramStringBuilder.append(")");
  }
  
  private void zza(byte[] paramArrayOfByte, DataOutputStream paramDataOutputStream)
    throws IOException
  {
    if (paramArrayOfByte != null)
    {
      paramDataOutputStream.writeInt(paramArrayOfByte.length);
      paramDataOutputStream.write(paramArrayOfByte, 0, paramArrayOfByte.length);
      return;
    }
    paramDataOutputStream.writeInt(0);
  }
  
  private void zza(int[] paramArrayOfInt, DataOutputStream paramDataOutputStream)
    throws IOException
  {
    int i = 0;
    if (paramArrayOfInt != null)
    {
      paramDataOutputStream.writeInt(paramArrayOfInt.length);
      while (i < paramArrayOfInt.length)
      {
        paramDataOutputStream.writeInt(paramArrayOfInt[i]);
        i += 1;
      }
    }
    paramDataOutputStream.writeInt(0);
  }
  
  private void zza(byte[][] paramArrayOfByte, DataOutputStream paramDataOutputStream)
    throws IOException
  {
    int i = 0;
    if (paramArrayOfByte != null)
    {
      paramDataOutputStream.writeInt(paramArrayOfByte.length);
      while (i < paramArrayOfByte.length)
      {
        zza(paramArrayOfByte[i], paramDataOutputStream);
        i += 1;
      }
    }
    paramDataOutputStream.writeInt(0);
  }
  
  private static byte[][] zza(List<ExperimentTokens> paramList, zza paramzza)
  {
    Object localObject1 = paramList.iterator();
    int i = 0;
    Object localObject2;
    if (((Iterator)localObject1).hasNext())
    {
      localObject2 = (ExperimentTokens)((Iterator)localObject1).next();
      if ((localObject2 == null) || (paramzza.zza((ExperimentTokens)localObject2) == null)) {
        break label175;
      }
      i += paramzza.zza((ExperimentTokens)localObject2).length;
    }
    label175:
    for (;;)
    {
      break;
      localObject1 = new byte[i][];
      paramList = paramList.iterator();
      int j;
      for (i = 0; paramList.hasNext(); i = j)
      {
        localObject2 = (ExperimentTokens)paramList.next();
        j = i;
        if (localObject2 != null)
        {
          j = i;
          if (paramzza.zza((ExperimentTokens)localObject2) != null)
          {
            localObject2 = paramzza.zza((ExperimentTokens)localObject2);
            int m = localObject2.length;
            int k = 0;
            for (;;)
            {
              j = i;
              if (k >= m) {
                break;
              }
              localObject1[i] = localObject2[k];
              k += 1;
              i += 1;
            }
          }
        }
      }
      return (byte[][])localObject1;
    }
  }
  
  private static byte[][] zzab(List<ExperimentTokens> paramList)
  {
    Object localObject1 = paramList.iterator();
    int i = 0;
    Object localObject2;
    int j;
    if (((Iterator)localObject1).hasNext())
    {
      localObject2 = (ExperimentTokens)((Iterator)localObject1).next();
      j = i;
      if (localObject2 != null)
      {
        j = i;
        if (((ExperimentTokens)localObject2).directExperimentToken != null) {
          j = i + 1;
        }
      }
      if ((localObject2 == null) || (((ExperimentTokens)localObject2).additionalDirectExperimentTokens == null)) {
        break label205;
      }
    }
    label205:
    for (i = ((ExperimentTokens)localObject2).additionalDirectExperimentTokens.length + j;; i = j)
    {
      break;
      localObject1 = new byte[i][];
      paramList = paramList.iterator();
      j = 0;
      while (paramList.hasNext())
      {
        localObject2 = (ExperimentTokens)paramList.next();
        i = j;
        if (localObject2 != null)
        {
          i = j;
          if (((ExperimentTokens)localObject2).directExperimentToken != null)
          {
            localObject1[j] = ((ExperimentTokens)localObject2).directExperimentToken;
            i = j + 1;
          }
        }
        if ((localObject2 != null) && (((ExperimentTokens)localObject2).additionalDirectExperimentTokens != null))
        {
          localObject2 = ((ExperimentTokens)localObject2).additionalDirectExperimentTokens;
          int m = localObject2.length;
          int k = 0;
          for (;;)
          {
            j = i;
            if (k >= m) {
              break;
            }
            localObject1[i] = localObject2[k];
            k += 1;
            i += 1;
          }
        }
        j = i;
      }
      return (byte[][])localObject1;
    }
  }
  
  private static int[] zzac(List<ExperimentTokens> paramList)
  {
    Object localObject1 = paramList.iterator();
    int i = 0;
    Object localObject2;
    if (((Iterator)localObject1).hasNext())
    {
      localObject2 = (ExperimentTokens)((Iterator)localObject1).next();
      if ((localObject2 == null) || (((ExperimentTokens)localObject2).weakExperimentIds == null)) {
        break label157;
      }
      i += ((ExperimentTokens)localObject2).weakExperimentIds.length;
    }
    label157:
    for (;;)
    {
      break;
      localObject1 = new int[i];
      paramList = paramList.iterator();
      int j;
      for (i = 0; paramList.hasNext(); i = j)
      {
        localObject2 = (ExperimentTokens)paramList.next();
        j = i;
        if (localObject2 != null)
        {
          j = i;
          if (((ExperimentTokens)localObject2).weakExperimentIds != null)
          {
            localObject2 = ((ExperimentTokens)localObject2).weakExperimentIds;
            int m = localObject2.length;
            int k = 0;
            for (;;)
            {
              j = i;
              if (k >= m) {
                break;
              }
              localObject1[i] = localObject2[k];
              k += 1;
              i += 1;
            }
          }
        }
      }
      return (int[])localObject1;
    }
  }
  
  private byte[][] zzb(byte[][] paramArrayOfByte)
  {
    if ((paramArrayOfByte == null) || (paramArrayOfByte.length == 0)) {
      return paramArrayOfByte;
    }
    byte[][] arrayOfByte = new byte[paramArrayOfByte.length][];
    int i = 0;
    if (i < paramArrayOfByte.length)
    {
      if ((paramArrayOfByte[i] == null) || (paramArrayOfByte[i].length == 0)) {
        arrayOfByte[i] = paramArrayOfByte[i];
      }
      for (;;)
      {
        i += 1;
        break;
        arrayOfByte[i] = Arrays.copyOf(paramArrayOfByte[i], paramArrayOfByte[i].length);
      }
    }
    return arrayOfByte;
  }
  
  private static List<String> zzc(byte[][] paramArrayOfByte)
  {
    if (paramArrayOfByte == null) {
      return Collections.EMPTY_LIST;
    }
    ArrayList localArrayList = new ArrayList(paramArrayOfByte.length);
    int j = paramArrayOfByte.length;
    int i = 0;
    while (i < j)
    {
      localArrayList.add(new String(paramArrayOfByte[i], UTF_8));
      i += 1;
    }
    Collections.sort(localArrayList);
    return localArrayList;
  }
  
  private static byte[] zzc(DataInputStream paramDataInputStream)
    throws IOException
  {
    int i = paramDataInputStream.readInt();
    if (i == 0) {
      return null;
    }
    byte[] arrayOfByte = new byte[i];
    paramDataInputStream.readFully(arrayOfByte);
    return arrayOfByte;
  }
  
  private static byte[][] zzd(DataInputStream paramDataInputStream)
    throws IOException
  {
    int j = paramDataInputStream.readInt();
    Object localObject;
    if (j == 0)
    {
      localObject = null;
      return (byte[][])localObject;
    }
    byte[][] arrayOfByte = new byte[j][];
    int i = 0;
    for (;;)
    {
      localObject = arrayOfByte;
      if (i >= j) {
        break;
      }
      arrayOfByte[i] = zzc(paramDataInputStream);
      i += 1;
    }
  }
  
  private static int[] zze(DataInputStream paramDataInputStream)
    throws IOException
  {
    int j = paramDataInputStream.readInt();
    Object localObject;
    if (j == 0)
    {
      localObject = null;
      return (int[])localObject;
    }
    int[] arrayOfInt = new int[j];
    int i = 0;
    for (;;)
    {
      localObject = arrayOfInt;
      if (i >= j) {
        break;
      }
      arrayOfInt[i] = paramDataInputStream.readInt();
      i += 1;
    }
  }
  
  private int[] zzg(int[] paramArrayOfInt)
  {
    if ((paramArrayOfInt == null) || (paramArrayOfInt.length == 0)) {
      return paramArrayOfInt;
    }
    return Arrays.copyOf(paramArrayOfInt, paramArrayOfInt.length);
  }
  
  private static List<Integer> zzh(int[] paramArrayOfInt)
  {
    if (paramArrayOfInt == null) {
      return Collections.EMPTY_LIST;
    }
    ArrayList localArrayList = new ArrayList(paramArrayOfInt.length);
    int j = paramArrayOfInt.length;
    int i = 0;
    while (i < j)
    {
      localArrayList.add(Integer.valueOf(paramArrayOfInt[i]));
      i += 1;
    }
    Collections.sort(localArrayList);
    return localArrayList;
  }
  
  public ExperimentTokens deepCopy()
  {
    return new ExperimentTokens(this.user, zzQ(this.directExperimentToken), zzb(this.gaiaCrossExperimentTokens), zzb(this.pseudonymousCrossExperimentTokens), zzb(this.alwaysCrossExperimentTokens), zzb(this.otherCrossExperimentTokens), zzg(this.weakExperimentIds), zzb(this.additionalDirectExperimentTokens));
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (paramObject != null)
    {
      bool1 = bool2;
      if ((paramObject instanceof ExperimentTokens))
      {
        paramObject = (ExperimentTokens)paramObject;
        bool1 = bool2;
        if (zzaa.equal(this.user, ((ExperimentTokens)paramObject).user))
        {
          bool1 = bool2;
          if (Arrays.equals(this.directExperimentToken, ((ExperimentTokens)paramObject).directExperimentToken))
          {
            bool1 = bool2;
            if (zzaa.equal(zzc(this.gaiaCrossExperimentTokens), zzc(((ExperimentTokens)paramObject).gaiaCrossExperimentTokens)))
            {
              bool1 = bool2;
              if (zzaa.equal(zzc(this.pseudonymousCrossExperimentTokens), zzc(((ExperimentTokens)paramObject).pseudonymousCrossExperimentTokens)))
              {
                bool1 = bool2;
                if (zzaa.equal(zzc(this.alwaysCrossExperimentTokens), zzc(((ExperimentTokens)paramObject).alwaysCrossExperimentTokens)))
                {
                  bool1 = bool2;
                  if (zzaa.equal(zzc(this.otherCrossExperimentTokens), zzc(((ExperimentTokens)paramObject).otherCrossExperimentTokens)))
                  {
                    bool1 = bool2;
                    if (zzaa.equal(zzh(this.weakExperimentIds), zzh(((ExperimentTokens)paramObject).weakExperimentIds)))
                    {
                      bool1 = bool2;
                      if (zzaa.equal(zzc(this.additionalDirectExperimentTokens), zzc(((ExperimentTokens)paramObject).additionalDirectExperimentTokens))) {
                        bool1 = true;
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
    return bool1;
  }
  
  /* Error */
  public String serializeToString()
  {
    // Byte code:
    //   0: new 324	java/io/ByteArrayOutputStream
    //   3: dup
    //   4: invokespecial 325	java/io/ByteArrayOutputStream:<init>	()V
    //   7: astore_2
    //   8: new 249	java/io/DataOutputStream
    //   11: dup
    //   12: aload_2
    //   13: invokespecial 328	java/io/DataOutputStream:<init>	(Ljava/io/OutputStream;)V
    //   16: astore_1
    //   17: aload_1
    //   18: iconst_1
    //   19: invokevirtual 252	java/io/DataOutputStream:writeInt	(I)V
    //   22: aload_1
    //   23: aload_0
    //   24: getfield 86	com/google/android/gms/phenotype/ExperimentTokens:user	Ljava/lang/String;
    //   27: invokevirtual 331	java/io/DataOutputStream:writeUTF	(Ljava/lang/String;)V
    //   30: aload_0
    //   31: aload_0
    //   32: getfield 88	com/google/android/gms/phenotype/ExperimentTokens:directExperimentToken	[B
    //   35: aload_1
    //   36: invokespecial 261	com/google/android/gms/phenotype/ExperimentTokens:zza	([BLjava/io/DataOutputStream;)V
    //   39: aload_0
    //   40: aload_0
    //   41: getfield 90	com/google/android/gms/phenotype/ExperimentTokens:gaiaCrossExperimentTokens	[[B
    //   44: aload_1
    //   45: invokespecial 333	com/google/android/gms/phenotype/ExperimentTokens:zza	([[BLjava/io/DataOutputStream;)V
    //   48: aload_0
    //   49: aload_0
    //   50: getfield 92	com/google/android/gms/phenotype/ExperimentTokens:pseudonymousCrossExperimentTokens	[[B
    //   53: aload_1
    //   54: invokespecial 333	com/google/android/gms/phenotype/ExperimentTokens:zza	([[BLjava/io/DataOutputStream;)V
    //   57: aload_0
    //   58: aload_0
    //   59: getfield 94	com/google/android/gms/phenotype/ExperimentTokens:alwaysCrossExperimentTokens	[[B
    //   62: aload_1
    //   63: invokespecial 333	com/google/android/gms/phenotype/ExperimentTokens:zza	([[BLjava/io/DataOutputStream;)V
    //   66: aload_0
    //   67: aload_0
    //   68: getfield 96	com/google/android/gms/phenotype/ExperimentTokens:otherCrossExperimentTokens	[[B
    //   71: aload_1
    //   72: invokespecial 333	com/google/android/gms/phenotype/ExperimentTokens:zza	([[BLjava/io/DataOutputStream;)V
    //   75: aload_0
    //   76: aload_0
    //   77: getfield 98	com/google/android/gms/phenotype/ExperimentTokens:weakExperimentIds	[I
    //   80: aload_1
    //   81: invokespecial 335	com/google/android/gms/phenotype/ExperimentTokens:zza	([ILjava/io/DataOutputStream;)V
    //   84: aload_0
    //   85: aload_0
    //   86: getfield 100	com/google/android/gms/phenotype/ExperimentTokens:additionalDirectExperimentTokens	[[B
    //   89: aload_1
    //   90: invokespecial 333	com/google/android/gms/phenotype/ExperimentTokens:zza	([[BLjava/io/DataOutputStream;)V
    //   93: aload_1
    //   94: invokevirtual 338	java/io/DataOutputStream:flush	()V
    //   97: aload_2
    //   98: invokevirtual 342	java/io/ByteArrayOutputStream:toByteArray	()[B
    //   101: iconst_0
    //   102: invokestatic 346	android/util/Base64:encodeToString	([BI)Ljava/lang/String;
    //   105: astore_2
    //   106: aload_1
    //   107: invokevirtual 349	java/io/DataOutputStream:close	()V
    //   110: aload_2
    //   111: areturn
    //   112: astore_1
    //   113: new 126	java/lang/RuntimeException
    //   116: dup
    //   117: aload_1
    //   118: invokespecial 150	java/lang/RuntimeException:<init>	(Ljava/lang/Throwable;)V
    //   121: athrow
    //   122: astore_2
    //   123: new 126	java/lang/RuntimeException
    //   126: dup
    //   127: aload_2
    //   128: invokespecial 150	java/lang/RuntimeException:<init>	(Ljava/lang/Throwable;)V
    //   131: athrow
    //   132: astore_2
    //   133: aload_1
    //   134: invokevirtual 349	java/io/DataOutputStream:close	()V
    //   137: aload_2
    //   138: athrow
    //   139: astore_1
    //   140: new 126	java/lang/RuntimeException
    //   143: dup
    //   144: aload_1
    //   145: invokespecial 150	java/lang/RuntimeException:<init>	(Ljava/lang/Throwable;)V
    //   148: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	149	0	this	ExperimentTokens
    //   16	91	1	localDataOutputStream	DataOutputStream
    //   112	22	1	localIOException1	IOException
    //   139	6	1	localIOException2	IOException
    //   7	104	2	localObject1	Object
    //   122	6	2	localIOException3	IOException
    //   132	6	2	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   106	110	112	java/io/IOException
    //   17	106	122	java/io/IOException
    //   17	106	132	finally
    //   123	132	132	finally
    //   133	137	139	java/io/IOException
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder("ExperimentTokens");
    localStringBuilder.append("(");
    if (this.user == null) {}
    String str2;
    String str3;
    for (String str1 = "null";; str1 = String.valueOf(str1).length() + String.valueOf(str2).length() + String.valueOf(str3).length() + str1 + str2 + str3)
    {
      localStringBuilder.append(str1);
      localStringBuilder.append(", ");
      zza(localStringBuilder, "direct", this.directExperimentToken);
      localStringBuilder.append(", ");
      zza(localStringBuilder, "GAIA", this.gaiaCrossExperimentTokens);
      localStringBuilder.append(", ");
      zza(localStringBuilder, "PSEUDO", this.pseudonymousCrossExperimentTokens);
      localStringBuilder.append(", ");
      zza(localStringBuilder, "ALWAYS", this.alwaysCrossExperimentTokens);
      localStringBuilder.append(", ");
      zza(localStringBuilder, "OTHER", this.otherCrossExperimentTokens);
      localStringBuilder.append(", ");
      zza(localStringBuilder, "weak", this.weakExperimentIds);
      localStringBuilder.append(", ");
      zza(localStringBuilder, "directs", this.additionalDirectExperimentTokens);
      localStringBuilder.append(")");
      return localStringBuilder.toString();
      str1 = String.valueOf("'");
      str2 = this.user;
      str3 = String.valueOf("'");
    }
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    zzd.zza(this, paramParcel, paramInt);
  }
  
  private static abstract interface zza
  {
    public abstract byte[][] zza(ExperimentTokens paramExperimentTokens);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/phenotype/ExperimentTokens.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */