package ch.enyo.openclassrooms.go4lunch.views;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;

import butterknife.BindView;
import butterknife.ButterKnife;
import ch.enyo.openclassrooms.go4lunch.R;
import ch.enyo.openclassrooms.go4lunch.data.DataSingleton;
import ch.enyo.openclassrooms.go4lunch.models.googleapi.placesdetails.PlaceDetails;
import ch.enyo.openclassrooms.go4lunch.utils.DataFormatter;

public class PlaceDetailsViewHolder extends ViewHolder implements DataFormatter {
    private static final String TAG=PlaceDetailsViewHolder.class.getSimpleName();

  /*  @BindView(R.id.fragment_item_name)
    TextView mNameTextView;
    @BindView(R.id.fragment_item_address)TextView mAddressTextView;
    @BindView(R.id.fragment_item_image) ImageView mImageView;
*/

    //WIDGET
    @BindView(R.id.item_restaurant_layout)
    ConstraintLayout mLayout;
    @BindView(R.id.item_restaurants_name)
    TextView mRestaurantName;
    @BindView(R.id.item_restaurant_imageView)
    ImageView mRestaurantPhoto;
    @BindView(R.id.item_restaurants_address)
    TextView mRestaurantAddress;
    @BindView(R.id.item_restaurants_opening)
    TextView mRestaurantOpening;
    @BindView(R.id.item_restaurants_distance)
    TextView mRestaurantDistance;
    @BindView(R.id.item_restaurants_nb_workmates)
    TextView mNbOfWorkmates;
    @BindView(R.id.item_restaurants_workmates_images)
    ImageView mWorkmateImageView;
    @BindView(R.id.item_restaurants_ratingbar)
    RatingBar mRatingBar;

    // VAR
    private double lat = DataSingleton.getInstance().getLocation().getLatitude();
    private double lng = DataSingleton.getInstance().getLocation().getLongitude();


    public PlaceDetailsViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    public void updateWithPlaceDetails(PlaceDetails placeDetails, RequestManager glide){
        String url= "https://maps.googleapis.com/maps/api/place/photo?maxwidth=100&maxheight=100&photoreference=";
        String apiKey = "&key=" + "AIzaSyAj8TgbhVVLCxEldGuNHxxo2w4P-S2mxG8";
        String urlbis= "https://maps.googleapis.com/maps/api/place/photo?key=AIzaSyAj8TgbhVVLCxEldGuNHxxo2w4P-S2mxG8&photoreference=";
        int nbOfWorkmate=1;

        if ( placeDetails.getResult() != null)
        {
            //Restaurant name
            this.mRestaurantName.setText(placeDetails.getResult().getName());
            //Restaurant photo
            if (placeDetails.getResult().getPhotos() != null) {
                String imageUrl=url + placeDetails.getResult().getPhotos().get(0).getPhotoReference() + apiKey;
                //String imageUrl =placeDetails.getImageUrl();
                glide.load(imageUrl)
                        .apply(RequestOptions.centerCropTransform())
                        .into(this.mRestaurantPhoto);
            }
            // Address
            this.mRestaurantAddress.setText(formatAddress(placeDetails.getResult().getFormattedAddress()));
            if (placeDetails.getResult().getRating() != null) {
                this.mRatingBar.setRating(formatRating(placeDetails.getResult().getRating()));
            }
            // Distance
            if (lat != 0 && lng != 0) {
                this.mRestaurantDistance.setText(computeDistance(lat, placeDetails.getResult().getGeometry().getLocation().getLat(),
                        lng, placeDetails.getResult().getGeometry().getLocation().getLng()));
            }
            //Opening time
            if (placeDetails.getResult().getOpeningHours() != null) {
                this.mRestaurantOpening.setText(formatWeekDayText(placeDetails.getResult().getOpeningHours()
                        .getWeekdayText()));

            }
            // Number of workmates
            if (nbOfWorkmate > 0) {
                mNbOfWorkmates.setText(String.valueOf("(" + nbOfWorkmate + ")"));
                this.mNbOfWorkmates.setVisibility(View.VISIBLE);
                this.mWorkmateImageView.setVisibility(View.VISIBLE);
            } else {
                this.mNbOfWorkmates.setVisibility(View.INVISIBLE);
                this.mWorkmateImageView.setVisibility(View.INVISIBLE);
            }

        }

        /*// Configure the clicks
        if (mRestaurantPhoto != null) {
            this.mRestaurantPhoto.setOnClickListener(this);
        }
        this.callbackWeakRef = new WeakReference<RestaurantAdapter.Listener>(callback);*/

    }


  /*  public void updateWithPlaceDetails(PlaceDetails placeDetails, RequestManager glide){
        PlaceDetailsResult result= placeDetails.getResult();
        mNameTextView.setText(placeDetails.getResult().getName());
        mAddressTextView.setText(result.getVicinity());
        String url= DataSingleton.getInstance().getUrl();
        String apiKey = "&key=" + "AIzaSyAj8TgbhVVLCxEldGuNHxxo2w4P-S2mxG8";
        glide.load(url+result.getPhotos().get(0).getPhotoReference()+ apiKey).apply(RequestOptions.centerCropTransform()).into(mImageView);

        Log.i(TAG," ref "+url+result.getPhotos().get(0).getPhotoReference()+ apiKey);


       *//* if(result.getPhotos().size()!=0)
            glide.load(result.getPhotos().get(0)).into(mImageView);
        *//*


        Log.i(TAG, " Reference ."+result.getReference());
        Log.i(TAG, " url ."+result.getUrl());
        Log.i(TAG, " first photo ."+result.getPhotos().get(0).getPhotoReference());



    }*/
}
