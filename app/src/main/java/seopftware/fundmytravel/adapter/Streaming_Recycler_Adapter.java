package seopftware.fundmytravel.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import seopftware.fundmytravel.R;
import seopftware.fundmytravel.dataset.Streaming_Item;

import static seopftware.fundmytravel.util.MyApp.SERVER_URL;
import static seopftware.fundmytravel.util.MyApp.USER_ID;

/**
 * 스트리밍 중 채팅 데이터 추가를 위한 Adapter
 * @author 김인섭
 * @version 1.0.0
 * @since 2018-01-06 오전 11:47
 * @class comment
 *   이 어댑터는 스트리밍 중 채팅 데이터를 뿌려 주기 위해 만들어졌습니다.
**/

public class Streaming_Recycler_Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "all_"+Streaming_Recycler_Adapter.class;

    // View Type 지정을 위한 변수
    private static final int ENTRANCE = 0; // 입장
    private static final int MESSAGE= 1; // 메세지
    private static final int IMAGE= 2; // 이미지 전송
    private static final int STAR= 3; // 별풍선

    ArrayList<Streaming_Item> itemlist = new ArrayList<Streaming_Item>(); // 아이템을 담기 위한 객체가 담겨 있는 ArrayList
    Context context;

    public Streaming_Recycler_Adapter() {

    }

    @Override
    public int getItemViewType(int position) {
        int viewType = itemlist.get(position).getStreaming_type();
        Log.d(TAG, "mItems.get(position).getHome_type: " + itemlist.get(position).getStreaming_type());

        switch (viewType) {
            // 입장 또는 나갔을 때 띄우는 view type
            case ENTRANCE:
                viewType =0;
                break;

            // 메세지를 보냈을 때 띄우는 view type
            case MESSAGE:
                viewType =1;
                break;

            // 이미지를 보냈을 때 띄우는 view type
            case IMAGE:
                viewType =2;
                break;

            // 별풍선을 보냈을 때 띄우는 view type
            case STAR:
                viewType =3;
                break;
        }
        return viewType;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = null;
        RecyclerView.ViewHolder viewHolder = null;
        context=parent.getContext();

        Log.d(TAG, "viewType : " + viewType);

        switch (viewType) {
            case ENTRANCE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_streaming_entrance, parent, false);
                viewHolder = new ItemOneViewHolder(view);
                break;

            case MESSAGE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_streaming_message, parent, false);
                viewHolder = new ItemTwoViewHolder(view);
                break;
        }


        return viewHolder;
    }


    // 데이터를 binding 시켜주는 함수
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Streaming_Item listviewItem = itemlist.get(position);

        // 1. 입장 또는 나갔을 때 View
        if(holder instanceof ItemOneViewHolder) {

            ((ItemOneViewHolder)holder).tv_Id.setText(listviewItem.getStreaming_user_id()); // 고유 Id
            ((ItemOneViewHolder)holder).tv_Entrance.setText(listviewItem.getStreaming_user_message()); // 닉네임
//            ((ItemOneViewHolder)holder).tv_Message.setText(listviewItem.getHome_message()); //메세지

        }

        // 2. 메세지 보냈을 때 View
        else if(holder instanceof ItemTwoViewHolder) {

            ((ItemTwoViewHolder)holder).tv_Id.setText(listviewItem.getStreaming_user_id()); // 고유 Id
            ((ItemTwoViewHolder)holder).tv_Name.setText(listviewItem.getStreaming_user_nickname()); // 닉네임
            ((ItemTwoViewHolder)holder).tv_Message.setText(listviewItem.getStreaming_user_message()); // 메세지
            Glide.with(context)
                    .load(listviewItem.getStreameing_image_profile())
                    .bitmapTransform(new CropCircleTransformation(context))
                    .into(((ItemTwoViewHolder)holder).iv_Profile); // 유저 이미지


            if(USER_ID.equals("50")) {
                ((ItemTwoViewHolder)holder).iv_Roommaker.setVisibility(View.VISIBLE);
                ((ItemTwoViewHolder)holder).iv_Roommaker.bringToFront();

            } else {
                ((ItemTwoViewHolder)holder).iv_Roommaker.setVisibility(View.INVISIBLE);

            }
        }


    }


    // =========================================================================================================
    // 커스텀 뷰홀더 (item layout에 존재하는 위젯들을 바인딩함.)
    // 1. ENTRANCE
    class ItemOneViewHolder extends RecyclerView.ViewHolder {
        TextView tv_Id, tv_Entrance;

        public ItemOneViewHolder(View itemView) {
            super(itemView);

            tv_Id= (TextView) itemView.findViewById(R.id.tv_Id);
            tv_Entrance= (TextView) itemView.findViewById(R.id.tv_Entrance);

        }
    }

    // 2. MESSAGE
    class ItemTwoViewHolder extends RecyclerView.ViewHolder {
        LinearLayout Linear_up, Linear_down;
        TextView tv_Id, tv_Name, tv_Message;
        ImageView iv_Profile, iv_Roommaker;

        public ItemTwoViewHolder(View itemView) {
            super(itemView);

            tv_Id= (TextView) itemView.findViewById(R.id.tv_Id);
            tv_Name= (TextView) itemView.findViewById(R.id.tv_Name);
            tv_Message= (TextView) itemView.findViewById(R.id.tv_Message);
            iv_Profile= (ImageView) itemView.findViewById(R.id.iv_Profile);
            iv_Roommaker= (ImageView) itemView.findViewById(R.id.iv_Roommaker);

        }
    }
    // =========================================================================================================


    // =========================================================================================================
    // List Item에 아이템들을 추가하는 함수들
    // ENTRANCE 메세지 추가하는 곳
    public void addEntrance(String message) {
        Streaming_Item item = new Streaming_Item();

        item.setStreaming_type(ENTRANCE);
        item.setStreaming_user_message(message);
        itemlist.add(item);
    }

    // MESSAGE 메세지 추가하는 곳
    public void addMessage(String name, String message, String profile) {
        Streaming_Item item = new Streaming_Item();

        item.setStreaming_type(MESSAGE);
        item.setStreaming_user_nickname(name);
        item.setStreaming_user_message(message);
        item.setStreameing_image_profile(SERVER_URL + "photo/"+profile);

        itemlist.add(item);
    }
    // =========================================================================================================

    @Override
    public int getItemCount() {
        return itemlist.size();
    }
}
