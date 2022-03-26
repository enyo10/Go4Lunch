package ch.enyoholali.openclassrooms.go4lunch.views;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import ch.enyoholali.openclassrooms.go4lunch.R;
import ch.enyoholali.openclassrooms.go4lunch.models.firebase.User;
import ch.enyoholali.openclassrooms.go4lunch.models.googleapi.placesdetails.PlaceDetails;

public class WorkmatesViewHolder extends ViewHolder {
    private static final String TAG= WorkmatesViewHolder.class.getSimpleName();

    @BindView(R.id.fragment_workmates_list_item_image)
    ImageView mImageView;
    @BindView(R.id.fragment_workmates_list_item_text)
    TextView mTextView;

    public WorkmatesViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    public void updateWithUser(User user, RequestManager glide, Context context,List<PlaceDetails>placeDetailsList,int tag){

        String restaurant_name=null;

        if(user.getRestaurantId()!=null){
            Log.d(TAG," restaurant id not null");
            Log.d(TAG, " place details list size  " +placeDetailsList.size());
            for(int i=0;i<placeDetailsList.size();i++){
              //  Log.d(TAG, " id : "+user.getRestaurantId() + "  place id "+ placeDetailsList.get(i).getResult().getPlaceId());
                if(user.getRestaurantId().equals(placeDetailsList.get(i).getResult().getPlaceId())) {

                    restaurant_name = placeDetailsList.get(i).getResult().getName();
                    Log.i(TAG, "matching  user-Rest-id " + user.getRestaurantId() + " placeId " + placeDetailsList.get(i).getResult().getName());
                }

            }
        }

      /*  if(restaurant_name!=null){

            mTextView.setText(String.format(Locale.US,"%s",user.getUsername()+ " "+context.getResources().
                    getString(R.string.workmate_has_decides)+ " "+restaurant_name));
            mTextView.setTextSize(11);

        }*/
      String value;
      if(tag==1){
        value= !TextUtils.isEmpty(restaurant_name)?String.format(Locale.US,"%s",user.getUsername()+ " "+context.getResources().
               getString(R.string.workmate_has_decides)+ " "+restaurant_name): String.format(Locale.US,"%s",user.getUsername() +" "+context.getResources().getString(R.string.workmate_has_not_decided));}
      else{
          value=String.format(Locale.US,"%s",user.getUsername()+ "  "+context.getResources().getString(R.string.is_joining));
      }
        mTextView.setText(value);

      //  String.format(Locale.US,"%s",user.getUsername() +" "+context.getResources().getString(R.string.workmate_has_not_decided));
        if(user.getUrlPicture()!=null)
            glide.load(user.getUrlPicture()).apply(RequestOptions.circleCropTransform()).into(mImageView);


    }
}
