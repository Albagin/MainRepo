package eviateam.evia.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import eviateam.evia.R;
import eviateam.evia.dialogs.CarChangeDialog;
import eviateam.evia.dialogs.CarOptionsDialog;
import eviateam.evia.dialogs.WaitDialog;
import eviateam.evia.entities.CarEntity;
import eviateam.evia.googlemaps.MapDriver;
import eviateam.evia.googlemaps.MapPassenger;
import eviateam.evia.utility.Communication;
import eviateam.evia.utility.UserRoleShiftClass;

/**
 * Class supporting car menu for driver. Allows adding, editing and removing cars
 */
public class CarsActivity extends AppCompatActivity implements CarOptionsDialog.CarOptionListener, CarChangeDialog.CarChangeDialogListener, WaitDialog.WaitDialogListener
{

    /**
     * Class with tools for XMPP communication
     */
    private Communication communication = new Communication();

    /**
     * Thread listening for car list requests
     */
    private Thread listener = new Thread(new Runnable() {
        @Override
        public void run() {
            communication.carsListener();
        }
    });

    private ListView listView;
    /**
     * Current carEntity list
     */
    private List<CarEntity> list = new ArrayList<>();
    /**
     * Adapter that supports listView
     */
    private ArrayAdapter<CarEntity> adapter;
    /**
     * Variable monitoring if onBackPressed needs to be overloaded
     */
    private boolean allowBack = true;
    /**
     * Bundle received from mapDriver, containing driver data
     */
    private Bundle bundle;
    private LinearLayout carColor;

    private ImageView favCar;
    private TextView infoCar;

    /**
     * If a user tries to go back to driver mode without any car registered, he will be transported to passenger mode instead
     */
    @Override
    public void onBackPressed() {
        if (!allowBack)
        {
            UserRoleShiftClass shift = new UserRoleShiftClass();

            try
                {
                    shift.userRoleShiftToPassenger(getApplicationContext());
                }
            catch (IOException e)
                {
                    Log.d("Exception", "IOException in onBackPressed");
                }

            Intent intent = new Intent(this, MapPassenger.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
        else
        {
            Intent intent = new Intent(this, MapDriver.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
        finish();
    }


    /**
     * Sets contents for activity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_cars);

        bundle = getIntent().getExtras();

        Log.d("Info", "hasCars:" + Objects.requireNonNull(bundle).getBoolean("hasCars"));



        try
            {
                allowBack = bundle.getBoolean("hasCars");
            }
            catch (Exception e)
                {
                    Log.d("Info", "No information about no cars received, driver has at least one car then.");
                }

        listView  = findViewById(R.id.cars_list_view);

        listener.start();

        communication.sendCarsRequest();

        final WaitDialog waitDialog = new WaitDialog(15);
        waitDialog.show(getSupportFragmentManager(), "wait");


        //w wątku po to, aby waitDialog się kręcił
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    if (communication.isFoundCars()) {
                        waitDialog.dismiss();
                        break;
                    }
                }
                communication.setFoundCars(false);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        mapTableView();

                        adapter = new ArrayAdapter<CarEntity>(getApplicationContext(),
                                R.layout.list_item_card, android.R.id.text1, list)
                        {
                            @NonNull
                            @Override
                            public View getView(int position, View convertView, @NonNull ViewGroup parent)
                            {
                                //View row = super.getView(position, convertView, parent);
                                //View row = convertView;

                                LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                View row = inflater.inflate(R.layout.list_item_card, parent, false);

                                favCar = row.findViewById(R.id.set_favourite);
                                infoCar = row.findViewById(R.id.car_info);
                                carColor = row.findViewById(R.id.car_color);

                                CarEntity car = getItem(position);

                                infoCar.setText(car.getBrand() +  "\n " + car.getRegistration());
                                //carColor.setBackgroundColor(Integer.parseInt(car.getColor()));
                                carColor.getBackground().setColorFilter(Integer.parseInt(car.getColor()), PorterDuff.Mode.SRC_IN);



                                if(Objects.requireNonNull(getItem(position)).getIsDefault())
                                {
                                    //row.setBackgroundColor (Color.YELLOW);
                                    favCar.setImageResource(R.drawable.ic_car_favourite);
                                }
                                else
                                {
                                    //row.setBackgroundColor (Color.WHITE);
                                    favCar.setImageResource(R.drawable.ic_car_not_favourite);
                                }
                                return row;
                            }
                        };
                        listView.setAdapter(adapter);

                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                        {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
                            {
                                CarEntity d = adapter.getItem(position);

                                if (d != null)
                                {
                                    CarOptionsDialog dialog = new CarOptionsDialog(d, returnCarString(d));
                                    dialog.show(getSupportFragmentManager(), "carOptions");
                                }
                                else Log.d("Exception", "Couldn't receive car information. CarsActivity -> onItemClick");
                            }
                        });

                        markDefaultCar();
                        adapter.notifyDataSetChanged();

                        if (list.isEmpty())
                        {
                            Snackbar snackbar = Snackbar.make(findViewById(R.id.cars_layout),"You have no cars in our database. Please add at least one car.", Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }

                    }
                });
            }
        }).start();


    }

    /**
     * Method called when closing activity. Makes sure, that acting threads are stopped
     */
    public void onDestroy()
    {
        super.onDestroy();
        communication.setStopThread(true);
    }

    /**
     * Method converting acquired JSONObject to carEntity, using one of the constructors depending on whether car has photo registered or not
     * @param jsonObject object with car data
     * @return newly created carEntity object
     * @throws JSONException thrown if car does not have photo registered. If exception is cough, method will use another constructor
     */
    private CarEntity convertJSONToCar(JSONObject jsonObject) throws JSONException
    {
        CarEntity car;
        try {
            car = new CarEntity(jsonObject.getInt("id"), jsonObject.getString("registration"), jsonObject.getString("brand"),
                    jsonObject.getString("color"), jsonObject.getInt("userId"), jsonObject.getBoolean("isDefault"), jsonObject.getInt("photoId"));
        } catch (JSONException e) {
            car = new CarEntity(jsonObject.getInt("id"), jsonObject.getString("registration"), jsonObject.getString("brand"),
                    jsonObject.getString("color"), jsonObject.getInt("userId"), jsonObject.getBoolean("isDefault"));
        }
        return car;
    }

    /**
     * Method creating JSONObject from carEntity object
     * @param carEntity object to be converted
     * @return newly created JSONObject object
     * @throws JSONException thrown by JSONObject.put
     */
    private JSONObject convertCarToJSON(CarEntity carEntity) throws JSONException
    {
        JSONObject car = new JSONObject();
        car.put("id", carEntity.getId());
        car.put("registration", carEntity.getRegistration());
        car.put("brand", carEntity.getBrand());
        car.put("color", carEntity.getColor());
        car.put("userId", carEntity.getUserId());
        car.put("isDefault", carEntity.getIsDefault());
        car.put("photoId", carEntity.getPhotoId());
        return car;
    }

    /**
     * Listener that will create JSONObject from specific carEntity, and send it to server requesting removing it.
     * @param carEntity carEntity to be removed
     */
    @Override
    public void onCarRemoveClicked(CarEntity carEntity)
    {
        try {
            JSONObject car = convertCarToJSON(carEntity);
            communication.sendCarChanges("removeCar", car);
        } catch (JSONException | XmppStringprepException e) {
            e.printStackTrace();
        }
        while(true) {if (communication.isFoundCars()) break;}
        communication.setFoundCars(false);


        mapTableView();
        adapter.notifyDataSetChanged();

        if (list.isEmpty())
            {
                Snackbar snackbar = Snackbar.make(findViewById(R.id.cars_layout),"You have no cars in our database. Please add at least one car.", Snackbar.LENGTH_LONG);
                snackbar.show();
                allowBack = false;
            }
    }

    /**
     * Listener that starts CarChangeDialog
     * @param carEntity carEntity to be edited
     * @param carString photo as String created in CarImageFinder class
     */
    @Override
    public void onCarEditClicked(CarEntity carEntity, String carString)
    {
        CarChangeDialog d = new CarChangeDialog(carEntity, carString);
        d.show(getSupportFragmentManager(), "carEdit");
    }

    /**
     * Listener called when requesting adding new carEntity without photo
     * @param carEntity carEntity to be added
     */
    @Override
    public void onCarAdd(CarEntity carEntity)
    {
        sendCarToServer("add", carEntity);
        allowBack = true;
        mapTableView();
        adapter.notifyDataSetChanged();
    }

    /**
     * Listener called when  requesting adding new carEntity with photo
     * @param carEntity carEntity to be added
     * @param string photo as String created in CarImageFinder class
     */
    @Override
    public void onCarAdd(CarEntity carEntity, String string)
    {
        sendCarWithPhotoToServer("photoAdd", carEntity, string);
        allowBack = true;
        mapTableView();
        adapter.notifyDataSetChanged();
    }

    /**
     * Listener called when requesting editing car without photo
     * @param carEntity carEntity to be edited
     */
    @Override
    public void onCarEdit(CarEntity carEntity)
    {
        sendCarToServer("edit", carEntity);
        mapTableView();
        adapter.notifyDataSetChanged();
    }

    /**
     * Listener called when requesting editing car with photo
     * @param carEntity carEntity to be edited
     * @param string photo as String created in CarImageFinder class
     */
    @Override
    public void onCarEdit(CarEntity carEntity, String string)
    {
        sendCarWithPhotoToServer("photoEdit", carEntity, string);
        mapTableView();
        adapter.notifyDataSetChanged();
    }


    /**
     * Listener called when requesting to mark car as default
     * @param carEntity carEntity to be marked as default
     */
    @Override
    public void onMarkedAsDefault(CarEntity carEntity)
    {
        sendCarToServer("edit", carEntity);
    }

    /**
     * Listener that starts CarChangeDialog
     * @param view default parameter, required for onClick methods
     */
    public void addCar(View view)
    {
        CarChangeDialog carChangeDialog = new CarChangeDialog();
        carChangeDialog.show(getSupportFragmentManager(), "carAdd");
    }


    /** Method that gets updated car list from server, and maps it to the ListView
     *
     */
    private void mapTableView()
    {
        JSONArray jsonArray = communication.getCars();
        list.clear();

        for (int i = 0; i< jsonArray.length(); i++)
            {
                try
                    {
                        CarEntity dummy = convertJSONToCar(jsonArray.getJSONObject(i));

                        list.add(dummy);

                    }
                catch (JSONException e)
                    {
                        Log.d("Exception", "ERRR");
                    }
            }
        markDefaultCar();
    }

    /**
     * Method that finds the default car position in ListView and sets its background yellow
     */
    private void markDefaultCar()
    {
        CarEntity dummy;


        for (int d = 0; d<listView.getCount(); d++)
            {
                try
                    {
                        dummy = (CarEntity) listView.getItemAtPosition(d);
                    }
                 catch (IndexOutOfBoundsException e)
                     {
                         break;
                     }

                if (dummy.getIsDefault().equals(true))
                    {
                        listView.getAdapter().getView(d, null, listView).setBackgroundColor(Color.YELLOW);
                        break;
                    }
            }
    }

    /**
     * Method sending car without photo as JSONObject requesting action depending on mode
     * @param mode add - adding new car, edit - overwriting existing car
     * @param carEntity carEntity object
     */
    private void sendCarToServer(String mode, CarEntity carEntity)
    {
                try
                    {
                        JSONObject car = convertCarToJSON(carEntity);
                        communication.sendCarChanges((mode + "Car"), car);
                    } catch (JSONException | XmppStringprepException e) {
                    e.printStackTrace();
                }
                while(true) {if (communication.isFoundCars()) break;}
                communication.setFoundCars(false);

                mapTableView();
                adapter.notifyDataSetChanged();
    }

    /**
     * Method sending car with photo as JSONObject requesting action depending on mode
     * @param mode add - adding new car, edit - overwriting existing car
     * @param carEntity carEntity object
     * @param photo photo as String created in CarImageFinder class
     */
    private void sendCarWithPhotoToServer(String mode, CarEntity carEntity, String photo)
    {
        try
            {
                JSONObject car = convertCarToJSON(carEntity);
                communication.sendCarChangesWithPhoto((mode + "Car"), car, photo);
            } catch (JSONException | XmppStringprepException e) {
            e.printStackTrace();
        }
        while(true) {if (communication.isFoundCars()) break;}
        communication.setFoundCars(false);

        mapTableView();
        adapter.notifyDataSetChanged();
    }

    /**
     * Method requesting photo as String from server based on car photo id
     * @param d carEntity object
     * @return photo as String if exists, null if not
     */
    private String returnCarString(CarEntity d)
    {
        if (d.getPhotoId() == null) {
            return null;
        }
        else {
            communication.carPhotoRequest(d.getPhotoId(), d.getId());

            while (true){if (communication.isCarPhotoReceived()) break;}

            communication.setCarPhotoReceived(false);

            String carString = null;
            try
            {
                carString = communication.getCarPhotoJSON().getString("carPhoto");
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }

            return carString;
        }
    }


    @Override
    public void onTimeout() {/*No additional action needs to be taken*/}
}
