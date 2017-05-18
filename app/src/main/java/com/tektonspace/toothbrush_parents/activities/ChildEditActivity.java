package com.tektonspace.toothbrush_parents.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tektonspace.toothbrush_parents.R;
import com.tektonspace.toothbrush_parents.adapter.ListItem;
import com.tektonspace.toothbrush_parents.bluetooth.BluetoothConnectService;
import com.tektonspace.toothbrush_parents.constants.DB_Data;
import com.tektonspace.toothbrush_parents.db.VerifyUserInfo;
import com.tektonspace.toothbrush_parents.utils.ExtensionEditText;

import java.io.FileNotFoundException;
import java.io.IOException;


/**
 * Created by SeungHee on 2017-04-26.
 * 아이 정보 편집 화면
 * <p>
 * Todo: SaveDataInDB() 내에서 태블릿이 블루투스 모드일 경우 DB에 송신하는 코드 추가 필요
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ChildEditActivity extends AppCompatActivity {
    RelativeLayout childEdit_photo_layout, childedit_layout, childedit_background_photo_layout;
    ImageView childEdit_childPhoto_imageView, childedit_background_photo_imageView;
    ExtensionEditText childEdit_name_editText;
    Button childEdit_save_button, childedit_background_photo_button, titlebar_button_back, titlebar_button_home, titlebar_button_instruction, titlebar_button_teachbrush;

    // 아이 이름, 아이 사진 uri
    String childName, childPhotoUri = "", childBackgroundPhotoUri = "";
    boolean ifChildPhotoExist = false;

    VerifyUserInfo verifyUserInfo;

    Intent intentToGallery;

    final int REQ_CODE_PICK_PICTURE = 0;
    final int REQ_CODE_PICK_PICTURE_BACKGROUND = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_edit);
        InstantiateInstance();
    }

    private void InstantiateInstance() {
        childedit_layout = (RelativeLayout) findViewById(R.id.childedit_layout);
        childEdit_photo_layout = (RelativeLayout) findViewById(R.id.childedit_photo_layout);
        childedit_background_photo_layout = (RelativeLayout) findViewById(R.id.childedit_background_photo_layout);
        childEdit_childPhoto_imageView = (ImageView) findViewById(R.id.childEdit_childPhoto_imageView);
        childedit_background_photo_imageView = (ImageView) findViewById(R.id.childedit_background_photo_imageView);
        childEdit_name_editText = (ExtensionEditText) findViewById(R.id.childEdit_name_editText);
        childEdit_save_button = (Button) findViewById(R.id.childEdit_save_button);
        childedit_background_photo_button = (Button) findViewById(R.id.childedit_background_photo_button);
        titlebar_button_back = (Button) findViewById(R.id.titlebar_button_back);
        titlebar_button_home = (Button) findViewById(R.id.titlebar_button_home);
        titlebar_button_instruction = (Button) findViewById(R.id.titlebar_button_instruction);
        titlebar_button_teachbrush = (Button) findViewById(R.id.titlebar_button_teachbrush);

        Typeface font_regular = Typeface.createFromAsset(getAssets(), "yanolza_regular.ttf");
        Typeface font_bold = Typeface.createFromAsset(getAssets(), "yanolza_bold.ttf");
        childEdit_name_editText.setTypeface(font_regular);

        childEdit_name_editText.requestFocus();
        childEdit_name_editText.setHiddenKeyboardOnBackPressed(false); // 백버튼을 눌러도 키보드가 사라지지 않게 지정
        Handler handler = new Handler() {
            /**
             * @param msg
             * @see android.os.Handler#handleMessage(android.os.Message)
             */
            @Override
            public void handleMessage(Message msg) {
                finish();
            }
        };

        childEdit_name_editText.setOnBackPressedHandler(handler);
//키보드를 띄운다.
        new Handler().postDelayed(new Runnable() {
            public void run() {
                InputMethodManager aaa = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                aaa.showSoftInput(childEdit_name_editText,
                        InputMethodManager.SHOW_IMPLICIT);
            }
        }, 100);
        // 버튼 클릭 이벤트 객체 생성
        BtnOnClickListener onClickListener = new BtnOnClickListener();
        childEdit_save_button.setOnClickListener(onClickListener);
        childEdit_photo_layout.setOnClickListener(onClickListener);
        childEdit_childPhoto_imageView.setOnClickListener(onClickListener);
        titlebar_button_back.setOnClickListener(onClickListener);
        titlebar_button_home.setOnClickListener(onClickListener);
        childedit_background_photo_button.setOnClickListener(onClickListener);

        verifyUserInfo = (VerifyUserInfo) getApplicationContext();

        // 아이 사진, 이름 정보가 있을 경우 화면에 출력
        ifChildPhotoExist = verifyUserInfo.SetImageViewPhoto(this, childEdit_childPhoto_imageView);
        verifyUserInfo.SetImageViewBackgroundPhoto(this, childedit_background_photo_imageView);
        verifyUserInfo.SetTextViewName(childEdit_name_editText);
        verifyUserInfo.addActivities(this);

//        // 아이 이름 정보가 없을 경우
        if (childEdit_name_editText.getText().toString().equals(getString(R.string.none_childName))) {
            childEdit_name_editText.setText("");
            // 커서 위치 제일 마지막으로 이동
            Editable editable = childEdit_name_editText.getText();
            Selection.setSelection(editable, editable.length());
        }
////        if(childEdit_name_editText.getText().toString().isEmpty()){
////            childedit_layout.setBackground(getDrawable(R.drawable.childdetail_background_none));
//        } else
//            childedit_layout.setBackground(getDrawable(R.drawable.childdetail_background));
        // 아이 사진 정보가 있을 경우
        if (ifChildPhotoExist) {
//            childEdit_photo_layout.setBackground(getDrawable(R.drawable.childedit_photo));
//            childEdit_childPhoto_imageView.setVisibility(View.VISIBLE);
        }
        // 아이 사진 정보가 없을 경우
        else {
//            childEdit_photo_layout.setBackground(getDrawable(R.drawable.childselect_photo_frame_none1));
            childEdit_childPhoto_imageView.setImageDrawable(getDrawable(R.drawable.childedit_photo1));
        }

        childEdit_name_editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId == EditorInfo.IME_ACTION_DONE) || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    saveButtonClickEvent();
                }
                return false;
            }
        });

    }

    private void saveButtonClickEvent() {
        SaveDataInDB();
        // 아이 정보 화면으로 전환
        Intent intentToDetail = new Intent(ChildEditActivity.this, HomeActivity.class);
        intentToDetail.putExtra(DB_Data.STRING_USER_MAILADDRESS, verifyUserInfo.getUserID());
        intentToDetail.putExtra(DB_Data.STRING_CHILD_NAME, verifyUserInfo.getChildData().getData(DB_Data.INDEX_CHILD_NAME));
        intentToDetail.putExtra(DB_Data.STRING_CHILD_ID, verifyUserInfo.getChildData().getData(DB_Data.INDEX_CHILD_ID));
        intentToDetail.putExtra(DB_Data.STRING_CHILD_PHOTO, verifyUserInfo.getChildData().getData(DB_Data.INDEX_CHILD_PHOTO));
        intentToDetail.putExtra(DB_Data.STRING_CHILD_BACKGROUNDPHOTO, verifyUserInfo.getChildData().getData(DB_Data.INDEX_CHILD_BACKGROUNDPHOTO));
        HomeActivity homeActivity = (HomeActivity) HomeActivity.homeActivity;
        homeActivity.finish();
        startActivity(intentToDetail);
        finish();
    }

    class BtnOnClickListener implements Button.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.childEdit_save_button:
                    saveButtonClickEvent();
                    break;
                case R.id.childedit_photo_layout:
                case R.id.childEdit_childPhoto_imageView:
                    verifyUserInfo.CheckPermission(ChildEditActivity.this);
                    if (verifyUserInfo.getisPermissionGranted()) {
                        intentToGallery = new Intent(Intent.ACTION_PICK);
                        intentToGallery.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                        intentToGallery.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        // 결과를 리턴하는 Activity 호출
                        startActivityForResult(intentToGallery, REQ_CODE_PICK_PICTURE);
                    }
                    break;
                case R.id.childedit_background_photo_button:
                    verifyUserInfo.CheckPermission(ChildEditActivity.this);
                    if (verifyUserInfo.getisPermissionGranted()) {
                        intentToGallery = new Intent(Intent.ACTION_PICK);
                        intentToGallery.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                        intentToGallery.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        // 결과를 리턴하는 Activity 호출
                        startActivityForResult(intentToGallery, REQ_CODE_PICK_PICTURE_BACKGROUND);
                    }
                    break;
                case R.id.titlebar_button_home:
                    Intent intentToHome = new Intent(ChildEditActivity.this, HomeActivity.class);
                    verifyUserInfo.clearActivities();
                    startActivity(intentToHome);
                    finish();
                    break;
                case R.id.titlebar_button_back:
                    finish();
                    break;
                case R.id.titlebar_button_instruction:
                    break;
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQ_CODE_PICK_PICTURE:
                if (resultCode == RESULT_CANCELED)
                    return;

                if (data != null) {
                    Uri imgUri = data.getData();
                    childPhotoUri = imgUri.toString();
                    if (!TextUtils.isEmpty(childPhotoUri)) {
//                    if (childPhotoUri.length() > 0 && !childPhotoUri.equals("null")) {
                        verifyUserInfo.CheckPermission(this);
                        if (verifyUserInfo.getisPermissionGranted()) {
                            String imagePath = "";
                            imagePath = verifyUserInfo.getRealPathFromURI(imgUri);

                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inSampleSize = verifyUserInfo.getBitmapImageSize();
                            Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);

                            if (childEdit_childPhoto_imageView.getVisibility() == View.INVISIBLE)
                                childEdit_childPhoto_imageView.setVisibility(View.VISIBLE);
                            childEdit_photo_layout.setBackground(getDrawable(R.drawable.childedit_photoframe));
                            childEdit_childPhoto_imageView.setImageURI(imgUri);
                        }
                    }

                }
                break;
            case REQ_CODE_PICK_PICTURE_BACKGROUND:
                if (resultCode == RESULT_CANCELED)
                    return;

                if (data != null) {
                    Uri imgUri = data.getData();
                    childBackgroundPhotoUri = imgUri.toString();
                    if (!TextUtils.isEmpty(childBackgroundPhotoUri)) {
//                    if (childPhotoUri.length() > 0 && !childPhotoUri.equals("null")) {
                        verifyUserInfo.CheckPermission(this);
                        if (verifyUserInfo.getisPermissionGranted()) {
                            String imagePath = "";
                            imagePath = verifyUserInfo.getRealPathFromURI(imgUri);

                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inSampleSize = verifyUserInfo.getBitmapImageSize();
                            Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);

                            childedit_background_photo_imageView.setImageURI(imgUri);
                        }
                    }

                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        finish();
    }

    private void SaveDataInDB() {
        childName = childEdit_name_editText.getText().toString();
        String result = "";
        if (!DB_Data.IS_TEST_VERSION) {
            String[] inputData = new String[3];
            inputData[0] = childName;
            if (!TextUtils.isEmpty(childPhotoUri)) {
//            if (childPhotoUri.length() > 0) {
                if (childPhotoUri != verifyUserInfo.getChildData().getData(DB_Data.INDEX_CHILD_PHOTO))   // 사진이 변경되었을 경우
                    inputData[1] = childPhotoUri;
            } else {
                if (verifyUserInfo.getChildData().getData(DB_Data.INDEX_CHILD_PHOTO).isEmpty())
                    childPhotoUri = "null";
                else
                    childPhotoUri = verifyUserInfo.getChildData().getData(DB_Data.INDEX_CHILD_PHOTO);
                inputData[1] = childPhotoUri;
            }

            if (!TextUtils.isEmpty(childBackgroundPhotoUri)) {
                if (childBackgroundPhotoUri != verifyUserInfo.getChildData().getData(DB_Data.INDEX_CHILD_BACKGROUNDPHOTO))   // 사진이 변경되었을 경우
                    inputData[2] = childBackgroundPhotoUri;
            } else {
                if (verifyUserInfo.getChildData().getData(DB_Data.INDEX_CHILD_BACKGROUNDPHOTO).isEmpty())
                    childBackgroundPhotoUri = "null";
                else
                    childBackgroundPhotoUri = verifyUserInfo.getChildData().getData(DB_Data.INDEX_CHILD_BACKGROUNDPHOTO);
                inputData[2] = childBackgroundPhotoUri;
            }
            verifyUserInfo.changeChildData(DB_Data.INDEX_CHILD_NAME, inputData[0]);
            verifyUserInfo.changeChildData(DB_Data.INDEX_CHILD_PHOTO, inputData[1]);
            verifyUserInfo.changeChildData(DB_Data.INDEX_CHILD_BACKGROUNDPHOTO, inputData[2]);
            // WIFI 모드, 블루투스 모드 상관 없이
            // 태블릿에서 수신한 id값으로 DB에 접근 후 수정
            // 태블릿에서 이미 ChildInfo 테이블에 아이 정보 insert한 상태
            // ChildInfo 테이블에 이름, 사진 uri 수정
            result = verifyUserInfo.UpdateDataInTable(DB_Data.TABLE_CHILD_INFO, inputData, verifyUserInfo.getChildID());
            inputData[1] = verifyUserInfo.getChildID();
            result = verifyUserInfo.UpdateDataInTable(DB_Data.TABLE_USER_INFO, inputData, verifyUserInfo.getUserID());

        } else {
            if (childPhotoUri == verifyUserInfo.getChildData().getData(DB_Data.INDEX_CHILD_PHOTO))   // 사진이 변경되었을 경우
                childPhotoUri = verifyUserInfo.getChildData().getData(DB_Data.INDEX_CHILD_PHOTO);
            verifyUserInfo.setChildData(new ListItem(verifyUserInfo.getChildID(), childName, "", "", "", childPhotoUri, "", ""));
        }
    }

}
