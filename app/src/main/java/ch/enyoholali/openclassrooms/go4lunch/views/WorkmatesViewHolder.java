package ch.enyoholali.openclassrooms.go4lunch.views;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import ch.enyoholali.openclassrooms.go4lunch.R;
import ch.enyoholali.openclassrooms.go4lunch.data.DataSingleton;
import ch.enyoholali.openclassrooms.go4lunch.models.firebase.User;
import ch.enyoholali.openclassrooms.go4lunch.models.googleapi.placesdetails.PlaceDetails;

public class WorkmatesViewHolder extends ViewHolder {

    @BindView(R.id.fragment_workmates_list_item_image)
    ImageView mImageView;
    @BindView(R.id.fragment_workmates_list_item_text)
    TextView mTextView;

    public WorkmatesViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    public void updateWithUser(User user, RequestManager glide){
        List<PlaceDetails> placeDetailsList= DataSingleton.getInstance().getPlaceDetailsList();
        String restaurant_name=null;
        String eatType;
        if(user.getRestaurantId()!=null){
            for(int i=0;i<placeDetailsList.size();i++){
                if(placeDetailsList.get(i).getResult().getPlaceId().equals(user.getRestaurantId()))
                    restaurant_name=placeDetailsList.get(i).getResult().getName();

            }
        }

        if(restaurant_name!=null){

            mTextView.setText(String.format(Locale.US,"%s",user.getUsername()+ " "+R.string.workmate_has_decides));

        }
        mTextView.setText(String.format(Locale.US,"%s",user.getUsername() +" "+R.string.workmate_has_not_decided));
        if(user.getUrlPicture()!=null)
            glide.load(user.getUrlPicture()).apply(RequestOptions.circleCropTransform()).into(mImageView);


    }
}
