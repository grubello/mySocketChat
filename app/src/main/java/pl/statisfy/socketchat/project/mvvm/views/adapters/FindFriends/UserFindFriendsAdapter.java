package pl.statisfy.socketchat.project.mvvm.views.adapters.FindFriends;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pl.statisfy.socketchat.R;
import pl.statisfy.socketchat.project.model.User;
import pl.statisfy.socketchat.project.mvvm.views.activities.BottomNavigationActivity;

public class UserFindFriendsAdapter extends RecyclerView.Adapter {

    private BottomNavigationActivity activity;
    private List<User> userlist;
    private LayoutInflater inflater;
    private AddOrCancelRequestListener addOrCancelRequestListener;

    private HashMap<String,User> friendRequestSentMap;
    private HashMap<String,User> friendRequestReceivedMap;
    private HashMap<String,User> friendsMap;

    public UserFindFriendsAdapter(BottomNavigationActivity activity, AddOrCancelRequestListener addOrCancelRequestListener) {
        this.activity = activity;
        this.addOrCancelRequestListener = addOrCancelRequestListener;
        inflater = activity.getLayoutInflater();
        userlist = new ArrayList<>();
        friendRequestSentMap = new HashMap<>();
        friendRequestReceivedMap = new HashMap<>();
        friendsMap = new HashMap<>();
    }

    public void setUserlist(List<User> userlist) {
        this.userlist.clear();
        this.userlist.addAll(userlist);
        notifyDataSetChanged();
    }

    public void setFriendRequestSentMap(HashMap<String, User> friendRequestSentMap) {
        this.friendRequestSentMap.clear();
        this.friendRequestSentMap.putAll(friendRequestSentMap);
        notifyDataSetChanged();
    }

    public void setFriendRequestReceivedMap(HashMap<String, User> friendRequestReceivedMap) {
        if(friendRequestReceivedMap != null){
            this.friendRequestReceivedMap.clear();
            this.friendRequestReceivedMap.putAll(friendRequestReceivedMap);
        }
        notifyDataSetChanged();
    }

    public void setFriendsMap(HashMap<String, User> friendsMap){
        this.friendsMap.clear();
        this.friendsMap.putAll(friendsMap);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = inflater.inflate(R.layout.item_users, viewGroup, false);
        UserFindFriendsViewHolder findFriendsViewHolder = new UserFindFriendsViewHolder(view);

        findFriendsViewHolder.addFriendIv.setOnClickListener(v -> {
            User user = (User) findFriendsViewHolder.itemView.getTag();
            addOrCancelRequestListener.addOrCancelClicked(user);
        });
        return findFriendsViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ((UserFindFriendsViewHolder) viewHolder).populate(userlist.get(i), friendRequestSentMap, friendRequestReceivedMap, friendsMap);
    }

    @Override
    public int getItemCount() {
        return this.userlist.size();
    }

    public interface AddOrCancelRequestListener {
        void addOrCancelClicked(User user);
    }

}
