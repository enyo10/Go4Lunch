package ch.enyo.openclassrooms.go4lunch.views;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ch.enyo.openclassrooms.go4lunch.R;
import ch.enyo.openclassrooms.go4lunch.models.google.Result;

public class NearbyResultViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = NearbyResultViewHolder.class.getSimpleName();

    @BindView(R.id.fragment_item_name)
    TextView mNameTextView;

    public NearbyResultViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    public void updateWithResult(Result result){
        mNameTextView.setText(result.getName());

        Log.i(TAG, " Update white result.");


    }
}
