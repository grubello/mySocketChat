package pl.statisfy.socketchat.project.mvvm.views.fragments;

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
import pl.statisfy.socketchat.project.model.User;
import pl.statisfy.socketchat.project.mvvm.viewmodels.UserFriendsViewModel;
import pl.statisfy.socketchat.project.mvvm.views.adapters.FriendRequest.UserFriendRequestAdapter;

public class ViewPagerFriendRequestFragment extends BaseFragment implements UserFriendRequestAdapter.OnAcceptOrDenyFriendHandler {

    @BindView(R2.id.fragment_friendrequest_recyclerView)
    RecyclerView recyclerView;

    @BindView(R2.id.fragment_friendrequest_nousersfound_tv)
    TextView noResultTextView;

    private static final String TAG = ViewPagerFriendRequestFragment.class.getSimpleName();
    private Unbinder unbinder;
    private UserFriendRequestAdapter adapter;
    private UserFriendsViewModel viewModel;

    public static ViewPagerFriendRequestFragment newInstance(){
        return new ViewPagerFriendRequestFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(UserFriendsViewModel.class);

        viewModel.getRequestReceived().observe(getViewLifecycleOwner(), requestReceivedResult -> {
            adapter.setUserList(requestReceivedResult.requestReceivedList);
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friendrequest, container, false);
        unbinder = ButterKnife.bind(this, view);

        adapter = new UserFriendRequestAdapter((BottomNavigationActivity) getActivity(),this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onOptionClicked(User user, int requestCode) {
        viewModel.acceptOrDenyFriendRequest(user, requestCode);
    }
}


