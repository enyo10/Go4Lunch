package ch.enyo.openclassrooms.go4lunch.base;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;


public abstract class BaseFragment extends Fragment {

   public  String name;


    public BaseFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =    inflater.inflate(getFragmentLayout(), container, false);

        ButterKnife.bind(this,view);

        configureView();
        return view;
    }


    //----------------------------//
    // ABSTRACT METHODS
    //----------------------------//

    public abstract BaseFragment newInstance();

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

}
