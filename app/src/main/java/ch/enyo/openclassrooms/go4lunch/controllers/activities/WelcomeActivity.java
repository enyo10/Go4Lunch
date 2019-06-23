package ch.enyo.openclassrooms.go4lunch.controllers.activities;

import android.Manifest;
import android.app.SearchManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;

import androidx.annotation.NonNull;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.ActionBar;

import android.os.Bundle;
import android.util.Log;

import com.google.android.material.navigation.NavigationView;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ch.enyo.openclassrooms.go4lunch.BuildConfig;
import ch.enyo.openclassrooms.go4lunch.R;
import ch.enyo.openclassrooms.go4lunch.auth.ProfileActivity;
import ch.enyo.openclassrooms.go4lunch.base.BaseActivity;
import ch.enyo.openclassrooms.go4lunch.controllers.fragments.ListViewFragment;
import ch.enyo.openclassrooms.go4lunch.controllers.fragments.MapViewFragment;
import ch.enyo.openclassrooms.go4lunch.controllers.fragments.WorkmatesFragment;
import ch.enyo.openclassrooms.go4lunch.data.DataSingleton;
import ch.enyo.openclassrooms.go4lunch.models.googleapi.placesdetails.PlaceDetails;
import ch.enyo.openclassrooms.go4lunch.utils.GoogleApiPlaceStreams;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class WelcomeActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = WelcomeActivity.class.getSimpleName();

    public interface DataInterface {
        void doMySearch(String query);

        void update(List<PlaceDetails> placeDetailsList);
    }

    //  - Identify each Http Request
    private static final int SIGN_OUT_TASK = 10;
    private static final int DELETE_USER_TASK = 20;

    int AUTOCOMPLETE_REQUEST_CODE = 1;

    // private final static int ALL_PERMISSIONS_RESULT = 101;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final String REQUESTING_LOCATION_UPDATES_KEY = "tracking_location";
    protected FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private Boolean requestingLocationUpdates;
    private Location mLastKnownLocation;
    private boolean mTrackingLocation;
    private List<PlaceDetails> mPlaceDetailsList;


    @BindView(R.id.activity_welcome_bottom_navigation)
    BottomNavigationView mBottomNavigationView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    protected ActionBar mActionBar;


    MapViewFragment mMapViewFragment;
    ListViewFragment mListViewFragment;
    WorkmatesFragment mWorkmatesFragment;

    final FragmentManager mFragmentManager = getSupportFragmentManager();
    Fragment activeFragment;
    PlacesClient mPlacesClient;

    private Disposable mDisposable;

    @Override
    public int getActivityLayout() {
        return R.layout.activity_welcome;
    }

    @Override
    public void configureView() {
        // Initialize the FusedLocationClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        this.mPlaceDetailsList =new ArrayList<>();
        toolbar = findViewById(R.id.toolbar);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        mActionBar = getSupportActionBar();
        intAppClient();

        //   createLocationRequest();
       // initLocationCallback();
        getDeviceLocation();

        configureBottomNavigationView();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // load the store fragment by default
        toolbar.setTitle(R.string.title_activity_maps);
        initFragments();
        handleIntent(getIntent());


    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            DataInterface searchInterface = (DataInterface) activeFragment;
            searchInterface.doMySearch(query);
            // placePrediction(query);
            //Log.d(TAG, "search query "+query);
        }
    }

    private void intAppClient() {
        Places.initialize(getApplicationContext(), BuildConfig.ApiKey);
        // Create a new Places client instance.
        mPlacesClient = Places.createClient(this);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
       /* outState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY,
                requestingLocationUpdates);*/
        // ...
        super.onSaveInstanceState(outState);
    }


    //----------------------------------------------------------------------------------------------
    //                                   CONFIGURE VIEWS.
    //----------------------------------------------------------------------------------------------


    // Configure BottomNavigationView
    public void configureBottomNavigationView() {

        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {

                    case R.id.bottom_navigation_map:
                        loadFragment(mMapViewFragment, R.string.title_activity_welcome);
                        //configureContentFrameFragment(mMapViewFragment, R.string.title_activity_welcome);

                        return true;

                    case R.id.bottom_navigation_restaurants:
                        mListViewFragment.update(mPlaceDetailsList);
                        loadFragment(mListViewFragment, R.string.title_activity_welcome);
                        // configureContentFrameFragment(new ListViewFragment(), R.string.title_activity_welcome);

                        return true;

                    case R.id.bottom_navigation_workmates:
                        // configureContentFrameFragment(new WorkmatesFragment(), R.string.title_workmates);
                        loadFragment(mWorkmatesFragment, R.string.title_activity_welcome);
                        return true;

                }

                return false;
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.welcome, menu);


        return super.onCreateOptionsMenu(menu);
    }
    //----------------------------------------------------------------------------------------------
    //---------------    ACTIONS
    //----------------------------------------------------------------------------------------------


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //no inspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(ProfileActivity.class);

        } else if (id == R.id.action_logout) {
            this.signOutFromFirebase();
            return true;

        } else if (id == R.id.action_search) {
            if (activeFragment instanceof WorkmatesFragment)
                onSearchRequested();
            else onPlaceAutoCompleteRequested();

            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.setting) {
            // start profile Activity
            startActivity(ProfileActivity.class);
        } else if (id == R.id.logout) {
            this.signOutFromFirebase();

        } else if (id == R.id.your_lunch) {
            // Go to your lunch.
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    //-------------------------------------------------------------------------------------------------------------
    //                    AUTOCOMPLETE MANAGEMENT
    //-------------------------------------------------------------------------------------------------------------

    private void onPlaceAutoCompleteRequested() {
        // Set the fields to specify which types of place data to
        // return after the user has made a selection.
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);

        // Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY, fields)
                .build(this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // super.onActivityResult(requestCode, resultCode, data);
        if(data!=null)
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                //  Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
                Log.d(TAG, " The user canceled the Operation");
            }
        }
    }


//--------------------------------------------------------------------------------------------------
    //               HELPER.
    //----------------------------------------------------------------------------------------------

    /**
     * This method to initialise the fragment and set one as active.
     */
    private void initFragments() {
        Log.d(TAG, " In init Fragment");
        mMapViewFragment = new MapViewFragment();
        mListViewFragment = new ListViewFragment();
        mWorkmatesFragment = new WorkmatesFragment();

        mFragmentManager.beginTransaction().add(R.id.activity_welcome_frame, mWorkmatesFragment, "3").hide(mWorkmatesFragment).commit();
        mFragmentManager.beginTransaction().add(R.id.activity_welcome_frame, mListViewFragment, "2").hide(mListViewFragment).commit();
        mFragmentManager.beginTransaction().add(R.id.activity_welcome_frame, mMapViewFragment, "1").commit();
        // activeFragment = mMapViewFragment;

    }


    /**
     * This method to load the fragment in to the frame.
     *
     * @param fragment,      the fragment to load.
     * @param toolbarTextId, the text to set to toolbar.
     */
    private void loadFragment(Fragment fragment, int toolbarTextId) {

        DataInterface dataInterface = (DataInterface) fragment;
     //   dataInterface.update(mPlaceDetailsList);
        Log.d(TAG," place details list size in load fragment "+mPlaceDetailsList.size());

        toolbar.setTitle(toolbarTextId);
        mFragmentManager.beginTransaction().hide(activeFragment).show(fragment).commit();
        activeFragment = fragment;

    }


    //--------------------------------------------------------------------------------------------------
    //    AUTHENTICATION MANAGEMENT
    //----------------------------------------------------------------------------------------------

    /**
     * This to sign out from firebae.
     */
    private void signOutFromFirebase() {
        Log.d(TAG, " sign out");

        AuthUI.getInstance()
                .signOut(this)
                .addOnSuccessListener(this, this.updateUIAfterRESTRequestsCompleted(SIGN_OUT_TASK));
    }


    private OnSuccessListener<Void> updateUIAfterRESTRequestsCompleted(final int origin) {

        return aVoid -> {
            switch (origin) {
                case SIGN_OUT_TASK:
                    finish();
                    break;
                case DELETE_USER_TASK:
                    finish();
                    break;
                default:
                    break;
            }
        };
    }


    //------------------------------------------------------------------------------------------------
    //        PERMISSION AND LOCATION AND UPDATE.
    //------------------------------------------------------------------------------------------------

    private void getDeviceLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);

            Log.d(TAG,"Location permission do not grandted");
        } else {
            mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {

                    if (location != null) {
                        if(mLastKnownLocation!=null){
                            mLastKnownLocation = location;
                            activeFragment = mMapViewFragment;
                            executeHttpRequestWithRetrofit();
                        }
                        else {
                            mLastKnownLocation=location;
                        }
                        DataSingleton.getInstance().setLocation(mLastKnownLocation);

                        Log.i(TAG,"Location found "+location);

                    } else {
                        Log.d(TAG, " Location not found ");
                    }
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                // If the permission is granted, get the location,
                // otherwise, show a Toast
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getDeviceLocation();
                    // startTrackingLocation();
                } else {
                    Toast.makeText(this,
                            R.string.location_permission_denied,
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    protected LocationRequest getLocationRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest = locationRequest;
        return mLocationRequest;
    }


    private void initLocationCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data
                    // ...
                    Log.i(TAG, " With location callback in On created  " + location);

                }
            }
        };

    }


    private void startTrackingLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            mTrackingLocation = true;
            mFusedLocationProviderClient.requestLocationUpdates
                    (getLocationRequest(),
                            mLocationCallback,
                            null /* Looper */);

            // Set a loading text while you wait for the address to be
            // returned


        }
    }


    //----------------------------------------------------------------------------------------------
    //                           REQUESTS
    //----------------------------------------------------------------------------------------------

    private void executeHttpRequestWithRetrofit(){

        LatLng latlng=new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude());

        String latlng1=latlng.latitude+","+latlng.longitude;

        Log.i(TAG," location "+latlng.toString());

        mDisposable = GoogleApiPlaceStreams.streamFPlaceDetailsList(latlng1)
                .subscribeWith(new DisposableObserver<List<PlaceDetails>>() {
                    @Override
                    public void onNext(List<PlaceDetails> placeDetailsList) {
                        Log.i(TAG," Place details list downloading...");
                        Log.i(TAG," Details list size "+placeDetailsList.size());

                        updateUIWithResult(placeDetailsList);
                        Log.i(TAG, " Place details list update and size : "+mPlaceDetailsList.size());
                    }
                    @Override
                    public void onError(Throwable e) {
                        Log.i("TAG","aie, error in place details search: "  +Log.getStackTraceString(e));

                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG," Place details downloaded ");

                    }
                });
    }

    //----------------------------------------------------------------------------------------------
    //                                   HELP METHODS
    //----------------------------------------------------------------------------------------------

    private void updateUIWithResult(List<PlaceDetails>list){
        this.mPlaceDetailsList.clear();
        this.mPlaceDetailsList.addAll(list);
        DataInterface dataInterface= (DataInterface)activeFragment;
        dataInterface.update(list);

    }

    /**
     * Stops tracking the device. Removes the location
     * updates, stops the animation, and resets the UI.
     */
    private void stopTrackingLocation() {
        if (mTrackingLocation) {
            mTrackingLocation = false;

        }
    }


    @Override
    protected void onPause() {
        if (mTrackingLocation) {
            stopTrackingLocation();
            mTrackingLocation = true;
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
      /*  if (mTrackingLocation) {
            startTrackingLocation();
        }*/
      getDeviceLocation();
        super.onResume();
    }


}
