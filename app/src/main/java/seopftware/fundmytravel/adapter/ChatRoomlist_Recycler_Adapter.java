package seopftware.fundmytravel.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import seopftware.fundmytravel.R;
import seopftware.fundmytravel.dataset.ChatRoomlist_Item;

/**
 * 채팅방 목록 데이터 추가를 위한 Adapter
 * @author 김인섭
 * @version 1.0.0
 * @since 2018-03-28 오후 14:04
 * @class comment
 *   이 어댑터는 채팅 목록을 뿌려주기 위해 만들어졌습니다.
**/

public class ChatRoomlist_Recycler_Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "all_"+ChatRoomlist_Recycler_Adapter.class;

    // View Type 지정을 위한 변수
    private static final int ENTRANCE = 0; // 입장

    ArrayList<ChatRoomlist_Item> itemlist; // 아이템을 담기 위한 객체가 담겨 있는 ArrayList
    Context context;

    public ChatRoomlist_Recycler_Adapter(ArrayList<ChatRoomlist_Item> items) {
        itemlist =items;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = null;
        RecyclerView.ViewHolder viewHolder = null;
        context=parent.getContext();

        // Item View에 layout inflater
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chatroomlist, parent, false);
        viewHolder = new ItemOneViewHolder(view);

        return viewHolder;
    }


    // 데이터를 binding 시켜주는 함수
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ChatRoomlist_Item listviewItem = itemlist.get(position);

        if(holder instanceof ItemOneViewHolder) {

            ((ItemOneViewHolder)holder).tv_Name.setText(listviewItem.getUser_name()); // 유저 이름
            ((ItemOneViewHolder)holder).tv_Date.setText(listviewItem.getMessage_time()); // 메세지 시간


            // 방송 상태를 가져오는 변수
            String message_status = listviewItem.getMessage_status(); // 받는 값: delivered, received, opened

            // 만약 방송 중이면
            if(message_status.equals("delivered")) {

                Log.d(TAG, "DELIVERED");
                ((ItemOneViewHolder)holder).tv_MessageStatus.setText("delivered"); // 메세지 전송
                ((ItemOneViewHolder)holder).iv_Status.setImageResource(R.drawable.snapchat_sent); // 메세지 전송 아이콘

            }

            else if(message_status.equals("received")) {

                Log.d(TAG, "RECEIVED");
                ((ItemOneViewHolder)holder).tv_MessageStatus.setText("received"); // 메세지 전송
                ((ItemOneViewHolder)holder).iv_Status.setImageResource(R.drawable.snapchat_icon); // 메세지 전송 아이콘
            }

            else if(message_status.equals("opened")) {

                Log.d(TAG, "OPENED");
                ((ItemOneViewHolder)holder).tv_MessageStatus.setText("opened"); // 메세지 전송
                ((ItemOneViewHolder)holder).iv_Status.setImageResource(R.drawable.snapchat_icon_opened); // 메세지 전송 아이콘
            }

        }
    } // onBindViewHolder finish


    // =========================================================================================================
    // 커스텀 뷰홀더 (item layout에 존재하는 위젯들을 바인딩함.)

    // 방송 리스트 기본 홀더
    class ItemOneViewHolder extends RecyclerView.ViewHolder {

        TextView tv_Name; // 유저 닉네임
        TextView tv_Date; // 메세지를 받은 날짜
        TextView tv_MessageStatus; // 메세지 상태 (received, opened)

        ImageView iv_Status; // 메세지 상태 아이콘


        public ItemOneViewHolder(View itemView) {
            super(itemView);

            tv_Name= (TextView) itemView.findViewById(R.id.tv_Name); // 유저 닉네임
            tv_Date= (TextView) itemView.findViewById(R.id.tv_Date); // 메세지를 받은(보낸) 날짜
            tv_MessageStatus= (TextView) itemView.findViewById(R.id.tv_MessageStatus); // 메세지 상태

            iv_Status= (ImageView) itemView.findViewById(R.id.iv_Status);

        }
    }
    // =========================================================================================================


    // =========================================================================================================
    // List Item에 아이템들을 추가하는 함수들
    // 방송 룸 추가
    public void addChatroom(int user_key, int receive_id, String user_name, String message_time, String message_status) {
        // 방 고유번호, 참가자 수, 방송 제목, 방송 태그, 스트리머 이름, 방송 메인 사진, 방송 상태

        ChatRoomlist_Item item = new ChatRoomlist_Item();

        item.setUser_key(user_key);
        item.setReceive_id(receive_id);
        item.setUser_name(user_name);
        item.setMessage_time(message_time);
        item.setMessage_status(message_status);

        itemlist.add(item);
    }
    // =========================================================================================================

    @Override
    public int getItemCount() {
        return itemlist.size();
    }
}
