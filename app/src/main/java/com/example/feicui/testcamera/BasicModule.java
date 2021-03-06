package com.example.feicui.testcamera;

import android.content.Context;

public abstract class BasicModule {

    protected String TAG = this.getClass().getName();

    protected boolean newApi = false;

    protected Context mContext;

    BasicModule(Context context) {
        mContext = context;
    }

    abstract public void resume();

    abstract public void pause();

    abstract public int startAction();

    public void setNewApi(boolean newApi){
        this.newApi = newApi;
    }

    public void restart(){
        pause();
        resume();
    }
}
