package pl.statisfy.socketchat.project.app;

import android.content.Context;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import io.socket.client.Socket;

public class AppLifecycleObserver implements LifecycleObserver {

    private Context mContext;
    private Socket mSocket;

    public AppLifecycleObserver(Context context) {
        mContext = context;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onEnterForeground() {

        MyChatApplication app = (MyChatApplication) mContext;
        mSocket = app.getSocket();
        mSocket.connect();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onEnterBackground() {
        mSocket.disconnect();
    }

}