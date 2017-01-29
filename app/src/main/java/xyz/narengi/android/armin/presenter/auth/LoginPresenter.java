package xyz.narengi.android.armin.presenter.auth;

import android.app.Application;

import javax.inject.Inject;

import retrofit2.Retrofit;
import xyz.narengi.android.armin.model.network.pojo.UserModel;
import xyz.narengi.android.armin.view.interfaces.auth.LoginView;
import xyz.narengi.android.ui.NarengiApplication;

/**
 * Created by arminghm on 1/29/17.
 */

public class LoginPresenter {

    @Inject
    Retrofit retrofit;
    private LoginView loginView;

    public LoginPresenter(Application application) {
        ((NarengiApplication) application).getNetComponent().inject(this);
    }

    public void attachView(LoginView loginView) {
        this.loginView = loginView;
    }

    public void detachView() {
        loginView = null;
    }

    public void destroy() {
        // TODO: arminghm 1/29/17 Cancel the api call.
    }

    public void login(String email, String password) {
        UserModel userModel = new UserModel();
        boolean validation;
        if (!userModel.emailValidation(email)) {
            loginView.emailValidationError("ایمیل را به صورت صحیح وارد کنید");
            validation = false;
        } else if (!userModel.passwordValidation(password)) {
            loginView.passwordValidationError("کلمه‌ی عبور باید حداقل ۶ کاراکتر باشد");
            // Remove the email validation error message.
            loginView.emailValidationError(null);
            validation = false;
        } else {
            validation = true;
            // Remove both email and password validation messages.
            loginView.emailValidationError(null);
            loginView.passwordValidationError(null);
        }
        if (validation) {
            loginUser(email, password);
        }
    }

    private void loginUser(String email, String password) {

    }
}
