package com.rssreader.app.module.activity;

import android.os.Bundle;

import com.rssreader.app.module.R;
import com.rssreader.app.module.base.BaseActionBarActivity;
import com.rssreader.app.module.presenter.FeedbackPresenter;

/**
 * Created by LuoChangAn on 16/4/5.
 */
public class FeedbackUIActivity extends BaseActionBarActivity<FeedbackPresenter> {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRealContentView(R.layout.fadeback_view);

		initView();
	}

	private void initView() {
		setTitle(R.string.submit_suggestion);
		setRightText(R.string.submit);
		setRightTextColor(R.color.white);
		findViewById(R.id.nav_right_text).setOnClickListener(presenter);

	}

	@Override
	protected void initPresenter() {
		presenter = new FeedbackPresenter(this);
	}


}
