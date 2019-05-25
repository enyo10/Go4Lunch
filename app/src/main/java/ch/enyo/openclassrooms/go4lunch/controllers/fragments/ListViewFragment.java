package ch.enyo.openclassrooms.go4lunch.controllers.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import ch.enyo.openclassrooms.go4lunch.R;
import ch.enyo.openclassrooms.go4lunch.base.BaseFragment;
import ch.enyo.openclassrooms.go4lunch.data.DataSingleton;
import ch.enyo.openclassrooms.go4lunch.models.googleapi.placesdetails.PlaceDetails;
import ch.enyo.openclassrooms.go4lunch.utils.GoogleApiPlaceStreams;
import ch.enyo.openclassrooms.go4lunch.views.PlaceDetailsViewAdapter;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListViewFragment extends BaseFragment {

    private static final String TAG = ListViewFragment.class.getSimpleName();

    private PlaceDetailsViewAdapter mAdapter;
    private List<PlaceDetails> mPlaceDetailsList;

    @BindView(R.id.fragment_list_view_recycleView)
    RecyclerView mRecyclerView;
    @BindView(R.id.fragment_list_view_swipeRefresh)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private Disposable mDisposable;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

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
        configureSwipeRefreshLayout();
        configureRecyclerView();
        executeHttpRequestWithRetrofit();


    }


    protected void configureRecyclerView(){

        this.mPlaceDetailsList =new ArrayList<>();
        this.mAdapter = new PlaceDetailsViewAdapter(mPlaceDetailsList, Glide.with(this));
        this.mRecyclerView.setAdapter(mAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        this.mRecyclerView.setLayoutManager(layoutManager);


        Log.i(TAG, " Recycler view configured ");
    }
    //**
    // * This method to refresh the layout.
     //*
    protected void configureSwipeRefreshLayout(){
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
               // updateUIWithResult(DataSingleton.getInstance().getPlaceDetailsList());
                executeHttpRequestWithRetrofit();

            }
        });
    }


    protected void executeHttpRequestWithRetrofit(){
        Map<String,String> map=DataSingleton.getInstance().getParametersMap();
        Log.i(TAG, "parameter map value "+map.toString());

        mDisposable = GoogleApiPlaceStreams.streamFPlaceDetailsList(map)
                .subscribeWith(new DisposableObserver<List<PlaceDetails>>() {

                    @Override
                    public void onNext(List<PlaceDetails> placeDetailsList) {
                        Log.i(TAG," Place details list downloading...");
                        Log.i(TAG," Details list size "+placeDetailsList.size());
                        if(placeDetailsList.size()!=0)
                            Log.i(TAG," place name "+placeDetailsList.get(0).getResult().getName());

                        updateUIWithResult(placeDetailsList);

                        Log.i(TAG, " Place details list update and size : "+mPlaceDetailsList.size());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i("TAG","aie, error in place details search: "  +Log.getStackTraceString(e));

                    }

                    @Override
                    public void onComplete() {

                        Log.i(TAG," Place details downloaded ");

                    }
                });

    }


    private void updateUIWithResult(List<PlaceDetails>list){
        this.mSwipeRefreshLayout.setRefreshing(false);
        this.mPlaceDetailsList.clear();
        this.mPlaceDetailsList.addAll(list);
        this.mAdapter.notifyDataSetChanged();
    }

    private void disposeWhenDestroy() {
        if (this.mDisposable != null && !this.mDisposable.isDisposed()) this.mDisposable.dispose();
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        disposeWhenDestroy();
    }
}
