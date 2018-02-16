package seopftware.fundmytravel.function.streaming;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Util;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.schedulers.Timed;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import seopftware.fundmytravel.R;
import seopftware.fundmytravel.adapter.Streaming_Recycler_Adapter;
import seopftware.fundmytravel.dataset.Parsing;
import seopftware.fundmytravel.dataset.Streaming_Item;
import seopftware.fundmytravel.function.retrofit.HttpService;
import seopftware.fundmytravel.function.retrofit.RetrofitClient;
import seopftware.fundmytravel.function.vodEmoticon.Emoticons;
import seopftware.fundmytravel.function.vodEmoticon.EmoticonsView;

/*
1. 재생하고자 하는 영상의 URL 값을 Streaminglist_Fragment에서 받아온다.

2. 접속한 방의 room_id 값을 받아 온다.
    접속한 방의 room_id 값을 서버(DB)에 보낸 다음 채팅 리스트를 모두 가져온다.

3. 가져온 채팅 리스트를 영상 재생 시간에 맞게 뿌려 준다.
    만약, 현재 재생시간과 이동한 재생시간이 1초 이상 차이나면 (progress bar)를 이동한 것으로 간주
    서버로 다시 query문을 날린 다음 그 시간 이후부터의 채팅 목록을 가져 온다.

    만약, 단순히 영상을 계속 시청 중이라면 0.5초의 간격으로 채팅 목록을 불러온다.
    0.5, 1.0, 1.5, 2.0 ... 보다 작은 아이템이 발견되면 addItem 해주는 방식으로.

 */

public class Vodplayer_Activity extends AppCompatActivity {

    private static final String TAG = "all_" + Vodplayer_Activity.class;
//    private static final String VOD_URL = null;
    private static final String VOD_URL = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";


    // 채팅 관련 Recycler 변수들
    RecyclerView recyclerView; // Recycler View 변수
    Streaming_Recycler_Adapter adapter; // Recycler Adapter 변수
    ArrayList<Streaming_Item> recycler_itemlist
            = new ArrayList<Streaming_Item>(); // Item들을 담을 Array list

    // 채팅 리스트를 뿌려주기 위한 변수 및 쓰레드
    // 채팅 데이터를 담아 두기 위한 HashMap
    HashMap<Integer, Streaming_Item> hashMap = new HashMap<Integer, Streaming_Item>();
    SendMessageHandler messageHaldner; // If 재생 시간에 뿌려줄 데이터가 존재한다면 UI 변경을 위해 handler를 이용
    CustomThread customThread; // 현재 재생 시간을 1초마다 알아내기 위한 Thread
    int before_time = 0; // 현재 재생 중인지 아닌지 구분하기 위한 변수

//    // Video View
//    VideoView videoView; // 비디오를 재생하는 view

    // 변수
    String room_id; // 현재 내가 시청하고자 하는 VOD 방 번호(채팅 목록 불러올 때 사용)



    // ExoPlayer
    private SimpleExoPlayerView simpleExoPlayerView;
    private SimpleExoPlayer player;
    private Timeline.Window window;
    private DataSource.Factory mediaDataSourceFactory;
    private DefaultTrackSelector trackSelector;
    private boolean shouldAutoPlay;
    private BandwidthMeter bandwidthMeter;
    private Handler mainHandler;
    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private ImageView ivHideControllerButton;
    protected String userAgent;


    // =========================================================================================================
    // 페이스북 이모티콘 효과를 나타내기 위한 변수들
    private Subscription emoticonSubscription;
    private Subscriber subscriber;
    private final int MINIMUM_DURATION_BETWEEN_EMOTICONS = 300; // in milliseconds

    // Emoticon ImageView
    @BindView(R.id.like_emoticon)
    ImageView likeEmoticonButton;
    @BindView(R.id.love_emoticon)
    ImageView loveEmoticonButton;
    @BindView(R.id.haha_emoticon)
    ImageView hahaEmoticonButton;
    @BindView(R.id.wow_emoticon)
    ImageView wowEmoticonButton;
    @BindView(R.id.sad_emoticon)
    ImageView sadEmoticonButton;
    @BindView(R.id.angry_emoticon)
    ImageView angryEmoticonButton;

    // Emoticon을 표시해 줄 View
    @BindView(R.id.custom_view)
    EmoticonsView emoticonsView;

    private Animation emoticonClickAnimation;
    // =========================================================================================================



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vodplayer);
        ButterKnife.bind(this);


        Intent intent = getIntent();
//        intent.getStringExtra(); // 영상 URL 주소 값 받아오기
        room_id = intent.getStringExtra("room_id"); // room id 값 받아오기

        Log.d(TAG, "VOD 방에 입장 ! room_id 값은 ? : " + room_id);

//        // 영상 재생 UI 부분
//        videoView = (VideoView) findViewById(R.id.videoView);
//        videoView.setVideoPath(VOD_URL);
//        videoView.start(); // 액티비티가 생성이 되면 영상을 재생 시켜 준다.
//
//        final MediaController mediaController = new MediaController(this);
//        videoView.setMediaController(mediaController);

        exoPlayerinit();

        // Recycler View
        recyclerView = (RecyclerView) findViewById(R.id.vod_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter = new Streaming_Recycler_Adapter(recycler_itemlist);
        recyclerView.setAdapter(adapter);

        // 메인 핸들러 설정
        messageHaldner = new SendMessageHandler();

        // 서버에서 채팅 목록을 모두 가져온다.
        // Table: message_normal
        // Send: room_id
        // Receive: chatting list (내가 시청할 VOD의 모든 채팅 내역)
        getChatList();

        // 현재 영상 재생 정보를 가져오는 Thread 시작
        ThreadStart();

    }

    // =========================================================================================================
    // ExoPlayer 준비 영상 재생 준비 시키기
    private void exoPlayerinit() {
        userAgent = Util.getUserAgent(this, "ExoPlayerDemo");
        shouldAutoPlay = true;
        bandwidthMeter = new DefaultBandwidthMeter();
        mediaDataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "mediaPlayerSample"), (TransferListener<? super DataSource>) bandwidthMeter);
        window = new Timeline.Window();
        mainHandler = new Handler();
        ivHideControllerButton = (ImageView) findViewById(R.id.exo_controller); // 컨트롤러 안보이게 하기


//        shouldAutoPlay = true;
//        bandwidthMeter = new DefaultBandwidthMeter();
//        mediaDataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "mediaPlayerSample"), (TransferListener<? super DataSource>) bandwidthMeter);
//        window = new Timeline.Window();
//        ivHideControllerButton = (ImageView) findViewById(R.id.exo_controller);

        initializePlayer();
    }

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
        Uri uri = Uri.parse("http://52.79.138.20/dashlive/" + room_id + ".mpd");
        MediaSource mediaSource = new DashMediaSource(uri, buildDataSourceFactory(false),
                new DefaultDashChunkSource.Factory(mediaDataSourceFactory), mainHandler, null);
        player.prepare(mediaSource);

        player.seekTo(1000); // vod 재생 시작 시 1초부터 재생하기

        // imageview click 이벤트
        // 재생화면 클릭 시 재생 시작/중지 컨트롤 view가 보였다가 안보였다가 함
        ivHideControllerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                simpleExoPlayerView.hideController();
            }
        });


        // exoplayer Listener 모음
        player.addListener(new ExoPlayer.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest) {
                Log.d(TAG, "시간 체크!!" + timeline.toString());
            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

                Log.d(TAG, "onTracksChanged");

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {
                Log.d(TAG, "onLoadingChanged");

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                Log.d(TAG, "onPlayerStateChanged");
                Log.d(TAG, "playbackState : " + String.valueOf(playbackState));

            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {
                Log.d(TAG, "onRepeatModeChanged");

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                Log.d(TAG, "onPlayerError");

            }

            @Override
            public void onPositionDiscontinuity() {
                Log.d(TAG, "onPositionDiscontinuity");
            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }
        });
//        Log.d(TAG, String.valueOf(simpleExoPlayerView.getScrollIndicators()));
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



    private void ThreadStart() {
        // customThread에서 채팅 리스트 불러들이는 작업을 처리해 준다.
        customThread = new CustomThread();
        customThread.start();
    }



    // HTTP 통신하는 부분
    // 해당 방의 번호를 보낸 다음 RDB로 부터 채팅 목록을 받아 온다.
    private void getChatList() {

        Log.d(TAG, "****************************************************************");
        Log.d(TAG, "HTTP 통신 시작");
        Log.d(TAG, "****************************************************************");

        Retrofit retrofit = RetrofitClient.getClient();
        HttpService httpService = retrofit.create(HttpService.class);
        Call<Parsing> comment = httpService.get_chatlist(room_id);
        comment.enqueue(new Callback<Parsing>() {
            @Override
            public void onResponse(Call<Parsing> call, Response<Parsing> response) {
                Parsing parsing = response.body();

                Log.d(TAG, "Parsing : " + String.valueOf(parsing.getChatCount()));

                // 친구목록을 뿌려주는 곳
                for (int i=0; i<parsing.getChatCount(); i++) {

                    Streaming_Item recycler_item = new Streaming_Item();


                    // 서버에서 받아온 메세지 리스트
                    String sender_name = parsing.getChatlist().get(i).getSenderName();
                    String sender_profile = parsing.getChatlist().get(i).getSenderProfile();
                    String sender_message = parsing.getChatlist().get(i).getSenderMessage();
                    String broadcast_time = parsing.getChatlist().get(i).getBroadcastTime();

                    Log.d(TAG, "String: (broadcast_time) : " + broadcast_time);
                    Log.d(TAG, "Int: (broadcast_time) : " + Integer.parseInt(broadcast_time));

                    int int_broadcast_time = Integer.parseInt(broadcast_time);
                    String str_broadcast_time = String.valueOf(int_broadcast_time);


                    // 객체화 시킨 다음 ArrayList에 담아 두었다가 시간에 맞게 꺼내서 adapter에 add 해준다.
                    recycler_item.setStreaming_user_nickname(sender_name);
                    recycler_item.setStreameing_image_profile(sender_profile);
                    recycler_item.setStreaming_user_message(sender_message);
                    recycler_item.setStreaming_message_time(broadcast_time);

                    Log.d(TAG, "HashMap 저장 전 (sender_name) : " + sender_name);
                    Log.d(TAG, "HashMap 저장 전 (sender_profile) : " + sender_profile);
                    Log.d(TAG, "HashMap 저장 전 (sender_message) : " + sender_message);
                    Log.d(TAG, "HashMap 저장 전 (broadcast_time) : " + broadcast_time);

                    Log.d(TAG, "String: (str_broadcast_time) key값으로 사용 : " + str_broadcast_time);

                    hashMap.put(int_broadcast_time, recycler_item);

                }


                Iterator<Integer> keySetIterator = hashMap.keySet().iterator();
                while (keySetIterator.hasNext()) {
                    Integer key = keySetIterator.next();

                    Log.d(TAG, "key: " + key + " value: " + hashMap.get(key).getStreaming_user_nickname());
                    Log.d(TAG, "key: " + key + " value: " + hashMap.get(key).getStreaming_user_message());
                    Log.d(TAG, "key: " + key + " value: " + hashMap.get(key).getStreaming_message_time());

                }


            }

            @Override
            public void onFailure(Call<Parsing> call, Throwable t) {

            }
        });
    }

    // Thread Class
    class CustomThread extends Thread implements Runnable {

        private boolean isPlay = false;

        public CustomThread() {
            isPlay = true; // isPlay가 true이면 쓰레드 시작
        }

        public void isThreadState(boolean isPlay) {
            this.isPlay = isPlay;
        }

        public void stopThread() {
            isPlay =!isPlay; // 실행(true) 중인 isPlay를 종료(false) 한다.
        }

        @Override
        public void run() {
            super.run();

            // 무한 반복문 이기 때문에 thread.interrupt()로 thread를 멈출 수 없다.
            // 반복문으로 thread를 조정한다.
            while (isPlay) {

                // 1초 간격으로 실행
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // 현재 재생 중인 영상의 재생 시간을 가져오는 곳 (1초마다 재생 시간을 가져오게 구현함)


                if(player == null) {
                    Log.d(TAG, "VOD 시청 끝났습니다. 돌아가세용~!");
                } else {
                    Log.d(TAG, "VOD 시청 중");

                    //                Integer current_position = (int) player.getCurrentPosition() / 1000;
                    int current_position = (int) player.getCurrentPosition() / 1000;

                    Log.d(TAG, "INTEGER : " + current_position);

                    if(current_position == 0) {


                    } else {
                        messageHaldner.sendEmptyMessage(current_position); // 핸들러로 메세지 보내기 (현재 재생 시간 값)
                    }
                }

            }
        }
    }

    // Handler Class (Thread로 부터 현재 영상 재생 시간 값을 받는다.)
    class SendMessageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            Log.d(TAG, "MSG.WHAT 값은 : " + msg.what);
            int current_time = msg.what; // 현재 시간 값

            Streaming_Item item = new Streaming_Item();
            item = hashMap.get(current_time);


            // 해당 재생 시간에 추가할 채팅 목록이 없는 경우
            if(item == null) {
                Log.d(TAG, "객체가 비어 있습니다. current_position : " + current_time);
                before_time = current_time;
            }

            // 해당 재생 시간에 추가할 채팅 목록이 있는 경우
            else {

                // 재생 중지 버튼을 클릭했을 경우
                if(before_time == current_time) {
                    Log.d(TAG, "재생 정지 중! recycler view 추가 하지 않는다.");
                    before_time = current_time;

                }

                // 만약 내가 스크롤 바를 앞으로 또는 뒤로 이동했을 경우
                else if(current_time - before_time >=2 || before_time - current_time >=2) {

                    Log.d(TAG, "스크롤 바를 이동한 경우! ");

                    // recycler view 클리어 시킨 다음 다시 서버에서 값을 받아온다.
                    recycler_itemlist.clear();
                    getChatList();

                } else {

                    Log.d(TAG, "여기서 채팅 메세지가 추가 됩니다.");

                    String sender_name = item.getStreaming_user_nickname();
                    String sender_profile = item.getStreameing_image_profile();
                    String sender_message = item.getStreaming_user_message();
                    String broadcast_time = item.getStreaming_message_time();

                    Log.d(TAG, "싱크되는 값 확인(sender_name) : " + sender_name);
                    Log.d(TAG, "싱크되는 값 확인(sender_message) : " + sender_message);
                    Log.d(TAG, "싱크되는 값 확인(sender_profile) : " + sender_profile);
                    Log.d(TAG, "싱크되는 값 확인(broadcast_time) : " + broadcast_time);

                    // 0001 -> 1, 0015 -> 15
                    int broadcast_time1= Integer.parseInt(broadcast_time);


                    if(broadcast_time1<10) {

                        adapter.addVODMessage(sender_name, sender_message, sender_profile, "00:0" + broadcast_time1);
                        adapter.notifyDataSetChanged();
                    } else {
                        adapter.addVODMessage(sender_name, sender_message, sender_profile, "00:" + broadcast_time1);
                        adapter.notifyDataSetChanged();
                    }


                    recyclerView.scrollToPosition(adapter.getItemCount() - 1);

                    before_time = current_time;
                }
            }
        }
    }

    // =========================================================================================================
    // FB 이모티콘 보내기 기능을 구현한 곳
    // =========================================================================================================

    // Creating a Subscriber
    // since we have a flowable, we need a subscriber to subscribe to the evetns generated by the flowable.
    // A subscriber has 4 methods namely onSubscribe(), onNext(), onError() and on Completed()
    private Subscriber getSubscriber() {
        return new Subscriber<Timed<Emoticons>>() {
            @Override
            public void onSubscribe(Subscription s) {
                emoticonSubscription = s;
                emoticonSubscription.request(1);

                // for lazy evaluation.
                emoticonsView.initView(Vodplayer_Activity.this);
            }

            @Override
            public void onNext(final Timed<Emoticons> timed) {

                emoticonsView.addView(timed.value());

                long currentTimeStamp = System.currentTimeMillis();
                long diffInMillis = currentTimeStamp - ((Timed) timed).time();
                if (diffInMillis > MINIMUM_DURATION_BETWEEN_EMOTICONS) {
                    emoticonSubscription.request(1);
                } else {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            emoticonSubscription.request(1);
                        }
                    }, MINIMUM_DURATION_BETWEEN_EMOTICONS - diffInMillis);
                }
            }

            @Override
            public void onError(Throwable t) {
                //Do nothing
            }

            @Override
            public void onComplete() {
                if (emoticonSubscription != null) {
                    emoticonSubscription.cancel();
                }
            }
        };
    }

    // 애니메이션 클릭 이벤트 리스너
    private void convertClickEventToStream(final FlowableEmitter emitter) {
        likeEmoticonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doOnClick(likeEmoticonButton, emitter, Emoticons.LIKE);
            }
        });

        loveEmoticonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doOnClick(loveEmoticonButton, emitter, Emoticons.LOVE);
            }
        });

        hahaEmoticonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doOnClick(hahaEmoticonButton, emitter, Emoticons.HAHA);
            }
        });

        wowEmoticonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doOnClick(wowEmoticonButton, emitter, Emoticons.WOW);
            }
        });

        sadEmoticonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doOnClick(sadEmoticonButton, emitter, Emoticons.SAD);
            }
        });

        angryEmoticonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doOnClick(angryEmoticonButton, emitter, Emoticons.ANGRY);
            }
        });
    }

    // 이모티콘을 클릭했을 때의 애니매이션 효과 주기
    private void doOnClick(View view, FlowableEmitter emitter, Emoticons emoticons) {
        emoticonClickAnimation = AnimationUtils.loadAnimation(Vodplayer_Activity.this, R.anim.emoticon_click_animation);
        view.startAnimation(emoticonClickAnimation);
        emitter.onNext(emoticons);
    }
    // =========================================================================================================



    // =========================================================================================================
    // 생명 주기
    // =========================================================================================================

    @Override
    public void onStart() {
        super.onStart();

        // Creating a Stream from click events
        // In RxJava, everything can be converted into stream. Therefore the clicks on various emoticons can be considered as one single stream
        // which will emit different types of items at different intervals.
        // Create an instance of FlowableOnSubscribe which will convert click events to streams
        FlowableOnSubscribe flowableOnSubscribe = new FlowableOnSubscribe() {
            @Override
            public void subscribe(final FlowableEmitter emitter) throws Exception {
                convertClickEventToStream(emitter);
            }
        };

        // Creating a Flowable
        // Since we need to check the time difference between two stream items, we need to get the timestamp
        // information from every item.
        // Give the backpressure strategy as BUFFER, so that the click items do not drop.
        Flowable emoticonsFlowable = Flowable.create(flowableOnSubscribe, BackpressureStrategy.BUFFER);


        //Convert the stream to a timed stream, as we require the timestamp of each event
        Flowable<Timed> emoticonsTimedFlowable = emoticonsFlowable.timestamp();
        subscriber = getSubscriber();
        //Subscribe
        emoticonsTimedFlowable.subscribeWith(subscriber);
    }

    protected void onPause() {
        super.onPause();

        // 쓰레드 종료
        customThread.stopThread();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (emoticonSubscription != null) {
            emoticonSubscription.cancel();
        }

        releasePlayer();
    }
    // =========================================================================================================

}
















/*    final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);


        }
    };*/