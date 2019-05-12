package ch.enyo.openclassrooms.go4lunch.utils;


import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import ch.enyo.openclassrooms.go4lunch.models.googleapi.nearbysearch.PlaceNearBySearch;
import ch.enyo.openclassrooms.go4lunch.models.googleapi.nearbysearch.Result;
import ch.enyo.openclassrooms.go4lunch.models.googleapi.placesdetails.PlaceDetails;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class GoogleApiPlaceStreams {
//    private static final String TAG =GoogleApiPlaceStreams.class.getSimpleName();

   // String mString="https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-33.8670522,151.1957362&radius=1500&type=" +
   //         "restaurant&keyword=cruise&key=AIzaSyD9pp59K2tcqgbzpAXeyjXQ_7DVcOaHQl0";
    //String mString2="https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-33.8670522,151.1957362&radius=1500&type=restaurant&keyword=cruise&key=AIzaSyAj8TgbhVVLCxEldGuNHxxo2w4P-S2mxG8";

    /**
     * This method to get a Stream of list of restaurants.
     * @param filter, a map filter of parameter.
     * @return a stream of restaurant.
     */

    public static Observable<PlaceNearBySearch>fetchPlaceNearBySearchStream(Map<String,String>filter){
        GoogleApiPlaceService googleApiPlaceService=GoogleApiPlaceService.retrofit.create(GoogleApiPlaceService.class);
        return googleApiPlaceService.getPlaceNearBySearch(filter)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(15,TimeUnit.SECONDS);
    }

    /*public static Observable<PlaceNearBySearch>getNearBySearchStream(Map<String,String>filter){
        GoogleApiPlaceService googleApiPlaceService = GoogleApiPlaceService.retrofit.create(GoogleApiPlaceService.class);
        return googleApiPlaceService.getPlaceNearBySearch(filter)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(15, TimeUnit.SECONDS);
    }
*/
    public static Observable<PlaceDetails>fetchPlaceDetailsStream(String key,String place_id){
        GoogleApiPlaceService googleApiPlaceService = GoogleApiPlaceService.retrofit.create(GoogleApiPlaceService.class);
        return googleApiPlaceService.getPlaceDetails(key,place_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(15,TimeUnit.SECONDS);

    }





    /*public static Observable<List<String>> streamFetchListRestaurantId(Map<String,String>filter){
        Log.d(TAG, "streamFetchListRestaurantId: ");



        return getNearBySearchStream(filter)
                .concatMap((Function<PlaceNearBySearch, Observable<List<String>>>) placeNearBySearch -> {

                    //List of restaurants Id found
                    List<String> listPlaceIdNearBySearch = new ArrayList<>();

                    Observable<List<String>> listPlacesId = Observable.fromArray(listPlaceIdNearBySearch);

                    if (placeNearBySearch.getResults().size() != 0) {
                        for (Result result : placeNearBySearch.getResults()) {
                            Log.d(TAG, "streamFetchListRestaurantId: placeId = " + result.getPlaceId());
                            listPlaceIdNearBySearch.add(result.getPlaceId());
                        }
                    } else
                        Log.d(TAG, "streamFetchListRestaurantId: placeNearBySearch.getResults().size() = null");
                    //Observable from the restaurant Id list found
                    return listPlacesId;
                });
    }*/



   /* public static Observable<List<PlaceDetails>> streamFetchListPlaceDetail(Map<String,String>filter) {
        return getNearBySearchStream(filter)
                .map(PlaceNearBySearch::getResults)
                .concatMap((Function<List<Result>, Observable<List<PlaceDetails>>>) results -> {
                            return Observable.fromIterable(results)
                                    .concatMap((Function<Result, Observable<PlaceDetails>>) result -> getPlaceDetailsStream(result.getId()))
                                    .toList()
                                    //Include data from firebase
                                    .toObservable();
                        });
    }
*/

    public static Observable<List<PlaceDetails>>streamFPlaceDetailsList(Map<String,String>filter){
        return fetchPlaceNearBySearchStream(filter)
                .map(new Function<PlaceNearBySearch, List<Result>>(){
                    @Override
                    public List<Result> apply(PlaceNearBySearch placeNearBySearch) throws Exception {
                        return placeNearBySearch.getResults();
                    }
                }).concatMap(new Function<List<Result>, Observable<List<PlaceDetails>>>() {
                    @Override
                    public Observable<List<PlaceDetails>> apply(List<Result> results) throws Exception {
                        return (Observable<List<PlaceDetails>>) Observable.fromIterable(results).concatMap(new Function<Result, Observable<PlaceDetails>>() {
                            @Override
                            public Observable<PlaceDetails> apply(Result result) throws Exception {
                                return fetchPlaceDetailsStream(filter.get("key"),result.getPlaceId());
                            }
                        }).toList()
                                .toObservable();
                    }
                });


    }





}
