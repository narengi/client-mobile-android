package xyz.narengi.android.ui.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import xyz.narengi.android.R;
import xyz.narengi.android.common.dto.BookRequest;
import xyz.narengi.android.ui.adapter.BookRequestContentRecyclerAdapter;
import xyz.narengi.android.ui.adapter.BookRequestsContentRecyclerAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BookRequestListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BookRequestListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookRequestListFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private BookRequest[] bookRequests;

    private OnFragmentInteractionListener mListener;

    public BookRequestListFragment() {
        // Required empty public constructor
    }

    public BookRequest[] getBookRequests() {
        return bookRequests;
    }

    public void setBookRequests(BookRequest[] bookRequests) {
        this.bookRequests = bookRequests;
    }

    public static BookRequestListFragment newInstance(String param1, String param2) {
        BookRequestListFragment fragment = new BookRequestListFragment();
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
        return inflater.inflate(R.layout.fragment_book_request_list, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupContentRecyclerView(view);
    }


    private void setupContentRecyclerView(View view) {
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.book_requests_recyclerView);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);

        BookRequestsContentRecyclerAdapter contentRecyclerAdapter = new BookRequestsContentRecyclerAdapter(getActivity(), bookRequests);
        recyclerView.setAdapter(contentRecyclerAdapter);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
