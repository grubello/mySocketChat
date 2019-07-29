package pl.statisfy.socketchat.project.api;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.LoginException;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.subjects.PublishSubject;
import io.socket.client.Ack;
import io.socket.client.Socket;
import pl.statisfy.socketchat.project.app.MyChatApplication;
import pl.statisfy.socketchat.project.model.Chatroom;
import pl.statisfy.socketchat.project.model.Message;
import pl.statisfy.socketchat.project.mvvm.model.Result;
import pl.statisfy.socketchat.project.utils.Utils;

public class ChatroomsMessagesAPIService {

    private static final String TAG = ChatroomsMessagesAPIService.class.getSimpleName();
    private static ChatroomsMessagesAPIService INSTANCE;

    private PublishSubject<Integer> getNewMessagesSubject;
    private PublishSubject<List<Chatroom>> getChatroomsSubject;
    private PublishSubject<List<Message>> getMessagesSubject;
    private Result setMessagesReadResult;
    private Socket socket = MyChatApplication.getInstance().getSocket();

    private ChatroomsMessagesAPIService(){
        getNewMessagesSubject = PublishSubject.create();
        getChatroomsSubject = PublishSubject.create();
        getMessagesSubject = PublishSubject.create();
        setMessagesReadResult = new Result();
    }

    public static ChatroomsMessagesAPIService getInstance(){
        if (INSTANCE == null) {
            INSTANCE = new ChatroomsMessagesAPIService();
        }
        return INSTANCE;
    }

    public PublishSubject<Integer> getNewMessages(String currentUserEmail) {

        List<Message> messageList = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference(Utils.FIREBASE_PATH_USER_NEWMESSAGES)
                .child(Utils.encodeEmail(currentUserEmail))
                .addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        messageList.clear();

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Message message = snapshot.getValue(Message.class);
                            messageList.add(message);
                        }
                        getNewMessagesSubject.onNext(messageList.size());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        getNewMessagesSubject.onError(databaseError.toException());
                    }
                });

        return getNewMessagesSubject;
    }

    public PublishSubject<List<Chatroom>> getChatrooms(String currentUserEmail){

        List<Chatroom> chatroomList = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference(Utils.FIREBASE_PATH_USER_CHATROOMS)
                .child(Utils.encodeEmail(currentUserEmail))
                .addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        chatroomList.clear();

                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                            chatroomList.add(snapshot.getValue(Chatroom.class));
                        }

                        getChatroomsSubject.onNext(chatroomList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        getChatroomsSubject.onError(databaseError.toException());
                    }
                });

        return getChatroomsSubject;
    }

    public PublishSubject<List<Message>> getMessages(String currentUserEmail, String friendEmail){

        List<Message> messageList = new ArrayList<>();

//        DatabaseReference newMessagesReference = FirebaseDatabase.getInstance().getReference(Utils.FIREBASE_PATH_USER_NEWMESSAGES)
//                .child(Utils.encodeEmail(currentUserEmail));

        FirebaseDatabase.getInstance()
                .getReference(Utils.FIREBASE_PATH_USER_MESSAGES)
                .child(Utils.encodeEmail(currentUserEmail))
                .child(Utils.encodeEmail(friendEmail))
                .addValueEventListener( new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        messageList.clear();

                        for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                            Message message = snapshot.getValue(Message.class);
                            //newMessagesReference.child(snapshot.getKey()).removeValue();
                            messageList.add(message);
                        }

                        getMessagesSubject.onNext(messageList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        getMessagesSubject.onError(databaseError.toException());
                    }
                });
        return getMessagesSubject;
    }

    public Observable<Result> clearReadMessages(String currentUserEmail, List<Message> messageList){

        Result result = new Result();
        List<String> ids = new ArrayList<>();

        //create array with message ids
        for (Message message: messageList){
            ids.add(message.getId());
        }

        return Observable.create(emitter -> {
            try {
                JSONObject data = new JSONObject();
                data.put("userEmail", currentUserEmail);
                data.put("messageIds", ids);

                socket.emit("clearMessagesRead", data, (Ack) args -> {
                    result.success = true;
                    result.message = "clearMessagesRead success";
                    emitter.onNext(result);
                    emitter.onComplete();
                })
                .on(Socket.EVENT_CONNECT_ERROR, args -> {
                    result.message = "Socket error: " + args[0];
                    result.success = false;
                    emitter.onNext(result);
                    emitter.onComplete();
                }).on(Socket.EVENT_CONNECT_TIMEOUT, args -> {
                    result.message = "Socket error: " + args[0];
                    result.success = false;
                    emitter.onNext(result);
                    emitter.onComplete();
                });

            } catch (JSONException e){
                Log.i(TAG, "JSON exception: " + e.getMessage());
                emitter.onError(e);
            }
        });
    }

    public Observable<Result> setMessagesRead(String currentUserEmail, String friendEmail){

        DatabaseReference chatroomReference = FirebaseDatabase.getInstance()
                .getReference(Utils.FIREBASE_PATH_USER_CHATROOMS)
                .child(Utils.encodeEmail(currentUserEmail))
                .child(Utils.encodeEmail(friendEmail));

        return Observable.create(emitter -> {

            chatroomReference.child("lastMessageRead").setValue(true)
                    .addOnSuccessListener(aVoid -> {
                        setMessagesReadResult.success = true;
                        setMessagesReadResult.message = "setMessagesRead success";
                        emitter.onNext(setMessagesReadResult);
                        emitter.onComplete();
                    })
                    .addOnFailureListener(e -> {
                        emitter.onError(e);
                    });
        });
    }

    public Result getLastMessageStatus(ArrayList data, String currentUserEmail){

        Result result = new Result();
        Map<String,Object> updateData = new HashMap<>();

        String lastSenderEmail = (String) data.get(3);
        String lastMessage = (String) data.get(4);
        String friendEmail = (String) data.get(5);

        updateData.put("lastMessage", lastMessage);
        updateData.put("lastMessageSenderEmail", lastSenderEmail);
        updateData.put("sentLastMessage", true);

        FirebaseDatabase.getInstance()
                .getReference(Utils.FIREBASE_PATH_USER_CHATROOMS)
                .child(Utils.encodeEmail(currentUserEmail))
                .child(Utils.encodeEmail(friendEmail))
                .updateChildren(updateData)
                .addOnSuccessListener(aVoid -> result.success = true);

       return result;
    }

    public Result updateLastMessageStatus(Chatroom chatroom, String currentUserEmail){

        Result result = new Result();

        FirebaseDatabase.getInstance()
                .getReference(Utils.FIREBASE_PATH_USER_CHATROOMS)
                .child(Utils.encodeEmail(currentUserEmail))
                .child(Utils.encodeEmail(chatroom.getFriendEmail())).setValue(chatroom)
                .addOnSuccessListener(aVoid -> {
                    result.success = true;
                });

        return result;
    }

    public Observable<Result> sendMessage(String friendName, String friendEmail, String friendPicture, String currentUserEmail,
                                          String currentUserPicture, String currentUserName, String messageText){

        Result result = new Result();

        DatabaseReference messagesReference = FirebaseDatabase.getInstance()
                .getReference(Utils.FIREBASE_PATH_USER_MESSAGES)
                .child(Utils.encodeEmail(currentUserEmail))
                .child(Utils.encodeEmail(friendEmail));

        DatabaseReference sendMessageReference = messagesReference.push();

        return Observable.create(emitter -> {

            //send message
            Message message = new Message(sendMessageReference.getKey(),
                    messageText, currentUserEmail, currentUserPicture);

            sendMessageReference.setValue(message);

            //start/update new chatroom
            Chatroom chatroom = new Chatroom(friendPicture, friendName,
                    friendEmail, messageText, currentUserEmail, true, true);

            FirebaseDatabase.getInstance()
                    .getReference(Utils.FIREBASE_PATH_USER_CHATROOMS)
                    .child(Utils.encodeEmail(currentUserEmail))
                    .child(Utils.encodeEmail(friendEmail)).setValue(chatroom);

            try {
                JSONObject data = new JSONObject();
                data.put("messageId", message.getId());
                data.put("messageText", messageText);
                data.put("friendEmail", friendEmail);
                data.put("senderEmail", currentUserEmail);
                data.put("senderPicture", currentUserPicture);
                data.put("senderName", currentUserName);

                socket.emit("messageSent", data, (Ack) args -> {
                    result.success = true;
                    result.message = "sendMessage success";
                    emitter.onNext(result);
                    emitter.onComplete();
                })
                .on(Socket.EVENT_CONNECT_ERROR, args -> {
                    result.message = "Socket error: " + args[0];
                    result.success = false;
                    emitter.onNext(result);
                    emitter.onComplete();
                }).on(Socket.EVENT_CONNECT_TIMEOUT, args -> {
                    result.message = "Socket error: " + args[0];
                    result.success = false;
                    emitter.onNext(result);
                    emitter.onComplete();
                });

            } catch (JSONException e){
                Log.i(TAG, "JSON exception: " + e.getMessage());
                emitter.onError(e);
            }
        });
    }
}

