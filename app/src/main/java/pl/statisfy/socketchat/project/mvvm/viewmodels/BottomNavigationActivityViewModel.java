package pl.statisfy.socketchat.project.mvvm.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import pl.statisfy.socketchat.project.mvvm.repository.ApiRequestManager;

public class BottomNavigationActivityViewModel extends ViewModel {

    private static final String TAG = BottomNavigationActivityViewModel.class.getSimpleName();
    private MutableLiveData<Integer> newMessagesData;
    private ApiRequestManager apiRequestManager;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    public BottomNavigationActivityViewModel(){
        apiRequestManager = ApiRequestManager.getInstance();
    }

    public LiveData<Integer> getNewMessagesData(){
        if(newMessagesData == null){
            newMessagesData = new MutableLiveData<>();
        }

        newMessagesData = (MutableLiveData<Integer>) getNewMessages();

        return newMessagesData;
    }

    private LiveData<Integer> getNewMessages(){

        MutableLiveData<Integer> newMessages = new MutableLiveData<>();

        apiRequestManager.getNewMessages().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(Integer countNewMessages) {
                        newMessages.setValue(countNewMessages);
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

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}
