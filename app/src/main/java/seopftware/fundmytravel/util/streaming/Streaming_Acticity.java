package seopftware.fundmytravel.util.streaming;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.pedro.encoder.input.video.CameraOpenException;
import com.pedro.rtplibrary.rtmp.RtmpCamera1;

import net.ossrs.rtmp.ConnectCheckerRtmp;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import seopftware.fundmytravel.R;
import seopftware.fundmytravel.activity.Home_Activity;
import seopftware.fundmytravel.adapter.Streaming_Recycler_Adapter;
import seopftware.fundmytravel.dataset.Streaming_Item;

import static seopftware.fundmytravel.util.MyApp.BROADCAST_NETTY_MESSAGE;
import static seopftware.fundmytravel.util.MyApp.TimeCheck;
import static seopftware.fundmytravel.util.MyApp.USER_ID;
import static seopftware.fundmytravel.util.MyApp.USER_NAME;
import static seopftware.fundmytravel.util.MyApp.USER_PHOTO;
import static seopftware.fundmytravel.util.chatting.Chat_Service.channel;

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

    private static final String TAG ="all_"+"Streaming_Activity";

    // 스트리밍 관련 UI 변수들
    private RtmpCamera1 rtmpCamera1;
    private ImageButton ibtn_switch_camera; // 카메라 전면/후면 변환 버튼
    private ImageButton ibtn_camera_onoff; // 카메라 전면/후면 변환 버튼
    private ImageButton ibtn_mic_onoff; // 카메라 전면/후면 변환 버튼
    private ImageButton ibtn_friend_invite; // 스트리밍 하단의 친구 초대 / 채팅 보이기/숨기기 버튼
    private Button btn_finish; // 스트리밍 종료
    private ImageView iv_number1, iv_number2, iv_number3; // 3, 2, 1 이미지 파일
    private LinearLayout linear_top, linear_bottom; // 상단 Linear, 하단 Linear
    private Button btn_live; // 생방송 표시 버튼

    // 채팅 관련 Recycler 변수들
    RecyclerView recyclerView; // Recycler View 변수
    Streaming_Recycler_Adapter adapter; // Recycler Adapter 변수
    Streaming_Item recycler_item; // Reclycler view에 데이터 추가할 Item
    ArrayList<Streaming_Item> recycler_itemlist
            = new ArrayList<Streaming_Item>(); // Item들을 담을 Array list

    // 채팅 관련 UI 변수들
    private ImageButton ibtn_chat_view; // 채팅 메세지 창 버튼
    private LinearLayout linear_message; // 채팅을 입력하는 Linear
    private EditText et_input_message; // 채팅 입력창
    private ImageButton ibtn_chat_send; // 보내기 버튼
    InputMethodManager imm; // 키보드 강제로 올리고 내리기 위한 변수

    // 방송 준비를 위한 카운트 다운을 위한 변수들
    private CountDownTimer countDownTimer; // 방송 시작 전 카운트 다운을 위한 함수
    private static final int MILLISINFUTURE = 4 * 1000; // 총 시간
    private static final int COUNT_DOWN_INTERVAL = 1000; // onTick()에 대한 시간
    int count = 3;

    // 방송 파일 저장 경로
    private File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/streamingsave");
    private String currentDateAndTime = ""; // 현재 시간을 영상 파일 제목으로 저장하기 위해 필요한 변수

    // 브로드 캐스트 리시버 동적 생성(매니페스트 intent filter 추가 안하고)
    BroadcastReceiver broadcast_receiver; // 서비스로부터 메세지를 받기 위해 브로드 캐스트 리시버 동적 생성
    IntentFilter intentfilter;

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
        linear_bottom.setClickable(false);


        // 바로 스트리밍이 시작되면 검은 화면이 뜬다. 그래서 3초 딜레이 줘야한다. (페북처럼 3 2 1 효과로 시간 벌기)
        handler.postDelayed(runnable, 2500); // 방송 스트리밍 준비 -> 시작

        countDownTimer();
        countDownTimer.start(); // 카운트 다운 시작

        // 채팅 기능을 위한 Recycler View
        imm= (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); // 키보드를 띄우기 위한 변수
        recyclerView= (RecyclerView) findViewById(R.id.streaming_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recycler_item = new Streaming_Item();
        adapter = new Streaming_Recycler_Adapter();
        recyclerView.setAdapter(adapter);

//        adapter.addEntrance("김인섭님이 입장했습니다");
//        for(int i=0; i<10; i++) {
//            adapter.addMessage("InseopKim", i+"번째", "1.jpg");
//        }

        // 채팅 기능을 위한 UI
        linear_message= (LinearLayout) findViewById(R.id.linear_message);
        et_input_message= (EditText) findViewById(R.id.et_input_message);
        et_input_message.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(et_input_message.isFocused()) {
                    // 한 글자 이상이라도 문자가 입력되어 있으면 메세지 보낼 수 있음.
                    if(s.length()>0) {
                        ibtn_chat_send.setImageResource(R.drawable.streaming_send_done);
                        ibtn_chat_send.setClickable(true);
                    }

                    // 다른 경우에는 메세지 보내기 버튼 막음
                    else {
                        ibtn_chat_send.setImageResource(R.drawable.streaming_send_before);
                        ibtn_chat_send.setClickable(false);
                    }
                }

            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // 텍스트 길이가 변경되었을 경우 발생할 이벤트 작성
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // 텍스트 변경 후 발생할 이벤트 작성

            }
        });
        ibtn_chat_send= (ImageButton) findViewById(R.id.ibtn_chat_send);
        ibtn_chat_send.setOnClickListener(this);


        // 브로드 캐스트 관련
        intentfilter = new IntentFilter(); // 인텐트 필터 생성
        intentfilter.addAction(BROADCAST_NETTY_MESSAGE); // 인텐트 필터에 액션 추가
        register_receiver(); // 리시버 등록하는 함수 작동

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

            // 채팅 보내기 버튼 클릭 시 채팅을 입력할 수 있는 창이 생성됨
            // linear_message 보이게 하기
            case R.id.ibtn_chat_view:

                Log.d(TAG, "ibtn_chat_view 클릭");
                linear_message.setVisibility(View.VISIBLE); // 채팅 입력창 보이게하고
                linear_bottom.setVisibility(View.INVISIBLE); // 방송 옵션창 없애기
                et_input_message.requestFocus();

                // 키보드 띄우기
                imm.showSoftInput(et_input_message, 0);
                break;

            // 채팅 메세지를 보내는 곳
            case R.id.ibtn_chat_send:

                Log.d(TAG, "ibtn_chat_send 클릭");

                // 입력 완료 후 키보드 강제 내리기
                imm.hideSoftInputFromWindow(et_input_message.getWindowToken(), 0);

                linear_message.setVisibility(View.INVISIBLE); // 채팅 입력창 없애기
                linear_bottom.setVisibility(View.VISIBLE); // 방송 옵션창 보이게 하고

                // 1.나의 RecylcerView에 Item 추가
                // 2.서버에 내가 작성한 메세지 보냄 (서버에서는 나에게 받은 메세지를 DB에 저장)
                sendMessage_toServer();

                et_input_message.setText(""); // 텍스트 메세지 초기화
                break;

            case R.id.btn_finish: // 방송 종료
                Log.d(TAG, "btn_finish 클릭");

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

    @Override
    protected void onStop() {
        super.onStop();
        unregister_receiver();
    }



    // =========================================================================================================
    // 채팅
    // =========================================================================================================

    // 브로드 캐스트 메세지 받아오는 곳
    // 여기서 recycler view에 메세지 추가해주는 작업 진행
    private void register_receiver() {
        broadcast_receiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String getMessage = intent.getStringExtra("MessageFromService");

                Log.d(TAG, "getMessage (서버에서 받은 메세지 (from Service) : " + getMessage);

                Log.d(TAG, "****************************************************************");
                Log.d(TAG, "BroadcastReceiver() : (받기) 2.서비스에서 받은 메세지를 리스트뷰에 추가하는 곳");
                Log.d(TAG, "****************************************************************");

                // JSON 객체를 분해하는 곳
                //msg : [you]
                // {"Sender_Id":"3",
                // "Sender_Name":"인섭",
                // "Sender_Message":"ggh",
                // "Sender_Profile":"1.jpg",
                // "Sender_Time":"2018년 01월 04일 목요일_163725"}

                try {
                    JSONObject jsonObject = new JSONObject(getMessage);

                    String Sender_Id = jsonObject.getString("Sender_Id");
                    String Sender_Name = jsonObject.getString("Sender_Name");
                    String Sender_Message = jsonObject.getString("Sender_Message");
                    String Sender_Profile = jsonObject.getString("Sender_Profile");


                    if(Sender_Id.equals(USER_ID)) {

                        Log.d(TAG, "보낸자와 받는자가 같으므로 Recycler View를 추가하지 않는다.");

                    } else {
                        adapter.addMessage(Sender_Name, Sender_Message, Sender_Profile); // Name, Message, Profile(파일명)
                        adapter.notifyDataSetChanged();
                        Log.d(TAG, "ListView 추가");
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        };

        registerReceiver(broadcast_receiver, intentfilter);
        Log.d(TAG, "broadcast receiver를 시작합니다.");

    }

    // 메세지를 보내는 곳
    private void sendMessage_toServer() {
        String message = et_input_message.getText().toString();

        // 1.나의 RecylcerView에 Item 추가
        adapter.addMessage(USER_NAME,message, USER_PHOTO); // Name, Message, Profile

        // 2.서버에 내가 작성한 메세지 JSON 형태로 보냄 (서버에서는 나에게 받은 메세지를 DB에 저장)
        try {

            String time = TimeCheck();
            Log.d(TAG, "메세지 보내는 시간 체크 : "+ time);

            JSONObject object = new JSONObject();
            object.put("Sender_Id", USER_ID);
            object.put("Sender_Name", USER_NAME);
            object.put("Sender_Message", message);
            object.put("Sender_Profile", USER_PHOTO);
            object.put("Sender_Time", time);
            String Object_Data = object.toString();

            // 서버에 메세지를 보냄
            channel.writeAndFlush(Object_Data);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // 동적으로(코드상으로) 브로드 캐스트 종료
    private void unregister_receiver() {
        if(broadcast_receiver !=null) {
            this.unregisterReceiver(broadcast_receiver);
            broadcast_receiver=null;
            Log.d(TAG, "broadcast receiver를 종료합니다.");
        }
    }
    // =========================================================================================================

}


/**



 // =========================================================================================================
 // Netty Engine
 // =========================================================================================================

 // 서버와 연결하기 위한 클래스
 class ChatClient extends Thread {

 private final String host;
 private final int port;

 public ChatClient(String host, int port) {
 this.host=host;
 this.port=port;
 }

 public void run() {
 EventLoopGroup group = new NioEventLoopGroup();

 try {

 Log.d(TAG, "**************************************************");
 Log.d(TAG, "1. 서버와 채널(소켓) 연결");
 Log.d(TAG, "**************************************************");
 // to set up a channel
 bootstrap = new Bootstrap()
 .group(group)
 .channel(NioSocketChannel.class)
 .handler(new ChatClientInitializer());

 // 서버에 최초 접속
 channel = bootstrap.connect(host, port).sync().channel();
 channel.writeAndFlush("서버와 연결되었습니다."); // 서버로 보내는 메세지(입장 메세지로?)
 // 소켓이 연결되고 안되고로 채팅방 입장/퇴장 구분하기


 } catch (InterruptedException e) {
 e.printStackTrace();
 }
 }
 }

 // 메세지를 thread 단에서 서버로 보내는 작업
 public class ChatClientInitializer extends ChannelInitializer<SocketChannel> {

 static final int MESSAGE_SIZE = 8192;

 @Override
 protected void initChannel(SocketChannel ch) throws Exception {
 // Here's define what Netty calls a pipeline
 // It basically describes how we want to organize our communication

 ChannelPipeline pipeline = ch.pipeline();

 // First, tell netty we're expecting frames of at most 8192 in size, each delimited with line endings
 pipeline.addLast("framer", new DelimiterBasedFrameDecoder(MESSAGE_SIZE, Delimiters.lineDelimiter()));

 // Since we're just exchanging Strings between the server and clients, we can use the StringDecoder to decode received bytes into Strings.
 pipeline.addLast("decoder", new StringDecoder());

 // StringEncoder to encode Strings into bytes, which we can then send over to the server
 pipeline.addLast("encoder", new StringEncoder());

 // finally, define a class which will handle all the decoded incoming Strings from the server
 pipeline.addLast("handler", new ChatClientHandler());

 }
 }

 // this class to handle incoming String objects
 // 메세지 받는 곳
 public class ChatClientHandler extends SimpleChannelInboundHandler<String> {
 @Override
 protected void channelRead0(ChannelHandlerContext ctx, final String msg) throws Exception {
 // To print any String message we receive from the server to the console
 // 서버로 부터 받은 메세지

 Log.d(TAG, "****************************************************************");
 Log.d(TAG, "받은 메세지");
 Log.d(TAG, "ctx : " + ctx);
 Log.d(TAG, "msg : " + msg);
 Log.d(TAG, "****************************************************************");

 //msg : [you]{"Sender_Id":"3","Sender_Name":"인섭","Sender_Message":"ggh","Sender_Profile":"1.jpg","Sender_Time":"2018년 01월 04일 목요일_163725"}
 //msg : [/192.168.1.64:55555] {"Sender_Id":"3","Sender_Name":"인섭","Sender_Message":"ggh","Sender_Profile":"1.jpg","Sender_Time":"2018년 01월 04일 목요일_163725"}

 // Thread를 생성한다.
 new Thread(new Runnable() {
 @Override
 public void run() {
 // runOnUiThread를 추가하고 그 안에 UI작업을 한다.
 runOnUiThread(new Runnable() {
 @Override
 public void run() {
 //                            adapter.addMessage();
 }
 });
 }
 }).start();

 }

 // 채팅 중 에러 발생시
 @Override
 public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
 cause.printStackTrace();
 ctx.close();
 }

 } // Finish: ChatClientHandler
 // =========================================================================================================


 */