package ch.enyo.openclassrooms.go4lunch.controllers.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;

import androidx.annotation.NonNull;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;

import android.os.Bundle;
import android.util.Log;

import com.google.android.material.navigation.NavigationView;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;
import ch.enyo.openclassrooms.go4lunch.R;
import ch.enyo.openclassrooms.go4lunch.auth.ProfileActivity;
import ch.enyo.openclassrooms.go4lunch.base.BaseActivity;
import ch.enyo.openclassrooms.go4lunch.controllers.fragments.ListViewFragment;
import ch.enyo.openclassrooms.go4lunch.controllers.fragments.MapViewFragment;
import ch.enyo.openclassrooms.go4lunch.controllers.fragments.WorkmatesFragment;
import ch.enyo.openclassrooms.go4lunch.utils.LocationTrack;
import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static java.security.AccessController.getContext;

public class WelcomeActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = WelcomeActivity.class.getSimpleName();

    //  - Identify each Http Request
    private static final int SIGN_OUT_TASK = 10;
    private static final int DELETE_USER_TASK = 20;


    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();

    private final static int ALL_PERMISSIONS_RESULT = 101;
    private static final String REQUESTING_LOCATION_UPDATES_KEY = "tracking_location";
 //   private final static int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 102;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private Boolean requestingLocationUpdates=false;
    private Location mLastKnownLocation;


    LocationTrack locationTrack;

    @BindView(R.id.activity_welcome_bottom_navigation)
    BottomNavigationView mBottomNavigationView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private ActionBar mActionBar;


    MapViewFragment mMapViewFragment = new MapViewFragment();
    // ListViewFragment mListViewFragment=new ListViewFragment();
    // WorkmatesFragment mWorkmatesFragment=new WorkmatesFragment();

    final FragmentManager mFragmentManager = getSupportFragmentManager();
    Fragment activeFragment;

    //

    @Override
    public int getActivityLayout() {
        return R.layout.activity_welcome;
    }

    @Override
    public void configureView() {
        toolbar = findViewById(R.id.toolbar);
        ButterKnife.bind(this);


        setSupportActionBar(toolbar);
        mActionBar = getSupportActionBar();
        configurePermission();
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
        configureContentFrameFragment(mMapViewFragment, R.string.title_activity_welcome);
        // initFragments();
        getDeviceLocation();
        createLocationRequest();
        configureLocationSettings();


        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data
                    // ...
                    Log.i(TAG, " With location callback in On created  "+location );

                }
            }
        };

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY,
                requestingLocationUpdates);
        // ...
        super.onSaveInstanceState(outState);
    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            return;
        }

        // Update the value of requestingLocationUpdates from the Bundle.
        if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
            requestingLocationUpdates = savedInstanceState.getBoolean(
                    REQUESTING_LOCATION_UPDATES_KEY);
        }

        // ...

        // Update UI to match restored state
      //  updateUI();
    }
    private void updateUI(){

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
                       // loadFragment(mMapViewFragment,R.string.title_activity_welcome);
                        configureContentFrameFragment(mMapViewFragment,R.string.title_activity_welcome);

                        return true;

                    case R.id.bottom_navigation_restaurants:
                       // mListViewFragment.setLocation(DataSingleton.getInstance().getLocation());
                       // loadFragment(new ListViewFragment(),R.string.title_activity_welcome);
                        configureContentFrameFragment(new ListViewFragment(),R.string.title_activity_welcome);

                        return true;

                    case R.id.bottom_navigation_workmates:
                        configureContentFrameFragment(new WorkmatesFragment(),R.string.title_workmates);
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
        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default

        return true;
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

//--------------------------------------------------------------------------------------------------
    //               HELPER.
    //----------------------------------------------------------------------------------------------

    private void initFragments(){
     //  mFragmentManager.beginTransaction().add(R.id.activity_welcome_frame, mWorkmatesFragment, "3").hide(mWorkmatesFragment).commit();
       // mFragmentManager.beginTransaction().add(R.id.activity_welcome_frame, mListViewFragment, "2").hide(mListViewFragment).commit();
        mFragmentManager.beginTransaction().add(R.id.activity_welcome_frame,mMapViewFragment, "1").commit();
        activeFragment = mMapViewFragment;

    }

    // Launch fragments
    private void configureContentFrameFragment(Fragment fragment,int title) {
        toolbar.setTitle(title);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.activity_welcome_frame, fragment).commit();
    }


   /* *//**
     * This method to load the fragment in to the frame.
     * @param fragment,
     *                the fragment to load.
     * @param toolbarTextId,
     *                    the text to set to toolbar.
     *//*
    private void loadFragment(Fragment fragment, int toolbarTextId){

        toolbar.setTitle(toolbarTextId);
        mFragmentManager.beginTransaction().hide(activeFragment).show(fragment).commit();
        activeFragment = fragment;

    }*/




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

    private void configurePermission(){
        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);
        permissionsToRequest = findUnAskedPermissions(permissions);
        //get the permissions we have asked for before but are not granted..
        //we will store this in a global list to access later.

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (permissionsToRequest.size() > 0)
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);

        }

    }

    private void getDeviceLocation(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                mLastKnownLocation=location;
                Log.i(TAG," location  " +mLastKnownLocation);

            }
        });
    }

    protected void createLocationRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest= locationRequest;
    }


    protected void configureLocationSettings(){
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...

                Log.i(TAG, "Location settings are successful ");
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(WelcomeActivity.this,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (getContext() != null)
                ActivityCompat.requestPermissions(this, new String[]
                                {Manifest.permission.ACCESS_FINE_LOCATION},
                        ALL_PERMISSIONS_RESULT);
        } else {
            requestingLocationUpdates=true;
            Log.d(TAG, "getLocation: permissions granted");
            mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback,
                    null /* Looper */);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();

    }

    private void stopLocationUpdates() {
        mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
        requestingLocationUpdates=false;
    }

  /*  private void configurePermission() {

        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);
        permissionsToRequest = findUnAskedPermissions(permissions);
        //get the permissions we have asked for before but are not granted..
        //we will store this in a global list to access later.

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (permissionsToRequest.size() > 0)
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }

        // Initialise location tracker.
        locationTrack = new LocationTrack(WelcomeActivity.this);

        if (locationTrack.canGetLocation()) {
            DataSingleton dataSingleton = DataSingleton.getInstance();

            double longitude = locationTrack.getLongitude();
            double latitude = locationTrack.getLatitude();
            // dataSingleton.setLongitude(longitude);
           //  dataSingleton.setLatitude(latitude);
            Log.i(TAG, "Longitude by tracker : " + longitude + "\nLatitude:" + latitude);

            Toast.makeText(getApplicationContext(), "Longitude:" + longitude + "\nLatitude:" + latitude, Toast.LENGTH_SHORT).show();
        } else {

            locationTrack.showSettingsAlert();
        }

    }
*/

    private ArrayList<String> findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList<>();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }


    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case ALL_PERMISSIONS_RESULT:
                for (String perms : permissionsToRequest) {
                    if (!hasPermission(perms)) {
                        permissionsRejected.add(perms);
                    }
                }
                if (permissionsRejected.size() > 0) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                         //   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                           // }
                                        }
                                    });
                            return;
                        }
                    }
                }
                break;
        }
    }


    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(WelcomeActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (requestingLocationUpdates) {
            startLocationUpdates();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationTrack.stopListener();
    }


}
