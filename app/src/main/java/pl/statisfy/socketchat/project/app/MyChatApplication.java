package pl.statisfy.socketchat.project.app;

import android.app.Application;
import android.widget.Toast;

import androidx.lifecycle.ProcessLifecycleOwner;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import pl.statisfy.socketchat.project.utils.Utils;

public class MyChatApplication extends Application {

    private static Socket mSocket;
    private static MyChatApplication myChatApplication;

    @Override
    public void onCreate() {
        super.onCreate();

        myChatApplication = this;

        // Observer to detect if the app is in background or foreground.
        AppLifecycleObserver lifeCycleObserver
                = new AppLifecycleObserver(getApplicationContext());

        // Adding the above observer to process lifecycle
        ProcessLifecycleOwner.get()
                .getLifecycle()
                .addObserver(lifeCycleObserver);

        //setup socket
        try {
            mSocket = IO.socket(Utils.SOCKET_ADDRESS);
            mSocket.io().timeout(10000);
        } catch (URISyntaxException e) {
            Toast.makeText(getApplicationContext(), "Failed to connect to server.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        //setup Firebase persistance
        if (!FirebaseApp.getApps(this).isEmpty()) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }
    }

    public static MyChatApplication getInstance(){
        return myChatApplication;
    }

    public Socket getSocket(){
        return mSocket;
    }

}
