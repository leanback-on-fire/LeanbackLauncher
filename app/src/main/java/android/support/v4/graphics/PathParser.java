package android.support.v4.graphics;

import android.graphics.Path;
import android.support.annotation.RestrictTo;
import android.util.Log;
import java.util.ArrayList;

@RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
public class PathParser
{
  private static final String LOGTAG = "PathParser";
  
  private static void addNode(ArrayList<PathDataNode> paramArrayList, char paramChar, float[] paramArrayOfFloat)
  {
    paramArrayList.add(new PathDataNode(paramChar, paramArrayOfFloat));
  }
  
  public static boolean canMorph(PathDataNode[] paramArrayOfPathDataNode1, PathDataNode[] paramArrayOfPathDataNode2)
  {
    if ((paramArrayOfPathDataNode1 == null) || (paramArrayOfPathDataNode2 == null)) {}
    while (paramArrayOfPathDataNode1.length != paramArrayOfPathDataNode2.length) {
      return false;
    }
    int i = 0;
    for (;;)
    {
      if (i >= paramArrayOfPathDataNode1.length) {
        break label64;
      }
      if ((paramArrayOfPathDataNode1[i].mType != paramArrayOfPathDataNode2[i].mType) || (paramArrayOfPathDataNode1[i].mParams.length != paramArrayOfPathDataNode2[i].mParams.length)) {
        break;
      }
      i += 1;
    }
    label64:
    return true;
  }
  
  static float[] copyOfRange(float[] paramArrayOfFloat, int paramInt1, int paramInt2)
  {
    if (paramInt1 > paramInt2) {
      throw new IllegalArgumentException();
    }
    int i = paramArrayOfFloat.length;
    if ((paramInt1 < 0) || (paramInt1 > i)) {
      throw new ArrayIndexOutOfBoundsException();
    }
    paramInt2 -= paramInt1;
    i = Math.min(paramInt2, i - paramInt1);
    float[] arrayOfFloat = new float[paramInt2];
    System.arraycopy(paramArrayOfFloat, paramInt1, arrayOfFloat, 0, i);
    return arrayOfFloat;
  }
  
  public static PathDataNode[] createNodesFromPathData(String paramString)
  {
    if (paramString == null) {
      return null;
    }
    int i = 0;
    int j = 1;
    ArrayList localArrayList = new ArrayList();
    while (j < paramString.length())
    {
      j = nextStart(paramString, j);
      String str = paramString.substring(i, j).trim();
      if (str.length() > 0)
      {
        float[] arrayOfFloat = getFloats(str);
        addNode(localArrayList, str.charAt(0), arrayOfFloat);
      }
      i = j;
      j += 1;
    }
    if ((j - i == 1) && (i < paramString.length())) {
      addNode(localArrayList, paramString.charAt(i), new float[0]);
    }
    return (PathDataNode[])localArrayList.toArray(new PathDataNode[localArrayList.size()]);
  }
  
  public static Path createPathFromPathData(String paramString)
  {
    Path localPath = new Path();
    PathDataNode[] arrayOfPathDataNode = createNodesFromPathData(paramString);
    if (arrayOfPathDataNode != null) {
      try
      {
        PathDataNode.nodesToPath(arrayOfPathDataNode, localPath);
        return localPath;
      }
      catch (RuntimeException localRuntimeException)
      {
        throw new RuntimeException("Error in parsing " + paramString, localRuntimeException);
      }
    }
    return null;
  }
  
  public static PathDataNode[] deepCopyNodes(PathDataNode[] paramArrayOfPathDataNode)
  {
    Object localObject;
    if (paramArrayOfPathDataNode == null)
    {
      localObject = null;
      return (PathDataNode[])localObject;
    }
    PathDataNode[] arrayOfPathDataNode = new PathDataNode[paramArrayOfPathDataNode.length];
    int i = 0;
    for (;;)
    {
      localObject = arrayOfPathDataNode;
      if (i >= paramArrayOfPathDataNode.length) {
        break;
      }
      arrayOfPathDataNode[i] = new PathDataNode(paramArrayOfPathDataNode[i]);
      i += 1;
    }
  }
  
  private static void extract(String paramString, int paramInt, ExtractFloatResult paramExtractFloatResult)
  {
    int m = paramInt;
    int n = 0;
    paramExtractFloatResult.mEndWithNegOrDot = false;
    int k = 0;
    int i1 = 0;
    for (;;)
    {
      int i3;
      int i2;
      int j;
      int i;
      if (m < paramString.length())
      {
        i3 = 0;
        switch (paramString.charAt(m))
        {
        default: 
          i2 = k;
          j = i3;
          i = n;
        }
      }
      while (i != 0)
      {
        paramExtractFloatResult.mEndPosition = m;
        return;
        i = 1;
        j = i3;
        i2 = k;
        continue;
        i = n;
        j = i3;
        i2 = k;
        if (m != paramInt)
        {
          i = n;
          j = i3;
          i2 = k;
          if (i1 == 0)
          {
            i = 1;
            paramExtractFloatResult.mEndWithNegOrDot = true;
            j = i3;
            i2 = k;
            continue;
            if (k == 0)
            {
              i2 = 1;
              i = n;
              j = i3;
            }
            else
            {
              i = 1;
              paramExtractFloatResult.mEndWithNegOrDot = true;
              j = i3;
              i2 = k;
              continue;
              j = 1;
              i = n;
              i2 = k;
            }
          }
        }
      }
      m += 1;
      n = i;
      i1 = j;
      k = i2;
    }
  }
  
  private static float[] getFloats(String paramString)
  {
    if ((paramString.charAt(0) == 'z') || (paramString.charAt(0) == 'Z')) {
      return new float[0];
    }
    for (;;)
    {
      int k;
      try
      {
        float[] arrayOfFloat = new float[paramString.length()];
        j = 1;
        ExtractFloatResult localExtractFloatResult = new ExtractFloatResult();
        int n = paramString.length();
        int i = 0;
        if (j < n)
        {
          extract(paramString, j, localExtractFloatResult);
          k = localExtractFloatResult.mEndPosition;
          if (j < k)
          {
            int m = i + 1;
            arrayOfFloat[i] = Float.parseFloat(paramString.substring(j, k));
            i = m;
            if (!localExtractFloatResult.mEndWithNegOrDot) {
              break label160;
            }
            j = k;
          }
        }
        else
        {
          arrayOfFloat = copyOfRange(arrayOfFloat, 0, i);
          return arrayOfFloat;
        }
      }
      catch (NumberFormatException localNumberFormatException)
      {
        throw new RuntimeException("error in parsing \"" + paramString + "\"", localNumberFormatException);
      }
      continue;
      label160:
      int j = k + 1;
    }
  }
  
  private static int nextStart(String paramString, int paramInt)
  {
    for (;;)
    {
      if (paramInt < paramString.length())
      {
        int i = paramString.charAt(paramInt);
        if ((((i - 65) * (i - 90) > 0) && ((i - 97) * (i - 122) > 0)) || (i == 101) || (i == 69)) {}
      }
      else
      {
        return paramInt;
      }
      paramInt += 1;
    }
  }
  
  public static void updateNodes(PathDataNode[] paramArrayOfPathDataNode1, PathDataNode[] paramArrayOfPathDataNode2)
  {
    int i = 0;
    while (i < paramArrayOfPathDataNode2.length)
    {
      paramArrayOfPathDataNode1[i].mType = paramArrayOfPathDataNode2[i].mType;
      int j = 0;
      while (j < paramArrayOfPathDataNode2[i].mParams.length)
      {
        paramArrayOfPathDataNode1[i].mParams[j] = paramArrayOfPathDataNode2[i].mParams[j];
        j += 1;
      }
      i += 1;
    }
  }
  
  private static class ExtractFloatResult
  {
    int mEndPosition;
    boolean mEndWithNegOrDot;
  }
  
  public static class PathDataNode
  {
    @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
    public float[] mParams;
    @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
    public char mType;
    
    PathDataNode(char paramChar, float[] paramArrayOfFloat)
    {
      this.mType = paramChar;
      this.mParams = paramArrayOfFloat;
    }
    
    PathDataNode(PathDataNode paramPathDataNode)
    {
      this.mType = paramPathDataNode.mType;
      this.mParams = PathParser.copyOfRange(paramPathDataNode.mParams, 0, paramPathDataNode.mParams.length);
    }
    
    private static void addCommand(Path paramPath, float[] paramArrayOfFloat1, char paramChar1, char paramChar2, float[] paramArrayOfFloat2)
    {
      char c1 = '\002';
      float f1 = paramArrayOfFloat1[0];
      float f2 = paramArrayOfFloat1[1];
      float f4 = paramArrayOfFloat1[2];
      float f6 = paramArrayOfFloat1[3];
      float f3 = paramArrayOfFloat1[4];
      float f5 = paramArrayOfFloat1[5];
      int i;
      float f7;
      float f8;
      float f9;
      switch (paramChar2)
      {
      default: 
        char c2 = '\000';
        i = paramChar1;
        paramChar1 = c2;
        f7 = f5;
        f5 = f3;
        f8 = f6;
        f9 = f4;
        if (paramChar1 < paramArrayOfFloat2.length) {
          switch (paramChar2)
          {
          default: 
            f6 = f7;
            f4 = f8;
            f3 = f9;
          }
        }
        break;
      case 'Z': 
      case 'z': 
      case 'L': 
      case 'M': 
      case 'T': 
      case 'l': 
      case 'm': 
      case 't': 
      case 'H': 
      case 'V': 
      case 'h': 
      case 'v': 
      case 'C': 
      case 'c': 
      case 'Q': 
      case 'S': 
      case 'q': 
      case 's': 
      case 'A': 
      case 'a': 
        for (;;)
        {
          label229:
          i = paramChar2;
          paramChar1 += c1;
          f9 = f3;
          f8 = f4;
          f7 = f6;
          break label229;
          paramPath.close();
          f1 = f3;
          f2 = f5;
          f4 = f3;
          f6 = f5;
          paramPath.moveTo(f1, f2);
          break;
          c1 = '\002';
          break;
          c1 = '\001';
          break;
          c1 = '\006';
          break;
          c1 = '\004';
          break;
          c1 = '\007';
          break;
          f1 += paramArrayOfFloat2[(paramChar1 + '\000')];
          f2 += paramArrayOfFloat2[(paramChar1 + '\001')];
          if (paramChar1 > 0)
          {
            paramPath.rLineTo(paramArrayOfFloat2[(paramChar1 + '\000')], paramArrayOfFloat2[(paramChar1 + '\001')]);
            f3 = f9;
            f4 = f8;
            f6 = f7;
          }
          else
          {
            paramPath.rMoveTo(paramArrayOfFloat2[(paramChar1 + '\000')], paramArrayOfFloat2[(paramChar1 + '\001')]);
            f5 = f1;
            f6 = f2;
            f3 = f9;
            f4 = f8;
            continue;
            f1 = paramArrayOfFloat2[(paramChar1 + '\000')];
            f2 = paramArrayOfFloat2[(paramChar1 + '\001')];
            if (paramChar1 > 0)
            {
              paramPath.lineTo(paramArrayOfFloat2[(paramChar1 + '\000')], paramArrayOfFloat2[(paramChar1 + '\001')]);
              f3 = f9;
              f4 = f8;
              f6 = f7;
            }
            else
            {
              paramPath.moveTo(paramArrayOfFloat2[(paramChar1 + '\000')], paramArrayOfFloat2[(paramChar1 + '\001')]);
              f5 = f1;
              f6 = f2;
              f3 = f9;
              f4 = f8;
              continue;
              paramPath.rLineTo(paramArrayOfFloat2[(paramChar1 + '\000')], paramArrayOfFloat2[(paramChar1 + '\001')]);
              f1 += paramArrayOfFloat2[(paramChar1 + '\000')];
              f2 += paramArrayOfFloat2[(paramChar1 + '\001')];
              f3 = f9;
              f4 = f8;
              f6 = f7;
              continue;
              paramPath.lineTo(paramArrayOfFloat2[(paramChar1 + '\000')], paramArrayOfFloat2[(paramChar1 + '\001')]);
              f1 = paramArrayOfFloat2[(paramChar1 + '\000')];
              f2 = paramArrayOfFloat2[(paramChar1 + '\001')];
              f3 = f9;
              f4 = f8;
              f6 = f7;
              continue;
              paramPath.rLineTo(paramArrayOfFloat2[(paramChar1 + '\000')], 0.0F);
              f1 += paramArrayOfFloat2[(paramChar1 + '\000')];
              f3 = f9;
              f4 = f8;
              f6 = f7;
              continue;
              paramPath.lineTo(paramArrayOfFloat2[(paramChar1 + '\000')], f2);
              f1 = paramArrayOfFloat2[(paramChar1 + '\000')];
              f3 = f9;
              f4 = f8;
              f6 = f7;
              continue;
              paramPath.rLineTo(0.0F, paramArrayOfFloat2[(paramChar1 + '\000')]);
              f2 += paramArrayOfFloat2[(paramChar1 + '\000')];
              f3 = f9;
              f4 = f8;
              f6 = f7;
              continue;
              paramPath.lineTo(f1, paramArrayOfFloat2[(paramChar1 + '\000')]);
              f2 = paramArrayOfFloat2[(paramChar1 + '\000')];
              f3 = f9;
              f4 = f8;
              f6 = f7;
              continue;
              paramPath.rCubicTo(paramArrayOfFloat2[(paramChar1 + '\000')], paramArrayOfFloat2[(paramChar1 + '\001')], paramArrayOfFloat2[(paramChar1 + '\002')], paramArrayOfFloat2[(paramChar1 + '\003')], paramArrayOfFloat2[(paramChar1 + '\004')], paramArrayOfFloat2[(paramChar1 + '\005')]);
              f3 = f1 + paramArrayOfFloat2[(paramChar1 + '\002')];
              f4 = f2 + paramArrayOfFloat2[(paramChar1 + '\003')];
              f1 += paramArrayOfFloat2[(paramChar1 + '\004')];
              f2 += paramArrayOfFloat2[(paramChar1 + '\005')];
              f6 = f7;
              continue;
              paramPath.cubicTo(paramArrayOfFloat2[(paramChar1 + '\000')], paramArrayOfFloat2[(paramChar1 + '\001')], paramArrayOfFloat2[(paramChar1 + '\002')], paramArrayOfFloat2[(paramChar1 + '\003')], paramArrayOfFloat2[(paramChar1 + '\004')], paramArrayOfFloat2[(paramChar1 + '\005')]);
              f1 = paramArrayOfFloat2[(paramChar1 + '\004')];
              f2 = paramArrayOfFloat2[(paramChar1 + '\005')];
              f3 = paramArrayOfFloat2[(paramChar1 + '\002')];
              f4 = paramArrayOfFloat2[(paramChar1 + '\003')];
              f6 = f7;
              continue;
              f3 = 0.0F;
              f4 = 0.0F;
              if ((i == 99) || (i == 115) || (i == 67) || (i == 83))
              {
                f3 = f1 - f9;
                f4 = f2 - f8;
              }
              paramPath.rCubicTo(f3, f4, paramArrayOfFloat2[(paramChar1 + '\000')], paramArrayOfFloat2[(paramChar1 + '\001')], paramArrayOfFloat2[(paramChar1 + '\002')], paramArrayOfFloat2[(paramChar1 + '\003')]);
              f3 = f1 + paramArrayOfFloat2[(paramChar1 + '\000')];
              f4 = f2 + paramArrayOfFloat2[(paramChar1 + '\001')];
              f1 += paramArrayOfFloat2[(paramChar1 + '\002')];
              f2 += paramArrayOfFloat2[(paramChar1 + '\003')];
              f6 = f7;
              continue;
              f4 = f1;
              f3 = f2;
              if ((i == 99) || (i == 115) || (i == 67) || (i == 83))
              {
                f4 = 2.0F * f1 - f9;
                f3 = 2.0F * f2 - f8;
              }
              paramPath.cubicTo(f4, f3, paramArrayOfFloat2[(paramChar1 + '\000')], paramArrayOfFloat2[(paramChar1 + '\001')], paramArrayOfFloat2[(paramChar1 + '\002')], paramArrayOfFloat2[(paramChar1 + '\003')]);
              f3 = paramArrayOfFloat2[(paramChar1 + '\000')];
              f4 = paramArrayOfFloat2[(paramChar1 + '\001')];
              f1 = paramArrayOfFloat2[(paramChar1 + '\002')];
              f2 = paramArrayOfFloat2[(paramChar1 + '\003')];
              f6 = f7;
              continue;
              paramPath.rQuadTo(paramArrayOfFloat2[(paramChar1 + '\000')], paramArrayOfFloat2[(paramChar1 + '\001')], paramArrayOfFloat2[(paramChar1 + '\002')], paramArrayOfFloat2[(paramChar1 + '\003')]);
              f3 = f1 + paramArrayOfFloat2[(paramChar1 + '\000')];
              f4 = f2 + paramArrayOfFloat2[(paramChar1 + '\001')];
              f1 += paramArrayOfFloat2[(paramChar1 + '\002')];
              f2 += paramArrayOfFloat2[(paramChar1 + '\003')];
              f6 = f7;
              continue;
              paramPath.quadTo(paramArrayOfFloat2[(paramChar1 + '\000')], paramArrayOfFloat2[(paramChar1 + '\001')], paramArrayOfFloat2[(paramChar1 + '\002')], paramArrayOfFloat2[(paramChar1 + '\003')]);
              f3 = paramArrayOfFloat2[(paramChar1 + '\000')];
              f4 = paramArrayOfFloat2[(paramChar1 + '\001')];
              f1 = paramArrayOfFloat2[(paramChar1 + '\002')];
              f2 = paramArrayOfFloat2[(paramChar1 + '\003')];
              f6 = f7;
              continue;
              f4 = 0.0F;
              f3 = 0.0F;
              if ((i == 113) || (i == 116) || (i == 81) || (i == 84))
              {
                f4 = f1 - f9;
                f3 = f2 - f8;
              }
              paramPath.rQuadTo(f4, f3, paramArrayOfFloat2[(paramChar1 + '\000')], paramArrayOfFloat2[(paramChar1 + '\001')]);
              f4 = f1 + f4;
              f6 = f2 + f3;
              f1 += paramArrayOfFloat2[(paramChar1 + '\000')];
              f2 += paramArrayOfFloat2[(paramChar1 + '\001')];
              f3 = f4;
              f4 = f6;
              f6 = f7;
              continue;
              f4 = f1;
              f3 = f2;
              if ((i == 113) || (i == 116) || (i == 81) || (i == 84))
              {
                f4 = 2.0F * f1 - f9;
                f3 = 2.0F * f2 - f8;
              }
              paramPath.quadTo(f4, f3, paramArrayOfFloat2[(paramChar1 + '\000')], paramArrayOfFloat2[(paramChar1 + '\001')]);
              f1 = f3;
              f8 = paramArrayOfFloat2[(paramChar1 + '\000')];
              f2 = paramArrayOfFloat2[(paramChar1 + '\001')];
              f3 = f4;
              f4 = f1;
              f6 = f7;
              f1 = f8;
            }
          }
        }
        f3 = paramArrayOfFloat2[(paramChar1 + '\005')];
        f4 = paramArrayOfFloat2[(paramChar1 + '\006')];
        f6 = paramArrayOfFloat2[(paramChar1 + '\000')];
        f8 = paramArrayOfFloat2[(paramChar1 + '\001')];
        f9 = paramArrayOfFloat2[(paramChar1 + '\002')];
        boolean bool1;
        if (paramArrayOfFloat2[(paramChar1 + '\003')] != 0.0F)
        {
          bool1 = true;
          label1771:
          if (paramArrayOfFloat2[(paramChar1 + '\004')] == 0.0F) {
            break label1857;
          }
        }
        label1857:
        for (boolean bool2 = true;; bool2 = false)
        {
          drawArc(paramPath, f1, f2, f3 + f1, f4 + f2, f6, f8, f9, bool1, bool2);
          f1 += paramArrayOfFloat2[(paramChar1 + '\005')];
          f2 += paramArrayOfFloat2[(paramChar1 + '\006')];
          f3 = f1;
          f4 = f2;
          f6 = f7;
          break;
          bool1 = false;
          break label1771;
        }
        f3 = paramArrayOfFloat2[(paramChar1 + '\005')];
        f4 = paramArrayOfFloat2[(paramChar1 + '\006')];
        f6 = paramArrayOfFloat2[(paramChar1 + '\000')];
        f8 = paramArrayOfFloat2[(paramChar1 + '\001')];
        f9 = paramArrayOfFloat2[(paramChar1 + '\002')];
        if (paramArrayOfFloat2[(paramChar1 + '\003')] != 0.0F)
        {
          bool1 = true;
          label1918:
          if (paramArrayOfFloat2[(paramChar1 + '\004')] == 0.0F) {
            break label1992;
          }
        }
        label1992:
        for (bool2 = true;; bool2 = false)
        {
          drawArc(paramPath, f1, f2, f3, f4, f6, f8, f9, bool1, bool2);
          f1 = paramArrayOfFloat2[(paramChar1 + '\005')];
          f2 = paramArrayOfFloat2[(paramChar1 + '\006')];
          f3 = f1;
          f4 = f2;
          f6 = f7;
          break;
          bool1 = false;
          break label1918;
        }
      }
      paramArrayOfFloat1[0] = f1;
      paramArrayOfFloat1[1] = f2;
      paramArrayOfFloat1[2] = f9;
      paramArrayOfFloat1[3] = f8;
      paramArrayOfFloat1[4] = f5;
      paramArrayOfFloat1[5] = f7;
    }
    
    private static void arcToBezier(Path paramPath, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, double paramDouble7, double paramDouble8, double paramDouble9)
    {
      int j = (int)Math.ceil(Math.abs(4.0D * paramDouble9 / 3.141592653589793D));
      double d1 = paramDouble8;
      double d6 = Math.cos(paramDouble7);
      double d7 = Math.sin(paramDouble7);
      paramDouble7 = Math.cos(d1);
      paramDouble8 = Math.sin(d1);
      double d2 = -paramDouble3 * d6 * paramDouble8 - paramDouble4 * d7 * paramDouble7;
      double d3 = -paramDouble3 * d7 * paramDouble8 + paramDouble4 * d6 * paramDouble7;
      double d8 = paramDouble9 / j;
      int i = 0;
      paramDouble8 = paramDouble6;
      paramDouble7 = paramDouble5;
      paramDouble9 = d1;
      paramDouble6 = d3;
      paramDouble5 = d2;
      while (i < j)
      {
        double d5 = paramDouble9 + d8;
        d3 = Math.sin(d5);
        double d9 = Math.cos(d5);
        double d4 = paramDouble3 * d6 * d9 + paramDouble1 - paramDouble4 * d7 * d3;
        d2 = paramDouble3 * d7 * d9 + paramDouble2 + paramDouble4 * d6 * d3;
        d1 = -paramDouble3 * d6 * d3 - paramDouble4 * d7 * d9;
        d3 = -paramDouble3 * d7 * d3 + paramDouble4 * d6 * d9;
        d9 = Math.tan((d5 - paramDouble9) / 2.0D);
        paramDouble9 = Math.sin(d5 - paramDouble9) * (Math.sqrt(4.0D + 3.0D * d9 * d9) - 1.0D) / 3.0D;
        paramPath.rLineTo(0.0F, 0.0F);
        paramPath.cubicTo((float)(paramDouble7 + paramDouble9 * paramDouble5), (float)(paramDouble8 + paramDouble9 * paramDouble6), (float)(d4 - paramDouble9 * d1), (float)(d2 - paramDouble9 * d3), (float)d4, (float)d2);
        paramDouble9 = d5;
        paramDouble7 = d4;
        paramDouble8 = d2;
        paramDouble5 = d1;
        paramDouble6 = d3;
        i += 1;
      }
    }
    
    private static void drawArc(Path paramPath, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6, float paramFloat7, boolean paramBoolean1, boolean paramBoolean2)
    {
      double d5 = Math.toRadians(paramFloat7);
      double d6 = Math.cos(d5);
      double d7 = Math.sin(d5);
      double d8 = (paramFloat1 * d6 + paramFloat2 * d7) / paramFloat5;
      double d9 = (-paramFloat1 * d7 + paramFloat2 * d6) / paramFloat6;
      double d1 = (paramFloat3 * d6 + paramFloat4 * d7) / paramFloat5;
      double d4 = (-paramFloat3 * d7 + paramFloat4 * d6) / paramFloat6;
      double d11 = d8 - d1;
      double d10 = d9 - d4;
      double d3 = (d8 + d1) / 2.0D;
      double d2 = (d9 + d4) / 2.0D;
      double d12 = d11 * d11 + d10 * d10;
      if (d12 == 0.0D)
      {
        Log.w("PathParser", " Points are coincident");
        return;
      }
      double d13 = 1.0D / d12 - 0.25D;
      if (d13 < 0.0D)
      {
        Log.w("PathParser", "Points are too far apart " + d12);
        float f = (float)(Math.sqrt(d12) / 1.99999D);
        drawArc(paramPath, paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramFloat5 * f, paramFloat6 * f, paramFloat7, paramBoolean1, paramBoolean2);
        return;
      }
      d12 = Math.sqrt(d13);
      d11 = d12 * d11;
      d10 = d12 * d10;
      if (paramBoolean1 == paramBoolean2)
      {
        d3 -= d10;
        d2 += d11;
        d8 = Math.atan2(d9 - d2, d8 - d3);
        d4 = Math.atan2(d4 - d2, d1 - d3) - d8;
        if (d4 < 0.0D) {
          break label427;
        }
        paramBoolean1 = true;
        label325:
        d1 = d4;
        if (paramBoolean2 != paramBoolean1) {
          if (d4 <= 0.0D) {
            break label433;
          }
        }
      }
      label427:
      label433:
      for (d1 = d4 - 6.283185307179586D;; d1 = d4 + 6.283185307179586D)
      {
        d3 *= paramFloat5;
        d2 *= paramFloat6;
        arcToBezier(paramPath, d3 * d6 - d2 * d7, d3 * d7 + d2 * d6, paramFloat5, paramFloat6, paramFloat1, paramFloat2, d5, d8, d1);
        return;
        d3 += d10;
        d2 -= d11;
        break;
        paramBoolean1 = false;
        break label325;
      }
    }
    
    public static void nodesToPath(PathDataNode[] paramArrayOfPathDataNode, Path paramPath)
    {
      float[] arrayOfFloat = new float[6];
      char c = 'm';
      int i = 0;
      while (i < paramArrayOfPathDataNode.length)
      {
        addCommand(paramPath, arrayOfFloat, c, paramArrayOfPathDataNode[i].mType, paramArrayOfPathDataNode[i].mParams);
        c = paramArrayOfPathDataNode[i].mType;
        i += 1;
      }
    }
    
    public void interpolatePathDataNode(PathDataNode paramPathDataNode1, PathDataNode paramPathDataNode2, float paramFloat)
    {
      int i = 0;
      while (i < paramPathDataNode1.mParams.length)
      {
        this.mParams[i] = (paramPathDataNode1.mParams[i] * (1.0F - paramFloat) + paramPathDataNode2.mParams[i] * paramFloat);
        i += 1;
      }
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v4/graphics/PathParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */