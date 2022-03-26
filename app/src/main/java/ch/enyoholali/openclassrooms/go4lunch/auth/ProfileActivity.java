package ch.enyoholali.openclassrooms.go4lunch.auth;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.viewbinding.ViewBinding;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;
import ch.enyoholali.openclassrooms.go4lunch.R;
import ch.enyoholali.openclassrooms.go4lunch.api.UserHelper;
import ch.enyoholali.openclassrooms.go4lunch.base.BaseActivity;
import ch.enyoholali.openclassrooms.go4lunch.models.firebase.User;

public class ProfileActivity extends BaseActivity {
    //FOR DATA
    // 2 - Identify each Http Request
    private static final int SIGN_OUT_TASK = 10;
    private static final int DELETE_USER_TASK = 20;
    // Creating identifier to identify REST REQUEST (Update username)
    private static final int UPDATE_USERNAME = 30;

    // FOR DESIGN
    @BindView(R.id.profile_activity_imageview_profile)
    ImageView mImageViewProfile;
    @BindView(R.id.profile_activity_edit_text_username)
    TextInputEditText mTextInputEditTextUsername;
    @BindView(R.id.profile_activity_text_view_email)
    TextView mTextViewEmail;
    @BindView(R.id.profile_activity_progress_bar)
    ProgressBar mProgressBar;


    @Override
    public int getActivityLayout() {
        return R.layout.activity_profile;
    }

    @Override
    public void configureView() {
        this.configureToolbar();
        this.updateUIWhenCreating();
    }

    @Override
    public ViewBinding initViewBinding() {
        return null;
    }

    @Override
    public void loadData() {

    }


    // --------------------
    // ACTIONS
    // --------------------

    @SuppressLint("NonConstantResourceId")
    @OnClick(R.id.profile_activity_button_update)
    public void onClickUpdateButton() {
        this.updateUsernameInFirebase();
    }


    @OnClick(R.id.profile_activity_button_sign_out)
    public void onClickSignOutButton() {
        this.signOutUserFromFirebase();
    }


    @OnClick(R.id.profile_activity_button_delete)
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

        this.mProgressBar.setVisibility(View.VISIBLE);

        //String username = this.mTextInputEditTextUsername.getText().toString();
        String username = Objects.requireNonNull(this.mTextInputEditTextUsername.getText()).toString();


        if (this.getCurrentUser() != null) {
            if (!username.isEmpty() && !username.equals(getString(R.string.info_no_username_found))) {
                UserHelper.updateUsername(username, this.getCurrentUser().getUid()).addOnFailureListener(this.onFailureListener()).addOnSuccessListener(this.updateUIAfterRESTRequestsCompleted(UPDATE_USERNAME));

            }
        }

    }


    // 2 - Update User Mentor (is or not)

    /*private void updateUserIsMentor() {

        if (this.getCurrentUser() != null) {
            UserHelper.updateIsMentor(this.getCurrentUser().getUid(), this.checkBoxIsMentor.isChecked()).addOnFailureListener(this.onFailureListener());
        }

    }
*/

    // --------------------
    // UI
    // --------------------

    private void updateUIWhenCreating() {

        if (this.getCurrentUser() != null) {

            //Get picture URL from Firebase
            if (this.getCurrentUser().getPhotoUrl() != null) {
                Glide.with(this)
                        .load(this.getCurrentUser().getPhotoUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(mImageViewProfile);

            }
            //Get email & username from Firebase
            String email = TextUtils.isEmpty(this.getCurrentUser().getEmail()) ? getString(R.string.info_no_email_found) : this.getCurrentUser().getEmail();
            //Update views with data
            this.mTextViewEmail.setText(email);

            // 5 - Get additional data from Fire store
            UserHelper.getUser(this.getCurrentUser().getUid()).addOnSuccessListener(documentSnapshot -> {

                User currentUser = documentSnapshot.toObject(User.class);
                assert currentUser != null;
                String username = TextUtils.isEmpty(currentUser.getUsername()) ? getString(R.string.info_no_username_found) : currentUser.getUsername();

              //  mCheckBoxIsMentor.setChecked(currentUser.getIsMentor());
                mTextInputEditTextUsername.setText(username);
            });
        }
    }


    private OnSuccessListener<Void> updateUIAfterRESTRequestsCompleted(final int origin) {

        return aVoid -> {

            switch (origin) {
                case UPDATE_USERNAME:
                    mProgressBar.setVisibility(View.INVISIBLE);
                    break;

                case SIGN_OUT_TASK:
                    finish();
                    break;

                case DELETE_USER_TASK:
                    finish();
                    break;

                default:
                    break;
            }
        };
    }
}
