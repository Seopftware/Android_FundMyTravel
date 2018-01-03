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

import seopftware.fundmytravel.R;
import seopftware.fundmytravel.dataset.Home_Recycler_Item;

/**
 * Created by MSI on 2018-01-03.
 */

public class Home_Recycler_Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "all_"+Home_Recycler_Adapter.class;
    private static final int ME = 0;
    private static final int FRIENDS = 1;

    ArrayList<Home_Recycler_Item> mItems;
    Context context;

    public Home_Recycler_Adapter(ArrayList<Home_Recycler_Item> items) {
        mItems = items;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = null;
        RecyclerView.ViewHolder viewHolder = null;
        context=parent.getContext();

        switch (viewType) {
            case ME:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_friendlist, parent, false);
                viewHolder = new ItemViewHolder(view);
        }


        return viewHolder;
    }

    @Override
    public int getItemViewType(int position) {
        int viewType =1;

        Log.d(TAG, "getItemViewType(position): " + position);

        switch (position) {
            case 0:
                viewType =1;
                break;
        }

        return viewType;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if(holder instanceof ItemViewHolder) {
            Home_Recycler_Item listviewItem = mItems.get(position);

            ((ItemViewHolder)holder).tv_Id.setText(listviewItem.getHome_id()); // 고유 Id
            ((ItemViewHolder)holder).tv_Name.setText(listviewItem.getHome_nickname()); // 닉네임
            ((ItemViewHolder)holder).tv_Message.setText(listviewItem.getHome_nickname()); // 상태 메세지
            Glide.with(context).load(mItems.get(position)).into(((ItemViewHolder)holder).iv_Profile); // 프로필
        }

    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    // 커스텀 뷰홀더
    // item layout에 존재하는 위젯들을 바인딩함.
    class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tv_Id, tv_Name, tv_Message ;
        ImageView iv_Profile;

        public ItemViewHolder(View itemView) {
            super(itemView);

            tv_Id= (TextView) itemView.findViewById(R.id.tv_Id);
            tv_Name= (TextView) itemView.findViewById(R.id.tv_Name);
            tv_Message= (TextView) itemView.findViewById(R.id.tv_Message);
            iv_Profile= (ImageView) itemView.findViewById(R.id.iv_Profile);

        }
    }
}
