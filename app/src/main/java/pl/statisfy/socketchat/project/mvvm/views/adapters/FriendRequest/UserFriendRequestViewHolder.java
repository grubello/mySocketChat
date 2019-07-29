package pl.statisfy.socketchat.project.mvvm.views.adapters.FriendRequest;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.statisfy.socketchat.R2;
import pl.statisfy.socketchat.project.model.User;

public class UserFriendRequestViewHolder extends RecyclerView.ViewHolder {

    @BindView(R2.id.listFriendRequestUserPicture)
    ImageView userPicture;

    @BindView(R2.id.listFriendRequestUserName)
    TextView userName;

    @BindView(R2.id.listFriendRequestAcceptRequest)
    ImageView acceptIv;

    @BindView(R2.id.listFriendRequestCancelRequest)
    ImageView cancelIv;

    public UserFriendRequestViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    public void populate(User user){
        itemView.setTag(user);
        userName.setText(user.getUserName());
        if (!user.getUserPicture().equals("")) Picasso.get().load(user.getUserPicture()).into(userPicture);
    }


}
