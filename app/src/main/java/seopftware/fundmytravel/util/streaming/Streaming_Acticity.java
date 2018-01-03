package seopftware.fundmytravel.util.streaming;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.pedro.encoder.input.video.CameraOpenException;
import com.pedro.rtplibrary.rtmp.RtmpCamera1;

import net.ossrs.rtmp.ConnectCheckerRtmp;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import seopftware.fundmytravel.R;
import seopftware.fundmytravel.activity.Home_Activity;

/**
 * 영상 송출하는 곳
 *
 * @author 김인섭
 * @version 1.0.0
 * @class comment
 * 이 클래스는 영상을 송출하는 액티비티 입니다.
 * @since 2017-12-22 오후 3:11
 **/
public class Streaming_Acticity extends AppCompatActivity implements ConnectCheckerRtmp, View.OnClickListener, SurfaceHolder.Callback {

    private static final String TAG = "Streaming_Activity";

    // UI 변수들
    private RtmpCamera1 rtmpCamera1;
    private ImageButton ibtn_switch_camera; // 카메라 전면/후면 변환 버튼
    private ImageButton ibtn_camera_onoff; // 카메라 전면/후면 변환 버튼
    private ImageButton ibtn_mic_onoff; // 카메라 전면/후면 변환 버튼
    private ImageButton ibtn_friend_invite; // 스트리밍 하단의 친구 초대 / 채팅 보이기/숨기기 버튼
    private ImageButton ibtn_chat_view; // 스트리밍 하단의 친구 초대 / 채팅 보이기/숨기기 버튼
    private Button btn_finish; // 스트리밍 종료
    private ImageView iv_number1, iv_number2, iv_number3; // 3, 2, 1 이미지 파일
    private LinearLayout linear_top, linear_bottom; // 상단 Linear, 하단 Linear
    private Button btn_live; // 생방송 표시 버튼


    // 방송 준비를 위한 카운트 다운을 위한 변수들
    private CountDownTimer countDownTimer;
    private static final int MILLISINFUTURE = 4 * 1000; // 총 시간
    private static final int COUNT_DOWN_INTERVAL = 1000; // onTick()에 대한 시간
    int count = 3;


    // 방송 파일 저장 경로
    private File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/streamingsave");
    private String currentDateAndTime = ""; // 현재 시간을 영상 파일 제목으로 저장하기 위해 필요한 변수


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_streaming);

        // 방송 화면 그려줄 View
        SurfaceView view_surface = findViewById(R.id.view_surface);
        rtmpCamera1 = new RtmpCamera1(view_surface, this); // RTMP 객체 생성

        // 카운트 다운
        iv_number3 = (ImageView) findViewById(R.id.iv_number3);
        iv_number2 = (ImageView) findViewById(R.id.iv_number2);
        iv_number1 = (ImageView) findViewById(R.id.iv_number1);


        // 방송 중 기능을 위한 버튼 선언 (방송 상단)
        // 1.카메라 전환
        ibtn_switch_camera = (ImageButton) findViewById(R.id.ibtn_switch_camera);
        ibtn_switch_camera.setOnClickListener(this);
        // 2.카메라 on/off
        ibtn_camera_onoff = (ImageButton) findViewById(R.id.ibtn_camera_onoff);
        ibtn_camera_onoff.setOnClickListener(this);

        // 3.마이크 on/off
        ibtn_mic_onoff = (ImageButton) findViewById(R.id.ibtn_mic_onoff);
        ibtn_mic_onoff.setOnClickListener(this);


        // 방송 하단의 버튼들
        // 1.친구초대
        ibtn_friend_invite = (ImageButton) findViewById(R.id.ibtn_friend_invite); // 친구초대 버튼
        ibtn_friend_invite.setOnClickListener(this);

        // 2.채팅창 on/off
        ibtn_chat_view = (ImageButton) findViewById(R.id.ibtn_chat_view); // 채팅 보이기 버튼
        ibtn_chat_view.setOnClickListener(this);

        // 3.방송 종료
        btn_finish = (Button) findViewById(R.id.btn_finish);
        btn_finish.setOnClickListener(this);

        // 방송 시작 전/후 보여주기/숨기기
        btn_live = (Button) findViewById(R.id.btn_live); // 생방송 표시 버튼
        linear_top = (LinearLayout) findViewById(R.id.linear_top);
        linear_bottom = (LinearLayout) findViewById(R.id.linear_bottom);

        // 바로 스트리밍이 시작되면 검은 화면이 뜬다. 그래서 3초 딜레이 줘야한다. (페북처럼 3 2 1 효과로 시간 벌기)
        handler.postDelayed(runnable, 2500); // 방송 스트리밍 준비 -> 시작

        countDownTimer();
        countDownTimer.start(); // 카운트 다운 시작

    }

    // =========================================================================================================
    // 3, 2, 1 카운트 다운 하는 곳
    // 방송을 시작하기 전에 카운트 다운을 해준다.
    // =========================================================================================================
    public void countDownTimer() {
        countDownTimer = new CountDownTimer(MILLISINFUTURE, COUNT_DOWN_INTERVAL) {
            @Override
            public void onTick(long l) {

                Log.d(TAG, "l 값은? " + l);
                Log.d(TAG, "count 값은? " + count);

                if (count == 3) {
                    iv_number3.setVisibility(View.VISIBLE);
                    iv_number2.setVisibility(View.INVISIBLE);
                    iv_number1.setVisibility(View.INVISIBLE);

                    count--;

                } else if (count == 2) {
                    iv_number3.setVisibility(View.INVISIBLE);
                    iv_number2.setVisibility(View.VISIBLE);
                    iv_number1.setVisibility(View.INVISIBLE);

                    count--;

                } else if (count == 1) {

                    iv_number3.setVisibility(View.INVISIBLE);
                    iv_number2.setVisibility(View.INVISIBLE);
                    iv_number1.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onFinish() {

                iv_number3.setVisibility(View.INVISIBLE);
                iv_number2.setVisibility(View.INVISIBLE);
                iv_number1.setVisibility(View.INVISIBLE);

                linear_bottom.setVisibility(View.VISIBLE);
                linear_bottom.bringToFront();
                linear_top.setVisibility(View.VISIBLE);
                btn_live.setVisibility(View.VISIBLE);

            }
        };
    }

    // =========================================================================================================
    // 클릭 리스너
    // =========================================================================================================
    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.ibtn_switch_camera: // 카메라 앞뒤 전환 버튼
                try {
                    rtmpCamera1.switchCamera();
                } catch (final CameraOpenException e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.ibtn_camera_onoff: // 카메라 on/off 버튼
                try {

                    if (rtmpCamera1.isVideoEnabled()) {
                        // 카메라 끄기
                        ibtn_camera_onoff.setImageDrawable(getResources().getDrawable(R.drawable.streaming_cameraoff));
                        rtmpCamera1.disableVideo();

                    } else {
                        // 카메라 켜기
                        ibtn_camera_onoff.setImageDrawable(getResources().getDrawable(R.drawable.streaming_cameraon));
                        rtmpCamera1.enableVideo();
                    }

                } catch (final CameraOpenException e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();

                }
                break;

            case R.id.ibtn_mic_onoff: // 마이크 on/off 버튼
                try {
                    if (!rtmpCamera1.isAudioMuted()) {
                        // 마이크 끄기
                        ibtn_mic_onoff.setImageDrawable(getResources().getDrawable(R.drawable.streaming_micoff));
                        rtmpCamera1.disableAudio();
                    } else {
                        // 마이크 켜기
                        ibtn_mic_onoff.setImageDrawable(getResources().getDrawable(R.drawable.streaming_micon));
                        rtmpCamera1.enableAudio();
                    }

                } catch (final CameraOpenException e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();

                }
                break;


            case R.id.btn_finish: // 방송 종료
                //If you see this all time when you start stream,
                //it is because your encoder device dont support the configuration
                //in video encoder maybe color format.
                //If you have more encoder go to VideoEncoder or AudioEncoder class,
                // change encoder and try

                if (rtmpCamera1.isStreaming()) {
                    rtmpCamera1.stopStream();
                    rtmpCamera1.stopPreview();
                }

                Intent intent = new Intent(getApplicationContext(), Home_Activity.class);
                startActivity(intent);
                finish();

                break;


        } // switch 구문 close
    } // onClick 함수 finish
    // =========================================================================================================


    // =========================================================================================================
    // RTMP 연결 상태 확인하는 클래스들
    // =========================================================================================================
    // RTMP 서버와 연결 성공
    @Override
    public void onConnectionSuccessRtmp() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(Streaming_Acticity.this, "Connection Success", Toast.LENGTH_LONG).show();
            }
        });
    }

    // RTMP 서버와 연결 실패
    @Override
    public void onConnectionFailedRtmp(final String reason) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(Streaming_Acticity.this, "Connection Failed", Toast.LENGTH_LONG).show();
                rtmpCamera1.stopStream();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2
                        && rtmpCamera1.isRecording()) {
                    rtmpCamera1.stopRecord();
                    Toast.makeText(getApplicationContext(),
                            "file " + currentDateAndTime + ".mp4 saved in " + folder.getAbsolutePath(),
                            Toast.LENGTH_SHORT).show();
                    currentDateAndTime = "";
                }
            }
        });
    }

    // RTMP 서버와 연결 끊기
    // rtmpCamera1.stopStream() 함수 이후 시작되는 클래스
    @Override
    public void onDisconnectRtmp() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "Disconnected", Toast.LENGTH_SHORT).show();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2
                        && rtmpCamera1.isRecording()) {
                    rtmpCamera1.stopRecord();
                    Toast.makeText(getApplicationContext(), "file " + currentDateAndTime + ".mp4 saved in " + folder.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                    currentDateAndTime = "";
                }
            }
        });
    }

    // RTMP 연결 실패 - 경로 오류
    @Override
    public void onAuthErrorRtmp() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(Streaming_Acticity.this, "Auth Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    // RTMP 연결 성공 - 경로 정상
    @Override
    public void onAuthSuccessRtmp() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(Streaming_Acticity.this, "Auth Success", Toast.LENGTH_LONG).show();

            }
        });
    }
    // =========================================================================================================


    // 핸들러 실행-스트리밍을 바로 시작하게 되면 검은 화면만 계속 나타남. 3초 정도의 화면 준비시간이 필요함. (FB도 마찬가지임)
    // 3초가 지나면 스트리밍 시작! + 방송 저장 시작
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {

            if (!rtmpCamera1.isStreaming()) { // 만약 스트리밍 중이 아니면

                if (rtmpCamera1.prepareAudio() && rtmpCamera1.prepareVideo()) { // 오디오 & 비디오 null 값 아니면
                    rtmpCamera1.startStream("rtmp://52.79.138.20:1935/dash/test"); // 스트리밍 시작. (스트리밍 주소)

                    try {
                        Thread.sleep(3000);
                        startRecord();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                } else {
                    Toast.makeText(getApplicationContext(), "Error preparing stream, This device cant do it", Toast.LENGTH_SHORT).show();
                }
            } else {
                rtmpCamera1.stopStream();
                rtmpCamera1.stopPreview();
            }

        }

    };


    // =========================================================================================================
    // 방송을 녹화하는 함수
    // =========================================================================================================
    public void startRecord() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            if (!rtmpCamera1.isRecording()) {
                try {
                    // 폴더가 없으면 폴더를 생성해라.
                    if (!folder.exists()) {
                        folder.mkdir();

                    }

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                    currentDateAndTime = sdf.format(new Date());
                    rtmpCamera1.startRecord(folder.getAbsolutePath() + "/" + currentDateAndTime + ".mp4");
                    Toast.makeText(this, "Start Recording... ", Toast.LENGTH_SHORT).show();

                } catch (IOException e) {
                    rtmpCamera1.stopRecord();
                    Log.d(TAG, "Stop Recording... ");
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            } else {
                rtmpCamera1.stopRecord();
                Toast.makeText(this, "file " + currentDateAndTime + ".mp4 saved in " + folder.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                currentDateAndTime = "";
            }
        } else {
            Toast.makeText(this, "You need min JELLY_BEAN_MR2(API 18) for do it...", Toast.LENGTH_SHORT).show();
        }
    }


    // =========================================================================================================
    // 방송 화면 그려 주는 함수들
    // =========================================================================================================
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        rtmpCamera1.startPreview();
        // optionally: 카메라 전면부 보는 것 부터 시작
        //rtmpCamera1.startPreview(Camera.CameraInfo.CAMERA_FACING_BACK);
        // or: 카메라 후면부 보는 것 부터 시작
        //rtmpCamera1.startPreview(Camera.CameraInfo.CAMERA_FACING_FRONT);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        rtmpCamera1.stopPreview();
    }

    // =========================================================================================================
    // 생명 주기
    // =========================================================================================================

    @Override
    protected void onPause() {
        super.onPause();

        // 도중에 예상치 못한 상황으로 방송 종료 시 스트리밍 stop 시키기 + 녹화도 중지
        if (rtmpCamera1.isStreaming()) {
            rtmpCamera1.stopStream();
            rtmpCamera1.stopPreview();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 && rtmpCamera1.isRecording()) {
            rtmpCamera1.stopRecord();
            Toast.makeText(this, "file " + currentDateAndTime + ".mp4 saved in " + folder.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            currentDateAndTime = "";
        }

    }


}
