package ch.enyo.openclassrooms.go4lunch.controllers.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.enyo.openclassrooms.go4lunch.BuildConfig;
import ch.enyo.openclassrooms.go4lunch.R;
import ch.enyo.openclassrooms.go4lunch.base.BaseFragment;
import ch.enyo.openclassrooms.go4lunch.data.DataSingleton;
import ch.enyo.openclassrooms.go4lunch.models.firebase.Restaurant;
import ch.enyo.openclassrooms.go4lunch.models.googleapi.placesdetails.PlaceDetails;
import ch.enyo.openclassrooms.go4lunch.utils.PermissionUtils;
import io.reactivex.disposables.Disposable;


public class MyMapViewFragment extends BaseFragment implements OnMapReadyCallback,OnMyLocationButtonClickListener,
        OnMyLocationClickListener,GoogleMap.OnMarkerClickListener {

    private static final String TAG = MyMapViewFragment.class.getSimpleName();

    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    public static final float DEFAULT_ZOOM = 16f;
    private static final String KEY_LOCATION = "location";

    // Variables.
    private Location mLastKnownLocation;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LatLng mDefaultLocation = new LatLng(48.813326, 2.348383);

    private Disposable mDisposable;
    private Marker marker;
    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean mPermissionDenied = false;
    private GoogleMap mMap;
    private MapView mMapView;

    // Some data.
    private List<PlaceDetails> mPlaceDetailList = new ArrayList<>();
    private HashMap<String, PlaceDetails> mMarkerMap = new HashMap<>();
    private List<Restaurant> restaurantListFromFirestore = new ArrayList<>();

    //Useful to initiate a map inside a fragment
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //To retrieve data when device rotate
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
        }

        mMapView =  view.findViewById(R.id.map);
        if (mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
        }
        super.onSaveInstanceState(outState);

    }

    @Override
    public BaseFragment newInstance() {
        MyMapViewFragment mapViewFragment=new MyMapViewFragment();
        mapViewFragment.name="Map View";
        return mapViewFragment;

    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_my_map_view;
    }

    @Override
    protected void configureDesign(View v) {

    }

    @Override
    protected void configureView() {
        this.instantiatePlacesApiClients();
        /*SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapFragment);
        if(mapFragment!=null)
        mapFragment.getMapAsync(this);
*/
    }


    private void instantiatePlacesApiClients() {

        // Construct a FusedLocationProviderClient
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());


    }


    public static Bitmap createCustomMarker(Context context, @DrawableRes int resource, RequestManager glide) {

        View marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.marker_custom_layout, null);

        ImageView markerImage =  marker.findViewById(R.id.marker_image);
        glide.load(resource).circleCrop().into(markerImage);
        //glide.load(resource).apply(RequestOptions.circleCropTransform()).into(markerImage);
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



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        enableMyLocation();

    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(getActivity(), "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(getActivity(), "Current location:\n" + location, Toast.LENGTH_LONG).show();
        Bundle bundle =location.getExtras();

        Log.i(TAG," Bundle to string " +bundle.toString());

    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission( getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission((AppCompatActivity) getActivity(), LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
            Log.i(TAG, "Location is enable");
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
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
                .newInstance(true).show((getChildFragmentManager()), "dialog");
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    //---------------------------------------------------------------------------------------------//
    //                                          UI                                                 //
    //---------------------------------------------------------------------------------------------//


    private void updateUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (!mPermissionDenied) {
              //  mGpsLocation.setVisibility(View.VISIBLE);
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                getDeviceLocation();
            } else {
               // mGpsLocation.setVisibility(View.GONE);
                mMap.setMyLocationEnabled(false);
                Toast.makeText(this.getActivity(), "ERROR", Toast.LENGTH_SHORT).show();
                mLastKnownLocation = null;
//              Try to obtain location permission
                enableMyLocation();
            }
        } catch (SecurityException e) {
            Log.e(TAG, "updateUI: SecurityException " + e.getMessage());
        }
    }


    private void addMarkerOnMap(List<PlaceDetails> placeDetailList) {
        //Initialize and store data in both array and singleton
        this.mPlaceDetailList.addAll(placeDetailList);
        DataSingleton.getInstance().setPlaceDetailsList(mPlaceDetailList);

        if ( mPlaceDetailList !=null && mPlaceDetailList.size() != 0 ) {
            for (int i = 0; i < mPlaceDetailList.size(); i++) {
                if (mPlaceDetailList.get(i).getResult() != null) {
                    marker = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(mPlaceDetailList.get(i).getResult().getGeometry().getLocation().getLat(),
                                    mPlaceDetailList.get(i).getResult().getGeometry().getLocation().getLng()))
                           // .icon(BitmapDescriptorFactory.fromResource(R.drawable.restaurant_locator_for_map_orange))
                            .title(mPlaceDetailList.get(i).getResult().getName()));

                    //Change color of marker if a workmate selected a restaurant
                   // this.changeMarkerColor(i);

                    // Store in HashMap for Marker id for clickHandler
                    mMarkerMap.put(marker.getId(), mPlaceDetailList.get(i));
                }
            }

        } else {
            Log.d(TAG, "addMarkerOnMap is empty :" + mPlaceDetailList.size());
        }
    }

    //---------------------------------------------------------------------------------------------//
    //                                         LOCATION                                            //
    //---------------------------------------------------------------------------------------------//


    private void getDeviceLocation() {
        try {
            if (!mPermissionDenied && getActivity() != null) {
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

                            String location =""+latitude+","+longitude;

                            Map<String,String> filter =new HashMap<>();
                            filter.put("api_key", BuildConfig.ApiKey);
                            filter.put("radius","1500");
                            filter.put("location",location);
                            filter.put("type","restaurant");


                            //Execute http request with retrofit and RxJava2
                            this.executeHttpRequestWithNearBySearchAndPlaceDetail(filter);

                        } else {
                            Toast.makeText(getContext(),
                                    " toast message geolocation ",
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


    //---------------------------------------------------------------------------------------------//
    //                                      HTTP RxJava                                            //
    //---------------------------------------------------------------------------------------------//


    public void executeHttpRequestWithNearBySearchAndPlaceDetail(Map<String,String> filter) {
        /*mDisposable = GoogleApiPlaceStreams.streamFetchListPlaceDetail(filter)
                .subscribeWith(new DisposableObserver<List<PlaceDetails>>() {
                    @Override
                    public void onNext(List<PlaceDetails> placeDetail) {
                        Log.d(TAG, "onNext: " + placeDetail.size());
                        addMarkerOnMap(placeDetail);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: " + mPlaceDetailList.size());
                        if (mPlaceDetailList.isEmpty())
                            Toast.makeText(getContext(), "Restaurant not found", Toast.LENGTH_LONG).show();
                    }
                });*/
    }

    // Dispose subscription
    private void disposeWhenDestroy() {
        if (this.mDisposable != null && !this.mDisposable.isDisposed())
            this.mDisposable.dispose();
    }

    // Called for better performances
    @Override
    public void onDestroy() {
        super.onDestroy();
        this.disposeWhenDestroy();
    }

    //---------------------------------------------------------------------------------------------//
    //                                      PERMISSION                                             //
    //---------------------------------------------------------------------------------------------//





}
