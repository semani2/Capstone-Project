package sai.developement.travelogue.helpers;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import sai.developement.travelogue.R;


/**
 * Created by sai on 3/14/17.
 */

public class PlaceHolderImageHelper {

    public static final Map<Integer, Integer> PLACE_HOLDER;

    static {

        Map<Integer, Integer> map = new HashMap<>();
        map.put(0, R.drawable.image_1);
        map.put(1, R.drawable.image_2);
        map.put(2, R.drawable.image_3);
        map.put(3, R.drawable.image_4);
        map.put(4, R.drawable.image_5);
        map.put(5, R.drawable.image_6);
        map.put(6, R.drawable.image_7);
        map.put(7, R.drawable.image_8);

        PLACE_HOLDER = Collections.unmodifiableMap(map);
    }

    public static int getPlaceHolderImage() {
        return PLACE_HOLDER.get(getRandomNum());
    }

    private static int getRandomNum() {
        return ThreadLocalRandom.current().nextInt(0, 8);
    }
}
