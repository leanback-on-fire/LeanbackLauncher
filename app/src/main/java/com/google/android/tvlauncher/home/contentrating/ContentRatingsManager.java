package com.google.android.tvlauncher.home.contentrating;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.media.tv.TvContentRating;
import android.media.tv.TvContentRatingSystemInfo;
import android.media.tv.TvInputManager;
import android.os.AsyncTask;
import android.util.Log;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ContentRatingsManager
{
  private static final boolean DEBUG = false;
  private static final String TAG = "ContentRatingsManager";
  private static ContentRatingsManager sInstance;
  private final List<ContentRatingSystem> mContentRatingSystems = new ArrayList();
  private final Context mContext;
  private boolean mSetupComplete;
  
  private ContentRatingsManager(Context paramContext)
  {
    this.mContext = paramContext;
  }
  
  private void ensureLoaded()
  {
    if (this.mSetupComplete) {
      return;
    }
    try
    {
      if (this.mSetupComplete) {
        return;
      }
    }
    finally {}
    setup();
    this.mSetupComplete = true;
  }
  
  public static ContentRatingsManager getInstance(Context paramContext)
  {
    if (sInstance == null) {
      sInstance = new ContentRatingsManager(paramContext.getApplicationContext());
    }
    return sInstance;
  }
  
  private ContentRatingSystem.Rating getRating(TvContentRating paramTvContentRating)
  {
    if (paramTvContentRating == null) {
      return null;
    }
    ContentRatingSystem.Rating localRating;
    do
    {
      Iterator localIterator = this.mContentRatingSystems.iterator();
      Object localObject;
      while (!((Iterator)localObject).hasNext())
      {
        do
        {
          if (!localIterator.hasNext()) {
            break;
          }
          localObject = (ContentRatingSystem)localIterator.next();
        } while ((!((ContentRatingSystem)localObject).getDomain().equals(paramTvContentRating.getDomain())) || (!((ContentRatingSystem)localObject).getName().equals(paramTvContentRating.getRatingSystem())));
        localObject = ((ContentRatingSystem)localObject).getRatings().iterator();
      }
      localRating = (ContentRatingSystem.Rating)((Iterator)localObject).next();
    } while (!localRating.getName().equals(paramTvContentRating.getMainRating()));
    return localRating;
    return null;
  }
  
  private List<ContentRatingSystem.SubRating> getSubRatings(ContentRatingSystem.Rating paramRating, TvContentRating paramTvContentRating)
  {
    ArrayList localArrayList = new ArrayList();
    if ((paramRating == null) || (paramRating.getSubRatings() == null) || (paramTvContentRating == null) || (paramTvContentRating.getSubRatings() == null)) {}
    label118:
    for (;;)
    {
      return localArrayList;
      paramTvContentRating = paramTvContentRating.getSubRatings().iterator();
      for (;;)
      {
        if (!paramTvContentRating.hasNext()) {
          break label118;
        }
        String str = (String)paramTvContentRating.next();
        Iterator localIterator = paramRating.getSubRatings().iterator();
        if (localIterator.hasNext())
        {
          ContentRatingSystem.SubRating localSubRating = (ContentRatingSystem.SubRating)localIterator.next();
          if (!localSubRating.getName().equals(str)) {
            break;
          }
          localArrayList.add(localSubRating);
        }
      }
    }
  }
  
  private void setup()
  {
    this.mContentRatingSystems.clear();
    Object localObject = (TvInputManager)this.mContext.getSystemService("tv_input");
    ContentRatingsParser localContentRatingsParser = new ContentRatingsParser(this.mContext);
    localObject = ((TvInputManager)localObject).getTvContentRatingSystemList();
    if (((List)localObject).isEmpty()) {}
    try
    {
      ((List)localObject).add(TvContentRatingSystemInfo.createTvContentRatingSystemInfo(2131230720, this.mContext.getPackageManager().getApplicationInfo(this.mContext.getPackageName(), 0)));
      localObject = ((List)localObject).iterator();
      while (((Iterator)localObject).hasNext())
      {
        List localList = localContentRatingsParser.parse((TvContentRatingSystemInfo)((Iterator)localObject).next());
        if (localList != null) {
          this.mContentRatingSystems.addAll(localList);
        }
      }
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      for (;;)
      {
        Log.e("ContentRatingsManager", "Can't retrieve content ratings, failed to load application info: " + localNameNotFoundException);
      }
    }
  }
  
  public List<ContentRatingSystem> getContentRatingSystems()
  {
    return new ArrayList(this.mContentRatingSystems);
  }
  
  public String getDisplayNameForRating(TvContentRating paramTvContentRating)
  {
    ensureLoaded();
    ContentRatingSystem.Rating localRating = getRating(paramTvContentRating);
    if (localRating == null) {
      return null;
    }
    Object localObject = getSubRatings(localRating, paramTvContentRating);
    if (!((List)localObject).isEmpty())
    {
      switch (((List)localObject).size())
      {
      default: 
        paramTvContentRating = new StringBuilder();
        localObject = ((List)localObject).iterator();
      }
      while (((Iterator)localObject).hasNext())
      {
        paramTvContentRating.append(((ContentRatingSystem.SubRating)((Iterator)localObject).next()).getTitle());
        paramTvContentRating.append(", ");
        continue;
        return this.mContext.getResources().getString(2131492923, new Object[] { localRating.getTitle(), ((ContentRatingSystem.SubRating)((List)localObject).get(0)).getTitle() });
        return this.mContext.getResources().getString(2131492924, new Object[] { localRating.getTitle(), ((ContentRatingSystem.SubRating)((List)localObject).get(0)).getTitle(), ((ContentRatingSystem.SubRating)((List)localObject).get(1)).getTitle() });
        return this.mContext.getResources().getString(2131492925, new Object[] { localRating.getTitle(), ((ContentRatingSystem.SubRating)((List)localObject).get(0)).getTitle(), ((ContentRatingSystem.SubRating)((List)localObject).get(1)).getTitle(), ((ContentRatingSystem.SubRating)((List)localObject).get(2)).getTitle() });
        return this.mContext.getResources().getString(2131492926, new Object[] { localRating.getTitle(), ((ContentRatingSystem.SubRating)((List)localObject).get(0)).getTitle(), ((ContentRatingSystem.SubRating)((List)localObject).get(1)).getTitle(), ((ContentRatingSystem.SubRating)((List)localObject).get(2)).getTitle(), ((ContentRatingSystem.SubRating)((List)localObject).get(3)).getTitle() });
        return this.mContext.getResources().getString(2131492927, new Object[] { localRating.getTitle(), ((ContentRatingSystem.SubRating)((List)localObject).get(0)).getTitle(), ((ContentRatingSystem.SubRating)((List)localObject).get(1)).getTitle(), ((ContentRatingSystem.SubRating)((List)localObject).get(2)).getTitle(), ((ContentRatingSystem.SubRating)((List)localObject).get(3)).getTitle(), ((ContentRatingSystem.SubRating)((List)localObject).get(4)).getTitle() });
      }
      return this.mContext.getResources().getString(2131492928, new Object[] { localRating.getTitle(), paramTvContentRating.substring(0, paramTvContentRating.length() - 2) });
    }
    return this.mContext.getResources().getString(2131492929, new Object[] { localRating.getTitle() });
  }
  
  public void preload()
  {
    new AsyncTask()
    {
      protected Void doInBackground(Void[] paramAnonymousArrayOfVoid)
      {
        ContentRatingsManager.this.ensureLoaded();
        return null;
      }
    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/home/contentrating/ContentRatingsManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */