package com.google.protobuf.nano;

import java.io.IOException;

public abstract class ExtendableMessageNano<M extends ExtendableMessageNano<M>>
  extends MessageNano
{
  protected FieldArray unknownFieldData;
  
  public M clone()
    throws CloneNotSupportedException
  {
    ExtendableMessageNano localExtendableMessageNano = (ExtendableMessageNano)super.clone();
    InternalNano.cloneUnknownFieldData(this, localExtendableMessageNano);
    return localExtendableMessageNano;
  }
  
  protected int computeSerializedSize()
  {
    int k = 0;
    int i = 0;
    if (this.unknownFieldData != null)
    {
      int j = 0;
      for (;;)
      {
        k = i;
        if (j >= this.unknownFieldData.size()) {
          break;
        }
        i += this.unknownFieldData.dataAt(j).computeSerializedSize();
        j += 1;
      }
    }
    return k;
  }
  
  public final <T> T getExtension(Extension<M, T> paramExtension)
  {
    if (this.unknownFieldData == null) {}
    FieldData localFieldData;
    do
    {
      return null;
      localFieldData = this.unknownFieldData.get(WireFormatNano.getTagFieldNumber(paramExtension.tag));
    } while (localFieldData == null);
    return (T)localFieldData.getValue(paramExtension);
  }
  
  public final boolean hasExtension(Extension<M, ?> paramExtension)
  {
    if (this.unknownFieldData == null) {}
    while (this.unknownFieldData.get(WireFormatNano.getTagFieldNumber(paramExtension.tag)) == null) {
      return false;
    }
    return true;
  }
  
  public final <T> M setExtension(Extension<M, T> paramExtension, T paramT)
  {
    int i = WireFormatNano.getTagFieldNumber(paramExtension.tag);
    if (paramT == null) {
      if (this.unknownFieldData != null)
      {
        this.unknownFieldData.remove(i);
        if (this.unknownFieldData.isEmpty()) {
          this.unknownFieldData = null;
        }
      }
    }
    for (;;)
    {
      return this;
      FieldData localFieldData = null;
      if (this.unknownFieldData == null) {
        this.unknownFieldData = new FieldArray();
      }
      for (;;)
      {
        if (localFieldData != null) {
          break label103;
        }
        this.unknownFieldData.put(i, new FieldData(paramExtension, paramT));
        break;
        localFieldData = this.unknownFieldData.get(i);
      }
      label103:
      localFieldData.setValue(paramExtension, paramT);
    }
  }
  
  protected final boolean storeUnknownField(CodedInputByteBufferNano paramCodedInputByteBufferNano, int paramInt)
    throws IOException
  {
    int i = paramCodedInputByteBufferNano.getPosition();
    if (!paramCodedInputByteBufferNano.skipField(paramInt)) {
      return false;
    }
    int j = WireFormatNano.getTagFieldNumber(paramInt);
    UnknownFieldData localUnknownFieldData = new UnknownFieldData(paramInt, paramCodedInputByteBufferNano.getData(i, paramCodedInputByteBufferNano.getPosition() - i));
    paramCodedInputByteBufferNano = null;
    if (this.unknownFieldData == null) {
      this.unknownFieldData = new FieldArray();
    }
    for (;;)
    {
      Object localObject = paramCodedInputByteBufferNano;
      if (paramCodedInputByteBufferNano == null)
      {
        localObject = new FieldData();
        this.unknownFieldData.put(j, (FieldData)localObject);
      }
      ((FieldData)localObject).addUnknownField(localUnknownFieldData);
      return true;
      paramCodedInputByteBufferNano = this.unknownFieldData.get(j);
    }
  }
  
  public void writeTo(CodedOutputByteBufferNano paramCodedOutputByteBufferNano)
    throws IOException
  {
    if (this.unknownFieldData == null) {}
    for (;;)
    {
      return;
      int i = 0;
      while (i < this.unknownFieldData.size())
      {
        this.unknownFieldData.dataAt(i).writeTo(paramCodedOutputByteBufferNano);
        i += 1;
      }
    }
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/protobuf/nano/ExtendableMessageNano.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */