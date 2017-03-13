package sai.developement.travelogue.asynctasks;

import android.os.AsyncTask;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.tasks.OnCompleteListener;
import com.orhanobut.logger.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import sai.developement.travelogue.BuildConfig;
import sai.developement.travelogue.helpers.FirebaseDatabaseHelper;
import sai.developement.travelogue.models.Trip;

/**
 * Created by sai on 3/12/17.
 */

public class LoadPlaceImageTask extends AsyncTask<Void, Void, Void> {

    private static final String PHOTOS = "photos";

    private static final String PHOTO = "photo";

    private static final String ID = "id";

    private static final String SECRET = "secret";

    private static final String SERVER = "server";

    private static final String FARM = "farm";

    private final Trip mTrip;

    private final Place mPlace;

    private final OnCompleteListener mOnCompleteListener;

    private final String FLICKR_API_URL = "https://api.flickr.com/services/rest/?api_key="+ BuildConfig.FLICKR_KEY +
            "&sort=interestingness-asc&format=json&accuracy=3&method=flickr.photos.search&nojsoncallback=1";

    private final String SEARCH_URL;

    private final OkHttpClient client = new OkHttpClient();

    public LoadPlaceImageTask(Trip trip, Place place, OnCompleteListener listener) {
        this.mTrip =  trip;
        this.mPlace = place;
        this.mOnCompleteListener = listener;

        SEARCH_URL = FLICKR_API_URL.concat("&lat="+mPlace.getLatLng().latitude+"&lon="+mPlace.getLatLng().longitude);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            Request request = new Request.Builder()
                    .url(SEARCH_URL)
                    .build();

            Response response = client.newCall(request).execute();
            if(response.code() != 200) {
                return null;
            }
            final String responseString = response.body().string();
            Logger.d(responseString);


            return parseSuggestionJson(responseString);
        }
        catch (IOException e){
            Logger.e("Error fetching photos from FLicke", e.getLocalizedMessage());
            return null;
        }
    }

    private Void parseSuggestionJson(String responseString) {
        try {
            JSONObject mainJson = new JSONObject(responseString);
            JSONObject photosObject = mainJson.getJSONObject(PHOTOS);
            JSONArray photoArray = photosObject.getJSONArray(PHOTO);

            if(photoArray.length() > 0) {
                JSONObject photoObject = photoArray.getJSONObject(0);
                String id = photoObject.getString(ID);
                String server = photoObject.getString(SERVER);
                String secret = photoObject.getString(SECRET);
                int farm = photoObject.getInt(FARM);

                String photoUrl = "https://farm"+farm+".staticflickr.com/"+server+"/"+id+"_"+secret+".jpg";

                mTrip.setPrimaryLocation(mPlace.getName().toString());
                mTrip.setPhotoUrl(photoUrl);
            }
        }
        catch (JSONException e) {
            Logger.e("Error parsing Flickr json", e.getLocalizedMessage());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        FirebaseDatabaseHelper.addNewTrip(mTrip, mOnCompleteListener);
    }
}
