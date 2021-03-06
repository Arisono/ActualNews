package com.example.qianlong;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.example.qianlong.base.BasePage;
import com.example.qianlong.bean.NewsCenterCategories.NewsCategory;
import com.example.qianlong.bean.TopicListBean;
import com.example.qianlong.bean.TopicListBean.Topic;
import com.example.qianlong.utils.CommonUtil;
import com.example.qianlong.utils.QLParser;
import com.example.qianlong.utils.SharePrefUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.qianlong.android.adapter.TopicAdapter;
import com.qianlong.android.ui.newscenter.TopicListActivity;
import com.qianlong.android.view.pullrefreshview.PullToRefreshBase;
import com.qianlong.android.view.pullrefreshview.PullToRefreshListView;
import com.qianlong.android.view.pullrefreshview.PullToRefreshBase.OnRefreshListener;

public class TopicPage extends BasePage {
	private String moreUrl;
	private NewsCategory category;
	public TopicPage(Context ct, NewsCategory newsCategory) {
		super(ct);
		category = newsCategory;
	}
	@ViewInject(R.id.lv_topic)
	private PullToRefreshListView ptrLv;
	private ArrayList<Topic> topicList = new ArrayList<Topic>();
	@Override
	protected View initView(LayoutInflater inflater) {
		View view = inflater.inflate(R.layout.frag_topic, null);
		ViewUtils.inject(this, view);
		ptrLv.setPullLoadEnabled(false);
		ptrLv.setScrollLoadEnabled(true);
		setLastUpdateTime();
		ptrLv.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				getTopicList(category.url1, true);
				
			}

			

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				getTopicList(moreUrl, false);
				
			}
		});
		ptrLv.getRefreshableView().setOnItemClickListener(
				new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						Topic topic = topicList.get(position);
						Intent intent = new Intent(ct,TopicListActivity.class);
						intent.putExtra("url", topic.url);
						intent.putExtra("title", topic.title);
						ct.startActivity(intent);
						
					}
				});
		return view;
	}
	private void getTopicList(final String loadUrl, final boolean isRefresh) {
		loadData(HttpMethod.GET, loadUrl, null, new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> info) {
				LogUtils.d("response_json---" + info.result);
				if(isRefresh){
					SharePrefUtil.saveString(ct, loadUrl, info.result);
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
	private void setLastUpdateTime() {
		String text = CommonUtil.getStringDate();
		ptrLv.setLastUpdatedLabel(text);
		
	}

	@Override
	public void initData() {
		String result = SharePrefUtil.getString(ct, category.url1, "");
		if(!TextUtils.isEmpty(result)){
			processData(true, result);
		}
		getTopicList(category.url1, true);


	}
	private void onLoaded() {
		dismissLoadingView();
		ptrLv.onPullDownRefreshComplete();
		ptrLv.onPullUpRefreshComplete();
	}
	private String countCommentUrl;
	private TopicAdapter adapter;
	private void processData(boolean isRefresh, String result) {
		TopicListBean topicbean = QLParser.parse(result,
				TopicListBean.class);
		if (topicbean.retcode != 200) {
		} else {
			isLoadSuccess= true;
			if (isRefresh) {
				topicList.clear();
			}
			topicList.addAll(topicbean.data.topic);
			moreUrl = topicbean.data.more;
			if (adapter == null) {
				adapter = new TopicAdapter(ct, topicList,countCommentUrl);
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
		
	@Override
	protected void processClick(View v) {
		// TODO Auto-generated method stub

	}

}
