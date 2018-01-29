//package seopftware.fundmytravel.function.googlemap;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//
//import seopftware.fundmytravel.R;
//
//public class map extends AppCompatActivity implements View.OnClickListener {
//
//    ViewGroup mListView;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_map);
//
//        mListView = (ViewGroup) findViewById(R.id.list);
//
//        addDemo("Clustering: Custom Look", CustomMarkerClusteringDemoActivity.class);
//
//
//    }
//
//    private void addDemo(String demoName, Class<? extends Activity> activityClass) {
//        Button b = new Button(this);
//        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        b.setLayoutParams(layoutParams);
//        b.setText(demoName);
//        b.setTag(activityClass);
//        b.setOnClickListener(this);
//        mListView.addView(b);
//    }
//
//    @Override
//    public void onClick(View view) {
//        Class activityClass = (Class) view.getTag();
//        startActivity(new Intent(this, activityClass));
//    }
//}
