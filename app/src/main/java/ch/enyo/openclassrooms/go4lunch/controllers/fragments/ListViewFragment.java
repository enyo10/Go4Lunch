package ch.enyo.openclassrooms.go4lunch.controllers.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
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
public class ListViewFragment extends BaseFragment {
    private static final String TAG= ListViewFragment.class.getSimpleName();

    Disposable mDisposable;



    @Override
    public BaseFragment newInstance() {
        ListViewFragment listViewFragment=new ListViewFragment();
        listViewFragment.name="List View";

        return listViewFragment;

    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_list_view;
    }

    @Override
    protected void configureDesign(View v) {

    }

    @Override
    protected void configureView() {
        executeHttpRequestWithRetrofit();
    }


    protected void executeHttpRequestWithRetrofit(){

        Map<String, String> map=new HashMap<>();
        map.put("key","AIzaSyAj8TgbhVVLCxEldGuNHxxo2w4P-S2mxG8");
        map.put("keyword","cruise");
        map.put("type","restaurant");
        map.put("radius","5000");
        map.put("location","-33.8670522,151.1957362");

        mDisposable= GoogleApiPlaceStreams.getNearBySearchResultStream(map)
                .subscribeWith(new DisposableObserver<NearBySearchResult>() {
                    @Override
                    public void onNext(NearBySearchResult nearBySearchResult) {
                        Log.i(TAG," Top stories Download...");

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG,"OOOps, aie aie "+Log.getStackTraceString(e));

                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG,"Downloaded");

                    }
                });

    }
}
