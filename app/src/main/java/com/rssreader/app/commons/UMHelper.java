package com.rssreader.app.commons;

import com.umeng.socialize.controller.RequestType;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;

/**
 * @author LuoChangAn
 *
 */
public class UMHelper
{
	private final static UMSocialService mController = UMServiceFactory.
			getUMSocialService("com.rssreader.app.reader.umeng.usr",	RequestType.SOCIAL);
	
	
	public static UMSocialService getUMSocialService()
	{
		return mController;
	}
}
