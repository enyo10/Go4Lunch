package ch.enyoholali.openclassrooms.go4lunch;

import org.junit.Test;

import java.util.List;

import ch.enyoholali.openclassrooms.go4lunch.models.googleapi.nearbysearch.PlaceNearBySearch;
import ch.enyoholali.openclassrooms.go4lunch.models.googleapi.placesdetails.PlaceDetails;
import ch.enyoholali.openclassrooms.go4lunch.utils.GoogleApiPlaceStreams;
import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;

import static org.hamcrest.MatcherAssert.assertThat;

public class GoogleApiPlaceStreamsTest {

    @Test
    public void fetchPlaceNearby() {
       String location="-33.8670522,151.1957362";
        //1 - Get the stream
        Observable<PlaceNearBySearch>observablePlaceNearbySearch= GoogleApiPlaceStreams.fetchPlaceNearBySearchStream(location);
        //2 - Create a new TestObserver
        TestObserver<PlaceNearBySearch> testObserver = new TestObserver<>();
        //3 - Launch observable
        observablePlaceNearbySearch.subscribeWith(testObserver)
                .assertNoErrors() // 3.1 - Check if no errors
                .assertNoTimeout() // 3.2 - Check if no Timeout
                .awaitTerminalEvent(); // 3.3 - Await the stream terminated before continue


       // 4 - Get the places fetched
       PlaceNearBySearch placeNearBySearchFetched = testObserver.values().get(0);

        // - Verify if status: "OK"
        assertThat("The status of the Stream was read correctly", placeNearBySearchFetched.getStatus().equals("OK"));

        // - Verify if Results Exist
        assertThat("Results exist in the Stream request", placeNearBySearchFetched.getResults().size()!=0);
        // - Verify if we have 20 article.
        assertThat(" Article size is 20.",placeNearBySearchFetched.getResults().size()==20);
    }

    @Test
    public void fetchPlacePlaceDetailsList() {
        String location="-33.8670522,151.1957362";
        //1 - Get the stream
        Observable<List<PlaceDetails>>observablePlaceDetailsList= GoogleApiPlaceStreams.streamFPlaceDetailsList(location);
        //2 - Create a new TestObserver
        TestObserver<List<PlaceDetails>> testObserver = new TestObserver<>();
        //3 - Launch observable
        observablePlaceDetailsList.subscribeWith(testObserver)
                .assertNoErrors() // 3.1 - Check if no errors
                .assertNoTimeout() // 3.2 - Check if no Timeout
                .awaitTerminalEvent(); // 3.3 - Await the stream terminated before continue


        // 4 - Get the places fetched
        List<PlaceDetails> placeDetailsListFetched = testObserver.values().get(0);

        // - Verify if Results Exist
        assertThat("Results exist in the Stream request", placeDetailsListFetched.size()!=0);
        // - Verify if we have 20 article.
        assertThat(" Article size is 20.",placeDetailsListFetched.size()==20);
    }



}
