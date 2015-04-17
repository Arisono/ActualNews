/**
 * 
 */
package com.example.qianlong.bean;

import java.util.ArrayList;

/**
 * @author LiuJie
 * 组图
 */
public class AtlasListBean extends BaseBean {
     
	public AtlasList data;
	 public static class AtlasList{
		
		 public String more;
		 public String countcommenturl;
		 public ArrayList<Atlas> news;
		 
	 }
	 
	 public static class Atlas{
	      public String id; 
	      public String title; 
	      public String url; 
	      public String listimage; 
	      public String smallimage; 
	      public String largeimage; 
	      public String pubdate;
	      public int commentcount;
	      public boolean comment;
	      public String commenturl;
	      public String type;
	      public String commentlist;
	      public boolean isRead;
		 
	 }
}

/*{
	  "retcode": 200, 
	  "data": {
	    "title": "组图", 
	    "topic": [ ], 
	    "news": [
	      {
	        "id": 122569, 
	        "title": "侏儒男子爱上变性人 自称全球最幸福", 
	        "url": "http://zhbj.qianlong.com/static/html/2015/04/01/714C675F4D6D1F7B662B7C48.html", 
	        "listimage": "http://zhbj.qianlong.com/static/images/2015/04/01/73/21079225963WXS.jpg", 
	        "smallimage": "http://zhbj.qianlong.com/static/images/2015/04/01/85/561948442VV65.jpg", 
	        "largeimage": "http://zhbj.qianlong.com/static/images/2015/04/01/3/2079888091S69L.jpg", 
	        "pubdate": "2015-04-01 09:08", 
	        "comment": true, 
	        "commenturl": "http://zhbj.qianlong.com/client/user/newComment/122569", 
	        "type": "news", 
	        "commentlist": "http://zhbj.qianlong.com/static/api/news/10003/69/122569/comment_1.json"
	      }
	         ], 
	    "countcommenturl": "http://zhbj.qianlong.com/client/content/countComment/", 
	    "more": "http://zhbj.qianlong.com/static/api/news/10003/list_2.json"
	  }
	}*/
