package seopftware.fundmytravel.function.streaming;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import seopftware.fundmytravel.R;
import seopftware.fundmytravel.function.retrofit.HttpService;
import seopftware.fundmytravel.function.retrofit.RetrofitClient;

import static seopftware.fundmytravel.function.chatting.Chat_Service.channel;

// 시청자 화면에서 별풍선을 보내기 위한 다이얼로그 액티비티
// 별풍선 들을 보여주고 BJ에게 별 풍선을 선물 할 수 있게 만드는 곳.
public class PlayerStarSend_Activity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "all_" + PlayerStarSend_Activity.class;

    TextView tv_currentmoney; // 현재 내가 갖고 있는 돈 현황
    ImageView iv_star1, iv_star5, iv_star9; // 첫 번쨰 줄의 별풍선
    ImageView iv_star30, iv_star100, iv_star300; // 두 번쨰 줄의 별풍선
    ImageView iv_star500, iv_star900, iv_star999; // 세 번쨰 줄의 별풍선

    // 변수
    String streamer_name; // 내가 접속한 방의 스트리머 이름
    String money = null; // 보내고자 하는 코인의 갯수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_star_send);

        Intent intent = getIntent();
        streamer_name = intent.getStringExtra("streamer_name"); // 스트리머 이름

        tv_currentmoney = (TextView) findViewById(R.id.tv_currentmoney); // 나의 돈 현황

        // 첫 번째 줄 별풍선
        iv_star1= (ImageView) findViewById(R.id.iv_star1);
        iv_star1.setOnClickListener(this);
        iv_star5= (ImageView) findViewById(R.id.iv_star5);
        iv_star5.setOnClickListener(this);
        iv_star9= (ImageView) findViewById(R.id.iv_star9);
        iv_star9.setOnClickListener(this);

        // 두 번쨰 줄 별풍선
        iv_star30= (ImageView) findViewById(R.id.iv_star30);
        iv_star30.setOnClickListener(this);
        iv_star100= (ImageView) findViewById(R.id.iv_star100);
        iv_star100.setOnClickListener(this);
        iv_star300= (ImageView) findViewById(R.id.iv_star300);
        iv_star300.setOnClickListener(this);


        // 세 번줄 별풍선
        iv_star500= (ImageView) findViewById(R.id.iv_star500);
        iv_star500.setOnClickListener(this);
        iv_star900= (ImageView) findViewById(R.id.iv_star900);
        iv_star900.setOnClickListener(this);
        iv_star999= (ImageView) findViewById(R.id.iv_star999);
        iv_star999.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if(id==R.id.iv_star1) {

            money = "1";
        }

        else if(id==R.id.iv_star5) {

            money = "5";
        }

        else if(id==R.id.iv_star9) {

            money = "9";
        }

        else if(id==R.id.iv_star30) {

            money = "30";
        }

        else if(id==R.id.iv_star100) {

            money = "100";
        }

        else if(id==R.id.iv_star300) {

            money = "300";
        }

        else if(id==R.id.iv_star500) {

            money = "500";
        }

        else if(id==R.id.iv_star900) {

            money = "900";
        }

        else if(id==R.id.iv_star999) {

            money = "999";
        }

        String message = streamer_name + "님에게 " + money + "코인을 보내시겠습니까?";

        AlertDialog.Builder gsDialog = new AlertDialog.Builder(this);
        gsDialog.setTitle("Notice");
        // gsDialog.setIcon(R.drawable.ic_menu_send);
        gsDialog.setMessage(message);

        gsDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                try {

                    // 서버로 보낼 메세지(스트리머 및 방에 속해있는 사람들에게 알리기)
                    JSONObject object = new JSONObject();
                    object.put("message_type", "message_star"); // 메세지 타입-별풍선 보내기
                    object.put("streamer_name", streamer_name); // 스트리머 이름
                    object.put("send_money", money); // 보내고자 하는 코인의 갯수
                    String Object_Data = object.toString();
                    channel.writeAndFlush(Object_Data);

                    starUpdate();

                    // 서버로 메세지 보낸 후 별풍선 보내기 화면 종료
                    finish();

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                }).create();
        gsDialog.show();

    } // onClick finish

    // Http 통신 하는 곳
    // 보내는 값: 스트리머의 이름, 보내고자 하는 코인의 갯수
    // 받는 값: 성공 여부
    // PHP: update_star
    private void starUpdate() {
        Retrofit retrofit = RetrofitClient.getClient();
        HttpService httpService = retrofit.create(HttpService.class);
        Call<ResponseBody> comment = httpService.update_star(streamer_name, Integer.valueOf(money)); // 클라에서 서버로 보내는 값
        comment.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()) {
                    Log.d(TAG, "별풍선 보내기에 성공했습니다.");

                } else {
                    Log.d(TAG, "별풍선 보내기에 실패했습니다.");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }
}
