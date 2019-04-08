package ch.enyo.openclassrooms.go4lunch.controllers.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ch.enyo.openclassrooms.go4lunch.R;
import ch.enyo.openclassrooms.go4lunch.base.BaseFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapViewFragment extends BaseFragment {


    @Override
    public BaseFragment newInstance() {

        MapViewFragment mapViewFragment=new MapViewFragment();
        mapViewFragment.name="Map View";
        return mapViewFragment;
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_map_view;
    }

    @Override
    protected void configureDesign(View v) {

    }
}
