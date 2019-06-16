package ch.enyo.openclassrooms.go4lunch.base;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;

import butterknife.ButterKnife;


public abstract class BaseFragment extends Fragment {
    private static final String TAG = BaseFragment.class.getSimpleName();


    // Constant
    public String name;


    public BaseFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         // Initialize the FusedLocationClient.
      /*  if (getActivity() != null)
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
*/

        // Initialize the FusedLocationClient.


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




    //Generic activity launcher method
    public void startActivity(Class activity) {
        Intent intent = new Intent(getContext(), activity);
        startActivity(intent);
    }

    public FirebaseUser getCurrentUser(){
        BaseActivity activity =(BaseActivity)getActivity();
        if(activity!=null)
        return activity. getCurrentUser();
        return null;
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
