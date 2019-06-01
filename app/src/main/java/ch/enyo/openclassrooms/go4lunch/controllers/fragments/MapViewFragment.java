package ch.enyo.openclassrooms.go4lunch.controllers.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import ch.enyo.openclassrooms.go4lunch.R;
import ch.enyo.openclassrooms.go4lunch.base.BaseFragment;
import ch.enyo.openclassrooms.go4lunch.data.DataSingleton;
import ch.enyo.openclassrooms.go4lunch.models.googleapi.nearbysearch.PlaceNearBySearch;
import ch.enyo.openclassrooms.go4lunch.models.googleapi.placesdetails.PlaceDetails;
import ch.enyo.openclassrooms.go4lunch.utils.GoogleApiPlaceStreams;

import icepick.Icepick;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapViewFragment extends BaseFragment implements OnMapReadyCallback, LocationListener{

    private static final String TAG = MapViewFragment.class.getSimpleName();

    // Constant
    private static final int REQUEST_LOCATION_PERMISSION=11;
    private static final String TRACKING_LOCATION_KEY = "tracking_location";
    public static final float DEFAULT_ZOOM = 16f;
    private GoogleMap mMap;
    // Location classes
    LocationCallback mLocationCallback;
    Location mLastKnownLocation;
    private final LatLng mDefaultLocation=new LatLng(DataSingleton.getInstance().getLatitude(),DataSingleton.getInstance().getLongitude());
    private boolean mTrackingLocation;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private List<PlaceDetails>mPlaceDetailsList;
    MapView mMapView;
    Disposable mDisposable;
    private boolean mLocationPermissionGranted=false;
   // @BindView(R.id.position_icon)
    Button mGpsButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPlaceDetailsList= new ArrayList<>();

        // Initialize the FusedLocationClient.
        if(getActivity()!=null)
        mFusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(getActivity());


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
                    Log.i(TAG, " latitude "+ locationResult.getLastLocation().getLatitude());
                    Log.i(TAG, " longitude "+ locationResult.getLastLocation().getLongitude());

                }
            }
        };
        startTrackingLocation();
        Icepick.restoreInstanceState (this,savedInstanceState);

    }

    @Override
    public void onMapReady(GoogleMap gMap) {

        mMap = gMap;

        getLocationPermission();
        updateUI();

        // Do other setup activities here too, as described elsewhere in this tutorial.

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();

       /* Log.d(TAG, "On MapReady latitude. "+DataSingleton.getInstance().getLatitude());
        Log.d(TAG, " On MapReady longitude "+DataSingleton.getInstance().getLongitude());

        // Add a marker in Sydney and move the camera
        LatLng latLng = new LatLng(DataSingleton.getInstance().getLatitude(),DataSingleton.getInstance().getLongitude());
        mGoogleMap.addMarker(new MarkerOptions().position(latLng).title("Marker in Sydney"));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(DataSingleton.getInstance().getLatitude(),
                        DataSingleton.getInstance().getLongitude()), DEFAULT_ZOOM));
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);*/

    }

   /* private void addMarkerOnMap(List<PlaceDetails> placeDetailList) {
        //Initialize and store data in both array and singleton
        this.mPlaceDetailsList.addAll(placeDetailList);
        DataSingleton.getInstance().setPlaceDetailsList(placeDetailList);

        
    }*/

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

         executeRequestWithRetrofit();
        Log.i(TAG, " Configure view method.");

    }

    @Override
    protected void configureOnclickRecyclerView() {

    }


    public void executeRequestWithRetrofit(){

        Map<String,String> parametersMap=DataSingleton.getInstance().getParametersMap();

        Log.i(TAG, "parameter map value "+parametersMap.toString());

       this.mDisposable= GoogleApiPlaceStreams.fetchPlaceNearBySearchStream(parametersMap)
               .subscribeWith(new DisposableObserver<PlaceNearBySearch>() {
                   @Override
                   public void onNext(PlaceNearBySearch placeNearBySearch) {
                       Log.i(TAG, " restaurant by near size "+placeNearBySearch.getResults().size());

                       try {

                           mMap.clear();
                           // Add a marker in Sydney and move the camera
                          // LatLng latLng1 = new LatLng(DataSingleton.getInstance().getLatitude(),DataSingleton.getInstance().getLongitude());
                          // mGoogleMap.addMarker(new MarkerOptions().position(latLng1).title("Marker in Sydney"));
                           mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                   new LatLng(DataSingleton.getInstance().getLatitude(),
                                           DataSingleton.getInstance().getLongitude()), DEFAULT_ZOOM));
                           mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                           for(int i=0;i<placeNearBySearch.getResults().size();i++){
                               Double lat =placeNearBySearch.getResults().get(i).getGeometry().getLocation().getLat();
                               Double lng=placeNearBySearch.getResults().get(i).getGeometry().getLocation().getLng();
                               String placename =placeNearBySearch.getResults().get(i).getName();
                               String vinicity =placeNearBySearch.getResults().get(i).getVicinity();

                               MarkerOptions markerOptions=new MarkerOptions();
                               LatLng latLng =new LatLng(lat,lng);
                               markerOptions.position(latLng);
                               markerOptions.title(placename +" : "+vinicity);
                               // Adding camera
                               mMap.addMarker(markerOptions);
                               // Adding color to the marker.
                               markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                               // move map camera
                               mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                               mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
                           }

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


/*
    private void addPointToViewPort(LatLng newPoint) {
        mBounds.include(newPoint);
        //mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(mBounds.build(),
           //     findViewById(R.id.checkout_button).getHeight()));
    }*/

  /*  private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }*/


   /* public static Bitmap createCustomMarker(Context context, @DrawableRes int resource, RequestManager glide) {

        View marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.marker_custom_layout, null);

        ImageView markerImage =  marker.findViewById(R.id.marker_image);
      //  glide.load(resource).circleCrop().into(markerImage);
        glide.load(resource).apply(RequestOptions.circleCropTransform()).into(markerImage);
        //markerImage.setImageResource(resource);
        //TextView txt_name = (TextView)marker.findViewById(R.id.name);
        // txt_name.setText(_name);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        marker.setLayoutParams(new ViewGroup.LayoutParams(52, ViewGroup.LayoutParams.WRAP_CONTENT));
        marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
       // marker.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(marker.getMeasuredWidth(), marker.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        marker.draw(canvas);

        return bitmap;
    }
*/


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
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
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
               // break;
        }
        updateLocationUI();
    }

    /**
     * This method to start location tracking. Check for permission, if not granted yet, request it..
     */
    private void startTrackingLocation(){

        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            Log.d(TAG, "getLocation: permissions granted");

           /* mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if(location!=null){

                        // Start the reverse geocode AsyncTask
                        new FetchAddressTask(MainActivity.this,
                                MainActivity.this).execute(location);
                        *//*mLastLocation=location;
                        mLocationTextView.setText(getString(R.string.location_text,
                                mLastLocation.getLatitude()
                                ,mLastLocation.getLongitude()
                                ,mLastLocation.getTime()));*//*
                    } else {
                        mLocationTextView.setText(R.string.no_location);
                    }

                }
            });
*/          mTrackingLocation = true;
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

                            // Store device coordinates
                            Double latitude = mLastKnownLocation.getLatitude();
                            Double longitude = mLastKnownLocation.getLongitude();
                            DataSingleton.getInstance().setLatitude(latitude);
                            DataSingleton.getInstance().setLongitude(longitude);

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

    public void updateUI(){
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
             //   mGpsButton.setVisibility(View.VISIBLE);
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                getDeviceLocation();
            } else {
            //    mGpsButton.setVisibility(View.GONE);
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
}
