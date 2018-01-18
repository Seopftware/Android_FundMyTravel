package seopftware.fundmytravel.function.googlevision.sticker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import seopftware.fundmytravel.R;


/**
 * 매트릭스 제어를 통해 스티커를 이동, 확대/축소, 회전 함
 */
public class StickerView extends View {

    private Context context;
    private String imgPath;
    private int stickerResId;
    private Bitmap mainBmp, deleteBmp, controlBmp;
    private int mainBmpWidth, mainBmpHeight, deleteBmpWidth, deleteBmpHeight, controlBmpWidth, controlBmpHeight;

    private float[] srcPs, dstPs;
    private Matrix matrix;
    private Paint paint, paintFrame;
    private float deltaX = 0, deltaY = 0;   // 변위 값
    private float scaleValue = 1;           // 스티커 축소 및 확대 값

    private Point lastPoint;
    private Point prePivot, lastPivot;
    private float defaultDegree, preDegree, lastDegree;
    private Point symmetricPoint = new Point();    // 현재 동작점
    private Point centerPoint = new Point();       // 중심점
    private Point rightBottomPoint = new Point();  // 오른쪽 밑(줌 포인트 및 회전)

    /**
     * 이미지 조작 유형
     */
    public static final int OPER_DEFAULT = -1;      // 기본값
    public static final int OPER_TRANSLATE = 0;     // 이동
    public static final int OPER_SCALE = 1;         // 확대 축소
    public static final int OPER_ROTATE = 2;        // 회전
    public static final int OPER_SELECTED = 3;      // 선택
    public int lastOper = OPER_SELECTED;

    private boolean isSelected = true;              // 선택여부 확인
    private boolean isActive = true;               // 삭제여부 확인

    /* 화면 좌표
     * 0--------1
     * |        |
      *|    4   |
     * |        |
     * 3--------2
     */
    public static final int CTR_NONE = -1;
    public static final int CTR_LEFT_TOP = 0;
    public static final int CTR_RIGHT_BOTTOM = 2;
    public static final int CTR_MID_MID = 4;
    public int current_ctr = CTR_NONE;


    /**
     * 재료 선택 및 제거 모니터링
     */
    public interface OnSelectedListener {
        void onSelected();
    }

    public OnSelectedListener mOnSelectedListener = null;

    public void setOnSelectedListener(OnSelectedListener listener) {
        this.mOnSelectedListener = listener;
    }

    /**
     * View 메서드 옵션 만들기
     */
    public interface OnRemovedListener {
        void onRemoved();
    }

    public OnRemovedListener mOnRemovedListener = null;

    public void setOnRemovedListener(OnRemovedListener listener) {
        this.mOnRemovedListener = listener;
    }

    public StickerView(Context context, int stickerResId) {
        super(context);
        this.context = context;
        this.stickerResId = stickerResId;
        initData(stickerResId);
    }

    /**
     * 데이터 초기화
     */
    private void initData(int stickerResId) {
//        mainBmp = BitmapFactory.decodeFile(imgPath);
        mainBmp = BitmapFactory.decodeResource(getResources(), stickerResId);
        deleteBmp = BitmapFactory.decodeResource(this.context.getResources(), R.drawable.ic_f_delete_normal);
        controlBmp = BitmapFactory.decodeResource(this.context.getResources(), R.drawable.ic_f_rotate_normal);
        mainBmpWidth = mainBmp.getWidth();
        mainBmpHeight = mainBmp.getHeight();
        deleteBmpWidth = deleteBmp.getWidth();
        deleteBmpHeight = deleteBmp.getHeight();
        controlBmpWidth = controlBmp.getWidth();
        controlBmpHeight = controlBmp.getHeight();

        srcPs = new float[]{
                0, 0,
                mainBmpWidth, 0,
                mainBmpWidth, mainBmpHeight,
                0, mainBmpHeight,
                mainBmpWidth / 2, mainBmpHeight / 2
        };
        dstPs = srcPs.clone();

        matrix = new Matrix();

        // 중앙 위치 이후 이동
        prePivot = new Point(mainBmpWidth / 2, mainBmpHeight / 2);

        // 중심 위치 이전 이동
        lastPivot = new Point(mainBmpWidth / 2, mainBmpHeight / 2);

        // 이전 터치 포인트 위치
        lastPoint = new Point(0, 0);

        paint = new Paint();
        paint.setAntiAlias(true);

        paintFrame = new Paint();
        paintFrame.setColor(Color.WHITE);
        paintFrame.setStrokeWidth(getResources().getDimension(R.dimen.stickerview_frame_width));
        paintFrame.setAntiAlias(true);

        defaultDegree = lastDegree = computeDegree(new Point(mainBmpWidth, mainBmpHeight), new Point(mainBmpWidth / 2, mainBmpHeight / 2));

        setMatrix(OPER_DEFAULT);
    }

    // 행렬 변환
    private void setMatrix(int operationType) {
        switch (operationType) {
            case OPER_TRANSLATE:
                matrix.postTranslate(deltaX, deltaY);
                break;
            case OPER_SCALE:
                matrix.postScale(scaleValue, scaleValue, dstPs[CTR_MID_MID * 2], dstPs[CTR_MID_MID * 2 + 1]);
                break;
            case OPER_ROTATE:
                matrix.postRotate(preDegree - lastDegree, dstPs[CTR_MID_MID * 2], dstPs[CTR_MID_MID * 2 + 1]);
                break;
        }

        matrix.mapPoints(dstPs, srcPs);
    }

    // 터치 포인트 위치 확인
    private boolean isOnPic(int x, int y) {
        // 获取逆向矩阵
        Matrix inMatrix = new Matrix();
        matrix.invert(inMatrix);

        float[] tempPs = new float[]{0, 0};
        inMatrix.mapPoints(tempPs, new float[]{x, y});
        if (tempPs[0] > 0 && tempPs[0] < mainBmp.getWidth() && tempPs[1] > 0 && tempPs[1] < mainBmp.getHeight()) {
            return true;
        } else {
            return false;
        }
    }

    private int getOperationType(MotionEvent event) {

        int evX = (int) event.getX();
        int evY = (int) event.getY();
        int curOper = lastOper;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                current_ctr = isOnCP(evX, evY);
                if (current_ctr != CTR_NONE || isOnPic(evX, evY)) {
                    curOper = OPER_SELECTED;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (current_ctr == CTR_LEFT_TOP) {
                    // 删除饰品
                } else if (current_ctr == CTR_RIGHT_BOTTOM) {
                    curOper = OPER_ROTATE;
                } else if (lastOper == OPER_SELECTED) {
                    curOper = OPER_TRANSLATE;
                }
                break;
            case MotionEvent.ACTION_UP:
                curOper = OPER_SELECTED;
                break;
            default:
                break;
        }
        return curOper;

    }

    /**
     * 제어점이 있는 지점 판단
     *
     * @param evX
     * @param evY
     * @return
     */
    private int isOnCP(int evx, int evy) {
        Rect rect = new Rect(evx - controlBmpWidth / 2, evy - controlBmpHeight / 2, evx + controlBmpWidth / 2, evy + controlBmpHeight / 2);
        int res = 0;
        for (int i = 0; i < dstPs.length; i += 2) {
            if (rect.contains((int) dstPs[i], (int) dstPs[i + 1])) {
                return res;
            }
            ++res;
        }
        return CTR_NONE;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int evX = (int) event.getX();
        int evY = (int) event.getY();

        if (!isOnPic(evX, evY) && isOnCP(evX, evY) == CTR_NONE) {
            isSelected = false;
            invalidate();//重绘
            return false;
        } else if (isOnCP(evX, evY) == CTR_LEFT_TOP) {
            mOnRemovedListener.onRemoved();
            isActive = false;
            invalidate();//重绘
        } else {
            mOnSelectedListener.onSelected();
            bringToFront();
            requestLayout();

            int operType = OPER_DEFAULT;
            operType = getOperationType(event);

            switch (operType) {
                case OPER_TRANSLATE:
                    if (isOnPic(evX, evY)) {
                        translate(evX, evY);
                    }
                    break;
                case OPER_ROTATE:
                    rotate(event);
                    scale(event);
                    break;
            }

            lastPoint.x = evX;
            lastPoint.y = evY;

            lastOper = operType;
            isSelected = true;
            invalidate();// 다시 그리기
        }

        return true;
    }

    /**
     * 이동
     *
     * @param evx
     * @param evy
     */
    private void translate(int evx, int evy) {
        deltaX = evx - lastPoint.x;
        deltaY = evy - lastPoint.y;

        prePivot.x += deltaX;
        prePivot.y += deltaY;

        lastPivot.x = prePivot.x;
        lastPivot.y = prePivot.y;

        setMatrix(OPER_TRANSLATE); //设置矩阵
    }

    /**
     * 확대 / 축소
     * 0---1---2
     * |       |
     * 7   8   3
     * |       |
     * 6---5---4
     *
     * @param evX
     * @param evY
     */
    private void scale(MotionEvent event) {

        int pointIndex = current_ctr * 2;

        float px = dstPs[pointIndex];
        float py = dstPs[pointIndex + 1];

        float evx = event.getX();
        float evy = event.getY();

        float oppositeX = 0;
        float oppositeY = 0;

        oppositeX = dstPs[pointIndex - 4];
        oppositeY = dstPs[pointIndex - 3];

        float temp1 = getDistanceOfTwoPoints(px, py, oppositeX, oppositeY);
        float temp2 = getDistanceOfTwoPoints(evx, evy, oppositeX, oppositeY);

        this.scaleValue = temp2 / temp1;
        symmetricPoint.x = (int) oppositeX;
        symmetricPoint.y = (int) oppositeY;
        centerPoint.x = (int) (symmetricPoint.x + px) / 2;
        centerPoint.y = (int) (symmetricPoint.y + py) / 2;
        rightBottomPoint.x = (int) dstPs[8];
        rightBottomPoint.y = (int) dstPs[9];
        Log.i("img", "scaleValue is " + scaleValue);
        if (getScaleValue() < (float) 0.3 && scaleValue < (float) 1) {
            // 限定最小缩放比为0.3
        } else {
            setMatrix(OPER_SCALE);
        }
    }

    /**
     * 그림 회전 시키는 곳
     * 0---1---2
     * |       |
     * 7   8   3
     * |       |
     * 6---5---4
     *
     * @param evX
     * @param evY
     */
    private void rotate(MotionEvent event) {

        if (event.getPointerCount() == 2) {
            preDegree = computeDegree(new Point((int) event.getX(0), (int) event.getY(0)), new Point((int) event.getX(1), (int) event.getY(1)));
        } else {
            preDegree = computeDegree(new Point((int) event.getX(), (int) event.getY()), new Point((int) dstPs[8], (int) dstPs[9]));
        }
        setMatrix(OPER_ROTATE);
        lastDegree = preDegree;
    }


    /**
     * 두 점과 수직 방향 사이의 각도 계산
     *
     * @param p1
     * @param p2
     * @return
     */
    public float computeDegree(Point p1, Point p2) {
        float tran_x = p1.x - p2.x;
        float tran_y = p1.y - p2.y;
        float degree = 0.0f;
        float angle = (float) (Math.asin(tran_x / Math.sqrt(tran_x * tran_x + tran_y * tran_y)) * 180 / Math.PI);
        if (!Float.isNaN(angle)) {
            if (tran_x >= 0 && tran_y <= 0) { // 첫 번째 사분면
                degree = angle;
            } else if (tran_x <= 0 && tran_y <= 0) { // 두 번째 사분면
                degree = angle;
            } else if (tran_x <= 0 && tran_y >= 0) {// 세 번째 사분면
                degree = -180 - angle;
            } else if (tran_x >= 0 && tran_y >= 0) {// 네 번째 사분면
                degree = 180 - angle;
            }
        }
        return degree;
    }

    /**
     * 두 점 사이의 거리 계산
     *
     * @param p1
     * @param p2
     * @return
     */
    private float getDistanceOfTwoPoints(Point p1, Point p2) {
        return (float) (Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y)));
    }

    private float getDistanceOfTwoPoints(float x1, float y1, float x2, float y2) {
        return (float) (Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)));
    }


    // 실제 스티커들을 그려주는 곳
    @Override
    public void onDraw(Canvas canvas) {
        if (!isActive) {
            return;
        }
        canvas.drawBitmap(mainBmp, matrix, paint);// 그림 그리기
        if (isSelected) {
            drawFrame(canvas);// 테두리 그리기 (선택여부 확인을 위해)
            drawControlPoints(canvas);// 컨트롤 포인트 그려주기
        }

    }

    private void drawFrame(Canvas canvas) {
        canvas.drawLine(dstPs[0], dstPs[1], dstPs[2], dstPs[3], paintFrame);
        canvas.drawLine(dstPs[2], dstPs[3], dstPs[4], dstPs[5], paintFrame);
        canvas.drawLine(dstPs[4], dstPs[5], dstPs[6], dstPs[7], paintFrame);
        canvas.drawLine(dstPs[0], dstPs[1], dstPs[6], dstPs[7], paintFrame);
    }

    private void drawControlPoints(Canvas canvas) {
        canvas.drawBitmap(deleteBmp, dstPs[0] - deleteBmpWidth / 2, dstPs[1] - deleteBmpHeight / 2, paint);
        canvas.drawBitmap(controlBmp, dstPs[4] - controlBmpWidth / 2, dstPs[5] - controlBmpHeight / 2, paint);
    }

    // 회전각 얻기
    public float getDegree() {
        return lastDegree - defaultDegree;
    }

    // 스티커 중심점 좌표 가져오기
    public float[] getCenterPoint() {
        float[] centerPoint = new float[2];
        centerPoint[0] = dstPs[8];
        centerPoint[1] = dstPs[9];
        return centerPoint;
    }

    // 스티커 확대 / 축소 비율 가져오기(원본 이미지와 비교)
    public float getScaleValue() {
        float preDistance = (srcPs[8] - srcPs[0]) * (srcPs[8] - srcPs[0]) + (srcPs[9] - srcPs[1]) * (srcPs[9] - srcPs[1]);
        float lastDistance = (dstPs[8] - dstPs[0]) * (dstPs[8] - dstPs[0]) + (dstPs[9] - dstPs[1]) * (dstPs[9] - dstPs[1]);
        float scaleValue = (float) Math.sqrt(lastDistance / preDistance);
        return scaleValue;
    }

    // 스티커 이미지 경로 가져오기
    public String getImgPath() {
        return imgPath;
    }

}  
