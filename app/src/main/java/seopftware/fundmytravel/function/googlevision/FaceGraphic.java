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
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

import com.google.android.gms.vision.face.Face;

import seopftware.fundmytravel.function.googlevision.camera.GraphicOverlay;

import static seopftware.fundmytravel.function.googlevision.FaceDetect_Activity.bitmap_current_mask;

/**
 * Graphic instance for rendering face position, orientation, and landmarks within an associated
 * graphic overlay view.
 */
class FaceGraphic extends GraphicOverlay.Graphic {

    private static final String TAG = "all_" + FaceGraphic.class;

    private static final float FACE_POSITION_RADIUS = 10.0f;
    private static final float ID_TEXT_SIZE = 40.0f;
    private static final float ID_Y_OFFSET = 50.0f;
    private static final float ID_X_OFFSET = -50.0f;
    private static final float GENERIC_POS_OFFSET = 20.0f;
    private static final float GENERIC_NEG_OFFSET = -20.0f;

    private static final float BOX_STROKE_WIDTH = 5.0f;




    private static final int COLOR_CHOICES[] = {
            Color.BLUE,
            Color.CYAN,
            Color.GREEN,
            Color.MAGENTA,
            Color.RED,
            Color.WHITE,
            Color.YELLOW
    };
    private static int mCurrentColorIndex = 0;

    private Paint mFacePositionPaint;
    private Paint mIdPaint;
    private Paint mBoxPaint;

    private volatile Face mFace;
    private int mFaceId;
    private float mFaceHappiness;
    private Bitmap bitmap;
    Bitmap op;

    FaceGraphic(GraphicOverlay overlay) {
        super(overlay);

        mCurrentColorIndex = (mCurrentColorIndex + 1) % COLOR_CHOICES.length;
        final int selectedColor = COLOR_CHOICES[mCurrentColorIndex];

        mFacePositionPaint = new Paint();
        mFacePositionPaint.setColor(selectedColor);

        mIdPaint = new Paint();
        mIdPaint.setColor(selectedColor);
        mIdPaint.setTextSize(ID_TEXT_SIZE);

        mBoxPaint = new Paint();
        mBoxPaint.setColor(selectedColor);
        mBoxPaint.setStyle(Paint.Style.STROKE);
        mBoxPaint.setStrokeWidth(BOX_STROKE_WIDTH);

//        if(mask_type==1) {
//            bitmap = BitmapFactory.decodeResource(getOverlay().getContext().getResources(), R.drawable.dog2);
//        }
//
//
//        else if(mask_type==2) {
//            bitmap = BitmapFactory.decodeResource(getOverlay().getContext().getResources(), R.drawable.glasses11);
//
//        }

        op = bitmap_current_mask;


    }

    void setId(int id) {
        mFaceId = id;
    }


    /**
     * Updates the face instance from the detection of the most recent frame.  Invalidates the
     * relevant portions of the overlay to trigger a redraw.
     */
    void updateFace(Face face) {
        mFace = face;
        op = Bitmap.createScaledBitmap(bitmap_current_mask, (int) scaleX(face.getWidth()),
                (int) scaleY(((bitmap_current_mask.getHeight() * face.getWidth()) / bitmap_current_mask.getWidth())), false);
        postInvalidate();
    }

    /**
     * Draws the face annotations for position on the supplied canvas.
     */
    @Override
    public void draw(Canvas canvas) {

        Face face = mFace;
        if (face == null) {
            return;
        }

        // Draws a circle at the position of the detected face, with the face's track id below.
        float x = translateX(face.getPosition().x + face.getWidth() / 2);
        float y = translateY(face.getPosition().y + face.getHeight() / 2);
        float xOffset = scaleX(face.getWidth() / 2.0f);
        float yOffset = scaleY(face.getHeight() / 2.0f);
        float left = x - xOffset;
        float top = y - yOffset;
        float right = x + xOffset;
        float bottom = y + yOffset;
//        canvas.drawRect(left, top, right, bottom, mBoxPaint);
//        canvas.drawBitmap(op, left, top-20, new Paint());
        canvas.drawBitmap(op, left, top-20, new Paint());
    }

    private float getNoseAndMouthDistance(PointF nose, PointF mouth) {
        return (float) Math.hypot(mouth.x - nose.x, mouth.y - nose.y);
    }
}
