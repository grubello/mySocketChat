package pl.statisfy.socketchat.project.mvvm.views.activities;

import android.util.Log;

import androidx.fragment.app.Fragment;

import pl.statisfy.socketchat.project.interfaces.ActionBarTitleHandler;
import pl.statisfy.socketchat.project.mvvm.views.fragments.TabFragmentFriends;
import pl.statisfy.socketchat.project.mvvm.views.fragments.TabInboxChatroomFragment;
import pl.statisfy.socketchat.project.mvvm.views.fragments.TabProfileFragment;

public class MainActivity extends BottomNavigationActivity implements ActionBarTitleHandler {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    public Fragment createFragment() {
        return null;
    }


    @Override
    public void changeTitle(String title) {
        Log.i(TAG, "changeTitle: " + title);
        getSupportActionBar().setTitle(title);
    }
}
