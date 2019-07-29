package pl.statisfy.socketchat.project.mvvm.model;

import java.util.ArrayList;

public class Result {

    public String message = "";
    public boolean success = false;
    public ArrayList data = new ArrayList();

    public Result(){

    }

    public void clear(){
        this.message = "";
        this.success = false;
        this.data.clear();
    }

}
