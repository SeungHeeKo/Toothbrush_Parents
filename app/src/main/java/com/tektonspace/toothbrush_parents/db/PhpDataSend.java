package com.tektonspace.toothbrush_parents.db;

import android.os.AsyncTask;

import com.tektonspace.toothbrush_parents.constants.DB_Data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

/**
 * Created by SeungHee on 2017-04-12.
 * DB에 정보를 저장하는 클래스
 */

public class PhpDataSend extends AsyncTask<String, Void, String> {
    // datas[0] : url
    // datas[1] : DB에 저장될 데이터
    // datas[2] : DB Update시, 테이블의 id값
    @Override
    protected String doInBackground(String... datas) {
        try {
            URL url = new URL(datas[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // 안드로이드에서 송신만 가능하도록
            connection.setDoInput(true);
            connection.setDoOutput(false);
            connection.setUseCaches(false);

            // 문자열로 이루어진 데이터를 서버에 POST 방식으로 전송
            connection.setRequestMethod("POST");

            List<String> sendingData = Arrays.asList(datas[1].split("\t"));
            String param = "";

            switch (VerifyURL(datas[0])) {
                case DB_Data.TABLE_USER_INFO: // UserInfo 테이블
                    if(datas.length > 2){
                        // Update문 - 비밀번호 초기화
                        if (sendingData.size() == 1) {
                            param = "Password=" + sendingData.get(0) + "&MailAddress=" + datas[2];
                        }
                        // Update문 - 아이 정보 추가
                        else {  // ChildID도 WHERE 조건 중 하나
                            param = "ChildName=" + sendingData.get(0) + "&ChildID=" + sendingData.get(1) + "&MailAddress=" + datas[2];
                        }
                    }
                    else{
                        if(sendingData.size() == 3) {
                            // Insert문 - 계정 생성
                            param = "MailAddress=" + sendingData.get(0) + "&Password=" + sendingData.get(1) + "&UserID=" + sendingData.get(2);
                        }
                        else if(sendingData.size() == 4){
                            // Insert문 - ChildID 추가 계정 생성
                            param = "MailAddress=" + sendingData.get(0) + "&Password=" + sendingData.get(1) + "&ChildID=" + sendingData.get(2)+ "&UserID=" + sendingData.get(3);
                        }
                    }
                    break;
                case DB_Data.TABLE_CHILD_INFO: // ChildInfo 테이블
                    if(datas.length > 2){ // Update문 - 블루투스 id, 닉네임, 캐릭터  (태블릿이 블루투스 모드일 경우)
//                        if (sendingData.size() == 3) {
//                            param = "BluetoothID=" + sendingData.get(0) + "&Nickname=" + sendingData.get(1) + "&Character=" + sendingData.get(2) + "&id=" + datas[2];
//                            // Update문 - 아이 이름, 사진 uri 변경
//                        } else if (sendingData.size() == 2) {
                        if (sendingData.size() == 3) {
                            param = "Name=" + sendingData.get(0) + "&Photo=" + sendingData.get(1) + "&BackgroundPhoto=" + sendingData.get(2) + "&id=" + datas[2];
                        }
                    }
                    // Insert문 - 블루투스 id, 닉네임, 캐릭터  (태블릿이 블루투스 모드일 경우)
                    else
                        param = "BluetoothID=" + sendingData.get(0) + "&Nickname=" + sendingData.get(1) + "&Character=" + sendingData.get(2);

                    break;
                case DB_Data.TABLE_REWARD_INFO: // RewardInfo 테이블
                    // Insert문
                    if (datas.length <= 2) {
                        param = "ChildID=" + sendingData.get(0) + "&Reward_detail=" + sendingData.get(1) + "&Reward_current=" + sendingData.get(2) + "&Reward_total=" + sendingData.get(3);
                    }
                    // Update문_초기화
                    else if (datas[1].contains("reset")) {
                        param = "ChildID=" + datas[2];
                    }
                    else if(datas[1].contains("delete")){
                        param = "ChildID=" + datas[2];
                    }
                    // Update문_정보 변경
                    else {
                        param = "Reward_detail=" + sendingData.get(0) + "&Reward_total=" + sendingData.get(1) + "&ChildID=" + datas[2];
                    }
                    break;
                case DB_Data.TABLE_DATA_INFO: // ToothbrushDataInfo 테이블
                    break;

            }

            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(param.getBytes());
            outputStream.flush();
            outputStream.close();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String line = null;

            // Read Server Response
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                break;
            }
            return sb.toString();

        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    protected void onPostExecute(String results) {
        super.onPostExecute(results);
    }


    private int VerifyURL(String url) {
        int retval = 0;

        if (url.contains("userInfo"))
            retval = DB_Data.TABLE_USER_INFO;
        else if (url.contains("childInfo"))
            retval = DB_Data.TABLE_CHILD_INFO;
        else if (url.contains("rewardInfo"))
            retval = DB_Data.TABLE_REWARD_INFO;
        else if (url.contains("toothbrushDataInfo"))
            retval = DB_Data.TABLE_DATA_INFO;

        return retval;
    }
}
