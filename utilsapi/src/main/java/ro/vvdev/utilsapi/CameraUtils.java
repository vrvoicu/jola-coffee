package ro.vvdev.utilsapi;

import android.app.Activity;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import java.util.List;

/**
 * Created by victor on 05.09.2015.
 */
public class CameraUtils {

    public static Camera getCameraInstance() {
        Camera camera = null;
        try {
            // attempt to get a Camera instance
            camera = Camera.open();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return camera;
    }

    public static byte[] getBuffer(Camera camera){
        int bufferSize;
        byte buffer[];
        int bitsPerPixel;

        Camera.Parameters cameraParams = camera.getParameters();
        Camera.Size mSize = cameraParams.getPreviewSize();

        //Log.d("Function", "previewBuffer: preview size=" + mSize.height + " " + mSize.width);
        int imageFormat = cameraParams.getPreviewFormat();

        if (imageFormat == ImageFormat.YV12) {
            int yStride = (int) Math.ceil(mSize.width / 16.0) * 16;
            int uvStride = (int) Math.ceil((yStride / 2) / 16.0) * 16;
            int ySize = yStride * mSize.height;
            int uvSize = uvStride * mSize.height / 2;
            bufferSize = ySize + uvSize * 2;
            buffer = new byte[bufferSize];
            //Log.d("Function", "previewBuffer: buffer size=" + Integer.toString(bufferSize));
        }
        else {
            bitsPerPixel = ImageFormat.getBitsPerPixel(imageFormat);
            bufferSize = (int) (mSize.height * mSize.width * ((bitsPerPixel / (float) 8)));
            buffer = new byte[bufferSize];
            //Log.d("Function", "previewBuffer: buffer size=" + Integer.toString(bufferSize));
        }

        return buffer;
    }

    public static void setCameraDisplayOrientation(Activity activity,
                                                   int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }
}
