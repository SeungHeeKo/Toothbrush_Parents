package com.tektonspace.toothbrush_parents.activities;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tektonspace.toothbrush_parents.adapter.ListItem;
import com.tektonspace.toothbrush_parents.constants.DB_Data;
import com.tektonspace.toothbrush_parents.utils.MailSender;
import com.tektonspace.toothbrush_parents.utils.PopupDialog;
import com.tektonspace.toothbrush_parents.R;
import com.tektonspace.toothbrush_parents.db.VerifyUserInfo;
import com.tektonspace.toothbrush_parents.db.DB_Manager;
import com.tektonspace.toothbrush_parents.utils.BaseActivity;

import java.util.ArrayList;

/**
 * Created by SeungHee on 2017-04-12.
 * 계정 생성 화면
 */
public class CreateAccountActivity extends AppCompatActivity {
    TextView createAccount_title, id_textView, mailAddress_textView, password_textView, password_check_textView;
    // 사용자의 아이디, 메일주소, 비밀번호를 입력받는 EditText
    EditText id_editText, mailAddress_editText, password_editText, password_check_editText;
    // 사용자의 아이디, 메일주소, 비밀번호 저장
    String user_id = null, mailAddress = null, password = null;

    // 비밀번호 일치 여부 문구 출력하는 TextView
    TextView password_correctCheck_textView;
    // 비밀번호 일치, 불일치 여부
    boolean isPasswordCorrect = false;

    /* 비밀번호 재설정 화면 */
    // 인증번호 전송 메세지 출력
    TextView createAccount_authentication_textView;
    // 비밀번호 재설정 화면시 인증번호 입력 EditText
    EditText createAccount_authentication_editText;
    // 비밀번호 재설정 화면시 인증번호 전송 및 인증 버튼
    Button createAccount_authentication_button;
    // 비밀번호 재설정 화면시 인증번호
    String authenticationNumber = null;
    // 인증번호 일치, 불일치 여부
    boolean isAuthenticationNumberCorrect = false;

    // 계정 생성, 뒤로가기, 홈 Button
    Button accountCreate_button, titlebar_button_back, titlebar_button_home;

    // DB 연동 객체
    DB_Manager dbManager;

    // 사용자 정보 검색을 위한 클래스 객체
    VerifyUserInfo verifyUserInfo;
    // 사용자 정보 검색 결과 저장
    String result = "";

    // 팝업창 객체
    PopupDialog popupDialog;

    // 비밀번호 재설정 화면인지 여부
    boolean isPasswordResetActivity = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        // 인스턴스 초기화
        InstantiateInstance();

    }

    private void InstantiateInstance() {
        // view 인스턴스 초기화
        createAccount_title = (TextView) findViewById(R.id.createAccount_title);
        id_textView = (TextView) findViewById(R.id.createAccount_id_textView);
        mailAddress_textView = (TextView) findViewById(R.id.createAccount_mailAddress_textView);
        password_textView = (TextView) findViewById(R.id.createAccount_password_textView);
        password_check_textView = (TextView) findViewById(R.id.createAccount_password_check_textView);
        id_editText = (EditText) findViewById(R.id.createAccount_id_editText);
        mailAddress_editText = (EditText) findViewById(R.id.createAccount_mailAddress_editText);
        password_editText = (EditText) findViewById(R.id.createAccount_password_editText);
        password_check_editText = (EditText) findViewById(R.id.createAccount_password_check_editText);
        password_correctCheck_textView = (TextView) findViewById(R.id.createAccount_password_correctCheck_textView);
        accountCreate_button = (Button) findViewById(R.id.createAccount_button);
        titlebar_button_back = (Button) findViewById(R.id.titlebar_button_back);
        titlebar_button_home = (Button) findViewById(R.id.titlebar_button_home);
        createAccount_authentication_textView = (TextView) findViewById(R.id.createAccount_authentication_textView);
        createAccount_authentication_editText = (EditText) findViewById(R.id.createAccount_authentication_editText);
        createAccount_authentication_button = (Button) findViewById(R.id.createAccount_authentication_button);

        // DB 연동 인스턴스 초기화
        dbManager = new DB_Manager();

        // 클래스 객체 초기화
        verifyUserInfo = (VerifyUserInfo) getApplicationContext();
        verifyUserInfo.addActivities(this);

        mailAddress_editText.setVisibility(View.VISIBLE);
        createAccount_authentication_textView.setVisibility(View.INVISIBLE);
        createAccount_authentication_editText.setVisibility(View.INVISIBLE);
        createAccount_authentication_button.setVisibility(View.INVISIBLE);
        // MainActivity에서 전달받은 intent가 있는지 확인
        // 사용자가 비밀번호를 5회 이상 틀렸을 경우, intent 수신
        Intent intent = getIntent();
        if (!TextUtils.isEmpty(intent.getStringExtra(DB_Data.STRING_USER_ID))) {
//        if (intent.getStringExtra(DB_Data.STRING_USER_MAILADDRESS) != null && intent.getStringExtra(DB_Data.STRING_USER_MAILADDRESS) != "") {
            user_id = intent.getStringExtra(DB_Data.STRING_USER_ID);
            // 사용자의 메일 주소를 editText에 설정
            id_editText.setText(user_id);

            // 비밀번호 재설정 화면으로 레이아웃 변경
            createAccount_title.setText(getString(R.string.password_reset_title));
            id_textView.setText(getString(R.string.password_reset_id));
            mailAddress_textView.setText(getString(R.string.password_reset_mailAddress));
            password_textView.setText(getString(R.string.password_reset_password));
            password_check_textView.setText(getString(R.string.password_reset_password_check));
            accountCreate_button.setText(getString(R.string.password_reset_button));
            isPasswordResetActivity = true;

            mailAddress_editText.setVisibility(View.INVISIBLE);
            createAccount_authentication_editText.setVisibility(View.VISIBLE);
            createAccount_authentication_button.setVisibility(View.VISIBLE);
        }


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
                    // 비밀번호 확인란 입력과 동시에 비밀번호 일치 여부 확인
                    checkPassword();
                }
                // 입력하지 않았을 경우 textView 초기화
                else {
                    if (password_correctCheck_textView.getText() != "")
                        password_correctCheck_textView.setText("");
                    // 계정 생성 버튼 비활성화
//                    accountCreate_button.setEnabled(false);
                }
            }
        };

        // 비밀번호 확인란 EditText 값 변경 이벤트 탐지
        password_check_editText.addTextChangedListener(textWatcher);

        // 버튼 클릭 이벤트 객체 생성
        BtnOnClickListener onClickListener = new BtnOnClickListener();
        accountCreate_button.setOnClickListener(onClickListener);
        titlebar_button_back.setOnClickListener(onClickListener);
        titlebar_button_home.setOnClickListener(onClickListener);
        if (isPasswordResetActivity)
            createAccount_authentication_button.setOnClickListener(onClickListener);

        // 계정 생성 버튼 비활성화
//        accountCreate_button.setEnabled(false);
    }

    class BtnOnClickListener implements Button.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.createAccount_button:
                    // DB에 계정 정보 삽입
                    if (!DB_Data.IS_TEST_VERSION) {
                        if (isPasswordResetActivity)
                            UpdateUserInfo();
                        else
                            InsertUserInfo();
                    }
                    break;
                case R.id.createAccount_authentication_button:
                    // 인증번호 전송일 경우
                    if(createAccount_authentication_button.getText().toString().equals(getString(R.string.button_authentication_before))){
                        createAccount_authentication_textView.setVisibility(View.INVISIBLE);
                        IfUserExistSendMail();
                        // 인증하기 텍스트로 변경
                        createAccount_authentication_button.setText(getString(R.string.button_authentication_after));
                    }
                    // 인증번호 확인일 경우
                    else if(createAccount_authentication_button.getText().toString().equals(getString(R.string.button_authentication_after))){
                        createAccount_authentication_textView.setVisibility(View.VISIBLE);
                        isAuthenticationNumberCorrect = CheckAuthenticationNumber();

                        // (인증번호)전송하기 텍스트로 변경
                        if(isAuthenticationNumberCorrect){
                            password_editText.requestFocus();
                            createAccount_authentication_button.setText(getString(R.string.button_authentication_after));
                        }
                    }
                    break;
                case R.id.titlebar_button_home:
                    Intent intentToHome = new Intent(CreateAccountActivity.this, MainActivity.class);
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

    // 비밀번호 입력란과 비밀번호 확인 입력란의 일치성 확인
    private void checkPassword() {
        password = password_editText.getText().toString();

        // 비밀번호가 일치할 경우
        if (password.equals(password_check_editText.getText().toString())) {
            // 일치 문구 출력
            password_correctCheck_textView.setText(R.string.password_check_correct);
            password_correctCheck_textView.setTextColor(ContextCompat.getColor(this, R.color.colorGreen));
            // 계정 생성 버튼 활성화
            accountCreate_button.setEnabled(true);
            isPasswordCorrect = true;
        }
        // 일치하지 않을 경우
        else {
            // 불일치 문구 출력
            password_correctCheck_textView.setText(R.string.password_check_incorrect);
            password_correctCheck_textView.setTextColor(ContextCompat.getColor(this, R.color.colorRed));
            isPasswordCorrect = false;
            // 계정 생성 버튼 비활성화
//            accountCreate_button.setEnabled(false);
        }
    }

    // DB UserInfo 테이블에 사용자 정보 삽입
    private void InsertUserInfo() {
        user_id = id_editText.getText().toString();
        mailAddress = mailAddress_editText.getText().toString();
        password = password_editText.getText().toString();

        result = verifyUserInfo.VerifyUserInfo(user_id, password);

        // 계정이 이미 존재할 경우
        if (result.equals(DB_Data.DATA_CORRECT))
            ShowDialog(R.drawable.error_exist_mailaddress);
            // 메일 주소 혹은 비밀번호를 제대로 입력하지 않았을 경우
        else if (result.equals(getString(R.string.error_empty_mailAddress_password)))
            ShowDialog(R.drawable.error_input_all);
            // 비밀번호 입력란, 비밀번호 확인란이 일치하지 않을 경우
        else if (!isPasswordCorrect)
            ShowDialog(R.drawable.error_password_check_incorrect);
        else {
            String[] inputData = new String[3];
            inputData[0] = mailAddress;
            inputData[1] = password;
            inputData[2] = user_id;

            String result = verifyUserInfo.InsertDataInTable(DB_Data.TABLE_USER_INFO, inputData);
            // 삽입에 성공할 경우, "1 record added" 메세지가 출력됨
//            toastMessage(result);
            if (result.equals(DB_Data.STRING_DB_RESULT_OK)) {
                // 사용자의 메일 주소를 모든 Activity에서 공유
                verifyUserInfo.setUserID(mailAddress);
                // Home 화면으로 이동
                MoveToHomeActivity();
            } else {
                toastMessage("DB 연결에 실패하였습니다.");
            }
        }
    }

    // Home 화면으로 이동
    private void MoveToHomeActivity() {
        Intent intent = new Intent(CreateAccountActivity.this, HomeActivity.class);
        intent.putExtra(DB_Data.STRING_USER_ID, user_id);
        intent.putExtra(DB_Data.STRING_USER_PASSWORD, password);
        startActivity(intent);
        finish();
    }

    // DB UserInfo 테이블에 사용자의 새로운 비밀번호로 갱신
    private void UpdateUserInfo() {
        password = password_editText.getText().toString();

        // 비밀번호를 제대로 입력하지 않았을 경우
        if (TextUtils.isEmpty(password) || TextUtils.isEmpty(password_check_editText.getText().toString()))
//        if (password == "" || password_check_editText.getText().toString() == "" || TextUtils.isEmpty(mailAddress))
            ShowDialog(R.drawable.error_login_empty);
            // 비밀번호 입력란, 비밀번호 확인란이 일치하지 않을 경우
        else if (!isPasswordCorrect)
            ShowDialog(R.drawable.error_password_check_incorrect);
        else if(!isAuthenticationNumberCorrect)
            ShowDialog(R.drawable.error_none_mailaddress);
        else {
            ArrayList<ListItem> userData = new ArrayList<ListItem>();
            userData = verifyUserInfo.GetDataFromTable(DB_Data.TABLE_USER_INFO, mailAddress, user_id);

            // DB에 검색해 반환받은 결과값이 없을 경우, 계정 정보가 없다는 오류 메세지 띄움
            if (userData.size() < 1) {
                ShowDialog(R.drawable.error_none_mailaddress);
                return;
            }

            // 비밀번호 재설정
            String[] inputData = new String[1];
            inputData[0] = password;

            result = verifyUserInfo.UpdateDataInTable(DB_Data.TABLE_USER_INFO, inputData, mailAddress);
            if (result.equals(DB_Data.STRING_DB_RESULT_OK) || result.equals(DB_Data.STRING_DB_UPDATE_RESULT_OK)) {
                toastMessage("비밀번호가 변경되었습니다.");
                // Home 화면으로 이동
                MoveToHomeActivity();
            } else {
                toastMessage("DB 연결에 실패하였습니다.");
            }

        }

    }

    private final String PASSWORD = "rhtmdgml77";
    private final String TAG = "Tekton_Main";

    // DB UserInfo 테이블에 사용자 정보가 존재할 경우, 해당 메일 주소로 인증번호 전송
    private void IfUserExistSendMail(){
        user_id = id_editText.getText().toString();

        if(TextUtils.isEmpty(user_id))
            toastMessage("id를 입력해 주세요.");
        else{
            ArrayList<ListItem> userData = new ArrayList<ListItem>();
            userData = verifyUserInfo.GetDataFromTable(DB_Data.TABLE_USER_INFO, user_id);

            // DB에 검색해 반환받은 결과값이 없을 경우, 계정 정보가 없다는 오류 메세지 띄움
            if (userData.size() < 1) {
                ShowDialog(R.drawable.error_none_mailaddress);
                return;
            }

            if(!TextUtils.isEmpty(verifyUserInfo.getUserData().get(0).getData(DB_Data.INDEX_USER_MAILADDRESS))){
                mailAddress = verifyUserInfo.getUserData().get(0).getData(DB_Data.INDEX_USER_MAILADDRESS);
                SendMail(verifyUserInfo.getUserData().get(0).getData(DB_Data.INDEX_USER_MAILADDRESS));
            }
            else{
                toastMessage("사용자 메일 주소가 존재하지 않습니다.");
            }

        }

    }

    private void SendMail(String mailTo) {
        MailSender mailSender = new MailSender("sh.ko@tektonspace.com", PASSWORD);
        if (android.os.Build.VERSION.SDK_INT > 9) {

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();

            StrictMode.setThreadPolicy(policy);

        }
        // HERE
        try {
            authenticationNumber = GetRandomNumber();
            String mailSubject = getString(R.string.app_name) + " 인증번호가 발급되었습니다.";
            String mailBody = "인증번호 [" + authenticationNumber + "]";
            String mailFrom = getString(R.string.tekton_mail);
            mailSender.sendMail(mailSubject, // subject.getText().toString(),
                    mailBody, // body.getText().toString(),
                    mailFrom, // from.getText().toString(),
                    mailTo // to.getText().toString()
            );

            toastMessage("전송되었습니다.");
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

    }

    private String GetRandomNumber() {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i <= 6; i++) {
            int num = (int) (Math.random() * 10);
            buffer.append(num);
        }

        return buffer.toString();
    }

    private boolean CheckAuthenticationNumber(){
        boolean retval = false;
        String inputAuthenticationNumber = createAccount_authentication_editText.getText().toString();
        if(inputAuthenticationNumber.equals(authenticationNumber))
            return retval = true;
        else
            ShowDialog(R.drawable.error_authentication);

        return retval;
    }
    private void ShowDialog(int errorMessage) {
        popupDialog = new PopupDialog(this, errorMessage, leftListener);
        popupDialog.show();
    }

    private View.OnClickListener leftListener = new View.OnClickListener() {
        public void onClick(View v) {
            popupDialog.dismiss();
        }
    };

    private void toastMessage(String Message) {
        Toast.makeText(this, Message, Toast.LENGTH_SHORT).show();
    }

}
