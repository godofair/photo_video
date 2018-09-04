package com.example.feicui.testcamera;

import android.graphics.SurfaceTexture;

public class CameraController {

    static private CameraController mCameraController;

    boolean isNewApi = false;
    private CameraAgent cameraAgent;
    private CameraAgentNew cameraAgentNew;
    private CameraAgentOld cameraAgentOld;

    static CameraController getInstance() {
        if (mCameraController == null)
            mCameraController = new CameraController();
        return mCameraController;
    }

    public void openCamera(boolean isNewApi) {
        if (isNewApi) {
            if (cameraAgentNew == null)
                cameraAgentNew = new CameraAgentNew();
            cameraAgent = cameraAgentNew;
        } else {
            if (cameraAgentOld == null)
                cameraAgentOld = new CameraAgentOld();
            cameraAgent = cameraAgentOld;
        }

        cameraAgent.openCamera();
    }

    public void startPreview() {
        if (cameraAgent != null)
            cameraAgent.startPreview();
    }

    public void stopPreview() {
        if (cameraAgent != null)
            cameraAgent.stopPreview();
    }

    public void closeCamera() {
        if (cameraAgent != null)
            cameraAgent.closeCamera();
    }

    public void takePicture() {
        if (cameraAgent != null)
            cameraAgent.takePicture();
    }

    public void startRecord() {
        if (cameraAgent != null)
            cameraAgent.startRecord();
    }

    public void stopRecord() {
        if (cameraAgent != null)
            cameraAgent.stopRecord();
    }

    public void setPreview(SurfaceTexture surfaceTexture) {
        if (cameraAgent != null)
            cameraAgent.setPreview(surfaceTexture);
    }



}
