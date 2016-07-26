package sk.smoradap.weatherdemo.weather;

import java.util.Map;


public class WeatherUtils {
	
	public static double findMinTemperature(Weather weather){
		Map<String, Weather.MainInfo> data = weather.getMainInfos();
		Double temp = null;
		for(Weather.MainInfo mi : data.values()){
			if(temp == null){
				temp = mi.getMinTemperature();
			} else {
				if(temp > mi.getMinTemperature()){
					temp = mi.getMinTemperature();
				}
			}
		}
		return temp;
	}
	
	public static double findMaxTemperature(Weather weather){
		Map<String, Weather.MainInfo> data = weather.getMainInfos();
		Double temp = null;
		for(Weather.MainInfo mi : data.values()){
			if(temp == null){
				temp = mi.getMaxTemperature();
			} else {
				if(temp < mi.getMaxTemperature()){
					temp = mi.getMaxTemperature();
				}
			}
		}
		return temp;
	}

	public static double averageWindSpeed(Weather weather) throws Exception {
		Map<String, Weather.WindInfo> windInfos = weather.getWindInfos();

		if(windInfos == null || windInfos.isEmpty()){
			throw new Exception("No data to count");
		}

		double d = 0;
		for(Weather.WindInfo i: windInfos.values()){
			d += i.getSpeed();
		}

		return d / windInfos.size();

	}

	public static double averageHumidity(Weather weather) throws Exception {
		Map<String, Weather.MainInfo> infos = weather.getMainInfos();

		if(infos == null || infos.isEmpty()){
			throw new Exception("No data to count");
		}

		double d = 0;
		for(Weather.MainInfo i: infos.values()){
			d += i.getHumidity();
		}

		return d / infos.size();

	}

}
