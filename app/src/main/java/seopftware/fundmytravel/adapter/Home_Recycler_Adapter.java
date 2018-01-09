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
import seopftware.fundmytravel.dataset.Home_Recycler_Item;

import static seopftware.fundmytravel.function.MyApp.SERVER_URL;

/**
 * 홈 화면 친구 리스트를 뿌려주기 위한 Adapter
 * @author 김인섭
 * @version 1.0.0
 * @since 2018-01-06 오전 11:41
 * @class comment
 *   이 클래스는 친구 목록을 뿌려주기 위한 Adapter로 만들어졌습니다.
**/

public class Home_Recycler_Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "all_"+Home_Recycler_Adapter.class;
    private static final int ME = 0; // 나일 경우
    private static final int FRIENDS = 1; // 친구일 경우

    ArrayList<Home_Recycler_Item> itemlist; // 데이터 바구니
    Context context;

    public Home_Recycler_Adapter(ArrayList<Home_Recycler_Item> items) {
        itemlist=items;

    }

    @Override
    public int getItemViewType(int position) {
        int viewType = itemlist.get(position).getHome_type();
        Log.d(TAG, "itemlist.get(position).getHome_type: " + itemlist.get(position).getHome_type());

        switch (viewType) {
            case ME:
                viewType =0;
                break;

            case FRIENDS:
                viewType =1;
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
            case ME:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_friendlist, parent, false);
                viewHolder = new ItemOneViewHolder(view);
                break;

            case FRIENDS:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_friendlist, parent, false);
                viewHolder = new ItemTwoViewHolder(view);
                break;
        }


        return viewHolder;
    }



    // 데이터를 binding 시켜주는 함수
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Home_Recycler_Item listviewItem = itemlist.get(position);

        // 1. 본인인 경우
        if(holder instanceof ItemOneViewHolder) {

            ((ItemOneViewHolder)holder).tv_Id.setText(""+listviewItem.getHome_id()); // 고유 Id
            ((ItemOneViewHolder)holder).tv_Name.setText(listviewItem.getHome_nickname()); // 닉네임
            ((ItemOneViewHolder)holder).tv_Message.setText(listviewItem.getHome_message()); // 상태 메세지
            Glide.with(context)
                    .load(listviewItem.getHome_profile())
                    .bitmapTransform(new CropCircleTransformation(context))
                    .into(((ItemOneViewHolder)holder).iv_Profile); // 원형 프로필

            // Glide.with(context).load(itemlist.get(position).getHome_profile()).into(((ItemOneViewHolder)holder).iv_Profile); // 사각형 프로필


        }

        // 2. 친구인 경우
        else if(holder instanceof ItemTwoViewHolder) {

            ((ItemTwoViewHolder)holder).tv_Id.setText(""+listviewItem.getHome_id()); // 고유 Id
            ((ItemTwoViewHolder)holder).tv_Name.setText(listviewItem.getHome_nickname()); // 닉네임
            ((ItemTwoViewHolder)holder).tv_Message.setText(listviewItem.getHome_message()); // 상태 메세지
            Glide.with(context)
                    .load(listviewItem.getHome_profile())
                    .bitmapTransform(new CropCircleTransformation(context))
                    .into(((ItemTwoViewHolder)holder).iv_Profile);
        }

    }

    // =========================================================================================================
    // 커스텀 뷰홀더 (item layout에 존재하는 위젯들을 바인딩함.)
    // 1. 본인인 경우
    class ItemOneViewHolder extends RecyclerView.ViewHolder {
        TextView tv_Id, tv_Name, tv_Message;
        ImageView iv_Profile;

        public ItemOneViewHolder(View itemView) {
            super(itemView);

            tv_Id= (TextView) itemView.findViewById(R.id.tv_Id);
            tv_Name= (TextView) itemView.findViewById(R.id.tv_name);
            tv_Message= (TextView) itemView.findViewById(R.id.tv_Message);
            iv_Profile= (ImageView) itemView.findViewById(R.id.iv_profile);

        }
    }

    // 2. 친구인 경우
    class ItemTwoViewHolder extends RecyclerView.ViewHolder {
        LinearLayout Linear_up, Linear_down; // 아래 위의 타이틀 없애기 위한 Linear 변수 선언
        TextView tv_Id, tv_Name, tv_Message;
        ImageView iv_Profile;

        public ItemTwoViewHolder(View itemView) {
            super(itemView);

            tv_Id= (TextView) itemView.findViewById(R.id.tv_Id);
            tv_Name= (TextView) itemView.findViewById(R.id.tv_name);
            tv_Message= (TextView) itemView.findViewById(R.id.tv_Message);
            iv_Profile= (ImageView) itemView.findViewById(R.id.iv_profile);
            Linear_up= (LinearLayout) itemView.findViewById(R.id.Linear_up);
            Linear_up.setVisibility(View.GONE);
            Linear_down= (LinearLayout) itemView.findViewById(R.id.Linear_down);
            Linear_down.setVisibility(View.GONE);

        }
    }
    // =========================================================================================================


    // Recycler View에 등록되어 있는 총 아이템 갯수 세기
    @Override
    public int getItemCount() {
        return itemlist.size();
    }

    // =========================================================================================================
    // 데이터 추가를 위한 함수들
    // 사진 메세지
    public void addMe(String name, String message, String profile) {
        Home_Recycler_Item item = new Home_Recycler_Item();

        item.setHome_type(ME); // view type
        item.setHome_nickname(name); // 닉네임
        item.setHome_message(message); // 상태 메세지
        item.setHome_profile(SERVER_URL + "photo/"+profile); // 사진이 저장되어 있는 경로

        itemlist.add(item);
    }

    // 사진 메세지
    public void addFriend(int id, String name, String message, String profile) {
        Home_Recycler_Item item = new Home_Recycler_Item();

        item.setHome_type(FRIENDS);
        item.setHome_id(id);
        item.setHome_nickname(name);
        item.setHome_message(message);
        item.setHome_profile(SERVER_URL + "photo/"+profile);

        itemlist.add(item);
    }
    // =========================================================================================================

}
