package com.rssreader.app.ui.activity;

import android.os.Bundle;

import com.rssreader.app.ui.R;
import com.rssreader.app.ui.base.BaseActionBarActivity;
import com.rssreader.app.ui.presenter.FeedbackPresenter;

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
