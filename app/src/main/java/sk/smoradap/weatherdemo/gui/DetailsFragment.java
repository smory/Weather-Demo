package sk.smoradap.weatherdemo.gui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import sk.smoradap.weatherdemo.Consts;
import sk.smoradap.weatherdemo.ImageLoaderTask;
import sk.smoradap.weatherdemo.R;
import sk.smoradap.weatherdemo.weather.Weather;
import sk.smoradap.weatherdemo.weather.WeatherUtils;

/**
 * Fragments shows datails concerning the weather
 * Created by smora on 24.07.2016.
 */
public class DetailsFragment extends Fragment {

    private View mMainView;
    private Weather mWeather;
    private HorizontalScrollView mScrollViewTempTime;
    private TextView mDetailsMinTemp;
    private TextView mDetailsMaxTemp;
    private TextView mDetailHumidity;
    private TextView mDetailWindSpeed;
    private LinearLayout mDetailTimeTempHostLayout;

    @Override
    public void onStart(){
        super.onStart();
        if(mWeather != null){
            setData();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.fragment_details, container, false);
        return mMainView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mScrollViewTempTime = (HorizontalScrollView) mMainView.findViewById(R.id.time_temp_sv);
        mDetailTimeTempHostLayout = (LinearLayout) mScrollViewTempTime.findViewById(R.id.details_time_temp_host_ll);
        mDetailsMinTemp = (TextView) mMainView.findViewById(R.id.detail_min_tv);
        mDetailsMaxTemp = (TextView) mMainView.findViewById(R.id.details_max_tv);
        mDetailHumidity = (TextView) mMainView.findViewById(R.id.details_humidity_tv);
        mDetailWindSpeed = (TextView) mMainView.findViewById(R.id.details_wind_speed_tv);
    }

    /**
     * Method polutes individual wiews with data
     */
    private void setData(){
        double minTemp = WeatherUtils.findMinTemperature(mWeather);
        double maxTemp = WeatherUtils.findMaxTemperature(mWeather);
        mDetailsMinTemp.setText(String.format(getString(R.string.weather_format_celsius), minTemp));
        mDetailsMaxTemp.setText(String.format(getString(R.string.weather_format_celsius), maxTemp));

        try{
            double humidity = WeatherUtils.averageHumidity(mWeather);
            mDetailHumidity.setText(String.format(getString(R.string.weather_format_humidity), humidity));
        } catch (Exception e){
            mDetailHumidity.setText(getString(R.string.weather_value_not_available));
        }

        try{
            double windSpeed = WeatherUtils.averageWindSpeed(mWeather);
            mDetailWindSpeed.setText(String.format(getString(R.string.weather_format_wind_speed), windSpeed));
        } catch (Exception e){
            mDetailWindSpeed.setText(getString(R.string.weather_value_not_available));
        }


        mDetailTimeTempHostLayout.removeAllViews();

        for(String key: mWeather.getMainInfos().keySet()){
            View v = getActivity().getLayoutInflater().inflate(R.layout.details_time_temp, mDetailTimeTempHostLayout, false);
            ((TextView) v.findViewById(R.id.details_time_tv)).setText(key);
            String temp = String.format(getString(R.string.weather_format_celsius), mWeather.getMainInfos().get(key).getTemperature() );
            ((TextView) v.findViewById(R.id.details_temp_tv)).setText(temp);
            mDetailTimeTempHostLayout.addView(v);
            ImageView iw = (ImageView) v.findViewById(R.id.details_icon_iw);
            ImageLoaderTask task = new ImageLoaderTask(Consts.ICON_URL + mWeather.getWeatherInfos().get(key).getIcon() + ".png", iw);
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
        }

    }

    /**
     * Method initiate update of the detail views
     * @param wetherDetails model
     */
    public void setWetherDetails(Weather wetherDetails){
        mWeather = wetherDetails;
        if(isVisible()){
            setData();
        }
    }



}
