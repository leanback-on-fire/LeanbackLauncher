package com.google.android.exoplayer2.metadata.emsg;

import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.metadata.Metadata.Entry;
import com.google.android.exoplayer2.metadata.MetadataDecoder;
import com.google.android.exoplayer2.metadata.MetadataInputBuffer;
import com.google.android.exoplayer2.util.ParsableByteArray;
import java.nio.ByteBuffer;
import java.util.Arrays;

public final class EventMessageDecoder
  implements MetadataDecoder
{
  public Metadata decode(MetadataInputBuffer paramMetadataInputBuffer)
  {
    Object localObject = paramMetadataInputBuffer.data;
    paramMetadataInputBuffer = ((ByteBuffer)localObject).array();
    int i = ((ByteBuffer)localObject).limit();
    localObject = new ParsableByteArray(paramMetadataInputBuffer, i);
    String str1 = ((ParsableByteArray)localObject).readNullTerminatedString();
    String str2 = ((ParsableByteArray)localObject).readNullTerminatedString();
    long l = ((ParsableByteArray)localObject).readUnsignedInt();
    ((ParsableByteArray)localObject).skipBytes(4);
    return new Metadata(new Metadata.Entry[] { new EventMessage(str1, str2, ((ParsableByteArray)localObject).readUnsignedInt() * 1000L / l, ((ParsableByteArray)localObject).readUnsignedInt(), Arrays.copyOfRange(paramMetadataInputBuffer, ((ParsableByteArray)localObject).getPosition(), i)) });
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/metadata/emsg/EventMessageDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */