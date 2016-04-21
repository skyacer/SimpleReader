package com.rssreader.app.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.rssreader.app.adapter.CommentAdapter;
import com.rssreader.app.commons.UMHelper;
import com.rssreader.app.ui.R;
import com.rssreader.app.ui.base.BaseActionBarActivity;
import com.rssreader.app.ui.presenter.CommentPresenter;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.bean.UMComment;
import com.umeng.socialize.controller.listener.SocializeListeners.FetchCommetsListener;

import org.apache.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LuoChangAn
 */
public class CommentActivity extends BaseActionBarActivity<CommentPresenter>
{
	private PullToRefreshListView commentLv;
	private ArrayList<UMComment> mComments = new ArrayList<UMComment>();
	private CommentAdapter mAdapter;
	private InputMethodManager inputMethodMgr;
	private EditText inputEt;
	private String oldUsrMsg;
	

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		initView();
		initData();
	}

    @Override
    protected void initPresenter() {
        presenter = new CommentPresenter(this);
    }

    private void initView()
	{
		setRealContentView(R.layout.comment);
        setTitle(R.string.comment_list_title);

		commentLv = (PullToRefreshListView) findViewById(R.id.comment_Lv);
		commentLv.setOnRefreshListener(new OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                refreshComments();
            }
        });
	}

	public void refreshComments()
	{
		UMHelper.getUMSocialService(this).getComments(this,
                new FetchCommetsListener() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onComplete(int status,
                                           List<UMComment> comments, SocializeEntity entity) {
                        if (status == HttpStatus.SC_OK && comments != null) {
                            mComments.addAll(comments);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                }, System.currentTimeMillis());
	}

	private void initData()
	{
//		File file = new File("");
//		UMCommentListEntity entity = (UMCommentListEntity) SeriaHelper
//				.newInstance().readObject(file);
		refreshComments();
		mAdapter = new CommentAdapter(this, mComments);
		commentLv.setAdapter(mAdapter);
	}

    // 必须public
    public void onAddComment(View v)
    {
        ViewStub vs = (ViewStub) findViewById(R.id.comment_menu);
        View view = vs.inflate();

        inputEt = (EditText) view
                .findViewById(R.id.comment_context_et);
        inputEt.setFocusable(true);
        inputEt.requestFocus();
        // call inputmethod widget
        inputMethodMgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodMgr.showSoftInput(inputEt, InputMethodManager.SHOW_FORCED);

        Button btn = (Button) view.findViewById(R.id.comment_send_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = inputEt.getText().toString();
                if (msg.equalsIgnoreCase(oldUsrMsg)) {
                    Toast.makeText(CommentActivity.this, "禁止重复提交", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (msg != null && !msg.isEmpty()) {
                    oldUsrMsg = msg;
                    presenter.sendComment(msg);
                }
            }
        });

    }
}
