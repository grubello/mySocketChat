package pl.statisfy.socketchat.project.mvvm.model;

import android.util.Patterns;

import androidx.databinding.BaseObservable;

public class LoginUser extends BaseObservable {

    private String strEmailAddress;
    private String strPassword;

    public LoginUser(){}

    public LoginUser(String EmailAddress, String Password) {
        strEmailAddress = EmailAddress;
        strPassword = Password;
    }

    public String getStrEmailAddress() {
        return strEmailAddress;
    }

    public String getStrPassword() {
        return strPassword;
    }

    public boolean isEmailValid() {
        return Patterns.EMAIL_ADDRESS.matcher(getStrEmailAddress()).matches();
    }


    public boolean isPasswordLengthGreaterThan5() {
        return getStrPassword().length() > 5;
    }

}