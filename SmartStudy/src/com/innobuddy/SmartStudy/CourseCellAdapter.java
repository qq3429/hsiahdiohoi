package com.innobuddy.SmartStudy;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.innobuddy.SmartStudy.Video.VideoPlayerActivity;
import com.nostra13.universalimageloader.core.ImageLoader;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CourseCellAdapter extends BaseAdapter {
	LayoutInflater _inflater;
	ArrayList<ArrayList<JSONObject>> _list;
	Context _context;
		
	  public CourseCellAdapter(Context context, ArrayList<ArrayList<JSONObject>> list) {
		  _inflater = LayoutInflater.from(context);
		  _list = list;
		  _context = context;
		  }
	
	@Override
	public int getCount() {
		return _list.size();
	}

	@Override
	public Object getItem(int position) {
		return _list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@SuppressLint("InflateParams") 
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		CourseCellHolder holder;
	    
	    if(convertView == null)
	    {
	      convertView = _inflater.inflate(R.layout.course_cell1, null);
	      holder = new CourseCellHolder();
	      holder.textView1 = (TextView) convertView.findViewById(R.id.textView1);
	      holder.imageView1 = (ImageView) convertView.findViewById(R.id.imageView1);
	      holder.textView2 = (TextView) convertView.findViewById(R.id.textView2);
	      holder.imageView2 = (ImageView) convertView.findViewById(R.id.imageView2);
	      convertView.setTag(holder);
	    }
	    else
	    {
	      holder = (CourseCellHolder) convertView.getTag();
	    }
	
	    ArrayList<JSONObject> arrayList = _list.get(position);
	    
	    JSONObject object1 = arrayList.get(0);
	    JSONObject object2 = arrayList.get(1);
	    
	    holder.imageView1.setTag(object1);
	    holder.imageView2.setTag(object2);

	    try {
	    	
//	    	holder.textView1.setText(object1.getString("name"));
			ImageLoader.getInstance().displayImage(object1.getString("poster"), holder.imageView1, MainActivity.options);
//	    	holder.textView2.setText(object2.getString("name"));
		    ImageLoader.getInstance().displayImage(object2.getString("poster"), holder.imageView2, MainActivity.options);

		} catch (JSONException e) {
			e.printStackTrace();
		}
	    	    
	    holder.imageView1.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("json", v.getTag().toString());
				intent.setClass(_context, VideoPlayerActivity.class);
				_context.startActivity(intent);
			}
			
		});
	    
	    holder.imageView2.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("json", v.getTag().toString());
				intent.setClass(_context, VideoPlayerActivity.class);
				_context.startActivity(intent);
			}
			
		});
	    
		return convertView;
	}
	
	  private class CourseCellHolder
	  {
	    TextView textView1;
	    ImageView imageView1;
	    TextView textView2;
	    ImageView imageView2;
	  }

}
