package seopftware.fundmytravel.activity;

import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.dinuscxj.progressbar.CircleProgressBar;

import java.util.Hashtable;
import java.util.Map;

import seopftware.fundmytravel.R;

import static seopftware.fundmytravel.function.MyApp.PIC_MESSAGE;
import static seopftware.fundmytravel.function.MyApp.SERVER_URL;
import static seopftware.fundmytravel.function.MyApp.numberofpic;

public class Pic_Receive_Activity extends AppCompatActivity {

    private static String TAG = "all_"+"Pic_Receive_Activity";

    LinearLayout linearLayout;
    ImageView imageView;
    Bitmap noti_bitmap;

    // Custom CircleProgress Bar
    private CircleProgressBar mSolidProgressBar;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 풀 스크린 만들기
        requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀바 없애기
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // 상태바 없애기
        setContentView(R.layout.activity_pic_receive);

        // 받은 알람 메세지 0으로 초기화 시켜주기
        PIC_MESSAGE = 0;

        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        imageView = (ImageView) findViewById(R.id.imageView);
        mSolidProgressBar = (CircleProgressBar) findViewById(R.id.solid_progress);
        mSolidProgressBar.bringToFront();

//        Collections.reverse(numberofpic); // 이미지뷰 역순으로 뒤집기

//        ImageView iv = new ImageView(this);
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.MATCH_PARENT);
//        iv.setLayoutParams(params);
//        linearLayout.addView(iv);

//        Glide.with(getApplicationContext()).load(SERVER_URL + "/pic_message/"+numberofpic.get(0)).into(imageView); // 사각형 프로필
        Glide.with(getApplicationContext()).load(SERVER_URL + "/pic_message/"+numberofpic).into(imageView); // 사각형 프로필

        simulateProgress();

//        for (int i=0; i<=numberofpic.size(); i++) {
//
//            // bitmap으로 굳이 변경하지 말고 glide로 이미지 바로 띄우기
////            String image_name = numberofpic.get(i);
////            try {
////                noti_bitmap = Glide.with(getApplicationContext()).load("http://115.71.239.151/" + image_name).asBitmap().into(100, 100).get();
////            } catch (InterruptedException e) {
////                e.printStackTrace();
////            } catch (ExecutionException e) {
////                e.printStackTrace();
////            }
//
//
//            iv.setImageBitmap(noti_bitmap);
//            linearLayout.addView(iv);
//        }




//        imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // 다음 이미지로 넘어가기
//                int pic = numberofpic.size(); // 받은 이미지 갯수
//
//                // 사진이 1장 이상일 때
//                if (pic == numberofpic.size()) {
//                    numberofpic.get(0); // 첫 번째 사진 경로가 담겨있음
//                }
//
//
//                // 사진이 0장일 때
//                else {
//
//                    numberofpic.clear();
//                    // 만약 마지막 이미지라면 액티비티 종료
//                    finish();
//                }
//            }
//        });

    }

    private void simulateProgress() {
        ValueAnimator animator = ValueAnimator.ofInt(0, 100);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int progress = (int) animation.getAnimatedValue();
                mSolidProgressBar.setProgress(progress);
            }
        });

        animator.setDuration(5000); // 5초 동안 프로그레스바 돌아감
        animator.start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                messageStatus_Update();
                finish();
            }
        }).start();
    }

    // HTTP 통신
    // 메세지를 읽은 상태 업데이트
    private void messageStatus_Update() {
        String url = "http://52.79.138.20/php/update/update_messagestatus.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "결과값 : " + response.toString());

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Anything you want
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {


                long timeInMillis = System.currentTimeMillis();
                String message_date = String.valueOf(timeInMillis);

                Map<String, String> map = new Hashtable<>();
                map.put("message_date", String.valueOf(message_date));

                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
}
