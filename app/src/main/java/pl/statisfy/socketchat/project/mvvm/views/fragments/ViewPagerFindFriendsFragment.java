package pl.statisfy.socketchat.project.mvvm.views.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import pl.statisfy.socketchat.project.mvvm.views.adapters.FindFriends.UserFindFriendsAdapter;

public class ViewPagerFindFriendsFragment extends BaseFragment implements UserFindFriendsAdapter.AddOrCancelRequestListener {

    private static final String TAG = ViewPagerFindFriendsFragment.class.getSimpleName();

    @BindView(R2.id.fragment_findfriends_et)
    EditText searchBar;

    @BindView(R2.id.fragment_findfriends_recyclerView)
    RecyclerView recyclerView;

    @BindView(R2.id.fragment_findfriends_nousersfound_tv)
    TextView noResultTextView;

    private Unbinder unbinder;
    private UserFindFriendsAdapter adapter;
    private UserFriendsViewModel viewModel;

    public static ViewPagerFindFriendsFragment newInstance(){
        return new ViewPagerFindFriendsFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(UserFriendsViewModel.class);

        viewModel.getFriends().observe(getViewLifecycleOwner(), getFriendResult -> {
            adapter.setFriendsMap(getFriendResult.getFriendsMap());
        });

        viewModel.getAllUsers().observe(getViewLifecycleOwner(), users -> {
            if(users.isEmpty()){
                noResultTextView.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                noResultTextView.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
            adapter.setUserlist(users);
        });

        viewModel.getRequestSent().observe(getViewLifecycleOwner(), stringUserHashMap -> {
            adapter.setFriendRequestSentMap(stringUserHashMap);
        });

        viewModel.getRequestReceived().observe(getViewLifecycleOwner(), result -> {
            adapter.setFriendRequestReceivedMap(result.getRequestReceivedMap());
        });

        viewModel.getSearchData().observe(getViewLifecycleOwner(), users -> {
            if(users.isEmpty()){
                recyclerView.setVisibility(View.GONE);
                noResultTextView.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                noResultTextView.setVisibility(View.GONE);
            }
            adapter.setUserlist(users);
        });

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_findfriends, container, false);
        unbinder = ButterKnife.bind(this,view);

        adapter = new UserFindFriendsAdapter((BottomNavigationActivity) getActivity(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.equals("")) viewModel.searchForUser(s.toString());
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void addOrCancelClicked(User user) {
        viewModel.userClicked(user);
    }

}
