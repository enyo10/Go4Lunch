package ch.enyo.openclassrooms.go4lunch.controllers.fragments;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;


// Add import statements for the new library.

import com.google.android.libraries.places.api.net.PlacesClient;


import ch.enyo.openclassrooms.go4lunch.BuildConfig;
import ch.enyo.openclassrooms.go4lunch.R;
import ch.enyo.openclassrooms.go4lunch.base.BaseFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapViewFragment extends BaseFragment implements OnMapReadyCallback {
    private static final String TAG =MapViewFragment.class.getSimpleName();
    private GoogleMap mGoogleMap=null;
    MapView mMapView;
    String apiKey = BuildConfig.ApiKey;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize Places.
      //  Places.initialize(getActivity().getApplicationContext(),getString( R.string.google_maps_key));

       // Create a new Places client instance.
       // PlacesClient placesClient = Places.createClient(getActivity());


    }

    /*@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= super.onCreateView(inflater, container, savedInstanceState);


        return view;
    }*/

    @Override
    public BaseFragment newInstance() {

        MapViewFragment mapViewFragment=new MapViewFragment();
        mapViewFragment.name="Map View";
        return mapViewFragment;
    }

    @Override
    public void onMapReady(GoogleMap gMap) {
        Log.i(TAG, "The key "+apiKey);
        mGoogleMap = gMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mGoogleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
       // mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mGoogleMap.clear();

                CameraPosition googlePlex = CameraPosition.builder()
                .target(new LatLng(37.4219999,-122.0862462))
                .zoom(10)
                .bearing(0)
                .tilt(45)
                .build();

        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 10000, null);

        mGoogleMap.addMarker(new MarkerOptions()
                .position(new LatLng(37.4219999, -122.0862462))
                .title("Spider Man")
               .icon(bitmapDescriptorFromVector(getActivity(),R.drawable.spider)));

        mGoogleMap.addMarker(new MarkerOptions()
                .position(new LatLng(37.4629101,-122.2449094))
                .title("Iron Man")
                .snippet("His Talent : Plenty of money"));

        mGoogleMap.addMarker(new MarkerOptions()
                .position(new LatLng(37.3092293,-122.1136845))
                .title("Captain America"));

    }
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
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
        //use SuppoprtMapFragment for using in fragment instead of activity  MapFragment = activity   SupportMapFragment = fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();

        Log.d(TAG, "onDestroy");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();

        Log.d(TAG, "onLowMemory");
    }


}
