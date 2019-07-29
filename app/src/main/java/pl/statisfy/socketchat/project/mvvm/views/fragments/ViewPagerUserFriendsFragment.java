package pl.statisfy.socketchat.project.mvvm.views.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import pl.statisfy.socketchat.R;
import pl.statisfy.socketchat.R2;
import pl.statisfy.socketchat.project.mvvm.views.activities.BottomNavigationActivity;
import pl.statisfy.socketchat.project.mvvm.views.activities.InboxMessagesActivity;
import pl.statisfy.socketchat.project.model.User;
import pl.statisfy.socketchat.project.mvvm.viewmodels.UserFriendsViewModel;
import pl.statisfy.socketchat.project.mvvm.views.adapters.FriendList.UserFriendListAdapter;

public class ViewPagerUserFriendsFragment extends BaseFragment implements UserFriendListAdapter.OnCancelFriendListener {

    @BindView(R2.id.fragment_friendlist_recyclerView) RecyclerView recyclerView;
    @BindView(R2.id.fragment_friendlist_nousersfound_tv) TextView noFriendsFound;

    private static final String TAG = ViewPagerUserFriendsFragment.class.getSimpleName();
    private Unbinder binder;
    private UserFriendListAdapter adapter;
    private UserFriendsViewModel viewModel;

    public static ViewPagerUserFriendsFragment newInstance(){
        return new ViewPagerUserFriendsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_userfriends, container, false);
        binder = ButterKnife.bind(this, view);

        adapter = new UserFriendListAdapter((BottomNavigationActivity) getActivity(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(UserFriendsViewModel.class);

        viewModel.getFriends().observe(getViewLifecycleOwner(), getFriendResult -> {
            if(getFriendResult.getFriendsList().isEmpty()){
                recyclerView.setVisibility(View.GONE);
                noFriendsFound.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                noFriendsFound.setVisibility(View.GONE);
            }
            adapter.setFriendList(getFriendResult.getFriendsList());
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binder.unbind();
    }

    @Override
    public void onClickedCancel(User user) {
        viewModel.removeFriend(user);
    }

    @Override
    public void onClickedUser(User user) {
        Intent intent = InboxMessagesActivity.newInstance(getActivity(), user);
        startActivity(intent);
        getActivity().overridePendingTransition(0, 0);
    }
}
