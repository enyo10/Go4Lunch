package ch.enyo.openclassrooms.go4lunch.utils;

import com.google.gson.GsonBuilder;

import java.util.Map;

import ch.enyo.openclassrooms.go4lunch.models.google.NearBySearchResult;
import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface GoogleApiPlaceService {

    @GET("nearbysearch/json")
    Observable<NearBySearchResult> getPlaceNearBySearch(@QueryMap Map<String, String> filters);



    // Use excludeFieldsWithoutExposeAnnotation() for ignore some fields
    Retrofit retrofit = new Retrofit.Builder()

            .baseUrl("https://maps.googleapis.com/maps/api/place/")
            .addConverterFactory(GsonConverterFactory.create(
                    new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build();

}
