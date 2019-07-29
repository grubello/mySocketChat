package pl.statisfy.socketchat.project.mvvm.views.adapters.FindFriends;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.statisfy.socketchat.R;
import pl.statisfy.socketchat.R2;
import pl.statisfy.socketchat.project.model.User;
import pl.statisfy.socketchat.project.utils.Utils;

public class UserFindFriendsViewHolder extends RecyclerView.ViewHolder {

    @BindView(R2.id.listUsersUserPicture)
    ImageView userPictureIv;

    @BindView(R2.id.listUsersAddFriend)
    ImageView addFriendIv;

    @BindView(R2.id.listUsersUserName)
    TextView userName;

    @BindView(R2.id.listUsersUserStatus)
    TextView userStatus;

    public UserFindFriendsViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void populate(User user, HashMap<String, User> friendRequestSentMap, HashMap<String, User> friendRequestReceivedMap, HashMap<String, User> friendsMap){

        itemView.setTag(user);
        userName.setText(user.getUserName());

        if (!user.getUserPicture().equals("")) Picasso.get().load(user.getUserPicture()).into(userPictureIv);

        if (Utils.isIncludedInMap(friendRequestSentMap, user)){
            userStatus.setVisibility(View.VISIBLE);
            userStatus.setText("Friend request sent");
            addFriendIv.setImageResource(R.mipmap.ic_cancelrequest);
        } else if(Utils.isIncludedInMap(friendRequestReceivedMap, user)){
            addFriendIv.setVisibility(View.GONE);
            userStatus.setVisibility(View.VISIBLE);
            userStatus.setText("User sent you an invitation");
        } else if (Utils.isIncludedInMap(friendsMap, user)){
            addFriendIv.setVisibility(View.GONE);
            userStatus.setVisibility(View.VISIBLE);
            userStatus.setText("User is your friend :)");
        } else {
            addFriendIv.setVisibility(View.VISIBLE);
            addFriendIv.setImageResource(R.mipmap.ic_add);
            userStatus.setVisibility(View.GONE);
        }

    }
}
