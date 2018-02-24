package com.google.android.gms.internal;

import java.io.IOException;

public abstract interface zzauy
{
  public static final class zza
    extends zzcfz<zza>
  {
    private static volatile zza[] zzbRE;
    public String name;
    public Boolean zzaID;
    public Boolean zzbRF;
    
    public zza()
    {
      zzPp();
    }
    
    public static zza[] zzPo()
    {
      if (zzbRE == null) {}
      synchronized (zzcge.Gh)
      {
        if (zzbRE == null) {
          zzbRE = new zza[0];
        }
        return zzbRE;
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
      if (this.zzaID != null) {
        j = i + zzcfy.zzl(2, this.zzaID.booleanValue());
      }
      i = j;
      if (this.zzbRF != null) {
        i = j + zzcfy.zzl(3, this.zzbRF.booleanValue());
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
            } while (!(paramObject instanceof zza));
            paramObject = (zza)paramObject;
            if (this.name != null) {
              break;
            }
            bool1 = bool2;
          } while (((zza)paramObject).name != null);
          if (this.zzaID != null) {
            break label127;
          }
          bool1 = bool2;
        } while (((zza)paramObject).zzaID != null);
        if (this.zzbRF != null) {
          break label143;
        }
        bool1 = bool2;
      } while (((zza)paramObject).zzbRF != null);
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
          if (this.name.equals(((zza)paramObject).name)) {
            break label41;
          }
          return false;
          label127:
          if (this.zzaID.equals(((zza)paramObject).zzaID)) {
            break label57;
          }
          return false;
          label143:
          if (!this.zzbRF.equals(((zza)paramObject).zzbRF)) {
            return false;
          }
        }
      }
      return this.FZ.equals(((zza)paramObject).FZ);
    }
    
    public int hashCode()
    {
      int n = 0;
      int i1 = getClass().getName().hashCode();
      int i;
      int j;
      label33:
      int k;
      if (this.name == null)
      {
        i = 0;
        if (this.zzaID != null) {
          break label106;
        }
        j = 0;
        if (this.zzbRF != null) {
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
        i = this.name.hashCode();
        break;
        j = this.zzaID.hashCode();
        break label33;
        k = this.zzbRF.hashCode();
        break label42;
      }
    }
    
    public void writeTo(zzcfy paramzzcfy)
      throws IOException
    {
      if (this.name != null) {
        paramzzcfy.zzu(1, this.name);
      }
      if (this.zzaID != null) {
        paramzzcfy.zzk(2, this.zzaID.booleanValue());
      }
      if (this.zzbRF != null) {
        paramzzcfy.zzk(3, this.zzbRF.booleanValue());
      }
      super.writeTo(paramzzcfy);
    }
    
    public zza zzP(zzcfx paramzzcfx)
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
        case 16: 
          this.zzaID = Boolean.valueOf(paramzzcfx.zzamO());
          break;
        case 24: 
          this.zzbRF = Boolean.valueOf(paramzzcfx.zzamO());
        }
      }
    }
    
    public zza zzPp()
    {
      this.name = null;
      this.zzaID = null;
      this.zzbRF = null;
      this.FZ = null;
      this.Gi = -1;
      return this;
    }
  }
  
  public static final class zzb
    extends zzcfz<zzb>
  {
    public String zzbLI;
    public Long zzbRG;
    public Integer zzbRH;
    public zzauy.zzc[] zzbRI;
    public zzauy.zza[] zzbRJ;
    public zzaux.zza[] zzbRK;
    
    public zzb()
    {
      zzPq();
    }
    
    protected int computeSerializedSize()
    {
      int m = 0;
      int j = super.computeSerializedSize();
      int i = j;
      if (this.zzbRG != null) {
        i = j + zzcfy.zzj(1, this.zzbRG.longValue());
      }
      j = i;
      if (this.zzbLI != null) {
        j = i + zzcfy.zzv(2, this.zzbLI);
      }
      i = j;
      if (this.zzbRH != null) {
        i = j + zzcfy.zzac(3, this.zzbRH.intValue());
      }
      j = i;
      Object localObject;
      if (this.zzbRI != null)
      {
        j = i;
        if (this.zzbRI.length > 0)
        {
          j = 0;
          while (j < this.zzbRI.length)
          {
            localObject = this.zzbRI[j];
            k = i;
            if (localObject != null) {
              k = i + zzcfy.zzc(4, (zzcgg)localObject);
            }
            j += 1;
            i = k;
          }
          j = i;
        }
      }
      i = j;
      if (this.zzbRJ != null)
      {
        i = j;
        if (this.zzbRJ.length > 0)
        {
          i = j;
          j = 0;
          while (j < this.zzbRJ.length)
          {
            localObject = this.zzbRJ[j];
            k = i;
            if (localObject != null) {
              k = i + zzcfy.zzc(5, (zzcgg)localObject);
            }
            j += 1;
            i = k;
          }
        }
      }
      int k = i;
      if (this.zzbRK != null)
      {
        k = i;
        if (this.zzbRK.length > 0)
        {
          j = m;
          for (;;)
          {
            k = i;
            if (j >= this.zzbRK.length) {
              break;
            }
            localObject = this.zzbRK[j];
            k = i;
            if (localObject != null) {
              k = i + zzcfy.zzc(6, (zzcgg)localObject);
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
            } while (!(paramObject instanceof zzb));
            paramObject = (zzb)paramObject;
            if (this.zzbRG != null) {
              break;
            }
            bool1 = bool2;
          } while (((zzb)paramObject).zzbRG != null);
          if (this.zzbLI != null) {
            break label175;
          }
          bool1 = bool2;
        } while (((zzb)paramObject).zzbLI != null);
        if (this.zzbRH != null) {
          break label191;
        }
        bool1 = bool2;
      } while (((zzb)paramObject).zzbRH != null);
      label175:
      label191:
      while (this.zzbRH.equals(((zzb)paramObject).zzbRH))
      {
        bool1 = bool2;
        if (!zzcge.equals(this.zzbRI, ((zzb)paramObject).zzbRI)) {
          break;
        }
        bool1 = bool2;
        if (!zzcge.equals(this.zzbRJ, ((zzb)paramObject).zzbRJ)) {
          break;
        }
        bool1 = bool2;
        if (!zzcge.equals(this.zzbRK, ((zzb)paramObject).zzbRK)) {
          break;
        }
        if ((this.FZ != null) && (!this.FZ.isEmpty())) {
          break label207;
        }
        if (((zzb)paramObject).FZ != null)
        {
          bool1 = bool2;
          if (!((zzb)paramObject).FZ.isEmpty()) {
            break;
          }
        }
        return true;
        if (this.zzbRG.equals(((zzb)paramObject).zzbRG)) {
          break label41;
        }
        return false;
        if (this.zzbLI.equals(((zzb)paramObject).zzbLI)) {
          break label57;
        }
        return false;
      }
      return false;
      label207:
      return this.FZ.equals(((zzb)paramObject).FZ);
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
      int i3;
      int i4;
      if (this.zzbRG == null)
      {
        i = 0;
        if (this.zzbLI != null) {
          break label151;
        }
        j = 0;
        if (this.zzbRH != null) {
          break label162;
        }
        k = 0;
        i2 = zzcge.hashCode(this.zzbRI);
        i3 = zzcge.hashCode(this.zzbRJ);
        i4 = zzcge.hashCode(this.zzbRK);
        m = n;
        if (this.FZ != null) {
          if (!this.FZ.isEmpty()) {
            break label173;
          }
        }
      }
      label151:
      label162:
      label173:
      for (int m = n;; m = this.FZ.hashCode())
      {
        return ((((k + (j + (i + (i1 + 527) * 31) * 31) * 31) * 31 + i2) * 31 + i3) * 31 + i4) * 31 + m;
        i = this.zzbRG.hashCode();
        break;
        j = this.zzbLI.hashCode();
        break label33;
        k = this.zzbRH.hashCode();
        break label42;
      }
    }
    
    public void writeTo(zzcfy paramzzcfy)
      throws IOException
    {
      int j = 0;
      if (this.zzbRG != null) {
        paramzzcfy.zzf(1, this.zzbRG.longValue());
      }
      if (this.zzbLI != null) {
        paramzzcfy.zzu(2, this.zzbLI);
      }
      if (this.zzbRH != null) {
        paramzzcfy.zzaa(3, this.zzbRH.intValue());
      }
      int i;
      Object localObject;
      if ((this.zzbRI != null) && (this.zzbRI.length > 0))
      {
        i = 0;
        while (i < this.zzbRI.length)
        {
          localObject = this.zzbRI[i];
          if (localObject != null) {
            paramzzcfy.zza(4, (zzcgg)localObject);
          }
          i += 1;
        }
      }
      if ((this.zzbRJ != null) && (this.zzbRJ.length > 0))
      {
        i = 0;
        while (i < this.zzbRJ.length)
        {
          localObject = this.zzbRJ[i];
          if (localObject != null) {
            paramzzcfy.zza(5, (zzcgg)localObject);
          }
          i += 1;
        }
      }
      if ((this.zzbRK != null) && (this.zzbRK.length > 0))
      {
        i = j;
        while (i < this.zzbRK.length)
        {
          localObject = this.zzbRK[i];
          if (localObject != null) {
            paramzzcfy.zza(6, (zzcgg)localObject);
          }
          i += 1;
        }
      }
      super.writeTo(paramzzcfy);
    }
    
    public zzb zzPq()
    {
      this.zzbRG = null;
      this.zzbLI = null;
      this.zzbRH = null;
      this.zzbRI = zzauy.zzc.zzPr();
      this.zzbRJ = zzauy.zza.zzPo();
      this.zzbRK = zzaux.zza.zzPe();
      this.FZ = null;
      this.Gi = -1;
      return this;
    }
    
    public zzb zzQ(zzcfx paramzzcfx)
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
          this.zzbRG = Long.valueOf(paramzzcfx.zzamL());
          break;
        case 18: 
          this.zzbLI = paramzzcfx.readString();
          break;
        case 24: 
          this.zzbRH = Integer.valueOf(paramzzcfx.zzamM());
          break;
        case 34: 
          j = zzcgj.zzb(paramzzcfx, 34);
          if (this.zzbRI == null) {}
          for (i = 0;; i = this.zzbRI.length)
          {
            localObject = new zzauy.zzc[j + i];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.zzbRI, 0, localObject, 0, i);
              j = i;
            }
            while (j < localObject.length - 1)
            {
              localObject[j] = new zzauy.zzc();
              paramzzcfx.zza(localObject[j]);
              paramzzcfx.zzamI();
              j += 1;
            }
          }
          localObject[j] = new zzauy.zzc();
          paramzzcfx.zza(localObject[j]);
          this.zzbRI = ((zzauy.zzc[])localObject);
          break;
        case 42: 
          j = zzcgj.zzb(paramzzcfx, 42);
          if (this.zzbRJ == null) {}
          for (i = 0;; i = this.zzbRJ.length)
          {
            localObject = new zzauy.zza[j + i];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.zzbRJ, 0, localObject, 0, i);
              j = i;
            }
            while (j < localObject.length - 1)
            {
              localObject[j] = new zzauy.zza();
              paramzzcfx.zza(localObject[j]);
              paramzzcfx.zzamI();
              j += 1;
            }
          }
          localObject[j] = new zzauy.zza();
          paramzzcfx.zza(localObject[j]);
          this.zzbRJ = ((zzauy.zza[])localObject);
          break;
        case 50: 
          j = zzcgj.zzb(paramzzcfx, 50);
          if (this.zzbRK == null) {}
          for (i = 0;; i = this.zzbRK.length)
          {
            localObject = new zzaux.zza[j + i];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.zzbRK, 0, localObject, 0, i);
              j = i;
            }
            while (j < localObject.length - 1)
            {
              localObject[j] = new zzaux.zza();
              paramzzcfx.zza(localObject[j]);
              paramzzcfx.zzamI();
              j += 1;
            }
          }
          localObject[j] = new zzaux.zza();
          paramzzcfx.zza(localObject[j]);
          this.zzbRK = ((zzaux.zza[])localObject);
        }
      }
    }
  }
  
  public static final class zzc
    extends zzcfz<zzc>
  {
    private static volatile zzc[] zzbRL;
    public String value;
    public String zzaA;
    
    public zzc()
    {
      zzPs();
    }
    
    public static zzc[] zzPr()
    {
      if (zzbRL == null) {}
      synchronized (zzcge.Gh)
      {
        if (zzbRL == null) {
          zzbRL = new zzc[0];
        }
        return zzbRL;
      }
    }
    
    protected int computeSerializedSize()
    {
      int j = super.computeSerializedSize();
      int i = j;
      if (this.zzaA != null) {
        i = j + zzcfy.zzv(1, this.zzaA);
      }
      j = i;
      if (this.value != null) {
        j = i + zzcfy.zzv(2, this.value);
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
          if (this.zzaA != null) {
            break;
          }
          bool1 = bool2;
        } while (((zzc)paramObject).zzaA != null);
        if (this.value != null) {
          break label111;
        }
        bool1 = bool2;
      } while (((zzc)paramObject).value != null);
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
          if (this.zzaA.equals(((zzc)paramObject).zzaA)) {
            break label41;
          }
          return false;
          label111:
          if (!this.value.equals(((zzc)paramObject).value)) {
            return false;
          }
        }
      }
      return this.FZ.equals(((zzc)paramObject).FZ);
    }
    
    public int hashCode()
    {
      int m = 0;
      int n = getClass().getName().hashCode();
      int i;
      int j;
      if (this.zzaA == null)
      {
        i = 0;
        if (this.value != null) {
          break label89;
        }
        j = 0;
        label33:
        k = m;
        if (this.FZ != null) {
          if (!this.FZ.isEmpty()) {
            break label100;
          }
        }
      }
      label89:
      label100:
      for (int k = m;; k = this.FZ.hashCode())
      {
        return (j + (i + (n + 527) * 31) * 31) * 31 + k;
        i = this.zzaA.hashCode();
        break;
        j = this.value.hashCode();
        break label33;
      }
    }
    
    public void writeTo(zzcfy paramzzcfy)
      throws IOException
    {
      if (this.zzaA != null) {
        paramzzcfy.zzu(1, this.zzaA);
      }
      if (this.value != null) {
        paramzzcfy.zzu(2, this.value);
      }
      super.writeTo(paramzzcfy);
    }
    
    public zzc zzPs()
    {
      this.zzaA = null;
      this.value = null;
      this.FZ = null;
      this.Gi = -1;
      return this;
    }
    
    public zzc zzR(zzcfx paramzzcfx)
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
          this.zzaA = paramzzcfx.readString();
          break;
        case 18: 
          this.value = paramzzcfx.readString();
        }
      }
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzauy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */