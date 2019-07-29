package pl.statisfy.socketchat.project.mvvm.views.activities;


import androidx.fragment.app.Fragment;

import pl.statisfy.socketchat.project.mvvm.views.fragments.RegisterFragment;

public class StartRegisterActivity extends FullScreenActivity {

    @Override
    public Fragment createFragment() {
        return RegisterFragment.newInstance();
    }

}
