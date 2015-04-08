package com.innobuddy.download.services;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;
import android.widget.Toast;

import com.innobuddy.SmartStudy.GlobalParams;
import com.innobuddy.SmartStudy.Md5Utils;
import com.innobuddy.download.utils.ConfigUtils;
import com.innobuddy.download.utils.DStorageUtils;
import com.innobuddy.download.utils.MyIntents;
import com.innobuddy.download.utils.NetworkUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class DownloadService extends Service {

	private final static int SUCCESSCODE = 100;
	private DownloadManager mDownloadManager;
	private HttpUtils http;
	private String url;
	private Integer j;
	private long download = 0;
	private ArrayList<String> list;
	private HttpHandler<File> httpHandler;
	private String target;
	private Handler handler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SUCCESSCODE:
				String result = (String) msg.obj;
				list = m3u8Parser(result, url);
				Message downloadMsg = obtainMessage();
				downloadMsg.what = 101;
				downloadMsg.obj = 0;
				handler.sendMessage(downloadMsg);
				break;
			case 101:
				j = (Integer) msg.obj;
				String tag = target + "/" + list.get(j).substring(list.get(j).lastIndexOf("/") + 1, list.get(j).length());
				httpHandler = http.download(list.get(j), tag, true, false, new RequestCallBack<File>() {

					@Override
					public void onSuccess(ResponseInfo<File> arg0) {
						if (j < list.size() - 1) {
							j = j + 1;
							Message msg = obtainMessage();
							msg.what = 101;
							msg.obj = j;
							handler.sendMessage(msg);
						} else {
							completeTask(url);// 如果下载完成的处理
						}

					}

					@Override
					public void onFailure(HttpException arg0, String arg1) {

						Intent errorIntent = new Intent("com.innobuddy.download.observe");
						errorIntent.putExtra(MyIntents.TYPE, MyIntents.Types.ERROR);
						errorIntent.putExtra(MyIntents.ERROR_CODE, "");
						errorIntent.putExtra(MyIntents.URL, url);
						sendBroadcast(errorIntent);
					}

					public void onLoading(long total, long current, boolean isUploading) {
					
						download = download + total;//
						System.out.println(download);
						int k = (j * 100) / list.size();
						Intent updateIntent = new Intent("com.innobuddy.download.observe");
						updateIntent.putExtra(MyIntents.TYPE, MyIntents.Types.PROCESS);
						updateIntent.putExtra(MyIntents.PROCESS_SPEED, "");
						updateIntent.putExtra(MyIntents.PROCESS_PROGRESS, k + "");
						updateIntent.putExtra("download", download);
						updateIntent.putExtra(MyIntents.DOWNLOAD_SIZE, total * (j - 1) + current);
						updateIntent.putExtra(MyIntents.TOTAL_SIZE, total * list.size());
						updateIntent.putExtra(MyIntents.URL, url);
						sendBroadcast(updateIntent);

					};
				});

				break;

			default:
				break;
			}
		};
	};


	@Override
	public IBinder onBind(Intent intent) {

		return new DownloadServiceImpl();
	}

	@Override
	public void onCreate() {

		super.onCreate();
		mDownloadManager = new DownloadManager(this);

	}

	@SuppressWarnings("deprecation")
	@Override
	public void onStart(Intent intent, int startId) {

		super.onStart(intent, startId);
		http = new HttpUtils();

		if (mDownloadManager == null) {
			mDownloadManager = new DownloadManager(this);
		}
		if (intent != null && intent.getAction() != null) {
			if (intent.getAction().equals("com.innobuddy.download.services.IDownloadService")) {
				int type = intent.getIntExtra(MyIntents.TYPE, -1);
				switch (type) {
				case MyIntents.Types.START:
					if (!mDownloadManager.isRunning()) {
						mDownloadManager.startManage();
					} else {
						mDownloadManager.reBroadcastAddAllTask();
					}
					break;
				case MyIntents.Types.ADD:
					url = intent.getStringExtra(MyIntents.URL);
					if (!TextUtils.isEmpty(url) && !mDownloadManager.hasTask(url)) {
						broadcastAddTask(url);
						target = DStorageUtils.FILE_ROOT + Md5Utils.encode(url) + "/";
						http.send(HttpMethod.GET, url, new RequestCallBack<String>() {

							@Override
							public void onFailure(HttpException arg0, String arg1) {
								// 网络连接失败
								Toast.makeText(DownloadService.this, "网络连接失败", Toast.LENGTH_LONG).show();
							}

							@Override
							public void onSuccess(ResponseInfo<String> responseInfo) {
								Message msg = Message.obtain();
								msg.what = SUCCESSCODE;
								msg.obj = responseInfo.result;
								handler.sendMessage(msg);
							}
						});
					}
					break;
				case MyIntents.Types.CONTINUE:
					url = intent.getStringExtra(MyIntents.URL);
					if (!TextUtils.isEmpty(url)) {
						mDownloadManager.continueTask(url);
					}//
					if (httpHandler != null)
						httpHandler.resume();
					break;
				case MyIntents.Types.DELETE:
					url = intent.getStringExtra(MyIntents.URL);
					if (!TextUtils.isEmpty(url)) {
						mDownloadManager.deleteTask(url);
					}
					if (httpHandler != null)
						httpHandler.cancel();// 取消
					break;
				case MyIntents.Types.PAUSE:
					url = intent.getStringExtra(MyIntents.URL);
					if (!TextUtils.isEmpty(url)) {
						mDownloadManager.pauseTask(url);
					}
					if (httpHandler != null)
						httpHandler.cancel();

					break;
				case MyIntents.Types.STOP:
					mDownloadManager.close();
					// mDownloadManager = null;
					break;

				default:
					break;
				}
			}
		}

	}

	private class DownloadServiceImpl extends IDownloadService.Stub {

		@Override
		public void startManage() throws RemoteException {

			mDownloadManager.startManage();
		}

		@Override
		public void addTask(String url) throws RemoteException {

			mDownloadManager.addTask(url);
		}

		@Override
		public void pauseTask(String url) throws RemoteException {

		}

		@Override
		public void deleteTask(String url) throws RemoteException {

		}

		@Override
		public void continueTask(String url) throws RemoteException {

		}

	}

	/*
	 * @return返回ts_url集合
	 */
	public ArrayList<String> m3u8Parser(String result, String m3u8_url) {
		// 写入本机文件当中

		try {
			File file = new File(DStorageUtils.FILE_ROOT + Md5Utils.encode(url) + "/" + NetworkUtils.getFileNameFromUrl(url));
			GlobalParams.path = Md5Utils.encode(url) + "/";
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter writer = new FileWriter(file);
			writer.write(result);
			writer.flush();
			writer.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();//  
		}

		InputStream in = new ByteArrayInputStream(result.getBytes());
		ArrayList<String> list_ts = new ArrayList<String>();
		try {

			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line = null;
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("#")) {
					// 这里是Metadata信息
					if (line.startsWith("#EXTINF:")) {

					}
				} else if (line.length() > 0) {
					// 这里是一个指向的视频流路径 ,可能是绝对地址，也可能是相对地址
					if (line.startsWith("http")) {// 如果以http开头，一定是绝对地址了
						list_ts.add(line);
					} else {// 不以http开头，是相对地址，需要进行拼接。
						String ts_url;
						String m3u8_postfixname = m3u8_url.substring(m3u8_url.lastIndexOf("/") + 1, m3u8_url.length());
						if (m3u8_postfixname.equals("dest.m3u8")) {
							ts_url = m3u8_url.replace("dest.m3u8", line);
						} else {
							ts_url = m3u8_url.replace("dest.m3u8", line);
						}
						list_ts.add(ts_url);
					}
				}

			}
			in.close();
			return list_ts;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private void broadcastAddTask(String url) {

		broadcastAddTask(url, false);
	}

	private void broadcastAddTask(String url, boolean isInterrupt) {

		Intent nofityIntent = new Intent("com.innobuddy.download.observe");
		nofityIntent.putExtra(MyIntents.TYPE, MyIntents.Types.ADD);
		nofityIntent.putExtra(MyIntents.URL, url);
		nofityIntent.putExtra(MyIntents.IS_PAUSED, isInterrupt);
		this.sendBroadcast(nofityIntent);
	}

	public void completeTask(String url) {

		// if (mDownloadingTasks.contains(task)) {
		// if(TextUtils.isEmpty(url)){
		// ConfigUtils.clearURL(this, );
		// }
		// mDownloadingTasks.remove(task);

		// notify list changed
		// ConfigUtils.clearURL(this,0);
		Intent nofityIntent = new Intent("com.innobuddy.download.observe");
		nofityIntent.putExtra(MyIntents.TYPE, MyIntents.Types.COMPLETE);
		nofityIntent.putExtra(MyIntents.URL, url);
		sendBroadcast(nofityIntent);

	}
}
