package objects;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class User implements Serializable{

    public String name;
    public String username;
    public String email;
    public String thumbnail_url;
    public int id;
    public boolean user_visibility;

    public User(int id,String name, String username, String email,String thumbnail_url,boolean user_visibility) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.email = email;
        this.thumbnail_url  = thumbnail_url;
        this.user_visibility = user_visibility;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isUser_visibility() {
        return user_visibility;
    }

    public void setUser_visibility(boolean user_visibility) {
        this.user_visibility = user_visibility;
    }

    public String getThumbnail_url() {
        return thumbnail_url;
    }

    public void setThumbnail_url(String thumbnail_url) {
        this.thumbnail_url = thumbnail_url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public static  User  getUserInstanceFromJSONData(JSONObject siteJson) throws JSONException{
        return new User(siteJson.getInt("user_id"), siteJson.getString("user_name"),
                siteJson.getString("user_username"), siteJson.getString("user_email"), siteJson.getString("user_thumbnail"), siteJson.get("user_visibility") == "1");

    }
}
