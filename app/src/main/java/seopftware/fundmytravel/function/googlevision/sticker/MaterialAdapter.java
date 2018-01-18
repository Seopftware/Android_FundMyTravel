//package seopftware.fundmytravel.function.googlevision.sticker;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.ImageView;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.HashMap;
//
//import seopftware.fundmytravel.R;
//
///**
// * Created by MSI on 2018-01-17.
// */
//
//public class MaterialAdapter extends BaseAdapter {
//
//    private Context context; // 컨텍스트 객체
//    private ArrayList<HashMap<String, String>> materialList; // 스티커 집합
//
//    // 생성자 생성
//    MaterialAdapter(Context context, ArrayList<HashMap<String, String>> materialList) {
//        this.context = context;
//        this.materialList = materialList;
//    }
//
//    // View 메소드 생성
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//
//        ViewHolder holder = null;
//
//        if(convertView == null) {
//            holder = new ViewHolder();
//            convertView = LayoutInflater.from(context).inflate(R.layout.item_material_grid, null);
//            holder.img = (ImageView) convertView.findViewById(R.id.img);
//            convertView.setTag(holder);
//        } else {
//            holder = (ViewHolder) convertView.getTag();
//        }
//
//
//        // 파일에서 스티커 불러온 다음 imageView에 set해주는 작업
//        File dir = context.getExternalFilesDir(null);
//        Bitmap bitmap = BitmapFactory.decodeFile(dir.getAbsolutePath()+"/decorate/"+materialList.get(position).get("thumbnailname"));
//        holder.img.setImageBitmap(bitmap);
//        return convertView;
//    }
//
//    private final class ViewHolder {
//        public ImageView img;
//    }
//
//    @Override
//    public int getCount() {
//        return materialList.size();
//    }
//
//    @Override
//    public Object getItem(int item) {
//        return item;
//    }
//
//    @Override
//    public long getItemId(int id) {
//        return id;
//    }
//}
