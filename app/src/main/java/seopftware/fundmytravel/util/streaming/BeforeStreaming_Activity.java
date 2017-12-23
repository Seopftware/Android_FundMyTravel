package seopftware.fundmytravel.util.streaming;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import seopftware.fundmytravel.R;

public class BeforeStreaming_Activity extends AppCompatActivity {

    Button btn_streamstart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beforestreaming);

        btn_streamstart = (Button) findViewById(R.id.btn_streamstart);
        btn_streamstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Go Live 버튼 클릭시 방송 시작
                Intent intent=new Intent(getApplicationContext(), Streaming_Acticity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}
