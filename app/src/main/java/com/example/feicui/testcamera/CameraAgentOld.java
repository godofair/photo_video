package com.example.feicui.testcamera;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;

import java.io.IOException;

public class CameraAgentOld extends CameraAgent {
    private Camera camera;

    private SurfaceTexture texture;
    private Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] bytes, Camera camera) {
            DataSaveImpl.saveImage(bytes);
        }
    };

    @Override
    public void openCamera() {
        Log.d(TAG, "openCamera");
        camera = Camera.open();
    }

    @Override
    public void startPreview() {
        if (texture == null)
            return;
        Log.d(TAG, "start preview");
        try {
            camera.setPreviewTexture(texture);
        } catch (IOException e) {
            e.printStackTrace();
        }
        camera.startPreview();
    }

    @Override
    public void closeCamera() {
        camera.release();
    }

    @Override
    public void stopPreview() {
        Log.d(TAG, "stop preview");
        camera.stopPreview();

    }

    @Override
    public void takePicture() {
        Log.d(TAG, "take picture");
        camera.takePicture(null, null, pictureCallback);
    }

    @Override
    public void startRecord() {

    }

    @Override
    public void stopRecord() {

    }

    @Override
    public void setParameter() {
        Log.d(TAG, "set parameter");
//        camera.setDisplayOrientation(180);

        Camera.Parameters p = camera.getParameters();
        p.setPreviewSize(1280, 720);
        camera.setParameters(p);
    }

    @Override
    public void setPreview(SurfaceTexture texture) {
        Log.d(TAG, "set preview  texture");
        this.texture = texture;
    }

    @Override
    public void clearPreview(SurfaceTexture texture) {
        if (this.texture == texture)
            this.texture = null;
    }
}
