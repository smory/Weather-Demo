package sk.smoradap.weatherdemo.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Helper to create SQLite database
 */
public class CitesSQLiteOpenHelper extends SQLiteOpenHelper {
	
	public static final String TABLE_CITY_LIST = "city_list";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_COUNTRY = "country";
    public static final String COLUMN_LONGITUDE = "lon";
    public static final String COLUMN_LATITUDE = "lat";

    private static final String DATABASE_NAME = "cities.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table " + TABLE_CITY_LIST + "( "
    		+ COLUMN_ID + " integer primary key not null, "
    		+ COLUMN_NAME + " text not null,"
    		+ COLUMN_COUNTRY + " text not null,"
    		+ COLUMN_LONGITUDE + " real,"
    		+ COLUMN_LATITUDE + " real" + ");";
    
    public CitesSQLiteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
        
    @Override
    public void onCreate(SQLiteDatabase database) {
            database.execSQL(DATABASE_CREATE);
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CITY_LIST);
            onCreate(db);
    }
}


