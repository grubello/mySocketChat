package pl.statisfy.socketchat.project.mvvm.repository;

import android.net.Uri;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import pl.statisfy.socketchat.project.app.MyChatApplication;
import pl.statisfy.socketchat.project.model.Chatroom;
import pl.statisfy.socketchat.project.model.Message;
import pl.statisfy.socketchat.project.model.User;
import pl.statisfy.socketchat.project.api.AccountAPIService;
import pl.statisfy.socketchat.project.api.ChatroomsMessagesAPIService;
import pl.statisfy.socketchat.project.api.FriendAPIService;
import pl.statisfy.socketchat.project.mvvm.model.GetFriendResult;
import pl.statisfy.socketchat.project.mvvm.model.LoginLogoutStatus;
import pl.statisfy.socketchat.project.mvvm.model.RegisterResult;
import pl.statisfy.socketchat.project.mvvm.model.RequestReceivedResult;
import pl.statisfy.socketchat.project.mvvm.model.Result;
import pl.statisfy.socketchat.project.utils.Utils;

public class ApiRequestManager {

    private static final String TAG = ApiRequestManager.class.getSimpleName();

    private AccountAPIService accountAPIService;
    private FriendAPIService friendAPIService;
    private ChatroomsMessagesAPIService chatroomsMessagesAPIService;
    private MyChatApplication app;
    private static ApiRequestManager INSTANCE;
    private UserDataManager userDataManager;

    public ApiRequestManager(){
        this.accountAPIService = AccountAPIService.getInstance();
        this.friendAPIService = FriendAPIService.getInstance();
        this.chatroomsMessagesAPIService = ChatroomsMessagesAPIService.getInstance();
        this.app = MyChatApplication.getInstance();
        this.userDataManager = UserDataManager.getInstance();
    }

    public static ApiRequestManager getInstance(){
        synchronized (ApiRequestManager.class) {
            if (INSTANCE == null){
                INSTANCE = new ApiRequestManager();
                return INSTANCE;
            } else {
                return INSTANCE;
            }
        }
    }


    /*
        Account API calls
     */
    public Observable<LoginLogoutStatus> userLogin(String useremail, String password){
        return accountAPIService.attemptLogin(useremail,password);
    }

    public Observable<RegisterResult> registerUser(String username, String useremail, String password){
        return accountAPIService.attemptRegister(username,useremail,password);
    }

    public Observable<LoginLogoutStatus> signOut(){
        return accountAPIService.signOut(userDataManager.getUserEmail());
    }

    public Observable<Result> uploadProfilePhoto(Uri photoUri){
        byte[] photoByteArray = Utils.getBitmapByteArray(app.getContentResolver(), photoUri, 512,60);
        return accountAPIService.uploadUserProfilePhoto(photoUri,userDataManager.getUserEmail(), photoByteArray);
    }


    /*
        Friend API calls
     */
    public PublishSubject<GetFriendResult> getFriends(String currentUserEmail) {
        return friendAPIService.getFriends(currentUserEmail);
    }

    public PublishSubject<List<User>> getAllUsers(){
        return friendAPIService.getAllUsers(userDataManager.getUserEmail());
    }

    public PublishSubject<HashMap<String,User>> getFriendRequestSent(){
        return friendAPIService.getFriendRequestSent(userDataManager.getUserEmail());
    }

    public PublishSubject<RequestReceivedResult> getFriendRequestReceived(){
       return friendAPIService.getFriendRequestReceived(userDataManager.getUserEmail());
    }

    public Observable<Result> sendOrCancelFriendRequest(User user, HashMap<String,User> requestsSentMap){
        return friendAPIService.sendOrCancelFriendRequest(user,requestsSentMap,userDataManager.getUserEmail());
    }

    public Observable<Result> acceptOrDenyFriendRequest(User user, int requestCode){
        return friendAPIService.acceptOrDenyFriendRequest(user,requestCode,userDataManager.getUserEmail());
    }

    public Observable<Result> removeFriend(User user){
        return friendAPIService.removeFriend(user, userDataManager.getUserEmail());
    }


    /*
        Messages API calls
     */
    public PublishSubject<List<Message>> getMessages(String friendEmail){
        return chatroomsMessagesAPIService.getMessages(userDataManager.getUserEmail(), friendEmail);
    }

    public PublishSubject<Integer> getNewMessages(){
        return chatroomsMessagesAPIService.getNewMessages(userDataManager.getUserEmail());
    }

    public Observable<Result> clearReadMessages(List<Message> messageList){
        return chatroomsMessagesAPIService.clearReadMessages(userDataManager.getUserEmail(),messageList);
    }

    public PublishSubject<List<Chatroom>> getChatrooms(){
        return chatroomsMessagesAPIService.getChatrooms(userDataManager.getUserEmail());
    }

    public Observable<Result> setMessagesRead(String friendEmail){
        return chatroomsMessagesAPIService.setMessagesRead(userDataManager.getUserEmail(), friendEmail);
    }

    public Result getLastMessageStatus(ArrayList data){
        return chatroomsMessagesAPIService.getLastMessageStatus(data, userDataManager.getUserEmail());
    }

    public Result updateLastMessageStatus(Chatroom chatroom){
        return chatroomsMessagesAPIService.updateLastMessageStatus(chatroom, userDataManager.getUserEmail());
    }

    public Observable<Result> sendMessage(String friendName, String friendEmail, String friendPicture, String messageText){
        return chatroomsMessagesAPIService.sendMessage(friendName, friendEmail,friendPicture, userDataManager.getUserEmail(),
                                            userDataManager.getUserPhoto(), userDataManager.getUserName(), messageText);
    }
}
