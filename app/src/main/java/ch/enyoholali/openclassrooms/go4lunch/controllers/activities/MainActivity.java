package ch.enyoholali.openclassrooms.go4lunch.controllers.activities;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewbinding.ViewBinding;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import ch.enyoholali.openclassrooms.go4lunch.R;
import ch.enyoholali.openclassrooms.go4lunch.api.UserHelper;
import ch.enyoholali.openclassrooms.go4lunch.base.BaseActivity;
import ch.enyoholali.openclassrooms.go4lunch.databinding.ActivityMainBinding;

public class MainActivity extends BaseActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private ActivityMainBinding binding;

    // [START auth_fui_create_launcher]
    // See: https://developer.android.com/training/basics/intents/result
    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            new ActivityResultCallback<FirebaseAuthUIAuthenticationResult>() {
                @Override
                public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {
                    onSignInResult(result);
                }
            }
    );
    // [END auth_fui_create_launcher]

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseApp.initializeApp(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        configureToolbar();

        binding.mainActivityButtonLogin.setOnClickListener(view1 -> onClickLoginButton());


        Log.d(TAG, " In on Create");

    }

    // [START auth_fui_result]
    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
        /*if (result.getResultCode() == RESULT_OK) {
            // Successfully signed in
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            // ...
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
        }*/

        if (result.getResultCode()==RESULT_OK){ // SUCCESS
            this.createUserInFirestore();

            showSnackBar(binding.mainActivityCoordinatorLayout,getString(R.string.connection_succeed));

        }else {
            if(response==null){
                showSnackBar(binding.mainActivityCoordinatorLayout,getString(R.string.error_authentication_canceled));
            }else if (Objects.requireNonNull(response.getError()).getErrorCode()== ErrorCodes.NO_NETWORK){
                showSnackBar(binding.mainActivityCoordinatorLayout,getString(R.string.error_no_internet));
            }else if (response.getError().getErrorCode()==ErrorCodes.UNKNOWN_ERROR){
                showSnackBar(binding.mainActivityCoordinatorLayout,getString(R.string.error_unknown_error));

            }
        }
    }
    // [END auth_fui_result]


    //FOR DATA
    // 1 - Identifier for Sign-In Activity
    private static final int RC_SIGN_IN = 123;


    @Override
    public int getActivityLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void configureView() {

    }

    @Override
    public ViewBinding initViewBinding() {
        return ActivityMainBinding.inflate(getLayoutInflater());
    }

    @Override
    public void loadData() {

    }

    // --------------------
    // ACTIONS
    // --------------------


   // @OnClick(R.id.main_activity_button_login)
    public void onClickLoginButton() {
        Log.d(TAG, " in on button clicked.");
        // 3 - Launch Sign-In Activity when user clicked on Login Button
        if (this.isCurrentUserLogged()){

            this.startWelcomeActivity();

        } else {
           // this.startSignInActivity();
            this.createSignIntent();
        }
    }

    // --------------------
    // UI
    // --------------------

    // 2 - Show Snack Bar with a message
    private void showSnackBar(CoordinatorLayout coordinatorLayout, String message){
        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT).show();
    }

  /*  @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Handle SignIn Activity response on activity result.
        this.handleResponseAfterSignIn(requestCode,resultCode,data);

    }*/

    //  Update UI when activity is resuming
    private void updateUIWhenResuming(){
       //this.mButtonLogin.setText(this.isCurrentUserLogged() ? getString(R.string.button_login_text_logged) : getString(R.string.button_login_text_not_logged));
        if(this.isCurrentUserLogged()){
            this.startWelcomeActivity();
        }
    }

    // --------------------
    // NAVIGATION
    // --------------------
    // 2 - Launch Sign-In Activity
    private void startSignInActivity(){
        Log.i(TAG, "in start sign in activity Method");
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTheme(R.style.LoginTheme)
                        .setAvailableProviders(
                                Arrays.asList(
                                        new AuthUI.IdpConfig.EmailBuilder().build()
                                       ,  new AuthUI.IdpConfig.GoogleBuilder().build()
                                        ,  new AuthUI.IdpConfig.FacebookBuilder().build()
                                        )
                                                )

                        .setIsSmartLockEnabled(false, true)
                        .setLogo(R.drawable.logo_go_for_lunch144)
                       // .setTheme(R.style.LoginTheme)
                        .build(),
                RC_SIGN_IN);
    }

    private void createSignIntent(){
        Intent signInIntent= AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setTheme(R.style.LoginTheme)
                .setAvailableProviders(
                        Arrays.asList(
                                new AuthUI.IdpConfig.EmailBuilder().build()
                                ,  new AuthUI.IdpConfig.GoogleBuilder().build()
                                ,  new AuthUI.IdpConfig.FacebookBuilder().build()
                        )
                )

                .setIsSmartLockEnabled(false, true)
                .setLogo(R.drawable.logo_go_for_lunch144)
                // .setTheme(R.style.LoginTheme)
                .build();
        signInLauncher.launch(signInIntent);

    }


    /**
     * This method call the Welcome activity.
     */
    private void startWelcomeActivity(){
        Intent intent =new Intent(this, WelcomeActivity.class);
        startActivity(intent);

    }



    // -------------------------
    // REST REQUEST FIRE BASE
    // ------------------------

    private void createUserInFirestore(){

        if (this.getCurrentUser() != null){

            String urlPicture = (this.getCurrentUser().getPhotoUrl() != null) ? this.getCurrentUser().getPhotoUrl().toString() : null;
            String username = this.getCurrentUser().getDisplayName();
            String uid = this.getCurrentUser().getUid();
            String email=this.getCurrentUser().getEmail();

            UserHelper.createUser(uid, username, urlPicture,email).addOnFailureListener(this.onFailureListener());
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

                showSnackBar(binding.mainActivityCoordinatorLayout,getString(R.string.connection_succeed));

            }else {
                if(response==null){
                    showSnackBar(binding.mainActivityCoordinatorLayout,getString(R.string.error_authentication_canceled));
                }else if (Objects.requireNonNull(response.getError()).getErrorCode()== ErrorCodes.NO_NETWORK){
                    showSnackBar(binding.mainActivityCoordinatorLayout,getString(R.string.error_no_internet));
                }else if (response.getError().getErrorCode()==ErrorCodes.UNKNOWN_ERROR){
                    showSnackBar(binding.mainActivityCoordinatorLayout,getString(R.string.error_unknown_error));

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
