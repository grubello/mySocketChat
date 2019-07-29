package pl.statisfy.socketchat.project.mvvm.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import pl.statisfy.socketchat.project.model.Chatroom;
import pl.statisfy.socketchat.project.model.Message;
import pl.statisfy.socketchat.project.mvvm.model.Result;
import pl.statisfy.socketchat.project.mvvm.repository.ApiRequestManager;

public class MessagesViewModel extends ViewModel {

    private static final String TAG = MessagesViewModel.class.getSimpleName();
    private ApiRequestManager apiRequestManager;
    private MutableLiveData<List<Message>> messagesData;
    private MutableLiveData<Result> getEditTextData;
    private PublishSubject<ArrayList> lastMessageSubject;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    public MessagesViewModel(){
        apiRequestManager = ApiRequestManager.getInstance();
        lastMessageSubject = PublishSubject.create();
    }

    public LiveData<List<Message>> getMessagesFromFriend(String friendEmail){
        if(messagesData == null){
            messagesData = new MutableLiveData<>();
        }

        messagesData = (MutableLiveData<List<Message>>) getMessages(friendEmail);

        return messagesData;
    }

    private LiveData<List<Message>> getMessages(String friendEmail){

        MutableLiveData<List<Message>> newMessages = new MutableLiveData<>();

        apiRequestManager.getMessages(friendEmail).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Message>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(List<Message> messages) {
                        newMessages.setValue(messages);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });

        return newMessages;
    }

    public LiveData<Result> getTypedLastMessage(){
        if(getEditTextData == null){
            getEditTextData = new MutableLiveData<>();
        }
        getEditTextData = (MutableLiveData<Result>) watchLastMessage();
        return getEditTextData;
    }

    private LiveData<Result> watchLastMessage(){

        MutableLiveData<Result> lastMessageResult = new MutableLiveData<>();

        lastMessageSubject.debounce(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .map(data -> {

                    Result result = new Result();

                    String messageString = (String) data.get(0);
                    int messageCount = (int) data.get(1);
                    boolean userClearedText = (boolean) data.get(2);

                    if(userClearedText){
                        result = apiRequestManager.getLastMessageStatus(data);
                    } else if(!messageString.equals("") && messageCount > 0) {
                        result = apiRequestManager.updateLastMessageStatus((Chatroom) data.get(3));
                    }

                    return result;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Result>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(Result result) {
                        lastMessageResult.postValue(result);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });

        return lastMessageResult;
    }


    //calls from view
    public void messagesRead(String friendEmail){
        compositeDisposable.add(apiRequestManager.setMessagesRead(friendEmail)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(throwable -> {
                    Log.i(TAG, "messagesRead error: " + throwable.getMessage());
                })
                .subscribe(result -> {
                    if(result.success) {
                        Log.i(TAG, "messagesRead success: " + result);
                    } else {
                        Log.i(TAG, "messagesRead result success false");
                    }
                }));
    }

    public void clearReadMessages(List<Message> messageList){
        compositeDisposable.add(apiRequestManager.clearReadMessages(messageList).subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(result -> {
                                        Log.i(TAG, "clearReadMessages result: " + result.toString());
                                    }));
    }

    public void lastMessage(ArrayList data){
        lastMessageSubject.onNext(data);
    }

    public void sendMessage(String friendName, String friendEmail, String friendPicture, String messageText){

        compositeDisposable.add(apiRequestManager.sendMessage(friendName,friendEmail,friendPicture, messageText)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnError(throwable -> {
                                    Log.i(TAG, "sendMessage error: " + throwable.getMessage());
                                })
                                .subscribe(result -> {
                                    Log.i(TAG, "sendMessage success");
                                }));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}
