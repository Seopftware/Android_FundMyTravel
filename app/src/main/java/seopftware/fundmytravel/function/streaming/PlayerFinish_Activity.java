package seopftware.fundmytravel.function.streaming;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import jp.wasabeef.glide.transformations.BlurTransformation;
import seopftware.fundmytravel.R;

// 스트리머 방송 종료시 시청자에게 나타나는 액티비티
public class PlayerFinish_Activity extends AppCompatActivity {

    ImageView iv_background;
    TextView tv_finish_message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_finish);

        tv_finish_message= (TextView) findViewById(R.id.tv_finish_message);
        tv_finish_message.bringToFront();

        iv_background = (ImageView) findViewById(R.id.iv_background);
        Glide.with(getApplicationContext())
                .load(R.drawable.videofinish1)
                .bitmapTransform(new BlurTransformation(getApplicationContext(), 50, 2)) // Glide Blur 효과
                .into(iv_background);
    }
}
