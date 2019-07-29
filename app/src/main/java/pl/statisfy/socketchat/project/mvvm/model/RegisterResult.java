package pl.statisfy.socketchat.project.mvvm.model;

public class RegisterResult {

    public boolean isLoading = false;
    public boolean isRegistered = false;
    public String message;

    public RegisterResult(){}

    public void refresh(){
        this.isLoading = false;
        this.message = null;
        this.isRegistered = false;
    }
}
