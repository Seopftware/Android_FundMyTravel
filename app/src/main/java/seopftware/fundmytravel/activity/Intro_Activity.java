package seopftware.fundmytravel.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import seopftware.fundmytravel.R;

/**
 * 인트로 화면
 * @author 김인섭
 * @version 1.0.0
 * @since 2017-12-22 오전 11:35
 * @class comment
 *   이 클래스는 인트로 화면용으로 만들어졌습니다.
 **/

public class Intro_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // 시스템 윈도우바 없애기
        setContentView(R.layout.activity_intro);

    }

    // 핸들러 실행
    // 3초가 지나면 다음 화면으로 넘어감
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {

            // 다음 화면인 Login_Activity로 넘어가기
            Intent intent=new Intent(getApplicationContext(), Login_Activity.class);
            startActivity(intent);
            finish();

        }

    };

    protected void onResume() {
        super.onResume();
        handler.postDelayed(runnable, 1000); // 핸들러 1초 딜레이
    }

    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }
}

