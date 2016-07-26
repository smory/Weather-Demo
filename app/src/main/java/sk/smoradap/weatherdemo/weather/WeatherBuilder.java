package sk.smoradap.weatherdemo.weather;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import sk.smoradap.weatherdemo.db.City;

/**
 * Class builds weather model.
 */
public class WeatherBuilder {
	
	// constants for parsing JSON
	public static final String JSON_UNIX_TIME = "dt";
	public static final String JSON_WEATER_INFO_LIST = "list";
	public static final String JSON_TIME_STRING = "dt_txt";
	public static final String JSON_WEATHER_MAIN_INFO = "main";
	public static final String JSON_WEATHER_INFO = "weather";
	public static final String JSON_WEATHER_RAIN_INFO = "rain";
	public static final String JSON_WEATHER_SNOW_INFO = "snow";
	public static final String JSON_WEATHER_WIND_INFO = "wind";
	public static final String JSON_TEMP = "temp";
	public static final String JSON_MIN_TEMP = "temp_min";
	public static final String JSON_MAX_TEMP = "temp_max";
	public static final String JSON_HUMIDITY = "humidity";
	public static final String JSON_PRESSURE = "pressure";
	public static final String JSON_DESCRIPTION = "description";
	public static final String JSON_ICON = "icon";
	public static final String JSON_RAIN_3H = "3h";
	public static final String JSON_WIND_SPEED = "speed";
	public static final String JSON_SNOW_3H = "3h";
	

	/**
	 * Method will parse Json string from Openweathermap and create weather models
	 * @param json json response from Openweathermap
	 * @return list of Weather models
	 */
	public static List<Weather> buildWeatherList(JSONObject json, boolean userSearch){
				
		LinkedList<Weather> weathers = new LinkedList<>();
		
		try {
			City city = buildCityFromJson(json);
			city.setFromUserSearch(userSearch);
			JSONArray array = json.getJSONArray(JSON_WEATER_INFO_LIST);
			
			Weather weather = null;
			Calendar lastCalendar = null;			
			
			for(int i = 0; i < array.length(); i++){
				JSONObject weatherJson = array.getJSONObject(i);
				Long time = weatherJson.getLong(JSON_UNIX_TIME) * 1000;
				Calendar c = Calendar.getInstance();
				c.setTimeInMillis(time);
				c.set(Calendar.HOUR_OF_DAY, 0);
				
				if(lastCalendar == null || !lastCalendar.equals(c)){
					lastCalendar = c;
					weather = new Weather();
					weather.setCity(city);
					weather.setCaledar(lastCalendar);
					weathers.add(weather);
				}
				
				jsonToWeatherObjectsInfos(weather, weatherJson);
			
			}
			
			System.out.println(weathers);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return weathers;
	}

	/**
	 * Checks json for error responses.
	 * @param json
	 * @return error string or null if no error found
     */
	public static String checkJsonForErrors(JSONObject json){
		String error = null;
		try {
			int code = json.getInt("cod");
			if(code != 200){
				error = code + ": " + json.getString("message");
			}
		} catch (JSONException e) {
			//no need to handle
			e.printStackTrace();
		}

		return error;
	}


	/**
	 * Builds inner weather objects per json list parameter.
	 * @param weather weather object to which info objects should be added
	 * @param json Json list object
	 * @throws JSONException in case of parsing error
	 */
	private static void jsonToWeatherObjectsInfos(Weather weather, JSONObject json) throws JSONException {
		
		String time = json.getString(JSON_TIME_STRING).split("\\s")[1].substring(0, 5);
		
		weather.addMainInfo(buildMainInfo(json.getJSONObject(JSON_WEATHER_MAIN_INFO)), time);
		weather.addWeatherInfos(buildWeatherInfo(json.getJSONArray(JSON_WEATHER_INFO)), time);
		
		// these are optional and may be missing therefore catch supplies null
		try{
			weather.addRainInfos(buildRainInfo(json.getJSONObject(JSON_WEATHER_RAIN_INFO)), time);
		} catch(Exception e){
			weather.addRainInfos(null, time);
		}
		try{
			weather.addSnowInfos(buildSnowInfo(json.getJSONObject(JSON_WEATHER_SNOW_INFO)), time);
		} catch(Exception e){
			weather.addSnowInfos(null, time);
		}
		try{
			weather.addWindInfos(buildWindInfo(json.getJSONObject(JSON_WEATHER_WIND_INFO)), time);
		} catch(Exception e){
			weather.addWindInfos(null, time);
		}
		
	}

	private static Weather.MainInfo buildMainInfo(JSONObject json) throws JSONException{
		Weather.MainInfo mi = new Weather.MainInfo();
		mi.setTemperature(json.getDouble(JSON_TEMP));
		mi.setMinTemperature(json.getDouble(JSON_MIN_TEMP));
		mi.setMaxTemperature(json.getDouble(JSON_MAX_TEMP));
		mi.setPressure(json.getDouble(JSON_HUMIDITY));
		mi.setPressure(json.getDouble(JSON_PRESSURE));		
		return mi;
	}
	
	private static Weather.WeatherInfo buildWeatherInfo(JSONArray array) throws JSONException{
		Weather.WeatherInfo wi = new Weather.WeatherInfo();
		JSONObject json = array.getJSONObject(0);
		wi.setMain(json.getString(JSON_WEATHER_MAIN_INFO));
		wi.setDescription(json.getString(JSON_DESCRIPTION));
		wi.setIcon(json.getString(JSON_ICON));
		return wi;
	}
	
	private static Weather.RainInfo buildRainInfo(JSONObject json) throws JSONException {
		Weather.RainInfo ri = new Weather.RainInfo();
		ri.setH(json.getDouble(JSON_RAIN_3H));
		return ri;
	}
	
	private static Weather.SnowInfo buildSnowInfo(JSONObject json) throws JSONException {
		Weather.SnowInfo ri = new Weather.SnowInfo();
		ri.setH(json.getDouble(JSON_SNOW_3H));
		return ri;
	}
	
	private static Weather.WindInfo buildWindInfo(JSONObject json) throws JSONException{
		Weather.WindInfo wi = new Weather.WindInfo();
		wi.setSpeed(json.getDouble(JSON_WIND_SPEED));
		return wi;
	}

	private static City buildCityFromJson(JSONObject json) throws JSONException {
		City city = new City();
		JSONObject cityObject = json.getJSONObject("city");
		city.setId(cityObject.getLong("id"));
		city.setName(cityObject.getString("name"));
		city.setCountry(cityObject.getString("country"));
		JSONObject coordObject = cityObject.getJSONObject("coord");
		city.setLatitude(coordObject.getDouble("lat"));
		city.setLongitude(coordObject.getDouble("lon"));

		return city;
	}
	
}
