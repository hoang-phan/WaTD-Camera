package com.eahackathon.watd.watdcamera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import com.eahackathon.watd.watdcamera.models.ResponseModel;
import com.eahackathon.watd.watdcamera.network.APIService;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback, Camera.PictureCallback {

    public static String TAG = MainActivity.class.getSimpleName();

    private SurfaceView mPreviewView;
    private SurfaceHolder mSurfaceHolder;
    private Camera mCamera;

    private boolean mSafeTakingPicture = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                captureImage();
            }
        });

        mPreviewView = (SurfaceView) findViewById(R.id.preview_view);
        mSurfaceHolder = mPreviewView.getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    private void captureImage() {
        if (mSafeTakingPicture) {
            mCamera.takePicture(null, null, this);
            mSafeTakingPicture = false;
        }
    }

    @Override

    protected void onPause() {
        super.onPause();
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCamera == null) {
            try {
                mCamera = openFrontFacingCameraGingerbread();
                mCamera.setPreviewDisplay(mSurfaceHolder);
                mCamera.startPreview();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (mCamera != null) {
            try {
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
                mSafeTakingPicture = true;
            } catch (IOException e) {
                Toast.makeText(MainActivity.this, "Unable to start camera preview.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    private Camera openFrontFacingCameraGingerbread() {
        int cameraCount = 0;
        Camera cam = null;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();
        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                try {
                    cam = Camera.open(camIdx);
                } catch (RuntimeException e) {
                    Log.e(TAG, "Camera failed to open: " + e.getLocalizedMessage());
                }
            }
        }

        return cam;
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Bitmap origin = BitmapFactory.decodeByteArray(data, 0, data.length);
        Bitmap bitmap = Bitmap.createScaledBitmap(origin, 400, 300, false);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 95, outputStream);
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg"), outputStream.toByteArray());
        Call<ResponseModel> call = APIService.getInstance().uploadImage(requestBody);
        call.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Response<ResponseModel> response, Retrofit retrofit) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    showToast(getString(R.string.done_upload_photo));
                } else {
                    showToast(String.valueOf(response.code()));
                }
                if (mCamera != null) {
                    mCamera.startPreview();
                    mSafeTakingPicture = true;
                }
            }

            @Override
            public void onFailure(Throwable t) {
                showToast(getString(R.string.upload_failed));
                if (mCamera != null) {
                    mCamera.startPreview();
                    mSafeTakingPicture = true;
                }
            }
        });
    }

    private void showToast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
