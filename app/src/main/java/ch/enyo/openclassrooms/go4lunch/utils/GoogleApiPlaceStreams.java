package ch.enyo.openclassrooms.go4lunch.utils;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import ch.enyo.openclassrooms.go4lunch.models.google.NearBySearchResult;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class GoogleApiPlaceStreams {

    String mString="https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-33.8670522,151.1957362&radius=1500&type=" +
            "restaurant&keyword=cruise&key=AIzaSyD9pp59K2tcqgbzpAXeyjXQ_7DVcOaHQl0";
    String mString2="https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-33.8670522,151.1957362&radius=1500&type=restaurant&keyword=cruise&key=AIzaSyAj8TgbhVVLCxEldGuNHxxo2w4P-S2mxG8";



    public static Observable<NearBySearchResult>getNearBySearchResultStream(Map<String,String>filter){
        GoogleApiPlaceService googleApiPlaceService = GoogleApiPlaceService.retrofit.create(GoogleApiPlaceService.class);
        return googleApiPlaceService.getPlaceNearBySearch(filter)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(15, TimeUnit.SECONDS);
    }
}
