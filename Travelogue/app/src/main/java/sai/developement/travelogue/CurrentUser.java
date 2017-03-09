package sai.developement.travelogue;

/**
 * Created by sai on 3/9/17.
 */

public class CurrentUser {
    private String userId;
    private String userName;
    private String userEmail;
    private int userAvatarId;

    public static CurrentUser sCurrentuser = null;

    private CurrentUser() {}

    public static void setCurrentUser(String userId, String userName, String userEmail) {
        if(sCurrentuser == null) {
            sCurrentuser = new CurrentUser();
            sCurrentuser.setUserId(userId);
            sCurrentuser.setUserName(userName);
            sCurrentuser.setUserEmail(userEmail);
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

    public static void deleteCurrentUser() {
        sCurrentuser = null;
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
