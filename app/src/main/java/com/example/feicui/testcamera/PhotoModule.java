package com.example.feicui.testcamera;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.util.Log;
import android.view.TextureView;

public class PhotoModule extends BasicModule implements TextureView.SurfaceTextureListener {

    PhotoModule(Context context) {
        super(context);
        Log.d(TAG, "init");

    }

    @Override
    public void resume() {
        Log.d(TAG, "resume");
        CameraController.getInstance().openCamera(false);
        CameraController.getInstance().startPreview();
    }

    @Override
    public void pause() {
        Log.d(TAG, "pause");

        CameraController.getInstance().stopPreview();
        CameraController.getInstance().closeCamera();
    }

    @Override
    public void startAction() {
        CameraController.getInstance().takePicture();
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
        Log.d(TAG, "onSurfaceTextureAvailable");

        CameraController.getInstance().setPreview(surfaceTexture);
        CameraController.getInstance().startPreview();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {

        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

    }
}
