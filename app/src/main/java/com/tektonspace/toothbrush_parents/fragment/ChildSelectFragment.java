package com.tektonspace.toothbrush_parents.fragment;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tektonspace.toothbrush_parents.R;
import com.tektonspace.toothbrush_parents.activities.ChildDetailActivity;
import com.tektonspace.toothbrush_parents.activities.ChildEditActivity;
import com.tektonspace.toothbrush_parents.activities.ChildSelectActivity;
import com.tektonspace.toothbrush_parents.activities.HomeActivity;
import com.tektonspace.toothbrush_parents.activities.InstructionActivity;
import com.tektonspace.toothbrush_parents.activities.RewardSystemActivity;
import com.tektonspace.toothbrush_parents.activities.ToothbrushDataActivity;
import com.tektonspace.toothbrush_parents.adapter.ChildSelectListViewAdapter;
import com.tektonspace.toothbrush_parents.adapter.HorizontalListView;
import com.tektonspace.toothbrush_parents.adapter.HorizontalListViewAdapter;
import com.tektonspace.toothbrush_parents.adapter.ListItem;
import com.tektonspace.toothbrush_parents.bluetooth.BluetoothConnectService;
import com.tektonspace.toothbrush_parents.constants.DB_Data;
import com.tektonspace.toothbrush_parents.db.VerifyUserInfo;

import java.util.ArrayList;


/**
 * Created by SeungHee on 2017-04-14.
 * 아이 선택 화면 (생성된 아이 정보가 있는 경우)
 * <p>
 * Bluetooth 통신 시도
 * <p>
 * Todo:
 * ChildSelectActivity에서 새로고침버튼을 눌렀을 경우,
 * 태블릿으로부터 수신한 데이터를 클라우드에 저장
 * <p>
 * ContextCompat.getDrawable(getActivity(), R.drawable.user) -> 각각 아이 캐릭터와 사진으로 변경
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ChildSelectFragment extends Fragment {
    RelativeLayout home_photo_layout;
    // 아이 배경 사진, 아이 사진, 캐릭터 사진
    ImageView home_background_photo_imageView, home_childPhoto_imageView, home_character_imageView;
    // 오늘 양치 데이터 (아침, 점심, 저녁)
    ImageView home_toothbrush_morning_imageView, home_toothbrush_afternoon_imageView, home_toothbrush_evening_imageView;
    // 보상 알림 표시
    ImageView home_icon_alert_reward_imageView;
    // 캐릭터 닉네임, 아이 이름
    TextView home_character_textView, home_child_name_textView;
    // 아이 정보 편집 버튼
    Button home_child_edit_button;
    // 양치 습관 확인, 보상 알림 및 설정 버튼
    Button home_data_button, home_reward_button;
    // 아이 목록 리스트
    HorizontalListView listView;
    HorizontalListViewAdapter childSelectListViewAdapter;

    ArrayList<ListItem> userInfo, childInfo;
    ListItem currChildInfo = new ListItem("", "", "", "", "", "", "", "");

    VerifyUserInfo verifyUserInfo;

    int characterID = -1;
    String childPhoto = "";
    int childIndexNum = 0;
    String childID = "";
    boolean ifChildPhotoExist = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            GetBundle();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_child_select, null);

        InstantiateInstance(view);
        InsertInfoIntoListview();

        return view;
    }

    private void InstantiateInstance(View view) {
        home_photo_layout = (RelativeLayout) view.findViewById(R.id.home_photo_layout);
        home_background_photo_imageView = (ImageView) view.findViewById(R.id.home_background_photo_imageView);
        home_childPhoto_imageView = (ImageView) view.findViewById(R.id.home_childPhoto_imageView);
        home_character_imageView = (ImageView) view.findViewById(R.id.home_character_imageView);
        home_toothbrush_morning_imageView = (ImageView) view.findViewById(R.id.home_toothbrush_morning_imageView);
        home_toothbrush_afternoon_imageView = (ImageView) view.findViewById(R.id.home_toothbrush_afternoon_imageView);
        home_toothbrush_evening_imageView = (ImageView) view.findViewById(R.id.home_toothbrush_evening_imageView);
        home_child_edit_button = (Button) view.findViewById(R.id.home_child_edit_button);
        home_data_button = (Button) view.findViewById(R.id.home_data_button);
        home_reward_button = (Button) view.findViewById(R.id.home_reward_button);
        home_character_textView = (TextView) view.findViewById(R.id.home_character_textView);
        home_child_name_textView = (TextView) view.findViewById(R.id.home_child_name_textView);
        home_icon_alert_reward_imageView = (ImageView) view.findViewById(R.id.home_icon_alert_reward_imageView);
        home_icon_alert_reward_imageView.setVisibility(View.INVISIBLE);
        listView = (HorizontalListView) view.findViewById(R.id.home_child_listview);
        childInfo = new ArrayList<ListItem>();
        // listview adapter 생성
        childSelectListViewAdapter = new HorizontalListViewAdapter();
        // listview에 adapter 연결
        listView.setAdapter(childSelectListViewAdapter);

        // BtnOnClickListener의 객체 생성
        BtnOnClickListener onClickListener = new BtnOnClickListener();
        home_data_button.setOnClickListener(onClickListener);
        home_reward_button.setOnClickListener(onClickListener);
        home_child_edit_button.setOnClickListener(onClickListener);
        verifyUserInfo = (VerifyUserInfo) getActivity().getApplicationContext();
        // 사용자 계정 정보 저장
        userInfo = verifyUserInfo.getUserData();
        // listview에 adapter 연결
        listView.setAdapter(childSelectListViewAdapter);
        // 사용자가 아이 리스트 중 한 명을 선택했을 때
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 해당 아이의 정보 화면으로 전환

                Intent intentToDetail = new Intent(getActivity(), ChildDetailActivity.class);
                if (!DB_Data.IS_TEST_VERSION) {
                    // 해당 ID값을 가진 아이의 정보를 ChildInfo 테이블에서 검색
                    childInfo = verifyUserInfo.GetDataFromTable(DB_Data.TABLE_CHILD_INFO, verifyUserInfo.getUserData().get(position).getData(DB_Data.INDEX_USER_CHILDID));
//                    verifyUserInfo.setChildData(childInfo.get(0));
                    childID = childInfo.get(0).getData(DB_Data.INDEX_CHILD_ID);
                    verifyUserInfo.setChildID(childInfo.get(0).getData(DB_Data.INDEX_CHILD_ID));
                    SetChildInfo(childInfo.get(0));

//                intentToDetail.putExtra(DB_Data.STRING_USER_ID, verifyUserInfo.getUserData().get(position).getData(DB_Data.INDEX_USER_ID));
//                intentToDetail.putExtra(DB_Data.STRING_CHILD_NAME, childInfo.get(0).getData(DB_Data.INDEX_CHILD_NAME));
//                intentToDetail.putExtra(DB_Data.STRING_CHILD_ID, childInfo.get(0).getData(DB_Data.INDEX_CHILD_ID));
//                intentToDetail.putExtra(DB_Data.STRING_CHILD_PHOTO, childInfo.get(0).getData(DB_Data.INDEX_CHILD_PHOTO));
                } else {
//                    intentToDetail.putExtra(DB_Data.STRING_USER_ID,verifyUserInfo.getUserData().get(position).getData(DB_Data.INDEX_USER_ID));
//                    intentToDetail.putExtra(DB_Data.STRING_CHILD_NAME, verifyUserInfo.getUserData().get(position).getData(DB_Data.INDEX_USER_CHILDNAME));
//                    intentToDetail.putExtra(DB_Data.STRING_CHILD_ID, verifyUserInfo.getUserData().get(position).getData(DB_Data.INDEX_USER_CHILDID));
//                    intentToDetail.putExtra(DB_Data.STRING_CHILD_PHOTO,"");
                }
//                startActivity(intentToDetail);
            }
        });

    }

    private void GetBundle() {
        String[] childData = new String[8];
        for (int i = 0; i < 8; i++) {
            childData[i] = "";
        }
        childData[DB_Data.INDEX_CHILD_ID] = getArguments().getString(DB_Data.STRING_CHILD_ID);
        childData[DB_Data.INDEX_CHILD_PHOTO] = getArguments().getString(DB_Data.STRING_CHILD_PHOTO);
        childData[DB_Data.INDEX_CHILD_NAME] = getArguments().getString(DB_Data.STRING_CHILD_NAME);
        childData[DB_Data.INDEX_CHILD_BACKGROUNDPHOTO] = getArguments().getString(DB_Data.STRING_CHILD_BACKGROUNDPHOTO);

        currChildInfo.setData(childData);
    }

    private void InsertInfoIntoListview() {
        if (!DB_Data.IS_TEST_VERSION) {
            // WIFI 모드일 경우 태블릿에서 수신한 ChildID값으로 ChildInfo 테이블 검색
            if (verifyUserInfo.getIsWIFIMode()) {
                childIndexNum = 0;
                // ChildInfo 테이블에 검색 후 child nickname, 캐릭터 가져오기
                for (ListItem item : userInfo) {
                    childPhoto = "";
                    if (!TextUtils.isEmpty(item.getData(DB_Data.INDEX_USER_CHILDID)) && Integer.parseInt(item.getData(DB_Data.INDEX_USER_CHILDID)) > 0) {
//                    if(!item.getData(DB_Data.INDEX_USER_CHILDID).isEmpty() && Integer.parseInt(item.getData(DB_Data.INDEX_USER_CHILDID)) > 0){
                        // 해당 ID값을 가진 아이의 정보를 ChildInfo 테이블에서 검색
                        childInfo = verifyUserInfo.GetDataFromTable(DB_Data.TABLE_CHILD_INFO, item.getData(DB_Data.INDEX_USER_CHILDID));
                        // 첫 화면에 첫 번째 아이의 정보가 출력되도록 함
                        if (childIndexNum++ == 0) {
                            if (TextUtils.isEmpty(currChildInfo.getData(0))) {
                                verifyUserInfo.setChildID(childInfo.get(0).getData(DB_Data.INDEX_CHILD_ID));
                                for (int i = 0; i < currChildInfo.length(); i++)
                                    currChildInfo.changeData(i, childInfo.get(0).getData(i));
                            }
                        }
                        if (!TextUtils.isEmpty(childInfo.get(0).getData(DB_Data.INDEX_CHILD_CHARACTER)))
//                        if (!childInfo.get(0).getData(DB_Data.INDEX_CHILD_CHARACTER).isEmpty())
                            characterID = verifyUserInfo.GetCharacter(childInfo.get(0).getData(DB_Data.INDEX_CHILD_CHARACTER));
                        // 지정된 아이의 사진이 있을 경우
                        if (!TextUtils.isEmpty(childInfo.get(0).getData(DB_Data.INDEX_CHILD_PHOTO)) && !childInfo.get(0).getData(DB_Data.INDEX_CHILD_PHOTO).equals("null")) {
//                        if (childInfo.get(0).getData(DB_Data.INDEX_CHILD_PHOTO).length() > 0 && !childInfo.get(0).getData(DB_Data.INDEX_CHILD_PHOTO).equals("null")){
                            verifyUserInfo.CheckPermission(getActivity());
                            if (verifyUserInfo.getisPermissionGranted()) {
                                childPhoto = verifyUserInfo.getRealPathFromURI(Uri.parse(childInfo.get(0).getData(DB_Data.INDEX_CHILD_PHOTO)));
                            }
                        } else {
                            childPhoto = "";
                        }
//
//                        String childName = getString(R.string.none_childName);
//                        // 아이 이름 정보가 있을 경우
//                        if (!TextUtils.isEmpty(childInfo.get(0).getData(DB_Data.INDEX_CHILD_NAME))) {
////                        if(childInfo.get(0).getData(DB_Data.INDEX_CHILD_NAME).length() > 0){
//                            childName = childInfo.get(0).getData(DB_Data.INDEX_CHILD_NAME);
//                        }
                        childSelectListViewAdapter.addItem(childPhoto);
                    }
                }

                SetChildInfo(currChildInfo);
            }
            // 블루투스 모드일 경우
            else {

            }
        }
//        else {
//        }

    }

    private void SetChildInfo(ListItem listItem) {
        childID = listItem.getData(DB_Data.INDEX_CHILD_ID);
        verifyUserInfo.setChildData(listItem);
        verifyUserInfo.SetImageViewPhoto(getActivity(), home_childPhoto_imageView);
        verifyUserInfo.SetImageViewBackgroundPhoto(getActivity(), home_background_photo_imageView);
        verifyUserInfo.SetTextViewName(home_child_name_textView);
        // DB 연동 후 보상 테이블 데이터 확인
        CheckRewardInfo();
    }

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
                        home_icon_alert_reward_imageView.setVisibility(View.VISIBLE);
                    } else
                        home_icon_alert_reward_imageView.setVisibility(View.INVISIBLE);
                }
                else
                    home_icon_alert_reward_imageView.setVisibility(View.INVISIBLE);
            }
            // RewardData에 검색된 ChildID와 현재 아이의 ChildID가 불일치할 경우, DB에 해당 아이의 보상 정보가 없는 것
            // rewardData 초기화
            else{
                verifyUserInfo.clearRewardData();
                home_icon_alert_reward_imageView.setVisibility(View.INVISIBLE);
            }

        }
        else{
            home_icon_alert_reward_imageView.setVisibility(View.VISIBLE);
        }

    }
    private Drawable GetDrawable(int drawableIndex) {
        Drawable returnDrawable;

        if (drawableIndex == -1)
            returnDrawable = ContextCompat.getDrawable(getActivity(), R.drawable.childselect_photo_frame_none);
        else
            returnDrawable = ContextCompat.getDrawable(getActivity(), drawableIndex);
        return returnDrawable;
    }

    class BtnOnClickListener implements Button.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.home_child_edit_button:
                    // 아이 정보 편집 화면으로 전환
                    Intent intentToEdit = new Intent(ChildSelectFragment.this.getActivity(), ChildEditActivity.class);
                    startActivity(intentToEdit);
                    break;
                case R.id.home_data_button:
                    // 양치 데이터 확인 화면으로 전환
                    Intent intentToToothbrush = new Intent(ChildSelectFragment.this.getActivity(), ToothbrushDataActivity.class);
                    startActivity(intentToToothbrush);
                    break;
                case R.id.home_reward_button:
                    // 보상 시스템 화면으로 전환
                    Intent intentToReward = new Intent(ChildSelectFragment.this.getActivity(), RewardSystemActivity.class);
                    startActivity(intentToReward);
                    break;
            }
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

}
