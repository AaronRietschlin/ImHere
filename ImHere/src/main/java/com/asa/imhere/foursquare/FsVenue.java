package com.asa.imhere.foursquare;

import android.os.Parcel;
import android.os.Parcelable;

import com.asa.imhere.model.Nameable;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Venue item. See
 * https://developer.foursquare.com/docs/responses/venue
 */
public class FsVenue extends BaseItem implements Nameable {

    private Long _id;
    @SerializedName("contact")
    private Contact contact;
    @SerializedName("location")
    private FsLocation location;
    @SerializedName("canonicalUrl")
    private String canonicalUrl;
    @SerializedName("categories")
    private List<FsCategory> categories;
    @SerializedName("verified")
    private boolean verified;
    @SerializedName("stats")
    private Stats stats;
    @SerializedName("url")
    private String url;
    @SerializedName("price")
    private Price price;
    @SerializedName("specials")
    private Special specials;
    @SerializedName("photos")
    private VenuePhotoGroup photos;
    /**
     * Seconds
     */
    @SerializedName("createdAt")
    private long createdAt;
    @SerializedName("mayor")
    private Mayor mayor;
    @SerializedName("like")
    private boolean like;
    @SerializedName("dislike")
    private boolean dislike;
    @SerializedName("tips")
    private TipItem tips;
    @SerializedName("description")
    private String description;

    // TODO do events
    // TODO add listed
    // TODO - add hereNow
    // TODO - do hours and popular
    // TODO - do menu
    // TODO - add tips
    // TODO - add phrases


    public Long getDatabaseId() {
        return _id;
    }

    public void setDatabaseId(Long _id) {
        this._id = _id;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public FsLocation getLocation() {
        return location;
    }

    public void setLocation(FsLocation location) {
        this.location = location;
    }

    public String getCanonicalUrl() {
        return canonicalUrl;
    }

    public void setCanonicalUrl(String canonicalUrl) {
        this.canonicalUrl = canonicalUrl;
    }

    public List<FsCategory> getCategories() {
        return categories;
    }

    public void setCategories(List<FsCategory> categories) {
        this.categories = categories;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public Stats getStats() {
        return stats;
    }

    public void setStats(Stats stats) {
        this.stats = stats;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Price getPrice() {
        return price;
    }

    public void setPrice(Price price) {
        this.price = price;
    }

    public Special getSpecials() {
        return specials;
    }

    public void setSpecials(Special specials) {
        this.specials = specials;
    }

    public VenuePhotoGroup getPhotos() {
        return photos;
    }

    public void setPhotos(VenuePhotoGroup photos) {
        this.photos = photos;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public Mayor getMayor() {
        return mayor;
    }

    public void setMayor(Mayor mayor) {
        this.mayor = mayor;
    }

    public boolean isLike() {
        return like;
    }

    public void setLike(boolean like) {
        this.like = like;
    }

    public boolean isDislike() {
        return dislike;
    }

    public void setDislike(boolean dislike) {
        this.dislike = dislike;
    }

    public TipItem getTips() {
        return tips;
    }

    public void setTips(TipItem tips) {
        this.tips = tips;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static class Contact {
        // TODO - figure out what this looks like
        // An object containing none, some, or all of twitter, phone, and
        // formattedPhone. All are strings.
    }

    public static class Stats {
        @SerializedName("checkinsCount")
        private int checkinsCount;
        @SerializedName("usersCount")
        private int usersCount;
        @SerializedName("tipCount")
        private int tipCount;

        public int getCheckinsCount() {
            return checkinsCount;
        }

        public void setCheckinsCount(int checkinsCount) {
            this.checkinsCount = checkinsCount;
        }

        public int getUsersCount() {
            return usersCount;
        }

        public void setUsersCount(int usersCount) {
            this.usersCount = usersCount;
        }

        public int getTipCount() {
            return tipCount;
        }

        public void setTipCount(int tipCount) {
            this.tipCount = tipCount;
        }
    }

    public static class Price {
        /**
         * Pricing tier from 1(least pricey) to 4(most pricey)
         */
        @SerializedName("tier")
        private int tier;
        @SerializedName("message")
        private String message;

        public int getTier() {
            return tier;
        }

        public void setTier(int tier) {
            this.tier = tier;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    public static class Special {
        @SerializedName("count")
        private int count;
        @SerializedName("items")
        private List<FsSpecial> items;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public List<FsSpecial> getItems() {
            return items;
        }

        public void setItems(List<FsSpecial> items) {
            this.items = items;
        }
    }

    /**
     * This represents a "photo group". In the API, it comes back nested as
     * follows: <br />
     * <br />
     * photos <br />
     * &nbsp;-groups (This is the VenuePhotoGroup) <br />
     * &nbsp;&nbsp;&nbsp;-items (This is {@link VenuePhotoItem}
     *
     * @author Aaron
     */
    public static class VenuePhotoGroup {
        @SerializedName("count")
        private int count;
        @SerializedName("groups")
        private List<VenuePhotoItem> items;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public List<VenuePhotoItem> getItems() {
            return items;
        }

        public void setItems(List<VenuePhotoItem> items) {
            this.items = items;
        }
    }

    /**
     * This is the actual photo object that comes back in this call. It contains
     * a few other nodes. The "items" node is an array of {@link FsPhoto} items.
     */
    public static class VenuePhotoItem {
        @SerializedName("")
        private String type;
        @SerializedName("name")
        private String name;
        @SerializedName("count")
        private int count;
        @SerializedName("items")
        private List<FsPhoto> items;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public List<FsPhoto> getItems() {
            return items;
        }

        public void setItems(List<FsPhoto> items) {
            this.items = items;
        }
    }

    public static class Mayor {
        @SerializedName("count")
        private int count;
        @SerializedName("user")
        private FsUser user;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public FsUser getUser() {
            return user;
        }

        public void setUser(FsUser user) {
            this.user = user;
        }
    }

    public static class BeenHere implements Parcelable {
        @SerializedName("count")
        private int count;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.count);
        }

        private BeenHere(Parcel in) {
            this.count = in.readInt();
        }

        public static Parcelable.Creator<BeenHere> CREATOR = new Parcelable.Creator<BeenHere>() {
            public BeenHere createFromParcel(Parcel source) {
                return new BeenHere(source);
            }

            public BeenHere[] newArray(int size) {
                return new BeenHere[size];
            }
        };
    }

    public static class TipItem {
        @SerializedName("count")
        private int count;
        @SerializedName("groups")
        private ArrayList<TipGroup> groups;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public List<TipGroup> getGroups() {
            return groups;
        }

        public void setGroups(ArrayList<TipGroup> groups) {
            this.groups = groups;
        }
    }

    public static class TipGroup implements Parcelable {
        @SerializedName("type")
        private String type;
        @SerializedName("name")
        private String name;
        @SerializedName("count")
        private int count;
        @SerializedName("items")
        private List<FsTip> items;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public List<FsTip> getItems() {
            return items;
        }

        public void setItems(List<FsTip> items) {
            this.items = items;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.type);
            dest.writeString(this.name);
            dest.writeInt(this.count);
            dest.writeList(this.items);
        }

        private TipGroup(Parcel in) {
            this.type = in.readString();
            this.name = in.readString();
            this.count = in.readInt();
            this.items = new ArrayList<FsTip>();
            in.readList(this.items, FsTip.class.getClassLoader());
        }

        public static Parcelable.Creator<TipGroup> CREATOR = new Parcelable.Creator<TipGroup>() {
            public TipGroup createFromParcel(Parcel source) {
                return new TipGroup(source);
            }

            public TipGroup[] newArray(int size) {
                return new TipGroup[size];
            }
        };
    }

    /**
     * A convenience method for getting a correct looking address.
     */
    public String getAddress() {
        if (location == null) {
            return null;
        }
        String addr = location.getAddress();
        if (addr == null) {
            return null;
        }
        String city = location.getCity();
        String state = location.getState();
        String zip = location.getPostalCode();
        if (city == null || state == null || zip == null) {
            return addr;
        }
        return addr += " " + city + ", " + state + " " + zip;
    }

    @Override
    public String getVenueId() {
        return getId();
    }
}
