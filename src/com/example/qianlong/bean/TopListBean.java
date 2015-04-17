/**
 * 
 */
package com.example.qianlong.bean;

import java.util.ArrayList;

import com.example.qianlong.bean.AtlasListBean.Atlas;
import com.example.qianlong.bean.NewsListBean.News;

/**
 * @author LiuJie
 *  专题
 */
public class TopListBean extends BaseBean {
	
	public ArrayList<TopList> data;
	
	public static class TopList{
	  public String countcommenturl;
	  public ArrayList<Top> top;
	}
	
	public static class Top{
		public String title;
		public String id;
		//其它实体类中的内部类
		public ArrayList<News> news;
	}
	

}

/*{
	  "retcode": 200, 
	  "data": {
	    "topic": [
	      {
	        "title": "双节争春闹元宵", 
	        "id": 10092, 
	        "news": [
	          {
	            "id": 23502, 
	            "title": "元宵习俗奇趣汇", 
	            "url": "http://zhbj.qianlong.com/static/html/2014/02/11/714F6D544A6D1A7B6A23.html", 
	            "listimage": "http://zhbj.qianlong.com/static/images/2014/02/11/59/1861510091Q6VY.jpg", 
	            "pubdate": "2014-02-11 15:05", 
	            "comment": true, 
	            "commenturl": "http://zhbj.qianlong.com/client/user/newComment/23502", 
	            "type": "news", 
	            "commentlist": "http://zhbj.qianlong.com/static/api/news/10092/02/23502/comment_1.json"
	          }
	        ]
	      }, 
	      {
	        "title": "喜迎新春 乐在北京", 
	        "id": 10081, 
	        "news": [
	          {
	            "id": 20644, 
	            "title": "马上过大年之品民俗", 
	            "url": "http://zhbj.qianlong.com/static/html/2014/01/23/77496B514E6A187D6E21.html", 
	            "listimage": "http://zhbj.qianlong.com/static/images/2014/01/23/18/14504802760GEJ.jpg", 
	            "pubdate": "2014-01-23 18:10", 
	            "comment": false, 
	            "type": "news"
	          }
	        ]
	      }
	    ], 
	    "countcommenturl": "http://zhbj.qianlong.com/client/content/countComment/"
	  }
	}
*/
