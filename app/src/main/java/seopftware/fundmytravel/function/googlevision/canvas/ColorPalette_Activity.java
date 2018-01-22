package seopftware.fundmytravel.function.googlevision.canvas;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;

import seopftware.fundmytravel.R;

// 색깔 선택하는 화면을 다이얼로그 창으로 띄우기
public class ColorPalette_Activity extends AppCompatActivity {

    GridView gridView;
    Button btn_close;
    ColorDataAdapter adapter;

    public static OnColorSelectedListener listener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_palette);

        this.setTitle("Choose Color");

        gridView = (GridView) findViewById(R.id.gridView);

        // 다이얼로그 창 종료 버튼
        btn_close = (Button) findViewById(R.id.btn_close);
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        gridView.setColumnWidth(14); // 14행
        gridView.setBackgroundColor(Color.GRAY);
        gridView.setVerticalSpacing(4);
        gridView.setHorizontalSpacing(4);

        adapter = new ColorDataAdapter(this);
        gridView.setAdapter(adapter);
        gridView.setNumColumns(adapter.getNumColumns());
    }
}

// 어댑터 설정
class ColorDataAdapter extends BaseAdapter {

    Context mContext;

    // color define
    public static final int [] colors = new int[] {
            0xff000000,0xff00007f,0xff0000ff,0xff007f00,0xff007f7f,0xff00ff00,0xff00ff7f,
            0xff00ffff,0xff7f007f,0xff7f00ff,0xff7f7f00,0xff7f7f7f,0xffff0000,0xffff007f,
            0xffff00ff,0xffff7f00,0xffff7f7f,0xffff7fff,0xffffff00,0xffffff7f,0xffffffff
    };

    int rowCount;
    int columnCount;

    public ColorDataAdapter(Context context) {
        super();
        mContext=context;

        // create test data
        rowCount=3;
        columnCount=7;
    }

    public int getNumColumns() {
        return columnCount;
    }

    public int getCount() {
        return rowCount * columnCount;
    }

    public Object getItem(int position) {
        return colors[position];
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View view, ViewGroup group) {
        Log.d("ColorDataAdapter", "getView(" + position + ") called.");

        // calculate position
        int rowIndex = position / rowCount;
        int columnIndex = position % rowCount;
        Log.d("ColorDataAdapter", "Index : " + rowIndex + ", " + columnIndex);

        GridView.LayoutParams params = new GridView.LayoutParams(
                GridView.LayoutParams.MATCH_PARENT,
                GridView.LayoutParams.MATCH_PARENT);

        // create a Button with the color
        Button aItem = new Button(mContext);
        aItem.setText(" ");
        aItem.setLayoutParams(params);
        aItem.setPadding(4, 4, 4, 4);
        aItem.setBackgroundColor(colors[position]);
        aItem.setHeight(120);
        aItem.setTag(colors[position]);

        // set listener
        aItem.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (ColorPalette_Activity.listener != null) {
                    ColorPalette_Activity.listener.onColorSelected(((Integer)v.getTag()).intValue());
                }

                ((ColorPalette_Activity)mContext).finish();
            }
        });

        return aItem;
    }

}