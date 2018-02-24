package android.support.v4.net;

import android.net.TrafficStats;
import android.os.Build.VERSION;
import android.os.ParcelFileDescriptor;
import android.support.annotation.RequiresApi;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;

public final class TrafficStatsCompat
{
  private static final TrafficStatsCompatBaseImpl IMPL = new TrafficStatsCompatBaseImpl();
  
  static
  {
    if (Build.VERSION.SDK_INT >= 24)
    {
      IMPL = new TrafficStatsCompatApi24Impl();
      return;
    }
  }
  
  @Deprecated
  public static void clearThreadStatsTag() {}
  
  @Deprecated
  public static int getThreadStatsTag()
  {
    return TrafficStats.getThreadStatsTag();
  }
  
  @Deprecated
  public static void incrementOperationCount(int paramInt)
  {
    TrafficStats.incrementOperationCount(paramInt);
  }
  
  @Deprecated
  public static void incrementOperationCount(int paramInt1, int paramInt2)
  {
    TrafficStats.incrementOperationCount(paramInt1, paramInt2);
  }
  
  @Deprecated
  public static void setThreadStatsTag(int paramInt)
  {
    TrafficStats.setThreadStatsTag(paramInt);
  }
  
  public static void tagDatagramSocket(DatagramSocket paramDatagramSocket)
    throws SocketException
  {
    IMPL.tagDatagramSocket(paramDatagramSocket);
  }
  
  @Deprecated
  public static void tagSocket(Socket paramSocket)
    throws SocketException
  {
    TrafficStats.tagSocket(paramSocket);
  }
  
  public static void untagDatagramSocket(DatagramSocket paramDatagramSocket)
    throws SocketException
  {
    IMPL.untagDatagramSocket(paramDatagramSocket);
  }
  
  @Deprecated
  public static void untagSocket(Socket paramSocket)
    throws SocketException
  {
    TrafficStats.untagSocket(paramSocket);
  }
  
  @RequiresApi(24)
  static class TrafficStatsCompatApi24Impl
    extends TrafficStatsCompat.TrafficStatsCompatBaseImpl
  {
    public void tagDatagramSocket(DatagramSocket paramDatagramSocket)
      throws SocketException
    {
      TrafficStats.tagDatagramSocket(paramDatagramSocket);
    }
    
    public void untagDatagramSocket(DatagramSocket paramDatagramSocket)
      throws SocketException
    {
      TrafficStats.untagDatagramSocket(paramDatagramSocket);
    }
  }
  
  static class TrafficStatsCompatBaseImpl
  {
    public void tagDatagramSocket(DatagramSocket paramDatagramSocket)
      throws SocketException
    {
      ParcelFileDescriptor localParcelFileDescriptor = ParcelFileDescriptor.fromDatagramSocket(paramDatagramSocket);
      TrafficStats.tagSocket(new DatagramSocketWrapper(paramDatagramSocket, localParcelFileDescriptor.getFileDescriptor()));
      localParcelFileDescriptor.detachFd();
    }
    
    public void untagDatagramSocket(DatagramSocket paramDatagramSocket)
      throws SocketException
    {
      ParcelFileDescriptor localParcelFileDescriptor = ParcelFileDescriptor.fromDatagramSocket(paramDatagramSocket);
      TrafficStats.untagSocket(new DatagramSocketWrapper(paramDatagramSocket, localParcelFileDescriptor.getFileDescriptor()));
      localParcelFileDescriptor.detachFd();
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v4/net/TrafficStatsCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */