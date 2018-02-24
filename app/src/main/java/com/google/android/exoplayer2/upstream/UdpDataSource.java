package com.google.android.exoplayer2.upstream;

import android.net.Uri;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.SocketException;

public final class UdpDataSource
  implements DataSource
{
  public static final int DEAFULT_SOCKET_TIMEOUT_MILLIS = 8000;
  public static final int DEFAULT_MAX_PACKET_SIZE = 2000;
  private InetAddress address;
  private final TransferListener<? super UdpDataSource> listener;
  private MulticastSocket multicastSocket;
  private boolean opened;
  private final DatagramPacket packet;
  private final byte[] packetBuffer;
  private int packetRemaining;
  private DatagramSocket socket;
  private InetSocketAddress socketAddress;
  private final int socketTimeoutMillis;
  private Uri uri;
  
  public UdpDataSource(TransferListener<? super UdpDataSource> paramTransferListener)
  {
    this(paramTransferListener, 2000);
  }
  
  public UdpDataSource(TransferListener<? super UdpDataSource> paramTransferListener, int paramInt)
  {
    this(paramTransferListener, paramInt, 8000);
  }
  
  public UdpDataSource(TransferListener<? super UdpDataSource> paramTransferListener, int paramInt1, int paramInt2)
  {
    this.listener = paramTransferListener;
    this.socketTimeoutMillis = paramInt2;
    this.packetBuffer = new byte[paramInt1];
    this.packet = new DatagramPacket(this.packetBuffer, 0, paramInt1);
  }
  
  public void close()
  {
    this.uri = null;
    if (this.multicastSocket != null) {}
    try
    {
      this.multicastSocket.leaveGroup(this.address);
      this.multicastSocket = null;
      if (this.socket != null)
      {
        this.socket.close();
        this.socket = null;
      }
      this.address = null;
      this.socketAddress = null;
      this.packetRemaining = 0;
      if (this.opened)
      {
        this.opened = false;
        if (this.listener != null) {
          this.listener.onTransferEnd(this);
        }
      }
      return;
    }
    catch (IOException localIOException)
    {
      for (;;) {}
    }
  }
  
  public Uri getUri()
  {
    return this.uri;
  }
  
  public long open(DataSpec paramDataSpec)
    throws UdpDataSource.UdpDataSourceException
  {
    this.uri = paramDataSpec.uri;
    String str = this.uri.getHost();
    int i = this.uri.getPort();
    for (;;)
    {
      try
      {
        this.address = InetAddress.getByName(str);
        this.socketAddress = new InetSocketAddress(this.address, i);
        if (this.address.isMulticastAddress())
        {
          this.multicastSocket = new MulticastSocket(this.socketAddress);
          this.multicastSocket.joinGroup(this.address);
          this.socket = this.multicastSocket;
        }
      }
      catch (IOException paramDataSpec)
      {
        throw new UdpDataSourceException(paramDataSpec);
      }
      try
      {
        this.socket.setSoTimeout(this.socketTimeoutMillis);
        this.opened = true;
        if (this.listener != null) {
          this.listener.onTransferStart(this, paramDataSpec);
        }
        return -1L;
      }
      catch (SocketException paramDataSpec)
      {
        throw new UdpDataSourceException(paramDataSpec);
      }
      this.socket = new DatagramSocket(this.socketAddress);
    }
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws UdpDataSource.UdpDataSourceException
  {
    if (paramInt2 == 0) {
      return 0;
    }
    if (this.packetRemaining == 0) {}
    try
    {
      this.socket.receive(this.packet);
      this.packetRemaining = this.packet.getLength();
      if (this.listener != null) {
        this.listener.onBytesTransferred(this, this.packetRemaining);
      }
      int i = this.packet.getLength();
      int j = this.packetRemaining;
      paramInt2 = Math.min(this.packetRemaining, paramInt2);
      System.arraycopy(this.packetBuffer, i - j, paramArrayOfByte, paramInt1, paramInt2);
      this.packetRemaining -= paramInt2;
      return paramInt2;
    }
    catch (IOException paramArrayOfByte)
    {
      throw new UdpDataSourceException(paramArrayOfByte);
    }
  }
  
  public static final class UdpDataSourceException
    extends IOException
  {
    public UdpDataSourceException(IOException paramIOException)
    {
      super();
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/upstream/UdpDataSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */