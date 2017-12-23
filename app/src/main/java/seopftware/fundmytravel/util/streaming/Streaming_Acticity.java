package seopftware.fundmytravel.util.streaming;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Toast;

import com.pedro.rtplibrary.rtmp.RtmpCamera1;

import net.ossrs.rtmp.ConnectCheckerRtmp;

import seopftware.fundmytravel.R;

/**
 * 영상 송출하는 곳
 *
 * @author 김인섭
 * @version 1.0.0
 * @class comment
 * 이 클래스는 영상을 송출하는 액티비티 입니다.
 * @since 2017-12-22 오후 3:11
 **/
public class Streaming_Acticity extends AppCompatActivity implements ConnectCheckerRtmp {

    private RtmpCamera1 rtmpCamera1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_streaming);

        // 선언
        SurfaceView view_surface = findViewById(R.id.view_surface);
        rtmpCamera1 = new RtmpCamera1(view_surface, this);

    }

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
                rtmpCamera1.stopPreview();
            }
        });
    }

    // RTMP 서버와 연결 끊기
    @Override
    public void onDisconnectRtmp() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(Streaming_Acticity.this, "Disconnected", Toast.LENGTH_LONG).show();
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


    // 핸들러 실행
    // 3초가 지나면 다음 화면으로 넘어감
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {

            if (!rtmpCamera1.isStreaming()) { // 만약 스트리밍 중이 아니면

                if (rtmpCamera1.prepareAudio() && rtmpCamera1.prepareVideo()) { // 오디오 & 비디오 null 값 아니면
                    rtmpCamera1.startStream("rtmp://52.79.138.20:1935/dash/test"); // 스트리밍 시작. (스트리밍 주소)
                } else {
                    Toast.makeText(getApplicationContext(), "Error preparing stream, This device cant do it", Toast.LENGTH_SHORT).show();
                }
            } else {
                rtmpCamera1.stopStream();
                rtmpCamera1.stopPreview();
            }

        }

    };

    // 바로 스트리밍이 시작되면 검은 화면이 뜬다. 그래서 3초 딜레이 주기 (페북처럼 3 2 1 효과줘도 될 듯!)
    protected void onResume() {
        super.onResume();
        handler.postDelayed(runnable, 3000); // 핸들러 3초 딜레이
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (rtmpCamera1.isStreaming()) {
            rtmpCamera1.stopStream();
            rtmpCamera1.stopPreview();
        }
    }


}
