package com.tektonspace.toothbrush_parents.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tektonspace.toothbrush_parents.adapter.ListItem;
import com.tektonspace.toothbrush_parents.utils.PopupDialog;
import com.tektonspace.toothbrush_parents.R;
import com.tektonspace.toothbrush_parents.constants.DB_Data;
import com.tektonspace.toothbrush_parents.db.VerifyUserInfo;

import java.util.ArrayList;

public class RewardSystemActivity extends AppCompatActivity {
    public TextView reward_content_textView, reward_rewardNum_textView, reward_divider_textView, reward_totalNum_textView;
    Button reward_setting_button, titlebar_button_back, titlebar_button_home;

    PopupDialog popupDialog, popupResetDialog;

    ArrayList<ListItem> rewardInfo;

    VerifyUserInfo verifyUserInfo;

    public static RewardSystemActivity rewardSystemActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward_system);

        InstantiateInstance();
        CheckRewardInfo();
    }

    private void InstantiateInstance() {
        // 액티비티 객체 생성
        rewardSystemActivity = RewardSystemActivity.this;
        // view 인스턴스 초기화
        reward_content_textView = (TextView) findViewById(R.id.reward_content_textView);
        reward_totalNum_textView = (TextView) findViewById(R.id.reward_totalNum_textView);
        reward_divider_textView = (TextView) findViewById(R.id.reward_divider_textView);
        reward_rewardNum_textView = (TextView) findViewById(R.id.reward_rewardNum_textView);
        reward_setting_button = (Button) findViewById(R.id.reward_setting_button);
        titlebar_button_back = (Button) findViewById(R.id.titlebar_button_back);
        titlebar_button_home = (Button) findViewById(R.id.titlebar_button_home);

        // 폰트 설정
        Typeface font_regular = Typeface.createFromAsset(this.getAssets(), "yanolza_regular.ttf");
        Typeface font_bold = Typeface.createFromAsset(this.getAssets(), "yanolza_bold.ttf");
        reward_totalNum_textView.setTypeface(font_bold);
        reward_rewardNum_textView.setTypeface(font_bold);
        reward_divider_textView.setTypeface(font_bold);
        reward_content_textView.setTypeface(font_bold);

        reward_content_textView.setText(getString(R.string.string_example_reward_content));
        reward_totalNum_textView.setText(getString(R.string.string_example_reward_current_num));
        reward_rewardNum_textView.setText(getString(R.string.string_example_reward_total_num));
        // 버튼 클릭 이벤트 객체 생성
        BtnOnClickListener onClickListener = new BtnOnClickListener();
        reward_setting_button.setOnClickListener(onClickListener);
        titlebar_button_back.setOnClickListener(onClickListener);
        titlebar_button_home.setOnClickListener(onClickListener);

        verifyUserInfo = (VerifyUserInfo) getApplicationContext();
        verifyUserInfo.addActivities(this);
    }

    private void CheckRewardInfo() {
        if (!DB_Data.IS_TEST_VERSION) {
            // DB에 보상 내용이 있을 경우 이를 화면에 출력
            if(!TextUtils.isEmpty(verifyUserInfo.getRewardData().getData(DB_Data.INDEX_REWARD_CHILDID))){
//            if(!verifyUserInfo.getRewardData().getData(DB_Data.INDEX_REWARD_CHILDID).isEmpty()){
                if (!TextUtils.isEmpty(verifyUserInfo.getRewardData().getData(DB_Data.INDEX_REWARD_REWARD)))
                    reward_content_textView.setText(verifyUserInfo.getRewardData().getData(DB_Data.INDEX_REWARD_REWARD));
                else
                    reward_content_textView.setText(getString(R.string.string_example_reward_content));
                if (!TextUtils.isEmpty(verifyUserInfo.getRewardData().getData(DB_Data.INDEX_REWARD_CURRENT)))
                    reward_totalNum_textView.setText(verifyUserInfo.getRewardData().getData(DB_Data.INDEX_REWARD_CURRENT));
                else
                    reward_totalNum_textView.setText(getString(R.string.string_example_reward_current_num));
                if (!TextUtils.isEmpty(verifyUserInfo.getRewardData().getData(DB_Data.INDEX_REWARD_TOTAL)))
                    reward_rewardNum_textView.setText(verifyUserInfo.getRewardData().getData(DB_Data.INDEX_REWARD_TOTAL));
                else
                    reward_rewardNum_textView.setText(getString(R.string.string_example_reward_total_num));
            }

        } else {
            reward_content_textView.setText("사탕");
//            reward_totalNum_textView.setText("2");
//            reward_rewardNum_textView.setText("20");
        }
    }

    class BtnOnClickListener implements Button.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.reward_setting_button:
                    // 세팅 팝업창 띄움
                    ShowDialog(DB_Data.POPUP_REWARD_SETTING, resetButtonListener, cancelButtonListener);
                    break;
                case R.id.titlebar_button_home:
                    Intent intentToHome = new Intent(RewardSystemActivity.this, HomeActivity.class);
                    verifyUserInfo.clearActivities();
                    startActivity(intentToHome);
                    finish();
                    break;
                case R.id.titlebar_button_back:
                    finish();
                    break;
            }
        }
    }

    private void ShowDialog(int message, View.OnClickListener onLeftClickListener, View.OnClickListener onRightClickListener) {
        if (message == DB_Data.POPUP_REWARD_SETTING) {
            popupDialog = new PopupDialog(this, message, onLeftClickListener, onRightClickListener);
            popupDialog.show();
        } else {
            popupResetDialog = new PopupDialog(this, message, onLeftClickListener, onRightClickListener);
            popupResetDialog.show();
        }
    }

    private View.OnClickListener resetButtonListener = new View.OnClickListener() {
        public void onClick(View v) {
            popupDialog.dismiss();
            ShowDialog(DB_Data.POPUP_REWARD_SETTING_RESET, resetOKButtonListener, resetCancelButtonListener);
        }
    };

    private View.OnClickListener cancelButtonListener = new View.OnClickListener() {
        public void onClick(View v) {
            popupDialog.dismiss();
        }
    };
    private View.OnClickListener resetOKButtonListener = new View.OnClickListener() {
        public void onClick(View v) {
            String childID = verifyUserInfo.getRewardData().getData(DB_Data.INDEX_REWARD_CHILDID);
            // DB 연동 후 누적 횟수 초기화
            String[] rewardData = new String[1];
            rewardData[0] = "reset";

            if (!DB_Data.IS_TEST_VERSION) {
                verifyUserInfo.UpdateDataInTable(DB_Data.TABLE_REWARD_INFO, rewardData, childID);
                // RewardInfo 테이블에서 child ID값에 따른 누적 양치 횟수 검색
                verifyUserInfo.GetDataFromTable(DB_Data.TABLE_REWARD_INFO, childID);
                reward_totalNum_textView.setText(verifyUserInfo.getRewardData().getData(DB_Data.INDEX_REWARD_CURRENT));
            }
            Toast.makeText(getApplicationContext(), "초기화 되었습니다.", Toast.LENGTH_SHORT).show();

            popupResetDialog.dismiss();
        }
    };

    private View.OnClickListener resetCancelButtonListener = new View.OnClickListener() {
        public void onClick(View v) {
            popupResetDialog.dismiss();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (popupDialog != null) {
            popupDialog.dismiss();
            popupDialog = null;
        }
    }

}
