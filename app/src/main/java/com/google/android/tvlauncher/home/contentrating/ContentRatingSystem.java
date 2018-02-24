package com.google.android.tvlauncher.home.contentrating;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.tv.TvContentRating;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class ContentRatingSystem
{
  private static final String DELIMITER = "/";
  public static final Comparator<ContentRatingSystem> DISPLAY_NAME_COMPARATOR = new Comparator()
  {
    public int compare(ContentRatingSystem paramAnonymousContentRatingSystem1, ContentRatingSystem paramAnonymousContentRatingSystem2)
    {
      return paramAnonymousContentRatingSystem1.getDisplayName().compareTo(paramAnonymousContentRatingSystem2.getDisplayName());
    }
  };
  private final List<String> mCountries;
  private final String mDescription;
  private final String mDisplayName;
  private final String mDomain;
  private final boolean mIsCustom;
  private final String mName;
  private final List<Order> mOrders;
  private final List<Rating> mRatings;
  private final List<SubRating> mSubRatings;
  private final String mTitle;
  
  private ContentRatingSystem(String paramString1, String paramString2, String paramString3, String paramString4, List<String> paramList, String paramString5, List<Rating> paramList1, List<SubRating> paramList2, List<Order> paramList3, boolean paramBoolean)
  {
    this.mName = paramString1;
    this.mDomain = paramString2;
    this.mTitle = paramString3;
    this.mDescription = paramString4;
    this.mCountries = paramList;
    this.mDisplayName = paramString5;
    this.mRatings = paramList1;
    this.mSubRatings = paramList2;
    this.mOrders = paramList3;
    this.mIsCustom = paramBoolean;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if ((paramObject instanceof ContentRatingSystem))
    {
      paramObject = (ContentRatingSystem)paramObject;
      bool1 = bool2;
      if (this.mName.equals(((ContentRatingSystem)paramObject).mName))
      {
        bool1 = bool2;
        if (this.mDomain.equals(((ContentRatingSystem)paramObject).mDomain)) {
          bool1 = true;
        }
      }
    }
    return bool1;
  }
  
  public List<String> getCountries()
  {
    return this.mCountries;
  }
  
  public String getDescription()
  {
    return this.mDescription;
  }
  
  public String getDisplayName()
  {
    return this.mDisplayName;
  }
  
  public String getDomain()
  {
    return this.mDomain;
  }
  
  public String getId()
  {
    return this.mDomain + "/" + this.mName;
  }
  
  public String getName()
  {
    return this.mName;
  }
  
  public List<Order> getOrders()
  {
    return this.mOrders;
  }
  
  public List<Rating> getRatings()
  {
    return this.mRatings;
  }
  
  public List<SubRating> getSubRatings()
  {
    return this.mSubRatings;
  }
  
  public String getTitle()
  {
    return this.mTitle;
  }
  
  public int hashCode()
  {
    return this.mName.hashCode() * 31 + this.mDomain.hashCode();
  }
  
  public boolean isCustom()
  {
    return this.mIsCustom;
  }
  
  public boolean ownsRating(TvContentRating paramTvContentRating)
  {
    return (this.mDomain.equals(paramTvContentRating.getDomain())) && (this.mName.equals(paramTvContentRating.getRatingSystem()));
  }
  
  public static class Builder
  {
    private final Context mContext;
    private List<String> mCountries;
    private String mDescription;
    private String mDomain;
    private boolean mIsCustom;
    private String mName;
    private final List<ContentRatingSystem.Order.Builder> mOrderBuilders = new ArrayList();
    private final List<ContentRatingSystem.Rating.Builder> mRatingBuilders = new ArrayList();
    private final List<ContentRatingSystem.SubRating.Builder> mSubRatingBuilders = new ArrayList();
    private String mTitle;
    
    public Builder(Context paramContext)
    {
      this.mContext = paramContext;
    }
    
    public void addCountry(String paramString)
    {
      if (this.mCountries == null) {
        this.mCountries = new ArrayList();
      }
      this.mCountries.add(new Locale("", paramString).getCountry());
    }
    
    public void addOrderBuilder(ContentRatingSystem.Order.Builder paramBuilder)
    {
      this.mOrderBuilders.add(paramBuilder);
    }
    
    public void addRatingBuilder(ContentRatingSystem.Rating.Builder paramBuilder)
    {
      this.mRatingBuilders.add(paramBuilder);
    }
    
    public void addSubRatingBuilder(ContentRatingSystem.SubRating.Builder paramBuilder)
    {
      this.mSubRatingBuilders.add(paramBuilder);
    }
    
    public ContentRatingSystem build()
    {
      if (TextUtils.isEmpty(this.mName)) {
        throw new IllegalArgumentException("Name cannot be empty");
      }
      if (TextUtils.isEmpty(this.mDomain)) {
        throw new IllegalArgumentException("Domain cannot be empty");
      }
      Object localObject1 = new StringBuilder();
      if (this.mCountries != null)
      {
        if (this.mCountries.size() != 1) {
          break label197;
        }
        ((StringBuilder)localObject1).append(new Locale("", (String)this.mCountries.get(0)).getDisplayCountry());
      }
      Object localObject2;
      for (;;)
      {
        if (!TextUtils.isEmpty(this.mTitle))
        {
          ((StringBuilder)localObject1).append(" (");
          ((StringBuilder)localObject1).append(this.mTitle);
          ((StringBuilder)localObject1).append(")");
        }
        localObject1 = ((StringBuilder)localObject1).toString();
        localObject2 = new ArrayList();
        if (this.mSubRatingBuilders == null) {
          break;
        }
        localObject3 = this.mSubRatingBuilders.iterator();
        while (((Iterator)localObject3).hasNext()) {
          ((List)localObject2).add(ContentRatingSystem.SubRating.Builder.access$000((ContentRatingSystem.SubRating.Builder)((Iterator)localObject3).next()));
        }
        label197:
        if (this.mCountries.size() > 1)
        {
          localObject2 = Locale.getDefault();
          if (this.mCountries.contains(((Locale)localObject2).getCountry())) {
            ((StringBuilder)localObject1).append(((Locale)localObject2).getDisplayCountry());
          } else {
            ((StringBuilder)localObject1).append(this.mContext.getString(2131493053));
          }
        }
      }
      if (this.mRatingBuilders.size() <= 0) {
        throw new IllegalArgumentException("Rating isn't available.");
      }
      Object localObject3 = new ArrayList();
      Object localObject4 = this.mRatingBuilders.iterator();
      while (((Iterator)localObject4).hasNext()) {
        ((List)localObject3).add(ContentRatingSystem.Rating.Builder.access$100((ContentRatingSystem.Rating.Builder)((Iterator)localObject4).next(), (List)localObject2));
      }
      localObject4 = ((List)localObject2).iterator();
      Object localObject5;
      while (((Iterator)localObject4).hasNext())
      {
        localObject5 = (ContentRatingSystem.SubRating)((Iterator)localObject4).next();
        int j = 0;
        Iterator localIterator = ((List)localObject3).iterator();
        do
        {
          i = j;
          if (!localIterator.hasNext()) {
            break;
          }
        } while (!((ContentRatingSystem.Rating)localIterator.next()).getSubRatings().contains(localObject5));
        int i = 1;
        if (i == 0) {
          throw new IllegalArgumentException("Subrating " + ((ContentRatingSystem.SubRating)localObject5).getName() + " isn't used by any rating");
        }
      }
      localObject4 = new ArrayList();
      if (this.mOrderBuilders != null)
      {
        localObject5 = this.mOrderBuilders.iterator();
        while (((Iterator)localObject5).hasNext()) {
          ((List)localObject4).add(ContentRatingSystem.Order.Builder.access$200((ContentRatingSystem.Order.Builder)((Iterator)localObject5).next(), (List)localObject3));
        }
      }
      return new ContentRatingSystem(this.mName, this.mDomain, this.mTitle, this.mDescription, this.mCountries, (String)localObject1, (List)localObject3, (List)localObject2, (List)localObject4, this.mIsCustom, null);
    }
    
    public void setDescription(String paramString)
    {
      this.mDescription = paramString;
    }
    
    public void setDomain(String paramString)
    {
      this.mDomain = paramString;
    }
    
    public void setIsCustom(boolean paramBoolean)
    {
      this.mIsCustom = paramBoolean;
    }
    
    public void setName(String paramString)
    {
      this.mName = paramString;
    }
    
    public void setTitle(String paramString)
    {
      this.mTitle = paramString;
    }
  }
  
  public static class Order
  {
    private final List<ContentRatingSystem.Rating> mRatingOrder;
    
    private Order(List<ContentRatingSystem.Rating> paramList)
    {
      this.mRatingOrder = paramList;
    }
    
    public int getRatingIndex(ContentRatingSystem.Rating paramRating)
    {
      int i = 0;
      while (i < this.mRatingOrder.size())
      {
        if (((ContentRatingSystem.Rating)this.mRatingOrder.get(i)).getName().equals(paramRating.getName())) {
          return i;
        }
        i += 1;
      }
      return -1;
    }
    
    public List<ContentRatingSystem.Rating> getRatingOrder()
    {
      return this.mRatingOrder;
    }
    
    public static class Builder
    {
      private final List<String> mRatingNames = new ArrayList();
      
      private ContentRatingSystem.Order build(List<ContentRatingSystem.Rating> paramList)
      {
        ArrayList localArrayList = new ArrayList();
        Iterator localIterator1 = this.mRatingNames.iterator();
        while (localIterator1.hasNext())
        {
          String str = (String)localIterator1.next();
          int j = 0;
          Iterator localIterator2 = paramList.iterator();
          ContentRatingSystem.Rating localRating;
          do
          {
            i = j;
            if (!localIterator2.hasNext()) {
              break;
            }
            localRating = (ContentRatingSystem.Rating)localIterator2.next();
          } while (!str.equals(localRating.getName()));
          int i = 1;
          localArrayList.add(localRating);
          if (i == 0) {
            throw new IllegalArgumentException("Unknown rating " + str + " in rating-order tag");
          }
        }
        return new ContentRatingSystem.Order(localArrayList, null);
      }
      
      public void addRatingName(String paramString)
      {
        this.mRatingNames.add(paramString);
      }
    }
  }
  
  public static class Rating
  {
    private final int mContentAgeHint;
    private final String mDescription;
    private final Drawable mIcon;
    private final String mName;
    private final List<ContentRatingSystem.SubRating> mSubRatings;
    private final String mTitle;
    
    private Rating(String paramString1, String paramString2, String paramString3, Drawable paramDrawable, int paramInt, List<ContentRatingSystem.SubRating> paramList)
    {
      this.mName = paramString1;
      this.mTitle = paramString2;
      this.mDescription = paramString3;
      this.mIcon = paramDrawable;
      this.mContentAgeHint = paramInt;
      this.mSubRatings = paramList;
    }
    
    public int getAgeHint()
    {
      return this.mContentAgeHint;
    }
    
    public String getDescription()
    {
      return this.mDescription;
    }
    
    public Drawable getIcon()
    {
      return this.mIcon;
    }
    
    public String getName()
    {
      return this.mName;
    }
    
    public List<ContentRatingSystem.SubRating> getSubRatings()
    {
      return this.mSubRatings;
    }
    
    public String getTitle()
    {
      return this.mTitle;
    }
    
    public static class Builder
    {
      private int mContentAgeHint = -1;
      private String mDescription;
      private Drawable mIcon;
      private String mName;
      private final List<String> mSubRatingNames = new ArrayList();
      private String mTitle;
      
      private ContentRatingSystem.Rating build(List<ContentRatingSystem.SubRating> paramList)
      {
        if (TextUtils.isEmpty(this.mName)) {
          throw new IllegalArgumentException("A rating should have non-empty name");
        }
        if ((paramList == null) && (this.mSubRatingNames.size() > 0)) {
          throw new IllegalArgumentException("Invalid subrating for rating " + this.mName);
        }
        if (this.mContentAgeHint < 0) {
          throw new IllegalArgumentException("Rating " + this.mName + " should define " + "non-negative contentAgeHint");
        }
        ArrayList localArrayList = new ArrayList();
        Iterator localIterator1 = this.mSubRatingNames.iterator();
        while (localIterator1.hasNext())
        {
          String str = (String)localIterator1.next();
          int j = 0;
          Iterator localIterator2 = paramList.iterator();
          ContentRatingSystem.SubRating localSubRating;
          do
          {
            i = j;
            if (!localIterator2.hasNext()) {
              break;
            }
            localSubRating = (ContentRatingSystem.SubRating)localIterator2.next();
          } while (!str.equals(localSubRating.getName()));
          int i = 1;
          localArrayList.add(localSubRating);
          if (i == 0) {
            throw new IllegalArgumentException("Unknown subrating name " + str + " in rating " + this.mName);
          }
        }
        return new ContentRatingSystem.Rating(this.mName, this.mTitle, this.mDescription, this.mIcon, this.mContentAgeHint, localArrayList, null);
      }
      
      public void addSubRatingName(String paramString)
      {
        this.mSubRatingNames.add(paramString);
      }
      
      public void setContentAgeHint(int paramInt)
      {
        this.mContentAgeHint = paramInt;
      }
      
      public void setDescription(String paramString)
      {
        this.mDescription = paramString;
      }
      
      public void setIcon(Drawable paramDrawable)
      {
        this.mIcon = paramDrawable;
      }
      
      public void setName(String paramString)
      {
        this.mName = paramString;
      }
      
      public void setTitle(String paramString)
      {
        this.mTitle = paramString;
      }
    }
  }
  
  public static class SubRating
  {
    private final String mDescription;
    private final Drawable mIcon;
    private final String mName;
    private final String mTitle;
    
    private SubRating(String paramString1, String paramString2, String paramString3, Drawable paramDrawable)
    {
      this.mName = paramString1;
      this.mTitle = paramString2;
      this.mDescription = paramString3;
      this.mIcon = paramDrawable;
    }
    
    public String getDescription()
    {
      return this.mDescription;
    }
    
    public Drawable getIcon()
    {
      return this.mIcon;
    }
    
    public String getName()
    {
      return this.mName;
    }
    
    public String getTitle()
    {
      return this.mTitle;
    }
    
    public static class Builder
    {
      private String mDescription;
      private Drawable mIcon;
      private String mName;
      private String mTitle;
      
      private ContentRatingSystem.SubRating build()
      {
        if (TextUtils.isEmpty(this.mName)) {
          throw new IllegalArgumentException("A subrating should have non-empty name");
        }
        return new ContentRatingSystem.SubRating(this.mName, this.mTitle, this.mDescription, this.mIcon, null);
      }
      
      public void setDescription(String paramString)
      {
        this.mDescription = paramString;
      }
      
      public void setIcon(Drawable paramDrawable)
      {
        this.mIcon = paramDrawable;
      }
      
      public void setName(String paramString)
      {
        this.mName = paramString;
      }
      
      public void setTitle(String paramString)
      {
        this.mTitle = paramString;
      }
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/home/contentrating/ContentRatingSystem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */