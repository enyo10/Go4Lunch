package ch.enyo.openclassrooms.go4lunch.base;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import butterknife.ButterKnife;
import ch.enyo.openclassrooms.go4lunch.R;
import ch.enyo.openclassrooms.go4lunch.data.DataSingleton;


public abstract class BaseFragment extends Fragment {
    private static final String TAG=BaseFragment.class.getSimpleName();


    // Constant
    private static final int REQUEST_LOCATION_PERMISSION = 11;

   public  String name;
   protected FusedLocationProviderClient mFusedLocationProviderClient;
   protected Location mLastKnownLocation;
   boolean mLocationPermissionGranted;


    public BaseFragment() {
        // Required empty public constructor
    }
    

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getContext()!=null)
        mFusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(getContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =    inflater.inflate(getFragmentLayout(), container, false);
        ButterKnife.bind(this,view);

        Log.i(TAG, " On create View method.");
        configureView();
        configureOnclickRecyclerView();

        return view;
    }


    public void getDeviceLocation1(){
        try {
            if (mLocationPermissionGranted && getActivity() != null) {

                mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last know location. In some rare situations this can be null.
                        if(location!=null){
                            // Logic to handle location object.
                            mLastKnownLocation=location;
                        } else {
                            Toast.makeText(getContext(),
                                    R.string.toast_message_geolocation,
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
               /* Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(getActivity(), task -> {
                    if (task.isSuccessful()) {
                        // Set the map's camera position to the current location of the device.
                        mLastKnownLocation = task.getResult();
                        if (mLastKnownLocation != null) {

                          Log.i(TAG, "");

                        } else {
                            Toast.makeText(getContext(),
                                    R.string.toast_message_geolocation,
                                    Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.");

                    }
                });*/
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());

        }

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


    //Generic activity launcher method
    public void startActivity(Class activity) {
        Intent intent = new Intent(getContext(), activity);
        startActivity(intent);
    }


    //----------------------------//
    // ABSTRACT METHODS
    //----------------------------//

    public abstract  BaseFragment newInstance();

    /**
     * This method to get the fragment layout resource id.
     * @return id,
     *         the resource id.
     */
    protected abstract int getFragmentLayout();

    /**
     * This method to configure the fragment view.
     */
    protected abstract void configureDesign(View v);

    protected abstract void configureView();

    protected abstract void configureOnclickRecyclerView();

}
