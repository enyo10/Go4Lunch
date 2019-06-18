package ch.enyo.openclassrooms.go4lunch.controllers.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import ch.enyo.openclassrooms.go4lunch.R;
import ch.enyo.openclassrooms.go4lunch.api.UserHelper;
import ch.enyo.openclassrooms.go4lunch.base.BaseFragment;
import ch.enyo.openclassrooms.go4lunch.controllers.activities.WelcomeActivity;
import ch.enyo.openclassrooms.go4lunch.data.DataSingleton;
import ch.enyo.openclassrooms.go4lunch.models.firebase.User;
import ch.enyo.openclassrooms.go4lunch.models.googleapi.nearbysearch.PlaceNearBySearch;
import ch.enyo.openclassrooms.go4lunch.models.googleapi.nearbysearch.Result;
import ch.enyo.openclassrooms.go4lunch.utils.GoogleApiPlaceStreams;

import icepick.Icepick;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapViewFragment extends BaseFragment implements OnMapReadyCallback, LocationListener,WelcomeActivity.SearchInterface {

    private static final String TAG = MapViewFragment.class.getSimpleName();

    // Constant
    private static final int REQUEST_LOCATION_PERMISSION = 11;
    private static final String TRACKING_LOCATION_KEY = "tracking_location";
    private static final float DEFAULT_ZOOM = 16f;
    private GoogleMap mMap;
    private Marker mMarker;
    private Location mLastKnownLocation;

    // Location Objects.
    private LocationCallback mLocationCallback;

    private final LatLng mDefaultLocation = new LatLng(47.14, 7.28);

    private boolean mTrackingLocation;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private List<Result> mPlaceList;
    private List<User> mUserList;
    private List<String>mSelectedPlaceId;

    MapView mMapView;
    private Disposable mDisposable;
    private boolean mLocationPermissionGranted = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPlaceList = new ArrayList<>();
        mUserList=new ArrayList<>();
        mSelectedPlaceId=new ArrayList<>();


        // Initialize the FusedLocationClient.
        if (getActivity() != null)
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        // Retrieve the last know location.
        getLocationPermission();

        // Restore the state if the activity is recreated.
        if (savedInstanceState != null) {
            mTrackingLocation = savedInstanceState.getBoolean(
                    TRACKING_LOCATION_KEY);
        }

        // Initialize the location callbacks.
        mLocationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                // If tracking is turned on, reverse geocode into an address
                if (mTrackingLocation) {
                    locationResult.getLastLocation();

                }
            }
        };
        startTrackingLocation();
        Icepick.restoreInstanceState (this,savedInstanceState);

    }

    @Override
    public void onMapReady(GoogleMap gMap) {
        getDeviceLocation();

        mMap = gMap;
        getLocationPermission();
        updateUI();
        updateLocationUI();
        // Get the current location of the device and set the position of the map.
    }

    @Override
    public BaseFragment newInstance() {

        MapViewFragment mapViewFragment=new MapViewFragment();
        mapViewFragment.name="Map View";
        return mapViewFragment;
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_map_view;
    }

    @Override
    protected void configureDesign(View v) {

    }

    @Override
    protected void configureView() {
        //use SupportMapFragment for using in fragment instead of activity  MapFragment = activity   SupportMapFragment = fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapFragment);
        if(mapFragment!=null)
            mapFragment.getMapAsync(this);

        // executeRequestWithRetrofit();
        Log.i(TAG, " Configure view method.");

    }

    @Override
    protected void configureOnclickRecyclerView() {

    }

    /**
     * this method to add the marker on the map.
     * @param placeNearbyResult,
     *          the placeNearbyResult containing the restaurant to be add.
     */
    private void addMarkerOnMap(List<Result> placeNearbyResult) {
        Log.d(TAG, "in addMarker on map method");

        getAllUsersFromFireBase();

        if(placeNearbyResult.size()!=0)

        for (int i = 0; i < placeNearbyResult.size(); i++) {
            Double lat = placeNearbyResult.get(i).getGeometry().getLocation().getLat();
            Double lng = placeNearbyResult.get(i).getGeometry().getLocation().getLng();
            String placename = placeNearbyResult.get(i).getName();
            String vinicity = placeNearbyResult.get(i).getVicinity();

            MarkerOptions markerOptions = new MarkerOptions();
            LatLng latLng = new LatLng(lat, lng);
            markerOptions.position(latLng);
            markerOptions.title(placename + " : " + vinicity);

           if(mSelectedPlaceId.size()!=0 && mSelectedPlaceId.contains(placeNearbyResult.get(i).getPlaceId())) {
             //   Log.i(TAG, " place match .." + placeNearbyResult.get(i).getPlaceId());

                mMarker = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(placename + " :" + vinicity)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.restaurant_locator_for_map_green)));
            }
            else {
              //  Log.i(TAG, " place no match .." + placeNearbyResult.get(i).getPlaceId());
               // Log.i(TAG, " selected list to string "+placeIdList.toString());
                mMarker = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(placename + " :" + vinicity)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.restaurant_locator_for_map_orange)));
            }

            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
        }
    }


    /**
     * This method to update the restaurant list.
     * @param list, the list to be set.
     */
    private void updatePlaceList(List<Result>list){
        mPlaceList.clear();
        mPlaceList.addAll(list);

    }

    //----------------------------------------------------------------------------------------------
    //                 HTTP RX JAVA
    //____________________________________________________________________________________________-_

    private void executeRequestWithRetrofit(){
        double lat=mLastKnownLocation.getLatitude();
        double lng=mLastKnownLocation.getLongitude();
        String latlng=lat+","+lng;

        this.mDisposable= GoogleApiPlaceStreams.fetchPlaceNearBySearchStream(latlng)
                .subscribeWith(new DisposableObserver<PlaceNearBySearch>() {
                    @Override
                    public void onNext(PlaceNearBySearch placeNearBySearch) {
                        Log.i(TAG, " restaurant by near size "+placeNearBySearch.getResults().size());
                        // Update the restaurantList.
                        updatePlaceList(placeNearBySearch.getResults());

                        try {
                            addMarkerOnMap(placeNearBySearch.getResults());


                        }catch (Exception e){
                            Log.d(TAG, "onNext: error");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i("TAG","aie, error in place nearby search: "  +Log.getStackTraceString(e));

                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "Restaurant near by search completed.");
                    }
                });

    }

    //---------------------------------------------------------------------------------------------//
    //                                       HTTP FIRE BASE                                         //
    //---------------------------------------------------------------------------------------------//

    private void getAllUsersFromFireBase(){

        UserHelper.getAllUsers().addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    // Handle error
                    Log.i(TAG, " Error by retrieve user from fire base-->: "+e.getMessage());
                    e.printStackTrace();
                    return;
                }
                // Convert query snapshot to a list of users.

                mUserList = snapshot.toObjects(User.class);
                for (int k=0;k<mUserList.size();k++) {

                    String id =mUserList.get(k).getRestaurantId();
                    if(id!=null)
                        mSelectedPlaceId.add(id);

                }
                Log.d(TAG," User list size "+mUserList.size());
                Log.d(TAG, "Selected id list "+mSelectedPlaceId.size());

                // Update UI
                // ...
            }
        });
    }




    //---------------------------------------------------------------------------------------------//
    //                         LOCATION PERMISSION & LOCATION TRACKING.                                        //
    //---------------------------------------------------------------------------------------------//

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if(getActivity()!=null)
            if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;


            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_LOCATION_PERMISSION);
            }
    }

    /**
     * Callback that is invoked when the user responds to the permissions
     * dialog.
     *
     * @param requestCode  Request code representing the permission request
     *                     issued by the app.
     * @param permissions  An array that contains the permissions that were
     *                     requested.
     * @param grantResults An array with the results of the request for each
     *                     permission requested.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode==REQUEST_LOCATION_PERMISSION) {
            // case REQUEST_LOCATION_PERMISSION:
            // If the permission is granted, get the location,
            // otherwise, show a Toast
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted=true;
                //Update Ui
                updateUI();

            } else {
                Toast.makeText(getContext(), R.string.location_permission_denied,
                        Toast.LENGTH_SHORT).show();
            }

        }
        updateLocationUI();
    }

    /**
     * This method to start location tracking. Check for permission, if not granted yet, request it..
     */
    private void startTrackingLocation(){
        if(getContext()!=null)

            if (ActivityCompat.checkSelfPermission(getContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                if(getActivity()!=null)
                    ActivityCompat.requestPermissions(getActivity(), new String[]
                                    {Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_LOCATION_PERMISSION);
            } else {
                Log.d(TAG, "getLocation: permissions granted");

           mTrackingLocation = true;
                mFusedLocationProviderClient.requestLocationUpdates(getLocationRequest(), mLocationCallback, null /* Looper */);
                // Set a loading text while you wait for the address to be
                // returned
           /* mLocationTextView.setText(getString(R.string.address_text,
                    getString(R.string.loading),
                    System.currentTimeMillis()));
            mLocationButton.setText(R.string.stop_tracking_location);
            mRotateAnim.start();*/

            }


    }
    /**
     * Method that stops tracking the device. It removes the location
     * updates, stops the animation and reset the UI.
     */
    private void stopTrackingLocation() {
        if (mTrackingLocation) {
            mTrackingLocation = false;
            mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
        }
    }

    private LocationRequest getLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }


    /**
     * This method to update the location on the map.
     */
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }


    /**
     * This method to get the device localisation.
     */
    private void getDeviceLocation() {
        try {
            if (mLocationPermissionGranted && getActivity() != null) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(getActivity(), task -> {
                    if (task.isSuccessful()) {
                        // Set the map's camera position to the current location of the device.
                        mLastKnownLocation = task.getResult();
                        if (mLastKnownLocation != null) {
                            DataSingleton.getInstance().setLocation(mLastKnownLocation);

                            // Store device coordinates
                            double latitude = mLastKnownLocation.getLatitude();
                            double longitude = mLastKnownLocation.getLongitude();

                            //Move camera toward device position
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(latitude, longitude), DEFAULT_ZOOM));

                            //Execute http request with retrofit and RxJava2
                            this.executeRequestWithRetrofit();

                        } else {
                            Toast.makeText(getContext(),
                                    R.string.toast_message_geolocation,
                                    Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.");
                        Log.e(TAG, "getDeviceLocation => Exception: %s" + task.getException());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                        mMap.getUiSettings().setMyLocationButtonEnabled(false);
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());

        }
    }
    //---------------------------------------------------------------------------------------------
    //                                          UI
    //---------------------------------------------------------------------------------------------

    private void updateUI(){
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
              //  getDeviceLocation();
            }
            else {

                mMap.setMyLocationEnabled(false);
                Toast.makeText(this.getActivity(), "ERROR", Toast.LENGTH_SHORT).show();
                mLastKnownLocation = null;
//              Try to obtain location permission
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e(TAG, "updateUI: SecurityException " + e.getMessage());
        }

    }


    @Override
    public void onPause() {
        if (mTrackingLocation) {
            stopTrackingLocation();
            mTrackingLocation = true;
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        if (mTrackingLocation) {
            startTrackingLocation();
        }
        super.onResume();
    }



    private void destroyMap(){
        if(mMapView!=null)
            mMapView.onDestroy();
    }

    private void disposeWhenDestroy() {
        if (this.mDisposable != null && !this.mDisposable.isDisposed()) this.mDisposable.dispose();
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        disposeWhenDestroy();
        destroyMap();

        Log.d(TAG, "onDestroy");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();

        Log.d(TAG, "onLowMemory");
    }

    @Override

    public void onLocationChanged(Location location) {

        Log.i(TAG, " Location changed "+location.getLongitude());

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void doMySearch(String query) {
        Log.d(TAG,"In Map View fragment"+ query);
    }
}
