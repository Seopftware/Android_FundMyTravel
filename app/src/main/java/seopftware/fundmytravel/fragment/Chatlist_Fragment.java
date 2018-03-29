package seopftware.fundmytravel.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.marlonlom.utilities.timeago.TimeAgo;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import seopftware.fundmytravel.R;
import seopftware.fundmytravel.adapter.ChatRoomlist_Recycler_Adapter;
import seopftware.fundmytravel.dataset.ChatRoomlist_Item;
import seopftware.fundmytravel.dataset.Parsing;
import seopftware.fundmytravel.function.etc.RecyclerItemClickListener;
import seopftware.fundmytravel.function.retrofit.HttpService;
import seopftware.fundmytravel.function.retrofit.RetrofitClient;

import static seopftware.fundmytravel.function.MyApp.USER_ID;

@SuppressLint("ValidFragment")
public class Chatlist_Fragment extends Fragment {

    private static final String TAG = "all_"+"Chatlist_Fragment";

    // pull to refresh
    SwipeRefreshLayout mSwipeRefresh;

    // Recycler View 관련 변수
    RecyclerView recyclerView;
    ChatRoomlist_Recycler_Adapter adapter;
    ArrayList<ChatRoomlist_Item> recycler_itemlist;
    ChatRoomlist_Item recycler_item;


    public static Chatlist_Fragment getInstance() {
        Chatlist_Fragment chatlist_fragment = new Chatlist_Fragment();
        return chatlist_fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_chatlist, null);

        // Recycler view 선언
        recyclerView = (RecyclerView) v.findViewById(R.id.chatlist_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler_itemlist = new ArrayList<ChatRoomlist_Item>();

        recycler_item = new ChatRoomlist_Item();
        adapter = new ChatRoomlist_Recycler_Adapter(recycler_itemlist);
        recyclerView.setAdapter(adapter);

        // Recycler View click Listener
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                String message_status = recycler_itemlist.get(position).getMessage_status();

                if(message_status.equals("Receive")) {
                    // 이미지 메세지 오픈
                }
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));


        // Pull to Refresh
        mSwipeRefresh = (SwipeRefreshLayout) v.findViewById(R.id.pulltoRefresh);
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // 동작이 완료 되면 새로고침 아이콘 없애기
                mSwipeRefresh.setRefreshing(false);

                recycler_itemlist.clear();
                getChatRoomList();
            }
        });

        // 채팅 목록 받아오기
        getChatRoomList();

        return v;
    }

    // HTTP 통신
    // 보내는 값: 유저 ID
    // 받는 값: 채팅방 리스트
    // PHP: select/find_chatlist.php
    private void getChatRoomList() {

        Retrofit retrofit = RetrofitClient.getClient();
        HttpService httpService = retrofit.create(HttpService.class);
        Call<Parsing> comment = httpService.get_chatlist(USER_ID);
        comment.enqueue(new Callback<Parsing>() {
            @Override
            public void onResponse(Call<Parsing> call, Response<Parsing> response) {
                Parsing parsing = response.body();


                for (int i = 0; i<parsing.getChatRoomCount(); i++) {

                    String userId = parsing.getChatroomlist().get(i).getUserId();
                    String receiverId = parsing.getChatroomlist().get(i).getReceiverId();
                    String messageTime = parsing.getChatroomlist().get(i).getMessageTime();
                    String messageStatus = parsing.getChatroomlist().get(i).getMessageStatus();
                    String receiverName = parsing.getChatroomlist().get(i).getReceiverName();

                    // TimeAgo Library를 활용한 남은 시간 표현
                    Long update_time = Long.valueOf(messageTime);
                    String text = TimeAgo.using(update_time); // 1minutes Ago

                    adapter.addChatroom(Integer.parseInt(userId), Integer.parseInt(receiverId), receiverName, text, messageStatus);

                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<Parsing> call, Throwable t) {

            }
        });

    }



}