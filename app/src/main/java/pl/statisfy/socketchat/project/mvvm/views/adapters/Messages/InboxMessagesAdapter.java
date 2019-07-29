package pl.statisfy.socketchat.project.mvvm.views.adapters.Messages;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import pl.statisfy.socketchat.R;
import pl.statisfy.socketchat.project.mvvm.views.activities.FullScreenActivity;
import pl.statisfy.socketchat.project.model.Message;

public class InboxMessagesAdapter extends RecyclerView.Adapter {

    private LayoutInflater inflater;
    private List<Message> messageList;
    private String currentUserEmail;

    public InboxMessagesAdapter(FullScreenActivity activity, String currentUserEmail) {
        this.inflater = activity.getLayoutInflater();
        this.currentUserEmail = currentUserEmail;
        messageList = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.item_message, viewGroup, false);
        InboxMessagesViewHolder viewHolder = new InboxMessagesViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ((InboxMessagesViewHolder) viewHolder).populate(messageList.get(i), currentUserEmail);
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public void setMessageList(List<Message> messageList) {
        this.messageList.clear();
        this.messageList.addAll(messageList);
        notifyDataSetChanged();
    }

    public List<Message> getMessageList(){
        return messageList;
    }
}
