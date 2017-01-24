package app.appurservice.data;

import android.Manifest;

public class Constant {

	// for search logs Tag
	public static final String LOG_TAG = "CITY_LOG";

	// Google analytics event category
	public enum Event {
		FAVORITES,
		THEME,
		NOTIFICATION,
		REFRESH
	}

		// for permission android M (6.0)
	public static String[] PERMISSIONS_LOCATION = {
			Manifest.permission.ACCESS_FINE_LOCATION,
			Manifest.permission.ACCESS_COARSE_LOCATION
	};

	public static String[] ALL_REQUIRED_PERMISSION = {
			Manifest.permission.GET_ACCOUNTS,
			Manifest.permission.ACCESS_FINE_LOCATION,
			Manifest.permission.ACCESS_COARSE_LOCATION,
			Manifest.permission.WRITE_EXTERNAL_STORAGE,
			Manifest.permission.READ_PHONE_STATE
	};

	// for map zoom
	public static final double city_lat = 14.428629 ;
	public static final double city_lng = 121.027073;

	// for gcm
	public static final String PROJECT_API_NUMBER = "25366220091";

	/** ------------------- EDIT THIS WITH YOURS ------------------------- */

	private static String HOST_URL = "http://app.rvnsolutions.com";
	private static String WEB_PATH = "/";

	/** ------------------------------------------------------------------ */
	// image file url
	public static String getURLimgPlace(String file_name){
		String URL = HOST_URL + WEB_PATH + IMG_PATH_PLACE + "/" + file_name;
		return URL;
	}

	// gcm registration url
	public static String getURLgcmserver(){
		String URL = HOST_URL + WEB_PATH + API_PATH_GCM;
		return URL;
	}

	// All in one API data url
	public static String getURLApiClientData(){
		String URL = HOST_URL + WEB_PATH + API_CLIENT_DATA;
		return URL;
	}

	public static String getURLApiSinglePlace(int place_id){
		String URL = HOST_URL + WEB_PATH + API_SINGLE_PLACE + "?place_id="+place_id;
		return URL;
	}

	/** ------------------- DON'T EDIT THIS ------------------------------ */

	private static String API_SINGLE_PLACE = "/app/services/getPlace";
	private static String API_CLIENT_DATA  = "/app/services/" + ( AppConfig.LAZY_LOAD ? "getApiClientDataDraft" : "getApiClientData");

	private static String IMG_PATH_PLACE   = "/uploads/place";
	private static String API_PATH_GCM 	   = "/app/services/insertGcm";

	/** ------------------------------------------------------------------ */

}
