package com.rssreader.app.ui.presenter;

import android.content.Intent;
import android.view.View;

import com.rssreader.app.commons.util.ResourcesUtil;
import com.rssreader.app.ui.R;
import com.rssreader.app.ui.activity.ImageDialog;
import com.rssreader.app.ui.activity.ItemDetailActivity;
import com.rssreader.app.ui.base.BasePresenter;
import com.rssreader.app.ui.common.Constants;
import com.rssreader.app.utils.MD5;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.bean.UMComment;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.TencentWbShareContent;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.TencentWBSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

import java.util.List;

/**
 * Created by LuoChangAn on 16/4/22.
 */
public class ItemDetailPresenter extends BasePresenter<ItemDetailActivity> implements View.OnClickListener{
    private String link;
    private String title;
    private String sectionTitle;
    private String sectionUrl;
    private String pubdate;
    private String itemDetail;
    private String firstImgUrl;
    public UMSocialService mController;


    public ItemDetailPresenter(ItemDetailActivity target) {
        super(target);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initData();
        initComments();
        configPlatforms();
        setShareContent();
    }

    private void initData() {
        Intent intent = target.getIntent();
        sectionTitle = intent.getStringExtra("section_title");
        sectionUrl = intent.getStringExtra("section_url");
        firstImgUrl = intent.getStringExtra("first_img_url");

        title = intent.getStringExtra("title");
        pubdate = intent.getStringExtra("pubdate");
        itemDetail = intent.getStringExtra("item_detail");
        link = intent.getStringExtra("link");
    }


    private void initComments() {
        String key = MD5.Md5(link);
        mController = UMServiceFactory.getUMSocialService(Constants.DESCRIPTOR);
        mController.getComments(target, new SocializeListeners.FetchCommetsListener() {
            @Override
            public void onStart() {
            }

            @Override
            public void onComplete(int status, List<UMComment> comments, SocializeEntity entity) {
                if (status == 200 && comments != null && !comments.isEmpty()) {
                    target.countTv.setText(comments.size() + "");
                }
            }
        }, -1);
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

    //根据不同的平台设置不同的分享内容
    private void setShareContent(){
        // 配置SSO
        mController.getConfig().setSsoHandler(new SinaSsoHandler());
        mController.getConfig().setSsoHandler(new TencentWBSsoHandler());
        //设置qq空间分享内容
        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(target,
                "1105267281", "pWvrIZZ2VSDTBQUk");
        qZoneSsoHandler.addToSocialSDK();

        mController.setShareContent(ResourcesUtil.stringFormat(R.string.share_content_format, title, link));
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

    private void addQQQZonePlatForm() {
        String appId = "1105267281";
        String appKey = "pWvrIZZ2VSDTBQUk";
        // 添加QQ支持, 并且设置QQ分享内容的target url
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(target,
                appId, appKey);
        qqSsoHandler.setTargetUrl("http://www.umeng.com/social");
        qqSsoHandler.addToSocialSDK();

        // 添加QZone平台
        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(target, appId, appKey);
        qZoneSsoHandler.addToSocialSDK();
    }

    private void addWXPlatform() {
        // 注意：在微信授权的时候，必须传递appSecret
        // wx967daebe835fbeac是你在微信开发平台注册应用的AppID, 这里需要替换成你注册的AppID
        String appId = "wxb35d435bd4c3e3ef";
        String appSecret = "0857e218de130d50936218692493e106";
        // 添加微信平台
        UMWXHandler wxHandler = new UMWXHandler(target, appId, appSecret);
        wxHandler.addToSocialSDK();

        // 支持微信朋友圈
        UMWXHandler wxCircleHandler = new UMWXHandler(target, appId, appSecret);
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.addToSocialSDK();
    }

    public void onImageClick(String url) {
        Intent intent = new Intent();
        intent.putExtra("url", url);
        intent.setClass(target, ImageDialog.class);
        target.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fid_btn_share:
                mController.openShare(target, false);
                break;
            case R.id.fid_btn_comment:
                mController.openComment(target, false);
                break;
        }
    }


}
