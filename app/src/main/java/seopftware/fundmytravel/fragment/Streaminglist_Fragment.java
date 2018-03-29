package seopftware.fundmytravel.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import seopftware.fundmytravel.R;
import seopftware.fundmytravel.adapter.Roomlist_Recycler_Adapter;
import seopftware.fundmytravel.dataset.Parsing;
import seopftware.fundmytravel.dataset.Roomlist_Item;
import seopftware.fundmytravel.function.etc.RecyclerItemClickListener;
import seopftware.fundmytravel.function.retrofit.HttpService;
import seopftware.fundmytravel.function.retrofit.RetrofitClient;
import seopftware.fundmytravel.function.streaming.BeforeStreaming_Activity;
import seopftware.fundmytravel.function.streaming.PlayerStreaming_Activity;
import seopftware.fundmytravel.function.streaming.Vodplayer_Activity;

@SuppressLint("ValidFragment")
public class Streaminglist_Fragment extends Fragment {

    private static final String TAG = "all_" + Streaminglist_Fragment.class;

    static final int REQUEST_ALBUM = 2002;

    // floating button
    com.melnykov.fab.FloatingActionButton fab;

    // Recycler View 관련 변수
    RecyclerView recyclerView;
    Roomlist_Recycler_Adapter adapter;
    ArrayList<Roomlist_Item> recycler_itemlist;
    Roomlist_Item recycler_item;

    // pull to refresh
    SwipeRefreshLayout mSwipeRefresh;


    public static Streaminglist_Fragment getInstance() {
        Streaminglist_Fragment home_fragment = new Streaminglist_Fragment();
        return home_fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        getActivity().setTitle(Html.fromHtml("<font color=\"red\">" + "streaming" + "</font>"));
//        getActivity().getResources().getColor(android.R.color.white);

        View v = inflater.inflate(R.layout.fragment_streaminglist, null);


        // Recycler view 선언
        recyclerView = (RecyclerView) v.findViewById(R.id.roomlist_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler_itemlist = new ArrayList<Roomlist_Item>();

        recycler_item = new Roomlist_Item();
        adapter = new Roomlist_Recycler_Adapter(recycler_itemlist);
        recyclerView.setAdapter(adapter);

        // Recycler View click Listener
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                // 클릭한 방이 현재 생방송 중인지 아닌지 파악
                String LiveOrVod = recycler_itemlist.get(position).getRoom_status();
                LiveOrVod =  LiveOrVod.replace("\"", "");
                Log.d(TAG, "현재 룸은 생방중인가? : " + LiveOrVod);

                // 방 번호
                String room_id = recycler_itemlist.get(position).getRoom_id();
                room_id =  room_id.replace("\"", "");
                Log.d(TAG, "현재 룸 ID 값 : " + room_id);

                String streamer_name = recycler_itemlist.get(position).getRoom_name_streamer();
                Log.d(TAG, "현재 룸 Streamer 이름 값 : " + streamer_name);

                // 만약 생방송 중이라면 ExoPlayer가 있는 액티비티로 이동
                if(LiveOrVod.equals("LIVE")) {

                    Intent intent = new Intent(getContext(), PlayerStreaming_Activity.class);
                    intent.putExtra("room_id", room_id);
                    intent.putExtra("streamer_name", streamer_name);
                    startActivity(intent);
                }

                // 만약 생방송이 끝난 상태라면 VideoView가 있는 액티비티로 이동(VOD 재생 화면으로 이동)
                else if(LiveOrVod.equals("VOD")){

                    Intent intent = new Intent(getContext(), Vodplayer_Activity.class);
                    intent.putExtra("room_id", room_id);
                    startActivity(intent);
                }

            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));


        // Floating Action Button
        fab = (com.melnykov.fab.FloatingActionButton) v.findViewById(R.id.floatingActionButton);
        fab.attachToRecyclerView(recyclerView);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 앨범 호출
                showFileChoose();
            }
        });


        // Pull to Refresh
        mSwipeRefresh = (SwipeRefreshLayout) v.findViewById(R.id.pulltoRefresh);
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // 동작이 완료 되면 새로고침 아이콘 없애기
                mSwipeRefresh.setRefreshing(false);

                recycler_itemlist.clear();
                getRoomList();
            }
        });


        // 방 목록을 parsing 받아오는 클래스
        getRoomList();

        return v;
    }

    // Http 통신 하는 곳
    // 보내는 값: 없음
    // 받는 값: 방송 리스트 (streaming_roomlist)
    // PHP: select/find_roomlist.php
    private void getRoomList() {
        Retrofit retrofit = RetrofitClient.getClient();
        HttpService httpService = retrofit.create(HttpService.class);
        Call<Parsing> comment = httpService.get_roomlist(1); // 변수를 하나도 안 보내면 error 메세지 뜸. 그래서 1을 보냄
        comment.enqueue(new Callback<Parsing>() {
            @Override
            public void onResponse(Call<Parsing> call, Response<Parsing> response) {
                Parsing parsing = response.body();

                Log.d(TAG, "Room List 아이템 갯수 : " + parsing.getRoomCount());

                for (int i = 0; i<parsing.getRoomCount(); i++) {

                    // 1
                    String room_id = parsing.getRoomlist().get(i).getRoomId();

                    // 2
                    int room_numpeople = parsing.getRoomlist().get(i).getRoomNumpeople();

                    // 3
                    String room_name_title1 = parsing.getRoomlist().get(i).getRoomNameTitle();
                    String room_name_title = room_name_title1.replaceAll("\"", "");

                    // 4
                    String room_name_tag1 = parsing.getRoomlist().get(i).getRoomNameTag();
                    String room_name_tag = room_name_tag1.replaceAll("\"", "");


                    // 5
                    String room_name_streamer1 = parsing.getRoomlist().get(i).getRoomNameStreamer();
                    String room_name_streamer = room_name_streamer1.replaceAll("\"", "");


                    // 6
                    String room_image_path = parsing.getRoomlist().get(i).getRoomImagePath();

                    // 7
                    String room_status = parsing.getRoomlist().get(i).getRoomStatus();
//                    Log.d(TAG, "room_status : " + room_status);

                    adapter.addRoom(room_id, room_numpeople, room_name_title, room_name_tag, room_name_streamer, room_image_path, room_status);

                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<Parsing> call, Throwable t) {

            }
        });

    }


    // 앨범 호출 함수
    private void showFileChoose() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_ALBUM);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_ALBUM:

                Uri album_uri = data.getData();

                if (album_uri != null) {
                    Intent intent = new Intent(getContext(), BeforeStreaming_Activity.class);
                    intent.putExtra("photoUri", album_uri);
                    startActivity(intent);
                    break;
                }
        }
    }

}
























//                // 방송 시작
//                if (hasPermissions(getContext(), PERMISSIONS)) {
//                    ActivityLink link = activities.get(0);
//                    int minSdk = link.getMinSdk();
//                    if (Build.VERSION.SDK_INT >= minSdk) {
//                        startActivity(link.getIntent());
//                    } else {
//                        showMinSdkError(minSdk);
//                    }
//                } else {
//                    showPermissionsErrorAndRequest();
//                }