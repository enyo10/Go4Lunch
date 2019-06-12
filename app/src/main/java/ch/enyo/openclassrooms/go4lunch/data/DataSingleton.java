package ch.enyo.openclassrooms.go4lunch.data;

import android.content.SharedPreferences;
import android.location.Location;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import ch.enyo.openclassrooms.go4lunch.models.googleapi.nearbysearch.Result;
import ch.enyo.openclassrooms.go4lunch.models.googleapi.placesdetails.PlaceDetails;

public class DataSingleton implements Serializable {

    private static final String TAG = DataSingleton.class.getSimpleName();


    private List<PlaceDetails> mPlaceDetailsList;
    private List<Result> mNearbySearchResultList;
    private Double mLatitude;
    private Double mLongitude;
    private PlaceDetails mPlaceDetail;
    private Location mLocation;
    private String location = "";
    private String url;


    private static volatile DataSingleton mSoleInstance;

    //private constructor.
    private DataSingleton() {
        //Prevent form the reflection api.
        if (mSoleInstance != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
        mPlaceDetailsList = new ArrayList<>();
        mNearbySearchResultList = new ArrayList<>();

    }


    public static DataSingleton getInstance() {
        if (mSoleInstance == null) { //if there is no instance available... create new one
            synchronized (DataSingleton.class) {
                if (mSoleInstance == null) mSoleInstance = new DataSingleton();
            }
        }

        return mSoleInstance;
    }

    //Make singleton from serialize and deserialize operation.
    protected DataSingleton readResolve() {
        return getInstance();
    }


    public List<PlaceDetails> getPlaceDetailsList() {
        return mPlaceDetailsList;
    }

    public void setPlaceDetailsList(List<PlaceDetails> placeDetailsList) {
        mPlaceDetailsList = placeDetailsList;
    }

    public Double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(Double latitude) {
        mLatitude = latitude;
    }

    public Double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(Double longitude) {
        this.mLongitude = longitude;
    }

    public PlaceDetails getPlaceDetail() {
        return mPlaceDetail;
    }

    public void setPlaceDetail(PlaceDetails placeDetail) {
        mPlaceDetail = placeDetail;
    }

    public Location getLocation() {

        return mLocation;
    }

    public void setLocation(Location location) {
        Log.i(TAG, " Location is set "+location.getLongitude());
        this.mLocation = location;


    }


    /**
     * This method format longitude and latitude to a String.
     *
     * @return location, a string format of longitude and latitude.
     */
    private String getLocationString() {

        if (this.getLongitude() != null && this.getLatitude() != null)
            location = getLongitude() + "," + getLatitude();
        return location;

    }

    public List<Result> getNearbySearchResultList() {
        return mNearbySearchResultList;
    }

    public void setNearbySearchResultList(List<Result> nearbySearchResultList) {
        mNearbySearchResultList = nearbySearchResultList;
    }

    public String getUrl(){
        return url;
    }
}

