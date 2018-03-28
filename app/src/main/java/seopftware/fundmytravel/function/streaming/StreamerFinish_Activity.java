package seopftware.fundmytravel.function.streaming;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import seopftware.fundmytravel.R;
import seopftware.fundmytravel.activity.Home_Activity;

// 스트리머 방송 종료시 스트리머에게 나타나는 액티비티
// 방 번호 및 총 방송 진행 시간은 Streaming_Activity에서 가져오기
// 성별 및 연령대별 숫자, 총 시청자수는 http 통신하기
public class StreamerFinish_Activity extends AppCompatActivity {

    private static final String TAG = "all_"+"StreamerFinish";

    // UI
    TextView tv_TotalViewer; // 전체 시청자 수
    TextView tv_Duration; // 방송 시간
    TextView tv_Stars; // 방송 동안 받은 별풍선 갯수
    Button btn_confirm;

    // PieChart
    private PieChart pieChart_sex;
    private PieChart pieChart_age;


    // 변수
    String room_id; // 방 번호
    String broadcast_time; // 총 방송 진행 시간

    String room_numpeople; // 방송 총 시청자 수
    String room_male, room_female; // 방송 성별
    String room_20s, room_30s, room_40s, room_50s; // 방송 연령대
    String room_star; // 방송 중 받은 별풍선 갯수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_streamer_finish);

        // Streaming Activity로 부터 정보받아오기
        // 방 번호, 방송 진행 시간
        Intent intent = getIntent();
        room_id = intent.getStringExtra("room_id"); // 방 번호
        broadcast_time = intent.getStringExtra("broadcast_time"); // 방송 총 시간 (Duration)

        // UI
        tv_TotalViewer = (TextView) findViewById(R.id.tv_TotalViewer);
        tv_Stars = (TextView) findViewById(R.id.tv_Stars);

        tv_Duration = (TextView) findViewById(R.id.tv_Duration);
        tv_Duration.setText(broadcast_time); // 총 방송 시간 입력

        btn_confirm = (Button) findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Home_Activity.class);
                startActivity(intent);
                finish();
            }
        });

        // Piechart_sex
        pieChart_sex = (PieChart)findViewById(R.id.piechart_sex);

        // Piechart_Age
        pieChart_age = (PieChart)findViewById(R.id.piechart_age);

        // HTTP 통신 - 방송 정보 받아오기
        getStreamingInfo();




    }

    private void getPiechart_sex() {

        pieChart_sex.setUsePercentValues(true);
        pieChart_sex.getDescription().setEnabled(false);
        pieChart_sex.setExtraOffsets(5,10,5,5);

        pieChart_sex.setDragDecelerationFrictionCoef(0.95f);

        pieChart_sex.setDrawHoleEnabled(false);
        pieChart_sex.setHoleColor(Color.WHITE);
        pieChart_sex.setTransparentCircleRadius(61f);

        ArrayList<PieEntry> yValues = new ArrayList<PieEntry>();

        yValues.add(new PieEntry(Float.parseFloat(room_male),"MALE")); // 남성
        yValues.add(new PieEntry(Float.parseFloat(room_female),"FEMALE")); // 여성

        pieChart_sex.animateY(1000, Easing.EasingOption.EaseInOutCubic); //애니메이션

        PieDataSet dataSet = new PieDataSet(yValues,"Countries");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);

        PieData data = new PieData((dataSet));
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.YELLOW);

        pieChart_sex.setData(data);

    }

    private void getPiechart_age() {

        pieChart_age.setUsePercentValues(true);
        pieChart_age.getDescription().setEnabled(false);
        pieChart_age.setExtraOffsets(5,10,5,5);

        pieChart_age.setDragDecelerationFrictionCoef(0.95f);

        pieChart_age.setDrawHoleEnabled(false);
        pieChart_age.setHoleColor(Color.WHITE);
        pieChart_age.setTransparentCircleRadius(61f);

        ArrayList<PieEntry> yValues2 = new ArrayList<PieEntry>();

        yValues2.add(new PieEntry(Float.parseFloat(room_20s),"20s"));
        yValues2.add(new PieEntry(Float.parseFloat(room_30s),"30s"));
        yValues2.add(new PieEntry(Float.parseFloat(room_40s),"40s"));
        yValues2.add(new PieEntry(Float.parseFloat(room_50s),"50s"));


        pieChart_age.animateY(1000, Easing.EasingOption.EaseInOutCubic); //애니메이션

        PieDataSet dataSet2 = new PieDataSet(yValues2,"Countries");
        dataSet2.setSliceSpace(3f);
        dataSet2.setSelectionShift(5f);
        dataSet2.setColors(ColorTemplate.JOYFUL_COLORS);

        PieData data2 = new PieData((dataSet2));
        data2.setValueTextSize(10f);
        data2.setValueTextColor(Color.YELLOW);

        pieChart_age.setData(data2);
    }

    // 보내는 값: 방 번호
    // 받는 값: 성별+연령별 데이터, 방송 동안 받은 별풍선 갯수
    // PHP: getStreamingInfo
    // 쉐프 프로필 정보 받아오는 함수
    private void getStreamingInfo() {

        String url = "http://52.79.138.20/php/select/getStreamingInfo.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("parsing1", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("result");

                    JSONObject jo = jsonArray.getJSONObject(0);

                    room_numpeople = jo.getString("room_numpeople");
                    tv_TotalViewer.setText(room_numpeople);

                    room_male = jo.getString("room_male");
                    room_female = jo.getString("room_female");

                    room_20s = jo.getString("room_20s");
                    room_30s = jo.getString("room_30s");
                    room_40s = jo.getString("room_40s");
                    room_50s = jo.getString("room_50s");

                    room_star = jo.getString("room_star");
                    tv_Stars.setText(room_star);

                    getPiechart_sex();
                    getPiechart_age();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Anything you want
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                String update_roomid = "\""+room_id + "\"";

                Map<String, String> map = new Hashtable<>();
                map.put("room_id", update_roomid);

                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
}
