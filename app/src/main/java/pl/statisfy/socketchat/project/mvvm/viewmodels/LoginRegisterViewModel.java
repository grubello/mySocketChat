package pl.statisfy.socketchat.project.mvvm.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import pl.statisfy.socketchat.project.mvvm.repository.ApiRequestManager;
import pl.statisfy.socketchat.project.mvvm.model.LoginLogoutStatus;
import pl.statisfy.socketchat.project.mvvm.model.RegisterResult;
import pl.statisfy.socketchat.project.mvvm.repository.UserDataManager;

public class LoginRegisterViewModel extends ViewModel{

    private MutableLiveData<LoginLogoutStatus> loginStatusData, logoutStatusData;
    private MutableLiveData<RegisterResult> registerStatusData;
    private LoginLogoutStatus loginStatus;
    private RegisterResult registerResult;
    private ApiRequestManager apiRequestManager;
    private UserDataManager userDataManager;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    public LoginRegisterViewModel(){
        loginStatus = new LoginLogoutStatus();
        registerResult = new RegisterResult();
        apiRequestManager = ApiRequestManager.getInstance();
        userDataManager = UserDataManager.getInstance();
    }

    public LiveData<LoginLogoutStatus> getLoginLogoutStatus() {
        if (loginStatusData == null) {
            loginStatusData = new MutableLiveData<>();
        }
        return loginStatusData;
    }

    public LiveData<RegisterResult> getRegisterResult(){
        if (registerStatusData == null){
            registerStatusData = new MutableLiveData<>();
        }
        return  registerStatusData;
    }

    public LiveData<LoginLogoutStatus> getLogoutStatus() {
        if (logoutStatusData == null) {
            logoutStatusData = new MutableLiveData<>();
        }
        return logoutStatusData;
    }

    public void login(String useremail, String password){

        loginStatus.refresh();

        compositeDisposable.add(apiRequestManager.userLogin(useremail,password)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .doOnSubscribe(disposable -> {
                                        loginStatus.isLoading = true;
                                        loginStatus.message = "Signing in...";
                                        loginStatusData.postValue(loginStatus);
                                    })
                                    .doOnError(throwable -> {
                                        loginStatus.isLoading = false;
                                        loginStatus.message = "Login error: " + throwable.getMessage();
                                        loginStatusData.postValue(loginStatus);
                                    })
                                    .subscribe(loginLogoutStatus -> {
                                        userDataManager.setUserToken(loginLogoutStatus.token);
                                        userDataManager.setUserName(loginLogoutStatus.displayName);
                                        userDataManager.setUserEmail(loginLogoutStatus.email);
                                        userDataManager.setUserPhoto(loginLogoutStatus.photo);
                                        loginStatusData.postValue(loginLogoutStatus);
                                    }));
    }

    public void register(String username, String useremail, String userpassword){

        registerResult.refresh();

        compositeDisposable.add(apiRequestManager.registerUser(username,useremail,userpassword)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .doOnSubscribe(disposable -> {
                                        registerResult.isLoading = true;
                                        registerResult.message = "Registering user...";
                                        registerStatusData.postValue(registerResult);
                                    })
                                    .doOnError(throwable -> {
                                        registerResult.isLoading = false;
                                        registerResult.message = "Registering user error: " + throwable.getMessage();
                                        registerStatusData.postValue(registerResult);
                                    })
                                    .subscribe(registerResult -> {
                                        registerStatusData.postValue(registerResult);
                                    }));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}