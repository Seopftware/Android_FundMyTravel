package seopftware.fundmytravel.function.googlevision;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import seopftware.fundmytravel.R;
import seopftware.fundmytravel.function.googlevision.camera.CameraSourcePreview;
import seopftware.fundmytravel.function.googlevision.camera.GraphicOverlay;

public class FaceDetect_Activity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "all_"+"FaceDetect_Activity";
    private static final int RC_HANDLE_GMS = 9001;
    // permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;

    CameraSource mCameraSource = null; // 카메라 뷰를 띄워주기 위한 변수
    CameraSourcePreview mPreview; // 카메라를 미리 보여주기 위한뷰
    GraphicOverlay mGraphicOverlay; // 얼굴이 인식된 좌표 위에 canvas로 마스크를 그려주기 위한 변수

    Bitmap bitmap_preview = null;
    public static Bitmap bitmap_current_mask =null;
    int masktype = 0;

    ImageButton ibtn_taka_picture; // 사진 촬영
    ImageButton ibtn_mask_face; // 얼굴 마스크 (얼굴 전체에 씌우는 마스크)
    ImageButton ibtn_mask_item; // 아이템 마스크 (선글라스와 같은 소품들)

    private Map<Integer, PointF> mPreviousProportions = new HashMap<>();


    int i =1; // 얼굴 마스크 변경하기 위한 변수
    int j =0; // 선글라스 변경하기 위한 변수


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 풀 스크린 만들기
        requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀바 없애기
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // 상태바 없애기
        setContentView(R.layout.activity_face_detect);

        // UI 설정
        mPreview = (CameraSourcePreview) findViewById(R.id.preview); // 카메라 프리뷰
        mGraphicOverlay= (GraphicOverlay) findViewById(R.id.faceOverlay); // 캔버스로 마스크 그려주는 곳
        bitmap_current_mask = BitmapFactory.decodeResource(getResources(), R.drawable.facemask_dog); // 현재 마스크

        // 버튼 설정
        // 1.사진촬영 (카메라 촬영 버튼을 클릭하면 화면이 멈추면서 그리기 + 스티커 입히기)
        ibtn_taka_picture= (ImageButton) findViewById(R.id.ibtn_taka_picture);
        ibtn_taka_picture.setOnClickListener(this);
        ibtn_taka_picture.bringToFront();

        // 2.얼굴 마스크
        ibtn_mask_face= (ImageButton) findViewById(R.id.ibtn_mask_face);
        ibtn_mask_face.setOnClickListener(this);
        ibtn_mask_face.bringToFront();

        // 3.아이템 마스크
        ibtn_mask_item= (ImageButton) findViewById(R.id.ibtn_mask_item);
        ibtn_mask_item.setOnClickListener(this);
        ibtn_mask_item.bringToFront();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            // 사진촬영
            case R.id.ibtn_taka_picture:

                // 마스크 뷰 저장
                mPreview.setDrawingCacheEnabled(true); // surfaceView에 저장된 카메라 뷰를 bitmap화 시키기 위한 작업
                bitmap_preview = mPreview.getDrawingCache(); // 마스크가 담겨있음
                Bitmap bitmap_preview_final = Bitmap.createScaledBitmap(bitmap_preview, 720, 1280, false); // bitmap, width, height, filter
                mPreview.takecamera(bitmap_preview_final); // 마스크 정보가 담겨있는 bitmap을 takecamera() 함수로 넘긴다.
                mPreview.destroyDrawingCache(); // clean up the cache

                break;

            // 얼굴 마스크
            case R.id.ibtn_mask_face:

                // 강아지
                if(i==0) {

                    Log.d(TAG, "강아지 안옴?");
                    masktype = 0; // 기존의 얼굴 인식 소스 사용
                    bitmap_current_mask = BitmapFactory.decodeResource(getResources(), R.drawable.facemask_dog); // Resource 폴더에 저장된 그림파일을 Bitmap으로 리턴해줌
                    i ++;
                }

                // 대머리
                else if(i==1) {

                    masktype = 0; // 기존의 얼굴 인식 소스 사용
                    bitmap_current_mask = BitmapFactory.decodeResource(getResources(), R.drawable.facemask_head); // Resource 폴더에 저장된 그림파일을 Bitmap으로 리턴해줌
                    i ++;
                }

                // 사자
                else if (i==2) {

                    masktype = 1; // 라이언! 가면 위치 내리고 크기 좀 더 키우기
                    bitmap_current_mask = BitmapFactory.decodeResource(getResources(), R.drawable.facemask_leopard); // Resource 폴더에 저장된 그림파일을 Bitmap으로 리턴해줌
                    i ++;
                }

                // 초록 마스크
                else if (i==3) {

                    masktype = 2; // 라이언! 가면 위치 내리고 크기 좀 더 키우기
                    bitmap_current_mask = BitmapFactory.decodeResource(getResources(), R.drawable.facemask_mellon); // Resource 폴더에 저장된 그림파일을 Bitmap으로 리턴해줌
                    i ++;
                }

                // 너구리
                else if (i==4) {

                    masktype = 3; // 라이언! 가면 위치 내리고 크기 좀 더 키우기
                    bitmap_current_mask = BitmapFactory.decodeResource(getResources(), R.drawable.facemask_noguri); // Resource 폴더에 저장된 그림파일을 Bitmap으로 리턴해줌
                    i ++;
                }

                // 유니콘
                else if (i==5) {

                    masktype = 4; // 라이언! 가면 위치 내리고 크기 좀 더 키우기
                    bitmap_current_mask = BitmapFactory.decodeResource(getResources(), R.drawable.facemask_unicon); // Resource 폴더에 저장된 그림파일을 Bitmap으로 리턴해줌
                    i=0;
                }

                startCameraSource();
                Toast.makeText(getApplicationContext(), "마스크 변경", Toast.LENGTH_LONG).show();

                break;

            // 선글라스 변경
            case R.id.ibtn_mask_item:
                Log.d(TAG, "int j 값은 : " + j);

                masktype=5;

                if(j==0) {
                    bitmap_current_mask = BitmapFactory.decodeResource(getResources(), R.drawable.glasses1);
                    j ++;
                }

                else if(j==1) {

                    bitmap_current_mask = BitmapFactory.decodeResource(getResources(), R.drawable.glasses2);
                    j ++;
                }

                else if(j==2) {

                    bitmap_current_mask = BitmapFactory.decodeResource(getResources(), R.drawable.glasses3);
                    j ++;
                }

                else if(j==3) {

                    bitmap_current_mask = BitmapFactory.decodeResource(getResources(), R.drawable.glasses8);
                    j ++;
                }

                else if(j==4) {

                    bitmap_current_mask = BitmapFactory.decodeResource(getResources(), R.drawable.glasses5);
                    j ++;
                }

                else if(j==5) {

                    bitmap_current_mask = BitmapFactory.decodeResource(getResources(), R.drawable.glasses6);
                    j ++;
                }

                else if(j==6) {

                    bitmap_current_mask = BitmapFactory.decodeResource(getResources(), R.drawable.glasses7);
                    j=0;
                }

                startCameraSource();
                Toast.makeText(getApplicationContext(), "선글라스 변경", Toast.LENGTH_LONG).show();


                break;
        }
    }

    // =========================================================================================================
    // 카메라 Preview 설정하는 곳
    // =========================================================================================================

    /*************************************************************************************************************
     * Handles the requesting of the camera permission.
     * This includes showing a "Snackbar" message of why the permission is needed the sending the request.
     *************************************************************************************************************/
    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };

        Snackbar.make(mGraphicOverlay, R.string.permission_camera_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, listener)
                .show();
    }

    /**
     * Creates and starts the camera.  Note that this uses a higher resolution in comparison
     * to other detection examples to enable the barcode detector to detect small barcodes at long distances.
     */
    private void createCameraSource() {

        Context context = getApplicationContext();
        FaceDetector detector = new FaceDetector.Builder(context)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setMode(FaceDetector.ACCURATE_MODE)
                .build();

        detector.setProcessor(
                new MultiProcessor.Builder<>(new GraphicFaceTrackerFactory())
                        .build());

        if (!detector.isOperational()) {
            // Note: The first time that an app using face API is installed on a device, GMS will
            // download a native library to the device in order to do detection.  Usually this
            // completes before the app is run for the first time.  But if that download has not yet
            // completed, then the above call will not detect any faces.
            //
            // isOperational() can be used to check if the required native library is currently
            // available.  The detector will automatically become operational once the library
            // download completes on device.
            Log.w(TAG, "Face detector dependencies are not yet available.");
        }

        mCameraSource = new CameraSource.Builder(context, detector)
                .setRequestedPreviewSize(1024, 768) // Sets the desired width and height of the camera frames in pixels
                .setFacing(CameraSource.CAMERA_FACING_FRONT) // 카메라 전면부 or 후면부 설정하는 곳
                .setRequestedFps(30.0f) // frame rate per second
                .build();
    }

    /**
     * Restarts the camera.
     */
    @Override
    protected void onResume() {
        super.onResume();

        // 카메라 Preview start
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource();
        } else {
            requestCameraPermission();
        }



        startCameraSource();
    }

    /**
     * Stops the camera.
     */
    @Override
    protected void onPause() {
        super.onPause();
        mPreview.stop();
    }

    /**
     * Releases the resources associated with the camera source, the associated detector, and the
     * rest of the processing pipeline.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on {@link #requestPermissions(String[], int)}.
     * <p>
     * <strong>Note:</strong> It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     * </p>
     *
     * @param requestCode  The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link PackageManager#PERMISSION_GRANTED}
     *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
     * @see #requestPermissions(String[], int)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");
            // we have permission, so create the camerasource
            createCameraSource();
            return;
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Face Tracker sample")
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(R.string.ok, listener)
                .show();
    }

    //==============================================================================================
    // Camera Source Preview
    //==============================================================================================

    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() {

        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    //==============================================================================================
    // Graphic Face Tracker
    //==============================================================================================

    /**
     * Factory for creating a face tracker to be associated with a new face.  The multiprocessor
     * uses this factory to create face trackers as needed -- one for each individual.
     */
    private class GraphicFaceTrackerFactory implements MultiProcessor.Factory<Face> {
        @Override
        public Tracker<Face> create(Face face) {
            return new GraphicFaceTracker(mGraphicOverlay);
        }
    }

    /**
     * Face tracker for each detected individual. This maintains a face graphic within the app's
     * associated face overlay.
     */
    private class GraphicFaceTracker extends Tracker<Face> {
        private GraphicOverlay mOverlay;
        private FaceGraphic mFaceGraphic;

        GraphicFaceTracker(GraphicOverlay overlay) {
            mOverlay = overlay;

            mFaceGraphic = new FaceGraphic(overlay);
        }

        /**
         * Start tracking the detected face instance within the face overlay.
         */
        @Override
        public void onNewItem(int faceId, Face item) {

        }

        /**
         * Update the position/characteristics of the face within the overlay.
         */
        @Override
        public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {

            Log.d(TAG, "onUpdate 발생!!!! mask type = " + masktype);

            mOverlay.add(mFaceGraphic);

            // 강아지, 대머리
            if (masktype==0) {
                mFaceGraphic.updateFace(face, masktype);
            }

            // 사자
            else if (masktype==1) {
                mFaceGraphic.updateLion(face, masktype);
            }

            // 멜론
            else if (masktype==2) {
                mFaceGraphic.updateMellon(face, masktype);
            }

            // 너구리
            else if (masktype==3) {
                mFaceGraphic.updateLion(face, masktype);
            }

            // 유니콘
            else if (masktype==4) {
                mFaceGraphic.updateLion(face, masktype);
            }

            // 선글라스
            else if (masktype==5){

                updatePreviousProportions(face);
                PointF leftPosition = getLandmarkPosition(face, Landmark.LEFT_EYE); // 왼쪽 눈 land mark
                PointF rightPosition = getLandmarkPosition(face, Landmark.RIGHT_EYE); // 오른쪽 눈 land mark
                mFaceGraphic.updateEyes(leftPosition, rightPosition, masktype);
            }

        }

        /**
         * Finds a specific landmark position, or approximates the position based on past observations
         * if it is not present.
         */
        private PointF getLandmarkPosition(Face face, int landmarkId) {
            for (Landmark landmark : face.getLandmarks()) {
                if (landmark.getType() == landmarkId) {
                    return landmark.getPosition();
                }
            }

            PointF prop = mPreviousProportions.get(landmarkId);
            if (prop == null) {
                return null;
            }

            float x = face.getPosition().x + (prop.x * face.getWidth());
            float y = face.getPosition().y + (prop.y * face.getHeight());
            return new PointF(x, y);
        }

        private void updatePreviousProportions(Face face) {
            for (Landmark landmark : face.getLandmarks()) {
                PointF position = landmark.getPosition();
                float xProp = (position.x - face.getPosition().x) / face.getWidth();
                float yProp = (position.y - face.getPosition().y) / face.getHeight();
                mPreviousProportions.put(landmark.getType(), new PointF(xProp, yProp));
            }
        }

        /**
         * Hide the graphic when the corresponding face was not detected.  This can happen for
         * intermediate frames temporarily (e.g., if the face was momentarily blocked from
         * view).
         */
        @Override
        public void onMissing(FaceDetector.Detections<Face> detectionResults) {
            mOverlay.remove(mFaceGraphic); // 얼굴 인식이 안될 경우 overlay를 그리지 않는다.
        }

        /**
         * Called when the face is assumed to be gone for good. Remove the graphic annotation from
         * the overlay.
         */
        @Override
        public void onDone() {
            mOverlay.remove(mFaceGraphic); // 사진 촬영이 끝나도 마스크는 그대로 남아 있게끔.
        }
    }


}
