package eviateam.evia.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.facebook.login.widget.ProfilePictureView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import eviateam.evia.R;

/**
 * Dialog allowing driver to accept or refuse passenger proposition
 */
public class DriverDialog extends Dialog
{
    /**
     * JSONObject containing driver data
     */
    private JSONObject jsonObject;
    private DriverDialogListener listener;
    /**
     * Current context
     */
    private Context context;

    /**
     * Variable informing if timer has been scheduled
     */
    private boolean isTimerOn = false;

    private Timer timer = new Timer();
    /**
     * Timertask supporting onOutOfTime method call
     */
    private TimerTask timerTask = new TimerTask()
    {
        @Override
        public void run()
        {
            listener.onOutOfTime();
            dismiss();
        }
    };

    public DriverDialog(Context context, JSONObject jsonObject)
    {
        super(context);

        this.context = context;
        this.jsonObject = jsonObject;

    }

    /**
     * Sets contents for dialog
     * @param savedInstanceState
     */
    @SuppressLint("SetTextI18n")
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_dialog);

        this.setCancelable(false);

        try
        {
            listener = (DriverDialogListener) context;
        } catch (ClassCastException e)
        {
            throw new ClassCastException(context.toString() +
                    "must implement ExampleDialogListener");
        }

        Button yes = findViewById(R.id.passenger_dialog_yes);
        Button no = findViewById(R.id.passenger_dialog_no);
        Button showOnMap = findViewById(R.id.show_on_map);
        TextView message = findViewById(R.id.passenger_dialog_message);
        TextView title = findViewById(R.id.passenger_dialog_title);

        RatingBar rate = findViewById(R.id.passenger_driver_dialog_rating);

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

        title.setText(R.string.Passenger_found);
        title.setTextColor(Color.RED);


        try
        {
            message.setText("Do you wish to go with " + jsonObject.getString("name") +  "?");
        } catch (JSONException e)
        {
            e.printStackTrace();
        }

        ProfilePictureView picture = findViewById(R.id.passenger_dialog_profile_picture);
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
                    listener.onDriverAgreed();
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
                listener.onDriverDeclined();
                if (isTimerOn)
                    {
                        isTimerOn = false;
                        timer.cancel();
                    }
                dismiss();
            }
        });

        showOnMap.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
            listener.showOnMap(jsonObject);
                if (isTimerOn)
                    {
                        isTimerOn = false;
                        timer.cancel();
                    }
            dismiss();
            }
        });

        timer.schedule(timerTask, (long)20 * 1000);
        isTimerOn = true;

    }

    public interface DriverDialogListener
    {
        void onDriverAgreed() throws JSONException;
        void onDriverDeclined();
        void showOnMap(JSONObject obj);
        void onOutOfTime();
    }
}
