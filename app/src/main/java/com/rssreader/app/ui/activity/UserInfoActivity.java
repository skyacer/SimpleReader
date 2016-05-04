package com.rssreader.app.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.rssreader.app.application.UserInfo;
import com.rssreader.app.commons.AppContext;
import com.rssreader.app.commons.util.ThreadUtil;
import com.rssreader.app.ui.R;
import com.rssreader.app.ui.base.BaseActionBarActivity;
import com.rssreader.app.ui.presenter.UserInfoPresenter;
import com.rssreader.app.utils.ImageUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by LuoChangAn on 16/4/26.
 */
public class UserInfoActivity extends BaseActionBarActivity<UserInfoPresenter>{
    TextView mUidTv;
    TextView mNickNameTv;
    RadioGroup mSexRg;
    TextView mDistrictTv;
    ImageView mAvatarIv;
    //头像
    Bitmap mAvatarBitmap;
    private final MyHandler mHandler = new MyHandler(this);
    public static final int GET_AVATAR = 1;


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
        initData();
    }

    private void initView() {
        setTitle(R.string.personal_info_title);
        setRealContentView(R.layout.activity_personal_info);

        mUidTv = (TextView) findViewById(R.id.tv_edit_detail_right_id);
        mNickNameTv = (TextView) findViewById(R.id.tv_edit_detail_right_nickname);
        mSexRg = (RadioGroup) findViewById(R.id.personal_info_sex_rg);
        mDistrictTv = (TextView) findViewById(R.id.tv_edit_detail_right_area);
        mAvatarIv = (ImageView) findViewById(R.id.personal_info_avatar_img);

        findViewById(R.id.btn_login_douban).setOnClickListener(presenter);
        findViewById(R.id.btn_login_tencentwb).setOnClickListener(presenter);
        findViewById(R.id.btn_login_weibo).setOnClickListener(presenter);
        findViewById(R.id.item_logout).setOnClickListener(presenter);

    }

    private void initData() {
        if (UserInfo.getUserNickName()!=null) {
            setNickName(UserInfo.getUserNickName());
        }
        if (UserInfo.getUserInfoId()!=null){
            setUid(UserInfo.getUserInfoId());
        }
        if (UserInfo.getUserArea()!=null){
            setArea(UserInfo.getUserArea());
        }
        if (UserInfo.getUserGender()!=-1) {
            setSex(UserInfo.getUserGender());
        }
        if (UserInfo.getUserAvatar()!=null){
            setAvatar(UserInfo.getUserAvatar());
        }
    }

    public void clearPersonalInfo(){
        setUid("");
        setNickName("");
        setSex(-1);
        setArea("");
        mAvatarIv.setImageBitmap(null);
        UserInfo.clearData();
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
        }else if(sex == -1){
            mSexRg.clearCheck();
        }
        else {
            mSexRg.check(R.id.personal_info_sex_female);
        }
    }

    public void setArea(String s){
        mDistrictTv.setText(s);
    }

    public void setAvatar(final String params){
        if (params==null || TextUtils.isEmpty(params)){
            return;
        }

        if (params.equals(UserInfo.getUserAvatar())){
            File file = AppContext.getSdImgCache(params);
            if(file.exists()) {
                try {
                    mAvatarBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    mAvatarIv.setImageBitmap(mAvatarBitmap);
                } catch (OutOfMemoryError e) {
                    e.printStackTrace();
                }
            }
        }else {
            ThreadUtil.runOnAnsy(new Runnable() {
                @Override
                public void run() {
                    try {
                        URL url = new URL(params);
                        HttpURLConnection conn  = (HttpURLConnection)url.openConnection();
                        conn.setDoInput(true);
                        conn.connect();
                        InputStream inputStream=conn.getInputStream();
                        mAvatarBitmap = BitmapFactory.decodeStream(inputStream);
                        ImageUtils.saveImageToSD(mAvatarBitmap,params);
                        Message msg=new Message();
                        msg.what=GET_AVATAR;
                        mHandler.sendMessage(msg);

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            },"AvatarThread");
        }
    }

    private static class MyHandler extends Handler {
        private final WeakReference<UserInfoActivity> mActivity;

        public MyHandler(UserInfoActivity activity) {
            mActivity = new WeakReference<UserInfoActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            UserInfoActivity activity = mActivity.get();
            if (activity != null) {
                switch (msg.what){
                    case GET_AVATAR:
                        activity.mAvatarIv.setImageBitmap(activity.mAvatarBitmap);
                }
            }
        }
    }

}
