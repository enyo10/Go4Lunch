package ch.enyo.openclassrooms.go4lunch.views;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bumptech.glide.RequestManager;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import ch.enyo.openclassrooms.go4lunch.R;
import ch.enyo.openclassrooms.go4lunch.models.firebase.User;

public class WorkmatesViewAdapter extends FirestoreRecyclerAdapter<User,WorkmatesViewHolder> {

    public interface Listener {
        void onDataChanged();
    }

    private Listener callback;
    private RequestManager mManager;


    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public WorkmatesViewAdapter(@NonNull FirestoreRecyclerOptions<User> options, RequestManager glide, Listener callback) {
        super(options);
        this.callback=callback;
        this.mManager=glide;
    }

    @Override
    protected void onBindViewHolder(@NonNull WorkmatesViewHolder holder, int position, @NonNull User user) {
        holder.updateWithUser(user,this.mManager);

    }

    @NonNull
    @Override
    public WorkmatesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new WorkmatesViewHolder((LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.fragment_workmates_list_item, viewGroup, false)));
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        this.callback.onDataChanged();
    }
}
