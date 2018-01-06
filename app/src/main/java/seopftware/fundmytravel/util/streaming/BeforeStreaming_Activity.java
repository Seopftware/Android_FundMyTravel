package seopftware.fundmytravel.util.streaming;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import seopftware.fundmytravel.R;

/**
 * 방송 전 방송 제목 입력하는 Activity
 * @author 김인섭
 * @version 1.0.0
 * @since 2018-01-06 오후 12:03
 * @class comment
 *   이 액티비티는 방송 제목 입력 후 방송을 시작하는 버튼이 있는 곳 입니다.
**/
public class BeforeStreaming_Activity extends AppCompatActivity {

    Button btn_streamstart; // 방송 시작

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beforestreaming);

        btn_streamstart = (Button) findViewById(R.id.btn_streamstart);

        // Go Live 버튼 클릭시 방송 시작
        btn_streamstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), Streaming_Acticity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}
