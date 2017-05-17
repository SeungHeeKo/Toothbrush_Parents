package com.tektonspace.toothbrush_parents.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.tektonspace.toothbrush_parents.utils.PopupDialog;
import com.tektonspace.toothbrush_parents.R;
import com.tektonspace.toothbrush_parents.constants.DB_Data;


/**
 * Created by SeungHee on 2017-04-13.
 * 아이 선택 화면 (생성된 아이 정보가 없는 경우)
 * A simple {@link Fragment} subclass.
 */
public class ChildSelectNoneFragment extends Fragment {
    Button wifi_button, bluetooth_button;

    // 팝업창 객체
    PopupDialog popupDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_child_select_none, null);

        InstantiateInstance(view);

        // Inflate the layout for this fragment
        return view;
    }

    private void InstantiateInstance(View view){
        wifi_button = (Button) view.findViewById(R.id.wifi_button);
        bluetooth_button = (Button) view.findViewById(R.id.bluetooth_button);

        // 버튼 클릭 이벤트 객체 생성
        BtnOnClickListener onClickListener = new BtnOnClickListener();
        wifi_button.setOnClickListener(onClickListener);
        bluetooth_button.setOnClickListener(onClickListener);
    }
    class BtnOnClickListener implements Button.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.wifi_button:
                    ShowDialog(DB_Data.POPUP_INSTRUCTION_WIFI);
                    break;
                case R.id.bluetooth_button:
                    ShowDialog(DB_Data.POPUP_INSTRUCTION_BLUETOOTH);
                    break;
            }
        }
    }

    private void ShowDialog(int buttonMessage) {
        popupDialog = new PopupDialog(getActivity(), buttonMessage);
        popupDialog.show();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
