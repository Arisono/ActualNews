package com.example.qianlong;


import java.util.ArrayList;
import java.util.HashSet;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.qianlong.base.BasePage;
import com.example.qianlong.bean.CountList;
import com.example.qianlong.bean.NewsListBean;
import com.example.qianlong.bean.NewsListBean.News;
import com.example.qianlong.bean.NewsListBean.TopNews;
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
import com.qianlong.android.adapter.NewsAdapter;
import com.qianlong.android.view.RollViewPager;
import com.qianlong.android.view.RollViewPager.OnPagerClickCallback;
import com.qianlong.android.view.pullrefreshview.PullToRefreshBase;
import com.qianlong.android.view.pullrefreshview.PullToRefreshBase.OnRefreshListener;
import com.qianlong.android.view.pullrefreshview.PullToRefreshListView;

/**
 * @author LiuJie
 * 新闻列表
 */

public class ItemNewsPage extends BasePage {
	
	@ViewInject(R.id.lv_item_news)
	private PullToRefreshListView ptrLv;
	@ViewInject(R.id.top_news_title)
	private TextView topNewsTitle;
	
	@ViewInject(R.id.top_news_viewpager)
	private LinearLayout mViewPagerLay;
	@ViewInject(R.id.dots_ll)
	private LinearLayout dotLl;
	
	private NewsDetailActivity  cActivity;
	private View topNewsView;
	private String url;
	private String moreUrl;
	private ArrayList<News> news = new ArrayList<NewsListBean.News>();
	private ArrayList<TopNews> topNews;
	public NewsAdapter adapter;
	private ArrayList<View> dotList;
	private ArrayList<String> titleList, urlList;
	private HashSet<String> readSet = new HashSet<String>();
	private String  hasReadIds;
	private RollViewPager mViewPager;
	public boolean isLoadSuccess;


	/**
	 * @param context
	 * @param url
	 */
	public ItemNewsPage(Context context, String url) {
		super(context);
		this.url = url;
	}

	@Override
	protected View initView(LayoutInflater inflater) {
		
		View view = inflater.inflate(R.layout.frag_item_news, null);
		topNewsView = inflater.inflate(R.layout.layout_roll_view, null);
		ViewUtils.inject(this, view);
		ViewUtils.inject(this, topNewsView);
		
		
		// 上拉加载不可用 
		ptrLv.setPullLoadEnabled(false);
		 // 滚动到底自动加载可用  
		ptrLv.setScrollLoadEnabled(true);
		// 得到实际的ListView  设置点击
		ptrLv.getRefreshableView().setOnItemClickListener(
				new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						Intent intent = new Intent(ct, NewsDetailActivity.class);
						String url = "";
						String title;
						News newsItem;
						if (ptrLv.getRefreshableView().getHeaderViewsCount() > 0) {
							newsItem = news.get(position - 1);
						} else {
							newsItem = news.get(position);
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
		// 设置下拉刷新的listener  
		ptrLv.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				getNewsList(url, true);
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				getNewsList(moreUrl, false);

			}
		});
		return view;
	}

	/* (non-Javadoc)
	 * @see com.example.qianlong.base.BasePage#initData()
	 */
	@Override
	public void initData() {
		
		hasReadIds = SharePrefUtil.getString(ct, Constants.READ_NEWS_IDS, "");
		String[] ids =hasReadIds.split(",");
		for(String id : ids){
			readSet.add(id);
		}
		if (!TextUtils.isEmpty(url)) {
			String result = SharePrefUtil.getString(ct, url, "");
			if(!TextUtils.isEmpty(result)){
				processDataFromCache(true, result);
			}else{
			getNewsList(url, true);
			}
		}
	}

	private void getNewsList(final String loadUrl, final boolean isRefresh) {
		LogUtils.i("==============================="+loadUrl);
		loadData(HttpMethod.GET, loadUrl, null, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> info) {
				LogUtils.d("NewsList response_json---" + info.result);
				if(isRefresh){
					SharePrefUtil.saveString(ct, url, info.result);
				}
				processData(isRefresh, info.result);

			}
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				LogUtils.d("fail_json---" + arg1);
				onLoaded();

			}
		});
	}

	/***********************************************************************************************/
	/**注释：初始化新闻数据 */
	private void getNewsCommentCount(String countcommenturl,
			final ArrayList<News> newsList, final boolean isRefresh) {
		StringBuffer sb = new StringBuffer(countcommenturl);
		for (News news : newsList) {
			sb.append(news.id + ",");
//			sb.append(news + ",");
		}
		loadData(HttpMethod.GET, sb.toString(), null,
				new RequestCallBack<String>() {
					@Override
					public void onSuccess(ResponseInfo<String> info) {
						LogUtils.d("NewsComment response_json---" + info.result);
						//获取评论数
						CountList countList = QLParser.parse(info.result,
								CountList.class);
						for (News news : newsList) {
							news.commentcount = countList.data
									.get(news.id + "");
							if(readSet.contains(news.id)){
								news.isRead= true;
							}else{
								news.isRead = false;
							}
						}
						
						
						if (isRefresh) {
							news.clear();
							news.addAll(newsList);
						} else {
							news.addAll(newsList);
						}
						
						if (adapter == null) {
							adapter = new NewsAdapter(ct, news, 0);
							ptrLv.getRefreshableView().setAdapter(adapter);
						} else {
							adapter.notifyDataSetChanged();
						}
						onLoaded();
						LogUtils.d("moreUrl---" + moreUrl);
						if (TextUtils.isEmpty(moreUrl)) {
							ptrLv.setHasMoreData(false);
						} else {
							ptrLv.setHasMoreData(true);
						}
						setLastUpdateTime();

					}

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						LogUtils.d("fail_json---" + arg1);
						onLoaded();
					}
				});

	}

	@Override
	protected void processClick(View v) {

	}

	/**
	 * 网络加载失败，加载完毕
	 */
	private void onLoaded() {
		dismissLoadingView();
		ptrLv.onPullDownRefreshComplete();
		ptrLv.onPullUpRefreshComplete();
	}

	/**
	 * @param size
	 */
	private void initDot(int size) {
		
		dotList = new ArrayList<View>();
		dotLl.removeAllViews();
		for (int i = 0; i < size; i++) {
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					CommonUtil.dip2px(ct, 6), CommonUtil.dip2px(ct, 6));
			params.setMargins(5, 0, 5, 0);
			View m = new View(ct);
			if (i == 0) {
				m.setBackgroundResource(R.drawable.dot_focus);
			} else {
				m.setBackgroundResource(R.drawable.dot_normal);
			}
			m.setLayoutParams(params);
			dotLl.addView(m);
			dotList.add(m);
		}
	}

	private void setLastUpdateTime() {
		String text = CommonUtil.getStringDate();
		ptrLv.setLastUpdatedLabel(text);
	}
	private String countCommentUrl;
	/**
	 * @param isRefresh
	 * @param result
	 */
	public void processData(final boolean isRefresh, String result) {
		
		NewsListBean newsList = QLParser.parse(result, NewsListBean.class);
		if (newsList.retcode != 200) {
		} else {
			isLoadSuccess = true;
			countCommentUrl = newsList.data.countcommenturl;
			if (isRefresh) {
				topNews = newsList.data.topnews;
				if (topNews != null) {
					//实例化标题和图片
					titleList = new ArrayList<String>();
					urlList = new ArrayList<String>();
					for (TopNews news : topNews) {
						titleList.add(news.title);
						urlList.add(news.topimage);
					}
					//初始化点布局
					initDot(topNews.size());
					//实例化自定义ViewPaher
					mViewPager = new RollViewPager(ct, dotList,
							R.drawable.dot_focus, R.drawable.dot_normal,
							new OnPagerClickCallback() {
								@Override
								public void onPagerClick(int position) {
			/***********************************************************************************************/		
									//点击轮转图片，进入详细内容
									TopNews news = topNews.get(position);
									if (news.type.equals("news")) {
										Intent intent = new Intent(ct,
												NewsDetailActivity.class);
										String url = topNews.get(position).url;
										String title = topNews.get(position).title;
										intent.putExtra("url", url);
										intent.putExtra("title", title);
										ct.startActivity(intent);
									} else if (news.type.equals("topic")) {
									}
		     /***********************************************************************************************/							
								}
							});
					mViewPager.setLayoutParams(new LinearLayout.LayoutParams(
							LayoutParams.MATCH_PARENT,
							LayoutParams.WRAP_CONTENT));
					//top新闻的图片地址
					mViewPager.setUriList(urlList);
					mViewPager.setTitle(topNewsTitle, titleList);
					//开启线程  并实例化
					mViewPager.startRoll();
					mViewPagerLay.removeAllViews();
					//添加View
					mViewPagerLay.addView(mViewPager);
					if (ptrLv.getRefreshableView().getHeaderViewsCount() < 1) {
						ptrLv.getRefreshableView().addHeaderView(topNewsView);
					} 
				}
			} 
			moreUrl = newsList.data.more;
			System.out.println("moreUrl="+moreUrl.toString());
			/***********************************************************************************************/
			//开始加载新闻列表
			if (newsList.data.news != null) {
				getNewsCommentCount(newsList.data.countcommenturl,
						newsList.data.news, isRefresh);
			}
			/***********************************************************************************************/
		}
	}
	
	public void processDataFromCache(boolean isRefresh, String result) {
		NewsListBean newsList = QLParser.parse(result, NewsListBean.class);
		if (newsList.retcode != 200) {
		} else {
			isLoadSuccess = true;
			countCommentUrl = newsList.data.countcommenturl;
			if (isRefresh) {
				topNews = newsList.data.topnews;
				if (topNews != null) {
					titleList = new ArrayList<String>();
					urlList = new ArrayList<String>();
					for (TopNews news : topNews) {
						titleList.add(news.title);
						urlList.add(news.topimage);
					}
					initDot(topNews.size());
					mViewPager = new RollViewPager(ct, dotList,
							R.drawable.dot_focus, R.drawable.dot_normal,
							new OnPagerClickCallback() {
								@Override
								public void onPagerClick(int position) {
									TopNews news = topNews.get(position);
									if (news.type.equals("news")) {
										Intent intent = new Intent(ct,
												NewsDetailActivity.class);
										String url = topNews.get(position).url;
										String commentUrl = topNews
												.get(position).commenturl;
										String newsId = topNews.get(position).id;
										String commentListUrl = topNews
												.get(position).commentlist;
										String title = topNews.get(position).title;
										String imgUrl = topNews.get(position).topimage;
										boolean comment = topNews.get(position).comment;
										intent.putExtra("url", url);
										intent.putExtra("commentUrl",
												commentUrl);
										intent.putExtra("newsId", newsId);
										intent.putExtra("imgUrl", imgUrl);
										intent.putExtra("title", title);
										intent.putExtra("comment", comment);
										intent.putExtra("countCommentUrl",
												countCommentUrl);
										intent.putExtra("commentListUrl",
												commentListUrl);
										ct.startActivity(intent);
									} else if (news.type.equals("topic")) {
									
									}
								}
							});
					mViewPager.setLayoutParams(new LinearLayout.LayoutParams(
							LayoutParams.MATCH_PARENT,
							LayoutParams.WRAP_CONTENT));
					mViewPager.setUriList(urlList);
					mViewPager.setTitle(topNewsTitle, titleList);
					mViewPager.startRoll();
					mViewPagerLay.removeAllViews();
					mViewPagerLay.addView(mViewPager);
					if (ptrLv.getRefreshableView().getHeaderViewsCount() < 1) {
						ptrLv.getRefreshableView().addHeaderView(topNewsView);
					} 
				}
			} 
			moreUrl = newsList.data.more;
			LogUtils.d("111111="+newsList.data.news.size());
			if (isRefresh) {
				news.clear();
				news.addAll(newsList.data.news);
			} else {
				news.addAll(newsList.data.news);
			}
			for (News newsItem : news) {
				if(readSet.contains(newsItem.id)){
					newsItem.isRead= true;
				}else{
					newsItem.isRead = false;
				}
			}
			if (adapter == null) {
				adapter = new NewsAdapter(ct, news, 0);
				ptrLv.getRefreshableView().setAdapter(adapter);
			} else {
				adapter.notifyDataSetChanged();
			}
			onLoaded();
			LogUtils.d("moreUrl---" + moreUrl);
			if (TextUtils.isEmpty(moreUrl)) {
				ptrLv.setHasMoreData(false);
			} else {
				ptrLv.setHasMoreData(true);
			}
			setLastUpdateTime();
		}
	}
	
	  /* (non-Javadoc)
	 * @see com.example.qianlong.base.BasePage#onResume()
	 */
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	
	
}
