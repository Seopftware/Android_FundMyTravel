package seopftware.fundmytravel.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import seopftware.fundmytravel.R;
import seopftware.fundmytravel.util.retrofit.HttpService;
import seopftware.fundmytravel.util.retrofit.RetrofitClient;

import static seopftware.fundmytravel.util.MyApp.AUTO_LOGIN_KEY;
import static seopftware.fundmytravel.util.MyApp.AUTO_LOGIN_STATUS;
import static seopftware.fundmytravel.util.MyApp.AUTO_LOGIN_USERID;
import static seopftware.fundmytravel.util.MyApp.USER_ID;

public class Login_Phone2_Activity extends AppCompatActivity {

    private static final String TAG = "all_"+Login_Phone2_Activity.class;

    // 화면 UI
    int verify_number; // 인증 번호
    String verify_phone; // 휴대폰 번호
    EditText et_num_input, et_num_input2,et_num_input3, et_num_input4;
    Button btn_verify_confirm;
    TextView tv_left_time; // 남은 인증시간
    TextView tv_resend_code; // 인증시간 만료 후 다시 보내기

    Retrofit retrofit;
    HttpService httpService;

    // 카운트 다운을 위한 변수들
    private CountDownTimer countDownTimer;
    private static final int MILLISINFUTURE = 16 * 1000; // 총 시간
    private static final int COUNT_DOWN_INTERVAL = 1000; // onTick()에 대한 시간
    int count = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_phone2);

        //액션바 설정 부분
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + "Verify your phone number" + "</font>"));

        // Login_Phone.Activity로 부터 인증번호 및 폰 번호 받아오기
        Intent intent=getIntent();
        verify_number=intent.getIntExtra("verify_number", 1111); // 인증번호 4자리
        verify_phone=intent.getStringExtra("verify_phone"); // 인증하고자 하는 폰번호

        // 화면 UI 선언
        et_num_input= (EditText) findViewById(R.id.et_num_input);
        et_num_input2= (EditText) findViewById(R.id.et_num_input2);
        et_num_input3= (EditText) findViewById(R.id.et_num_input3);
        et_num_input4= (EditText) findViewById(R.id.et_num_input4);
        btn_verify_confirm= (Button) findViewById(R.id.btn_verify_confirm);
        tv_left_time= (TextView) findViewById(R.id.tv_left_time);
        tv_resend_code= (TextView) findViewById(R.id.tv_resend_code);

        // 제한된 시간안에 인증을 유도하기 위한 함수 작동(15초내 인증못하면 이전 화면으로 돌아가야함)
        countDownTimer();
        countDownTimer.start(); // 카운트 다운 시작

        // 인증번호 다시 보내기 tv 클릭
        tv_resend_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // 인증번호 입력 후 확인 받는 버튼
        btn_verify_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user_input
                        = et_num_input.getText().toString() +
                          et_num_input2.getText().toString() +
                          et_num_input3.getText().toString() +
                          et_num_input4.getText().toString();

                Log.d(TAG, "user_input :" + user_input);

                // 숫자를 입력하는 곳에 빈칸이 있을 경우 입력 유도하는 부분
                if (et_num_input.getText().toString().length() == 0) {
                    Toast.makeText(getApplicationContext(), "인증번호 4자리를 입력해 주세요", Toast.LENGTH_SHORT).show();
                    et_num_input.requestFocus();
                    return;
                } else if(et_num_input2.getText().toString().length() ==0) {
                    Toast.makeText(getApplicationContext(), "인증번호 4자리를 입력해 주세요", Toast.LENGTH_SHORT).show();
                    et_num_input2.requestFocus();
                    return;
                } else if(et_num_input3.getText().toString().length() ==0) {
                    Toast.makeText(getApplicationContext(), "인증번호 4자리를 입력해 주세요", Toast.LENGTH_SHORT).show();
                    et_num_input3.requestFocus();
                    return;
                } else if(et_num_input4.getText().toString().length() ==0) {
                    Toast.makeText(getApplicationContext(), "인증번호 4자리를 입력해 주세요", Toast.LENGTH_SHORT).show();
                    et_num_input4.requestFocus();
                    return;
                }

                // 시스템이 생성한 인증번호와 사용자가 입력한 인증번호가 일치했을 경우
                if(verify_number == Integer.parseInt(user_input)) {

                    // Http 통신하는 부분
                    retrofit = RetrofitClient.getClient();
                    httpService = retrofit.create(HttpService.class);
                    Call<ResponseBody> comment = httpService.register_phone("phone", verify_phone); // 1.회원가입 방법(폰번호) 2.휴대폰 번호
                    comment.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                            if(!response.isSuccessful()) {
                                Log.d(TAG, "유저 정보 등록 실패");
                                return;
                            }

                            try {

                                // 자동 로그인을 위해 SharedPreferences에 회원 정보 저장
                                USER_ID=response.body().string(); // 로그인 세션 유지를 위해 user의 고유 ID 값 변수에 담는다.
                                SharedPreferences pref = getSharedPreferences(AUTO_LOGIN_STATUS, MODE_PRIVATE);
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putString(AUTO_LOGIN_KEY, "success"); // 자동 로그인 상태 저장
                                editor.putString(AUTO_LOGIN_USERID, USER_ID); // 유저 고유 번호ID 저장
                                editor.commit();

                            } catch (IOException e) {
                                e.printStackTrace();
                            }


                            Toast.makeText(getApplicationContext(), "Register is sucess", Toast.LENGTH_LONG).show();
                            countDownTimer.cancel(); // 카운트 다운 쓰레드 멈추기

                            // SMS 인증에 성공하면 Home 화면으로 넘어가기
                            Intent intent=new Intent(getApplicationContext(), Home_Activity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 최상위 액티비티(Home_Activity) 남기고 다 없애기 (회원가입 화면이 뜨지 않게끔 하기 위해)

                            startActivity(intent);
                            finish();


                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Log.d(TAG, "네트워크 통신 실패");
                        }
                    }); // HTTP 통신 종료

                // 시스템이 생성한 인증번호와 사용자가 입력한 인증번호가 일치하지 않을 경우
                } else {
                    Toast.makeText(getApplicationContext(), "Code is not correspond", Toast.LENGTH_LONG).show();
                }
            }
        });

    }



    // =========================================================================================================
    // 제한된 시간 내에 인증을 유도하기 위한 함수 (카운트 다운 하는 곳)
    // =========================================================================================================
    public void countDownTimer() {
        countDownTimer = new CountDownTimer(MILLISINFUTURE, COUNT_DOWN_INTERVAL) {
            @Override
            public void onTick(long l) {

                Log.d(TAG, "l 값은? " + l);
                Log.d(TAG, "count 값은? " + count);

                count --;
                if(count>=10) {
                    tv_left_time.setText(Integer.toString(count));
                } else {
                    tv_left_time.setText("0"+Integer.toString(count));
                }

            }

            @Override
            public void onFinish() {
                // 만약 15초 동안 입력에 실패하면 버튼 클릭 막고 Resend code text view 보이게하기
                tv_left_time.setText("00");
                tv_resend_code.setVisibility(View.VISIBLE);
                btn_verify_confirm.setEnabled(false);
                btn_verify_confirm.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.colorGrey));
            }
        };
    }
    // =========================================================================================================
}




/**-----------------------------------------------------------------------------------------------------------------
 The boss group will accept incoming connections as they arrive and pass them on processing to the worker group
 -----------------------------------------------------------------------------------------------------------------*/