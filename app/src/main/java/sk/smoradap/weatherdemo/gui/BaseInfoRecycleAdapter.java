package sk.smoradap.weatherdemo.gui;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import sk.smoradap.weatherdemo.Consts;
import sk.smoradap.weatherdemo.ImageLoaderTask;
import sk.smoradap.weatherdemo.R;
import sk.smoradap.weatherdemo.weather.Weather;
import sk.smoradap.weatherdemo.weather.WeatherUtils;


/**
 * Adapter for RecyclerView showing basic information for the weather.
 */
public class BaseInfoRecycleAdapter extends RecyclerView.Adapter<BaseInfoRecycleAdapter.ViewHolder> {

    Context mContext;
    List<Weather> mWeatherList;
    private OnWeatherDateSelectionListener mListener;

    public BaseInfoRecycleAdapter(Context context, List<Weather> weatherList, OnWeatherDateSelectionListener listener) {
        this.mContext = context;
        this.mWeatherList = weatherList;
        mListener = listener;
    }

    @Override
    public BaseInfoRecycleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater vi = LayoutInflater.from(parent.getContext());
        View v = vi.inflate(R.layout.basic_info_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
    }

    @Override
    public void onBindViewHolder(final BaseInfoRecycleAdapter.ViewHolder holder, final int position) {
        System.out.println("onBindViewHolder");
        Weather weather = mWeatherList.get(position);
        double minTemperature = WeatherUtils.findMinTemperature(weather);
        double maxTemperature = WeatherUtils.findMaxTemperature(weather);
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd. MM. yyyy");

        ((TextView) holder.mView.findViewById(R.id.week_day)).setText(sdf.format(weather.getCaledar().getTime()));
        ((TextView) holder.mView.findViewById(R.id.nin_temp)).setText(String.format(mContext.getString(R.string.weather_format_celsius), minTemperature));
        ((TextView) holder.mView.findViewById(R.id.max_temp)).setText(String.format(mContext.getString(R.string.weather_format_celsius), maxTemperature));
        TextView weatherOnitions = (TextView) holder.mView.findViewById(R.id.weather_condition);

        Weather.WeatherInfo wi;
        if (position == 0) {
            wi = weather.getWeatherInfos().firstEntry().getValue();
        } else {
            wi = weather.getWeatherInfos().get(Weather.TIME_1200) == null ?
                    weather.getWeatherInfos().firstEntry().getValue() :
                    weather.getWeatherInfos().get(Weather.TIME_1200);
        }
        weatherOnitions.setText(wi.getDescription());

        ImageView iw = (ImageView) holder.mView.findViewById(R.id.weather_icon_iw);
        ImageLoaderTask task = new ImageLoaderTask(Consts.ICON_URL + wi.getIcon() + ".png", iw);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                mListener.onWeatherDateSelected(mWeatherList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mWeatherList.size();
    }

    /**
     * Interface for receiving click events from cells.
     */
    public interface OnWeatherDateSelectionListener {
        void onWeatherDateSelected(Weather weather);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;

        public ViewHolder(View v) {
            super(v);
            mView = v;
        }
    }
}
