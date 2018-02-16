package seopftware.fundmytravel.function.googlevision;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import adil.dev.lib.materialnumberpicker.dialog.NumberPickerDialog;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import seopftware.fundmytravel.R;
import seopftware.fundmytravel.function.googlevision.canvas.BestPaintBoard;
import seopftware.fundmytravel.function.googlevision.canvas.ColorPalette_Activity;
import seopftware.fundmytravel.function.googlevision.canvas.OnColorSelectedListener;
import seopftware.fundmytravel.function.googlevision.canvas.OnPenSelectedListener;
import seopftware.fundmytravel.function.googlevision.canvas.PenPalette_Activity;
import seopftware.fundmytravel.function.googlevision.sticker.Material_Activity;
import seopftware.fundmytravel.function.googlevision.sticker.StickerView;
import seopftware.fundmytravel.function.googlevision.sticker.StickerViewLayout;
import seopftware.fundmytravel.function.retrofit.HttpService;

import static seopftware.fundmytravel.activity.Home_Profile_Activity.PIC_MESSAGE_USERINFO;
import static seopftware.fundmytravel.activity.Home_Profile_Activity.PIC_MESSAGE_USERINFO_ID;
import static seopftware.fundmytravel.function.MyApp.SERVER_URL;
import static seopftware.fundmytravel.function.MyApp.TimeCheck;
import static seopftware.fundmytravel.function.MyApp.USER_ID;
import static seopftware.fundmytravel.function.MyApp.USER_NAME;
import static seopftware.fundmytravel.function.MyApp.USER_PHOTO;
import static seopftware.fundmytravel.function.chatting.Chat_Service.channel;

public class FaceCanvas_Activity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "all_" + "FaceCanvas_Activity";
    public static final int SELECT_STICKER_REQUEST_CODE = 123; // 스티커 선택을 위한 request code 값

    BestPaintBoard board;
    StickerViewLayout mStickerLayout;
    Bitmap src;
    FrameLayout frameLayout;
    LinearLayout optionLayout, boardLayout, paintLayout; // 이미지 옵션창, 페인트 작업할 레이아웃, 페인트 옵션창

    // 서버로 보낼 파일 변수
//    File pic_file;

    // 버튼
    ImageButton ibtn_save; // 이미지 저장
    ImageButton ibtn_pic_send; // 이미지 보내기
    ImageButton ibtn_exit; // 뒤로가기
    ImageButton ibtn_sticker, ibtn_timer, ibtn_paint, ibtn_color, ibtn_border, ibtn_undo; // 스티커, 페인트, 색깔, 굵기, 지우개, 취소

    // 페인트 옵션 변수들
    int i = 0; // 옵션창 껐다 켰다 하기
    int mColor = 0xff000000;
    int mSize = 2;
    int oldColor;
    int oldSize;
    boolean eraserSelected = false;

    String limited_time ="5"; // 기본 사진 종료 시간 설정
    String image_file_name; // 보내고자 하는 이미지 파일 이름

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 풀 스크린 만들기
        requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀바 없애기
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // 상태바 없애기
        setContentView(R.layout.activity_face_canvas);

        // UI 설정
        frameLayout = (FrameLayout) findViewById(R.id.frameLayout); // 스크린샷 찍을 부분
        optionLayout = (LinearLayout) findViewById(R.id.optionLayout); // 이미지 수정 옵션창
        boardLayout = (LinearLayout) findViewById(R.id.boardLayout); // canvas 작업할 곳
        paintLayout = (LinearLayout) findViewById(R.id.paintLayout); // 캔버스 옵션창
        mStickerLayout = (StickerViewLayout) findViewById(R.id.sticker_layout);

        optionLayout.bringToFront();

        // Button 설정

        //1.저장
        ibtn_save = (ImageButton) findViewById(R.id.ibtn_save);
        ibtn_save.setOnClickListener(this);
        ibtn_save.bringToFront();

        // 사진 보내기
        ibtn_pic_send = (ImageButton) findViewById(R.id.ibtn_pic_send);
        ibtn_pic_send.setOnClickListener(this);
        ibtn_pic_send.bringToFront();

        // 타이머
        ibtn_timer= (ImageButton) findViewById(R.id.ibtn_timer);
        ibtn_timer.setOnClickListener(this);
        ibtn_timer.bringToFront();

        //나가기
        ibtn_exit = (ImageButton) findViewById(R.id.ibtn_exit);
        ibtn_exit.setOnClickListener(this);
        ibtn_exit.bringToFront();

        //스티커
        ibtn_sticker = (ImageButton) findViewById(R.id.ibtn_sticker);
        ibtn_sticker.setOnClickListener(this);

/*        //페인트 옵션 열기
        ibtn_paint = (ImageButton) findViewById(R.id.ibtn_paint);
        ibtn_paint.setOnClickListener(this);*/


        //색깔 변경
        ibtn_color = (ImageButton) findViewById(R.id.ibtn_color);
        ibtn_color.setOnClickListener(this);

        //굵기 변경
        ibtn_border = (ImageButton) findViewById(R.id.ibtn_border);
        ibtn_border.setOnClickListener(this);

        //되돌리기
        ibtn_undo = (ImageButton) findViewById(R.id.ibtn_undo);
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
                picSend();
                    break;

                // 나가기
            case R.id.ibtn_exit:
                finish();
                break;


                // 제한 시간 설정
            case R.id.ibtn_timer:
                numberPicker();
                break;

            // 스티커
            case R.id.ibtn_sticker:
                // 스티커를 보여주는 화면으로 전환
                Intent intent = new Intent();
                intent.setClass(FaceCanvas_Activity.this, Material_Activity.class);
                startActivityForResult(intent, SELECT_STICKER_REQUEST_CODE); // 스티커 불러오기 위한 화면
                overridePendingTransition(R.anim.push_up, 0); // 애니메이션 효과
                break;

/*            // 페인트 옵션 열기
            case R.id.ibtn_paint:
                if (i == 0) {
                    paintLayout.setVisibility(View.VISIBLE);
                    i++;
                } else {
                    paintLayout.setVisibility(View.INVISIBLE);
                    i = 0;
                }
                break;*/

            // 색깔 변경
            case R.id.ibtn_color:
                colorChange();
                break;

            // 굵기 변경
            case R.id.ibtn_border:
                borderChange();
                break;

            // 되돌리기
            case R.id.ibtn_undo:
                board.undo();
                break;
        }
    }

    // 페인트 색깔 변경
    private void colorChange() {
        ColorPalette_Activity.listener = new OnColorSelectedListener() {
            public void onColorSelected(int color) {
                mColor = color;
                board.updatePaintProperty(mColor, mSize); // 현재 페인트의 상태 업데이트
            }
        };

        // show color palette dialog
        Intent intent = new Intent(getApplicationContext(), ColorPalette_Activity.class);
        startActivity(intent);
    }

    // 페인트 두께 변경
    private void borderChange() {

        PenPalette_Activity.listener = new OnPenSelectedListener() {
            public void onPenSelected(int size) {
                mSize = size; // 변경한 페인트 두께의 사이즈 값을 받아온다.
                board.updatePaintProperty(mColor, mSize); // 현재 페인트의 상태 업데이트
            }
        };

        // show pen palette dialog
        Intent intent = new Intent(getApplicationContext(), PenPalette_Activity.class);
        startActivity(intent);
    }

    // 메세지 종료 시간 설정
    private void numberPicker() {
        NumberPickerDialog dialog=new NumberPickerDialog(FaceCanvas_Activity.this, 1, 10, new NumberPickerDialog.NumberPickerCallBack() {
            @Override
            public void onSelectingValue(int value) {
                Toast.makeText(FaceCanvas_Activity.this, "Selected "+String.valueOf(value), Toast.LENGTH_SHORT).show();
                limited_time= String.valueOf(value);
            }
        });
        dialog.show();
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

            if (requestCode == SELECT_STICKER_REQUEST_CODE && requestCode == RESULT_OK) {
                Log.d(TAG, "여기서 Sticker View가 추가됩니다.11111");

                if (data != null) {


                }
            }
        }
    } // onActivityResult() finish


    private void picSend() {

    }


    // 화면 스크린샷
    private void takeScreenshot() {

        try {
            // image naming and path  to include sd card  appending name you choose for file
            // 저장할 주소 + 이름

            image_file_name = Long.toString(System.currentTimeMillis()) + "_save.jpg";
            File storagePath1 = new File(Environment.getExternalStorageDirectory() + "/FundMyTravel/"); // 저장 경로 설정
            File pic_file = new File(storagePath1, image_file_name); // 파일 저장 경로, 파일 이름

            // create bitmap screen capture
            frameLayout.setDrawingCacheEnabled(true); // 뷰가 업데이트 될 때마다 그 때의 뷰 이미지를 Drawing cache에 저장할지 여부를 결정합니다.
            Bitmap bitmap = frameLayout.getDrawingCache();

            // 이미지 파일 생성
            FileOutputStream outputStream = new FileOutputStream(pic_file);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);

            frameLayout.destroyDrawingCache();
            outputStream.flush();
            outputStream.close();

            Toast.makeText(getApplicationContext(), "이미지 저장완료 : " + pic_file, Toast.LENGTH_LONG).show();


            pictoServer(pic_file);


        } catch (Throwable e) {
            // Several error may come out with file handling or OOM
            e.printStackTrace();
        }
    }

    // 1. 사진을 서버로 전송
    // 2. DB에 이미지 메세지 내용 저장
    // 3. netty를 통해 상대방에게 메세지 전달
    private void pictoServer(File pic_file) {

        // =========================================================================================================
        // HTTP 통신
        // 메세지 (sender_id, receiver_id, image_name, message_limited_time, message_date)를 message_pic 테이블에 입력
        // =========================================================================================================

        // 메세지를 보낼 유저의 ID 받아오기
        SharedPreferences pref = getSharedPreferences(PIC_MESSAGE_USERINFO, Activity.MODE_PRIVATE);
        final int receiver_id = pref.getInt(PIC_MESSAGE_USERINFO_ID, 999); // 저장된 자동로그인 정보가 있으면 "success" 없을 경우 "fail"

        // 서버로 보낼 변수들
        String message_date = TimeCheck(); // 시간 받아오기


        // MultipartBody.Part is used to send also the actual file name
        RequestBody filepart = RequestBody.create(MediaType.parse("image/*"), pic_file);
        MultipartBody.Part file = MultipartBody.Part.createFormData("uploaded_file", pic_file.getName(), filepart); // 서버로 보낼 FILES['uploaded_file']['name];

        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("http://52.79.138.20/") // 서버 주소
                .baseUrl(SERVER_URL) // 서버 주소
                .addConverterFactory(GsonConverterFactory.create()) // Gson을 통해 Json 변환
                .build();

        HttpService httpService = retrofit.create(HttpService.class);
        Call<ResponseBody> comment = httpService.uploadPhoto(file, USER_ID, receiver_id, limited_time, message_date); // 1.사진을 보내는 사람의 아이디(me), 2.받는 사람의 ID, 3.이미지 경로, 4.메세지 종료 시간, 5.메세지를 보낸 시간
        comment.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    Log.d(TAG, "response : " + response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {

                    // todo 여기서 result 치환이 잘 안되고 있다. 공백문자 다시 한번 더 체크해보기
//                   String result = response.body().string(); // 이미지 파일 저장에 성공하면 "success" 메세지를 서버로부터 받는다.
////                    result = result.replaceAll(" ", ""); // 문자열 공백 제거
////                    result = result.replace(System.getProperty("line.separator"), "");
////                    result = result.replace("\n","");
//
//                    Log.d(TAG, result);
//
//
//                    result = result.replaceAll("(\r\n|\r|\n|\n\r)", " ");
//                    Log.d(TAG, "new result : " + result);
//
//                    // 서버에 파일 저장 성공하면 상대방에게 메세지
//                    if (result.equals("success")) {

                        JSONObject object = new JSONObject();
                        object.put("user_id", USER_ID); // 보내는 사람 ID
                        object.put("receiver_id", receiver_id); // 받는 사람의 ID
                        object.put("message_type", "message_pic"); // 보내는 사람의 이름
                        object.put("image_file_name", image_file_name); // 보내는 사람의 Profile
                        object.put("sender_name", USER_NAME); // 보내는 사람의 이름
                        object.put("sender_profile", USER_PHOTO); // 보내는 사람의 Profile

                        String Object_Data = object.toString();
                        channel.writeAndFlush(Object_Data);
//                    }


                }

//                catch (IOException e) {
//                    e.printStackTrace();
//                }

                catch (JSONException e) {
                    e.printStackTrace();
                }





            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "no!!!", Toast.LENGTH_LONG).show();

            }
        });


        // =========================================================================================================
        // Netty를 통해 상대방에게 메세지 전달
        // =========================================================================================================



    }



    // 파일 경로 얻어오기
    private String getRealPathFromURIPath(Uri contentURI, Activity activity) {
        Cursor cursor = activity.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }
}





/*    // 지우개 사용
    private void useErase() {
        eraserSelected = !eraserSelected; // erase ( false -> true로 변경)

        // 지우개가 선택되었을 때
        if (eraserSelected) {
            oldColor = mColor; // 현재 저장되어 있는 색깔을 oldColor 변수에 저장
            oldSize = mSize; // 현재 저장되어 있는 두께를 oldSize 변수에 저장

            mColor = Color.WHITE; // 선을 지우기 위해 흰색으로 변경
            mSize = 20;

            board.updatePaintProperty(mColor, mSize); // 현재 페인트의 상태 업데이트

        }

        // 지우개가 선택되지 않았을 때
        else {

            mColor = oldColor; // 지우개 선택을 포기한 경우 이전의 색깔 값 받아오기
            mSize = oldSize; // 지우개 선택을 포기한 경우 이전의 두께 값 받아오기

            board.updatePaintProperty(mColor, mSize); // 지우개 모드가 아닌 이전의 페인트 상태로 업데이트
        }
    }*/