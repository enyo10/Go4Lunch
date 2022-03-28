package ch.enyoholali.openclassrooms.go4lunch.auth;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Objects;

import ch.enyoholali.openclassrooms.go4lunch.R;
import ch.enyoholali.openclassrooms.go4lunch.api.UserHelper;
import ch.enyoholali.openclassrooms.go4lunch.base.BaseActivity;
import ch.enyoholali.openclassrooms.go4lunch.databinding.ActivityProfileBinding;
import ch.enyoholali.openclassrooms.go4lunch.models.firebase.User;

public class ProfileActivity extends BaseActivity<ActivityProfileBinding> {
    //FOR DATA
    // 2 - Identify each Http Request
    private static final int SIGN_OUT_TASK = 10;
    private static final int DELETE_USER_TASK = 20;
    // Creating identifier to identify REST REQUEST (Update username)
    private static final int UPDATE_USERNAME = 30;


    @Override
    public void configureView() {
        this.configureToolbar();
        this.updateUIWhenCreating();

        binding.profileActivityButtonUpdate.setOnClickListener(view -> onClickUpdateButton());

        binding.profileActivityButtonSignOut.setOnClickListener(view -> onClickSignOutButton());

        binding.profileActivityButtonDelete.setOnClickListener(view -> onClickDeleteButton());
    }



    @Override
    public void loadData() {

    }

    @Override
    protected ActivityProfileBinding getViewBinding() {
        return ActivityProfileBinding.inflate(getLayoutInflater());
    }


    // --------------------
    // ACTIONS
    // --------------------

    public void onClickUpdateButton() {

        this.updateUsernameInFirebase();
    }


    public void onClickSignOutButton() {
        this.signOutUserFromFirebase();
    }


    public void onClickDeleteButton() {

        new AlertDialog.Builder(this)

                .setMessage(R.string.popup_message_confirmation_delete_account)
                .setPositiveButton(R.string.popup_message_choice_yes, (dialogInterface, i) -> deleteUserFromFirebase())
                .setNegativeButton(R.string.popup_message_choice_no, null)
                .show();

    }


    // --------------------
    // REST REQUESTS
    // --------------------

    private void signOutUserFromFirebase() {

        AuthUI.getInstance()
                .signOut(this)
                .addOnSuccessListener(this, this.updateUIAfterRESTRequestsCompleted(SIGN_OUT_TASK));

    }


    private void deleteUserFromFirebase() {

        if (this.getCurrentUser() != null) {

            //4 - We also delete user from firestore storage

            UserHelper.deleteUser(this.getCurrentUser().getUid()).addOnFailureListener(this.onFailureListener());
            AuthUI.getInstance()
                    .delete(this)
                    .addOnSuccessListener(this, this.updateUIAfterRESTRequestsCompleted(DELETE_USER_TASK));

        }

    }


    // 3 - Update User Username

    private void updateUsernameInFirebase() {

        binding.profileActivityProgressBar.setVisibility(View.VISIBLE);

        //String username = this.mTextInputEditTextUsername.getText().toString();
        String username = Objects.requireNonNull(binding.profileActivityEditTextUsername.getText()).toString();


        if (this.getCurrentUser() != null) {
            if (!username.isEmpty() && !username.equals(getString(R.string.info_no_username_found))) {
                UserHelper.updateUsername(username, this.getCurrentUser().getUid()).addOnFailureListener(this.onFailureListener()).addOnSuccessListener(this.updateUIAfterRESTRequestsCompleted(UPDATE_USERNAME));

            }
        }

    }


       // --------------------
    // UI
    // --------------------

    private void updateUIWhenCreating() {
        ImageView imageView = binding.profileActivityImageviewProfile;

        if (this.getCurrentUser() != null) {

            //Get picture URL from Firebase
            if (this.getCurrentUser().getPhotoUrl() != null) {
                Glide.with(this)
                        .load(this.getCurrentUser().getPhotoUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(imageView);

            }
            //Get email & username from Firebase
            String email = TextUtils.isEmpty(this.getCurrentUser().getEmail()) ? getString(R.string.info_no_email_found) : this.getCurrentUser().getEmail();
            //Update views with data
            binding.profileActivityTextViewEmail.setText(email);

            // 5 - Get additional data from Fire store
            UserHelper.getUser(this.getCurrentUser().getUid()).addOnSuccessListener(documentSnapshot -> {

                User currentUser = documentSnapshot.toObject(User.class);
                assert currentUser != null;
                String username = TextUtils.isEmpty(currentUser.getUsername()) ? getString(R.string.info_no_username_found) : currentUser.getUsername();

              //  mCheckBoxIsMentor.setChecked(currentUser.getIsMentor());
                binding.profileActivityEditTextUsername.setText(username);
            });
        }
    }


    private OnSuccessListener<Void> updateUIAfterRESTRequestsCompleted(final int origin) {

        return aVoid -> {

            switch (origin) {
                case UPDATE_USERNAME:
                    binding.profileActivityProgressBar.setVisibility(View.INVISIBLE);
                    break;

                case SIGN_OUT_TASK:

                case DELETE_USER_TASK:
                    finish();
                    break;

                default:
                    break;
            }
        };
    }
}
