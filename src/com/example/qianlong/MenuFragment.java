package com.example.qianlong;

import java.util.ArrayList;
import java.util.List;

import com.example.qianlong.base.BaseFragment;
import com.example.qianlong.base.QLBaseAdapter;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnItemClick;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
/**
 * @author :LiuJie 时间: 2015年4月2日 上午10:40:48
 * @注释:应用的左侧菜单管理类
 */
public class MenuFragment extends BaseFragment {
	
	public static final int NEWS_CENTER =1;
	public static final int TOPS_CENTER =2;
	public static final int ATLAS_CENTER =3;
	
	@ViewInject(R.id.lv_menu_news_center)
	private ListView newsCenterclassifyLv;
	@ViewInject(R.id.lv_menu_smart_service)
	private ListView smartServiceclassifyLv;
	@ViewInject(R.id.lv_menu_govaffairs)
	private ListView govAffairsclassifyLv;
	@ViewInject(R.id.tv_menu_classify)
	private TextView classifyTv;
	
	private int menuType = 0;
	//左侧菜单标识
	public static int newsCenterPosition = 0;//新闻
	public static int topsCenterPosition = 0;//专题
	public static int atlasCenterPosition = 0;//组图
	//左侧菜单适配器
	private MenuAdapter newsCenterAdapter = null;
	private ArrayList<String> newsCenterMenu = new ArrayList<String>();
	//上下文
	private MainActivity act;
	//管理者
	private FragmentManager fragManager;
	//新闻中心片段
	private NewsCenterPage newsCenterFragment;
	
	@Override
	protected View initView(LayoutInflater inflater) {
		View view = inflater.inflate(R.layout.layout_left_menu, null);
		ViewUtils.inject(this, view);
		return view;
	}

	@Override
	protected void initData(Bundle savedInstanceState) {
		act = (MainActivity) ct;
		switchMenu(menuType);

	}

	@Override
	protected void processClick(View v) {
		// TODO Auto-generated method stub

	}

	
	

	/**
	 * @param menuList
	 */
	public void initNewsCenterMenu(ArrayList<String> menuList) {
		newsCenterMenu.clear();
		newsCenterMenu.addAll(menuList);
		if (newsCenterAdapter == null) {
			newsCenterAdapter = new MenuAdapter(ct, newsCenterMenu);
			newsCenterclassifyLv.setAdapter(newsCenterAdapter);
		} else {
			newsCenterAdapter.notifyDataSetChanged();
		}
		newsCenterAdapter.setSelectedPosition(newsCenterPosition);

	}

	public void setMenuType(int menuType) {
		this.menuType = menuType;
		switchMenu(menuType);
	}

	
	

	public void switchMenu(int type) {
		newsCenterclassifyLv.setVisibility(View.GONE);
		//隐藏其它列表控件
		smartServiceclassifyLv.setVisibility(View.GONE);
		govAffairsclassifyLv.setVisibility(View.GONE);
		switch (type) {
		case NEWS_CENTER:
			newsCenterclassifyLv.setVisibility(View.VISIBLE);
			//实例化管理者
			fragManager = act.getSupportFragmentManager();
			//实例化新闻片段
			newsCenterFragment = ((HomeFragment2) fragManager
					.findFragmentByTag("Home")).getNewsCenterPage();
			classifyTv.setText("分类");
			//判断适配器是否为空
			if (newsCenterAdapter == null) {
				newsCenterAdapter = new MenuAdapter(ct, newsCenterMenu);
				newsCenterclassifyLv.setAdapter(newsCenterAdapter);
			} else {
				newsCenterAdapter.notifyDataSetChanged();
			}
			newsCenterAdapter.setSelectedPosition(newsCenterPosition);

			break;
		case TOPS_CENTER:
		/***********************************************************************************************/
		/**注释：切换专题 */	
			newsCenterclassifyLv.setVisibility(View.VISIBLE);
			//实例化管理者
			fragManager = act.getSupportFragmentManager();
			break;
		case ATLAS_CENTER:
		/***********************************************************************************************/
		/**注释：切换组图 */	
			newsCenterclassifyLv.setVisibility(View.VISIBLE);
			//实例化管理者
			fragManager = act.getSupportFragmentManager();
			AtlasItemPage aItemPage=new AtlasItemPage();
			act.getSupportFragmentManager().beginTransaction().
			replace(R.id.content_frame, aItemPage,"Home").commit();
			break;

		default:
			break;
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt("newsCenter_position", newsCenterPosition);
		super.onSaveInstanceState(outState);
	}

	/**
	 * @param parent
	 * @param view
	 * @param position
	 * @param id
	 */
	@OnItemClick(R.id.lv_menu_news_center)
	public void onNewsCenterItemClick(AdapterView<?> parent, View view,
			int position, long id) {
		// 当前位置等于点击位置直接切换
		if (position == newsCenterPosition) {
			//sm.toggle();
			//仔细研究观察者模式
			newsCenterAdapter.setSelectedPosition(0);
			fragManager = act.getSupportFragmentManager();
			HomeFragment2 newsPage=new HomeFragment2();
			act.getSupportFragmentManager().beginTransaction().
			replace(R.id.content_frame, newsPage,"Home").commit();
			return;
		}else if(position == 1){
			Toast.makeText(ct, "专题", 2000).show();
			newsCenterAdapter.setSelectedPosition(1);
		}else if (position ==2) {
			newsCenterAdapter.setSelectedPosition(2);
			Toast.makeText(ct, "组图", 2000).show();
			fragManager = act.getSupportFragmentManager();
			AtlasItemPage aItemPage=new AtlasItemPage();
			act.getSupportFragmentManager().beginTransaction().
			replace(R.id.content_frame, aItemPage,"Home").commit();
		}
       
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		if (savedInstanceState != null
				&& savedInstanceState.containsKey("newsCenter_position")) {
			newsCenterPosition = savedInstanceState
					.getInt("newsCenter_position");
		}

		super.onCreate(savedInstanceState);
	}

	class MenuAdapter extends QLBaseAdapter<String, ListView> {
		private int selectedPosition = 0;// 选中的位置

		public MenuAdapter(Context context, List<String> list) {
			super(context, list);
			// TODO Auto-generated constructor stub
		}

		public void setSelectedPosition(int position) {
			selectedPosition = position;
			notifyDataSetInvalidated();

		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = View.inflate(ct, R.layout.layout_item_menu, null);
			}
			TextView tv = (TextView) convertView
					.findViewById(R.id.tv_menu_item);
			ImageView iv = (ImageView) convertView
					.findViewById(R.id.iv_menu_item);
			tv.setText(list.get(position));
			if (selectedPosition == position) {
				convertView.setSelected(true);
				convertView.setPressed(true);
				convertView
						.setBackgroundResource(R.drawable.menu_item_bg_select);
				tv.setTextColor(ct.getResources().getColor(
						R.color.menu_item_text_color));
				iv.setBackgroundResource(R.drawable.menu_arr_select);
			} else {
				convertView.setSelected(false);
				convertView.setPressed(false);
				convertView.setBackgroundColor(Color.TRANSPARENT);
				iv.setBackgroundResource(R.drawable.menu_arr_normal);
				tv.setTextColor(ct.getResources().getColor(R.color.white));
			}
			return convertView;
		}

	}
}
