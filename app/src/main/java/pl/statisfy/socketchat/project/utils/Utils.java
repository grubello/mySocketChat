package pl.statisfy.socketchat.project.utils;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

import pl.statisfy.socketchat.project.model.User;

public class Utils {

    //Constants
    public static final String IP_ADDRESS = "192.168.8.105";
    public static final String PORT = "3000";
    public static final String SOCKET_ADDRESS = "http://" + IP_ADDRESS + ":" + PORT;

    public static final String PREFS = "MYCHAT_PREFS";
    public static final String USER_EMAIL = "USER_EMAIL";
    public static final String USER_NAME = "USER_NAME";
    public static final String USER_PHOTO = "USER_PHOTO";
    public static final String USER_TOKEN = "user_token";

    public static final String FIREBASE_PATH_TOKEN = "tokens";
    public static final String FIREBASE_PATH_USERS = "users";
    public static final String FIREBASE_PATH_SEND_REQUEST_SENT = "friendRequestSent";
    public static final String FIREBASE_PATH_SEND_REQUEST_RECEIVED = "friendRequestReceived";
    public static final String FIREBASE_PATH_USER_FRIENDS = "friends";
    public static final String FIREBASE_PATH_USER_MESSAGES = "userMessages";
    public static final String FIREBASE_PATH_USER_NEWMESSAGES = "userNewMessages";
    public static final String FIREBASE_PATH_USER_CHATROOMS = "chatrooms";
    public static final String FIREBASE_PATH_USER_PROFILE_PICTURES = "userProfilePictures";

    public static final String FRIEND_DETAILS = "friend_details";
    public static final String EXTRA_FRIEND_DETAILS = "extra_friend_details";


    //Utils methods
    public static String encodeEmail(String email){
        return email.replace('.',',');
    }

    public static boolean isIncludedInMap(HashMap<String, User> userHashMap, User user){
        return userHashMap != null && userHashMap.size() != 0 && userHashMap.containsKey(user.getEmail());
    }

    public static byte[] getBitmapByteArray(ContentResolver contentResolver, Uri bitmapUri, int size, int compressRatio){
        Bitmap bitmap;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(contentResolver, bitmapUri);
            int bitmapWidth = bitmap.getWidth();
            int bitmapHeight = bitmap.getHeight();
            int newHeight = bitmapHeight * size/bitmapWidth;

            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap,size,newHeight, true);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG,compressRatio, byteArrayOutputStream);

            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
