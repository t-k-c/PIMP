package objects;

import android.location.Location;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Site implements Serializable{
    public int id,subscriptions_number;
    public boolean site_visibility,user_follows;
    public double[] latlng;
    public double distance;
    public String name,short_name,description,contact,thumbnail,working_period,site_created_at,site_since;
    public User user;

    public Site(int id, boolean site_visibility, double[] latlng, String name,String short_name, String description, String contact, String thumbnail, String working_period, String site_created_at, String site_since, User user) {
        this.id = id;
        this.site_visibility = site_visibility;
        this.latlng = latlng;
        this.name = name;
        this.description = description;
        this.contact = contact;
        this.thumbnail = thumbnail;
        this.working_period = working_period;
        this.site_created_at = site_created_at;
        this.site_since = site_since;
        this.user = user;
        this.short_name = short_name;
    }

    public boolean isUser_follows() {
        return user_follows;
    }

    public void setUser_follows(boolean user_follows) {
        this.user_follows = user_follows;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getSubscriptions_number() {
        return subscriptions_number;
    }

    public void setSubscriptions_number(int subscriptions_number) {
        this.subscriptions_number = subscriptions_number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShort_name() {
        return short_name;
    }

    public void setShort_name(String short_name) {
        this.short_name = short_name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isSite_visibility() {
        return site_visibility;
    }

    public void setSite_visibility(boolean site_visibility) {
        this.site_visibility = site_visibility;
    }

    public double[] getLocation() {
        return latlng;
    }

    public void setLocation(double[] latlng) {
        this.latlng = latlng;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getWorking_period() {
        return working_period;
    }

    public void setWorking_period(String working_period) {
        this.working_period = working_period;
    }

    public String getSite_created_at() {
        return site_created_at;
    }

    public void setSite_created_at(String site_created_at) {
        this.site_created_at = site_created_at;
    }

    public String getSite_since() {
        return site_since;
    }

    public void setSite_since(String site_since) {
        this.site_since = site_since;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public static Site getSiteInstanceFromJSONData(JSONObject itemJson, User user) throws JSONException {
        return  new Site(itemJson.getInt("site_id"),
                itemJson.getString("site_visibility") == "1",
                new double[]{itemJson.getDouble("site_latitude"), itemJson.getDouble("site_longitude")},
                itemJson.getString("site_name"), itemJson.getString("site_short_name"),
                itemJson.getString("site_description"), itemJson.getString("site_contact"),
                itemJson.getString("site_thumbnail"), itemJson.getString("site_working_period"),
                itemJson.getString("site_created_at"), itemJson.getString("site_since"), user
        );
    }
}
