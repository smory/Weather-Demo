package sk.smoradap.weatherdemo.db;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

/**
 * Model for onformation about cities
 */
public class City {
	
	private long id;
	private String name;
	private String country;
	private double longitude;
	private double latitude;

	private transient boolean fromUserSearch = false; // used to indicate that serch for made by user
	   // to iindicate that it should be saved to a file
	
	
	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}
	/**
	 * @param country the country to set
	 */
	public void setCountry(String country) {
		this.country = country;
	}
	/**
	 * @return the longitude
	 */
	public double getLongitude() {
		return longitude;
	}
	/**
	 * @param longitude the longitude to set
	 */
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	/**
	 * @return the latitude
	 */
	public double getLatitude() {
		return latitude;
	}
	/**
	 * @param latitude the latitude to set
	 */
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	@Override
	public String toString() {
		return "City [id=" + id + ", name=" + name + ", country=" + country
				+ ", longitude=" + longitude + ", latitude=" + latitude + "]";
	}

	/**
	 * Creates a json string from this object
	 * @return json string
     */
	public String asJsonString(){
		String s = "{\"_id\":%d,\"name\":\"%s\",\"country\":\"%s\",\"coord\":{\"lon\":%f,\"lat\":%f}}";
		return String.format(Locale.US, s, getId(), getName(), getCountry(), getLongitude(), getLatitude());
	}

	/**
	 * Creates City object from JSON string
	 * @param data string containing the data
	 * @return city object
	 * @throws JSONException in case of JSON issues
     */
	public static City fromJsonString(String data) throws JSONException {
		City c = new City();
		JSONObject json = new JSONObject(data);
		c.setId(json.getLong("_id"));
		c.setName(json.getString("name"));
		c.setCountry(json.getString("country"));
		JSONObject coordinates = json.getJSONObject("coord");
		c.setLatitude(coordinates.getDouble("lat"));
		c.setLongitude(coordinates.getDouble("lon"));
		return c;
	}

	public boolean isFromUserSearch() {
		return fromUserSearch;
	}

	public void setFromUserSearch(boolean fromUserSearch) {
		this.fromUserSearch = fromUserSearch;
	}
}
