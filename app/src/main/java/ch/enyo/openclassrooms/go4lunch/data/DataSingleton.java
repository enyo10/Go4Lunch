package ch.enyo.openclassrooms.go4lunch.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ch.enyo.openclassrooms.go4lunch.BuildConfig;
import ch.enyo.openclassrooms.go4lunch.models.googleapi.placesdetails.PlaceDetails;

public class DataSingleton implements Serializable {

    private static final String TAG =DataSingleton.class.getSimpleName();

    private List<PlaceDetails> mPlaceDetailsList;
    private Double mLatitude;
    private Double mLongitude;
    private PlaceDetails mPlaceDetail;
    private Locale mLocale;
    private Map<String,String> mParameterMap;
    private String location="";


    private static volatile DataSingleton mSoleInstance;

        //private constructor.
        private DataSingleton(){
            //Prevent form the reflection api.
            if (mSoleInstance != null){
                throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
            }
            mPlaceDetailsList= new ArrayList<>();

            // Use to make the filter by retrofit call.
            mParameterMap=new HashMap<>();
            mParameterMap.put("key","AIzaSyAj8TgbhVVLCxEldGuNHxxo2w4P-S2mxG8");
            mParameterMap.put("type","restaurant");
            mParameterMap.put("radius","35000");
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

    public Locale getLocale() {
        return mLocale;
    }

    public void setLocale(Locale locale) {
        mLocale = locale;
    }

    public Map<String, String> getParametersMap() {
            this.mParameterMap.put("location",getLocationString());
        return mParameterMap;
    }

    /**
     * This method format longitude and latitude to a String.
     * @return location, a string format of longitude and latitude.
     */
    public String getLocationString(){

            if(this.getLongitude()!=null && this.getLatitude()!=null)
                location = getLongitude() + "," + getLatitude();
            return location;

    }
}

