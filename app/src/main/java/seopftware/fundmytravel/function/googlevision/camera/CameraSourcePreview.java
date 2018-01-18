/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package seopftware.fundmytravel.function.googlevision.camera;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

import com.google.android.gms.common.images.Size;
import com.google.android.gms.vision.CameraSource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import seopftware.fundmytravel.function.googlevision.FaceCanvas_Activity;

public class CameraSourcePreview extends ViewGroup {
    private static final String TAG = "all_" + "CameraSourcePreview";

    private Context mContext;
    private SurfaceView mSurfaceView;
    private CameraSource mCameraSource;
    private GraphicOverlay mOverlay;
    private Bitmap maskBitmap = null;

    private boolean mStartRequested;
    private boolean mSurfaceAvailable;



    public CameraSourcePreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mStartRequested = false;
        mSurfaceAvailable = false;

        mSurfaceView = new SurfaceView(context);

        // SurfaceView 위에 view들을 그릴 수 있게 해줌. (true로 바꾸면 surfaceView가 최상단에 그려지기 때문에 다른 view들이 보이지 않는다.)
        mSurfaceView.setZOrderOnTop(false);
        // Surface 객체는 직접 처리할 수 없고 반드시 SurfaceHolder를 통해서 처리해야 한다.
        // Surface 객체가 초기화 될 때 getholder()를 호출해서 SurfaceHolder를 얻으면 된다.
        // getHolder의 콜백 메서드를 받기 위해서 addCallback() 함수를 이용한다.
        mSurfaceView.getHolder().addCallback(new SurfaceCallback());
        addView(mSurfaceView);

    }

    // CameraSource Start
    public void start(CameraSource cameraSource) throws IOException {
        if (cameraSource == null) {
            stop();
        }

        mCameraSource = cameraSource;

        if (mCameraSource != null) {
            mStartRequested = true;
            startIfReady(); // 카메라뷰 준비되면 시작
        }
    }

    public void start(CameraSource cameraSource, GraphicOverlay overlay) throws IOException {
        mOverlay = overlay;
        start(cameraSource);
    }

    public void stop() {
        if (mCameraSource != null) {
            mCameraSource.stop();
        }


    }

    public void release() {
        if (mCameraSource != null) {
            mCameraSource.release();
            mCameraSource = null;
        }
    }


    // 사진 촬영 후 'CAMERA'와 'CANVAS' 합치는 곳
    public void takecamera(Bitmap bitmap) {
        maskBitmap = bitmap; // 현재 마스크
        mCameraSource.takePicture(null, myPictureCallback_JPG);
    }


    // SurfaceView에 찍힌 카메라 뷰를 저장하기 위해 사용하는 함수
    CameraSource.PictureCallback myPictureCallback_JPG = new CameraSource.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] arg0) {
            try {

                Bitmap cameraBitmap = BitmapFactory.decodeByteArray(arg0, 0, arg0.length); // 카메라 비트맵 생성
                Bitmap newbm = rotate(cameraBitmap, -90); // 카메라 비트맵: 각도 -90도 돌리기
                Bitmap scaledbm = Bitmap.createScaledBitmap(newbm, 720, 1280, false);

                // 카메라 비트맵: 좌우 반전
                Matrix sideInversion = new Matrix();
                sideInversion.setScale(-1, 1); // 좌우반전
                Bitmap sideInversionImg = Bitmap.createBitmap(scaledbm, 0, 0, 720, 1280, sideInversion, false);

                // Bitmap 합쳐주기 작업
                String fileName = Long.toString(System.currentTimeMillis()) + "_create.jpg";
                File storagePath = new File(Environment.getExternalStorageDirectory() + "/FundMyTravel/"); // 저장 경로 설정
                storagePath.mkdirs(); // 만약 존재하지 않는 파일이면 파일 생성
                File file = new File(storagePath, fileName); // 파일 생성 완료
                FileOutputStream stream = new FileOutputStream(file);

                // 'camera' + 'canvas'가 합쳐진 최종 Bitmap
                // todo 여기 손봐야함. 카메라 bitmap 뒤집히는지 안뒤집히는지
                Bitmap save = overlay(sideInversionImg, maskBitmap); // 카메라 bitmap 바로 적용했을 때, 만약 sideInversioninImg 안해도 되면 그냥 ㄱㄱㄱ
                save.compress(Bitmap.CompressFormat.JPEG, 100, stream);

                // clean up
                stream.close();
                save.recycle();

                // 파일명을 보낸 다음 캔버스를 그려줄 액티비티에서 파일명을 가져와서 이미지를 띄움
                Intent int1 = new Intent(getContext(), FaceCanvas_Activity.class);
                int1.putExtra("image", fileName);
                getContext().startActivity(int1);


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mCameraSource.stop();
            mCameraSource.release();
            mCameraSource = null;

        }
    };


    // Bitmap 2개를 합치는 곳 (bmp1 은 카메라 preview, bmp2는 마스크)
    public Bitmap overlay(Bitmap bmp1, Bitmap bmp2) {
        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        Bitmap bmp21 = Bitmap.createScaledBitmap(bmp2, 720, 1280, true); // 최종적으로 합치는 BITMAP의 사이즈

        canvas.drawBitmap(bmp1, new Matrix(), null);
        canvas.drawBitmap(bmp21, 0, 0, null); // bitmap을 그려줄 때의 위치
        return bmOverlay;
    }



    private void startIfReady() throws IOException {
        if (mStartRequested && mSurfaceAvailable) {

            // 퍼미션 관련 추가하는 부분
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                return;
            }


            mCameraSource.start(mSurfaceView.getHolder());
            if (mOverlay != null) {
                Size size = mCameraSource.getPreviewSize();
                int min = Math.min(size.getWidth(), size.getHeight());
                int max = Math.max(size.getWidth(), size.getHeight());
                if (isPortraitMode()) {
                    // Swap width and height sizes when in portrait, since it will be rotated by
                    // 90 degrees
                    mOverlay.setCameraInfo(min, max, mCameraSource.getCameraFacing());
                } else {
                    mOverlay.setCameraInfo(max, min, mCameraSource.getCameraFacing());
                }
                mOverlay.clear();
            }
            mStartRequested = false;
        }
    }

    private class SurfaceCallback implements SurfaceHolder.Callback {
        @Override
        public void surfaceCreated(SurfaceHolder surface) { // SurfaceView가 만들어질 때 호출된다.
            mSurfaceAvailable = true;
            try {
                startIfReady();
            } catch (IOException e) {
                Log.e(TAG, "Could not start camera source.", e);
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surface) { // SurfaceView가 종료될 때 호출된다
            mSurfaceAvailable = false;
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) { // SurfaceView의 크기가 바뀔 때 호출된다
        }
    }


    // 화면에 뿌려주는 camera preview의 크기를 정해주는 곳
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int layoutWidth = right - left;
        final int layoutHeight = bottom - top;


        Log.d(TAG, "layoutWidth : " + layoutWidth); // 720
        Log.d(TAG, "layoutHeight : " + layoutHeight); // 1280

        for (int i = 0; i < getChildCount(); ++i) {
            getChildAt(i).layout(0, 0, layoutWidth, layoutHeight); // 기존의 코드에서 childHeight -> layoutHeight로 바꿨더니 밑에 흰생 배경이 없어졌다.
        }

        try {
            startIfReady();
        } catch (IOException e) {
            Log.e(TAG, "Could not start camera source.", e);
        }
    }


    // 화면을 뒤집었을 때 사용되는 함수
    private boolean isPortraitMode() {
        int orientation = mContext.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return false;
        }
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            return true;
        }

        Log.d(TAG, "isPortraitMode returning false by default");
        return false;
    }



    public static Bitmap rotate(Bitmap bitmap, int degrees) {
        if (degrees != 0 && bitmap != null) {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) bitmap.getWidth() / 2,
                    (float) bitmap.getHeight() / 2);

            try {
                Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0,
                        bitmap.getWidth(), bitmap.getHeight(), m, true);
                if (bitmap != converted) {
                    bitmap.recycle();
                    bitmap = converted;
                }
            } catch (OutOfMemoryError ex) {
                // 메모리가 부족하여 회전을 시키지 못할 경우 그냥 원본을 반환합니다.
            }
        }
        return bitmap;
    }

    public int exifOrientationToDegrees(int exifOrientation)
    {
        if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_90)
        {
            return 90;
        }
        else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_180)
        {
            return 180;
        }
        else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_270)
        {
            return 270;
        }
        return 0;
    }

}
