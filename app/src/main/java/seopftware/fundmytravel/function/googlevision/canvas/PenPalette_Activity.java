package seopftware.fundmytravel.function.googlevision.canvas;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;

import seopftware.fundmytravel.R;


// 펜의 굵기를 설정해주는 화면
public class PenPalette_Activity extends AppCompatActivity {

    GridView gridView;
    Button btn_close;
    PenDataAdapter adapter;
    public static OnPenSelectedListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_palette); // color palette랑 같은 레이아웃 이용

        this.setTitle("border choose"); // 타이틀 설정

        gridView= (GridView) findViewById(R.id.gridView);

        // 종료 버튼
        btn_close= (Button) findViewById(R.id.btn_close);
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        gridView.setColumnWidth(14);
        gridView.setBackgroundColor(Color.GRAY);
        gridView.setVerticalSpacing(4);
        gridView.setHorizontalSpacing(4);

        adapter = new PenDataAdapter(this);
        gridView.setAdapter(adapter);
        gridView.setNumColumns(adapter.getNumColumns()); // 선 굵기의 종류에 따라 행 갯수 설정
    }
}


// Adapter for pen data
class PenDataAdapter extends BaseAdapter {

    /**
     * Application Context
     */
    Context mContext;

    /**
     * Pens defined
     */
    public static final int[] pens = new int[]{
            1, 2, 3, 4, 5,
            6, 7, 8, 9, 10,
            11, 13, 15, 17, 20
    };

    int rowCount;
    int columnCount;

    public PenDataAdapter(Context context) {
        super();

        mContext = context;

        rowCount = 3;
        columnCount = 5;

    }

    public int getNumColumns() {
        return columnCount;
    }

    public int getCount() {
        return rowCount * columnCount;
    }

    public Object getItem(int position) {
        return pens[position];
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View view, ViewGroup group) {
        Log.d("PenDataAdapter", "getView(" + position + ") called.");

        // calculate position
        int rowIndex = position / rowCount;
        int columnIndex = position % rowCount;
        Log.d("PenDataAdapter", "Index : " + rowIndex + ", " + columnIndex);

        GridView.LayoutParams params = new GridView.LayoutParams(
                GridView.LayoutParams.MATCH_PARENT,
                GridView.LayoutParams.MATCH_PARENT);

        // create a Pen Image
        int areaWidth = 10;
        int areaHeight = 20;

        Bitmap penBitmap = Bitmap.createBitmap(areaWidth, areaHeight, Bitmap.Config.ARGB_8888);
        Canvas penCanvas = new Canvas();
        penCanvas.setBitmap(penBitmap);

        Paint mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
        penCanvas.drawRect(0, 0, areaWidth, areaHeight, mPaint);

        mPaint.setColor(Color.BLACK);
        mPaint.setStrokeWidth((float) pens[position]);
        penCanvas.drawLine(0, areaHeight / 2, areaWidth - 1, areaHeight / 2, mPaint);
        BitmapDrawable penDrawable = new BitmapDrawable(mContext.getResources(), penBitmap);

        // create a Button with the color
        Button aItem = new Button(mContext);
        aItem.setText(" ");
        aItem.setLayoutParams(params);
        aItem.setPadding(4, 4, 4, 4);
        aItem.setBackgroundDrawable(penDrawable);
        aItem.setHeight(120);
        aItem.setTag(pens[position]);

        // set listener
        aItem.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (PenPalette_Activity.listener != null) {
                    PenPalette_Activity.listener.onPenSelected(((Integer) v.getTag()).intValue());
                }

                ((PenPalette_Activity) mContext).finish();
            }
        });

        return aItem;
    }
}
