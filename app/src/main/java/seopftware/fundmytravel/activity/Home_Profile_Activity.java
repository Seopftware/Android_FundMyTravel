package seopftware.fundmytravel.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import seopftware.fundmytravel.R;
import seopftware.fundmytravel.dataset.Parsing;
import seopftware.fundmytravel.function.retrofit.HttpService;
import seopftware.fundmytravel.function.retrofit.RetrofitClient;
import seopftware.fundmytravel.webrtc.Call_Activity;

import static seopftware.fundmytravel.function.MyApp.SERVER_URL;
import static seopftware.fundmytravel.function.MyApp.USER_ID;
import static seopftware.fundmytravel.function.chatting.Chat_Service.channel;

public class Home_Profile_Activity extends Activity implements View.OnClickListener{

    private static final String TAG = "all_"+ "ConnectActivity";
    private static final int CONNECTION_REQUEST = 1;
    private static boolean commandLineRun = false;

    private SharedPreferences sharedPref;
    private String keyprefResolution;
    private String keyprefFps;
    private String keyprefVideoBitrateType;
    private String keyprefVideoBitrateValue;
    private String keyprefAudioBitrateType;
    private String keyprefAudioBitrateValue;
    private String keyprefRoomServerUrl;
    private String keyprefRoom;

    // 화면 변수들
    private ImageButton ibtn_videocall, ibtn_exit; // 영상통화 버튼, 프로필 나가기 버튼
    private ImageView iv_background, iv_profile; // 백그라운드 이미지, 프로필 이미지
    private TextView tv_message, tv_name; // 상태 메세지, 유저 이름

    // 친구 ID
    private int receiver_id; // 영통을 받을 사람의 id
    String user_name, user_profile, user_status, user_background;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get setting keys.
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        keyprefResolution = getString(R.string.pref_resolution_key);
        keyprefFps = getString(R.string.pref_fps_key);
        keyprefVideoBitrateType = getString(R.string.pref_maxvideobitrate_key);
        keyprefVideoBitrateValue = getString(R.string.pref_maxvideobitratevalue_key);
        keyprefAudioBitrateType = getString(R.string.pref_startaudiobitrate_key);
        keyprefAudioBitrateValue = getString(R.string.pref_startaudiobitratevalue_key);
        keyprefRoomServerUrl = getString(R.string.pref_room_server_url_key);
        keyprefRoom = getString(R.string.pref_room_key);
        setContentView(R.layout.activity_connect);


        // UI 선언
        // 1.영상 통화
        ibtn_videocall = (ImageButton) findViewById(R.id.ibtn_videocall);
        ibtn_videocall.setOnClickListener(this);

        // 2.프로필 화면 나가기
        ibtn_exit= (ImageButton) findViewById(R.id.ibtn_exit);
        ibtn_exit.setOnClickListener(this);

        // 3.백그라운드 이미지
        iv_background= (ImageView) findViewById(R.id.iv_background);

        // 4.프로필 이미지
        iv_profile= (ImageView) findViewById(R.id.iv_profile);

        // 5.상태 메세지
        tv_message= (TextView) findViewById(R.id.tv_message);

        // 6.유저 이름
        tv_name= (TextView) findViewById(R.id.tv_name);




        // If an implicit VIEW intent is launching the app, go directly to that URL.
        final Intent intent = getIntent();
        if ("android.intent.action.VIEW".equals(intent.getAction()) && !commandLineRun) {
            boolean loopback = intent.getBooleanExtra(Call_Activity.EXTRA_LOOPBACK, false);
            int runTimeMs = intent.getIntExtra(Call_Activity.EXTRA_RUNTIME, 0);
            boolean useValuesFromIntent =
                    intent.getBooleanExtra(Call_Activity.EXTRA_USE_VALUES_FROM_INTENT, false);
            String room = sharedPref.getString(keyprefRoom, "");
            connectToRoom(room, true, loopback, useValuesFromIntent, runTimeMs);
        }

        receiver_id = intent.getIntExtra("FRIENDS_ID", 0);

        getFriendsInfo();


    } // onCreate() finish


    // =========================================================================================================
    // 클릭 리스너
    // =========================================================================================================
    @Override
    public void onClick(View view) {

        switch (view.getId()) {


            // 1.영상 통화
            case R.id.ibtn_videocall:
                Random generator = new Random();
                int random_num= generator.nextInt(9000)+1000;
                // netxtInt(9000): 0~8999 +1000 = 1000 ~ 9999의 4자리 랜덤 숫자 발생
                Log.d(TAG, "난수 발생: " + random_num);

                String room_number = "seope" + random_num;
                connectToRoom(room_number, false, false, false, 0); // 영상통화 연결


                try {

                    // Netty로 영통이 왔다는 걸 알림
                    JSONObject object = new JSONObject();
                    object.put("message_type", "video_call"); // 서버와 연결됨
                    object.put("user_id", USER_ID); // 나의 id
                    object.put("receiver_id", receiver_id); // 통화를 받는 친구의 ID
                    object.put("room_number", room_number); // 통화를 하기 위해 필요한 Room Number

                    String Object_Data = object.toString();
                    channel.writeAndFlush(Object_Data);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;


            // 2.프로필 화면 나가기
            case R.id.ibtn_exit:
                finish();
                break;

            // 3.백그라운드 이미지
            case R.id.iv_background:

                break;



        } // switch 구문 close
    } // onClick 함수 finish
    // =========================================================================================================
    // 보내는 값: User ID (내가 보고자 하는 유저의 ID)
    // 받는 값: User_Nickname, User_Profile, User_Status, User_Background
    private void getFriendsInfo() {
        // Http 통신하는 부분
        Retrofit retrofit = RetrofitClient.getClient();
        HttpService httpService = retrofit.create(HttpService.class);
        Call<Parsing> comment = httpService.get_userinfo(receiver_id); // 1.User Id 보내고 정보 받아옴
        comment.enqueue(new Callback<Parsing>() {
            @Override
            public void onResponse(Call<Parsing> call, Response<Parsing> response) {
                Parsing parsing = response.body();

                // 유저의 이름
                user_name = parsing.getResult().get(0).getUserName();
                tv_name.setText(user_name);

                // 유저의 프로필 사진
                user_profile = parsing.getResult().get(0).getUserPhoto();
                Glide.with(getApplicationContext())
                        .load(SERVER_URL + "photo/"+user_profile)
                        .bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                        .into(iv_profile);

                // 유저의 상태 메세지
                user_status = parsing.getResult().get(0).getUserStatus();
                tv_message.setText(user_status);

                // 유저의 백그라운드 이미지
                user_background = parsing.getResult().get(0).getUserPhotoBackground();
                Glide.with(getApplicationContext()).load(SERVER_URL + "photo/"+user_background).into(iv_background); // 사각형 프로필

            }

            @Override
            public void onFailure(Call<Parsing> call, Throwable t) {


            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CONNECTION_REQUEST && commandLineRun) {
            Log.d(TAG, "Return: " + resultCode);
            setResult(resultCode);
            commandLineRun = false;
            finish();
        }
    }

    /**
     * Get a value from the shared preference or from the intent, if it does not
     * exist the default is used.
     */
    private String sharedPrefGetString(
            int attributeId, String intentName, int defaultId, boolean useFromIntent) {
        String defaultValue = getString(defaultId);
        if (useFromIntent) {
            String value = getIntent().getStringExtra(intentName);
            if (value != null) {
                return value;
            }
            return defaultValue;
        } else {
            String attributeName = getString(attributeId);
            return sharedPref.getString(attributeName, defaultValue);
        }
    }

    /**
     * Get a value from the shared preference or from the intent, if it does not
     * exist the default is used.
     */
    private boolean sharedPrefGetBoolean(
            int attributeId, String intentName, int defaultId, boolean useFromIntent) {
        boolean defaultValue = Boolean.valueOf(getString(defaultId));
        if (useFromIntent) {
            return getIntent().getBooleanExtra(intentName, defaultValue);
        } else {
            String attributeName = getString(attributeId);
            return sharedPref.getBoolean(attributeName, defaultValue);
        }
    }

    /**
     * Get a value from the shared preference or from the intent, if it does not
     * exist the default is used.
     */
    private int sharedPrefGetInteger(
            int attributeId, String intentName, int defaultId, boolean useFromIntent) {
        String defaultString = getString(defaultId);
        int defaultValue = Integer.parseInt(defaultString);
        if (useFromIntent) {
            return getIntent().getIntExtra(intentName, defaultValue);
        } else {
            String attributeName = getString(attributeId);
            String value = sharedPref.getString(attributeName, defaultString);
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                Log.e(TAG, "Wrong setting for: " + attributeName + ":" + value);
                return defaultValue;
            }
        }
    }

    private void connectToRoom(String roomId, boolean commandLineRun, boolean loopback,
                               boolean useValuesFromIntent, int runTimeMs) {
        this.commandLineRun = commandLineRun;

        // roomId is random for loopback.
        if (loopback) {
            roomId = Integer.toString((new Random()).nextInt(100000000));
        }

        String roomUrl = sharedPref.getString(
                keyprefRoomServerUrl, getString(R.string.pref_room_server_url_default));

        // Video call enabled flag.
        boolean videoCallEnabled = sharedPrefGetBoolean(R.string.pref_videocall_key,
                Call_Activity.EXTRA_VIDEO_CALL, R.string.pref_videocall_default, useValuesFromIntent);

        // Use screencapture option.
        boolean useScreencapture = sharedPrefGetBoolean(R.string.pref_screencapture_key,
                Call_Activity.EXTRA_SCREENCAPTURE, R.string.pref_screencapture_default, useValuesFromIntent);

        // Use Camera2 option.
        boolean useCamera2 = sharedPrefGetBoolean(R.string.pref_camera2_key, Call_Activity.EXTRA_CAMERA2,
                R.string.pref_camera2_default, useValuesFromIntent);

        // Get default codecs.
        String videoCodec = sharedPrefGetString(R.string.pref_videocodec_key,
                Call_Activity.EXTRA_VIDEOCODEC, R.string.pref_videocodec_default, useValuesFromIntent);
        String audioCodec = sharedPrefGetString(R.string.pref_audiocodec_key,
                Call_Activity.EXTRA_AUDIOCODEC, R.string.pref_audiocodec_default, useValuesFromIntent);

        // Check HW codec flag.
        boolean hwCodec = sharedPrefGetBoolean(R.string.pref_hwcodec_key,
                Call_Activity.EXTRA_HWCODEC_ENABLED, R.string.pref_hwcodec_default, useValuesFromIntent);

        // Check Capture to texture.
        boolean captureToTexture = sharedPrefGetBoolean(R.string.pref_capturetotexture_key,
                Call_Activity.EXTRA_CAPTURETOTEXTURE_ENABLED, R.string.pref_capturetotexture_default,
                useValuesFromIntent);

        // Check FlexFEC.
        boolean flexfecEnabled = sharedPrefGetBoolean(R.string.pref_flexfec_key,
                Call_Activity.EXTRA_FLEXFEC_ENABLED, R.string.pref_flexfec_default, useValuesFromIntent);

        // Check Disable Audio Processing flag.
        boolean noAudioProcessing = sharedPrefGetBoolean(R.string.pref_noaudioprocessing_key,
                Call_Activity.EXTRA_NOAUDIOPROCESSING_ENABLED, R.string.pref_noaudioprocessing_default,
                useValuesFromIntent);

        // Check Disable Audio Processing flag.
        boolean aecDump = sharedPrefGetBoolean(R.string.pref_aecdump_key,
                Call_Activity.EXTRA_AECDUMP_ENABLED, R.string.pref_aecdump_default, useValuesFromIntent);

        // Check OpenSL ES enabled flag.
        boolean useOpenSLES = sharedPrefGetBoolean(R.string.pref_opensles_key,
                Call_Activity.EXTRA_OPENSLES_ENABLED, R.string.pref_opensles_default, useValuesFromIntent);

        // Check Disable built-in AEC flag.
        boolean disableBuiltInAEC = sharedPrefGetBoolean(R.string.pref_disable_built_in_aec_key,
                Call_Activity.EXTRA_DISABLE_BUILT_IN_AEC, R.string.pref_disable_built_in_aec_default,
                useValuesFromIntent);

        // Check Disable built-in AGC flag.
        boolean disableBuiltInAGC = sharedPrefGetBoolean(R.string.pref_disable_built_in_agc_key,
                Call_Activity.EXTRA_DISABLE_BUILT_IN_AGC, R.string.pref_disable_built_in_agc_default,
                useValuesFromIntent);

        // Check Disable built-in NS flag.
        boolean disableBuiltInNS = sharedPrefGetBoolean(R.string.pref_disable_built_in_ns_key,
                Call_Activity.EXTRA_DISABLE_BUILT_IN_NS, R.string.pref_disable_built_in_ns_default,
                useValuesFromIntent);

        // Check Enable level control.
        boolean enableLevelControl = sharedPrefGetBoolean(R.string.pref_enable_level_control_key,
                Call_Activity.EXTRA_ENABLE_LEVEL_CONTROL, R.string.pref_enable_level_control_key,
                useValuesFromIntent);

        // Check Disable gain control
        boolean disableWebRtcAGCAndHPF = sharedPrefGetBoolean(
                R.string.pref_disable_webrtc_agc_and_hpf_key, Call_Activity.EXTRA_DISABLE_WEBRTC_AGC_AND_HPF,
                R.string.pref_disable_webrtc_agc_and_hpf_key, useValuesFromIntent);

        // Get video resolution from settings.
        int videoWidth = 0;
        int videoHeight = 0;
        if (useValuesFromIntent) {
            videoWidth = getIntent().getIntExtra(Call_Activity.EXTRA_VIDEO_WIDTH, 0);
            videoHeight = getIntent().getIntExtra(Call_Activity.EXTRA_VIDEO_HEIGHT, 0);
        }
        if (videoWidth == 0 && videoHeight == 0) {
            String resolution =
                    sharedPref.getString(keyprefResolution, getString(R.string.pref_resolution_default));
            String[] dimensions = resolution.split("[ x]+");
            if (dimensions.length == 2) {
                try {
                    videoWidth = Integer.parseInt(dimensions[0]);
                    videoHeight = Integer.parseInt(dimensions[1]);
                } catch (NumberFormatException e) {
                    videoWidth = 0;
                    videoHeight = 0;
                    Log.e(TAG, "Wrong video resolution setting: " + resolution);
                }
            }
        }

        // Get camera fps from settings.
        int cameraFps = 0;
        if (useValuesFromIntent) {
            cameraFps = getIntent().getIntExtra(Call_Activity.EXTRA_VIDEO_FPS, 0);
        }
        if (cameraFps == 0) {
            String fps = sharedPref.getString(keyprefFps, getString(R.string.pref_fps_default));
            String[] fpsValues = fps.split("[ x]+");
            if (fpsValues.length == 2) {
                try {
                    cameraFps = Integer.parseInt(fpsValues[0]);
                } catch (NumberFormatException e) {
                    cameraFps = 0;
                    Log.e(TAG, "Wrong camera fps setting: " + fps);
                }
            }
        }

        // Check capture quality slider flag.
        boolean captureQualitySlider = sharedPrefGetBoolean(R.string.pref_capturequalityslider_key,
                Call_Activity.EXTRA_VIDEO_CAPTUREQUALITYSLIDER_ENABLED,
                R.string.pref_capturequalityslider_default, useValuesFromIntent);

        // Get video and audio start bitrate.
        int videoStartBitrate = 0;
        if (useValuesFromIntent) {
            videoStartBitrate = getIntent().getIntExtra(Call_Activity.EXTRA_VIDEO_BITRATE, 0);
        }
        if (videoStartBitrate == 0) {
            String bitrateTypeDefault = getString(R.string.pref_maxvideobitrate_default);
            String bitrateType = sharedPref.getString(keyprefVideoBitrateType, bitrateTypeDefault);
            if (!bitrateType.equals(bitrateTypeDefault)) {
                String bitrateValue = sharedPref.getString(
                        keyprefVideoBitrateValue, getString(R.string.pref_maxvideobitratevalue_default));
                videoStartBitrate = Integer.parseInt(bitrateValue);
            }
        }

        int audioStartBitrate = 0;
        if (useValuesFromIntent) {
            audioStartBitrate = getIntent().getIntExtra(Call_Activity.EXTRA_AUDIO_BITRATE, 0);
        }
        if (audioStartBitrate == 0) {
            String bitrateTypeDefault = getString(R.string.pref_startaudiobitrate_default);
            String bitrateType = sharedPref.getString(keyprefAudioBitrateType, bitrateTypeDefault);
            if (!bitrateType.equals(bitrateTypeDefault)) {
                String bitrateValue = sharedPref.getString(
                        keyprefAudioBitrateValue, getString(R.string.pref_startaudiobitratevalue_default));
                audioStartBitrate = Integer.parseInt(bitrateValue);
            }
        }

        // Check statistics display option.
        boolean displayHud = sharedPrefGetBoolean(R.string.pref_displayhud_key,
                Call_Activity.EXTRA_DISPLAY_HUD, R.string.pref_displayhud_default, useValuesFromIntent);

        boolean tracing = sharedPrefGetBoolean(R.string.pref_tracing_key, Call_Activity.EXTRA_TRACING,
                R.string.pref_tracing_default, useValuesFromIntent);

        // Get datachannel options
        boolean dataChannelEnabled = sharedPrefGetBoolean(R.string.pref_enable_datachannel_key,
                Call_Activity.EXTRA_DATA_CHANNEL_ENABLED, R.string.pref_enable_datachannel_default,
                useValuesFromIntent);
        boolean ordered = sharedPrefGetBoolean(R.string.pref_ordered_key, Call_Activity.EXTRA_ORDERED,
                R.string.pref_ordered_default, useValuesFromIntent);
        boolean negotiated = sharedPrefGetBoolean(R.string.pref_negotiated_key,
                Call_Activity.EXTRA_NEGOTIATED, R.string.pref_negotiated_default, useValuesFromIntent);
        int maxRetrMs = sharedPrefGetInteger(R.string.pref_max_retransmit_time_ms_key,
                Call_Activity.EXTRA_MAX_RETRANSMITS_MS, R.string.pref_max_retransmit_time_ms_default,
                useValuesFromIntent);
        int maxRetr =
                sharedPrefGetInteger(R.string.pref_max_retransmits_key, Call_Activity.EXTRA_MAX_RETRANSMITS,
                        R.string.pref_max_retransmits_default, useValuesFromIntent);
        int id = sharedPrefGetInteger(R.string.pref_data_id_key, Call_Activity.EXTRA_ID,
                R.string.pref_data_id_default, useValuesFromIntent);
        String protocol = sharedPrefGetString(R.string.pref_data_protocol_key,
                Call_Activity.EXTRA_PROTOCOL, R.string.pref_data_protocol_default, useValuesFromIntent);


        // Start AppRTCMobile activity.
        // Call_Activity로 보내는 값들
        Log.d(TAG, "Connecting to room " + roomId + " at URL " + roomUrl);
        if (validateUrl(roomUrl)) {
            Uri uri = Uri.parse(roomUrl);
            Intent intent = new Intent(this, Call_Activity.class);
            intent.setData(uri);
            intent.putExtra("user_name", user_name);
            intent.putExtra(Call_Activity.EXTRA_ROOMID, roomId);
            intent.putExtra(Call_Activity.EXTRA_LOOPBACK, loopback);
            intent.putExtra(Call_Activity.EXTRA_VIDEO_CALL, videoCallEnabled);
            intent.putExtra(Call_Activity.EXTRA_SCREENCAPTURE, useScreencapture);
            intent.putExtra(Call_Activity.EXTRA_CAMERA2, useCamera2);
            intent.putExtra(Call_Activity.EXTRA_VIDEO_WIDTH, videoWidth);
            intent.putExtra(Call_Activity.EXTRA_VIDEO_HEIGHT, videoHeight);
            intent.putExtra(Call_Activity.EXTRA_VIDEO_FPS, cameraFps);
            intent.putExtra(Call_Activity.EXTRA_VIDEO_CAPTUREQUALITYSLIDER_ENABLED, captureQualitySlider);
            intent.putExtra(Call_Activity.EXTRA_VIDEO_BITRATE, videoStartBitrate);
            intent.putExtra(Call_Activity.EXTRA_VIDEOCODEC, videoCodec);
            intent.putExtra(Call_Activity.EXTRA_HWCODEC_ENABLED, hwCodec);
            intent.putExtra(Call_Activity.EXTRA_CAPTURETOTEXTURE_ENABLED, captureToTexture);
            intent.putExtra(Call_Activity.EXTRA_FLEXFEC_ENABLED, flexfecEnabled);
            intent.putExtra(Call_Activity.EXTRA_NOAUDIOPROCESSING_ENABLED, noAudioProcessing);
            intent.putExtra(Call_Activity.EXTRA_AECDUMP_ENABLED, aecDump);
            intent.putExtra(Call_Activity.EXTRA_OPENSLES_ENABLED, useOpenSLES);
            intent.putExtra(Call_Activity.EXTRA_DISABLE_BUILT_IN_AEC, disableBuiltInAEC);
            intent.putExtra(Call_Activity.EXTRA_DISABLE_BUILT_IN_AGC, disableBuiltInAGC);
            intent.putExtra(Call_Activity.EXTRA_DISABLE_BUILT_IN_NS, disableBuiltInNS);
            intent.putExtra(Call_Activity.EXTRA_ENABLE_LEVEL_CONTROL, enableLevelControl);
            intent.putExtra(Call_Activity.EXTRA_DISABLE_WEBRTC_AGC_AND_HPF, disableWebRtcAGCAndHPF);
            intent.putExtra(Call_Activity.EXTRA_AUDIO_BITRATE, audioStartBitrate);
            intent.putExtra(Call_Activity.EXTRA_AUDIOCODEC, audioCodec);
            intent.putExtra(Call_Activity.EXTRA_DISPLAY_HUD, displayHud);
            intent.putExtra(Call_Activity.EXTRA_TRACING, tracing);
            intent.putExtra(Call_Activity.EXTRA_CMDLINE, commandLineRun);
            intent.putExtra(Call_Activity.EXTRA_RUNTIME, runTimeMs);
            intent.putExtra(Call_Activity.EXTRA_DATA_CHANNEL_ENABLED, dataChannelEnabled);

            if (dataChannelEnabled) {
                intent.putExtra(Call_Activity.EXTRA_ORDERED, ordered);
                intent.putExtra(Call_Activity.EXTRA_MAX_RETRANSMITS_MS, maxRetrMs);
                intent.putExtra(Call_Activity.EXTRA_MAX_RETRANSMITS, maxRetr);
                intent.putExtra(Call_Activity.EXTRA_PROTOCOL, protocol);
                intent.putExtra(Call_Activity.EXTRA_NEGOTIATED, negotiated);
                intent.putExtra(Call_Activity.EXTRA_ID, id);
            }

            if (useValuesFromIntent) {
                if (getIntent().hasExtra(Call_Activity.EXTRA_VIDEO_FILE_AS_CAMERA)) {
                    String videoFileAsCamera =
                            getIntent().getStringExtra(Call_Activity.EXTRA_VIDEO_FILE_AS_CAMERA);
                    intent.putExtra(Call_Activity.EXTRA_VIDEO_FILE_AS_CAMERA, videoFileAsCamera);
                }

                if (getIntent().hasExtra(Call_Activity.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE)) {
                    String saveRemoteVideoToFile =
                            getIntent().getStringExtra(Call_Activity.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE);
                    intent.putExtra(Call_Activity.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE, saveRemoteVideoToFile);
                }

                if (getIntent().hasExtra(Call_Activity.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_WIDTH)) {
                    int videoOutWidth =
                            getIntent().getIntExtra(Call_Activity.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_WIDTH, 0);
                    intent.putExtra(Call_Activity.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_WIDTH, videoOutWidth);
                }

                if (getIntent().hasExtra(Call_Activity.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT)) {
                    int videoOutHeight =
                            getIntent().getIntExtra(Call_Activity.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT, 0);
                    intent.putExtra(Call_Activity.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT, videoOutHeight);
                }
            }

            startActivityForResult(intent, CONNECTION_REQUEST);
        }
    }


    private boolean validateUrl(String url) {
        if (URLUtil.isHttpsUrl(url) || URLUtil.isHttpUrl(url)) {
            return true;
        }

        new AlertDialog.Builder(this)
                .setTitle(getText(R.string.invalid_url_title))
                .setMessage(getString(R.string.invalid_url_text, url))
                .setCancelable(false)
                .setNeutralButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                .create()
                .show();
        return false;
    }

    // =========================================================================================================
    // 생명주기
    // =========================================================================================================

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
