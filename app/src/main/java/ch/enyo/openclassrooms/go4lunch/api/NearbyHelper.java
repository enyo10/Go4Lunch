package ch.enyo.openclassrooms.go4lunch.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ch.enyo.openclassrooms.go4lunch.models.googleapi.nearbysearch.PlaceNearBySearch;
import ch.enyo.openclassrooms.go4lunch.models.googleapi.nearbysearch.Result;
import ch.enyo.openclassrooms.go4lunch.utils.GoogleApiPlaceStreams;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class NearbyHelper {
    private List<Result>list;



    public static void getPlacesNearBy(Map<String,String>parameters){

        Disposable disposable= GoogleApiPlaceStreams.fetchPlaceNearBySearchStream(parameters)
              .subscribeWith(new DisposableObserver<PlaceNearBySearch>() {
                  @Override
                  public void onNext(PlaceNearBySearch placeNearBySearch) {
                    List<Result> list1=placeNearBySearch.getResults();


                  }

                  @Override
                  public void onError(Throwable e) {

                  }

                  @Override
                  public void onComplete() {

                  }
              });

    }
}
