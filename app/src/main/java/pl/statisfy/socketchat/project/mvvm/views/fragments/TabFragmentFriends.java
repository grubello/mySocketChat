package pl.statisfy.socketchat.project.mvvm.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import pl.statisfy.socketchat.R;
import pl.statisfy.socketchat.R2;
import pl.statisfy.socketchat.project.mvvm.views.adapters.FriendsFragmentViewPagerAdapter;

public class TabFragmentFriends extends BaseFragment {

    @BindView(R2.id.friendsFragmentTabLayout2) TabLayout tabLayout;
    @BindView(R2.id.friendsFragmentViewPager2) ViewPager viewPager;

    private Unbinder mUnbinder;

    public static TabFragmentFriends newInstance(){
        return new TabFragmentFriends();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends2, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        FriendsFragmentViewPagerAdapter friendsViewPagerAdapter = new FriendsFragmentViewPagerAdapter(getActivity().getSupportFragmentManager());
        viewPager.setAdapter(friendsViewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

}
