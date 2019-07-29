package pl.statisfy.socketchat.project.mvvm.views.adapters.FriendList;

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

public class UserFriendListAdapter extends RecyclerView.Adapter {

    private static final String TAG = UserFriendListAdapter.class.getSimpleName();

    List<User> friendList;
    LayoutInflater inflater;
    private OnCancelFriendListener clickFriendListener;

    public UserFriendListAdapter(BottomNavigationActivity activity, OnCancelFriendListener clickFriendListener){
        this.inflater = activity.getLayoutInflater();
        this.clickFriendListener = clickFriendListener;
        friendList = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = inflater.inflate(R.layout.item_friends, viewGroup, false);
        UserFriendListViewHolder viewHolder = new UserFriendListViewHolder(view);


        viewHolder.cancelFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                User user = (User) viewHolder.itemView.getTag();
                clickFriendListener.onClickedCancel(user);
            }
        });

        viewHolder.listFriendLinearLayoutToClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = (User) viewHolder.itemView.getTag();
                clickFriendListener.onClickedUser(user);
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ((UserFriendListViewHolder) viewHolder).populate(friendList.get(i));
    }

    @Override
    public int getItemCount() {
        return this.friendList.size();
    }

    public void setFriendList(List<User> friendList){
        this.friendList.clear();
        this.friendList.addAll(friendList);
        notifyDataSetChanged();
    }

    public interface OnCancelFriendListener{
        void onClickedCancel(User user);
        void onClickedUser(User user);
    }
}
