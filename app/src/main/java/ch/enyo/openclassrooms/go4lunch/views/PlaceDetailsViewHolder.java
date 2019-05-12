package ch.enyo.openclassrooms.go4lunch.views;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import ch.enyo.openclassrooms.go4lunch.R;
import ch.enyo.openclassrooms.go4lunch.models.googleapi.placesdetails.PlaceDetails;
import ch.enyo.openclassrooms.go4lunch.models.googleapi.placesdetails.PlaceDetailsResult;

public class PlaceDetailsViewHolder extends ViewHolder {
    private static final String TAG=PlaceDetailsViewHolder.class.getSimpleName();

    @BindView(R.id.fragment_item_name)
    TextView mNameTextView;
    @BindView(R.id.fragment_item_address)TextView mAddressTextView;
    @BindView(R.id.fragment_item_image)
    ImageView mImageView;


    public PlaceDetailsViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }


    protected void updateWithPlaceDetails(PlaceDetails placeDetails, RequestManager glide){
        PlaceDetailsResult result= placeDetails.getResult();
        mNameTextView.setText(placeDetails.getResult().getName());
        mAddressTextView.setText(result.getVicinity());

        if(result.getPhotos().size()!=0)
            glide.load(result.getPhotos().get(0).getPhotoReference()).into(mImageView);

        Log.i(TAG, " place name ."+result.getName());


    }
}
