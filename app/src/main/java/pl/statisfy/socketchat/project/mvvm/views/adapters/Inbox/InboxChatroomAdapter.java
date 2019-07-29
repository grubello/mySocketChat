package pl.statisfy.socketchat.project.mvvm.views.adapters.Inbox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import pl.statisfy.socketchat.R;
import pl.statisfy.socketchat.project.model.Chatroom;
import pl.statisfy.socketchat.project.mvvm.views.activities.BottomNavigationActivity;

public class InboxChatroomAdapter extends RecyclerView.Adapter {

    private LayoutInflater inflater;
    private ArrayList<Chatroom> chatroomList;
    private ChatroomClickListener chatroomClickListener;
    private String currentUserEmail;

    public InboxChatroomAdapter(BottomNavigationActivity activity, String currentUserEmail,
                                ChatroomClickListener chatroomClickListener) {
        this.inflater = activity.getLayoutInflater();
        this.chatroomClickListener = chatroomClickListener;
        this.chatroomList = new ArrayList<>();
        this.currentUserEmail = currentUserEmail;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.item_chatrooms, viewGroup, false);
        InboxChatroomViewHolder viewHolder = new InboxChatroomViewHolder(view);

        viewHolder.friendPictureIv.setOnClickListener(v ->
                chatroomClickListener.onClicked((Chatroom) viewHolder.itemView.getTag())
        );

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ((InboxChatroomViewHolder) viewHolder).populate(chatroomList.get(i), currentUserEmail);
    }

    @Override
    public int getItemCount() {
        return chatroomList.size();
    }

    public void setChatroomList(List<Chatroom> chatroomList) {
        this.chatroomList.clear();
        this.chatroomList.addAll(chatroomList);
        notifyDataSetChanged();
    }

    public interface ChatroomClickListener{
        void onClicked(Chatroom chatroom);
    }

}
