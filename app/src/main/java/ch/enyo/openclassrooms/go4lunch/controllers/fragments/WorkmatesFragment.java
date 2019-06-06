package ch.enyo.openclassrooms.go4lunch.controllers.fragments;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

import butterknife.BindView;
import ch.enyo.openclassrooms.go4lunch.R;
import ch.enyo.openclassrooms.go4lunch.api.UserHelper;
import ch.enyo.openclassrooms.go4lunch.base.BaseFragment;
import ch.enyo.openclassrooms.go4lunch.models.firebase.User;
import ch.enyo.openclassrooms.go4lunch.views.WorkmatesViewAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class WorkmatesFragment extends BaseFragment implements WorkmatesViewAdapter.Listener{
    private static final String TAG=WorkmatesFragment.class.getSimpleName();

    @BindView(R.id.recycler_view_id)RecyclerView mRecyclerView;
    @BindView(R.id.workmates_fragment_message)
    TextView mTextView;

    private WorkmatesViewAdapter mWorkmatesViewAdapter;



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

    // --------------------
    // UI
    // --------------------

    @Override
    protected void configureView() {
       // getUsersList();
        this.configureRecyclerView();

    }

    private void configureRecyclerView(){

            this.mWorkmatesViewAdapter=
                    new WorkmatesViewAdapter(generateOptionsForAdapter(UserHelper.getAllUsers()), Glide.with(Objects.requireNonNull(this)),
                            this);

            mWorkmatesViewAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    mRecyclerView.smoothScrollToPosition(mWorkmatesViewAdapter.getItemCount());
                }

            });

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(this.mWorkmatesViewAdapter);
        //Handle the click on item of recycler view
        this.configureOnclickRecyclerView();
    }



    @Override
    protected void configureOnclickRecyclerView() {

    }


    // --------------------
    // REST REQUESTS
    // --------------------


   /* private void getUsersList(){

        UserHelper.getUsersCollection().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot documentSnapshot:task.getResult()){
                        User user=documentSnapshot.toObject(User.class);
                        //mUsers.add(user);
                        Log.i(TAG, " User  " +user.getUid());
                        Log.i(TAG, "User name "+ user.getUsername());

                    }
                }else {
                    Log.w(TAG, "Error getting documents.");
                }

            }
        });
    }
*/

    // 6 - Create options for RecyclerView from a Query
    private FirestoreRecyclerOptions<User> generateOptionsForAdapter(Query query){
        return new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .setLifecycleOwner(this)
                .build();
    }


    @Override
    public void onDataChanged() {
        Log.i(TAG, "  number of users  -->"+ mWorkmatesViewAdapter.getItemCount());
        // Show TextView in case RecyclerView is empty
        this.mTextView.setVisibility(this.mWorkmatesViewAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);



    }
}
