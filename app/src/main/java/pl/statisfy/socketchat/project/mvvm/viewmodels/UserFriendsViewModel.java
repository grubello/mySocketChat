package pl.statisfy.socketchat.project.mvvm.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import pl.statisfy.socketchat.project.model.User;
import pl.statisfy.socketchat.project.mvvm.repository.ApiRequestManager;
import pl.statisfy.socketchat.project.mvvm.model.GetFriendResult;
import pl.statisfy.socketchat.project.mvvm.model.RequestReceivedResult;
import pl.statisfy.socketchat.project.mvvm.repository.UserDataManager;

public class UserFriendsViewModel extends ViewModel {

    private static final String TAG = UserFriendsViewModel.class.getSimpleName();

    private MutableLiveData<GetFriendResult> friendsListData;
    private MutableLiveData<List<User>> allUsersListData;
    private MutableLiveData<HashMap<String,User>> requestSentData;
    private MutableLiveData<RequestReceivedResult> requestReceivedData;
    private MutableLiveData<List<User>> getSearchData;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private PublishSubject<String> searchBarSearchSubject;
    private ApiRequestManager apiRequestManager;
    private UserDataManager userDataManager;

    public UserFriendsViewModel(){
        apiRequestManager = ApiRequestManager.getInstance();
        userDataManager = UserDataManager.getInstance();
        searchBarSearchSubject = PublishSubject.create();
    }

    //ViewModel LiveData
    public LiveData<GetFriendResult> getFriends(){
        if (friendsListData == null){
            friendsListData = new MutableLiveData<>();
        }

        friendsListData = (MutableLiveData<GetFriendResult>) watchUserFriendDataChange();

        return friendsListData;
    }

    public LiveData<List<User>> getAllUsers(){
        if (allUsersListData == null){
            allUsersListData = new MutableLiveData<>();
        }

        allUsersListData = (MutableLiveData<List<User>>) watchUsersDataChange();

        return allUsersListData;
    }

    public LiveData<HashMap<String,User>> getRequestSent(){
        if(requestSentData == null){
            requestSentData = new MutableLiveData<>();
        }

        requestSentData = (MutableLiveData<HashMap<String, User>>) watchUserRequestSentDataChange();

        return requestSentData;
    }

    public LiveData<RequestReceivedResult> getRequestReceived(){
        if(requestReceivedData == null){
            requestReceivedData = new MutableLiveData<>();
        }

        requestReceivedData = (MutableLiveData<RequestReceivedResult>) watchUserRequestReceivedDataChange();

        return requestReceivedData;
    }

    public LiveData<List<User>> getSearchData(){
        if(getSearchData == null){
            getSearchData = new MutableLiveData<>();
        }
        getSearchData = (MutableLiveData<List<User>>) watchSearchBarTextChanged();
        return getSearchData;
    }


    //methods called from view
    public void searchForUser(String text){
        searchBarSearchSubject.onNext(text);
    }

    public void userClicked(User user){

        compositeDisposable.add(apiRequestManager.sendOrCancelFriendRequest(user,userDataManager.getRequestSentMap())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(throwable -> {
                    Log.i(TAG, "error: " + throwable.getMessage());
                })
                .subscribe(result -> {
                    Log.i(TAG, "" + result.message);
                }));
    }

    public void acceptOrDenyFriendRequest(User user, int requestCode){

        compositeDisposable.add(apiRequestManager.acceptOrDenyFriendRequest(user,requestCode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(throwable -> {
                    Log.i(TAG, "acceptOrDenyFriendRequest error: " + throwable.getMessage());
                })
                .subscribe(result -> {
                    Log.i(TAG, "acceptOrDenyFriendRequest with requestCode: " + requestCode + " success");
                }));
    }

    public void removeFriend(User user){
        compositeDisposable.add(apiRequestManager.removeFriend(user)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(throwable -> {
                    Log.i(TAG, "removeFriend error: " + throwable.getMessage());
                })
                .subscribe(result -> {
                    Log.i(TAG, "removeFriend success");
                }));
    }


    //Watch data changes in repo
    private LiveData<List<User>> watchUsersDataChange(){

        MutableLiveData<List<User>> usersListData = new MutableLiveData<>();

        apiRequestManager.getAllUsers().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<User>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(List<User> users) {
                        userDataManager.setUserList(users);
                        usersListData.setValue(users);
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });

        return usersListData;
    }

    private LiveData<GetFriendResult> watchUserFriendDataChange(){

        MutableLiveData<GetFriendResult> friendData = new MutableLiveData<>();

        apiRequestManager.getFriends(userDataManager.getUserEmail()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GetFriendResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(GetFriendResult getFriendResult) {
                        friendData.setValue(getFriendResult);
                    }

                    @Override
                    public void onError(Throwable e) {
                        GetFriendResult result = new GetFriendResult();
                        result.setError("firebase error: " + e.getMessage());

                        friendData.setValue(result);
                    }

                    @Override
                    public void onComplete() {

                    }
                });

        return friendData;
    }

    private LiveData<HashMap<String,User>> watchUserRequestSentDataChange(){

        MutableLiveData<HashMap<String,User>> requestSentData = new MutableLiveData<>();

        apiRequestManager.getFriendRequestSent().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HashMap<String, User>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(HashMap<String, User> requestSentHashMap) {
                        userDataManager.setRequestsSentMap(requestSentHashMap);
                        requestSentData.setValue(requestSentHashMap);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });

        return requestSentData;
    }

    private LiveData<RequestReceivedResult> watchUserRequestReceivedDataChange(){

        MutableLiveData<RequestReceivedResult> requestReceivedData = new MutableLiveData<>();

        apiRequestManager.getFriendRequestReceived().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RequestReceivedResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(RequestReceivedResult requestReceivedResult) {
                        requestReceivedData.setValue(requestReceivedResult);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });

        return requestReceivedData;
    }

    private LiveData<List<User>> watchSearchBarTextChanged(){

        MutableLiveData data = new MutableLiveData();

        searchBarSearchSubject.debounce(500, TimeUnit.MILLISECONDS)
                                .subscribeOn(Schedulers.io())
                                .map(text -> {
                                    List<User> foundMatchingList = new ArrayList<>();

                                    if (text.length() == 0) return userDataManager.getUserList();

                                    for (User user: userDataManager.getUserList()){
                                        if (user.getUserName().toLowerCase().startsWith(text.toLowerCase())){
                                            foundMatchingList.add(user);
                                        }
                                    }

                                    return foundMatchingList;
                                })
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Observer<List<User>>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {
                                        compositeDisposable.add(d);
                                    }

                                    @Override
                                    public void onNext(List<User> users) {
                                        data.setValue(users);
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        Log.i(TAG, "onError: " + e.getMessage());
                                    }

                                    @Override
                                    public void onComplete() {

                                    }
                                });

        return data;
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}
