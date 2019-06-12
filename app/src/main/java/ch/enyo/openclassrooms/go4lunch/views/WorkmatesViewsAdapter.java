package ch.enyo.openclassrooms.go4lunch.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;

import java.util.List;

import ch.enyo.openclassrooms.go4lunch.R;
import ch.enyo.openclassrooms.go4lunch.models.firebase.User;


public class WorkmatesViewsAdapter extends RecyclerView.Adapter<WorkmatesViewHolder> {

    // FOR DATA.
    private List<User> mPlaceDetailsList;
    private RequestManager glide;

    public WorkmatesViewsAdapter(List<User>users,RequestManager glide){
        this.mPlaceDetailsList=users;
        this.glide=glide;

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
        holder.updateWithUser(mPlaceDetailsList.get(position),glide);

    }

    @Override
    public int getItemCount() {
        return mPlaceDetailsList.size();
    }
}
