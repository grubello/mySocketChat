package pl.statisfy.socketchat.project.api;

import android.net.Uri;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.Observable;
import io.socket.client.Ack;
import io.socket.client.Socket;
import pl.statisfy.socketchat.project.app.MyChatApplication;
import pl.statisfy.socketchat.project.mvvm.model.LoginLogoutStatus;
import pl.statisfy.socketchat.project.mvvm.model.RegisterResult;
import pl.statisfy.socketchat.project.mvvm.model.Result;
import pl.statisfy.socketchat.project.utils.Utils;

public class AccountAPIService {

    private static final String TAG = AccountAPIService.class.getSimpleName();
    private LoginLogoutStatus loginStatus, logoutStatus;
    private RegisterResult registerResult;
    private Result uploadResult;
    private static Socket socket = MyChatApplication.getInstance().getSocket();
    private static AccountAPIService INSTANCE;

    public AccountAPIService(){
        logoutStatus = new LoginLogoutStatus();
        loginStatus = new LoginLogoutStatus();
        registerResult = new RegisterResult();
        uploadResult = new Result();
    }

    public static AccountAPIService getInstance(){
        if (INSTANCE == null) {
            INSTANCE = new AccountAPIService();
        }
        return INSTANCE;
    }

    public Observable<LoginLogoutStatus> attemptLogin(String useremail, String password){
        return Observable.create(emitter ->
                FirebaseAuth.getInstance().signInWithEmailAndPassword(useremail, password)
                .addOnCanceledListener(() -> {
                    loginStatus.success = false;
                    loginStatus.isLoading = false;
                    loginStatus.message = "Firebase sign in canceled";
                    emitter.onNext(loginStatus);
                })
                .addOnFailureListener(e -> {
                    loginStatus.success = false;
                    loginStatus.isLoading = false;
                    loginStatus.message = "Firebase sign in failure: " + e.getMessage();
                    emitter.onNext(loginStatus);
                })
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        loginStatus.success = false;
                        loginStatus.isLoading = false;
                        loginStatus.message = "Firebase task error: " + task.getException();
                        emitter.onNext(loginStatus);
                    } else {
                        try {
                            JSONObject data = new JSONObject();
                            data.put("email", useremail);
                            data.put("password", password);

                            socket.emit("userLoginData", data).on("token", args -> {

                                JSONObject jsonObject = (JSONObject) args[0];

                                try {
                                    String token = (String) jsonObject.get("token");
                                    String displayName = (String) jsonObject.get("displayName");
                                    String photo = (String) jsonObject.get("photo");
                                    String email = (String) jsonObject.get("email");

                                    FirebaseAuth.getInstance().signInWithCustomToken(token)
                                            .addOnCompleteListener(task1 -> {
                                                if(!task1.isSuccessful()){
                                                    loginStatus.isLoading = false;
                                                    loginStatus.message = "Login error: " + task1.getException();
                                                } else {
                                                    loginStatus.isLoading = false;
                                                    loginStatus.token = token;
                                                    loginStatus.message = "Login successful";
                                                    loginStatus.displayName = displayName;
                                                    loginStatus.email = email;
                                                    loginStatus.photo = photo;
                                                    loginStatus.success = true;

                                                    //TODO
                                                    FirebaseDatabase.getInstance().getReference()
                                                            .child(Utils.FIREBASE_PATH_TOKEN).child(Utils.encodeEmail(useremail))
                                                            .child(FirebaseInstanceId.getInstance().getId()).child("token").setValue(token);

                                                    emitter.onNext(loginStatus);
                                                    emitter.onComplete();
                                                }
                                            })
                                            .addOnFailureListener(e -> emitter.onError(e))
                                            .addOnCanceledListener(() -> {
                                                    loginStatus.success = false;
                                                    loginStatus.message = "Login canceled";
                                                    emitter.onNext(loginStatus);
                                                    emitter.onComplete();
                                            });


                                } catch (JSONException e) {
                                    loginStatus.isLoading = false;
                                    loginStatus.message = "JSON error: " + e.getMessage();
                                    emitter.onNext(loginStatus);
                                    emitter.onComplete();
                                    e.printStackTrace();
                                }
                            })
                              .on(Socket.EVENT_CONNECT_ERROR, args -> {
                                  loginStatus.isLoading = false;
                                  loginStatus.message = "Socket error: " + args[0];
                                  emitter.onNext(loginStatus);
                                  emitter.onComplete();
                              })
                              .on(Socket.EVENT_CONNECT_TIMEOUT, args -> {
                                  loginStatus.isLoading = false;
                                  loginStatus.message = "Socket error: " + args[0];
                                  emitter.onNext(loginStatus);
                                  emitter.onComplete();
                              });

                        } catch (JSONException e) {
                            emitter.onError(e);
                            Log.i(TAG, e.getMessage());
                        }
                    }
                }));
    }

    public Observable<RegisterResult> attemptRegister(String username, String useremail, String password){
            return Observable.create(emitter -> {

                JSONObject data = new JSONObject();
                data.put("email", useremail);
                data.put("username", username);
                data.put("password", password);

                socket.emit("registerUserData", data).on("message",
                        args -> {
                            try {
                                JSONObject jsonObject = (JSONObject) args[0];
                                String responseText = (String) jsonObject.get("messageText");

                                if (responseText.equals("success")){
                                    registerResult.isRegistered = true;
                                    registerResult.message = "User registered successfully";
                                    emitter.onNext(registerResult);
                                } else {
                                    registerResult.isRegistered = false;
                                    registerResult.message = "User registration error: " + responseText;
                                    emitter.onNext(registerResult);
                                }
                                emitter.onComplete();

                            } catch (JSONException e){
                                Log.i(TAG, e.getMessage());
                                emitter.onError(e);
                            }
                        })
                        .on(Socket.EVENT_CONNECT_ERROR, args -> {
                            registerResult.isLoading = false;
                            registerResult.message = "Socket error: " + args[0];
                            emitter.onNext(registerResult);
                            emitter.onComplete();})
                        .on(Socket.EVENT_CONNECT_TIMEOUT, args -> {
                            registerResult.isLoading = false;
                            registerResult.message = "Socket error: " + args[0];
                            emitter.onNext(registerResult);
                            emitter.onComplete();});
            });
    }

    public Observable<LoginLogoutStatus> signOut(String currentUserEmail){
        return Observable.create(emitter -> {

            DatabaseReference tokenReference = FirebaseDatabase.getInstance().getReference()
                    .child(Utils.FIREBASE_PATH_TOKEN).child(Utils.encodeEmail(currentUserEmail));

            tokenReference.child(FirebaseInstanceId.getInstance().getId()).removeValue();

            FirebaseAuth.getInstance().signOut();

            logoutStatus.isLoading = false;
            logoutStatus.message = "Sign out successful";
            logoutStatus.success = true;

            emitter.onNext(logoutStatus);
            emitter.onComplete();
        });
    }

    public Observable<Result> uploadUserProfilePhoto(Uri photoUri, String currentUserEmail, byte[] photoByteArray){
        return Observable.create(emitter -> {

            uploadResult.clear();

            if (photoByteArray == null || photoUri == null){
                return;
            }

            StorageReference storageReference = FirebaseStorage.getInstance().getReference(Utils.FIREBASE_PATH_USER_PROFILE_PICTURES)
                    .child(Utils.encodeEmail(currentUserEmail))
                    .child(photoUri.getLastPathSegment());

            DatabaseReference userReference = FirebaseDatabase.getInstance().getReference(Utils.FIREBASE_PATH_USERS)
                    .child(Utils.encodeEmail(currentUserEmail));

            UploadTask uploadTask = storageReference.putBytes(photoByteArray);

            uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    emitter.onError(task.getException());
                    throw task.getException();
                }
                return storageReference.getDownloadUrl();
            }).addOnCompleteListener(task -> {

                if (task.isSuccessful()) {

                    Uri downloadUri = task.getResult();
                    uploadResult.data.add(downloadUri.toString());
                    userReference.child("userPicture").setValue(downloadUri.toString());

                    try {
                        JSONObject data = new JSONObject();
                        data.put("userEmail", currentUserEmail);
                        data.put("userPhoto", downloadUri.toString());

                        socket.emit("updatePhoto", data, (Ack) args -> {
                            uploadResult.success = true;
                            uploadResult.message = "Upload photo success";
                            emitter.onNext(uploadResult);
                        })
                        .on(Socket.EVENT_CONNECT_ERROR, args -> {
                            uploadResult.success = false;
                            uploadResult.message = "Socket error: " + args[0];

                            emitter.onNext(uploadResult);
                            emitter.onComplete();
                        })
                        .on(Socket.EVENT_CONNECT_TIMEOUT, args -> {
                            uploadResult.success = false;
                            uploadResult.message = "Socket error: " + args[0];

                            emitter.onNext(uploadResult);
                            emitter.onComplete();
                        });

                    } catch (JSONException e){
                        emitter.onError(e);
                        Log.i(TAG, "JSON exception: " + e.getMessage());
                    }
                } else {
                    emitter.onError(task.getException());
                    Log.i(TAG, "onFailure: " + task.getResult());
                }
            });
        });
    }
}
