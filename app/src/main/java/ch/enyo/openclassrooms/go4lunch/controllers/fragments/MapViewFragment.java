package ch.enyo.openclassrooms.go4lunch.controllers.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.enyo.openclassrooms.go4lunch.R;
import ch.enyo.openclassrooms.go4lunch.api.UserHelper;
import ch.enyo.openclassrooms.go4lunch.base.BaseFragment;
import ch.enyo.openclassrooms.go4lunch.controllers.activities.PlaceDetailsActivity;
import ch.enyo.openclassrooms.go4lunch.controllers.activities.WelcomeActivity;
import ch.enyo.openclassrooms.go4lunch.data.DataSingleton;
import ch.enyo.openclassrooms.go4lunch.models.firebase.User;
import ch.enyo.openclassrooms.go4lunch.models.googleapi.nearbysearch.PlaceNearBySearch;
import ch.enyo.openclassrooms.go4lunch.models.googleapi.nearbysearch.Result;
import ch.enyo.openclassrooms.go4lunch.models.googleapi.placesdetails.PlaceDetails;
import ch.enyo.openclassrooms.go4lunch.utils.GoogleApiPlaceStreams;

import icepick.Icepick;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapViewFragment extends BaseFragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, WelcomeActivity.DataInterface {

    private static final String TAG = MapViewFragment.class.getSimpleName();

    // Constant
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final String KEY_LOCATION = "location";
    private GoogleMap mMap;
    private Marker mMarker;
    private Location mLastKnownLocation;

    private List<Result> mPlaceList;
    private List<User> mUserList;
    private List<String> mSelectedPlaceId;
    private Map<String , PlaceDetails> mPlaceDetailsMap;
    private List<PlaceDetails> mPlaceDetailsList;

    MapView mMapView;
    private Disposable mDisposable;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPlaceList = new ArrayList<>();
        mUserList = new ArrayList<>();
        mSelectedPlaceId = new ArrayList<>();
        mPlaceDetailsMap=new HashMap<>();
        mPlaceDetailsList=new ArrayList<>();
        getAllUsersFromFireBase();

        Icepick.restoreInstanceState(this, savedInstanceState);

    }



    @Override
    public BaseFragment newInstance() {

        MapViewFragment mapViewFragment = new MapViewFragment();
        mapViewFragment.name = "Map View";
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
        if (mapFragment != null)
            mapFragment.getMapAsync(this);
        Log.i(TAG, " Configure view method.");

    }

    @Override
    protected void configureOnclickRecyclerView() {

    }

    // ------------------------------------------------------------------------------------------------------------------------
    //                                       UI
    //_________________________________________________________________________________________________________________________


    @Override
    public void onMapReady(GoogleMap gMap) {
        Log.i(TAG," On map Ready");

        mMap = gMap;
        mMap.setOnMarkerClickListener(this);

        //  updateUI();
        //  updateLocationUI();
        // Get the current location of the device and set the position of the map.
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.d(TAG, " on marker clicked "+marker.getPosition().toString() + " "+marker.getId());
        PlaceDetails placeDetails = DataSingleton.getInstance().getPlaceDetailsHashMap().get(marker.getId());
        Log.d(TAG, "marker size " +mPlaceDetailsMap.keySet());
        if(placeDetails!=null){
        Log.d(TAG, " place details " +placeDetails.getResult().getId());
        DataSingleton.getInstance().setPlaceDetails(placeDetails);

        startActivity(PlaceDetailsActivity.class);
        }


        return false;
    }



    /**
     * This method to update the location on the map.
     */
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLastKnownLocation != null) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void setMyLocationOnMap() {
        if(getActivity()!=null)
        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

            Log.d(TAG,"Location permission do not grandted");
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

    }

  /*  *//**
     * this method to add the marker on the map.
     * @param placeNearbyResult,
     *          the placeNearbyResult containing the restaurant to be add.
     *//*
       private void addMarkerOnMap(List<Result> placeNearbyResult) {
        Log.d(TAG, "in addMarker on map method");
        Map<String,Result>resultMap=new HashMap<>();

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

                if( mSelectedPlaceId.contains(placeNearbyResult.get(i).getPlaceId())) {


                    mMarker = mMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title(placename + " :" + vinicity)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.restaurant_locator_for_map_green)));
                }
                else {
                    mMarker = mMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title(placename + " :" + vinicity)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.restaurant_locator_for_map_orange)));
                }
                Log.d(TAG, " marker id "+ mMarker.getId());
                resultMap.put(mMarker.getId(),placeNearbyResult.get(i));

                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(12));

            }
        DataSingleton.getInstance().setResultHashMap(resultMap);
        setMyLocationOnMap();


    }
*/
    private void addMarkerOnMap1(List<PlaceDetails> placeNearbyResult) {
        Log.d(TAG, "in addMarker on map method");
        Map<String,PlaceDetails>resultMap=new HashMap<>();

        getAllUsersFromFireBase();

        if(placeNearbyResult.size()!=0)

            for (int i = 0; i < placeNearbyResult.size(); i++) {
                Double lat = placeNearbyResult.get(i).getResult().getGeometry().getLocation().getLat();
                Double lng = placeNearbyResult.get(i).getResult().getGeometry().getLocation().getLng();
                String placename = placeNearbyResult.get(i).getResult().getName();
                String vinicity = placeNearbyResult.get(i).getResult().getVicinity();

                MarkerOptions markerOptions = new MarkerOptions();
                LatLng latLng = new LatLng(lat, lng);
                markerOptions.position(latLng);
                markerOptions.title(placename + " : " + vinicity);

                if( mSelectedPlaceId.contains(placeNearbyResult.get(i).getResult().getPlaceId())) {


                    mMarker = mMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title(placename + " :" + vinicity)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.restaurant_locator_for_map_green)));
                }
                else {
                    mMarker = mMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title(placename + " :" + vinicity)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.restaurant_locator_for_map_orange)));
                }

                resultMap.put(mMarker.getId(),placeNearbyResult.get(i));

                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(12));

            }
       // mPlaceDetailsMap = resultMap;
        setMyLocationOnMap();
        DataSingleton.getInstance().setPlaceDetailsMap(resultMap);


    }


    @Override
    public void doMySearch(String query) {
        Log.d(TAG,"In Map View fragment"+ query);
    }


    @Override
    public void update(List<PlaceDetails>placeDetailsList) {
        Log.d(TAG, " In mapView fragment: place details list " +placeDetailsList.size());
        updateUIWithPlaceDetailsList(placeDetailsList);
       // addMarkerOnMap1(placeDetailsList);
        Log.i(TAG, " Place details list update and size : "+mPlaceDetailsList.size());
        try {
            addMarkerOnMap1(placeDetailsList);


        }catch (Exception e){
            Log.d(TAG, "onNext: error");
        }



    }



    private void updateUIWithPlaceDetailsList(List<PlaceDetails>list){

      //  this.mPlaceDetailsList.clear();
        this.mPlaceDetailsList=list;

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

                List<User> userList = snapshot.toObjects(User.class);
                List<String>selectedRestaurantId=new ArrayList<>();
                for (int k=0;k<userList.size();k++) {

                    String id =userList.get(k).getRestaurantId();
                    if(id!=null)
                        selectedRestaurantId.add(id);

                }
                mUserList=userList;
                // Selected Restaurant id list.
                mSelectedPlaceId=selectedRestaurantId;
                Log.d(TAG," User list size : "+mUserList.size());
                Log.d(TAG, "Selected id list size : "+mSelectedPlaceId.size());

                // Update UI
                // ...
            }
        });
    }






    /**
     * This method to get the device localisation.
     */
   /* private void getDeviceLocation() {
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
    }*/
    //---------------------------------------------------------------------------------------------
    //                                          UI
    //---------------------------------------------------------------------------------------------

    /*private void updateUI(){
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

    }*/

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


}
