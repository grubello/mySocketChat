package pl.statisfy.socketchat.project.mvvm.views.adapters.Messages;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.statisfy.socketchat.R2;
import pl.statisfy.socketchat.project.model.Message;

public class InboxMessagesViewHolder extends RecyclerView.ViewHolder {

    private static final String TAG = InboxMessagesViewHolder.class.getSimpleName();

    @BindView(R2.id.list_messages_friendPicture)
    ImageView friendPicture;

    @BindView(R2.id.list_messages_UserPicture)
    ImageView userPicture;

    @BindView(R2.id.list_messages_friendText)
    TextView friendText;

    @BindView(R2.id.list_messages_UserText)
    TextView userText;


    public InboxMessagesViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void populate(Message message, String currentUserEmail){
        itemView.setTag(message);

        //if message from current user
        if (message.getEmail().equals(currentUserEmail)){
            userPicture.setVisibility(View.VISIBLE);
            friendPicture.setVisibility(View.GONE);
            userText.setVisibility(View.VISIBLE);
            friendText.setVisibility(View.GONE);

            userText.setText(message.getMessageText());
            Picasso.get().load(message.getPicture()).into(userPicture);
        }
        //if message from friend
        else {
            friendPicture.setVisibility(View.VISIBLE);
            userPicture.setVisibility(View.GONE);
            friendText.setVisibility(View.VISIBLE);
            userText.setVisibility(View.GONE);

            friendText.setText(message.getMessageText());
            Picasso.get().load(message.getPicture()).into(friendPicture);
        }
    }
}
