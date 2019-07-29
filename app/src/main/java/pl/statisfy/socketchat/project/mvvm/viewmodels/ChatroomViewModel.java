package pl.statisfy.socketchat.project.mvvm.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import pl.statisfy.socketchat.project.model.Chatroom;
import pl.statisfy.socketchat.project.mvvm.repository.ApiRequestManager;

public class ChatroomViewModel extends ViewModel {

    private static final String TAG = ChatroomViewModel.class.getSimpleName();
    private MutableLiveData<List<Chatroom>> getChatroomsData;
    private ApiRequestManager apiRequestManager;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public ChatroomViewModel(){
        apiRequestManager = ApiRequestManager.getInstance();
    }

    public LiveData<List<Chatroom>> getChatrooms(){
        if (getChatroomsData == null){
            getChatroomsData = new MutableLiveData<>();
        }
        getChatroomsData = (MutableLiveData<List<Chatroom>>) getChatroomList();
        return getChatroomsData;
    }

    private LiveData<List<Chatroom>> getChatroomList(){

        MutableLiveData<List<Chatroom>> chatroomList = new MutableLiveData();

        apiRequestManager.getChatrooms().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Chatroom>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(List<Chatroom> chatrooms) {
                        chatroomList.setValue(chatrooms);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });

        return chatroomList;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}
