package com.innobuddy.SmartStudy;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.innobuddy.download.utils.DStorageUtils;
import com.innobuddy.download.utils.MyIntents;


import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CourseCell3Adapter extends BaseAdapter {

	LayoutInflater _inflater;
	Context _context;
	Cursor cursor;
	JSONObject downloadObject;
	HashMap<String, CourseCell3Holder> hashMap;

	  public CourseCell3Adapter(Context context, Cursor c, JSONObject d) {
		  _inflater = LayoutInflater.from(context);
		  _context = context;
		  cursor = c;
		  downloadObject = d;
		  hashMap = new HashMap<String, CourseCell3Adapter.CourseCell3Holder>();
	  }
	
	@Override
	public int getCount() {
		if (cursor != null) {
			return cursor.getCount();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		CourseCell3Holder holder;
		
		  if(convertView == null)
		    {
			  
		      convertView = _inflater.inflate(R.layout.course_cell3, null);
		      
		      holder = new CourseCell3Holder();
		      holder.nameTextView = (TextView) convertView.findViewById(R.id.textView1);
		      holder.progressTextView = (TextView) convertView.findViewById(R.id.textView2);
		      holder.totalSizeTextView = (TextView) convertView.findViewById(R.id.textView3);
		      holder.imageView = (ImageView) convertView.findViewById(R.id.imageView1);
		      holder.statusTextView = (TextView) convertView.findViewById(R.id.textView4);
		      holder.statusImageView = (ImageView) convertView.findViewById(R.id.imageView2);
		      convertView.setTag(holder);

		    }
		  else {
		      holder = (CourseCell3Holder)convertView.getTag();
		  }
		  
		  if (cursor != null) {
			  
			  cursor.moveToPosition(position);
			  ImageLoader.getInstance().displayImage(cursor.getString(cursor.getColumnIndex("poster")), holder.imageView, MainActivity.options);
			  holder.nameTextView.setText(cursor.getString(cursor.getColumnIndex("name")));
			  
			  String url = cursor.getString(cursor.getColumnIndex("cache_url"));
			  hashMap.put(url, holder);
			  
			  updateStatus(url);
			  
		}
		  
		return convertView;
	}
	
	public void updateStatus(String url) {
		if (url != null) {
			
			CourseCell3Holder holder = hashMap.get(url);
			
			if (holder == null) {
				return;
			}
			
			  long downloadSize = 0;
			  long totalSize = 0;
			  int status = 0;
			  
			  try {

				  if (downloadObject != null) {
					  
					  JSONObject jsonObject = downloadObject.getJSONObject(url);

					  if (jsonObject != null) {
						  downloadSize = jsonObject.getLong(MyIntents.DOWNLOAD_SIZE);
						  totalSize = jsonObject.getLong(MyIntents.TOTAL_SIZE);
						  status = jsonObject.getInt(MyIntents.DOWNLOAD_STATUS);
					}
					  
				  }
				  				  
			} catch (JSONException e) {
				e.printStackTrace();
			}
			  
			  if (totalSize > 0) {
				  holder.progressTextView.setText((int)Math.ceil(100.0 * downloadSize / totalSize) + "%");
				  holder.totalSizeTextView.setText(DStorageUtils.size(totalSize));
			  } else {
				  holder.progressTextView.setText("0%");				  
			  }
			  
			  if (status == MyIntents.Status.WAITING) {
//				  holder.statusTextView.setText("等待中");
//				  holder.statusImageView.setImageResource(R.drawable.download_waiting);
				  holder.statusTextView.setText("下载中");
				  holder.statusImageView.setImageResource(R.drawable.download_downloading);
			  } else if (status == MyIntents.Status.DOWNLOADING) {
				  holder.statusTextView.setText("下载中");
				  holder.statusImageView.setImageResource(R.drawable.download_downloading);
			  } else if (status == MyIntents.Status.PAUSE) {
				  holder.statusTextView.setText("暂停");
				  holder.statusImageView.setImageResource(R.drawable.download_paused);
			  } else if (status == MyIntents.Status.ERROR) {
				  holder.statusTextView.setText("失败");
				  holder.statusImageView.setImageResource(R.drawable.download_error);
			  }
			  			  
		}
	}

	  private class CourseCell3Holder
	  {
		    TextView nameTextView;
		    TextView progressTextView;
		    TextView totalSizeTextView;
		    TextView statusTextView;
		    ImageView statusImageView;
		    ImageView imageView;
	  }
	
}
