package com.tektonspace.toothbrush_parents.adapter;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ssomai.android.scalablelayout.ScalableLayout;
import com.tektonspace.toothbrush_parents.R;
import com.tsengvn.typekit.TypekitContextWrapper;

import java.util.ArrayList;

/**
 * Created by SeungHee on 2017-04-14.
 * 생성된 아이 정보를 화면에 나타내기 위한 ListView Adapter
 */

public class ChildSelectListViewAdapter extends BaseAdapter {
    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<ChildSelectListViewItem> listViewItemList = new ArrayList<ChildSelectListViewItem>();

    // ListViewAdapter의 생성자
    public ChildSelectListViewAdapter() {

    }

    public class ChildSelectListViewHolder {
        public FrameLayout childselect_background_layout;
        public RelativeLayout childselect_list_layout;

        public RelativeLayout childselect_character_layout;
        public RelativeLayout childCharacter_relativeLayout;
        public ImageView childCharacter_imageView;

        public RelativeLayout childPhoto_relativeLayout;
        public ImageView childPhoto_imageView;

        public TextView childName_textView;
        public TextView childNickname_textView;

    }


    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return listViewItemList.size();
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();
        ChildSelectListViewHolder viewHolder;
        Drawable characterLayout, photoLayout;
        Typeface font_regular = Typeface.createFromAsset(context.getAssets(), "yanolza_regular.ttf");
        Typeface font_bold = Typeface.createFromAsset(context.getAssets(), "yanolza_bold.ttf");

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_item, parent, false);

            viewHolder = new ChildSelectListViewHolder();

            // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
            viewHolder.childselect_background_layout = (FrameLayout) convertView.findViewById(R.id.childselect_background_layout);
            viewHolder.childselect_list_layout = (RelativeLayout) convertView.findViewById(R.id.childselect_list_layout);

            viewHolder.childselect_character_layout = (RelativeLayout) convertView.findViewById(R.id.childselect_character_layout);
            viewHolder.childCharacter_relativeLayout = (RelativeLayout) convertView.findViewById(R.id.childselect_characterframe);
            viewHolder.childCharacter_imageView = (ImageView) convertView.findViewById(R.id.childselect_character_frame_imageView);
            viewHolder.childCharacter_relativeLayout.bringToFront();

            viewHolder.childPhoto_relativeLayout = (RelativeLayout) convertView.findViewById(R.id.childphoto_layout);
            viewHolder.childPhoto_imageView = (ImageView) convertView.findViewById(R.id.childPhoto_imageView);

            viewHolder.childName_textView = (TextView) convertView.findViewById(R.id.childName_textView);
            viewHolder.childName_textView.setTypeface(font_regular);
            viewHolder.childNickname_textView = (TextView) convertView.findViewById(R.id.childNickname_textView);
            viewHolder.childNickname_textView.setTypeface(font_bold);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ChildSelectListViewHolder) convertView.getTag();
        }

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        ChildSelectListViewItem listViewItem = listViewItemList.get(position);

        // 짝수
        if(position % 2 == 0 ){
            if(listViewItem.getChildName().equals(convertView.getContext().getString(R.string.none_childName))){
                viewHolder.childselect_list_layout.setBackground(convertView.getContext().getDrawable(R.drawable.childselect_background_none));
            }
            else{
                viewHolder.childselect_list_layout.setBackground(convertView.getContext().getDrawable(R.drawable.childselect_background));
            }
        }
        else{
            if(listViewItem.getChildName().equals(convertView.getContext().getString(R.string.none_childName))){
                viewHolder.childselect_list_layout.setBackground(convertView.getContext().getDrawable(R.drawable.childselect_background_none));
            }
            else{
                viewHolder.childselect_list_layout.setBackground(convertView.getContext().getDrawable(R.drawable.childselect_background1));
            }
        }



        // 캐릭터 정보가 존재하지 않을 경우
        if(listViewItem.getCharacterLayout() == -1){
            characterLayout = convertView.getContext().getDrawable(R.drawable.childselect_character_none1);
            viewHolder.childCharacter_imageView.setVisibility(ImageView.INVISIBLE);
        }
        else{
            characterLayout = convertView.getContext().getDrawable(R.drawable.childselect_character_frame1);
            viewHolder.childCharacter_imageView.setVisibility(ImageView.VISIBLE);
        }

        // 아이 사진 정보가 존재하지 않을 경우
        if(listViewItem.getChildPhoto().length() <= 0){
            photoLayout = convertView.getContext().getDrawable(R.drawable.childselect_photo_frame_none1);
            viewHolder.childPhoto_imageView.setVisibility(ImageView.INVISIBLE);
        }
        else{
            photoLayout = convertView.getContext().getDrawable(R.drawable.childselect_photo_frame1);
            viewHolder.childPhoto_imageView.setVisibility(ImageView.VISIBLE);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            Bitmap bitmap = BitmapFactory.decodeFile(listViewItem.getChildPhoto(), options);

            viewHolder.childPhoto_imageView.setImageBitmap(null);
            viewHolder.childPhoto_imageView.setImageBitmap(bitmap);
        }


        // 아이템 내 각 위젯에 데이터 반영
        viewHolder.childName_textView.setText(listViewItem.getChildName());
        viewHolder.childNickname_textView.setText(listViewItem.getNickName());
        if(characterLayout.equals(convertView.getContext().getDrawable(R.drawable.childselect_photo_frame_none1)))
            viewHolder.childCharacter_relativeLayout.setBackground(characterLayout);
        else
            viewHolder.childCharacter_relativeLayout.getBackground().setAlpha(0);
        Drawable color = new ColorDrawable(Color.WHITE);
        LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{color, listViewItem.getChildCharacter()});
        viewHolder.childCharacter_imageView.setImageDrawable(layerDrawable);
        viewHolder.childPhoto_relativeLayout.setBackground(photoLayout);
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
        return listViewItemList.get(position);
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem(int layout_character, Drawable character, String photo, String name, String nickname) {
        ChildSelectListViewItem item = new ChildSelectListViewItem();

        item.setCharacterLayout(layout_character);
        item.setChildCharacter(character);
        item.setChildPhoto(photo);
        item.setChildName(name);
        item.setNickName(nickname);

        listViewItemList.add(item);
    }

}
