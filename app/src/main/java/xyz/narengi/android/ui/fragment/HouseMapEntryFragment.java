package xyz.narengi.android.ui.fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import com.byagowi.persiancalendar.Utils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import xyz.narengi.android.R;
import xyz.narengi.android.common.dto.GeoPoint;
import xyz.narengi.android.ui.activity.AddHouseActivity;

/**
 * @author Siavash Mahmoudpour
 */
public class HouseMapEntryFragment extends HouseEntryBaseFragment implements OnMapReadyCallback {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private GoogleMap mMap;
    private OnTouchListener onTouchListener;

    public HouseMapEntryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HouseMapEntryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HouseMapEntryFragment newInstance(String param1, String param2) {
        HouseMapEntryFragment fragment = new HouseMapEntryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public void setListener(OnTouchListener listener) {
        onTouchListener = listener;
    }

    public interface OnTouchListener {
        public abstract void onTouch();
    }

    public class TouchableWrapper extends FrameLayout {

        public TouchableWrapper(Context context) {
            super(context);
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    onTouchListener.onTouch();
                    break;
                case MotionEvent.ACTION_UP:
                    onTouchListener.onTouch();
                    break;
            }
            return super.dispatchTouchEvent(event);
        }
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
        return inflater.inflate(R.layout.fragment_house_map_entry, container, false);

        /*View view = super.onCreateView(inflater, container, savedInstanceState);
        if (view != null && view instanceof ViewGroup && ((ViewGroup)view).getChildCount() > 0 && ((ViewGroup)view).getChildAt(0) instanceof FrameLayout) {
            FrameLayout mainChild = (FrameLayout) ((ViewGroup)view).getChildAt(0);
            mainChild.addView(new View(getActivity()));
        }

//        View layout = super.onCreateView(inflater, container, savedInstanceState);

        TouchableWrapper frameLayout = new TouchableWrapper(getActivity());

        frameLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        ((ViewGroup) layout).addView(frameLayout,
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));*/

//        return layout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

//        SupportMapFragment mapFragment = new SupportMapFragment();
        MapFragment mapFragment = new MapFragment();
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.house_entry_map, mapFragment, "AddHouseMapFragment")
                .commit();

        mapFragment.setListener(new MapFragment.OnTouchListener() {
            @Override
            public void onTouch() {
                if (getActivity() != null && getActivity() instanceof AddHouseActivity) {
                    ((AddHouseActivity)getActivity()).requestDisallowInterceptTouchEvent(true);
                }
            }
        });

//        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
//                .findFragmentById(R.id.house_entry_map);
        mapFragment.getMapAsync(this);

        Button nextButton = (Button)view.findViewById(R.id.house_map_entry_nextButton);
        Button previousButton = (Button)view.findViewById(R.id.house_map_entry_previousButton);

        switch (getEntryMode()) {
            case ADD:
                if (nextButton != null) {
                    nextButton.setVisibility(View.VISIBLE);
                    nextButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (validate() && getOnInteractionListener() != null) {
                                getOnInteractionListener().onGoToNextSection(getHouse());
                            }
                        }
                    });
                }

                if (previousButton != null) {
                    previousButton.setVisibility(View.VISIBLE);
                    previousButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (validate() && getOnInteractionListener() != null) {
                                getOnInteractionListener().onBackToPreviousSection(getHouse());
                            }
                        }
                    });
                }
                break;
            case EDIT:
                if (nextButton != null)
                    nextButton.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setMyLocationEnabled(true);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                HouseMapEntryFragment.this.onMapClick(latLng);
            }
        });


        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                HouseMapEntryFragment.this.onMapClick(latLng);
            }
        });

        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                if (getHouse() != null && getHouse().getPosition() != null)
                    return;

                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                float zoomLevel = 16.0f;
                if (mMap.getCameraPosition().zoom != 16.0f)
                    zoomLevel = 16.0f;
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
                mMap.setOnMyLocationChangeListener(null);
            }
        });

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(35.715298, 51.404343), 12));

        // Calculate ActionBar height
        TypedValue typedValue = new TypedValue();
        if (getActivity().getTheme().resolveAttribute(android.R.attr.actionBarSize, typedValue, true))
        {
            int actionBarHeight = TypedValue.complexToDimensionPixelSize(typedValue.data, getResources().getDisplayMetrics());
            mMap.setPadding(0, 0, 0, actionBarHeight);
        }

        if (getHouse() != null && getHouse().getPosition() != null) {
            addMarker(new LatLng(getHouse().getPosition().getLat(), getHouse().getPosition().getLng()));
        }
    }

    private BitmapDescriptor getBitmapDescriptor(int id) {
        Drawable vectorDrawable = getContext().getResources().getDrawable(id);
        if (vectorDrawable == null)
            return null;
//        int h = ((int) Utils.convertDpToPixel(42, context));
//        int w = ((int) Utils.convertDpToPixel(25, context));
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bm = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bm);
    }

    private void onMapClick(LatLng latLng) {
        addMarker(latLng);
        updateHousePosition(latLng);
    }

    private void addMarker(LatLng latLng) {
        mMap.clear();
        BitmapDescriptor bitmapDescriptor = getBitmapDescriptor(R.drawable.ic_action_location);
        if (bitmapDescriptor != null) {
            mMap.addMarker(new MarkerOptions().position(latLng).icon(bitmapDescriptor));
        } else {
            mMap.addMarker(new MarkerOptions().position(latLng));
        }


//        Marker mMarker = mMap.addMarker(new MarkerOptions().position(latLng).
//                icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_location))));
        float zoomLevel = 18.0f;
        if (mMap.getCameraPosition().zoom != zoomLevel)
            zoomLevel = 18.0f;
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
    }

    private void updateHousePosition(LatLng latLng) {
        if (latLng == null || getHouse() == null)
            return;

        GeoPoint geoPoint = getHouse().getPosition();
        if (geoPoint == null)
            geoPoint = new GeoPoint();
        geoPoint.setLat(latLng.latitude);
        geoPoint.setLng(latLng.longitude);
        getHouse().setPosition(geoPoint);
    }
}
