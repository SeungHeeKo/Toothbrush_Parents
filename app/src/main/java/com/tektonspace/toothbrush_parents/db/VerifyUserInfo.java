package com.tektonspace.toothbrush_parents.db;

import android.app.Activity;
import android.app.Application;
import android.content.CursorLoader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tektonspace.toothbrush_parents.constants.DB_Data;
import com.tektonspace.toothbrush_parents.adapter.ListItem;
import com.tektonspace.toothbrush_parents.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

import android.Manifest;

/**
 * Created by SeungHee on 2017-04-12.
 * 사용자가 입력한 계정 정보가 DB에 존재하는지 확인.
 * <p>
 * TODO: toothbrushData 초기화, get, set 추가
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class VerifyUserInfo extends Application {

    public boolean IS_TOOTH = false;
    public void setIsTooth(boolean value){this.IS_TOOTH = value;}
    public boolean getIsTooth(){return this.IS_TOOTH;}
    final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 0;

    private int bitmapImageSize = 2;
    String results = "";
    String jsonResult = "";
    DB_Manager dbManager;
    public ArrayList<ListItem> listItems;

    public ArrayList<ListItem> userItems;

    // 사용자 정보는 자녀 명수에 따라 여러개가 존재할 수 있으므로 ArrayList
    public ArrayList<ListItem> userData;
    private ListItem childData, rewardData, toothbrushData;

    // 양치 정보 - 제한시간, 아침, 점심, 저녁
    private String dataLimit, dataMorning, dataAfternoon, dataEvening;

    boolean mailAddressExists = false;
    boolean isPasswordCorrect = false;

    // 사용자의 메일 주소
    private String userMailAddress = "";
    // 사용자의 id
    private String userID = "";

    // 태블릿 연결 방식이 WIFI 혹은 블루투스인지 여부
    // isWIFIMode가 true면 WIFI, false면 블루투스 방식
    private boolean isWIFIMode = true;

    private boolean isPermissionGranted = false;

    // 현재 관리받는 아이의 ChildID 값
    private String childID = "";

    // 블루투스모드일 경우, 태블릿에서 수신한 data
    public ArrayList<String> dataFromTablet;

    // WIFI모드일 경우, 태블릿에서 수신한 ChildID 값
    private ArrayList<String> childIDFromTablet;

    // Home 버튼으로 이동시 Activity 모두 종료시키기 위한 ArrayList
    private ArrayList<Activity> activities = new ArrayList<Activity>();

    public String getDataLimit() {
        return dataLimit;
    }

    public void setDataLimit(String dataLimit) {
        this.dataLimit = dataLimit;
    }

    public String getDataMorning() {
        return dataMorning;
    }

    public void setDataMorning(String dataMorning) {
        this.dataMorning = dataMorning;
    }

    public String getDataAfternoon() {
        return dataAfternoon;
    }

    public void setDataAfternoon(String dataAfternoon) {
        this.dataAfternoon = dataAfternoon;
    }

    public String getDataEvening() {
        return dataEvening;
    }

    public void setDataEvening(String dataEvening) {
        this.dataEvening = dataEvening;
    }

    public String getChildID() {
        return childID;
    }

    public void setChildID(String childID) {
        this.childID = childID;
    }

    public String getUserMailAddress() {
        return userMailAddress;
    }

    public void setUserMailAddress(String userMailAddress) {
        this.userMailAddress = userMailAddress;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public ArrayList<String> getDataFromTablet() {
        return dataFromTablet;
    }

    public void addDataFromTablet(String data) {
        this.dataFromTablet.add(data);
    }

    public void setDataFromTablet(ArrayList<String> listData) {
        this.dataFromTablet = listData;
    }

    public ArrayList<String> getChildIDFromTablet() {
        return childIDFromTablet;
    }

    public void addChildIDFromTablet(String data) {
        this.childIDFromTablet.add(data);
    }

    public void setChildIDFromTablet(ArrayList<String> listData) {
        this.childIDFromTablet = listData;
    }

    public void clearDataFromTablet() {
        this.dataFromTablet.clear();
    }

    public ArrayList<ListItem> getUserData() {
        return userData;
    }

    public void addUserData(ListItem data) {
        this.userData.add(data);
    }

    public void setUserData(ArrayList<ListItem> userData) {
        this.userData = userData;
    }

    public ListItem getChildData() {
        return childData;
    }

    public void setChildData(ListItem childData) {
        this.childData = childData;
    }

    public void changeChildData(int index, String data) {
        this.childData.changeData(index, data);
    }

    public ListItem getRewardData() {
        return rewardData;
    }

    public void setRewardData(ListItem rewardData) {
        this.rewardData = rewardData;
    }

    public void clearRewardData() {
        for (int i = 0; i < rewardData.length(); i++) {
            rewardData.changeData(i, "");
        }
    }

    public boolean getIsWIFIMode() {
        return isWIFIMode;
    }

    public void setIsWIFIMode(boolean isWIFIMode) {
        this.isWIFIMode = isWIFIMode;
    }

    public boolean getisPermissionGranted() {
        return isPermissionGranted;
    }

    public void setIsPermissionGranted(boolean isPermissionGranted) {
        this.isPermissionGranted = isPermissionGranted;
    }

    public int getBitmapImageSize() {
        return bitmapImageSize;
    }

    public void addActivities(Activity activity) {
        this.activities.add(activity);
    }

    public void clearActivities() {
        for (int i = 0; i < activities.size(); i++) {
            activities.get(i).finish();
        }
    }

    public void setBitmapImageSize(int bitmapImageSize) {
        this.bitmapImageSize = bitmapImageSize;
    }

    @Override
    public void onCreate() {
        //전역 변수 초기화
        userMailAddress = "";
        userID = "";
        childID = "";
        dataFromTablet = new ArrayList<String>();
        childIDFromTablet = new ArrayList<String>();
        userData = new ArrayList<ListItem>();
        childData = new ListItem("", "", "", "", "", "", "", "");
        rewardData = new ListItem("", "", "", "", "");
//        toothbrushData = new ListItem("", "", "", "", "");
        isWIFIMode = true;
        listItems = new ArrayList<ListItem>();
        bitmapImageSize = 2;
        dataLimit = "20";
        dataMorning = "오전 9:00";
        dataAfternoon = "오후 12:30";
        dataEvening = "오후 6:00";

        super.onCreate();
    }

    public String VerifyUserInfo(String userID, String password) {
        dbManager = new DB_Manager();
        listItems = new ArrayList<ListItem>();
        ListItem Item;

        if (TextUtils.isEmpty(userID) || password.length() < 1) {
            results = getString(R.string.error_empty_mailAddress_password);
            return results;
        }

        if (!DB_Data.IS_TEST_VERSION)
            FindUserInfo(userID, password);


        // 사용자 계정이 존재하지 않을 경우
        if (!mailAddressExists) {
            results = getString(R.string.error_none_mailAddress);
            return results;
        }
        // 계정이 존재하지만, 비밀번호가 일치하지 않을 경우
        if (!isPasswordCorrect) {
            results = getString(R.string.password_check_incorrect);
            return results;
        }

        //userMailAddress = mailAddress;
        // 계정이 존재할 경우
        results = DB_Data.DATA_CORRECT;

        return results;
    }

    public String VerifyUserInfo(String userID,String mailAddress, String password) {
        dbManager = new DB_Manager();
        listItems = new ArrayList<ListItem>();
        ListItem Item;

        if (TextUtils.isEmpty(userID) || mailAddress.length() < 1 || password.length() < 1) {
            results = getString(R.string.error_empty_mailAddress_password);
            return results;
        }

        if (!DB_Data.IS_TEST_VERSION)
            FindUserInfo(userID, password);


        // 사용자 계정이 존재하지 않을 경우
        if (!mailAddressExists) {
            results = getString(R.string.error_none_mailAddress);
            return results;
        }
        // 계정이 존재하지만, 비밀번호가 일치하지 않을 경우
        if (!isPasswordCorrect) {
            results = getString(R.string.password_check_incorrect);
            return results;
        }

        //userMailAddress = mailAddress;
        // 계정이 존재할 경우
        results = DB_Data.DATA_CORRECT;

        return results;
    }

    private void ParsingJSONdata(int tableName, String jsonHtml) {
        String firstColumn;
        String secondColumn;
        String thirdColumn;
        String fourthColumn;
        String fifthColumn;
        String sixthColumn;
        String seventhColumn;
        String eighthColumn;
        try {
            if (jsonHtml == "") {
                Toast.makeText(this, getString(R.string.error_fail_to_access_DB), Toast.LENGTH_SHORT).show();
                return;
            }
            JSONObject root = new JSONObject(jsonHtml);
            String jsonStatus = root.getString("status");
            JSONArray jsonArray = root.getJSONArray("results");
            if (!jsonStatus.equals("OK") && jsonArray.length() <= 0) {
                Toast.makeText(this, getString(R.string.error_fail_to_access_DB), Toast.LENGTH_SHORT).show();
                return;
            }
            switch (tableName){
                case DB_Data.TABLE_USER_INFO:
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        firstColumn = jsonObject.getString(dbManager.GetTableIndex(tableName, 0));
                        secondColumn = jsonObject.getString(dbManager.GetTableIndex(tableName, 1));
                        thirdColumn = jsonObject.getString(dbManager.GetTableIndex(tableName, 2));
                        fourthColumn = jsonObject.getString(dbManager.GetTableIndex(tableName, 3));
                        fifthColumn = jsonObject.getString(dbManager.GetTableIndex(tableName, 4));
                        listItems.add(new ListItem(firstColumn, secondColumn, thirdColumn, fourthColumn, fifthColumn));
                    }
                    break;
                case DB_Data.TABLE_CHILD_INFO:
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        firstColumn = jsonObject.getString(dbManager.GetTableIndex(tableName, 0));
                        secondColumn = jsonObject.getString(dbManager.GetTableIndex(tableName, 1));
                        thirdColumn = jsonObject.getString(dbManager.GetTableIndex(tableName, 2));
                        fourthColumn = jsonObject.getString(dbManager.GetTableIndex(tableName, 3));
                        fifthColumn = jsonObject.getString(dbManager.GetTableIndex(tableName, 4));
                        sixthColumn = jsonObject.getString(dbManager.GetTableIndex(tableName, 5));
                        seventhColumn = jsonObject.getString(dbManager.GetTableIndex(tableName, 6));
                        eighthColumn = jsonObject.getString(dbManager.GetTableIndex(tableName, 7));
                        listItems.add(new ListItem(firstColumn, secondColumn, thirdColumn, fourthColumn, fifthColumn, sixthColumn, seventhColumn, eighthColumn));
                        this.setChildData(listItems.get(i));
                    }
                    break;
                case DB_Data.TABLE_REWARD_INFO:
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        firstColumn = jsonObject.getString(dbManager.GetTableIndex(tableName, 0));
                        secondColumn = jsonObject.getString(dbManager.GetTableIndex(tableName, 1));
                        thirdColumn = jsonObject.getString(dbManager.GetTableIndex(tableName, 2));
                        fourthColumn = jsonObject.getString(dbManager.GetTableIndex(tableName, 3));
                        fifthColumn = jsonObject.getString(dbManager.GetTableIndex(tableName, 4));
                        listItems.add(new ListItem(firstColumn, secondColumn, thirdColumn, fourthColumn, fifthColumn));
                        this.setRewardData(listItems.get(i));
                    }
                    break;
                case DB_Data.TABLE_DATA_INFO:
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        firstColumn = jsonObject.getString(dbManager.GetTableIndex(tableName, 0));
                        secondColumn = jsonObject.getString(dbManager.GetTableIndex(tableName, 1));
                        thirdColumn = jsonObject.getString(dbManager.GetTableIndex(tableName, 2));
                        fourthColumn = jsonObject.getString(dbManager.GetTableIndex(tableName, 3));
                        listItems.add(new ListItem(firstColumn, secondColumn, thirdColumn, fourthColumn));
                    }
                    break;
            }
            // ChildInfo
            if (tableName == DB_Data.TABLE_CHILD_INFO) {
            }
            // RewardInfo
            else if (tableName == DB_Data.TABLE_REWARD_INFO) {
            }
            // UserInfo, ToothbrushDataInfo
            else {
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    public ArrayList<ListItem> GetDataFromTable(int tableName) {
        String jsonHtml = "";
        // listItem 초기화
        listItems = new ArrayList<ListItem>();
        switch (tableName) {
            case DB_Data.TABLE_USER_INFO:
                jsonHtml = dbManager.receive_data_fromDB(DB_Data.TABLE_USER_INFO);
                break;
            case DB_Data.TABLE_CHILD_INFO:
                jsonHtml = dbManager.receive_data_fromDB(DB_Data.TABLE_CHILD_INFO);
                break;
            case DB_Data.TABLE_REWARD_INFO:
                jsonHtml = dbManager.receive_data_fromDB(DB_Data.TABLE_REWARD_INFO);
                break;
            case DB_Data.TABLE_DATA_INFO:
                jsonHtml = dbManager.receive_data_fromDB(DB_Data.TABLE_DATA_INFO);
                break;
        }
        ParsingJSONdata(tableName, jsonHtml);
        return listItems;
    }

    public ArrayList<ListItem> GetDataFromTable(int tableName, String id) {
        String jsonHtml = "";
        // listItem 초기화
        listItems = new ArrayList<ListItem>();
        switch (tableName) {
            case DB_Data.TABLE_USER_INFO:
                jsonHtml = dbManager.receive_data_fromDB(DB_Data.TABLE_USER_INFO, id);
                break;
            case DB_Data.TABLE_CHILD_INFO:
                jsonHtml = dbManager.receive_data_fromDB(DB_Data.TABLE_CHILD_INFO, id);
                break;
            case DB_Data.TABLE_REWARD_INFO:
                jsonHtml = dbManager.receive_data_fromDB(DB_Data.TABLE_REWARD_INFO, id);
                break;
            case DB_Data.TABLE_DATA_INFO:
                jsonHtml = dbManager.receive_data_fromDB(DB_Data.TABLE_DATA_INFO, id);
                break;
        }
        ParsingJSONdata(tableName, jsonHtml);
        return listItems;
    }

    public ArrayList<ListItem> GetDataFromTable(int tableName, String id, String secondData) {
        String jsonHtml = "";
        // listItem 초기화
        listItems = new ArrayList<ListItem>();
        switch (tableName) {
            case DB_Data.TABLE_USER_INFO:
                jsonHtml = dbManager.receive_data_fromDB(DB_Data.TABLE_USER_INFO, id, secondData);
                break;
            case DB_Data.TABLE_CHILD_INFO:
//                jsonHtml = dbManager.receive_data_fromDB(DB_Data.TABLE_CHILD_INFO, id);
                break;
            case DB_Data.TABLE_REWARD_INFO:
//                jsonHtml = dbManager.receive_data_fromDB(DB_Data.TABLE_REWARD_INFO, id);
                break;
            case DB_Data.TABLE_DATA_INFO:
                jsonHtml = dbManager.receive_data_fromDB(DB_Data.TABLE_DATA_INFO, id, secondData);
                break;
        }
        ParsingJSONdata(tableName, jsonHtml);
        return listItems;
    }
    // 사용자 계정이 DB에 존재하는지 확인 후 저장된 사용자 계정만 ArrayList로 저장 및 반환
    public ArrayList<ListItem> FindUserInfo(String userID, String password) {
        // boolean 값 초기화
        mailAddressExists = false;
        isPasswordCorrect = false;
        // userData 초기화
        userData = new ArrayList<ListItem>();

        // DB에 저장된 데이터 가져오기
        GetDataFromTable(DB_Data.TABLE_USER_INFO);
        // DB에 저장된 계정 정보를 새로운 ArrayList에 저장
        ArrayList<ListItem> foundUserItems = new ArrayList<ListItem>();

        for (ListItem item : listItems) {
            if (item.getData(DB_Data.INDEX_USER_ID).equals(userID)) {
                mailAddressExists = true;
                foundUserItems.add(item);
                this.addUserData(item);
                // 비밀번호 일치 여부 확인
                if (item.getData(DB_Data.INDEX_USER_PASSWORD).equals(password))
                    isPasswordCorrect = true;
            }
        }
        return foundUserItems;
    }

    public String InsertDataInTable(int tableName, String[] inputDatas) {
        String result = "";
        String inputData = "";
        for (int i = 0; i < inputDatas.length; i++) {
            if (i != 0)
                inputData += "\t";
            inputData += inputDatas[i];
        }

        switch (tableName) {
            case DB_Data.TABLE_USER_INFO:
                result = dbManager.insert_data_toDB(DB_Data.TABLE_USER_INFO, inputData);
                break;
            case DB_Data.TABLE_USER_INFO_CHILDID:
                result = dbManager.insert_data_toDB(DB_Data.TABLE_USER_INFO_CHILDID, inputData);
                break;
            case DB_Data.TABLE_CHILD_INFO:
                result = dbManager.insert_data_toDB(DB_Data.TABLE_CHILD_INFO, inputData);
                break;
            case DB_Data.TABLE_REWARD_INFO:
                result = dbManager.insert_data_toDB(DB_Data.TABLE_REWARD_INFO, inputData);
                break;
            case DB_Data.TABLE_DATA_INFO:
                result = dbManager.insert_data_toDB(DB_Data.TABLE_DATA_INFO, inputData);
                break;
        }

        return result;
    }

    public String InsertDataInTable(int tableName, ListItem inputDatas) {
        String result = "";
        String inputData = "";
        for (int i = 0; i < inputDatas.length(); i++) {
            if (i != 0)
                inputData += "\t";
            inputData += inputDatas.getData(i);
        }

        switch (tableName) {
            case DB_Data.TABLE_USER_INFO:
                result = dbManager.insert_data_toDB(DB_Data.TABLE_USER_INFO, inputData);
                break;
            case DB_Data.TABLE_CHILD_INFO:
                result = dbManager.insert_data_toDB(DB_Data.TABLE_CHILD_INFO, inputData);
                break;
            case DB_Data.TABLE_REWARD_INFO:
                result = dbManager.insert_data_toDB(DB_Data.TABLE_REWARD_INFO, inputData);
                break;
            case DB_Data.TABLE_DATA_INFO:
                result = dbManager.insert_data_toDB(DB_Data.TABLE_DATA_INFO, inputData);
                break;
        }

        return result;
    }

    public String UpdateDataInTable(int tableName, String[] inputDatas, String id) {
        String result = "";
        String inputData = "";
        for (int i = 0; i < inputDatas.length; i++) {
            if (i != 0)
                inputData += "\t";
            inputData += inputDatas[i];
        }

        switch (tableName) {
            case DB_Data.TABLE_USER_INFO:
                result = dbManager.update_data_toDB(DB_Data.TABLE_USER_INFO, inputData, id);
                break;
            case DB_Data.TABLE_CHILD_INFO:
                // Update문 - 연결 방식 저장 (블루투스)
                if(inputDatas.length == 1)
                    result = dbManager.update_data_toDB(DB_Data.TABLE_CHILD_INFO_NAME, inputData, id);
                    // Update문 - 아이 이름, 사진 uri, 배경 사진 uri 변경
                else if (inputDatas.length == 3)
                    result = dbManager.update_data_toDB(DB_Data.TABLE_CHILD_INFO_NAME, inputData, id);
                // Update문 - 블루투스 id, 닉네임, 캐릭터  (태블릿이 블루투스 모드일 경우)
//                else if (inputDatas.length == 3)
//                    result = dbManager.update_data_toDB(DB_Data.TABLE_CHILD_INFO_BLUETOOTHID, inputData, id);
                break;
            case DB_Data.TABLE_REWARD_INFO:
                result = dbManager.update_data_toDB(DB_Data.TABLE_REWARD_INFO, inputData, id);
                break;
            case DB_Data.TABLE_DATA_INFO:
                result = dbManager.update_data_toDB(DB_Data.TABLE_DATA_INFO, inputData, id);
                break;
        }

        return result;
    }

    public String UpdateDataInTable(int tableName, ListItem inputDatas, String id) {
        String result = "";
        String inputData = "";
        for (int i = 0; i < inputDatas.length(); i++) {
            if (i != 0)
                inputData += "\t";
            inputData += inputDatas.getData(i);
        }

        switch (tableName) {
            case DB_Data.TABLE_USER_INFO:
                result = dbManager.update_data_toDB(DB_Data.TABLE_USER_INFO, inputData, id);
                break;
            case DB_Data.TABLE_CHILD_INFO:
                // Update문 - 아이 이름, 사진 uri 변경
                if (inputDatas.length() == 2)
                    result = dbManager.update_data_toDB(DB_Data.TABLE_CHILD_INFO_NAME, inputData, id);
                    // Update문 - 블루투스 id, 닉네임, 캐릭터  (태블릿이 블루투스 모드일 경우)
                else if (inputDatas.length() == 3)
                    result = dbManager.update_data_toDB(DB_Data.TABLE_CHILD_INFO_BLUETOOTHID, inputData, id);
                break;
            case DB_Data.TABLE_REWARD_INFO:
                result = dbManager.update_data_toDB(DB_Data.TABLE_REWARD_INFO, inputData, id);
                break;
            case DB_Data.TABLE_DATA_INFO:
                result = dbManager.update_data_toDB(DB_Data.TABLE_DATA_INFO, inputData, id);
                break;
        }

        return result;
    }

    public String DeleteDataInTable(int tableName, String[] inputDatas, String id) {
        String result = "";
        String inputData = "";
        for (int i = 0; i < inputDatas.length; i++) {
            if (i != 0)
                inputData += "\t";
            inputData += inputDatas[i];
        }

        switch (tableName) {
            case DB_Data.TABLE_USER_INFO:
                break;
            case DB_Data.TABLE_CHILD_INFO:
                break;
            case DB_Data.TABLE_REWARD_INFO:
                result = dbManager.delete_data_toDB(DB_Data.TABLE_REWARD_INFO, inputData, id);
                break;
            case DB_Data.TABLE_DATA_INFO:
                break;
        }

        return result;
    }

    public String DoesChildExist(ListItem inputChildData) {
        String outputChildID = "";

        ArrayList<ListItem> childDataInDB = GetDataFromTable(DB_Data.TABLE_CHILD_INFO);
        for (ListItem childData : childDataInDB) {
            if (childData.getData(DB_Data.INDEX_CHILD_BLUETOOTHID).equals(inputChildData.getData(0)) && childData.getData(DB_Data.INDEX_CHILD_CHARACTER).equals(inputChildData.getData(2))) {
                if (childData.getData(DB_Data.INDEX_CHILD_ID).length() > 0)
                    outputChildID = childData.getData(DB_Data.INDEX_CHILD_ID);
            }
        }

        return outputChildID;
    }

    public void InsertUserItems() {
        ListItem item = new ListItem("i", "p", "홍길동", "닉네임");
        ListItem item1 = new ListItem("i", "p", "???", "asd");
        ListItem item2 = new ListItem("i", "p", "bbb", "sdf");

        userItems = new ArrayList<ListItem>();
        userItems.add(item);
        userItems.add(item1);
        userItems.add(item2);

        this.setUserData(userItems);
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};

        //This method was deprecated in API level 11
        //Cursor cursor = managedQuery(contentUri, proj, null, null, null);

        CursorLoader cursorLoader = new CursorLoader(
                this,
                contentUri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        try {
            int column_index =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            if (cursor.moveToFirst()){
                return cursor.getString(column_index);
            }
        }catch (Exception e) {

        }
        return "";
    }

    public boolean SetImageViewPhoto(Activity activity, ImageView imageView, RelativeLayout relativeLayout) {
        boolean retval = false;
        // 등록된 아이 사진이 있을 경우
        if (this.getChildData().getData(DB_Data.INDEX_CHILD_PHOTO).length() > 0 && !this.getChildData().getData(DB_Data.INDEX_CHILD_PHOTO).equals("null")) {
            this.CheckPermission(activity);
            if (this.getisPermissionGranted()) {
                String imagePath = "";
                imagePath = this.getRealPathFromURI(Uri.parse(this.getChildData().getData(DB_Data.INDEX_CHILD_PHOTO)));

                if(imagePath.isEmpty()){

                    return retval = false;
                }
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = this.getBitmapImageSize();
                Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);

                relativeLayout.setBackground(getDrawable(R.drawable.home_child_photo));
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageBitmap(null);
                imageView.setImageBitmap(bitmap);
                retval = true;
            }
        }
        // 등록된 아이 사진이 없을 경우
        else{
            relativeLayout.setBackground(getDrawable(R.drawable.home_child_photo));
            imageView.setVisibility(View.INVISIBLE);
        }
        return retval;
    }

    public boolean SetImageViewPhoto(Activity activity, ImageView imageView) {
        boolean retval = false;
        // 등록된 아이 사진이 있을 경우
        if (this.getChildData().getData(DB_Data.INDEX_CHILD_PHOTO).length() > 0 && !this.getChildData().getData(DB_Data.INDEX_CHILD_PHOTO).equals("null")) {
            this.CheckPermission(activity);
            if (this.getisPermissionGranted()) {
                String imagePath = "";
                imagePath = this.getRealPathFromURI(Uri.parse(this.getChildData().getData(DB_Data.INDEX_CHILD_PHOTO)));

                if(imagePath.isEmpty()){

                    return retval = false;
                }
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = this.getBitmapImageSize();
                Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);

                imageView.setVisibility(View.VISIBLE);
                imageView.setImageBitmap(null);
                imageView.setImageBitmap(bitmap);
                retval = true;
            }
        }
        // 등록된 아이 사진이 없을 경우
        else{
            imageView.setImageBitmap(null);
            imageView.setImageDrawable(getDrawable(R.drawable.home_child_photo1));
        }
        return retval;
    }

    public boolean SetImageViewBackgroundPhoto(Activity activity, ImageView imageView) {
        boolean retval = false;
        // 등록된 아이 사진이 있을 경우
        if (!TextUtils.isEmpty(this.getChildData().getData(DB_Data.INDEX_CHILD_BACKGROUNDPHOTO))&& !this.getChildData().getData(DB_Data.INDEX_CHILD_BACKGROUNDPHOTO).equals("null")) {
//        if (this.getChildData().getData(DB_Data.INDEX_CHILD_BACKGROUNDPHOTO).length() > 0 && !this.getChildData().getData(DB_Data.INDEX_CHILD_BACKGROUNDPHOTO).equals("null")) {
            this.CheckPermission(activity);
            if (this.getisPermissionGranted()) {
                String imagePath = "";
                imagePath = this.getRealPathFromURI(Uri.parse(this.getChildData().getData(DB_Data.INDEX_CHILD_BACKGROUNDPHOTO)));

                if(imagePath.isEmpty()){
                    return retval = false;
                }
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = this.getBitmapImageSize();
                Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);

                imageView.setVisibility(View.VISIBLE);
                imageView.setImageBitmap(null);
                imageView.setImageBitmap(bitmap);
                imageView.setAdjustViewBounds(true);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                retval = true;
            }
        }
        // 등록된 아이 사진이 없을 경우
        else{
            imageView.setImageBitmap(null);
            imageView.setBackground(getDrawable(R.drawable.home_child_background1));
        }
        return retval;
    }

    public void SetTextViewName(TextView textView) {
        if (!TextUtils.isEmpty(this.getChildData().getData(DB_Data.INDEX_CHILD_NAME)))
//        if (this.getChildData().getData(DB_Data.INDEX_CHILD_NAME).length() > 0)
            textView.setText(this.getChildData().getData(DB_Data.INDEX_CHILD_NAME));
        else
            textView.setText(getString(R.string.none_childName));
    }

    public boolean SetViewCharacter(Activity activity, ImageView imageView, RelativeLayout relativeLayout, TextView textView){
        boolean retval = false;
        if(!TextUtils.isEmpty(this.getChildData().getData(DB_Data.INDEX_CHILD_CHARACTER))){
//        if(!this.getChildData().getData(DB_Data.INDEX_CHILD_CHARACTER).isEmpty()){
            relativeLayout.getBackground().setAlpha(0);
            imageView.setVisibility(ImageView.VISIBLE);

            Drawable color = new ColorDrawable(Color.WHITE);
            LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{color, getDrawable(GetCharacter(this.getChildData().getData(DB_Data.INDEX_CHILD_CHARACTER)))});
            imageView.setImageDrawable(layerDrawable);
        }
        else{
            relativeLayout.setBackground(getDrawable(R.drawable.childselect_character_none1));
            imageView.setVisibility(ImageView.INVISIBLE);
        }
        if(!TextUtils.isEmpty(this.getChildData().getData(DB_Data.INDEX_CHILD_NICKNAME))){
//        if(!this.getChildData().getData(DB_Data.INDEX_CHILD_NICKNAME).isEmpty()){
            textView.setText(this.getChildData().getData(DB_Data.INDEX_CHILD_NICKNAME));
        }
        else{
            textView.setText(getString(R.string.none_nickName));
        }
        return retval;
    }

    public boolean SetViewCharacter(Activity activity, ImageView imageView, TextView textView){
        boolean retval = false;
        if(!TextUtils.isEmpty(this.getChildData().getData(DB_Data.INDEX_CHILD_CHARACTER))){
//        if(!this.getChildData().getData(DB_Data.INDEX_CHILD_CHARACTER).isEmpty()){
            imageView.setVisibility(ImageView.VISIBLE);

            Drawable color = new ColorDrawable(Color.WHITE);
            LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{color, getDrawable(GetCharacter(this.getChildData().getData(DB_Data.INDEX_CHILD_CHARACTER)))});
            imageView.setImageDrawable(layerDrawable);
        }
        else{
            imageView.setVisibility(ImageView.INVISIBLE);
        }
        if(!TextUtils.isEmpty(this.getChildData().getData(DB_Data.INDEX_CHILD_NICKNAME))){
//        if(!this.getChildData().getData(DB_Data.INDEX_CHILD_NICKNAME).isEmpty()){
            textView.setText(this.getChildData().getData(DB_Data.INDEX_CHILD_NICKNAME));
        }
        else{
            textView.setText(getString(R.string.none_nickName));
        }
        return retval;
    }
    public int GetCharacter(String characterName) {
        int resultID = -1;
        switch (characterName) {
            case DB_Data.STRING_CHARACTER_MEERKAT:
                resultID = R.drawable.chichi_icon;
                break;
            case DB_Data.STRING_CHARACTER_CROCODILE:
                resultID = R.drawable.kaka_icon;
                break;
            case DB_Data.STRING_CHARACTER_ELEPHANT:
                resultID = R.drawable.foo_icon;
                break;
            default:
                break;
        }
        return resultID;
    }
    public void CheckPermission(Activity activity) {
        String photoUri = "";
    /* 사용자의 OS 버전이 마시멜로우 이상인지 체크한다. */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 갤러리 사용 권한 체크 (사용권한이 없을 경우 -1)
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // 권한이 없을 경우

                // 최초 권한 요청인지, 혹은 사용자에 의한 재요청인지 확인
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    // 사용자가 임의로 권한을 취소시킨 경우
                    // 권한 재요청
                    ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                } else {
                    // 최초로 권한을 요청하는 경우 (첫 실행)
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                }
            } else {
                // 사용 권한이 있음을 확인한 경우
                setIsPermissionGranted(true);
            }
        }
    }

    /**
     * 사용자가 권한을 허용했는지 거부했는지 체크
     *
     * @param requestCode  1000번
     * @param permissions  개발자가 요청한 권한들
     * @param grantResults 권한에 대한 응답들
     *                     permissions와 grantResults는 인덱스 별로 매칭된다.
     */
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_CONTACTS) {

            /* 요청한 권한을 사용자가 "허용"했다면 인텐트를 띄워라
                내가 요청한 게 하나밖에 없기 때문에. 원래 같으면 for문을 돈다.*/
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setIsPermissionGranted(true);
            } else {
                Toast.makeText(this, "권한 요청을 거부했습니다.", Toast.LENGTH_SHORT).show();
                setIsPermissionGranted(false);
            }

        }
    }
}
