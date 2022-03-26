package ch.enyoholali.openclassrooms.go4lunch.controllers.fragments;


import android.util.Log;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import ch.enyoholali.openclassrooms.go4lunch.R;
import ch.enyoholali.openclassrooms.go4lunch.api.UserHelper;
import ch.enyoholali.openclassrooms.go4lunch.base.BaseFragment;
import ch.enyoholali.openclassrooms.go4lunch.controllers.activities.WelcomeActivity;
import ch.enyoholali.openclassrooms.go4lunch.data.DataSingleton;
import ch.enyoholali.openclassrooms.go4lunch.models.firebase.User;
import ch.enyoholali.openclassrooms.go4lunch.models.googleapi.placesdetails.PlaceDetails;
import ch.enyoholali.openclassrooms.go4lunch.views.WorkmatesViewsAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class WorkmatesFragment extends BaseFragment implements WelcomeActivity.DataInterface {
    private static final String TAG= WorkmatesFragment.class.getSimpleName();

    @BindView(R.id.recycler_view_id)RecyclerView mRecyclerView;
   // @BindView(R.id.workmates_fragment_message) TextView mTextView;
    @BindView(R.id.swipe_container)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private WorkmatesViewsAdapter mAdapter;
    private List<User>mUserList;
    private List<PlaceDetails>mDetailsList;


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

    private void updateUserList(List<User>users){

        this.mSwipeRefreshLayout.setRefreshing(false);
        this.mUserList.clear();
        this.mUserList.addAll(users);
        this.mAdapter.notifyDataSetChanged();
    }

    /**
     * This method to refresh the layout.
     */
    private void configureSwipeRefreshLayout(){
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAllUsersWithoutCurrentUser();
            }
        });
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

        this.mAdapter = new WorkmatesViewsAdapter(mUserList, Glide.with(this),getContext(),1);
        this.mRecyclerView.setAdapter(mAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        this.mRecyclerView.setLayoutManager(layoutManager);
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
