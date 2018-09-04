package com.example.feicui.testcamera;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

public class MainCameraActivity extends Activity {
    protected Switch mDC_DV_Switch;
    protected Button mCaptureBtn;
    protected TextureView mPreviewView;
    protected BasicModule mCurrentModule;
    protected PhotoModule mPhotoModule;
    protected VideoModule mVideoModule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_camera);
        ActionBar bar = getActionBar();
        if (bar != null)
            bar.hide();

        initModule();
        initView();
        DataSaveImpl.parentPath = this.getFilesDir().getAbsolutePath();
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
        if (mCurrentModule != null)
            mCurrentModule.resume();
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
        mCaptureBtn = findViewById(R.id.capture_btn);

        mDC_DV_Switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
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
        });
        mPreviewView = findViewById(R.id.preview_view);
        mPreviewView.setSurfaceTextureListener(mPhotoModule);
    }

    public void switchModule(BasicModule newModule) {
        if (newModule.equals(mCurrentModule))
            return;

        mCurrentModule.pause();
        mCurrentModule = newModule;
        mCurrentModule.resume();

    }
}
