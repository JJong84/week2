package com.example.q.week2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.StringTokenizer;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class WhiteBoard extends Fragment {

    ArrayList<Point> points = new ArrayList<Point>();
    Button draw_red_btn, draw_blue_btn, draw_black_btn, clearbtn;
    LinearLayout drawlinear;
    int color = Color.BLACK;
    private MyView m;

    private Socket mSocket;
    private Socket mSocket2;
    private boolean isDrawer = false;
    private int saved_index=0;
//    public String CurrentProblem="n1";

    private Boolean isConnected = true;
    private static final String TAG = "WhiteBoardfragment";

    public void setisDrawertrue(){
        Log.d("ddiddi","setisDra");
        //isDrawer = true;

        EditText text = (EditText) getView().findViewById(R.id.editText2);
        text.setVisibility(View.VISIBLE);
        Button btn = (Button) getView().findViewById(R.id.button);
        btn.setVisibility(View.VISIBLE);
        Button btn2 = (Button) getView().findViewById(R.id.button);
        btn2.setVisibility(View.VISIBLE);

    }
    public void setisDrawerfalse(){
        isDrawer = false;

        EditText text = (EditText) getView().findViewById(R.id.editText2);
        text.setVisibility(View.GONE);
        Button btn = (Button) getView().findViewById(R.id.button);
        btn.setVisibility(View.GONE);
        Button btn2 = (Button) getView().findViewById(R.id.button);
        btn2.setVisibility(View.GONE);
    }

    public void setNextPlayer() {
        points.clear();
        m.invalidate();
        saved_index = 0;
        Log.d("ddiddi", "???");
        setisDrawerfalse();
        mSocket.emit("clear image");
    }

    public String getmSocketid(){
        return mSocket2.id();
    }

    class Point {
        float x;
        float y;
        boolean check;
        int color;

        public Point(float x, float y, boolean check, int color) {
            this.x = x;
            this.y = y;
            this.check = check;
            this.color = color;
        }
    }

    class MyView extends View {
        Canvas mCanvas = null;

        public MyView(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {

            mCanvas = canvas;
            Paint p = new Paint();
            p.setStrokeWidth(15);
            String inter= "";

            for (int i = 1; i < points.size(); i++) {
                if(saved_index<=i+2) {

                    if (points.get(i - 1).check == true) {
                        inter = inter + Float.toString(points.get(i - 1).x) + "!" + Float.toString(points.get(i - 1).y) + "!" + "1" + "!" + points.get(i - 1).color;
                        saved_index++;
                    }
                    else {
                        inter = inter + Float.toString(points.get(i - 1).x) + "!" + Float.toString(points.get(i - 1).y) + "!" + "0" + "!" + points.get(i - 1).color;
                        saved_index++;
                    }

                    if (i != points.size() - 1)
                        inter = inter + '!';
                    else {
                        inter = inter+"!";
                        if (points.get(i).check == true) {
                            inter = inter + Float.toString(points.get(i).x) + "!" + Float.toString(points.get(i).y) + "!" + "1" + "!" + points.get(i).color;

                        }
                        else {
                            inter = inter + Float.toString(points.get(i).x) + "!" + Float.toString(points.get(i).y) + "!" + "0" + "!" + points.get(i).color;

                        }
                    }
                }
                p.setColor(points.get(i).color);
                if (!points.get(i).check)
                    continue;
                canvas.drawLine(points.get(i - 1).x, points.get(i - 1).y, points.get(i).x, points.get(i).y, p);
            }

            if(isDrawer) {
                Log.d("aaaaaa",points.size()+"");
                mSocket.emit("new Image", inter);
            }
            else
                Log.d("aaaaaa",points.size()+"");
//            else
//                points.clear();
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if(isDrawer) {
                float x = event.getX();
                float y = event.getY();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        points.add(new Point(x, y, false, color));
                    case MotionEvent.ACTION_MOVE:
                        points.add(new Point(x, y, true, color));
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }
                invalidate();
            }
            return true;
        }

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ChatApplication app = (ChatApplication) getActivity().getApplication();
        mSocket = app.getSocket2();
        mSocket2 = app.getSocket3();

        mSocket.on(Socket.EVENT_CONNECT, onConnect);
        mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.on("clear image", onClear);
        mSocket.on("new Image2", onNewImage);
        mSocket.on("full Image", onFullImage);
        mSocket2.on("user joined", onUserJoined);
        mSocket2.on("user left", onUserLeft);
        mSocket2.on("get users", onGetUsers);
        mSocket2.on("correct", onCorrect);
        mSocket2.on("clear image", onClearEnd);


//        mSocket.on("stop typing", onStopTyping);
        mSocket.connect();
        mSocket2.connect();


    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mSocket.disconnect();
        mSocket2.disconnect();

        mSocket.off(Socket.EVENT_CONNECT, onConnect);
        mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.off("clear image", onClear);
        mSocket.off("new Image2", onNewImage);
        mSocket.off("full Image", onFullImage);
        mSocket2.off("user joined", onUserJoined);
        mSocket2.off("user left", onUserLeft);
        mSocket2.off("get users", onGetUsers);
        mSocket2.off("correct", onCorrect);
        mSocket2.off("clear image", onClearEnd);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        m = new MyView(getActivity());
        /* ----- 색 변경 ------ */
        view.findViewById(R.id.draw_red_btn).setBackgroundColor(0);
        view.findViewById(R.id.draw_black_btn).setBackgroundColor(0);
        view.findViewById(R.id.draw_green_btn).setBackgroundColor(0);
        view.findViewById(R.id.draw_yellow_btn).setBackgroundColor(0);
        view.findViewById(R.id.draw_blue_btn).setBackgroundColor(0);
        view.findViewById(R.id.draw_red_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                color = Color.RED;
            }
        });
        view.findViewById(R.id.draw_blue_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                color = Color.BLUE;
            }
        });
        view.findViewById(R.id.draw_black_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                color = Color.BLACK;
            }
        });
        view.findViewById(R.id.draw_green_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                color = Color.GREEN;
            }
        });
        view.findViewById(R.id.draw_yellow_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                color = Color.YELLOW;
            }
        });
        view.findViewById(R.id.draw_white_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                color = Color.WHITE;
            }
        });

        view.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText text = (EditText) getView().findViewById(R.id.editText2);
                Button btn = (Button) getView().findViewById(R.id.button);

                mSocket2.emit("set answer",text.getText().toString());

                btn.setVisibility(View.GONE);
                text.setVisibility(View.GONE);
                isDrawer = true;
            }
        });

        view.findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText text = (EditText) getView().findViewById(R.id.editText2);
                Button btn = (Button) getView().findViewById(R.id.button);

                btn.setVisibility(View.VISIBLE);
                text.setVisibility(View.VISIBLE);
                isDrawer = false;
            }
        });


        clearbtn = (Button) view.findViewById(R.id.clearbtn);
        drawlinear = (LinearLayout) view.findViewById(R.id.draw_linear);
        clearbtn.setOnClickListener(new View.OnClickListener() { //지우기 버튼 눌렸을때
            @Override
            public void onClick(View v) {
                if(isDrawer) {
                    points.clear();
                    m.invalidate();
                    saved_index = 0;
                    mSocket.emit("clear image");
                }

            }
        });
        drawlinear.addView(m);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_white_board, container, false);
    }

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!isConnected) {
                        mSocket.emit("get image");
                        Toast.makeText(getActivity().getApplicationContext(),
                                R.string.connect, Toast.LENGTH_LONG).show();
                        isConnected = true;
                        mSocket2.emit("get users");
                    }
                }
            });
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "diconnected");
                    isConnected = false;
                    setisDrawerfalse();
                    points.clear();
                    m.invalidate();
                    Toast.makeText(getActivity().getApplicationContext(),
                            R.string.disconnect, Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG, "Error connecting");
                    Toast.makeText(getActivity().getApplicationContext(),
                            R.string.error_connect, Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    private Emitter.Listener onNewImage = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    float x, y, x1, y1;
                    int z, z1, w;
                    String inter;
                    try {

                        inter = data.getString("x");
                        //Log.d("????",inter);
                        StringTokenizer s = new StringTokenizer(inter);
                        while(s.hasMoreTokens()) {
                            x = Float.parseFloat(s.nextToken("!"));
                            y = Float.parseFloat(s.nextToken("!"));
                            z = Integer.parseInt(s.nextToken("!"));
                            w = Integer.parseInt(s.nextToken("!"));
                            if(z==1)
                                points.add(new Point(x, y, true, w));
                            else
                                points.add(new Point(x, y, false, w));
                            //saved_index++;
                        }
                        m.invalidate();

                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                        return;
                    }
                }
            });
        }
    };

    private Emitter.Listener onUserJoined = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    int numUsers;
                    try {
                        username = data.getString("username");
                        numUsers = data.getInt("numUsers");
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                        return;
                    }
                    if(isDrawer){
                        String inter="";
                        for (int i = 1; i < points.size(); i++) {
                            if (points.get(i - 1).check == true) {
                                inter = inter + Float.toString(points.get(i - 1).x) + "!" + Float.toString(points.get(i - 1).y) + "!" + "1" + "!" + points.get(i - 1).color;
                            }
                            else {
                                inter = inter + Float.toString(points.get(i - 1).x) + "!" + Float.toString(points.get(i - 1).y) + "!" + "0" + "!" + points.get(i - 1).color;
                            }
                            if (i != points.size() - 1)
                                inter = inter + '!';
                            else {
                                inter = inter+"!";
                                if (points.get(i).check == true) {
                                    inter = inter + Float.toString(points.get(i).x) + "!" + Float.toString(points.get(i).y) + "!" + "1" + "!" + points.get(i).color;
                                }
                                else {
                                    inter = inter + Float.toString(points.get(i).x) + "!" + Float.toString(points.get(i).y) + "!" + "0" + "!" + points.get(i).color;
                                }
                            }
                        }
                        mSocket.emit("full Image", inter);
                    }

                }
            });
        }
    };


    private Emitter.Listener onUserLeft = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    int numUsers;
                    try {
                        username = data.getString("username");
                        numUsers = data.getInt("numUsers");
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                        return;
                    }

                    if(numUsers ==1){
                        Log.d("ddiddi","onuserleft");
                        setisDrawertrue();
                    }


                }
            });
        }
    };

    private Emitter.Listener onClear = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    points.clear();
                    m.invalidate();
                    saved_index = 0;
                }
            });
        }
    };

    private Emitter.Listener onClearEnd = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    points.clear();
                    m.invalidate();
                    saved_index = 0;
                    setisDrawerfalse();
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Someone solved" , Toast.LENGTH_LONG).show();
                }
            });
        }
    };
    private Emitter.Listener onGetUsers = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    int numUsers;
                    try {
                        numUsers = data.getInt("numUsers");
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                        return;
                    }

                    if(numUsers ==1) {
                        Log.d("ddiddi","ongetusers");
                        setisDrawertrue();
                    }
                }
            });
        }
    };

    private Emitter.Listener onFullImage = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    if (points.size() == 0) {
                        float x, y;
                        int z, w;
                        String inter;
                        try {
                            inter = data.getString("x");
                            //Log.d("????",inter);
                            StringTokenizer s = new StringTokenizer(inter);
                            while (s.hasMoreTokens()) {
                                x = Float.parseFloat(s.nextToken("!"));
                                y = Float.parseFloat(s.nextToken("!"));
                                z = Integer.parseInt(s.nextToken("!"));
                                w = Integer.parseInt(s.nextToken("!"));
                                if (z == 1)
                                    points.add(new Point(x, y, true, w));
                                else
                                    points.add(new Point(x, y, false, w));
                            }
                            m.invalidate();

                        } catch (JSONException e) {
                            Log.e(TAG, e.getMessage());
                            return;
                        }
                    }
                }
            });
        }
    };

    private Emitter.Listener onCorrect = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("ddiddi","onCorrect");
                        setisDrawertrue();
                }
            });
        }
    };

//    private Emitter.Listener onNewMessage = new Emitter.Listener() {
//        @Override
//        public void call(final Object... args) {
//            getActivity().runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    JSONObject data = (JSONObject) args[0];
//                    String username;
//                    String message;
//                    try {
//                        username = data.getString("username");
//                        message = data.getString("message");
//                    } catch (JSONException e) {
//                        Log.e(TAG, e.getMessage());
//                        return;
//                    }
//                    if(message.equals(CurrentProblem)){
//                        points.clear();
//                        m.invalidate();
//                        saved_index = 0;
//                        mSocket.emit("clear image");
//                        isDrawer = false;
//                    }
//                }
//            });
//        }
//    };
}
