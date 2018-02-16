package seopftware.fundmytravel.function.vodEmoticon;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Random;

import seopftware.fundmytravel.R;

// 이모티콘을 클릭했을 때, 이모티콘에 애니메이션과 같은 효과를 주는 곳
// 화면의 오른쪽에서 왼쪽으로 흐르는 효과를 줌. (Canvas를 이용해서 화면에 bitmap을 계속 그려주는 방식을 사용)

public class EmoticonsView extends View {

  // Canvas(도화지) 이모티콘을 그려줄 붓(Paint) 역활을 함
  private Paint mPaint;

  // 도화지(Canvas)에 어떤 도형(직선, 곡선, 다각형)을 그리는데 미리 그려진 궤적 정보
  private Path mAnimPath; // 이모티콘의 이동경로

  // Matrix를 이용한 이미지 확대/축소/회전/이동을 구현
  private Matrix mMatrix;
  private Bitmap mLike48, mLove48, mHaha48, mWow48, mSad48, mAngry48; // 클릭한 이모티콘들을 담을 bitmap 변수

  // ArrayList에 이모티콘의 정보(이모티콘, x좌표, y좌표) 객체를 담는다. (이모티콘을 클릭한 순서대로 뿌려주기 위해)
  private ArrayList<LiveEmoticon> mLiveEmoticons = new ArrayList<>();

  // 이모티콘이 이동할 좌표
  private final int X_CORDINATE_STEP = 8, Y_CORDINATE_OFFSET = 100, Y_CORDINATE_RANGE = 200;
  private int mScreenWidth; // 화면의 가로 해상도

  public EmoticonsView(Context activity) {
    super(activity);

  }

  public EmoticonsView(Context activity, AttributeSet attrs) {
    super(activity, attrs);
  }

  public EmoticonsView(Context activity, AttributeSet attrs, int defStyleAttr) {
    super(activity, attrs, defStyleAttr);
  }

  // 뷰를 생성해주는 곳
  public void initView(Activity activity) {

    // DisplayMetrics => 화면의 해상도를 구할 수 있게 도와주는 함수
    DisplayMetrics displayMetrics = new DisplayMetrics();
    activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
    mScreenWidth = displayMetrics.widthPixels;

    // 이모티콘을 그려주는 함수들
    mPaint = new Paint();
    mAnimPath = new Path();
    mMatrix = new Matrix();

    // 감정 이모티콘들으 담는 곳
    Resources res = getResources();

    //Like emoticons
    mLike48 = BitmapFactory.decodeResource(res, R.drawable.like_48);
    //Love emoticons
    mLove48 = BitmapFactory.decodeResource(res, R.drawable.love_48);
    //Haha emoticons
    mHaha48 = BitmapFactory.decodeResource(res, R.drawable.haha_48);
    //Wow emoticons
    mWow48 = BitmapFactory.decodeResource(res, R.drawable.wow_48);
    //Sad emoticons
    mSad48 = BitmapFactory.decodeResource(res, R.drawable.sad_48);
    //Angry emoticons
    mAngry48 = BitmapFactory.decodeResource(res, R.drawable.angry_48);
  }

  // Canvas draw
  protected void onDraw(Canvas canvas) {
    canvas.drawPath(mAnimPath, mPaint);
    drawAllLiveEmoticons(canvas);
  }

  // 이모티콘을 그려주는 곳
  private void drawAllLiveEmoticons(Canvas canvas) {
    ListIterator<LiveEmoticon> iterator = mLiveEmoticons.listIterator();

    // 이모티콘 버튼 클릭을 기다렸다가 계속해서 뿌려준다.
    while (iterator.hasNext()) {
      Object object = iterator.next(); // 이모티콘 객체 불러오기

      // Emoticon 정보를 담는 아이템 객체(이모티콘, X좌표, Y좌표)
      LiveEmoticon liveEmoticon = (LiveEmoticon) object;
      Integer xCoordinate = liveEmoticon.getxCordinate() - X_CORDINATE_STEP; // X 좌표
      Integer yCoordinate = liveEmoticon.getyCordinate(); // Y 좌표
      liveEmoticon.setxCordinate(xCoordinate);

      // if x좌표 보다 크면 이모티콘을 그려준다.
      if (xCoordinate > 0) {
        mMatrix.reset();
        mMatrix.postTranslate(xCoordinate, yCoordinate);
        resizeImageSizeBasedOnXCoordinates(canvas, liveEmoticon);

        // View에서 onDraw() 가 호출 된 이후, 화면은 더 이상 갱신되지 않은채로 남아있게 된다.
        // 하지만 애니메이션등 기타 효과들을 구현하기 위해서는 이벤트에 따라 화면을 갱신 할 필요가 있어서 invalidate()를 통해 화면을 갱신해준다.
        // 즉, 현재 View에 나타나 있는 모든 그림 및 이미지들을 무효화해서 화면에 나타나지 않게 한다는 것이다. 뷰를 무효화 한 후에는 다시 화면을 그리기 위해 onDraw를 호출하는 것
        invalidate(); // View 의 화면 갱신
      }
      // 화면 밖으로 나가면 이모티콘을 없애준다.
      else {
        iterator.remove();
      }
    }
  }

  // 이모티콘이 이동하는 경로를 표시 (X축의 왼쪽으로 이동할 수록 크기가 점점 작아지게 구현
  private void resizeImageSizeBasedOnXCoordinates(Canvas canvas, LiveEmoticon liveEmoticon) {
    if (liveEmoticon == null) {
      return;
    }

    int xCoordinate = liveEmoticon.getxCordinate(); // 이모티콘의 X 좌표
    Bitmap bitMap48 = null; // 클라에게 보여줄 이모티콘을 담을 비트맵
    Bitmap scaled = null; // 비트맵 사이즈 변경하는 부분

    Emoticons emoticons = liveEmoticon.getEmoticons();

    // 만약 emoticons가 null이면(이모티콘을 클릭하지 않았을 떄) return
    if (emoticons == null) {
      return;
    }

    switch (emoticons) {
      // 좋아요
      case LIKE:
        bitMap48 = mLike48;
        break;
      // 사랑해요
      case LOVE:
        bitMap48 = mLove48;
        break;
      // 웃겨요
      case HAHA:
        bitMap48 = mHaha48;
        break;
      // 놀라워요
      case WOW:
        bitMap48 = mWow48;
        break;
      // 슬퍼요
      case SAD:
        bitMap48 = mSad48;
        break;
      //화나요
      case ANGRY:
        bitMap48 = mAngry48;
        break;
    }

    // 이모티콘이 이동하는 좌표를 그려주는 곳
    if (xCoordinate > mScreenWidth / 2) {
      canvas.drawBitmap(bitMap48, mMatrix, null);
    }

    else if (xCoordinate > mScreenWidth / 4) {
      scaled = Bitmap.createScaledBitmap(bitMap48, 3 * bitMap48.getWidth() / 4, 3 * bitMap48.getHeight() / 4, false);
      canvas.drawBitmap(scaled, mMatrix, null);
    }

    else {
      scaled = Bitmap.createScaledBitmap(bitMap48, bitMap48.getWidth() / 2, bitMap48.getHeight() / 2, false);
      canvas.drawBitmap(scaled, mMatrix, null);
    }
  }

  // 클릭 이벤트가 일어날 때 마다 addView를 통해서 이모티콘을 추가로 그려 준다.
  public void addView(Emoticons emoticons) {
    int startXCoordinate = mScreenWidth;
    int startYCoordinate = new Random().nextInt(Y_CORDINATE_RANGE) + Y_CORDINATE_OFFSET;
    LiveEmoticon liveEmoticon = new LiveEmoticon(emoticons, startXCoordinate, startYCoordinate);
    mLiveEmoticons.add(liveEmoticon);
    invalidate();
  }
}