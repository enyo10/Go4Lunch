package ch.enyoholali.openclassrooms.go4lunch.models.firebase;

import androidx.annotation.Nullable;

import java.util.Comparator;

public class Restaurant {
    private String mIdentifier;             // Restaurant Identifier
    private String mName;                   // Name of the Restaurant
    @Nullable private String mAddress;      // Address of Restaurant
    @Nullable private String mOpeningTime;  // Opening time of Restaurant
    @Nullable private int mDistance;        // Distance where the restaurant is from the current position
    @Nullable private int mNbrParticipants; // Number of participants
    @Nullable private int mNbrLikes;        // Number of likes that the restaurant got
    @Nullable private String mPhotoUrl;     // URL of the Restaurant photo
    @Nullable private String mWebSiteUrl;   // URL of the Web site
    @Nullable private String mType;         // Type of the Restaurant
    @Nullable private String mLat;          // Latitude  of the Restaurant on the Map
    @Nullable private String mLng;          // Longitude of the Restaurant on the Map
    @Nullable private String mPhone;        // Phone number to call the Restaurant


    /**
     * Blank constructor necessary to use FireBase
     */
    public Restaurant() { }


    /**
     *
     * @param identifier
     * @param name
     * @param address
     * @param openingTime
     * @param distance
     * @param nbrParticipants
     * @param nbrLikes
     * @param photoUrl
     * @param webSiteUrl
     * @param type
     * @param lat
     * @param lng
     * @param phone
     */
    public Restaurant(String identifier, String name, @Nullable String address, @Nullable String openingTime,

                      @Nullable int distance, @Nullable int nbrParticipants, @Nullable int nbrLikes,
                      @Nullable String photoUrl, @Nullable String webSiteUrl, @Nullable String type,
                      @Nullable String lat, @Nullable String lng, @Nullable String phone) {

        mIdentifier = identifier;
        mName = name;
        mAddress = address;
        mOpeningTime = openingTime;
        mDistance = distance;
        mNbrParticipants = nbrParticipants;
        mNbrLikes = nbrLikes;
        mPhotoUrl = photoUrl;
        mWebSiteUrl = webSiteUrl;
        mType = type;
        mLat = lat;
        mLng = lng;
        mPhone = phone;

    }


    // --- SETTERS & GETTERS ---

    public String getIdentifier() {
        return mIdentifier;
    }

    public void setIdentifier(String identifier) {
        mIdentifier = identifier;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    @Nullable
    public String getAddress() {
        return mAddress;
    }

    public void setAddress(@Nullable String address) {
        mAddress = address;
    }

    @Nullable
    public String getOpeningTime() {
        return mOpeningTime;
    }

    public void setOpeningTime(@Nullable String openingTime) {
        mOpeningTime = openingTime;
    }

    public int getDistance() {
        return mDistance;
    }

    public void setDistance(int distance) {
        mDistance = distance;
    }

    public int getNbrParticipants() {
        return mNbrParticipants;
    }

    public void setNbrParticipants(int nbrParticipants) {
        mNbrParticipants = nbrParticipants;
    }

    public int getNbrLikes() {
        return mNbrLikes;
    }

    public void setNbrLikes(int nbrLikes) {
        mNbrLikes = nbrLikes;
    }

    @Nullable
    public String getPhotoUrl() {
        return mPhotoUrl;
    }

    public void setPhotoUrl(@Nullable String photoUrl) {
        mPhotoUrl = photoUrl;
    }

    @Nullable
    public String getWebSiteUrl() {
        return mWebSiteUrl;
    }

    public void setWebSiteUrl(@Nullable String webSiteUrl) {
        mWebSiteUrl = webSiteUrl;
    }

    @Nullable
    public String getType() {
        return mType;
    }

    public void setType(@Nullable String type) {
        mType = type;
    }

    @Nullable
    public String getLat() {
        return mLat;
    }

    public void setLat(@Nullable String lat) {
        mLat = lat;
    }

    @Nullable
    public String getLng() {
        return mLng;
    }

    public void setLng(@Nullable String lng) {
        mLng = lng;
    }

    @Nullable
    public String getPhone() {
        return mPhone;
    }

    public void setPhone(@Nullable String phone) {
        mPhone = phone;
    }








    /*Comparator for sorting the list by Restaurant Name*/

    public static Comparator<Restaurant> RestaurantNameComparator = new Comparator<Restaurant>() {


        public int compare(Restaurant r1, Restaurant r2) {

            String RestaurantName1 = r1.getName().toUpperCase();
            String RestaurantName2 = r2.getName().toUpperCase();

            //ascending order

            return RestaurantName1.compareTo(RestaurantName2);

            //descending order

            //return RestaurantName2.compareTo(RestaurantName1);

        }};


    /*Comparator for sorting the list by Restaurant distance*/

    public static Comparator<Restaurant> RestaurantDistanceComparator = new Comparator<Restaurant>() {


        public int compare(Restaurant r1, Restaurant r2) {

            Integer RestaurantDistance1 = r1.getDistance();
            Integer RestaurantDistance2 = r2.getDistance();

            //ascending order
            return RestaurantDistance1.compareTo(RestaurantDistance2);

            //descending order
            //return RestaurantName2.compareTo(RestaurantName1);

        }};



    /*Comparator for sorting the list by Restaurant NbrLikes*/

    public static Comparator<Restaurant> RestaurantNbrLikesComparator = new Comparator<Restaurant>() {

        public int compare(Restaurant r1, Restaurant r2) {

            Integer RestaurantNbrLikes1 = r1.getNbrLikes();
            Integer RestaurantNbrLikes2 = r2.getNbrLikes();

            //ascending order
            //return RestaurantNbrLikes1.compareTo(RestaurantNbrLikes2);
            //descending order

            return RestaurantNbrLikes2.compareTo(RestaurantNbrLikes1);

        }};



    /*Comparator for sorting the list by Restaurant NbrParticipants*/

    public static Comparator<Restaurant> RestaurantNbrParticipantsComparator = new Comparator<Restaurant>() {



        public int compare(Restaurant r1, Restaurant r2) {

            Integer RestaurantNbrParticipants1 = r1.getNbrParticipants();
            Integer RestaurantNbrParticipants2 = r2.getNbrParticipants();

            //ascending order
            //return RestaurantNbrParticipants1.compareTo(RestaurantNbrParticipants2);
            //descending order

            return RestaurantNbrParticipants2.compareTo(RestaurantNbrParticipants1);

        }};
}
