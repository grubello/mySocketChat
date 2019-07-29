package pl.statisfy.socketchat.project.mvvm.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pl.statisfy.socketchat.project.model.User;

public class RequestReceivedResult{

    public HashMap<String, User> requestReceivedMap;
    public List<User> requestReceivedList;

    public RequestReceivedResult(){}

    public void clear(){
        if(requestReceivedMap == null) {
            requestReceivedMap = new HashMap<>();
        } else {
            requestReceivedMap.clear();
        }

        if(requestReceivedList == null){
            requestReceivedList = new ArrayList<>();
        } else {
            requestReceivedList.clear();
        }

    }

    public HashMap<String, User> getRequestReceivedMap() {
        return requestReceivedMap;
    }

    public List<User> getRequestReceivedList() {
        return requestReceivedList;
    }

    public void setRequestReceivedMap(HashMap<String, User> requestReceivedMap) {
        this.requestReceivedMap = requestReceivedMap;
    }

    public void setRequestReceivedList(List<User> requestReceivedList) {
        this.requestReceivedList = requestReceivedList;
    }
}
