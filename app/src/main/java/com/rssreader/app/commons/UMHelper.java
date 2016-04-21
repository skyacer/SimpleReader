package com.rssreader.app.commons;

import android.content.Context;

import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;

/**
 * @author LuoChangAn
 *
 */
public class UMHelper
{
    private UMHelper(){}

	private final static UMSocialService mController = UMServiceFactory
            .getUMSocialService("com.umeng.share");


    public static UMSocialService getUMSocialService(Context context)
	{
		return mController;
	}
}
