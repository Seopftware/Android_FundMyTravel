package seopftware.fundmytravel.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import seopftware.fundmytravel.R;
import seopftware.fundmytravel.dataset.Streaming_Item;

import static seopftware.fundmytravel.function.MyApp.SERVER_URL;
import static seopftware.fundmytravel.function.MyApp.USER_ID;

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
    private static final int VOD_MESSAGE= 4; // 별풍선

    ArrayList<Streaming_Item> itemlist = new ArrayList<Streaming_Item>(); // 아이템을 담기 위한 객체가 담겨 있는 ArrayList
    Context context;

    public Streaming_Recycler_Adapter() {
    }

    public Streaming_Recycler_Adapter(ArrayList<Streaming_Item> itemlist) {
        this.itemlist = itemlist;
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

            // 메세지를 보냈을 때 띄우는 view type
            case VOD_MESSAGE:
                viewType =4;
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

            case VOD_MESSAGE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vod_message, parent, false);
                viewHolder = new ItemThreeViewHolder(view);
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


            // TODO
            // 만약 유저가 방장 이면 방패 모양 띄워주기 (디버깅 필요)
            if(USER_ID == 50) {
                ((ItemTwoViewHolder)holder).iv_Roommaker.setVisibility(View.VISIBLE);
                ((ItemTwoViewHolder)holder).iv_Roommaker.bringToFront();

            } else {
                ((ItemTwoViewHolder)holder).iv_Roommaker.setVisibility(View.INVISIBLE);

            }
        }

        else if(holder instanceof ItemThreeViewHolder) {

            ((ItemThreeViewHolder)holder).tv_vod_name.setText(listviewItem.getStreaming_user_nickname()); // 닉네임
            ((ItemThreeViewHolder)holder).tv_vod_message.setText(listviewItem.getStreaming_user_message()); // 메세지
            ((ItemThreeViewHolder)holder).tv_vod_time.setText(listviewItem.getStreaming_message_time()); // 메세지

            Glide.with(context)
                    .load(listviewItem.getStreameing_image_profile())
                    .bitmapTransform(new CropCircleTransformation(context))
                    .into(((ItemThreeViewHolder)holder).iv_Profile); // 유저 이미지


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

    // 2. STREAMING MESSAGE
    class ItemTwoViewHolder extends RecyclerView.ViewHolder {
        TextView tv_Id, tv_Name, tv_Message;
        ImageView iv_Profile, iv_Roommaker;

        public ItemTwoViewHolder(View itemView) {
            super(itemView);

            tv_Id= (TextView) itemView.findViewById(R.id.tv_Id);
            tv_Name= (TextView) itemView.findViewById(R.id.tv_name);
            tv_Message= (TextView) itemView.findViewById(R.id.tv_Message);
            iv_Profile= (ImageView) itemView.findViewById(R.id.iv_streampic);
            iv_Roommaker= (ImageView) itemView.findViewById(R.id.iv_Roommaker);

        }
    }


    // 3. VOD MESSAGE
    class ItemThreeViewHolder extends RecyclerView.ViewHolder {
        TextView tv_vod_name, tv_vod_message, tv_vod_time;
        ImageView iv_Profile;

        public ItemThreeViewHolder(View itemView) {
            super(itemView);

            tv_vod_name= (TextView) itemView.findViewById(R.id.tv_vod_name);
            tv_vod_message= (TextView) itemView.findViewById(R.id.tv_vod_message);
            tv_vod_time= (TextView) itemView.findViewById(R.id.tv_vod_time);
            iv_Profile= (ImageView) itemView.findViewById(R.id.iv_streampic);

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

    // MESSAGE 메세지 추가하는 곳
    public void addVODMessage(String name, String message, String profile, String time) {
        Streaming_Item item = new Streaming_Item();

        item.setStreaming_type(VOD_MESSAGE);
        item.setStreaming_user_nickname(name);
        item.setStreaming_user_message(message);
        item.setStreameing_image_profile(SERVER_URL + "photo/"+profile);
        item.setStreaming_message_time(time);


        itemlist.add(item);
    }
    // =========================================================================================================

    @Override
    public int getItemCount() {
        return itemlist.size();
    }
}
