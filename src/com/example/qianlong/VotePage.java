package com.example.qianlong;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.example.qianlong.base.BasePage;
import com.example.qianlong.bean.NewsCenterCategories.NewsCategory;
import com.lidroid.xutils.ViewUtils;

public class VotePage extends BasePage {

	private NewsCategory category;
	public VotePage(Context ct, NewsCategory newsCategory) {
		super(ct);
		category = newsCategory;
	}

	@Override
	protected View initView(LayoutInflater inflater) {
		View view = inflater.inflate(R.layout.frag_topic, null);
		ViewUtils.inject(this, view);
		return view;
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void processClick(View v) {
		// TODO Auto-generated method stub

	}

}
