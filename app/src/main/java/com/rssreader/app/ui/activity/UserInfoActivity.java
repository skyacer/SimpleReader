package com.rssreader.app.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.rssreader.app.ui.R;
import com.rssreader.app.ui.base.BaseActionBarActivity;
import com.rssreader.app.ui.presenter.UserInfoPresenter;

/**
 * Created by LuoChangAn on 16/4/26.
 */
public class UserInfoActivity extends BaseActionBarActivity<UserInfoPresenter>{
    @Override
    protected void initPresenter() {
        presenter = new UserInfoPresenter(this);
    }

    public static void startActivity(Activity activity){
        Intent intent = new Intent(activity,UserInfoActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        setTitle(R.string.personal_info_title);
        setRealContentView(R.layout.activity_personal_info);

    }
}
