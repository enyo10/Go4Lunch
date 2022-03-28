package ch.enyoholali.openclassrooms.go4lunch.controllers.fragments;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import ch.enyoholali.openclassrooms.go4lunch.api.UserHelper;
import ch.enyoholali.openclassrooms.go4lunch.base.BaseFragment;
import ch.enyoholali.openclassrooms.go4lunch.controllers.activities.WelcomeActivity;
import ch.enyoholali.openclassrooms.go4lunch.data.DataSingleton;
import ch.enyoholali.openclassrooms.go4lunch.databinding.FragmentWorkmatesBinding;
import ch.enyoholali.openclassrooms.go4lunch.models.firebase.User;
import ch.enyoholali.openclassrooms.go4lunch.models.googleapi.placesdetails.PlaceDetails;
import ch.enyoholali.openclassrooms.go4lunch.views.WorkmatesViewsAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class WorkmatesFragment extends BaseFragment<FragmentWorkmatesBinding> implements WelcomeActivity.DataInterface {
    private static final String TAG= WorkmatesFragment.class.getSimpleName();

    private WorkmatesViewsAdapter mAdapter;
    private List<User>mUserList;
    private List<PlaceDetails>mDetailsList;


    @Override
    public BaseFragment<FragmentWorkmatesBinding> newInstance() {
        WorkmatesFragment workmatesFragment=new WorkmatesFragment();
        workmatesFragment.name="Workmates";
        return workmatesFragment;
    }

    @Override
    public FragmentWorkmatesBinding getBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentWorkmatesBinding.inflate(inflater,container,false);
    }


    @Override
    protected void configureDesign(View v) {

    }

    private void updateUserList(List<User>users){
        binding.workmatesRecycler.swipeContainer.setRefreshing(false);

        this.mUserList.clear();
        this.mUserList.addAll(users);
        this.mAdapter.notifyDataSetChanged();
    }

    /**
     * This method to refresh the layout.
     */
    private void configureSwipeRefreshLayout(){
        binding.workmatesRecycler.swipeContainer.
        setOnRefreshListener(this::getAllUsersWithoutCurrentUser);
    }

    // ---------------------------------------------------------------------------------------------
    //                                          UI
    // ---------------------------------------------------------------------------------------------

    @Override
    protected void configureView() {
      mUserList=new ArrayList<>();
      mDetailsList=new ArrayList<>();
      configureRecyclerView();
      configureSwipeRefreshLayout();
      getAllUsersWithoutCurrentUser();

    }

    private void configureRecyclerView(){
        RecyclerView recyclerView = binding.workmatesRecycler.recyclerViewId;

        this.mAdapter = new WorkmatesViewsAdapter(mUserList, Glide.with(this),getContext(),1);

        recyclerView.setAdapter(mAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
    }



    @Override
    protected void configureOnclickRecyclerView() {

    }


    // ---------------------------------------------------------------------------------------------
    //                                  REST REQUESTS
    // ---------------------------------------------------------------------------------------------

    /**
     * This method retrieves all the user without the current.
     */
    private void getAllUsersWithoutCurrentUser(){
        UserHelper.getAllUsers().addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    // Handle error
                    //...
                    Log.i(TAG, " Error -->: "+e.getMessage());
                    e.printStackTrace();
                    return;
                }

                // Convert query snapshot to a list of chats
                List<User> userList = snapshot.toObjects(User.class);
                List<User>otherList=new ArrayList<>();

                for (int i=0;i<userList.size();i++) {
                    if(!getCurrentUser().getUid().equals(userList.get(i).getUid())) {
                        otherList.add(userList.get(i));

                    }
                    else {
                        DataSingleton.getInstance().setActuelUser(userList.get(i));
                        Log.d(TAG, " current user ");
                    }

                }
                Log.d(TAG, " other list size "+otherList.size());
                updateUserList(otherList);

            }
        });
    }


    @Override
    public void doMySearch(String query) {
        Log.d(TAG, "In Workmates Fragment "+query);
    }

    @Override
    public void update(List<PlaceDetails>placeDetailsList) {
        Log.d(TAG, " place details updated. Size is "+placeDetailsList.size());
        DataSingleton.getInstance().setPlaceDetailsList(placeDetailsList);

    }


}
