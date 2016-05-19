package com.rssreader.app.module.presenter;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.rssreader.app.application.UserInfo;
import com.rssreader.app.commons.util.ResourcesUtil;
import com.rssreader.app.commons.util.ToastUtil;
import com.rssreader.app.module.R;
import com.rssreader.app.module.activity.UserInfoActivity;
import com.rssreader.app.module.base.BasePresenter;
import com.rssreader.app.module.common.Constants;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
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


    @Override
    public void onCreate() {
        super.onCreate();
    }

    public UserInfoPresenter(UserInfoActivity target) {
        super(target);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_login_douban:
                if (UserInfo.getUserPlatForm() ==-1) {
                    login(SHARE_MEDIA.DOUBAN);
                }else {
                    ToastUtil.makeShortToast(R.string.personal_info_logout_first);
                }
                break;
            case R.id.btn_login_weibo:
                if (UserInfo.getUserPlatForm() == -1) {
                    login(SHARE_MEDIA.SINA);
                }else {
                    ToastUtil.makeShortToast(R.string.personal_info_logout_first);
                }
                break;
            case R.id.btn_login_tencentwb:
                if (UserInfo.getUserPlatForm() == -1) {
                    login(SHARE_MEDIA.TENCENT);
                }else {
                    ToastUtil.makeShortToast(R.string.personal_info_logout_first);
                }
                break;
            case R.id.item_logout:
                if (UserInfo.getUserPlatForm()!=-1) {
                    switch (UserInfo.getUserPlatForm()) {
                        case 1:
                            logout(SHARE_MEDIA.SINA);
                            break;
                        case 2:
                            logout(SHARE_MEDIA.DOUBAN);
                            break;
                        case 3:
                            logout(SHARE_MEDIA.TENCENT);
                            break;
                        default:
                            break;
                    }
                }else {
                    ToastUtil.makeShortToast(R.string.personal_info_login_first);
                }
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
                    switch (platform){
                        case SINA:
                            UserInfo.setUserPlatForm(1);
                            break;
                        case DOUBAN:
                            UserInfo.setUserPlatForm(2);
                            break;
                        case TENCENT:
                            UserInfo.setUserPlatForm(3);
                            break;
                        default:
                            break;
                    }
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
     * 注销本次登录</br>
     */
    private void logout(final SHARE_MEDIA platform) {
        mController.deleteOauth(target, platform, new SocializeListeners.SocializeClientListener() {

            @Override
            public void onStart() {

            }

            @Override
            public void onComplete(int status, SocializeEntity entity) {
                ToastUtil.makeShortToast(ResourcesUtil.stringFormat(R.string.personal_info_logout_success));
                target.clearPersonalInfo();
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
                        if (info.get("profile_image_url")!=null){
                            target.setAvatar((String)info.get("profile_image_url"));
                            UserInfo.setUserAvatar((String)info.get("profile_image_url"));
                        }
                        if ((Long)info.get("uid")!=0) {
                            target.setUid(info.get("uid")+"");
                            UserInfo.setUserInfoId(info.get("uid")+"");
                        }
                        if (info.get("screen_name")!=null){
                            target.setNickName((String)info.get("screen_name"));
                            UserInfo.setUserNickName((String)info.get("screen_name"));
                        }
                        if (info.get("gender")!=null){
                            target.setSex((Integer)info.get("gender"));
                            UserInfo.setUserGender((Integer)info.get("gender"));
                        }
                        if (info.get("location")!=null){
                            target.setArea((String)info.get("location"));
                            UserInfo.setUserArea((String)info.get("location"));
                        }

                    }else if(platform == SHARE_MEDIA.TENCENT || platform == SHARE_MEDIA.DOUBAN){
                        if (info.get("profile_image_url")!=null){
                            target.setAvatar((String)info.get("profile_image_url"));
                            UserInfo.setUserAvatar((String)info.get("profile_image_url"));
                        }
                        if (info.get("uid")!=null) {
                            target.setUid((String)info.get("uid"));
                            UserInfo.setUserInfoId((String)info.get("uid"));
                        }
                        if (info.get("screen_name")!=null){
                            target.setNickName((String)info.get("screen_name"));
                            UserInfo.setUserNickName((String)info.get("screen_name"));
                        }
                        if (info.get("gender")!=null){
                            target.setSex((Integer)info.get("gender"));
                            UserInfo.setUserGender((Integer)info.get("gender"));
                        }
                        if (info.get("location")!=null){
                            target.setArea((String)info.get("location"));
                            UserInfo.setUserArea((String)info.get("location"));
                        }

                    }else {
                        ToastUtil.makeShortToast(R.string.login_failed);
                    }
                }
            }
        });
    }

}
