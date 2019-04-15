package ch.enyo.openclassrooms.go4lunch.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ch.enyo.openclassrooms.go4lunch.R;
import ch.enyo.openclassrooms.go4lunch.models.google.Result;

public class NearbyResultViewAdapter extends RecyclerView.Adapter<NearbyResultViewHolder> {
    // FOR DATA.
    private List<Result>mResultList;

    /**
     * A constructor
     * @param resultList,
     * A list to initialise.
     */
    public NearbyResultViewAdapter(List<Result> resultList){
        this.mResultList=resultList;

    }

    @NonNull
    @Override
    public NearbyResultViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        // CREATE VIEW HOLDER AND INFLATE ITS XML LAYOUT.
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view= inflater.inflate(R.layout.fragment_list_view_item,viewGroup,false);

        return new NearbyResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NearbyResultViewHolder nearbyResultViewHolder, int i) {
        nearbyResultViewHolder.updateWithResult(this.mResultList.get(i));

    }

    @Override
    public int getItemCount() {
        return this.mResultList.size();
    }
}
