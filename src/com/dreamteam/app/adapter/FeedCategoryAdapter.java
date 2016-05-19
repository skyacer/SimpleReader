package com.rssreader.app.module.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rssreader.app.dao.FeedCategoryDao;
import com.rssreader.app.entity.FeedCategory;
import com.rssreader.app.module.R;

/**
 * @description 
 * @author LuoChangAn
 * @date 2013年11月1日
 */
public class FeedCategoryAdapter extends BaseAdapter
{
	private LayoutInflater inflater;
	private ArrayList<FeedCategory> fcList;
	
	public FeedCategoryAdapter(Context context, ArrayList<FeedCategory> fcList)
	{
		this.fcList = fcList;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}	
	
	@Override
	public int getCount()
	{
		return fcList.size();
	}

	@Override
	public Object getItem(int position)
	{
		return fcList.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder viewHolder;
		
		if(convertView == null)
		{	convertView = inflater.inflate(R.layout.category_item, null);
			viewHolder = new ViewHolder();
			viewHolder.categoryTitle = (TextView) convertView.findViewById(R.id.category_title);
			convertView.setTag(viewHolder);
		}
		else
		{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.categoryTitle.setText(fcList.get(position).getName());
		return convertView;
	}
	
	private final static class ViewHolder
	{
		TextView categoryTitle;
	}
}
