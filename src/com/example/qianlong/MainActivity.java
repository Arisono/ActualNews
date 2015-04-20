package com.example.qianlong;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import android.os.Bundle;
import android.view.Window;
/**
 * @author :LiuJie 时间: 2015年4月20日 下午1:35:49
 * @注释:
 */
public class MainActivity extends SlidingFragmentActivity {
	private MenuFragment mMenuFragment;
	private HomeFragment2 mHomeFragment;
   
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		//菜单
		setBehindContentView(R.layout.menu_frame);
		//内容
		setContentView(R.layout.content_frame);
		
		SlidingMenu sm = getSlidingMenu();
		sm.setShadowWidthRes(R.dimen.shadow_width);
		sm.setShadowDrawable(R.drawable.shadow);
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		sm.setFadeDegree(0.35f);
		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		if(savedInstanceState==null){
			/**注释：菜单片段 */
			mMenuFragment = new MenuFragment();
			/**注释：主界面片段 */
			mHomeFragment =new HomeFragment2();
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.menu_frame, mMenuFragment,"Menu").commit();
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.content_frame,mHomeFragment ,"Home").commit();
			
		}
		sm.setMode(SlidingMenu.LEFT);
	}


	public MenuFragment getMenuFragment(){
		mMenuFragment = (MenuFragment) getSupportFragmentManager().findFragmentByTag("Menu");
		return mMenuFragment;
		
	}
}
