package pl.statisfy.socketchat.project.mvvm.views.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.statisfy.socketchat.R;
import pl.statisfy.socketchat.R2;
import pl.statisfy.socketchat.project.mvvm.views.fragments.TabFragmentFriends;
import pl.statisfy.socketchat.project.mvvm.views.fragments.TabInboxChatroomFragment;
import pl.statisfy.socketchat.project.mvvm.views.fragments.TabProfileFragment;
import pl.statisfy.socketchat.project.interfaces.ActionBarTitleHandler;
import pl.statisfy.socketchat.project.mvvm.repository.UserDataManager;
import pl.statisfy.socketchat.project.mvvm.viewmodels.BottomNavigationActivityViewModel;

public abstract class BottomNavigationActivity extends BaseActivity implements ActionBarTitleHandler {

    @BindView(R2.id.mainBottomNavigation) AHBottomNavigation mainBottomNavigation;

    private static final String TAG = BottomNavigationActivity.class.getSimpleName();

    private TabInboxChatroomFragment inboxFragment;
    private TabFragmentFriends friendsFragment;
    private TabProfileFragment profileFragment;
    private FragmentManager fm;
    public Fragment activeFragment;
    private UserDataManager userDataManager;

    private BottomNavigationActivityViewModel viewModel;

    public abstract Fragment createFragment();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_bottom_navigation);
        ButterKnife.bind(this);

        userDataManager = UserDataManager.getInstance();
        String userEmail = userDataManager.getUserEmail();

        //setup viewModel
        viewModel = ViewModelProviders.of(this).get(BottomNavigationActivityViewModel.class);
        viewModel.getNewMessagesData().observe(this, countNewMessages -> {
            if (countNewMessages > 0){
                mainBottomNavigation.setNotification(String.valueOf(countNewMessages), 0);
            } else {
                mainBottomNavigation.setNotification("",0);
            }
        });

        //setup BottomNavigation view
        mainBottomNavigation.setOnTabSelectedListener(setOnTabSelectedListener(this));

        AHBottomNavigationItem item1 = new AHBottomNavigationItem("Inbox", R.drawable.ic_vector_inbox );
        AHBottomNavigationItem item2 = new AHBottomNavigationItem("Friends", R.drawable.ic_vector_friends);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem("Profile", R.drawable.ic_vector_profile);
        mainBottomNavigation.addItem(item1);
        mainBottomNavigation.addItem(item2);
        mainBottomNavigation.addItem(item3);

        //setup fragments
        fm = getSupportFragmentManager();
        inboxFragment = TabInboxChatroomFragment.newInstance();
        friendsFragment = TabFragmentFriends.newInstance();
        profileFragment = TabProfileFragment.newInstance();
        setActiveFragment(inboxFragment);

        //if isLoggedIn add starting point fragments to activity
        if(!userEmail.equals("")) {
            fm.beginTransaction().add(R.id.main_container, profileFragment, "3").hide(profileFragment).commit();
            fm.beginTransaction().add(R.id.main_container, friendsFragment, "2").hide(friendsFragment).commit();
            fm.beginTransaction().add(R.id.main_container, inboxFragment, "1").commit();
        }
    }

    public AHBottomNavigation.OnTabSelectedListener setOnTabSelectedListener(BottomNavigationActivity activity) {

        return (position, wasSelected) -> {
            switch (position) {
                case 0:
                    fm.beginTransaction().hide(activity.getActiveFragment()).show(inboxFragment).commit();
                    activity.setActiveFragment(inboxFragment);
                    return true;

                case 1:
                    fm.beginTransaction().hide(activity.getActiveFragment()).show(friendsFragment).commit();
                    activity.setActiveFragment(friendsFragment);
                    return true;

                case 2:
                    fm.beginTransaction().hide(activity.getActiveFragment()).show(profileFragment).commit();
                    activity.setActiveFragment(profileFragment);
                    return true;
            }
            return false;

        };
    }

    public void setActiveFragment(Fragment fragment){
        this.activeFragment = fragment;
        if(fragment instanceof TabInboxChatroomFragment){
            getSupportActionBar().setTitle("Inbox");
        }  else if(fragment instanceof TabFragmentFriends){
            getSupportActionBar().setTitle("Friends");
        } else if (fragment instanceof TabProfileFragment){
            getSupportActionBar().setTitle("Profile");
        }
    }

    public Fragment getActiveFragment(){
        return activeFragment;
    }

}
