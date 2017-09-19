package com.zxing.support.library.camera;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Build;
import android.util.Log;
import android.view.SurfaceHolder;

import com.google.zxing.PlanarYUVLuminanceSource;
import com.zxing.support.library.QRCodeSupport;

import java.io.IOException;


/**
 * 照相机的管理类
 *
 * @author bingbing
 * @date 15/9/22
 */
public class CameraManager {

    private static final String TAG = CameraManager.class.getSimpleName();
    static final int SDK_INT;

    static {
        int sdkInt;
        try {
            sdkInt = Integer.parseInt(Build.VERSION.SDK);
        } catch (NumberFormatException nfe) {
            // Just to be safe
            sdkInt = 10000;
        }
        SDK_INT = sdkInt;
    }

    private Context mContext;
    private CameraConfig mCameraConfig;
    private AutoFucesManager mAutoFucesManager;
    private Camera mCamera;

    private Rect framingRectInPreview;
    private Rect framingRect;
    private QRCodeSupport.Builder mBuidler;

    public CameraManager(QRCodeSupport.Builder builder, Context context) {
        this.mContext = context;
        this.mBuidler = builder;
        mCameraConfig = new CameraConfig(context);
        mAutoFucesManager = new AutoFucesManager();
    }


    public CameraConfig getCameraConfig() {
        return mCameraConfig;
    }


    /**
     * 相机是否打开
     *
     * @return
     */
    public boolean isOpen() {
        return mCamera != null;
    }

    /**
     * 打开相机
     *
     * @throws IOException
     */
    public void openDevice() throws IOException {
        if (mCamera == null) {
            mCamera = Camera.open();
            if (mCamera == null) throw new IOException("camera not open");
        }
    }

    public void initCameraParameter(SurfaceHolder surfaceHolder, int width, int height, int rotation) {
        if (mCamera == null) return;

        try {
            mCameraConfig.configCamera(mCamera, width, height, rotation);
            mCamera.setPreviewDisplay(surfaceHolder);
            mAutoFucesManager.setCamera(mCamera);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 停止预览
     */
    public void stopPreview() {
        if (mCamera != null) {
            mAutoFucesManager.stop();
            mCamera.stopPreview();
        }
    }

    /**
     * 关闭设备
     */
    public void closeDriver() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    /**
     * 响应预览图像
     *
     * @param previewCallback
     */
    public void requestPreview(Camera.PreviewCallback previewCallback) {
        if (mCamera != null)
            mCamera.setOneShotPreviewCallback(previewCallback);
    }

    /**
     * 开始预览
     */
    public void startPreview() {
        if (mCamera != null)
            mCamera.startPreview();
    }

    public void setAutoFucesListener(AutoFucesListener autoFucesListener) {
        mAutoFucesManager.start(autoFucesListener);
    }

    public Rect getFramingRectInPreview(boolean horizontal) {
        Rect rect = new Rect(getFramingRect());
        Point cameraResolution = mCameraConfig.getCameraResolution();
        Point screenResolution = mCameraConfig.getScreenResolution();

        if (horizontal) {
            rect.left = rect.left * cameraResolution.x / screenResolution.x;
            rect.right = rect.right * cameraResolution.x / screenResolution.x;
            rect.top = rect.top * cameraResolution.y / screenResolution.y;
            rect.bottom = rect.bottom * cameraResolution.y / screenResolution.y;

        } else {
            rect.left = rect.left * cameraResolution.y / screenResolution.x;
            rect.right = rect.right * cameraResolution.y / screenResolution.x;
            rect.top = rect.top * cameraResolution.x / screenResolution.y;
            rect.bottom = rect.bottom * cameraResolution.x / screenResolution.y;

        }


        framingRectInPreview = rect;
        Log.d(TAG, "cameraResolution:" + cameraResolution.toString());
        Log.d(TAG, "screenResolution:" + screenResolution.toString());
        Log.d(TAG, "framingRectInPreview:" + framingRectInPreview.toString());
        return framingRectInPreview;
    }

    public Rect getFramingRect() {
        framingRect = new Rect(mBuidler.getScanRectLeft()
                , mBuidler.getScanRectTop()
                , mBuidler.getScanRectLeft() + mBuidler.getScanRectWidth()
                , mBuidler.getScanRectTop() + mBuidler.getScanRectHeight());
        Log.d(TAG, "Calculated framing rect: " + framingRect);
        return framingRect;
    }

    public PlanarYUVLuminanceSource buildLuminanceSource(byte[] rotatedData, int width, int height, boolean horizontal) {
        Rect rect = getFramingRectInPreview(horizontal);
        int previewFormat = mCameraConfig.getPreviewFormat();
        String previewFormatString = mCameraConfig.getPreviewFormatString();
        switch (previewFormat) {
            // This is the standard Android format which all devices are REQUIRED to support.
            // In theory, it's the only one we should ever care about.
            case PixelFormat.YCbCr_420_SP:
                // This format has never been seen in the wild, but is compatible as we only care
                // about the Y channel, so allow it.
            case PixelFormat.YCbCr_422_SP:
                return new PlanarYUVLuminanceSource(rotatedData, width, height, rect.left, rect.top,
                        rect.width(), rect.height(), horizontal);
            default:
                // The Samsung Moment incorrectly uses this variant instead of the 'sp' version.
                // Fortunately, it too has all the Y data up front, so we can read it.
                if ("yuv420p".equals(previewFormatString)) {
                    return new PlanarYUVLuminanceSource(rotatedData, width, height, rect.left, rect.top,
                            rect.width(), rect.height(), horizontal);
                }
        }
        throw new IllegalArgumentException("Unsupported picture format: " +
                previewFormat + '/' + previewFormatString);

    }

    /**
     * 切换相机的闪光灯
     *
     * @return
     */
    public boolean toggleFlashLight() {
        if (mCamera != null) {
            try {
                Camera.Parameters mParameters = mCamera.getParameters();
                if (isOpenFlashLight()) { //已经打开状态
                    mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                } else {
                    mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                }
                mCamera.setParameters(mParameters);
                return true;
            } catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * @return
     */
    public boolean isOpenFlashLight() {
        if (mCamera != null) {
            Camera.Parameters mParameters = mCamera.getParameters();
            String flashMode = mParameters.getFlashMode();
            if (Camera.Parameters.FLASH_MODE_TORCH.equals(flashMode)) { //已经打开状态
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean isZoomSupported() {
        if (mCamera == null) return false;
        return mCamera.getParameters().isZoomSupported();
    }

    public void setZoom(int zoom) {
        if (!isZoomSupported()){
            Log.d("setZoom","false");
            return;
        }
        try {
            Camera.Parameters params = mCamera.getParameters();
            final int MAX = params.getMaxZoom();
            if(MAX==0)return;

            int zoomValue = params.getZoom();
            zoomValue += zoom;
            params.setZoom(zoomValue);
            mCamera.setParameters(params);

        }catch (Exception e){
            Log.d(TAG,"变焦异常");
        }

    }
}
