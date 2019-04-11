package ch.enyo.openclassrooms.go4lunch.controllers.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

import ch.enyo.openclassrooms.go4lunch.R;
import ch.enyo.openclassrooms.go4lunch.base.BaseFragment;
import ch.enyo.openclassrooms.go4lunch.models.google.NearBySearchResult;
import ch.enyo.openclassrooms.go4lunch.utils.GoogleApiPlaceStreams;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

/**
 * A simple {@link Fragment} subclass.
 */
public class WorkmatesFragment extends BaseFragment {



    @Override
    public BaseFragment newInstance() {
        WorkmatesFragment workmatesFragment=new WorkmatesFragment();
        workmatesFragment.name="Workmates";
        return workmatesFragment;
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_workmates;
    }

    @Override
    protected void configureDesign(View v) {

    }

    @Override
    protected void configureView() {

    }


}
