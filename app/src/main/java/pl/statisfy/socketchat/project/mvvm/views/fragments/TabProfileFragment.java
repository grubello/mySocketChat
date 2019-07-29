package pl.statisfy.socketchat.project.mvvm.views.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProviders;

import com.squareup.picasso.Picasso;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import pl.statisfy.socketchat.BuildConfig;
import pl.statisfy.socketchat.R;
import pl.statisfy.socketchat.R2;
import pl.statisfy.socketchat.project.mvvm.repository.UserDataManager;
import pl.statisfy.socketchat.project.mvvm.viewmodels.ProfileViewModel;
import pl.statisfy.socketchat.project.mvvm.views.activities.StartLoginActivity;

import static android.app.Activity.RESULT_OK;

public class TabProfileFragment extends BaseFragment {

    @BindView(R2.id.profileLogoutButton) Button signOutButton;
    @BindView(R2.id.fragment_profile_userPicture) ImageView userPicture;
    @BindView(R2.id.fragment_profile_imgPicture) ImageView imgPicture;
    @BindView(R2.id.fragment_profile_camPicture) ImageView camPicture;
    @BindView(R2.id.fragment_profile_userName) TextView userName;
    @BindView(R2.id.fragment_profile_userEmail) TextView userEmail;

    private static final int CAMERA_REQUEST_CODE = 111;
    private static final int CAMERA_PERMISSION_CODE = 222;
    private static final int IMAGE_REQUEST_CODE = 333;
    private static final int WRITE_STORAGE_PERMISSION_CODE = 444;
    private static final int READ_STORAGE_PERMISSION_CODE = 444;
    private static final String TAG = TabProfileFragment.class.getSimpleName();

    private Uri camPhotoTempUri;
    private Unbinder mUnbinder;
    private String currentUserEmail;
    private String currentUserName;

    private ProfileViewModel viewModel;

    public static TabProfileFragment newInstance(){
        return new TabProfileFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentUserEmail = UserDataManager.getInstance().getUserEmail();
        currentUserName = UserDataManager.getInstance().getUserName();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);

        viewModel.getUserDetails().observe(getViewLifecycleOwner(), user -> {
            currentUserEmail = user.getEmail();
            currentUserName = user.getUserName();

            Picasso.get().load(user.getUserPicture()).into(userPicture);
            userName.setText(currentUserName);
            userEmail.setText(currentUserEmail);

        });

        viewModel.getCreatedFile().observe(getViewLifecycleOwner(), this::pickFromCameraToFile);

        viewModel.getUploadedPhoto().observe(getViewLifecycleOwner(), uploadResult -> {
           if(uploadResult.success){
               Picasso.get().load((String) uploadResult.data.get(0)).into(userPicture);
           }
        });

        viewModel.getSignOutStatus().observe(getViewLifecycleOwner(), logoutStatus -> {
             if(logoutStatus.success){
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
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                viewModel.createImageFileForCamUpload();
            } else {
                Toast.makeText(getActivity(), "Camera permission denied", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == WRITE_STORAGE_PERMISSION_CODE){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickPhotoFromGallery();
            } else {
                Toast.makeText(getActivity(), "Read/write storage permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult: " +requestCode + " " + resultCode + " " + data);
        if (resultCode == RESULT_OK && requestCode == CAMERA_REQUEST_CODE){
            viewModel.uploadProfilePhoto(camPhotoTempUri);
        }
        if (resultCode == RESULT_OK && requestCode == IMAGE_REQUEST_CODE){
            Uri selectedImage = data.getData();
            viewModel.uploadProfilePhoto(selectedImage);
        }
    }

    @OnClick(R2.id.profileLogoutButton)
    public void onClickSignOutButton(){
        viewModel.signOut();
    }

    @OnClick(R2.id.fragment_profile_imgPicture)
    public void onClickImgPicture(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_STORAGE_PERMISSION_CODE);
            } else {
                pickPhotoFromGallery();
            }
        } else {
            pickPhotoFromGallery();
        }
    }

    @OnClick(R2.id.fragment_profile_camPicture)
    public void onClickCamPicture() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
            }
            else {
                viewModel.createImageFileForCamUpload();
            }
        } else {
            viewModel.createImageFileForCamUpload();
        }
    }

    private void pickPhotoFromGallery(){
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        pickPhoto.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(Intent.createChooser(pickPhoto, "Choose image with..."),IMAGE_REQUEST_CODE);
    }

    private void pickFromCameraToFile(File file){
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        camPhotoTempUri = FileProvider.getUriForFile(getActivity(),BuildConfig.APPLICATION_ID + ".provider", file);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, camPhotoTempUri);
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
    }

}
