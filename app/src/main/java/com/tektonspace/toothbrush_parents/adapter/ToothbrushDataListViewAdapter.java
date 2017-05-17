package com.tektonspace.toothbrush_parents.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tektonspace.toothbrush_parents.R;

import java.util.ArrayList;

/**
 * Created by SeungHee on 2017-04-28.
 */

public class ToothbrushDataListViewAdapter extends BaseAdapter {
    private Context mContext = null;
    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<ToothbrushDataListViewItem> toothbrushDataList = new ArrayList<ToothbrushDataListViewItem>();

    int[] dayResouces = {
            R.string.string_sunday,
            R.string.string_monday,
            R.string.string_tuesday,
            R.string.string_wednesday,
            R.string.string_thursday,
            R.string.string_friday,
            R.string.string_saturday
    };
    // ListViewAdapter의 생성자
    public ToothbrushDataListViewAdapter() {
    }

    public class ToothbrushDataListViewHolder {
        TextView toothbrush_data_day_textView;
        ImageView toothbrush_data_morning_imageView, toothbrush_data_afternoon_imageView, toothbrush_data_evening_imageView;
    }


    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return toothbrushDataList.size();
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();
        ToothbrushDataListViewHolder viewHolder;
        Typeface font_regular = Typeface.createFromAsset(context.getAssets(), "yanolza_regular.ttf");
        Typeface font_bold = Typeface.createFromAsset(context.getAssets(), "yanolza_bold.ttf");

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_toothbrush_data, parent, false);

            viewHolder = new ToothbrushDataListViewHolder();

            // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
            viewHolder.toothbrush_data_day_textView = (TextView) convertView.findViewById(R.id.toothbrush_data_day_textView);
            viewHolder.toothbrush_data_morning_imageView = (ImageView) convertView.findViewById(R.id.toothbrush_data_morning_imageView);
            viewHolder.toothbrush_data_afternoon_imageView = (ImageView) convertView.findViewById(R.id.toothbrush_data_afternoon_imageView);
            viewHolder.toothbrush_data_evening_imageView = (ImageView) convertView.findViewById(R.id.toothbrush_data_evening_imageView);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ToothbrushDataListViewHolder) convertView.getTag();
        }

        // Data Set(toothbrushDataList)에서 position에 위치한 데이터 참조 획득
        ToothbrushDataListViewItem listViewItem = toothbrushDataList.get(position);

        // 요일 정보 출력
        viewHolder.toothbrush_data_day_textView.setText(dayResouces[position]);

        // 아침, 점심, 저녁별 아이 양치 정보 출력
        viewHolder.toothbrush_data_morning_imageView.setImageDrawable(listViewItem.getMorningData());
        viewHolder.toothbrush_data_afternoon_imageView.setImageDrawable(listViewItem.getAfternoonData());
        viewHolder.toothbrush_data_evening_imageView.setImageDrawable(listViewItem.getEveningData());

        // 아이템 내 각 위젯에 데이터 반영
        return convertView;
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Object getItem(int position) {
        return toothbrushDataList.get(position);
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem(Drawable morning, Drawable afternoon, Drawable evening) {
        ToothbrushDataListViewItem item = new ToothbrushDataListViewItem();

        item.setMorningData(morning);
        item.setAfternoonData(afternoon);
        item.setEveningData(evening);

        toothbrushDataList.add(item);
    }

    public void clearAll(){
        toothbrushDataList.clear();
    }
}
