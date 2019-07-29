package pl.statisfy.socketchat.project.mvvm.repository;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pl.statisfy.socketchat.project.app.MyChatApplication;
import pl.statisfy.socketchat.project.model.User;
import pl.statisfy.socketchat.project.utils.Utils;

public class UserDataManager {

    public SharedPreferences sharedPrefs;
    public MyChatApplication app;
    private List<User> userList;
    private HashMap<String, User> requestsSentMap;
    private static UserDataManager INSTANCE;

    public UserDataManager(){
        this.app = MyChatApplication.getInstance();
        this.sharedPrefs = app.getSharedPreferences(Utils.PREFS, Context.MODE_PRIVATE);
        this.requestsSentMap = new HashMap<>();
        this.userList = new ArrayList<>();
    }

    public static synchronized UserDataManager getInstance(){
        if (INSTANCE == null){
            INSTANCE = new UserDataManager();
        }
        return INSTANCE;
    }

    public void setUserToken(String token){
        sharedPrefs.edit().putString(Utils.USER_TOKEN, token).apply();
    }

    public void setUserName(String name){
        sharedPrefs.edit().putString(Utils.USER_NAME, name).apply();
    }

    public String getUserName(){
        return sharedPrefs.getString(Utils.USER_NAME,"");
    }

    public void setUserEmail(String email){
        sharedPrefs.edit().putString(Utils.USER_EMAIL, email).apply();
    }

    public String getUserEmail(){
        return sharedPrefs.getString(Utils.USER_EMAIL, "");
    }

    public void setUserPhoto(String photo){
        sharedPrefs.edit().putString(Utils.USER_PHOTO, photo).apply();
    }

    public String getUserPhoto(){
        return sharedPrefs.getString(Utils.USER_PHOTO,"");
    }

    public void clearUserData(){
        sharedPrefs.edit().putString(Utils.USER_TOKEN, "").apply();
        sharedPrefs.edit().putString(Utils.USER_NAME, "").apply();
        sharedPrefs.edit().putString(Utils.USER_EMAIL, "").apply();
        sharedPrefs.edit().putString(Utils.USER_PHOTO, "").apply();
    }

    public void setUserList(List<User> userList) {
        if(this.userList == null){
            this.userList = new ArrayList<>();
        }
        this.userList.clear();
        this.userList.addAll(userList);
    }

    public List<User> getUserList(){
        return this.userList;
    }

    public void setRequestsSentMap(HashMap<String, User> requestsSentMap) {
        if(this.requestsSentMap == null){
            this.requestsSentMap = new HashMap<>();
        }
        this.requestsSentMap.clear();
        this.requestsSentMap.putAll(requestsSentMap);
    }

    public HashMap<String,User> getRequestSentMap(){
        return this.requestsSentMap;
    }
}
