package xyz.narengi.android.ui.fragment;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import xyz.narengi.android.R;
import xyz.narengi.android.util.SimpleTextWatcher;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnRegisterButtonClickListener} interface
 * to handle interaction events.
 * Use the {@link SignUpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignUpFragment extends Fragment {
    private static final String EMAIL_REGEX = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private EditText etPassword;
    private EditText etEmail;
    private TextInputLayout tilPassword;
    private TextInputLayout tilEmail;
    private Button btnRegister;

    private OnRegisterButtonClickListener mListener;

    public SignUpFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SignUpFragment.
     */
    public static SignUpFragment newInstance(String param1, String param2) {
        SignUpFragment fragment = new SignUpFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        if (view == null)
            return null;


//        this.tilPassword = (TextInputLayout) view.findViewById(R.id.tilPasswordInputLayout);
//        this.tilEmail = (TextInputLayout) view.findViewById(R.id.tilEmailInputLayout);
//        this.etEmail = (EditText) view.findViewById(R.id.etEmail);
//        this.etPassword = (EditText) view.findViewById(R.id.etPassword);
//        this.btnRegister = (Button) view.findViewById(R.id.btnRegister);

        final Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/IRAN-Sans.ttf");

        int childCount = tilPassword.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = tilPassword.getChildAt(i);
            if (child instanceof TextView)
                ((TextView) child).setTypeface(tf);
        }
        tilPassword.setTypeface(tf);


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                onRegisterButtonPressed(email, password);
            }
        });

        etEmail.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilEmail.setErrorEnabled(false);
            }
        });

        etPassword.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilPassword.setErrorEnabled(false);
            }
        });

        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etEmail.clearFocus();
        etPassword.clearFocus();

        etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
    }

    public void onRegisterButtonPressed(String email, String password) {
        if (!checkForInputError())
            return;

        if (mListener != null) {
            mListener.onRegisterButtonPressed(email, password);
        }
    }

    private boolean checkForInputError() {
        boolean result = true;
        if (TextUtils.isEmpty(etEmail.getText().toString())) {
            result = false;
            tilEmail.setError("لطفا ایمیل را وارد کنید");
        } else {
            if (!etEmail.getText().toString().matches(EMAIL_REGEX)) {
                result = false;
                tilEmail.setError("لطفا ایمیل را به صورت صحیح وارد کنید");
            }
        }
        if (TextUtils.isEmpty(etPassword.getText().toString())) {
            result = false;
            tilPassword.setError("لطفا رمز عبور را وارد کنید");
        }
        return result;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnRegisterButtonClickListener) {
            mListener = (OnRegisterButtonClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnRegisterButtonClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnRegisterButtonClickListener {
        void onRegisterButtonPressed(String email, String password);
    }

    public interface OnTextFocusingChanged {
        void onFocusingChanged(boolean hasFocus);
    }
}
