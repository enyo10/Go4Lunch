package ch.enyoholali.openclassrooms.go4lunch.controllers.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

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

import ch.enyoholali.openclassrooms.go4lunch.R;
import ch.enyoholali.openclassrooms.go4lunch.api.UserHelper;
import ch.enyoholali.openclassrooms.go4lunch.base.BaseFragment;
import ch.enyoholali.openclassrooms.go4lunch.controllers.activities.PlaceDetailsActivity;
import ch.enyoholali.openclassrooms.go4lunch.controllers.activities.WelcomeActivity;
import ch.enyoholali.openclassrooms.go4lunch.data.DataSingleton;
import ch.enyoholali.openclassrooms.go4lunch.databinding.FragmentMapViewBinding;
import ch.enyoholali.openclassrooms.go4lunch.models.firebase.User;
import ch.enyoholali.openclassrooms.go4lunch.models.googleapi.nearbysearch.Result;
import ch.enyoholali.openclassrooms.go4lunch.models.googleapi.placesdetails.PlaceDetails;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapViewFragment extends BaseFragment<FragmentMapViewBinding> implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, WelcomeActivity.DataInterface {

    private static final String TAG = MapViewFragment.class.getSimpleName();

    // Constant
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private GoogleMap mMap;
    private Marker mMarker;

    private final List<Result> mPlaceList=new ArrayList<>();
    private final List<User> mUserList=new ArrayList<>();
    private final List<String> mSelectedPlaceId=new ArrayList<>();
    private final List<PlaceDetails> mPlaceDetailsList=new ArrayList<>();
    private Map<String , PlaceDetails> mPlaceDetailsMap=new HashMap<>();


    MapView mMapView;
   // @State String jsonPlaceDetailsList;


    @Override
    public BaseFragment<FragmentMapViewBinding> newInstance() {

        MapViewFragment mapViewFragment = new MapViewFragment();
        mapViewFragment.name = "Map View";
        return mapViewFragment;
    }

    @Override
    public FragmentMapViewBinding getBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentMapViewBinding.inflate(inflater,container,false);
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

        getAllUsersFromFireBase();

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
        Log.d(TAG, " on marker clicked "+ marker.getPosition() + " "+marker.getId());
       // PlaceAutoComplete placeDetails = DataSingleton.getInstance().getPlaceDetailsHashMap().get(marker.getId());
        PlaceDetails placeDetails= mPlaceDetailsMap.get(marker.getPosition().toString());
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
  /*  private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLastKnownLocation != null) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);

                mMap.getUiSettings().setZoomControlsEnabled(true);
                mMap.getUiSettings().setCompassEnabled(true);

            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }*/

    private void setMyLocationOnMap() {
        if(getActivity()!=null)
        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

            Log.d(TAG,"Location permission do not granted");
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }


   private void addMarkerOnMap(List<PlaceDetails> placesDetailsList) {
        Log.d(TAG, "in addMarker on map method");
        Map<String,PlaceDetails>resultMap=new HashMap<>();

        getAllUsersFromFireBase();

        if(placesDetailsList.size()!=0)

            for (int i = 0; i < placesDetailsList.size(); i++) {
                Double lat = placesDetailsList.get(i).getResult().getGeometry().getLocation().getLat();
                Double lng = placesDetailsList.get(i).getResult().getGeometry().getLocation().getLng();
                String placename = placesDetailsList.get(i).getResult().getName();
                String vinicity = placesDetailsList.get(i).getResult().getVicinity();

                MarkerOptions markerOptions = new MarkerOptions();
                LatLng latLng = new LatLng(lat, lng);
                markerOptions.position(latLng);
                markerOptions.title(placename + " : " + vinicity);

                if( mSelectedPlaceId.contains(placesDetailsList.get(i).getResult().getPlaceId())) {
                    mMarker = mMap.addMarker(new MarkerOptions()
                            .position(latLng)
                     //       .title(placename + " :" + vinicity)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.restaurant_locator_for_map_green)));
                }
                else {
                    mMarker = mMap.addMarker(new MarkerOptions()
                            .position(latLng)
                      //      .title(placename + " :" + vinicity)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.restaurant_locator_for_map_orange)));
                }

                resultMap.put(mMarker.getPosition().toString(),placesDetailsList.get(i));

                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
            }
         mPlaceDetailsMap = resultMap;
        setMyLocationOnMap();
       // DataSingleton.getInstance().setPlaceDetailsMap(resultMap);

    }


    @Override
    public void doMySearch(String query) {
        Log.d(TAG,"In Map View fragment"+ query);
    }


    @Override
    public void update(List<PlaceDetails>placeDetailsList) {
        Log.d(TAG, " In mapView fragment: place details list " +placeDetailsList.size());
        updateUIWithPlaceDetailsList(mPlaceDetailsList);

        try {
            addMarkerOnMap(placeDetailsList);

        }catch (Exception e){
            Log.d(TAG, "onNext: error");
        }
    }

    private void updateUIWithPlaceDetailsList(List<PlaceDetails>list){

        this.mPlaceDetailsList.clear();
        this.mPlaceDetailsList.addAll(list);

    }
    private void updateUserList(List<User>list){
        mUserList.clear();
        mUserList.addAll(list);
    }
    private void updateSelectedIdList(List<String>list){
       mSelectedPlaceId.clear();
       mSelectedPlaceId.addAll(list);
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
                List<String>selectedRestaurantIdList= new ArrayList<>();
                for (int k=0;k<userList.size();k++) {

                    String id =userList.get(k).getRestaurantId();
                    if(id!=null)
                        selectedRestaurantIdList.add(id);

                }
                updateUserList(userList);
               // mUserList=userList;
                // Selected Restaurant id list.
               // mSelectedPlaceId=selectedRestaurantId;
                updateSelectedIdList(selectedRestaurantIdList);
                Log.d(TAG," User list size : "+mUserList.size());
                Log.d(TAG, "Selected id list size : "+mSelectedPlaceId.size());

                // Update UI
                // ...
            }
        });
    }


    private void destroyMap(){
        if(mMapView!=null)
            mMapView.onDestroy();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
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
