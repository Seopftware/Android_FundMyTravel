package seopftware.fundmytravel.util.streaming;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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
 * ExoPlayer_스트리밍 재생
 * @author 김인섭
 * @version 1.0.0
 * @since 2017-12-23 오전 10:47
 * @class comment
 * 이 클래스는  스트리밍 영상을 재생하기 위한 용도로 만들어졌습니다.
 * Exoplayer Library 사용
 * 참고한 예제 주소: https://github.com/yusufcakmak/ExoPlayerSample (깃헙)
**/

public class PlayerStreaming_Activity extends Activity implements View.OnClickListener{

    private static final String TAG = "all_" + PlayerStreaming_Activity.class;

    // ExoPlayer 변수
    private SimpleExoPlayerView simpleExoPlayerView;
    private SimpleExoPlayer player;
    private Timeline.Window window;
    private DataSource.Factory mediaDataSourceFactory;
    private DefaultTrackSelector trackSelector;
    private boolean shouldAutoPlay;
    private BandwidthMeter bandwidthMeter;
    private Handler mainHandler;
    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    protected String userAgent;

    // 화면 UI
    // private ImageView ivHideControllerButton; // 하단 플레이 보이게 안보이게 하는 역할
    private LinearLayout linear_bottom; // 하단의 Linear layout
    private LinearLayout linear_viewer; // Total View 보기위한 Linear
    private LinearLayout linear_message; // 메세지 입력창 보여주기 위한 Linear
    private Button btn_show_message; // 메세지 입력 창 띄우기
    private ImageButton ibtn_show_star; // 별풍선 보내는 화면 띄우기
    private ImageButton ibtn_show_settings; // 설정 화면 띄우기 (채팅창 보이기. Streamer 정보 등)
    private ImageButton ibtn_viewer_exit; // 스트리밍 시청 그만하기 (나가기)
    private TextView tv_viewer_num; // 현재 방송 시청자 수
    private EditText et_input_message; // 보낼 메세지를 입력하는 곳
    private ImageButton ibtn_chat_send; // 메세지를 보내는 버튼

    // 키보드 작업
    InputMethodManager imm; // 키보드 강제로 올리고 내리기 위한 변수

    // 채팅 관련 Recycler 변수들
    RecyclerView recyclerView; // Recycler View 변수
    Streaming_Recycler_Adapter adapter; // Recycler Adapter 변수
    Streaming_Item recycler_item; // Reclycler view에 데이터 추가할 Item
    ArrayList<Streaming_Item> recycler_itemlist
            = new ArrayList<Streaming_Item>(); // Item들을 담을 Array list

    // 브로드 캐스트 리시버 동적 생성(매니페스트 intent filter 추가 안하고)
    BroadcastReceiver broadcast_receiver; // 서비스로부터 메세지를 받기 위해 브로드 캐스트 리시버 동적 생성
    IntentFilter intentfilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playerstreaming);

        userAgent = Util.getUserAgent(this, "ExoPlayerDemo");
        shouldAutoPlay = true;
        bandwidthMeter = new DefaultBandwidthMeter();
        mediaDataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "mediaPlayerSample"), (TransferListener<? super DataSource>) bandwidthMeter);
        window = new Timeline.Window();
        mainHandler = new Handler();
        // ivHideControllerButton = (ImageView) findViewById(R.id.exo_controller); // 컨트롤러 안보이게 하기

        // Recycler View UI 적용
        imm= (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); // 키보드를 띄우기 위한 변수
        recyclerView= (RecyclerView) findViewById(R.id.viewer_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.bringToFront();
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recycler_item = new Streaming_Item();
        adapter= new Streaming_Recycler_Adapter();
        recyclerView.setAdapter(adapter);
        recyclerView.bringToFront();

        // 브로드 캐스트 관련
        intentfilter = new IntentFilter(); // 인텐트 필터 생성
        intentfilter.addAction(BROADCAST_NETTY_MESSAGE); // 인텐트 필터에 액션 추가
        register_receiver(); // 리시버 등록하는 함수 작동

        // UI
        linear_bottom= (LinearLayout) findViewById(R.id.linear_bottom); // 하단의 옵션들 모아놓은 Linear
        linear_viewer= (LinearLayout) findViewById(R.id.linear_viewer); // 현재 방송 시청자 수를 곳(클릭 시 총 시청자 다 보여줌)
        linear_message= (LinearLayout) findViewById(R.id.linear_message); // 메세지 입력창 보여주기 위한 Linear

        btn_show_message= (Button) findViewById(R.id.btn_show_message); // 텍스트 입력창 표시
        btn_show_message.setOnClickListener(this);

        ibtn_show_star= (ImageButton) findViewById(R.id.ibtn_show_star); // 별풍선 보내기 화면 표시
        ibtn_show_star.setOnClickListener(this);

        ibtn_show_settings= (ImageButton) findViewById(R.id.ibtn_show_settings); // 설정화면 표시
        ibtn_show_settings.setOnClickListener(this);

        ibtn_viewer_exit= (ImageButton) findViewById(R.id.ibtn_viewer_exit);
        ibtn_viewer_exit.setOnClickListener(this);

        tv_viewer_num= (TextView) findViewById(R.id.tv_viewer_num); // 현재 방송 시청자 수
        ibtn_chat_send= (ImageButton) findViewById(R.id.ibtn_chat_send); // 채팅 메세지 보내기
        ibtn_chat_send.setOnClickListener(this);

        // 채팅 메세지 입력
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

    }

    // =========================================================================================================
    // 동적 리시버를 사용하는 이유.
    // 정적 리시버는 한번등록하면, 쉽게 해체하기 어렵기 때문에 계속적으로 유지가 되는 경향이 있다.
    // 하지만 동적 리시버는 등록과 해체가 유연하기 때문에 등록/해체가 빈번하게 일어나는 경우엔 유리하다.

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



    // =========================================================================================================
    // 클릭 Listener 모음
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // 채팅을 입력하고 보내기 위한 창 띄우기
            // linear_message 보이게 하기
            case R.id.btn_show_message:

                Log.d(TAG, "btn_show_message 클릭");
                linear_message.setVisibility(View.VISIBLE); // 채팅 입력창 보이게하고
                linear_bottom.setVisibility(View.INVISIBLE); // 방송 시청 옵션창 없애기
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
                ibtn_chat_send.setImageResource(R.drawable.streaming_send_before);


                // 1.나의 RecylcerView에 Item 추가
                // 2.서버에 내가 작성한 메세지 보냄 (서버에서는 나에게 받은 메세지를 DB에 저장)
                sendMessage_toServer();

                et_input_message.setText(""); // 텍스트 메세지 초기화



                break;

            case R.id.ibtn_viewer_exit: // 방송 시청 종료
                Log.d(TAG, "btn_finish 클릭");

                Intent intent = new Intent(getApplicationContext(), Home_Activity.class);
                startActivity(intent);
                finish();

                break;

        }
    }
    // =========================================================================================================


/*    // =========================================================================================================
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
    // =========================================================================================================*/


    // =========================================================================================================
    // ExoPlayer 준비를 위한 함수
    private void initializePlayer() {

        // play를 위한 view 생성
        simpleExoPlayerView = (SimpleExoPlayerView) findViewById(R.id.player_view);
        simpleExoPlayerView.requestFocus();

        // 영상 재생 준비
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
        simpleExoPlayerView.setPlayer(player);
        player.setPlayWhenReady(shouldAutoPlay);


        // 해당 url에 접근 후 DASH 재생
        Uri uri = Uri.parse("http://52.79.138.20/dashlive/test.mpd");
        MediaSource mediaSource = new DashMediaSource(uri, buildDataSourceFactory(false),
                new DefaultDashChunkSource.Factory(mediaDataSourceFactory), mainHandler, null);
        player.prepare(mediaSource);

/*        // imageview click 이벤트
        // 재생화면 클릭 시 재생 시작/중지 컨트롤 view가 보였다가 안보였다가 함
        ivHideControllerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                simpleExoPlayerView.hideController();
            }
        });*/
    }

    // 화면에서 빠져나갈시 재생 중지를 위해 필요한 부분
    private void releasePlayer() {
        if (player != null) {
            shouldAutoPlay = player.getPlayWhenReady();
            player.release();
            player = null;
            trackSelector = null;
        }
    }
    // =========================================================================================================


    // =========================================================================================================
    // Dash 재생을 위한 Dash datasource Factory
    // Returns a new DataSource factory.
    // @param useBandwidthMeter Whether to set {@link #BANDWIDTH_METER} as a listener to the new
    // DataSource factory.
    // @return A new DataSource factory.
    private DataSource.Factory buildDataSourceFactory(boolean useBandwidthMeter) {
        return buildDataSourceFactory(useBandwidthMeter ? BANDWIDTH_METER : null);
    }

    public DataSource.Factory buildDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultDataSourceFactory(this, bandwidthMeter,
                buildHttpDataSourceFactory(bandwidthMeter));
    }

    public HttpDataSource.Factory buildHttpDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultHttpDataSourceFactory(userAgent, bandwidthMeter);
    }
    // =========================================================================================================


    // =========================================================================================================
    // 생명주기 함수
    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            initializePlayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if ((Util.SDK_INT <= 23 || player == null)) {
            initializePlayer();
        }

//        register_receiver();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
        unregister_receiver();

    }
    // =========================================================================================================

}