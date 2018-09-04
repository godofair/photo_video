package com.example.feicui.testcamera;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.IOException;

public class CameraAgentOld extends CameraAgent implements MediaRecorder.OnInfoListener, MediaRecorder.OnErrorListener {
    private Camera camera;

    private SurfaceTexture texture;
    private CamcorderProfile camcorderProfile;

    private MediaRecorder mediaRecorder;
    private Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] bytes, Camera camera) {
            DataSaveImpl.saveImage(bytes);
            Log.d(TAG, "pictureCallback saveimage over");
            camera.startPreview();
        }
    };

    @Override
    public void openCamera() {
        Log.d(TAG, "openCamera");
        camera = Camera.open();
    }

    @Override
    public void startPreview() {
        if (texture == null || camera == null)
            return;
        try {
            camera.setPreviewTexture(texture);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "start preview w = " + camera.getParameters().getPreviewSize().width+" h = " + camera.getParameters().getPreviewSize().height);
        camera.startPreview();
    }

    @Override
    public void closeCamera() {
        if (mediaRecorder != null) {
            mediaRecorder.reset();
            mediaRecorder.release();
            mediaRecorder = null;
        }
        try {
            camera.setPreviewTexture(null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        camera.stopPreview();
        camera.release();
        camera = null;
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
        if (mediaRecorder == null)
            mediaRecorder = new MediaRecorder();

        mediaRecorder.reset();
        if (camcorderProfile == null)
            camcorderProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_480P);
        if (camera != null) {
//            camera.setDisplayOrientation(90);
            camera.stopPreview();
            Camera.Parameters p = camera.getParameters();
            Log.d(TAG, "startRecord change setting before w = " + p.getPreviewSize().width+" h = " + p.getPreviewSize().height);

            p.setPreviewSize(camcorderProfile.videoFrameWidth,camcorderProfile.videoFrameHeight);

            camera.setParameters(p);
            p = camera.getParameters();

            Log.d(TAG, "startRecord change setting after w = " + p.getPreviewSize().width+" h = " + p.getPreviewSize().height);
            try {
                camera.setPreviewTexture(texture);
            } catch (IOException e) {
                e.printStackTrace();
            }
            camera.unlock();
            mediaRecorder.setCamera(camera);
        }

        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);


        mediaRecorder.setProfile(camcorderProfile);

//        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setVideoSize(camcorderProfile.videoFrameWidth,camcorderProfile.videoFrameHeight);
        Log.d(TAG,"set video size  w = " + camcorderProfile.videoFrameWidth + " h = " + camcorderProfile.videoFrameHeight);
//        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
//        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
        mediaRecorder.setCaptureRate(30);

        mediaRecorder.setOrientationHint(90);
        mediaRecorder.setOutputFile(DataSaveImpl.getNextVideoFileName());
        mediaRecorder.setMaxDuration(60 * 1000);
        mediaRecorder.setOnInfoListener(this);
        mediaRecorder.setOnErrorListener(this);

        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaRecorder.start();
        Log.d(TAG, "start record");
    }

    @Override
    public void stopRecord() {
        if (mediaRecorder != null) {
            Log.d(TAG, "stop record");
            mediaRecorder.stop();
            mediaRecorder.reset();
        }
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

    /**
     * Called when an error occurs while recording.
     *
     * @param mr    the MediaRecorder that encountered the error
     * @param what  the type of error that has occurred:
     *              <ul>
     *              <li>{@link MediaRecorder#MEDIA_RECORDER_ERROR_UNKNOWN}
     *              <li>{@link MediaRecorder#MEDIA_ERROR_SERVER_DIED}
     *              </ul>
     * @param extra an extra code, specific to the error type
     */
    @Override
    public void onError(MediaRecorder mr, int what, int extra) {
        Log.d(TAG, "media record error = " + what);
    }

    /**
     * Called to indicate an info or a warning during recording.
     *
     * @param mr    the MediaRecorder the info pertains to
     * @param what  the type of info or warning that has occurred
     *              <ul>
     *              <li>{@link MediaRecorder#MEDIA_RECORDER_INFO_UNKNOWN}
     *              <li>{@link MediaRecorder#MEDIA_RECORDER_INFO_MAX_DURATION_REACHED}
     *              <li>{@link MediaRecorder#MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED}
     *              </ul>
     * @param extra an extra code, specific to the info type
     */
    @Override
    public void onInfo(MediaRecorder mr, int what, int extra) {
        Log.d(TAG, "media record info = " + what);
        if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED || what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED) {
            stopRecord();
        }
    }
}
