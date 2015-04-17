/**
 * 
 */
package com.example.qianlong;

import java.util.ArrayList;
import java.util.HashSet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.example.qianlong.base.BaseActivity;
import com.example.qianlong.base.BaseFragment;
import com.example.qianlong.base.BasePage;
import com.example.qianlong.bean.AtlasListBean;
import com.example.qianlong.bean.CountList;
import com.example.qianlong.bean.AtlasListBean.Atlas;
import com.example.qianlong.bean.NewsListBean.News;
import com.example.qianlong.utils.CommonUtil;
import com.example.qianlong.utils.Constants;
import com.example.qianlong.utils.QLApi;
import com.example.qianlong.utils.QLParser;
import com.example.qianlong.utils.SharePrefUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.qianlong.android.adapter.AtlasAdapter;
import com.qianlong.android.view.pullrefreshview.PullToRefreshBase;
import com.qianlong.android.view.pullrefreshview.PullToRefreshBase.OnRefreshListener;
import com.qianlong.android.view.pullrefreshview.PullToRefreshListView;

/**
 * @author LiuJie
 * 组图列表
 */

public class AtlasItemPage extends BaseFragment {
	
	private String TAG="AtlasItemPage";
	
	private String url;
	private String  hasReadIds;
	private String moreUrl;
	private String countCommentUrl;
	@ViewInject(R.id.lv_item_atlas)
	private PullToRefreshListView lv_atlas;
	private AtlasAdapter adapter;
	private HashSet<String> readSet = new HashSet<String>();
	private ArrayList<Atlas> atlas=new ArrayList<AtlasListBean.Atlas>();
	
	public boolean isLoadSuccess;


	/* (non-Javadoc)
	 * @see com.example.qianlong.base.BasePage#initView(android.view.LayoutInflater)
	 */
//	@Override
//	protected void initView() {
//
//	}

	/* (non-Javadoc)
	 * @see com.example.qianlong.base.BasePage#initData()
	 */
//	@Override
//	public void initData() {
//
//	}
     
	/**
	 * @param loadUrl
	 * @param isRefresh
	 * 网络请求任务
	 */
	private void getAtlasList(final String loadUrl, final boolean isRefresh){
		LogUtils.i("============组图  list URL==================="+loadUrl);
		loadData(HttpMethod.GET, loadUrl, null, new RequestCallBack<String>() {
			
			@Override
			public void onSuccess(ResponseInfo<String> info) {
				LogUtils.d("AtlasList response_json---" + info.result);		
				if(isRefresh){
					SharePrefUtil.saveString(ct, url, info.result);
				}
				processData(isRefresh, info.result);
			}
			
			@Override
			public void onFailure(HttpException error, String msg) {
				LogUtils.d("AtlasList fail_json---" + msg);		
				onLoaded();
			}
		});
	}
	
	/**
	 * 网络加载失败，加载完毕
	 */
	private void onLoaded() {
		dismissLoadingView();
		lv_atlas.onPullDownRefreshComplete();
		lv_atlas.onPullUpRefreshComplete();
	}
    
	/**
	 * @param isRefresh
	 * @param result
	 * 适配数据，展示view
	 */
	public void processData(final boolean isRefresh, String result) {
		AtlasListBean atlasList=QLParser.parse(result, AtlasListBean.class);
		if (atlasList.retcode!=200) {
		} else {
			isLoadSuccess = true;
			countCommentUrl=atlasList.data.countcommenturl;
			moreUrl=atlasList.data.more;
			System.out.println("Atlas moreUrl="+moreUrl.toString());
			if (atlasList.data.news!=null) {
				getAtlasCommentCount(countCommentUrl, atlasList.data.news, isRefresh);
			} 
		}
	}
	
	/**获取评论数，最终生成新闻列表
	 * @param countcommenturl
	 * @param atlasList
	 * @param isRefresh
	 */
	public void getAtlasCommentCount(String countcommenturl,
			final ArrayList<Atlas> atlasList, final boolean isRefresh){
		StringBuffer sBuffer=new StringBuffer(countcommenturl);
		for (Atlas atlas : atlasList) {
			sBuffer.append(atlas.id+",");
		}
		//获取请求评论数
		loadData(HttpMethod.GET, sBuffer.toString(), null, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> info) {
				LogUtils.d("AtlasComment response_json---" + info.result);
				CountList countList = QLParser.parse(info.result,
						CountList.class);
				for (Atlas atlas : atlasList) {
					atlas.commentcount=countList.data.get(atlas.id+"");
					if (readSet.contains(atlas.id)) {
						atlas.isRead=true;
					} else {
                         atlas.isRead=false;
					}
				}
				
				
				if (isRefresh) {
					if (atlasList!=null) {
						atlas.clear();
						atlas.addAll(atlasList);
					} 
				}else {
					   atlas.addAll(atlasList);
				}
				//设置适配器
				if (adapter==null) {
					adapter=new AtlasAdapter(ct, atlas, 0);
					lv_atlas.getRefreshableView().setAdapter(adapter);
				} else {
	                adapter.notifyDataSetChanged();
				}
				//加载完毕
				onLoaded();
				
				if (TextUtils.isEmpty(moreUrl)) {
				    lv_atlas.setHasMoreData(false);
				} else {
					lv_atlas.setHasMoreData(true);
				}
				
				setLastUpdateTime();
				
			}
			
			@Override
			public void onFailure(HttpException error, String msg) {
				LogUtils.d("AtlasComment  fail_json---" + msg);
				onLoaded();
			}
		});
		
		
	}
	
	/* (non-Javadoc)
	 * @see com.example.qianlong.base.BasePage#processClick(android.view.View)
	 */
	@Override
	protected void processClick(View v) {
		// TODO Auto-generated method stub

	}
  
	
	private void setLastUpdateTime() {
		String text = CommonUtil.getStringDate();
		lv_atlas.setLastUpdatedLabel(text);
	}

	/* (non-Javadoc)
	 * @see com.example.qianlong.base.BaseFragment#initView(android.view.LayoutInflater)
	 */
	@Override
	protected View initView(LayoutInflater inflater) {
		View view = inflater.inflate(R.layout.frag_item_atlas, null);
  //      setContentView(R.layout.frag_item_atlas);
		ViewUtils.inject(this, view);
		initTitleBar(view);
		url=QLApi.ATLAS_LISL_URL;
		lv_atlas.setPullLoadEnabled(false);
		lv_atlas.setScrollLoadEnabled(true);
		
		//监听
		lv_atlas.getRefreshableView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(ct, NewsDetailActivity.class);
				String url = "";
				String title;
				Atlas newsItem;
				if (lv_atlas.getRefreshableView().getHeaderViewsCount() > 0) {
					newsItem = atlas.get(position - 1);
				} else {
					newsItem = atlas.get(position);
				}
				url = newsItem.url;
				if(!newsItem.isRead){
					readSet.add(newsItem.id);
					newsItem.isRead= true;
					SharePrefUtil.saveString(ct, Constants.READ_NEWS_IDS, hasReadIds+","+newsItem.id);
				 
				}
				title = newsItem.title;
				intent.putExtra("url", url);
				intent.putExtra("title", title);
				ct.startActivity(intent);
				
				 //两秒后，通知观察者，数据改变
		         new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						adapter.notifyDataSetChanged();
					}
				}, 2000);
				
			}
		});
		
		setLastUpdateTime();
		//下拉加载，上拉加载跟多
		lv_atlas.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				getAtlasList(url, true);
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				getAtlasList(moreUrl, false);
			}
		});
		return view;
//		return null;
	}

	/* (non-Javadoc)
	 * @see com.example.qianlong.base.BaseFragment#initData(android.os.Bundle)
	 */
	@Override
	protected void initData(Bundle savedInstanceState) {
		
		hasReadIds = SharePrefUtil.getString(ct, Constants.READ_NEWS_IDS, "");
		String[] ids =hasReadIds.split(",");
		for(String id : ids){
			readSet.add(id);
		}
		//url不为空
		if (!TextUtils.isEmpty(url)) {
			// 读缓存
			String result = SharePrefUtil.getString(ct, url, "");
			if (!TextUtils.isEmpty(result)) {
				// 缓存
				getAtlasList(url, true);
			} else {
				// 网络
				getAtlasList(url, true);
			}

		}
	}

	/* (non-Javadoc)
	 * @see com.example.qianlong.base.BaseActivity#initView()
	 */
}
