package com.google.protobuf.nano;

import java.io.IOException;
import java.util.Arrays;

final class UnknownFieldData
{
  final byte[] bytes;
  final int tag;
  
  UnknownFieldData(int paramInt, byte[] paramArrayOfByte)
  {
    this.tag = paramInt;
    this.bytes = paramArrayOfByte;
  }
  
  int computeSerializedSize()
  {
    return 0 + CodedOutputByteBufferNano.computeRawVarint32Size(this.tag) + this.bytes.length;
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {}
    do
    {
      return true;
      if (!(paramObject instanceof UnknownFieldData)) {
        return false;
      }
      paramObject = (UnknownFieldData)paramObject;
    } while ((this.tag == ((UnknownFieldData)paramObject).tag) && (Arrays.equals(this.bytes, ((UnknownFieldData)paramObject).bytes)));
    return false;
  }
  
  public int hashCode()
  {
    return (this.tag + 527) * 31 + Arrays.hashCode(this.bytes);
  }
  
  void writeTo(CodedOutputByteBufferNano paramCodedOutputByteBufferNano)
    throws IOException
  {
    paramCodedOutputByteBufferNano.writeRawVarint32(this.tag);
    paramCodedOutputByteBufferNano.writeRawBytes(this.bytes);
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/protobuf/nano/UnknownFieldData.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */