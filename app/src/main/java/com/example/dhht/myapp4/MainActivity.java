package com.example.dhht.myapp4;

import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.media.VolumeShaper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback{
    private Button mBtnStart,mBtnStop;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private Camera mCamera;

    private static final String TAG = "信息：";
    private int mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;   //默认后置摄像头
    private int mCameraWidth,mCameraHeight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        initView();
        initData();
        initListener();
    }

    private void initView() {
        mBtnStart = findViewById(R.id.btn_start);
        mBtnStop = findViewById(R.id.btn_stop);
        mSurfaceView = findViewById(R.id.sv_camera);
    }

    private void initData() {
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
    }

    private void initListener() {
        mSurfaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "surfaceView", Toast.LENGTH_SHORT).show();
            }
        });
        mBtnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                destroyCamera();
                Toast.makeText(MainActivity.this, "拍照", Toast.LENGTH_SHORT).show();
            }
        });
        mBtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchCamera();
                Toast.makeText(MainActivity.this, "button", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        initCamera();
        initPara();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        openCamera();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        destroyCamera();
    }

    private void initCamera() {
        if (null != mCamera) {
            mCamera.stopPreview();//停掉原来摄像头的预览
            mCamera.release();//释放资源
            mCamera = null;//取消原来摄像头
        }
        try {
            mCamera = Camera.open(mCameraId);//打开当前选中的摄像头
            mCamera.setPreviewDisplay(mSurfaceHolder);//通过surfaceview显示取景画面
        } catch (Exception e) {
            if (null != mCamera) {
                mCamera.release();
                mCamera = null;
            }
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "启动摄像头失败,请开启摄像头权限", Toast.LENGTH_SHORT).show();
        }

        mCamera.setPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                Camera.Size size = mCamera.getParameters().getPreviewSize();
                Log.i(TAG,"width=="+size.width+",height=="+size.height+",data的大小=="+data.length);
            }
        });
    }

    private void initPara() {
        mCameraWidth = 1280; mCameraHeight = 720;
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPreviewSize(mCameraWidth,mCameraHeight);
        parameters.setPictureSize(mCameraWidth,mCameraHeight);
        parameters.setPreviewFormat(ImageFormat.NV21);
        parameters.setRotation(90);
        mCamera.setDisplayOrientation(90);
        //获得摄像头支持的数据格式
        List<Integer> list = parameters.getSupportedPreviewFormats();
        for(Integer val:list){
            Log.i(TAG,"val=="+val);
        }
        //选择合适的预览尺寸
        List<Camera.Size> sizeList = parameters.getSupportedPreviewSizes();
        if( sizeList.size() > 1 ){
            Iterator<Camera.Size> itor = sizeList.iterator();
            while (itor.hasNext()){
                Camera.Size cur = itor.next();
                Log.i(TAG,"width = "+cur.width+",height = "+cur.height);
            }
        }
        mCamera.setParameters(parameters);
    }

    private void openCamera(){
        mCamera.startPreview();//开始预览
        mCamera.setDisplayOrientation(90);//将预览旋转90度
        mCameraId = 1;
    }

    private void destroyCamera(){
        if(mCamera == null){
            return;
        }
        mCamera.setPreviewCallback(null);
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
    }

    private void switchCamera() {
        //切换前后摄像头
        int cameraCount = 0;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();//得到摄像头的个数
        for (int i = 0; i < cameraCount; i++) {
            Camera.getCameraInfo(i, cameraInfo);//得到每一个摄像头的信息
            if (cameraCount >= 2 && mCameraId == 1) {
                //现在是后置，变更为前置
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {//代表摄像头的方位，CAMERA_FACING_FRONT前置      CAMERA_FACING_BACK后置
                    initCamera();
                    initPara();
                    openCamera();
                    mCameraId = 0;
                    break;
                }
            } else {
                //现在是前置， 变更为后置
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {//代表摄像头的方位，CAMERA_FACING_FRONT前置      CAMERA_FACING_BACK后置
                    initCamera();
                    initPara();
                    openCamera();
                    mCameraId = 1;
                    break;
                }
            }

        }
    }












}
