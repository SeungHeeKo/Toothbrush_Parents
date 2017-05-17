package com.tektonspace.toothbrush_parents.db;

import android.app.LauncherActivity;
import android.os.AsyncTask;

import com.tektonspace.toothbrush_parents.constants.DB_Data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by SeungHee on 2017-04-12.
 * DB에서 정보를 받아오는 클래스
 */

public class PhpDataReceive extends AsyncTask<String, Integer, String> {

    @Override
    protected String doInBackground(String... urls) {
        StringBuilder jsonHtml = new StringBuilder();
        try {
            String url_string = urls[0];
            if (urls.length > 1) {    // 조건문이 붙은 쿼리일 경우
                switch (urls[0]){
                    case DB_Data.URL_GET_USERINFO_ID:
                        url_string += "?UserID=";
                        url_string += urls[1];
                        break;
                    case DB_Data.URL_GET_USERINFO_MAIL_ID:
                        url_string += "?MailAddress=";
                        url_string += urls[1];
                        url_string += "&UserID=";
                        url_string += urls[2];
                        break;
                    case DB_Data.URL_GET_REWARDINFO:
                        url_string += "?ChildID=";
                        url_string += urls[1];
                        break;
                    case DB_Data.URL_GET_DATAINFO:
                        url_string += "?ChildID=";
                        url_string += urls[1];
                        break;
                    case DB_Data.URL_GET_DATAINFO_DATE:
                    url_string += "?ChildID=";
                    url_string += urls[1];
                    url_string += "&Date=";
                    url_string += urls[2];
                        break;
                    default:
                        url_string += "?id=";
                        url_string += urls[1];
                        break;
                }
//                if (urls[0].equals(DB_Data.URL_GET_REWARDINFO)) {
//                    url_string += "?ChildID=";
//                    url_string += urls[1];
//                } else if (urls[0].equals(DB_Data.URL_GET_DATAINFO)) {
//                    url_string += "?ChildID=";
//                    url_string += urls[1];
//
//                } else if (urls[0].equals(DB_Data.URL_GET_DATAINFO_DATE)) {
//                    url_string += "?ChildID=";
//                    url_string += urls[1];
//                    url_string += "&Date=";
//                    url_string += urls[2];
//                } else if(urls[0].equals(DB_Data.URL_GET_USERINFO_MAIL_ID)){
//                    url_string += "?MailAddress=";
//                    url_string += urls[1];
//                    url_string += "&UserID=";
//                    url_string += urls[2];
//                }
//                else {
//                    url_string += "?id=";
//                    url_string += urls[1];
//                }
            }
            // 연결 url 설정
            URL url = new URL(url_string);
            // 커넥션 객체 생성
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // 연결 성공시
            if (connection != null) {
                connection.setConnectTimeout(10000);
                connection.setUseCaches(false);
                // 연결 후 코드가 리턴되었을 경우
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                    for (; ; ) {
                        // 웹상에 보여지는 텍스트를 라인단위로 읽어 저장
                        String line = bufferedReader.readLine();
                        if (line == null) break;

                        // 저장된 텍스트 라인을 jsonHtml에 붙여넣음
                        jsonHtml.append(line + "\n");
                    }
                    bufferedReader.close();
                }
                connection.disconnect();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return jsonHtml.toString();
    }

    protected void onPostExecute(String string) {
        super.onPostExecute(string);
    }
}
