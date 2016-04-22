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
import com.rssreader.app.commons.IFlyHelper;
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
    private SpeechSynthesizer tts;
    private SynthesizerListener mTtsListener;
    private static int speechCount = 0;
    private boolean existSpeech = false;// 退出tts
    // 开始词
    private static final String START_WORDS = "欢迎收听";

    public ItemListPresenter(ItemListActivity target) {
        super(target);
    }



    @Override
    public void onCreate() {
        super.onCreate();
        initTts();
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

    private void initTts()
    {
        tts = new SpeechSynthesizer(target, null);
        tts.setParameter(SpeechConstant.ENGINE_TYPE, "local");
        tts.setParameter(SpeechSynthesizer.SPEED, "50");
        tts.setParameter(SpeechSynthesizer.PITCH, "50");
        mTtsListener = new SynthesizerListener.Stub()
        {
            @Override
            public void onSpeakResumed() throws RemoteException
            {
            }
            @Override
            public void onSpeakProgress(int arg0) throws RemoteException
            {
            }
            @Override
            public void onSpeakPaused() throws RemoteException
            {
            }
            @Override
            public void onSpeakBegin() throws RemoteException
            {
            }

            @Override
            public void onCompleted(int arg0) throws RemoteException
            {
                speechCount++;
                if (speechCount > target.speechTextList.size())
                    return;
                tts.startSpeaking(target.speechTextList.get(speechCount), mTtsListener);
            }

            @Override
            public void onBufferProgress(int arg0) throws RemoteException
            {
            }
        };
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nav_right_img:
            {
                if (!IFlyHelper.checkSpeechServiceInstall(target)) {
                    IFlyHelper.openDownloadDialog(target);
                    return;
                }
                if (existSpeech) {
                    tts.stopSpeaking(mTtsListener);
                    existSpeech = false;
                    return;
                }
                startSpeech();
                existSpeech = true;
                Toast.makeText(target, "再按一次退出播放", Toast.LENGTH_SHORT)
                        .show();
                break;
            }
        }
    }

    private void startSpeech()
    {
        DateFormat df = SimpleDateFormat.getTimeInstance();
        String time = df.format(new Date());
        String timeTip = "现在是：" + time;
        tts.startSpeaking(START_WORDS + target.sectionTitle + "频道" + timeTip
                + target.speechTextList.get(0), mTtsListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        tts.stopSpeaking(mTtsListener);
        tts.destory();
    }
}
