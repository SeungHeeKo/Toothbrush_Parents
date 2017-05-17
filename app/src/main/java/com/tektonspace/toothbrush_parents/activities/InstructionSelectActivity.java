package com.tektonspace.toothbrush_parents.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.tektonspace.toothbrush_parents.constants.DB_Data;
import com.tektonspace.toothbrush_parents.R;
import com.tektonspace.toothbrush_parents.db.VerifyUserInfo;

/**
 * Created by SeungHee on 2017-04-17.
 * 조작 방법 선택 화면
 */
public class InstructionSelectActivity extends AppCompatActivity {
    Button instruction_mom_button, instruction_kid_button, instruction_connect_button, titlebar_button_back, titlebar_button_home;

    VerifyUserInfo verifyUserInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction_select);
        InstantiateInstance();

    }

    private void InstantiateInstance(){
        instruction_mom_button = (Button) findViewById(R.id.instructionSelect_mom_button);
        instruction_kid_button = (Button) findViewById(R.id.instructionSelect_kid_button);
        instruction_connect_button = (Button) findViewById(R.id.instructionSelect_connect_button);
        titlebar_button_back = (Button) findViewById(R.id.titlebar_button_back);
        titlebar_button_home = (Button) findViewById(R.id.titlebar_button_home);

        // BtnOnClickListener의 객체 생성.
        BtnOnClickListener onClickListener = new BtnOnClickListener() ;
        instruction_mom_button.setOnClickListener(onClickListener);
        instruction_kid_button.setOnClickListener(onClickListener);
        instruction_connect_button.setOnClickListener(onClickListener);
        titlebar_button_back.setOnClickListener(onClickListener);
        titlebar_button_home.setOnClickListener(onClickListener);

        verifyUserInfo = (VerifyUserInfo)getApplicationContext();
        verifyUserInfo.addActivities(this);
    }
    class BtnOnClickListener implements Button.OnClickListener {
        @Override
        public void onClick(View view) {
            Intent intentInstruction = new Intent(InstructionSelectActivity.this, InstructionActivity.class);
            switch (view.getId()) {
                case R.id.instructionSelect_mom_button:
                    intentInstruction.putExtra(DB_Data.STRING_INSTRUCTION, DB_Data.STRING_INSTRUCTION_MOM);
                    startActivity(intentInstruction);
                    break;
                case R.id.instructionSelect_kid_button:
                    intentInstruction.putExtra(DB_Data.STRING_INSTRUCTION, DB_Data.STRING_INSTRUCTION_KID);
                    startActivity(intentInstruction);
                    break;
                case R.id.instructionSelect_connect_button:
                    intentInstruction.putExtra(DB_Data.STRING_INSTRUCTION, DB_Data.STRING_INSTRUCTION_CONNECT);
                    startActivity(intentInstruction);
                    break;
                case R.id.titlebar_button_home:
                    Intent intentToHome = new Intent(InstructionSelectActivity.this, HomeActivity.class);
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
}
