package pl.statisfy.socketchat.project.mvvm.model;

import java.util.ArrayList;
import java.util.HashMap;
import pl.statisfy.socketchat.project.model.User;

public class GetFriendResult{

    private ArrayList<User> userList;
    private HashMap<String,User> userMap;
    private String error;

    public GetFriendResult(){
        userList = new ArrayList<>();
        userMap = new HashMap<>();
    }

    public void clear(){
        userList.clear();
        userMap.clear();
    }

    public ArrayList<User> getFriendsList() {
        return userList;
    }

    public void setUserList(ArrayList<User> userList) {
        this.userList = userList;
    }

    public HashMap<String, User> getFriendsMap() {
        return userMap;
    }

    public void setUserMap(HashMap<String, User> userMap) {
        this.userMap = userMap;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
