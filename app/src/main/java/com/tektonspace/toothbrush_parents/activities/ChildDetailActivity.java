package com.tektonspace.toothbrush_parents.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tektonspace.toothbrush_parents.utils.PopupDialog;
import com.tektonspace.toothbrush_parents.R;
import com.tektonspace.toothbrush_parents.adapter.ListItem;
import com.tektonspace.toothbrush_parents.constants.DB_Data;
import com.tektonspace.toothbrush_parents.db.VerifyUserInfo;

import java.util.ArrayList;

/**
 * Created by SeungHee on 2017-04-19.
 * 아이 정보 화면
 *
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ChildDetailActivity extends AppCompatActivity {
    Button setting_button, toothbrushData_button, reward_button, edit_button, titlebar_button_back, titlebar_button_home;
    RelativeLayout childdetail_childphoto_layout, childdetail_list_layout, childdetail_characterframe;
    ImageView childPhoto_imageView, childdetail_character_frame_imageView;
    TextView childName_textView, childdetail_childNickname_textView;
    ImageView childreward_icon_reward_imageView;

    PopupDialog popupDialog;
    VerifyUserInfo verifyUserInfo;

    String childID = "";
    String mailAddress = "";
    String childPhoto = "";
    boolean ifChildPhotoExist = false;

    public static ChildDetailActivity childDetailActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_detail);

        InstantiateInstance();
        // DB 연동 후 보상 테이블 데이터 확인
        CheckRewardInfo();
    }

    private void InstantiateInstance() {
        // 액티비티 객체 생성
       childDetailActivity = ChildDetailActivity.this;
        // view 인스턴스 초기화
        childdetail_characterframe = (RelativeLayout) findViewById(R.id.childdetail_characterframe);
        childdetail_character_frame_imageView = (ImageView) findViewById(R.id.childdetail_character_frame_imageView);
//        setting_button = (Button) findViewById(R.id.childDetail_setting_button);
        toothbrushData_button = (Button) findViewById(R.id.childDetail_toothbrushData_button);
        reward_button = (Button) findViewById(R.id.childDetail_reward_button);
        edit_button = (Button) findViewById(R.id.childDetail_edit_button);
        childdetail_childphoto_layout = (RelativeLayout) findViewById(R.id.childdetail_childphoto_layout);
        childdetail_list_layout = (RelativeLayout) findViewById(R.id.childdetail_list_layout);
        childPhoto_imageView = (ImageView) findViewById(R.id.childDetail_childPhoto_imageView);
        childName_textView = (TextView) findViewById(R.id.childDetail_childName_textView);
        childdetail_childNickname_textView = (TextView) findViewById(R.id.childdetail_childNickname_textView);
        childreward_icon_reward_imageView = (ImageView) findViewById(R.id.childDetail_icon_reward_imageView);
        titlebar_button_back = (Button) findViewById(R.id.titlebar_button_back);
        titlebar_button_home = (Button) findViewById(R.id.titlebar_button_home);


        // 폰트 변경
        Typeface font_regular = Typeface.createFromAsset(getAssets(), "yanolza_regular.ttf");
        Typeface font_bold = Typeface.createFromAsset(getAssets(), "yanolza_bold.ttf");
        childName_textView.setTypeface(font_regular);
        childdetail_childNickname_textView.setTypeface(font_bold);


        // 버튼 클릭 이벤트 객체 생성
        BtnOnClickListener onClickListener = new BtnOnClickListener();
        setting_button.setOnClickListener(onClickListener);
        toothbrushData_button.setOnClickListener(onClickListener);
        reward_button.setOnClickListener(onClickListener);
        edit_button.setOnClickListener(onClickListener);
        titlebar_button_back.setOnClickListener(onClickListener);
        titlebar_button_home.setOnClickListener(onClickListener);

        verifyUserInfo = (VerifyUserInfo)getApplicationContext();
        verifyUserInfo.addActivities(this);

        // ChildSelectFragment로부터 intent 받아옴
        Intent intent = getIntent();
        mailAddress = intent.getStringExtra(DB_Data.STRING_USER_MAILADDRESS);
        // 해당되는 아이의 이름을 textView에 출력
        if(!TextUtils.isEmpty(intent.getStringExtra(DB_Data.STRING_CHILD_NAME))){
//        if(intent.getStringExtra(DB_Data.STRING_CHILD_NAME).length() > 0){
            childName_textView.setText(intent.getStringExtra(DB_Data.STRING_CHILD_NAME));
            childdetail_list_layout.setBackground(getDrawable(R.drawable.childdetail_background));
        }
        else{
            childName_textView.setText("???");
            childdetail_list_layout.setBackground(getDrawable(R.drawable.childdetail_background_none));
        }

        if(DB_Data.IS_TEST_VERSION && intent.getStringExtra(DB_Data.STRING_CHILD_NAME).equals(getString(R.string.none_childName)))
            childdetail_list_layout.setBackground(getDrawable(R.drawable.childdetail_background_none));

        childID = intent.getStringExtra(DB_Data.STRING_CHILD_ID);
        verifyUserInfo.setChildID(childID);

        childPhoto = intent.getStringExtra(DB_Data.STRING_CHILD_PHOTO);
        if(!TextUtils.isEmpty(childPhoto)){
//        if(childPhoto.length() > 0 && !childPhoto.equals("null")){
            // 아이 사진, 이름 정보가 있을 경우 화면에 출력
            ifChildPhotoExist = verifyUserInfo.SetImageViewPhoto(this, childPhoto_imageView, childdetail_childphoto_layout);
            verifyUserInfo.SetTextViewName(childName_textView);
        }

        // 아이 사진 정보가 있을 경우
        if(ifChildPhotoExist){
            childdetail_childphoto_layout.setBackground(getDrawable(R.drawable.childdetail_photoframe1));
            childPhoto_imageView.setVisibility(View.VISIBLE);
        }
            // 아이 사진 정보가 없을 경우
        else{
            childdetail_childphoto_layout.setBackground(getDrawable(R.drawable.childselect_photo_frame_none1));
            childPhoto_imageView.setVisibility(View.INVISIBLE);
        }

        verifyUserInfo.SetViewCharacter(this, childdetail_character_frame_imageView, childdetail_characterframe, childdetail_childNickname_textView);
    }

    class BtnOnClickListener implements Button.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
//                case R.id.childDetail_setting_button:
//                    // 세팅 팝업창 띄움
//                    ShowDialog(DB_Data.POPUP_SETTING);
//                    break;
                case R.id.childDetail_toothbrushData_button:
                    // 양치 데이터 확인 화면으로 전환
                    Intent intentToToothbrush = new Intent(ChildDetailActivity.this, ToothbrushDataActivity.class);
                    startActivity(intentToToothbrush);
                    break;
                case R.id.childDetail_reward_button:
                    // 보상 시스템 화면으로 전환
                    Intent intentToReward = new Intent(ChildDetailActivity.this, RewardSystemActivity.class);
                    startActivity(intentToReward);
                    break;
                case R.id.childDetail_edit_button:
                    // 아이 정보 편집 화면으로 전환
                    Intent intentToEdit = new Intent(ChildDetailActivity.this, ChildEditActivity.class);
                    startActivity(intentToEdit);
//                    finish();
                    break;
                case R.id.titlebar_button_home:
                    Intent intentToHome = new Intent(ChildDetailActivity.this, HomeActivity.class);
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

    private void ShowDialog(int message) {
        popupDialog = new PopupDialog(this, message, resetButtonListener, changeButtonListener, closeButtonListener);
        popupDialog.show();
    }

    private View.OnClickListener resetButtonListener = new View.OnClickListener() {
        public void onClick(View v) {
            popupDialog.dismiss();
            ShowDialog(DB_Data.POPUP_SETTING_RESET);
        }
    };
    private View.OnClickListener changeButtonListener = new View.OnClickListener() {
        public void onClick(View v) {
            popupDialog.dismiss();
            ShowDialog(DB_Data.POPUP_SETTING_CHANGE);
        }
    };

    private View.OnClickListener closeButtonListener = new View.OnClickListener() {
        public void onClick(View v) {
            popupDialog.dismiss();
        }
    };
    private void CheckRewardInfo(){
        if(!DB_Data.IS_TEST_VERSION){
            // RewardInfo 테이블에서 child ID값에 따른 누적 양치 횟수 검색
            verifyUserInfo.GetDataFromTable(DB_Data.TABLE_REWARD_INFO, childID);
            // 아이가 둘 이상일 경우, 보상 정보가 있는 아이의 정보가 화면에 출력될 수 있으니 ChildID 일치 여부를 먼저 확인
            // 현재 ChildID에 해당하는 보상 정보가 존재할 경우

            if(!TextUtils.isEmpty(verifyUserInfo.getRewardData().getData(DB_Data.INDEX_REWARD_CHILDID)) && verifyUserInfo.getRewardData().getData(DB_Data.INDEX_REWARD_CHILDID).equals(childID)){
                // 누적 양치 횟수와 설정한 보상 횟수 값이 같을 경우 알림 배지가 보이도록 함
                if(!TextUtils.isEmpty(verifyUserInfo.getRewardData().getData(DB_Data.INDEX_REWARD_CURRENT)) && !TextUtils.isEmpty(verifyUserInfo.getRewardData().getData(DB_Data.INDEX_REWARD_TOTAL))) {
                    if (Integer.parseInt(verifyUserInfo.getRewardData().getData(DB_Data.INDEX_REWARD_CURRENT)) >= Integer.parseInt(verifyUserInfo.getRewardData().getData(DB_Data.INDEX_REWARD_TOTAL))) {
                        childreward_icon_reward_imageView.setImageDrawable(getDrawable(R.drawable.childdetail_alarm1));
                    } else
                        childreward_icon_reward_imageView.setImageDrawable(getDrawable(R.drawable.childdetail_icon_reward));
                }
                else
                    childreward_icon_reward_imageView.setImageDrawable(getDrawable(R.drawable.childdetail_icon_reward));
            }
            // RewardData에 검색된 ChildID와 현재 아이의 ChildID가 불일치할 경우, DB에 해당 아이의 보상 정보가 없는 것
            // rewardData 초기화
            else{
                verifyUserInfo.clearRewardData();
                childreward_icon_reward_imageView.setImageDrawable(getDrawable(R.drawable.childdetail_icon_reward));
            }

        }
        else{   
            childreward_icon_reward_imageView.setImageDrawable(getDrawable(R.drawable.childdetail_alarm1));
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if(!TextUtils.isEmpty(verifyUserInfo.getChildData().getData(DB_Data.INDEX_CHILD_PHOTO))){
//        if(verifyUserInfo.getChildData().getData(DB_Data.INDEX_CHILD_PHOTO).length() > 0 && !verifyUserInfo.getChildData().getData(DB_Data.INDEX_CHILD_PHOTO).equals("null")){
            verifyUserInfo.CheckPermission(this);
            if(verifyUserInfo.getisPermissionGranted()){
                String imagePath = "";
                imagePath = verifyUserInfo.getRealPathFromURI(Uri.parse(verifyUserInfo.getChildData().getData(DB_Data.INDEX_CHILD_PHOTO)));

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = verifyUserInfo.getBitmapImageSize();
                Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);

                childPhoto_imageView.setImageBitmap(null);
                childPhoto_imageView.setImageBitmap(bitmap);
            }
        }
        if(!TextUtils.isEmpty(verifyUserInfo.getChildData().getData(DB_Data.INDEX_CHILD_NAME)))
//        if(verifyUserInfo.getChildData().getData(DB_Data.INDEX_CHILD_NAME).length() > 0)
            childName_textView.setText(verifyUserInfo.getChildData().getData(DB_Data.INDEX_CHILD_NAME));
    }

}
