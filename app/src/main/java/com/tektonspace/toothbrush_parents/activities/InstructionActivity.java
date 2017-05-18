package com.tektonspace.toothbrush_parents.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.tektonspace.toothbrush_parents.adapter.InstructionPagerAdapter;
import com.tektonspace.toothbrush_parents.constants.DB_Data;
import com.tektonspace.toothbrush_parents.R;
import com.tektonspace.toothbrush_parents.db.VerifyUserInfo;

/**
 * Created by SeungHee on 2017-04-18.
 * Mom, Kid, Connect별 조작 정보 화면 및 양치질 정보 화면
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class InstructionActivity extends AppCompatActivity {
    ViewPager viewPager, viewPager_toothbrush;
    TextView instruction_title_textView;
    RelativeLayout instruction_viewPager_layout;
    Button moveLeft_button, moveRight_button, titlebar_button_back, titlebar_button_home, titlebar_button_instruction, titlebar_button_teachbrush;
    Button instruction_mom_button, instruction_kid_button, instruction_connect_button;

    String instruction = "";
    int currPageNum = 1;
    int firstPageNum = 0;
    int lastPageNum;
    Drawable leftAlpha;
    Drawable rightAlpha;
    int returnAlpha = 255;

    // 설명 이미지를 담는 PagerAdapter
    InstructionPagerAdapter instructionPagerAdapter;
    PageChangeListener pageChangeListener;

    int[] momInstructResourcesImage = {
            R.drawable.instruction_mom1,
            R.drawable.instruction_mom2,
            R.drawable.instruction_mom3,
            R.drawable.instruction_mom4
    };

    int[] kidInstructResourcesImage = {
            R.drawable.instruction_toothbrush1,
            R.drawable.instruction_toothbrush2,
            R.drawable.instruction_toothbrush3,
            R.drawable.instruction_toothbrush4

    };
    int[] connectInstructResourcesImage = {

    };
    int[] toothbrushingInstructResourcesImage = {
            R.drawable.instruction_toothbrush1,
            R.drawable.instruction_toothbrush2,
            R.drawable.instruction_toothbrush3,
            R.drawable.instruction_toothbrush4

    };

    int[] currImageResources = {};
    int[] currTextResources = {};

    VerifyUserInfo verifyUserInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction);
        InstantiateInstance();

    }

    private void InstantiateInstance() {
        instruction_viewPager_layout = (RelativeLayout) findViewById(R.id.instruction_viewPager_layout);
        moveLeft_button = (Button) findViewById(R.id.instruction_left_button);
        moveRight_button = (Button) findViewById(R.id.instruction_right_button);
        titlebar_button_back = (Button) findViewById(R.id.titlebar_button_back);
        titlebar_button_home = (Button) findViewById(R.id.titlebar_button_home);
        instruction_mom_button = (Button) findViewById(R.id.instruction_mom_button);
        instruction_kid_button = (Button) findViewById(R.id.instruction_kid_button);
        instruction_connect_button = (Button) findViewById(R.id.instruction_connect_button);
        titlebar_button_instruction = (Button) findViewById(R.id.titlebar_button_instruction);
        titlebar_button_teachbrush = (Button) findViewById(R.id.titlebar_button_teachbrush);
        instruction_title_textView = (TextView) findViewById(R.id.instruction_title_textView);
        viewPager = (ViewPager) findViewById(R.id.instruction_viewPager);
        viewPager_toothbrush = (ViewPager) findViewById(R.id.instruction_viewPager_toothbrush);

        leftAlpha = ((Button) findViewById(R.id.instruction_left_button)).getBackground();
        rightAlpha = ((Button) findViewById(R.id.instruction_right_button)).getBackground();

        verifyUserInfo = (VerifyUserInfo) getApplicationContext();
        verifyUserInfo.addActivities(this);

        Intent intent = getIntent();
        instruction = intent.getStringExtra(DB_Data.STRING_INSTRUCTION);
        currPageNum = 0;

        SetInstructionView(instruction);


        // BtnOnClickListener의 객체 생성.
        BtnOnClickListener onClickListener = new BtnOnClickListener();
        moveLeft_button.setOnClickListener(onClickListener);
        moveRight_button.setOnClickListener(onClickListener);
        titlebar_button_back.setOnClickListener(onClickListener);
        titlebar_button_home.setOnClickListener(onClickListener);
        titlebar_button_instruction.setOnClickListener(onClickListener);
        titlebar_button_teachbrush.setOnClickListener(onClickListener);
        instruction_mom_button.setOnClickListener(onClickListener);
        instruction_kid_button.setOnClickListener(onClickListener);
        instruction_connect_button.setOnClickListener(onClickListener);

        returnAlpha = 255;
        if (currPageNum == firstPageNum) {
            leftAlpha.setAlpha(50);
            rightAlpha.setAlpha(returnAlpha);
            moveLeft_button.setEnabled(false);
        }
    }

    private void SetInstructionView(String instruction){
        switch (instruction) {
            case DB_Data.STRING_INSTRUCTION_MOM:
                currPageNum = 0;
                instruction_title_textView.setText(getString(R.string.title_instruction_select));
                viewPager.setVisibility(View.VISIBLE);
                viewPager_toothbrush.setVisibility(View.INVISIBLE);
                SetButtons(DB_Data.STRING_INSTRUCTION_MOM);
                lastPageNum = DB_Data.PAGE_NUM_MOM;
                currImageResources = momInstructResourcesImage;
                verifyUserInfo.setIsTooth(false);
                break;
            case DB_Data.STRING_INSTRUCTION_KID:
                currPageNum = 0;
                instruction_title_textView.setText(getString(R.string.title_instruction_select));
                viewPager.setVisibility(View.VISIBLE);
                viewPager_toothbrush.setVisibility(View.INVISIBLE);
                SetButtons(DB_Data.STRING_INSTRUCTION_KID);
                lastPageNum = DB_Data.PAGE_NUM_KID;
                currImageResources = kidInstructResourcesImage;
                verifyUserInfo.setIsTooth(false);
                break;
            case DB_Data.STRING_INSTRUCTION_CONNECT:
                currPageNum = 0;
                instruction_title_textView.setText(getString(R.string.title_instruction_select));
                viewPager.setVisibility(View.VISIBLE);
                viewPager_toothbrush.setVisibility(View.INVISIBLE);
                SetButtons(DB_Data.STRING_INSTRUCTION_CONNECT);
                lastPageNum = DB_Data.PAGE_NUM_CONNECT;
                currImageResources = connectInstructResourcesImage;
                verifyUserInfo.setIsTooth(false);
                break;
            case DB_Data.STRING_INSTRUCTION_TOOTHBRUSH:
                currPageNum = 0;
                instruction_title_textView.setText(getString(R.string.title_instruction_toothbrush));
                viewPager.setVisibility(View.INVISIBLE);
                viewPager_toothbrush.setVisibility(View.VISIBLE);
                SetButtons(DB_Data.STRING_INSTRUCTION_TOOTHBRUSH);
                viewPager.setVisibility(View.VISIBLE);
                lastPageNum = DB_Data.PAGE_NUM_TOOTHBRUSH;
                currImageResources = toothbrushingInstructResourcesImage;
                verifyUserInfo.setIsTooth(true);
                break;

        }
        if (currPageNum == firstPageNum) {
            leftAlpha.setAlpha(50);
            rightAlpha.setAlpha(returnAlpha);
            moveLeft_button.setEnabled(false);
        }

        SetPager();
    }

    private void SetPager(){
        instructionPagerAdapter = new InstructionPagerAdapter(this, currImageResources);
        pageChangeListener = new PageChangeListener();
        if (verifyUserInfo.getIsTooth())
            viewPager_toothbrush.setAdapter(instructionPagerAdapter);
        else
            viewPager.setAdapter(instructionPagerAdapter);
        if (verifyUserInfo.getIsTooth())
            viewPager_toothbrush.addOnPageChangeListener(pageChangeListener);
        else
            viewPager.addOnPageChangeListener(pageChangeListener);
    }
    private void SetButtons(String button) {
        switch (button) {
            case DB_Data.STRING_INSTRUCTION_MOM:
                instruction_mom_button.setVisibility(View.VISIBLE);
                instruction_kid_button.setVisibility(View.VISIBLE);
                instruction_connect_button.setVisibility(View.VISIBLE);
                instruction_mom_button.setBackground(getDrawable(R.drawable.instruction_select));
                instruction_kid_button.setBackground(getDrawable(R.drawable.instruction_nonselect));
                instruction_connect_button.setBackground(getDrawable(R.drawable.instruction_nonselect));
                instruction_mom_button.setTextColor(Color.WHITE);
                instruction_kid_button.setTextColor(Color.GRAY);
                instruction_connect_button.setTextColor(Color.GRAY);
                titlebar_button_instruction.setVisibility(View.INVISIBLE);
                titlebar_button_teachbrush.setVisibility(View.VISIBLE);
                break;
            case DB_Data.STRING_INSTRUCTION_KID:
                instruction_mom_button.setVisibility(View.VISIBLE);
                instruction_kid_button.setVisibility(View.VISIBLE);
                instruction_connect_button.setVisibility(View.VISIBLE);
                instruction_mom_button.setBackground(getDrawable(R.drawable.instruction_nonselect));
                instruction_kid_button.setBackground(getDrawable(R.drawable.instruction_select));
                instruction_connect_button.setBackground(getDrawable(R.drawable.instruction_nonselect));
                instruction_mom_button.setTextColor(Color.GRAY);
                instruction_kid_button.setTextColor(Color.WHITE);
                instruction_connect_button.setTextColor(Color.GRAY);
                titlebar_button_instruction.setVisibility(View.INVISIBLE);
                titlebar_button_teachbrush.setVisibility(View.VISIBLE);
                break;
            case DB_Data.STRING_INSTRUCTION_CONNECT:
                instruction_mom_button.setVisibility(View.VISIBLE);
                instruction_kid_button.setVisibility(View.VISIBLE);
                instruction_connect_button.setVisibility(View.VISIBLE);
                instruction_mom_button.setBackground(getDrawable(R.drawable.instruction_nonselect));
                instruction_kid_button.setBackground(getDrawable(R.drawable.instruction_nonselect));
                instruction_connect_button.setBackground(getDrawable(R.drawable.instruction_select));
                instruction_mom_button.setTextColor(Color.GRAY);
                instruction_kid_button.setTextColor(Color.GRAY);
                instruction_connect_button.setTextColor(Color.WHITE);
                titlebar_button_instruction.setVisibility(View.INVISIBLE);
                titlebar_button_teachbrush.setVisibility(View.VISIBLE);
                break;
            case DB_Data.STRING_INSTRUCTION_TOOTHBRUSH:
                instruction_mom_button.setVisibility(View.INVISIBLE);
                instruction_kid_button.setVisibility(View.INVISIBLE);
                instruction_connect_button.setVisibility(View.INVISIBLE);
                titlebar_button_instruction.setVisibility(View.VISIBLE);
                titlebar_button_teachbrush.setVisibility(View.INVISIBLE);
                break;
        }

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    class PageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            currPageNum = position;

            if (!moveRight_button.isEnabled()) {
                rightAlpha.setAlpha(returnAlpha);
                moveRight_button.setEnabled(true);
            } else if (!moveLeft_button.isEnabled()) {
                leftAlpha.setAlpha(returnAlpha);
                moveLeft_button.setEnabled(true);
            }

            if (currPageNum == firstPageNum) {
                leftAlpha.setAlpha(50);
                moveLeft_button.setEnabled(false);
            } else if (currPageNum == lastPageNum - 1) {
                rightAlpha.setAlpha(50);
                moveRight_button.setEnabled(false);
            }
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
                    case R.id.titlebar_button_instruction:
                        // 조작 방법 선택 화면으로 이동
                        Intent intentToInstruction = new Intent(InstructionActivity.this, InstructionActivity.class);
                        intentToInstruction.putExtra(DB_Data.STRING_INSTRUCTION, DB_Data.STRING_INSTRUCTION_MOM);
                        startActivity(intentToInstruction);
                        finish();
                        break;
                    case R.id.titlebar_button_teachbrush:
                        // 올바른 양치질 습관과 관련된 정보를 제공하는 화면으로 이동
                        Intent intentToToothbrushInstruction = new Intent(InstructionActivity.this, InstructionActivity.class);
                        intentToToothbrushInstruction.putExtra(DB_Data.STRING_INSTRUCTION, DB_Data.STRING_INSTRUCTION_TOOTHBRUSH);
                        startActivity(intentToToothbrushInstruction);
                        finish();
                        break;
                    case R.id.instruction_mom_button:
                        SetInstructionView(DB_Data.STRING_INSTRUCTION_MOM);
                        break;
                    case R.id.instruction_kid_button:
                        SetInstructionView(DB_Data.STRING_INSTRUCTION_KID);
                        break;
                    case R.id.instruction_connect_button:
                        SetInstructionView(DB_Data.STRING_INSTRUCTION_CONNECT);
                        break;
                    case R.id.instruction_left_button:
                        if (currPageNum == firstPageNum) {
                            leftAlpha.setAlpha(50);
                            moveLeft_button.setEnabled(false);
                            break;
                        }

                        currPageNum--;
                        if (!moveRight_button.isEnabled()) {
                            rightAlpha.setAlpha(returnAlpha);
                            moveRight_button.setEnabled(true);
                        }
                        if (verifyUserInfo.getIsTooth())
                            viewPager_toothbrush.setCurrentItem(currPageNum);
                        else
                            viewPager.setCurrentItem(currPageNum);

                        if (currPageNum == firstPageNum) {
                            leftAlpha.setAlpha(50);
                            moveLeft_button.setEnabled(false);
                            break;
                        }
                        break;
                    case R.id.instruction_right_button:

                        currPageNum++;
                        if (!moveLeft_button.isEnabled()) {
                            leftAlpha.setAlpha(returnAlpha);
                            moveLeft_button.setEnabled(true);
                        }
                        if (verifyUserInfo.getIsTooth())
                            viewPager_toothbrush.setCurrentItem(currPageNum);
                        else
                            viewPager.setCurrentItem(currPageNum);
                        if (currPageNum == lastPageNum - 1) {
                            rightAlpha.setAlpha(50);
                            moveRight_button.setEnabled(false);
                            break;
                        }
                        break;
                    case R.id.titlebar_button_home:
                        Intent intentToHome = new Intent(InstructionActivity.this, HomeActivity.class);
                        verifyUserInfo.clearActivities();
                        startActivity(intentToHome);
                        finish();
                        break;
                    case R.id.titlebar_button_back:
                        finish();
                        break;
                }
            } catch (Exception e) {
            }
        }
    }

    private void toastMessage(String Message) {
        Toast.makeText(this, Message, Toast.LENGTH_SHORT).show();
    }
}
