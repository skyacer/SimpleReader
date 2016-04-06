package com.rssreader.app.ui.presenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.widget.AdapterView;

import com.rssreader.app.entity.FeedItem;
import com.rssreader.app.ui.activity.FavoriteItemListActivity;
import com.rssreader.app.ui.activity.ItemDetail;
import com.rssreader.app.ui.activity.ItemList;
import com.rssreader.app.ui.base.BasePresenter;

/**
 * Created by LuoChangAn on 16/4/5.
 */
public class FavoriteItemListPresenter extends BasePresenter<FavoriteItemListActivity> implements AdapterView.OnItemClickListener{
    private Intent intent = new Intent();

    private BroadcastReceiver mReceiver;


    public FavoriteItemListPresenter(FavoriteItemListActivity target) {
        super(target);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initBroadCast();
    }

    private void initBroadCast(){
        mReceiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                String link = intent.getStringExtra("link");
                for(int i = 0; i < target.items.size(); i++)
                {
                    FeedItem item = target.items.get(i);
                    if(item.getLink().equals(link))
                    {
                        target.items.remove(i);
                        target.mAdapter.notifyDataSetChanged();
                        return;
                    }
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(ItemList.ACTION_UPDATE_ITEM_LIST);
        target.registerReceiver(mReceiver, filter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        FeedItem item = target.items.get(position);
        String title = item.getTitle();
        String content = item.getContent();
        String pubdate = item.getPubdate();
        String link = item.getLink();
        if (content != null && content.length() != 0) {
            intent.putExtra("item_detail", content);
        }
        intent.putExtra("title", title);
        intent.putExtra("pubdate", pubdate);
        intent.putExtra("is_favorite", true);
        intent.putExtra("link", link);
        intent.putExtra("section_url", target.sectionUrl);

        intent.setClass(target, ItemDetail.class);
        target.startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        target.unregisterReceiver(mReceiver);

    }
}
