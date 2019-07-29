package pl.statisfy.socketchat.project.mvvm.views.adapters;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import pl.statisfy.socketchat.project.mvvm.views.fragments.ViewPagerFindFriendsFragment;
import pl.statisfy.socketchat.project.mvvm.views.fragments.ViewPagerFriendRequestFragment;
import pl.statisfy.socketchat.project.mvvm.views.fragments.ViewPagerUserFriendsFragment;

public class FriendsFragmentViewPagerAdapter extends FragmentStatePagerAdapter {

    public FriendsFragmentViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch(i){
            case 0:
                return ViewPagerUserFriendsFragment.newInstance();
            case 1:
                return ViewPagerFriendRequestFragment.newInstance();
            case 2:
                return ViewPagerFindFriendsFragment.newInstance();
        }

        return null;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        switch (position){
            case 0:
                return "Friends";
            case 1:
                return "Friend requests";
            case 2:
                return "Find friends";
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
