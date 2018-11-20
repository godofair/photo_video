package com.example.feicui.testcamera;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.TextView;


public class TextTimer extends TextView {

    private int second = 0;

    private static final int ACTION_START = 0;
    private static final int ACTION_STOP = 1;
    private static final int ACTION_REFRESH = 2;
    boolean needStop = false;
    private Handler handler;

    private TimerThread timerThread;

    public TextTimer(Context context) {
        super(context);
    }

    public TextTimer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TextTimer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TextTimer(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                int msgid = msg.what;
                switch (msgid) {
                    case ACTION_START:
                        needStop = false;
                        setText(second);
                        break;
                    case ACTION_STOP:
                        needStop = true;
                    case ACTION_REFRESH:
                        setText(++second);
                        break;
                }
            }
        };
    }

    class TimerThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (!needStop) {

            }
        }
    }
}
