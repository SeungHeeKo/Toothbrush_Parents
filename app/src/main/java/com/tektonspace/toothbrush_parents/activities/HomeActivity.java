package com.tektonspace.toothbrush_parents.activities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tektonspace.toothbrush_parents.adapter.HorizontalListViewAdapter;
import com.tektonspace.toothbrush_parents.adapter.HorizontalListView;
import com.tektonspace.toothbrush_parents.adapter.ListItem;
import com.tektonspace.toothbrush_parents.bluetooth.BluetoothConnectService;
import com.tektonspace.toothbrush_parents.constants.DB_Data;
import com.tektonspace.toothbrush_parents.R;
import com.tektonspace.toothbrush_parents.db.VerifyUserInfo;
import com.tektonspace.toothbrush_parents.fragment.ChildSelectFragment;
import com.tektonspace.toothbrush_parents.fragment.ChildSelectNoneFragment;
import com.tektonspace.toothbrush_parents.utils.BaseActivity;

import java.util.ArrayList;

/**
 * Created by SeungHee on 2017-04-13.
 * Home 화면
 */

public class HomeActivity extends BaseActivity {
    public static HomeActivity homeActivity;
    // 새로고침 버튼
    Button home_refresh_button;
    // 타이틀바 버튼
    Button titlebar_button_instruction, titlebar_button_teachbrush;

    // 사용자의 아이디, 비밀번호, 아이 index 정보가 저장된 intent
    Intent intent_fromMainOrCreateAccountOrEditActivity;
    // 사용자의 아이디, 비밀번호, 아이 index 저장
    public String userID = "", password = "", childID = "";

    // Fragment
    FragmentManager fragmentManager;
    ChildSelectNoneFragment childSelectNoneFragment;
    ChildSelectFragment childSelectFragment;

    boolean isChildExists = false;
    boolean ifChildPhotoExist = false;

    ArrayList<ListItem> userInfo, childInfo;
    VerifyUserInfo verifyUserInfo;
    int childNum = 0;

    int characterID = -1;
    String childPhoto = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Get local Bluetooth adapter
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (bluetoothAdapter == null) {
            FragmentActivity activity = this;
            toastMessage(getString(R.string.error_bluetooth_unable));
            activity.finish();
        }

        // 객체 초기화
        InstantiateInstance();
        getFragmentInstance();
        IfChildExists_ChangeFragment(false);
    }

    private void InstantiateInstance() {
        // 액티비티 객체 생성
        homeActivity = HomeActivity.this;
        home_refresh_button = (Button) findViewById(R.id.home_refresh_button);
        titlebar_button_instruction = (Button) findViewById(R.id.titlebar_button_instruction);
        titlebar_button_teachbrush = (Button) findViewById(R.id.titlebar_button_teachbrush);

        // BtnOnClickListener의 객체 생성
        BtnOnClickListener onClickListener = new BtnOnClickListener();
        home_refresh_button.setOnClickListener(onClickListener);
        titlebar_button_instruction.setOnClickListener(onClickListener);
        titlebar_button_teachbrush.setOnClickListener(onClickListener);

        verifyUserInfo = (VerifyUserInfo) getApplicationContext();
        verifyUserInfo.addActivities(this);
        // 사용자 계정 정보 저장
        userInfo = new ArrayList<ListItem>();
        userInfo = verifyUserInfo.getUserData();

        intent_fromMainOrCreateAccountOrEditActivity = getIntent();
        userID = intent_fromMainOrCreateAccountOrEditActivity.getStringExtra(DB_Data.STRING_USER_ID);
        password = intent_fromMainOrCreateAccountOrEditActivity.getStringExtra(DB_Data.STRING_USER_PASSWORD);
        childID = intent_fromMainOrCreateAccountOrEditActivity.getStringExtra(DB_Data.STRING_CHILD_ID);

        if (TextUtils.isEmpty(userID))
            userID = verifyUserInfo.getUserID();
        if (TextUtils.isEmpty(password))
            password = verifyUserInfo.getUserData().get(0).getData(DB_Data.INDEX_USER_PASSWORD);

        if (DB_Data.IS_TEST_VERSION)
            verifyUserInfo.InsertUserItems();
    }

    private void getFragmentInstance() {

        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //childSelectFragment = new ChildSelectFragment();

        childSelectNoneFragment = new ChildSelectNoneFragment();
        fragmentTransaction.add(R.id.fragment_child_select, childSelectNoneFragment);
        fragmentTransaction.commit();
    }

    private void SaveDataInDBAfterRefresh() {
        ArrayList<ListItem> tabletChildInfo = new ArrayList<ListItem>();
        ArrayList<ListItem> tabletRewardInfo = new ArrayList<ListItem>();
        ArrayList<ListItem> tabletDataInfo = new ArrayList<ListItem>();
        ListItem currItem;

        // 블루투스로 수신한 데이터가 없을 경우 return
        if (verifyUserInfo.getDataFromTablet().size() < 1) {
            return;
        }
        // 먼저 데이터를 테이블별로 나눔
        // 만약 비동기 -> 동기화로 변경됐을 경우, 마지막 index에 태블릿 내장 DB에 저장된 child의 id값이 저장되어 있음.
        // 처음부터 부모앱과 동기화 상태로 실행되었다면 마지막 값은 비어있다.
        for (String currData : verifyUserInfo.getDataFromTablet()) {
            if (currData.contains("ChildInfo")) {
                // BluetoothID, Nickname, Character, (비동기)id / (동기)""
                currItem = new ListItem("", "", "", "");
                currItem.setData(ParseData(currData));
                tabletChildInfo.add(currItem);
            }
            // 비동기 -> 동기화로 변경됐을 경우에만 존재하는 정보
            else if (currData.contains("RewardInfo")) {
                // Reward, TotalNum, RewardNum, ChildID (태블릿 내장 DB id)
                currItem = new ListItem("", "", "", "");
                currItem.setData(ParseData(currData));
                tabletRewardInfo.add(currItem);
            } else if (currData.contains("ToothbrushDataInfo")) {
                // Duration, Time, Zone1~9
                currItem = new ListItem("", "", "", "");        // TODO: ListItem Index 추가 필요
                currItem.setData(ParseData(currData));
                tabletDataInfo.add(currItem);
            }
        }
        // 각 테이블별로 데이터 전송
        String insert_childID = "";
        for (ListItem childData : tabletChildInfo) {
            // ChildInfo 테이블에 송신한 후 해당 id값 받음
            // ChildInfo 테이블에 해당 아이 정보가 없을 경우 Insert, 있을 경우 Update
            if (!TextUtils.isEmpty(verifyUserInfo.DoesChildExist(childData)))
//            if (verifyUserInfo.DoesChildExist(childData).length() > 0)
                insert_childID = verifyUserInfo.InsertDataInTable(DB_Data.TABLE_CHILD_INFO, childData);
            else {
                insert_childID = verifyUserInfo.DoesChildExist(childData);
                verifyUserInfo.UpdateDataInTable(DB_Data.TABLE_CHILD_INFO, childData, verifyUserInfo.DoesChildExist(childData));
            }

            // 비동기 -> 동기화로 변경됐을 경우 (tabletRewardInfo에 값이 들어있을 경우) 각 id값별로 테이블에 송신
            if (!TextUtils.isEmpty(tabletRewardInfo.get(0).getData(0))) {
//            if (tabletRewardInfo.get(0).getData(0).length() > 0) {
                for (ListItem rewardData : tabletRewardInfo) {
                    // 한 아이당 보상 정보는 하나씩밖에 없으므로 하나를 찾게되면 break한 후 칫솔 데이터 송신으로 넘어감.
                    if (rewardData.getData(rewardData.length() - 1).equals(childData.getData(childData.length() - 1))) {
                        // 받아온 후, 보상 ListItem에 해당 id값을 추가해 DB에 전송
                        rewardData.changeData(rewardData.length() - 1, insert_childID);
                        verifyUserInfo.InsertDataInTable(DB_Data.TABLE_REWARD_INFO, rewardData);
                        break;
                    }
                }
                // 양치 데이터는 한 아이당 하나 이상씩 가지고 있을 수 있으므로 break 불가.
                for (ListItem toothbrushData : tabletDataInfo) {
                    if (toothbrushData.getData(toothbrushData.length() - 1).equals(childData.getData(childData.length() - 1))) {
                        if (insert_childID == "")
                            insert_childID = verifyUserInfo.InsertDataInTable(DB_Data.TABLE_CHILD_INFO, childData);
                        toothbrushData.changeData(toothbrushData.length() - 1, insert_childID);
                        verifyUserInfo.InsertDataInTable(DB_Data.TABLE_DATA_INFO, toothbrushData);
                    }
                }

                InsertOrUpdateUserData(insert_childID);
            } else {
                insert_childID = verifyUserInfo.InsertDataInTable(DB_Data.TABLE_CHILD_INFO, childData);
                // 현재 아이 정보를 UserInfo 테이블에 추가
                InsertOrUpdateUserData(insert_childID);
                for (ListItem toothbrushData : tabletDataInfo) {
                    if (toothbrushData.getData(toothbrushData.length() - 1).equals(childData.getData(childData.length() - 1))) {
                        if (insert_childID == "")
                            insert_childID = verifyUserInfo.InsertDataInTable(DB_Data.TABLE_CHILD_INFO, childData);
                        toothbrushData.changeData(toothbrushData.length() - 1, insert_childID);
                        verifyUserInfo.InsertDataInTable(DB_Data.TABLE_DATA_INFO, toothbrushData);
                    }
                }
            }
        }
        // 데이터를 서버로 모두 송신한 후, dataFromTable 정보 초기화
        verifyUserInfo.clearDataFromTablet();
    }

    private void InsertOrUpdateUserData(String insert_childID) {
        String[] inputData;
        boolean ifChildExists = false;
        boolean childInfoNone = true;
        for (ListItem userData : verifyUserInfo.getUserData()) {
            if (userData.getData(DB_Data.INDEX_USER_CHILDID).length() > 0)
                childInfoNone = false;
            if (userData.getData(DB_Data.INDEX_USER_CHILDID).equals(insert_childID)) {
                ifChildExists = true;
                childInfoNone = false;
                break;
            }
        }
        // 계정 정보는 존재하지만 아이 정보가 없을 경우 Update
        if (childInfoNone) {
            // 현재 아이 정보를 UserInfo 테이블에 추가
            inputData = new String[2];
            inputData[0] = "";  // childName
            inputData[1] = insert_childID;

            verifyUserInfo.UpdateDataInTable(DB_Data.TABLE_USER_INFO, inputData, userID);
        } else {
            // 현재 아이 정보가 UserInfo 테이블에 존재하지 않을 경우 테이블에 추가
            if (!ifChildExists) {
                inputData = new String[3];
                inputData[0] = verifyUserInfo.getUserData().get(0).getData(DB_Data.INDEX_USER_ID);
                inputData[1] = verifyUserInfo.getUserData().get(0).getData(DB_Data.INDEX_USER_PASSWORD);
                inputData[2] = insert_childID;


                verifyUserInfo.InsertDataInTable(DB_Data.TABLE_USER_INFO_CHILDID, inputData);
            }
        }
    }

    private String[] ParseData(String data) {
        int index = data.indexOf(":");
        data = data.substring(index + 1); // : 다음값부터 다시 저장
        String[] datas = data.split(",");
        return datas;
    }

    private void IfChildExists_ChangeFragment(boolean isButtonPressed) {
        if (!DB_Data.IS_TEST_VERSION) {
            isChildExists = false;

            // 블루투스 모드일 경우, 태블릿에서 수신한 데이터를 서버에 송신
            if (!verifyUserInfo.getIsWIFIMode())
                SaveDataInDBAfterRefresh();
                // WIFI 모드일 경우, 수신한 ChildID로 ChildInfo에서 검색해 해당 정보를 UserInfo에 송신
            else {
                if (verifyUserInfo.getChildIDFromTablet().size() > 0) {
                    for (String currID : verifyUserInfo.getChildIDFromTablet()) {
                        InsertOrUpdateUserData(currID);
                    }
                }
            }

            // 업데이트된 서버에서 사용자 정보 검색
            userInfo = verifyUserInfo.FindUserInfo(userID, password);
            childNum = userInfo.size();
            // 태블릿의 전송방식모드에 상관 없이 서버에서 UserInfo 테이블 검색
            for (ListItem item : userInfo) {
//                if (item.getData(DB_Data.INDEX_USER_CHILDID) != null && item.getData(DB_Data.INDEX_USER_CHILDID) != "") {
                if (item.getData(DB_Data.INDEX_USER_CHILDID).length() > 0) {
                    if (!isChildExists) {
                        // 등록된 아이 정보가 있을 경우 boolean 값을 true로 바꿔줌
                        isChildExists = true;
                        break;
                    }
                }
            }

            if (isChildExists) {
                // UserInfo 테이블에 존재하는 ChildID로 ChildInfo 테이블에 정보 검색 후 ConnectMode 확인
                verifyUserInfo.GetDataFromTable(DB_Data.TABLE_CHILD_INFO, userInfo.get(0).getData(DB_Data.INDEX_USER_CHILDID));
                if (!verifyUserInfo.getChildData().getData(DB_Data.INDEX_CHILD_CONNECTMODE).isEmpty()) {
                    // WIFI 모드일 경우
                    if (verifyUserInfo.getChildData().getData(DB_Data.INDEX_CHILD_CONNECTMODE).equals(getString(R.string.connectmode_wifi))) {
                        verifyUserInfo.setIsWIFIMode(true);
                        ChangeFragment();
                    }
                    // 블루투스 모드일 경우
                    else if (verifyUserInfo.getChildData().getData(DB_Data.INDEX_CHILD_CONNECTMODE).equals(getString(R.string.connectmode_bluetooth))) {
                        verifyUserInfo.setIsWIFIMode(false);

                        Intent serverIntent = new Intent(this, DeviceListActivity.class);
                        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
                    }
                }

                // 블루투스 모드일 경우 수신한 데이터를 DB에 저장
                if (!verifyUserInfo.getIsWIFIMode()) {
                    SaveDataInDBAfterRefresh();
                }

            }
            // 태블릿과 처음 블루투스 연결하는 경우, 부모앱쪽에서 블루투스 기기를 검색해 태블릿과 연결하고 태블릿에서 정보 수신함
            else {
                if (isButtonPressed) {
                    Toast.makeText(this, "생성된 아이의 정보가 없습니다.", Toast.LENGTH_SHORT).show();

                    Intent serverIntent = new Intent(this, DeviceListActivity.class);
                    startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
                }
            }
        } else {
            // Test용
            ChangeFragment();

        }

    }

    private void ChangeFragment() {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        childSelectFragment = new ChildSelectFragment();
        // 편집화면에서 이동된 경우, 방금 편집된 아이의 정보가 메인에 나타나도록 함
        if(!TextUtils.isEmpty(childID)){
            Bundle bundle = new Bundle();
            bundle.putString(DB_Data.STRING_CHILD_NAME, intent_fromMainOrCreateAccountOrEditActivity.getStringExtra(DB_Data.STRING_CHILD_NAME));
            bundle.putString(DB_Data.STRING_CHILD_ID, intent_fromMainOrCreateAccountOrEditActivity.getStringExtra(DB_Data.STRING_CHILD_ID));
            bundle.putString(DB_Data.STRING_CHILD_BACKGROUNDPHOTO, intent_fromMainOrCreateAccountOrEditActivity.getStringExtra(DB_Data.STRING_CHILD_BACKGROUNDPHOTO));
            bundle.putString(DB_Data.STRING_CHILD_PHOTO, intent_fromMainOrCreateAccountOrEditActivity.getStringExtra(DB_Data.STRING_CHILD_PHOTO));
            childSelectFragment.setArguments(bundle);
        }
        fragmentTransaction.replace(R.id.fragment_child_select, childSelectFragment, DB_Data.STRING_FRAGMENT);
        fragmentTransaction.commit();
    }

    private void toastMessage(String Message) {
        Toast.makeText(this, Message, Toast.LENGTH_SHORT).show();
    }

    class BtnOnClickListener implements Button.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.home_refresh_button:
                    IfChildExists_ChangeFragment(true);
                    break;
                case R.id.titlebar_button_instruction:
                    // 조작 방법 선택 화면으로 이동
                    Intent intentToInstruction = new Intent(HomeActivity.this, InstructionActivity.class);
                    intentToInstruction.putExtra(DB_Data.STRING_INSTRUCTION, DB_Data.STRING_INSTRUCTION_MOM);
                    startActivity(intentToInstruction);
                    break;
                case R.id.titlebar_button_teachbrush:
                    // 올바른 양치질 습관과 관련된 정보를 제공하는 화면으로 이동
                    Intent intentToToothbrushInstruction = new Intent(HomeActivity.this, InstructionActivity.class);
                    intentToToothbrushInstruction.putExtra(DB_Data.STRING_INSTRUCTION, DB_Data.STRING_INSTRUCTION_TOOTHBRUSH);
                    startActivity(intentToToothbrushInstruction);
                    break;
            }
        }
    }


    /* Bluetooth */

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    /**
     * Name of the connected device
     */
    private String connectedDeviceName = null;

    /**
     * Local Bluetooth adapter
     */
    private BluetoothAdapter bluetoothAdapter = null;

    /**
     * Member object for the chat services
     */
    private BluetoothConnectService bluetoothConnectService = null;

    @Override
    public void onStart() {
        super.onStart();
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else if (bluetoothConnectService == null) {
            setupConnect();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (bluetoothConnectService != null) {
            bluetoothConnectService.stop();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (bluetoothConnectService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (bluetoothConnectService.getState() == BluetoothConnectService.STATE_NONE) {
                // Start the Bluetooth chat services
                bluetoothConnectService.start();
            }
        }
    }

    /**
     * Set up the UI and background operations for chat.
     */
    private void setupConnect() {
        // Initialize the BluetoothChatService to perform bluetooth connections
        bluetoothConnectService = new BluetoothConnectService(this, mHandler);
    }


    /**
     * Makes this device discoverable for 300 seconds (5 minutes).
     */
    private void ensureDiscoverable() {
        if (bluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    /**
     * Sends a message.
     *
     * @param message A string of text to send.
     */
    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (bluetoothConnectService.getState() != BluetoothConnectService.STATE_CONNECTED) {
            toastMessage(getString(R.string.title_not_connected));
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            bluetoothConnectService.write(send);
        }
    }


    /**
     * The Handler that gets information back from the BluetoothConnectService
     */
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            FragmentActivity activity = HomeActivity.this;
            switch (msg.what) {
                case DB_Data.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothConnectService.STATE_CONNECTED:
                            toastMessage(getString(R.string.title_connected_to) + connectedDeviceName);
                            break;
                        case BluetoothConnectService.STATE_CONNECTING:
                            toastMessage(getString(R.string.title_connecting));
                            break;
                        case BluetoothConnectService.STATE_LISTEN:
                        case BluetoothConnectService.STATE_NONE:
//                            toastMessage(getString(R.string.title_not_connected));
//                            break;
                    }
                    break;
                case DB_Data.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    break;
                case DB_Data.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
//                    messageFromTablet = readMessage;
                    // 태블릿의 연결 방식 먼저 확인. WIFI 혹은 블루투스
//                    if (readMessage.equals("WIFI")){
//                        verifyUserInfo.setIsWIFIMode(true);
//                        break;
//                    }
//                    else if (readMessage.equals("Bluetooth")){
//                        verifyUserInfo.setIsWIFIMode(false);
//                        break;
//                    }

                    // WIFI 모드일 경우
                    // 키즈앱으로부터 필요한 정보만 받아옴.
                    // ChildInfo : id
                    if (verifyUserInfo.getIsWIFIMode()) {
//                        verifyUserInfo.setChildID(readMessage);
                        verifyUserInfo.addChildIDFromTablet(readMessage);
                        toastMessage(connectedDeviceName + ":  " + verifyUserInfo.getChildID());
                    }

                    // 블루투스 모드일 경우
                    // 키즈앱으로부터 UserInfo를 제외한 모든 정보를 받아옴.

                    // 키즈앱이 비동기로 정보를 저장하고 있었다면 아래의 데이터를 받아옴.
                    // ChildInfo : BluetoothID, Nickname, Character, id (태블릿 내장 DB에 저장된 child의 id(PK)값)
                    // --> 태블릿에서 받아온 정보가 두 아이 이상의 정보일 경우, 이를 구분하기 위해 받아옴. AWS DB에선 AI로 id값이 들어가도록 한다. 즉 태블릿에서 수신한 childID값과 AWS DB 내의 childID, 두 id값이 다를 수도 있음.
                    // RewardInfo : Reward, TotalNum, RewardNum, ChildID
                    // BluetoothDataInfo : ChildID, Duration, Time, Zone1~9

                    // 부모앱과 동기화 상태로 실행되었다면 아래의 데이터를 받아옴.
                    // ChildInfo : BluetoothID, Nickname, Character, id
                    // BluetoothDataInfo : Duration, Time, Zone1~9, ChildID
                    else if (!verifyUserInfo.getIsWIFIMode()) {
                        verifyUserInfo.addDataFromTablet(readMessage);
                    }
                    toastMessage(connectedDeviceName + ":  " + readMessage);
                    break;
                case DB_Data.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    connectedDeviceName = msg.getData().getString(DB_Data.DEVICE_NAME);
                    if (null != activity) {
                        toastMessage(getString(R.string.title_connected_to) + connectedDeviceName);
                    }
                    break;
                case DB_Data.MESSAGE_TOAST:
                    if (null != activity) {
                        toastMessage(msg.getData().getString(DB_Data.TOAST));
                    }
                    break;
            }
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, true);
//                    Log.w(TAG, "connectDevice");
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, false);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setupConnect();
                } else {
                    // User did not enable Bluetooth or an error occurred
//                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(this, R.string.bt_not_enabled_leaving,
                            Toast.LENGTH_SHORT).show();
                    this.finish();
                }
        }
    }

    /**
     * Establish connection with other device
     *
     * @param data   An {@link Intent} with {@link DeviceListActivity#EXTRA_DEVICE_ADDRESS} extra.
     * @param secure Socket Security type - Secure (true) , Insecure (false)
     */
    private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras()
                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        bluetoothConnectService.connect(device, secure);
    }


}
