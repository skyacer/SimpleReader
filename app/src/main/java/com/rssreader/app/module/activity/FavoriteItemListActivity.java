package com.rssreader.app.module.activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListView;

import com.rssreader.app.module.adapter.ItemListAdapter;
import com.rssreader.app.db.DbManager;
import com.rssreader.app.entity.FeedItem;
import com.rssreader.app.module.R;
import com.rssreader.app.module.base.BaseActionBarActivity;
import com.rssreader.app.module.presenter.FavoriteItemListPresenter;

import java.util.ArrayList;

/**
 * Created by LuoChangAn on 16/4/5.
 */
public class FavoriteItemListActivity extends BaseActionBarActivity<FavoriteItemListPresenter>
{
	public String sectionUrl;

	public ListView favoriteLv;

	public ItemListAdapter mAdapter;

	public ArrayList<FeedItem> items = new ArrayList<FeedItem>();


	@Override
	protected void initPresenter() {
		presenter = new FavoriteItemListPresenter(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		initData();
	}

	private void initView()
	{
		setRealContentView(R.layout.favorite_list);
		setTitle(R.string.favorite_title);
		favoriteLv = (ListView) findViewById(R.id.favorite_list);
		favoriteLv.setOnItemClickListener(presenter);
	}

	private void initData()
	{
		DbManager mgr = new DbManager(this, DbManager.DB_NAME, null, 1);
		SQLiteDatabase db = mgr.getWritableDatabase();
		Cursor cursor = db.query(DbManager.FAVORITE_ITEM_TABLE_NAME, null, null, null, null, null, null);
		if(cursor.moveToFirst())
		{
			for(int i = 0, n = cursor.getCount(); i < n; i++)
			{
				FeedItem item = new FeedItem();
				String title = cursor.getString(cursor.getColumnIndex("title"));
				String pubdate = cursor.getString(cursor.getColumnIndex("pubdate"));
				String itemDetail = cursor.getString(cursor.getColumnIndex("item_detail"));
				String link = cursor.getString(cursor.getColumnIndex("link"));

				sectionUrl = cursor.getString(cursor.getColumnIndex("table_url"));

				item.setTitle(title);
				item.setPubdate(pubdate);
				item.setContent(itemDetail);
				item.setFavorite(true);
				item.setLink(link);

				items.add(item);
				cursor.moveToNext();
			}
		}
		mAdapter = new ItemListAdapter(this, items, false);

		favoriteLv.setAdapter(mAdapter);
		cursor.close();
		db.close();
	}


	public void setAdapter(ItemListAdapter adapter){
		favoriteLv.setAdapter(adapter);
	}

}
