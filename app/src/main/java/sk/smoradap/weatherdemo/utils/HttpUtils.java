package sk.smoradap.weatherdemo.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Class provides simple to use http methods
 */
public class HttpUtils {

    /**
     * Method will perform simple get request.
     * @param url url for the request
     * @param params request parameters
     * @param headers headers to be included in the request
     * @return response body as string
     * @throws IOException in case of connection error or wrong url
     */
    public static String simpleGet(String url, Map<String, String> params, Map<String, String> headers) throws IOException {

        //set up params if provided
    	url = addParamsToGet(url, params);

        HttpURLConnection connection = null;
        try {
            URL u = new URL(url);
            connection = (HttpURLConnection) u.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);

            // set up headers if provided
            addHeadersToRequest(connection, headers);

            InputStream is = connection.getInputStream();
            InputStreamReader stream = new InputStreamReader(is);
            int arraySize = 2048;
            StringBuilder builder = new StringBuilder();
            char[] chars = new char[arraySize];

            int i;
            while ((i = (stream.read(chars))) > 0) {
                builder.append(chars, 0, i);
                if (i < arraySize) {
                    chars = new char[arraySize];
                }
            }
            stream.close();
            return builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }



    /**
     * Method will perform simple get request and return response as byte array.
     * @param url url for the request
     * @param params request parameters
     * @param headers headers to be included in the request
     * @return response body as byte[]
     * @throws IOException in case of connection error or wrong url
     */
    public static byte[] loadUrlContentAsByteArray(String url, Map<String, String> params, Map<String, String> headers) throws IOException {
        //set up params if provided
        url = addParamsToGet(url, params);

        HttpURLConnection connection = null;
        try {
            URL u = new URL(url);
            connection = (HttpURLConnection) u.openConnection();
            connection.setRequestMethod("GET");

            // set up headers if provided
            addHeadersToRequest(connection, headers);

            InputStream is = connection.getInputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int arraySize = 4096;
            byte[] bytes = new byte[arraySize];

            int i;
            while ((i = (is.read(bytes))) > 0) {
                baos.write(bytes, 0, i);
            }

            is.close();
            baos.close();
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public static HttpURLConnection addHeadersToRequest(HttpURLConnection connection, Map<String, String> headers){
    	if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> e : headers.entrySet()) {
                connection.setRequestProperty(e.getKey(), e.getValue());
            }
        }
    	return connection;
    }

    private static String addParamsToGet(String url, Map<String, String> params){
    	//set up params if provided
        if (params != null && !params.isEmpty()) {
            StringBuilder s = new StringBuilder(url);
            if (!url.endsWith("?")) {
                s.append("?");
            }
            for (Map.Entry<String, String> e : params.entrySet()) {
                try {
					s.append(String.format("&%s=%s", URLEncoder.encode(e.getKey(), "UTF-8"),
					        URLEncoder.encode(e.getValue(), "UTF-8")));
				} catch (UnsupportedEncodingException e1) {
					//no need to handle in case of UTF-8
				}
            }
            System.out.println(s);
            return s.toString();
        }
        return url;
    }

}