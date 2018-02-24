package com.google.android.gms.internal;

import java.io.IOException;
import java.util.Arrays;

public abstract interface zzcbz
{
  public static final class zza
    extends zzcfz<zza>
  {
    public long timestamp;
    public zzcbz.zzd[] xi;
    public byte[][] xj;
    
    public zza()
    {
      zzakE();
    }
    
    protected int computeSerializedSize()
    {
      int n = 0;
      int i = super.computeSerializedSize();
      int j = i;
      Object localObject;
      int k;
      if (this.xi != null)
      {
        j = i;
        if (this.xi.length > 0)
        {
          j = 0;
          while (j < this.xi.length)
          {
            localObject = this.xi[j];
            k = i;
            if (localObject != null) {
              k = i + zzcfy.zzc(1, (zzcgg)localObject);
            }
            j += 1;
            i = k;
          }
          j = i;
        }
      }
      i = j;
      if (this.timestamp != 0L) {
        i = j + zzcfy.zzk(2, this.timestamp);
      }
      j = i;
      if (this.xj != null)
      {
        j = i;
        if (this.xj.length > 0)
        {
          k = 0;
          int m = 0;
          j = n;
          while (j < this.xj.length)
          {
            localObject = this.xj[j];
            int i1 = k;
            n = m;
            if (localObject != null)
            {
              n = m + 1;
              i1 = k + zzcfy.zzau((byte[])localObject);
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
              bool1 = bool2;
            } while (!zzcge.equals(this.xi, ((zza)paramObject).xi));
            bool1 = bool2;
          } while (this.timestamp != ((zza)paramObject).timestamp);
          bool1 = bool2;
        } while (!zzcge.zza(this.xj, ((zza)paramObject).xj));
        if ((this.FZ != null) && (!this.FZ.isEmpty())) {
          break label109;
        }
        if (((zza)paramObject).FZ == null) {
          break;
        }
        bool1 = bool2;
      } while (!((zza)paramObject).FZ.isEmpty());
      return true;
      label109:
      return this.FZ.equals(((zza)paramObject).FZ);
    }
    
    public int hashCode()
    {
      int j = getClass().getName().hashCode();
      int k = zzcge.hashCode(this.xi);
      int m = (int)(this.timestamp ^ this.timestamp >>> 32);
      int n = zzcge.zzd(this.xj);
      if ((this.FZ == null) || (this.FZ.isEmpty())) {}
      for (int i = 0;; i = this.FZ.hashCode()) {
        return i + ((((j + 527) * 31 + k) * 31 + m) * 31 + n) * 31;
      }
    }
    
    public void writeTo(zzcfy paramzzcfy)
      throws IOException
    {
      int j = 0;
      int i;
      Object localObject;
      if ((this.xi != null) && (this.xi.length > 0))
      {
        i = 0;
        while (i < this.xi.length)
        {
          localObject = this.xi[i];
          if (localObject != null) {
            paramzzcfy.zza(1, (zzcgg)localObject);
          }
          i += 1;
        }
      }
      if (this.timestamp != 0L) {
        paramzzcfy.zzg(2, this.timestamp);
      }
      if ((this.xj != null) && (this.xj.length > 0))
      {
        i = j;
        while (i < this.xj.length)
        {
          localObject = this.xj[i];
          if (localObject != null) {
            paramzzcfy.zzb(3, (byte[])localObject);
          }
          i += 1;
        }
      }
      super.writeTo(paramzzcfy);
    }
    
    public zza zzai(zzcfx paramzzcfx)
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
        case 10: 
          j = zzcgj.zzb(paramzzcfx, 10);
          if (this.xi == null) {}
          for (i = 0;; i = this.xi.length)
          {
            localObject = new zzcbz.zzd[j + i];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.xi, 0, localObject, 0, i);
              j = i;
            }
            while (j < localObject.length - 1)
            {
              localObject[j] = new zzcbz.zzd();
              paramzzcfx.zza(localObject[j]);
              paramzzcfx.zzamI();
              j += 1;
            }
          }
          localObject[j] = new zzcbz.zzd();
          paramzzcfx.zza(localObject[j]);
          this.xi = ((zzcbz.zzd[])localObject);
          break;
        case 17: 
          this.timestamp = paramzzcfx.zzamN();
          break;
        case 26: 
          j = zzcgj.zzb(paramzzcfx, 26);
          if (this.xj == null) {}
          for (i = 0;; i = this.xj.length)
          {
            localObject = new byte[j + i][];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.xj, 0, localObject, 0, i);
              j = i;
            }
            while (j < localObject.length - 1)
            {
              localObject[j] = paramzzcfx.readBytes();
              paramzzcfx.zzamI();
              j += 1;
            }
          }
          localObject[j] = paramzzcfx.readBytes();
          this.xj = ((byte[][])localObject);
        }
      }
    }
    
    public zza zzakE()
    {
      this.xi = zzcbz.zzd.zzakI();
      this.timestamp = 0L;
      this.xj = zzcgj.Gt;
      this.FZ = null;
      this.Gi = -1;
      return this;
    }
  }
  
  public static final class zzb
    extends zzcfz<zzb>
  {
    private static volatile zzb[] xk;
    public byte[] xl;
    public String zzaA;
    
    public zzb()
    {
      zzakG();
    }
    
    public static zzb[] zzakF()
    {
      if (xk == null) {}
      synchronized (zzcge.Gh)
      {
        if (xk == null) {
          xk = new zzb[0];
        }
        return xk;
      }
    }
    
    protected int computeSerializedSize()
    {
      int j = super.computeSerializedSize();
      int i = j;
      if (this.zzaA != null)
      {
        i = j;
        if (!this.zzaA.equals("")) {
          i = j + zzcfy.zzv(1, this.zzaA);
        }
      }
      j = i;
      if (!Arrays.equals(this.xl, zzcgj.Gu)) {
        j = i + zzcfy.zzc(2, this.xl);
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
      do
      {
        do
        {
          return bool1;
          bool1 = bool2;
        } while (!(paramObject instanceof zzb));
        paramObject = (zzb)paramObject;
        if (this.zzaA != null) {
          break;
        }
        bool1 = bool2;
      } while (((zzb)paramObject).zzaA != null);
      while (this.zzaA.equals(((zzb)paramObject).zzaA))
      {
        bool1 = bool2;
        if (!Arrays.equals(this.xl, ((zzb)paramObject).xl)) {
          break;
        }
        if ((this.FZ != null) && (!this.FZ.isEmpty())) {
          break label111;
        }
        if (((zzb)paramObject).FZ != null)
        {
          bool1 = bool2;
          if (!((zzb)paramObject).FZ.isEmpty()) {
            break;
          }
        }
        return true;
      }
      return false;
      label111:
      return this.FZ.equals(((zzb)paramObject).FZ);
    }
    
    public int hashCode()
    {
      int k = 0;
      int m = getClass().getName().hashCode();
      int i;
      int n;
      if (this.zzaA == null)
      {
        i = 0;
        n = Arrays.hashCode(this.xl);
        j = k;
        if (this.FZ != null) {
          if (!this.FZ.isEmpty()) {
            break label87;
          }
        }
      }
      label87:
      for (int j = k;; j = this.FZ.hashCode())
      {
        return ((i + (m + 527) * 31) * 31 + n) * 31 + j;
        i = this.zzaA.hashCode();
        break;
      }
    }
    
    public void writeTo(zzcfy paramzzcfy)
      throws IOException
    {
      if ((this.zzaA != null) && (!this.zzaA.equals(""))) {
        paramzzcfy.zzu(1, this.zzaA);
      }
      if (!Arrays.equals(this.xl, zzcgj.Gu)) {
        paramzzcfy.zzb(2, this.xl);
      }
      super.writeTo(paramzzcfy);
    }
    
    public zzb zzaj(zzcfx paramzzcfx)
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
          this.xl = paramzzcfx.readBytes();
        }
      }
    }
    
    public zzb zzakG()
    {
      this.zzaA = "";
      this.xl = zzcgj.Gu;
      this.FZ = null;
      this.Gi = -1;
      return this;
    }
  }
  
  public static final class zzc
    extends zzcfz<zzc>
  {
    public int xm;
    public boolean xn;
    public long xo;
    
    public zzc()
    {
      zzakH();
    }
    
    protected int computeSerializedSize()
    {
      int j = super.computeSerializedSize();
      int i = j;
      if (this.xm != 0) {
        i = j + zzcfy.zzac(1, this.xm);
      }
      j = i;
      if (this.xn) {
        j = i + zzcfy.zzl(2, this.xn);
      }
      i = j;
      if (this.xo != 0L) {
        i = j + zzcfy.zzk(3, this.xo);
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
              bool1 = bool2;
            } while (this.xm != ((zzc)paramObject).xm);
            bool1 = bool2;
          } while (this.xn != ((zzc)paramObject).xn);
          bool1 = bool2;
        } while (this.xo != ((zzc)paramObject).xo);
        if ((this.FZ != null) && (!this.FZ.isEmpty())) {
          break label103;
        }
        if (((zzc)paramObject).FZ == null) {
          break;
        }
        bool1 = bool2;
      } while (!((zzc)paramObject).FZ.isEmpty());
      return true;
      label103:
      return this.FZ.equals(((zzc)paramObject).FZ);
    }
    
    public int hashCode()
    {
      int k = getClass().getName().hashCode();
      int m = this.xm;
      int i;
      int n;
      if (this.xn)
      {
        i = 1231;
        n = (int)(this.xo ^ this.xo >>> 32);
        if ((this.FZ != null) && (!this.FZ.isEmpty())) {
          break label97;
        }
      }
      label97:
      for (int j = 0;; j = this.FZ.hashCode())
      {
        return j + ((i + ((k + 527) * 31 + m) * 31) * 31 + n) * 31;
        i = 1237;
        break;
      }
    }
    
    public void writeTo(zzcfy paramzzcfy)
      throws IOException
    {
      if (this.xm != 0) {
        paramzzcfy.zzaa(1, this.xm);
      }
      if (this.xn) {
        paramzzcfy.zzk(2, this.xn);
      }
      if (this.xo != 0L) {
        paramzzcfy.zzg(3, this.xo);
      }
      super.writeTo(paramzzcfy);
    }
    
    public zzc zzak(zzcfx paramzzcfx)
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
          this.xm = paramzzcfx.zzamM();
          break;
        case 16: 
          this.xn = paramzzcfx.zzamO();
          break;
        case 25: 
          this.xo = paramzzcfx.zzamN();
        }
      }
    }
    
    public zzc zzakH()
    {
      this.xm = 0;
      this.xn = false;
      this.xo = 0L;
      this.FZ = null;
      this.Gi = -1;
      return this;
    }
  }
  
  public static final class zzd
    extends zzcfz<zzd>
  {
    private static volatile zzd[] xp;
    public zzcbz.zzb[] xq;
    public String zzaTl;
    
    public zzd()
    {
      zzakJ();
    }
    
    public static zzd[] zzakI()
    {
      if (xp == null) {}
      synchronized (zzcge.Gh)
      {
        if (xp == null) {
          xp = new zzd[0];
        }
        return xp;
      }
    }
    
    protected int computeSerializedSize()
    {
      int j = super.computeSerializedSize();
      int i = j;
      if (this.zzaTl != null)
      {
        i = j;
        if (!this.zzaTl.equals("")) {
          i = j + zzcfy.zzv(1, this.zzaTl);
        }
      }
      j = i;
      if (this.xq != null)
      {
        j = i;
        if (this.xq.length > 0)
        {
          j = 0;
          while (j < this.xq.length)
          {
            zzcbz.zzb localzzb = this.xq[j];
            int k = i;
            if (localzzb != null) {
              k = i + zzcfy.zzc(2, localzzb);
            }
            j += 1;
            i = k;
          }
          j = i;
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
      do
      {
        do
        {
          return bool1;
          bool1 = bool2;
        } while (!(paramObject instanceof zzd));
        paramObject = (zzd)paramObject;
        if (this.zzaTl != null) {
          break;
        }
        bool1 = bool2;
      } while (((zzd)paramObject).zzaTl != null);
      while (this.zzaTl.equals(((zzd)paramObject).zzaTl))
      {
        bool1 = bool2;
        if (!zzcge.equals(this.xq, ((zzd)paramObject).xq)) {
          break;
        }
        if ((this.FZ != null) && (!this.FZ.isEmpty())) {
          break label111;
        }
        if (((zzd)paramObject).FZ != null)
        {
          bool1 = bool2;
          if (!((zzd)paramObject).FZ.isEmpty()) {
            break;
          }
        }
        return true;
      }
      return false;
      label111:
      return this.FZ.equals(((zzd)paramObject).FZ);
    }
    
    public int hashCode()
    {
      int k = 0;
      int m = getClass().getName().hashCode();
      int i;
      int n;
      if (this.zzaTl == null)
      {
        i = 0;
        n = zzcge.hashCode(this.xq);
        j = k;
        if (this.FZ != null) {
          if (!this.FZ.isEmpty()) {
            break label87;
          }
        }
      }
      label87:
      for (int j = k;; j = this.FZ.hashCode())
      {
        return ((i + (m + 527) * 31) * 31 + n) * 31 + j;
        i = this.zzaTl.hashCode();
        break;
      }
    }
    
    public void writeTo(zzcfy paramzzcfy)
      throws IOException
    {
      if ((this.zzaTl != null) && (!this.zzaTl.equals(""))) {
        paramzzcfy.zzu(1, this.zzaTl);
      }
      if ((this.xq != null) && (this.xq.length > 0))
      {
        int i = 0;
        while (i < this.xq.length)
        {
          zzcbz.zzb localzzb = this.xq[i];
          if (localzzb != null) {
            paramzzcfy.zza(2, localzzb);
          }
          i += 1;
        }
      }
      super.writeTo(paramzzcfy);
    }
    
    public zzd zzakJ()
    {
      this.zzaTl = "";
      this.xq = zzcbz.zzb.zzakF();
      this.FZ = null;
      this.Gi = -1;
      return this;
    }
    
    public zzd zzal(zzcfx paramzzcfx)
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
          this.zzaTl = paramzzcfx.readString();
          break;
        case 18: 
          int j = zzcgj.zzb(paramzzcfx, 18);
          if (this.xq == null) {}
          zzcbz.zzb[] arrayOfzzb;
          for (i = 0;; i = this.xq.length)
          {
            arrayOfzzb = new zzcbz.zzb[j + i];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.xq, 0, arrayOfzzb, 0, i);
              j = i;
            }
            while (j < arrayOfzzb.length - 1)
            {
              arrayOfzzb[j] = new zzcbz.zzb();
              paramzzcfx.zza(arrayOfzzb[j]);
              paramzzcfx.zzamI();
              j += 1;
            }
          }
          arrayOfzzb[j] = new zzcbz.zzb();
          paramzzcfx.zza(arrayOfzzb[j]);
          this.xq = arrayOfzzb;
        }
      }
    }
  }
  
  public static final class zze
    extends zzcfz<zze>
  {
    public zzcbz.zza xr;
    public zzcbz.zza xs;
    public zzcbz.zza xt;
    public zzcbz.zzc xu;
    public zzcbz.zzf[] xv;
    
    public zze()
    {
      zzakK();
    }
    
    protected int computeSerializedSize()
    {
      int j = super.computeSerializedSize();
      int i = j;
      if (this.xr != null) {
        i = j + zzcfy.zzc(1, this.xr);
      }
      j = i;
      if (this.xs != null) {
        j = i + zzcfy.zzc(2, this.xs);
      }
      int k = j;
      if (this.xt != null) {
        k = j + zzcfy.zzc(3, this.xt);
      }
      i = k;
      if (this.xu != null) {
        i = k + zzcfy.zzc(4, this.xu);
      }
      j = i;
      if (this.xv != null)
      {
        j = i;
        if (this.xv.length > 0)
        {
          j = 0;
          while (j < this.xv.length)
          {
            zzcbz.zzf localzzf = this.xv[j];
            k = i;
            if (localzzf != null) {
              k = i + zzcfy.zzc(5, localzzf);
            }
            j += 1;
            i = k;
          }
          j = i;
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
              } while (!(paramObject instanceof zze));
              paramObject = (zze)paramObject;
              if (this.xr != null) {
                break;
              }
              bool1 = bool2;
            } while (((zze)paramObject).xr != null);
            if (this.xs != null) {
              break label159;
            }
            bool1 = bool2;
          } while (((zze)paramObject).xs != null);
          if (this.xt != null) {
            break label175;
          }
          bool1 = bool2;
        } while (((zze)paramObject).xt != null);
        if (this.xu != null) {
          break label191;
        }
        bool1 = bool2;
      } while (((zze)paramObject).xu != null);
      label159:
      label175:
      label191:
      while (this.xu.equals(((zze)paramObject).xu))
      {
        bool1 = bool2;
        if (!zzcge.equals(this.xv, ((zze)paramObject).xv)) {
          break;
        }
        if ((this.FZ != null) && (!this.FZ.isEmpty())) {
          break label207;
        }
        if (((zze)paramObject).FZ != null)
        {
          bool1 = bool2;
          if (!((zze)paramObject).FZ.isEmpty()) {
            break;
          }
        }
        return true;
        if (this.xr.equals(((zze)paramObject).xr)) {
          break label41;
        }
        return false;
        if (this.xs.equals(((zze)paramObject).xs)) {
          break label57;
        }
        return false;
        if (this.xt.equals(((zze)paramObject).xt)) {
          break label73;
        }
        return false;
      }
      return false;
      label207:
      return this.FZ.equals(((zze)paramObject).FZ);
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
      label52:
      int i3;
      if (this.xr == null)
      {
        i = 0;
        if (this.xs != null) {
          break label137;
        }
        j = 0;
        if (this.xt != null) {
          break label148;
        }
        k = 0;
        if (this.xu != null) {
          break label159;
        }
        m = 0;
        i3 = zzcge.hashCode(this.xv);
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
        return ((m + (k + (j + (i + (i2 + 527) * 31) * 31) * 31) * 31) * 31 + i3) * 31 + n;
        i = this.xr.hashCode();
        break;
        j = this.xs.hashCode();
        break label33;
        k = this.xt.hashCode();
        break label42;
        m = this.xu.hashCode();
        break label52;
      }
    }
    
    public void writeTo(zzcfy paramzzcfy)
      throws IOException
    {
      if (this.xr != null) {
        paramzzcfy.zza(1, this.xr);
      }
      if (this.xs != null) {
        paramzzcfy.zza(2, this.xs);
      }
      if (this.xt != null) {
        paramzzcfy.zza(3, this.xt);
      }
      if (this.xu != null) {
        paramzzcfy.zza(4, this.xu);
      }
      if ((this.xv != null) && (this.xv.length > 0))
      {
        int i = 0;
        while (i < this.xv.length)
        {
          zzcbz.zzf localzzf = this.xv[i];
          if (localzzf != null) {
            paramzzcfy.zza(5, localzzf);
          }
          i += 1;
        }
      }
      super.writeTo(paramzzcfy);
    }
    
    public zze zzakK()
    {
      this.xr = null;
      this.xs = null;
      this.xt = null;
      this.xu = null;
      this.xv = zzcbz.zzf.zzakL();
      this.FZ = null;
      this.Gi = -1;
      return this;
    }
    
    public zze zzam(zzcfx paramzzcfx)
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
          if (this.xr == null) {
            this.xr = new zzcbz.zza();
          }
          paramzzcfx.zza(this.xr);
          break;
        case 18: 
          if (this.xs == null) {
            this.xs = new zzcbz.zza();
          }
          paramzzcfx.zza(this.xs);
          break;
        case 26: 
          if (this.xt == null) {
            this.xt = new zzcbz.zza();
          }
          paramzzcfx.zza(this.xt);
          break;
        case 34: 
          if (this.xu == null) {
            this.xu = new zzcbz.zzc();
          }
          paramzzcfx.zza(this.xu);
          break;
        case 42: 
          int j = zzcgj.zzb(paramzzcfx, 42);
          if (this.xv == null) {}
          zzcbz.zzf[] arrayOfzzf;
          for (i = 0;; i = this.xv.length)
          {
            arrayOfzzf = new zzcbz.zzf[j + i];
            j = i;
            if (i != 0)
            {
              System.arraycopy(this.xv, 0, arrayOfzzf, 0, i);
              j = i;
            }
            while (j < arrayOfzzf.length - 1)
            {
              arrayOfzzf[j] = new zzcbz.zzf();
              paramzzcfx.zza(arrayOfzzf[j]);
              paramzzcfx.zzamI();
              j += 1;
            }
          }
          arrayOfzzf[j] = new zzcbz.zzf();
          paramzzcfx.zza(arrayOfzzf[j]);
          this.xv = arrayOfzzf;
        }
      }
    }
  }
  
  public static final class zzf
    extends zzcfz<zzf>
  {
    private static volatile zzf[] xw;
    public int resourceId;
    public long xx;
    public String zzaTl;
    
    public zzf()
    {
      zzakM();
    }
    
    public static zzf[] zzakL()
    {
      if (xw == null) {}
      synchronized (zzcge.Gh)
      {
        if (xw == null) {
          xw = new zzf[0];
        }
        return xw;
      }
    }
    
    protected int computeSerializedSize()
    {
      int j = super.computeSerializedSize();
      int i = j;
      if (this.resourceId != 0) {
        i = j + zzcfy.zzac(1, this.resourceId);
      }
      j = i;
      if (this.xx != 0L) {
        j = i + zzcfy.zzk(2, this.xx);
      }
      i = j;
      if (this.zzaTl != null)
      {
        i = j;
        if (!this.zzaTl.equals("")) {
          i = j + zzcfy.zzv(3, this.zzaTl);
        }
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
          } while (this.resourceId != ((zzf)paramObject).resourceId);
          bool1 = bool2;
        } while (this.xx != ((zzf)paramObject).xx);
        if (this.zzaTl != null) {
          break;
        }
        bool1 = bool2;
      } while (((zzf)paramObject).zzaTl != null);
      for (;;)
      {
        if ((this.FZ == null) || (this.FZ.isEmpty()))
        {
          if (((zzf)paramObject).FZ != null)
          {
            bool1 = bool2;
            if (!((zzf)paramObject).FZ.isEmpty()) {
              break;
            }
          }
          return true;
          if (!this.zzaTl.equals(((zzf)paramObject).zzaTl)) {
            return false;
          }
        }
      }
      return this.FZ.equals(((zzf)paramObject).FZ);
    }
    
    public int hashCode()
    {
      int k = 0;
      int m = getClass().getName().hashCode();
      int n = this.resourceId;
      int i1 = (int)(this.xx ^ this.xx >>> 32);
      int i;
      if (this.zzaTl == null)
      {
        i = 0;
        j = k;
        if (this.FZ != null) {
          if (!this.FZ.isEmpty()) {
            break label105;
          }
        }
      }
      label105:
      for (int j = k;; j = this.FZ.hashCode())
      {
        return (i + (((m + 527) * 31 + n) * 31 + i1) * 31) * 31 + j;
        i = this.zzaTl.hashCode();
        break;
      }
    }
    
    public void writeTo(zzcfy paramzzcfy)
      throws IOException
    {
      if (this.resourceId != 0) {
        paramzzcfy.zzaa(1, this.resourceId);
      }
      if (this.xx != 0L) {
        paramzzcfy.zzg(2, this.xx);
      }
      if ((this.zzaTl != null) && (!this.zzaTl.equals(""))) {
        paramzzcfy.zzu(3, this.zzaTl);
      }
      super.writeTo(paramzzcfy);
    }
    
    public zzf zzakM()
    {
      this.resourceId = 0;
      this.xx = 0L;
      this.zzaTl = "";
      this.FZ = null;
      this.Gi = -1;
      return this;
    }
    
    public zzf zzan(zzcfx paramzzcfx)
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
          this.resourceId = paramzzcfx.zzamM();
          break;
        case 17: 
          this.xx = paramzzcfx.zzamN();
          break;
        case 26: 
          this.zzaTl = paramzzcfx.readString();
        }
      }
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzcbz.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */