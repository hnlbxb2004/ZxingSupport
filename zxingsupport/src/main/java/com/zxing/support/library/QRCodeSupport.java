package com.zxing.support.library;

import android.app.Activity;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.zxing.support.library.camera.AutoFucesListener;
import com.zxing.support.library.camera.CameraManager;
import com.zxing.support.library.qrcode.QRCodeCameraDecode;
import com.zxing.support.library.utils.DeviceUtils;
import com.zxing.support.library.view.QRCodeFindView;

import java.io.IOException;

/**
 * zxing 扫码的主要入口
 *
 * @author bingbing
 * @date 15/9/22
 */
public class QRCodeSupport {


    private static final String TAG = QRCodeSupport.class.getSimpleName();
    private SurfaceView mSurfaceView;
    private QRCodeFindView mQRCodeFindView;
    private OnScanResultListener mOnScanResultListener;
    private OnErrorListener mOnErrorListener;
    private OnTestScanRectListener mTestScanRectListener;
    private CameraManager mCameraManager;
    private QRCodeCameraDecode mCameraDecode;
    private Builder mBuilder;
    private boolean isPrivew;


    public QRCodeSupport(Builder builder, SurfaceView surfaceView, QRCodeFindView findView) {
        this.mBuilder = builder;
        this.mSurfaceView = surfaceView;
        this.mQRCodeFindView = findView;
        mCameraManager = new CameraManager(mBuilder, surfaceView.getContext());
        mQRCodeFindView.setCamanerManager(mCameraManager);
    }


    public void setScanResultListener(OnScanResultListener onScanResultListener) {
        this.mOnScanResultListener = onScanResultListener;
    }

    public void setOnErrotListener(OnErrorListener onErrorListener) {
        this.mOnErrorListener = onErrorListener;
    }

    public void setOnTestScanRectListener(OnTestScanRectListener onTestScanRectListener) {
        this.mTestScanRectListener = onTestScanRectListener;
    }


    private AutoFucesListener mAutoFucesListener = new AutoFucesListener() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {

        }
    };

    /**
     * 处理预览
     */
    private final Camera.PreviewCallback mPreviewCallback = new Camera.PreviewCallback() {

        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            CameraDecodeTask mCameraDecodeTask = new CameraDecodeTask();
            boolean isOrtation = DeviceUtils.isOrtation((Activity) mSurfaceView.getContext());
            byte[] oranation = new byte[]{(byte) (isOrtation ? 1 : 0)};
            mCameraDecodeTask.execute(data, oranation);
        }
    };

    private SurfaceHolder.Callback mSurfaceViewCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            openCamera();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            isPrivew = true;
            initCamera(holder, width, height, DeviceUtils.getDisplayRotation((Activity) (mSurfaceView.getContext())));
            mCameraManager.requestPreview(mPreviewCallback);
            mCameraManager.startPreview();
            mCameraManager.setAutoFucesListener(mAutoFucesListener);
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            mCameraManager.stopPreview();
            mCameraManager.closeDriver();
            isPrivew = false;
        }
    };


    private void openCamera() {
        if (mCameraManager.isOpen()) {
            return;
        }
        try {
            mCameraManager.openDevice();
        } catch (IOException e) {
            if (mOnErrorListener != null) {
                mOnErrorListener.onOpenCameraError(e);
            }
        }

    }


    public void initCamera(SurfaceHolder surfaceHolder, int width, int height, int rotation) {
        mCameraManager.initCameraParameter(surfaceHolder, width, height, rotation);
    }

    /**
     * 在activity 的onResume 中调用
     */
    public void onResume() {
        SurfaceHolder surfaceHolder = mSurfaceView.getHolder();
        surfaceHolder.addCallback(mSurfaceViewCallback);
    }

    /**
     * 在activity 的onPause 中调用
     */
    public void onPause() {
        isPrivew = false;
        SurfaceHolder surfaceHolder = mSurfaceView.getHolder();
        surfaceHolder.removeCallback(mSurfaceViewCallback);

    }

    public void onDestory() {
        mCameraManager.stopPreview();
        mCameraManager.closeDriver();
        isPrivew = false;
    }

    private class CameraDecodeTask extends AsyncTask<byte[], Void, QRCodeCameraDecode.CameraDecodeResult> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mCameraDecode == null) {
                mCameraDecode = new QRCodeCameraDecode(mCameraManager, mQRCodeFindView);
            }
        }

        @Override
        protected void onPostExecute(QRCodeCameraDecode.CameraDecodeResult result) {
            super.onPostExecute(result);

            if (mTestScanRectListener != null) {
                mTestScanRectListener.onScanRect(result.getDecodeByte());
            }

            if (isPrivew)
                mCameraManager.requestPreview(mPreviewCallback);

            if (result.getDecodeResult() == null) { // 未解析出来，重新解析
                Log.d(TAG, "onPostExecute isPriview:" + isPrivew);
            } else { //解析出来
                String resultString = result.getDecodeResult().getText();
                if (!TextUtils.isEmpty(resultString) && mOnScanResultListener != null) {

                    mOnScanResultListener.onScanResult(resultString, result.getDecodeByte());
                }
            }
        }

        @Override
        protected QRCodeCameraDecode.CameraDecodeResult doInBackground(byte[]... params) {

            return mCameraDecode.decode(params[0],params[1][0] == 1);
        }
    }


    public interface OnScanResultListener {
        /**
         * 扫描结果的监听
         *
         * @param notNullResult 解析结果的字符串,不会为null
         * @param resultByte    解析结果的byte数组,
         */
        void onScanResult(String notNullResult, byte[] resultByte);
    }

    /**
     * 测试的扫描区域的一个监听
     */
    public interface OnTestScanRectListener {

        /**
         * 测试扫描区域的数据
         *
         * @param rectByte
         */
        void onScanRect(byte[] rectByte);
    }


    public interface OnErrorListener {
        /**
         * 相机打开异常
         *
         * @param e
         */
        void onOpenCameraError(Exception e);
    }


    public static class Builder {
        /**
         * 扫描区域距离屏幕左边的距离
         */
        private int scanRectLeft;

        /**
         * 扫描区域距离屏幕上面的距离
         */
        private int scanRectTop;

        /**
         * 扫描区域的宽度
         */
        private int scanRectWidth;

        /**
         * 扫描区域的高度
         */
        private int scanRectHeight;

        public void setScanRect(int left, int top, int width, int height) {
            this.scanRectLeft = left;
            this.scanRectTop = top;
            this.scanRectWidth = width;
            this.scanRectHeight = height;
        }

        public int getScanRectLeft() {
            return scanRectLeft;
        }

        public int getScanRectTop() {
            return scanRectTop;
        }

        public int getScanRectWidth() {
            return scanRectWidth;
        }

        public int getScanRectHeight() {
            return scanRectHeight;
        }
    }

}
