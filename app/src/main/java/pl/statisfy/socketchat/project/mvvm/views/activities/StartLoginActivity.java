package pl.statisfy.socketchat.project.mvvm.views.activities;

import androidx.fragment.app.Fragment;

import pl.statisfy.socketchat.project.mvvm.views.fragments.LoginFragment;

public class StartLoginActivity extends FullScreenActivity {

    private static final String TAG = StartLoginActivity.class.getSimpleName();

    @Override
    public Fragment createFragment() {
        return LoginFragment.newInstance();
    }
}
