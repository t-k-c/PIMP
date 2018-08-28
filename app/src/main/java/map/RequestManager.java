package map;

/**
 * Created by codename-tkc on 06/02/2018.
 */

public class RequestManager {
    public static String createUrlFromSrcDesLatLng( double sourcelatitiude,double sourcelongitude, double destlatitude, double destlongitude) {
        return  new StringBuilder()
                .append("https://maps.googleapis.com/maps/api/directions/json")
                .append("?origin=")
                .append(Double.toString(sourcelatitiude))
                .append(",")
                .append(Double.toString(sourcelongitude))
                .append("&destination=")
                .append(Double.toString(destlatitude))
                .append(",")
                .append(Double.toString(destlongitude))
                .append("&sensor=false&mode=driving&alternatives=true")
                .append("&key=AIzaSyCFPnQwBl3HYD0m56yruFt9l3dYaJvpvV8").toString();
    }
}
