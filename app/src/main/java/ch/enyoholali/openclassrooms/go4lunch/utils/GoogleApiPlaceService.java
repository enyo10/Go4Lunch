package ch.enyoholali.openclassrooms.go4lunch.utils;

import com.google.gson.GsonBuilder;

import java.util.Map;

import ch.enyoholali.openclassrooms.go4lunch.models.googleapi.autocomplete.PlaceAutoComplete;
import ch.enyoholali.openclassrooms.go4lunch.models.googleapi.nearbysearch.PlaceNearBySearch;
import ch.enyoholali.openclassrooms.go4lunch.models.googleapi.placesdetails.PlaceDetails;
import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;


public interface GoogleApiPlaceService {
   // String mString="https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-33.8670522,151.1957362&radius=1500&type=" +
     //       "restaurant&keyword=cruise&key=AIzaSyD9pp59K2tcqgbzpAXeyjXQ_7DVcOaHQl0";


    @GET("nearbysearch/json")
    Observable<PlaceNearBySearch> getPlaceNearBySearch(@QueryMap Map<String, String> filters);

    @GET("details/json?")
  // @GET("details/json?&key="+apiKey)
    Observable<PlaceDetails> getPlaceDetails(@Query("key")String key,@Query("placeid")String placeId);

    @GET("queryautocomplete/json")
    Observable<PlaceAutoComplete>getAutoCompletePlaceDetails(@QueryMap Map<String,String >parameters);


    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/place/")
            .addConverterFactory(GsonConverterFactory.create(
                    new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build();



//    @GET("nearbysearch/json")
//    Observable<PlaceNearBySearch> getPlaceNearBySearch(@QueryMap Map<String, String> filters);
//    @GET("nearbysearch/json")
//    Observable<PlaceNearBySearch>get_PlaceNearBySearch(@QueryMap Map<String,String>filters);
//
//    // Place Details
//
//   // @GET("details/json?")
//   @GET("details/json?&key="+apiKey)
//    Observable<PlaceAutoComplete> getPlaceDetails(@Query("placeId")String placeId);
//
//
//
//
//    // Use excludeFieldsWithoutExposeAnnotation() for ignore some fields
//    Retrofit retrofit = new Retrofit.Builder()
//
//            .baseUrl("https://maps.googleapis.com/maps/api/place/")
//            .addConverterFactory(GsonConverterFactory.create(
//                    new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()))
//            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//            .build();

}
