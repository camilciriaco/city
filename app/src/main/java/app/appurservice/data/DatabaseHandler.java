package app.appurservice.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.renderscript.Sampler;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import app.appurservice.R;
import app.appurservice.model.Category;
import app.appurservice.model.Images;
import app.appurservice.model.Place;
import app.appurservice.model.PlaceCategory;
import app.appurservice.sigin.FormDetailData;
import app.appurservice.sigin.RegisterData;

public class DatabaseHandler extends SQLiteOpenHelper {

    private SQLiteDatabase db;
    private Context context;
    String password;
    String firstname;

    // Database Version
    private static final int DATABASE_VERSION = 2;

    // Database Name
    private static final String DATABASE_NAME = "the_city";

    // Main Table Name
    private static final String TABLE_PLACE         = "place";
    private static final String TABLE_IMAGES        = "images";
    private static final String TABLE_CATEGORY      = "category";
    public static final String TABLE_USERINFO      = "userinfo";
    public static final String TABLE_USERDETAIL      = "userdetail";

    // Relational table Place to Category ( N to N )
    private static final String TABLE_PLACE_CATEGORY = "place_category";

    // table only for android client
    private static final String TABLE_FAVORITES     = "favorites_table";

    // Table Columns names TABLE_PLACE
    private static final String KEY_PLACE_ID    = "place_id";
    private static final String KEY_NAME        = "name";
    private static final String KEY_IMAGE       = "image";
    private static final String KEY_ADDRESS     = "address";
    private static final String KEY_PHONE       = "phone";
    private static final String KEY_WEBSITE     = "website";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_LNG         = "lng";
    private static final String KEY_LAT         = "lat";
    private static final String KEY_LAST_UPDATE = "last_update";

    // Table Columns names TABLE_IMAGES
    private static final String KEY_IMG_PLACE_ID    = "place_id";
    private static final String KEY_IMG_NAME        = "name";

    // Table Columns names TABLE_CATEGORY
    private static final String KEY_CAT_ID      = "cat_id";
    private static final String KEY_CAT_NAME    = "name";
    private static final String KEY_CAT_ICON    = "icon";

    // Table Columns name TABLE_USERINFO
    private static final String KEY_USR_ID          ="usr_id";
    private static final String KEY_USR_EMAIL       ="usr_email";
    private static final String KEY_USR_MOBILE      ="usr_mobile";
    public static final String KEY_USR_USRNAME     ="usr_username";
    public static final String KEY_USR_PWD         ="usr_pwd";
//// TODO: 8/3/2016 finish the names

    // Table Columns name TABLE_USERDETAILS
    private static final String KEY_DTL_ID           ="dtl_id";
    private static final String KEY_DTL_LNAME        ="dtl_lname";
    private static final String KEY_DTL_FNAME        ="dtl_fname";
    private static final String KEY_DTL_MNAME        ="dtl_mname";
    private static final String KEY_DTL_MONTH        ="dtl_month";
    private static final String KEY_DTL_DAY          ="dtl_day";
    private static final String KEY_DTL_YEAR         ="dtl_year";
    private static final String KEY_DTL_ADDRESS      ="dtl_address";
    private static final String KEY_DTL_MOBILE       ="dtl_mobile";
    private static final String KEY_DTL_LANDLINE     ="dtl_landline";
    private static final String KEY_DTL_GENDER       ="dtl_gender";
    private static final String KEY_DTL_CIVILSTATUS  ="dtl_civilstatus";
    private static final String KEY_DTL_EMAIL        ="dtl_email";



    // Table Relational Columns names TABLE_PLACE_CATEGORY
    private static final String KEY_RELATION_PLACE_ID = KEY_PLACE_ID;
    private static final String KEY_RELATION_CAT_ID = KEY_CAT_ID;

    private int cat_id[]; // category id
    private String cat_name[]; // category name
    private TypedArray cat_icon; // category name

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        this.db = getWritableDatabase();

        // get data from res/values/category.xml
        cat_id = context.getResources().getIntArray(R.array.id_category);
        cat_name = context.getResources().getStringArray(R.array.category_name);
        cat_icon = context.getResources().obtainTypedArray(R.array.category_icon);

        // if length not equal refresh table category
        if(getCategorySize() != cat_id.length) {
            defineCategory(this.db);  // define table category
        }

    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase d) {
        createTablePlace(d);
        createTableImages(d);
        createTableCategory(d);
        createTableRelational(d);
        createTableUserInfo(d);
        createTableFavorites(d);
        createTableFormDetails(d);

    }

    private void createTablePlace(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_PLACE + " ("
                + KEY_PLACE_ID + " INTEGER PRIMARY KEY, "
                + KEY_NAME + " TEXT, "
                + KEY_IMAGE + " TEXT, "
                + KEY_ADDRESS + " TEXT, "
                + KEY_PHONE + " TEXT, "
                + KEY_WEBSITE + " TEXT, "
                + KEY_DESCRIPTION + " TEXT, "
                + KEY_LNG + " REAL, "
                + KEY_LAT + " REAL, "
                + KEY_LAST_UPDATE + " NUMERIC "
                + ")";
        db.execSQL(CREATE_TABLE);
    }

    private void createTableImages(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_IMAGES + " ("
                + KEY_IMG_PLACE_ID + " INTEGER, "
                + KEY_IMG_NAME + " TEXT, "
                + " FOREIGN KEY(" + KEY_IMG_PLACE_ID + ") REFERENCES " + TABLE_PLACE + "(" + KEY_PLACE_ID + ")"
                + " )";
        db.execSQL(CREATE_TABLE);
    }

    private void createTableCategory(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_CATEGORY + "("
                + KEY_CAT_ID + " INTEGER PRIMARY KEY, "
                + KEY_CAT_NAME + " TEXT, "
                + KEY_CAT_ICON + " INTEGER"
                + ")";
        db.execSQL(CREATE_TABLE);
    }

    private void createTableUserInfo(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_USERINFO + "("
                + KEY_USR_ID + " INTEGER PRIMARY KEY, "
                + KEY_USR_EMAIL + " TEXT, "
                + KEY_USR_MOBILE + " INTEGER, "
                + KEY_USR_USRNAME + " TEXT, "
                + KEY_USR_PWD + " TEXT "

                + ")";
        db.execSQL(CREATE_TABLE);
    }


    // TODO: 8/3/2016 createTableFormDetails
    private void createTableFormDetails(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_USERDETAIL + "("
                + KEY_DTL_ID + " INTEGER PRIMARY KEY, "
                + KEY_DTL_LNAME + " TEXT, "
                + KEY_DTL_FNAME + " TEXT, "
                + KEY_DTL_MNAME + " TEXT, "
                + KEY_DTL_MONTH + " TEXT "
                + KEY_DTL_DAY + " TEXT, "
                + KEY_DTL_YEAR + " INTEGER, "
                + KEY_DTL_ADDRESS + " TEXT, "
                + KEY_DTL_MOBILE + " TEXT "
                + KEY_DTL_LANDLINE + " TEXT, "
                + KEY_DTL_GENDER + " TEXT, "
                + KEY_DTL_CIVILSTATUS + " TEXT "
                + KEY_DTL_EMAIL + "TEXT"

                + ")";
        db.execSQL(CREATE_TABLE);
    }


    private void createTableFavorites(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_FAVORITES + "("
                + KEY_PLACE_ID + " INTEGER PRIMARY KEY "
                + ")";
        db.execSQL(CREATE_TABLE);
    }

    private void defineCategory(SQLiteDatabase db) {
        db.execSQL("DELETE FROM " + TABLE_CATEGORY); // refresh table content
        db.execSQL("VACUUM");
        for (int i = 0; i < cat_id.length; i++) {
            ContentValues values = new ContentValues();
            values.put(KEY_CAT_ID, cat_id[i]);
            values.put(KEY_CAT_NAME, cat_name[i]);
            values.put(KEY_CAT_ICON, cat_icon.getResourceId(i, 0));
            db.insert(TABLE_CATEGORY, null, values); // Inserting Row
        }
    }

	// Table Relational place_category
    private void createTableRelational(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_PLACE_CATEGORY + "("
                + KEY_RELATION_PLACE_ID + " INTEGER, "      // id from table place
                + KEY_RELATION_CAT_ID + " INTEGER "        // id from table category
                + ")";
        db.execSQL(CREATE_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("DB ", "onUpgrade " + oldVersion + " to " + newVersion);
        if(oldVersion < newVersion) {
            // Drop older table if existed
            truncateDB(db);
            // define table category
            defineCategory(db);
        }
    }

       public void truncateDB(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLACE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_IMAGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLACE_CATEGORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERINFO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITES);

        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    // adding the new register
    public void addregister(RegisterData registerdata)

    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USR_USRNAME, registerdata.getUsername()); // register UserName

        values.put(KEY_USR_EMAIL, registerdata.getEmail_id());//register email id
        values.put(KEY_USR_MOBILE, registerdata.getMobNo());//register mobile no
        values.put(KEY_USR_PWD, registerdata.getPassword());
        // Inserting Row

        db.insert(TABLE_USERINFO, null, values);
        db.close(); // Closing database connection

    }

    // adding the new register
    public void editNewDetails(FormDetailData formDetailData)

    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_DTL_LNAME, formDetailData.getlastName()); // EDIT LAST NAME
        values.put(KEY_DTL_FNAME, formDetailData.getfirstName()); //EDIT FIRST NAME
        values.put(KEY_DTL_MNAME, formDetailData.getMiddle_i()); //EDIT MIDDLE NAME
        values.put(KEY_DTL_MOBILE, formDetailData.getMobNo()); //EDIT MOBILE NUMBER
        values.put(KEY_DTL_LANDLINE, formDetailData.getLandline()); //EDIT LANDLINE NUMBER
        values.put(KEY_DTL_MONTH, formDetailData.getBday_Month()); //EDIT BIRTH MONTH
        values.put(KEY_DTL_DAY, formDetailData.getBday_day()); //EDIT BIRTH DAY
        values.put(KEY_DTL_YEAR, formDetailData.getBday_year()); //EDIT BIRTH YEAR
        values.put(KEY_DTL_GENDER, formDetailData.getGender()); //EDIT GENDER
        values.put(KEY_DTL_ADDRESS, formDetailData.getAddress()); //EDIT ADDRESS
        values.put(KEY_DTL_CIVILSTATUS, formDetailData.getCivilstatus()); //EDIT CIVILSTATUS
        values.put(KEY_DTL_EMAIL, formDetailData.getEmailId()); //EDIT EMAILADD

        // Inserting Row

        db.insert(TABLE_USERDETAIL, null, values);
        db.close(); // Closing database connection

    }
    public String getformdetail(String lastname) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERDETAIL + " WHERE " + KEY_DTL_LNAME + " = ?", new String[]{lastname});
        if (cursor.getCount() > 0 && cursor.moveToFirst()) {

            firstname = cursor.getString(cursor.getColumnIndex(KEY_DTL_FNAME));
            cursor.close();
        }
        return firstname;
    }

    //code to get the register

  public String getregister(String username) {
       SQLiteDatabase db = this.getReadableDatabase();

      Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERINFO + " WHERE " + KEY_USR_USRNAME + " = ?", new String[]{username});

      if (cursor.getCount() < 1) {
          cursor.close();
          return "Not Exist";
      } else if (cursor.getCount() > 0 && cursor.moveToFirst()) {

          password = cursor.getString(cursor.getColumnIndex(KEY_USR_PWD));
          cursor.close();
      }
          return password;
      }


        // Adding One New place
    public void addListPlace(List<Place> modelList) {
        db.execSQL("DELETE FROM " + TABLE_PLACE); // refresh table content
        db.execSQL("VACUUM");
        for (int i = 0; i < modelList.size(); i++) {
            Place model = modelList.get(i);
            ContentValues values = getPlaceValue(model);
            db.insert(TABLE_PLACE, null, values); // Inserting Row
        }
    }

    // Update one place
    public Place updatePlace(Place place) {
        ContentValues values = getPlaceValue(place);
        db.update(TABLE_PLACE, values, KEY_PLACE_ID + "=" + place.place_id, null);
        if(isPlaceExist(place.place_id)){
            return getPlace(place.place_id);
        }
        return null;
    }

    private ContentValues getPlaceValue(Place model){
        ContentValues values = new ContentValues();
        values.put(KEY_PLACE_ID, model.place_id);
        values.put(KEY_NAME, model.name);
        values.put(KEY_IMAGE, model.image);
        values.put(KEY_ADDRESS, model.address);
        values.put(KEY_PHONE, model.phone);
        values.put(KEY_WEBSITE, model.website);
        values.put(KEY_DESCRIPTION, model.description);
        values.put(KEY_LNG, model.lng);
        values.put(KEY_LAT, model.lat);
        values.put(KEY_LAST_UPDATE, model.last_update);
        return values;
    }

    // Adding new location by Category
    public List<Place> searchAllPlace(String keyword) {
        List<Place> locList = new ArrayList<>();
        Cursor cur;
        if (keyword.equals("")) {
            cur = db.rawQuery("SELECT * FROM " + TABLE_PLACE + " ORDER BY " + KEY_LAST_UPDATE + " DESC", null);
        } else {
            cur = db.rawQuery("SELECT * FROM " + TABLE_PLACE + " WHERE " + KEY_NAME + " LIKE ? OR "+ KEY_ADDRESS + " LIKE ? OR "+ KEY_DESCRIPTION + " LIKE ? ",
                  new String[]{"%" + keyword + "%", "%" + keyword + "%", "%" + keyword + "%", "%" + keyword + "%"});
        }
        locList = getListPlaceByCursor(cur);
        return locList;
    }

    public List<Place> getAllPlace() {
        return searchAllPlace("");
    }

    public List<Place> getAllPlaceByCategory(int c_id) {
        List<Place> locList = new ArrayList<>();
        String query = "SELECT DISTINCT p.* FROM "+TABLE_PLACE+" p, "+TABLE_CATEGORY+" c WHERE p."+KEY_PLACE_ID
                      +" IN (SELECT pc."+KEY_PLACE_ID+" FROM "+TABLE_PLACE_CATEGORY+" pc WHERE pc."+KEY_RELATION_CAT_ID+"=?)"
                      +" ORDER BY p."+KEY_LAST_UPDATE+" DESC";
        Cursor cursor = db.rawQuery(query, new String[]{c_id+""});
        if (cursor.moveToFirst()) {
            locList = getListPlaceByCursor(cursor);
        }
        return locList;
    }

    public Place getPlace(int place_id) {
        String query = "SELECT * FROM " + TABLE_PLACE + " p WHERE p." + KEY_PLACE_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{place_id+""});
        cursor.moveToFirst();
        return getPlaceByCursor(cursor);
    }

    private List<Place> getListPlaceByCursor(Cursor cur) {
        List<Place> locList = new ArrayList<>();
        // looping through all rows and adding to list
        if (cur.moveToFirst()) {
            do {
                // Adding place to list
                locList.add(getPlaceByCursor(cur));
            } while (cur.moveToNext());
        }
        return locList;
    }

    private Place getPlaceByCursor(Cursor cur){
        Place p       = new Place();
        p.place_id    = cur.getInt(0);
        p.name        = cur.getString(1);
        p.image       = cur.getString(2);
        p.address     = cur.getString(3);
        p.phone       = cur.getString(4);
        p.website     = cur.getString(5);
        p.description = cur.getString(6);
        p.lng         = Double.parseDouble(cur.getString(7));
        p.lat         = Double.parseDouble(cur.getString(8));
        p.last_update = cur.getLong(9);
        return p;
    }

    // Get LIst Images By Place Id
    public List<Images> getListImageByPlaceId(int place_id) {
        List<Images> imageList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_IMAGES + " WHERE " + KEY_IMG_PLACE_ID + " = ?";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{place_id + ""});
        if (cursor.moveToFirst()) {
            do {
                Images img = new Images();
                img.place_id = cursor.getInt(0);
                img.name = cursor.getString(1);
                imageList.add(img);
            } while (cursor.moveToNext());
        }
        return imageList;
    }

    public Category getCategory(int c_id){
        Category category = new Category();
        try {
            Cursor cur = db.rawQuery("SELECT * FROM " + TABLE_CATEGORY + " WHERE " + KEY_CAT_ID + " = ?", new String[]{c_id + ""});
            cur.moveToFirst();
            category.cat_id = cur.getInt(0);
            category.name = cur.getString(1);
            category.icon = cur.getInt(2);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Db Error", e.toString());
            return null;
        }
        return category;
    }

    // Adding new imagesList
    public void addListImages(List<Images> images) {
        // refresh images table
        db.execSQL("DELETE FROM " + TABLE_IMAGES);
        db.execSQL("VACUUM");
        for (int i = 0; i < images.size(); i++) {
            ContentValues values = new ContentValues();
            values.put(KEY_IMG_PLACE_ID, images.get(i).place_id);
            values.put(KEY_IMG_NAME, images.get(i).name);
            db.insert(TABLE_IMAGES, null, values); // Inserting Row
        }
    }

    // Adding new Table PLACE_CATEGORY relational
    public void addListPlaceCategory(List<PlaceCategory> place_category) {
        // refresh category table
        db.execSQL("DELETE FROM " + TABLE_PLACE_CATEGORY);
        db.execSQL("VACUUM");
        for (int i = 0; i < place_category.size(); i++) {
            ContentValues values = new ContentValues();
            values.put(KEY_RELATION_PLACE_ID, place_category.get(i).place_id);
            values.put(KEY_RELATION_CAT_ID, place_category.get(i).cat_id);
            // Inserting Row
            db.insert(TABLE_PLACE_CATEGORY, null, values);
        }
    }


    // Adding new Connector
    public void addFavorites(int id) {
        ContentValues values = new ContentValues();
        values.put(KEY_PLACE_ID, id);
        // Inserting Row
        db.insert(TABLE_FAVORITES, null, values);
    }

    // all Favorites
    public List<Place> getAllFavorites() {
        List<Place> locList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT p.* FROM " + TABLE_PLACE + " p, " + TABLE_FAVORITES + " f" +" WHERE p." + KEY_PLACE_ID + " = f." + KEY_PLACE_ID, null);
        locList = getListPlaceByCursor(cursor);
        return locList;
    }

    public void deleteFavorites(int id) {
        if (isFavoritesExist(id)) {
            db.delete(TABLE_FAVORITES, KEY_PLACE_ID + " = ?", new String[]{id+""});
        }
    }

    public boolean isFavoritesExist(int id) {
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_FAVORITES + " WHERE " + KEY_PLACE_ID + " = ?", new String[]{id+""});
        int count = cursor.getCount();
        return count > 0;
    }

    private boolean isPlaceExist(int id) {
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PLACE + " WHERE " + KEY_PLACE_ID + " = ?", new String[]{id + ""});
        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }

    public int getCategorySize() {
        Cursor cursor = db.rawQuery("SELECT COUNT(" + KEY_CAT_ID + ") FROM " + TABLE_CATEGORY, null);
        cursor.moveToFirst();
        return cursor.getInt(0);
    }

    public int getFavoritesSize() {
        Cursor cursor = db.rawQuery("SELECT COUNT(" + KEY_PLACE_ID + ") FROM " + TABLE_FAVORITES, null);
        cursor.moveToFirst();
        return cursor.getInt(0);
    }

    public int getPlacesSize() {
        Cursor cursor = db.rawQuery("SELECT COUNT(" + KEY_PLACE_ID + ") FROM " + TABLE_PLACE, null);
        cursor.moveToFirst();
        return cursor.getInt(0);
    }
    public static String getTableContacts() {
        return TABLE_USERINFO;
    }
}
