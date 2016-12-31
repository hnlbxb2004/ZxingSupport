package com.zxing.support.library.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

/**
 * Created by xubingbing on 2016/11/5.
 */

public class DeviceUtils {

    /**
     * 获取当前屏幕旋转角度
     *
     * @param activity
     * @return 0表示是竖屏; 90表示是左横屏; 180表示是反向竖屏; 270表示是右横屏
     */
    public static int getDisplayRotation(Activity activity) {
        if(activity == null)
            return 0;

        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        switch (rotation) {
            case Surface.ROTATION_0:
                return 0;
            case Surface.ROTATION_90:
                return 90;
            case Surface.ROTATION_180:
                return 180;
            case Surface.ROTATION_270:
                return 270;
        }
        return 0;
    }

    public static boolean isOrtation(Activity activity){
        int rotation = getDisplayRotation(activity);
        if (rotation == 0 || rotation == 180){
            return false;
        }else {
            return true;
        }
    }

    /**
     * 获取屏幕的尺寸
     * @param context
     * @return
     */
    public static Point getScreenSize(Context context){
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point screenResolution = new Point(display.getWidth(), display.getHeight());
        return screenResolution;
    }

}
