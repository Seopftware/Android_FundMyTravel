package seopftware.fundmytravel.function.chatting;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import seopftware.fundmytravel.R;

public class Chatroom_Activity extends AppCompatActivity {

    private static final String TAG = "all_"+"Chatroom_Activity";

    // layout
    DrawerLayout layout_drawer;
    LinearLayout nav_linear;
    ActionBar actionBar;

    // mainlayout
    Button btn_send;
    ImageButton ibtn_transfer;
    EditText et_chatinput;

    // linear layout
    Button btn_invite, btn_out;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);

        // Toolbar 작업
        Toolbar toolbar = (Toolbar) findViewById(R.id.chatroom_toolbar);
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setTitle("numpeople");

        // main layout
        layout_drawer=(DrawerLayout) findViewById(R.id.chatroom_layout_drawer);
        btn_send= (Button) findViewById(R.id.chatroom_btn_send); // 메세지 보내는 버튼
        et_chatinput= (EditText) findViewById(R.id.chatroom_et_chatinput); // 메세지 입력하는 곳
        ibtn_transfer= (ImageButton) findViewById(R.id.chatroom_ibtn_transfer); // 이미지,파일 전송 버튼

        // linear layout
        nav_linear= (LinearLayout) findViewById(R.id.chatroom_nav_linear);
        btn_invite= (Button) findViewById(R.id.chatroom_btn_invite);
        btn_out= (Button) findViewById(R.id.chatroom_btn_out);




//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, layout_drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        layout_drawer.addDrawerListener(toggle);
//        toggle.syncState();

    }




    //=========================================================================================================
    // 네비게이션 드로어 함수 부분 (채팅방 정보 표시)
    //=========================================================================================================
    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
            switch (position) {

                case 0:
                    Log.d(TAG, "position 0 : " + position);
                    break;

                case 1:
                    Log.d(TAG, "position 1 : " + position);
                    break;

                case 2:
                    Log.d(TAG, "position 2 : " + position);
                    break;
            }

//            drawer.closeDrawer(linearLayout);


        }

    }

    // 뷰로 inflate 시켜주기
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chatroom, menu);
        return true;
    }



    //액션바 백키 버튼 구현
    // 메뉴에 해당하는 아이템들 클릭 시 호출되는 함수
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                return true;
            }

            case R.id.chatroom_menu: {
                Toast.makeText(getApplicationContext(), "open", Toast.LENGTH_SHORT).show();
                layout_drawer.openDrawer(nav_linear);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (layout_drawer.isDrawerOpen(nav_linear)) {
            layout_drawer.closeDrawer(nav_linear);
        } else {
            super.onBackPressed();
        }
    }


    //=========================================================================================================
}
