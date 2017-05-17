package com.tektonspace.toothbrush_parents.adapter;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by SeungHee on 2017-04-28.
 */

public class ToothbrushDataListViewItem {
    private Drawable morningData, afternoonData, eveningData;

    public Drawable getMorningData(){
        return morningData;
    }
    public Drawable getAfternoonData(){
        return afternoonData;
    }
    public Drawable getEveningData(){
        return eveningData;
    }

    public void setMorningData(Drawable morningData){
        this.morningData = morningData;
    }
    public void setAfternoonData(Drawable afternoonData){
        this.afternoonData = afternoonData;
    }
    public void setEveningData(Drawable eveningData){
        this.eveningData = eveningData;
    }



}
