package ch.enyoholali.openclassrooms.go4lunch.data;

import android.location.Location;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ch.enyoholali.openclassrooms.go4lunch.models.firebase.User;
import ch.enyoholali.openclassrooms.go4lunch.models.googleapi.nearbysearch.Result;
import ch.enyoholali.openclassrooms.go4lunch.models.googleapi.placesdetails.PlaceDetails;

public class DataSingleton implements Serializable {

    private static final String TAG = DataSingleton.class.getSimpleName();


    private List<PlaceDetails> mPlaceDetailsList;
    private List<Result> mNearbySearchResultList;
    private Map<String, PlaceDetails>mStringPlaceDetailsMap;
    private Double mLatitude;
    private Double mLongitude;
    private PlaceDetails mPlaceDetails;
    private Location mLocation;
    private final String location = "";
    private String url;
    private User actuelUser;


    private static volatile DataSingleton mSoleInstance;

    //private constructor.
    private DataSingleton() {
        //Prevent form the reflection api.
        if (mSoleInstance != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
        mPlaceDetailsList = new ArrayList<>();
        mNearbySearchResultList = new ArrayList<>();
        mPlaceDetails=null;
        mLocation=null;

    }


    public static DataSingleton getInstance() {
        if (mSoleInstance == null) { //if there is no instance available... create new one
            synchronized (DataSingleton.class) {
                if (mSoleInstance == null) mSoleInstance = new DataSingleton();
            }
        }

        return mSoleInstance;
    }

    public Map<String, PlaceDetails> getPlaceDetailsHashMap() {
        return mStringPlaceDetailsMap;
    }

    public void setPlaceDetailsMap(Map<String, PlaceDetails> resultHashMap) {
        mStringPlaceDetailsMap = resultHashMap;
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
    private void setResultHashMap(){

    }

    public PlaceDetails getPlaceDetails() {
        return mPlaceDetails;
    }

    public void setPlaceDetails(PlaceDetails placeDetails) {
        this.mPlaceDetails = placeDetails;
    }

    public Location getLocation() {

        return mLocation;
    }

    public void setLocation(Location location) {
        Log.i(TAG, " Location is set "+location.getLongitude());
        this.mLocation = location;


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

    public User getActuelUser() {
        return actuelUser;
    }

    public void setActuelUser(User actuelUser) {
        this.actuelUser = actuelUser;
    }
}

