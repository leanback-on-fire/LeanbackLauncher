package com.google.protobuf.nano.android;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.util.Log;
import com.google.protobuf.nano.InvalidProtocolBufferNanoException;
import com.google.protobuf.nano.MessageNano;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public final class ParcelableMessageNanoCreator<T extends MessageNano>
  implements Parcelable.Creator<T>
{
  private static final String TAG = "PMNCreator";
  private final Class<T> mClazz;
  
  public ParcelableMessageNanoCreator(Class<T> paramClass)
  {
    this.mClazz = paramClass;
  }
  
  static <T extends MessageNano> void writeToParcel(Class<T> paramClass, MessageNano paramMessageNano, Parcel paramParcel)
  {
    paramParcel.writeString(paramClass.getName());
    paramParcel.writeByteArray(MessageNano.toByteArray(paramMessageNano));
  }
  
  public T createFromParcel(Parcel paramParcel)
  {
    String str = paramParcel.readString();
    byte[] arrayOfByte = paramParcel.createByteArray();
    Parcel localParcel2 = null;
    Parcel localParcel3 = null;
    Parcel localParcel4 = null;
    Parcel localParcel5 = null;
    Parcel localParcel6 = null;
    Parcel localParcel1 = null;
    try
    {
      paramParcel = (MessageNano)Class.forName(str, false, getClass().getClassLoader()).asSubclass(MessageNano.class).getConstructor(new Class[0]).newInstance(new Object[0]);
      localParcel1 = paramParcel;
      localParcel2 = paramParcel;
      localParcel3 = paramParcel;
      localParcel4 = paramParcel;
      localParcel5 = paramParcel;
      localParcel6 = paramParcel;
      MessageNano.mergeFrom(paramParcel, arrayOfByte);
      return paramParcel;
    }
    catch (ClassNotFoundException paramParcel)
    {
      Log.e("PMNCreator", "Exception trying to create proto from parcel", paramParcel);
      return localParcel1;
    }
    catch (NoSuchMethodException paramParcel)
    {
      Log.e("PMNCreator", "Exception trying to create proto from parcel", paramParcel);
      return localParcel2;
    }
    catch (InvocationTargetException paramParcel)
    {
      Log.e("PMNCreator", "Exception trying to create proto from parcel", paramParcel);
      return localParcel3;
    }
    catch (IllegalAccessException paramParcel)
    {
      Log.e("PMNCreator", "Exception trying to create proto from parcel", paramParcel);
      return localParcel4;
    }
    catch (InstantiationException paramParcel)
    {
      Log.e("PMNCreator", "Exception trying to create proto from parcel", paramParcel);
      return localParcel5;
    }
    catch (InvalidProtocolBufferNanoException paramParcel)
    {
      Log.e("PMNCreator", "Exception trying to create proto from parcel", paramParcel);
    }
    return localParcel6;
  }
  
  public T[] newArray(int paramInt)
  {
    return (MessageNano[])Array.newInstance(this.mClazz, paramInt);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/protobuf/nano/android/ParcelableMessageNanoCreator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */