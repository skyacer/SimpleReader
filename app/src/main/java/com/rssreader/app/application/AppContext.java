package com.rssreader.app.application;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.webkit.CacheManager;

import com.rssreader.app.commons.util.FileUtils;
import com.rssreader.app.commons.util.MD5;

import java.io.File;

/**
 * @author LuoChangAn
 */
@SuppressWarnings("deprecation")
public class AppContext
{
	
	public static boolean isNetworkAvailable(Context context)
	{
		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE); 
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if(networkInfo == null || networkInfo.isConnected() == false)
		{
			return false;
		}
		return true;
	}

	public static boolean isWifi(Context context)
	{
		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE); 
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		return networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
	}
	
	public static SharedPreferences getPrefrences(Context context)
	{
		return PreferenceManager.getDefaultSharedPreferences(context);
	}
	
	//清楚缓存
	public static void clearCache(Context context)
	{
		clearSdCache();
		clearWebViewCache(context);
	}
	
	public static void clearSdCache()
	{
		FileUtils.deleteDirectory(AppConfig.APP_SECTION_DIR);
		FileUtils.deleteDirectory(AppConfig.APP_IMAGE_CACHE_DIR);
	}
	
	//清除webview缓存
	public static void clearWebViewCache(Context context)
	{
		File file = CacheManager.getCacheFileBaseDir();  
		if (file != null && file.exists() && file.isDirectory()) {  
		    for (File item : file.listFiles()) {  
		    	item.delete();  
		    }  
		    file.delete();  
		}
		context.deleteDatabase("webview.db");  
		context.deleteDatabase("webview.db-shm");  
		context.deleteDatabase("webview.db-wal");  
		context.deleteDatabase("webviewCache.db");  
		context.deleteDatabase("webviewCache.db-shm");  
		context.deleteDatabase("webviewCache.db-wal");  
	}	
	
	public static File getSdImgCache(String url)
	{
		return new File(AppConfig.APP_IMAGE_CACHE_DIR
				+ File.separator + MD5.Md5(url));
	}
	
	public static File getSectionCache(String url)
	{
		return new File(AppConfig.APP_SECTION_DIR 
				+ File.separator + MD5.Md5(url));
	}
}
