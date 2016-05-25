package com.rssreader.app.module.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.rssreader.app.application.AppContext;
import com.rssreader.app.commons.HtmlFilter;
import com.rssreader.app.commons.SeriaHelper;
import com.rssreader.app.commons.UIHelper;
import com.rssreader.app.commons.util.ThreadUtil;
import com.rssreader.app.commons.util.ToastUtil;
import com.rssreader.app.db.DbManager;
import com.rssreader.app.db.FavoItemDbHelper;
import com.rssreader.app.entity.FeedItem;
import com.rssreader.app.entity.ItemListEntity;
import com.rssreader.app.module.R;
import com.rssreader.app.module.base.BaseActionBarActivity;
import com.rssreader.app.module.presenter.ItemDetailPresenter;
import com.umeng.socialize.sso.UMSsoHandler;

import java.io.File;
import java.util.ArrayList;

@SuppressLint("JavascriptInterface")
@SuppressWarnings("deprecation")
public class ItemDetailActivity extends BaseActionBarActivity<ItemDetailPresenter> {
    private ImageButton collectBtn;
    public static WebView mWebView;
    private String sectionTitle;
    private String sectionUrl;
    private String title;
    public TextView countTv;//评论列表
    private String pubdate;
    private String itemDetail;
    private String link;
    private String firstImgUrl;
    private boolean isFavorite;//文章是否已收藏
    public String css = UIHelper.WEB_STYLE;
    private int[] favoIcons = {
            R.drawable.btn_favorite_empty,
            R.drawable.btn_favorite_full
    };//0为空

    public String speechText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        loadData();
    }

    @Override
    protected void initPresenter() {
        presenter = new ItemDetailPresenter(this);
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            Intent intent = new Intent();
            intent.setAction(ItemListActivity.ACTION_UPDATE_ITEM_LIST);
            sendBroadcast(intent);
            super.handleMessage(msg);
        }
    };



    @SuppressLint("SetJavaScriptEnabled")
    private void initView() {
        Intent intent = getIntent();
        sectionTitle = intent.getStringExtra("section_title");
        sectionUrl = intent.getStringExtra("section_url");
        firstImgUrl = intent.getStringExtra("first_img_url");

        title = intent.getStringExtra("title");
        pubdate = intent.getStringExtra("pubdate");
        itemDetail = intent.getStringExtra("item_detail");
        link = intent.getStringExtra("link");

        SharedPreferences prefs = AppContext.getPrefrences(this);
        if (prefs.getBoolean("day_night_mode", false)) {
            setTheme(R.style.AppNightTheme);
            css = UIHelper.WEB_STYLE_NIGHT;
            favoIcons = new int[]{
                    R.drawable.btn_favorite_empty_night,
                    R.drawable.btn_favorite_full_night
            };
        }
        isFavorite = getIntent().getBooleanExtra("is_favorite", false);

        setRealContentView(R.layout.feed_item_detail);
        setTitle(sectionTitle);
        setRightView(R.drawable.btn_play);

        findViewById(R.id.nav_right_img).setOnClickListener(presenter);
        findViewById(R.id.fid_btn_share).setOnClickListener(presenter);
        findViewById(R.id.fid_btn_comment).setOnClickListener(presenter);

        collectBtn = (ImageButton) findViewById(R.id.fid_btn_collecte);
        if (isFavorite)
            collectBtn.setImageResource(R.drawable.btn_favorite_full);
        collectBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DbManager helper = new DbManager(ItemDetailActivity.this, DbManager.DB_NAME, null, 1);
                final SQLiteDatabase db = helper.getWritableDatabase();
                //已收藏，取消收藏
                if (isFavorite) {
                    collectBtn.setImageResource(favoIcons[0]);
                    Toast.makeText(ItemDetailActivity.this, "取消了收藏", Toast.LENGTH_SHORT).show();
                    FavoItemDbHelper.removeRecord(db, link);
                    isFavorite = false;
                } else {
                    //加入收藏
                    isFavorite = true;
                    collectBtn.setImageResource(favoIcons[1]);
                    Toast.makeText(ItemDetailActivity.this, "收藏成功!", Toast.LENGTH_SHORT)
                            .show();
                    FavoItemDbHelper
                            .insert(db, title, pubdate, itemDetail,
                                    link, firstImgUrl, sectionTitle, sectionUrl);
                }
                Intent intent = new Intent();
                intent.putExtra("link", link);
                intent.putExtra("is_favorite", isFavorite);
                intent.setAction(ItemListActivity.ACTION_UPDATE_ITEM_LIST);
                sendBroadcast(intent);

                ThreadUtil.runOnAnsy(new Runnable() {
                    @Override
                    public void run() {
                        SeriaHelper helper = SeriaHelper.newInstance();
                        File cache = AppContext.getSectionCache(sectionUrl);
                        ItemListEntity entity = (ItemListEntity) helper
                                .readObject(cache);
                        ArrayList<FeedItem> items = entity.getItemList();
                        for (FeedItem f : items) {
                            if (f.getLink().equals(link))
                                f.setFavorite(isFavorite);
                        }
                        entity.setItemList(items);
                        helper.saveObject(entity, cache);
                    }
                },"Favorite Thread");

            }
        });
        countTv = (TextView) findViewById(R.id.fid_tv_comment_count);
        mWebView = (WebView) findViewById(R.id.my_web_view);
        mWebView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
        mWebView.getSettings().setJavaScriptEnabled(true);
    }


    private void loadData() {
        if (itemDetail!=null) {
            StringBuffer sb = new StringBuffer();
            //过滤style
            itemDetail = itemDetail.replaceAll(HtmlFilter.regexpForStyle, "");
            //过滤img宽和高
            itemDetail = itemDetail.replaceAll("(<img[^>]*?)\\s+width\\s*=\\s*\\S+", "$1");
            itemDetail = itemDetail.replaceAll(
                    "(<img[^>]*?)\\s+height\\s*=\\s*\\S+", "$1");
            //图片双击
            itemDetail = itemDetail.replaceAll("(<img[^>]+src=\")(\\S+)\"",
                    "$1$2\" onClick=\"javascript:mWebViewImageListener.onImageClick('$2')\"");
            mWebView.addJavascriptInterface(this, "mWebViewImageListener");
            //是否加载图片
            SharedPreferences pref = AppContext.getPrefrences(this);
            if (!pref.getBoolean("pref_imageLoad", true)) {
                itemDetail = itemDetail.replaceAll("(<|;)\\s*(IMG|img)\\s+([^;^>]*)\\s*(;|>)", "");
            }
            sb.append("<h1>" + title + "</h1>");
            sb.append("<body>" + itemDetail + "</body>");
            mWebView.loadDataWithBaseURL(null, css + sb.toString(), "text/html", "UTF-8", null);

            speechText = HtmlFilter.filterHtml(title + itemDetail);
        }else {
            ToastUtil.makeShortToast("暂无内容");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMSsoHandler ssoHandler = presenter.mController.getConfig().getSsoHandler(
                requestCode);
        if (ssoHandler != null) {
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
            Log.d("", "#### ssoHandler.authorizeCallBack");
        }
    }

}

