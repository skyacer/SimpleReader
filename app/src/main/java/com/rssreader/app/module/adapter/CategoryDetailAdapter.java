package com.rssreader.app.module.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rssreader.app.commons.DatabaseHelper;
import com.rssreader.app.commons.util.ThreadUtil;
import com.rssreader.app.db.DbManager;
import com.rssreader.app.db.FeedDBManager;
import com.rssreader.app.entity.Feed;
import com.rssreader.app.module.R;
import com.rssreader.app.module.activity.MainActivity;

import java.util.ArrayList;

/**
 * @author LuoChangAn
 */
public class CategoryDetailAdapter extends BaseAdapter
{
	public static final String tag = "CategoryDetailAdapter";
	private LayoutInflater inflater;
	private Context context;
	private ArrayList<Feed> feeds;
	private String tableName;//所分类对应的表名
    private int mRightWidth;
	public static final String SECTION_TABLE_NAME = "section";
	private int[] imgIds = {
			R.drawable.add,
			R.drawable.added
	};

    /**
     * 单击事件监听器
     */
    private onRightItemClickListener mListener = null;
	
	
	public CategoryDetailAdapter(Context context, ArrayList<Feed> feeds, String tableName,int rightWidth)
	{
		this.context = context;
		this.feeds = feeds;
		this.tableName = tableName;
        this.mRightWidth = rightWidth;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public void updateData(ArrayList<Feed> feeds)
	{
		this.feeds = feeds;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount()
	{
		return feeds.size();
	}

	@Override
	public Object getItem(int position)
	{
		return feeds.get(position);
	}

	@Override
	public long getItemId(int id)
	{
		return id;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent)
	{
		final ViewHolder holder;

		if(convertView == null)
		{
			convertView = inflater.inflate(R.layout.category_detail_item, null);
			holder = new ViewHolder();

            holder.feedTitle = (TextView) convertView.findViewById(R.id.category_detail_feed_title);
			holder.addBtn = (ImageButton) convertView.findViewById(R.id.category_detail_add);
            holder.item_left = (RelativeLayout) convertView.findViewById(R.id.category_left_rv);
            holder.item_right = (RelativeLayout)convertView.findViewById(R.id.category_right_rv);

            holder.item_right_txt = (TextView) convertView.findViewById(R.id.item_right_txt);
            convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) convertView.getTag();
		}
        LinearLayout.LayoutParams lp1 = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        holder.item_left.setLayoutParams(lp1);

        LinearLayout.LayoutParams lp2 = new LayoutParams(mRightWidth, LayoutParams.MATCH_PARENT);
        holder.item_right.setLayoutParams(lp2);

        holder.item_right.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteFeed(holder.feedTitle);
                feeds.remove(position);
                updateData(feeds);
                deleteAnimator();
            }
        }

     );


		holder.addBtn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Feed feed = feeds.get(position);
				String title = feed.getTitle();
				String url = feed.getUrl();
				Intent intent = new Intent();
				int state = 0;//初始未选中
				DbManager mgr = new DbManager(context, DbManager.DB_NAME, null, 1);

				
				//已经选中，取消选中状态
				if(feed.isSelected())
				{
					//改变传入feeds
					feed.setSelectStatus(state);
					holder.addBtn.setImageResource(imgIds[0]);
					//更新主界面
					intent.putExtra("url", feed.getUrl());
					intent.setAction(MainActivity.ACTION_DELETE_SECTION);
					context.sendBroadcast(intent);
					//删除section表中记录的数据
					DatabaseHelper.removeRecord(mgr.getWritableDatabase(), url);
					//更新feed.db中所对应表的状态为0
					new FeedDBManager(context, FeedDBManager.DB_NAME, null, 1)
								.updateState(tableName, state, url);
					return;
				}
				//否则，选中状态
				state = 1;
				feed.setSelectStatus(state);
				holder.addBtn.setImageResource(imgIds[1]);
				//更新主界面
				intent.setAction(MainActivity.ACTION_ADD_SECTION);
				context.sendBroadcast(intent);
				//加入section表
				SQLiteDatabase db = mgr.getWritableDatabase();
				DatabaseHelper.insertToSection(db, tableName, title, url);
				db.close();
				//更新feed.db中所对应表的状态为1
				FeedDBManager feedHelper = new FeedDBManager(context, FeedDBManager.DB_NAME, null, 1);
				feedHelper.updateState(tableName,state, url);
			}
		});
		Feed feed = feeds.get(position);
		holder.feedTitle.setText((CharSequence)
                feed.getTitle());
		//addBtn状态图标设置
		holder.addBtn.setImageResource(imgIds[feed.getSelectStatus()]);
        //每次删除后恢复默认x值
        convertView.setScrollX(0);

        return convertView;
	}

    private void deleteAnimator() {

    }

    private void deleteFeed(final TextView feedTitle) {
        ThreadUtil.runOnAnsy(new Runnable() {
            @Override
            public void run() {
                String title = feedTitle.getText().toString();
                DbManager mgr = new DbManager(context, DbManager.FEED_DB_NAME, null, 1);
                SQLiteDatabase db = mgr.getWritableDatabase();
                DatabaseHelper.removeRecordFromFeed(db, title);
            }
        },"Delete Feed");
    }

    private static final class ViewHolder
	{
        RelativeLayout item_left;
        RelativeLayout item_right;

        TextView feedTitle;
		ImageButton addBtn;

        TextView item_right_txt;
    }



    public void setOnRightItemClickListener(onRightItemClickListener listener){
        mListener = listener;
    }

    public interface onRightItemClickListener {
        void onRightItemClick(View v, int position);
    }
	
}
