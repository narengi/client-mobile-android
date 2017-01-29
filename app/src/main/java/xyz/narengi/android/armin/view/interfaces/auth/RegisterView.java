package xyz.narengi.android.armin.view.interfaces.auth;

import xyz.narengi.android.armin.view.interfaces.BaseView;

/**
 * Created by arminghm on 1/30/17.
 */

public interface RegisterView extends BaseView {
    void emailValidationError(String message);

    void passwordValidationError(String message);
}
