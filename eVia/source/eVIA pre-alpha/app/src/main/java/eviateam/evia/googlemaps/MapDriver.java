package eviateam.evia.googlemaps;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.facebook.login.LoginManager;
import com.facebook.login.widget.ProfilePictureView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import org.jivesoftware.smack.SmackException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import eviateam.evia.R;
import eviateam.evia.activities.CarsActivity;
import eviateam.evia.activities.ProfileActivity;
import eviateam.evia.activities.SettingsActivity;
import eviateam.evia.dialogs.DriverDialog;
import eviateam.evia.dialogs.EndCourseDialog;
import eviateam.evia.dialogs.RateDialog;
import eviateam.evia.dialogs.WaitDialog;
import eviateam.evia.utility.Communication;
import eviateam.evia.utility.JsonReader;
import eviateam.evia.utility.UserRoleShiftClass;

import static com.google.maps.android.SphericalUtil.computeDistanceBetween;
import static com.google.maps.android.SphericalUtil.computeHeading;

/**
 * An activity that displays a map at the device's current location.
 * Google sample
 */
public class MapDriver extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, EndCourseDialog.EndCourseListener, DriverDialog.DriverDialogListener, OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, RateDialog.RateDialogListener, WaitDialog.WaitDialogListener, LifecycleObserver, GoogleMap.OnCameraMoveStartedListener
{

    private static final String TAG = MapDriver.class.getSimpleName();
    public static final String DRIVER = "driver";
    public static final String XMPP_LOGIN = "xmppLogin";
    public static final String END_COURSE = "endCourse";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String RATE = "rate";
    public static final String FREE = "FREE";
    public static final String BUSY = "BUSY";


    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private Location currentLocation;
    /**
     * cancel course button
     */
    private FloatingActionButton cancelCourseButton;
    /**
     * end course button
     */
    private FloatingActionButton endCourseButton;
    /**
     * stop navigation button
     */
    private FloatingActionButton stopNavigationButton;
    /**
     * start navigation button
     */
    private FloatingActionButton startNavigationButton;
    /**
     * center button
     */
    private FloatingActionButton centerButton;

    private ImageButton ib;
    /**
     * drawer layout
     */
    private DrawerLayout drawer;
    /**
     * side menu bar
     */
    private NavigationView navView;
    /**
     * bottom panel
     */
    private LinearLayout lCapitanPirata;
    /**
     * map layout
     */
    private LinearLayout lMapa;
    /**
     * search layout
     */
    private LinearLayout lSzukaj;
    /**
     * destination info bottom panel
     */
    private ConstraintLayout destinationInfoPanel;
    /**
     * passenger info bottom panel
     */
    private LinearLayout passengerInfoBoard;
    /**
     * passenger picture
     */
    private ProfilePictureView profiloweInfoBoard;
    /**
     * passenger name
     */
    private TextView nameInfoBoard;
    /**
     * passenger rate
     */
    private RatingBar rateInfoBoard;


    /**
     * driver picture
     */
    private ProfilePictureView profilowe;
    /**
     * driver name
     */
    private TextView name;
    /**
     * arrival time
     */
    private TextView textEta;
    /**
     * distance
     */
    private TextView textDistance;
    /**
     * destination place name
     */
    private TextView textLocationName;
    /**
     * rate
     */
    private RatingBar rate;

    //bundle from LoggedActivity containing base user data
    Bundle bundle;

    private boolean czyRysowac;
    private Marker marker;
    private Marker passengerMarker;
    private Marker driverMarker;
    private Marker markerAddress;
    private LatLng address;
    private LatLng oldPosition = new LatLng(-33.8523341, 151.2106085);
    private LatLng passengerDestination;
    private LatLng driverCurrentPosition;
    private LatLng driverMoving;
    private LatLng current;
    private String addressName;
    private double distance;
    private String parsedDuration;
    private String ETA;
    private LatLngBounds.Builder builder = new LatLngBounds.Builder();

    private LatLngBounds.Builder showOnMapBuilder = new LatLngBounds.Builder();

    private LatLngBounds.Builder navigationZoom = new LatLngBounds.Builder();

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;

    //private MarkerOptions markerOptions = new MarkerOptions();

    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;
    private Location mLastKnownLocation;



    /**
     * user id
     */
    private String mId;
    /**
     * user name
     */
    private String mFirstName;
    /**
     * user surname
     */
    private String mLastName;


    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    private Communication communication = new Communication();
    private boolean passengerFound = false;
    private boolean passengerAgreed = false;
    private String status = FREE;
    private JSONObject passenger = new JSONObject();
    private boolean startCourse = false;
    private boolean endCourse = false;
    private boolean stopSendingLocation = false;
    private boolean stopChecking = false;
    private boolean userHaveCars = false;
    private boolean cancelCourse = false;
    private boolean pseudoNavigationOn = false;
    private boolean dreiTimerOn = false;
    private boolean vierTimerOn = false;
    private boolean allowBack = true;

    JSONObject sendJson = new JSONObject();

    private Thread listener = new Thread(new Runnable() {
        @Override
        public void run() {
            communication.passengerListener();
        }
    });

    private Thread waitForMeeting = new Thread(new Runnable() {
        @Override
        public void run() {
            waitForMeetingMethod();
        }
    });

    private Thread waitForPassengerDecision = new Thread(new Runnable() {
        @Override
        public void run() {
            waitForDriverDecisionMethod(waitForMeeting);
        }
    });

    private TimerTask sendLocationTimerTask = new TimerTask()
    {
        @Override
        public void run()
        {
            while(!stopSendingLocation) {
                getCurrentLocation();

                double x;
                double y;

                if (currentLocation != null)
                    {
                        x = currentLocation.getLatitude();
                        y = currentLocation.getLongitude();
                    }
                else if (mLastKnownLocation != null)
                    {
                        x = mLastKnownLocation.getLatitude();
                        y = mLastKnownLocation.getLongitude();
                    }
                else
                    {
                        x = mDefaultLocation.latitude;
                        y = mDefaultLocation.longitude;
                    }


                try
                {
                    updateToDB(x,y);
                    Thread.sleep(10* 1000L);
                }
                catch (InterruptedException | SmackException.NotConnectedException | JSONException | XmppStringprepException e)
                    {
                        e.printStackTrace();
                    }

            }
        }

        /**
         * Method that sends driver's current location to server, and initializes driver-passenger searching, if passenger found
         * @param x coordinate x
         * @param y coordinate y
         * @throws InterruptedException
         * @throws SmackException.NotConnectedException
         * @throws JSONException
         * @throws XmppStringprepException
         */
        private void updateToDB(double x, double y) throws InterruptedException, SmackException.NotConnectedException, JSONException, XmppStringprepException
        {
            putInSendJson(x, y, mId, mFirstName, mLastName, status);
            communication.sendLocationToServer(sendJson, DRIVER);
            if(status.equals(FREE)) passengerFound = communication.passenger_found();
            if(passengerFound) {
                setStatus(BUSY);
                passenger = communication.getJs2();
                passengerFound = false;
                communication.setPassenger_found(false);
                runOnUiThread(new Runnable()
                {
                    public void run()
                    {
                        DriverDialog dialog = new DriverDialog(MapDriver.this, passenger);
                        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));
                        Log.d("DIALOG", "SHOW DRIVER DIALOG");
                        dialog.show();
                        Log.d("DIALOG", " DRIVER DIALOG HAS BEEN SHOWN");
                        if (dreiTimerOn)  drei.cancel();

                    }
                });
            }
        }

        /**
         * Creates JSON which contains user's information
         * @param longitude
         * @param latitude
         * @param fbId
         * @param name
         * @param surname
         * @param status
         * @throws JSONException
         */
        private void putInSendJson(double longitude, double latitude, String fbId, String name, String surname, String status) throws JSONException
        {
            sendJson.put(LONGITUDE, longitude);
            sendJson.put(LATITUDE, latitude);
            sendJson.put("direction", computeDirection());
            sendJson.put("fbId", fbId);
            sendJson.put("name", name);
            sendJson.put("surname", surname);
            sendJson.put("status", status);
        }

        /**
         * Method that compute driver direction
         * @return -  driver direction
         */
        private double computeDirection()
        {
            LatLng currentPosition;

            getCurrentLocation();

            if (currentLocation != null)  currentPosition = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            else currentPosition = new LatLng(mDefaultLocation.latitude,mDefaultLocation.longitude);

            double direction;
            if(oldPosition.equals(mDefaultLocation))
            {
                getCurrentLocation();
                if (currentLocation != null) oldPosition = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                else oldPosition = new LatLng(mDefaultLocation.latitude, mDefaultLocation.longitude);

                return direction = 0;//todo Karol czy ta sentencja ma sens? (na przyszłość myśl czy lokacje nie są nullami)
            }
            else{
                direction = computeHeading(oldPosition, currentPosition);//todo Karol naprawić bo wali NullPointerException
                return direction;
            }
        }
    };


    private Timer zwei = new Timer();
    private Timer drei;
    private Timer vier;



    private Thread t4;


    /**
     * Sets contents for activity
     * @param savedInstanceState
     */
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);

        WaitDialog waitDialog = new WaitDialog(15);
        waitDialog.show(getSupportFragmentManager(), "wait");

        setContentView(R.layout.activity_maps_driver);

        listener.start();
        waitForPassengerDecision.start();

        lCapitanPirata = findViewById(R.id.mini_passanger_found);
        lMapa = findViewById(R.id.layout_mapa);
        lSzukaj = findViewById(R.id.layout_szukajka);
        name = findViewById(R.id.passenger_name);
        rate = findViewById(R.id.passenger_rating);
        profilowe = findViewById(R.id.driver_layout_profile_picture);
        destinationInfoPanel = findViewById(R.id.destination_point_info);
        passengerInfoBoard = findViewById(R.id.mini_passanger_info_board);
        profiloweInfoBoard = findViewById(R.id.profile_picture_passanger_info_board);
        nameInfoBoard = findViewById(R.id.passenger_name_info_board);
        rateInfoBoard = findViewById(R.id.passenger_rating_info_board);

        textEta = findViewById(R.id.eta_czas);
        textDistance = findViewById(R.id.dystans);
        textLocationName = findViewById(R.id.lokacja);

        bundle = getIntent().getExtras();

        communication.sendUserHaveCarsRequest();
        Log.d("INFO", "Wysyłam zapytanie!");

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean closeWhile = false;
                do {
                    if (communication.getUserHaveCars() == 1) {
                        userHaveCars = true;
                        closeWhile = true;
                    }
                    else if(communication.getUserHaveCars() == 2) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                goToCars();
                            }
                        });
                        closeWhile = true;
                    }
                } while (communication.getUserHaveCars() == 0 || !closeWhile);

            }
        }).start();


        //guziki panelu akceptacji lub odmowy pasażera
        Button yes = findViewById(R.id.driver_yes);
        Button no = findViewById(R.id.driver_no);

        yes.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onDriverAgreed();

                showSearchHideBottomPanel("MiniPassenger","yes");
                try {
                    profiloweInfoBoard.setProfileId(passenger.getString("fbId"));
                    String dummy;
                    float rating = (float)passenger.getDouble("rate");
                    if (rating == 0)
                    {
                        rateInfoBoard.setRating(0);
                    }
                    else
                    {
                        rateInfoBoard.setRating(rating);
                    }
                    dummy = passenger.getString("name");
                    nameInfoBoard.setText(dummy);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        no.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onDriverDeclined();

                showSearchHideBottomPanel("MiniPassenger","no");

                lSzukaj.setVisibility(View.VISIBLE);
                lSzukaj.setEnabled(true);
            }
        });

        endCourseButton = findViewById(R.id.driver_end_course_button);
        cancelCourseButton = findViewById(R.id.driver_cancel_course_button);

        startNavigationButton = findViewById(R.id.start_navigation_driver);
        stopNavigationButton = findViewById(R.id.stop_navigation_driver);

        endCourseButton.setEnabled(false);
        endCourseButton.hide();
        cancelCourseButton.setEnabled(false);
        cancelCourseButton.hide();

        stopNavigationButton.setEnabled(false);
        stopNavigationButton.hide();
        startNavigationButton.setEnabled(false);
        startNavigationButton.hide();

        centerButton = findViewById(R.id.centerMapButton);
        centerButton.setEnabled(true);
        centerButton.show();


        assert bundle != null;
        mId = bundle.getString("id");
        mFirstName = bundle.getString("fname");
        mLastName = bundle.getString("lname");

        Places.initialize(getApplicationContext(), "AIzaSyAPHYqAw07v2vu7oeqlo3TymykYJIFro3M");
        PlacesClient placesClient = Places.createClient(this);


        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            //Location mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        // Retrieve the content view that renders the map.
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //deklaracja drawer View
        drawer = findViewById(R.id.layout_driver);
        navView = findViewById(R.id.nav_view_driver);
        navView.setNavigationItemSelectedListener(this);


        //Ustawienie nagłówka w menu wysuwanym
        View headerView = navView.getHeaderView(0);
        TextView navRole = (TextView) headerView.findViewById(R.id.role);
        navRole.setText(R.string.driver_mode);

        TextView navName = headerView.findViewById(R.id.name);
        navName.setText(mFirstName + " " + mLastName);

        ProfilePictureView navImage = headerView.findViewById(R.id.profileImage);
        navImage.setProfileId(mId);

        // Initialize the AutocompleteSupportFragment.
        final AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.driver_place_autocomplete);

        // Specify the types of place data to return.
        assert autocompleteFragment != null;
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
        autocompleteFragment.setCountries("PL");
        autocompleteFragment.setHint("Enter your desired location");


        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener()
        {
            @Override
            public void onPlaceSelected(Place place)
            {
                Log.d("Maps", "Place selected: " + place.getName());
                mMap.clear();
                address = place.getLatLng();
                addressName = place.getName();

                assert address != null;

                markerAddress = mMap.addMarker(new MarkerOptions()
                                            .position(address));


                mMap.setOnMarkerClickListener(null);
                getCurrentLocation();
                current = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

                builder.include(current);
                builder.include(address);
                LatLngBounds boundsAutoComplete = builder.build();

                try
                    {
                        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(boundsAutoComplete, 100));
                        drawPath(address, current);
                    }
                catch (NullPointerException e)
                    {
                        Log.d("Exception", "Failed to animate map in szukajka");
                    }

                startNavigationButton.setEnabled(true);
                startNavigationButton.show();
                stopNavigationButton.setEnabled(true);
                stopNavigationButton.show();
            }

            @Override
            public void onError(@NonNull Status status)
            {
                Log.d("Maps", "An error occurred: " + status);
            }
        });



        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Build the map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.driver_mapFragment_layout);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        //Initializing googleApiClient
        googleApiClient = new GoogleApiClient.Builder(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();


        zwei.schedule(sendLocationTimerTask, 10*1000L);

        waitDialog.dismiss();

    }
    /**
     * Method uncheck items in item list
     */
    public void onResume()
    {
        super.onResume();
        int size = navView.getMenu().size();
        for (int i = 0; i < size; i++) {
            navView.getMenu().getItem(i).setChecked(false);
        }
    }

    /**
     * Method sets current status for driver
     * @param status current status
     */
    public void setStatus(String status) {
        this.status = status;
    }


    /**
     * Method that shows bottom panel with information about passenger and shows on map passenger destination position
     * @param obj - information about passenger
     */
    @Override
    public void showOnMap(JSONObject obj)
    {
        hideSearchShowBottomPanel("MiniPassenger");
        try {
            String dummy;
            profilowe.setProfileId(obj.getString("fbId"));

            float rating = (float)obj.getDouble("rate");
            if (rating == 0)
            {
                //this.rating.setText(R.string.empty_rating);
                this.rate.setRating(0);
            }
            else
            {
                //dummy = getString(R.string.rating) + rating;
                this.rate.setRating(rating);
            }

            dummy = obj.getString("name");

            name.setText(dummy);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        getCurrentLocation();
        LatLng driverCurrentPosition = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        try {
            passengerDestination = new LatLng(obj.getDouble("latitude"), obj.getDouble("longitude"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Marker markerPassengerDestination = mMap.addMarker(new MarkerOptions()
                .position(passengerDestination)
        );

        showOnMapBuilder.include(driverCurrentPosition);
        showOnMapBuilder.include(passengerDestination);
        LatLngBounds zoom = showOnMapBuilder.build();

        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(zoom, 100));

        drawPath(driverCurrentPosition, passengerDestination);
    }


    /**
     * Method that shows bottom panel and hides search panel
     * @param panel - text telling which panel show
     */
    private void hideSearchShowBottomPanel(String panel)
    {

        Resources r = getResources();
        int px = Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 80,r.getDisplayMetrics()));

        if (panel.equals("MiniPassenger"))
        {            //schowaj szukajke, pokaż mini okno przyjęcia lub odrzucenia pasażera
            lCapitanPirata.setVisibility(View.VISIBLE);

            LinearLayout.LayoutParams param2 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    0,
                    0.0f
            );
            lSzukaj.setLayoutParams(param2);

            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    0,
                    1.0f
            );
            lMapa.setLayoutParams(param);

            lSzukaj.setVisibility(View.GONE);
            lSzukaj.setEnabled(false);

            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) lMapa.getLayoutParams();
            lp.setMargins(0, 0, 0, px);
            lMapa.setLayoutParams(lp);

        }
        else if(panel.equals("eta"))
        {                 //schowaj szukajke, pokaż okno eta
            destinationInfoPanel.setVisibility(View.VISIBLE);

            LinearLayout.LayoutParams param2 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    0,
                    0.0f
            );
            lSzukaj.setLayoutParams(param2);

            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    0,
                    1.0f
            );
            lMapa.setLayoutParams(param);

            lSzukaj.setVisibility(View.GONE);
            lSzukaj.setEnabled(false);

            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) lMapa.getLayoutParams();
            lp.setMargins(0, 0, 0, px);
            lMapa.setLayoutParams(lp);
        } else if(panel.equals("mini_info"))
        {               //schowaj szukajke, pokaz informacje dotyczace pasazera ktorego trzeba odebrac
            passengerInfoBoard.setVisibility(View.VISIBLE);

            LinearLayout.LayoutParams param2 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    0,
                    0.0f
            );
            lSzukaj.setLayoutParams(param2);

            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    0,
                    1.0f
            );
            lMapa.setLayoutParams(param);

            lSzukaj.setVisibility(View.GONE);
            lSzukaj.setEnabled(false);

            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) lMapa.getLayoutParams();
            lp.setMargins(0, 0, 0, px);
            lMapa.setLayoutParams(lp);

        }
    }
    /**
     * Method that hides bottom panel and shows search panel
     * @param panel - text telling which panel hide
     * @param buttonYesNo - text telling which button was pressed in MiniPassenger bottom panel
     */
    private void showSearchHideBottomPanel(String panel,String buttonYesNo)
    {
        if (panel.equals("MiniPassenger"))
        {        // schowaj mini okno przyjęcia lub odrzucenia pasażera

            if(buttonYesNo.equals("yes"))           //mapa na cały ekran
            {

                lCapitanPirata.setVisibility(View.GONE);

                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        0,
                        1.0f
                );
                lMapa.setLayoutParams(param);

                ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) lMapa.getLayoutParams();
                lp.setMargins(0, 0, 0, 0);
                lMapa.setLayoutParams(lp);

                hideSearchShowBottomPanel("mini_info");     //TODO czy to ma tu byc ??????????

            }
            else if(buttonYesNo.equals("no"))       //mapa z szukajką
            {

                lCapitanPirata.setVisibility(View.GONE);
                LinearLayout.LayoutParams param2 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        0,
                        .1f
                );
                lSzukaj.setLayoutParams(param2);

                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        0,
                        .9f
                );
                lMapa.setLayoutParams(param);

                ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) lMapa.getLayoutParams();
                lp.setMargins(0, 0, 0, 0);
                lMapa.setLayoutParams(lp);

                lSzukaj.setVisibility(View.VISIBLE);
                lSzukaj.setEnabled(true);
            }

        }
        else if(panel.equals("eta"))
        {            //pokaż szukajke, schowaj okno eta

            destinationInfoPanel.setVisibility(View.GONE);

            LinearLayout.LayoutParams param2 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    0,
                    .1f
            );
            lSzukaj.setLayoutParams(param2);

            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    0,
                    .9f
            );
            lMapa.setLayoutParams(param);

            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) lMapa.getLayoutParams();
            lp.setMargins(0, 0, 0, 0);
            lMapa.setLayoutParams(lp);

            lSzukaj.setVisibility(View.VISIBLE);
            lSzukaj.setEnabled(true);
        }
        else if (panel.equals("mini_info"))
        {           //pokaż szukajke, schowaj informacje o pasazerze po skonczonym kursie
            passengerInfoBoard.setVisibility(View.GONE);
            LinearLayout.LayoutParams param2 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    0,
                    .1f
            );
            lSzukaj.setLayoutParams(param2);

            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    0,
                    .9f
            );
            lMapa.setLayoutParams(param);

            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) lMapa.getLayoutParams();
            lp.setMargins(0, 0, 0, 0);
            lMapa.setLayoutParams(lp);

            lSzukaj.setVisibility(View.VISIBLE);
            lSzukaj.setEnabled(true);
        }
    }

    /**
     * Method that hides search panel during course
     */
    private void hideSearchDuringCourse() {
        LinearLayout.LayoutParams param2 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                0.0f
        );
        lSzukaj.setLayoutParams(param2);

        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1.0f
        );
        lMapa.setLayoutParams(param);

        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) lMapa.getLayoutParams();
        lp.setMargins(0, 0, 0, 0);
        lMapa.setLayoutParams(lp);

        lSzukaj.setVisibility(View.GONE);
        lSzukaj.setEnabled(false);
    }

    /**
     * Method that starts driver navigation on the map and show tile with target location info
     * @param view default variable, required for onClick method
     * @throws IOException
     */
    public void startNavigation(View view) throws IOException {
        hideSearchShowBottomPanel("eta");
        lBelka();

        mMap.setOnMapClickListener(null);
        if (!pseudoNavigationOn)
            {
                redrawMap();

                pseudoNavigationOn = true;

                Snackbar snackbar = Snackbar.make(findViewById(R.id.driver_mapFragment_layout), "Click again to start navigation", Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        else
            {
                drei = new Timer();
                drei.schedule(new TimerTask()
                {
                    @Override
                    public void run()
                    {
                        redrawMap();
                    }

                }, 0, 5 * 1000L);
                dreiTimerOn = true;

                startNavigationButton.setEnabled(false);
                startNavigationButton.hide();
            }
    }

    /**
     * Method that stops driver navigation and clears the map
     * @param view default variable, required for onClick method
     */
    public void stopNavigation(View view)
    {
        showSearchHideBottomPanel("eta","null");
        mMap.clear();
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener(){
            @Override
            public void onMapClick(LatLng latLng) {
                // Clears the previously touched position
                mMap.clear();

                // Creating a marker
                marker = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                );

                // Animating to the touched position
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                address = latLng;

                startNavigationButton.setEnabled(true);
                startNavigationButton.show();
                stopNavigationButton.setEnabled(true);
                stopNavigationButton.show();
            }
        });

        if (dreiTimerOn) drei.cancel();

        pseudoNavigationOn = false;
        dreiTimerOn = false;
        stopNavigationButton.setEnabled(false);
        stopNavigationButton.hide();
        startNavigationButton.setEnabled(false);
        startNavigationButton.hide();
    }

    /**
     * Method that clears the map and draws path ro target location again.
     */
    private void redrawMap()
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                mMap.clear();
                getCurrentLocation();
                current = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                //navigationZoom.include(address);
                //navigationZoom.include(current);

                //LatLngBounds navigationBounds = navigationZoom.build();

                markerAddress = mMap.addMarker(new MarkerOptions()
                        .position(address));

                //mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(navigationBounds, 120));
                drawPath(current, address);

                //TODO
                try {
                    lBelka();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });
    }

    /**
     * Method called when driver agrees on the course. Sets tile with base info on passenger and communicate with server on starting the course
     */
    @SuppressLint("RestrictedApi")
    @Override
    public void onDriverAgreed()
    {

        showSearchHideBottomPanel("MiniPassenger","yes");
        try {
            profiloweInfoBoard.setProfileId(passenger.getString("fbId"));
            String dummy;
            float rating = (float)passenger.getDouble("rate");
            if (rating == 0)
            {
                rateInfoBoard.setRating(0);
            }
            else
            {
                rateInfoBoard.setRating(rating);
            }
            dummy = passenger.getString("name");
            nameInfoBoard.setText(dummy);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //cancelCourseButton.setEnabled(true);
        //cancelCourseButton.setVisibility(View.VISIBLE);
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    communication.sendYesOrNo(true, communication.getJs2().getInt("questionId"));

                    getCurrentLocation();
                    communication.sendLocationToClient(passenger.getString(XMPP_LOGIN), currentLocation.getLatitude(), currentLocation.getLongitude());

                    while (!communication.isPassengerWithMe() && !communication.isDriverWithMe() && !communication.isEndCourseAgreed() && !communication.isCancelClicked() && !cancelCourse) {
                        getCurrentLocation();
                        communication.sendLocationToClient(passenger.getString("xmppLogin"), currentLocation.getLatitude(), currentLocation.getLongitude());
                        Thread.sleep(2000);
                    }
                    if(cancelCourse) {
                        stopChecking = true;
                    }
                    else if (communication.isCancelClicked()) {
                        runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    stopChecking = true;
                                    restartActivity();
                                }
                        });
                    }
                } catch (XmppStringprepException | SmackException.NotConnectedException | InterruptedException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        startNavigationButton.setEnabled(false);
        startNavigationButton.hide();
        t1.start();
    }

    /**
     * Method called when driver refuses to start the course. Sets driver status as "free", and if navigation was on, restarts it.
     */
    @Override
    public void onDriverDeclined() {
        setStatus(FREE);
        communication.setStatus(FREE);
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    communication.sendYesOrNo(false, communication.getJs2().getInt("questionId"));
                } catch (XmppStringprepException | SmackException.NotConnectedException | InterruptedException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        if (pseudoNavigationOn && !dreiTimerOn)
            {
                drei = new Timer();
                drei.schedule(new TimerTask()
                {
                    @Override
                    public void run()
                    {
                        redrawMap();
                    }
                }, 0, 5 * 1000L);
            }
        t1.start();
    }

    /**
     * Method called when passenger fails to answer.
     */
    @Override
    public void onOutOfTime()
    {
        try {
            communication.sendOutOfTime(passenger.getString("xmppLogin"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setStatus(FREE);
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                Toast toast = Toast.makeText(getApplicationContext(), "Time out", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

    }

    /**
     * Method called when, after requesting to end the current coarse, the user agrees. Sends request to end the course to server and starts RateDialog
     */
    @Override
    public void onEndCourseAgreed()
    {
        Thread t6 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    communication.sendInfo(passenger.getString(XMPP_LOGIN), DRIVER, currentLocation.getLatitude(), currentLocation.getLongitude(), END_COURSE, "server");
                } catch (XmppStringprepException | JSONException | SmackException.NotConnectedException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t6.start();
        communication.setEndCourseAgreed(true);

        RateDialog rateDialog = new RateDialog();
        rateDialog.show(getSupportFragmentManager(), RATE);
    }

    /**
     * Method called when, after requesting to end the current coarse, the user refuses.
     */
    @Override
    public void onEndCourseDecline(){
        endCourse = false;
        communication.setEndCourse(false);
    }

    /**
     * Method that supports navigation menu
     * @param menuItem selected item from menu
     * @return true when completed
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem)
    {
        switch (menuItem.getItemId())
            {
            case R.id.my_profile:
            {
                goToMyProfile();
                break;
            }
            case R.id.settings:
            {
                goToSettings();
                break;
            }
            case R.id.logout:
            {
                logout();
                break;
            }
            case R.id.car:
            {
                communication.setStopThread(true);
                goToCars();
                break;
            }
            case R.id.switch_passanger_driver:
            {
                switchRole();
                break;
            }
            default:
                return true;

        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Method that starts ProfileActivity activity
     */
    private void goToMyProfile()
    {
        Intent intent;
        intent = new Intent(this, ProfileActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /**
     * Method that starts SettingsActivity activity
     */
    private void goToSettings()
    {
        Intent intent;
        intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    /**
     * Method that stops all timers and initializes logout
     */
    private void logout()
    {
        LoginManager.getInstance().logOut();
        if (dreiTimerOn) drei.cancel();
        if (vierTimerOn) vier.cancel();
        finish();
    }

    /**
     * Method that requests user's car list from server and after acquiring if starts CarsActivity activity
     */
    private void goToCars()
    {
        Log.d("Info", "Gotocars clicked");

        ProcessLifecycleOwner.get().getLifecycle().removeObserver(this);

        final WaitDialog waitDialog = new WaitDialog(15);
        waitDialog.show(getSupportFragmentManager(), "wait");

        Log.d("Info", "After waitdialog");

        //TODO Mateusz
        new Thread(new Runnable() {
            @Override
            public void run() {
                setStatus(BUSY);
                try {
                    sendJson.put("status", status);
                    communication.sendLocationToServer(sendJson, DRIVER);
                } catch (XmppStringprepException | SmackException.NotConnectedException | InterruptedException | JSONException e) {
                    e.printStackTrace();
                }
                while(true)
                {
                    if (communication.getUserHaveCars() != 0)
                    {
                        bundle.putBoolean("hasCars", userHaveCars);
                        waitDialog.dismiss();
                        break;
                    }
                }
                    runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                waitDialog.dismiss();
                                Log.d("Info", "Went after the loop. Creating intent");


                                Log.d("INFO", "Otwieram listę samochodów!");
                                Intent intent;
                                intent = new Intent(getApplicationContext(), CarsActivity.class);

                                bundle.putBoolean("hasCars", userHaveCars);

                                intent.putExtras(bundle);
                                if (dreiTimerOn) drei.cancel();
                                if (vierTimerOn) vier.cancel();
                                startActivity(intent);
                                finish();
                            }
                        });
            }
        }).start();
    }

    /**
     * Method that changes current mode from driver to passenger - stops all existing timers and starts MapPassenger activity
     */
    private void switchRole()
    {
        ProcessLifecycleOwner.get().getLifecycle().removeObserver(this);

        Intent intent;
        UserRoleShiftClass shiftClass = new UserRoleShiftClass();

        intent = new Intent(this, MapPassenger.class);
        try
            {
                shiftClass.userRoleShiftToPassenger(getApplicationContext());
            }
        catch (IOException e)
            {
                Log.d("Info", Objects.requireNonNull(e.getMessage()));
            }
        intent.putExtras(bundle);
        if (dreiTimerOn) drei.cancel();
        if (vierTimerOn) vier.cancel();
        startActivity(intent);
        finish();
    }

    /**
     * Method called when driver cancels course, durgin driver-passenger locating
     * @param v default variable, required for onClick method
     */
    @SuppressLint("RestrictedApi")
    public void driverCancelCourse(View v)
    {

        showSearchHideBottomPanel("mini_info",null);


        Thread t5 = new Thread(new Runnable()
        {
            @Override
            public void run() {
                try
                {
                    communication.sendCancelCourse(passenger.getString("xmppLogin"), currentLocation.getLatitude(), currentLocation.getLongitude());
                } catch (XmppStringprepException | JSONException | SmackException.NotConnectedException | InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        });
        t5.start();
        cancelCourse = true;
        stopChecking = true;
        cancelCourseButton.setEnabled(false);
        cancelCourseButton.hide();
        restartActivity();

    }

    /**
     * Method called when driver requests to end course
     * @param v default variable, required for onClick method
     */
    @SuppressLint("RestrictedApi")
    public void driverEndCourse(View v) {
        Thread t5 = new Thread(new Runnable()
        {
            @Override
            public void run() {
                try
                {
                    communication.sendEndCourse(passenger.getString("xmppLogin"), currentLocation.getLatitude(), currentLocation.getLongitude());
                } catch (XmppStringprepException | JSONException | SmackException.NotConnectedException | InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        });
        t5.start();
        endCourseButton.setEnabled(false);
        endCourseButton.hide();


        WaitDialog waitDialog = new WaitDialog(15);
        waitDialog.show(getSupportFragmentManager(), "wait");

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(!communication.isEndCourseAgreed()) {/*Loop freezes application, this is intended*/}
                runOnUiThread(new Runnable()
                {
                    public void run()
                    {
                        RateDialog rateDialog = new RateDialog();
                        rateDialog.show(getSupportFragmentManager(), RATE);
                    }
                });
            }
        }).start();
    }

    /**
     * Waits for passenger decision
     * @param thread
     */
    private void waitForDriverDecisionMethod(Thread thread) {
        while(!passengerAgreed) {
            passengerAgreed = communication.isPassenger_agreed();
            if(passengerAgreed) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        try {
                            markTwoPoints(communication.getJs2().getDouble(LATITUDE), communication.getJs2().getDouble(LONGITUDE));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
        try {
            thread.start();
            while(true) {
                if(isPassengerWithMe(communication.getJs2().getDouble(LATITUDE), communication.getJs2().getDouble(LONGITUDE))) {
                    communication.setPassengerWithMe(true);
                    getCurrentLocation();
                    communication.sendInfo(passenger.getString(XMPP_LOGIN), DRIVER, currentLocation.getLatitude(), currentLocation.getLongitude(), "meeting", "server");
                    break;
                }
                else if(stopChecking) break;
            }
        } catch (JSONException | InterruptedException | SmackException.NotConnectedException | XmppStringprepException e) {
            e.printStackTrace();
        }
    }

    /**
     * Waits for clients to meet
     */
    private void waitForMeetingMethod()
    {
        while(!startCourse && !stopChecking) {
            runOnUiThread(new Runnable() {
                public void run() {
                    try {
                        cancelCourseButton.setEnabled(true);
                        cancelCourseButton.setVisibility(View.VISIBLE);
                        updateTwoPoints(communication.getJs2().getDouble("latitude"), communication.getJs2().getDouble("longitude"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            startCourse = communication.isStartCourse();
            if(startCourse) {
                runOnUiThread(new Runnable() {
                    @SuppressLint("RestrictedApi")
                    @Override
                    public void run()
                    {
                        Snackbar snackbar = null;
                        try
                            {
                                snackbar = Snackbar.make(findViewById(R.id.driver_mapFragment_layout), (getString(R.string.in_course_with)+" "+passenger.getString("name")), Snackbar.LENGTH_LONG);
                            }
                        catch (JSONException e)
                            {
                                e.printStackTrace();
                            }
                        snackbar.show();

                        showSearchHideBottomPanel("mini_info",null);
                        hideSearchDuringCourse();


                        cancelCourseButton.setEnabled(false);
                        cancelCourseButton.hide();
                        endCourseButton.setEnabled(true);
                        endCourseButton.show();
                    }
                });
            }
        }
        while(!endCourse && !communication.isEndCourseByDistance() && !communication.isEndCourseAgreed()) {
            endCourse = communication.isEndCourse();
            if(endCourse) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run()
                    {
                        allowBack = false;
                        EndCourseDialog dialog = new EndCourseDialog();
                        dialog.show(getSupportFragmentManager(), END_COURSE);
                    }
                });
            }
            try {
                if(!isPassengerWithMe(communication.getJs2().getDouble(LATITUDE), communication.getJs2().getDouble(LONGITUDE)) && !stopChecking) {
                    communication.sendEndCourseByDistance(passenger.getString(XMPP_LOGIN), communication.getJs2().getDouble(LATITUDE), communication.getJs2().getDouble(LONGITUDE));
                    communication.sendInfo(passenger.getString(XMPP_LOGIN), DRIVER, currentLocation.getLatitude(), currentLocation.getLongitude(), END_COURSE, passenger.getString(XMPP_LOGIN));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            RateDialog rateDialog = new RateDialog();
                            rateDialog.show(getSupportFragmentManager(), RATE);
                        }
                    });
                    break;
                }
            } catch (JSONException | XmppStringprepException | SmackException.NotConnectedException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Listener called when sending rate for current course. Ends current course and restarts activity
     * @param rate value for rate
     */
    @Override
    public void onRateClick(int rate)
    {
        try {
            communication.sendRate(rate, passenger.getString(XMPP_LOGIN), "P");
        } catch (XmppStringprepException | JSONException | SmackException.NotConnectedException | InterruptedException e) {
            e.printStackTrace();
        }
        restartActivity();
        allowBack = true;
    }

    /**
     * Method called when closing activity, makes cure, that threads are stopped, and location for navigation is no longer sent
     */
    public void onDestroy() {
        super.onDestroy();
        stopSendingLocation = true;
        communication.setStopThread(true);
    }

    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    /**
     * Method called when need arises to restart activity. Calls onDestroy and creates this activity anew
     */
    private void restartActivity() {
        Intent intent = new Intent(MapDriver.this, MapDriver.class);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }



    /**
     * Manipulates the map when it's available.
     * This callback is triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        // Enabled traffics
        mMap.setTrafficEnabled(true);

        mMap.setOnCameraMoveStartedListener(this);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                //TODO zakomentowane - Maciej
                //sendLocation.setEnabled(true);

                // Clears the previously touched position
                mMap.clear();

                // Creating a marker
                marker = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                );

                // Animating to the touched position
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                address = latLng;

                startNavigationButton.setEnabled(true);
                startNavigationButton.show();
                stopNavigationButton.setEnabled(true);
                stopNavigationButton.show();
            }
        });

        // Use a custom info window adapter to handle multiple lines of text in the
        // info window contents.

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            // Return null here, so that getInfoContents() is called next.
            public View getInfoWindow(Marker arg0) {
                return null;
            }

          @Override
            public View getInfoContents(Marker marker) {
                // Inflate the layouts for the info window, title and snippet.
                View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_contents,
                        (FrameLayout) findViewById(R.id.driver_mapFragment_layout), false);

                TextView title = ((TextView) infoWindow.findViewById(R.id.title));
                title.setText(marker.getTitle());

                TextView snippet = ((TextView) infoWindow.findViewById(R.id.snippet));
                snippet.setText(marker.getSnippet());

                return infoWindow;
            }
        });


        // Prompt the user for permission.
        getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            if(mLastKnownLocation != null)
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(mLastKnownLocation.getLatitude(),
                                                mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            else{
                                mMap.moveCamera(CameraUpdateFactory
                                        .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", Objects.requireNonNull(e.getMessage()));
        }
    }


    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
                &&
                grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED;

       if (mLocationPermissionGranted) updateLocationUI();
       else
           {
               Toast toast = Toast.makeText(this, "If you don't allow requested permission, application will perform fully!", Toast.LENGTH_LONG);
               toast.show();
           }
    }


    /**
     * Updates the map's UI user_settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", Objects.requireNonNull(e.getMessage()));
        }
    }


    /**
     * Method called when requesting current location. If current location cannot be received, last known location is requested and it cannot be acquired
     * as well, location is set to null
     */
    private void getCurrentLocation() {
        //mMap.clear();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (location != null) {
            currentLocation = location;
        }
        else if (mLastKnownLocation != null)
        {
            currentLocation = mLastKnownLocation;
        }
        else
        {
            currentLocation = null;
        }
    }

    /**
     * Method that draws path between driver and passenger during driver-passenger locating
     * @param driv driver coordinates
     * @param pass passenger coordinates
     */
    private void drawPath(LatLng driv, LatLng pass) {

        new GetPathFromLocation(driv, pass, new DirectionPointListener() {
            @Override
            public void onPath(PolylineOptions polyLine) {
                mMap.addPolyline(polyLine);
            }
        }).execute();
    }

    /**
     * Method called when setting driver and passenger location on map
     * @param passLat passenger latitude
     * @param passLong passenger longitude
     */
    private void markTwoPoints(double passLat, double passLong){
        getCurrentLocation();

        mMap.setOnMarkerClickListener(null);

        LatLng driverPosition = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        LatLng passPosition = new LatLng(passLat, passLong);

        builder.include(passPosition);
        builder.include(driverPosition);
        LatLngBounds bounds = builder.build();

        Bitmap bitmapPassenger = getBitmap(R.drawable.ic_passenger_on_map);
        Bitmap bitmapDriver = getBitmap(R.drawable.ic_driver_on_map);

        passengerMarker = mMap.addMarker(new MarkerOptions()
                .position(passPosition)
                .icon(BitmapDescriptorFactory.fromBitmap(bitmapPassenger))
        );
        driverMarker = mMap.addMarker(new MarkerOptions()
                .position(driverPosition)
                .icon(BitmapDescriptorFactory.fromBitmap(bitmapDriver))
        );
        czyRysowac = false;
        if(czyRysowac){
            drawPath(driverPosition, passPosition);
        }


        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
    }

    private Bitmap getBitmap(int drawableRes) {
        Drawable drawable = getResources().getDrawable(drawableRes);
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    /**
     * Function that updating positions of driver and passenger on map
     * @param drivLat latitude of driver
     * @param drivLong longtitude of driver
     */
    private void updateTwoPoints(double drivLat, double drivLong){
        getCurrentLocation();

        LatLng driverPosition = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        System.out.println(drivLat + "+++++++++++" + drivLong);
        LatLng passPosition = new LatLng(drivLat, drivLong);


        passengerMarker.setPosition(passPosition);
        driverMarker.setPosition(driverPosition);

        builder.include(passPosition);
        builder.include(driverPosition);
        LatLngBounds bounds = builder.build();

        czyRysowac = false;
        if(czyRysowac){
            drawPath(driverPosition, passPosition);
        }

        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
    }

    /**
     * Method checks if passenger and driver are close to each other
     * @param passLat - passenger latitude
     * @param passLong - passenger longitude
     * @return - true if distance between passenger and driver is lower than 50.0
     */
    private boolean isPassengerWithMe(double passLat, double passLong) {
        getCurrentLocation();
        LatLng passPosition = new LatLng(passLat, passLong);
        LatLng drivPosition = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

        double distance = computeDistanceBetween(passPosition, drivPosition);
        return distance < 50.0;
    }


    /**
     * Method calculates ETA(estimated time of arrival) based on current position and target location
     * @param destination target location
     * @return ETA as String
     */
    private String getETA(LatLng destination)
    {

        LatLng driv = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());


        try {
            String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + driv.latitude + "," + driv.longitude + "&destination=" + destination.latitude + "," + destination.longitude + "&sensor=false&units=metric&mode=driving&key=AIzaSyAPHYqAw07v2vu7oeqlo3TymykYJIFro3M";
            JSONObject json = JsonReader.readJsonFromUrl(url);
            JSONArray array = json.getJSONArray("routes");
            JSONObject routes = array.getJSONObject(0);
            JSONArray legs = routes.getJSONArray("legs");
            JSONObject steps = legs.getJSONObject(0);
            JSONObject duration = steps.getJSONObject("duration");
            parsedDuration = duration.getString("text");
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return parsedDuration;
    }

    /**
     * Method that shows time to arrive, distance and name of your destination point
     * @throws IOException
     */
    private void lBelka() throws IOException {
        getCurrentLocation();
        LatLng driverLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        distance = computeDistanceBetween(driverLocation, address);
        distance /= 1000;
        String formattedDistance = String.format("%.2f", distance);
        ETA = getETA(address);

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses  = geocoder.getFromLocation(address.latitude, address.longitude, 1);
        if(addresses != null){
            String addressN = addresses.get(0).getAddressLine(0);
            //String city = addresses.get(0).getLocality();
            //String state = addresses.get(0).getAdminArea();
            //String zip = addresses.get(0).getPostalCode();
            //String country = addresses.get(0).getCountryName();

            textLocationName.setText(addressN);
        }
        else{
            textLocationName.setText("brak adresu dla podanej lokalizacji");
        }

        textEta.setText(String.valueOf(ETA));
        textDistance.setText(formattedDistance + " km");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onAppBackgrounded() {
        communication.setStopThread(true);
        stopSendingLocation = true;
        communication.disconnect();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onAppForegrounded() {
        final WaitDialog waitDialog = new WaitDialog(15);

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (Communication.getConnection() == null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            waitDialog.show(getSupportFragmentManager(), "wait");
                        }
                    });
                    communication.serverConnect();

                    while (true) {
                        if (Communication.getConnection() != null) break;
                    }
                    Log.d("Info", "connected");

                    communication.loginToServer("pass", bundle);
                    while (true) {
                        if (communication.isLoggedIn()) break;
                    }
                    Log.d("Info", "logged in");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            waitDialog.dismiss();
                        }
                    });

                }
            }
        }).start();
    }

    @Override
    public void onBackPressed()
    {
        if (allowBack)
            {
                //communication.setStopThread(true);
                //stopSendingLocation = true;
                //communication.disconnect();
                super.onBackPressed();
            }
    }


    /**
     * Method called on activity start. Connects with google API client
     */
    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    /**
     * Method called on activity ending. Disconnects with google API client
     */
    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {/*No action needs to be taken*/}

    //TODO Mateusz Trzeba przygotować wysyłanie danych do servera w każdym z przypadków timeout'u WaitDialog
    /**
     * this method will be called if WaitDialog has not received answer from server for longer than 15 seconds.
     * It restores app to base state
     */
    @Override
    public void onTimeout()
    {
//        Toast toast = Toast.makeText(this, "An error has occurred!", Toast.LENGTH_SHORT);
//        toast.show();

    }

    /**
     * This method will ??? todo Karol
     * @param i
     */
    @Override
    public void onCameraMoveStarted(int i)
    {
        System.out.println("map moved");
        if (i != GoogleMap.OnCameraMoveStartedListener.REASON_DEVELOPER_ANIMATION)
        {
            if (vierTimerOn)
            {
                vier.cancel();
                vierTimerOn = false;
                centerButton.setEnabled(true);
                centerButton.show();
            }
            else Log.d("Exception", "Timer vier should be active at this moment");
        }
    }

    /**
     * This method will start timer upon pressing the corresponding button
     * @param view default variable, required fo onClick method
     */
    public void centerMap(View view)
    {
        System.out.println("centerMap called");

        if (!vierTimerOn)
            {
                vier = new Timer();
                vier.schedule(new TimerTask()
                {
                    @Override
                    public void run()
                    {
                        getCurrentLocation();
                        driverMoving = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                System.out.println("map centered");
                                mMap.animateCamera(CameraUpdateFactory.newLatLng(driverMoving));
                            }
                        });
                    }
                }, 0, 2 * 1000L);
                vierTimerOn = true;
                centerButton.setEnabled(false);
                centerButton.hide();
            }
    }
}
