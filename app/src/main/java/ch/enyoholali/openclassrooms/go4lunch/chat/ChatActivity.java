package ch.enyoholali.openclassrooms.go4lunch.chat;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import butterknife.BindView;
import butterknife.OnClick;
import ch.enyoholali.openclassrooms.go4lunch.R;
import ch.enyoholali.openclassrooms.go4lunch.api.MessageHelper;
import ch.enyoholali.openclassrooms.go4lunch.api.UserHelper;
import ch.enyoholali.openclassrooms.go4lunch.base.BaseActivity;
import ch.enyoholali.openclassrooms.go4lunch.databinding.ActivityChatBinding;
import ch.enyoholali.openclassrooms.go4lunch.models.chat.Message;
import ch.enyoholali.openclassrooms.go4lunch.models.firebase.User;

public class ChatActivity extends BaseActivity<ActivityChatBinding> implements ChatAdapter.Listener {


        // FOR DESIGN
        // 1 - Getting all views needed
        @BindView(R.id.activity_chat_recycler_view)
        RecyclerView recyclerView;
        @BindView(R.id.activity_chat_text_view_recycler_view_empty)
        TextView textViewRecyclerViewEmpty;
        @BindView(R.id.activity_chat_message_edit_text)
        TextInputEditText editTextMessage;
        @BindView(R.id.activity_chat_image_chosen_preview)
        ImageView imageViewPreview;

        // FOR DATA
        // 2 - Declaring Adapter and data
        private ChatAdapter mChatAdapter;
        @Nullable
        private User modelCurrentUser;
        private String currentChatName;

        // STATIC DATA FOR CHAT (3)
        private static final String CHAT_NAME_ANDROID = "android";
        private static final String CHAT_NAME_BUG = "bug";
        private static final String CHAT_NAME_FIREBASE = "firebase";

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            this.configureRecyclerView(CHAT_NAME_ANDROID);
            this.configureToolbar();
            this.getCurrentUserFromFirestore();
        }



    @Override
    public void configureView() {

    }



    @Override
    public void loadData() {

    }

    @Override
    protected ActivityChatBinding getViewBinding() {
        return ActivityChatBinding.inflate(getLayoutInflater());
    }

    // --------------------
        // ACTIONS
        // --------------------

    @OnClick(R.id.activity_chat_send_button)
    public void onClickSendMessage() {
        // 1 - Check if text field is not empty and current user properly downloaded from Firestore
        if (!TextUtils.isEmpty(editTextMessage.getText()) && modelCurrentUser != null){
            // 2 - Create a new Message to Firestore
            MessageHelper.createMessageForChat(editTextMessage.getText().toString(), this.currentChatName, modelCurrentUser).addOnFailureListener(this.onFailureListener());
            // 3 - Reset text field
            this.editTextMessage.setText("");
        }
    }

    @OnClick({ R.id.activity_chat_android_chat_button, R.id.activity_chat_firebase_chat_button, R.id.activity_chat_bug_chat_button})
    public void onClickChatButtons(ImageButton imageButton) {
            // 8 - Re-Configure the RecyclerView depending chosen chat
            switch (Integer.valueOf(imageButton.getTag().toString())){
                case 10:
                    this.configureRecyclerView(CHAT_NAME_ANDROID);
                    break;
                case 20:
                    this.configureRecyclerView(CHAT_NAME_FIREBASE);
                    break;
                case 30:
                    this.configureRecyclerView(CHAT_NAME_BUG);
                    break;
            }
        }

        @OnClick(R.id.activity_chat_add_file_button)
        public void onClickAddFile() { }

        // --------------------
        // REST REQUESTS
        // --------------------
        // 4 - Get Current User from Firestore
        private void getCurrentUserFromFirestore(){
            UserHelper.getUser(getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    modelCurrentUser = documentSnapshot.toObject(User.class);
                }
            });
        }

        // --------------------
        // UI
        // --------------------
        // 5 - Configure RecyclerView with a Query
        private void configureRecyclerView(String chatName){
            //Track current chat name
            this.currentChatName = chatName;
            //Configure Adapter & RecyclerView
            this.mChatAdapter = new ChatAdapter(generateOptionsForAdapter(MessageHelper.getGetAllMessagesForChat(this.currentChatName)), Glide.with(this), this, this.getCurrentUser().getUid());
            mChatAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    recyclerView.smoothScrollToPosition(mChatAdapter.getItemCount()); // Scroll to bottom on new messages
                }
            });
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(this.mChatAdapter);
        }

        // 6 - Create options for RecyclerView from a Query
        private FirestoreRecyclerOptions<Message> generateOptionsForAdapter(Query query){
            return new FirestoreRecyclerOptions.Builder<Message>()
                    .setQuery(query, Message.class)
                    .setLifecycleOwner(this)
                    .build();
        }

        // --------------------
        // CALLBACK
        // --------------------

        @Override
        public void onDataChanged() {
            // 7 - Show TextView in case RecyclerView is empty
            textViewRecyclerViewEmpty.setVisibility(this.mChatAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
        }


    }

