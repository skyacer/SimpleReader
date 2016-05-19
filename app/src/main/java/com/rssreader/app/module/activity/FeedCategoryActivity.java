package com.rssreader.app.module.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.rssreader.app.module.adapter.FeedCategoryAdapter;
import com.rssreader.app.application.AppContext;
import com.rssreader.app.commons.util.ToastUtil;
import com.rssreader.app.dao.FeedCategoryDao;
import com.rssreader.app.entity.FeedCategory;
import com.rssreader.app.module.R;

import java.util.ArrayList;

public class FeedCategoryActivity extends FragmentActivity
{
	public static final String tag = "FeedCategoryActivity";
	public static final int REQUEST_CODE_CATEGORY = 1;
	private ListView categoryLv;
	private TextView addBtnTv;
	private ArrayList<FeedCategory> fcList = new ArrayList<FeedCategory>();
	private FeedCategoryAdapter mAdapter;
	private FeedCategoryDao fcDao;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		initView();
	}

	private void initView()
	{
		setContentView(R.layout.feed_category);
		categoryLv = (ListView) findViewById(R.id.feed_category_lsit);
		addBtnTv = (TextView) findViewById(R.id.feed_category_add_btn);
		addBtnTv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!AppContext.isNetworkAvailable(FeedCategoryActivity.this)) {
					ToastUtil.makeShortToast(R.string.please_check_network);
					return;
				}
				AddFeedActivity.start(FeedCategoryActivity.this);

			}
		});
		findViewById(R.id.feed_category_btn_back).setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				finish();
			}
		});
		fcDao = new FeedCategoryDao(this);
		fcList = fcDao.getList();
		mAdapter = new FeedCategoryAdapter(this, fcList);
		categoryLv.setAdapter(mAdapter);
		categoryLv.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{
				Intent intent = new Intent();
				intent.putExtra("category", fcList.get(position).getId());
				intent.setClass(FeedCategoryActivity.this, FeedUIActivity.class);
				FeedCategoryActivity.this.startActivityForResult(intent, REQUEST_CODE_CATEGORY);
			}
		});
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_CATEGORY){
            if (resultCode == RESULT_OK){
                finish();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
