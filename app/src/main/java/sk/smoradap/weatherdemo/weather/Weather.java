package sk.smoradap.weatherdemo.weather;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TreeMap;

import sk.smoradap.weatherdemo.db.City;

/**
 * Model for the information for the weather condition in a given day
 */
public class Weather {

    // constants for the times used as keys in the maps.
    public static final String TIME_0000 = "00:00";
    public static final String TIME_0300 = "03:00";
    public static final String TIME_0600 = "06:00";
    public static final String TIME_0900 = "09:00";
    public static final String TIME_1200 = "12:00";
    public static final String TIME_1500 = "15:00";
    public static final String TIME_1800 = "18:00";
    public static final String TIME_2100 = "21:00";

    // date of the weather forecast
    private Calendar caledar;

    /*
     * Maps holding models and time for which forecasts were made.
     */
    private TreeMap<String, MainInfo> mainInfos = new TreeMap<>();
    private TreeMap<String, WeatherInfo> weatherInfos = new TreeMap<>();
    private TreeMap<String, WindInfo> windInfos = new TreeMap<>();
    private TreeMap<String, RainInfo> rainInfos = new TreeMap<>();
    private TreeMap<String, SnowInfo> snowInfos = new TreeMap<>();

    private City city;


    public Calendar getCaledar() {
        return caledar;
    }

    public void setCaledar(Calendar caledar) {
        this.caledar = caledar;
    }

    public TreeMap<String, MainInfo> getMainInfos() {
        return mainInfos;
    }

    void addMainInfo(MainInfo mainInfo, String time) {
        mainInfos.put(time, mainInfo);
    }

    public TreeMap<String, WeatherInfo> getWeatherInfos() {
        return weatherInfos;
    }

    void addWeatherInfos(WeatherInfo info, String time) {
        weatherInfos.put(time, info);
    }


    public TreeMap<String, WindInfo> getWindInfos() {
        return windInfos;
    }

    void addWindInfos(WindInfo info, String time) {
        windInfos.put(time, info);
    }

    public TreeMap<String, RainInfo> getRainInfos() {
        return rainInfos;
    }

    void addRainInfos(RainInfo info, String time) {
        rainInfos.put(time, info);
    }

    public TreeMap<String, SnowInfo> getSnowInfos() {
        return snowInfos;
    }

    void addSnowInfos(SnowInfo info, String time) {
        snowInfos.put(time, info);
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd. MM. yyyy");
        return "Weather [caledar=" + sdf.format(caledar.getTime()) + ", mainInfos=" + mainInfos
                + ", weatherInfos=" + weatherInfos + ", windInfos=" + windInfos
                + ", rainInfos=" + rainInfos + ", snowInfos=" + snowInfos + "]";
    }

    /**
     * Main info model
     */
    public static class MainInfo {

        private double temperature;
        private double minTemperature;
        private double maxTemperature;
        private double humidity;
        private double pressure;

        public double getTemperature() {
            return temperature;
        }

        public void setTemperature(double temperature) {
            this.temperature = temperature;
        }

        public double getMinTemperature() {
            return minTemperature;
        }

        public void setMinTemperature(double minTemperature) {
            this.minTemperature = minTemperature;
        }

        public double getMaxTemperature() {
            return maxTemperature;
        }

        public void setMaxTemperature(double maxTemperature) {
            this.maxTemperature = maxTemperature;
        }

        public double getHumidity() {
            return humidity;
        }

        public void setHumidity(double humidity) {
            this.humidity = humidity;
        }

        public double getPressure() {
            return pressure;
        }

        public void setPressure(double pressure) {
            this.pressure = pressure;
        }

        @Override
        public String toString() {
            return "MainInfo [temperature=" + temperature + ", minTemperature="
                    + minTemperature + ", maxTemperature=" + maxTemperature
                    + ", humidity=" + humidity + ", pressure=" + pressure + "]";
        }


    }

    /**
     * Weather info model
     */
    public static class WeatherInfo {

        String main;
        String description;
        String icon;

        public String getMain() {
            return main;
        }

        public void setMain(String main) {
            this.main = main;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        @Override
        public String toString() {
            return "WeatherInfo [main=" + main + ", description=" + description
                    + ", icon=" + icon + "]";
        }


    }

    /**
     * Wind info model
     */
    static class WindInfo {

        private double speed;

        public double getSpeed() {
            return speed;
        }

        public void setSpeed(double speed) {
            this.speed = speed;
        }

        @Override
        public String toString() {
            return "WindInfo [speed=" + speed + "]";
        }


    }

    /**
     * Rain info model
     */
    static class RainInfo {

        private double h;

        public double getH() {
            return h;
        }

        public void setH(double h) {
            this.h = h;
        }

    }

    /**
     * Snow info model
     */
    static class SnowInfo {

        private double h;

        public double getH() {
            return h;
        }

        public void setH(double h) {
            this.h = h;
        }

        @Override
        public String toString() {
            return "SnowInfo [h=" + h + "]";
        }


    }
}
