package commondata;

public class ServerData {
    public static final String SERVER_PROTOCOL="http://";
    public static final String SERVER_URL="127.0.0.1/PIMP/";
    public static final String SERVER_HEADER=SERVER_PROTOCOL+SERVER_URL;
    public static final String SERVER_API_PATH=SERVER_HEADER+"api/";
    public static final String LOGIN_LINK = SERVER_API_PATH+"login/";
    public static final String USERS_LINK = SERVER_API_PATH+"users/";
    public static final String SIGNUP_LINK = SERVER_API_PATH+"signup/";
    public static final String POSTS_LINK = SERVER_API_PATH+"posts/";
    public static final String ASSOCS_LINK = SERVER_API_PATH+"assocs/";
    public static final String ITEMS_LINK = SERVER_API_PATH+"items/";
    public static final String SEARCH_LINK = SERVER_API_PATH+"search/";
    public static final String SITES_LINK = SERVER_API_PATH+"sites/";
    public static final String INTERERSTS_LINK = SERVER_API_PATH+"interests/";
    public static final String COMMENTS_LINK = SERVER_API_PATH+"comments/";
    public static final String SUBSCRIPTIONS_LINK = SERVER_API_PATH+"follows/";
    public static final String POST_IMAGES_LINK = SERVER_HEADER+"post_thumbnails/";
    public static final String SITE_IMAGES_LINK = SERVER_HEADER+"site_thumbnails/";
}
