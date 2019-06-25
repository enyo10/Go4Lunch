package ch.enyoholali.openclassrooms.go4lunch.utils;


import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import ch.enyoholali.openclassrooms.go4lunch.BuildConfig;
import ch.enyoholali.openclassrooms.go4lunch.models.googleapi.nearbysearch.PlaceNearBySearch;
import ch.enyoholali.openclassrooms.go4lunch.models.googleapi.nearbysearch.Result;
import ch.enyoholali.openclassrooms.go4lunch.models.googleapi.placesdetails.PlaceDetails;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class GoogleApiPlaceStreams {
    private static final String TAG =GoogleApiPlaceStreams.class.getSimpleName();

    private static Map<String,String>mParametersMap=new HashMap<>();




   // String mString="https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-33.8670522,151.1957362&radius=1500&type=" +"restaurant&keyword=cruise&key=AIzaSyD9pp59K2tcqgbzpAXeyjXQ_7DVcOaHQl0";
    //"AIzaSyDor0zXuE7bHOAJvcG8X4QVWZI5XhEx9fo"
    //String mString2="https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-33.8670522,151.1957362&radius=1500&type=restaurant&keyword=cruise&key=AIzaSyAj8TgbhVVLCxEldGuNHxxo2w4P-S2mxG8";

    /**
     * This method to get a Stream of list of restaurants.
     * @param location, the location.
     * @return a stream of restaurant.
     */

    public static Observable<PlaceNearBySearch>fetchPlaceNearBySearchStream(String location){
         Log.i(TAG, "Api_key :"+BuildConfig.ApiKey);// "AIzaSyAj8TgbhVVLCxEldGuNHxxo2w4P-S2mxG8"
        mParametersMap.put("key",BuildConfig.ApiKey);
        mParametersMap.put("type", "restaurant");
        mParametersMap.put("radius", "5000");
        mParametersMap.put("location",location);
        GoogleApiPlaceService googleApiPlaceService=GoogleApiPlaceService.retrofit.create(GoogleApiPlaceService.class);
        return googleApiPlaceService.getPlaceNearBySearch(mParametersMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(15,TimeUnit.SECONDS);
    }


    public static Observable<PlaceDetails>fetchPlaceDetailsStream(String place_id){
        String key=BuildConfig.ApiKey;
       // Log.i(TAG, "key ---- "+key);
      //  String key="AIzaSyAj8TgbhVVLCxEldGuNHxxo2w4P-S2mxG8";
        GoogleApiPlaceService googleApiPlaceService = GoogleApiPlaceService.retrofit.create(GoogleApiPlaceService.class);
        return googleApiPlaceService.getPlaceDetails(key,place_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(15,TimeUnit.SECONDS);
    }

    public static Observable<List<PlaceDetails>>streamFPlaceDetailsList(String location){
        return fetchPlaceNearBySearchStream(location)
                .map(new Function<PlaceNearBySearch, List<Result>>(){
                    @Override
                    public List<Result> apply(PlaceNearBySearch placeNearBySearch) throws Exception {
                        return placeNearBySearch.getResults();
                    }
                }).concatMap(new Function<List<Result>, Observable<List<PlaceDetails>>>() {
                    @Override
                    public Observable<List<PlaceDetails>> apply(List<Result> results){
                        return Observable.fromIterable(results).concatMap(new Function<Result, Observable<PlaceDetails>>() {
                            @Override
                            public Observable<PlaceDetails> apply(Result result){
                                return fetchPlaceDetailsStream(result.getPlaceId());
                            }
                        }).toList()
                                .toObservable();
                    }
                });

    }
}
