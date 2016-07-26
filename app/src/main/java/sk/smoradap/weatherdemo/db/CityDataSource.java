package sk.smoradap.weatherdemo.db;

import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Class provides an easy access to the sqlite database
 */
public class CityDataSource {
	
	private SQLiteDatabase database;
    private CitesSQLiteOpenHelper dbHelper;
    
    
    public CityDataSource(Context context) {
        dbHelper = new CitesSQLiteOpenHelper(context);
    }

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();	        
	}
	
	public void insertCity(City city){
		ContentValues values = new ContentValues();
		values.put(CitesSQLiteOpenHelper.COLUMN_ID, city.getId());
		values.put(CitesSQLiteOpenHelper.COLUMN_NAME, city.getName());
		values.put(CitesSQLiteOpenHelper.COLUMN_COUNTRY, city.getCountry());
		values.put(CitesSQLiteOpenHelper.COLUMN_LATITUDE, city.getLatitude());
		values.put(CitesSQLiteOpenHelper.COLUMN_LONGITUDE, city.getLongitude());
		database.insert(CitesSQLiteOpenHelper.TABLE_CITY_LIST, null, values);
		
	}
	
	public List<City> findCity(String name){
		String q = "SELECT * FROM " + CitesSQLiteOpenHelper.TABLE_CITY_LIST + " WHERE " 
			+ CitesSQLiteOpenHelper.COLUMN_NAME + " LIKE '" + name + "%';";
		Cursor cursor = database.rawQuery(q, null);
		LinkedList<City> list = new LinkedList<>();
		
		for(int i = 0; i < cursor.getCount(); i ++){
			list.add(cursorToCity(cursor));
		}
		
		return list;
	}
	
	private City cursorToCity(Cursor cursor) {
        City city = new City();
        city.setId(cursor.getLong(0));
        city.setName(cursor.getString(1));
        city.setCountry(cursor.getString(2));
        city.setLongitude(cursor.getFloat(3));
        city.setLatitude(cursor.getFloat(4));
        return city;
	}
	    
    

}
