package com.example.feicui.testcamera;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.util.Log;
import android.view.TextureView;

public class VideoModule extends BasicModule implements TextureView.SurfaceTextureListener{

    private boolean isRecording = false;

    VideoModule(Context context) {
        super(context);
        Log.d(TAG, "init");
        isRecording = false;

    }

    @Override
    public void resume() {
        Log.d(TAG, "resume");
        CameraController.getInstance().openCamera(mContext,false);
        CameraController.getInstance().startPreview();
        isRecording = false;
    }

    @Override
    public void pause() {
        Log.d(TAG, "pause");
        CameraController.getInstance().stopPreview();
        CameraController.getInstance().closeCamera();
        isRecording = false;
    }

    @Override
    public void startAction() {
        if (isRecording){
            CameraController.getInstance().stopRecord();
            isRecording = false;
        }else {
            CameraController.getInstance().startRecord();
            isRecording = true;
        }
    }

    /**
     * Invoked when a {@link TextureView}'s SurfaceTexture is ready for use.
     *
     * @param surfaceTexture The surface returned by
     *                {@link TextureView#getSurfaceTexture()}
     * @param width   The width of the surface
     * @param height  The height of the surface
     */
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        Log.d(TAG, "onSurfaceTextureAvailable");
        CameraController.getInstance().setPreview(surfaceTexture);
        CameraController.getInstance().startPreview();

    }

    /**
     * Invoked when the {@link SurfaceTexture}'s buffers size changed.
     *
     * @param surfaceTexture The surface returned by
     *                {@link TextureView#getSurfaceTexture()}
     * @param width   The new width of the surface
     * @param height  The new height of the surface
     */
    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {

    }

    /**
     * Invoked when the specified {@link SurfaceTexture} is about to be destroyed.
     * If returns true, no rendering should happen inside the surface texture after this method
     * is invoked. If returns false, the client needs to call {@link SurfaceTexture#release()}.
     * Most applications should return true.
     *
     * @param surfaceTexture The surface about to be destroyed
     */
    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        return false;
    }

    /**
     * Invoked when the specified {@link SurfaceTexture} is updated through
     * {@link SurfaceTexture#updateTexImage()}.
     *
     * @param surfaceTexture The surface just updated
     */
    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

    }
}
