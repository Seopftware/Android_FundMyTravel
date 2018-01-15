package seopftware.fundmytravel.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seopftware.fundmytravel.R;
import seopftware.fundmytravel.function.chatting.Chat_Service;

import static seopftware.fundmytravel.function.MyApp.AUTO_LOGIN_KEY;
import static seopftware.fundmytravel.function.MyApp.AUTO_LOGIN_STATUS;
import static seopftware.fundmytravel.function.MyApp.AUTO_LOGIN_USERID;
import static seopftware.fundmytravel.function.MyApp.USER_ID;
import static seopftware.fundmytravel.function.MyApp.getMyInfo;

/**
 * 로그인 화면
 * @author 김인섭
 * @version 1.0.0
 * @class comment
 * @since 2018-01-06 오전 11:29
 * 이 액티비티는 사용자들이 로그인할 때 이용할 용도로 만들어졌습니다.
 **/

public class Login_Activity extends AppCompatActivity implements Button.OnClickListener {

    private static final String TAG = "all_" + "Login_Activity";

    // 로그인 버튼
    Button btn_login_phone; // 폰으로 로그인할 때 사용
    Button btn_login_google; // 구글 아이디로 로그인할 때 사용
    Button btn_login_naver; // 네이버 아이디로 로그인할 때 사용

    TextView tv_terms; // 이용약관을 보여줌

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // =========================================================================================================
        // 자동 로그인 확인을 위한 부분
        SharedPreferences autologin = getSharedPreferences(AUTO_LOGIN_STATUS, Activity.MODE_PRIVATE);
        String status = autologin.getString(AUTO_LOGIN_KEY, "fail"); // 저장된 자동로그인 정보가 있으면 "success" 없을 경우 "fail"

        // 자동 로그인 정보가 저장되어 있다면 "success" 아닌 경우 "fail"
        if (status.equals("success")) {

            // 로그인 한 경험이 있는 회원은 자동으로 Home 화면으로 넘어가게끔.
            USER_ID = autologin.getInt(AUTO_LOGIN_USERID, 0); // USER_ID 전역 변수에 고유 ID 값 담아서 어디서든 사용하기 편하게 해준다.
            Log.d(TAG, "USER_ID 값은? : " + USER_ID);

            Intent intent2 = new Intent(getApplicationContext(), Home_Activity.class);
            startActivity(intent2);
            finish();

            getMyInfo();

            // 네티와의 채팅 연결을 위한 Service 시작
            Intent intent1 = new Intent(Login_Activity.this, Chat_Service.class);
            Log.d(TAG, "채팅을 위한 (netty Channel connection)서비스 시작");
//            startService(intent1);

        }
        // =========================================================================================================


        // 버튼 선언
        btn_login_phone = (Button) findViewById(R.id.btn_login_phone); // 폰 로그인
        btn_login_google = (Button) findViewById(R.id.btn_login_google); // 구글 로그인
        btn_login_naver = (Button) findViewById(R.id.btn_login_naver); // 네이버 로그인

        // 클릭 이벤트를 위해 버튼에 클릭 리스너 달아주기
        btn_login_phone.setOnClickListener(this);
        btn_login_google.setOnClickListener(this);
        btn_login_naver.setOnClickListener(this);

        // Terms & Conditions 링크걸기
        tv_terms = (TextView) findViewById(R.id.tv_terms);
        String terms = "by signing up you agree to our ToS and Privacy Policy";
        tv_terms.setText(terms);

        // 지정한 단어들에 링크를 걸어주는 함수
        Linkify.TransformFilter transform = new Linkify.TransformFilter() {
            @Override
            public String transformUrl(Matcher matcher, String s) {
                return "";
            }
        };

        Pattern pattern1 = Pattern.compile("ToS");
        Pattern pattern2 = Pattern.compile("Privacy Policy");
        Linkify.addLinks(tv_terms, pattern1, "http://blog.naver.com/manadra", null, transform);
        Linkify.addLinks(tv_terms, pattern2, "http://blog.naver.com/manadra", null, transform);

    }

    // =========================================================================================================
    // 버튼 리스너
    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            // 폰으로 로그인
            case R.id.btn_login_phone:
                Intent intent = new Intent(getApplicationContext(), Login_Phone_Activity.class);
                startActivity(intent);
                finish();
                break;

            // 구글로 로그인
            case R.id.btn_login_google:


                USER_ID = 59;
                SharedPreferences pref = getSharedPreferences(AUTO_LOGIN_STATUS, MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString(AUTO_LOGIN_KEY, "success"); // 자동 로그인 상태 저장
                editor.putInt(AUTO_LOGIN_USERID, USER_ID); // 유저 고유 번호ID 저장
                editor.commit();

                getMyInfo();

                // 임시로 서비스 시작 가능하게끔
                // 네티와의 채팅 연결을 위한 Service 시작
                Intent intent2 = new Intent(Login_Activity.this, Chat_Service.class);
                Log.d(TAG, "채팅을 위한 (netty Channel connection)서비스 시작");
                startService(intent2);


                Intent intent3 = new Intent(getApplicationContext(), Home_Activity.class);
                startActivity(intent3);
                finish();
                break;

            // 네이버 로그인
            case R.id.btn_login_naver:
                Intent intent4 = new Intent(getApplicationContext(), Home_Activity.class);
                startActivity(intent4);
                finish();
                break;
        }
    }
    // =========================================================================================================

}
