package com.example.qianlong;

import java.util.ArrayList;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.example.qianlong.base.BasePage;
import com.example.qianlong.bean.NewsCenterCategories;
import com.example.qianlong.bean.NewsCenterCategories.NewsCategory;
import com.example.qianlong.utils.GsonTools;
import com.example.qianlong.utils.QLApi;
import com.example.qianlong.utils.QLParser;
import com.example.qianlong.utils.SharePrefUtil;
import com.example.qianlong.utils.SharePrefUtil.KEY;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * @author LiuJie
 *
 */
public class NewsCenterPage extends BasePage {
	private ArrayList<BasePage> pageList;
	public ArrayList<NewsCategory> categorieList;
	public ArrayList<String> newsCenterMenuList = new ArrayList<String>();
	/**
	 * @param context
	 */
	public NewsCenterPage(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public void onResume() {
		super.onResume();
	}

	/* (non-Javadoc)
	 * @see com.example.qianlong.base.BasePage#initView(android.view.LayoutInflater)
	 */
	@Override
	protected View initView(LayoutInflater inflater) {
		View view = inflater.inflate(R.layout.news_center_frame, null);
		ViewUtils.inject(this, view);
		initTitleBar(view);
		return view;
	}

	

	@Override
	public void initData() {
		pageList = new ArrayList<BasePage>();
		if (newsCenterMenuList.size() == 0) {
			String result = SharePrefUtil.getString(ct,
					QLApi.NEWS_CENTER_CATEGORIES, "");
			/***********************************************************************************************/
			//取到缓存的时候，直接用缓存
			if (!TextUtils.isEmpty(result)) {
				//放入缓存数据
				processData(result);
			}else{
				getNewsCenterCategories();
			}
		}
	}

	

	private void processData(String result) {
		NewsCenterCategories categories = QLParser.parse(result,
				NewsCenterCategories.class);
		if (categories.retcode != 200) {
			return;
		}
		categorieList = categories.data;
		newsCenterMenuList.clear();
		for (NewsCategory cate : categories.data) {
			newsCenterMenuList.add(cate.title);
		}
		((MainActivity) ct).getMenuFragment().initNewsCenterMenu(
				newsCenterMenuList);
		
		
		NewsCategory newsCategory = categorieList.get(0);
		SharePrefUtil.saveString(ct, KEY.CATE_ALL_JSON,
				GsonTools.createGsonString(newsCategory.children));
		SharePrefUtil.saveString(ct, KEY.CATE_EXTEND_ID,
				GsonTools.createGsonString(categories.extend));
		pageList.clear();
		//新闻
		BasePage newsPage = new NewsPage(ct, newsCategory);
		//专题
		BasePage topicPage = new TopicPage(ct, categorieList.get(1));
		//组图
		BasePage picPage = new PicPage(ct, categorieList.get(2));
		//互动
//		BasePage interactPage = new InteractPage(ct, categorieList.get(3));
		//投票
//		BasePage votePage = new VotePage(ct, categorieList.get(4));
		pageList.add(newsPage);
		pageList.add(topicPage);
		pageList.add(picPage);
//		pageList.add(interactPage);
//		pageList.add(votePage);
		switchFragment(MenuFragment.newsCenterPosition);
	}

	@ViewInject(R.id.news_center_fl)
	private FrameLayout news_center_fl;

	public void switchFragment(int newsCenterPosition) {
		BasePage page = pageList.get(newsCenterPosition);
		switch (newsCenterPosition) {
		case 0:
			news_center_fl.removeAllViews();
			news_center_fl.addView(page.getContentView());
			break;
		case 1:
			news_center_fl.removeAllViews();
			news_center_fl.addView(page.getContentView());
			break;
		case 2:
			news_center_fl.removeAllViews();
			news_center_fl.addView(page.getContentView());
			break;
		case 3:
			news_center_fl.removeAllViews();
			news_center_fl.addView(page.getContentView());
			break;
		case 4:
			news_center_fl.removeAllViews();
			news_center_fl.addView(page.getContentView());
			break;
		}
		page.initData();
	}

	private void getNewsCenterCategories() {
		loadData(HttpMethod.GET, QLApi.NEWS_CENTER_CATEGORIES, null,
				new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> info) {
						LogUtils.d("NewsCenter response_json---" + info.result);
						 SharePrefUtil.saveString(ct,
						 QLApi.NEWS_CENTER_CATEGORIES, info.result);
						processData(info.result);

					}

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						LogUtils.d("response_fail---" + arg1);

					}
				});
	}
    
	
	/*{
	    "retcode": 200,
	    "data": [
	        {
	            "id": 10000,
	            "title": "新闻",
	            "type": 1,
	            "children": [
	                {
	                    "id": 10007,
	                    "title": "北京",
	                    "type": 1,
	                    "url": "http://zhbj.qianlong.com/static/api/news/10007/list_1.json"
	                },
	                {
	                    "id": 10006,
	                    "title": "中国",
	                    "type": 1,
	                    "url": "http://zhbj.qianlong.com/static/api/news/10006/list_1.json"
	                },
	                {
	                    "id": 10008,
	                    "title": "国际",
	                    "type": 1,
	                    "url": "http://zhbj.qianlong.com/static/api/news/10008/list_1.json"
	                },
	                {
	                    "id": 10014,
	                    "title": "文娱",
	                    "type": 1,
	                    "url": "http://zhbj.qianlong.com/static/api/news/10014/list_1.json"
	                },
	                {
	                    "id": 10010,
	                    "title": "体育",
	                    "type": 1,
	                    "url": "http://zhbj.qianlong.com/static/api/news/10010/list_1.json"
	                },
	                {
	                    "id": 10091,
	                    "title": "生活",
	                    "type": 1,
	                    "url": "http://zhbj.qianlong.com/static/api/news/10091/list_1.json"
	                },
	                {
	                    "id": 10012,
	                    "title": "旅游",
	                    "type": 1,
	                    "url": "http://zhbj.qianlong.com/static/api/news/10012/list_1.json"
	                },
	                {
	                    "id": 10095,
	                    "title": "科技",
	                    "type": 1,
	                    "url": "http://zhbj.qianlong.com/static/api/news/10095/list_1.json"
	                },
	                {
	                    "id": 10009,
	                    "title": "军事",
	                    "type": 1,
	                    "url": "http://zhbj.qianlong.com/static/api/news/10009/list_1.json"
	                },
	                {
	                    "id": 10011,
	                    "title": "财经",
	                    "type": 1,
	                    "url": "http://zhbj.qianlong.com/static/api/news/10011/list_1.json"
	                },
	                {
	                    "id": 10093,
	                    "title": "女性",
	                    "type": 1,
	                    "url": "http://zhbj.qianlong.com/static/api/news/10093/list_1.json"
	                },
	                {
	                    "id": 10192,
	                    "title": "倍儿逗",
	                    "type": 1,
	                    "url": "http://zhbj.qianlong.com/static/api/news/10192/list_1.json"
	                }
	            ]
	        },
	        {
	            "id": 10002,
	            "title": "专题",
	            "type": 10,
	            "url": "http://zhbj.qianlong.com/static/api/news/10002/list_1.json",
	            "url1": "http://zhbj.qianlong.com/static/api/news/10002/list1_1.json"
	        },
	        {
	            "id": 10003,
	            "title": "组图",
	            "type": 2,
	            "url": "http://zhbj.qianlong.com/static/api/news/10003/list_1.json"
	        },
	        {
	            "id": 10004,
	            "title": "互动",
	            "type": 3,
	            "excurl": "http://zhbj.qianlong.com/static/api/news/comment/exc_1.json",
	            "dayurl": "http://zhbj.qianlong.com/static/api/news/comment/day_1.json",
	            "weekurl": "http://zhbj.qianlong.com/static/api/news/comment/week_1.json"
	        },
	        {
	            "id": 10005,
	            "title": "投票",
	            "type": 4,
	            "url": "http://zhbj.qianlong.com/static/api/news/vote/vote_1.json"
	        }
	    ],
	    "extend": [
	        10007,
	        10006,
	        10008,
	        10014,
	        10091,
	        10010,
	        10192,
	        10009,
	        10095,
	        10093,
	        10012,
	        10011
	    ]
	}*/
	@Override
	protected void processClick(View v) {
		// TODO Auto-generated method stub

	}

}
