package pl.statisfy.socketchat.project.mvvm.viewmodels;

import android.app.Application;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import pl.statisfy.socketchat.project.model.User;
import pl.statisfy.socketchat.project.mvvm.repository.ApiRequestManager;
import pl.statisfy.socketchat.project.mvvm.model.LoginLogoutStatus;
import pl.statisfy.socketchat.project.mvvm.model.Result;
import pl.statisfy.socketchat.project.mvvm.repository.UserDataManager;

public class ProfileViewModel extends AndroidViewModel {

    private static final String TAG = ProfileViewModel.class.getSimpleName();
    private MutableLiveData<LoginLogoutStatus> signOutStatusData;
    private MutableLiveData<File> createdFileStatusData;
    private MutableLiveData<User> userDetails;
    private MutableLiveData<Result> uploadPhotoStatusData;
    private LoginLogoutStatus result;
    private ApiRequestManager apiRequestManager;
    private UserDataManager userDataManager;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    public ProfileViewModel(Application application){
        super(application);
        result = new LoginLogoutStatus();
        apiRequestManager = ApiRequestManager.getInstance();
        userDataManager = UserDataManager.getInstance();
    }

    public LiveData<LoginLogoutStatus> getSignOutStatus(){
        if(signOutStatusData == null){
            signOutStatusData = new MutableLiveData<>();
        }
        return signOutStatusData;
    }

    public LiveData<File> getCreatedFile(){
        if(createdFileStatusData == null) {
            createdFileStatusData = new MutableLiveData<>();
        }

        return createdFileStatusData;
    }

    public LiveData<User> getUserDetails(){
        if(userDetails == null){
            userDetails = new MutableLiveData<>();
        }
        userDetails = (MutableLiveData<User>) getCurrentUserDetails();
        return userDetails;
    }

    public LiveData<Result> getUploadedPhoto(){
        if(uploadPhotoStatusData == null){
            uploadPhotoStatusData = new MutableLiveData<>();
        }
        return uploadPhotoStatusData;
    }



    public void uploadProfilePhoto(Uri photoUri){

        compositeDisposable.add(apiRequestManager.uploadProfilePhoto(photoUri)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .doOnError(throwable -> {
                                        Log.i(TAG, "uploadProfilePhoto error: " + throwable.getMessage());
                                    })
                                    .subscribe(result -> {
                                        userDataManager.setUserPhoto((String) result.data.get(0));
                                        uploadPhotoStatusData.postValue(result);
                                    }));
    }

    private LiveData<User> getCurrentUserDetails(){

        MutableLiveData<User> currentUserDetails = new MutableLiveData<>();
        User currentUser = new User(userDataManager.getUserEmail(), userDataManager.getUserPhoto(), userDataManager.getUserName());
        currentUserDetails.setValue(currentUser);

        return currentUserDetails;
    }

    public void createImageFileForCamUpload() {

        File image = null;
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = getApplication().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try {
            image = File.createTempFile(imageFileName, ".jpg" ,storageDir);
        } catch (IOException e) {
            e.printStackTrace();
        }

        createdFileStatusData.setValue(image);
    }

    public void signOut(){

        compositeDisposable.add(apiRequestManager.signOut()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> {
                    result.success = false;
                    result.message = "Signing out...";
                    signOutStatusData.postValue(result);
                })
                .doOnError(throwable -> {
                    result.success = false;
                    result.message = "Sign out error: " + throwable.getMessage();
                    signOutStatusData.postValue(result);
                })
                .subscribe(logoutStatus -> {
                    signOutStatusData.postValue(logoutStatus);
                    userDataManager.clearUserData();
                }));
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}
