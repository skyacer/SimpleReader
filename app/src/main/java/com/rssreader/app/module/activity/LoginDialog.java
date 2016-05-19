package com.rssreader.app.module.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.widget.Toast;

import com.rssreader.app.application.AppContext;
import com.rssreader.app.commons.util.ToastUtil;
import com.rssreader.app.entity.SinaPersonalInfo;
import com.rssreader.app.module.R;
import com.rssreader.app.module.common.Constants;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.bean.StatusCode;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.controller.listener.SocializeListeners.SocializeClientListener;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;

import java.util.Map;

/**
 * @author LuoChangAn
 */
public class LoginDialog extends DialogFragment
{

	public static final String tag = "LoginDialog";
	private Activity mActivity;
	private static final int POS_SINA_WEIBO = 0;
	private static final int POS_TENCENT = 1;

    private SinaPersonalInfo sinaPersonalInfo;

    // 整个平台的Controller, 负责管理整个SDK的配置、操作等处理
    private UMSocialService mController = UMServiceFactory
            .getUMSocialService(Constants.DESCRIPTOR);
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		mActivity = getActivity();
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        addQZoneQQPlatform();
        //设置新浪sso登录
        mController.getConfig().setSsoHandler(new SinaSsoHandler());
        sinaPersonalInfo = new SinaPersonalInfo();
		builder.setItems(R.array.login_accounts, new OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				if(!AppContext.isNetworkAvailable(mActivity))	
				{
					Toast.makeText(mActivity, "请检查网络设置！", Toast.LENGTH_SHORT).show();
					return;
				}
				
				switch(which) {
                    case POS_SINA_WEIBO:
                        login(SHARE_MEDIA.SINA);
                        break;
                    case POS_TENCENT:
                        login(SHARE_MEDIA.TENCENT);
                        break;
				}
			}
		});
		return builder.create();
	}

    private void login(final SHARE_MEDIA platform) {
        mController.doOauthVerify(getActivity(), platform, new SocializeListeners.UMAuthListener() {

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
     * 注销本次登录</br>
     */
    private void logout(final SHARE_MEDIA platform) {
        mController.deleteOauth(mActivity, platform, new SocializeClientListener() {

            @Override
            public void onStart() {

            }

            @Override
            public void onComplete(int status, SocializeEntity entity) {
                String showText = "解除" + platform.toString() + "平台授权成功";
                if (status != StatusCode.ST_CODE_SUCCESSED) {
                    showText = "解除" + platform.toString() + "平台授权失败[" + status + "]";
                }
                ToastUtil.makeShortToast(showText);
            }
        });
    }


    /**
     * 获取授权平台的用户信息</br>
     */
    private void getUserInfo(final SHARE_MEDIA platform) {
        mController.getPlatformInfo(mActivity, platform, new SocializeListeners.UMDataListener() {

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

                    }
                }
            }
        });
    }



    private void addQZoneQQPlatform() {
        String appId = "100424468";
        String appKey = "c7394704798a158208a74ab60104f0ba";
        // 添加QQ支持, 并且设置QQ分享内容的target url
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(getActivity(),
                appId, appKey);
        qqSsoHandler.setTargetUrl("http://www.umeng.com");
        qqSsoHandler.addToSocialSDK();

        // 添加QZone平台
        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(getActivity(), appId, appKey);
        qZoneSsoHandler.addToSocialSDK();
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(
                requestCode);
        if (ssoHandler != null) {
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }
}
