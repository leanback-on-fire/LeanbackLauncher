package com.google.android.gms.internal;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteFullException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build.VERSION;
import android.os.Parcel;
import android.os.SystemClock;
import android.support.annotation.WorkerThread;
import java.io.File;

public class zzaty
  extends zzauk
{
  private final zza zzbNt = new zza(getContext(), zznV());
  private boolean zzbNu;
  
  zzaty(zzauh paramzzauh)
  {
    super(paramzzauh);
  }
  
  @TargetApi(11)
  @WorkerThread
  private boolean zza(int paramInt, byte[] paramArrayOfByte)
  {
    zzLR();
    zzmW();
    if (this.zzbNu) {
      return false;
    }
    ContentValues localContentValues = new ContentValues();
    localContentValues.put("type", Integer.valueOf(paramInt));
    localContentValues.put("entry", paramArrayOfByte);
    zzMi().zzNk();
    int i = 0;
    paramInt = 5;
    while (i < 5)
    {
      Object localObject3 = null;
      paramArrayOfByte = null;
      Object localObject1 = null;
      try
      {
        SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
        if (localSQLiteDatabase == null)
        {
          localObject1 = localSQLiteDatabase;
          localObject3 = localSQLiteDatabase;
          paramArrayOfByte = localSQLiteDatabase;
          this.zzbNu = true;
          return false;
        }
        localObject1 = localSQLiteDatabase;
        localObject3 = localSQLiteDatabase;
        paramArrayOfByte = localSQLiteDatabase;
        localSQLiteDatabase.beginTransaction();
        long l2 = 0L;
        localObject1 = localSQLiteDatabase;
        localObject3 = localSQLiteDatabase;
        paramArrayOfByte = localSQLiteDatabase;
        Cursor localCursor = localSQLiteDatabase.rawQuery("select count(1) from messages", null);
        long l1 = l2;
        if (localCursor != null)
        {
          l1 = l2;
          localObject1 = localSQLiteDatabase;
          localObject3 = localSQLiteDatabase;
          paramArrayOfByte = localSQLiteDatabase;
          if (localCursor.moveToFirst())
          {
            localObject1 = localSQLiteDatabase;
            localObject3 = localSQLiteDatabase;
            paramArrayOfByte = localSQLiteDatabase;
            l1 = localCursor.getLong(0);
          }
        }
        if (l1 >= 100000L)
        {
          localObject1 = localSQLiteDatabase;
          localObject3 = localSQLiteDatabase;
          paramArrayOfByte = localSQLiteDatabase;
          zzMg().zzNT().log("Data loss, local db full");
          l1 = 100000L - l1 + 1L;
          localObject1 = localSQLiteDatabase;
          localObject3 = localSQLiteDatabase;
          paramArrayOfByte = localSQLiteDatabase;
          l2 = localSQLiteDatabase.delete("messages", "rowid in (select rowid from messages order by rowid asc limit ?)", new String[] { Long.toString(l1) });
          if (l2 != l1)
          {
            localObject1 = localSQLiteDatabase;
            localObject3 = localSQLiteDatabase;
            paramArrayOfByte = localSQLiteDatabase;
            zzMg().zzNT().zzd("Different delete count than expected in local db. expected, received, difference", Long.valueOf(l1), Long.valueOf(l2), Long.valueOf(l1 - l2));
          }
        }
        localObject1 = localSQLiteDatabase;
        localObject3 = localSQLiteDatabase;
        paramArrayOfByte = localSQLiteDatabase;
        localSQLiteDatabase.insertOrThrow("messages", null, localContentValues);
        localObject1 = localSQLiteDatabase;
        localObject3 = localSQLiteDatabase;
        paramArrayOfByte = localSQLiteDatabase;
        localSQLiteDatabase.setTransactionSuccessful();
        localObject1 = localSQLiteDatabase;
        localObject3 = localSQLiteDatabase;
        paramArrayOfByte = localSQLiteDatabase;
        localSQLiteDatabase.endTransaction();
        return true;
      }
      catch (SQLiteFullException localSQLiteFullException)
      {
        paramArrayOfByte = (byte[])localObject1;
        zzMg().zzNT().zzm("Error writing entry to local database", localSQLiteFullException);
        paramArrayOfByte = (byte[])localObject1;
        this.zzbNu = true;
        j = paramInt;
        if (localObject1 != null)
        {
          ((SQLiteDatabase)localObject1).close();
          j = paramInt;
        }
        i += 1;
        paramInt = j;
      }
      catch (SQLiteException localSQLiteException)
      {
        int j;
        paramArrayOfByte = localSQLiteFullException;
        if (Build.VERSION.SDK_INT >= 11)
        {
          paramArrayOfByte = localSQLiteFullException;
          if ((localSQLiteException instanceof SQLiteDatabaseLockedException))
          {
            paramArrayOfByte = localSQLiteFullException;
            SystemClock.sleep(paramInt);
            paramInt += 20;
          }
        }
        for (;;)
        {
          j = paramInt;
          if (localSQLiteFullException == null) {
            break;
          }
          localSQLiteFullException.close();
          j = paramInt;
          break;
          if (localSQLiteFullException != null)
          {
            paramArrayOfByte = localSQLiteFullException;
            if (localSQLiteFullException.inTransaction())
            {
              paramArrayOfByte = localSQLiteFullException;
              localSQLiteFullException.endTransaction();
            }
          }
          paramArrayOfByte = localSQLiteFullException;
          zzMg().zzNT().zzm("Error writing entry to local database", localSQLiteException);
          paramArrayOfByte = localSQLiteFullException;
          this.zzbNu = true;
        }
      }
      finally
      {
        if (paramArrayOfByte != null) {
          paramArrayOfByte.close();
        }
      }
    }
    zzMg().zzNV().log("Failed to write entry to local database");
    return false;
  }
  
  @WorkerThread
  SQLiteDatabase getWritableDatabase()
  {
    int i = Build.VERSION.SDK_INT;
    if (this.zzbNu) {
      return null;
    }
    SQLiteDatabase localSQLiteDatabase = this.zzbNt.getWritableDatabase();
    if (localSQLiteDatabase == null)
    {
      this.zzbNu = true;
      return null;
    }
    return localSQLiteDatabase;
  }
  
  protected void onInitialize() {}
  
  boolean zzNH()
  {
    return getContext().getDatabasePath(zznV()).exists();
  }
  
  public boolean zza(zzatt paramzzatt)
  {
    int i = Build.VERSION.SDK_INT;
    Parcel localParcel = Parcel.obtain();
    paramzzatt.writeToParcel(localParcel, 0);
    paramzzatt = localParcel.marshall();
    localParcel.recycle();
    if (paramzzatt.length > 131072)
    {
      zzMg().zzNV().log("Event is too long for local database. Sending event directly to service");
      return false;
    }
    return zza(0, paramzzatt);
  }
  
  public boolean zza(zzaut paramzzaut)
  {
    int i = Build.VERSION.SDK_INT;
    Parcel localParcel = Parcel.obtain();
    paramzzaut.writeToParcel(localParcel, 0);
    paramzzaut = localParcel.marshall();
    localParcel.recycle();
    if (paramzzaut.length > 131072)
    {
      zzMg().zzNV().log("User property too long for local database. Sending directly to service");
      return false;
    }
    return zza(1, paramzzaut);
  }
  
  public boolean zzc(zzatj paramzzatj)
  {
    int i = Build.VERSION.SDK_INT;
    paramzzatj = zzMc().zza(paramzzatj);
    if (paramzzatj.length > 131072)
    {
      zzMg().zzNV().log("Conditional user property too long for local database. Sending directly to service");
      return false;
    }
    return zza(2, paramzzatj);
  }
  
  String zznV()
  {
    return zzMi().zzNa();
  }
  
  /* Error */
  @TargetApi(11)
  public java.util.List<com.google.android.gms.common.internal.safeparcel.zza> zzpM(int paramInt)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 45	com/google/android/gms/internal/zzaty:zzmW	()V
    //   4: aload_0
    //   5: invokevirtual 42	com/google/android/gms/internal/zzaty:zzLR	()V
    //   8: getstatic 173	android/os/Build$VERSION:SDK_INT	I
    //   11: istore_2
    //   12: aload_0
    //   13: getfield 47	com/google/android/gms/internal/zzaty:zzbNu	Z
    //   16: ifeq +5 -> 21
    //   19: aconst_null
    //   20: areturn
    //   21: new 325	java/util/ArrayList
    //   24: dup
    //   25: invokespecial 326	java/util/ArrayList:<init>	()V
    //   28: astore 12
    //   30: aload_0
    //   31: invokevirtual 328	com/google/android/gms/internal/zzaty:zzNH	()Z
    //   34: ifne +6 -> 40
    //   37: aload 12
    //   39: areturn
    //   40: iconst_5
    //   41: istore_2
    //   42: iconst_0
    //   43: istore 4
    //   45: iload 4
    //   47: iconst_5
    //   48: if_icmpge +711 -> 759
    //   51: aconst_null
    //   52: astore 11
    //   54: aconst_null
    //   55: astore 7
    //   57: aconst_null
    //   58: astore 10
    //   60: aload_0
    //   61: invokevirtual 82	com/google/android/gms/internal/zzaty:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   64: astore 8
    //   66: aload 8
    //   68: ifnonnull +20 -> 88
    //   71: aload_0
    //   72: iconst_1
    //   73: putfield 47	com/google/android/gms/internal/zzaty:zzbNu	Z
    //   76: aload 8
    //   78: ifnull +8 -> 86
    //   81: aload 8
    //   83: invokevirtual 87	android/database/sqlite/SQLiteDatabase:close	()V
    //   86: aconst_null
    //   87: areturn
    //   88: aload 8
    //   90: invokevirtual 90	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   93: iload_1
    //   94: invokestatic 331	java/lang/Integer:toString	(I)Ljava/lang/String;
    //   97: astore 7
    //   99: aload 8
    //   101: ldc -128
    //   103: iconst_3
    //   104: anewarray 132	java/lang/String
    //   107: dup
    //   108: iconst_0
    //   109: ldc_w 333
    //   112: aastore
    //   113: dup
    //   114: iconst_1
    //   115: ldc 53
    //   117: aastore
    //   118: dup
    //   119: iconst_2
    //   120: ldc 65
    //   122: aastore
    //   123: aconst_null
    //   124: aconst_null
    //   125: aconst_null
    //   126: aconst_null
    //   127: ldc_w 335
    //   130: aload 7
    //   132: invokevirtual 339	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   135: astore 9
    //   137: ldc2_w 340
    //   140: lstore 5
    //   142: aload 9
    //   144: invokeinterface 344 1 0
    //   149: ifeq +476 -> 625
    //   152: aload 9
    //   154: iconst_0
    //   155: invokeinterface 106 2 0
    //   160: lstore 5
    //   162: aload 9
    //   164: iconst_1
    //   165: invokeinterface 348 2 0
    //   170: istore_3
    //   171: aload 9
    //   173: iconst_2
    //   174: invokeinterface 352 2 0
    //   179: astore 11
    //   181: iload_3
    //   182: ifne +145 -> 327
    //   185: invokestatic 277	android/os/Parcel:obtain	()Landroid/os/Parcel;
    //   188: astore 7
    //   190: aload 7
    //   192: aload 11
    //   194: iconst_0
    //   195: aload 11
    //   197: arraylength
    //   198: invokevirtual 356	android/os/Parcel:unmarshall	([BII)V
    //   201: aload 7
    //   203: iconst_0
    //   204: invokevirtual 360	android/os/Parcel:setDataPosition	(I)V
    //   207: getstatic 364	com/google/android/gms/internal/zzatt:CREATOR	Landroid/os/Parcelable$Creator;
    //   210: aload 7
    //   212: invokeinterface 370 2 0
    //   217: checkcast 279	com/google/android/gms/internal/zzatt
    //   220: astore 10
    //   222: aload 7
    //   224: invokevirtual 290	android/os/Parcel:recycle	()V
    //   227: aload 10
    //   229: ifnull +13 -> 242
    //   232: aload 12
    //   234: aload 10
    //   236: invokeinterface 376 2 0
    //   241: pop
    //   242: goto -100 -> 142
    //   245: astore 10
    //   247: aload_0
    //   248: invokevirtual 112	com/google/android/gms/internal/zzaty:zzMg	()Lcom/google/android/gms/internal/zzaua;
    //   251: invokevirtual 118	com/google/android/gms/internal/zzaua:zzNT	()Lcom/google/android/gms/internal/zzaua$zza;
    //   254: ldc_w 378
    //   257: invokevirtual 126	com/google/android/gms/internal/zzaua$zza:log	(Ljava/lang/String;)V
    //   260: aload 7
    //   262: invokevirtual 290	android/os/Parcel:recycle	()V
    //   265: goto -123 -> 142
    //   268: astore 9
    //   270: aload 7
    //   272: invokevirtual 290	android/os/Parcel:recycle	()V
    //   275: aload 9
    //   277: athrow
    //   278: astore 9
    //   280: aload 8
    //   282: astore 7
    //   284: aload_0
    //   285: invokevirtual 112	com/google/android/gms/internal/zzaty:zzMg	()Lcom/google/android/gms/internal/zzaua;
    //   288: invokevirtual 118	com/google/android/gms/internal/zzaua:zzNT	()Lcom/google/android/gms/internal/zzaua$zza;
    //   291: ldc_w 380
    //   294: aload 9
    //   296: invokevirtual 167	com/google/android/gms/internal/zzaua$zza:zzm	(Ljava/lang/String;Ljava/lang/Object;)V
    //   299: aload 8
    //   301: astore 7
    //   303: aload_0
    //   304: iconst_1
    //   305: putfield 47	com/google/android/gms/internal/zzaty:zzbNu	Z
    //   308: aload 8
    //   310: ifnull +491 -> 801
    //   313: aload 8
    //   315: invokevirtual 87	android/database/sqlite/SQLiteDatabase:close	()V
    //   318: iload 4
    //   320: iconst_1
    //   321: iadd
    //   322: istore 4
    //   324: goto -279 -> 45
    //   327: iload_3
    //   328: iconst_1
    //   329: if_icmpne +179 -> 508
    //   332: invokestatic 277	android/os/Parcel:obtain	()Landroid/os/Parcel;
    //   335: astore 10
    //   337: aload 10
    //   339: aload 11
    //   341: iconst_0
    //   342: aload 11
    //   344: arraylength
    //   345: invokevirtual 356	android/os/Parcel:unmarshall	([BII)V
    //   348: aload 10
    //   350: iconst_0
    //   351: invokevirtual 360	android/os/Parcel:setDataPosition	(I)V
    //   354: getstatic 381	com/google/android/gms/internal/zzaut:CREATOR	Landroid/os/Parcelable$Creator;
    //   357: aload 10
    //   359: invokeinterface 370 2 0
    //   364: checkcast 298	com/google/android/gms/internal/zzaut
    //   367: astore 7
    //   369: aload 10
    //   371: invokevirtual 290	android/os/Parcel:recycle	()V
    //   374: aload 7
    //   376: ifnull -134 -> 242
    //   379: aload 12
    //   381: aload 7
    //   383: invokeinterface 376 2 0
    //   388: pop
    //   389: goto -147 -> 242
    //   392: astore 9
    //   394: aload 8
    //   396: astore 7
    //   398: getstatic 173	android/os/Build$VERSION:SDK_INT	I
    //   401: bipush 11
    //   403: if_icmplt +297 -> 700
    //   406: aload 8
    //   408: astore 7
    //   410: aload 9
    //   412: instanceof 175
    //   415: ifeq +285 -> 700
    //   418: aload 8
    //   420: astore 7
    //   422: iload_2
    //   423: i2l
    //   424: invokestatic 181	android/os/SystemClock:sleep	(J)V
    //   427: iload_2
    //   428: bipush 20
    //   430: iadd
    //   431: istore_3
    //   432: iload_3
    //   433: istore_2
    //   434: aload 8
    //   436: ifnull -118 -> 318
    //   439: aload 8
    //   441: invokevirtual 87	android/database/sqlite/SQLiteDatabase:close	()V
    //   444: iload_3
    //   445: istore_2
    //   446: goto -128 -> 318
    //   449: astore 7
    //   451: aload_0
    //   452: invokevirtual 112	com/google/android/gms/internal/zzaty:zzMg	()Lcom/google/android/gms/internal/zzaua;
    //   455: invokevirtual 118	com/google/android/gms/internal/zzaua:zzNT	()Lcom/google/android/gms/internal/zzaua$zza;
    //   458: ldc_w 383
    //   461: invokevirtual 126	com/google/android/gms/internal/zzaua$zza:log	(Ljava/lang/String;)V
    //   464: aload 10
    //   466: invokevirtual 290	android/os/Parcel:recycle	()V
    //   469: aconst_null
    //   470: astore 7
    //   472: goto -98 -> 374
    //   475: astore 7
    //   477: aload 10
    //   479: invokevirtual 290	android/os/Parcel:recycle	()V
    //   482: aload 7
    //   484: athrow
    //   485: astore 7
    //   487: aload 8
    //   489: astore 9
    //   491: aload 7
    //   493: astore 8
    //   495: aload 9
    //   497: ifnull +8 -> 505
    //   500: aload 9
    //   502: invokevirtual 87	android/database/sqlite/SQLiteDatabase:close	()V
    //   505: aload 8
    //   507: athrow
    //   508: iload_3
    //   509: iconst_2
    //   510: if_icmpne +99 -> 609
    //   513: invokestatic 277	android/os/Parcel:obtain	()Landroid/os/Parcel;
    //   516: astore 10
    //   518: aload 10
    //   520: aload 11
    //   522: iconst_0
    //   523: aload 11
    //   525: arraylength
    //   526: invokevirtual 356	android/os/Parcel:unmarshall	([BII)V
    //   529: aload 10
    //   531: iconst_0
    //   532: invokevirtual 360	android/os/Parcel:setDataPosition	(I)V
    //   535: getstatic 386	com/google/android/gms/internal/zzatj:CREATOR	Landroid/os/Parcelable$Creator;
    //   538: aload 10
    //   540: invokeinterface 370 2 0
    //   545: checkcast 385	com/google/android/gms/internal/zzatj
    //   548: astore 7
    //   550: aload 10
    //   552: invokevirtual 290	android/os/Parcel:recycle	()V
    //   555: aload 7
    //   557: ifnull -315 -> 242
    //   560: aload 12
    //   562: aload 7
    //   564: invokeinterface 376 2 0
    //   569: pop
    //   570: goto -328 -> 242
    //   573: astore 7
    //   575: aload_0
    //   576: invokevirtual 112	com/google/android/gms/internal/zzaty:zzMg	()Lcom/google/android/gms/internal/zzaua;
    //   579: invokevirtual 118	com/google/android/gms/internal/zzaua:zzNT	()Lcom/google/android/gms/internal/zzaua$zza;
    //   582: ldc_w 383
    //   585: invokevirtual 126	com/google/android/gms/internal/zzaua$zza:log	(Ljava/lang/String;)V
    //   588: aload 10
    //   590: invokevirtual 290	android/os/Parcel:recycle	()V
    //   593: aconst_null
    //   594: astore 7
    //   596: goto -41 -> 555
    //   599: astore 7
    //   601: aload 10
    //   603: invokevirtual 290	android/os/Parcel:recycle	()V
    //   606: aload 7
    //   608: athrow
    //   609: aload_0
    //   610: invokevirtual 112	com/google/android/gms/internal/zzaty:zzMg	()Lcom/google/android/gms/internal/zzaua;
    //   613: invokevirtual 118	com/google/android/gms/internal/zzaua:zzNT	()Lcom/google/android/gms/internal/zzaua$zza;
    //   616: ldc_w 388
    //   619: invokevirtual 126	com/google/android/gms/internal/zzaua$zza:log	(Ljava/lang/String;)V
    //   622: goto -380 -> 242
    //   625: aload 9
    //   627: invokeinterface 389 1 0
    //   632: aload 8
    //   634: ldc -128
    //   636: ldc_w 391
    //   639: iconst_1
    //   640: anewarray 132	java/lang/String
    //   643: dup
    //   644: iconst_0
    //   645: lload 5
    //   647: invokestatic 138	java/lang/Long:toString	(J)Ljava/lang/String;
    //   650: aastore
    //   651: invokevirtual 142	android/database/sqlite/SQLiteDatabase:delete	(Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;)I
    //   654: aload 12
    //   656: invokeinterface 394 1 0
    //   661: if_icmpge +16 -> 677
    //   664: aload_0
    //   665: invokevirtual 112	com/google/android/gms/internal/zzaty:zzMg	()Lcom/google/android/gms/internal/zzaua;
    //   668: invokevirtual 118	com/google/android/gms/internal/zzaua:zzNT	()Lcom/google/android/gms/internal/zzaua$zza;
    //   671: ldc_w 396
    //   674: invokevirtual 126	com/google/android/gms/internal/zzaua$zza:log	(Ljava/lang/String;)V
    //   677: aload 8
    //   679: invokevirtual 158	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   682: aload 8
    //   684: invokevirtual 161	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   687: aload 8
    //   689: ifnull +8 -> 697
    //   692: aload 8
    //   694: invokevirtual 87	android/database/sqlite/SQLiteDatabase:close	()V
    //   697: aload 12
    //   699: areturn
    //   700: aload 8
    //   702: ifnull +24 -> 726
    //   705: aload 8
    //   707: astore 7
    //   709: aload 8
    //   711: invokevirtual 184	android/database/sqlite/SQLiteDatabase:inTransaction	()Z
    //   714: ifeq +12 -> 726
    //   717: aload 8
    //   719: astore 7
    //   721: aload 8
    //   723: invokevirtual 161	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   726: aload 8
    //   728: astore 7
    //   730: aload_0
    //   731: invokevirtual 112	com/google/android/gms/internal/zzaty:zzMg	()Lcom/google/android/gms/internal/zzaua;
    //   734: invokevirtual 118	com/google/android/gms/internal/zzaua:zzNT	()Lcom/google/android/gms/internal/zzaua$zza;
    //   737: ldc_w 380
    //   740: aload 9
    //   742: invokevirtual 167	com/google/android/gms/internal/zzaua$zza:zzm	(Ljava/lang/String;Ljava/lang/Object;)V
    //   745: aload 8
    //   747: astore 7
    //   749: aload_0
    //   750: iconst_1
    //   751: putfield 47	com/google/android/gms/internal/zzaty:zzbNu	Z
    //   754: iload_2
    //   755: istore_3
    //   756: goto -324 -> 432
    //   759: aload_0
    //   760: invokevirtual 112	com/google/android/gms/internal/zzaty:zzMg	()Lcom/google/android/gms/internal/zzaua;
    //   763: invokevirtual 187	com/google/android/gms/internal/zzaua:zzNV	()Lcom/google/android/gms/internal/zzaua$zza;
    //   766: ldc_w 398
    //   769: invokevirtual 126	com/google/android/gms/internal/zzaua$zza:log	(Ljava/lang/String;)V
    //   772: aconst_null
    //   773: areturn
    //   774: astore 8
    //   776: aload 7
    //   778: astore 9
    //   780: goto -285 -> 495
    //   783: astore 9
    //   785: aload 11
    //   787: astore 8
    //   789: goto -395 -> 394
    //   792: astore 9
    //   794: aload 10
    //   796: astore 8
    //   798: goto -518 -> 280
    //   801: goto -483 -> 318
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	804	0	this	zzaty
    //   0	804	1	paramInt	int
    //   11	744	2	i	int
    //   170	586	3	j	int
    //   43	280	4	k	int
    //   140	506	5	l	long
    //   55	366	7	localObject1	Object
    //   449	1	7	localzza1	com.google.android.gms.common.internal.safeparcel.zzb.zza
    //   470	1	7	localObject2	Object
    //   475	8	7	localObject3	Object
    //   485	7	7	localObject4	Object
    //   548	15	7	localzzatj	zzatj
    //   573	1	7	localzza2	com.google.android.gms.common.internal.safeparcel.zzb.zza
    //   594	1	7	localObject5	Object
    //   599	8	7	localObject6	Object
    //   707	70	7	localObject7	Object
    //   64	682	8	localObject8	Object
    //   774	1	8	localObject9	Object
    //   787	10	8	localObject10	Object
    //   135	37	9	localCursor	Cursor
    //   268	8	9	localObject11	Object
    //   278	17	9	localSQLiteFullException1	SQLiteFullException
    //   392	19	9	localSQLiteException1	SQLiteException
    //   489	290	9	localObject12	Object
    //   783	1	9	localSQLiteException2	SQLiteException
    //   792	1	9	localSQLiteFullException2	SQLiteFullException
    //   58	177	10	localzzatt	zzatt
    //   245	1	10	localzza3	com.google.android.gms.common.internal.safeparcel.zzb.zza
    //   335	460	10	localParcel	Parcel
    //   52	734	11	arrayOfByte	byte[]
    //   28	670	12	localArrayList	java.util.ArrayList
    // Exception table:
    //   from	to	target	type
    //   190	222	245	com/google/android/gms/common/internal/safeparcel/zzb$zza
    //   190	222	268	finally
    //   247	260	268	finally
    //   71	76	278	android/database/sqlite/SQLiteFullException
    //   88	137	278	android/database/sqlite/SQLiteFullException
    //   142	181	278	android/database/sqlite/SQLiteFullException
    //   185	190	278	android/database/sqlite/SQLiteFullException
    //   222	227	278	android/database/sqlite/SQLiteFullException
    //   232	242	278	android/database/sqlite/SQLiteFullException
    //   260	265	278	android/database/sqlite/SQLiteFullException
    //   270	278	278	android/database/sqlite/SQLiteFullException
    //   332	337	278	android/database/sqlite/SQLiteFullException
    //   369	374	278	android/database/sqlite/SQLiteFullException
    //   379	389	278	android/database/sqlite/SQLiteFullException
    //   464	469	278	android/database/sqlite/SQLiteFullException
    //   477	485	278	android/database/sqlite/SQLiteFullException
    //   513	518	278	android/database/sqlite/SQLiteFullException
    //   550	555	278	android/database/sqlite/SQLiteFullException
    //   560	570	278	android/database/sqlite/SQLiteFullException
    //   588	593	278	android/database/sqlite/SQLiteFullException
    //   601	609	278	android/database/sqlite/SQLiteFullException
    //   609	622	278	android/database/sqlite/SQLiteFullException
    //   625	677	278	android/database/sqlite/SQLiteFullException
    //   677	687	278	android/database/sqlite/SQLiteFullException
    //   71	76	392	android/database/sqlite/SQLiteException
    //   88	137	392	android/database/sqlite/SQLiteException
    //   142	181	392	android/database/sqlite/SQLiteException
    //   185	190	392	android/database/sqlite/SQLiteException
    //   222	227	392	android/database/sqlite/SQLiteException
    //   232	242	392	android/database/sqlite/SQLiteException
    //   260	265	392	android/database/sqlite/SQLiteException
    //   270	278	392	android/database/sqlite/SQLiteException
    //   332	337	392	android/database/sqlite/SQLiteException
    //   369	374	392	android/database/sqlite/SQLiteException
    //   379	389	392	android/database/sqlite/SQLiteException
    //   464	469	392	android/database/sqlite/SQLiteException
    //   477	485	392	android/database/sqlite/SQLiteException
    //   513	518	392	android/database/sqlite/SQLiteException
    //   550	555	392	android/database/sqlite/SQLiteException
    //   560	570	392	android/database/sqlite/SQLiteException
    //   588	593	392	android/database/sqlite/SQLiteException
    //   601	609	392	android/database/sqlite/SQLiteException
    //   609	622	392	android/database/sqlite/SQLiteException
    //   625	677	392	android/database/sqlite/SQLiteException
    //   677	687	392	android/database/sqlite/SQLiteException
    //   337	369	449	com/google/android/gms/common/internal/safeparcel/zzb$zza
    //   337	369	475	finally
    //   451	464	475	finally
    //   71	76	485	finally
    //   88	137	485	finally
    //   142	181	485	finally
    //   185	190	485	finally
    //   222	227	485	finally
    //   232	242	485	finally
    //   260	265	485	finally
    //   270	278	485	finally
    //   332	337	485	finally
    //   369	374	485	finally
    //   379	389	485	finally
    //   464	469	485	finally
    //   477	485	485	finally
    //   513	518	485	finally
    //   550	555	485	finally
    //   560	570	485	finally
    //   588	593	485	finally
    //   601	609	485	finally
    //   609	622	485	finally
    //   625	677	485	finally
    //   677	687	485	finally
    //   518	550	573	com/google/android/gms/common/internal/safeparcel/zzb$zza
    //   518	550	599	finally
    //   575	588	599	finally
    //   60	66	774	finally
    //   284	299	774	finally
    //   303	308	774	finally
    //   398	406	774	finally
    //   410	418	774	finally
    //   422	427	774	finally
    //   709	717	774	finally
    //   721	726	774	finally
    //   730	745	774	finally
    //   749	754	774	finally
    //   60	66	783	android/database/sqlite/SQLiteException
    //   60	66	792	android/database/sqlite/SQLiteFullException
  }
  
  @TargetApi(11)
  private class zza
    extends SQLiteOpenHelper
  {
    zza(Context paramContext, String paramString)
    {
      super(paramString, null, 1);
    }
    
    @WorkerThread
    public SQLiteDatabase getWritableDatabase()
    {
      try
      {
        SQLiteDatabase localSQLiteDatabase = super.getWritableDatabase();
        return localSQLiteDatabase;
      }
      catch (SQLiteException localSQLiteException1)
      {
        if ((Build.VERSION.SDK_INT >= 11) && ((localSQLiteException1 instanceof SQLiteDatabaseLockedException))) {
          throw localSQLiteException1;
        }
        zzaty.this.zzMg().zzNT().log("Opening the local database failed, dropping and recreating it");
        Object localObject = zzaty.this.zznV();
        if (!zzaty.this.getContext().getDatabasePath((String)localObject).delete()) {
          zzaty.this.zzMg().zzNT().zzm("Failed to delete corrupted local db file", localObject);
        }
        try
        {
          localObject = super.getWritableDatabase();
          return (SQLiteDatabase)localObject;
        }
        catch (SQLiteException localSQLiteException2)
        {
          zzaty.this.zzMg().zzNT().zzm("Failed to open local database. Events will bypass local storage", localSQLiteException2);
        }
      }
      return null;
    }
    
    @WorkerThread
    public void onCreate(SQLiteDatabase paramSQLiteDatabase)
    {
      zzatm.zza(zzaty.this.zzMg(), paramSQLiteDatabase);
    }
    
    @WorkerThread
    public void onOpen(SQLiteDatabase paramSQLiteDatabase)
    {
      Cursor localCursor;
      if (Build.VERSION.SDK_INT < 15) {
        localCursor = paramSQLiteDatabase.rawQuery("PRAGMA journal_mode=memory", null);
      }
      try
      {
        localCursor.moveToFirst();
        localCursor.close();
        zzatm.zza(zzaty.this.zzMg(), paramSQLiteDatabase, "messages", "create table if not exists messages ( type INTEGER NOT NULL, entry BLOB NOT NULL)", "type,entry", null);
        return;
      }
      finally
      {
        localCursor.close();
      }
    }
    
    @WorkerThread
    public void onUpgrade(SQLiteDatabase paramSQLiteDatabase, int paramInt1, int paramInt2) {}
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gms/internal/zzaty.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */