package ch.enyoholali.openclassrooms.go4lunch.base;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ch.enyoholali.openclassrooms.go4lunch.R;

public abstract class BaseActivity extends AppCompatActivity {
   public ViewBinding binding;

    //--------------------
    // LIFE CYCLE
    // --------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);

       /*binding = initViewBinding();
        View view = binding.getRoot();
        setContentView(view);*/

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
      //  Icepick.saveInstanceState(this, outState);
    }

    // --------------------
    // UI
    // --------------------
    protected void configureToolbar(){

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }
    //Generic activity launcher method
    public void startActivity(Class activity) {
        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }


    //     Some abstract methods.
    public abstract int getActivityLayout();
    public abstract void configureView();
    public abstract ViewBinding initViewBinding();
    public abstract void loadData();

    // --------------------
    // ERROR HANDLER
    // --------------------

    protected OnFailureListener onFailureListener(){

        return e -> Toast.makeText(getApplicationContext(), getString(R.string.error_unknown_error), Toast.LENGTH_LONG).show();

    }

    @Nullable
    public  FirebaseUser getCurrentUser(){ return FirebaseAuth.getInstance().getCurrentUser(); }

    protected Boolean isCurrentUserLogged(){ return (this.getCurrentUser() != null); }






}
