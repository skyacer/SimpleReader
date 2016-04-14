package com.rssreader.app.utils;

import android.util.Log;

import org.apache.http.HttpStatus;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author:	LuoChangAn
 *
 */
public class HttpUtils
{
	private static HttpURLConnection conn = null;
	public static final String tag = "HttpUtils";
	private static final int TIMEOUT = 1000*10;
	
	/**
	 * @param url
	 * @return InputStream
	 * @throws Exception 
	 */
	public static InputStream getInputStream(String url) throws Exception
	{
		InputStream is = null;
		URL httpURL = null;
		
		httpURL = new URL(url);
		conn = (HttpURLConnection) httpURL.openConnection();
		conn.setConnectTimeout(TIMEOUT);
		conn.setReadTimeout(TIMEOUT);
		if(conn.getResponseCode() == HttpStatus.SC_OK)
		{
			Log.d(tag, "connected!");
			is = conn.getInputStream();
		}
		return is;
	}
	
	public static void disConnect()
	{
		if(null != conn)
		{
			conn.disconnect();
		}
	}
}
