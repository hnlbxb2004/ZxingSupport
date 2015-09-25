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

import java.io.IOException;


/**
 * 照相机的管理类
 * @author bingbing
 * @date 15/9/22
 */
public class CameraManager {

    private static final String TAG = CameraManager.class.getSimpleName();

    private static final int MIN_FRAME_WIDTH = 240;
    private static final int MIN_FRAME_HEIGHT = 240;
    private static final int MAX_FRAME_WIDTH = 675;
    private static final int MAX_FRAME_HEIGHT = 675;

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

    public CameraManager(Context context) {
        this.mContext = context;
        mCameraConfig = new CameraConfig(context);
        mAutoFucesManager = new AutoFucesManager();
    }



    public CameraConfig getCameraConfig(){
        return mCameraConfig;
    }


    /**
     * 相机是否打开
     * @return
     */
    public boolean isOpen() {
        return mCamera != null;
    }

    /**
     * 打开相机
     * @param surfaceHolder
     */
    public void openDevice(SurfaceHolder surfaceHolder) throws IOException {
        if (mCamera == null){
            mCamera = Camera.open();
            if (mCamera == null) throw  new IOException();

            mCamera.setPreviewDisplay(surfaceHolder);
            mCameraConfig.configCamera(mCamera);
            mAutoFucesManager.setCamera(mCamera);

        }

    }

    /**
     * 停止预览
     */
    public void stopPreview() {
        if (mCamera != null){
            mCamera.stopPreview();
            mAutoFucesManager.stop();
        }
    }

    /**
     * 关闭设备
     */
    public void closeDriver() {
        if (mCamera != null){
            mCamera.release();
            mCamera = null;
        }
    }

    /**
     * 响应预览图像
     * @param previewCallback
     */
    public void requestPreview(Camera.PreviewCallback previewCallback) {
        mCamera.setOneShotPreviewCallback(previewCallback);
    }

    /**
     * 开始预览
     */
    public void startPreview() {
        mCamera.startPreview();
    }

    public void setAutoFucesListener(AutoFucesListener autoFucesListener){
        mAutoFucesManager.start(autoFucesListener);
    }

    public Rect getFramingRectInPreview() {
        if (framingRectInPreview == null) {
            Rect rect = new Rect(getFramingRect());
            Point cameraResolution = mCameraConfig.getCameraResolution();
            Point screenResolution = mCameraConfig.getScreenResolution();
            //modify here
//      rect.left = rect.left * cameraResolution.x / screenResolution.x;
//      rect.right = rect.right * cameraResolution.x / screenResolution.x;
//      rect.top = rect.top * cameraResolution.y / screenResolution.y;
//      rect.bottom = rect.bottom * cameraResolution.y / screenResolution.y;
            rect.left = rect.left * cameraResolution.y / screenResolution.x;
            rect.right = rect.right * cameraResolution.y / screenResolution.x;
            rect.top = rect.top * cameraResolution.x / screenResolution.y;
            rect.bottom = rect.bottom * cameraResolution.x / screenResolution.y;
            framingRectInPreview = rect;
            Log.e(TAG,"cameraResolution:"+cameraResolution.toString());
            Log.e(TAG,"screenResolution:"+screenResolution.toString());
            Log.e(TAG,"framingRectInPreview:"+framingRectInPreview.toString());
        }
        return framingRectInPreview;
    }

    public Rect getFramingRect() {
        Point screenResolution = mCameraConfig.getScreenResolution();
        if (framingRect == null) {
            if (mCamera == null) {
                return null;
            }
            int width = screenResolution.x * 3 / 4;
            if (width < MIN_FRAME_WIDTH) {
                width = MIN_FRAME_WIDTH;
            } else if (width > MAX_FRAME_WIDTH) {
                width = MAX_FRAME_WIDTH;
            }
            int height = screenResolution.y * 3 / 4;
            if (height < MIN_FRAME_HEIGHT) {
                height = MIN_FRAME_HEIGHT;
            } else if (height > MAX_FRAME_HEIGHT) {
                height = MAX_FRAME_HEIGHT;
            }
            int leftOffset = (screenResolution.x - width) / 2;
            int topOffset = (screenResolution.y - height) / 2;
            framingRect = new Rect(leftOffset, topOffset, leftOffset + width, topOffset + height);
            Log.d(TAG, "Calculated framing rect: " + framingRect);
        }
        return framingRect;
    }


    public PlanarYUVLuminanceSource buildLuminanceSource(byte[] rotatedData, int width, int height) {
        Rect rect = getFramingRectInPreview();
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
                        rect.width(), rect.height(),false);
            default:
                // The Samsung Moment incorrectly uses this variant instead of the 'sp' version.
                // Fortunately, it too has all the Y data up front, so we can read it.
                if ("yuv420p".equals(previewFormatString)) {
                    return new PlanarYUVLuminanceSource(rotatedData, width, height, rect.left, rect.top,
                            rect.width(), rect.height(),false);
                }
        }
        throw new IllegalArgumentException("Unsupported picture format: " +
                previewFormat + '/' + previewFormatString);

    }

}
