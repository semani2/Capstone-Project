package sai.developement.travelogue.helpers;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by sai on 3/3/17.
 */

public class GenerateGUIDHelper {

    public enum Model {
        TRIP,
        TRIP_DAY,
        TRIP_VISIT,
        TRIP_MESSAGE
    }

    public static final String generateGUID(Model model) {
        if(FirebaseAuth.getInstance() != null && FirebaseAuth.getInstance().getCurrentUser() != null) {
            StringBuilder builder = new StringBuilder(model.name());
            builder.append("_");
            builder.append(FirebaseAuth.getInstance().getCurrentUser().getUid());
            builder.append("_");
            builder.append(System.currentTimeMillis());

            return builder.toString();
        }
        return null;
    }
}
