package pl.statisfy.socketchat.project.api;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.socket.client.Ack;
import io.socket.client.Socket;
import pl.statisfy.socketchat.project.app.MyChatApplication;
import pl.statisfy.socketchat.project.model.User;
import pl.statisfy.socketchat.project.mvvm.model.GetFriendResult;
import pl.statisfy.socketchat.project.mvvm.model.RequestReceivedResult;
import pl.statisfy.socketchat.project.mvvm.model.Result;
import pl.statisfy.socketchat.project.utils.Utils;

public class FriendAPIService {

    private static final String TAG = FriendAPIService.class.getSimpleName();
    private static Socket socket = MyChatApplication.getInstance().getSocket();
    private static FriendAPIService INSTANCE;
    private PublishSubject<List<User>> getAllUsersSubject;
    private PublishSubject<GetFriendResult> getUserFriendsSubject;
    private PublishSubject<HashMap<String,User>> getFriendRequestSentSubject;
    private PublishSubject<RequestReceivedResult> getFriendRequestReceivedSubject;
    private Result result;
    private RequestReceivedResult requestReceivedResult;

    public FriendAPIService(){
        getAllUsersSubject = PublishSubject.create();
        getUserFriendsSubject = PublishSubject.create();
        getFriendRequestSentSubject = PublishSubject.create();
        getFriendRequestReceivedSubject = PublishSubject.create();
        result = new Result();
        requestReceivedResult = new RequestReceivedResult();
    }

    public static FriendAPIService getInstance(){
        if (INSTANCE == null) {
            INSTANCE = new FriendAPIService();
        }
        return INSTANCE;
    }

    public PublishSubject<GetFriendResult> getFriends(String currentUserEmail) {
        FirebaseDatabase.getInstance().getReference()
                .child(Utils.FIREBASE_PATH_USER_FRIENDS)
                .addValueEventListener(new ValueEventListener() {

                    GetFriendResult result = new GetFriendResult();

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        result.clear();

                        for (DataSnapshot snapshot : dataSnapshot.child(Utils.encodeEmail(currentUserEmail)).getChildren()) {
                            User user = snapshot.getValue(User.class);
                            result.getFriendsMap().put(user.getEmail(), user);
                            result.getFriendsList().add(user);
                        }

                        getUserFriendsSubject.onNext(result);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        getUserFriendsSubject.onError(databaseError.toException());
                    }
                });

        return getUserFriendsSubject;
    }

    public PublishSubject<List<User>> getAllUsers(String currentUserEmail){

        FirebaseDatabase.getInstance().getReference().child(Utils.FIREBASE_PATH_USERS)
                .addValueEventListener(new ValueEventListener() {

                    List<User> userList = new ArrayList<>();

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        userList.clear();
                        for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                            User user = snapshot.getValue(User.class);
                            if(!user.getEmail().equals(currentUserEmail) && user.isHasLoggedIn()){
                                userList.add(user);
                            }
                        }

                        getAllUsersSubject.onNext(userList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        getAllUsersSubject.onError(databaseError.toException());
                    }
                });

        return getAllUsersSubject;
    }

    public PublishSubject<HashMap<String,User>> getFriendRequestSent(String currentUserEmail){

        FirebaseDatabase.getInstance().getReference().child(Utils.FIREBASE_PATH_SEND_REQUEST_SENT)
                .child(Utils.encodeEmail(currentUserEmail))
                .addValueEventListener(new ValueEventListener() {

                    HashMap<String, User> requestSentMap = new HashMap<>();

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        requestSentMap.clear();
                        for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                            User user = snapshot.getValue(User.class);
                            requestSentMap.put(user.getEmail(), user);
                        }

                        getFriendRequestSentSubject.onNext(requestSentMap);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        getFriendRequestSentSubject.onError(databaseError.toException());
                    }
                });

        return getFriendRequestSentSubject;
    }

    public PublishSubject<RequestReceivedResult> getFriendRequestReceived(String currentUserEmail){
        FirebaseDatabase.getInstance().getReference().child(Utils.FIREBASE_PATH_SEND_REQUEST_RECEIVED)
                .child(Utils.encodeEmail(currentUserEmail))
                .addValueEventListener(new ValueEventListener() {

                    HashMap<String, User> requestReceivedMap = new HashMap<>();
                    List<User> requestReceivedList = new ArrayList<>();

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        requestReceivedMap.clear();
                        requestReceivedList.clear();
                        requestReceivedResult.clear();

                        for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                            User user = snapshot.getValue(User.class);
                            requestReceivedMap.put(user.getEmail(), user);
                            requestReceivedList.add(user);
                        }

                        requestReceivedResult.setRequestReceivedMap(requestReceivedMap);
                        requestReceivedResult.setRequestReceivedList(requestReceivedList);

                        getFriendRequestReceivedSubject.onNext(requestReceivedResult);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        getFriendRequestReceivedSubject.onError(databaseError.toException());
                    }
                });

        return getFriendRequestReceivedSubject;
    }

    public Observable<Result> sendOrCancelFriendRequest(User user, HashMap<String,User> requestsSentMap, String currentUserEmail){

        DatabaseReference requestSentReference = FirebaseDatabase.getInstance().getReference()
                .child(Utils.FIREBASE_PATH_SEND_REQUEST_SENT)
                .child(Utils.encodeEmail(currentUserEmail));

        return Observable.create(emitter -> {
                                    int requestCode;

                                    if(Utils.isIncludedInMap(requestsSentMap, user)){
                                        requestCode = 0;
                                        requestSentReference.child(Utils.encodeEmail(user.getEmail())).removeValue();
                                    } else {
                                        requestCode = 1;
                                        requestSentReference.child(Utils.encodeEmail(user.getEmail())).setValue(user);
                                    }

                                    try {
                                        JSONObject data = new JSONObject();

                                        data.put("friendEmail", user.getEmail());
                                        data.put("userEmail", currentUserEmail);
                                        data.put("requestCode", requestCode);

                                        socket.emit("friendRequest", data, (Ack) args -> {
                                            result.success = true;
                                            result.message = "friendRequest with requestCode: " + requestCode + " success";
                                            emitter.onNext(result);
                                            emitter.onComplete();
                                        })
                                                .on(Socket.EVENT_CONNECT_ERROR, args -> {
                                            result.message = "Socket error: " + args[0];
                                            result.success = false;
                                            emitter.onNext(result);
                                            emitter.onComplete();
                                        })
                                                .on(Socket.EVENT_CONNECT_TIMEOUT, args -> {
                                                    result.message = "Socket error: " + args[0];
                                                    result.success = false;
                                                    emitter.onNext(result);
                                                    emitter.onComplete();
                                                });

                                    } catch (JSONException e) {
                                        Log.i(TAG, e.getMessage());
                                        emitter.onError(e);
                                    }
                                });
    }

    public Observable<Result> acceptOrDenyFriendRequest(User user, int requestCode, String currentUserEmail){

        return Observable.create(emitter -> {
            try {
                JSONObject data = new JSONObject();
                data.put("friendEmail", user.getEmail());
                data.put("userEmail", currentUserEmail);
                data.put("requestCode", requestCode);
                data.put("instanceId", FirebaseInstanceId.getInstance().getId());

                socket.emit("acceptOrDenyRequest", data, (Ack) args -> {
                    result.success = true;
                    result.message = "acceptOrDenyRequest with requestCode: " + requestCode + " success";
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


            } catch (JSONException e) {
                Log.i(TAG, e.getMessage());
                emitter.onError(e);
            }
        });
    }

    public Observable<Result> removeFriend(User user, String currentUserEmail){
        return Observable.create(emitter -> {
            try {
                JSONObject data = new JSONObject();
                data.put("userEmail", currentUserEmail);
                data.put("friendEmail", user.getEmail());

                socket.emit("cancelFriend", data, (Ack) args -> {
                    result.success = true;
                    result.message = "cancelFriend with success";
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


            } catch (JSONException e) {
                Log.i(TAG, e.getMessage());
                emitter.onError(e);
            }
        });
    }

}
