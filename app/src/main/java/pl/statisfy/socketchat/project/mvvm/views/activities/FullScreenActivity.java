package pl.statisfy.socketchat.project.mvvm.views.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import pl.statisfy.socketchat.R;

public abstract class FullScreenActivity extends BaseActivity {

    public abstract Fragment createFragment();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen);

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.activity_full_screen_baseFragmentFrameLayout);

        if(fragment == null){
            fragment = createFragment();
            fragmentManager.beginTransaction().add(R.id.activity_full_screen_baseFragmentFrameLayout, fragment).commit();
        }
    }
}
