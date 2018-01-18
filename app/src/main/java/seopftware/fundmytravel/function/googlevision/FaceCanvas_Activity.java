package seopftware.fundmytravel.function.googlevision;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import seopftware.fundmytravel.R;
import seopftware.fundmytravel.function.googlevision.canvas.BestPaintBoard;
import seopftware.fundmytravel.function.googlevision.sticker.Material_Activity;
import seopftware.fundmytravel.function.googlevision.sticker.StickerView;
import seopftware.fundmytravel.function.googlevision.sticker.StickerViewLayout;

public class FaceCanvas_Activity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG= "all_"+"FaceCanvas_Activity";
    public static final int SELECT_STICKER_REQUEST_CODE = 123; // 스티커 선택을 위한 request code 값

    BestPaintBoard board;
    StickerViewLayout mStickerLayout;
    Bitmap src;
    FrameLayout frameLayout;
    LinearLayout optionLayout, boardLayout; // 이미지 옵션창, 페인트 옵션창

    // 버튼
    ImageButton ibtn_save; // 이미지 저장
    ImageButton ibtn_pic_send; // 이미지 보내기
    ImageButton ibtn_exit; // 뒤로가기
    ImageButton ibtn_sticker, ibtn_paint, ibtn_color, ibtn_border, ibtn_eraser, ibtn_undo; // 스티커, 페인트, 색깔, 굵기, 지우개, 취소

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 풀 스크린 만들기
        requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀바 없애기
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // 상태바 없애기
        setContentView(R.layout.activity_face_canvas);

        // UI 설정
        frameLayout= (FrameLayout) findViewById(R.id.frameLayout); // 스크린샷 찍을 부분
        optionLayout= (LinearLayout) findViewById(R.id.optionLayout); // 이미지 수정 옵션창
        boardLayout= (LinearLayout) findViewById(R.id.boardLayout); // canvas 작업할 곳
        mStickerLayout = (StickerViewLayout) findViewById(R.id.sticker_layout);

        optionLayout.bringToFront();

        // Button 설정

        //1.저장
        ibtn_save= (ImageButton) findViewById(R.id.ibtn_save);
        ibtn_save.setOnClickListener(this);
        ibtn_save.bringToFront();

        ibtn_pic_send= (ImageButton) findViewById(R.id.ibtn_pic_send);
        ibtn_pic_send.setOnClickListener(this);
        ibtn_pic_send.bringToFront();

        //나가기
        ibtn_exit= (ImageButton) findViewById(R.id.ibtn_exit);
        ibtn_exit.setOnClickListener(this);
        ibtn_exit.bringToFront();

        //스티커
        ibtn_sticker= (ImageButton) findViewById(R.id.ibtn_sticker);
        ibtn_sticker.setOnClickListener(this);

        //페인트 옵션 열기
        ibtn_paint= (ImageButton) findViewById(R.id.ibtn_paint);
        ibtn_paint.setOnClickListener(this);


        //색깔 변경
        ibtn_color= (ImageButton) findViewById(R.id.ibtn_color);
        ibtn_color.setOnClickListener(this);

        //굵기 변경
        ibtn_border= (ImageButton) findViewById(R.id.ibtn_border);
        ibtn_border.setOnClickListener(this);

        //지우개
        ibtn_eraser= (ImageButton) findViewById(R.id.ibtn_eraser);
        ibtn_eraser.setOnClickListener(this);

        //되돌리기
        ibtn_undo= (ImageButton) findViewById(R.id.ibtn_undo);
        ibtn_undo.setOnClickListener(this);


        Bitmap getbitmap = null;
        try {

            File storagePath = new File(Environment.getExternalStorageDirectory() + "/FundMyTravel/");
            String filename = getIntent().getStringExtra("image");
            FileInputStream is = new FileInputStream(new File(storagePath, filename));
            getbitmap = BitmapFactory.decodeStream(is);
//            scaled_bmp = Bitmap.createScaledBitmap(getbitmap, 720, 1280, false);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        board = new BestPaintBoard(getApplicationContext(), getbitmap);
        board.setLayoutParams(params);
        board.setPadding(2, 2, 2, 2);
        boardLayout.addView(board);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            // 저장
            case R.id.ibtn_save:
                takeScreenshot();
                break;

            // 보내기
            case R.id.ibtn_pic_send:

                break;

            // 나가기
            case R.id.ibtn_exit:
                finish();
                break;

            // 스티커
            case R.id.ibtn_sticker:
                // 스티커를 보여주는 화면으로 전환
                Intent intent = new Intent();
                intent.setClass(FaceCanvas_Activity.this, Material_Activity.class);
                startActivityForResult(intent, SELECT_STICKER_REQUEST_CODE); // 스티커 불러오기 위한 화면
                overridePendingTransition(R.anim.push_up, 0); // 애니메이션 효과
                break;

            // 페인트 옵션 열기
            case R.id.ibtn_paint:

                break;

            // 색깔 변경
            case R.id.ibtn_color:
                break;

            // 굵기 변경
            case R.id.ibtn_border:
                break;

            // 지우개
            case R.id.ibtn_eraser:
                break;

            // 되돌리기
            case R.id.ibtn_undo:
                break;
        }
    }


    // 스티커를 보여주는 화면에서 받아온 값
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "requestCode : " + requestCode);
        Log.d(TAG, "resultCode : " + resultCode);
        Log.d(TAG, "data : " + data);
        Log.d(TAG, "여기서 Sticker View가 추가됩니다.22222222");

        int stickerId = data.getIntExtra(Material_Activity.EXTRA_STICKER_ID, 0); // sticker Id 가져 온다음 StickerView로 보낸다.
        if (stickerId != 0) {

            Log.d(TAG, "stcierID 값 : " + stickerId);
            StickerView view = new StickerView(FaceCanvas_Activity.this, stickerId); // 리소스 분해 후 이미지 삽입 함
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            view.setLayoutParams(params);
            mStickerLayout.addView(view);
            Log.d(TAG, "여기서 Sticker View가 추가됩니다.333333333");
            mStickerLayout.bringToFront();
            view.bringToFront();

        if(requestCode == SELECT_STICKER_REQUEST_CODE && requestCode == RESULT_OK) {
            Log.d(TAG, "여기서 Sticker View가 추가됩니다.11111");

            if(data !=null) {


                }
            }


        }
    }

    // 화면 스크린샷
    private void takeScreenshot() {

        try {
            // image naming and path  to include sd card  appending name you choose for file
            // 저장할 주소 + 이름
            File storagePath1 = new File(Environment.getExternalStorageDirectory() + "/FundMyTravel/"); // 저장 경로 설정
            File file = new File(storagePath1, Long.toString(System.currentTimeMillis()) + "_save.jpg"); // 파일 저장 경로, 파일 이름

            // create bitmap screen capture
            frameLayout.setDrawingCacheEnabled(true); // 뷰가 업데이트 될 때마다 그 때의 뷰 이미지를 Drawing cache에 저장할지 여부를 결정합니다.
            Bitmap bitmap = frameLayout.getDrawingCache();

            // 이미지 파일 생성
            FileOutputStream outputStream = new FileOutputStream(file);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);

            frameLayout.destroyDrawingCache();
            outputStream.flush();
            outputStream.close();

            Toast.makeText(getApplicationContext(), "이미지 저장완료 : " + file , Toast.LENGTH_LONG).show();


        } catch (Throwable e) {
            // Several error may come out with file handling or OOM
            e.printStackTrace();
        }
    }
}
