package com.rssreader.app.ui.presenter;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.iflytek.speech.SpeechConstant;
import com.iflytek.speech.SpeechSynthesizer;
import com.iflytek.speech.SynthesizerListener;
import com.rssreader.app.commons.AppContext;
import com.rssreader.app.commons.DatabaseHelper;
import com.rssreader.app.commons.ItemListEntityParser;
import com.rssreader.app.commons.SeriaHelper;
import com.rssreader.app.commons.util.ToastUtil;
import com.rssreader.app.entity.FeedItem;
import com.rssreader.app.entity.ItemListEntity;
import com.rssreader.app.ui.R;
import com.rssreader.app.ui.activity.ItemDetailActivity;
import com.rssreader.app.ui.activity.ItemListActivity;
import com.rssreader.app.ui.base.BasePresenter;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by LuoChangAn on 16/4/21.
 */
public class ItemListPresenter extends BasePresenter<ItemListActivity> implements PullToRefreshBase.OnRefreshListener,AdapterView.OnItemClickListener,View.OnClickListener{


    public ItemListPresenter(ItemListActivity target) {
        super(target);
    }



    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        if (!AppContext.isNetworkAvailable(target))
        {
            target.itemLv.onRefreshComplete();
            ToastUtil.makeShortToast(R.string.no_network);
            return;
        }
        new ItemListPresenter.RefreshTask().execute(target.sectionUrl);
    }

    public class RefreshTask extends
            AsyncTask<String, Integer, ItemListEntity>
    {
        @Override
        protected void onPostExecute(ItemListEntity result)
        {
            if (result == null)
            {
                target.itemLv.onRefreshComplete();
                ToastUtil.makeShortToast(R.string.network_exception);
                return;
            }
            ArrayList<FeedItem> newItems = new ArrayList<FeedItem>();
            File cache = DatabaseHelper.getSdCache(target.sectionUrl);
            SeriaHelper helper = SeriaHelper.newInstance();
            ArrayList<FeedItem> items = result.getItemList();
            ItemListEntity old = (ItemListEntity) helper.readObject(cache);
            String oldFirstDate = old.getFirstItem().getPubdate();
            int newCount = 0;
            for (FeedItem i : items)
            {
                if (i.getPubdate().equals(oldFirstDate))
                {
                    target.itemLv.onRefreshComplete();
                    ToastUtil.makeShortToast(R.string.no_update);
                    return;
                }
                newCount++;
                newItems.add(i);
            }
            helper.saveObject(result, cache);
            target.mAdapter.addItemsToHead(newItems);
            Toast.makeText(target, "更新了" + newCount + "条",
                    Toast.LENGTH_SHORT).show();
            target.itemLv.onRefreshComplete();
        }

        @Override
        protected ItemListEntity doInBackground(String... params)
        {
            ItemListEntityParser parser = new ItemListEntityParser();
            return parser.parse(params[0]);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent();
        FeedItem item = target.mItems.get(position - 1);

        final String link = item.getLink();
        // 改变阅读状态
        if (!item.isReaded())
        {
            item.setReaded(true);
            target.mAdapter.notifyDataSetChanged();

            new Thread()
            {
                @Override
                public void run()
                {
                    SeriaHelper helper = SeriaHelper.newInstance();
                    File cache = DatabaseHelper.getSdCache(target.sectionUrl);
                    ItemListEntity entity = new ItemListEntity();
                    for (FeedItem i : target.mItems)
                    {
                        if (i.getLink().equals(link))
                        {
                            i.setReaded(true);
                        }
                    }
                    entity.setItemList(target.mItems);
                    helper.saveObject(entity, cache);
                }

            }.start();
        }
        String title = item.getTitle();
        String contentEncoded = item.getContent();
        String pubdate = item.getPubdate();
        boolean isFavorite = item.isFavorite();
        String firstImgUrl = item.getFirstImageUrl();
        if (contentEncoded != null && contentEncoded.length() != 0)
        {
            intent.putExtra("item_detail", contentEncoded);
        }
        intent.putExtra("section_title", target.sectionTitle);
        intent.putExtra("section_url", target.sectionUrl);
        intent.putExtra("title", title);
        intent.putExtra("pubdate", pubdate);
        intent.putExtra("link", link);
        intent.putExtra("is_favorite", isFavorite);
        intent.putExtra("first_img_url", firstImgUrl);
        intent.setClass(target, ItemDetailActivity.class);
        target.startActivity(intent);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

}
