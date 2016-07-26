package sk.smoradap.weatherdemo.gui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import sk.smoradap.weatherdemo.R;
import sk.smoradap.weatherdemo.weather.Weather;

/**
 * Fragment to hold recycler view showing basic information about weather.
 */
public class BasicInfoRecyclerFragment extends Fragment implements BaseInfoRecycleAdapter.OnWeatherDateSelectionListener {

    private RecyclerView mRecyclerView;
    private WeatherDateSelectedListener mCallback;
    private View mMainView;
    private BaseInfoRecycleAdapter mAdapter;

    @Override
    public void onStart(){
        super.onStart();
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.fragment_basic_info, container, false);
        return mMainView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRecyclerView = (RecyclerView) mMainView.findViewById(R.id.recycle_view_basic_info);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (WeatherDateSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement WeatherDateSelectedListener");
        }
    }

    @Override
    public void onStop(){
        super.onStop();
        mRecyclerView.setAdapter(null);
    }

    /**
     * Method updates recycler view with provided list of weather objects
     * @param list
     */
    public void setWeatherList(List<Weather> list){
        if(list == null || list.isEmpty()){
            mAdapter = null;
        } else {
            mAdapter = new BaseInfoRecycleAdapter(getContext(), list, this);
        }

        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onWeatherDateSelected(Weather weather) {
        mCallback.onWeatherDateSelected(weather);
    }

    /**
     * Interface provides callback when user selects an item
     */
    public interface WeatherDateSelectedListener{

        /**
         * Received information that user has picked an item
         * @param weather model which holds details of the weather
         */
        void onWeatherDateSelected(Weather weather);
    }
}
