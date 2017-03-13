package sai.developement.travelogue.asynctasks;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.orhanobut.logger.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import sai.developement.travelogue.BuildConfig;
import sai.developement.travelogue.models.Suggestion;

/**
 * Created by sai on 3/10/17.
 */

public class LoadSuggestionTaskLoader extends AsyncTaskLoader<List<Suggestion>> {

    private final static String RESPONSE = "response";
    private final static String GROUPS = "groups";
    private static final String ITEMS = "items";
    private static final String VENUE = "venue";
    private static final String NAME = "name";
    private static final String LOCATION = "location";
    private static final String ADDRESS = "address";
    private static final String CITY = "city";
    private static final String STATE = "state";
    private static final String POSTAL_CODE = "postalCode";
    private static final String PHOTOS = "photos";
    private static final String PREFIX = "prefix";
    private static final String SUFFIX = "suffix";
    private static final String HEIGHT = "height";
    private static final String WIDTH = "width";
    private static final String ID = "id";

    private static final String FOURSQUARE_BASE_URL =
            "https://api.foursquare.com/v2/venues/explore?client_id="+ BuildConfig.FOURSQUARE_CLIENT_ID
                    +"&client_secret="+BuildConfig.FOURSQUARE_CLIENT_SECRET+"&venuePhotos=1&v=20170307";

    private final String mExploreUrl;

    private final Context mContext;

    private final String mLocation;

    private final OkHttpClient client = new OkHttpClient();

    public LoadSuggestionTaskLoader(Context context, String location) {
        super(context);

        this.mLocation = location;
        mExploreUrl = FOURSQUARE_BASE_URL.concat("&near="+ location);
        this.mContext = context;
    }

    @Override
    public List<Suggestion> loadInBackground() {
        try {
            Request request = new Request.Builder()
                    .url(mExploreUrl)
                    .build();

            Response response = client.newCall(request).execute();
            if(response.code() != 200) {
                return new ArrayList<>();
            }
            final String responseString = response.body().string();
            Logger.d(responseString);


            return parseSuggestionJson(responseString);
        }
        catch(IOException e) {
            Logger.e("Error fetching suggestions from FourSquare", e.getLocalizedMessage());
            return null;
        }
    }



    private List<Suggestion> parseSuggestionJson(String responseString) {
        List<Suggestion> suggestionList = new ArrayList<>();
        try{
            JSONObject mainJson = new JSONObject(responseString);
            JSONObject responseJson = mainJson.getJSONObject(RESPONSE);
            JSONArray groupsArray = responseJson.getJSONArray(GROUPS);
            JSONObject groupObject = groupsArray.getJSONObject(0);

            JSONArray itemsArray = groupObject.getJSONArray(ITEMS);
            for(int i= 0; i < itemsArray.length(); i++) {
                Suggestion suggestion = new Suggestion();

                JSONObject item = itemsArray.getJSONObject(i);
                JSONObject venueObject = item.getJSONObject(VENUE);
                String id = venueObject.getString(ID);
                suggestion.setId(id);
                String suggestionname = venueObject.getString(NAME);
                suggestion.setName(suggestionname);

                JSONObject locationObject = venueObject.getJSONObject(LOCATION);
                String address = locationObject.getString(ADDRESS);
                /*if(locationObject.getString(CITY) != null) {
                    address += ", " + locationObject.getString(CITY);
                }
                if(locationObject.getString(STATE) != null) {
                    address += ", " + locationObject.getString(STATE);
                }
                if(locationObject.getString(POSTAL_CODE) != null) {
                    address += ", " + locationObject.getString(POSTAL_CODE);
                }*/
                suggestion.setAddress(address);

                JSONObject photosObject = venueObject.getJSONObject(PHOTOS);
                JSONArray photoGroupsArray = photosObject.getJSONArray(GROUPS);
                // lets take the first photo
                if(photoGroupsArray.length() > 0) {
                    JSONObject photoGroupObject = photoGroupsArray.getJSONObject(0);
                    JSONArray photoItemsObjectArray = photoGroupObject.getJSONArray(ITEMS);
                    if(photoItemsObjectArray.length() > 0) {
                        JSONObject photoItemObject = photoItemsObjectArray.getJSONObject(0);
                        String prefix = photoItemObject.getString(PREFIX);
                        String suffix = photoItemObject.getString(SUFFIX);
                        int width = photoItemObject.getInt(WIDTH);
                        int height = photoItemObject.getInt(HEIGHT);

                        String url = prefix + width + "x" + height + suffix;
                        suggestion.setPhotoUrl(url);
                    }
                }
                suggestionList.add(suggestion);
            }
        }
        catch (JSONException ex) {
            Logger.e("Error parsing suggestion json", ex.getLocalizedMessage());
        }

        return suggestionList;
    }
}
