package com.example.feicui.testcamera;

import android.graphics.SurfaceTexture;

public abstract class CameraAgent {
    protected String TAG = this.getClass().getName();

    abstract public void openCamera();

    abstract public void startPreview();

    abstract public void closeCamera();

    abstract public void stopPreview();

    abstract public void takePicture();

    abstract public void startRecord();

    abstract public void stopRecord();

    abstract public void setParameter();

    abstract public void setPreview(SurfaceTexture texture);

    abstract public void clearPreview(SurfaceTexture texture);
}
