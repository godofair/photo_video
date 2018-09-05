package com.example.feicui.testcamera;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import static android.content.pm.PackageManager.PERMISSION_DENIED;

public class MainCameraActivity extends Activity {
    protected Switch mDC_DV_Switch;
    protected Switch mApiSwitch;
    protected Button mCaptureBtn;
    protected TextureView mPreviewView;
    protected BasicModule mCurrentModule;
    protected PhotoModule mPhotoModule;
    protected VideoModule mVideoModule;

    protected String[] permission = {Manifest.permission.CAMERA};

    private String TAG = this.getClass().getSimpleName();

    protected CompoundButton.OnCheckedChangeListener switchCheckedChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_camera);
        ActionBar bar = getActionBar();
        if (bar != null)
            bar.hide();

        initModule();
        initView();
    }

    public void initModule() {
        if (mCurrentModule != null)
            return;

        if (mPhotoModule == null)
            mPhotoModule = new PhotoModule(this);

        mCurrentModule = mPhotoModule;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            requestPermissions(permission,1);
            return;
        }
        if (mCurrentModule != null)
            mCurrentModule.resume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i = 0; i < permissions.length ;i++){
            if (Manifest.permission.CAMERA.equals(permissions[i]) && grantResults[i] == PERMISSION_DENIED){
                Log.d(TAG,"do not have permission");
            }
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mCurrentModule != null)
            mCurrentModule.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void onCaptureClick(View view) {
        mCurrentModule.startAction();
    }

    public void initView() {
        mDC_DV_Switch = findViewById(R.id.switch_dc_dv);
        mApiSwitch = findViewById(R.id.switch_api);
        mCaptureBtn = findViewById(R.id.capture_btn);

        switchCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton == mDC_DV_Switch){
                    onCheckModeChange(b);
                }
                if (compoundButton == mApiSwitch){
                    onCheckApiChange(b);
                }
            }
        };
        mApiSwitch.setOnCheckedChangeListener(switchCheckedChangeListener);
        mDC_DV_Switch.setOnCheckedChangeListener(switchCheckedChangeListener);
        mPreviewView = findViewById(R.id.preview_view);
        mPreviewView.setSurfaceTextureListener(mPhotoModule);
    }

    private void onCheckApiChange(boolean b){
        if (b){
            mApiSwitch.setText(R.string.api2);
            //change api2
        }else {
            mApiSwitch.setText(R.string.api1);
            //change api1
        }
        mCurrentModule.setNewApi(b);
        mCurrentModule.restart();
    }

    private void onCheckModeChange(boolean b){
        if (b) {
            mDC_DV_Switch.setText(R.string.photo);
            //start photo
            if (mPhotoModule == null)
                mPhotoModule = new PhotoModule(MainCameraActivity.this);
            switchModule(mPhotoModule);
        } else {
            mDC_DV_Switch.setText(R.string.video);
            //start video
            if (mVideoModule == null)
                mVideoModule = new VideoModule(MainCameraActivity.this);
            switchModule(mVideoModule);
        }

    }

    public void switchModule(BasicModule newModule) {
        if (newModule.equals(mCurrentModule))
            return;
        mCurrentModule.pause();
        mCurrentModule = newModule;
        mCurrentModule.resume();
    }
}
