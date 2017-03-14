package sai.developement.travelogue;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by sai on 3/9/17.
 */

public class CurrentUser {
    private String userId;
    private String userName;
    private String userEmail;
    private int userAvatarId;

    public static CurrentUser sCurrentuser = null;

    public static final String USER_ID_KEY = "user_id";

    private CurrentUser() {}

    public static void setCurrentUser(Context context, String userId, String userName, String userEmail) {
        if(sCurrentuser == null) {
            sCurrentuser = new CurrentUser();
            sCurrentuser.setUserId(userId);
            sCurrentuser.setUserName(userName);
            sCurrentuser.setUserEmail(userEmail);

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(USER_ID_KEY, userId);
            editor.apply();
        }
    }

    public static void setCurrentUserAvatarId(int avatarId) {
        if(sCurrentuser != null) {
            sCurrentuser.setUserAvatarId(avatarId);
        }
    }

    public static CurrentUser getCurrentuser() {
        return sCurrentuser;
    }

    public static void deleteCurrentUser(Context context) {
        sCurrentuser = null;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER_ID_KEY, null);
        editor.apply();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public int getUserAvatarId() {
        return userAvatarId;
    }

    public void setUserAvatarId(int userAvatarId) {
        this.userAvatarId = userAvatarId;
    }
}
