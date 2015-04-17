package com.example.qianlong.base;

import com.example.qianlong.MainActivity;
import com.example.qianlong.R;
import com.example.qianlong.utils.CommonUtil;
import com.example.qianlong.utils.CustomToast;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * @author LiuJie
 *
 */
public abstract class BaseFragment extends Fragment implements OnClickListener  {
	
	protected Context ct;     
	protected SlidingMenu sm;
	public View rootView;
	
	protected Button leftBtn;
	protected ImageButton rightBtn;
	protected ImageButton leftImgBtn;
	protected ImageButton rightImgBtn;
	protected TextView titleTv;
	@ViewInject(R.id.loading_view)
	protected View loadingView;
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		
		super.onActivityCreated(savedInstanceState);
	    sm = ((MainActivity)getActivity()).getSlidingMenu();
		initData(savedInstanceState);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		ct = getActivity();
	}
	
	/**
	 * @param view
	 */
	protected void initTitleBar(View view) {
		leftBtn = (Button) view.findViewById(R.id.btn_left);
		rightBtn = (ImageButton) view.findViewById(R.id.btn_right);
		leftImgBtn = (ImageButton) view.findViewById(R.id.imgbtn_left);
		rightImgBtn = (ImageButton) view.findViewById(R.id.imgbtn_right);
		leftImgBtn.setImageResource(R.drawable.img_menu);
		titleTv = (TextView) view.findViewById(R.id.txt_title);
		leftBtn.setVisibility(View.GONE);
		rightBtn.setVisibility(View.GONE);
		if(leftImgBtn!=null)
		leftImgBtn.setOnClickListener(this);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView  = initView(inflater);
		loadingView = rootView.findViewById(R.id.loading_view);
		return rootView;
	}
	public View getRootView(){
		return rootView;
	}
	
	public void dismissLoadingView() {
		if (loadingView != null)
			loadingView.setVisibility(View.INVISIBLE);
	}
	
	protected void loadData(HttpRequest.HttpMethod method, String url,
			RequestParams params, RequestCallBack<String> callback) {
		HttpUtils http = new HttpUtils();
		http.configCurrentHttpCacheExpiry(1000 * 1);
		LogUtils.allowD = true;
		if (params != null) {
			if (params.getQueryStringParams() != null)
				LogUtils.d(url + params.getQueryStringParams().toString());
		} else {
			params = new RequestParams();
		}
		//设备ID
//		params.addHeader("x-deviceid", app.deviceId);
		//渠道，统计用
//		params.addHeader("x-channel", app.channel);
		if (0 == CommonUtil.isNetworkAvailable(ct)) {
			showToast("无网络，请检查网络连接！");
			http.send(method, url, params, callback);
		} else {
			http.send(method, url, params, callback);
		}
	}
	/**
	 * @param msg
	 */
	public void showToast(String msg) {
		showToast(msg, 0);
	}
	public void showToast(String msg, int time) {
		CustomToast customToast = new CustomToast(ct, msg, time);
		customToast.show();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imgbtn_left:
			Handler handler  = new Handler();
			handler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					sm.toggle();
					
				}
			}, 100);
		
			break;

		default:
			break;
		}
		
	}
	
	protected abstract View initView(LayoutInflater inflater);

	protected abstract void initData(Bundle savedInstanceState);

	protected abstract void processClick(View v);
}
