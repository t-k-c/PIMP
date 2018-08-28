package objects;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class Item implements Serializable{
    public String name,description,created_at;
    public int id,price,interest_number;
    public boolean item_visibility;
    public ArrayList<String> images;
    public Site site;
    public double distance; //distance in km
    public boolean user_interested;

    public Item(String name, String description, String created_at, int id, int price, boolean item_visibility, ArrayList<String> images, double distance,int interest_number,boolean user_interested, Site site) {
        this.name = name;
        this.description = description;
        this.created_at = created_at;
        this.id = id;
        this.price = price;
        this.item_visibility = item_visibility;
        this.images = images;
        this.site = site;
        this.distance = distance;
        this.interest_number = interest_number;
        this.user_interested = user_interested;
    }

    public void setInterest_number(int interest_number) {
        this.interest_number = interest_number;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public boolean isUser_interested() {
        return user_interested;
    }

    public void setUser_interested(boolean user_interested) {
        this.user_interested = user_interested;
    }

    public int getInterest_number() {
        return interest_number;
    }

    public double getDistance() {
        return distance;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public boolean isItem_visibility() {
        return item_visibility;
    }

    public void setItem_visibility(boolean item_visibility) {
        this.item_visibility = item_visibility;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public static   ArrayList<String> getThumbnailsFromJSONData(JSONArray jsonArray) throws JSONException{
        ArrayList<String> thumbnails = new ArrayList<>();
        for(int i =0;i<jsonArray.length();i++){
            thumbnails.add(jsonArray.getJSONObject(i).getString("item_thumbnail"));
        }
        return  thumbnails;
    }
    public static Item getItemInstanceFromJSONData(JSONObject itemJson,Site site) throws JSONException{
        return new Item(itemJson.getString("item_name"),
                itemJson.getString("item_description"), itemJson.getString("item_created_at"),
                itemJson.getInt("item_id") ,itemJson.getInt("item_price"),
                itemJson.getInt("item_visibility") == 1,
                Item.getThumbnailsFromJSONData(itemJson.getJSONArray("item_thumbnails")),
                itemJson.getDouble("distance"),itemJson.getInt("interests"),
                itemJson.getInt("user_interests") == 1,
                site);
    }

}

