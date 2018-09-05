package com.example.feicui.testcamera;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.view.Surface;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class CameraAgentNew extends CameraAgent {

    private Context context;
    private CameraManager cameraManager;
    private CameraDevice cameraDevice;
    private CameraDevice.StateCallback deviceStateCallback;
    private CameraCaptureSession.StateCallback sessionStateCallback;
    private SurfaceTexture texture;
    private Handler handler;
    private PREVIEW_STATE preview_state = PREVIEW_STATE.NOT_START;
    private CameraCharacteristics cameraCharacteristics;
    private ImageReader.OnImageAvailableListener onImageAvailableListener;

    enum PREVIEW_STATE {NOT_START, REQUEST_START, STARTING, STARTED}

    private CameraCaptureSession cameraCaptureSession;
    private ImageReader imageReader;

    public Surface surface;


    CameraAgentNew(Context context) {
        this.context = context;
        cameraManager = (CameraManager) context.getSystemService(Service.CAMERA_SERVICE);
        try {
            cameraCharacteristics = cameraManager.getCameraCharacteristics("0");
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        HandlerThread thread = new HandlerThread("do camera");
        thread.start();
        handler = new Handler(thread.getLooper());

        deviceStateCallback = new CameraDevice.StateCallback() {
            @Override
            public void onOpened(CameraDevice camera) {
                cameraDevice = camera;
                if (preview_state == PREVIEW_STATE.REQUEST_START) {
                    startPreview();
                }
            }

            @Override
            public void onDisconnected(CameraDevice camera) {
                cameraDevice = null;
                preview_state = PREVIEW_STATE.NOT_START;
            }

            @Override
            public void onError(CameraDevice camera, int error) {
                Log.d(TAG, "camera error id = " + error);
                preview_state = PREVIEW_STATE.NOT_START;
                camera.close();
            }
        };
        sessionStateCallback = new CameraCaptureSession.StateCallback() {
            @Override
            public void onConfigured(CameraCaptureSession session) {
                cameraCaptureSession = session;
                try {
                    final CaptureRequest.Builder previewRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                    if (imageReader == null) {
                        StreamConfigurationMap map = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                        Size size = Collections.max(Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)), new CompareSizesByArea());
                        imageReader = ImageReader.newInstance(size.getWidth(), size.getHeight(), ImageFormat.JPEG, 2);
                    }
                    previewRequestBuilder.addTarget(surface);

                    previewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_AUTO);
                    previewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
                    cameraCaptureSession.setRepeatingRequest(previewRequestBuilder.build(), null, handler);
                    preview_state = PREVIEW_STATE.STARTED;
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onConfigureFailed(CameraCaptureSession session) {
                if (cameraCaptureSession == session)
                    cameraCaptureSession = null;
                preview_state = PREVIEW_STATE.NOT_START;
            }
        };
    }

    @Override
    public void openCamera() {
        Log.d(TAG, "openCamera");
        if (context.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            Log.d(TAG, "do not have permissions");
            return;
        }
        try {
            cameraManager.openCamera("0", deviceStateCallback, handler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startPreview() {
        if (texture == null || cameraDevice == null) {
            preview_state = PREVIEW_STATE.REQUEST_START;
            return;
        }
        if (imageReader == null) {
            StreamConfigurationMap map = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            Size size = Collections.max(Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)), new CompareSizesByArea());
            imageReader = ImageReader.newInstance(size.getWidth(), size.getHeight(), ImageFormat.JPEG, 2);
        }
        try {
            cameraDevice.createCaptureSession(Arrays.asList(surface,imageReader.getSurface()), sessionStateCallback, handler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        preview_state = PREVIEW_STATE.STARTING;
        Log.d(TAG, "startPreview");
    }

    @Override
    public void closeCamera() {
        if (preview_state != PREVIEW_STATE.NOT_START) {
            stopPreview();
        }
        if (cameraCaptureSession != null)
            cameraCaptureSession.close();
        cameraCaptureSession = null;
        if (cameraDevice != null)
            cameraDevice.close();
        cameraDevice = null;

        Log.d(TAG, "closeCamera");

    }

    @Override
    public void stopPreview() {
        Log.d(TAG, "stopPreview");
        preview_state = PREVIEW_STATE.NOT_START;
        if (cameraCaptureSession != null) {
            try {
                cameraCaptureSession.stopRepeating();
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void takePicture() {
        final CaptureRequest.Builder captureRequestBuilder;
        try {
            if (imageReader == null) {
                StreamConfigurationMap map = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                Size size = Collections.max(Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)), new CompareSizesByArea());
                imageReader = ImageReader.newInstance(size.getWidth(), size.getHeight(), ImageFormat.JPEG, 2);
            }
            if (onImageAvailableListener == null)
                onImageAvailableListener = new ImageReader.OnImageAvailableListener() {
                    @Override
                    public void onImageAvailable(ImageReader reader) {
                        Image image = reader.acquireLatestImage();
                        DataSaveImpl.saveImage(image);
                        Log.d(TAG, "save image over");
                    }
                };
            imageReader.setOnImageAvailableListener(onImageAvailableListener, handler);
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureRequestBuilder.addTarget(imageReader.getSurface());
            captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_AUTO);
            captureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
            captureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, 90);
            cameraCaptureSession.capture(captureRequestBuilder.build(), null, handler);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    static class CompareSizesByArea implements Comparator<Size> {
        @Override
        public int compare(Size o1, Size o2) {
            return Long.signum(o1.getHeight() * o1.getWidth() - o2.getHeight() * o2.getWidth());
        }
    }

    @Override
    public void startRecord() {

    }

    @Override
    public void stopRecord() {

    }

    @Override
    public void setParameter() {

    }

    @Override
    public void setPreview(SurfaceTexture texture) {
        if (this.texture != texture) {
            this.texture = texture;
            surface = new Surface(texture);
        }
    }

    @Override
    public void clearPreview(SurfaceTexture texture) {
        if (this.texture == texture) {
            this.texture = null;
            if (surface != null)
                surface = null;
        }
    }
}
