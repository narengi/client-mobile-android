package xyz.narengi.android.ui.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import xyz.narengi.android.R;
import xyz.narengi.android.util.SimpleTextWatcher;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SignInFragment.OnLoginButtonClickListener} interface
 * to handle interaction events.
 * Use the {@link SignInFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignInFragment extends Fragment {
    private static final String EMAIL_REGEX = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private EditText etxtEmail;
    private EditText etxtPassword;
    private TextInputLayout tilEmailLayout;
    private TextInputLayout tilPasswordLayout;

    private OnLoginButtonClickListener mListener;

    public SignInFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SignInFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SignInFragment newInstance(String param1, String param2) {
        SignInFragment fragment = new SignInFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);
        if (view == null)
            return null;

        Button loginButton = (Button) view.findViewById(R.id.login_loginButton);

        this.etxtEmail = (EditText) view.findViewById(R.id.login_email);
        this.etxtPassword = (EditText) view.findViewById(R.id.login_password);
        this.tilEmailLayout = (TextInputLayout) view.findViewById(R.id.tilEmailInputLayout);
        this.tilPasswordLayout = (TextInputLayout) view.findViewById(R.id.tilPasswordInputLayout);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etxtEmail.getText().toString();
                String password = etxtPassword.getText().toString();
                onLoginButtonPressed(email, password);
            }
        });

        this.etxtPassword.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilPasswordLayout.setErrorEnabled(false);
            }
        });
        this.etxtEmail.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilEmailLayout.setErrorEnabled(false);
            }
        });

        return view;
    }

    public void onLoginButtonPressed(String email, String password) {
        boolean shouldReturn = false;
        if (TextUtils.isEmpty(password)) {
            this.tilPasswordLayout.setError("Password Should not be empty");
            shouldReturn = true;
        }
        if (TextUtils.isEmpty(email)) {
            this.tilEmailLayout.setError("Email should not be empty");
            shouldReturn = true;
        } else if (!email.matches(EMAIL_REGEX)) {
            this.tilEmailLayout.setError("Email is not correct");
            shouldReturn = true;
        }
        if (shouldReturn)
            return;

        if (mListener != null) {
            mListener.onLoginButtonPressed(email, password);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLoginButtonClickListener) {
            mListener = (OnLoginButtonClickListener) context;
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
    public interface OnLoginButtonClickListener {

        void onLoginButtonPressed(String email, String password);
    }
}
