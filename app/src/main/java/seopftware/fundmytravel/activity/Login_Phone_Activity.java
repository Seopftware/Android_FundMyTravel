package seopftware.fundmytravel.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Random;

import seopftware.fundmytravel.R;


/**
 * 폰 번호로 SMS 메세지 보낸 후 회원가입하는 액티비티
 * @author 김인섭
 * @version 1.0.0
 * @since 2017-12-30 오후 3:58
 * @class comment
 *   이 클래스는 폰 번호 인증 후 회원가입을 위한 용도로 만들어졌습니다.
**/

public class Login_Phone_Activity extends AppCompatActivity {

    private static final String TAG ="all_"+Login_Phone_Activity.class;

    // 화면 UI
    EditText et_phone_input; // 내 휴대폰 번호 입력
    Button btn_phone_verify; // 번호 인증 확인 클릭

    String my_phone_number; // 인증하고자 하는 폰 번호
    int verify_number; // 인증 번호(숫자 4자리)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_phone);

        et_phone_input = (EditText) findViewById(R.id.et_phone_input);
        btn_phone_verify = (Button) findViewById(R.id.btn_phone_verify);

        //액션바 설정 부분
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + "Enter your phone number" + "</font>"));


        // 나의 휴대폰 번호 받아오기 위한 함수 호출
        TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            // ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            // public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        my_phone_number = manager.getLine1Number();
        Log.d(TAG, "변경 전 폰번호: " + my_phone_number);
        my_phone_number = my_phone_number.replace("+82", "0"); // +82를 0으로 변경
        Log.d(TAG, "변경 후 폰번호: " + my_phone_number);

        et_phone_input.setText(my_phone_number);

        // 폰 번호 인증하기 클릭 시
        btn_phone_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phonenumDialog();
            }
        });
    }

    // =========================================================================================================
    // 다이얼 로그창 띄우기 - 입력된 폰 번호가 나의 번호가 맞는지? 한번 더 확인하기
    private void phonenumDialog(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Login_Phone_Activity.this);
        alertDialog.setTitle("입력한 번호 확인");
        alertDialog.setMessage("나의 번호가 맞나요?\n"+my_phone_number);
        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        // 숫자 4개 랜덤 생성 => 값이 같으면 인증완료.
                        String verify_sms = null;

                        // 난수 생성
                        Random generator = new Random();
                        verify_number= generator.nextInt(9000)+1000;
                        // netxtInt(9000): 0~8999 +1000 = 1000 ~ 9999의 4자리 랜덤 숫자 발생
                        Log.d(TAG, "난수 발생: " + verify_number);


                        // 내 폰을 통한 문자 보내기
                        verify_sms = "FundMyTravel 본인확인 인증번호\n["+verify_number+"] 입니다";
                        SmsManager sms = SmsManager.getDefault();
                        sms.sendTextMessage(et_phone_input.toString(), null, verify_sms, null, null);

                        // 인증 번호 입력을 위한 화면 전환
                        Intent intent=new Intent(getApplicationContext(), Login_Phone2_Activity.class);
                        intent.putExtra("verify_number", verify_number); // 인증번호
                        intent.putExtra("verify_phone", my_phone_number); // 유저가 입력한 휴대폰 번호
                        startActivity(intent);
                    }
                });
        alertDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }
    // =========================================================================================================

}
