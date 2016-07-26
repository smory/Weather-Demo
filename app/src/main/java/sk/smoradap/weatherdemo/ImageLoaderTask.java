package sk.smoradap.weatherdemo;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;

import sk.smoradap.weatherdemo.utils.ImageLoader;

/**
 * Task loads images from web into image view.
 * Created by smoradap on 24.07.2016.
 */
public class ImageLoaderTask extends AsyncTask<Void, Void, Drawable > {

    private String mUrl;
    private ImageView mImageView;

    public ImageLoaderTask(String url, ImageView imageView){
        mUrl = url;
        mImageView = imageView;
    }

    @Override
    protected Drawable doInBackground(Void... params) {

        if(mUrl != null){
            return ImageLoader.loadImageDrawable(mUrl);
        }
        return null;
    }

    @Override
    public void onPostExecute(Drawable d){
        if(d != null && mImageView != null){
            mImageView.setImageDrawable(d);
        }
    }
}
