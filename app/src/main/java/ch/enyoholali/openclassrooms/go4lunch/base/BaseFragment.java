package ch.enyoholali.openclassrooms.go4lunch.base;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewbinding.ViewBinding;

import com.google.firebase.auth.FirebaseUser;



public abstract class BaseFragment<V extends ViewBinding> extends Fragment {
    private static final String TAG = BaseFragment.class.getSimpleName();

    // Constant
    public String name;
    protected V binding;


    public BaseFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = getBinding(inflater,container);
        View view = binding.getRoot();
        Log.i(TAG, binding.toString());

        Log.i(TAG, " On create View method.");
        configureView();
        configureOnclickRecyclerView();

        return view;
    }

    //_____________________________________________


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

    public abstract  BaseFragment<V> newInstance();
    public abstract V getBinding(LayoutInflater inflater,ViewGroup container);

    /**
     * This method to get the fragment layout resource id.
     * @return id,
     *         the resource id.
     */
    /**
     * This method to configure the fragment view.
     */
    protected abstract void configureDesign(View v);

    protected abstract void configureView();

    protected abstract void configureOnclickRecyclerView();

}
