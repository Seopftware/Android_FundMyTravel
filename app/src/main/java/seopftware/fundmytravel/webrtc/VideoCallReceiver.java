package seopftware.fundmytravel.webrtc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import seopftware.fundmytravel.activity.Videocall_Receive_Activity;

import static seopftware.fundmytravel.function.MyApp.BROADCAST_NETTY_VIDEOCALL;

public class VideoCallReceiver extends BroadcastReceiver {

    private static final String TAG = "all_"+VideoCallReceiver.class;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "VideoCallReceiver() 작동");

        String name = intent.getAction(); // receiver intent filter 값


        // 만약 intent filter 값이 똑같다면 Receiver 작동!
        if(name.equals(BROADCAST_NETTY_VIDEOCALL)) {

            String user_id = intent.getStringExtra("user_id"); // 전화건 사람
            String receiver_id = intent.getStringExtra("receiver_id"); // 전화 받는 사람
            String room_number = intent.getStringExtra("room_number"); // webRTC 방번호

            Log.d(TAG, "user_id (서버에서 받은 메세지 (from Service) : " + user_id);
            Log.d(TAG, "receiver_id (서버에서 받은 메세지 (from Service) : " + receiver_id);
            Log.d(TAG, "room_number (서버에서 받은 메세지 (from Service) : " + room_number);

            Log.d(TAG, "****************************************************************");
            Log.d(TAG, "BroadcastReceiver() : (받기) 2.서비스에서 받은 메세지를 리스트뷰에 추가하는 곳");
            Log.d(TAG, "****************************************************************");


            // 보내는 값: caller_id, room_number
            // 보내는 곳: Videocall_Receive_Activity
            Intent callintent=new Intent(context, Videocall_Receive_Activity.class);

            // Broadcast에서 Activity를 실행시키기 위해서 필요함
            callintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            callintent.putExtra("user_id", user_id); // 전화를 건 사람
            callintent.putExtra("receiver_id", receiver_id); // 전화를 받는 사람
            callintent.putExtra("room_number", room_number);
            context.startActivity(callintent);
        }
    }
}
