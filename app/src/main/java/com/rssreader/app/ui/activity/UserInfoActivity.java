package com.rssreader.app.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.rssreader.app.ui.R;
import com.rssreader.app.ui.base.BaseActionBarActivity;
import com.rssreader.app.ui.presenter.UserInfoPresenter;

/**
 * Created by LuoChangAn on 16/4/26.
 */
public class UserInfoActivity extends BaseActionBarActivity<UserInfoPresenter>{
    TextView mUidTv;
    TextView mNickNameTv;
    RadioGroup mSexRg;
    TextView mDistrictTv;

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

        mUidTv = (TextView) findViewById(R.id.tv_edit_detail_right_id);
        mNickNameTv = (TextView) findViewById(R.id.tv_edit_detail_right_nickname);
        mSexRg = (RadioGroup) findViewById(R.id.personal_info_sex_rg);
        mDistrictTv = (TextView) findViewById(R.id.tv_edit_detail_right_area);

        findViewById(R.id.btn_login_douban).setOnClickListener(presenter);
        findViewById(R.id.btn_login_tencentwb).setOnClickListener(presenter);
        findViewById(R.id.btn_login_weibo).setOnClickListener(presenter);
    }

    public void setUid(String s){
        mUidTv.setText(s);
    }

    public void setNickName(String s){
        mNickNameTv.setText(s);
    }

    public void setSex(int sex){
        if (sex == 1){
            mSexRg.check(R.id.personal_info_sex_male);
        }else {
            mSexRg.check(R.id.personal_info_sex_female);
        }
    }

    public void setArea(String s){
        mDistrictTv.setText(s);
    }

}
