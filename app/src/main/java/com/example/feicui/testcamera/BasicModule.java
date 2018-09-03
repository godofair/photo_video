package com.example.feicui.testcamera;

import android.content.Context;

public abstract class BasicModule {
    protected Context mContext;

    BasicModule(Context context) {
        mContext = context;
    }

    abstract public void resume();

    abstract public void pause();

    abstract public void startAction();
}
