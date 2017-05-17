package com.tektonspace.toothbrush_parents.adapter;

/**
 * Created by SeungHee on 2017-04-12.
 * DB에 저장된 정보를 받아와 디바이스 내에 저장시킴
 */

public class ListItem {
    private String[] mData;

    public ListItem(String[] data ){
        mData = data;
    }

    public ListItem(String id, String first_data, String second_data, String third_data){

        mData = new String[4];
        mData[0] = id;
        mData[1] = first_data;
        mData[2] = second_data;
        mData[3] = third_data;

    }

    public ListItem(String id, String first_data, String second_data, String third_data, String fourth_data){

        mData = new String[5];
        mData[0] = id;
        mData[1] = first_data;
        mData[2] = second_data;
        mData[3] = third_data;
        mData[4] = fourth_data;
    }
    public ListItem(String id, String first_data, String second_data, String third_data, String fourth_data, String fifth_data, String sixth_data, String seventh_data){

        mData = new String[8];
        mData[0] = id;
        mData[1] = first_data;
        mData[2] = second_data;
        mData[3] = third_data;
        mData[4] = fourth_data;
        mData[5] = fifth_data;
        mData[6] = sixth_data;
        mData[7] = seventh_data;
    }
    public String[] getData(){
        return mData;
    }

    public String getData(int index){
        return mData[index];
    }

    public void setData(String[] data){
        mData = data;
    }

    public void changeData(int index, String data){ mData[index] = data;}

    public int length(){
        return mData.length;
    }

}
