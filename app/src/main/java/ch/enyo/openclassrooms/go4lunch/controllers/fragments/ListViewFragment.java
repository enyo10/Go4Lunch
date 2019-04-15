package ch.enyo.openclassrooms.go4lunch.controllers.fragments;


import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import ch.enyo.openclassrooms.go4lunch.BuildConfig;
import ch.enyo.openclassrooms.go4lunch.R;
import ch.enyo.openclassrooms.go4lunch.base.BaseFragment;
import ch.enyo.openclassrooms.go4lunch.models.google.NearBySearchResult;
import ch.enyo.openclassrooms.go4lunch.models.google.Result;
import ch.enyo.openclassrooms.go4lunch.utils.GoogleApiPlaceStreams;
import ch.enyo.openclassrooms.go4lunch.views.NearbyResultViewAdapter;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListViewFragment extends BaseFragment {
    private static final String TAG= ListViewFragment.class.getSimpleName();

    Disposable mDisposable;

    @BindView(R.id.fragment_list_view_recycleView)
    RecyclerView mRecyclerView;
    @BindView(R.id.fragment_list_view_swipeRefresh)
    SwipeRefreshLayout mSwipeRefreshLayout;


    private NearbyResultViewAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private List<Result> mResultList;


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


    protected void executeHttpRequestWithRetrofit(){

        Map<String, String> map=new HashMap<>();
        map.put("key", BuildConfig.ApiKey);
        map.put("keyword","cruise");
        map.put("type","restaurant");
        map.put("radius","5000");
        map.put("location","-33.8670522,151.1957362");

        mDisposable= GoogleApiPlaceStreams.getNearBySearchResultStream(map)
                .subscribeWith(new DisposableObserver<NearBySearchResult>() {
                    @Override
                    public void onNext(NearBySearchResult nearBySearchResult) {
                        Log.i(TAG," NearBySearchResult downloading...");

                        updateUIWithResult(nearBySearchResult.getResults());

                      /* Log.i(TAG," size " +mResultList.size());
                       for(Result r:mResultList) {
                           Log.i(TAG, "NAME " + r.getName());
                           Log.i(TAG, " Id " +r.getPlaceId());
                           Log.i(TAG, " Opening Hour " +r.getOpeningHours());
                           Log.i(TAG, " Type "+r.getTypes());
                           Log.i(TAG, " User Rating  "+r.getUserRatingsTotal());
                           Log.i(TAG, " Vicinity  "+r.getVicinity());
                           Log.i(TAG, " Scope  "+r.getScope());
                           Log.i(TAG, " Lat "+r.getGeometry().getViewport().getNortheast().getLat());
                       }
*/
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


    protected void configureRecyclerView(){

        this.mResultList =new ArrayList<>();
        this.mAdapter = new NearbyResultViewAdapter(mResultList);
        this.mRecyclerView.setAdapter(mAdapter);
        this.mLayoutManager= new LinearLayoutManager(getActivity());
        this.mRecyclerView.setLayoutManager(mLayoutManager);

        Log.i(TAG, " Recycler view configured ");
    }
    /**
     * This method to refresh the layout.
     */
    protected void configureSwipeRefreshLayout(){
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                executeHttpRequestWithRetrofit();
            }
        });
    }


    private void updateUIWithResult(List<Result>list){
        this.mSwipeRefreshLayout.setRefreshing(false);
        this.mResultList.clear();
        this.mResultList.addAll(list);
        this.mAdapter.notifyDataSetChanged();
    }



    private void disposeWhenDestroy(){
        if (this.mDisposable != null && !this.mDisposable.isDisposed()) this.mDisposable.dispose();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposeWhenDestroy();
    }
}
