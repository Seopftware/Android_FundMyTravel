package seopftware.fundmytravel.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import seopftware.fundmytravel.R;
import seopftware.fundmytravel.dataset.Roomlist_Item;

import static seopftware.fundmytravel.function.MyApp.SERVER_URL;

/**
 * 방송 목록 데이터 추가를 위한 Adapter
 * @author 김인섭
 * @version 1.0.0
 * @since 2018-01-23 오전 11:47
 * @class comment
 *   이 어댑터는 방송 목록을 뿌려주기 위해 만들어졌습니다.
**/

public class Roomlist_Recycler_Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "all_"+Roomlist_Recycler_Adapter.class;

    // View Type 지정을 위한 변수
    private static final int ENTRANCE = 0; // 입장

    ArrayList<Roomlist_Item> itemlist; // 아이템을 담기 위한 객체가 담겨 있는 ArrayList
    Context context;

    public Roomlist_Recycler_Adapter(ArrayList<Roomlist_Item> items) {
        itemlist =items;

    }

/*    @Override
    public int getItemViewType(int position) {
        int viewType = itemlist.get(position).getStreaming_type();
        Log.d(TAG, "mItems.get(position).getHome_type: " + itemlist.get(position).getStreaming_type());

        switch (viewType) {
            // 입장 또는 나갔을 때 띄우는 view type
            case ENTRANCE:
                viewType =0;
                break;
        }
        return viewType;
    }*/

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = null;
        RecyclerView.ViewHolder viewHolder = null;
        context=parent.getContext();

        // Item View에 layout inflater
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_streaming_roomlist, parent, false);
        viewHolder = new ItemOneViewHolder(view);

/*        Log.d(TAG, "viewType : " + viewType);

        switch (viewType) {
            case ENTRANCE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_streaming_entrance, parent, false);
                viewHolder = new ItemOneViewHolder(view);
                break;

            case MESSAGE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_streaming_message, parent, false);
                viewHolder = new ItemTwoViewHolder(view);
                break;
        }*/


        return viewHolder;
    }


    // 데이터를 binding 시켜주는 함수
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Roomlist_Item listviewItem = itemlist.get(position);

        // 1. 입장 또는 나갔을 때 View
        if(holder instanceof ItemOneViewHolder) {

            ((ItemOneViewHolder)holder).tv_roomid.setText(listviewItem.getRoom_id()); // 방송 고유 번호
            ((ItemOneViewHolder)holder).tv_numpeople.setText(Integer.toString(listviewItem.getRoom_numpeople())); // 방송 시청자 수(int형 ㅡ>String으로 바꿔줘야함)

            ((ItemOneViewHolder)holder).tv_title.setText(listviewItem.getRoom_name_title()); // 방송 제목
            ((ItemOneViewHolder)holder).tv_tag.setText(listviewItem.getRoom_name_tag()); // 방송 태그
            ((ItemOneViewHolder)holder).tv_nickname.setText(listviewItem.getRoom_name_streamer()); // 방송하는 사람 이름

            Glide.with(context)
                    .load(listviewItem.getRoom_image_path())
//                    .bitmapTransform(new CropCircleTransformation(context))
                    .into(((ItemOneViewHolder)holder).iv_streampic); // 방송 대문 이미지


            // 방송 상태를 가져오는 변수
            String room_status = listviewItem.getRoom_status(); // 받는 값: true(방송 중), false(방송 종료->VOD)

            // 만약 방송 중이면
            if(room_status.equals(true)) {

                ((ItemOneViewHolder)holder).btn_streamstatus.setText("LIVE"); // 방송 중
                ((ItemOneViewHolder)holder).btn_streamstatus.setBackgroundColor(Color.rgb(224,73,46)); // 방송 중 (빨간색)

            }

            // 만약 방송 중이 아니면
            else if(room_status.equals(false)) {

                ((ItemOneViewHolder)holder).btn_streamstatus.setText("VOD"); // 방송 종료
                ((ItemOneViewHolder)holder).btn_streamstatus.setBackgroundColor(Color.rgb(189,189,189)); // 방송 중 (빨간색)

            }
        }
    } // onBindViewHolder finish


    // =========================================================================================================
    // 커스텀 뷰홀더 (item layout에 존재하는 위젯들을 바인딩함.)

    // 방송 리스트 기본 홀더
    class ItemOneViewHolder extends RecyclerView.ViewHolder {
        TextView tv_roomid, tv_numpeople; // 방 고유번호, 방 참여자 수
        TextView tv_title, tv_tag, tv_nickname; // 방 제목, 방 태그, 스트리머 이름
        ImageView iv_streampic; // 방송의 대문 사진
        Button btn_streamstatus; // 방송 상태를 나타내는 버튼

        public ItemOneViewHolder(View itemView) {
            super(itemView);

            tv_roomid= (TextView) itemView.findViewById(R.id.tv_roomid);
            tv_numpeople= (TextView) itemView.findViewById(R.id.tv_numpeople);
            tv_title= (TextView) itemView.findViewById(R.id.tv_title);
            tv_tag= (TextView) itemView.findViewById(R.id.tv_tag);
            tv_nickname= (TextView) itemView.findViewById(R.id.tv_nickname);
            iv_streampic= (ImageView) itemView.findViewById(R.id.iv_streampic);
            btn_streamstatus= (Button) itemView.findViewById(R.id.btn_streamstatus);

        }
    }
    // =========================================================================================================


    // =========================================================================================================
    // List Item에 아이템들을 추가하는 함수들
    // 방송 룸 추가
    public void addRoom(String room_id, int room_numpeople, String room_name_title, String room_name_tag,
                        String room_name_streamer, String room_image_path, String room_status) {
        // 방 고유번호, 참가자 수, 방송 제목, 방송 태그, 스트리머 이름, 방송 메인 사진, 방송 상태

        Roomlist_Item item = new Roomlist_Item();

        item.setRoom_id(room_id);
        item.setRoom_numpeople(room_numpeople);
        item.setRoom_name_title(room_name_title);
        item.setRoom_name_tag(room_name_tag);
        item.setRoom_name_streamer(room_name_streamer);
        item.setRoom_image_path(SERVER_URL + "/streaming_room/" + room_image_path);
        item.setRoom_status(room_status);

        itemlist.add(item);
    }
    // =========================================================================================================

    @Override
    public int getItemCount() {
        return itemlist.size();
    }
}
