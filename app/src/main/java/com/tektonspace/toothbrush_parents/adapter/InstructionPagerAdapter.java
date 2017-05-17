package com.tektonspace.toothbrush_parents.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tektonspace.toothbrush_parents.R;
import com.tektonspace.toothbrush_parents.constants.DB_Data;
import com.tektonspace.toothbrush_parents.db.VerifyUserInfo;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by SeungHee on 2017-04-19.
 */

public class InstructionPagerAdapter extends PagerAdapter {
    Context mContext;
    LayoutInflater layoutInflater;
    ImageView instructionImageView;
    TextView instructionTextView, instructionFullTextView;
    int[] currImageResources;
    int[] currTextResources;

    PhotoViewAttacher photoViewAttacher;
    VerifyUserInfo verifyUserInfo;

    public InstructionPagerAdapter(Context context, int[] imageResources) {
        // Required empty public constructor
        mContext = context;
        currImageResources = imageResources;
        layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return currImageResources.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((FrameLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        verifyUserInfo = (VerifyUserInfo)mContext.getApplicationContext();
        View itemView;
        if (verifyUserInfo.getIsTooth()) {
            itemView = layoutInflater.inflate(R.layout.instruct_item, container, false);
        } else {
            itemView = layoutInflater.inflate(R.layout.instruct_items, container, false);
        }
        instructionImageView = (ImageView) itemView.findViewById(R.id.instruction_item_imageView);

       /* if (verifyUserInfo.getIsTooth()) {
            if (currImageResources[position] == -1) {
                instructionImageView.setVisibility(View.INVISIBLE);
                instructionTextView.setVisibility(View.INVISIBLE);
                instructionFullTextView.setVisibility(View.VISIBLE);
                instructionFullTextView.setText(currTextResources[position]);
            } else {
                instructionImageView.setVisibility(View.VISIBLE);
                instructionImageView.setImageResource(currImageResources[position]);
                instructionFullTextView.setVisibility(View.INVISIBLE);
                instructionTextView.setVisibility(View.VISIBLE);
                instructionTextView.setText(currTextResources[position]);
            }
        }
            else{
                instructionImageView.setImageResource(currImageResources[position]);
            }*/

        instructionImageView.setImageResource(currImageResources[position]);
        photoViewAttacher = new PhotoViewAttacher(instructionImageView);
        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((FrameLayout) object);
    }
}
