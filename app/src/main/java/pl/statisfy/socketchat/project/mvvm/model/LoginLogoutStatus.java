package pl.statisfy.socketchat.project.mvvm.model;

public class LoginLogoutStatus {

    public boolean success;
    public boolean isLoading;
    public String message;
    public String token;
    public String email;
    public String photo;
    public String displayName;

    public LoginLogoutStatus(){}

    public void refresh(){
        this.success = false;
        this.isLoading = false;
        this.message = null;
        this.token = null;
        this.email = null;
        this.photo = null;
        this.displayName = null;
    }

}
