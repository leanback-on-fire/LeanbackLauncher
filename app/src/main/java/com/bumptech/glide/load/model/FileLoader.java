package com.bumptech.glide.load.model;

import android.os.ParcelFileDescriptor;
import android.util.Log;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.data.DataFetcher.DataCallback;
import com.bumptech.glide.signature.ObjectKey;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class FileLoader<Data>
  implements ModelLoader<File, Data>
{
  private static final String TAG = "FileLoader";
  private final FileOpener<Data> fileOpener;
  
  public FileLoader(FileOpener<Data> paramFileOpener)
  {
    this.fileOpener = paramFileOpener;
  }
  
  public ModelLoader.LoadData<Data> buildLoadData(File paramFile, int paramInt1, int paramInt2, Options paramOptions)
  {
    return new ModelLoader.LoadData(new ObjectKey(paramFile), new FileFetcher(paramFile, this.fileOpener));
  }
  
  public boolean handles(File paramFile)
  {
    return true;
  }
  
  public static class Factory<Data>
    implements ModelLoaderFactory<File, Data>
  {
    private final FileLoader.FileOpener<Data> opener;
    
    public Factory(FileLoader.FileOpener<Data> paramFileOpener)
    {
      this.opener = paramFileOpener;
    }
    
    public final ModelLoader<File, Data> build(MultiModelLoaderFactory paramMultiModelLoaderFactory)
    {
      return new FileLoader(this.opener);
    }
    
    public final void teardown() {}
  }
  
  public static class FileDescriptorFactory
    extends FileLoader.Factory<ParcelFileDescriptor>
  {
    public FileDescriptorFactory()
    {
      super()
      {
        public void close(ParcelFileDescriptor paramAnonymousParcelFileDescriptor)
          throws IOException
        {
          paramAnonymousParcelFileDescriptor.close();
        }
        
        public Class<ParcelFileDescriptor> getDataClass()
        {
          return ParcelFileDescriptor.class;
        }
        
        public ParcelFileDescriptor open(File paramAnonymousFile)
          throws FileNotFoundException
        {
          return ParcelFileDescriptor.open(paramAnonymousFile, 268435456);
        }
      };
    }
  }
  
  private static class FileFetcher<Data>
    implements DataFetcher<Data>
  {
    private Data data;
    private final File file;
    private final FileLoader.FileOpener<Data> opener;
    
    public FileFetcher(File paramFile, FileLoader.FileOpener<Data> paramFileOpener)
    {
      this.file = paramFile;
      this.opener = paramFileOpener;
    }
    
    public void cancel() {}
    
    public void cleanup()
    {
      if (this.data != null) {}
      try
      {
        this.opener.close(this.data);
        return;
      }
      catch (IOException localIOException) {}
    }
    
    public Class<Data> getDataClass()
    {
      return this.opener.getDataClass();
    }
    
    public DataSource getDataSource()
    {
      return DataSource.LOCAL;
    }
    
    public void loadData(Priority paramPriority, DataFetcher.DataCallback<? super Data> paramDataCallback)
    {
      try
      {
        this.data = this.opener.open(this.file);
        paramDataCallback.onDataReady(this.data);
        return;
      }
      catch (FileNotFoundException paramPriority)
      {
        if (Log.isLoggable("FileLoader", 3)) {
          Log.d("FileLoader", "Failed to open file", paramPriority);
        }
        paramDataCallback.onLoadFailed(paramPriority);
      }
    }
  }
  
  public static abstract interface FileOpener<Data>
  {
    public abstract void close(Data paramData)
      throws IOException;
    
    public abstract Class<Data> getDataClass();
    
    public abstract Data open(File paramFile)
      throws FileNotFoundException;
  }
  
  public static class StreamFactory
    extends FileLoader.Factory<InputStream>
  {
    public StreamFactory()
    {
      super()
      {
        public void close(InputStream paramAnonymousInputStream)
          throws IOException
        {
          paramAnonymousInputStream.close();
        }
        
        public Class<InputStream> getDataClass()
        {
          return InputStream.class;
        }
        
        public InputStream open(File paramAnonymousFile)
          throws FileNotFoundException
        {
          return new FileInputStream(paramAnonymousFile);
        }
      };
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/model/FileLoader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */