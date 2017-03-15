package sai.developement.travelogue.asynctasks;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;

import sai.developement.travelogue.Constants;

/**
 * Created by sai on 3/15/17.
 */

public class AvatarsTaskLoader extends AsyncTaskLoader<List<Integer>> {
    public AvatarsTaskLoader(Context context) {
        super(context);
    }

    @Override
    public List<Integer> loadInBackground() {
        return Constants.getAvatars();
    }
}
