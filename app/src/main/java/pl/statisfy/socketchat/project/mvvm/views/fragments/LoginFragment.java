package pl.statisfy.socketchat.project.mvvm.views.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import pl.statisfy.socketchat.R;
import pl.statisfy.socketchat.R2;
import pl.statisfy.socketchat.project.mvvm.views.activities.BaseActivity;
import pl.statisfy.socketchat.project.mvvm.views.activities.MainActivity;
import pl.statisfy.socketchat.project.mvvm.views.activities.StartRegisterActivity;
import pl.statisfy.socketchat.project.mvvm.viewmodels.LoginRegisterViewModel;

public class LoginFragment extends BaseFragment {

    private static final String TAG = LoginFragment.class.getSimpleName();
    private final int SERVER_FAILURE = 2000;

    @BindView(R2.id.loginUserEmail)
    EditText mUserEmailEt;

    @BindView(R2.id.loginUserPassword)
    EditText mUserPasswordEt;

    @BindView(R2.id.loginRegister)
    Button mRegisterButton;

    @BindView(R2.id.loginSingIn)
    Button mLoginButton;

    @BindView(R2.id.fragment_login_progressBarLayout)
    RelativeLayout progressBarLayout;

    private Unbinder mUnbinder;
    private BaseActivity mBaseActivity;

    private LoginRegisterViewModel loginRegisterViewModel;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    private void displayToast(String s){
        Toast.makeText(getActivity(), "Login success: " + s, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        loginRegisterViewModel = ViewModelProviders.of(this).get(LoginRegisterViewModel.class);

        loginRegisterViewModel.getLoginLogoutStatus().observe(getViewLifecycleOwner(), loginStatus -> {
            if (loginStatus.isLoading) {
                showProgressBar();
            } else {
                hideProgressBar();
            }

            if (loginStatus.message != null) {
                displayToast(loginStatus.message);
            }

            if (loginStatus.success){
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                getActivity().finish();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        mUnbinder = ButterKnife.bind(this,view);

        return  view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mBaseActivity = (BaseActivity) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mBaseActivity = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @OnClick(R2.id.loginSingIn)
    public void onClickSignIn() {
        loginRegisterViewModel.login(mUserEmailEt.getText().toString(), mUserPasswordEt.getText().toString());
    }

    @OnClick(R2.id.loginRegister)
    public void onClickRegisterButton(){
        Intent intent = new Intent(getActivity(), StartRegisterActivity.class);
        startActivity(intent);
    }

    public void showProgressBar(){
        progressBarLayout.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar(){
        progressBarLayout.setVisibility(View.GONE);
    }

}
