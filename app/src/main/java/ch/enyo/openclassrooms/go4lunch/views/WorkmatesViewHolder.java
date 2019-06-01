package ch.enyo.openclassrooms.go4lunch.views;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;

import butterknife.BindView;
import butterknife.ButterKnife;
import ch.enyo.openclassrooms.go4lunch.R;
import ch.enyo.openclassrooms.go4lunch.models.firebase.User;

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
        mTextView.setText(user.getUsername());
        if(user.getUrlPicture()!=null)
            glide.load(user.getUrlPicture()).apply(RequestOptions.circleCropTransform()).into(mImageView);


    }
}
