package ch.enyoholali.openclassrooms.go4lunch.views;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import ch.enyoholali.openclassrooms.go4lunch.BuildConfig;
import ch.enyoholali.openclassrooms.go4lunch.R;
import ch.enyoholali.openclassrooms.go4lunch.api.UserHelper;
import ch.enyoholali.openclassrooms.go4lunch.data.DataFormatter;
import ch.enyoholali.openclassrooms.go4lunch.data.DataSingleton;
import ch.enyoholali.openclassrooms.go4lunch.models.firebase.User;
import ch.enyoholali.openclassrooms.go4lunch.models.googleapi.placesdetails.PlaceDetails;

public class PlaceDetailsViewHolder extends ViewHolder implements DataFormatter {
    private static final String TAG= PlaceDetailsViewHolder.class.getSimpleName();

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
    private final double lat = DataSingleton.getInstance().getLocation().getLatitude();
    private final double lng = DataSingleton.getInstance().getLocation().getLongitude();
    private final List<User>mSubscribers=new ArrayList<>();
    private final List<User>mUserList=new ArrayList<>();
    private PlaceDetails mPlaceDetails;



    public PlaceDetailsViewHolder(@NonNull View itemView) {
        super(itemView);
        getAllUsersFromFireBase();
        ButterKnife.bind(this,itemView);

    }

    public void updateWithPlaceDetails(PlaceDetails placeDetails, RequestManager glide){
        String url= "https://maps.googleapis.com/maps/api/place/photo?maxwidth=100&maxheight=100&photoreference=";
        String apiKey = "&key=" + BuildConfig.APIKEY;// "AIzaSyAj8TgbhVVLCxEldGuNHxxo2w4P-S2mxG8";
        String urlbis= "https://maps.googleapis.com/maps/api/place/photo?key=AIzaSyAj8TgbhVVLCxEldGuNHxxo2w4P-S2mxG8&photoreference=";
              updateSubscriberList(placeDetails);

        int nbOfWorkmate = mSubscribers.size();

        if ( placeDetails.getResult() != null)
        {
           Log.d(TAG, " rating "+ placeDetails.getResult().getRating());
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
                mNbOfWorkmates.setText(String.format(Locale.US, "(%d)", nbOfWorkmate));
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

    /**
     * Get the users who has selected a given restaurant.
     *       the place details that the user are supposed to choose.
     */

    private void getAllUsersFromFireBase() {

        UserHelper.getAllUsers().addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    // Handle error
                    Log.i(TAG, " Error by retrieve user from fire base-->: " + e.getMessage());
                    e.printStackTrace();
                    return;
                }
                // Convert query snapshot to a list of users.
                List<User> list = snapshot.toObjects(User.class);
                updateUserList(list);

                }


        });
    }

    /**
     * This to update the subscriber list.
     * @param list,
     *      the list to add.
     */
    private void updateUserList(List<User>list){
        mUserList.clear();
        mUserList.addAll(list);

    }

    private void updateSubscriberList(PlaceDetails placeDetails){
        mSubscribers.clear();

        for(int i=0;i<mUserList.size();i++){
            if(mUserList.get(i).getRestaurantId()!=null){
                if(mUserList.get(i).getRestaurantId().equals(placeDetails.getResult().getPlaceId()))
                    mSubscribers.add(mUserList.get(i));
            }

        }

    }
}
