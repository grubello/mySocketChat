package pl.statisfy.socketchat.project.mvvm.views.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import pl.statisfy.socketchat.R;
import pl.statisfy.socketchat.R2;
import pl.statisfy.socketchat.project.mvvm.views.activities.FullScreenActivity;
import pl.statisfy.socketchat.project.interfaces.ActionBarTitleHandler;
import pl.statisfy.socketchat.project.model.Chatroom;
import pl.statisfy.socketchat.project.model.Message;
import pl.statisfy.socketchat.project.mvvm.repository.UserDataManager;
import pl.statisfy.socketchat.project.mvvm.viewmodels.MessagesViewModel;
import pl.statisfy.socketchat.project.utils.Utils;
import pl.statisfy.socketchat.project.mvvm.views.adapters.Messages.InboxMessagesAdapter;

public class InboxMessagesFragment extends BaseFragment {

    @BindView(R2.id.fragment_messages_userPicture) ImageView friendPicture;
    @BindView(R2.id.fragment_messages_userName) TextView friendName;
    @BindView(R2.id.fragment_messages_et_message) EditText messageEditText;
    @BindView(R2.id.fragment_messages_recyclerView) RecyclerView recyclerView;
    @BindView(R2.id.fragment_messages_sendMessage) ImageView sendMessage;

    private static final String TAG = InboxMessagesFragment.class.getSimpleName();
    private Unbinder binder;
    private InboxMessagesAdapter adapter;
    private InputMethodManager inputMethodManager;

    private String friendEmailString;
    private String friendPictureString;
    private String friendNameString;
    private String currentUserEmailString;
    private String currentUserPictureString;
    private ActionBarTitleHandler actionBarTitleHandler;
    boolean isJustSent;
    
    private MessagesViewModel viewModel;

    public static InboxMessagesFragment newInstance(ArrayList<String> friendDetails){
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(Utils.FRIEND_DETAILS, friendDetails);
        InboxMessagesFragment messageFragment = new InboxMessagesFragment();
        messageFragment.setArguments(bundle);
        return messageFragment;
    }

    public void setActionBarTitleHandler(ActionBarTitleHandler actionBarTitleHandler){
        this.actionBarTitleHandler = actionBarTitleHandler;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        ArrayList<String> friendDetails = getArguments().getStringArrayList(Utils.FRIEND_DETAILS);
        friendEmailString = friendDetails.get(0);
        friendPictureString = friendDetails.get(1);
        friendNameString = friendDetails.get(2);

        currentUserEmailString = UserDataManager.getInstance().getUserEmail();
        currentUserPictureString = UserDataManager.getInstance().getUserPhoto();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messages, container, false);
        binder = ButterKnife.bind(this,view);

        actionBarTitleHandler.changeTitle("Chat with " + friendNameString);
        adapter = new InboxMessagesAdapter((FullScreenActivity)getActivity(), currentUserEmailString);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        messageEditText.addTextChangedListener(new TextWatcher() {

            ArrayList dataArray = new ArrayList();

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                dataArray.clear();

                dataArray.add(s.toString());
                dataArray.add(recyclerView.getChildCount());

                //user wrote text but cleared the message and there are some messages present
                if(before > 0 && s.length() == 0 && recyclerView.getChildCount() > 0) {

                    if(isJustSent) return;

                    dataArray.add(true);

                    Message lastMessage = (Message) recyclerView.getChildViewHolder(recyclerView.getChildAt(recyclerView.getChildCount() - 1))
                                .itemView.getTag();

                    dataArray.add(lastMessage.getEmail());
                    dataArray.add(lastMessage.getMessageText());
                    dataArray.add(friendEmailString);

                } else {
                    dataArray.add(false);

                    Chatroom chatroom = new Chatroom(friendPictureString, friendNameString,
                            friendEmailString, s.toString(), currentUserEmailString, true, false);

                    dataArray.add(chatroom);
                }

                if(s.length()>0){
                    if(adapter.getItemCount()>0) recyclerView.smoothScrollToPosition(adapter.getItemCount()-1);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.lastMessage(dataArray);
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(MessagesViewModel.class);
        
        viewModel.getMessagesFromFriend(friendEmailString).observe(this, messages -> {
            if (messages.isEmpty()){
                recyclerView.setVisibility(View.GONE);
                friendName.setVisibility(View.VISIBLE);
                friendPicture.setVisibility(View.VISIBLE);

                Picasso.get().load(friendPictureString).into(friendPicture);
                friendName.setText(friendNameString);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                friendName.setVisibility(View.GONE);
                friendPicture.setVisibility(View.GONE);
            }
            adapter.setMessageList(messages);
            recyclerView.scrollToPosition(adapter.getItemCount()-1);

            viewModel.clearReadMessages(messages);
        });

        viewModel.getTypedLastMessage().observe(this, result -> {
            isJustSent = false;
        });

        viewModel.messagesRead(friendEmailString);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binder.unbind();
    }

    @OnClick (R2.id.fragment_messages_sendMessage)
    public void sendMessage(){
        if (!messageEditText.getText().toString().equals("")){

            viewModel.sendMessage(friendNameString, friendEmailString, friendPictureString, messageEditText.getText().toString());
            isJustSent = true;
            messageEditText.setText("");
            recyclerView.scrollToPosition(adapter.getItemCount()-1);


            //hide & clear messageEditText
            //inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
    }

}

