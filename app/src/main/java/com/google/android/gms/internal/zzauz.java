package com.google.android.gms.internal;

import java.io.IOException;

public abstract interface zzauz
{
  public static final class zza
    extends zzcfz<zza>
  {
    private static volatile zza[] zzbRM;
    public zzauz.zzf zzbRN;
    public zzauz.zzf zzbRO;
    public Boolean zzbRP;
    public Integer zzbRe;
    
    public zza()
    {
      zzPu();
    }
    
    public static zza[] zzPt()
    {
      if (zzbRM == null) {}
      synchronized (zzcge.Gh)
      {
        if (zzbRM == null) {
          zzbRM = new zza[0];
        }
        return zzbRM;
      }
    }
    
    protected int computeSerializedSize()
    {
      int j = super.computeSerializedSize();
      int i = j;
      if (this.zzbRe != null) {
        i = j + zzcfy.zzac(1, this.zzbRe.intValue());
      }
      j = i;
      if (this.zzbRN != null) {
        j = i + zzcfy.zzc(2, this.zzbRN);
      }
      i = j;
      if (this.zzbRO != null) {
        i = j + zzcfy.zzc(3, this.zzbRO);
      }
      j = i;
      if (this.zzbRP != null) {
        j = i + zzcfy.zzl(4, this.zzbRP.booleanValue());
      }
      return j;
    }
    
    public boolean equals(Object paramObject)
    {
      boolean bool2 = false;
      boolean bool1;
      if (paramObject == this) {
        bool1 = true;
      }
      label41:
      label57:
      label73:
      do
      {
        do
        {
          do
          {
            do
            {
              do
              {
                return bool1;
                bool1 = bool2;
              } while (!(paramObject instanceof zza));
              paramObject = (zza)paramObject;
              if (this.zzbRe != null) {
                break;
              }
              bool1 = bool2;
            } while (((zza)paramObject).zzbRe != null);
            if (this.zzbRN != null) {
              break label143;
            }
            bool1 = bool2;
          } while (((zza)paramObject).zzbRN != null);
          if (this.zzbRO != null) {
            break label159;
          }
          bool1 = bool2;
        } while (((zza)paramObject).zzbRO != null);
        if (this.zzbRP != null) {
          break label175;
        }
        bool1 = bool2;
      } while (((zza)paramObject).zzbRP != null);
      for (;;)
      {
        if ((this.FZ == null) || (this.FZ.isEmpty()))
        {
          if (((zza)paramObject).FZ != null)
          {
            bool1 = bool2;
            if (!((zza)paramObject).FZ.isEmpty()) {
              break;
            }
          }
          return true;
          if (this.zzbRe.equals(((zza)paramObject).zzbRe)) {
            break label41;
          }
          return false;
          label143:
          if (this.zzbRN.equals(((zza)paramObject).zzbRN)) {
            break label57;
          }
          return false;
          label159:
          if (this.zzbRO.equals(((zza)paramObject).zzbRO)) {
            break label73;
          }
          return false;
          label175:
          if (!this.zzbRP.equals(((zza)paramObject).zzbRP)) {
            return false;
          }
        }
      }
      return this.FZ.equals(((zza)paramObject).FZ);
    }
    
    public int hashCode()
    {
      int i1 = 0;
      int i2 = getClass().getName().hashCode();
      int i;
      int j;
      label33:
      int k;
      label42:
      int m;
      if (this.zzbRe == null)
      {
        i = 0;
        if (this.zzbRN != null) {
          break label122;
        }
        j = 0;
        if (this.zzbRO != null) {
          break label133;
        }
        k = 0;
        if (this.zzbRP != null) {
          break label144;
        }
        m = 0;
        label52:
        n = i1;
        if (this.FZ != null) {
          if (!this.FZ.isEmpty()) {
            break label156;
          }
        }
      }
      label122:
      label133:
      label144:
      label156:
      for (int n = i1;; n = this.FZ.hashCode())
      {
        return (m + (k + (j + (i + (i2 + 527) * 31) * 31) * 31) * 31) * 31 + n;
        i = this.zzbRe.hashCode();
        break;
        j = this.zzbRN.hashCode();
        break label33;
        k = this.zzbRO.hashCode();
        break label42;
        m = this.zzbRP.hashCode();
        break label52;
      }
    }
    
    public void writeTo(zzcfy paramzzcfy)
      throws IOException
    {
      if (this.zzbRe != null) {
        paramzzcfy.zzaa(1, this.zzbRe.intValue());
      }
      if (this.zzbRN != null) {
        paramzzcfy.zza(2, this.zzbRN);
      }
      if (this.zzbRO != null) {
        paramzzcfy.zza(3, this.zzbRO);
      }
      if (this.zzbRP != null) {
        paramzzcfy.zzk(4, this.zzbRP.booleanValue());
      }
      super.writeTo(paramzzcfy);
    }
    
    public zza zzPu()
    {
      this.zzbRe = null;
      this.zzbRN = null;
      this.zzbRO = null;
      this.zzbRP = null;
      this.FZ = null;
      this.Gi = -1;
      return this;
    }
    
    public zza zzS(zzcfx paramzzcfx)
      throws IOException
    {
      for (;;)
      {
        int i = paramzzcfx.zzamI();
        switch (i)
        {
        default: 
          if (super.zza(paramzzcfx, i)) {}
          break;
        case 0: 
          return this;
        case 8: 
          this.zzbRe = Integer.valueOf(paramzzcfx.zzamM());
          break;
        case 18: 
          if (this.zzbRN == null) {
            this.zzbRN = new zzauz.zzf();
          }
          paramzzcfx.zza(this.zzbRN);
          break;
        case 26: 
          if (this.zzbRO == null) {
            this.zzbRO = new zzauz.zzf();
          }
          paramzzcfx.zza(this.zzbRO);
          break;
        case 32: 
          this.zzbRP = Boolean.valueOf(paramzzcfx.zzamO());
        }
      }
    }
  }
  
  public static final class zzb
    extends zzcfz<zzb>
  {
    private static volatile zzb[] zzbRQ;
    public Integer count;
    public String name;
    public zzauz.zzc[] zzbRR;
    public Long zzbRS;
    public Long zzbRT;
    
    public zzb()
    {
      zzPw();
    }
    
    public static zzb[] zzPv()
    {
      if (zzbRQ == null) {}
      synchronized (zzcge.Gh)
      {
        if (zzbRQ == null) {
          zzbRQ = new zzb[0];
        }
        return zzbRQ;
      }
    }
    
    protected int computeSerializedSize()
    {
      int i = super.computeSerializedSize();
      int j = i;
      if (this.zzbRR != null)
      {
        j = i;
        if (this.zzbRR.length > 0)
        {
          int k = 0;
          for (;;)
          {
            j = i;
            if (k >= this.zzbRR.length) {
              break;
            }
            zzauz.zzc localzzc = this.zzbRR[k];
            j = i;
            if (localzzc != null) {
              j = i + zzcfy.zzc(1, localzzc);
            }
            k += 1;
            i = j;
          }
        }
      }
      i = j;
      if (this.name != null) {
        i = j + zzcfy.zzv(2, this.name);
      }
      j = i;
      if (this.zzbRS != null) {
        j = i + zzcfy.zzj(3, this.zzbRS.longValue());
      }
      i = j;
      if (this.zzbRT != null) {
        i = j + zzcfy.zzj(4, this.zzbRT.longValue());
      }
      j = i;
      if (this.count != null) {
        j = i + zzcfy.zzac(5, this.count.intValue());
      }
      return j;
    }
    
    public boolean equals(Object paramObject)
    {
      boolean bool2 = false;
      boolean bool1;
      if (paramObject == this) {
        bool1 = true;
      }
      label57:
      label73:
      label89:
      do
      {
        do
        {
          do
          {
            do
            {
              do
              {
                do
                {
                  return bool1;
                  bool1 = bool2;
                } while (!(paramObject instanceof zzb));
                paramObject = (zzb)paramObject;
                bool1 = bool2;
              } while (!zzcge.equals(this.zzbRR, ((zzb)paramObject).zzbRR));
              if (this.name != null) {
                break;
              }
              bool1 = bool2;
            } while (((zzb)paramObject).name != null);
            if (this.zzbRS != null) {
              break label159;
            }
            bool1 = bool2;
          } while (((zzb)paramObject).zzbRS != null);
          if (this.zzbRT != null) {
            break label175;
          }
          bool1 = bool2;
        } while (((zzb)paramObject).zzbRT != null);
        if (this.count != null) {
          break label191;
        }
        bool1 = bool2;
      } while (((zzb)paramObject).count != null);
      for (;;)
      {
        if ((this.FZ == null) || (this.FZ.isEmpty()))
        {
          if (((zzb)paramObject).FZ != null)
          {
            bool1 = bool2;
            if (!((zzb)paramObject).FZ.isEmpty()) {
              break;
            }
          }
          return true;
          if (this.name.equals(((zzb)paramObject).name)) {
            break label57;
          }
          return false;
          label159:
          if (this.zzbRS.equals(((zzb)paramObject).zzbRS)) {
            break label73;
          }
          return false;
          label175:
          if (this.zzbRT.equals(((zzb)paramObject).zzbRT)) {
            break label89;
          }
          return false;
          label191:
          if (!this.count.equals(((zzb)paramObject).count)) {
            return false;
          }
        }
      }
      return this.FZ.equals(((zzb)paramObject).FZ);
    }
    
    public int hashCode()
    {
      int i1 = 0;
      int i2 = getClass().getName().hashCode();
      int i3 = zzcge.hashCode(this.zzbRR);
      int i;
      int j;
      label42:
      int k;
      label51:
      int m;
      if (this.name == null)
      {
        i = 0;
        if (this.zzbRS != null) {
          break label137;
        }
        j = 0;
        if (this.zzbRT != null) {
          break label148;
        }
        k = 0;
        if (this.count != null) {
          break label159;
        }
        m = 0;
        label61:
        n = i1;
        if (this.FZ != null) {
          if (!this.FZ.isEmpty()) {
            break label171;
          }
        }
      }
      label137:
      label148:
      label159:
      label171:
      for (int n = i1;; n = this.FZ.hashCode())
      {
        return (m + (k + (j + (i + ((i2 + 527) * 31 + i3) * 31) * 31) * 31) * 31) * 31 + n;
        i = this.name.hashCode();
        break;
        j = this.zzbRS.hashCode();
        break label42;
        k = this.zzbRT.hashCode();
        break label51;
        m = this.count.hashCode();
        break label61;
      }
    }
    
    public void writeTo(zzcfy paramzzcfy)
      throws IOException
    {
      if ((this.zzbRR != null) && (this.zzbRR.length > 0))
      {
        int i = 0;
        while (i < this.zzbRR.length)
        {
          zzauz.zzc localzzc = this.zzbRR[i];
          if (localzzc != null) {
            paramzzcfy.zza(1, localzzc);
          }
          i += 1;
        }
      }
      if (this.name != null) {
        paramzzcfy.zzu(2, this.name);
      }
      if (this.zzbRS != null) {
        paramzzcfy.zzf(3, this.zzbRS.longValue());
      }
      if (this.zzbRT != null) {
        paramzzcfy.zzf(4, this.zzbRT.longValue());
      }
      if (this.count != null) {
        paramzzcfy.zzaa(5, this.count.intValue());
      }
      super.writeTo(paramzzcfy);
    }
    
    public zzb zzPw()
    {
      this.zzbRR = zzauz.zzc.zzPx();
      this.name = null;
      this.zzbRS = null;
      this.zzbRT = null;
      this.count = null;
      this.FZ = null;
      this.Gi = -1;
      return this;
    }
    
    public zzb zzT(zzcfx paramzzcfx)
      throws IOException
    {
      for (;;)
      {
        int i = paramzzcfx.zzamI();
        switch (i)
        {
        default: 
          if (super.zza(paramzzcfx, i)) {}
          break;
        case 0: 
          return this;
        case 10: 
          int j = zzcgj.zzb(paramzzcfx, 10);
          if (this.zzbRR == null) {}
          zzauz.zzc[] arrayOfzzc;
          for (i = 0;; i = this.zzbRR.length)
          {
            arrayOfzzc = new zzauz.zzc[j + i];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.zzbRR, 0, arrayOfzzc, 0, i);
              j = i;
            }
            while (j < arrayOfzzc.length - 1)
            {
              arrayOfzzc[j] = new zzauz.zzc();
              paramzzcfx.zza(arrayOfzzc[j]);
              paramzzcfx.zzamI();
              j += 1;
            }
          }
          arrayOfzzc[j] = new zzauz.zzc();
          paramzzcfx.zza(arrayOfzzc[j]);
          this.zzbRR = arrayOfzzc;
          break;
        case 18: 
          this.name = paramzzcfx.readString();
          break;
        case 24: 
          this.zzbRS = Long.valueOf(paramzzcfx.zzamL());
          break;
        case 32: 
          this.zzbRT = Long.valueOf(paramzzcfx.zzamL());
          break;
        case 40: 
          this.count = Integer.valueOf(paramzzcfx.zzamM());
        }
      }
    }
  }
  
  public static final class zzc
    extends zzcfz<zzc>
  {
    private static volatile zzc[] zzbRU;
    public String name;
    public String stringValue;
    public Float zzbQY;
    public Double zzbQZ;
    public Long zzbRV;
    
    public zzc()
    {
      zzPy();
    }
    
    public static zzc[] zzPx()
    {
      if (zzbRU == null) {}
      synchronized (zzcge.Gh)
      {
        if (zzbRU == null) {
          zzbRU = new zzc[0];
        }
        return zzbRU;
      }
    }
    
    protected int computeSerializedSize()
    {
      int j = super.computeSerializedSize();
      int i = j;
      if (this.name != null) {
        i = j + zzcfy.zzv(1, this.name);
      }
      j = i;
      if (this.stringValue != null) {
        j = i + zzcfy.zzv(2, this.stringValue);
      }
      i = j;
      if (this.zzbRV != null) {
        i = j + zzcfy.zzj(3, this.zzbRV.longValue());
      }
      j = i;
      if (this.zzbQY != null) {
        j = i + zzcfy.zzd(4, this.zzbQY.floatValue());
      }
      i = j;
      if (this.zzbQZ != null) {
        i = j + zzcfy.zzb(5, this.zzbQZ.doubleValue());
      }
      return i;
    }
    
    public boolean equals(Object paramObject)
    {
      boolean bool2 = false;
      boolean bool1;
      if (paramObject == this) {
        bool1 = true;
      }
      label41:
      label57:
      label73:
      label89:
      do
      {
        do
        {
          do
          {
            do
            {
              do
              {
                do
                {
                  return bool1;
                  bool1 = bool2;
                } while (!(paramObject instanceof zzc));
                paramObject = (zzc)paramObject;
                if (this.name != null) {
                  break;
                }
                bool1 = bool2;
              } while (((zzc)paramObject).name != null);
              if (this.stringValue != null) {
                break label159;
              }
              bool1 = bool2;
            } while (((zzc)paramObject).stringValue != null);
            if (this.zzbRV != null) {
              break label175;
            }
            bool1 = bool2;
          } while (((zzc)paramObject).zzbRV != null);
          if (this.zzbQY != null) {
            break label191;
          }
          bool1 = bool2;
        } while (((zzc)paramObject).zzbQY != null);
        if (this.zzbQZ != null) {
          break label207;
        }
        bool1 = bool2;
      } while (((zzc)paramObject).zzbQZ != null);
      for (;;)
      {
        if ((this.FZ == null) || (this.FZ.isEmpty()))
        {
          if (((zzc)paramObject).FZ != null)
          {
            bool1 = bool2;
            if (!((zzc)paramObject).FZ.isEmpty()) {
              break;
            }
          }
          return true;
          if (this.name.equals(((zzc)paramObject).name)) {
            break label41;
          }
          return false;
          label159:
          if (this.stringValue.equals(((zzc)paramObject).stringValue)) {
            break label57;
          }
          return false;
          label175:
          if (this.zzbRV.equals(((zzc)paramObject).zzbRV)) {
            break label73;
          }
          return false;
          label191:
          if (this.zzbQY.equals(((zzc)paramObject).zzbQY)) {
            break label89;
          }
          return false;
          label207:
          if (!this.zzbQZ.equals(((zzc)paramObject).zzbQZ)) {
            return false;
          }
        }
      }
      return this.FZ.equals(((zzc)paramObject).FZ);
    }
    
    public int hashCode()
    {
      int i2 = 0;
      int i3 = getClass().getName().hashCode();
      int i;
      int j;
      label33:
      int k;
      label42:
      int m;
      label52:
      int n;
      if (this.name == null)
      {
        i = 0;
        if (this.stringValue != null) {
          break label138;
        }
        j = 0;
        if (this.zzbRV != null) {
          break label149;
        }
        k = 0;
        if (this.zzbQY != null) {
          break label160;
        }
        m = 0;
        if (this.zzbQZ != null) {
          break label172;
        }
        n = 0;
        label62:
        i1 = i2;
        if (this.FZ != null) {
          if (!this.FZ.isEmpty()) {
            break label184;
          }
        }
      }
      label138:
      label149:
      label160:
      label172:
      label184:
      for (int i1 = i2;; i1 = this.FZ.hashCode())
      {
        return (n + (m + (k + (j + (i + (i3 + 527) * 31) * 31) * 31) * 31) * 31) * 31 + i1;
        i = this.name.hashCode();
        break;
        j = this.stringValue.hashCode();
        break label33;
        k = this.zzbRV.hashCode();
        break label42;
        m = this.zzbQY.hashCode();
        break label52;
        n = this.zzbQZ.hashCode();
        break label62;
      }
    }
    
    public void writeTo(zzcfy paramzzcfy)
      throws IOException
    {
      if (this.name != null) {
        paramzzcfy.zzu(1, this.name);
      }
      if (this.stringValue != null) {
        paramzzcfy.zzu(2, this.stringValue);
      }
      if (this.zzbRV != null) {
        paramzzcfy.zzf(3, this.zzbRV.longValue());
      }
      if (this.zzbQY != null) {
        paramzzcfy.zzc(4, this.zzbQY.floatValue());
      }
      if (this.zzbQZ != null) {
        paramzzcfy.zza(5, this.zzbQZ.doubleValue());
      }
      super.writeTo(paramzzcfy);
    }
    
    public zzc zzPy()
    {
      this.name = null;
      this.stringValue = null;
      this.zzbRV = null;
      this.zzbQY = null;
      this.zzbQZ = null;
      this.FZ = null;
      this.Gi = -1;
      return this;
    }
    
    public zzc zzU(zzcfx paramzzcfx)
      throws IOException
    {
      for (;;)
      {
        int i = paramzzcfx.zzamI();
        switch (i)
        {
        default: 
          if (super.zza(paramzzcfx, i)) {}
          break;
        case 0: 
          return this;
        case 10: 
          this.name = paramzzcfx.readString();
          break;
        case 18: 
          this.stringValue = paramzzcfx.readString();
          break;
        case 24: 
          this.zzbRV = Long.valueOf(paramzzcfx.zzamL());
          break;
        case 37: 
          this.zzbQY = Float.valueOf(paramzzcfx.readFloat());
          break;
        case 41: 
          this.zzbQZ = Double.valueOf(paramzzcfx.readDouble());
        }
      }
    }
  }
  
  public static final class zzd
    extends zzcfz<zzd>
  {
    public zzauz.zze[] zzbRW;
    
    public zzd()
    {
      zzPz();
    }
    
    protected int computeSerializedSize()
    {
      int i = super.computeSerializedSize();
      int k = i;
      if (this.zzbRW != null)
      {
        k = i;
        if (this.zzbRW.length > 0)
        {
          int j = 0;
          for (;;)
          {
            k = i;
            if (j >= this.zzbRW.length) {
              break;
            }
            zzauz.zze localzze = this.zzbRW[j];
            k = i;
            if (localzze != null) {
              k = i + zzcfy.zzc(1, localzze);
            }
            j += 1;
            i = k;
          }
        }
      }
      return k;
    }
    
    public boolean equals(Object paramObject)
    {
      boolean bool2 = false;
      boolean bool1;
      if (paramObject == this) {
        bool1 = true;
      }
      do
      {
        do
        {
          do
          {
            return bool1;
            bool1 = bool2;
          } while (!(paramObject instanceof zzd));
          paramObject = (zzd)paramObject;
          bool1 = bool2;
        } while (!zzcge.equals(this.zzbRW, ((zzd)paramObject).zzbRW));
        if ((this.FZ != null) && (!this.FZ.isEmpty())) {
          break label79;
        }
        if (((zzd)paramObject).FZ == null) {
          break;
        }
        bool1 = bool2;
      } while (!((zzd)paramObject).FZ.isEmpty());
      return true;
      label79:
      return this.FZ.equals(((zzd)paramObject).FZ);
    }
    
    public int hashCode()
    {
      int j = getClass().getName().hashCode();
      int k = zzcge.hashCode(this.zzbRW);
      if ((this.FZ == null) || (this.FZ.isEmpty())) {}
      for (int i = 0;; i = this.FZ.hashCode()) {
        return i + ((j + 527) * 31 + k) * 31;
      }
    }
    
    public void writeTo(zzcfy paramzzcfy)
      throws IOException
    {
      if ((this.zzbRW != null) && (this.zzbRW.length > 0))
      {
        int i = 0;
        while (i < this.zzbRW.length)
        {
          zzauz.zze localzze = this.zzbRW[i];
          if (localzze != null) {
            paramzzcfy.zza(1, localzze);
          }
          i += 1;
        }
      }
      super.writeTo(paramzzcfy);
    }
    
    public zzd zzPz()
    {
      this.zzbRW = zzauz.zze.zzPA();
      this.FZ = null;
      this.Gi = -1;
      return this;
    }
    
    public zzd zzV(zzcfx paramzzcfx)
      throws IOException
    {
      for (;;)
      {
        int i = paramzzcfx.zzamI();
        switch (i)
        {
        default: 
          if (super.zza(paramzzcfx, i)) {}
          break;
        case 0: 
          return this;
        case 10: 
          int j = zzcgj.zzb(paramzzcfx, 10);
          if (this.zzbRW == null) {}
          zzauz.zze[] arrayOfzze;
          for (i = 0;; i = this.zzbRW.length)
          {
            arrayOfzze = new zzauz.zze[j + i];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.zzbRW, 0, arrayOfzze, 0, i);
              j = i;
            }
            while (j < arrayOfzze.length - 1)
            {
              arrayOfzze[j] = new zzauz.zze();
              paramzzcfx.zza(arrayOfzze[j]);
              paramzzcfx.zzamI();
              j += 1;
            }
          }
          arrayOfzze[j] = new zzauz.zze();
          paramzzcfx.zza(arrayOfzze[j]);
          this.zzbRW = arrayOfzze;
        }
      }
    }
  }
  
  public static final class zze
    extends zzcfz<zze>
  {
    private static volatile zze[] zzbRX;
    public String zzaR;
    public String zzbAI;
    public String zzbLI;
    public String zzbLJ;
    public String zzbLM;
    public String zzbLQ;
    public Integer zzbRY;
    public zzauz.zzb[] zzbRZ;
    public zzauz.zzg[] zzbSa;
    public Long zzbSb;
    public Long zzbSc;
    public Long zzbSd;
    public Long zzbSe;
    public Long zzbSf;
    public String zzbSg;
    public String zzbSh;
    public String zzbSi;
    public Integer zzbSj;
    public Long zzbSk;
    public Long zzbSl;
    public String zzbSm;
    public Boolean zzbSn;
    public String zzbSo;
    public Long zzbSp;
    public Integer zzbSq;
    public Boolean zzbSr;
    public zzauz.zza[] zzbSs;
    public Integer zzbSt;
    public Integer zzbSu;
    public Integer zzbSv;
    public String zzbSw;
    public Long zzbSx;
    public Long zzbSy;
    public String zzba;
    
    public zze()
    {
      zzPB();
    }
    
    public static zze[] zzPA()
    {
      if (zzbRX == null) {}
      synchronized (zzcge.Gh)
      {
        if (zzbRX == null) {
          zzbRX = new zze[0];
        }
        return zzbRX;
      }
    }
    
    protected int computeSerializedSize()
    {
      int m = 0;
      int j = super.computeSerializedSize();
      int i = j;
      if (this.zzbRY != null) {
        i = j + zzcfy.zzac(1, this.zzbRY.intValue());
      }
      j = i;
      Object localObject;
      if (this.zzbRZ != null)
      {
        j = i;
        if (this.zzbRZ.length > 0)
        {
          j = 0;
          while (j < this.zzbRZ.length)
          {
            localObject = this.zzbRZ[j];
            k = i;
            if (localObject != null) {
              k = i + zzcfy.zzc(2, (zzcgg)localObject);
            }
            j += 1;
            i = k;
          }
          j = i;
        }
      }
      i = j;
      if (this.zzbSa != null)
      {
        i = j;
        if (this.zzbSa.length > 0)
        {
          i = j;
          j = 0;
          while (j < this.zzbSa.length)
          {
            localObject = this.zzbSa[j];
            k = i;
            if (localObject != null) {
              k = i + zzcfy.zzc(3, (zzcgg)localObject);
            }
            j += 1;
            i = k;
          }
        }
      }
      j = i;
      if (this.zzbSb != null) {
        j = i + zzcfy.zzj(4, this.zzbSb.longValue());
      }
      i = j;
      if (this.zzbSc != null) {
        i = j + zzcfy.zzj(5, this.zzbSc.longValue());
      }
      j = i;
      if (this.zzbSd != null) {
        j = i + zzcfy.zzj(6, this.zzbSd.longValue());
      }
      i = j;
      if (this.zzbSf != null) {
        i = j + zzcfy.zzj(7, this.zzbSf.longValue());
      }
      j = i;
      if (this.zzbSg != null) {
        j = i + zzcfy.zzv(8, this.zzbSg);
      }
      i = j;
      if (this.zzba != null) {
        i = j + zzcfy.zzv(9, this.zzba);
      }
      j = i;
      if (this.zzbSh != null) {
        j = i + zzcfy.zzv(10, this.zzbSh);
      }
      i = j;
      if (this.zzbSi != null) {
        i = j + zzcfy.zzv(11, this.zzbSi);
      }
      j = i;
      if (this.zzbSj != null) {
        j = i + zzcfy.zzac(12, this.zzbSj.intValue());
      }
      i = j;
      if (this.zzbLJ != null) {
        i = j + zzcfy.zzv(13, this.zzbLJ);
      }
      j = i;
      if (this.zzaR != null) {
        j = i + zzcfy.zzv(14, this.zzaR);
      }
      i = j;
      if (this.zzbAI != null) {
        i = j + zzcfy.zzv(16, this.zzbAI);
      }
      j = i;
      if (this.zzbSk != null) {
        j = i + zzcfy.zzj(17, this.zzbSk.longValue());
      }
      i = j;
      if (this.zzbSl != null) {
        i = j + zzcfy.zzj(18, this.zzbSl.longValue());
      }
      j = i;
      if (this.zzbSm != null) {
        j = i + zzcfy.zzv(19, this.zzbSm);
      }
      i = j;
      if (this.zzbSn != null) {
        i = j + zzcfy.zzl(20, this.zzbSn.booleanValue());
      }
      j = i;
      if (this.zzbSo != null) {
        j = i + zzcfy.zzv(21, this.zzbSo);
      }
      i = j;
      if (this.zzbSp != null) {
        i = j + zzcfy.zzj(22, this.zzbSp.longValue());
      }
      j = i;
      if (this.zzbSq != null) {
        j = i + zzcfy.zzac(23, this.zzbSq.intValue());
      }
      i = j;
      if (this.zzbLM != null) {
        i = j + zzcfy.zzv(24, this.zzbLM);
      }
      j = i;
      if (this.zzbLI != null) {
        j = i + zzcfy.zzv(25, this.zzbLI);
      }
      int k = j;
      if (this.zzbSe != null) {
        k = j + zzcfy.zzj(26, this.zzbSe.longValue());
      }
      i = k;
      if (this.zzbSr != null) {
        i = k + zzcfy.zzl(28, this.zzbSr.booleanValue());
      }
      j = i;
      if (this.zzbSs != null)
      {
        j = i;
        if (this.zzbSs.length > 0)
        {
          k = m;
          for (;;)
          {
            j = i;
            if (k >= this.zzbSs.length) {
              break;
            }
            localObject = this.zzbSs[k];
            j = i;
            if (localObject != null) {
              j = i + zzcfy.zzc(29, (zzcgg)localObject);
            }
            k += 1;
            i = j;
          }
        }
      }
      i = j;
      if (this.zzbLQ != null) {
        i = j + zzcfy.zzv(30, this.zzbLQ);
      }
      j = i;
      if (this.zzbSt != null) {
        j = i + zzcfy.zzac(31, this.zzbSt.intValue());
      }
      i = j;
      if (this.zzbSu != null) {
        i = j + zzcfy.zzac(32, this.zzbSu.intValue());
      }
      j = i;
      if (this.zzbSv != null) {
        j = i + zzcfy.zzac(33, this.zzbSv.intValue());
      }
      i = j;
      if (this.zzbSw != null) {
        i = j + zzcfy.zzv(34, this.zzbSw);
      }
      j = i;
      if (this.zzbSx != null) {
        j = i + zzcfy.zzj(35, this.zzbSx.longValue());
      }
      i = j;
      if (this.zzbSy != null) {
        i = j + zzcfy.zzj(36, this.zzbSy.longValue());
      }
      return i;
    }
    
    public boolean equals(Object paramObject)
    {
      boolean bool2 = false;
      boolean bool1;
      if (paramObject == this) {
        bool1 = true;
      }
      label41:
      label89:
      label105:
      label121:
      label137:
      label153:
      label169:
      label185:
      label201:
      label217:
      label233:
      label249:
      label265:
      label281:
      label297:
      label313:
      label329:
      label345:
      label361:
      label377:
      label393:
      label409:
      label425:
      label441:
      label473:
      label489:
      label505:
      label521:
      label537:
      label553:
      do
      {
        do
        {
          do
          {
            do
            {
              do
              {
                do
                {
                  do
                  {
                    do
                    {
                      do
                      {
                        do
                        {
                          do
                          {
                            do
                            {
                              do
                              {
                                do
                                {
                                  do
                                  {
                                    do
                                    {
                                      do
                                      {
                                        do
                                        {
                                          do
                                          {
                                            do
                                            {
                                              do
                                              {
                                                do
                                                {
                                                  do
                                                  {
                                                    do
                                                    {
                                                      do
                                                      {
                                                        do
                                                        {
                                                          do
                                                          {
                                                            do
                                                            {
                                                              do
                                                              {
                                                                do
                                                                {
                                                                  do
                                                                  {
                                                                    do
                                                                    {
                                                                      do
                                                                      {
                                                                        do
                                                                        {
                                                                          do
                                                                          {
                                                                            return bool1;
                                                                            bool1 = bool2;
                                                                          } while (!(paramObject instanceof zze));
                                                                          paramObject = (zze)paramObject;
                                                                          if (this.zzbRY != null) {
                                                                            break;
                                                                          }
                                                                          bool1 = bool2;
                                                                        } while (((zze)paramObject).zzbRY != null);
                                                                        bool1 = bool2;
                                                                      } while (!zzcge.equals(this.zzbRZ, ((zze)paramObject).zzbRZ));
                                                                      bool1 = bool2;
                                                                    } while (!zzcge.equals(this.zzbSa, ((zze)paramObject).zzbSa));
                                                                    if (this.zzbSb != null) {
                                                                      break label623;
                                                                    }
                                                                    bool1 = bool2;
                                                                  } while (((zze)paramObject).zzbSb != null);
                                                                  if (this.zzbSc != null) {
                                                                    break label639;
                                                                  }
                                                                  bool1 = bool2;
                                                                } while (((zze)paramObject).zzbSc != null);
                                                                if (this.zzbSd != null) {
                                                                  break label655;
                                                                }
                                                                bool1 = bool2;
                                                              } while (((zze)paramObject).zzbSd != null);
                                                              if (this.zzbSe != null) {
                                                                break label671;
                                                              }
                                                              bool1 = bool2;
                                                            } while (((zze)paramObject).zzbSe != null);
                                                            if (this.zzbSf != null) {
                                                              break label687;
                                                            }
                                                            bool1 = bool2;
                                                          } while (((zze)paramObject).zzbSf != null);
                                                          if (this.zzbSg != null) {
                                                            break label703;
                                                          }
                                                          bool1 = bool2;
                                                        } while (((zze)paramObject).zzbSg != null);
                                                        if (this.zzba != null) {
                                                          break label719;
                                                        }
                                                        bool1 = bool2;
                                                      } while (((zze)paramObject).zzba != null);
                                                      if (this.zzbSh != null) {
                                                        break label735;
                                                      }
                                                      bool1 = bool2;
                                                    } while (((zze)paramObject).zzbSh != null);
                                                    if (this.zzbSi != null) {
                                                      break label751;
                                                    }
                                                    bool1 = bool2;
                                                  } while (((zze)paramObject).zzbSi != null);
                                                  if (this.zzbSj != null) {
                                                    break label767;
                                                  }
                                                  bool1 = bool2;
                                                } while (((zze)paramObject).zzbSj != null);
                                                if (this.zzbLJ != null) {
                                                  break label783;
                                                }
                                                bool1 = bool2;
                                              } while (((zze)paramObject).zzbLJ != null);
                                              if (this.zzaR != null) {
                                                break label799;
                                              }
                                              bool1 = bool2;
                                            } while (((zze)paramObject).zzaR != null);
                                            if (this.zzbAI != null) {
                                              break label815;
                                            }
                                            bool1 = bool2;
                                          } while (((zze)paramObject).zzbAI != null);
                                          if (this.zzbSk != null) {
                                            break label831;
                                          }
                                          bool1 = bool2;
                                        } while (((zze)paramObject).zzbSk != null);
                                        if (this.zzbSl != null) {
                                          break label847;
                                        }
                                        bool1 = bool2;
                                      } while (((zze)paramObject).zzbSl != null);
                                      if (this.zzbSm != null) {
                                        break label863;
                                      }
                                      bool1 = bool2;
                                    } while (((zze)paramObject).zzbSm != null);
                                    if (this.zzbSn != null) {
                                      break label879;
                                    }
                                    bool1 = bool2;
                                  } while (((zze)paramObject).zzbSn != null);
                                  if (this.zzbSo != null) {
                                    break label895;
                                  }
                                  bool1 = bool2;
                                } while (((zze)paramObject).zzbSo != null);
                                if (this.zzbSp != null) {
                                  break label911;
                                }
                                bool1 = bool2;
                              } while (((zze)paramObject).zzbSp != null);
                              if (this.zzbSq != null) {
                                break label927;
                              }
                              bool1 = bool2;
                            } while (((zze)paramObject).zzbSq != null);
                            if (this.zzbLM != null) {
                              break label943;
                            }
                            bool1 = bool2;
                          } while (((zze)paramObject).zzbLM != null);
                          if (this.zzbLI != null) {
                            break label959;
                          }
                          bool1 = bool2;
                        } while (((zze)paramObject).zzbLI != null);
                        if (this.zzbSr != null) {
                          break label975;
                        }
                        bool1 = bool2;
                      } while (((zze)paramObject).zzbSr != null);
                      bool1 = bool2;
                    } while (!zzcge.equals(this.zzbSs, ((zze)paramObject).zzbSs));
                    if (this.zzbLQ != null) {
                      break label991;
                    }
                    bool1 = bool2;
                  } while (((zze)paramObject).zzbLQ != null);
                  if (this.zzbSt != null) {
                    break label1007;
                  }
                  bool1 = bool2;
                } while (((zze)paramObject).zzbSt != null);
                if (this.zzbSu != null) {
                  break label1023;
                }
                bool1 = bool2;
              } while (((zze)paramObject).zzbSu != null);
              if (this.zzbSv != null) {
                break label1039;
              }
              bool1 = bool2;
            } while (((zze)paramObject).zzbSv != null);
            if (this.zzbSw != null) {
              break label1055;
            }
            bool1 = bool2;
          } while (((zze)paramObject).zzbSw != null);
          if (this.zzbSx != null) {
            break label1071;
          }
          bool1 = bool2;
        } while (((zze)paramObject).zzbSx != null);
        if (this.zzbSy != null) {
          break label1087;
        }
        bool1 = bool2;
      } while (((zze)paramObject).zzbSy != null);
      for (;;)
      {
        if ((this.FZ == null) || (this.FZ.isEmpty()))
        {
          if (((zze)paramObject).FZ != null)
          {
            bool1 = bool2;
            if (!((zze)paramObject).FZ.isEmpty()) {
              break;
            }
          }
          return true;
          if (this.zzbRY.equals(((zze)paramObject).zzbRY)) {
            break label41;
          }
          return false;
          label623:
          if (this.zzbSb.equals(((zze)paramObject).zzbSb)) {
            break label89;
          }
          return false;
          label639:
          if (this.zzbSc.equals(((zze)paramObject).zzbSc)) {
            break label105;
          }
          return false;
          label655:
          if (this.zzbSd.equals(((zze)paramObject).zzbSd)) {
            break label121;
          }
          return false;
          label671:
          if (this.zzbSe.equals(((zze)paramObject).zzbSe)) {
            break label137;
          }
          return false;
          label687:
          if (this.zzbSf.equals(((zze)paramObject).zzbSf)) {
            break label153;
          }
          return false;
          label703:
          if (this.zzbSg.equals(((zze)paramObject).zzbSg)) {
            break label169;
          }
          return false;
          label719:
          if (this.zzba.equals(((zze)paramObject).zzba)) {
            break label185;
          }
          return false;
          label735:
          if (this.zzbSh.equals(((zze)paramObject).zzbSh)) {
            break label201;
          }
          return false;
          label751:
          if (this.zzbSi.equals(((zze)paramObject).zzbSi)) {
            break label217;
          }
          return false;
          label767:
          if (this.zzbSj.equals(((zze)paramObject).zzbSj)) {
            break label233;
          }
          return false;
          label783:
          if (this.zzbLJ.equals(((zze)paramObject).zzbLJ)) {
            break label249;
          }
          return false;
          label799:
          if (this.zzaR.equals(((zze)paramObject).zzaR)) {
            break label265;
          }
          return false;
          label815:
          if (this.zzbAI.equals(((zze)paramObject).zzbAI)) {
            break label281;
          }
          return false;
          label831:
          if (this.zzbSk.equals(((zze)paramObject).zzbSk)) {
            break label297;
          }
          return false;
          label847:
          if (this.zzbSl.equals(((zze)paramObject).zzbSl)) {
            break label313;
          }
          return false;
          label863:
          if (this.zzbSm.equals(((zze)paramObject).zzbSm)) {
            break label329;
          }
          return false;
          label879:
          if (this.zzbSn.equals(((zze)paramObject).zzbSn)) {
            break label345;
          }
          return false;
          label895:
          if (this.zzbSo.equals(((zze)paramObject).zzbSo)) {
            break label361;
          }
          return false;
          label911:
          if (this.zzbSp.equals(((zze)paramObject).zzbSp)) {
            break label377;
          }
          return false;
          label927:
          if (this.zzbSq.equals(((zze)paramObject).zzbSq)) {
            break label393;
          }
          return false;
          label943:
          if (this.zzbLM.equals(((zze)paramObject).zzbLM)) {
            break label409;
          }
          return false;
          label959:
          if (this.zzbLI.equals(((zze)paramObject).zzbLI)) {
            break label425;
          }
          return false;
          label975:
          if (this.zzbSr.equals(((zze)paramObject).zzbSr)) {
            break label441;
          }
          return false;
          label991:
          if (this.zzbLQ.equals(((zze)paramObject).zzbLQ)) {
            break label473;
          }
          return false;
          label1007:
          if (this.zzbSt.equals(((zze)paramObject).zzbSt)) {
            break label489;
          }
          return false;
          label1023:
          if (this.zzbSu.equals(((zze)paramObject).zzbSu)) {
            break label505;
          }
          return false;
          label1039:
          if (this.zzbSv.equals(((zze)paramObject).zzbSv)) {
            break label521;
          }
          return false;
          label1055:
          if (this.zzbSw.equals(((zze)paramObject).zzbSw)) {
            break label537;
          }
          return false;
          label1071:
          if (this.zzbSx.equals(((zze)paramObject).zzbSx)) {
            break label553;
          }
          return false;
          label1087:
          if (!this.zzbSy.equals(((zze)paramObject).zzbSy)) {
            return false;
          }
        }
      }
      return this.FZ.equals(((zze)paramObject).FZ);
    }
    
    public int hashCode()
    {
      int i28 = 0;
      int i29 = getClass().getName().hashCode();
      int i;
      int i30;
      int i31;
      int j;
      label51:
      int k;
      label60:
      int m;
      label70:
      int n;
      label80:
      int i1;
      label90:
      int i2;
      label100:
      int i3;
      label110:
      int i4;
      label120:
      int i5;
      label130:
      int i6;
      label140:
      int i7;
      label150:
      int i8;
      label160:
      int i9;
      label170:
      int i10;
      label180:
      int i11;
      label190:
      int i12;
      label200:
      int i13;
      label210:
      int i14;
      label220:
      int i15;
      label230:
      int i16;
      label240:
      int i17;
      label250:
      int i18;
      label260:
      int i19;
      label270:
      int i32;
      int i20;
      label289:
      int i21;
      label299:
      int i22;
      label309:
      int i23;
      label319:
      int i24;
      label329:
      int i25;
      label339:
      int i26;
      if (this.zzbRY == null)
      {
        i = 0;
        i30 = zzcge.hashCode(this.zzbRZ);
        i31 = zzcge.hashCode(this.zzbSa);
        if (this.zzbSb != null) {
          break label599;
        }
        j = 0;
        if (this.zzbSc != null) {
          break label610;
        }
        k = 0;
        if (this.zzbSd != null) {
          break label621;
        }
        m = 0;
        if (this.zzbSe != null) {
          break label633;
        }
        n = 0;
        if (this.zzbSf != null) {
          break label645;
        }
        i1 = 0;
        if (this.zzbSg != null) {
          break label657;
        }
        i2 = 0;
        if (this.zzba != null) {
          break label669;
        }
        i3 = 0;
        if (this.zzbSh != null) {
          break label681;
        }
        i4 = 0;
        if (this.zzbSi != null) {
          break label693;
        }
        i5 = 0;
        if (this.zzbSj != null) {
          break label705;
        }
        i6 = 0;
        if (this.zzbLJ != null) {
          break label717;
        }
        i7 = 0;
        if (this.zzaR != null) {
          break label729;
        }
        i8 = 0;
        if (this.zzbAI != null) {
          break label741;
        }
        i9 = 0;
        if (this.zzbSk != null) {
          break label753;
        }
        i10 = 0;
        if (this.zzbSl != null) {
          break label765;
        }
        i11 = 0;
        if (this.zzbSm != null) {
          break label777;
        }
        i12 = 0;
        if (this.zzbSn != null) {
          break label789;
        }
        i13 = 0;
        if (this.zzbSo != null) {
          break label801;
        }
        i14 = 0;
        if (this.zzbSp != null) {
          break label813;
        }
        i15 = 0;
        if (this.zzbSq != null) {
          break label825;
        }
        i16 = 0;
        if (this.zzbLM != null) {
          break label837;
        }
        i17 = 0;
        if (this.zzbLI != null) {
          break label849;
        }
        i18 = 0;
        if (this.zzbSr != null) {
          break label861;
        }
        i19 = 0;
        i32 = zzcge.hashCode(this.zzbSs);
        if (this.zzbLQ != null) {
          break label873;
        }
        i20 = 0;
        if (this.zzbSt != null) {
          break label885;
        }
        i21 = 0;
        if (this.zzbSu != null) {
          break label897;
        }
        i22 = 0;
        if (this.zzbSv != null) {
          break label909;
        }
        i23 = 0;
        if (this.zzbSw != null) {
          break label921;
        }
        i24 = 0;
        if (this.zzbSx != null) {
          break label933;
        }
        i25 = 0;
        if (this.zzbSy != null) {
          break label945;
        }
        i26 = 0;
        label349:
        i27 = i28;
        if (this.FZ != null) {
          if (!this.FZ.isEmpty()) {
            break label957;
          }
        }
      }
      label599:
      label610:
      label621:
      label633:
      label645:
      label657:
      label669:
      label681:
      label693:
      label705:
      label717:
      label729:
      label741:
      label753:
      label765:
      label777:
      label789:
      label801:
      label813:
      label825:
      label837:
      label849:
      label861:
      label873:
      label885:
      label897:
      label909:
      label921:
      label933:
      label945:
      label957:
      for (int i27 = i28;; i27 = this.FZ.hashCode())
      {
        return (i26 + (i25 + (i24 + (i23 + (i22 + (i21 + (i20 + ((i19 + (i18 + (i17 + (i16 + (i15 + (i14 + (i13 + (i12 + (i11 + (i10 + (i9 + (i8 + (i7 + (i6 + (i5 + (i4 + (i3 + (i2 + (i1 + (n + (m + (k + (j + (((i + (i29 + 527) * 31) * 31 + i30) * 31 + i31) * 31) * 31) * 31) * 31) * 31) * 31) * 31) * 31) * 31) * 31) * 31) * 31) * 31) * 31) * 31) * 31) * 31) * 31) * 31) * 31) * 31) * 31) * 31) * 31 + i32) * 31) * 31) * 31) * 31) * 31) * 31) * 31) * 31 + i27;
        i = this.zzbRY.hashCode();
        break;
        j = this.zzbSb.hashCode();
        break label51;
        k = this.zzbSc.hashCode();
        break label60;
        m = this.zzbSd.hashCode();
        break label70;
        n = this.zzbSe.hashCode();
        break label80;
        i1 = this.zzbSf.hashCode();
        break label90;
        i2 = this.zzbSg.hashCode();
        break label100;
        i3 = this.zzba.hashCode();
        break label110;
        i4 = this.zzbSh.hashCode();
        break label120;
        i5 = this.zzbSi.hashCode();
        break label130;
        i6 = this.zzbSj.hashCode();
        break label140;
        i7 = this.zzbLJ.hashCode();
        break label150;
        i8 = this.zzaR.hashCode();
        break label160;
        i9 = this.zzbAI.hashCode();
        break label170;
        i10 = this.zzbSk.hashCode();
        break label180;
        i11 = this.zzbSl.hashCode();
        break label190;
        i12 = this.zzbSm.hashCode();
        break label200;
        i13 = this.zzbSn.hashCode();
        break label210;
        i14 = this.zzbSo.hashCode();
        break label220;
        i15 = this.zzbSp.hashCode();
        break label230;
        i16 = this.zzbSq.hashCode();
        break label240;
        i17 = this.zzbLM.hashCode();
        break label250;
        i18 = this.zzbLI.hashCode();
        break label260;
        i19 = this.zzbSr.hashCode();
        break label270;
        i20 = this.zzbLQ.hashCode();
        break label289;
        i21 = this.zzbSt.hashCode();
        break label299;
        i22 = this.zzbSu.hashCode();
        break label309;
        i23 = this.zzbSv.hashCode();
        break label319;
        i24 = this.zzbSw.hashCode();
        break label329;
        i25 = this.zzbSx.hashCode();
        break label339;
        i26 = this.zzbSy.hashCode();
        break label349;
      }
    }
    
    public void writeTo(zzcfy paramzzcfy)
      throws IOException
    {
      int j = 0;
      if (this.zzbRY != null) {
        paramzzcfy.zzaa(1, this.zzbRY.intValue());
      }
      int i;
      Object localObject;
      if ((this.zzbRZ != null) && (this.zzbRZ.length > 0))
      {
        i = 0;
        while (i < this.zzbRZ.length)
        {
          localObject = this.zzbRZ[i];
          if (localObject != null) {
            paramzzcfy.zza(2, (zzcgg)localObject);
          }
          i += 1;
        }
      }
      if ((this.zzbSa != null) && (this.zzbSa.length > 0))
      {
        i = 0;
        while (i < this.zzbSa.length)
        {
          localObject = this.zzbSa[i];
          if (localObject != null) {
            paramzzcfy.zza(3, (zzcgg)localObject);
          }
          i += 1;
        }
      }
      if (this.zzbSb != null) {
        paramzzcfy.zzf(4, this.zzbSb.longValue());
      }
      if (this.zzbSc != null) {
        paramzzcfy.zzf(5, this.zzbSc.longValue());
      }
      if (this.zzbSd != null) {
        paramzzcfy.zzf(6, this.zzbSd.longValue());
      }
      if (this.zzbSf != null) {
        paramzzcfy.zzf(7, this.zzbSf.longValue());
      }
      if (this.zzbSg != null) {
        paramzzcfy.zzu(8, this.zzbSg);
      }
      if (this.zzba != null) {
        paramzzcfy.zzu(9, this.zzba);
      }
      if (this.zzbSh != null) {
        paramzzcfy.zzu(10, this.zzbSh);
      }
      if (this.zzbSi != null) {
        paramzzcfy.zzu(11, this.zzbSi);
      }
      if (this.zzbSj != null) {
        paramzzcfy.zzaa(12, this.zzbSj.intValue());
      }
      if (this.zzbLJ != null) {
        paramzzcfy.zzu(13, this.zzbLJ);
      }
      if (this.zzaR != null) {
        paramzzcfy.zzu(14, this.zzaR);
      }
      if (this.zzbAI != null) {
        paramzzcfy.zzu(16, this.zzbAI);
      }
      if (this.zzbSk != null) {
        paramzzcfy.zzf(17, this.zzbSk.longValue());
      }
      if (this.zzbSl != null) {
        paramzzcfy.zzf(18, this.zzbSl.longValue());
      }
      if (this.zzbSm != null) {
        paramzzcfy.zzu(19, this.zzbSm);
      }
      if (this.zzbSn != null) {
        paramzzcfy.zzk(20, this.zzbSn.booleanValue());
      }
      if (this.zzbSo != null) {
        paramzzcfy.zzu(21, this.zzbSo);
      }
      if (this.zzbSp != null) {
        paramzzcfy.zzf(22, this.zzbSp.longValue());
      }
      if (this.zzbSq != null) {
        paramzzcfy.zzaa(23, this.zzbSq.intValue());
      }
      if (this.zzbLM != null) {
        paramzzcfy.zzu(24, this.zzbLM);
      }
      if (this.zzbLI != null) {
        paramzzcfy.zzu(25, this.zzbLI);
      }
      if (this.zzbSe != null) {
        paramzzcfy.zzf(26, this.zzbSe.longValue());
      }
      if (this.zzbSr != null) {
        paramzzcfy.zzk(28, this.zzbSr.booleanValue());
      }
      if ((this.zzbSs != null) && (this.zzbSs.length > 0))
      {
        i = j;
        while (i < this.zzbSs.length)
        {
          localObject = this.zzbSs[i];
          if (localObject != null) {
            paramzzcfy.zza(29, (zzcgg)localObject);
          }
          i += 1;
        }
      }
      if (this.zzbLQ != null) {
        paramzzcfy.zzu(30, this.zzbLQ);
      }
      if (this.zzbSt != null) {
        paramzzcfy.zzaa(31, this.zzbSt.intValue());
      }
      if (this.zzbSu != null) {
        paramzzcfy.zzaa(32, this.zzbSu.intValue());
      }
      if (this.zzbSv != null) {
        paramzzcfy.zzaa(33, this.zzbSv.intValue());
      }
      if (this.zzbSw != null) {
        paramzzcfy.zzu(34, this.zzbSw);
      }
      if (this.zzbSx != null) {
        paramzzcfy.zzf(35, this.zzbSx.longValue());
      }
      if (this.zzbSy != null) {
        paramzzcfy.zzf(36, this.zzbSy.longValue());
      }
      super.writeTo(paramzzcfy);
    }
    
    public zze zzPB()
    {
      this.zzbRY = null;
      this.zzbRZ = zzauz.zzb.zzPv();
      this.zzbSa = zzauz.zzg.zzPD();
      this.zzbSb = null;
      this.zzbSc = null;
      this.zzbSd = null;
      this.zzbSe = null;
      this.zzbSf = null;
      this.zzbSg = null;
      this.zzba = null;
      this.zzbSh = null;
      this.zzbSi = null;
      this.zzbSj = null;
      this.zzbLJ = null;
      this.zzaR = null;
      this.zzbAI = null;
      this.zzbSk = null;
      this.zzbSl = null;
      this.zzbSm = null;
      this.zzbSn = null;
      this.zzbSo = null;
      this.zzbSp = null;
      this.zzbSq = null;
      this.zzbLM = null;
      this.zzbLI = null;
      this.zzbSr = null;
      this.zzbSs = zzauz.zza.zzPt();
      this.zzbLQ = null;
      this.zzbSt = null;
      this.zzbSu = null;
      this.zzbSv = null;
      this.zzbSw = null;
      this.zzbSx = null;
      this.zzbSy = null;
      this.FZ = null;
      this.Gi = -1;
      return this;
    }
    
    public zze zzW(zzcfx paramzzcfx)
      throws IOException
    {
      for (;;)
      {
        int i = paramzzcfx.zzamI();
        int j;
        Object localObject;
        switch (i)
        {
        default: 
          if (super.zza(paramzzcfx, i)) {}
          break;
        case 0: 
          return this;
        case 8: 
          this.zzbRY = Integer.valueOf(paramzzcfx.zzamM());
          break;
        case 18: 
          j = zzcgj.zzb(paramzzcfx, 18);
          if (this.zzbRZ == null) {}
          for (i = 0;; i = this.zzbRZ.length)
          {
            localObject = new zzauz.zzb[j + i];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.zzbRZ, 0, localObject, 0, i);
              j = i;
            }
            while (j < localObject.length - 1)
            {
              localObject[j] = new zzauz.zzb();
              paramzzcfx.zza(localObject[j]);
              paramzzcfx.zzamI();
              j += 1;
            }
          }
          localObject[j] = new zzauz.zzb();
          paramzzcfx.zza(localObject[j]);
          this.zzbRZ = ((zzauz.zzb[])localObject);
          break;
        case 26: 
          j = zzcgj.zzb(paramzzcfx, 26);
          if (this.zzbSa == null) {}
          for (i = 0;; i = this.zzbSa.length)
          {
            localObject = new zzauz.zzg[j + i];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.zzbSa, 0, localObject, 0, i);
              j = i;
            }
            while (j < localObject.length - 1)
            {
              localObject[j] = new zzauz.zzg();
              paramzzcfx.zza(localObject[j]);
              paramzzcfx.zzamI();
              j += 1;
            }
          }
          localObject[j] = new zzauz.zzg();
          paramzzcfx.zza(localObject[j]);
          this.zzbSa = ((zzauz.zzg[])localObject);
          break;
        case 32: 
          this.zzbSb = Long.valueOf(paramzzcfx.zzamL());
          break;
        case 40: 
          this.zzbSc = Long.valueOf(paramzzcfx.zzamL());
          break;
        case 48: 
          this.zzbSd = Long.valueOf(paramzzcfx.zzamL());
          break;
        case 56: 
          this.zzbSf = Long.valueOf(paramzzcfx.zzamL());
          break;
        case 66: 
          this.zzbSg = paramzzcfx.readString();
          break;
        case 74: 
          this.zzba = paramzzcfx.readString();
          break;
        case 82: 
          this.zzbSh = paramzzcfx.readString();
          break;
        case 90: 
          this.zzbSi = paramzzcfx.readString();
          break;
        case 96: 
          this.zzbSj = Integer.valueOf(paramzzcfx.zzamM());
          break;
        case 106: 
          this.zzbLJ = paramzzcfx.readString();
          break;
        case 114: 
          this.zzaR = paramzzcfx.readString();
          break;
        case 130: 
          this.zzbAI = paramzzcfx.readString();
          break;
        case 136: 
          this.zzbSk = Long.valueOf(paramzzcfx.zzamL());
          break;
        case 144: 
          this.zzbSl = Long.valueOf(paramzzcfx.zzamL());
          break;
        case 154: 
          this.zzbSm = paramzzcfx.readString();
          break;
        case 160: 
          this.zzbSn = Boolean.valueOf(paramzzcfx.zzamO());
          break;
        case 170: 
          this.zzbSo = paramzzcfx.readString();
          break;
        case 176: 
          this.zzbSp = Long.valueOf(paramzzcfx.zzamL());
          break;
        case 184: 
          this.zzbSq = Integer.valueOf(paramzzcfx.zzamM());
          break;
        case 194: 
          this.zzbLM = paramzzcfx.readString();
          break;
        case 202: 
          this.zzbLI = paramzzcfx.readString();
          break;
        case 208: 
          this.zzbSe = Long.valueOf(paramzzcfx.zzamL());
          break;
        case 224: 
          this.zzbSr = Boolean.valueOf(paramzzcfx.zzamO());
          break;
        case 234: 
          j = zzcgj.zzb(paramzzcfx, 234);
          if (this.zzbSs == null) {}
          for (i = 0;; i = this.zzbSs.length)
          {
            localObject = new zzauz.zza[j + i];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.zzbSs, 0, localObject, 0, i);
              j = i;
            }
            while (j < localObject.length - 1)
            {
              localObject[j] = new zzauz.zza();
              paramzzcfx.zza(localObject[j]);
              paramzzcfx.zzamI();
              j += 1;
            }
          }
          localObject[j] = new zzauz.zza();
          paramzzcfx.zza(localObject[j]);
          this.zzbSs = ((zzauz.zza[])localObject);
          break;
        case 242: 
          this.zzbLQ = paramzzcfx.readString();
          break;
        case 248: 
          this.zzbSt = Integer.valueOf(paramzzcfx.zzamM());
          break;
        case 256: 
          this.zzbSu = Integer.valueOf(paramzzcfx.zzamM());
          break;
        case 264: 
          this.zzbSv = Integer.valueOf(paramzzcfx.zzamM());
          break;
        case 274: 
          this.zzbSw = paramzzcfx.readString();
          break;
        case 280: 
          this.zzbSx = Long.valueOf(paramzzcfx.zzamL());
          break;
        case 288: 
          this.zzbSy = Long.valueOf(paramzzcfx.zzamL());
        }
      }
    }
  }
  
  public static final class zzf
    extends zzcfz<zzf>
  {
    public long[] zzbSA;
    public long[] zzbSz;
    
    public zzf()
    {
      zzPC();
    }
    
    protected int computeSerializedSize()
    {
      int m = 0;
      int k = super.computeSerializedSize();
      int j;
      if ((this.zzbSz != null) && (this.zzbSz.length > 0))
      {
        i = 0;
        j = 0;
        while (i < this.zzbSz.length)
        {
          j += zzcfy.zzbv(this.zzbSz[i]);
          i += 1;
        }
      }
      for (int i = k + j + this.zzbSz.length * 1;; i = k)
      {
        j = i;
        if (this.zzbSA != null)
        {
          j = i;
          if (this.zzbSA.length > 0)
          {
            k = 0;
            j = m;
            while (j < this.zzbSA.length)
            {
              k += zzcfy.zzbv(this.zzbSA[j]);
              j += 1;
            }
            j = i + k + this.zzbSA.length * 1;
          }
        }
        return j;
      }
    }
    
    public boolean equals(Object paramObject)
    {
      boolean bool2 = false;
      boolean bool1;
      if (paramObject == this) {
        bool1 = true;
      }
      do
      {
        do
        {
          do
          {
            do
            {
              return bool1;
              bool1 = bool2;
            } while (!(paramObject instanceof zzf));
            paramObject = (zzf)paramObject;
            bool1 = bool2;
          } while (!zzcge.equals(this.zzbSz, ((zzf)paramObject).zzbSz));
          bool1 = bool2;
        } while (!zzcge.equals(this.zzbSA, ((zzf)paramObject).zzbSA));
        if ((this.FZ != null) && (!this.FZ.isEmpty())) {
          break label95;
        }
        if (((zzf)paramObject).FZ == null) {
          break;
        }
        bool1 = bool2;
      } while (!((zzf)paramObject).FZ.isEmpty());
      return true;
      label95:
      return this.FZ.equals(((zzf)paramObject).FZ);
    }
    
    public int hashCode()
    {
      int j = getClass().getName().hashCode();
      int k = zzcge.hashCode(this.zzbSz);
      int m = zzcge.hashCode(this.zzbSA);
      if ((this.FZ == null) || (this.FZ.isEmpty())) {}
      for (int i = 0;; i = this.FZ.hashCode()) {
        return i + (((j + 527) * 31 + k) * 31 + m) * 31;
      }
    }
    
    public void writeTo(zzcfy paramzzcfy)
      throws IOException
    {
      int j = 0;
      int i;
      if ((this.zzbSz != null) && (this.zzbSz.length > 0))
      {
        i = 0;
        while (i < this.zzbSz.length)
        {
          paramzzcfy.zze(1, this.zzbSz[i]);
          i += 1;
        }
      }
      if ((this.zzbSA != null) && (this.zzbSA.length > 0))
      {
        i = j;
        while (i < this.zzbSA.length)
        {
          paramzzcfy.zze(2, this.zzbSA[i]);
          i += 1;
        }
      }
      super.writeTo(paramzzcfy);
    }
    
    public zzf zzPC()
    {
      this.zzbSz = zzcgj.Go;
      this.zzbSA = zzcgj.Go;
      this.FZ = null;
      this.Gi = -1;
      return this;
    }
    
    public zzf zzX(zzcfx paramzzcfx)
      throws IOException
    {
      for (;;)
      {
        int i = paramzzcfx.zzamI();
        int j;
        long[] arrayOfLong;
        int k;
        switch (i)
        {
        default: 
          if (super.zza(paramzzcfx, i)) {}
          break;
        case 0: 
          return this;
        case 8: 
          j = zzcgj.zzb(paramzzcfx, 8);
          if (this.zzbSz == null) {}
          for (i = 0;; i = this.zzbSz.length)
          {
            arrayOfLong = new long[j + i];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.zzbSz, 0, arrayOfLong, 0, i);
              j = i;
            }
            while (j < arrayOfLong.length - 1)
            {
              arrayOfLong[j] = paramzzcfx.zzamK();
              paramzzcfx.zzamI();
              j += 1;
            }
          }
          arrayOfLong[j] = paramzzcfx.zzamK();
          this.zzbSz = arrayOfLong;
          break;
        case 10: 
          k = paramzzcfx.zzBv(paramzzcfx.zzamR());
          i = paramzzcfx.getPosition();
          j = 0;
          while (paramzzcfx.zzamW() > 0)
          {
            paramzzcfx.zzamK();
            j += 1;
          }
          paramzzcfx.zzBx(i);
          if (this.zzbSz == null) {}
          for (i = 0;; i = this.zzbSz.length)
          {
            arrayOfLong = new long[j + i];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.zzbSz, 0, arrayOfLong, 0, i);
              j = i;
            }
            while (j < arrayOfLong.length)
            {
              arrayOfLong[j] = paramzzcfx.zzamK();
              j += 1;
            }
          }
          this.zzbSz = arrayOfLong;
          paramzzcfx.zzBw(k);
          break;
        case 16: 
          j = zzcgj.zzb(paramzzcfx, 16);
          if (this.zzbSA == null) {}
          for (i = 0;; i = this.zzbSA.length)
          {
            arrayOfLong = new long[j + i];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.zzbSA, 0, arrayOfLong, 0, i);
              j = i;
            }
            while (j < arrayOfLong.length - 1)
            {
              arrayOfLong[j] = paramzzcfx.zzamK();
              paramzzcfx.zzamI();
              j += 1;
            }
          }
          arrayOfLong[j] = paramzzcfx.zzamK();
          this.zzbSA = arrayOfLong;
          break;
        case 18: 
          k = paramzzcfx.zzBv(paramzzcfx.zzamR());
          i = paramzzcfx.getPosition();
          j = 0;
          while (paramzzcfx.zzamW() > 0)
          {
            paramzzcfx.zzamK();
            j += 1;
          }
          paramzzcfx.zzBx(i);
          if (this.zzbSA == null) {}
          for (i = 0;; i = this.zzbSA.length)
          {
            arrayOfLong = new long[j + i];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.zzbSA, 0, arrayOfLong, 0, i);
              j = i;
            }
            while (j < arrayOfLong.length)
            {
              arrayOfLong[j] = paramzzcfx.zzamK();
              j += 1;
            }
          }
          this.zzbSA = arrayOfLong;
          paramzzcfx.zzBw(k);
        }
      }
    }
  }
  
  public static final class zzg
    extends zzcfz<zzg>
  {
    private static volatile zzg[] zzbSB;
    public String name;
    public String stringValue;
    public Float zzbQY;
    public Double zzbQZ;
    public Long zzbRV;
    public Long zzbSC;
    
    public zzg()
    {
      zzPE();
    }
    
    public static zzg[] zzPD()
    {
      if (zzbSB == null) {}
      synchronized (zzcge.Gh)
      {
        if (zzbSB == null) {
          zzbSB = new zzg[0];
        }
        return zzbSB;
      }
    }
    
    protected int computeSerializedSize()
    {
      int j = super.computeSerializedSize();
      int i = j;
      if (this.zzbSC != null) {
        i = j + zzcfy.zzj(1, this.zzbSC.longValue());
      }
      j = i;
      if (this.name != null) {
        j = i + zzcfy.zzv(2, this.name);
      }
      i = j;
      if (this.stringValue != null) {
        i = j + zzcfy.zzv(3, this.stringValue);
      }
      j = i;
      if (this.zzbRV != null) {
        j = i + zzcfy.zzj(4, this.zzbRV.longValue());
      }
      i = j;
      if (this.zzbQY != null) {
        i = j + zzcfy.zzd(5, this.zzbQY.floatValue());
      }
      j = i;
      if (this.zzbQZ != null) {
        j = i + zzcfy.zzb(6, this.zzbQZ.doubleValue());
      }
      return j;
    }
    
    public boolean equals(Object paramObject)
    {
      boolean bool2 = false;
      boolean bool1;
      if (paramObject == this) {
        bool1 = true;
      }
      label41:
      label57:
      label73:
      label89:
      label105:
      do
      {
        do
        {
          do
          {
            do
            {
              do
              {
                do
                {
                  do
                  {
                    return bool1;
                    bool1 = bool2;
                  } while (!(paramObject instanceof zzg));
                  paramObject = (zzg)paramObject;
                  if (this.zzbSC != null) {
                    break;
                  }
                  bool1 = bool2;
                } while (((zzg)paramObject).zzbSC != null);
                if (this.name != null) {
                  break label175;
                }
                bool1 = bool2;
              } while (((zzg)paramObject).name != null);
              if (this.stringValue != null) {
                break label191;
              }
              bool1 = bool2;
            } while (((zzg)paramObject).stringValue != null);
            if (this.zzbRV != null) {
              break label207;
            }
            bool1 = bool2;
          } while (((zzg)paramObject).zzbRV != null);
          if (this.zzbQY != null) {
            break label223;
          }
          bool1 = bool2;
        } while (((zzg)paramObject).zzbQY != null);
        if (this.zzbQZ != null) {
          break label239;
        }
        bool1 = bool2;
      } while (((zzg)paramObject).zzbQZ != null);
      for (;;)
      {
        if ((this.FZ == null) || (this.FZ.isEmpty()))
        {
          if (((zzg)paramObject).FZ != null)
          {
            bool1 = bool2;
            if (!((zzg)paramObject).FZ.isEmpty()) {
              break;
            }
          }
          return true;
          if (this.zzbSC.equals(((zzg)paramObject).zzbSC)) {
            break label41;
          }
          return false;
          label175:
          if (this.name.equals(((zzg)paramObject).name)) {
            break label57;
          }
          return false;
          label191:
          if (this.stringValue.equals(((zzg)paramObject).stringValue)) {
            break label73;
          }
          return false;
          label207:
          if (this.zzbRV.equals(((zzg)paramObject).zzbRV)) {
            break label89;
          }
          return false;
          label223:
          if (this.zzbQY.equals(((zzg)paramObject).zzbQY)) {
            break label105;
          }
          return false;
          label239:
          if (!this.zzbQZ.equals(((zzg)paramObject).zzbQZ)) {
            return false;
          }
        }
      }
      return this.FZ.equals(((zzg)paramObject).FZ);
    }
    
    public int hashCode()
    {
      int i3 = 0;
      int i4 = getClass().getName().hashCode();
      int i;
      int j;
      label33:
      int k;
      label42:
      int m;
      label52:
      int n;
      label62:
      int i1;
      if (this.zzbSC == null)
      {
        i = 0;
        if (this.name != null) {
          break label154;
        }
        j = 0;
        if (this.stringValue != null) {
          break label165;
        }
        k = 0;
        if (this.zzbRV != null) {
          break label176;
        }
        m = 0;
        if (this.zzbQY != null) {
          break label188;
        }
        n = 0;
        if (this.zzbQZ != null) {
          break label200;
        }
        i1 = 0;
        label72:
        i2 = i3;
        if (this.FZ != null) {
          if (!this.FZ.isEmpty()) {
            break label212;
          }
        }
      }
      label154:
      label165:
      label176:
      label188:
      label200:
      label212:
      for (int i2 = i3;; i2 = this.FZ.hashCode())
      {
        return (i1 + (n + (m + (k + (j + (i + (i4 + 527) * 31) * 31) * 31) * 31) * 31) * 31) * 31 + i2;
        i = this.zzbSC.hashCode();
        break;
        j = this.name.hashCode();
        break label33;
        k = this.stringValue.hashCode();
        break label42;
        m = this.zzbRV.hashCode();
        break label52;
        n = this.zzbQY.hashCode();
        break label62;
        i1 = this.zzbQZ.hashCode();
        break label72;
      }
    }
    
    public void writeTo(zzcfy paramzzcfy)
      throws IOException
    {
      if (this.zzbSC != null) {
        paramzzcfy.zzf(1, this.zzbSC.longValue());
      }
      if (this.name != null) {
        paramzzcfy.zzu(2, this.name);
      }
      if (this.stringValue != null) {
        paramzzcfy.zzu(3, this.stringValue);
      }
      if (this.zzbRV != null) {
        paramzzcfy.zzf(4, this.zzbRV.longValue());
      }
      if (this.zzbQY != null) {
        paramzzcfy.zzc(5, this.zzbQY.floatValue());
      }
      if (this.zzbQZ != null) {
        paramzzcfy.zza(6, this.zzbQZ.doubleValue());
      }
      super.writeTo(paramzzcfy);
    }
    
    public zzg zzPE()
    {
      this.zzbSC = null;
      this.name = null;
      this.stringValue = null;
      this.zzbRV = null;
      this.zzbQY = null;
      this.zzbQZ = null;
      this.FZ = null;
      this.Gi = -1;
      return this;
    }
    
    public zzg zzY(zzcfx paramzzcfx)
      throws IOException
    {
      for (;;)
      {
        int i = paramzzcfx.zzamI();
        switch (i)
        {
        default: 
          if (super.zza(paramzzcfx, i)) {}
          break;
        case 0: 
          return this;
        case 8: 
          this.zzbSC = Long.valueOf(paramzzcfx.zzamL());
          break;
        case 18: 
          this.name = paramzzcfx.readString();
          break;
        case 26: 
          this.stringValue = paramzzcfx.readString();
          break;
        case 32: 
          this.zzbRV = Long.valueOf(paramzzcfx.zzamL());
          break;
        case 45: 
          this.zzbQY = Float.valueOf(paramzzcfx.readFloat());
          break;
        case 49: 
          this.zzbQZ = Double.valueOf(paramzzcfx.readDouble());
        }
      }
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzauz.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */