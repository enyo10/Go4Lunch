package ch.enyoholali.openclassrooms.go4lunch.controllers.fragments;


import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.enyoholali.openclassrooms.go4lunch.R;
import ch.enyoholali.openclassrooms.go4lunch.base.BaseFragment;
import ch.enyoholali.openclassrooms.go4lunch.controllers.activities.PlaceDetailsActivity;
import ch.enyoholali.openclassrooms.go4lunch.controllers.activities.WelcomeActivity;
import ch.enyoholali.openclassrooms.go4lunch.data.DataManager;
import ch.enyoholali.openclassrooms.go4lunch.data.DataSingleton;
import ch.enyoholali.openclassrooms.go4lunch.databinding.FragmentListViewBinding;
import ch.enyoholali.openclassrooms.go4lunch.models.googleapi.placesdetails.PlaceDetails;
import ch.enyoholali.openclassrooms.go4lunch.utils.ItemClickSupport;
import ch.enyoholali.openclassrooms.go4lunch.views.PlaceDetailsViewAdapter;

import io.reactivex.disposables.Disposable;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListViewFragment extends BaseFragment implements WelcomeActivity.DataInterface {

    private static final String TAG = ListViewFragment.class.getSimpleName();

    private PlaceDetailsViewAdapter mAdapter;
    private List<PlaceDetails> mPlaceDetailsList=new ArrayList<>();
   private Location mLocation;

    private Disposable mDisposable;
    String jsonPlaceDetailsList;

    private FragmentListViewBinding binding;

    @Override
    public View onCreateView (LayoutInflater inflater,
                              ViewGroup container,
                              Bundle savedInstanceState) {
        binding = FragmentListViewBinding.inflate(inflater, container, false);

        configureView();
        configureRecyclerView();
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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
//        Collections.sort(placeDetailsList,Collections.reverseOrder());
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
        this.mPlaceDetailsList =new ArrayList<>();
        if(jsonPlaceDetailsList!=null)
            mPlaceDetailsList= DataManager.jsonToPlaceDetailsList(jsonPlaceDetailsList);
        configureRecyclerView();
        configureSwipeRefreshLayout();


    }

    private  void configureRecyclerView(){

        this.mAdapter = new PlaceDetailsViewAdapter(mPlaceDetailsList, Glide.with(this));
        binding.fragmentListViewRecycleView.setAdapter(mAdapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        binding.fragmentListViewRecycleView.setLayoutManager(layoutManager);

        Log.i(TAG, " Recycler view configured ");
    }

    /**
     * Configure the swipeRefreshLayout. It execute a request and refresh the page.
     */
    private void configureSwipeRefreshLayout(){
        binding.fragmentListViewSwipeRefresh.setOnRefreshListener(() -> updateUIWithResult( DataSingleton.getInstance().getPlaceDetailsList()));
    }

    //-----------------------------------------------------------------------------------------
    //                                  CONFIGURE ACTIONS
    //------------------------------------------------------------------------------------------

    @Override
    protected void configureOnclickRecyclerView() {
        ItemClickSupport.addTo(binding.fragmentListViewRecycleView, R.layout.fragment_list_view_item)
                .setOnItemClickListener((recyclerView, position, v) -> {

                 PlaceDetails placeDetails =  mAdapter.getItem(position);
                 DataSingleton.getInstance().setPlaceDetails(placeDetails);

                    Log.i(TAG,"  selected photo reference : "+placeDetails.getImageUrl());
                    Log.i(TAG," selected status: "+ placeDetails.getStatus());

                    Toast.makeText(getContext(), "CLICK on position: " + position + " name: " +
                            placeDetails.getResult().getName(), Toast.LENGTH_SHORT).show();

                    // start place details activity.
                    startActivity(PlaceDetailsActivity.class);
                });
    }



    //----------------------------------------------------------------------------------------------
    //                           REQUESTS
    //----------------------------------------------------------------------------------------------

    //----------------------------------------------------------------------------------------------
    //                                   HELP METHODS
    //----------------------------------------------------------------------------------------------


    private void updateUIWithResult(List<PlaceDetails>list){
        Log.d(TAG," placeDetails size "+list.size());
        Collections.sort(list,Collections.reverseOrder());
        binding.fragmentListViewSwipeRefresh.setRefreshing(false);
        this.mPlaceDetailsList.clear();
        this.mPlaceDetailsList.addAll(list);
        this.mAdapter.notifyDataSetChanged();

    }


 /*   private void disposeWhenDestroy() {
        if (this.mDisposable != null && !this.mDisposable.isDisposed()) this.mDisposable.dispose();



    }*/



   /* @Override
    public void onDestroy() {
        super.onDestroy();
        disposeWhenDestroy();
    }*/


}
