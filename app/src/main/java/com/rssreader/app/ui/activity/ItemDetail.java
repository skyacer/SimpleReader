package com.rssreader.app.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.rssreader.app.commons.AppConfig;
import com.rssreader.app.commons.AppContext;
import com.rssreader.app.commons.HtmlFilter;
import com.rssreader.app.commons.SeriaHelper;
import com.rssreader.app.commons.UIHelper;
import com.rssreader.app.commons.util.ResourcesUtil;
import com.rssreader.app.db.DbManager;
import com.rssreader.app.db.FavoItemDbHelper;
import com.rssreader.app.entity.FeedItem;
import com.rssreader.app.entity.ItemListEntity;
import com.rssreader.app.ui.R;
import com.rssreader.app.utils.MD5;
import com.umeng.socialize.bean.RequestType;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.bean.UMComment;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.FetchCommetsListener;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.TencentWbShareContent;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.TencentWBSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@SuppressLint("JavascriptInterface")
@SuppressWarnings("deprecation")
public class ItemDetail extends FragmentActivity {
    private ImageButton collectBtn;
    private ImageButton shareBtn;
    private ImageButton commentBtn;
    private TextView countTv;//评论列表
    private TextView mReadTitleTv;
    private static WebView mWebView;
    private String sectionTitle;
    private String sectionUrl;
    private String title;
    private String pubdate;
    private String itemDetail;
    private String link;
    private String firstImgUrl;
    private UMSocialService mController;
    private boolean isFavorite;//文章是否已收藏
    private String css = UIHelper.WEB_STYLE;
    private int[] favoIcons = {
            R.drawable.btn_favorite_empty,
            R.drawable.btn_favorite_full
    };//0为空


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        loadData();
        initComments();
        configPlatforms();
        setShareContent();
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

    private void initComments() {
        String key = MD5.Md5(link);
        mController = UMServiceFactory.getUMSocialService(AppConfig.UM_BASE_KEY + key,
                RequestType.SOCIAL);
        mController.getComments(this, new FetchCommetsListener() {
            @Override
            public void onStart() {
            }

            @Override
            public void onComplete(int status, List<UMComment> comments, SocializeEntity entity) {
                if (status == 200 && comments != null && !comments.isEmpty()) {
                    countTv.setText(comments.size() + "");
                }
            }
        }, -1);
    }

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
        setContentView(R.layout.feed_item_detail);
        shareBtn = (ImageButton) findViewById(R.id.fid_btn_share);
        shareBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mController.openShare(ItemDetail.this, false);
            }

        });
        commentBtn = (ImageButton) findViewById(R.id.fid_btn_comment);
        commentBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openCommentUi();
            }
        });
        collectBtn = (ImageButton) findViewById(R.id.fid_btn_collecte);
        if (isFavorite)
            collectBtn.setImageResource(R.drawable.btn_favorite_full);
        collectBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DbManager helper = new DbManager(ItemDetail.this, DbManager.DB_NAME, null, 1);
                final SQLiteDatabase db = helper.getWritableDatabase();
                //已收藏，取消收藏
                if (isFavorite) {
                    collectBtn.setImageResource(favoIcons[0]);
                    Toast.makeText(ItemDetail.this, "取消了收藏", Toast.LENGTH_SHORT).show();
                    FavoItemDbHelper.removeRecord(db, link);
                    isFavorite = false;
                } else {
                    //加入收藏
                    isFavorite = true;
                    collectBtn.setImageResource(favoIcons[1]);
                    Toast.makeText(ItemDetail.this, "收藏成功!", Toast.LENGTH_SHORT)
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

                new Thread() {
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
                }.start();
            }
        });
        //回退箭头
        findViewById(R.id.read_return_btn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mReadTitleTv = (TextView) findViewById(R.id.read_title_tv);
        mReadTitleTv.setText(sectionTitle);
        countTv = (TextView) findViewById(R.id.fid_tv_comment_count);
        mWebView = (WebView) findViewById(R.id.my_web_view);
        mWebView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
        mWebView.getSettings().setJavaScriptEnabled(true);
    }

    private void loadData() {
        StringBuffer sb = new StringBuffer();
        //过滤style
        itemDetail = itemDetail.replaceAll(HtmlFilter.regexpForStyle, "");
        //过滤img宽和高
        itemDetail = itemDetail.replaceAll("(<img[^>]*?)\\s+width\\s*=\\s*\\S+", "$1");
        itemDetail = itemDetail.replaceAll(
                "(<img[^>]*?)\\s+height\\s*=\\s*\\S+", "$1");
//		//图片双击
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
    }

    public void onImageClick(String url) {
        Intent intent = new Intent();
        intent.putExtra("url", url);
        intent.setClass(this, ImageDialog.class);
        startActivity(intent);
    }

    private void openCommentUi() {
        Intent intent = new Intent();
        intent.setClass(ItemDetail.this, CommentActivity.class);
        ItemDetail.this.startActivity(intent);
    }

    private void configPlatforms() {
        //添加新浪SSO授权
        mController.getConfig().setSsoHandler(new SinaSsoHandler());
        //添加腾讯微博SSO授权
        mController.getConfig().setSsoHandler(new TencentWBSsoHandler());
        //添加QQ Qzon平台
        addQQQZonePlatForm();

        //添加微信,朋友圈平台
        addWXPlatform();
    }

    private void addQQQZonePlatForm() {
        String appId = "1105267281";
        String appKey = "pWvrIZZ2VSDTBQUk";
        // 添加QQ支持, 并且设置QQ分享内容的target url
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(this,
                appId, appKey);
        qqSsoHandler.setTargetUrl("http://www.umeng.com/social");
        qqSsoHandler.addToSocialSDK();

        // 添加QZone平台
        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(this, appId, appKey);
        qZoneSsoHandler.addToSocialSDK();
    }

    private void addWXPlatform() {
        // 注意：在微信授权的时候，必须传递appSecret
        // wx967daebe835fbeac是你在微信开发平台注册应用的AppID, 这里需要替换成你注册的AppID
        String appId = "wxb35d435bd4c3e3ef";
        String appSecret = "0857e218de130d50936218692493e106";
        // 添加微信平台
        UMWXHandler wxHandler = new UMWXHandler(this, appId, appSecret);
        wxHandler.addToSocialSDK();

        // 支持微信朋友圈
        UMWXHandler wxCircleHandler = new UMWXHandler(this, appId, appSecret);
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.addToSocialSDK();
    }

    //根据不同的平台设置不同的分享内容
    private void setShareContent(){
        // 配置SSO
        mController.getConfig().setSsoHandler(new SinaSsoHandler());
        mController.getConfig().setSsoHandler(new TencentWBSsoHandler());
        //设置qq空间分享内容
        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(this,
                "1105267281", "pWvrIZZ2VSDTBQUk");
        qZoneSsoHandler.addToSocialSDK();

        mController.setShareContent(ResourcesUtil.stringFormat(R.string.share_content_format,title,link));
        //设置微信分享内容
        WeiXinShareContent weixinContent = new WeiXinShareContent();
        weixinContent.setShareContent(ResourcesUtil.stringFormat(R.string.share_content_format,title,link));
        mController.setShareMedia(weixinContent);

        // 设置朋友圈分享的内容
        CircleShareContent circleMedia = new CircleShareContent();
        circleMedia.setShareContent(ResourcesUtil.stringFormat(R.string.share_content_format,title,link));
        mController.setShareMedia(circleMedia);

        // 设置QQ空间分享内容
        QZoneShareContent qzone = new QZoneShareContent();
        qzone.setShareContent(ResourcesUtil.stringFormat(R.string.share_content_format,title,link));
        qzone.setTargetUrl(link);
        qzone.setTitle("易悦阅读器分享");
        mController.setShareMedia(qzone);

        //设置QQ分享内容
        TencentWbShareContent tencent = new TencentWbShareContent();
        tencent.setShareContent(ResourcesUtil.stringFormat(R.string.share_content_format,title,link));
        // 设置tencent分享内容
        mController.setShareMedia(tencent);
    }

}

