package com.example.qianlong;
import java.io.File;

import com.example.qianlong.base.BaseActivity;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.laiwang.media.LWDynamicShareContent;
import com.umeng.socialize.laiwang.media.LWShareContent;
import com.umeng.socialize.media.GooglePlusShareContent;
import com.umeng.socialize.media.MailShareContent;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.RenrenShareContent;
import com.umeng.socialize.media.SinaShareContent;
import com.umeng.socialize.media.SmsShareContent;
import com.umeng.socialize.media.TencentWbShareContent;
import com.umeng.socialize.media.TwitterShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMVideo;
import com.umeng.socialize.media.UMusic;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.RenrenSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.TencentWBSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Adapter;
import android.widget.ImageButton;
import android.widget.Toast;

public class NewsDetailActivity extends BaseActivity {
	private final UMSocialService mController = UMServiceFactory
            .getUMSocialService("钟点新闻");
	
	@ViewInject(R.id.news_detail_wv)
	private WebView mWebView;
	private WebSettings settings;
	private ImageButton textSizeBtn;
	
	@Override
	protected void initView() {
		
		setContentView(R.layout.act_news_detail);
		configPlatforms();
		
		initTitleBar();
		ViewUtils.inject(this);
		rightBtn.setImageResource(R.drawable.icon_share);
		rightBtn.setVisibility(View.VISIBLE);
		rightBtn.setOnClickListener(this);
	}

	private String url;
	private String title;

	@Override
	protected void initData() {
		url = getIntent().getStringExtra("url");
		title = getIntent().getStringExtra("title");
		//社会化分享
		setShareContent("新闻客户端",url,title,null,null,null);
		dealNewsDetail();

	}

	public void loadurl(final WebView view, final String url) {
		view.loadUrl(url);
	}

	private void dealNewsDetail() {
		settings = mWebView.getSettings();
		settings.setUseWideViewPort(true);
		settings.setJavaScriptEnabled(true);
		settings.setJavaScriptCanOpenWindowsAutomatically(true);
		settings.setLoadWithOverviewMode(true);
		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				loadurl(view, url);
				return true;
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				Log.e("onPageStarted", "");
				showLoadingView();
				super.onPageStarted(view, url, favicon);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				// TODO Auto-generated method stub
				Log.e("onPageFinished", "");
				dismissLoadingView();
				super.onPageFinished(view, url);
			}

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				Toast.makeText(ct, "加载失败，请检查网络", 0).show();
				super.onReceivedError(view, errorCode, description, failingUrl);
			}
		});
		loadurl(mWebView, url);
	}

	@Override
	protected void processClick(View v) {
		switch (v.getId()) {
		case R.id.btn_right:
			// 分享
			showShare();
			break;

		default:
			break;
		}

	}

	private void showShare() {
		Toast.makeText(ct, "分享", 0).show();
		 mController.getConfig().setPlatforms(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE,
                 SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE,SHARE_MEDIA.SINA,SHARE_MEDIA.TENCENT, SHARE_MEDIA.DOUBAN,
                 SHARE_MEDIA.RENREN);
         mController.openShare(this, false);
	}

	/**
     * 配置分享平台参数</br>
     */
    private void configPlatforms() {
        // 添加新浪SSO授权
        mController.getConfig().setSsoHandler(new SinaSsoHandler());
        // 添加腾讯微博SSO授权
        mController.getConfig().setSsoHandler(new TencentWBSsoHandler());
        // 添加人人网SSO授权
        RenrenSsoHandler renrenSsoHandler = new RenrenSsoHandler(this,
                "201874", "28401c0964f04a72a14c812d6132fcef",
                "3bf66e42db1e4fa9829b955cc300b737");
        mController.getConfig().setSsoHandler(renrenSsoHandler);

        // 添加QQ、QZone平台
        addQQQZonePlatform();

        // 添加微信、微信朋友圈平台
        addWXPlatform();
    }
    
    /**
     * @功能描述 : 添加微信平台分享
     * @return
     */
    private void addWXPlatform() {
        // 注意：在微信授权的时候，必须传递appSecret
        // wx967daebe835fbeac是你在微信开发平台注册应用的AppID, 这里需要替换成你注册的AppID
        String appId = "wx967daebe835fbeac";
        String appSecret = "5bb696d9ccd75a38c8a0bfe0675559b3";
        // 添加微信平台
        UMWXHandler wxHandler = new UMWXHandler(this, appId, appSecret);
        wxHandler.addToSocialSDK();

        // 支持微信朋友圈
        UMWXHandler wxCircleHandler = new UMWXHandler(this, appId, appSecret);
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.addToSocialSDK();
    }

    /**
     * @功能描述 : 添加QQ平台支持 QQ分享的内容， 包含四种类型， 即单纯的文字、图片、音乐、视频. 参数说明 : title, summary,
     *       image url中必须至少设置一个, targetUrl必须设置,网页地址必须以"http://"开头 . title :
     *       要分享标题 summary : 要分享的文字概述 image url : 图片地址 [以上三个参数至少填写一个] targetUrl
     *       : 用户点击该分享时跳转到的目标地址 [必填] ( 若不填写则默认设置为友盟主页 )
     * @return
     */
    private void addQQQZonePlatform() {
    	
        String appId = "100424468";
        String appKey = "c7394704798a158208a74ab60104f0ba";
        // 添加QQ支持, 并且设置QQ分享内容的target url
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(this,
                appId, appKey);
        qqSsoHandler.setTargetUrl("http://www.umeng.com/social");
        qqSsoHandler.addToSocialSDK();

        // 添加QZone平台
        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(this, appId, appKey);
        qZoneSsoHandler.addToSocialSDK();
    }
	
    
    /**
     * 根据不同的平台设置不同的分享内容</br>
     */
    private void setShareContent(String content,String ur,String title,UMImage image,UMusic uMusic
    		,UMVideo video) {
        // 配置SSO
        mController.getConfig().setSsoHandler(new SinaSsoHandler());
        mController.getConfig().setSsoHandler(new TencentWBSsoHandler());

        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(this,
                "100424468", "c7394704798a158208a74ab60104f0ba");
        qZoneSsoHandler.addToSocialSDK();
        mController.setShareContent(content);
        mController.getConfig().setSsoHandler(qZoneSsoHandler);

        // APP ID：201874, API
        // * KEY：28401c0964f04a72a14c812d6132fcef, Secret
        // * Key：3bf66e42db1e4fa9829b955cc300b737.
        RenrenSsoHandler renrenSsoHandler = new RenrenSsoHandler(this,
                "201874", "28401c0964f04a72a14c812d6132fcef",
                "3bf66e42db1e4fa9829b955cc300b737");
        mController.getConfig().setSsoHandler(renrenSsoHandler);
        
        //图片
        UMImage localImage = new UMImage(this, R.drawable.device);
        UMImage urlImage = new UMImage(this,
                "http://www.umeng.com/images/pic/social/integrated_3.png");

        // 视频分享
        video = new UMVideo(
                "http://v.youku.com/v_show/id_XNTc0ODM4OTM2.html");
        video.setTitle("友盟社会化组件视频");
        video.setThumb(urlImage);
        video.setThumb(new UMImage(this, BitmapFactory.decodeResource(
                getResources(), R.drawable.device)));

        uMusic = new UMusic(
        "http://music.huoxing.com/upload/20130330/1364651263157_1085.mp3");
        uMusic.setAuthor("umeng");
        uMusic.setTitle("天籁之音");
        uMusic.setThumb("http://www.umeng.com/images/pic/social/chart_1.png");
        
        /**注释：微信 */
        WeiXinShareContent weixinContent = new WeiXinShareContent();
        weixinContent.setShareContent(content);
        weixinContent.setTitle(title);
        weixinContent.setTargetUrl(url);
        weixinContent.setShareMedia(urlImage);
        mController.setShareMedia(weixinContent);

        // 设置朋友圈分享的内容
        CircleShareContent circleMedia = new CircleShareContent();
        circleMedia.setShareContent(content);
        circleMedia.setTitle(title);
        circleMedia.setShareMedia(urlImage);
        // circleMedia.setShareMedia(uMusic);
        // circleMedia.setShareMedia(video);
        circleMedia.setTargetUrl(url);
        mController.setShareMedia(circleMedia);

        // 设置renren分享内容
        RenrenShareContent renrenShareContent = new RenrenShareContent();
        renrenShareContent.setShareContent(content);
         image = new UMImage(this,
                BitmapFactory.decodeResource(getResources(), R.drawable.device));
        image.setTitle(title);
        image.setThumb("http://www.umeng.com/images/pic/social/integrated_3.png");
        renrenShareContent.setShareImage(image);
        renrenShareContent.setAppWebSite(url);
        mController.setShareMedia(renrenShareContent);

        /**注释：设置QQ空间分享 */
        QZoneShareContent qzone = new QZoneShareContent();
        qzone.setShareContent(content);
        qzone.setTargetUrl(url);
        qzone.setTitle(title);
        qzone.setShareMedia(urlImage);
        mController.setShareMedia(qzone);

        
         /**注释：QQ分享 */
        QQShareContent qqShareContent = new QQShareContent();
        qqShareContent.setShareContent(content);
        qqShareContent.setTitle(title);
       // qqShareContent.setShareMedia(uMusic);
        qqShareContent.setTargetUrl(url);
        mController.setShareMedia(qqShareContent);

        /**注释：TencentWb */
        TencentWbShareContent tencent = new TencentWbShareContent();
        tencent.setShareContent(content);
        mController.setShareMedia(tencent);

        /**注释：设置邮件分享内容， 如果需要分享图片则只支持本地图片 */
        MailShareContent mail = new MailShareContent(localImage);
        mail.setTitle(title);
        mail.setShareContent(content);
        mController.setShareMedia(mail);

        /**注释：设置短信分享内容 */
        SmsShareContent sms = new SmsShareContent();
        sms.setShareContent(content);
        sms.setShareImage(urlImage);
        mController.setShareMedia(sms);
        /**注释：新浪 */
        SinaShareContent sinaContent = new SinaShareContent();
        sinaContent.setShareContent(content);
        mController.setShareMedia(sinaContent);


    }
	
}
