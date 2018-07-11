package com.example.q.week2;

        import android.app.Application;

        import io.socket.client.IO;
        import io.socket.client.Socket;

        import java.net.URISyntaxException;

public class ChatApplication extends Application {

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket(Constants.CHAT_SERVER_URL);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private Socket mSocket2;
    {
        try {
            mSocket2 = IO.socket(Constants.WHITEBOARD_SERVER_URL);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
    private Socket mSocket3;
    {
        try {
            mSocket3 = IO.socket(Constants.CHAT_SERVER_URL);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public Socket getSocket() {
        return mSocket;
    }
    public Socket getSocket2() {return mSocket2;}
    public Socket getSocket3() {return mSocket3;}
}
