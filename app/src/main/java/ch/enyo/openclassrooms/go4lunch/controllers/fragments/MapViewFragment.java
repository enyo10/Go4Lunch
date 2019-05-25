package ch.enyo.openclassrooms.go4lunch.controllers.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
//import com.google.android.gms.location.FusedLocationProviderClient;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.List;
import java.util.Map;
import ch.enyo.openclassrooms.go4lunch.R;
import ch.enyo.openclassrooms.go4lunch.base.BaseFragment;
import ch.enyo.openclassrooms.go4lunch.data.DataSingleton;
import ch.enyo.openclassrooms.go4lunch.models.googleapi.nearbysearch.PlaceNearBySearch;
import ch.enyo.openclassrooms.go4lunch.models.googleapi.nearbysearch.Result;
import ch.enyo.openclassrooms.go4lunch.utils.GoogleApiPlaceStreams;

import icepick.Icepick;
import icepick.State;
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


    private GoogleMap mGoogleMap=null;
    // Location classes
    LocationCallback mLocationCallback;
    private boolean mTrackingLocation;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    MapView mMapView;
    Disposable mDisposable;
    private List<Result> mResultList;

    @State
    Double mLongitude;
    @State
    Double mLatitude;
    //LatLngBound will cover all your marker on Google Maps
    LatLngBounds.Builder builder;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataSingleton dataSingleton=DataSingleton.getInstance();
        mLatitude=dataSingleton.getLatitude();
        mLongitude=dataSingleton.getLongitude();
        builder = new LatLngBounds.Builder();

        // Initialize the FusedLocationClient.
        if(getActivity()!=null)
        mFusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(getActivity());


        // Restore the state if the activity is recreated.
        if (savedInstanceState != null) {
            mTrackingLocation = savedInstanceState.getBoolean(
                    TRACKING_LOCATION_KEY);
        }

        // Initialize the location callbacks.
        mLocationCallback=new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                // If tracking is turned on, reverse geocode into an address
                if (mTrackingLocation) {
                    locationResult.getLastLocation();
                    Log.i(TAG, " latitude "+ locationResult.getLastLocation().getLatitude());

                }
            }
        };
        startTrackingLocation();

        Icepick.restoreInstanceState (this,savedInstanceState);


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

                           mGoogleMap.clear();
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
                               mGoogleMap.addMarker(markerOptions);
                               // Adding color to the marker.
                               markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                               // move map camera
                               mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                               mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(12));
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


    public void updateUI(PlaceNearBySearch placeNearBySearch){
        try {

            mGoogleMap.clear();
            for(int i=0;i<placeNearBySearch.getResults().size();i++){
                Double lat = placeNearBySearch.getResults().get(i).getGeometry().getLocation().getLat();
                Double lng=  placeNearBySearch.getResults().get(i).getGeometry().getLocation().getLng();
                String placeName =placeNearBySearch.getResults().get(i).getName();
                String vinicity =placeNearBySearch.getResults().get(i).getVicinity();

                MarkerOptions markerOptions=new MarkerOptions();
                LatLng latLng =new LatLng(lat,lng);
                markerOptions.position(latLng);
                markerOptions.title(placeName +" : "+vinicity);
                // Adding camera
                Marker marker = mGoogleMap.addMarker(markerOptions);
                // Adding color to the marker.
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                // move map camera
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(11));
            }

        }catch (Exception e){
            Log.d(TAG, "onNext: error");
        }


    }

    @Override
    public void onMapReady(GoogleMap gMap) {

       // Log.i(TAG, "The key "+apiKey);
        mGoogleMap = gMap;

       Log.d(TAG, "On MapReady latitude. "+DataSingleton.getInstance().getLatitude());

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(DataSingleton.getInstance().getLongitude(),DataSingleton.getInstance().getLatitude());
        mGoogleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
       // mGoogleMap.clear();
       // updateResultList(mResultList);
//        updateUI(mResultList);



      //  LatLng latLng =new LatLng(-33.8670522,151.1957362);
             //  CameraPosition googlePlex = CameraPosition.builder()
               // .target(new LatLng(37.4219999,-122.0862462))
               //        .target(sydney)
              //  .zoom(10)
              //  .bearing(0)
              //  .tilt(45)
             //   .build();

               /*for(Result result:mResultList){
                   Log.i(TAG, " marker "+result.getName());
                   this.addMarker(result,R.drawable.baseline_restaurant_white_36);
               }*/

     //   mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 10000, null);

       /* LatLng customMarkerLocationFour=new LatLng(37.4219999, -122.0862462);

        mGoogleMap.addMarker(new MarkerOptions()
                .position(customMarkerLocationFour)
                .title("Spider Man")
               .icon(bitmapDescriptorFromVector(getActivity(),R.drawable.spider)));

        LatLng customMarkerLocationOne= new LatLng(37.3092293,-122.1136845);

        mGoogleMap.addMarker(new MarkerOptions()
                .position(customMarkerLocationOne)
                .icon(BitmapDescriptorFactory.fromBitmap( createCustomMarker(getActivity(),R.drawable.baseline_restaurant_white_36, Glide.with(this))))).setTitle("iPragmatech Solutions Pvt Lmt");


        mGoogleMap.addMarker(new MarkerOptions()
                .position(new LatLng(37.4629101,-122.2449094))
                .title("Iron Man")
                .snippet("His Talent : Plenty of money"));

        mGoogleMap.addMarker(new MarkerOptions()
                .position(new LatLng(37.3092293,-122.1136845))
                .title("Captain America"));*/

        /*LatLng customMarkerLocationtwo = new LatLng(37.3092293,-122.1136845);
        mGoogleMap.addMarker(new MarkerOptions()
                .position(customMarkerLocationtwo)
                .icon(BitmapDescriptorFactory.fromBitmap(
                        createCustomMarker(getActivity(),R.drawable.baseline_restaurant_white_36, Glide.with(this))))).setTitle("iPragmatech Solutions Pvt Lmt");


        //LatLngBound will cover all your marker on Google Maps
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(customMarkerLocationFour); //Taking Point B (Second LatLng)
        builder.include(customMarkerLocationOne);
        builder.include(customMarkerLocationtwo);
        LatLngBounds bounds = builder.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 200);
        mGoogleMap.moveCamera(cu);
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);*/
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


    public static Bitmap createCustomMarker(Context context, @DrawableRes int resource, RequestManager glide) {

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
            mFusedLocationProviderClient.requestLocationUpdates
                    (getLocationRequest(), mLocationCallback,
                            null /* Looper */);
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
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startTrackingLocation();
                } else {
                    Toast.makeText(getContext(), R.string.location_permission_denied,
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }



    private LocationRequest getLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
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
