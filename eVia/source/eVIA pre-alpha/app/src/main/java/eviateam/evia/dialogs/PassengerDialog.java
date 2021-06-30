package eviateam.evia.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.widget.ProfilePictureView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import eviateam.evia.R;

/**
 * Dialog allowing passenger to accept or refuse driver proposition
 */
public class PassengerDialog extends Dialog
{
    private JSONObject jsonObject;
    private PassengerDialogListener listener;
    private Context context;

    private boolean isTimerOn = false;

    private Timer timer = new Timer();
    private TimerTask timerTask = new TimerTask()
    {
        @Override
        public void run()
        {
            listener.onOutOfTime();
            dismiss();
        }
    };

    public PassengerDialog(Context context, JSONObject jsonObject)
    {
        super(context);
        this.context = context;
        this.jsonObject = jsonObject;

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.passenger_dialog);

        this.setCancelable(false);

        try
        {
            listener = (PassengerDialogListener) context;
        } catch (ClassCastException e)
        {
            throw new ClassCastException(context.toString() +
                    "must implement ExampleDialogListener");
        }

        Button yes = findViewById(R.id.passenger_dialog_yes);
        Button no = findViewById(R.id.passenger_dialog_no);
        TextView message = findViewById(R.id.passenger_dialog_message);
        TextView title = findViewById(R.id.passenger_dialog_title);
        LinearLayout linearColor = findViewById(R.id.car_color_passenger_dialog);


        RatingBar rate = findViewById(R.id.driver_passenger_dialog_rating);



        try
            {
                float rating = (float)jsonObject.getDouble("rate");


                if (rating == 0)
                    {
                        //rating.setText("Rating: none");
                        rate.setRating(0);
                    }
                else
                    {
                       //rating.setText("Rating: " + rate);
                        rate.setRating(rating);
                    }

            }
        catch (JSONException e)
            {
                e.printStackTrace();
            }


        title.setText(R.string.you_have_found_a_driver);
        title.setTextColor(Color.RED);

        byte[] byteArray = null;

        try
        {
            String string = jsonObject.getString("carPhoto");

            byteArray = Base64.decode(string, Base64.NO_WRAP);
        }
    catch (JSONException e)
        {
            e.printStackTrace();
        }

        Log.d("Info", ""+Arrays.toString(byteArray));

        try
        {
            message.setText("Do you wish to go with " + jsonObject.getString("name") + "?" +
                    "\nCar: " + jsonObject.getString("brand") + " " + jsonObject.getString("registration"));
            linearColor.getBackground().setColorFilter(Integer.parseInt(jsonObject.getString("color")), PorterDuff.Mode.SRC_IN);
        } catch (JSONException e)
        {
            e.printStackTrace();
        }

        ProfilePictureView picture = findViewById(R.id.passenger_dialog_profile_picture);
        ImageView car = findViewById(R.id.passengerDialog_car_image);

        try
        {
            picture.setProfileId(jsonObject.getString("fbId"));
        } catch (JSONException e)
        {
            e.printStackTrace();
        }

        yes.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    if (isTimerOn)
                        {
                            isTimerOn = false;
                            timer.cancel();
                        }
                    listener.onPassengerAgreed(jsonObject);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }

                dismiss();
            }
        });

        no.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (isTimerOn)
                    {
                        isTimerOn = false;
                        timer.cancel();
                    }
                listener.onPassengerDeclined();
                dismiss();
            }
        });


        if (byteArray != null)
            {
                Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                car.setImageBitmap(bitmap);
            }

        timer.schedule(timerTask, (long)20 * 1000);
        isTimerOn = true;
    }

    public interface PassengerDialogListener
    {
        void onPassengerAgreed(JSONObject jsonObject) throws JSONException;
        void onPassengerDeclined();
        void onOutOfTime();
    }
}
