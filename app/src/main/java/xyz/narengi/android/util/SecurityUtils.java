package xyz.narengi.android.util;

import android.content.Context;

/**
 * @author Siavash Mahmoudpour
 */
public class SecurityUtils {

    private static SecurityUtils instance;
    private Context context;
    private boolean updateUserTitleNeeded = false;

    public static SecurityUtils getInstance(Context context) {

        if (instance == null)
            instance = new SecurityUtils();
        instance.context = context;

        return instance;
    }

    public boolean isUpdateUserTitleNeeded() {
        return updateUserTitleNeeded;
    }

    public void setUpdateUserTitleNeeded(boolean updateUserTitleNeeded) {
        this.updateUserTitleNeeded = updateUserTitleNeeded;
    }
}
