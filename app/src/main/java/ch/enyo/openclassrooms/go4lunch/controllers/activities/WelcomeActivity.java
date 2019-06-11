package ch.enyo.openclassrooms.go4lunch.controllers.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;

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

import android.widget.Toast;


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

public class WelcomeActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = WelcomeActivity.class.getSimpleName();

    //  - Identify each Http Request
    private static final int SIGN_OUT_TASK = 10;
    private static final int DELETE_USER_TASK = 20;

    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    //  Disposable mDisposable;

    private final static int ALL_PERMISSIONS_RESULT = 101;
    private final static int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 102;

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


    /**
     * This method to load the fragment in to the frame.
     * @param fragment,
     *                the fragment to load.
     * @param toolbarTextId,
     *                    the text to set to toolbar.
     */
    private void loadFragment(Fragment fragment, int toolbarTextId){

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


    //*******************************************************************
    //        Here we handle the localisation process.
    //*******************************************************************

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
                Log.i(TAG," location  " +location);

            }
        });
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
    protected void onDestroy() {
        super.onDestroy();
        locationTrack.stopListener();
    }


}
