package com.innobuddy.SmartStudy;

import org.json.JSONException;
import org.json.JSONObject;

import com.innobuddy.SmartStudy.DB.DBHelper;
import com.innobuddy.download.utils.MyIntents;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * A simple {@link Fragment} subclass.
 * 
 */
public class DownloadFragment extends Fragment {
	
	public final static String DOWNLOAD_INFOS = "download_infos";
	public final static String PROGRESS_INFO = "progress_info";
	
	CourseCell3Adapter adapter;
	
    private MyReceiver mReceiver;
    
    JSONObject downloadObject;
	
	public DownloadFragment() {
		// Required empty public constructor
		
	}

	@Override
	public void onDestroy() {
		
		if (adapter.cursor != null) {
			adapter.cursor.close();
			adapter.cursor = null;
		}
		
		SharedPreferences downloadPreferences = getActivity().getSharedPreferences(DOWNLOAD_INFOS, 0);
		SharedPreferences.Editor editor = downloadPreferences.edit();
		editor.putString(PROGRESS_INFO, downloadObject.toString());
		editor.commit();
		
		if (mReceiver != null) {
	        getActivity().unregisterReceiver(mReceiver);
		}
		
		super.onDestroy();
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		
		SharedPreferences downloadPreferences = getActivity().getSharedPreferences(DOWNLOAD_INFOS, Context.MODE_PRIVATE);
		String jsonString = downloadPreferences.getString(PROGRESS_INFO, "");
		
        try {
			
    		if (jsonString == null || jsonString.length() == 0) {
    			downloadObject = new JSONObject();
			} else {
				downloadObject = new JSONObject(jsonString);
			}
        	
		} catch (JSONException e) {
			
		}
		
		if (mReceiver == null) {
	        mReceiver = new MyReceiver();
	        IntentFilter filter = new IntentFilter();
	        filter.addAction("com.innobuddy.download.observe");
	        getActivity().registerReceiver(mReceiver, filter);
		}
		
		View view = inflater.inflate(R.layout.fragment_download, container, false);
		ListView listView = (ListView)view.findViewById(R.id.listView1);
		
		Cursor cursor = DBHelper.getInstance(null).queryDownload();

		adapter = new CourseCell3Adapter(getActivity(), cursor, downloadObject);
		listView.setAdapter(adapter);
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long id) {
								
				adapter.cursor.moveToPosition(position);
				String url = adapter.cursor.getString(adapter.cursor.getColumnIndex("cache_url"));

                JSONObject jsonObject = null;
                try {
                	
                    if (!TextUtils.isEmpty(url)) {
                        jsonObject = downloadObject.getJSONObject(url);
                    }

				} catch (JSONException e) {
				}
                
                if (jsonObject == null) {
                	jsonObject = new JSONObject();
                	try {
                        jsonObject.put(MyIntents.DOWNLOAD_SIZE, 0L);
                        jsonObject.put(MyIntents.TOTAL_SIZE, 0L);
                        jsonObject.put(MyIntents.IS_PAUSED, false);
					} catch (JSONException e) {
					}
				}
                
                try {
                	
                    boolean isPaused = jsonObject.getBoolean(MyIntents.IS_PAUSED);
                    
        			Intent downloadIntent = new Intent("com.innobuddy.download.services.IDownloadService");
                    
                    if (isPaused) {
        				downloadIntent.putExtra(MyIntents.TYPE,
        						MyIntents.Types.CONTINUE);
        				downloadIntent.putExtra(MyIntents.URL, url);
        				getActivity().getApplicationContext().startService(downloadIntent);
                        jsonObject.put(MyIntents.IS_PAUSED, false);
                        jsonObject.put(MyIntents.IS_ERROR, false);
                        
					} else {
						downloadIntent.putExtra(MyIntents.TYPE, MyIntents.Types.PAUSE);
						downloadIntent.putExtra(MyIntents.URL, url);
						getActivity().getApplicationContext().startService(downloadIntent);
                        jsonObject.put(MyIntents.IS_PAUSED, true);
                        jsonObject.put(MyIntents.IS_ERROR, false);

					}
                    
                    adapter.notifyDataSetChanged();
                    
				} catch (Exception e) {
				}
				
			}
			
		});
		
		return view;
	}
	
    public class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            handleIntent(intent);

        }

        private void handleIntent(Intent intent) {

            if (intent != null && intent.getAction().equals("com.innobuddy.download.observe")) {
                int type = intent.getIntExtra(MyIntents.TYPE, -1);
                String url = intent.getStringExtra(MyIntents.URL);

                JSONObject jsonObject = null;
                try {
                	
                    if (!TextUtils.isEmpty(url)) {
                        jsonObject = downloadObject.getJSONObject(url);
                    }
                    
				} catch (JSONException e) {
				}
                
                try {
                    if (jsonObject == null) {
                    	jsonObject = new JSONObject();
                    	try {
                            jsonObject.put(MyIntents.DOWNLOAD_SIZE, 0L);
                            jsonObject.put(MyIntents.TOTAL_SIZE, 0L);
                            jsonObject.put(MyIntents.IS_PAUSED, false);
    					} catch (JSONException e) {
    						// TODO: handle exception
    					}
                        downloadObject.put(url, jsonObject);
    				}
				} catch (Exception e) {
					
				}
                
                
                switch (type) {
                    case MyIntents.Types.ADD:
                        
                        boolean isPaused = intent.getBooleanExtra(MyIntents.IS_PAUSED, false);
                        if (!TextUtils.isEmpty(url)) {
                        	try {
                                jsonObject.put(MyIntents.IS_PAUSED, isPaused);
							} catch (JSONException e) {
								
							}
                        }
                        
                		if (adapter.cursor != null) {
                			adapter.cursor.close();
                			adapter.cursor = null;
                		}

                		adapter.cursor = DBHelper.getInstance(null).queryDownload();
                    	adapter.notifyDataSetChanged();
                        
                        break;
                    case MyIntents.Types.COMPLETE:
                        if (!TextUtils.isEmpty(url)) {
                        	
                        	Cursor cursor = DBHelper.getInstance(null).queryDownload(url);
                        	if (cursor != null) {
                            	DBHelper.getInstance(null).insertOffline(cursor);
                            	cursor.close();
                            	cursor = null;
							}
                        	
                        	DBHelper.getInstance(null).deleteDownload(url);
                        	
                    		if (adapter.cursor != null) {
                    			adapter.cursor.close();
                    			adapter.cursor = null;
                    		}

                    		adapter.cursor = DBHelper.getInstance(null).queryDownload();
                        	
                        	adapter.notifyDataSetChanged();
                        	
                            Intent nofityIntent = new Intent("downloadFinished");
                            getActivity().sendBroadcast(nofityIntent);
                        }
                        break;
                    case MyIntents.Types.PROCESS:
                        long downloadSize = intent.getLongExtra(MyIntents.DOWNLOAD_SIZE, 0);
                        long totalSize = intent.getLongExtra(MyIntents.TOTAL_SIZE, 0);
                		
                        try {
							
                            jsonObject.put(MyIntents.DOWNLOAD_SIZE, downloadSize);
                            jsonObject.put(MyIntents.TOTAL_SIZE, totalSize);
                        	
						} catch (JSONException e) {
						}
                                                
                		adapter.updateStatus(url);
                		
                        break;
                    case MyIntents.Types.ERROR:
                        
                    	try {
                            jsonObject.put(MyIntents.IS_ERROR, true);
						} catch (JSONException e) {
							
						}

                		adapter.updateStatus(url);
                        
                        break;
                    default:
                        break;
                }
            }
        }
        
    }

	

}
