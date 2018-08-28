package objects;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Post implements Serializable {
    public String title,date_time,content;
    public boolean user_liked;
    public String thumbnail;
    public int id;
    public int comments_number;
    public int likes_number;
    public Site site;
    public static final int STYLE_MORDERN = 1002;
    public static final int STYLE_DEPRECATED = 109;
    public Post(int id, String title, String date_time, String content, boolean user_liked, String url, int comments_number, int likes_number, Site site) {
        this.id = id;
        this.title = title;
        this.date_time = date_time;
        this.content = content;
        this.user_liked = user_liked;
        this.thumbnail = url;
        this.comments_number = comments_number;
        this.likes_number = likes_number;
        this.site = site;
    }
    public Post(int id, String title, String date_time, String content, String url, Site site, int comments_number, int likes_number,boolean user_liked) {
        this.id = id;
        this.title = title;
        this.date_time = date_time;
        this.content = content;
        this.thumbnail = url;
        this.site = site;
        this.comments_number = comments_number;
        this.likes_number = likes_number;
        this.user_liked = user_liked;
    }

    public Post(int id, String title, String date_time, String content, String url, Site site, int comments_number, int likes_number) {
        this.id = id;
        this.title = title;
        this.date_time = date_time;
        this.content = content;
        this.thumbnail = url;
        this.site = site;
        this.comments_number = comments_number;
        this.likes_number = likes_number;
    }

    public Post(int id,String title, String date_time, String content, boolean user_liked, String url, int comments_number, int likes_number) {
        this.id = id;
        this.title = title;
        this.date_time = date_time;
        this.content = content;
        this.user_liked = user_liked;
        this.thumbnail = url;
        this.comments_number = comments_number;
        this.likes_number = likes_number;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public boolean isUser_liked() {
        return user_liked;
    }

    public void setUser_liked(boolean user_liked) {
        this.user_liked = user_liked;
    }

    public int getComments_number() {
        return comments_number;
    }

    public void setComments_number(int comments_number) {
        this.comments_number = comments_number;
    }

    public int getLikes_number() {
        return likes_number;
    }

    public void setLikes_number(int likes_number) {
        this.likes_number = likes_number;
    }

/**
 * Remember the problem you faced, the first was the traditional way and you found a better
 * way. The style is actucally to be able to differentiate
 * */
    public static Post getPostInstanceFromJSONData(JSONObject post,Site site,int style) throws JSONException{
        if(style == STYLE_DEPRECATED){
            JSONObject likesandcommentsnumber = post.getJSONObject("1");
            return new Post(post.getInt("post_id"), post.getString("post_title"), post.getString("post_date")
                    , post.getString("post_content"), post.getString("post_thumbnail"), site,
                    likesandcommentsnumber.getInt("post_comment_number"), likesandcommentsnumber.getInt("post_like_number"));
        }
    else{
            return new Post(post.getInt("post_id"), post.getString("post_title"), post.getString("post_date")
                    , post.getString("post_content"), post.getString("post_thumbnail"), site,post.getInt("post_comment_number"),post.getInt("post_like_number"),
                    post.getString("user_interested") == "1");
        }
    }
}