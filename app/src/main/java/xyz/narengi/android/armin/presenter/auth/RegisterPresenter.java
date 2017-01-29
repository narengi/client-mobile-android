package xyz.narengi.android.armin.presenter.auth;

import android.app.Application;

import javax.inject.Inject;

import retrofit2.Retrofit;
import xyz.narengi.android.armin.model.network.pojo.UserModel;
import xyz.narengi.android.armin.view.interfaces.auth.RegisterView;
import xyz.narengi.android.ui.NarengiApplication;

/**
 * Created by arminghm on 1/30/17.
 */

public class RegisterPresenter {

    @Inject
    Retrofit retrofit;
    private RegisterView registerView;

    public RegisterPresenter(Application application) {
        ((NarengiApplication) application).getNetComponent().inject(this);
    }

    public void attachView(RegisterView registerView) {
        this.registerView = registerView;
    }

    public void detachView() {
        registerView = null;
    }

    public void destroy() {
        // TODO: arminghm 1/30/17 Cancel the api call.
    }

    public void register(String email, String password) {
        UserModel userModel = new UserModel();
        boolean validation;
        if (!userModel.emailValidation(email)) {
            registerView.emailValidationError("ایمیل را به صورت صحیح وارد کنید");
            validation = false;
        } else if (!userModel.passwordValidation(password)) {
            registerView.emailValidationError("کلمه‌ی عبور باید حداقل ۶ کاراکتر باشد");
            // Remove the email validation error message.
            registerView.emailValidationError(null);
            validation = false;
        } else {
            validation = true;
            // Remove both email and password validation messages.
            registerView.emailValidationError(null);
            registerView.passwordValidationError(null);
        }
        if (validation) {
            registerUser(email, password);
        }
    }

    private void registerUser(String email, String password) {

    }
}
