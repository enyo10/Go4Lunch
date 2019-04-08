package ch.enyo.openclassrooms.go4lunch.controllers.activities;


import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.Button;

import com.crashlytics.android.Crashlytics;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.OnClick;
import ch.enyo.openclassrooms.go4lunch.MapsActivity;
import ch.enyo.openclassrooms.go4lunch.R;
import ch.enyo.openclassrooms.go4lunch.api.UserHelper;
import ch.enyo.openclassrooms.go4lunch.auth.ProfileActivity;
import ch.enyo.openclassrooms.go4lunch.base.BaseActivity;

public class MainActivity extends BaseActivity {
    private static final String TAG= MainActivity.class.getSimpleName();

    //FOR DATA
    // 1 - Identifier for Sign-In Activity
    private static final int RC_SIGN_IN = 123;

    @BindView(R.id.main_activity_coordinator_layout)
    CoordinatorLayout mCoordinatorLayour;
    @BindView(R.id.main_activity_button_login)
    Button mButtonLogin;

    @Override
    public int getActivityLayout() {
        return R.layout.activity_main;
    }

    // --------------------
    // ACTIONS
    // --------------------

    @OnClick(R.id.main_activity_button_login)
    public void onClickLoginButton() {
        // 3 - Launch Sign-In Activity when user clicked on Login Button
        if (this.isCurrentUserLogged()){
            Log.i(TAG," Welcome activity call");
          //  this.startProfileActivity();
            this.startWelcomeActivity();
        } else {
            this.startSignInActivity();
        }
    }

    // --------------------
    // UI
    // --------------------

    // 2 - Show Snack Bar with a message
    private void showSnackBar(CoordinatorLayout coordinatorLayout, String message){
        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Handle SignIn Activity response on activity result.
        this.handleResponseAfterSignIn(requestCode,resultCode,data);

    }

    //  Update UI when activity is resuming
    private void updateUIWhenResuming(){
        this.mButtonLogin.setText(this.isCurrentUserLogged() ? getString(R.string.button_login_text_logged) : getString(R.string.button_login_text_not_logged));
    }

    // --------------------
    // NAVIGATION
    // --------------------
    // 2 - Launch Sign-In Activity
    private void startSignInActivity(){
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTheme(R.style.LoginTheme)
                        .setAvailableProviders(
                                Arrays.asList(
                                        new AuthUI.IdpConfig.EmailBuilder().build()
                                    //    ,  new AuthUI.IdpConfig.GoogleBuilder().build()
                                        ,  new AuthUI.IdpConfig.FacebookBuilder().build()
                                        )
                                                )

                        .setIsSmartLockEnabled(false, true)
                        .setLogo(R.drawable.logo_go_for_lunch144)
                       // .setTheme(R.style.LoginTheme)
                        .build(),
                RC_SIGN_IN);
    }

    // 3 - Launching Profile Activity
    private void startProfileActivity(){
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    private void startMapActivity(){
        Intent intent =new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    /**
     * This method call the Welcome activity.
     */
    private void startWelcomeActivity(){
        Intent intent =new Intent(this, WelcomeActivity.class);
        startActivity(intent);

    }

    // --------------------
    // REST REQUEST
    // --------------------

    private void createUserInFirestore(){

        if (this.getCurrentUser() != null){

            String urlPicture = (this.getCurrentUser().getPhotoUrl() != null) ? this.getCurrentUser().getPhotoUrl().toString() : null;
            String username = this.getCurrentUser().getDisplayName();
            String uid = this.getCurrentUser().getUid();

            UserHelper.createUser(uid, username, urlPicture).addOnFailureListener(this.onFailureListener());
        }

    }

    // ------------------------------------
    // UTILS
    // ------------------------------------

    // Method that handles response after SignIn Activity close.
    private void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data){
        IdpResponse response =IdpResponse.fromResultIntent(data);

        if(requestCode==RC_SIGN_IN){
            if (resultCode==RESULT_OK){ // SUCCESS
                this.createUserInFirestore();
                showSnackBar(this.mCoordinatorLayour,getString(R.string.connection_succeed));

            }else {
                if(response==null){
                    showSnackBar(this.mCoordinatorLayour,getString(R.string.error_authentication_canceled));
                }else if (response.getError().getErrorCode()== ErrorCodes.NO_NETWORK){
                    showSnackBar(this.mCoordinatorLayour,getString(R.string.error_no_internet));
                }else if (response.getError().getErrorCode()==ErrorCodes.UNKNOWN_ERROR){
                    showSnackBar(this.mCoordinatorLayour,getString(R.string.error_unknown_error));

                }
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG," On Resume");
        this.updateUIWhenResuming();

    }
}
