package commondata;

import android.content.Context;
import android.content.SharedPreferences;

public class CookieData {
    private static String  USER_ID_SHARED_PREFERENCE_KEY ="user-id-key";
    private static String  LOCATION_SHARED_PREFERENCE_KEY ="location-key";
    private static String  DISTANCE_SHARED_PREFERENCE_KEY ="distance-key";
    private static String  DEFAULT_VALUE_USER_ID="no-user-id";
    private static String  DEFAULT_VALUE_DISTANCE="15";     // String value in kilometers
    private static String  DEFAULT_VALUE_LOCATION="no-location";
    private static String  SHARED_PREFERNCED_FOLDER = "user-parameters-folder";
    private static SharedPreferences getSharedPreferences(Context context){
        return  context.getSharedPreferences(SHARED_PREFERNCED_FOLDER,Context.MODE_PRIVATE);
    }
    private static SharedPreferences.Editor getEditor(Context context){
        return getSharedPreferences(context).edit();
    }
    private  static  String readSharedPreferences(String param1,String defaultparam,Context context){
       return getSharedPreferences(context).getString(param1,defaultparam);
    }
    public static void setUserId(int id, Context context){
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(USER_ID_SHARED_PREFERENCE_KEY,id+"");
        editor.apply();
    }
    public static void updateLocation(String latlng, Context context){
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(LOCATION_SHARED_PREFERENCE_KEY,latlng+"");
        editor.apply();
    }

    public static void updateViewDistance(int distanceinkm, Context context){
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(DISTANCE_SHARED_PREFERENCE_KEY,distanceinkm+"");
        editor.apply();
    }
    public static  String getUserId(Context context){
        return readSharedPreferences(USER_ID_SHARED_PREFERENCE_KEY,DEFAULT_VALUE_USER_ID,context);
    }
    
   public static  String getViewDistance(Context context){
        return readSharedPreferences(DISTANCE_SHARED_PREFERENCE_KEY,DEFAULT_VALUE_DISTANCE,context);
   }

    public static double[] getLastLocation(Context context){
        String location = readSharedPreferences(LOCATION_SHARED_PREFERENCE_KEY,DEFAULT_VALUE_LOCATION,context);
        try{
            String[] split = location.split(",");
            return new double[]{Double.parseDouble(split[0]),Double.parseDouble(split[1])};
        }catch (NumberFormatException|ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
            return new double[]{0,0};
        }
    }
    public static boolean userLoggedIn(Context context){
        return !getSharedPreferences(context).getString(USER_ID_SHARED_PREFERENCE_KEY,DEFAULT_VALUE_USER_ID).equals(DEFAULT_VALUE_USER_ID);
    }

    public static boolean hasUserLastLocation(Context context){
        return !getSharedPreferences(context).getString(LOCATION_SHARED_PREFERENCE_KEY,DEFAULT_VALUE_LOCATION).equals(DEFAULT_VALUE_LOCATION);
    }

}
