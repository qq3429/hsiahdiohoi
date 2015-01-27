package com.innobuddy.SmartStudy;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import com.innobuddy.SmartStudy.R;
import com.innobuddy.SmartStudy.DB.DBHelper;
import com.innobuddy.SmartStudy.DownloadFragment.MyReceiver;
import com.innobuddy.SmartStudy.Fragment1.OnBackListener;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.innobuddy.download.utils.DStorageUtils;
import com.innobuddy.download.utils.MyIntents;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
//import android.support.v13.app.FragmentTabHost;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost.TabSpec;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.text.TextUtils;

public class MainActivity extends FragmentActivity implements OnBackListener {
    	
	FragmentTabHost mTabHost;
	RadioGroup mTabRg;
			
	private static final Class<?>[] fragments = {Fragment1.class, Fragment2.class, Fragment3.class, Fragment4.class};

	public static final DisplayImageOptions options = new DisplayImageOptions.Builder()  
    .showImageOnLoading(R.drawable.course_default) //设置图片在下载期间显示的图片  
    .showImageForEmptyUri(R.drawable.course_default)//设置图片Uri为空或是错误的时候显示的图片  
   .showImageOnFail(R.drawable.course_default)  //设置图片加载/解码过程中错误时候显示的图片
   .cacheInMemory(true)//设置下载的图片是否缓存在内存中  
   .cacheOnDisk(true)//设置下载的图片是否缓存在SD卡中  
   .considerExifParams(true)  //是否考虑JPEG图像EXIF参数（旋转，翻转）
   //.delayBeforeLoading(int delayInMillis)//int delayInMillis为你设置的下载前的延迟时间
   //设置图片加入缓存前，对bitmap进行设置  
   //.preProcessor(BitmapProcessor preProcessor)  
   .resetViewBeforeLoading(true)//设置图片在下载前是否重置，复位  
//   .displayer(new RoundedBitmapDisplayer(20))//是否设置为圆角，弧度为多少  
//   .displayer(new FadeInBitmapDisplayer(100))//是否图片加载好后渐入的动画时间  
   .build();//构建完成
	
	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
	 * derivative, which will keep every loaded fragment in memory. If this
	 * becomes too memory intensive, it may be best to switch to a
	 * {@link android.support.v13.app.FragmentStatePagerAdapter}.
	 */

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        File cacheDir = StorageUtils.getOwnCacheDirectory(getApplicationContext(), "ImageLoader/Cache"); 
        
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
        .memoryCacheExtraOptions(720, 1280) // default = device screen dimensions
        .diskCacheExtraOptions(720, 1280, null)
        .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
        .memoryCacheSize(2 * 1024 * 1024)
        .memoryCacheSizePercentage(13) // default
        .diskCache(new UnlimitedDiscCache(cacheDir)) // default
        .diskCacheSize(50 * 1024 * 1024)
        .diskCacheFileCount(100)
        .diskCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
        .imageDownloader(new BaseImageDownloader(getApplicationContext())) // default
        .defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
        .build();
        ImageLoader.getInstance().init(config);

        setContentView(R.layout.activity_main);
		initView();
		
		DBHelper.getInstance(getApplicationContext());
		
        if (!DStorageUtils.isSDCardPresent()) {
            Toast.makeText(this, "未发现SD卡", Toast.LENGTH_LONG).show();
            return;
        }

        if (!DStorageUtils.isSdCardWrittenable()) {
            Toast.makeText(this, "SD卡不能读写", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            DStorageUtils.mkdir();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        Intent downloadIntent = new Intent("com.innobuddy.download.services.IDownloadService");
        downloadIntent.putExtra(MyIntents.TYPE, MyIntents.Types.START);
        getApplicationContext().startService(downloadIntent);
		
    }
    
	private void initView() {
		mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
		// 得到fragment的个数
		int count = fragments.length;
		for (int i = 0; i < count; i++) {
			// 为每一个Tab按钮设置图标、文字和内容
			TabSpec tabSpec = mTabHost.newTabSpec(i + "").setIndicator(i + "");
			// 将Tab按钮添加进Tab选项卡中
			mTabHost.addTab(tabSpec, fragments[i], null);
		}

		mTabRg = (RadioGroup) findViewById(R.id.tab_rg_menu);
		mTabRg.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.tab_rb_1:
					mTabHost.setCurrentTab(0);
					break;
				case R.id.tab_rb_2:
					mTabHost.setCurrentTab(1);

					break;
				case R.id.tab_rb_3:

					mTabHost.setCurrentTab(2);
					break;
				case R.id.tab_rb_4:

					mTabHost.setCurrentTab(3);
					break;

				default:
					break;
				}
			}
		});

		mTabHost.setCurrentTab(0);
	}
 
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

	@Override
	public void onBackPressed() {
	}
	
	@Override
	public void backEvent() {
		Toast.makeText(this, "back", Toast.LENGTH_SHORT).show();
	}
	
	
	
}
