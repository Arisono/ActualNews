/**
 * 
 */
package com.qianlong.android.adapter;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.qianlong.R;
import com.example.qianlong.base.QLBaseAdapter;
import com.example.qianlong.bean.AtlasListBean.Atlas;
import com.example.qianlong.utils.CommonUtil;
import com.example.qianlong.utils.Constants;
import com.example.qianlong.utils.SharePrefUtil;
import com.lidroid.xutils.BitmapUtils;
import com.qianlong.android.adapter.NewsAdapter.ViewHolder;

/**
 * @author LiuJie
 * 接口设计可扩展
 */
public class AtlasAdapter extends QLBaseAdapter<Atlas, ListView> {
	
	public BitmapUtils bitmapUtil;
	public int type;
	/**
	 * @param context
	 * @param list
	 * @param view
	 */
	public AtlasAdapter(Context context, List<Atlas> list, int type) {
		super(context, list);
		bitmapUtil = new BitmapUtils(context);
		this.type = type;
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		Atlas atlas=list.get(position);
		if (convertView==null) {
			holder=new ViewHolder();
			convertView = View.inflate(context, R.layout.layout_atlas_item, null);
			holder.iv = (ImageView) convertView.findViewById(R.id.iv_atlas_image);
			holder.title = (TextView) convertView.findViewById(R.id.tv_atlas_title);
			holder.pub_date = (TextView) convertView
					.findViewById(R.id.tv_pub_date);
			//评论数
			holder.comment_count = (TextView) convertView
					.findViewById(R.id.tv_comment_count);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		//是读过
		if (atlas.isRead) {
			holder.title.setTextColor(context.getResources().getColor(R.color.news_item_has_read_textcolor));
		} else {
			holder.title.setTextColor(context.getResources().getColor(R.color.news_item_no_read_textcolor));
		}
		holder.title.setText(atlas.title);
		holder.pub_date.setText(atlas.pubdate);
		
		if (type==0) {
			if (TextUtils.isEmpty(atlas.largeimage)) {
				holder.iv.setVisibility(View.GONE);
			} else {
				int read_model = SharePrefUtil.getInt(context,
						Constants.READ_MODEL, 1);
				switch (read_model) {
				case 1:
					int type = CommonUtil.isNetworkAvailable(context);
					//设置为wify 图片是可见的
					if(type==1){
						holder.iv.setVisibility(View.VISIBLE);
						bitmapUtil.display(holder.iv, atlas.largeimage);
					}else{
						//holder.iv.setVisibility(View.GONE);
					}
					break;
				case 2:
					holder.iv.setVisibility(View.VISIBLE);
					bitmapUtil.display(holder.iv, atlas.largeimage);
					break;
				case 3:
					holder.iv.setVisibility(View.GONE);
					break;

				default:
					break;
				}
			}
			
		} else {
			holder.iv.setVisibility(View.GONE);
		}
		return convertView;
	}
	
	class ViewHolder {
		ImageView iv;
		TextView title;
		TextView pub_date;
		TextView comment_count;
	}

}
