package com.tektonspace.toothbrush_parents.utils;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by SeungHee on 2017-04-26.
 */

public class BaseActivity extends AppCompatActivity {
//    private ActivityBaseBinding binding;

    private BackPressCloseHandler backPressCloseHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        backPressCloseHandler = new BackPressCloseHandler(this);

//        binding = DataBindingUtil.setContentView(this, R.layout.activity_base);
//        binding.setModel(new BaseActivityViewModel());
    }

//    protected <T extends ViewDataBinding> T putContentView(@LayoutRes int resId) {
//        return DataBindingUtil.inflate(getLayoutInflater(), resId, binding.activityContainer, true);
//    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        backPressCloseHandler.onBackPressed();
    }
}
