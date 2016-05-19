package com.rssreader.app.module;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;

import com.rssreader.app.module.R;

public class About extends Activity 
{
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		initView();
	}
	
	private void initView()
	{
		findViewById(R.id.about_return_btn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		WebView mWebView = (WebView) findViewById(R.id.about_webview);
		mWebView.loadUrl("file:///android_asset/about.html");
	}
}
