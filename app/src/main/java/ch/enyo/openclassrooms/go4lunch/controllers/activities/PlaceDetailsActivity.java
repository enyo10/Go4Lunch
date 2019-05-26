package ch.enyo.openclassrooms.go4lunch.controllers.activities;


import android.support.design.widget.FloatingActionButton;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;

import butterknife.BindView;
import butterknife.ButterKnife;
import ch.enyo.openclassrooms.go4lunch.R;
import ch.enyo.openclassrooms.go4lunch.base.BaseActivity;
import ch.enyo.openclassrooms.go4lunch.data.DataSingleton;
import ch.enyo.openclassrooms.go4lunch.models.googleapi.placesdetails.PlaceDetails;
import ch.enyo.openclassrooms.go4lunch.utils.DataFormatter;

public class PlaceDetailsActivity extends BaseActivity implements DataFormatter {
    private static final String TAG = PlaceDetailsActivity.class.getSimpleName();

    // The views.
    @BindView(R.id.restaurant_image)ImageView mInfoImage;
    @BindView(R.id.floatingActionButton) FloatingActionButton mFloatingActionButton;
    @BindView(R.id.restaurant_name) TextView mNameTextView;
    @BindView(R.id.restaurant_address)TextView mAddress;
    @BindView(R.id.restaurant_rating_bar) RatingBar mRatingBar;
    @BindView(R.id.restaurant_phone_button) Button mPhoneButton;
    @BindView(R.id.restaurant_like_button)Button mRestaurantLikeButton;
    @BindView(R.id.restaurant_website_button)Button mWebsiteButton;

    // For DATA
    RequestManager glide;
    private String phoneNumber;
    private String webAddress;
    private double rating;

    @Override
    public int getActivityLayout() {
        return R.layout.activity_place_details;
    }

    @Override
    public void configureView() {
        DataSingleton dataSingleton= DataSingleton.getInstance();
        PlaceDetails placeDetails= dataSingleton.getPlaceDetail();
        ButterKnife.bind(this);
        glide = Glide.with(this);
        String  url="https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&maxheight=400&photoreference=";
        String apiKey = "&key=" + "AIzaSyAj8TgbhVVLCxEldGuNHxxo2w4P-S2mxG8";

        if ( placeDetails.getResult() != null) {
            //Restaurant name
            this.mNameTextView.setText(placeDetails.getResult().getName());
            //Restaurant photo
            if (placeDetails.getResult().getPhotos() != null) {
                String imageUrl=url + placeDetails.getResult().getPhotos().get(0).getPhotoReference() + apiKey;
               // String imageUrl = placeDetails.getImageUrl();
                glide.load(imageUrl)
                        .apply(RequestOptions.centerCropTransform())
                        .into(this.mInfoImage);
            }
            // Address
            this.mAddress.setText(formatAddress(placeDetails.getResult().getFormattedAddress()));
            if (placeDetails.getResult().getRating() != null) {
                this.mRatingBar.setRating(formatRating(placeDetails.getResult().getRating()));
            }
            // Distance
           /* if (lat != 0 && lng != 0) {
                this.mRestaurantDistance.setText(computeDistance(lat, placeDetails.getResult().getGeometry().getLocation().getLat(),
                        lng, placeDetails.getResult().getGeometry().getLocation().getLng()));
            }*/
            //Opening time
            /*if (placeDetails.getResult().getOpeningHours() != null) {
                this.mRestaurantOpening.setText(formatWeekDayText(placeDetails.getResult().getOpeningHours()
                        .getWeekdayText()));

            }*/

           phoneNumber= placeDetails.getResult().getInternationalPhoneNumber();
           webAddress=placeDetails.getResult().getWebsite();
           rating=placeDetails.getResult().getRating();

        }
    }
}
