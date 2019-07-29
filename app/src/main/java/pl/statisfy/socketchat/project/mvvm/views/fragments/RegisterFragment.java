package pl.statisfy.socketchat.project.mvvm.views.fragments;

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
import pl.statisfy.socketchat.project.mvvm.views.activities.StartLoginActivity;
import pl.statisfy.socketchat.project.mvvm.viewmodels.LoginRegisterViewModel;

public class RegisterFragment extends BaseFragment {

    private static final String TAG = RegisterFragment.class.getSimpleName();

    @BindView(R2.id.registerUserName)
    EditText mUserNameEt;

    @BindView(R2.id.registerEmail)
    EditText mUserEmailEt;

    @BindView(R2.id.registerUserPassword)
    EditText mUserPasswordEt;

    @BindView(R2.id.registerButton)
    Button mRegisterButton;

    @BindView(R2.id.fragment_register_progressBarLayout)
    RelativeLayout progressBarLayout;

    private Unbinder mUnbinder;
    private LoginRegisterViewModel loginRegisterViewModel;

    public static RegisterFragment newInstance() {
        return new RegisterFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        loginRegisterViewModel = ViewModelProviders.of(this).get(LoginRegisterViewModel.class);

        loginRegisterViewModel.getRegisterResult().observe(getViewLifecycleOwner(), registerStatus -> {
            if (registerStatus.isLoading){
                showProgressBar();
            } else {
                hideProgressBar();
            }

            displayToast(registerStatus.message);

            if (registerStatus.isRegistered){
                Intent intent = new Intent(getActivity(), StartLoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                getActivity().finish();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @OnClick(R2.id.registerButton)
    public void onClickRegisterButton(){
        loginRegisterViewModel.register(mUserNameEt.getText().toString(), mUserEmailEt.getText().toString(), mUserPasswordEt.getText().toString());
    }

    public void showProgressBar(){
        progressBarLayout.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar(){
        progressBarLayout.setVisibility(View.GONE);
    }

    private void displayToast(String s){
        Toast.makeText(getActivity(), "Register success: " + s, Toast.LENGTH_SHORT).show();
    }

}

