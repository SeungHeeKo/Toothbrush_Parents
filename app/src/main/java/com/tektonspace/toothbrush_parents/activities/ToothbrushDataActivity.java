package com.tektonspace.toothbrush_parents.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tektonspace.toothbrush_parents.adapter.ListItem;
import com.tektonspace.toothbrush_parents.utils.PopupDialog;
import com.tektonspace.toothbrush_parents.R;
import com.tektonspace.toothbrush_parents.adapter.ToothbrushDataListViewAdapter;
import com.tektonspace.toothbrush_parents.constants.DB_Data;
import com.tektonspace.toothbrush_parents.db.VerifyUserInfo;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ToothbrushDataActivity extends AppCompatActivity {
    public static ToothbrushDataActivity toothbrushDataActivity;
    private static final int MORNING_MIN_HOUR = 0;
    private static final int MORNING_MIN_MINUTE = 1;
    private static final int MORNING_MAX_HOUR = 2;
    private static final int MORNING_MAX_MINUTE = 3;
    private static final int AFTERNOON_MIN_HOUR = 4;
    private static final int AFTERNOON_MIN_MINUTE = 5;
    private static final int AFTERNOON_MAX_HOUR = 6;
    private static final int AFTERNOON_MAX_MINUTE = 7;
    private static final int EVENING_MIN_HOUR = 8;
    private static final int EVENING_MIN_MINUTE = 9;
    private static final int EVENING_MAX_HOUR = 10;
    private static final int EVENING_MAX_MINUTE = 11;

    private static final int MORNING_HOUR = 0;
    private static final int MORNING_MINUTE = 1;
    private static final int AFTERNOON_HOUR = 2;
    private static final int AFTERNOON_MINUTE = 3;
    private static final int EVENING_HOUR = 4;
    private static final int EVENING_MINUTE = 5;

    private static final int CASE_LESS_THAN_ZERO = 0;
    private static final int CASE_MORE_THAN_LASTDAY = 1;
    private static final int CASE_FEBRUARY = 2;

    GregorianCalendar calendar;
    public int year, month, day, week;
    int firstDayOfWeek, lastDayOfWeek;
    int[] currOneWeekYear = new int[7];
    int[] currOneWeekMonth = new int[7];
    int[] currOneWeekDay = new int[7];
    int morningResult, afternoonResult, eveningResult;
    int[] timeCondition = new int[12];
    int[] settingTime = new int[6];
    String[] currOneDate = new String[7];

    boolean morningDone = false, afternoonDone = false, eveningDone = false;
    RelativeLayout toothbrush_layout;
    ImageView toothbrush_childPhoto_imageView;
    TextView toothbrush_childName_textView;
    Button toothbrush_setting_button, toothbrush_calendar_left_button, toothbrush_calendar_button, toothbrush_calendar_right_button, titlebar_button_back, titlebar_button_home;
    // 타이틀바 버튼
    Button titlebar_button_instruction, titlebar_button_teachbrush;

    ListView listView;
    ToothbrushDataListViewAdapter toothbrushDataListViewAdapter;

    VerifyUserInfo verifyUserInfo;
    PopupDialog popupDialog;

    ArrayList<ListItem> dataList;
    ArrayList<ListItem> dataList_date;

    int[] dayResources = {
            R.string.string_sunday,
            R.string.string_monday,
            R.string.string_tuesday,
            R.string.string_wednesday,
            R.string.string_thursday,
            R.string.string_friday,
            R.string.string_saturday
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toothbrush_data);

        InitiateInstance();
    }

    private void InitiateInstance() {
        toothbrushDataActivity = this;
        toothbrush_layout = (RelativeLayout) findViewById(R.id.toothbrush_layout);
        toothbrush_childPhoto_imageView = (ImageView) findViewById(R.id.toothbrush_childPhoto_imageView);
        toothbrush_childName_textView = (TextView) findViewById(R.id.toothbrush_childName_textView);
        toothbrush_calendar_left_button = (Button) findViewById(R.id.toothbrush_calendar_left_button);
        toothbrush_calendar_button = (Button) findViewById(R.id.toothbrush_calendar_button);
        toothbrush_calendar_right_button = (Button) findViewById(R.id.toothbrush_calendar_right_button);
        toothbrush_setting_button = (Button) findViewById(R.id.toothbrush_setting_button);
        titlebar_button_back = (Button) findViewById(R.id.titlebar_button_back);
        titlebar_button_home = (Button) findViewById(R.id.titlebar_button_home);
        titlebar_button_instruction = (Button) findViewById(R.id.titlebar_button_instruction);
        titlebar_button_teachbrush = (Button) findViewById(R.id.titlebar_button_teachbrush);
        listView = (ListView) findViewById(R.id.toothbrush_data_listView);
        toothbrushDataListViewAdapter = new ToothbrushDataListViewAdapter();

        Typeface font_regular = Typeface.createFromAsset(getAssets(), "yanolza_regular.ttf");
        Typeface font_bold = Typeface.createFromAsset(getAssets(), "yanolza_bold.ttf");
        toothbrush_childName_textView.setTypeface(font_regular);

        // listview에 adapter 연결
        listView.setAdapter(toothbrushDataListViewAdapter);
        listView.setClickable(false);
        toothbrushDataListViewAdapter.notifyDataSetChanged();

        calendar = new GregorianCalendar();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        week = calendar.get(Calendar.WEEK_OF_MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        BtnOnClickListener onClickListener = new BtnOnClickListener();
        toothbrush_calendar_left_button.setOnClickListener(onClickListener);
        toothbrush_calendar_button.setOnClickListener(onClickListener);
        toothbrush_calendar_right_button.setOnClickListener(onClickListener);
        toothbrush_setting_button.setOnClickListener(onClickListener);
        titlebar_button_back.setOnClickListener(onClickListener);
        titlebar_button_home.setOnClickListener(onClickListener);
        titlebar_button_instruction.setOnClickListener(onClickListener);
        titlebar_button_teachbrush.setOnClickListener(onClickListener);

        verifyUserInfo = (VerifyUserInfo) getApplicationContext();
        verifyUserInfo.addActivities(this);

        // 아이 사진, 이름 정보가 있을 경우 화면에 출력
        verifyUserInfo.SetImageViewPhoto(this, toothbrush_childPhoto_imageView);
        verifyUserInfo.SetTextViewName(toothbrush_childName_textView);

        // 현재 날짜로 캘린터 수정
        SetMonthWeek(year, month, week);

        // 양치 데이터 저장할 ArrayList
        dataList = new ArrayList<ListItem>();

//        currOneWeekDay = new int[7];
//        timeCondition = new int[12];
        morningResult = R.drawable.data_morning_off1;
        afternoonResult = R.drawable.data_afternoon_off1;
        eveningResult = R.drawable.data_evening_off1;

    }

    class BtnOnClickListener implements Button.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.toothbrush_calendar_left_button:
                    CalculateWeek(-1);
                    break;
                case R.id.toothbrush_calendar_button:
                    new DatePickerDialog(ToothbrushDataActivity.this, dateSetListener, year, month, day).show();
                    break;
                case R.id.toothbrush_calendar_right_button:
                    CalculateWeek(1);
                    break;
                case R.id.toothbrush_setting_button:
                    ShowDialog(DB_Data.POPUP_DATA_SETTING);
                    break;
                case R.id.titlebar_button_home:
                    Intent intentToHome = new Intent(ToothbrushDataActivity.this, HomeActivity.class);
                    verifyUserInfo.clearActivities();
                    startActivity(intentToHome);
                    finish();
                    break;
                case R.id.titlebar_button_back:
                    finish();
                    break;
                case R.id.titlebar_button_instruction:
                    // 조작 방법 선택 화면으로 이동
                    Intent intentToInstruction = new Intent(ToothbrushDataActivity.this, InstructionActivity.class);
                    intentToInstruction.putExtra(DB_Data.STRING_INSTRUCTION, DB_Data.STRING_INSTRUCTION_MOM);
                    startActivity(intentToInstruction);
                    break;
                case R.id.titlebar_button_teachbrush:
                    // 올바른 양치질 습관과 관련된 정보를 제공하는 화면으로 이동
                    Intent intentToToothbrushInstruction = new Intent(ToothbrushDataActivity.this, InstructionActivity.class);
                    intentToToothbrushInstruction.putExtra(DB_Data.STRING_INSTRUCTION, DB_Data.STRING_INSTRUCTION_TOOTHBRUSH);
                    startActivity(intentToToothbrushInstruction);
                    break;
            }
        }
    }

    private void ShowDialog(int message) {
        popupDialog = new PopupDialog(this, message, cancelButtonListener);
        popupDialog.show();
    }

    private View.OnClickListener cancelButtonListener = new View.OnClickListener() {
        public void onClick(View v) {
            popupDialog.dismiss();
            Toast.makeText(getApplicationContext(), "cancel", Toast.LENGTH_SHORT).show();
            ShowToothbrushData(year, month, day);
        }
    };

    private void CalculateWeek(int num) {
        calendar.set(year, month, day);
        calendar.add(Calendar.WEEK_OF_MONTH, num);

        month = calendar.get(Calendar.MONTH);
        week = calendar.get(Calendar.WEEK_OF_MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        SetMonthWeek(year, month, week);
        calendar.set(year, month, day);
    }

    private void SetMonthWeek(int year, int month, int week) {
        String currWeek = String.format("%d월 %d주차", month + 1, week);
        toothbrush_calendar_button.setText(currWeek);

        // Fragment 변경
        ShowToothbrushData(year, month, week);
    }

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int newYear, int newMonth, int dayOfMonth) {
            // TODO Auto-generated method stub
            year = newYear;
            month = newMonth;
            day = dayOfMonth;
            calendar.set(newYear, newMonth, dayOfMonth);
            week = calendar.get(Calendar.WEEK_OF_MONTH);
            SetMonthWeek(year, month, week);
        }
    };

    public void ShowToothbrushData(int year, int month, int week) {
        if (!DB_Data.IS_TEST_VERSION) {
            toothbrushDataListViewAdapter.clearAll();
            // 한 주 정보 가져옴
            GetOneWeek(year, month, day);
            dataList = verifyUserInfo.GetDataFromTable(DB_Data.TABLE_DATA_INFO, verifyUserInfo.getChildID());

            if (dataList.size() < 1) {
//                Toast.makeText(this, getString(R.string.error_fail_to_access_DB), Toast.LENGTH_SHORT).show();
                for (int i = 0; i < 7; i++)
                    toothbrushDataListViewAdapter.addItem(getDrawable(R.drawable.data_morning_off1), getDrawable(R.drawable.data_afternoon_off1), getDrawable(R.drawable.data_evening_off1), currOneDate[i]);
                toothbrushDataListViewAdapter.notifyDataSetChanged();
                listView.setAdapter(toothbrushDataListViewAdapter);
                return;
            }

            GetTimeCondition();
            int currData = 0;
            // 일주일단위로 검색
            for (int i = 0; i < 7; i++) {
                // 이미지 초기화
                morningResult = R.drawable.data_morning_off1;
                afternoonResult = R.drawable.data_afternoon_off1;
                eveningResult = R.drawable.data_evening_off1;
                morningDone = false;
                afternoonDone = false;
                eveningDone = false;
                currData = 0;

                String currDateString = String.valueOf(currOneWeekYear[i]) + String.valueOf(currOneWeekMonth[i] + 1);
                String currDateStringDB = String.valueOf(currOneWeekYear[i]) + "_" + String.valueOf(currOneWeekMonth[i] + 1) + "_";
                // 한 자리수 day일 경우
                if (String.valueOf(currOneWeekDay[i]).length() == 1) {
                    currDateString += "0";
                    currDateString += String.valueOf(currOneWeekDay[i]);
                    currDateStringDB += "0";
                    currDateStringDB += String.valueOf(currOneWeekDay[i]);
                } else {
                    currDateString += String.valueOf(currOneWeekDay[i]);
                    currDateStringDB += String.valueOf(currOneWeekDay[i]);
                }
                // 양치 데이터 저장할 ArrayList
                dataList_date = new ArrayList<ListItem>();
                dataList_date = verifyUserInfo.GetDataFromTable(DB_Data.TABLE_DATA_INFO, verifyUserInfo.getChildID(), currDateStringDB);

                if (dataList_date.size() < 1) {
//                Toast.makeText(this, getString(R.string.error_fail_to_access_DB), Toast.LENGTH_SHORT).show();
                    for (int j = 0; j < 7; j++)
                        toothbrushDataListViewAdapter.addItem(getDrawable(R.drawable.data_morning_off1), getDrawable(R.drawable.data_afternoon_off1), getDrawable(R.drawable.data_evening_off1), currOneDate[j]);
                    toothbrushDataListViewAdapter.notifyDataSetChanged();
                    listView.setAdapter(toothbrushDataListViewAdapter);
                    return;
                }

                for (ListItem data : dataList_date) {
                    if (data.getData(DB_Data.INDEX_DATA_DATE).equals(currDateStringDB)) {
                        if (!data.getData(DB_Data.INDEX_DATA_TIME).isEmpty()) {
                            String[] time = data.getData(DB_Data.INDEX_DATA_TIME).split(":");
                            int timeHour = 0, timeMinute = 0;
                            timeHour = Integer.parseInt(time[0]);
                            timeMinute = Integer.parseInt(time[1]);

                            currData = CheckToothbrushTime(timeHour, timeMinute, dataList_date.size());

                            switch (currData) {
                                case 0:
                                    if (IsTimeOnOff(currDateString, timeHour, timeMinute, MORNING_MIN_HOUR))
                                        morningResult = R.drawable.data_morning_on;
                                    else
                                        morningResult = R.drawable.data_morning_off1;

                                    currData++;
                                    if (!morningDone)
                                        morningDone = true;
                                    break;
                                case 1:
                                    if (IsTimeOnOff(currDateString, timeHour, timeMinute, AFTERNOON_MIN_HOUR))
                                        afternoonResult = R.drawable.data_afternoon_on;
                                    else
                                        afternoonResult = R.drawable.data_afternoon_off1;
                                    currData++;
                                    if (!afternoonDone)
                                        afternoonDone = true;
                                    break;
                                case 2:
                                    if (IsTimeOnOff(currDateString, timeHour, timeMinute, EVENING_MIN_HOUR))
                                        eveningResult = R.drawable.data_evening_on;
                                    else
                                        eveningResult = R.drawable.data_evening_off1;
                                    if (!eveningDone)
                                        eveningDone = true;
                                    break;
                            }

                        }


                    }

                }

                toothbrushDataListViewAdapter.addItem(getDrawable(morningResult), getDrawable(afternoonResult), getDrawable(eveningResult), currOneDate[i]);
                currData = 0;
            }

            toothbrushDataListViewAdapter.notifyDataSetChanged();
            listView.setAdapter(toothbrushDataListViewAdapter);

        } else {
            for (int i = 0; i < 7; i++)
                toothbrushDataListViewAdapter.addItem(getDrawable(R.drawable.data_morning_off1), getDrawable(R.drawable.data_afternoon_off1), getDrawable(R.drawable.data_evening_off1), currOneDate[i]);
            toothbrushDataListViewAdapter.notifyDataSetChanged();
            listView.setAdapter(toothbrushDataListViewAdapter);
        }
    }

    private int CheckToothbrushTime(int timeHour, int timeMinute, int arraySize) {
        int retval = -1;

        long childData_millisecond = SetCalendarTime(timeHour, timeMinute);
        long morning_millisecond = SetCalendarTime(settingTime[MORNING_HOUR], settingTime[MORNING_MINUTE]);
        long afternoon_millisecond = SetCalendarTime(settingTime[AFTERNOON_HOUR], settingTime[AFTERNOON_MINUTE]);
        long evening_millisecond = SetCalendarTime(settingTime[EVENING_HOUR], settingTime[EVENING_MINUTE]);

        long morningGap = Math.abs(childData_millisecond - morning_millisecond);
        long afternoonGap = Math.abs(childData_millisecond - afternoon_millisecond);
        long eveningGap = Math.abs(childData_millisecond - evening_millisecond);

//        if(arraySize >= 3){
//            // 아침, 점심, 저녁
//            if(Math.min(morningGap, afternoonGap) == morningGap){
//                if(Math.min(morningGap, eveningGap) == morningGap){
//                    // 아침
//                    retval = 0;
//                }
//                // 저녁
//                else
//                    retval = 2;
//            }
//            else{
//                if(Math.min(afternoonGap, eveningGap) == afternoonGap){
//                    // 점심
//                    retval = 1;
//                }
//                // 저녁
//                else
//                    retval = 2;
//            }
//        }
//        else if(arraySize == 2){
//            // 아침, 점심, 저녁 중 한번 안 함
//        }
//        else if(arraySize == 1){
//            // 하루에 양치 한 번만
//            // 설정된 아침, 점심, 저녁 중 제일 오차값이 적은
//        }

        if (Math.min(morningGap, afternoonGap) == morningGap) {
            if (Math.min(morningGap, eveningGap) == morningGap) {
                // 아침
                retval = 0;
            }
            // 저녁
            else
                retval = 2;
        } else {
            if (Math.min(afternoonGap, eveningGap) == afternoonGap) {
                // 점심
                retval = 1;
            }
            // 저녁
            else
                retval = 2;
        }
        return retval;
    }

    // 4월 30일 5월 1일 (4월 31일로 저장됨)
    private void GetOneWeek(int year, int month, int week) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        String dayOfWeekString = "";
        switch (dayOfWeek) {
            case 1:
                firstDayOfWeek = day;
                lastDayOfWeek = day + 6;
                dayOfWeekString = "일";
                break;
            case 2:
                firstDayOfWeek = day - 1;
                lastDayOfWeek = day + 5;
                dayOfWeekString = "월";
                break;
            case 3:
                firstDayOfWeek = day - 2;
                lastDayOfWeek = day + 4;
                dayOfWeekString = "화";
                break;
            case 4:
                firstDayOfWeek = day - 3;
                lastDayOfWeek = day + 3;
                dayOfWeekString = "수";
                break;
            case 5:
                firstDayOfWeek = day - 4;
                lastDayOfWeek = day + 2;
                dayOfWeekString = "목";
                break;
            case 6:
                firstDayOfWeek = day - 5;
                lastDayOfWeek = day + 1;
                dayOfWeekString = "금";
                break;
            case 7:
                firstDayOfWeek = day - 6;
                lastDayOfWeek = day;
                dayOfWeekString = "토";
                break;
        }

        // firstDayOfWeek = 한 주의 일요일, lastDayOfWeek = 한 주의 토요일
        for (int i = 0; i < 7; i++) {
            currOneWeekDay[i] = firstDayOfWeek + i;
            currOneWeekMonth[i] = month;
            currOneWeekYear[i] = year;

            if (month == 2 && currOneWeekDay[i] > 28) {
                ModifyOneWeek(currOneWeekDay[i], i, CASE_FEBRUARY);
            }
            else if (currOneWeekDay[i] < 1)
                ModifyOneWeek(currOneWeekDay[i], i, CASE_LESS_THAN_ZERO);
            else if (currOneWeekDay[i] > 30) {
                ModifyOneWeek(currOneWeekDay[i], i, CASE_MORE_THAN_LASTDAY);
            }

            currOneDate[i] = String.valueOf(currOneWeekMonth[i] + 1) + "월 " + String.valueOf(currOneWeekDay[i]) + "일 " +  getString(dayResources[i]);
        }
    }

    private void ModifyOneWeek(int oneWeekDay, int index, int caseIndex) {
        Calendar calendar = Calendar.getInstance();
        switch (caseIndex) {
            case CASE_LESS_THAN_ZERO:
                calendar.set(year, month, 1);
                // 한 주에 두 가지의 월이 걸쳐있을 경우 (ex: 4월30일(일) 5월 1일(월))
                calendar.add(Calendar.DAY_OF_MONTH, -(Math.abs(oneWeekDay) + 1));
                break;
            case CASE_MORE_THAN_LASTDAY:
                calendar.set(year, month, 30);
                // 30일 다음이 31일인지 1일인지 확인
                calendar.add(Calendar.DAY_OF_MONTH, oneWeekDay - 30);
                break;
            case CASE_FEBRUARY:
                calendar.set(year, month, 28);
                // 2월 28일 이후가 29일인지 3월 1일인지 확인
                calendar.add(Calendar.DAY_OF_MONTH, oneWeekDay - 28);
                break;
        }

        currOneWeekMonth[index] = calendar.get(Calendar.MONTH);
        currOneWeekDay[index] = calendar.get(Calendar.DAY_OF_MONTH);
        // 새로 입력한 달이 12월달일 경우, year-1
        if (currOneWeekMonth[index] == 12)
            currOneWeekYear[index] = calendar.get(Calendar.YEAR);
    }

    // String -> int
    private void GetTimeCondition() {
        Date date = new Date();
        String[] currCondition, currTime;
        int currHour, currMinute;
        // 아침
        currCondition = verifyUserInfo.getDataMorning().split(" ");
        currTime = currCondition[1].split(":");

        currHour = Integer.parseInt(currTime[0]);
        currMinute = Integer.parseInt(currTime[1]);
        if (currCondition[0].equals("오전")) {

        } else {
            currHour += 12;
        }
        CalculateMinMax(currHour, currMinute, MORNING_MIN_HOUR);
        settingTime[MORNING_HOUR] = currHour;
        settingTime[MORNING_MINUTE] = currMinute;

        // 점심
        currCondition = verifyUserInfo.getDataAfternoon().split(" ");
        currTime = currCondition[1].split(":");
        currHour = Integer.parseInt(currTime[0]);
        currMinute = Integer.parseInt(currTime[1]);
        if (currCondition[0].equals("오전")) {

        } else {
            if (currHour != 12)
                currHour += 12;
        }
        CalculateMinMax(currHour, currMinute, AFTERNOON_MIN_HOUR);
        settingTime[AFTERNOON_HOUR] = currHour;
        settingTime[AFTERNOON_MINUTE] = currMinute;

        // 저녁
        currCondition = verifyUserInfo.getDataEvening().split(" ");
        currTime = currCondition[1].split(":");
        currHour = Integer.parseInt(currTime[0]);
        currMinute = Integer.parseInt(currTime[1]);
        if (currCondition[0].equals("오전")) {

        } else {
            currHour += 12;
        }
        CalculateMinMax(currHour, currMinute, EVENING_MIN_HOUR);
        settingTime[EVENING_HOUR] = currHour;
        settingTime[EVENING_MINUTE] = currMinute;
    }

    private long SetCalendarTime(int timeHour, int timeMinute) {
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.set(Calendar.HOUR_OF_DAY, timeHour);
        gregorianCalendar.set(Calendar.MINUTE, timeMinute);
        gregorianCalendar.set(Calendar.SECOND, 0);
        gregorianCalendar.set(Calendar.MILLISECOND, 0);
        Date gregorianCalendar_date = gregorianCalendar.getTime();

        return gregorianCalendar_date.getTime();

    }

    private boolean IsTimeOnOff(String currDateString, int timeHour, int timeMinute, int index) {
        boolean retval = false;

        int currIndex = index;

        // 아침
        long childData_millisecond = SetCalendarTime(timeHour, timeMinute);
        long minData_millisecond = SetCalendarTime(timeCondition[index++], timeCondition[index++]);
        long maxData_millisecond = SetCalendarTime(timeCondition[index++], timeCondition[index++]);

        if (timeHour >= timeCondition[currIndex] && timeHour <= timeCondition[currIndex + 2]) {
            if (childData_millisecond >= minData_millisecond && childData_millisecond <= maxData_millisecond)
                return retval = true;
        }

        return retval;
    }

    private void CalculateMinMax(int hour, int minute, int index) {
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.add(Calendar.MINUTE, -Integer.parseInt(verifyUserInfo.getDataLimit()));
        timeCondition[index++] = calendar.get(Calendar.HOUR_OF_DAY);
        timeCondition[index++] = calendar.get(Calendar.MINUTE);

        calendar.add(Calendar.MINUTE, +Integer.parseInt(verifyUserInfo.getDataLimit()) * 2);
        timeCondition[index++] = calendar.get(Calendar.HOUR_OF_DAY);
        timeCondition[index] = calendar.get(Calendar.MINUTE);
    }
}
