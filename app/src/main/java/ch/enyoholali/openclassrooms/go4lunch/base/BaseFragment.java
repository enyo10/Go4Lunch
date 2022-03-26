package ch.enyoholali.openclassrooms.go4lunch.base;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseUser;

import butterknife.ButterKnife;
//import icepick.Icepick;


public abstract class BaseFragment extends Fragment {
    private static final String TAG = BaseFragment.class.getSimpleName();


    // Constant
    public String name;


    public BaseFragment() {
        // Required empty public constructor
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
        //Icepick.restoreInstanceState(this, savedInstanceState);

        return view;
    }

    //_____________________________________________

   /* @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState BF");
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated BF");
        if (savedInstanceState != null && !savedInstanceState.isEmpty()) {
            Icepick.restoreInstanceState(this, savedInstanceState);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate BF");
        setRetainInstance(true);
    }
*/

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
