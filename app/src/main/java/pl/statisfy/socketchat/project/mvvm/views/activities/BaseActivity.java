package pl.statisfy.socketchat.project.mvvm.views.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import pl.statisfy.socketchat.project.utils.Utils;

public abstract class BaseActivity extends AppCompatActivity {

    public abstract Fragment createFragment();
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        SharedPreferences sharedPrefs = getSharedPreferences(Utils.PREFS, MODE_PRIVATE);
        String userEmail = sharedPrefs.getString(Utils.USER_EMAIL,"");

        //check if user logged in
        if(!(this instanceof StartLoginActivity || this instanceof StartRegisterActivity)){
            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    if (firebaseAuth.getCurrentUser() == null){
                        Intent intent = new Intent(getApplicationContext(), StartLoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else if (userEmail.equals("")){
                        firebaseAuth.signOut();
                        finish();
                    }
                }
            };
        }

    }


    @Override
    protected void onStart() {
        super.onStart();
        //register listener in not in Login or Register Activity
        if(!(this instanceof StartLoginActivity || this instanceof StartRegisterActivity)) {
            mAuth.addAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //remove listener if not in Login or Register Activity
        if(!(this instanceof StartLoginActivity || this instanceof StartRegisterActivity)) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
