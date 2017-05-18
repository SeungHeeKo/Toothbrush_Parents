package com.tektonspace.toothbrush_parents.utils;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.tektonspace.toothbrush_parents.R;
import com.tektonspace.toothbrush_parents.activities.ChildDetailActivity;
import com.tektonspace.toothbrush_parents.activities.InstructionActivity;
import com.tektonspace.toothbrush_parents.activities.RewardSystemActivity;
import com.tektonspace.toothbrush_parents.activities.ToothbrushDataActivity;
import com.tektonspace.toothbrush_parents.adapter.InstructionPagerAdapter;
import com.tektonspace.toothbrush_parents.constants.DB_Data;
import com.tektonspace.toothbrush_parents.db.VerifyUserInfo;

import java.sql.Time;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by SeungHee on 2017-04-18.
 * 이벤트별 팝업창
 */
public class PopupDialog extends Dialog {
    // 이벤트별로 팝업창을 띄우기 위한 인덱스
    int popup_index = -1;

    // 경고 팝업창 객체
    ImageView message_imageView;
    Button ok_button;
    int imageResource = -1;

    // 세팅 팝업창 객체
    Button reset_button, change_button, popup_setting_close_button;

    // 아이 정보 초기화 설명 팝업창 객체
    TextView popup_textView;
    ViewPager viewPager;
    Button popup_left_button, popup_right_button;
    int currPageNum = 1;
    int firstPageNum = 0;
    int lastPageNum;
    // 설명 이미지를 담는 PagerAdapter
    InstructionPagerAdapter instructionPagerAdapter;

    // 아이 정보 초기화 설명 리소스
    int[] resetResources = {
            R.drawable.instruct1,
            R.drawable.instruct2,
            R.drawable.instruct3
    };
    // 접속 방법 초기화 설명 리소스
    int[] changeResources = {

    };
    int[] currResources = {};
    // 와이파이 모드 설정 방법 설명 리소스
    int[] wifiResources = {
    };
    int[] bluetoothResources = {};

    // 보상 설정 팝업창 객체
    Button reward_num_left_button, reward_num_right_button, reward_totalNum_reset_button, reward_ok_button, reward_cancel_button, reward_stop_reward_button;
    EditText reward_num_editText, reward_content_editText;
    // 보상 횟수 설정
    int rewardNum = 0;
    // 키보드 사라짐 효과
    InputMethodManager inputMethodManager;

    // 보상 설정_누적 횟수 초기화 팝업창 객체
    TextView reward_reset_textView;
    Button reward_reset_ok_button, reward_reset_cancel_button;

    // 양치 데이터 확인 설정 팝업창 객체
    EditText data_limit_time_editText;
    Button data_limit_time_left_button, data_limit_time_right_button;
    Button data_morning_left_button, data_morning_right_button, data_afternoon_left_button, data_afternoon_right_button, data_evening_left_button, data_evening_right_button;
    Button data_morning_button, data_afternoon_button, data_evening_button;
    Button data_setting_ok_button, data_setting_cancel_button;
    String currTimeString = "";
    String[] currTimeStringArray;
    int currTimeInt = 0;
    int dataLimitTimeInterval = 5;
    int dataTimeInterval = 30;
    int hour = 0, min = 0;
    String AM_PM = "";
    boolean isAM = false;
    Calendar calendar;

    // 버튼 클릭 이벤트
    private View.OnClickListener mLeftClickListener;
    private View.OnClickListener mRightClickListener;
    private View.OnClickListener thirdClickListener;

    VerifyUserInfo verifyUserInfo;
    Editable editable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 다이얼로그 외부 화면 흐리게 표현
        BlurOutsideActivity();

        verifyUserInfo = (VerifyUserInfo) getContext().getApplicationContext();

        currPageNum = 0;
        switch (popup_index) {
            case DB_Data.POPUP_SETTING: // 설정 팝업창
                InstantiateSettingPopup();
                break;
            case DB_Data.POPUP_SETTING_RESET:   // 설정_아이 정보 초기화 팝업창
                currResources = resetResources;
                InstantiateSettingDetailPopup();
                break;
            case DB_Data.POPUP_SETTING_CHANGE:  // 설정_접속방법 초기화 팝업창
                currResources = changeResources;
                InstantiateSettingDetailPopup();
                break;
            case DB_Data.POPUP_INSTRUCTION_WIFI:
                currResources = wifiResources;
                InstantiateSettingDetailPopup();
                break;
            case DB_Data.POPUP_INSTRUCTION_BLUETOOTH:
                currResources = bluetoothResources;
                InstantiateSettingDetailPopup();
                break;
            case DB_Data.POPUP_REWARD_SETTING:  // 보상 설정 팝업창
                InstantiateRewardSettingPopup();
                break;
            case DB_Data.POPUP_REWARD_SETTING_RESET:    // 보상 설정_누적 횟수 초기화 팝업창
                InstantiateRewardResetPopup();
                break;
            case DB_Data.POPUP_REWARD_SETTING_NONE:    // 보상 설정_첫 보상 설정 팝업창
                InstantiateRewardNonePopup();
                break;
            case DB_Data.POPUP_REWARD_SETTING_STOP:    // 보상 설정_첫 보상 설정 팝업창
                InstantiateRewardStopPopup();
                break;
            case DB_Data.POPUP_DATA_SETTING:
                InstantiateDataSettingPopup();
                break;
            default:    // 경고 팝업창
                InstantiateAlertPopup();
                break;
        }
    }

    private void InstantiateAlertPopup() {
        setContentView(R.layout.dialog_alert_popup);
        message_imageView = (ImageView) findViewById(R.id.popup_error_message_imageView);
        ok_button = (Button) findViewById(R.id.popup_ok_button);

        message_imageView.setImageResource(imageResource);

        if (mLeftClickListener != null && mRightClickListener == null) {
            ok_button.setOnClickListener(mLeftClickListener);
        }
    }

    private void InstantiateSettingPopup() {
        setContentView(R.layout.dialog_setting_popup);
        reset_button = (Button) findViewById(R.id.popup_setting_reset_button);
        change_button = (Button) findViewById(R.id.popup_setting_change_button);
        popup_setting_close_button = (Button) findViewById(R.id.popup_setting_close_button);

        if (mLeftClickListener != null && mRightClickListener != null) {
            reset_button.setOnClickListener(mLeftClickListener);
            change_button.setOnClickListener(mRightClickListener);
            popup_setting_close_button.setOnClickListener(thirdClickListener);
        }

    }

    private void InstantiateSettingDetailPopup() {
        setContentView(R.layout.dialog_setting_reset_popup);
        popup_textView = (TextView) findViewById(R.id.popup_textView);
        viewPager = (ViewPager) findViewById(R.id.popup_instruction_viewPager);
        popup_left_button = (Button) findViewById(R.id.popup_instruction_left_button);
        popup_right_button = (Button) findViewById(R.id.popup_instruction_right_button);

        if (popup_index == DB_Data.POPUP_INSTRUCTION_WIFI)
            popup_textView.setText(R.string.title_instruction_wifi);
        else if (popup_index == DB_Data.POPUP_INSTRUCTION_BLUETOOTH)
            popup_textView.setText(R.string.title_instruction_bluetooth);


        lastPageNum = currResources.length - 1;

        instructionPagerAdapter = new InstructionPagerAdapter(getContext(), currResources);
        viewPager.setAdapter(instructionPagerAdapter);

        PageChangeListener pageChangeListener = new PageChangeListener();
        viewPager.addOnPageChangeListener(pageChangeListener);

        // BtnOnClickListener의 객체 생성.
        BtnOnClickListener onClickListener = new BtnOnClickListener();
        popup_left_button.setOnClickListener(onClickListener);
        popup_right_button.setOnClickListener(onClickListener);

    }

    private void InstantiateRewardSettingPopup() {
        setContentView(R.layout.dialog_reward_setting_popup);
        reward_num_left_button = (Button) findViewById(R.id.reward_num_left_button);
        reward_num_right_button = (Button) findViewById(R.id.reward_num_right_button);
        reward_totalNum_reset_button = (Button) findViewById(R.id.reward_totalNum_reset_button);
        reward_stop_reward_button = (Button) findViewById(R.id.reward_stop_reward_button);
        reward_ok_button = (Button) findViewById(R.id.reward_ok_button);
        reward_cancel_button = (Button) findViewById(R.id.reward_cancel_button);
        reward_num_editText = (EditText) findViewById(R.id.reward_num_editText);
        reward_content_editText = (EditText) findViewById(R.id.reward_content_editText);
        inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        if (!TextUtils.isEmpty(verifyUserInfo.getRewardData().getData(DB_Data.INDEX_REWARD_REWARD))) {
            reward_content_editText.setText(verifyUserInfo.getRewardData().getData(DB_Data.INDEX_REWARD_REWARD));
            // 커서 위치 제일 마지막으로 이동
            editable = reward_content_editText.getText();
            Selection.setSelection(editable, editable.length());
        }
        if (!TextUtils.isEmpty(verifyUserInfo.getRewardData().getData(DB_Data.INDEX_REWARD_TOTAL))) {
            reward_num_editText.setText(verifyUserInfo.getRewardData().getData(DB_Data.INDEX_REWARD_TOTAL));
            // 커서 위치 제일 마지막으로 이동
            editable = reward_num_editText.getText();
            Selection.setSelection(editable, editable.length());
        }

        // 보상 내용 editText에서 키보드 엔터 버튼 클릭시 키보드가 사라지도록
        reward_content_editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId == EditorInfo.IME_ACTION_DONE) || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    hideKeyboard();
                }
                return false;
            }
        });

        // EditText 값 변경 이벤트 탐지
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String string = editable.toString();
                if (string.length() > 8) {
                    // 보상 내용을 9자 이상 입력했을 경우 경고 토스트 메세지 띄우고 8자로 줄임
                    toastMessage("8자 이내로 입력해 주세요.");
                    String currText = reward_content_editText.getText().toString();
                    reward_content_editText.setText(currText.substring(0, currText.length()-1));
                    // 커서 위치 제일 마지막으로 이동
                    editable = reward_content_editText.getText();
                    Selection.setSelection(editable, editable.length());

                }
            }
        };

        // 비밀번호 확인란 EditText 값 변경 이벤트 탐지
        reward_content_editText.addTextChangedListener(textWatcher);
        reward_num_editText.setFocusable(false);
        reward_num_editText.setClickable(false);

        // BtnOnClickListener의 객체 생성.
        BtnOnClickListener onClickListener = new BtnOnClickListener();
        reward_num_left_button.setOnClickListener(onClickListener);
        reward_num_right_button.setOnClickListener(onClickListener);
        reward_ok_button.setOnClickListener(onClickListener);

        if (mLeftClickListener != null && mRightClickListener != null && thirdClickListener != null) {
            reward_totalNum_reset_button.setOnClickListener(mLeftClickListener);
            reward_cancel_button.setOnClickListener(mRightClickListener);
            reward_stop_reward_button.setOnClickListener(thirdClickListener);
        }

    }

    private void hideKeyboard() {
        inputMethodManager.hideSoftInputFromWindow(reward_content_editText.getWindowToken(), 0);
        inputMethodManager.hideSoftInputFromWindow(reward_content_editText.getWindowToken(), 0);
    }

    private void InstantiateRewardResetPopup() {
        setContentView(R.layout.dialog_reward_reset_popup);
        reward_reset_textView = (TextView) findViewById(R.id.reward_reset_textView);
        reward_reset_ok_button = (Button) findViewById(R.id.reward_reset_ok_button);
        reward_reset_cancel_button = (Button) findViewById(R.id.reward_reset_cancel_button);

        if (!verifyUserInfo.getRewardData().getData(DB_Data.INDEX_REWARD_CURRENT).isEmpty()) {
            if (Integer.parseInt(verifyUserInfo.getRewardData().getData(DB_Data.INDEX_REWARD_CURRENT)) >= Integer.parseInt(verifyUserInfo.getRewardData().getData(DB_Data.INDEX_REWARD_TOTAL))) {
                reward_reset_textView.setText(getContext().getString(R.string.confirm_reward_reset));
            } else {
                reward_reset_textView.setText(getContext().getString(R.string.alert_reward_reset));
            }
        }
        if (mLeftClickListener != null && mRightClickListener != null) {
            reward_reset_ok_button.setOnClickListener(mLeftClickListener);
            reward_reset_cancel_button.setOnClickListener(mRightClickListener);
        }
    }

    private void InstantiateRewardNonePopup() {
        setContentView(R.layout.dialog_reward_reset_popup);
        reward_reset_textView = (TextView) findViewById(R.id.reward_reset_textView);
        reward_reset_ok_button = (Button) findViewById(R.id.reward_reset_ok_button);
        reward_reset_cancel_button = (Button) findViewById(R.id.reward_reset_cancel_button);


        reward_reset_textView.setText(getContext().getString(R.string.confirm_reward_none));
        if (mLeftClickListener != null && mRightClickListener != null) {
            reward_reset_ok_button.setOnClickListener(mLeftClickListener);
            reward_reset_cancel_button.setOnClickListener(mRightClickListener);
        }
    }

    private void InstantiateRewardStopPopup() {
        setContentView(R.layout.dialog_reward_reset_popup);
        reward_reset_textView = (TextView) findViewById(R.id.reward_reset_textView);
        reward_reset_ok_button = (Button) findViewById(R.id.reward_reset_ok_button);
        reward_reset_cancel_button = (Button) findViewById(R.id.reward_reset_cancel_button);


        reward_reset_textView.setText(getContext().getString(R.string.confirm_reward_stop));
        if (mLeftClickListener != null && mRightClickListener != null) {
            reward_reset_ok_button.setOnClickListener(mLeftClickListener);
            reward_reset_cancel_button.setOnClickListener(mRightClickListener);
        }
    }

    private void InstantiateDataSettingPopup() {
        setContentView(R.layout.dialog_data_setting_popup);
        data_limit_time_editText = (EditText) findViewById(R.id.data_limit_time_editText);
        data_limit_time_left_button = (Button) findViewById(R.id.data_limit_time_left_button);
        data_limit_time_right_button = (Button) findViewById(R.id.data_limit_time_right_button);

        data_morning_button = (Button) findViewById(R.id.data_morning_button);
        data_afternoon_button = (Button) findViewById(R.id.data_afternoon_button);
        data_evening_button = (Button) findViewById(R.id.data_evening_button);
        data_morning_left_button = (Button) findViewById(R.id.data_morning_left_button);
        data_morning_right_button = (Button) findViewById(R.id.data_morning_right_button);
        data_afternoon_left_button = (Button) findViewById(R.id.data_afternoon_left_button);
        data_afternoon_right_button = (Button) findViewById(R.id.data_afternoon_right_button);
        data_evening_left_button = (Button) findViewById(R.id.data_evening_left_button);
        data_evening_right_button = (Button) findViewById(R.id.data_evening_right_button);

        data_limit_time_editText.setText(verifyUserInfo.getDataLimit());
        // 커서 위치 제일 마지막으로 이동
        editable = data_limit_time_editText.getText();
        Selection.setSelection(editable, editable.length());
        data_morning_button.setText(verifyUserInfo.getDataMorning());
        data_afternoon_button.setText(verifyUserInfo.getDataAfternoon());
        data_evening_button.setText(verifyUserInfo.getDataEvening());

        data_setting_ok_button = (Button) findViewById(R.id.data_setting_ok_button);

        // editText 읽기 전용
        data_limit_time_editText.setFocusable(false);
        data_limit_time_editText.setClickable(false);
        // BtnOnClickListener의 객체 생성.
        BtnOnClickListener onClickListener = new BtnOnClickListener();
        data_limit_time_left_button.setOnClickListener(onClickListener);
        data_limit_time_right_button.setOnClickListener(onClickListener);
        data_morning_button.setOnClickListener(onClickListener);
        data_afternoon_button.setOnClickListener(onClickListener);
        data_evening_button.setOnClickListener(onClickListener);
        data_morning_left_button.setOnClickListener(onClickListener);
        data_morning_right_button.setOnClickListener(onClickListener);
        data_afternoon_left_button.setOnClickListener(onClickListener);
        data_afternoon_right_button.setOnClickListener(onClickListener);
        data_evening_left_button.setOnClickListener(onClickListener);
        data_evening_right_button.setOnClickListener(onClickListener);
        data_setting_ok_button.setOnClickListener(onClickListener);

        calendar = Calendar.getInstance();
        hour = 0;
        min = 0;

        // EditText 값 변경 이벤트 탐지
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String string = editable.toString();
                if (string.length() > 0) {
                    // 제한 시간 입력과 동시에 사용자가 60분 이상의 값을 입력하는지 확인
                    DataPopupCheckLimitTime();
                }
            }
        };

        // 제한 시간 EditText 값 변경 이벤트 탐지
        data_limit_time_editText.addTextChangedListener(textWatcher);

    }

    class PageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            currPageNum = position;
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    class BtnOnClickListener implements Button.OnClickListener {
        @Override
        public void onClick(View view) {
            try {
                switch (view.getId()) {
                    case R.id.popup_instruction_left_button:
                        InstructionPopupButtonClickListener(R.id.popup_instruction_left_button);
                        break;
                    case R.id.popup_instruction_right_button:
                        InstructionPopupButtonClickListener(R.id.popup_instruction_right_button);
                        break;
                    case R.id.reward_num_left_button:
                        RewardPopupButtonClickListener(R.id.reward_num_left_button);
                        break;
                    case R.id.reward_num_right_button:
                        RewardPopupButtonClickListener(R.id.reward_num_right_button);
                        break;
                    case R.id.reward_ok_button:
                        RewardPopupButtonClickListener(R.id.reward_ok_button);
                        break;
                    case R.id.data_limit_time_left_button:
                        DataPopupButtonClickListener(R.id.data_limit_time_left_button);
                        break;
                    case R.id.data_limit_time_right_button:
                        DataPopupButtonClickListener(R.id.data_limit_time_right_button);
                        break;
                    case R.id.data_morning_button:
                        DataPopupButtonClickListener(R.id.data_morning_button);
                        break;
                    case R.id.data_afternoon_button:
                        DataPopupButtonClickListener(R.id.data_afternoon_button);
                        break;
                    case R.id.data_evening_button:
                        DataPopupButtonClickListener(R.id.data_evening_button);
                        break;
                    case R.id.data_morning_left_button:
                        DataPopupButtonClickListener(R.id.data_morning_left_button);
                        break;
                    case R.id.data_morning_right_button:
                        DataPopupButtonClickListener(R.id.data_morning_right_button);
                        break;
                    case R.id.data_afternoon_left_button:
                        DataPopupButtonClickListener(R.id.data_afternoon_left_button);
                        break;
                    case R.id.data_afternoon_right_button:
                        DataPopupButtonClickListener(R.id.data_afternoon_right_button);
                        break;
                    case R.id.data_evening_left_button:
                        DataPopupButtonClickListener(R.id.data_evening_left_button);
                        break;
                    case R.id.data_evening_right_button:
                        DataPopupButtonClickListener(R.id.data_evening_right_button);
                        break;
                    case R.id.data_setting_ok_button:
                        DataPopupButtonClickListener(R.id.data_setting_ok_button);
                        break;

                }
            } catch (Exception e) {
            }
        }
    }

    private void InstructionPopupButtonClickListener(int index) {
        switch (index) {
            case R.id.instruction_left_button:
                currPageNum--;
                if (currPageNum < firstPageNum) {
                    toastMessage(getContext().getString(R.string.alert_first_page_of_instruction));
                    // 현재 페이지 번호 0으로 설정
                    currPageNum++;
                    break;
                }
                viewPager.setCurrentItem(currPageNum);
                break;
            case R.id.reward_num_right_button:
                currPageNum++;
                if (currPageNum > lastPageNum) {
                    toastMessage(getContext().getString(R.string.alert_last_page_of_instruction));
                    // 마지막 페이지 번호로 설정
                    currPageNum--;
                    break;
                }
                viewPager.setCurrentItem(currPageNum);
                break;
        }
    }

    private void RewardPopupButtonClickListener(int index) {
        switch (index) {
            case R.id.reward_num_left_button:
                rewardNum = Integer.parseInt(reward_num_editText.getText().toString());
                if (rewardNum > 2) {
                    rewardNum--;
                }
                //  보상 횟수가 1 이하일 경우
                else
                    rewardNum = 1;

                reward_num_editText.setText(String.valueOf(rewardNum));
                // 커서 위치 제일 마지막으로 이동
                editable = reward_num_editText.getText();
                Selection.setSelection(editable, editable.length());
                break;
            case R.id.reward_num_right_button:
                rewardNum = Integer.parseInt(reward_num_editText.getText().toString());
                if (rewardNum < 15) {
                    rewardNum++;
                }
                //  보상 횟수가 15 이상일 경우
                else
                    rewardNum = 15;

                reward_num_editText.setText(String.valueOf(rewardNum));
                // 커서 위치 제일 마지막으로 이동
                editable = reward_num_editText.getText();
                Selection.setSelection(editable, editable.length());
                break;
            case R.id.reward_ok_button:
                // 태블릿 혹은 DB에 보상 정보 송신
                String[] rewardData;

                if (!DB_Data.IS_TEST_VERSION) {
                    String result = "";
                    // DB에 보상 정보가 존재하지 않을 경우 Insert
                    if (verifyUserInfo.getRewardData().getData(DB_Data.INDEX_REWARD_CHILDID).isEmpty()) {
                        if (TextUtils.isEmpty(reward_content_editText.getText())){
                            reward_content_editText.setText(getContext().getString(R.string.string_example_reward_content));
                            // 커서 위치 제일 마지막으로 이동
                            editable = reward_content_editText.getText();
                            Selection.setSelection(editable, editable.length());
                        }
                        if (TextUtils.isEmpty(reward_num_editText.getText())){
                            reward_num_editText.setText(getContext().getString(R.string.string_example_reward_total_num));
                            // 커서 위치 제일 마지막으로 이동
                            editable = reward_num_editText.getText();
                            Selection.setSelection(editable, editable.length());
                        }
                        rewardData = new String[4];
                        rewardData[0] = verifyUserInfo.getChildID();
                        rewardData[1] = reward_content_editText.getText().toString();
                        rewardData[2] = "0";
                        rewardData[3] = reward_num_editText.getText().toString();


                        result = verifyUserInfo.InsertDataInTable(DB_Data.TABLE_REWARD_INFO, rewardData);
                    }
                    // 존재할 경우 Update
                    else {
                        rewardData = new String[2];
                        rewardData[0] = reward_content_editText.getText().toString();
                        rewardData[1] = reward_num_editText.getText().toString();
                        result = verifyUserInfo.UpdateDataInTable(DB_Data.TABLE_REWARD_INFO, rewardData, verifyUserInfo.getRewardData().getData(DB_Data.INDEX_REWARD_CHILDID));
                    }

//                    if (result.equals(R.string.db_insert_result_ok) || result.equals(R.string.db_update_result_ok))
//                        this.dismiss();
//                    else {
////                        toastMessage(getContext().getString(R.string.error_fail_to_access_DB));
//                        toastMessage(result);
//                    }

                    if (!verifyUserInfo.GetDataFromTable(DB_Data.TABLE_REWARD_INFO, verifyUserInfo.getRewardData().getData(DB_Data.INDEX_REWARD_CHILDID)).isEmpty()) {
                        RewardSystemActivity rewardSystemActivity = (RewardSystemActivity) RewardSystemActivity.rewardSystemActivity;
                        rewardSystemActivity.reward_content_textView.setText(verifyUserInfo.getRewardData().getData(DB_Data.INDEX_REWARD_REWARD));
                        rewardSystemActivity.reward_totalNum_textView.setText(verifyUserInfo.getRewardData().getData(DB_Data.INDEX_REWARD_CURRENT));
                        rewardSystemActivity.reward_rewardNum_textView.setText(verifyUserInfo.getRewardData().getData(DB_Data.INDEX_REWARD_TOTAL));
                    }

                } else {
                    rewardData = new String[2];
                    rewardData[0] = reward_content_editText.getText().toString();
                    rewardData[1] = reward_num_editText.getText().toString();
                    Toast.makeText(getContext(), rewardData[0] + " " + rewardData[1], Toast.LENGTH_SHORT).show();
                }

                this.dismiss();
                break;
        }
    }

    private void DataPopupButtonClickListener(int index) {
        switch (index) {
            case R.id.data_limit_time_left_button:
                currTimeString = data_limit_time_editText.getText().toString();
                currTimeInt = Integer.parseInt(currTimeString);
//                if (currTimeInt < 10) {
//                    dataLimitTimeInterval = 1;
//                } else {
//                    dataLimitTimeInterval = 5;
//                }
                currTimeInt = DataPopupSetTime(currTimeString, -dataLimitTimeInterval);
                // 설정한 시간이 dataLimitTimeInterval보다 작을 경우
                if (currTimeInt < 1) {
                    // 최소값 (1분)으로 설정
                    currTimeInt = 1;
                }
                currTimeString = String.valueOf(currTimeInt);
                data_limit_time_editText.setText(currTimeString);
                // 커서 위치 제일 마지막으로 이동
                editable = data_limit_time_editText.getText();
                Selection.setSelection(editable, editable.length());
                break;
            case R.id.data_limit_time_right_button:
                currTimeString = data_limit_time_editText.getText().toString();
                currTimeInt = Integer.parseInt(currTimeString);
//                if (currTimeInt > 50) {
//                    dataLimitTimeInterval = 1;
//                } else {
//                    dataLimitTimeInterval = 5;
//                }
                currTimeInt = DataPopupSetTime(currTimeString, dataLimitTimeInterval);
                if (currTimeInt == 6) {
                    currTimeInt = 5;
                }
                // 설정한 시간이 60분보다 클 경우
                else if (currTimeInt > 60) {
                    // 최대값 (60분)으로 설정
                    currTimeInt = 60;
                }
                currTimeString = String.valueOf(currTimeInt);
                data_limit_time_editText.setText(currTimeString);
                // 커서 위치 제일 마지막으로 이동
                editable = data_limit_time_editText.getText();
                Selection.setSelection(editable, editable.length());
                break;
            case R.id.data_morning_button:
                DataPopupShowTimePickerDialog(data_morning_button);
                break;
            case R.id.data_afternoon_button:
                DataPopupShowTimePickerDialog(data_afternoon_button);
                break;
            case R.id.data_evening_button:
                DataPopupShowTimePickerDialog(data_evening_button);
                break;
            case R.id.data_morning_left_button:
                DataPopupSetTime(data_morning_button, -dataTimeInterval);
                break;
            case R.id.data_morning_right_button:
                DataPopupSetTime(data_morning_button, dataTimeInterval);
                break;
            case R.id.data_afternoon_left_button:
                DataPopupSetTime(data_afternoon_button, -dataTimeInterval);
                break;
            case R.id.data_afternoon_right_button:
                DataPopupSetTime(data_afternoon_button, dataTimeInterval);
                break;
            case R.id.data_evening_left_button:
                DataPopupSetTime(data_evening_button, -dataTimeInterval);
                break;
            case R.id.data_evening_right_button:
                DataPopupSetTime(data_evening_button, dataTimeInterval);
                break;
            case R.id.data_setting_ok_button:
                ToothbrushDataActivity toothbrushDataActivity = (ToothbrushDataActivity) ToothbrushDataActivity.toothbrushDataActivity;
                verifyUserInfo.setDataLimit(data_limit_time_editText.getText().toString());
                verifyUserInfo.setDataMorning(data_morning_button.getText().toString());
                verifyUserInfo.setDataAfternoon(data_afternoon_button.getText().toString());
                verifyUserInfo.setDataEvening(data_evening_button.getText().toString());
                this.dismiss();
                toothbrushDataActivity.ShowToothbrushData(toothbrushDataActivity.year, toothbrushDataActivity.month, toothbrushDataActivity.week);
                break;

        }
    }

    private void DataPopupShowTimePickerDialog(final Button btn) {
        new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Time tme = new Time(hourOfDay, minute, 0);//seconds by default set to zero
                hour = hourOfDay;
                min = minute;

                Format formatter;
                formatter = new SimpleDateFormat("a h:mm");
                btn.setText(formatter.format(tme));
            }
        }, Calendar.HOUR_OF_DAY, Calendar.MINUTE, true).show();
    }

    private int DataPopupSetTime(String currTime, int interval) {
        int currLimitTimeInt = 0;
        // 시간 재설정
        currLimitTimeInt = Integer.parseInt(currTime);
        currLimitTimeInt += interval;

        return currLimitTimeInt;
    }

    private void DataPopupSetTime(Button button, int interval) {
        currTimeString = button.getText().toString();
        AM_PM = currTimeString.substring(0, 2);
        currTimeString = currTimeString.substring(3);
        currTimeStringArray = new String[2];
        currTimeStringArray = currTimeString.split(":");
        hour = Integer.parseInt(currTimeStringArray[0]);
        min = Integer.parseInt(currTimeStringArray[1]);
        if (AM_PM.equals("오후")) {
            if (hour != 12)
                hour += 12;
        }
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        calendar.add(Calendar.MINUTE, interval);
        Format formatterr;
        formatterr = new SimpleDateFormat("a h:mm");
        button.setText(formatterr.format(calendar.getTime()));
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        min = calendar.get(Calendar.MINUTE);
    }

    private void DataPopupCheckLimitTime() {
        currTimeString = data_limit_time_editText.getText().toString();
        if (currTimeString == "")
            return;
        currTimeInt = Integer.parseInt(currTimeString);

        if (currTimeInt > 60) {
            Toast.makeText(getContext(), getContext().getString(R.string.alert_data_popup_max_limit_time), Toast.LENGTH_SHORT).show();
            currTimeInt = 60;
            currTimeString = String.valueOf(currTimeInt);
            data_limit_time_editText.setText(currTimeString);
            // 커서 위치 제일 마지막으로 이동
            editable = data_limit_time_editText.getText();
            Selection.setSelection(editable, editable.length());
        }

    }

    // 다이얼로그 외부 화면 흐리게 표현
    private void BlurOutsideActivity() {
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);

    }


    // 경고 팝업창, 양치 데이터 확인 설정 팝업창
    public PopupDialog(Context context, int imageResource, View.OnClickListener singleListener) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        popup_index = imageResource;
        this.imageResource = imageResource;
        this.mLeftClickListener = singleListener;
    }


    // 세팅 팝업창 / 초기화, 접속 방법 변경 버튼
    public PopupDialog(Context context, int index, View.OnClickListener firstListener, View.OnClickListener secondListener) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        popup_index = index;
        this.mLeftClickListener = firstListener;
        this.mRightClickListener = secondListener;
    }

    // 보상 설정 팝업창 / 초기화, 보상 안하기, 취소 버튼
    public PopupDialog(Context context, int index, View.OnClickListener firstListener, View.OnClickListener secondListener, View.OnClickListener thirdListener) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        popup_index = index;
        this.mLeftClickListener = firstListener;
        this.mRightClickListener = secondListener;
        this.thirdClickListener = thirdListener;
    }

    // 조작 방법 설명, 접속 방법 및 정보 초기화 설명
    public PopupDialog(Context context, int index) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        popup_index = index;
    }

    private void toastMessage(String Message) {
        Toast.makeText(getContext(), Message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Rect dialogBounds = new Rect();
        getWindow().getDecorView().getHitRect(dialogBounds);

        if (!dialogBounds.contains((int) ev.getX(), (int) ev.getY())) {
            this.dismiss();
        }
        return super.dispatchTouchEvent(ev);
    }
}
