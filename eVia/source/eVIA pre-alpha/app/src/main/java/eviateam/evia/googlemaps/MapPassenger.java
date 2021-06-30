package eviateam.evia.googlemaps;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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
import com.google.android.gms.maps.model.CameraPosition;
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
import org.json.JSONException;
import org.json.JSONObject;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import eviateam.evia.R;
import eviateam.evia.activities.ProfileActivity;
import eviateam.evia.activities.SettingsActivity;
import eviateam.evia.dialogs.EndCourseDialog;
import eviateam.evia.dialogs.JakDojadeDialog;
import eviateam.evia.dialogs.PassengerDialog;
import eviateam.evia.dialogs.RateDialog;
import eviateam.evia.dialogs.WaitDialog;
import eviateam.evia.utility.Communication;
import eviateam.evia.utility.UserRoleShiftClass;

import static com.google.maps.android.SphericalUtil.computeHeading;


/**
 * An activity that displays a map at the device's current location.
 * Google sample
 */
public class MapPassenger extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, EndCourseDialog.EndCourseListener, JakDojadeDialog.JakDojadeListener, PassengerDialog.PassengerDialogListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, RateDialog.RateDialogListener, WaitDialog.WaitDialogListener, LifecycleObserver
{


    private static final String TAG = MapPassenger.class.getSimpleName();
    private GoogleMap mMap;
    private CameraPosition mCameraPosition;
    private GoogleApiClient googleApiClient;
    private Location currentLocation;
    private LatLng destination = null;
    /**
     * end course button
     */
    private FloatingActionButton endCourseButton;
    /**
     * cancel course button
     */
    private FloatingActionButton cancelCourseButton;
    /**
     * search button
     */
    private FloatingActionButton searchButton;
    /**
     * passanger mode logo
     */
    private ImageView passangerModeLogo;
    /**
     * map layout
     */
    private LinearLayout lMapa;
    /**
     * search layout
     */
    private LinearLayout lSzukaj;
    /**
     * driverInfoBoard bottom panel
     */
    //dolna belka
    private LinearLayout driverInfoBoard;
    /**
     * driver profile picture
     */
    private ProfilePictureView profiloweInfoBoard;
    /**
     * information about car
     */
    private ImageView carInfoBoard;
    /**
     * car colour
     */
    private LinearLayout colorInfoBoard;
    /**
     * driver name
     */
    private TextView nameInfoBoard;
    /**
     * driver rate
     */
    private RatingBar rateInfoBoard;

    /**
     * drawer layout
     */
    private DrawerLayout drawer;
    /**
     * side menu bar
     */
    private NavigationView navView;

    /**
     * JakDojade window
     */
    WebView wv;

    private boolean czyRysowac;
    private Bitmap bitmapPassenger;
    private Bitmap bitmapDriver;
    private Marker passengerMarker;
    private Marker driverMarker;
    private LatLngBounds.Builder builder = new LatLngBounds.Builder();
    private Marker marker;

    private boolean driver_found = false;
    private boolean driver_agreed = false;
    private boolean jakDojade = false;
    private boolean startCourse = false;
    private boolean endCourse = false;
    private boolean jakDojadeBack = false;

    Bundle bundle;

    private Thread listener = new Thread(new Runnable() {
        @Override
        public void run() {
            communication.driverListener();
        }
    });

    private Thread waitForMeeting = new Thread(new Runnable() {
        @Override
        public void run() {
            waitForMeetingMethod();
        }
    });

    private Thread waitForDriverDecision = new Thread(new Runnable() {
        @Override
        public void run() {
            waitForDriverDecisionMethod(waitForMeeting);
        }
    });


    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;

    //private MarkerOptions markerOptions = new MarkerOptions();

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private LatLng address;
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    /**
     * last known location
     */
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
    private JSONObject driver = new JSONObject();

    private JSONObject sendJson = new JSONObject();
    private boolean stopChecking = false;
    private boolean cancelCourse = false;
    private boolean allowBack = true;
    private boolean outOfTime = false;

    /**
     * Listener called when item from menu bar is seleceted
     * @param menuItem - selected item
     * @return - true to display the item as the selected item
     */
    @Override
    public boolean onNavigationItemSelected( MenuItem menuItem) {
        switch (menuItem.getItemId())
            {
            case R.id.my_profile:
            {
                goToProfile();
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
     * Method that changes from passenger mode to driver mode
     */
    private void switchRole()
    {
        ProcessLifecycleOwner.get().getLifecycle().removeObserver(this);

        Intent intent;
        intent = new Intent(this, MapDriver.class);
        intent.putExtras(bundle);

        UserRoleShiftClass shiftClass = new UserRoleShiftClass();

        try
            {
                shiftClass.userRoleShiftToDriver(getApplicationContext());
            }
        catch (IOException e)
            {
                Log.d("Exception", "IOException in switchRole()");
                e.printStackTrace();
            }
        startActivity(intent);
        finish();
    }

    /**
     * Method closes application
     */
    private void logout()
    {
        //communication.setStopThread(true);
        //communication.disconnect();
        LoginManager.getInstance().logOut();
        finish();
    }

    /**
     * Method shows settings window
     */
    private void goToSettings()
    {
        Intent intent;
        intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    /**
     * Method shows profile window
     */
    private void goToProfile()
    {
        Intent intent;
        intent = new Intent(this, ProfileActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /**
     * Method uncheck items in item list
     */
    public void onResume() {
        super.onResume();
        int size = navView.getMenu().size();
        for (int i = 0; i < size; i++) {
            navView.getMenu().getItem(i).setChecked(false);
        }
    }

    /**
     * Method hides search board and shows information board
     */
    private void hideSearchShowInfoBoard() {

        driverInfoBoard.setVisibility(View.VISIBLE);

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
        lp.setMargins(0, 0, 0, 250);
        lMapa.setLayoutParams(lp);

        lSzukaj.setVisibility(View.GONE);
        lSzukaj.setEnabled(false);
    }

    /**
     * Method hides information board
     */
    private void hideInfoBoard() {
        driverInfoBoard.setVisibility(View.GONE);
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) lMapa.getLayoutParams();
        lp.setMargins(0, 0, 0, 0);
        lMapa.setLayoutParams(lp);
    }

    /**
     * Method shows search board and hides information board
     */
    private void showSearchhideInfoBoard() {

        driverInfoBoard.setVisibility(View.GONE);

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
    /**
     * Sets contents for activity
     * @param savedInstanceState
     */
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);

        setContentView(R.layout.activity_maps_passenger);

        endCourseButton = findViewById(R.id.passenger_end_course_button);
        cancelCourseButton = findViewById(R.id.passenger_cancel_course_button);
        searchButton = findViewById(R.id.search_driver_button);
        passangerModeLogo = findViewById(R.id.imageView2);

        endCourseButton.setEnabled(false);
        endCourseButton.hide();
        cancelCourseButton.setEnabled(false);
        cancelCourseButton.hide();
        searchButton.setEnabled(false);
        searchButton.hide();

        driverInfoBoard = findViewById(R.id.mini_driver_info_board);
        profiloweInfoBoard = findViewById(R.id.profile_picture_driver_info_board);
        carInfoBoard = findViewById(R.id.car_driver_info_board);
        colorInfoBoard = findViewById(R.id.color_driver_info_board);
        nameInfoBoard = findViewById(R.id.driver_name_info_board);
        rateInfoBoard = findViewById(R.id.driver_rating_info_board);

        lMapa = findViewById(R.id.layout_mapa);
        lSzukaj = findViewById(R.id.layout_szukajka);

        Places.initialize(getApplicationContext(), "AIzaSyAPHYqAw07v2vu7oeqlo3TymykYJIFro3M");
        PlacesClient placesClient = Places.createClient(this);

        //deklaracja drawer View
        drawer = findViewById(R.id.layout_passanger);
        navView = findViewById(R.id.nav_view);
        navView.getMenu().getItem(2).setVisible(false); //niewidzialny guzik samochodu
        navView.setNavigationItemSelectedListener(this);


        bundle = getIntent().getExtras();

        mId = bundle.getString("id");
        mFirstName = bundle.getString("fname");
        mLastName = bundle.getString("lname");

        //Ustawienie nagłówka w menu wysuwanym
        View headerView = navView.getHeaderView(0);
        TextView navRole = (TextView) headerView.findViewById(R.id.role);
        navRole.setText("Tryb: passenger");

        TextView navName = headerView.findViewById(R.id.name);
        navName.setText(mFirstName + " " + mLastName);

        ProfilePictureView navImage = headerView.findViewById(R.id.profileImage);
        navImage.setProfileId(mId);


        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.passenger_place_autocomplete);

        // Specify the types of place data to return.
        assert autocompleteFragment != null;
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
        autocompleteFragment.setCountries("PL");
        autocompleteFragment.setHint("Enter your desired location");


        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener()
        {
            /**
             * Method called  when selected places
             * @param place variable containings data on selected place
             */
            @Override
            public void onPlaceSelected(Place place)
            {
                Log.d("Maps", "Place selected: " + place.getName());

                address = place.getLatLng();                        // KORDY

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(address);
                mMap.addMarker(markerOptions);


                try
                {
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(address));
                }
                catch (NullPointerException e)
                {
                    e.printStackTrace();
                }

                searchButton.setEnabled(true);
                searchButton.show();
                Toast.makeText(getApplicationContext(), "Click on search icon to search for driver", Toast.LENGTH_LONG).show();
            }

            /**
             * Method called on error when selected places
             * @param status error status
             */
            @Override
            public void onError(@NonNull Status status)
            {
                Log.d("Maps", "An error occurred: " + status);
            }
        });


        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        // Retrieve the content view that renders the map.
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);



        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Build the map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.passenger_mapFragment_layout);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        //Initializing googleApiClient
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        listener.start();
        waitForDriverDecision.start();

    }

    /**
     * Method that search for driver
     * @param v - default variable, required for onClick method
     */
    //po kliknięciu na guzik z szukaniem kierowcy
    public void searchForDriver(View v)
    {
        Log.d("ALLELUJA!!!!!!!!", "Nacisnąłem");
        /*      //Okienko wyszukiwnia kierowcy
        WaitingDialog dialog =  new WaitingDialog(MapPassenger.this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));
        dialog.show();
        */
        sendLocation(true);
    }

    /**
     * Method that cancel course
     * @param v - default variable, required for onClick method
     */
    @SuppressLint("RestrictedApi")
    public void passengerCancelCourse(View v)
    {
        Thread t5 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    communication.sendCancelCourse(driver.getString("xmppLogin"), currentLocation.getLatitude(), currentLocation.getLongitude());
                } catch (XmppStringprepException | JSONException | SmackException.NotConnectedException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t5.start();
        cancelCourse = true;
        cancelCourseButton.setEnabled(false);
        cancelCourseButton.setVisibility(View.GONE);
        restartActivity();

    }

    /**
     * Method that end course
     * @param v - default variable, required for onClick method
     */
    @SuppressLint("RestrictedApi")
    public void passengerEndCourse(View v) {
        Thread t5 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    communication.sendEndCourse(driver.getString("xmppLogin"), currentLocation.getLatitude(), currentLocation.getLongitude());
                } catch (XmppStringprepException | JSONException | SmackException.NotConnectedException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t5.start();

        endCourseButton.setEnabled(false);
        endCourseButton.hide();

        showSearchhideInfoBoard();


        final WaitDialog waitDialog = new WaitDialog(15);
        waitDialog.show(getSupportFragmentManager(), "wait");


        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!communication.isEndCourseAgreed()) {
                }
                runOnUiThread(new Runnable() {
                    public void run() {
                        waitDialog.dismiss();
                        RateDialog rateDialog = new RateDialog();
                        rateDialog.show(getSupportFragmentManager(), "rate");
                    }
                });
            }
        }).start();
    }

    /**
     * Creates JSON which contains user's information
     * @param dstLatitude
     * @param dstLongitude
     * @param longitude
     * @param latitude
     * @param fbId
     * @param name
     * @param surname
     * @throws JSONException
     */
    private void putInSendJson(double dstLatitude, double dstLongitude, double longitude, double latitude, String fbId, String name, String surname) throws JSONException {
        sendJson.put("dstLongitude", dstLongitude);
        sendJson.put("dstLatitude", dstLatitude);
        sendJson.put("direction", computeDirection(dstLatitude, dstLongitude));
        sendJson.put("longitude", longitude);
        sendJson.put("latitude", latitude);
        sendJson.put("fbId", fbId);
        sendJson.put("name", name);
        sendJson.put("surname", surname);
    }

    /**
     * Waits for driver decision
     * @param thread
     */
    private void waitForDriverDecisionMethod(final Thread thread) {
        while(!driver_agreed) {
            driver_agreed = communication.isDriver_agreed();
            if(driver_agreed) {
                runOnUiThread(new Runnable()
                {
                    public void run() {
                        getCurrentLocation();
                        try {
                            markTwoPoints(communication.getJs2().getDouble("latitude"), communication.getJs2().getDouble("longitude"));
                            thread.start();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                try {
                    while(!communication.isPassengerWithMe() && !communication.isDriverWithMe() && !communication.isEndCourseAgreed() && !communication.isCancelClicked() && !cancelCourse) {
                        communication.sendLocationToClient(driver.getString("xmppLogin"), currentLocation.getLatitude(), currentLocation.getLongitude());
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

                } catch (XmppStringprepException | JSONException | SmackException.NotConnectedException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Waits for clients to meet
     */
    private void waitForMeetingMethod() {
        while(!startCourse && !stopChecking) {
            runOnUiThread(new Runnable() {
                public void run() {
                    try {
                        cancelCourseButton.setEnabled(true);
                        cancelCourseButton.show();
                        updateTwoPoints(communication.getJs2().getDouble("latitude"), communication.getJs2().getDouble("longitude"));
                        System.out.println(communication.getJs2().getDouble("latitude") + " " + communication.getJs2().getDouble("longitude"));
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
                                snackbar = Snackbar.make(findViewById(R.id.passenger_mapFragment_layout), (getString(R.string.in_course_with) + " " + driver.getString("name")), Snackbar.LENGTH_LONG);
                            }
                        catch (JSONException e)
                            {
                                e.printStackTrace();
                            }
                        snackbar.show();

                        cancelCourseButton.setEnabled(false);
                        cancelCourseButton.setVisibility(View.GONE);
                        endCourseButton.setEnabled(true);
                        endCourseButton.show();

                        hideInfoBoard();
                    }
                });
            }
        }
        while(!endCourse) {
            endCourse = communication.isEndCourse();
            if(endCourse) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run()
                    {
                        allowBack = false;
                        EndCourseDialog dialog = new EndCourseDialog();
                        dialog.show(getSupportFragmentManager(), "endCourse");
                    }
                });
            }
            if(communication.isEndCourseByDistance()) {
                communication.setEndCourseByDistance(false);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        RateDialog rateDialog = new RateDialog();
                        rateDialog.show(getSupportFragmentManager(), "rate");
                    }
                });
                break;
            }
        }
    }

    /**
     * Method that stops sending location
     */
    public void sendLocationWhenDeclined() {
        sendLocation(false);
    }

    /**
     * Sends location to server
     * @param sendLocationToServer
     */
    private void sendLocation(final boolean sendLocationToServer) {
        outOfTime = false;
        getCurrentLocation();
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    putInSendJson(address.latitude, address.longitude, currentLocation.getLongitude(), currentLocation.getLatitude(), mId, mFirstName, mLastName);
                    if (sendLocationToServer)
                        communication.sendLocationToServer(sendJson, "passenger_mode"); //wyslanie lokalizacji do serwera
                    while (!driver_found && !jakDojade && !driver_agreed && !outOfTime) { //pętla, która czeka, aż serwer przyśle wiadomość o znalezieniu kierowcy
                        driver_found = communication.isDriver_found();
                        jakDojade = communication.isJakDojade();
                        outOfTime = communication.isOutOfTime();
                    }
                    if (driver_found) { // jeśli znaleziono kierowcę, to utworzy okienko z wyborem tak/nie

                        communication.setDriver_found(false);
                        driver_found = communication.isDriver_found();

                        driver = communication.getJs2();

                        runOnUiThread(new Runnable() {
                            //TODO upewnić się, że w bu ndlu jest ocena użytkownika
                            public void run() {
                                System.out.println("wyswietlam dialog!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                                PassengerDialog dialog = new PassengerDialog(MapPassenger.this, driver);
                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));
                                dialog.show();
                            }
                        });
                    } else if (jakDojade) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                System.out.println("JAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK");
                                JakDojadeDialog dialog = new JakDojadeDialog();
                                passangerModeLogo.setVisibility(View.GONE);
                                dialog.show(getSupportFragmentManager(), "JakDojade");
                            }
                        });
                    } else if (outOfTime) {
                        communication.setOutOfTime(false);
                        System.out.println("TOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOSSSSSSSSSSSSSSSSSSSSSSSTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT");
                        onOutOfTime();
                    }


                    if(!driver_agreed && !jakDojade && !outOfTime) sendLocationWhenDeclined();
                }
                catch (JSONException | InterruptedException | IOException | SmackException e) {
                    e.printStackTrace();
                }
            }
        });
        t1.start();
    }

    /**
     * Listener which draw a path when passenger accepted riding together with driver
     * @throws JSONException
     */
    @SuppressLint("RestrictedApi")
    @Override
    public void onPassengerAgreed(JSONObject jsonObject)
    {
        //cancelCourseButton.setEnabled(true);
        //cancelCourseButton.show();
        searchButton.setEnabled(false);
        searchButton.hide();

        hideSearchShowInfoBoard();

        try {
            //profilowe fb kierowcy
            profiloweInfoBoard.setProfileId(jsonObject.getString("fbId"));
            //kolor samochodu kierowcy
            colorInfoBoard.getBackground().setColorFilter(Integer.parseInt(jsonObject.getString("color")), PorterDuff.Mode.SRC_IN);
            //imie,marka,nr
            nameInfoBoard.setText(jsonObject.getString("name")  + "\n" + jsonObject.getString("brand") + "\n" + jsonObject.getString("registration"));
            //zdjęcie samochodu
            String string = jsonObject.getString("carPhoto");
            byte[] byteArray = null;
            byteArray = Base64.decode(string, Base64.NO_WRAP);
            Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            carInfoBoard.setImageBitmap(bitmap);
            //ocena
            float rating = (float)jsonObject.getDouble("rate");
            if (rating == 0)
            {
                rateInfoBoard.setRating(0);
            }
            else
            {
                rateInfoBoard.setRating(rating);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    communication.sendYesOrNo(true, communication.getJs2().getInt("questionId"));
                } catch (XmppStringprepException | SmackException.NotConnectedException | InterruptedException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        t1.start();

        //LatLng driver = new LatLng(communication.getJs2().getDouble("latitude"), communication.getJs2().getDouble("longitude"));
        //LatLng passenger = new LatLng(53.030549, 18.598114);
        //drawPath(driver, passenger);
    }

    /**
     * Listenr which changes status of driver when passenger declined riding together with driver
     */
    @Override
    public void onPassengerDeclined() {
        communication.setStatus("FREE");
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
        t1.start();
    }

    /**
     * Method called when waiting for answer is too long
     */
    @Override
    public void onOutOfTime()
    {
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
     * Listener called when passenger accept using JakDojade
     */
    @SuppressLint("RestrictedApi")
    @Override
    public void onJakDojadeAgreed()
    {
        jakDojade = false;
        communication.setJakDojade(false);

        if (address == null || currentLocation == null)
        {
            Snackbar snackbar = Snackbar.make(findViewById(R.id.passenger_mapFragment_layout), "Couldn't find your location or destination!", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
        else
        {
            searchButton.setEnabled(false);
            searchButton.hide();

            wv = findViewById(R.id.webview);

            otworzJakDojade(currentLocation);

        }
    }

    /**
     * Method showing JakDojade window with path to the place where you want to go
     * @param location - your actual position
     */
    private void otworzJakDojade(final Location location)
    {
        jakDojadeBack = true;

        runOnUiThread(new Runnable()
        {
            public void run()
            {
                //pobranie daty z telefonu
                Date currentTime = Calendar.getInstance().getTime();
                //ustawienie formatu daty
                SimpleDateFormat format1 = new SimpleDateFormat("dd.MM.yy");
                String date = format1.format(Date.parse(currentTime.toString()));
                //ustawienie formatu casu
                SimpleDateFormat format2 = new SimpleDateFormat("HH:mm");
                String time = format2.format(Date.parse(currentTime.toString()));

                //enable JavaScript
                WebSettings webSettings = wv.getSettings();
                webSettings.setJavaScriptEnabled(true);
                //dzieki temu działają zwracane przez JakDojade trasy
                wv.getSettings().setDomStorageEnabled(true);

                wv.setVisibility(View.VISIBLE);

                //System.out.println("KORDY: "+coords.latitude);

                String url = "https://jakdojade.pl/torun/trasa/z--undefined--do--undefined?tc="+address.latitude+":"+address.longitude+"&fc="+ location.getLatitude()+":"+ location.getLongitude()+"&ft=LOCATION_TYPE_COORDINATE&tt=LOCATION_TYPE_COORDINATE&d="+date+"&h="+time+"&aro=1&t=1&rc=3&ri=1&r=0";
                wv.loadUrl(url);
            }
        });
    }

    /**
     * Listener called when passenger declined using JakDojade
     */
    @Override
    public void onJakDojadeDeclined() {
        jakDojade = false;
        communication.setJakDojade(false);
    }

    /**
     * Listener called when passenger accept end of the course
     */
    @Override
    public void onEndCourseAgreed() {
        Thread t6 = new Thread(new Runnable()
            {
                @Override
                public void run() {
                    try
                        {
                            communication.sendInfo(driver.getString("xmppLogin"), "passenger", currentLocation.getLatitude(), currentLocation.getLongitude(), "endCourse", "server");
                        } catch (XmppStringprepException | JSONException | SmackException.NotConnectedException | InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                }

        });
        t6.start();
        communication.setEndCourseAgreed(true);

        RateDialog rateDialog = new RateDialog();
        rateDialog.show(getSupportFragmentManager(), "rate");
    }
    /**
     * Listener called when passenger decline end of the course
     */
    @Override
    public void onEndCourseDecline(){}

    /**
     * Listener called when passenger rate driver
     * @param rate - rate of the driver
     */
    @Override
    public void onRateClick(int rate)
    {
        try {
            communication.sendRate(rate, driver.getString("xmppLogin"), "D");
        } catch (XmppStringprepException | JSONException | SmackException.NotConnectedException | InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb");
        restartActivity();
        allowBack = true;
    }

    public void onDestroy() {
        super.onDestroy();
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
     * Method that restarts activity
     */
    private void restartActivity() {
        Intent intent = new Intent(MapPassenger.this, MapPassenger.class);
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

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @SuppressLint("RestrictedApi")
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

                // Placing a marker on the touched position
                address = latLng;

                //guzik do szukania kierowcy
                searchButton.setEnabled(true);
                searchButton.show();
                Toast.makeText(getApplicationContext(),"Click on search icon to search for driver", Toast.LENGTH_LONG).show();
            }
        });

        /*mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.remove();
                return false;
            }
        });*/


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
            Log.e("Exception: %s", e.getMessage());
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
        // If request is cancelled, the result arrays are empty.
        mLocationPermissionGranted = requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION && grantResults.length > 0
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
            Log.e("Exception: %s", "SecurityException");
        }
    }

    /**
     * Drawing path between two points
     * @param driv coordinates of driver
     * @param pass coordinates of passenger
     */
    private void drawPath(LatLng driv, LatLng pass) {

        new GetPathFromLocation(driv, pass, new DirectionPointListener() {      ////////////////////////////////////////////////////////////////////////////
            @Override
            //////////////////RYSOWANIE//////////////////////////////////////////
            public void onPath(PolylineOptions polyLine) {
                mMap.addPolyline(polyLine);
            }                                                                              /////////////////////////TRASA//////////////////////////////////
        }).execute();
    }


//    private void acceptedRequest(double latDriv, double longDriv){
//        getCurrentLocation();
//
//        if (currentLocation == null)
//        {
//            Snackbar snack = Snackbar.make(findViewById(R.id.driver_mapFragment_layout), "Cannot find current location", Snackbar.LENGTH_LONG);
//            snack.show();
//        }
//        else//if cannot find currentLocation for whatever reason, we cannot continue
//        {
//
//            LatLng pass = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
//            LatLng driv = new LatLng(latDriv, longDriv);
//        }
//    }

    /**
     * Method that gets my current location
     */
    //Getting current location
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
    }

    /**
     * Method that sets on map passenger and driver positions
     * @param drivLat - driver latitude position
     * @param drivLong - driver longitude position
     */
    private void markTwoPoints(double drivLat, double drivLong){
        getCurrentLocation();

        mMap.setOnMapClickListener(null);

        LatLng passPosition = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        System.out.println(drivLat + "+++++++++++" + drivLong);
        LatLng driverPosition = new LatLng(drivLat, drivLong);

        builder.include(passPosition);
        builder.include(driverPosition);
        LatLngBounds bounds = builder.build();

        bitmapPassenger = getBitmap(R.drawable.ic_passenger_on_map);
        bitmapDriver = getBitmap(R.drawable.ic_driver_on_map);

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

    /**
     * Method that updates passenger and driver positions
     *  @param drivLat - driver latitude position
     *  @param drivLong - driver longitude position
     */
    private void updateTwoPoints(double drivLat, double drivLong){
        getCurrentLocation();
        LatLng passPosition = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        System.out.println(drivLat + "+++++++++++" + drivLong);
        LatLng driverPosition = new LatLng(drivLat, drivLong);


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
     * Method that computing directions between driver and passenger
     * @param drivLat driver latitude
     * @param drivLong driver longtitude
     * @return value between [-180, 180)
     */
    private double computeDirection(double drivLat, double drivLong){
        getCurrentLocation();
        LatLng passPosition = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        LatLng drivPosition = new LatLng(drivLat, drivLong);

        return computeHeading(passPosition, drivPosition);
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

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onAppBackgrounded() {
        communication.setStopThread(true);
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

    /**
     * Method hiding JakDojade functionality if variable jakDojadeBack is set
     */
    @Override
    public void onBackPressed()
    {
        if (jakDojadeBack)
            {
                wv.setVisibility(View.GONE);
                wv.setEnabled(false);
                jakDojadeBack = false;

            }
        else if (allowBack)
            {
                //communication.setStopThread(true);
                //communication.disconnect();
                super.onBackPressed();
            }

    }

    /**
     * Method called on activity connecting. Updates current location
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getCurrentLocation();
    }

    /**
     * Method called on activity connecting suspended.
     */
    @Override
    public void onConnectionSuspended(int i) {

    }

    /**
     * Method called on activity connecting failed.
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture)
    {

    }

    /**
     * this method will be called if WaitDialog has not received answer from server for longer than 15 seconds.
     * It restores app to base state
     */
    @Override
    public void onTimeout()
    {

    }
}
