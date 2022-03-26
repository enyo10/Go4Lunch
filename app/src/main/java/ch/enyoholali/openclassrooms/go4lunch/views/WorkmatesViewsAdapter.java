package ch.enyoholali.openclassrooms.go4lunch.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;

import java.util.List;

import ch.enyoholali.openclassrooms.go4lunch.R;
import ch.enyoholali.openclassrooms.go4lunch.data.DataSingleton;
import ch.enyoholali.openclassrooms.go4lunch.models.firebase.User;
import ch.enyoholali.openclassrooms.go4lunch.models.googleapi.placesdetails.PlaceDetails;


public class WorkmatesViewsAdapter extends RecyclerView.Adapter<WorkmatesViewHolder> {

    // FOR DATA.
    private final List<User> mWorkmateList;
    private final RequestManager glide;
    private final Context mContext;
    private List<PlaceDetails>mPlaceDetailsList;
    private final int tag;


    public WorkmatesViewsAdapter(List<User>users,RequestManager glide,Context context, int tag){
        this.mWorkmateList=users;
        this.glide=glide;
        this.mContext=context;
        this.mPlaceDetailsList= DataSingleton.getInstance().getPlaceDetailsList();
        this.tag=tag;



    }
    @NonNull
    @Override
    public WorkmatesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context =parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view= inflater.inflate(R.layout.fragment_workmates_list_item,parent,false);

        return new WorkmatesViewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull WorkmatesViewHolder holder, int position) {
        mPlaceDetailsList=DataSingleton.getInstance().getPlaceDetailsList();
        holder.updateWithUser(mWorkmateList.get(position),glide,mContext,mPlaceDetailsList,tag);

    }

    @Override
    public int getItemCount() {
        return mWorkmateList.size();
    }
}
