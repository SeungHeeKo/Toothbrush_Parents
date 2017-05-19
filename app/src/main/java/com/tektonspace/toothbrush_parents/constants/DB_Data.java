package com.tektonspace.toothbrush_parents.constants;

import java.util.ArrayList;

/**
 * Created by SeungHee on 2017-04-12.
 * 전체 Activity가 공통적으로 접근 및 사용하는 변수들
 */

public interface DB_Data {
    // ToothbrushData DB 접속 IP
    public final String publicIP = "http://52.78.108.174/";

    // 각 테이블별 접근 int변수
    public static final int TABLE_USER_INFO = 10;
    public static final int TABLE_CHILD_INFO = 11;
    public static final int TABLE_REWARD_INFO = 12;
    public static final int TABLE_DATA_INFO = 13;

    public static final int TABLE_CHILD_INFO_NAME = 14;
    public static final int TABLE_CHILD_INFO_BLUETOOTHID = 15;
    public static final int TABLE_USER_INFO_CHILDID = 16;

    // row별로 데이터를 저장하기 위해 ListItem에 저장될 인덱스 분류
    public static final int INDEX_USER_ID = 0;
    public static final int INDEX_USER_MAILADDRESS = 1;
    public static final int INDEX_USER_PASSWORD = 2;
    public static final int INDEX_USER_CHILDNAME = 3;
    public static final int INDEX_USER_CHILDID = 4;

    public static final int INDEX_CHILD_ID = 0;
    public static final int INDEX_CHILD_NAME = 1;
    public static final int INDEX_CHILD_BLUETOOTHID = 2;
    public static final int INDEX_CHILD_NICKNAME = 3;
    public static final int INDEX_CHILD_CHARACTER = 4;
    public static final int INDEX_CHILD_PHOTO = 5;
    public static final int INDEX_CHILD_CONNECTMODE = 6;
    public static final int INDEX_CHILD_BACKGROUNDPHOTO = 7;

    public static final int INDEX_REWARD_ID = 0;
    public static final int INDEX_REWARD_CHILDID = 1;
    public static final int INDEX_REWARD_REWARD = 2;
    public static final int INDEX_REWARD_CURRENT = 3;
    public static final int INDEX_REWARD_TOTAL = 4;

    public static final int INDEX_DATA_ID = 0;
    public static final int INDEX_DATA_CHILDID = 1;
    public static final int INDEX_DATA_DATE = 2;
    public static final int INDEX_DATA_TIME = 3;
//    public static final int INDEX_DATA_ZONE

    public static final int PAGE_NUM_MOM = 4;
    public static final int PAGE_NUM_KID = 4;
    public static final int PAGE_NUM_CONNECT = 5;
    public static final int PAGE_NUM_TOOTHBRUSH = 4;


    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    public static final int POPUP_SETTING = 1;
    public static final int POPUP_SETTING_RESET = 2;
    public static final int POPUP_SETTING_CHANGE = 3;
    public static final int POPUP_INSTRUCTION_WIFI = 4;
    public static final int POPUP_INSTRUCTION_BLUETOOTH = 5;
    public static final int POPUP_REWARD_SETTING = 6;
    public static final int POPUP_REWARD_SETTING_RESET = 7;
    public static final int POPUP_REWARD_SETTING_NONE = 8;
    public static final int POPUP_REWARD_SETTING_STOP = 9;
    public static final int POPUP_DATA_SETTING = 10;


    // 생성된 계정 정보를 UserInfo 테이블에 저장할 도메인 주소
    public final String URL_INSERT_USERINFO = publicIP + "insert_userInfo.php";
    // UserInfo 테이블에 계정 정보, ChildID를 저장할 도메인 주소
    public final String URL_INSERT_USERINFO_CHILDID = publicIP + "insert_userInfo_ChildID.php";
    // 아이 정보를 ChildInfo 테이블에 저장할 도메인 주소
    public final String URL_INSERT_CHILDINFO = publicIP + "insert_childInfo.php";
    // 보상 정보를 RewardInfo 테이블에 저장할 도메인 주소
    public final String URL_INSERT_REWARDINFO = publicIP + "insert_rewardInfo.php";

    // UserInfo 테이블에 존재하는 값 전체를 받아오는 도메인 주소
    public final String URL_GET_USERINFO = publicIP + "get_userInfo.php";
    // UserInfo 테이블에 존재하는 값 전체를 받아오는 도메인 주소
    public final String URL_GET_USERINFO_MAIL_ID = publicIP + "get_userInfo_MailAddress,UserID.php";
    // UserInfo 테이블에 존재하는 값 전체를 받아오는 도메인 주소
    public final String URL_GET_USERINFO_ID = publicIP + "get_userInfo_UserID.php";
    // ChildInfo 테이블에 존재하는 값 전체를 받아오는 도메인 주소
    public final String URL_GET_CHILDINFO = publicIP + "get_childInfo.php";
    // ChildInfo 테이블에 id값을 가진 값 전체를 받아오는 도메인 주소
    public final String URL_GET_CHILDINFO_ID = publicIP + "get_childInfo_id.php";
    // RewardInfo 테이블에 존재하는 값 전체를 받아오는 도메인 주소
    public final String URL_GET_REWARDINFO = publicIP + "get_rewardInfo.php";
    // ChildID 값을 가진 ToothbrushDataInfo 테이블에 존재하는 값 전체를 받아오는 도메인 주소
    public final String URL_GET_DATAINFO = publicIP + "get_toothbrushDataInfo_ChildID.php";
    // ChildID,date 값을 가진 ToothbrushDataInfo 테이블에 존재하는 값 전체를 받아오는 도메인 주소
    public final String URL_GET_DATAINFO_DATE = publicIP + "get_toothbrushDataInfo_ChildID,Date.php";

    // UserInfo 테이블의 아이 정보 추가 / 변경
    public final String URL_UPDATE_USERINFO = publicIP + "update_userInfo.php";
    // UserInfo 테이블의 비밀번호 변경
    public final String URL_UPDATE_USERINFO_PASSWORD = publicIP + "update_userInfo_Password.php";
    // ChildInfo 테이블의 아이 이름, 아이 사진 uri 추가 / 변경
    public final String URL_UPDATE_CHILDINFO_NAME = publicIP + "update_childInfo_Name.php";
    // ChildInfo 테이블의 아이 이름, 아이 사진 uri 추가 / 변경
    public final String URL_UPDATE_CHILDINFO_BLUETOOTHID = publicIP + "update_childInfo_BluetoothID.php";
    // RewardInfo 테이블의 누적 횟수 초기화
    public final String URL_UPDATE_RESET_REWARDINFO = publicIP + "update_reset_rewardInfo.php";
    // RewardInfo 테이블의 보상 내용, 총 보상 횟수 변경
    public final String URL_UPDATE_REWARDINFO = publicIP + "update_rewardInfo.php";

    // RewardInfo 테이블에 존재하는 특정 값 삭제
    public final String URL_DELETE_REWARDINFO = publicIP + "delete_rewardInfo.php";

    public final String DATA_CORRECT = "DATA_CORRECT";

    // Bundle 혹은 Intent로 전달될 KEY 목록
    public final String STRING_USER_ID = "USER_ID";
    public final String STRING_USER_MAILADDRESS = "USER_MAILADDRESS";
    public final String STRING_USER_PASSWORD  = "USER_PASSWORD";
    public final String STRING_CHILD_NAME = "STRING_CHILD_NAME";
    public final String STRING_CHILD_NICKNAME = "STRING_CHILD_NICKNAME";
    public final String STRING_CHILD_CHARACTER = "STRING_CHILD_CHARACTER";
    public final String STRING_CHILD_CONNECTMODE = "STRING_CHILD_CONNECTMODE";
    public final String STRING_CHILD_ID = "STRING_CHILD_ID";
    public final String STRING_CHILD_PHOTO = "STRING_CHILD_PHOTO";
    public final String STRING_CHILD_BACKGROUNDPHOTO = "STRING_CHILD_BACKGROUNDPHOTO";
    public final String STRING_CHILD_BLUETOOTHID = "STRING_CHILD_BLUETOOTHID";
    public final String INT_TOTAL_CHILD_NUM = "TOTAL_CHILD_NUM";
    public final String STRING_INSTRUCTION = "STRING_INSTRUCTION";
    public final String STRING_INSTRUCTION_MOM = "STRING_INSTRUCTION_MOM";
    public final String STRING_INSTRUCTION_KID = "STRING_INSTRUCTION_KID";
    public final String STRING_INSTRUCTION_CONNECT = "STRING_INSTRUCTION_CONNECT";
    public final String STRING_INSTRUCTION_TOOTHBRUSH = "STRING_INSTRUCTION_TOOTHBRUSH";
    public final String STRING_FRAGMENT = "STRING_FRAGMENT";
    public final String STRING_RESET_PASSWORD = "STRING_RESET_PASSWORD";
    public final String STRING_IS_HOME_BUTTON_PRESSED = "STRING_IS_HOME_BUTTON_PRESSED";

    public final String STRING_CHARACTER_MEERKAT = "Chichi";
    public final String STRING_CHARACTER_CROCODILE = "Kaka";
    public final String STRING_CHARACTER_ELEPHANT = "Foo";

    public final String STRING_DB_RESULT_OK = "1 record added";
    public final String STRING_DB_UPDATE_RESULT_OK = "1 record updated";

    public boolean IS_TEST_VERSION = false;


}
