package com.waynesplanet.connor.recipebox20;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.transition.Explode;
import android.util.Log;
import android.util.Size;
//import android.util.SparseIntArray;
import android.view.MenuItem;
import android.view.Surface;
import android.view.TextureView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
//import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import static android.hardware.camera2.CameraDevice.TEMPLATE_STILL_CAPTURE;
import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static android.hardware.camera2.CameraMetadata.LENS_FACING_BACK;

public class TakePhotoActivity extends AppCompatActivity {

    private final BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
            case R.id.nav_home:
                Intent home_intent = new Intent(TakePhotoActivity.this, MainActivity.class);
                startActivity(home_intent,
                        ActivityOptions.makeSceneTransitionAnimation(TakePhotoActivity.this).toBundle());
                return true;
            case R.id.nav_take_photo:
                takePicture();
                return true;
            }
            return onNavigationItemSelected(menuItem);
        }
    };

    private static final String TAG = TakePhotoActivity.class.getName();
    private static final String MyTAG = "TakePhotoActivity";
    private TextureView textureView;

    protected CameraDevice cameraDevice;
    protected CameraCaptureSession cameraCaptureSessions;
    protected CaptureRequest captureRequest;
    protected CaptureRequest.Builder captureRequestBuilder;
    private Size imageDimension;
    private ImageReader imageReader;
    private File file;
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private boolean mFlashSupported;
    private Handler mBackgroundHandler;
    private HandlerThread mBackgroundThread;

    // for use when saving the text file; originally
    // located in takePicture, but need to speed-up that method.
    protected String recipeLine = "Recipe Label: ";
    protected String categoryLine = "Category: ";
    protected String ingredientsLine = "Ingredients: ";
    protected String instructionsLine = "Instructions: ";
    protected String notesLine = "Notes: ";
    protected String newLine = "\n";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setSharedElementEnterTransition(new Explode());
        getWindow().setSharedElementExitTransition(new Explode());
        getWindow().setEnterTransition(new Explode());
        getWindow().setExitTransition(new Explode());

        setContentView(R.layout.activity_take_photo);

        Toolbar toolbar = findViewById(R.id.action_bar);
        setSupportActionBar(toolbar);

        BottomNavigationView bottomNavView = findViewById(R.id.bottom_nav_view);
        bottomNavView.setOnNavigationItemSelectedListener(navListener);

        textureView = findViewById(R.id.textureview);
        assert textureView != null;
        textureView.setSurfaceTextureListener(textureListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startBackgroundThread();
        if (textureView.isAvailable()) { openCamera(); }
        else { textureView.setSurfaceTextureListener(textureListener); }
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeCamera();
        stopBackgroundThread();
    }

    /*	Here's some code from Google's NDK-Camera Example	*/
    private int getRotationDegree() {
    /**
     * Configure the rotation angle necessary to apply to
     * Camera image when presenting: all rotations should be accumulated:
     *    CameraSensorOrientation + Android Device Native Orientation +
     *    Human Rotation (rotated degree related to Phone native orientation
     */
        return 90 * ((WindowManager)(getSystemService(WINDOW_SERVICE)))
                    .getDefaultDisplay()
                    .getRotation();    
    }

	private boolean isCamera2Device() {
		CameraManager camMgr = (CameraManager)getSystemService(Context.CAMERA_SERVICE);
        boolean camera2Dev = true;
        try {
            String[] cameraIds = camMgr.getCameraIdList();
            if (cameraIds.length != 0 ) {
                for (String id : cameraIds) {
                    CameraCharacteristics characteristics = camMgr.getCameraCharacteristics(id);
                    int deviceLevel = characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
                    int facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                    if (deviceLevel == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY &&
                            facing == LENS_FACING_BACK) {
                        camera2Dev =  false;
                    }
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
            camera2Dev = false;
        }
        return camera2Dev;
	}
	/*	End of sample code from Google's NDK-Camera Example	*/

    TextureView.SurfaceTextureListener textureListener
    = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            openCamera();
        }
        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            /*
             * configureTransform(width, height);
             */
        }
        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return true;
        }
        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            // I may want to get the screen orientation, and
            // act accordingly....
            /* Do something possibly? lol */
    }};

    private final CameraDevice.StateCallback stateCallback
    = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            cameraDevice = camera;
            createCameraPreview();
        }
        @Override
        public void onDisconnected(CameraDevice camera) {
            cameraDevice.close();
        }
        @Override
        public void onError(CameraDevice camera, int error) {
            cameraDevice.close();
            cameraDevice = null;
    }};

    final CameraCaptureSession.CaptureCallback captureCallbackListener
    = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureCompleted(CameraCaptureSession session,
                                       CaptureRequest request,
                                       TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
            createCameraPreview();
    }};

    /*
     * Setup the starting & stopping BackgroundThreads
     */
    protected void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("Camera Background");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    protected void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) { e.printStackTrace(); }
    }
    /**
      * 
      * See the following url for a reference to the getJpegOrientation method:
      * https://developer.android.com/reference/android/hardware/camera2/CaptureResult#JPEG_ORIENTATION
      * 
      * Need to get the proper orientation for use by a JPEG-image-file:
      * 
     */
    private int getJpegOrientation(CameraCharacteristics c, int deviceOrientation) {
        if (deviceOrientation == android.view.OrientationEventListener.ORIENTATION_UNKNOWN)
            return 0;
        int sensorOrientation = c.get(CameraCharacteristics.SENSOR_ORIENTATION);
        // Round device orientation to a multiple of 90
        deviceOrientation = (deviceOrientation + 45) / 90 * 90;
        // Reverse device orientation for front-facing cameras:
        boolean facingFront = c.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT;
        if (facingFront)
            deviceOrientation = -deviceOrientation;
        // Calculate desired JPEG orientation relative to camera orientation to make
        // the image upright relative to the device orientation
        int jpegOrientation = (sensorOrientation + deviceOrientation + 360) % 360;
        return jpegOrientation;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void takePicture() {
        if (null == cameraDevice) { return; }
        try {
            int height = 480;
            int width = 640;
            CameraManager manager = (CameraManager) this.getSystemService(Context.CAMERA_SERVICE);
            String cameraId = manager.getCameraIdList()[0];
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
            ImageReader reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);
            List<Surface> outputSurfaces = new ArrayList<Surface>(2);
            outputSurfaces.add(reader.getSurface());
            outputSurfaces.add(new Surface(textureView.getSurfaceTexture()));
            int rotation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
            int deviceRotation = this.getWindowManager().getDefaultDisplay().getRotation();
            // int jpegOrientation = getJpegOrientation(characteristics, rotation);
            final CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(reader.getSurface());
            captureBuilder.addTarget(new Surface(textureView.getSurfaceTexture()));
            // captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, rotation);
            //captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, getJpegOrientation(characteristics, rotation));
            // This is how the ndk-version attempts to get the correct rotation-degree value
            int rotationDegree = getRotationDegree();
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, rotationDegree);
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CaptureRequest.CONTROL_AF_MODE_AUTO);

            ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
            File fileDir = contextWrapper.getDir("RecipeImages", MODE_PRIVATE);
            Context context = this;
            File textDirectory = context.getFilesDir();
            long milliseconds = System.currentTimeMillis();
            String imageFilename = milliseconds + "_recipe.jpg";
            String textFilename = milliseconds + "_recipe.txt";

            // The below line creates a file to save the image
            final File file = new File(fileDir, imageFilename);

            // The below line creates a file to save the textual information
            final File tfile = new File(textDirectory, textFilename);

            FileOutputStream outputStream;
            try {
                outputStream = openFileOutput(textFilename, Context.MODE_PRIVATE);
                // Field #1:
                //outputStream.write(ingredientsLine.getBytes());
                outputStream.write(newLine.getBytes());

                // Field #2:
                //outputStream.write(recipeLine.getBytes());
                outputStream.write(newLine.getBytes());

                // Field #3:
                //outputStream.write(instructionsLine.getBytes());
                outputStream.write(newLine.getBytes());

                // Field #4:
                //outputStream.write(categoryLine.getBytes());
                outputStream.write(newLine.getBytes());

                // Field #5:
                //outputStream.write(notesLine.getBytes());
                outputStream.write(newLine.getBytes());

                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            ImageReader.OnImageAvailableListener readerListener
            = new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {
                try (Image image = reader.acquireLatestImage()) {
                    ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                    byte[] bytes = new byte[buffer.capacity()];
                    buffer.get(bytes);
                    save(bytes);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            private void save(byte[] bytes) throws IOException {
                try (OutputStream outputStream = new FileOutputStream(file)) { outputStream.write(bytes); }
            }};
            reader.setOnImageAvailableListener(readerListener, mBackgroundHandler);

            final CameraCaptureSession.CaptureCallback captureListener
            = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(CameraCaptureSession session,
                                               CaptureRequest request,
                                               TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                    createCameraPreview();
            }};

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cameraDevice.createCaptureSession
                (outputSurfaces, new CameraCaptureSession.StateCallback() {
                    @Override
                    public void onConfigured(CameraCaptureSession session) {
                        try { session.capture(captureBuilder.build(), captureListener, mBackgroundHandler); }
                        catch (CameraAccessException e) { e.printStackTrace(); }
                    }
                    @Override
                    public void onConfigureFailed(CameraCaptureSession session) {}
                }, mBackgroundHandler);
            }
        } catch (CameraAccessException e) { e.printStackTrace(); }
    }

    protected void createCameraPreview() {
        try {
            SurfaceTexture texture = textureView.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(imageDimension.getWidth(), imageDimension.getHeight());
            Surface surface = new Surface(texture);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            }
            captureRequestBuilder.addTarget(surface);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cameraDevice.createCaptureSession(Collections.singletonList(surface),
                        new CameraCaptureSession.StateCallback() {
                            @Override
                            public void onConfigured(CameraCaptureSession cameraCaptureSession) {
                                // The camera is already closed if the cameraDevice object is null
                                if (null == cameraDevice) {
                                    return;
                                }
                                // When the CameraCaptureSession is ready, we start displaying the preview.
                                cameraCaptureSessions = cameraCaptureSession;
                                updatePreview();
                            }

                            @Override
                            public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {
                                Toast.makeText(TakePhotoActivity.this, "Configuration change", Toast.LENGTH_SHORT)
                                        .show();
                            }
                        }, null);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @androidx.annotation.RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void openCamera() {
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            String cameraId = manager.getCameraIdList()[0];
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            assert map != null;
            imageDimension = map.getOutputSizes(SurfaceTexture.class)[0];
            if (ActivityCompat.checkSelfPermission
                (this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && 
                ActivityCompat.checkSelfPermission
                (this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(TakePhotoActivity.this,
                                                        new String[]
                                                        { Manifest.permission.CAMERA,
                                                          Manifest.permission.WRITE_EXTERNAL_STORAGE },
                                                        REQUEST_CAMERA_PERMISSION);
                                                        return;
            }
            manager.openCamera(cameraId, stateCallback, null);
        } catch (CameraAccessException e) { e.printStackTrace(); }
    }

    private void closeCamera() {
        if (cameraDevice != null) {
            cameraDevice.close();
            cameraDevice = null;
        }
        if (imageReader != null) {
            imageReader.close();
            imageReader = null;
        }
    }

    protected void updatePreview() {
        if (null == cameraDevice) { Log.e(TAG, "updatePreview error, return"); }
        if (Build.VERSION.SDK_INT >= LOLLIPOP) {
            captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        }
        try {
            cameraCaptureSessions.setRepeatingRequest(captureRequestBuilder.build(), null, mBackgroundHandler);
        } catch (CameraAccessException e) { e.printStackTrace(); }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                // close the app
                Toast.makeText(TakePhotoActivity.this, "Sorry! Please grant app permission to use the Camera!",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}