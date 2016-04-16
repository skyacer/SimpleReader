package com.rssreader.app.ui.presenter;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;

import com.rssreader.app.commons.ItemListEntityParser;
import com.rssreader.app.commons.DatabaseHelper;
import com.rssreader.app.commons.SeriaHelper;
import com.rssreader.app.commons.util.ToastUtil;
import com.rssreader.app.db.DbManager;
import com.rssreader.app.entity.ItemListEntity;
import com.rssreader.app.ui.R;
import com.rssreader.app.ui.activity.AddFeedActivity;
import com.rssreader.app.ui.activity.Main;
import com.rssreader.app.ui.base.BasePresenter;
import com.rssreader.app.utils.CategoryNameExchange;

import java.io.File;

/**
 * Created by LuoChangAn on 16/4/6.
 */
public class AddFeedPresenter extends BasePresenter<AddFeedActivity> implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    private int tableCid;


    public AddFeedPresenter(AddFeedActivity target) {
        super(target);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String tableNameStr = parent.getItemAtPosition(position).toString();
        tableCid = CategoryNameExchange.zh2cid(tableNameStr);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.nav_right_text:
                saveRssFeed();
                break;
            default:
                break;
        }
    }

    private void saveRssFeed() {
        final String url = target.getRssAddress();
        final String title = target.getRssName();

        new AsyncTask<String, Integer, String>() {

            @Override
            protected void onPostExecute(String result)
            {
                target.hideLoading();

                if(result == null)
                {
                    ToastUtil.makeShortToast(R.string.add_feed_fail);
                    return;
                }
                ToastUtil.makeShortToast(R.string.add_feed_success);

                //加入section表

                insertToFeedDB(title,url);

                Intent intent = new Intent();
                intent.setAction(Main.ACTION_ADD_SECTION);
                target.sendBroadcast(intent);
                target.finish();
            }

            @Override
            protected void onPreExecute()
            {
                target.showLoading();
            }

            @Override
            protected String doInBackground(String... params) {
                String title = null;

                ItemListEntityParser parser = new ItemListEntityParser();
                ItemListEntity entity = parser.parse(params[0]);
                if(entity != null)
                {
                    SeriaHelper helper = SeriaHelper.newInstance();
                    File cache = DatabaseHelper.newSdCache(params[0]);
                    helper.saveObject(entity, cache);
                    title = parser.getFeedTitle();
                }
                return title;
            }

        }.execute(url);
    }

    private void insertToFeedDB(String title,String url){
        DbManager mgr = new DbManager(target, DbManager.FEED_DB_NAME, null, 1);
        SQLiteDatabase db = mgr.getWritableDatabase();
        DatabaseHelper.insertToFeed(db, title, url, tableCid);
        db.close();
    }
}
