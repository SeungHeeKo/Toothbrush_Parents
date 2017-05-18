package com.tektonspace.toothbrush_parents.db;

import android.app.Application;
import android.widget.Toast;

import com.tektonspace.toothbrush_parents.R;
import com.tektonspace.toothbrush_parents.adapter.ListItem;
import com.tektonspace.toothbrush_parents.constants.DB_Data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by SeungHee on 2017-04-12.
 * ToothbrushData DataBase와 연동해 각 테이블에 데이터를 삽입, 갱신, 삭제하거나 테이블에 접근하여 데이터를 받아온다.
 */

public class DB_Manager{
    // DB 접속 URL
    private String urlPath;

    // DB 접속 후 결과 반환값
    String results;

    // 테이블에 존재하는 모든 데이터를 가져옴
    public String receive_data_fromDB(int tableName) {
        switch (tableName) {
            case DB_Data.TABLE_USER_INFO:
                urlPath = DB_Data.URL_GET_USERINFO;
                break;
            case DB_Data.TABLE_CHILD_INFO:
                urlPath = DB_Data.URL_GET_CHILDINFO;
                break;
            case DB_Data.TABLE_REWARD_INFO:
                urlPath = DB_Data.URL_GET_REWARDINFO;
                break;
            case DB_Data.TABLE_DATA_INFO:
                urlPath = DB_Data.URL_GET_DATAINFO;
                break;
        }
        try {
            results = new PhpDataReceive().execute(urlPath).get();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } catch (ExecutionException ex) {
            ex.printStackTrace();
        }
        return results;
    }

    // 테이블 중 id 값을 가진 데이터만 가져옴
    public String receive_data_fromDB(int tableName, String id) {
        switch (tableName) {
            case DB_Data.TABLE_USER_INFO:
                urlPath = DB_Data.URL_GET_USERINFO_ID;
                break;
            case DB_Data.TABLE_CHILD_INFO:
                urlPath = DB_Data.URL_GET_CHILDINFO_ID;
                break;
            case DB_Data.TABLE_REWARD_INFO:
                urlPath = DB_Data.URL_GET_REWARDINFO;
                break;
            case DB_Data.TABLE_DATA_INFO:
                urlPath = DB_Data.URL_GET_DATAINFO;
                break;
        }
        try {
            results = new PhpDataReceive().execute(urlPath, id).get();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } catch (ExecutionException ex) {
            ex.printStackTrace();
        }
        return results;
    }

    // 테이블 중 id, date 값을 가진 데이터만 가져옴
    public String receive_data_fromDB(int tableName, String id, String secondData) {
        switch (tableName) {
            case DB_Data.TABLE_USER_INFO:
                urlPath = DB_Data.URL_GET_USERINFO_MAIL_ID;
                break;
            case DB_Data.TABLE_CHILD_INFO:
//                urlPath = DB_Data.URL_GET_CHILDINFO_ID;
                break;
            case DB_Data.TABLE_REWARD_INFO:
//                urlPath = DB_Data.URL_GET_REWARDINFO;
                break;
            case DB_Data.TABLE_DATA_INFO:
                urlPath = DB_Data.URL_GET_DATAINFO_DATE;
                break;
        }
        try {
            results = new PhpDataReceive().execute(urlPath, id, secondData).get();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } catch (ExecutionException ex) {
            ex.printStackTrace();
        }
        return results;
    }
    // 테이블에 데이터 삽입
    public String insert_data_toDB(int tableName, String sendData) {
        switch (tableName) {
            case DB_Data.TABLE_USER_INFO:
                urlPath = DB_Data.URL_INSERT_USERINFO;
                break;
            case DB_Data.TABLE_USER_INFO_CHILDID:
                urlPath = DB_Data.URL_INSERT_USERINFO_CHILDID;
                break;
            case DB_Data.TABLE_CHILD_INFO:
                urlPath = DB_Data.URL_INSERT_CHILDINFO;
                break;
            case DB_Data.TABLE_REWARD_INFO:
                urlPath = DB_Data.URL_INSERT_REWARDINFO;
                break;
            case DB_Data.TABLE_DATA_INFO:
                break;
        }

        try {
            results = new PhpDataSend().execute(urlPath, sendData).get();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } catch (ExecutionException ex) {
            ex.printStackTrace();
        }
        return results;
    }

    public String update_data_toDB(int tableName, String sendData,String id){
        switch (tableName) {
            case DB_Data.TABLE_USER_INFO:
                if(sendData.contains("\t"))
                    urlPath = DB_Data.URL_UPDATE_USERINFO;
                else
                    urlPath = DB_Data.URL_UPDATE_USERINFO_PASSWORD;
                break;
            case DB_Data.TABLE_CHILD_INFO_NAME:
                urlPath = DB_Data.URL_UPDATE_CHILDINFO_NAME;
                break;
            case DB_Data.TABLE_CHILD_INFO_BLUETOOTHID:
                urlPath = DB_Data.URL_UPDATE_CHILDINFO_BLUETOOTHID;
                break;
            case DB_Data.TABLE_REWARD_INFO:
                if(sendData.contains("reset"))
                    urlPath = DB_Data.URL_UPDATE_RESET_REWARDINFO;
                else
                    urlPath = DB_Data.URL_UPDATE_REWARDINFO;
                break;
            case DB_Data.TABLE_DATA_INFO:
                break;
        }

        try {
            results = new PhpDataSend().execute(urlPath, sendData, id).get();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } catch (ExecutionException ex) {
            ex.printStackTrace();
        }
        return results;
    }


    public String delete_data_toDB(int tableName, String sendData, String id){
        switch (tableName) {
            case DB_Data.TABLE_USER_INFO:
//                    urlPath = DB_Data.URL_UPDATE_USERINFO_PASSWORD;
                break;
            case DB_Data.TABLE_CHILD_INFO_NAME:
//                urlPath = DB_Data.URL_UPDATE_CHILDINFO_NAME;
                break;
            case DB_Data.TABLE_CHILD_INFO_BLUETOOTHID:
//                urlPath = DB_Data.URL_UPDATE_CHILDINFO_BLUETOOTHID;
                break;
            case DB_Data.TABLE_REWARD_INFO:
                    urlPath = DB_Data.URL_DELETE_REWARDINFO;
                break;
            case DB_Data.TABLE_DATA_INFO:
                break;
        }

        try {
            results = new PhpDataSend().execute(urlPath, sendData, id).get();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } catch (ExecutionException ex) {
            ex.printStackTrace();
        }
        return results;
    }


    // 각 테이블별 인덱스값 반환
    public String GetTableIndex(int tableNum, int indexNum) {
        String indexString = "";
        switch (tableNum) {
            case DB_Data.TABLE_USER_INFO:
                switch (indexNum) {
                    case DB_Data.INDEX_USER_ID:
                        indexString = "UserID";
                        break;
                    case DB_Data.INDEX_USER_MAILADDRESS:
                        indexString = "MailAddress";
                        break;
                    case DB_Data.INDEX_USER_PASSWORD:
                        indexString = "Password";
                        break;
                    case DB_Data.INDEX_USER_CHILDNAME:
                        indexString = "ChildName";
                        break;
                    case DB_Data.INDEX_USER_CHILDID:
                        indexString = "ChildID";
                        break;
                }
                break;
            case DB_Data.TABLE_CHILD_INFO:
                switch (indexNum) {
                    case DB_Data.INDEX_CHILD_ID:
                        indexString = "id";
                        break;
                    case DB_Data.INDEX_CHILD_NAME:
                        indexString = "Name";
                        break;
                    case DB_Data.INDEX_CHILD_BLUETOOTHID:
                        indexString = "BluetoothID";
                        break;
                    case DB_Data.INDEX_CHILD_NICKNAME:
                        indexString = "Nickname";
                        break;
                    case DB_Data.INDEX_CHILD_CHARACTER:
                        indexString = "Character";
                        break;
                    case DB_Data.INDEX_CHILD_PHOTO:
                        indexString = "Photo";
                        break;
                    case DB_Data.INDEX_CHILD_BACKGROUNDPHOTO:
                        indexString = "BackgroundPhoto";
                        break;
                    case DB_Data.INDEX_CHILD_CONNECTMODE:
                        indexString = "ConnectMode";
                        break;
                }
                break;
            case DB_Data.TABLE_REWARD_INFO:
                switch (indexNum) {
                    case DB_Data.INDEX_REWARD_ID:
                        indexString = "id";
                        break;
                    case DB_Data.INDEX_REWARD_CHILDID:
                        indexString = "ChildID";
                        break;
                    case DB_Data.INDEX_REWARD_REWARD:
                        indexString = "Reward_detail";
                        break;
                    case DB_Data.INDEX_REWARD_CURRENT:
                        indexString = "Reward_current";
                        break;
                    case DB_Data.INDEX_REWARD_TOTAL:
                        indexString = "Reward_total";
                        break;
                }
                break;
            case DB_Data.TABLE_DATA_INFO:
                switch (indexNum) {
                    case DB_Data.INDEX_DATA_ID:
                        indexString = "id";
                        break;
                    case DB_Data.INDEX_DATA_CHILDID:
                        indexString = "ChildID";
                        break;
                    case DB_Data.INDEX_DATA_DATE:
                        indexString = "Date";
                        break;
                    case DB_Data.INDEX_DATA_TIME:
                        indexString = "Time";
                        break;
                }
                break;

        }

        return indexString;
    }


}
