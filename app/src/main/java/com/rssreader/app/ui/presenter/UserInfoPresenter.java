package com.rssreader.app.ui.presenter;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.rssreader.app.commons.util.ToastUtil;
import com.rssreader.app.entity.SinaPersonalInfo;
import com.rssreader.app.ui.R;
import com.rssreader.app.ui.activity.UserInfoActivity;
import com.rssreader.app.ui.base.BasePresenter;
import com.rssreader.app.ui.common.Constants;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.exception.SocializeException;

import java.util.Map;

/**
 * Created by LuoChangAn on 16/4/26.
 */
public class UserInfoPresenter extends BasePresenter<UserInfoActivity> implements View.OnClickListener{
    // 整个平台的Controller, 负责管理整个SDK的配置、操作等处理
    private UMSocialService mController = UMServiceFactory
            .getUMSocialService(Constants.DESCRIPTOR);

    private SinaPersonalInfo sinaPersonalInfo;

    @Override
    public void onCreate() {
        super.onCreate();
        initData();
    }

    private void initData() {
        sinaPersonalInfo = new SinaPersonalInfo();
    }

    public UserInfoPresenter(UserInfoActivity target) {
        super(target);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_login_douban:
                login(SHARE_MEDIA.DOUBAN);
                break;
            case R.id.btn_login_weibo:
                login(SHARE_MEDIA.SINA);
                break;
            case R.id.btn_login_tencentwb:
                login(SHARE_MEDIA.TENCENT);
                break;
            default:
                break;
        }
    }

    private void login(final SHARE_MEDIA platform) {
        mController.doOauthVerify(target, platform, new SocializeListeners.UMAuthListener() {

            @Override
            public void onStart(SHARE_MEDIA platform) {

            }

            @Override
            public void onError(SocializeException e, SHARE_MEDIA platform) {
                ToastUtil.makeShortToast(R.string.login_failed);
            }

            @Override
            public void onComplete(Bundle value, SHARE_MEDIA platform) {
                String uid = value.getString("uid");
                if (!TextUtils.isEmpty(uid)) {
                    getUserInfo(platform);
                } else {
                    ToastUtil.makeShortToast(R.string.login_failed);
                }
            }

            @Override
            public void onCancel(SHARE_MEDIA platform) {
            }
        });
    }

    /**
     * 获取授权平台的用户信息</br>
     */
    private void getUserInfo(final SHARE_MEDIA platform) {
        mController.getPlatformInfo(target, platform, new SocializeListeners.UMDataListener() {

            @Override
            public void onStart() {

            }

            @Override
            public void onComplete(int status, Map<String, Object> info) {
                if (info != null) {
                    if (platform == SHARE_MEDIA.SINA) {
                        if (info.get("screen_name")!=null) {
                            sinaPersonalInfo.setScreen_name((String) info.get("screen_name"));
                            sinaPersonalInfo.setGender((Integer) info.get("gender"));

                        }
                    }else if(platform == SHARE_MEDIA.TENCENT){
                        ToastUtil.makeShortToast(info.toString());

                    }else if (platform == SHARE_MEDIA.DOUBAN){

                    }else {

                    }
                }
            }
        });
    }

}
