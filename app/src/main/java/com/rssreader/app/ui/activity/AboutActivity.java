package com.rssreader.app.ui.activity;

import android.os.Bundle;
import android.webkit.WebView;

import com.rssreader.app.ui.R;
import com.rssreader.app.ui.base.BaseActionBarActivity;
import com.rssreader.app.ui.presenter.AboutPresenter;

public class AboutActivity extends BaseActionBarActivity<AboutPresenter>{
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setRealContentView(R.layout.about);
		initView();
	}

	@Override
	protected void initPresenter() {
		presenter = new AboutPresenter(this);
	}

	private void initView(){
		setTitle(R.string.about_me);
		showUrl();

	}

	private void showUrl(){
		WebView mWebView = (WebView) findViewById(R.id.about_webview);
		mWebView.loadUrl("file:///android_asset/about.html");
	}
}
