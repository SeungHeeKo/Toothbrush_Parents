package com.tektonspace.toothbrush_parents.adapter;

/**
 * Created by SeungHee on 2017-05-17.
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.tektonspace.toothbrush_parents.R;
import com.tektonspace.toothbrush_parents.adapter.ChildSelectListViewItem;
import com.tektonspace.toothbrush_parents.adapter.HorizontalListViewItem;
import com.tektonspace.toothbrush_parents.db.VerifyUserInfo;
import com.tektonspace.toothbrush_parents.utils.CircularImageView;

import java.util.ArrayList;

public class HorizontalListViewAdapter extends BaseAdapter {
    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<HorizontalListViewItem> listViewItemList = new ArrayList<HorizontalListViewItem>();

    // ListViewAdapter의 생성자
    public HorizontalListViewAdapter() {

    }

    public class HorizontalListViewHolder {
        public ImageView childPhoto_imageView;
    }

    @Override
    public int getCount() {
        return listViewItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();
        HorizontalListViewHolder viewHolder;

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_horizontal_item, parent, false);

            viewHolder = new HorizontalListViewHolder();

            // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
            viewHolder.childPhoto_imageView = (ImageView) convertView.findViewById(R.id.childlist_childPhoto_imageView);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (HorizontalListViewHolder) convertView.getTag();
        }

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        HorizontalListViewItem listViewItem = listViewItemList.get(position);

        //아이 사진 정보가 존재하지 않을 경우
        if(listViewItem.getChildPhoto().length() <= 0){
//                photoLayout = convertView.getContext().getDrawable(R.drawable.childselect_photo_frame_none1);
            viewHolder.childPhoto_imageView.setImageDrawable(convertView.getContext().getDrawable(R.drawable.home_child_photo1));
        }
        else{
//                photoLayout = convertView.getContext().getDrawable(R.drawable.childselect_photo_frame1);
            viewHolder.childPhoto_imageView.setVisibility(ImageView.VISIBLE);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            Bitmap bitmap = BitmapFactory.decodeFile(listViewItem.getChildPhoto(), options);

            viewHolder.childPhoto_imageView.setImageBitmap(null);
            viewHolder.childPhoto_imageView.setImageBitmap(bitmap);
        }

        return convertView;
    }
    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem(String photo) {
        HorizontalListViewItem item = new HorizontalListViewItem();

        item.setChildPhoto(photo);

        listViewItemList.add(item);
    }

}