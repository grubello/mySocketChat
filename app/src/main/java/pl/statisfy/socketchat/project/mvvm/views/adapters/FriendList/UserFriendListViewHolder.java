package pl.statisfy.socketchat.project.mvvm.views.adapters.FriendList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.statisfy.socketchat.R2;
import pl.statisfy.socketchat.project.model.User;

public class UserFriendListViewHolder extends RecyclerView.ViewHolder {

    private static final String TAG = UserFriendListViewHolder.class.getSimpleName();

    @BindView(R2.id.listFriendsUserPicture)
    ImageView userPicture;

    @BindView(R2.id.listFriendsUsersUserName)
    TextView userName;

    @BindView(R2.id.listFriendsAddCancelFriend)
    ImageView cancelFriend;

    @BindView(R2.id.listFriendLinearLayoutToClick)
    LinearLayout listFriendLinearLayoutToClick;


    public UserFriendListViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    public void populate(User user){
        itemView.setTag(user);

        Picasso.get().load(user.getUserPicture()).into(userPicture);
        userName.setVisibility(View.VISIBLE);
        cancelFriend.setVisibility(View.VISIBLE);
        userName.setText(user.getUserName());
    }

}
