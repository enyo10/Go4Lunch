package ch.enyoholali.openclassrooms.go4lunch.controllers.activities;

import android.Manifest;
import android.app.SearchManager;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ch.enyoholali.openclassrooms.go4lunch.BuildConfig;
import ch.enyoholali.openclassrooms.go4lunch.R;
import ch.enyoholali.openclassrooms.go4lunch.api.UserHelper;
import ch.enyoholali.openclassrooms.go4lunch.auth.ProfileActivity;
import ch.enyoholali.openclassrooms.go4lunch.base.BaseActivity;
import ch.enyoholali.openclassrooms.go4lunch.controllers.fragments.ListViewFragment;
import ch.enyoholali.openclassrooms.go4lunch.controllers.fragments.MapViewFragment;
import ch.enyoholali.openclassrooms.go4lunch.controllers.fragments.WorkmatesFragment;
import ch.enyoholali.openclassrooms.go4lunch.data.DataSingleton;
import ch.enyoholali.openclassrooms.go4lunch.models.firebase.User;
import ch.enyoholali.openclassrooms.go4lunch.models.googleapi.placesdetails.PlaceDetails;
import ch.enyoholali.openclassrooms.go4lunch.utils.GoogleApiPlaceStreams;
import icepick.State;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class WelcomeActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = WelcomeActivity.class.getSimpleName();
    //  - Identify each Http Request
    private static final int SIGN_OUT_TASK = 10;
    private static final int DELETE_USER_TASK = 20;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    public static final String SHARED_PREF_NAME = "pref";
    public static final String LONGTITUDE = "long";
    public static final String LATITUDE = "lat";
    final FragmentManager mFragmentManager = getSupportFragmentManager();
    protected FusedLocationProviderClient mFusedLocationProviderClient;
    protected LocationRequest mLocationRequest;
    protected ActionBar mActionBar;

    int AUTOCOMPLETE_REQUEST_CODE = 1;

    @BindView(R.id.nav_view)NavigationView mNavigationView;

    TextView mUserEmailTextView;
    ImageView mImageView;
    TextView mUsernameTextView;
    @BindView(R.id.activity_welcome_bottom_navigation) BottomNavigationView mBottomNavigationView;
    @BindView(R.id.toolbar) Toolbar toolbar;

    MapViewFragment mMapViewFragment;
    ListViewFragment mListViewFragment;
    WorkmatesFragment mWorkmatesFragment;
    Fragment activeFragment;
    PlacesClient mPlacesClient;
    Location mCurrentLocation;

    private List<PlaceDetails> mPlaceDetailsList;
    private Disposable mDisposable;
    private User mUser;
    SharedPreferences mSharedPreferences;


    @State String jsonArrayList;
    @State String tag;

    @Override
    public void loadData() {

    }

    /*private Fragment getActiveFragment(String tag){
        Fragment activeFragment=null;
        List<Fragment>list =mFragmentManager.getFragments();
        for(int k=0;k<list.size();k++)
            if(list.get(k).getTag().equals(tag))
                activeFragment=list.get(k);
            return activeFragment;


    }*/

    @Override
    public int getActivityLayout() {
        return R.layout.activity_welcome;
    }

    @Override
    public void configureView() {
       // getConnectedUser();

        Log.d(TAG," load state "+tag);
        reloadUser();
         mSharedPreferences=getSharedPreferences(SHARED_PREF_NAME,MODE_PRIVATE);
        // Initialize the FusedLocationClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        // Location request.
        createLocationRequest();
        getCurrentLocationSettings();

        this.mPlaceDetailsList = new ArrayList<>();
        toolbar = findViewById(R.id.toolbar);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        mActionBar = getSupportActionBar();
        intAppClient();

        getDeviceLocation();


        configureBottomNavigationView();
        configureNavHeader();

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


    //----------------------------------------------------------------------------------------------
    //                                   CONFIGURE VIEWS.
    //----------------------------------------------------------------------------------------------

    private void configureNavHeader(){
        View view= mNavigationView.getHeaderView(0);
        mUserEmailTextView =view.findViewById(R.id.nav_header_email_textView);
        mUsernameTextView=  view.findViewById(R.id.nav_header_username_textView);
        mImageView =view.findViewById(R.id.nav_header_imageView);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.welcome, menu);

        return super.onCreateOptionsMenu(menu);
    }
    //----------------------------------------------------------------------------------------------
    //                                        ACTIONS
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

   // @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected( MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.setting) {
            // start profile Activity
            startActivity(ProfileActivity.class);
        } else if (id == R.id.logout) {
            this.signOutFromFirebase();

        } else if (id == R.id.your_lunch) {
            // Go to your lunch.
            goToLunch();
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * This method to lunch the place activity and will show the restaurant selected by the user.
     */
    private void goToLunch(){

        PlaceDetails placeDetails=null;

        if(mUser.getRestaurantId()!=null){
            Log.d(TAG, " goToLunch  " +mUser.getUsername());
            for(int i=0;i<mPlaceDetailsList.size();i++){
                Log.d(TAG,"placeDetails list size "+mPlaceDetailsList.size());
                if(mPlaceDetailsList.get(i).getResult().getPlaceId().equals(mUser.getRestaurantId())){
                    placeDetails=mPlaceDetailsList.get(i);
                    Log.d(TAG, " place details fund.");
                }

            }
        }
        if(placeDetails!=null){
            DataSingleton.getInstance().setPlaceDetails(placeDetails);
            startActivity(PlaceDetailsActivity.class);
        }else {
            Toast.makeText(this,"You do not select lunch yet",Toast.LENGTH_LONG).show();

        }

    }


    // Configure BottomNavigationView
    public void configureBottomNavigationView() {

        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {

                    case R.id.bottom_navigation_map:
                        loadFragment(mMapViewFragment, R.string.title_activity_welcome);
                        return true;

                    case R.id.bottom_navigation_restaurants:
                        loadFragment(mListViewFragment, R.string.title_activity_welcome);
                        return true;

                    case R.id.bottom_navigation_workmates:
                        loadFragment(mWorkmatesFragment, R.string.title_activity_welcome);
                        return true;

                }

                return false;
            }
        });
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
                .setTypeFilter(TypeFilter.ESTABLISHMENT)
                .build(this);

        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // super.onActivityResult(requestCode, resultCode, data);
        if (data != null)
            if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
                if (resultCode == RESULT_OK) {
                    Place place = Autocomplete.getPlaceFromIntent(data);
                    Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());

                   mDisposable = GoogleApiPlaceStreams.fetchPlaceDetailsStream(place.getId())
                            .subscribeWith(new DisposableObserver<PlaceDetails>() {
                                @Override
                                public void onNext(PlaceDetails placeDetails) {
                                    DataSingleton.getInstance().setPlaceDetails(placeDetails);
                                    startActivity(PlaceDetailsActivity.class);

                                }

                                @Override
                                public void onError(Throwable e) {
                                    Log.i("TAG", "aie, error in place details search: " + Log.getStackTraceString(e));
                                }

                                @Override
                                public void onComplete() {
                                    Log.i(TAG, " Place details downloaded ");
                                }
                            });

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
    //               FRAGMENT MANAGEMENT
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


    }

    /**
     * This method to load the fragment in to the frame.
     *
     * @param fragment,      the fragment to load.
     * @param toolbarTextId, the text to set to toolbar.
     */
    private void loadFragment(Fragment fragment, int toolbarTextId) {

        DataInterface dataInterface = (DataInterface) fragment;
          dataInterface.update(mPlaceDetailsList);
        Log.d(TAG, " place details list size in load fragment " + mPlaceDetailsList.size());

        toolbar.setTitle(toolbarTextId);
        mFragmentManager.beginTransaction().hide(activeFragment).show(fragment).commit();
        activeFragment = fragment;

    }


    //--------------------------------------------------------------------------------------------------
    //    AUTHENTICATION MANAGEMENT
    //----------------------------------------------------------------------------------------------

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

    /**
     * This to sign out from firebae.
     */
    private void signOutFromFirebase() {
        Log.d(TAG, " sign out");

        AuthUI.getInstance()
                .signOut(this)
                .addOnSuccessListener(this, this.updateUIAfterRESTRequestsCompleted(SIGN_OUT_TASK));
    }

    //------------------------------------------------------------------------------------------------
    //        PERMISSION AND LOCATION AND UPDATE.
    //------------------------------------------------------------------------------------------------

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

    /**
     * This method to get the device location.
     */
    private void getDeviceLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);

            Log.d(TAG, "Location permission do not grandted");
        } else {
            mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {

                    if (location != null) {

                       mCurrentLocation=location;
                       executeHttpRequestWithRetrofit();
                       activeFragment=mMapViewFragment;
                       DataSingleton.getInstance().setLocation(mCurrentLocation);
                        Log.i(TAG, "Location found " + location);

                    } else {
                        Log.d(TAG, " Location not found ");
                    }
                }
            });
        }
    }

    protected void createLocationRequest() {
        mLocationRequest  = LocationRequest.create();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    protected void  getCurrentLocationSettings(){
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
                getDeviceLocation();
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




    //----------------------------------------------------------------------------------------------
    //                           REQUESTS
    //----------------------------------------------------------------------------------------------

    private void executeHttpRequestWithRetrofit() {

       LatLng     latlng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());

        String latlng1 = latlng.latitude + "," + latlng.longitude;

        Log.i(TAG, " location " + latlng.toString());

        mDisposable = GoogleApiPlaceStreams.streamFPlaceDetailsList(latlng1)
                .subscribeWith(new DisposableObserver<List<PlaceDetails>>() {
                    @Override
                    public void onNext(List<PlaceDetails> placeDetailsList) {
                        Log.i(TAG, " Place details list downloading...");

                        updateUIWithResult(placeDetailsList);
                        Log.i(TAG, " Place details list update and size : " + mPlaceDetailsList.size());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i("TAG", "aie, error in place details search: " + Log.getStackTraceString(e));

                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, " Place details downloaded ");

                    }
                });
    }

   /* public  void executeHttpRequestPlaceAutoComplete(){
        Map<String,String>map=new HashMap<>();
        mDisposable=GoogleApiPlaceStreams.streamAutocompletePlaceDetaills(map)
                .subscribeWith(new DisposableObserver<PlaceAutoComplete>() {
                    @Override
                    public void onNext(PlaceAutoComplete placeAutoComplete) {
                        placeAutoComplete.getPredictions().size();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }


*/


    /**
     * This method to get the connected user.
     */
  /*  private void getConnectedUser(){
        if(getCurrentUser()!=null)
            UserHelper.getUser(getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    mUser = documentSnapshot.toObject(User.class);

                    if(mUser!=null){
                        Log.d(TAG, "user "+ mUser.getUsername() +"   retrieved");

                        updateNavigationHeader();

                    }


                }
            });

    }*/

    private void reloadUser(){
        UserHelper.getAllUsers().addSnapshotListener(new EventListener<QuerySnapshot>() {
        @Override
        public void onEvent(@com.google.firebase.database.annotations.Nullable QuerySnapshot snapshot, @com.google.firebase.database.annotations.Nullable FirebaseFirestoreException e) {
            if (e != null) {
                // Handle error
                Log.i(TAG, " Error by retrieve user from fire base-->: "+e.getMessage());
                e.printStackTrace();
                return;
            }
            // Convert query snapshot to a list of users.
            List<User> userList = snapshot.toObjects(User.class);

            for (int k=0;k<userList.size();k++) {

                if(userList.get(k).getUid().equals(getCurrentUser().getUid())){
                    mUser=userList.get(k);
                    updateNavigationHeader();
                }

            }

        }
        });

    }


    //----------------------------------------------------------------------------------------------
    //                                   HELP METHODS
    //----------------------------------------------------------------------------------------------
    private void updateUIWithResult(List<PlaceDetails> list) {
        DataSingleton.getInstance().setPlaceDetailsList(list);
        this.mPlaceDetailsList.clear();
        this.mPlaceDetailsList.addAll(list);
        DataInterface dataInterface = (DataInterface) activeFragment;
        dataInterface.update(list);

    }


    private void updateNavigationHeader(){

        Log.d(TAG, " actual user "+mUser.getEmail() );

        mUsernameTextView.setText(mUser.getUsername());
        mUserEmailTextView.setText(mUser.getEmail());
        Glide.with(this).load(mUser.getUrlPicture()).into(mImageView);

    }

    private void disposeWhenDestroy() {
        if (this.mDisposable != null && !this.mDisposable.isDisposed()) this.mDisposable.dispose();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }


    @Override
    protected void onResume() {
        super.onResume();
      // getDeviceLocation();

    }

    @Override
    protected void onDestroy() {

        /*
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        float longitude = (float) mCurrentLocation.getLongitude();
        float latitude= (float)mCurrentLocation.getLatitude();
        editor.putFloat(LONGTITUDE,longitude);
        editor.putFloat(LATITUDE,latitude);
        editor.apply();
*/

        super.onDestroy();
        this.disposeWhenDestroy();
    }


    /**
     * This interface help us to send data to the class that implement it.
     */
    public interface DataInterface {
        void doMySearch(String query);

        void update(List<PlaceDetails> placeDetailsList);
    }


}
