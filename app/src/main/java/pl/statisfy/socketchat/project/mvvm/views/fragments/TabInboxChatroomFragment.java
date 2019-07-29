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
import pl.statisfy.socketchat.R2;
import pl.statisfy.socketchat.project.model.Chatroom;
import pl.statisfy.socketchat.project.model.User;
import pl.statisfy.socketchat.project.mvvm.repository.UserDataManager;
import pl.statisfy.socketchat.project.mvvm.viewmodels.ChatroomViewModel;
import pl.statisfy.socketchat.project.mvvm.views.activities.BottomNavigationActivity;
import pl.statisfy.socketchat.project.mvvm.views.activities.InboxMessagesActivity;
import pl.statisfy.socketchat.project.mvvm.views.adapters.Inbox.InboxChatroomAdapter;

public class TabInboxChatroomFragment extends BaseFragment implements InboxChatroomAdapter.ChatroomClickListener {

    @BindView(R2.id.fragment_inbox_TextView) TextView noResultTextView;
    @BindView(R2.id.fragment_inbox_recyclerView) RecyclerView recyclerView;

    private static final String TAG = TabInboxChatroomFragment.class.getSimpleName();

    private Unbinder mUnbinder;
    private InboxChatroomAdapter adapter;
    private String currentUserEmail;
    private String currentUserName;
    private ChatroomViewModel viewModel;

    public static TabInboxChatroomFragment newInstance(){
        return new TabInboxChatroomFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(ChatroomViewModel.class);

        viewModel.getChatrooms().observe(getViewLifecycleOwner(), chatrooms -> {
            if(chatrooms.isEmpty()){
                recyclerView.setVisibility(View.GONE);
                noResultTextView.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                noResultTextView.setVisibility(View.GONE);
            }
            adapter.setChatroomList(chatrooms);
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R2.layout.fragment_inbox, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        currentUserEmail = UserDataManager.getInstance().getUserEmail();
        currentUserName = UserDataManager.getInstance().getUserName();

        adapter = new InboxChatroomAdapter((BottomNavigationActivity) getActivity(), currentUserEmail,this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    public void onClicked(Chatroom chatroom) {
        User user = new User(chatroom.getFriendEmail(), chatroom.getFriendPicture(), chatroom.getFriendName());

        Intent intent = InboxMessagesActivity.newInstance(getActivity(), user);
        startActivity(intent);
        getActivity().overridePendingTransition(0, 0);
    }


}
