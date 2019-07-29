package pl.statisfy.socketchat.project.mvvm.views.adapters.FriendRequest;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import pl.statisfy.socketchat.R;
import pl.statisfy.socketchat.project.model.User;
import pl.statisfy.socketchat.project.mvvm.views.activities.BottomNavigationActivity;

public class UserFriendRequestAdapter extends RecyclerView.Adapter {

    private BottomNavigationActivity activity;
    private LayoutInflater inflater;
    private List<User> userList;

    private OnAcceptOrDenyFriendHandler acceptOrDenyListener;

    public UserFriendRequestAdapter(BottomNavigationActivity activity, OnAcceptOrDenyFriendHandler listener) {
        this.activity = activity;
        this.inflater = activity.getLayoutInflater();
        this.acceptOrDenyListener = listener;
        userList = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.item_friend_request,viewGroup,false);
        UserFriendRequestViewHolder viewHolder = new UserFriendRequestViewHolder(view);

        viewHolder.acceptIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = (User) viewHolder.itemView.getTag();
                acceptOrDenyListener.onOptionClicked(user, 1);
            }
        });

        viewHolder.cancelIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = (User) viewHolder.itemView.getTag();
                acceptOrDenyListener.onOptionClicked(user, 0);
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ((UserFriendRequestViewHolder) viewHolder).populate(userList.get(i));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void setUserList(List<User> userList) {
        if(userList != null){
            this.userList.clear();
            this.userList.addAll(userList);
        }
        notifyDataSetChanged();
    }

    public interface OnAcceptOrDenyFriendHandler {
        void onOptionClicked(User user, int requestCode);
    }
}
