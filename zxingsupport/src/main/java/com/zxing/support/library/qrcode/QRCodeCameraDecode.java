package com.zxing.support.library.qrcode;

import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.ResultPointCallback;
import com.google.zxing.common.HybridBinarizer;
import com.zxing.support.library.camera.CameraManager;

import java.util.Hashtable;
import java.util.Vector;

/**
 * 照相机预览解码
 * @author bingbing
 * @date 15/9/22
 */
public class QRCodeCameraDecode {

    private static final String TAG = QRCodeCameraDecode.class.getSimpleName();

    private  Hashtable<DecodeHintType, Object> hints;
    private CameraManager mCameraManger;
    private MultiFormatReader multiFormatReader;

    public QRCodeCameraDecode(CameraManager cameraManager,ResultPointCallback resultPointCallback){
        this.mCameraManger = cameraManager;
        hints = new Hashtable<>(3);

        Vector<BarcodeFormat> decodeFormats = new Vector<>();
        decodeFormats.addAll(QRCodeDecodeFormat.ONE_D_FORMATS);
        decodeFormats.addAll(QRCodeDecodeFormat.QR_CODE_FORMATS);
        decodeFormats.addAll(QRCodeDecodeFormat.DATA_MATRIX_FORMATS);
        decodeFormats.addAll(QRCodeDecodeFormat.PRODUCT_FORMATS);
        hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);
        if (resultPointCallback != null) {
            hints.put(DecodeHintType.NEED_RESULT_POINT_CALLBACK, resultPointCallback);
        }

        multiFormatReader = new MultiFormatReader();
        multiFormatReader.setHints(hints);
    }

    /**
     * Decode the data within the viewfinder rectangle, and time how long it took. For efficiency,
     * reuse the same reader objects from one decode to the next.
     *
     * @param data   The YUV preview frame.
     */
    public Result decode(byte[] data) {
        int width = mCameraManger.getCameraConfig().getCameraResolution().x;
        int height = mCameraManger.getCameraConfig().getCameraResolution().y;
        Result rawResult = null;

        //modify here
        byte[] rotatedData = new byte[data.length];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++)
                rotatedData[x * height + height - y - 1] = data[x + y * width];
        }
        int tmp = width; // Here we are swapping, that's the difference to #11
        width = height;
        height = tmp;

        PlanarYUVLuminanceSource source = mCameraManger.buildLuminanceSource(rotatedData, width, height);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        try {
            rawResult = multiFormatReader.decodeWithState(bitmap);
        } catch (ReaderException re) {
            // continue
        } finally {
            multiFormatReader.reset();
        }
        Log.e(TAG, "decode:" + rawResult);

        return rawResult;
    }

}
