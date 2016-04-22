package com.rssreader.app.ui.activity;

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

import com.rssreader.app.commons.AppContext;
import com.rssreader.app.commons.util.ToastUtil;
import com.rssreader.app.ui.R;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.bean.StatusCode;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.controller.listener.SocializeListeners.SocializeClientListener;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;

import java.util.Map;

/**
 * @author LuoChangAn
 */
public class LoginDialog extends DialogFragment
{

	public static final String tag = "LoginDialog";
    public static final String DESCRIPTOR = "com.rsssreader.ui.app";
	private Activity mActivity;
	private static final int POS_SINA_WEIBO = 0;
    private static final int POS_SINA_WEIBO_LOGOUT = 1;
	private static final int POS_QQZONE = 2;
	private static final int POS_QQZONE_LOGOUT = 3;
    // 整个平台的Controller, 负责管理整个SDK的配置、操作等处理
    private UMSocialService mController = UMServiceFactory
            .getUMSocialService(DESCRIPTOR);
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		mActivity = getActivity();
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        addQZoneQQPlatform();
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
                    case POS_SINA_WEIBO_LOGOUT:
                        logout(SHARE_MEDIA.SINA);
                        break;
                    case POS_QQZONE:
                        login(SHARE_MEDIA.QQ);
                        break;
                    case POS_QQZONE_LOGOUT:
                        logout(SHARE_MEDIA.QQ);
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
                ToastUtil.makeShortToast("start");
            }

            @Override
            public void onError(SocializeException e, SHARE_MEDIA platform) {
            }

            @Override
            public void onComplete(Bundle value, SHARE_MEDIA platform) {
                ToastUtil.makeShortToast("onComplete");
                String uid = value.getString("uid");
                if (!TextUtils.isEmpty(uid)) {
                    getUserInfo(platform);
                } else {
                    ToastUtil.makeShortToast("授权失败...");
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
                Toast.makeText(getActivity(), showText, Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * 获取授权平台的用户信息</br>
     */
    private void getUserInfo(SHARE_MEDIA platform) {
        mController.getPlatformInfo(mActivity, platform, new SocializeListeners.UMDataListener() {

            @Override
            public void onStart() {

            }

            @Override
            public void onComplete(int status, Map<String, Object> info) {
                if (info != null) {
                    ToastUtil.makeLongToast(info.toString());
                }
            }
        });
    }



    private void addQZoneQQPlatform() {
        String appId = "1105267281";
        String appKey = "pWvrIZZ2VSDTBQUk";
        // 添加QQ支持, 并且设置QQ分享内容的target url
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(getActivity(),
                appId, appKey);
        qqSsoHandler.setTargetUrl("http://www.umeng.com");
        qqSsoHandler.addToSocialSDK();

        // 添加QZone平台
        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(getActivity(), appId, appKey);
        qZoneSsoHandler.addToSocialSDK();
    }


    private final class ScializeMonitor implements SocializeClientListener
	{

		@Override
		public void onStart(){}
		
		@Override
		public void onComplete(int status, SocializeEntity entity)
		{
			if(status == 200)
			{
//				Toast.makeText(mActivity, "登陆成功！", Toast.LENGTH_SHORT).show();
                ToastUtil.makeShortToast(entity.getNickName());
            }
			else
			{
				Toast.makeText(mActivity, "网络异常！", Toast.LENGTH_SHORT).show();
			}
		}
		
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
