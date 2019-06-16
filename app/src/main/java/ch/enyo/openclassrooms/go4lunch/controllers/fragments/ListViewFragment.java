package ch.enyo.openclassrooms.go4lunch.controllers.fragments;


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
import java.util.List;

import butterknife.BindView;
import ch.enyo.openclassrooms.go4lunch.R;
import ch.enyo.openclassrooms.go4lunch.base.BaseFragment;
import ch.enyo.openclassrooms.go4lunch.controllers.activities.PlaceDetailsActivity;
import ch.enyo.openclassrooms.go4lunch.controllers.activities.WelcomeActivity;
import ch.enyo.openclassrooms.go4lunch.data.DataSingleton;
import ch.enyo.openclassrooms.go4lunch.models.googleapi.placesdetails.PlaceDetails;
import ch.enyo.openclassrooms.go4lunch.utils.GoogleApiPlaceStreams;
import ch.enyo.openclassrooms.go4lunch.utils.ItemClickSupport;
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
    private Location mLocation;
    private LatLng mDevicePosition;

    @BindView(R.id.fragment_list_view_recycleView)
    RecyclerView mRecyclerView;
    @BindView(R.id.fragment_list_view_swipeRefresh)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private Disposable mDisposable;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mPlaceDetailsList =new ArrayList<>();
        getLatLng();

      //  Log.i(TAG, " Location in OnCreate -- latitude "+mLastKnownLocation.getLongitude());
//        executeHttpRequestWithRetrofit();

    }

    private void getLatLng(){
        WelcomeActivity welcomeActivity=(WelcomeActivity)getActivity();
        if(welcomeActivity!=null)
            mLocation= welcomeActivity.getDeviceLocation();

        Log.d(TAG, " location ");

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


    public Location getLocation() {
        return mLocation;
    }

    public void setLocation(Location location) {

        mLocation = location;
        executeHttpRequestWithRetrofit();
    }

//----------------------------------------------------------------------------------------------
    //                      CONFIGURE VIEWS
    //----------------------------------------------------------------------------------------------

    @Override
    protected void configureView() {
        configureRecyclerView();
        configureSwipeRefreshLayout();
        executeHttpRequestWithRetrofit();

    }

    private  void configureRecyclerView(){

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
                executeHttpRequestWithRetrofit();

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
                     DataSingleton.getInstance().setPlaceDetail(placeDetails);

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
        LatLng latlng;
        if(mLocation==null){
        latlng =new LatLng(43,17);
        }else
            latlng=new LatLng(mLocation.getLatitude(),mLocation.getLongitude());

        String latlng1=latlng.latitude+","+latlng.longitude;

        Log.i(TAG," location "+latlng.toString());

        mDisposable = GoogleApiPlaceStreams.streamFPlaceDetailsList(latlng1)
                .subscribeWith(new DisposableObserver<List<PlaceDetails>>() {
                    @Override
                    public void onNext(List<PlaceDetails> placeDetailsList) {
                        Log.i(TAG," Place details list downloading...");
                        Log.i(TAG," Details list size "+placeDetailsList.size());

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
        this.mSwipeRefreshLayout.setRefreshing(false);
        this.mPlaceDetailsList.clear();
        setImageUrls(list);
        this.mPlaceDetailsList.addAll(list);
        this.mAdapter.notifyDataSetChanged();
    }
    /**
     * This method is a helper that help to add an image url to the place details.
     * @param list, a list of place details that will be modify.
     */
    private void setImageUrls(List<PlaceDetails>list){
        String url= DataSingleton.getInstance().getUrl();
        String apiKey = "&key=" + "AIzaSyAj8TgbhVVLCxEldGuNHxxo2w4P-S2mxG8";
        for(int i=0;i<list.size();i++){
            if (list.get(i).getResult().getPhotos() != null) {
                String imageUrl=url +list.get(i).getResult().getPhotos().get(0).getPhotoReference() + apiKey;
                list.get(i).setImageUrl(imageUrl);

                Log.i(TAG, "image Url "+ list.get(i).getImageUrl());
            }

        }

    }

    @Override
    public void onResume() {
        executeHttpRequestWithRetrofit();
        super.onResume();

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
