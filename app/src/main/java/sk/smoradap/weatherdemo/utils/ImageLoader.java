package sk.smoradap.weatherdemo.utils;

import android.graphics.drawable.Drawable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;

/**
 * Class provide cache or image loading and loading of images.
 */
public class ImageLoader {

    private static HashMap<String, byte[]> imageCache = new HashMap<>();

    private static byte[] loadImageAsByteArray(String url){
        if(imageCache.containsKey(url)){
            return imageCache.get(url);
        } else {
            try{
                byte[] bytes = HttpUtils.loadUrlContentAsByteArray(url, null, null);
                imageCache.put(url, bytes);
                return bytes;
            } catch(IOException e){
                return null;
            }

        }
    }

    /**
     * Loads image from url, stores it in case and returns drawable.
     * @param url
     * @return
     */
    public static Drawable loadImageDrawable(String url) {
        byte[] bytes = loadImageAsByteArray(url);

        if (bytes == null) {
            return null;
        } else {
            return Drawable.createFromStream(new ByteArrayInputStream(bytes), "scr");
        }
    }


}
