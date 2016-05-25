package com.rssreader.app.module.presenter;

import com.rssreader.app.commons.UMHelper;
import com.rssreader.app.commons.util.ResourcesUtil;
import com.rssreader.app.commons.util.ToastUtil;
import com.rssreader.app.module.R;
import com.rssreader.app.module.activity.CommentActivity;
import com.rssreader.app.module.base.BasePresenter;
import com.umeng.socialize.bean.MultiStatus;
import com.umeng.socialize.bean.SocializeConfig;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.bean.UMComment;
import com.umeng.socialize.controller.listener.SocializeListeners;

import org.apache.http.HttpStatus;

/**
 * Created by LuoChangAn on 16/4/20.
 */
public class CommentPresenter extends BasePresenter<CommentActivity> {
    public CommentPresenter(CommentActivity target) {
        super(target);
    }



    public void sendComment(String msg)
    {
        UMComment comment = new UMComment();
        comment.mText = msg;
        UMHelper.getUMSocialService(target).postComment(target, comment,
                new SocializeListeners.MulStatusListener() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onComplete(MultiStatus multiStatus, int status,
                                           SocializeEntity entity) {
                        if (status == HttpStatus.SC_OK) {
                            ToastUtil.makeShortToast(ResourcesUtil.getString(R.string.publish_success));
                            target.refreshComments();
                        }else {
                            ToastUtil.makeShortToast(ResourcesUtil.getString(R.string.publish_failed));
                        }
                    }
                }, SocializeConfig.getSelectedPlatfrom());
    }
}
