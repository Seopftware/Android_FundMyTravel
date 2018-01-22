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
package seopftware.fundmytravel.function.googlevision;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.Log;

import com.google.android.gms.vision.face.Face;

import seopftware.fundmytravel.function.googlevision.camera.GraphicOverlay;

import static seopftware.fundmytravel.function.googlevision.FaceDetect_Activity.bitmap_current_mask;

/**
 * Graphic instance for rendering face position, orientation, and landmarks within an associated
 * graphic overlay view.
 */
class FaceGraphic extends GraphicOverlay.Graphic {

    private static final String TAG = "all_" + FaceGraphic.class;

    private volatile Face mFace;
    private volatile PointF mLeftPosition; // 왼쪽 눈의 위치
    private volatile PointF mRightPosition; // 오른쪽 눈의 위치
    private float prevAngle = 0;
    private PointF prevLeftPos = new PointF();


    Bitmap op;
    int masktype;

    FaceGraphic(GraphicOverlay overlay) {
        super(overlay);
        op = bitmap_current_mask;
    }

    /**
     * Updates the face instance from the detection of the most recent frame.  Invalidates the
     * relevant portions of the overlay to trigger a redraw.
     */
    void updateFace(Face face, int masktype) {
        mFace = face;
        this.masktype = masktype;
        op = Bitmap.createScaledBitmap(bitmap_current_mask, (int) scaleX(face.getWidth()),
                (int) scaleY(((bitmap_current_mask.getHeight() * face.getWidth()) / bitmap_current_mask.getWidth())), false);
        postInvalidate();
    }

    void updateLion(Face face, int masktype) {
        mFace = face;
        this.masktype = masktype;
        op = Bitmap.createScaledBitmap(bitmap_current_mask, 600, 600, false);
        postInvalidate();
    }

    void updateMellon(Face face, int masktype) {
        mFace = face;
        this.masktype = masktype;
        op = Bitmap.createScaledBitmap(bitmap_current_mask, 600, 800, false);
        postInvalidate();
    }

    void updateEyes(PointF leftPosition, PointF rightPosition, int masktype) {
        mLeftPosition = leftPosition;
        mRightPosition = rightPosition;
        this.masktype = masktype;
        postInvalidate();
    }

    /**
     * Draws the face annotations for position on the supplied canvas.
     */
    @Override
    public void draw(Canvas canvas) {

        PointF detectLeftPosition = mLeftPosition; // 왼쪽 눈이 위치한 좌표
        PointF detectRightPosition = mRightPosition; // 오른쪽 눈이 위치한 좌표

        Log.d(TAG, "detectLeftPosition (GoogleGraphics) : " + detectLeftPosition); // 왼쪽눈
        Log.d(TAG, "detectRightPosition (GoogleGraphics) : " + detectRightPosition); // 오른쪽눈

        Face face = mFace;
        if (face == null) {
            return;
        }


        Log.d(TAG, "masktype은 : " + masktype);

        if (masktype==0) {

            // Draws a circle at the position of the detected face, with the face's track id below.
            float x = translateX(face.getPosition().x + face.getWidth() / 2);
            float y = translateY(face.getPosition().y + face.getHeight() / 2);
            float xOffset = scaleX(face.getWidth() / 2.0f);
            float yOffset = scaleY(face.getHeight() / 2.0f);
            float left = x - xOffset;
            float top = y - yOffset;
            canvas.drawBitmap(op, left, top-20, new Paint());

        }

        // 라이언
        else if (masktype==1) {
            // Draws a circle at the position of the detected face, with the face's track id below.
            float x = translateX(face.getPosition().x + face.getWidth() / 2);
            float y = translateY(face.getPosition().y + face.getHeight() / 2);
            float xOffset = scaleX(face.getWidth() / 2.0f);
            float yOffset = scaleY(face.getHeight() / 2.0f);
            float left = x - xOffset;
            float top = y - yOffset;
            canvas.drawBitmap(op, left-80, top+150, new Paint());
        }

        // 초록 마스크
        else if (masktype==2) {
            // Draws a circle at the position of the detected face, with the face's track id below.
            float x = translateX(face.getPosition().x + face.getWidth() / 2);
            float y = translateY(face.getPosition().y + face.getHeight() / 2);
            float xOffset = scaleX(face.getWidth() / 2.0f);
            float yOffset = scaleY(face.getHeight() / 2.0f);
            float left = x - xOffset;
            float top = y - yOffset;
            canvas.drawBitmap(op, left-80, top+30, new Paint());
        }

        // 너구리
        else if (masktype==3) {
            // Draws a circle at the position of the detected face, with the face's track id below.
            float x = translateX(face.getPosition().x + face.getWidth() / 2);
            float y = translateY(face.getPosition().y + face.getHeight() / 2);
            float xOffset = scaleX(face.getWidth() / 2.0f);
            float yOffset = scaleY(face.getHeight() / 2.0f);
            float left = x - xOffset;
            float top = y - yOffset;
            canvas.drawBitmap(op, left-80, top+100, new Paint());
        }

        else if (masktype==4) {
            // Draws a circle at the position of the detected face, with the face's track id below.
            float x = translateX(face.getPosition().x + face.getWidth() / 2);
            float y = translateY(face.getPosition().y + face.getHeight() / 2);
            float xOffset = scaleX(face.getWidth() / 2.0f);
            float yOffset = scaleY(face.getHeight() / 2.0f);
            float left = x - xOffset;
            float top = y - yOffset;
            canvas.drawBitmap(op, left-60, top-200, new Paint());
        }

        else if (masktype==5) {
            if((mLeftPosition!=null) && (mRightPosition!=null)) {

                PointF leftPosition = new PointF(translateX(detectLeftPosition.x), translateY(detectLeftPosition.y)); // 왼쪽 눈 좌표
                PointF rightPosition = new PointF(translateX(detectRightPosition.x), translateY(detectRightPosition.y)); // 오른쪽 눈 좌표

                // Use the inter-eye distance to set the size of the eyes. 눈과 눈 사이의 거리
                float distance = (float) Math.sqrt(Math.pow(rightPosition.x - leftPosition.x, 2)
                                                    +Math.pow(rightPosition.y - leftPosition.y, 2));

                Bitmap bitmap =null;
                int newWidth = (int)(distance * 2.5);
                int newHeight = (int)((bitmap_current_mask.getHeight()* (newWidth)) / (float) bitmap_current_mask.getWidth());

                // 그려줄 이미지의 사이즈
                bitmap = Bitmap.createScaledBitmap(bitmap_current_mask, newWidth+5, newHeight+5, true); // 거리에 따라서 그려줄 비트맵의 크기가 조정됨.

                int left = bitmap.getWidth() / 3;
                float angle = getAngle(leftPosition, rightPosition);
                double angleInRadian = Math.atan2(leftPosition.y - rightPosition.y, rightPosition.x - leftPosition.x);

                Log.d(TAG, "내 얼굴의 각도 angle -> " + String.valueOf(angle));

                // 이게 어떤 상황일까??
                if (Math.abs(angle - prevAngle) > 2.0f) {

                    Log.d(TAG, "Math.abs(angle - prevAngle) > 2.0f");

                    Matrix matrix = new Matrix();
                    matrix.postRotate(-angle, leftPosition.x, leftPosition.y);
                    canvas.setMatrix(matrix);
                    left = (int) (left / Math.cos(angleInRadian));

                    // 그려줄 이미지의 위치
                    canvas.drawBitmap(bitmap, leftPosition.x - left + 30, leftPosition.y - bitmap.getHeight() / 2, null);
                    prevAngle = angle;
                    prevLeftPos = leftPosition;
                }


                else {

                    Log.d(TAG, "Math.abs(angle - prevAngle) < 2.0f");

                    Matrix matrix = new Matrix();
                    matrix.postRotate(-prevAngle, prevLeftPos.x, prevLeftPos.y);
                    canvas.setMatrix(matrix);
                    left = (int) (left / Math.cos(Math.toRadians(prevAngle)));
                    canvas.drawBitmap(bitmap, prevLeftPos.x - left, prevLeftPos.y - bitmap.getHeight() / 2, null);
                }
            } else {
                Log.d(TAG, "선글라스 : 빈 값이 있습니다.");
                return;
            }

        }


    }

    private float getAngle(PointF leftPosition, PointF rightPosition) {
        float angle = (float) Math.toDegrees(Math.atan2(leftPosition.y - rightPosition.y,
                                                rightPosition.x - leftPosition.x));

        if(angle<0) {
            angle +=360;
        }

        return angle;

    }

    private float getNoseAndMouthDistance(PointF nose, PointF mouth) {
        return (float) Math.hypot(mouth.x - nose.x, mouth.y - nose.y);
    }

}
