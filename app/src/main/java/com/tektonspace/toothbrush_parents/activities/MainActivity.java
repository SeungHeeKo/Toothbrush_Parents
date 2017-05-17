package com.tektonspace.toothbrush_parents.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tektonspace.toothbrush_parents.constants.DB_Data;
import com.tektonspace.toothbrush_parents.utils.MailSender;
import com.tektonspace.toothbrush_parents.utils.PopupDialog;
import com.tektonspace.toothbrush_parents.R;
import com.tektonspace.toothbrush_parents.db.DB_Manager;
import com.tektonspace.toothbrush_parents.db.VerifyUserInfo;
import com.tektonspace.toothbrush_parents.utils.BaseActivity;

/**
 * Created by SeungHee on 2017-04-11.
 * 로고, 타이틀, 로그인 화면
 */

public class MainActivity extends BaseActivity {
    // main layout
    LinearLayout main_layout;
    // 회사 로고 이미지, 앱 타이틀 이미지를 담는 ImageView
    ImageView logo_imageView;
    // 사용자의 아이디, 비밀번호를 입력받는 EditText
    EditText user_id_editText, password_editText;
    // 로그인, 계정생성 Button
    Button login_button, accountCreate_button;
    // 로딩 progress bar
    ProgressBar progressBar;
    TextView title_copyright;

    // 사용자의 아이디, 메일주소, 비밀번호 저장
    String userID = "", mailAddress = "", password = "";
    // 이미지 fade in, out 효과 및 위로 이동
    Animation fadeIn, fadeOut, translate_up;

    // DB 연동 객체
    DB_Manager dbManager;
    VerifyUserInfo verifyUserInfo;

    // 팝업창 객체
    PopupDialog popupDialog;

    // 사용자의 비밀번호 틀린 횟수
    int incorrectPasswordCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 인스턴스 초기화
        InstantiateInstance();

        // 로고 및 타이틀 이미지, 프로그래스바 진행
        showLogo_Progressbar();
    }

    private void InstantiateInstance() {
        // view 인스턴스 초기화
        main_layout = (LinearLayout) findViewById(R.id.activity_main);
        logo_imageView = (ImageView) findViewById(R.id.logo_imageView);
        user_id_editText = (EditText) findViewById(R.id.user_id_editText);
        password_editText = (EditText) findViewById(R.id.password_editText);
        login_button = (Button) findViewById(R.id.login_button);
        accountCreate_button = (Button) findViewById(R.id.moveToCreateAccount_button);
        title_copyright = (TextView) findViewById(R.id.main_copyright_textView);

        // 버튼 클릭 이벤트 객체 생성
        BtnOnClickListener onClickListener = new BtnOnClickListener();
        login_button.setOnClickListener(onClickListener);
        accountCreate_button.setOnClickListener(onClickListener);

        // animation 인스턴스 초기화
        fadeOut = AnimationUtils.loadAnimation(this, R.anim.fadeout);
        fadeIn = AnimationUtils.loadAnimation(this, R.anim.fadein);
        translate_up = AnimationUtils.loadAnimation(this, R.anim.translate_up);

        // 프로그래스바 인스턴스 초기화
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        // 처음엔 회사 로고만 보이도록 프로그래스바, 로그인화면 비활성화
        progressBar.setVisibility(ProgressBar.GONE);
        user_id_editText.setVisibility(View.GONE);
        password_editText.setVisibility(View.GONE);
        login_button.setVisibility(View.GONE);
        accountCreate_button.setVisibility(View.GONE);

        // DB 연동 인스턴스 초기화
        dbManager = new DB_Manager();

        // DB UserInfo 테이블에 존재하는 데이터와 비교하기 위한 클래스 객체
        verifyUserInfo = (VerifyUserInfo) getApplicationContext();

    }

    private void showLogo_Progressbar() {
        // fade out 효과
        main_layout.setAnimation(fadeOut);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // fade out 효과가 끝나면 회사 로고 -> 앱 타이틀 이미지로 변경, 배경 추가
                logo_imageView.setImageResource(R.mipmap.ui_android_icon);
                main_layout.setBackground(getDrawable(R.drawable.background2));
                title_copyright.setVisibility(View.INVISIBLE);

                // fade in 효과
                main_layout.setAnimation(fadeIn);
                // 앱 타이틀 이미지가 위로 이동되도록 함
                moveAppLogo();
                InstantiateLoginPage();
                /*
                // 프로그래스바 시각화
                progressBar.setVisibility((ProgressBar.VISIBLE));
                // 프로그래스바 진행 시작
                startProgressBarThread();
                */
            }
        }, 2500);   // 2.5초 딜레이를 준 후 앱 타이틀 이미지 fade in, 프로그래스바 진행 시작

    }

    private void setAnimation(Animation animation) {
        // animation으로 이동된 이미지가 animation이 종료된 후 그대로 있도록 함
        animation.setFillAfter(true);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
            }
        });

        // 앱 타이틀 이미지에 animatino 효과 적용
        logo_imageView.startAnimation(animation);
    }

    private volatile Thread progressBarThread;
    public int currentPosition = 0;

    // 프로그래스바가 진행되도록 스레드 선언
    public synchronized void startProgressBarThread() {
        if (progressBarThread == null) {
            progressBarThread = new Thread(null, backgroundThread, "startProgressBarThread");
            currentPosition = 0;
            progressBarThread.start();
        }
    }

    // 프로그래스바를 멈추고, 스레드 종료
    public synchronized void stopProgresBarThread() {
        if (progressBarThread != null) {
            Thread tmpThread = progressBarThread;
            progressBarThread = null;
            tmpThread.interrupt();
        }
        progressBar.setVisibility((ProgressBar.GONE));
    }

    // 프로그래스바의 이동 상태를 0.05초 간격으로 갱신해 진행
    private Runnable backgroundThread = new Runnable() {
        @Override
        public void run() {
            if (Thread.currentThread() == progressBarThread) {
                currentPosition = 0;
                final int total = 100;
                while (currentPosition < total) {
                    try {
                        progressBarHandler.sendMessage(progressBarHandler.obtainMessage());
                        Thread.sleep(50);
                    } catch (final InterruptedException e) {
                        return;
                    } catch (final Exception e) {
                        return;
                    }
                }
            }
        }

        Handler progressBarHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                currentPosition++;
                progressBar.setProgress(currentPosition);
                if (currentPosition == 100) {
                    // 앱 타이틀 이미지가 위로 이동되도록 함
                    moveAppLogo();
                    // 프로그래스바 비활성화
                    stopProgresBarThread();
                }
            }
        };
    };

    private void moveAppLogo() {
        setAnimation(translate_up);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 로그인 화면 시각화
                InstantiateLoginPage();
            }
        }, 500);   // 0.5초 딜레이를 준 후 로그인 화면 출력

    }

    private void InstantiateLoginPage() {
        // 로그인 화면 시각화
        user_id_editText.setVisibility(View.VISIBLE);
        password_editText.setVisibility(View.VISIBLE);
        login_button.setVisibility(View.VISIBLE);
        accountCreate_button.setVisibility(View.VISIBLE);
    }

    class BtnOnClickListener implements Button.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.login_button:
                    CheckUserInfo();
                    break;
                case R.id.moveToCreateAccount_button:
                    Intent intent = new Intent(MainActivity.this, CreateAccountActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    }

    private void CheckUserInfo() {
        userID = user_id_editText.getText().toString();
        password = password_editText.getText().toString();
        String result = "";

        if (TextUtils.isEmpty(userID) || TextUtils.isEmpty(password)) {
//        if (userID.length() < 1 || password.length() < 1) {
            ShowDialog(R.drawable.error_login_empty);
            return;
        }
        if (!DB_Data.IS_TEST_VERSION) {
            result = verifyUserInfo.VerifyUserInfo(userID, password);
        } else {
            result = DB_Data.DATA_CORRECT;
        }

//        result = getString(R.string.password_check_incorrect);
        // 사용자 계정이 존재하지 않을 경우
        if (result.equals(getString(R.string.error_none_mailAddress)))
            ShowDialog(R.drawable.error_none_mailaddress);
            // 비밀번호가 일치하지 않을 경우
        else if (result.equals(getString(R.string.password_check_incorrect))){
            // 사용자가 비밀번호를 5회 이상 틀렸을 경우 비밀번호 재설정 화면으로 전환
            if(incorrectPasswordCount >= 4){
                ShowDialog(R.drawable.error_password_reset);
            }
            else{
                incorrectPasswordCount++;
                ShowDialog(R.drawable.error_password_check_incorrect);
            }
        }
            // 메일 주소 혹은 비밀번호를 제대로 입력하지 않았을 경우
        else if (result.equals(getString(R.string.error_empty_mailAddress_password)))
            ShowDialog(R.drawable.error_login_empty);
            // 사용자 계정이 존재할 경우
        else if (result.equals(DB_Data.DATA_CORRECT)) {
            // 사용자의 메일 주소를 모든 Activity에서 공유
            verifyUserInfo.setUserID(userID);
            // 사용자 계정이 존재하고, 사용자가 제대로 입력했을 경우 HomeActivity로 전환
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            intent.putExtra(DB_Data.STRING_USER_ID, userID);
            intent.putExtra(DB_Data.STRING_USER_PASSWORD, password);
            startActivity(intent);
            finish();
        }
    }

    private void ShowDialog(int errorMessage) {
        if(errorMessage == R.drawable.error_password_reset){
            popupDialog = new PopupDialog(this, errorMessage, passwordResetListener);
            popupDialog.show();
        }
        else{
            popupDialog = new PopupDialog(this, errorMessage, leftListener);
            popupDialog.show();
        }
    }

    private View.OnClickListener leftListener = new View.OnClickListener() {
        public void onClick(View v) {
            popupDialog.dismiss();
        }
    };
    private View.OnClickListener passwordResetListener = new View.OnClickListener() {
        public void onClick(View v) {
            popupDialog.dismiss();
            Intent intent = new Intent(MainActivity.this, CreateAccountActivity.class);
            intent.putExtra(DB_Data.STRING_USER_ID, user_id_editText.getText().toString());
            startActivity(intent);
        }
    };

    private void toastMessage(String Message) {
        Toast.makeText(this, Message, Toast.LENGTH_SHORT).show();
    }

}
