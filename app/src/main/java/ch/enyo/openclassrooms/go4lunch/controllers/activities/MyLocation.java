package ch.enyo.openclassrooms.go4lunch.controllers.activities;

        import android.Manifest;

        import android.content.pm.PackageManager;
        import android.location.Location;
        import android.support.annotation.NonNull;
        import android.support.v4.app.ActivityCompat;
        import android.support.v4.content.ContextCompat;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.util.Log;
        import android.widget.Toast;
        import com.google.android.gms.location.FusedLocationProviderClient;
        import com.google.android.gms.location.LocationServices;
        import com.google.android.gms.maps.GoogleMap;
        import com.google.android.gms.maps.OnMapReadyCallback;
        import com.google.android.gms.maps.SupportMapFragment;


        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.List;
        import java.util.Map;

        import ch.enyo.openclassrooms.go4lunch.BuildConfig;
        import ch.enyo.openclassrooms.go4lunch.R;
        import ch.enyo.openclassrooms.go4lunch.models.googleapi.nearbysearch.PlaceNearBySearch;
        import ch.enyo.openclassrooms.go4lunch.models.googleapi.placesdetails.PlaceDetails;
        import ch.enyo.openclassrooms.go4lunch.utils.GoogleApiPlaceStreams;
        import ch.enyo.openclassrooms.go4lunch.utils.PermissionUtils;
        import io.reactivex.disposables.Disposable;
        import io.reactivex.observers.DisposableObserver;


public class MyLocation extends AppCompatActivity implements
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = MyLocation.class.getSimpleName();
    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private FusedLocationProviderClient fusedLocationClient;
    private Location mLocation;
    private Disposable mDisposable;
    private List<PlaceDetails> mPlaceDetails;

    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean mPermissionDenied = false;

    private GoogleMap mMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_location);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
        Log.i(TAG, "My Location activity is created");

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mPlaceDetails = new ArrayList<>();

        getLocation();
        upDate();


    }


    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).


        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        enableMyLocation();

    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_COARSE_LOCATION, true);
            Log.i(TAG, "My Location is enable");
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_COARSE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }


    // @RequiresPermission(allOf = {ACCESS_FINE_LOCATION, ACCESS_WIFI_STATE})
    public void getLocation() {
        Log.i(TAG, " In get location method");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.i(TAG, "Permission not allowed");
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        mLocation=location;
                        // Logic to handle location object
                        Log.i(TAG, "Location latitude:-> " + location.getLatitude() + " Location longitude:-> " + location.getLongitude());
                        Log.i(TAG, "location time " + location.getTime());


                        //Move camera toward device position
                        /*mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                new LatLng(latitude, longitude), DEFAULT_ZOOM));*/


                    }
                });
    }


    public void executeHttpRequestWithNearBySearchAndPlaceDetail(Map<String, String> filter) {
        /*mDisposable = GoogleApiPlaceStreams.fetchPlaceNearBySearchStream(filter)
                .subscribeWith(new DisposableObserver<PlaceNearBySearch>() {
                    @Override
                    public void onNext(PlaceNearBySearch placeNearBySearch) {
                        Log.i(TAG,"filter" +filter.toString());
                        Log.i(TAG, "Place near by " +placeNearBySearch.getResults().size());

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG,"OOOps, aie aie: Error --> "+Log.getStackTraceString(e));

                    }

                    @Override
                    public void onComplete() {

                    }
                });
*/




               /* .subscribeWith(new DisposableObserver<List<PlaceDetails>>() {
                    @Override
                    public void onNext(List<PlaceDetails> placeDetail) {
                        Log.d(TAG, "onNext: " + placeDetail.size());
                        // addMarkerOnMap(placeDetail);
                        mPlaceDetails.addAll(placeDetail);

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG,"OOOps, aie aie: Error --> "+Log.getStackTraceString(e));

                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: " + mPlaceDetails.size());
                        if (mPlaceDetails.isEmpty())
                            Toast.makeText(getApplicationContext(), "Restaurant not found", Toast.LENGTH_LONG).show();
                    }
                });*/
    }

    public void upDate(){

        Log.i(TAG, " upDate method call");
        if(this.mLocation!=null){
            // Store device coordinates
           // Double latitude = mLocation.getLatitude();
           // Double longitude = mLocation.getLongitude();

            String my_location =""+mLocation.getLatitude()+","+mLocation.getLongitude();
            Map<String,String> filter =new HashMap<>();
            filter.put("key", BuildConfig.ApiKey);
            filter.put("radius","1500");
            filter.put("location",my_location);
            filter.put("type","restaurant");
            filter.put("keyword","cruise");

            //Execute http request with retrofit and RxJava2
            this.executeHttpRequestWithNearBySearchAndPlaceDetail(filter);

        }
    }


}
