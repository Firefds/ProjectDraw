package com.shauli.ProjectDraw;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import java.io.ByteArrayOutputStream;

/**
 * @author Shauli Bracha
 * This class handles the camera input and returns a bitmap to the
 * main activity if a picture is taken
 */
public class CameraActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private Camera camera;
    private boolean previewRunning = false;

    /**
     * Saves the current surface view as a byte stream and sends it in an intent to the main activity
     */
    private PictureCallback jpegCallback = (data, camera) -> {
        if (data != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bs);
            Intent intent = new Intent();
            intent.putExtra("bitmap", bs.toByteArray());
            setResult(RESULT_OK, intent);
            bitmap.recycle();
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.camera_activity);

        setSurface();
        setOverlay();
        setListeners();
    }

    /**
     * Listens to camera rotation and adjusts camera accordingly
     */
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        try {
            if (previewRunning)
                camera.stopPreview();

            Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();

            switch (display.getRotation()) {
                case Surface.ROTATION_0:
                    camera.setDisplayOrientation(90);
                    break;
                case Surface.ROTATION_90:
                    camera.setDisplayOrientation(0);
                    break;
                case Surface.ROTATION_180:
                    camera.setDisplayOrientation(270);
                    break;
                case Surface.ROTATION_270:
                    camera.setDisplayOrientation(180);
                    break;
            }

            camera.setPreviewDisplay(holder);
            camera.startPreview();
            previewRunning = true;
        } catch (Exception ignored) {
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
        camera = Camera.open();
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.stopPreview();
        previewRunning = false;
        camera.release();
        camera = null;
    }

    public void setListeners() {
        Button takePictureBtn = findViewById(R.id.takePictureBtn);
        takePictureBtn.setOnClickListener(view -> camera.takePicture(null, null, jpegCallback));
    }

    public void setSurface() {
        surfaceView = findViewById(R.id.surface);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
    }

    public void setOverlay() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View overView = inflater.inflate(R.layout.cam_overlay, null);
        addContentView(overView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }
}