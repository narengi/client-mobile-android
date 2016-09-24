package xyz.narengi.android.common.dto;

import android.support.annotation.DrawableRes;

/**
 * Created by Sepebr Behroozi on 9/22/2016 AD.
 */

public class DrawerItem {

    private String title;
    private DrawerAction action;
    @DrawableRes
    private int iconResource;

    public DrawerItem(String title, int iconResource, DrawerAction action) {
        this.title = title;
        this.iconResource = iconResource;
        this.action = action;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @DrawableRes
    public int getIconResource() {
        return iconResource;
    }

    public void setIconResource(int iconResource) {
        this.iconResource = iconResource;
    }

    public DrawerAction getAction() {
        return action;
    }

    public void setAction(DrawerAction action) {
        this.action = action;
    }

    public enum DrawerAction {
        ACTION_HOME,
        ACTION_INBOX,
        ACTION_FAVORITES,
        ACTION_PROFILE,
        ACTION_LOGIN_SIGN_UP,
        ACTION_USER_GUIDE,
        ACTION_SETTINGS
    }
}
