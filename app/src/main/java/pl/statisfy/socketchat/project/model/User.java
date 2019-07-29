package pl.statisfy.socketchat.project.model;

public class User {

    private final static String TAG = User.class.getSimpleName();

    private String email;
    private String userPicture;
    private String userName;
    private boolean hasLoggedIn;

    public User(){}

    public User(String email, String userPicture, String userName) {
        this.email = email;
        this.userPicture = userPicture;
        this.userName = userName;
    }

    public User(String email, String userPicture, String userName, boolean hasLoggedIn) {
        this.email = email;
        this.userPicture = userPicture;
        this.userName = userName;
        this.hasLoggedIn = hasLoggedIn;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserPicture() {
        return userPicture;
    }

    public void setUserPicture(String userPicture) {
        this.userPicture = userPicture;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isHasLoggedIn() {
        return hasLoggedIn;
    }

    public void setHasLoggedIn(boolean hasLoggedIn) {
        this.hasLoggedIn = hasLoggedIn;
    }
}
