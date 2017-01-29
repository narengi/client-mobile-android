package xyz.narengi.android.armin.view.fragments.auth;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import uk.co.chrisjenx.calligraphy.CalligraphyTypefaceSpan;
import uk.co.chrisjenx.calligraphy.TypefaceUtils;
import xyz.narengi.android.R;
import xyz.narengi.android.armin.presenter.auth.LoginPresenter;
import xyz.narengi.android.armin.view.interfaces.auth.LoginView;

/**
 * Created by arminghm on 1/29/17.
 */

public class LoginFragment extends Fragment implements View.OnClickListener, LoginView {

    private Button buttonLogin;
    private Button buttonForgotPassword;

    private TextInputLayout textInputLayoutEmail;
    private TextInputLayout textInputLayoutPassword;
    private TextInputEditText textInputEditTextPassword;

    private LoginPresenter loginPresenter;

    public static LoginFragment newInstance(String title) {

        Bundle args = new Bundle();
        args.putString("title", title);
        LoginFragment fragment = new LoginFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sign_in, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        buttonLogin = (Button) view.findViewById(R.id.ButtonLogin);
        buttonForgotPassword = (Button) view.findViewById(R.id.ButtonForgotPassword);
        textInputLayoutEmail = (TextInputLayout) view.findViewById(R.id.TextInputLayoutEmail);
        textInputLayoutPassword = (TextInputLayout) view.findViewById(R.id.TextInputLayoutPassword);


        // Calligraphy (the library I use to set the Fonts) does not change the font
        // while the inputType=password was set in the xml layout.
        // According to the issues (#186) of Calligraphy, @QiiqeAzuara suggested to
        // remove inputType from the xml and set the inputType in your java code.
        textInputEditTextPassword = (TextInputEditText) view
                .findViewById(R.id.TextInputEditTextPassword);
        textInputEditTextPassword.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        textInputEditTextPassword.setTransformationMethod(
                PasswordTransformationMethod.getInstance()
        );

        buttonLogin.setOnClickListener(this);
        buttonForgotPassword.setOnClickListener(this);

        if (loginPresenter == null) {
            loginPresenter = new LoginPresenter(getActivity().getApplication());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loginPresenter.attachView(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        loginPresenter.detachView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        loginPresenter.destroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ButtonLogin:
                loginPresenter.login(textInputLayoutEmail.getEditText().getText().toString(),
                        textInputLayoutPassword.getEditText().getText().toString());

                break;
            case R.id.ButtonForgotPassword:
                break;
        }
    }

    @Override
    public void showLoading(boolean loading) {
        if (getView() != null) {
            Snackbar.make(getView(), "لطفا کمی صبر کنید...", Snackbar.LENGTH_INDEFINITE).show();
        }
    }

    @Override
    public void showError(String message) {
        if (getView() != null) {
            Snackbar.make(getView(), "خطا در برقراری ارتباط با سرور", Snackbar.LENGTH_INDEFINITE)
                    .show();
        }
    }

    @Override
    public void emailValidationError(String message) {
        if (message != null) {
            textInputLayoutEmail.setError(wrapInCustomFont(message));
        } else {
            textInputLayoutEmail.setError(null);
        }
    }

    @Override
    public void passwordValidationError(String message) {
        if (message != null) {
            textInputLayoutPassword.setError(wrapInCustomFont(message));
        } else {
            textInputLayoutPassword.setError(null);
        }
    }

    private Spannable wrapInCustomFont(String s) {
        // Errors also are not changed with Calligraphy.
        // According to the issue (#201) we can set the font in this way.
        Typeface typeface = TypefaceUtils.load(getActivity().getAssets(), "fonts/IRAN-Sans.ttf");
        CalligraphyTypefaceSpan calligraphyTypefaceSpan = new CalligraphyTypefaceSpan(typeface);
        SpannableString spannableString = new SpannableString(s);
        spannableString.setSpan(calligraphyTypefaceSpan, 0,
                s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        return spannableString;
    }
}
