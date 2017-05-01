package hfad.com.photogallery;

import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mephisto on 4/4/2017.
 */

public class FlickrFetchr {
    public static final String TAG = "FlickrFetchr";
    public static final String API_KEY = "e2500d36609f24591c314a4bff70c538";
    public static final String FETCH_RECENT_METHOD = "flickr.photos.getRecent";
    public static final String SEARCH_METHOD = "flickr.photos.search";
    private static final Uri ENDPOINT = Uri
            .parse("https://api.flickr.com/services/rest/")
            .buildUpon()
            .appendQueryParameter("api_key", API_KEY)
            .appendQueryParameter("format", "json")
            .appendQueryParameter("nojsoncallback", "1")
            .appendQueryParameter("extras", "url_s")
            .build();
    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream inputStream = connection.getInputStream();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() +
                        ": with " + urlSpec);
            }
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = inputStream.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }
    public List<GalleryItem> fetchRecentPhotos(){
        String url = buildUrl(FETCH_RECENT_METHOD, null);
        return downloadGalleryItems(url);
    }

    public List<GalleryItem> searchPhotos(String query){
        String url = buildUrl(SEARCH_METHOD, query);
        return downloadGalleryItems(url);
    }

    public List<GalleryItem> downloadGalleryItems(String url) {
        List<GalleryItem> items = new ArrayList<>();
        try {

            String jsonString = getUrlString(url);
//            Log.i(TAG, "Receive JSON: " + jsonString);
            Log.d(TAG, "received JSON: " + jsonString);
            parseItemsGSON(items, jsonString);
        } catch (JSONException je) {
            Log.e(TAG, "Failed to parse JSON", je);
        } catch (IOException e) {
            Log.e(TAG, "Failed to fetch items", e);
        }
        return items;
    }
    private String buildUrl(String method, String query){
        Uri.Builder builder = ENDPOINT.buildUpon()
                .appendQueryParameter("method", method);
        if(method.equals(SEARCH_METHOD)) {
            builder.appendQueryParameter("text", query);
        }
        return builder.build().toString();
    }

    private void parseItemsGSON(List<GalleryItem> items, String jsonBody) throws IOException, JSONException {
        Gson gson = new GsonBuilder().create();
        Flickr flickr = gson.fromJson(jsonBody, Flickr.class);
        for (Photo p : flickr.photos.photo) {
            GalleryItem item = new GalleryItem();
            item.setId(p.id);
            item.setCaption(p.title);
            item.setOwner(p.owner);
            item.setUrl(p.url_s);
            items.add(item);
        }
    }

}
