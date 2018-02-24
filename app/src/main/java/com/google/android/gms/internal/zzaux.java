package com.google.android.gms.internal;

import java.io.IOException;

public abstract interface zzaux
{
  public static final class zza
    extends zzcfz<zza>
  {
    private static volatile zza[] zzbRd;
    public Integer zzbRe;
    public zzaux.zze[] zzbRf;
    public zzaux.zzb[] zzbRg;
    
    public zza()
    {
      zzPf();
    }
    
    public static zza[] zzPe()
    {
      if (zzbRd == null) {}
      synchronized (zzcge.Gh)
      {
        if (zzbRd == null) {
          zzbRd = new zza[0];
        }
        return zzbRd;
      }
    }
    
    protected int computeSerializedSize()
    {
      int m = 0;
      int i = super.computeSerializedSize();
      int j = i;
      if (this.zzbRe != null) {
        j = i + zzcfy.zzac(1, this.zzbRe.intValue());
      }
      i = j;
      Object localObject;
      if (this.zzbRf != null)
      {
        i = j;
        if (this.zzbRf.length > 0)
        {
          i = j;
          j = 0;
          while (j < this.zzbRf.length)
          {
            localObject = this.zzbRf[j];
            k = i;
            if (localObject != null) {
              k = i + zzcfy.zzc(2, (zzcgg)localObject);
            }
            j += 1;
            i = k;
          }
        }
      }
      int k = i;
      if (this.zzbRg != null)
      {
        k = i;
        if (this.zzbRg.length > 0)
        {
          j = m;
          for (;;)
          {
            k = i;
            if (j >= this.zzbRg.length) {
              break;
            }
            localObject = this.zzbRg[j];
            k = i;
            if (localObject != null) {
              k = i + zzcfy.zzc(3, (zzcgg)localObject);
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
          return bool1;
          bool1 = bool2;
        } while (!(paramObject instanceof zza));
        paramObject = (zza)paramObject;
        if (this.zzbRe != null) {
          break;
        }
        bool1 = bool2;
      } while (((zza)paramObject).zzbRe != null);
      while (this.zzbRe.equals(((zza)paramObject).zzbRe))
      {
        bool1 = bool2;
        if (!zzcge.equals(this.zzbRf, ((zza)paramObject).zzbRf)) {
          break;
        }
        bool1 = bool2;
        if (!zzcge.equals(this.zzbRg, ((zza)paramObject).zzbRg)) {
          break;
        }
        if ((this.FZ != null) && (!this.FZ.isEmpty())) {
          break label127;
        }
        if (((zza)paramObject).FZ != null)
        {
          bool1 = bool2;
          if (!((zza)paramObject).FZ.isEmpty()) {
            break;
          }
        }
        return true;
      }
      return false;
      label127:
      return this.FZ.equals(((zza)paramObject).FZ);
    }
    
    public int hashCode()
    {
      int k = 0;
      int m = getClass().getName().hashCode();
      int i;
      int n;
      int i1;
      if (this.zzbRe == null)
      {
        i = 0;
        n = zzcge.hashCode(this.zzbRf);
        i1 = zzcge.hashCode(this.zzbRg);
        j = k;
        if (this.FZ != null) {
          if (!this.FZ.isEmpty()) {
            break label102;
          }
        }
      }
      label102:
      for (int j = k;; j = this.FZ.hashCode())
      {
        return (((i + (m + 527) * 31) * 31 + n) * 31 + i1) * 31 + j;
        i = this.zzbRe.hashCode();
        break;
      }
    }
    
    public void writeTo(zzcfy paramzzcfy)
      throws IOException
    {
      int j = 0;
      if (this.zzbRe != null) {
        paramzzcfy.zzaa(1, this.zzbRe.intValue());
      }
      int i;
      Object localObject;
      if ((this.zzbRf != null) && (this.zzbRf.length > 0))
      {
        i = 0;
        while (i < this.zzbRf.length)
        {
          localObject = this.zzbRf[i];
          if (localObject != null) {
            paramzzcfy.zza(2, (zzcgg)localObject);
          }
          i += 1;
        }
      }
      if ((this.zzbRg != null) && (this.zzbRg.length > 0))
      {
        i = j;
        while (i < this.zzbRg.length)
        {
          localObject = this.zzbRg[i];
          if (localObject != null) {
            paramzzcfy.zza(3, (zzcgg)localObject);
          }
          i += 1;
        }
      }
      super.writeTo(paramzzcfy);
    }
    
    public zza zzJ(zzcfx paramzzcfx)
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
          this.zzbRe = Integer.valueOf(paramzzcfx.zzamM());
          break;
        case 18: 
          j = zzcgj.zzb(paramzzcfx, 18);
          if (this.zzbRf == null) {}
          for (i = 0;; i = this.zzbRf.length)
          {
            localObject = new zzaux.zze[j + i];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.zzbRf, 0, localObject, 0, i);
              j = i;
            }
            while (j < localObject.length - 1)
            {
              localObject[j] = new zzaux.zze();
              paramzzcfx.zza(localObject[j]);
              paramzzcfx.zzamI();
              j += 1;
            }
          }
          localObject[j] = new zzaux.zze();
          paramzzcfx.zza(localObject[j]);
          this.zzbRf = ((zzaux.zze[])localObject);
          break;
        case 26: 
          j = zzcgj.zzb(paramzzcfx, 26);
          if (this.zzbRg == null) {}
          for (i = 0;; i = this.zzbRg.length)
          {
            localObject = new zzaux.zzb[j + i];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.zzbRg, 0, localObject, 0, i);
              j = i;
            }
            while (j < localObject.length - 1)
            {
              localObject[j] = new zzaux.zzb();
              paramzzcfx.zza(localObject[j]);
              paramzzcfx.zzamI();
              j += 1;
            }
          }
          localObject[j] = new zzaux.zzb();
          paramzzcfx.zza(localObject[j]);
          this.zzbRg = ((zzaux.zzb[])localObject);
        }
      }
    }
    
    public zza zzPf()
    {
      this.zzbRe = null;
      this.zzbRf = zzaux.zze.zzPl();
      this.zzbRg = zzaux.zzb.zzPg();
      this.FZ = null;
      this.Gi = -1;
      return this;
    }
  }
  
  public static final class zzb
    extends zzcfz<zzb>
  {
    private static volatile zzb[] zzbRh;
    public Integer zzbRi;
    public String zzbRj;
    public zzaux.zzc[] zzbRk;
    public Boolean zzbRl;
    public zzaux.zzd zzbRm;
    
    public zzb()
    {
      zzPh();
    }
    
    public static zzb[] zzPg()
    {
      if (zzbRh == null) {}
      synchronized (zzcge.Gh)
      {
        if (zzbRh == null) {
          zzbRh = new zzb[0];
        }
        return zzbRh;
      }
    }
    
    protected int computeSerializedSize()
    {
      int i = super.computeSerializedSize();
      int j = i;
      if (this.zzbRi != null) {
        j = i + zzcfy.zzac(1, this.zzbRi.intValue());
      }
      i = j;
      if (this.zzbRj != null) {
        i = j + zzcfy.zzv(2, this.zzbRj);
      }
      j = i;
      if (this.zzbRk != null)
      {
        j = i;
        if (this.zzbRk.length > 0)
        {
          j = 0;
          while (j < this.zzbRk.length)
          {
            zzaux.zzc localzzc = this.zzbRk[j];
            int k = i;
            if (localzzc != null) {
              k = i + zzcfy.zzc(3, localzzc);
            }
            j += 1;
            i = k;
          }
          j = i;
        }
      }
      i = j;
      if (this.zzbRl != null) {
        i = j + zzcfy.zzl(4, this.zzbRl.booleanValue());
      }
      j = i;
      if (this.zzbRm != null) {
        j = i + zzcfy.zzc(5, this.zzbRm);
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
                if (this.zzbRi != null) {
                  break;
                }
                bool1 = bool2;
              } while (((zzb)paramObject).zzbRi != null);
              if (this.zzbRj != null) {
                break label159;
              }
              bool1 = bool2;
            } while (((zzb)paramObject).zzbRj != null);
            bool1 = bool2;
          } while (!zzcge.equals(this.zzbRk, ((zzb)paramObject).zzbRk));
          if (this.zzbRl != null) {
            break label175;
          }
          bool1 = bool2;
        } while (((zzb)paramObject).zzbRl != null);
        if (this.zzbRm != null) {
          break label191;
        }
        bool1 = bool2;
      } while (((zzb)paramObject).zzbRm != null);
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
          if (this.zzbRi.equals(((zzb)paramObject).zzbRi)) {
            break label41;
          }
          return false;
          label159:
          if (this.zzbRj.equals(((zzb)paramObject).zzbRj)) {
            break label57;
          }
          return false;
          label175:
          if (this.zzbRl.equals(((zzb)paramObject).zzbRl)) {
            break label89;
          }
          return false;
          label191:
          if (!this.zzbRm.equals(((zzb)paramObject).zzbRm)) {
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
      int i;
      int j;
      label33:
      int i3;
      int k;
      label51:
      int m;
      if (this.zzbRi == null)
      {
        i = 0;
        if (this.zzbRj != null) {
          break label137;
        }
        j = 0;
        i3 = zzcge.hashCode(this.zzbRk);
        if (this.zzbRl != null) {
          break label148;
        }
        k = 0;
        if (this.zzbRm != null) {
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
        return (m + (k + ((j + (i + (i2 + 527) * 31) * 31) * 31 + i3) * 31) * 31) * 31 + n;
        i = this.zzbRi.hashCode();
        break;
        j = this.zzbRj.hashCode();
        break label33;
        k = this.zzbRl.hashCode();
        break label51;
        m = this.zzbRm.hashCode();
        break label61;
      }
    }
    
    public void writeTo(zzcfy paramzzcfy)
      throws IOException
    {
      if (this.zzbRi != null) {
        paramzzcfy.zzaa(1, this.zzbRi.intValue());
      }
      if (this.zzbRj != null) {
        paramzzcfy.zzu(2, this.zzbRj);
      }
      if ((this.zzbRk != null) && (this.zzbRk.length > 0))
      {
        int i = 0;
        while (i < this.zzbRk.length)
        {
          zzaux.zzc localzzc = this.zzbRk[i];
          if (localzzc != null) {
            paramzzcfy.zza(3, localzzc);
          }
          i += 1;
        }
      }
      if (this.zzbRl != null) {
        paramzzcfy.zzk(4, this.zzbRl.booleanValue());
      }
      if (this.zzbRm != null) {
        paramzzcfy.zza(5, this.zzbRm);
      }
      super.writeTo(paramzzcfy);
    }
    
    public zzb zzK(zzcfx paramzzcfx)
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
          this.zzbRi = Integer.valueOf(paramzzcfx.zzamM());
          break;
        case 18: 
          this.zzbRj = paramzzcfx.readString();
          break;
        case 26: 
          int j = zzcgj.zzb(paramzzcfx, 26);
          if (this.zzbRk == null) {}
          zzaux.zzc[] arrayOfzzc;
          for (i = 0;; i = this.zzbRk.length)
          {
            arrayOfzzc = new zzaux.zzc[j + i];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.zzbRk, 0, arrayOfzzc, 0, i);
              j = i;
            }
            while (j < arrayOfzzc.length - 1)
            {
              arrayOfzzc[j] = new zzaux.zzc();
              paramzzcfx.zza(arrayOfzzc[j]);
              paramzzcfx.zzamI();
              j += 1;
            }
          }
          arrayOfzzc[j] = new zzaux.zzc();
          paramzzcfx.zza(arrayOfzzc[j]);
          this.zzbRk = arrayOfzzc;
          break;
        case 32: 
          this.zzbRl = Boolean.valueOf(paramzzcfx.zzamO());
          break;
        case 42: 
          if (this.zzbRm == null) {
            this.zzbRm = new zzaux.zzd();
          }
          paramzzcfx.zza(this.zzbRm);
        }
      }
    }
    
    public zzb zzPh()
    {
      this.zzbRi = null;
      this.zzbRj = null;
      this.zzbRk = zzaux.zzc.zzPi();
      this.zzbRl = null;
      this.zzbRm = null;
      this.FZ = null;
      this.Gi = -1;
      return this;
    }
  }
  
  public static final class zzc
    extends zzcfz<zzc>
  {
    private static volatile zzc[] zzbRn;
    public zzaux.zzf zzbRo;
    public zzaux.zzd zzbRp;
    public Boolean zzbRq;
    public String zzbRr;
    
    public zzc()
    {
      zzPj();
    }
    
    public static zzc[] zzPi()
    {
      if (zzbRn == null) {}
      synchronized (zzcge.Gh)
      {
        if (zzbRn == null) {
          zzbRn = new zzc[0];
        }
        return zzbRn;
      }
    }
    
    protected int computeSerializedSize()
    {
      int j = super.computeSerializedSize();
      int i = j;
      if (this.zzbRo != null) {
        i = j + zzcfy.zzc(1, this.zzbRo);
      }
      j = i;
      if (this.zzbRp != null) {
        j = i + zzcfy.zzc(2, this.zzbRp);
      }
      i = j;
      if (this.zzbRq != null) {
        i = j + zzcfy.zzl(3, this.zzbRq.booleanValue());
      }
      j = i;
      if (this.zzbRr != null) {
        j = i + zzcfy.zzv(4, this.zzbRr);
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
              } while (!(paramObject instanceof zzc));
              paramObject = (zzc)paramObject;
              if (this.zzbRo != null) {
                break;
              }
              bool1 = bool2;
            } while (((zzc)paramObject).zzbRo != null);
            if (this.zzbRp != null) {
              break label143;
            }
            bool1 = bool2;
          } while (((zzc)paramObject).zzbRp != null);
          if (this.zzbRq != null) {
            break label159;
          }
          bool1 = bool2;
        } while (((zzc)paramObject).zzbRq != null);
        if (this.zzbRr != null) {
          break label175;
        }
        bool1 = bool2;
      } while (((zzc)paramObject).zzbRr != null);
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
          if (this.zzbRo.equals(((zzc)paramObject).zzbRo)) {
            break label41;
          }
          return false;
          label143:
          if (this.zzbRp.equals(((zzc)paramObject).zzbRp)) {
            break label57;
          }
          return false;
          label159:
          if (this.zzbRq.equals(((zzc)paramObject).zzbRq)) {
            break label73;
          }
          return false;
          label175:
          if (!this.zzbRr.equals(((zzc)paramObject).zzbRr)) {
            return false;
          }
        }
      }
      return this.FZ.equals(((zzc)paramObject).FZ);
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
      if (this.zzbRo == null)
      {
        i = 0;
        if (this.zzbRp != null) {
          break label122;
        }
        j = 0;
        if (this.zzbRq != null) {
          break label133;
        }
        k = 0;
        if (this.zzbRr != null) {
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
        i = this.zzbRo.hashCode();
        break;
        j = this.zzbRp.hashCode();
        break label33;
        k = this.zzbRq.hashCode();
        break label42;
        m = this.zzbRr.hashCode();
        break label52;
      }
    }
    
    public void writeTo(zzcfy paramzzcfy)
      throws IOException
    {
      if (this.zzbRo != null) {
        paramzzcfy.zza(1, this.zzbRo);
      }
      if (this.zzbRp != null) {
        paramzzcfy.zza(2, this.zzbRp);
      }
      if (this.zzbRq != null) {
        paramzzcfy.zzk(3, this.zzbRq.booleanValue());
      }
      if (this.zzbRr != null) {
        paramzzcfy.zzu(4, this.zzbRr);
      }
      super.writeTo(paramzzcfy);
    }
    
    public zzc zzL(zzcfx paramzzcfx)
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
          if (this.zzbRo == null) {
            this.zzbRo = new zzaux.zzf();
          }
          paramzzcfx.zza(this.zzbRo);
          break;
        case 18: 
          if (this.zzbRp == null) {
            this.zzbRp = new zzaux.zzd();
          }
          paramzzcfx.zza(this.zzbRp);
          break;
        case 24: 
          this.zzbRq = Boolean.valueOf(paramzzcfx.zzamO());
          break;
        case 34: 
          this.zzbRr = paramzzcfx.readString();
        }
      }
    }
    
    public zzc zzPj()
    {
      this.zzbRo = null;
      this.zzbRp = null;
      this.zzbRq = null;
      this.zzbRr = null;
      this.FZ = null;
      this.Gi = -1;
      return this;
    }
  }
  
  public static final class zzd
    extends zzcfz<zzd>
  {
    public Integer zzbRs;
    public Boolean zzbRt;
    public String zzbRu;
    public String zzbRv;
    public String zzbRw;
    
    public zzd()
    {
      zzPk();
    }
    
    protected int computeSerializedSize()
    {
      int j = super.computeSerializedSize();
      int i = j;
      if (this.zzbRs != null) {
        i = j + zzcfy.zzac(1, this.zzbRs.intValue());
      }
      j = i;
      if (this.zzbRt != null) {
        j = i + zzcfy.zzl(2, this.zzbRt.booleanValue());
      }
      i = j;
      if (this.zzbRu != null) {
        i = j + zzcfy.zzv(3, this.zzbRu);
      }
      j = i;
      if (this.zzbRv != null) {
        j = i + zzcfy.zzv(4, this.zzbRv);
      }
      i = j;
      if (this.zzbRw != null) {
        i = j + zzcfy.zzv(5, this.zzbRw);
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
                } while (!(paramObject instanceof zzd));
                paramObject = (zzd)paramObject;
                if (this.zzbRs != null) {
                  break;
                }
                bool1 = bool2;
              } while (((zzd)paramObject).zzbRs != null);
              if (this.zzbRt != null) {
                break label159;
              }
              bool1 = bool2;
            } while (((zzd)paramObject).zzbRt != null);
            if (this.zzbRu != null) {
              break label175;
            }
            bool1 = bool2;
          } while (((zzd)paramObject).zzbRu != null);
          if (this.zzbRv != null) {
            break label191;
          }
          bool1 = bool2;
        } while (((zzd)paramObject).zzbRv != null);
        if (this.zzbRw != null) {
          break label207;
        }
        bool1 = bool2;
      } while (((zzd)paramObject).zzbRw != null);
      for (;;)
      {
        if ((this.FZ == null) || (this.FZ.isEmpty()))
        {
          if (((zzd)paramObject).FZ != null)
          {
            bool1 = bool2;
            if (!((zzd)paramObject).FZ.isEmpty()) {
              break;
            }
          }
          return true;
          if (this.zzbRs.equals(((zzd)paramObject).zzbRs)) {
            break label41;
          }
          return false;
          label159:
          if (this.zzbRt.equals(((zzd)paramObject).zzbRt)) {
            break label57;
          }
          return false;
          label175:
          if (this.zzbRu.equals(((zzd)paramObject).zzbRu)) {
            break label73;
          }
          return false;
          label191:
          if (this.zzbRv.equals(((zzd)paramObject).zzbRv)) {
            break label89;
          }
          return false;
          label207:
          if (!this.zzbRw.equals(((zzd)paramObject).zzbRw)) {
            return false;
          }
        }
      }
      return this.FZ.equals(((zzd)paramObject).FZ);
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
      if (this.zzbRs == null)
      {
        i = 0;
        if (this.zzbRt != null) {
          break label138;
        }
        j = 0;
        if (this.zzbRu != null) {
          break label149;
        }
        k = 0;
        if (this.zzbRv != null) {
          break label160;
        }
        m = 0;
        if (this.zzbRw != null) {
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
        i = this.zzbRs.intValue();
        break;
        j = this.zzbRt.hashCode();
        break label33;
        k = this.zzbRu.hashCode();
        break label42;
        m = this.zzbRv.hashCode();
        break label52;
        n = this.zzbRw.hashCode();
        break label62;
      }
    }
    
    public void writeTo(zzcfy paramzzcfy)
      throws IOException
    {
      if (this.zzbRs != null) {
        paramzzcfy.zzaa(1, this.zzbRs.intValue());
      }
      if (this.zzbRt != null) {
        paramzzcfy.zzk(2, this.zzbRt.booleanValue());
      }
      if (this.zzbRu != null) {
        paramzzcfy.zzu(3, this.zzbRu);
      }
      if (this.zzbRv != null) {
        paramzzcfy.zzu(4, this.zzbRv);
      }
      if (this.zzbRw != null) {
        paramzzcfy.zzu(5, this.zzbRw);
      }
      super.writeTo(paramzzcfy);
    }
    
    public zzd zzM(zzcfx paramzzcfx)
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
          i = paramzzcfx.zzamM();
          switch (i)
          {
          default: 
            break;
          case 0: 
          case 1: 
          case 2: 
          case 3: 
          case 4: 
            this.zzbRs = Integer.valueOf(i);
          }
          break;
        case 16: 
          this.zzbRt = Boolean.valueOf(paramzzcfx.zzamO());
          break;
        case 26: 
          this.zzbRu = paramzzcfx.readString();
          break;
        case 34: 
          this.zzbRv = paramzzcfx.readString();
          break;
        case 42: 
          this.zzbRw = paramzzcfx.readString();
        }
      }
    }
    
    public zzd zzPk()
    {
      this.zzbRt = null;
      this.zzbRu = null;
      this.zzbRv = null;
      this.zzbRw = null;
      this.FZ = null;
      this.Gi = -1;
      return this;
    }
  }
  
  public static final class zze
    extends zzcfz<zze>
  {
    private static volatile zze[] zzbRx;
    public Integer zzbRi;
    public String zzbRy;
    public zzaux.zzc zzbRz;
    
    public zze()
    {
      zzPm();
    }
    
    public static zze[] zzPl()
    {
      if (zzbRx == null) {}
      synchronized (zzcge.Gh)
      {
        if (zzbRx == null) {
          zzbRx = new zze[0];
        }
        return zzbRx;
      }
    }
    
    protected int computeSerializedSize()
    {
      int j = super.computeSerializedSize();
      int i = j;
      if (this.zzbRi != null) {
        i = j + zzcfy.zzac(1, this.zzbRi.intValue());
      }
      j = i;
      if (this.zzbRy != null) {
        j = i + zzcfy.zzv(2, this.zzbRy);
      }
      i = j;
      if (this.zzbRz != null) {
        i = j + zzcfy.zzc(3, this.zzbRz);
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
            if (this.zzbRi != null) {
              break;
            }
            bool1 = bool2;
          } while (((zze)paramObject).zzbRi != null);
          if (this.zzbRy != null) {
            break label127;
          }
          bool1 = bool2;
        } while (((zze)paramObject).zzbRy != null);
        if (this.zzbRz != null) {
          break label143;
        }
        bool1 = bool2;
      } while (((zze)paramObject).zzbRz != null);
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
          if (this.zzbRi.equals(((zze)paramObject).zzbRi)) {
            break label41;
          }
          return false;
          label127:
          if (this.zzbRy.equals(((zze)paramObject).zzbRy)) {
            break label57;
          }
          return false;
          label143:
          if (!this.zzbRz.equals(((zze)paramObject).zzbRz)) {
            return false;
          }
        }
      }
      return this.FZ.equals(((zze)paramObject).FZ);
    }
    
    public int hashCode()
    {
      int n = 0;
      int i1 = getClass().getName().hashCode();
      int i;
      int j;
      label33:
      int k;
      if (this.zzbRi == null)
      {
        i = 0;
        if (this.zzbRy != null) {
          break label106;
        }
        j = 0;
        if (this.zzbRz != null) {
          break label117;
        }
        k = 0;
        label42:
        m = n;
        if (this.FZ != null) {
          if (!this.FZ.isEmpty()) {
            break label128;
          }
        }
      }
      label106:
      label117:
      label128:
      for (int m = n;; m = this.FZ.hashCode())
      {
        return (k + (j + (i + (i1 + 527) * 31) * 31) * 31) * 31 + m;
        i = this.zzbRi.hashCode();
        break;
        j = this.zzbRy.hashCode();
        break label33;
        k = this.zzbRz.hashCode();
        break label42;
      }
    }
    
    public void writeTo(zzcfy paramzzcfy)
      throws IOException
    {
      if (this.zzbRi != null) {
        paramzzcfy.zzaa(1, this.zzbRi.intValue());
      }
      if (this.zzbRy != null) {
        paramzzcfy.zzu(2, this.zzbRy);
      }
      if (this.zzbRz != null) {
        paramzzcfy.zza(3, this.zzbRz);
      }
      super.writeTo(paramzzcfy);
    }
    
    public zze zzN(zzcfx paramzzcfx)
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
          this.zzbRi = Integer.valueOf(paramzzcfx.zzamM());
          break;
        case 18: 
          this.zzbRy = paramzzcfx.readString();
          break;
        case 26: 
          if (this.zzbRz == null) {
            this.zzbRz = new zzaux.zzc();
          }
          paramzzcfx.zza(this.zzbRz);
        }
      }
    }
    
    public zze zzPm()
    {
      this.zzbRi = null;
      this.zzbRy = null;
      this.zzbRz = null;
      this.FZ = null;
      this.Gi = -1;
      return this;
    }
  }
  
  public static final class zzf
    extends zzcfz<zzf>
  {
    public Integer zzbRA;
    public String zzbRB;
    public Boolean zzbRC;
    public String[] zzbRD;
    
    public zzf()
    {
      zzPn();
    }
    
    protected int computeSerializedSize()
    {
      int n = 0;
      int j = super.computeSerializedSize();
      int i = j;
      if (this.zzbRA != null) {
        i = j + zzcfy.zzac(1, this.zzbRA.intValue());
      }
      j = i;
      if (this.zzbRB != null) {
        j = i + zzcfy.zzv(2, this.zzbRB);
      }
      i = j;
      if (this.zzbRC != null) {
        i = j + zzcfy.zzl(3, this.zzbRC.booleanValue());
      }
      j = i;
      if (this.zzbRD != null)
      {
        j = i;
        if (this.zzbRD.length > 0)
        {
          int k = 0;
          int m = 0;
          j = n;
          while (j < this.zzbRD.length)
          {
            String str = this.zzbRD[j];
            int i1 = k;
            n = m;
            if (str != null)
            {
              n = m + 1;
              i1 = k + zzcfy.zzmU(str);
            }
            j += 1;
            k = i1;
            m = n;
          }
          j = i + k + m * 1;
        }
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
            if (this.zzbRA != null) {
              break;
            }
            bool1 = bool2;
          } while (((zzf)paramObject).zzbRA != null);
          if (this.zzbRB != null) {
            break label143;
          }
          bool1 = bool2;
        } while (((zzf)paramObject).zzbRB != null);
        if (this.zzbRC != null) {
          break label159;
        }
        bool1 = bool2;
      } while (((zzf)paramObject).zzbRC != null);
      label143:
      label159:
      while (this.zzbRC.equals(((zzf)paramObject).zzbRC))
      {
        bool1 = bool2;
        if (!zzcge.equals(this.zzbRD, ((zzf)paramObject).zzbRD)) {
          break;
        }
        if ((this.FZ != null) && (!this.FZ.isEmpty())) {
          break label175;
        }
        if (((zzf)paramObject).FZ != null)
        {
          bool1 = bool2;
          if (!((zzf)paramObject).FZ.isEmpty()) {
            break;
          }
        }
        return true;
        if (this.zzbRA.equals(((zzf)paramObject).zzbRA)) {
          break label41;
        }
        return false;
        if (this.zzbRB.equals(((zzf)paramObject).zzbRB)) {
          break label57;
        }
        return false;
      }
      return false;
      label175:
      return this.FZ.equals(((zzf)paramObject).FZ);
    }
    
    public int hashCode()
    {
      int n = 0;
      int i1 = getClass().getName().hashCode();
      int i;
      int j;
      label33:
      int k;
      label42:
      int i2;
      if (this.zzbRA == null)
      {
        i = 0;
        if (this.zzbRB != null) {
          break label121;
        }
        j = 0;
        if (this.zzbRC != null) {
          break label132;
        }
        k = 0;
        i2 = zzcge.hashCode(this.zzbRD);
        m = n;
        if (this.FZ != null) {
          if (!this.FZ.isEmpty()) {
            break label143;
          }
        }
      }
      label121:
      label132:
      label143:
      for (int m = n;; m = this.FZ.hashCode())
      {
        return ((k + (j + (i + (i1 + 527) * 31) * 31) * 31) * 31 + i2) * 31 + m;
        i = this.zzbRA.intValue();
        break;
        j = this.zzbRB.hashCode();
        break label33;
        k = this.zzbRC.hashCode();
        break label42;
      }
    }
    
    public void writeTo(zzcfy paramzzcfy)
      throws IOException
    {
      if (this.zzbRA != null) {
        paramzzcfy.zzaa(1, this.zzbRA.intValue());
      }
      if (this.zzbRB != null) {
        paramzzcfy.zzu(2, this.zzbRB);
      }
      if (this.zzbRC != null) {
        paramzzcfy.zzk(3, this.zzbRC.booleanValue());
      }
      if ((this.zzbRD != null) && (this.zzbRD.length > 0))
      {
        int i = 0;
        while (i < this.zzbRD.length)
        {
          String str = this.zzbRD[i];
          if (str != null) {
            paramzzcfy.zzu(4, str);
          }
          i += 1;
        }
      }
      super.writeTo(paramzzcfy);
    }
    
    public zzf zzO(zzcfx paramzzcfx)
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
          i = paramzzcfx.zzamM();
          switch (i)
          {
          default: 
            break;
          case 0: 
          case 1: 
          case 2: 
          case 3: 
          case 4: 
          case 5: 
          case 6: 
            this.zzbRA = Integer.valueOf(i);
          }
          break;
        case 18: 
          this.zzbRB = paramzzcfx.readString();
          break;
        case 24: 
          this.zzbRC = Boolean.valueOf(paramzzcfx.zzamO());
          break;
        case 34: 
          int j = zzcgj.zzb(paramzzcfx, 34);
          if (this.zzbRD == null) {}
          String[] arrayOfString;
          for (i = 0;; i = this.zzbRD.length)
          {
            arrayOfString = new String[j + i];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.zzbRD, 0, arrayOfString, 0, i);
              j = i;
            }
            while (j < arrayOfString.length - 1)
            {
              arrayOfString[j] = paramzzcfx.readString();
              paramzzcfx.zzamI();
              j += 1;
            }
          }
          arrayOfString[j] = paramzzcfx.readString();
          this.zzbRD = arrayOfString;
        }
      }
    }
    
    public zzf zzPn()
    {
      this.zzbRB = null;
      this.zzbRC = null;
      this.zzbRD = zzcgj.Gs;
      this.FZ = null;
      this.Gi = -1;
      return this;
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzaux.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */