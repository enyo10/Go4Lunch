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
import ch.enyoholali.openclassrooms.go4lunch.models.googleapi.placesdetails.PlaceDetails;

public class PlaceDetailsViewAdapter extends RecyclerView.Adapter<PlaceDetailsViewHolder> {

    private static final String TAG = PlaceDetailsViewAdapter.class.getSimpleName();

    // FOR DATA.
    private final List<PlaceDetails> mPlaceDetailsList;
    private final RequestManager mRequestManager;

    /**
     * A constructor the view holder. It take two parameters that will be initialize.
     * @param placeDetailsList, the place details list to display.
     * @param requestManager, Glide use to display image.
     */
    public PlaceDetailsViewAdapter(List<PlaceDetails> placeDetailsList, RequestManager requestManager) {
        mPlaceDetailsList= placeDetailsList;
        mRequestManager = requestManager;
    }


    @NonNull
    @Override
    public PlaceDetailsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        // CREATE VIEW HOLDER AND INFLATE ITS XML LAYOUT.
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view= inflater.inflate(R.layout.fragment_list_view_item,viewGroup,false);

        return new PlaceDetailsViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull PlaceDetailsViewHolder placeDetailsViewHolder, int i) {
        placeDetailsViewHolder.updateWithPlaceDetails(mPlaceDetailsList.get(i),mRequestManager);

    }

    @Override
    public int getItemCount() {
        return mPlaceDetailsList.size();
    }

    /**
     * This method to return the item at position "position" on the recycler view.
     * @param position, the position
     * @return Object, the item to return.
     */
    public PlaceDetails getItem(int position){
        return this.mPlaceDetailsList.get(position);
    }


}
