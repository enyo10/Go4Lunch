package ch.enyo.openclassrooms.go4lunch.controllers.activities;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;

import android.widget.Toast;


import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import ch.enyo.openclassrooms.go4lunch.R;
import ch.enyo.openclassrooms.go4lunch.base.BaseActivity;
import ch.enyo.openclassrooms.go4lunch.controllers.fragments.ListViewFragment;
import ch.enyo.openclassrooms.go4lunch.controllers.fragments.MapViewFragment;
import ch.enyo.openclassrooms.go4lunch.controllers.fragments.WorkmatesFragment;
import ch.enyo.openclassrooms.go4lunch.data.DataSingleton;
import ch.enyo.openclassrooms.go4lunch.models.googleapi.nearbysearch.PlaceNearBySearch;
import ch.enyo.openclassrooms.go4lunch.utils.GoogleApiPlaceStreams;
import ch.enyo.openclassrooms.go4lunch.utils.LocationTrack;
import ch.enyo.openclassrooms.go4lunch.views.MyPagerAdapter;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class WelcomeActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = WelcomeActivity.class.getSimpleName();

    // 2 - Identify each Http Request
    private static final int SIGN_OUT_TASK = 10;
    private static final int DELETE_USER_TASK = 20;

  //  private SharedPreferences mSharedPreferences;


    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    Disposable mDisposable;

    private final static int ALL_PERMISSIONS_RESULT = 101;
    private final static int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION=102;

    LocationTrack locationTrack;

    private int[] mTabIcons = {
            R.drawable.ic_action_map,
            R.drawable.ic_action_list,
            R.drawable.ic_action_groupe};

    @BindView(R.id.activity_main_view_pager)
    ViewPager mViewPager;
    @BindView(R.id.activity_main_tabs)
    TabLayout mTabLayout;

    private FragmentPagerAdapter mFragmentPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toolbar toolbar = findViewById(R.id.toolbar);
        ButterKnife.bind(this);

       //mSharedPreferences=getSharedPreferences(DataSingleton.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE);

        setSupportActionBar(toolbar);

       // fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        configurePermission();


        configureViewPagerAndTabs();

        executeRequestWithRetrofit();

       /* FloatingActionButton fab =  findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.welcome, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_logout) {
            this.signOutFromFirebase();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    protected void configureViewPagerAndTabs() {
        mFragmentPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        // ((MyPagerAdapter) mFragmentPagerAdapter).addFragment((new MapViewFragment()).newInstance());
        ((MyPagerAdapter) mFragmentPagerAdapter).addFragment((new MapViewFragment()).newInstance());
        ((MyPagerAdapter) mFragmentPagerAdapter).addFragment(new ListViewFragment().newInstance());
        ((MyPagerAdapter) mFragmentPagerAdapter).addFragment((new WorkmatesFragment()).newInstance());

        mViewPager.setAdapter(mFragmentPagerAdapter);

        //Glue TabLayout and ViewPager together
        mTabLayout.setupWithViewPager(mViewPager);

        setUpIcons();
        //Design purpose. Tabs have the same width
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);

    }


    private void setUpIcons() {
        /*View view1 = getLayoutInflater().inflate(R.layout.customtab, null);
        view1.findViewById(R.id.icon).setBackgroundResource(R.drawable.baseline_map_black_48);

        mTabLayout.getTabAt(0).setCustomView(view1);

        mTabLayout.getTabAt(1).setIcon(mTabIcons[1]);
        mTabLayout.getTabAt(2).setIcon(mTabIcons[2]);*/


        for (int i = 0; i < mTabIcons.length; i++) {
            mTabLayout.getTabAt(i).setIcon(mTabIcons[i]);

        }

    }

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

    @Override
    public int getActivityLayout() {
        return R.layout.activity_welcome;
    }


    //*******************************************************************
    //        Here we handle the localisation process.
    //*******************************************************************

    private void configurePermission() {

        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);
        permissionsToRequest = findUnAskedPermissions(permissions);
        //get the permissions we have asked for before but are not granted..
        //we will store this in a global list to access later.

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (permissionsToRequest.size() > 0)
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);

        }

        locationTrack = new LocationTrack(WelcomeActivity.this);


        if (locationTrack.canGetLocation()) {
            DataSingleton dataSingleton =DataSingleton.getInstance();

            double longitude = locationTrack.getLongitude();
            double latitude = locationTrack.getLatitude();
             dataSingleton.setLongitude(longitude);
             dataSingleton.setLatitude(latitude);
            Log.i(TAG, ""+"Longitude:" + longitude + "\nLatitude:" + latitude);

            Toast.makeText(getApplicationContext(), "Longitude:" + longitude + "\nLatitude:" + latitude, Toast.LENGTH_SHORT).show();
        } else {

            locationTrack.showSettingsAlert();
        }

    }

  /* private void requestPermission(){
       // Here, thisActivity is the current activity
       if (ContextCompat.checkSelfPermission(this,
               ACCESS_COARSE_LOCATION)
               != PackageManager.PERMISSION_GRANTED) {

           // Permission is not granted
           // Should we show an explanation?
           if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                   ACCESS_COARSE_LOCATION)) {
               // Show an explanation to the user *asynchronously* -- don't block
               // this thread waiting for the user's response! After the user
               // sees the explanation, try again to request the permission.
           } else {
               // No explanation needed; request the permission
               ActivityCompat.requestPermissions(this,
                       new String[]{ACCESS_COARSE_LOCATION},
                       MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);

               // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
               // app-defined int constant. The callback method gets the
               // result of the request.
           }
       } else {
           // Permission has already been granted
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

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
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }

                }

                break;
        }

    }


    public void executeRequestWithRetrofit(){
        Map<String,String> parametersMap= DataSingleton.getInstance().getParametersMap();
        Log.i(TAG," parameters "+parametersMap.toString());

        mDisposable = GoogleApiPlaceStreams.fetchPlaceNearBySearchStream(parametersMap)
                .subscribeWith(new DisposableObserver<PlaceNearBySearch>() {
                    @Override
                    public void onNext(PlaceNearBySearch placeNearBySearch) {
                        DataSingleton.getInstance().setNearbySearchResultList(placeNearBySearch.getResults());



                        Log.i(TAG, " restaurant by near size "+placeNearBySearch.getResults().size());

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


       /* Map<String,String> parametersMap= DataSingleton.getInstance().getParametersMap();

        Log.i(TAG, "parameter map value "+parametersMap.toString());

        mDisposable= GoogleApiPlaceStreams.fetchPlaceNearBySearchStream(parametersMap)
                .subscribeWith(new DisposableObserver<PlaceNearBySearch>() {
                    @Override
                    public void onNext(PlaceNearBySearch placeNearBySearch) {
                        Log.i(TAG, " restaurant by near size "+placeNearBySearch.getResults().size());

                        // updateResultList(placeNearBySearch.getResults());
                        //  mResultList=new ArrayList<>();
                         DataSingleton.getInstance().setNearbySearchResultList(placeNearBySearch.getResults());
                    }

                    @Override
                    public void onError(Throwable e) {

                        Log.i("TAG","aie, error in place nearby search: "  +Log.getStackTraceString(e));

                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "Restaurant near by search completed.");

                    }
                });*/

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
