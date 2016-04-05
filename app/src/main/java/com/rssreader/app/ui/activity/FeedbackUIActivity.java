package com.rssreader.app.ui.activity;

import android.os.Bundle;

import com.rssreader.app.commons.util.ResourcesUtil;
import com.rssreader.app.ui.R;
import com.rssreader.app.ui.base.BaseActionBarActivity;
import com.rssreader.app.ui.presenter.FeedbackPresenter;

public class FeedbackUIActivity extends BaseActionBarActivity<FeedbackPresenter>
{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRealContentView(R.layout.fadeback_view);

		initView();
	}

	private void initView() {
		setTitle(R.string.submit_suggestion);
		setRightText(R.string.submit);
		setRightTextColor(ResourcesUtil.getColor(R.color.white));
		findViewById(R.id.feedback_btn).setOnClickListener(presenter);

	}

	@Override
	protected void initPresenter() {
		presenter = new FeedbackPresenter(this);
	}

}
