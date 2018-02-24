package com.bumptech.glide.load.data;

import android.text.TextUtils;
import android.util.Log;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.HttpException;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.util.ContentLengthInputStream;
import com.bumptech.glide.util.LogTime;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class HttpUrlFetcher
  implements DataFetcher<InputStream>
{
  static final HttpUrlConnectionFactory DEFAULT_CONNECTION_FACTORY = new DefaultHttpUrlConnectionFactory(null);
  private static final int MAXIMUM_REDIRECTS = 5;
  private static final String TAG = "HttpUrlFetcher";
  private final HttpUrlConnectionFactory connectionFactory;
  private final GlideUrl glideUrl;
  private volatile boolean isCancelled;
  private InputStream stream;
  private final int timeout;
  private HttpURLConnection urlConnection;
  
  public HttpUrlFetcher(GlideUrl paramGlideUrl, int paramInt)
  {
    this(paramGlideUrl, paramInt, DEFAULT_CONNECTION_FACTORY);
  }
  
  HttpUrlFetcher(GlideUrl paramGlideUrl, int paramInt, HttpUrlConnectionFactory paramHttpUrlConnectionFactory)
  {
    this.glideUrl = paramGlideUrl;
    this.timeout = paramInt;
    this.connectionFactory = paramHttpUrlConnectionFactory;
  }
  
  private InputStream getStreamForSuccessfulRequest(HttpURLConnection paramHttpURLConnection)
    throws IOException
  {
    if (TextUtils.isEmpty(paramHttpURLConnection.getContentEncoding()))
    {
      int i = paramHttpURLConnection.getContentLength();
      this.stream = ContentLengthInputStream.obtain(paramHttpURLConnection.getInputStream(), i);
      return this.stream;
    }
    if (Log.isLoggable("HttpUrlFetcher", 3))
    {
      str = String.valueOf(paramHttpURLConnection.getContentEncoding());
      if (str.length() == 0) {
        break label82;
      }
    }
    label82:
    for (String str = "Got non empty content encoding: ".concat(str);; str = new String("Got non empty content encoding: "))
    {
      Log.d("HttpUrlFetcher", str);
      this.stream = paramHttpURLConnection.getInputStream();
      break;
    }
  }
  
  private InputStream loadDataWithRedirects(URL paramURL1, int paramInt, URL paramURL2, Map<String, String> paramMap)
    throws IOException
  {
    if (paramInt >= 5) {
      throw new HttpException("Too many (> 5) redirects!");
    }
    if (paramURL2 != null) {
      try
      {
        if (paramURL1.toURI().equals(paramURL2.toURI())) {
          throw new HttpException("In re-direct loop");
        }
      }
      catch (URISyntaxException paramURL2) {}
    }
    this.urlConnection = this.connectionFactory.build(paramURL1);
    paramURL2 = paramMap.entrySet().iterator();
    while (paramURL2.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)paramURL2.next();
      this.urlConnection.addRequestProperty((String)localEntry.getKey(), (String)localEntry.getValue());
    }
    this.urlConnection.setConnectTimeout(this.timeout);
    this.urlConnection.setReadTimeout(this.timeout);
    this.urlConnection.setUseCaches(false);
    this.urlConnection.setDoInput(true);
    this.urlConnection.connect();
    if (this.isCancelled) {
      return null;
    }
    int i = this.urlConnection.getResponseCode();
    if (i / 100 == 2) {
      return getStreamForSuccessfulRequest(this.urlConnection);
    }
    if (i / 100 == 3)
    {
      paramURL2 = this.urlConnection.getHeaderField("Location");
      if (TextUtils.isEmpty(paramURL2)) {
        throw new HttpException("Received empty or null redirect url");
      }
      return loadDataWithRedirects(new URL(paramURL1, paramURL2), paramInt + 1, paramURL1, paramMap);
    }
    if (i == -1) {
      throw new HttpException(i);
    }
    throw new HttpException(this.urlConnection.getResponseMessage(), i);
  }
  
  public void cancel()
  {
    this.isCancelled = true;
  }
  
  public void cleanup()
  {
    if (this.stream != null) {}
    try
    {
      this.stream.close();
      if (this.urlConnection != null) {
        this.urlConnection.disconnect();
      }
      return;
    }
    catch (IOException localIOException)
    {
      for (;;) {}
    }
  }
  
  public Class<InputStream> getDataClass()
  {
    return InputStream.class;
  }
  
  public DataSource getDataSource()
  {
    return DataSource.REMOTE;
  }
  
  public void loadData(Priority paramPriority, DataFetcher.DataCallback<? super InputStream> paramDataCallback)
  {
    long l = LogTime.getLogTime();
    try
    {
      paramPriority = loadDataWithRedirects(this.glideUrl.toURL(), 0, null, this.glideUrl.getHeaders());
      if (Log.isLoggable("HttpUrlFetcher", 2))
      {
        double d = LogTime.getElapsedMillis(l);
        String str = String.valueOf(paramPriority);
        Log.v("HttpUrlFetcher", String.valueOf(str).length() + 74 + "Finished http url fetcher fetch in " + d + " ms and loaded " + str);
      }
      paramDataCallback.onDataReady(paramPriority);
      return;
    }
    catch (IOException paramPriority)
    {
      if (Log.isLoggable("HttpUrlFetcher", 3)) {
        Log.d("HttpUrlFetcher", "Failed to load data for url", paramPriority);
      }
      paramDataCallback.onLoadFailed(paramPriority);
    }
  }
  
  private static class DefaultHttpUrlConnectionFactory
    implements HttpUrlFetcher.HttpUrlConnectionFactory
  {
    public HttpURLConnection build(URL paramURL)
      throws IOException
    {
      return (HttpURLConnection)paramURL.openConnection();
    }
  }
  
  static abstract interface HttpUrlConnectionFactory
  {
    public abstract HttpURLConnection build(URL paramURL)
      throws IOException;
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/data/HttpUrlFetcher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */