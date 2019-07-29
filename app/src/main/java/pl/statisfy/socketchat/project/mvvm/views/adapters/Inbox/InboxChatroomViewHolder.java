package pl.statisfy.socketchat.project.mvvm.views.adapters.Inbox;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.statisfy.socketchat.R2;
import pl.statisfy.socketchat.project.model.Chatroom;

public class InboxChatroomViewHolder extends RecyclerView.ViewHolder {

    @BindView(R2.id.list_chatrooms_FriendPicture) RoundedImageView friendPictureIv;
    @BindView(R2.id.list_chatrooms_UserName) TextView friendName;
    @BindView(R2.id.list_chatrooms_LastMessage) TextView lastMessage;
    @BindView(R2.id.list_chatrooms_message) ImageView newMessageIcon;

    public InboxChatroomViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void populate(Chatroom chatroom, String currentUserEmail){
        itemView.setTag(chatroom);

        friendName.setText(chatroom.getFriendName());

        String lastMessageSent = chatroom.getLastMessage();

        if (lastMessageSent.length() > 50){
            lastMessageSent = lastMessageSent.substring(0,50) + "...";
        }
        if(!chatroom.isSentLastMessage()){
            lastMessageSent = lastMessageSent + " (not sent)";
        }

        if(chatroom.getLastMessageSenderEmail().equals(currentUserEmail)){
            lastMessageSent = "Me: " + lastMessageSent;
        }

        if(!chatroom.isLastMessageRead()){
            newMessageIcon.setVisibility(View.VISIBLE);
        } else {
            newMessageIcon.setVisibility(View.GONE);
        }

        lastMessage.setText(lastMessageSent);

        Picasso.get().load(chatroom.getFriendPicture()).into(friendPictureIv);
    }
}
