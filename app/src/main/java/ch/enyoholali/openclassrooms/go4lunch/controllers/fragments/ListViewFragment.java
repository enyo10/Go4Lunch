package ch.enyoholali.openclassrooms.go4lunch.controllers.fragments;


import android.location.Location;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import ch.enyoholali.openclassrooms.go4lunch.R;
import ch.enyoholali.openclassrooms.go4lunch.base.BaseFragment;
import ch.enyoholali.openclassrooms.go4lunch.controllers.activities.PlaceDetailsActivity;
import ch.enyoholali.openclassrooms.go4lunch.controllers.activities.WelcomeActivity;
import ch.enyoholali.openclassrooms.go4lunch.data.DataSingleton;
import ch.enyoholali.openclassrooms.go4lunch.models.googleapi.placesdetails.PlaceDetails;
import ch.enyoholali.openclassrooms.go4lunch.utils.GoogleApiPlaceStreams;
import ch.enyoholali.openclassrooms.go4lunch.utils.ItemClickSupport;
import ch.enyoholali.openclassrooms.go4lunch.views.PlaceDetailsViewAdapter;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListViewFragment extends BaseFragment implements WelcomeActivity.DataInterface {

    private static final String TAG = ListViewFragment.class.getSimpleName();

    private PlaceDetailsViewAdapter mAdapter;
    private List<PlaceDetails> mPlaceDetailsList;
    private Location mLocation;

    @BindView(R.id.fragment_list_view_recycleView)
    RecyclerView mRecyclerView;
    @BindView(R.id.fragment_list_view_swipeRefresh)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private Disposable mDisposable;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mPlaceDetailsList =new ArrayList<>();

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
    public void update(List<PlaceDetails>placeDetailsList) {

        Log.d(TAG, "in ListView Fragment: place details size "+ placeDetailsList.size());
        Collections.sort(placeDetailsList,Collections.reverseOrder());
        updateUIWithResult(placeDetailsList);

    }


    @Override
    public void doMySearch(String query) {
        Log.i(TAG,"In ListView Fragment ");
    }


//----------------------------------------------------------------------------------------------
    //                      CONFIGURE VIEWS
    //----------------------------------------------------------------------------------------------

    @Override
    protected void configureView() {
        configureRecyclerView();
      //  configureSwipeRefreshLayout();


    }

    private  void configureRecyclerView(){
        Collections.sort(mPlaceDetailsList,Collections.reverseOrder());

        this.mAdapter = new PlaceDetailsViewAdapter(mPlaceDetailsList, Glide.with(this));
        this.mRecyclerView.setAdapter(mAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        this.mRecyclerView.setLayoutManager(layoutManager);

        Log.i(TAG, " Recycler view configured ");
    }

    /**
     * Configure the swipeRefreshLayout. It execute a request and refresh the page.
     */
    private void configureSwipeRefreshLayout(){
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                updateUIWithResult( DataSingleton.getInstance().getPlaceDetailsList());

            }
        });
    }

    //-----------------------------------------------------------------------------------------
    //                                  CONFIGURE ACTIONS
    //------------------------------------------------------------------------------------------

    @Override
    protected void configureOnclickRecyclerView() {
        ItemClickSupport.addTo(mRecyclerView, R.layout.fragment_list_view_item)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {

                     PlaceDetails placeDetails =  mAdapter.getItem(position);
                     DataSingleton.getInstance().setPlaceDetails(placeDetails);

                        Log.i(TAG,"  selected photo reference : "+placeDetails.getImageUrl());
                        Log.i(TAG," selected status: "+ placeDetails.getStatus());

                        Toast.makeText(getContext(), "CLICK on position: " + position + " name: " +
                                placeDetails.getResult().getName(), Toast.LENGTH_SHORT).show();

                        // start place details activity.
                        startActivity(PlaceDetailsActivity.class);
                    }
                });
    }



    //----------------------------------------------------------------------------------------------
    //                           REQUESTS
    //----------------------------------------------------------------------------------------------

    private void executeHttpRequestWithRetrofit(){

        LatLng latlng=new LatLng(mLocation.getLatitude(),mLocation.getLongitude());

        String latlng1=latlng.latitude+","+latlng.longitude;

        Log.i(TAG," location "+latlng.toString());

        mDisposable = GoogleApiPlaceStreams.streamFPlaceDetailsList(latlng1)
                .subscribeWith(new DisposableObserver<List<PlaceDetails>>() {
                    @Override
                    public void onNext(List<PlaceDetails> placeDetailsList) {
                        Log.i(TAG," Place details list downloading...");
                        Log.i(TAG," Details list size "+placeDetailsList.size());


                        /*Double lat = placeNearbyResult.get(i).getGeometry().getLocation().getLat();
                        Double lng = placeNearbyResult.get(i).getGeometry().getLocation().getLng();
                        String placename = placeNearbyResult.get(i).getName();
                        String vinicity = placeNearbyResult.get(i).getVicinity();
*/

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

    //----------------------------------------------------------------------------------------------
    //                                   HELP METHODS
    //----------------------------------------------------------------------------------------------

    private void updateUIWithResult(List<PlaceDetails>list){
        Collections.sort(list,Collections.reverseOrder());
        this.mSwipeRefreshLayout.setRefreshing(false);
        this.mPlaceDetailsList.clear();
        //setImageUrls(list);
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
