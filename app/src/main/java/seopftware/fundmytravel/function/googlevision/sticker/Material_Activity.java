package seopftware.fundmytravel.function.googlevision.sticker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import seopftware.fundmytravel.R;


// 스티커를 RecyclerView로 보여주고 선택할 수 있는 화면
public class Material_Activity extends AppCompatActivity {
    public static final String EXTRA_STICKER_ID = "extra_sticker_id";

    // 추가하고자 하는 아이템들의 Resource. 스티커 추가 하고 싶으면 Resource 더해주면 된다.
    private final int[] stickerIds = {
            R.drawable.abra,
            R.drawable.bellsprout,
            R.drawable.keai_01,
            R.drawable.keai_02,
            R.drawable.keai_03,
            R.drawable.keai_04,
            R.drawable.keai_05,
            R.drawable.keai_06,
            R.drawable.keai_07,
            R.drawable.keai_08,
            R.drawable.keai_09,
            R.drawable.keai_10,
            R.drawable.keai_11,
            R.drawable.mustache,
            R.drawable.rainbow,
            R.drawable.kakao1,
            R.drawable.kakao2,
            R.drawable.kakao3,
            R.drawable.kakao4,
            R.drawable.kakao5,
            R.drawable.kakao6,
            R.drawable.kakao7,
            R.drawable.kakao8,


    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material);

        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 활성화

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.stickers_recycler_view);
        GridLayoutManager glm = new GridLayoutManager(this, 3); // 3열의 recycler view
        recyclerView.setLayoutManager(glm);

        List<Integer> stickers = new ArrayList<>(stickerIds.length);
        for (Integer id : stickerIds) {
            stickers.add(id);
        }

        recyclerView.setAdapter(new StickersAdapter(stickers, this));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onStickerSelected(int stickerId) {

        Log.d("material", "스티커 선택 완료 (stickerId): " + stickerId);

        Intent intent = new Intent();
        intent.putExtra(EXTRA_STICKER_ID, stickerId);
        setResult(RESULT_OK, intent);
        finish();
    }

    class StickersAdapter extends RecyclerView.Adapter<StickersAdapter.StickerViewHolder> {

        private final List<Integer> stickerIds;
        private final Context context;
        private final LayoutInflater layoutInflater;

        StickersAdapter(@NonNull List<Integer> stickerIds, @NonNull Context context) {
            this.stickerIds = stickerIds;
            this.context = context;
            this.layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public StickerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new StickerViewHolder(layoutInflater.inflate(R.layout.item_sticker, parent, false));
        }

        @Override
        public void onBindViewHolder(StickerViewHolder holder, int position) {
            holder.image.setImageDrawable(ContextCompat.getDrawable(context, getItem(position)));
        }

        @Override
        public int getItemCount() {
            return stickerIds.size();
        }

        private int getItem(int position) {
            return stickerIds.get(position);
        }

        class StickerViewHolder extends RecyclerView.ViewHolder {

            ImageView image;

            StickerViewHolder(View itemView) {
                super(itemView);
                image = (ImageView) itemView.findViewById(R.id.sticker_image);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int pos = getAdapterPosition();
                        if (pos >= 0) { // might be NO_POSITION


                            Log.d("material", "pos 값은 : " + pos);

                            onStickerSelected(getItem(pos));
                        }
                    }
                });
            }
        }
    }
}