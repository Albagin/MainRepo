package eviateam.evia.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import eviateam.evia.R;

/**
 * Dialog used to stop user input in specific activity or dialog, until all data has been initialized
 */
public class WaitDialog extends AppCompatDialogFragment
    {
        private WaitDialogListener listener;

        /**
         * Variable informing if timer has been scheduled
         */
        private boolean isTimerOn = false;

        private Timer timer = new Timer();
        private int time;

        /**
         * Default counstructor
         */
        public WaitDialog(){}

        /**
         * Constructor used to set time for dialog to exist
         * @param time time as value
         */
        //Tobe Gundam
        public WaitDialog(int time)
        {
            this.time = time;
        }


        /**
         * Sets contents for dialog, starts timer, if time set
         * @param savedInstanceState
         * @return newly constructed dialog
         */
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));

            this.setCancelable(false);

            try

                {
                    builder.setView(R.layout.wait_dialog);
//
            }catch (Exception e)
                {
                    Log.d("Exception", "Exception");
                }

            TimerTask task = new TimerTask()
            {
                @Override
                public void run()
                {
                    Log.d("TimerTask", "Activating timertask in waitdialog");
                    if (listener != null) listener.onTimeout();
                    dismiss();
                }
            };

            Log.d("MSG FROM Wait", "WaitDialog");

            timer.schedule(task, (time * 1000));
            isTimerOn = true;

            Log.d("MSG FROM Wait", "Timer scheduled");



            return builder.create();

        }

        public interface WaitDialogListener
        {
            void onTimeout();
        }

        /**
         * this method will be called the moment it will get a parent (Activity)
         * @param context context of parent activity
         */
        @Override
        public void onAttach(@NonNull Context context) {
            super.onAttach(context);

            try {
                listener = (WaitDialogListener) context;
            } catch (ClassCastException e) {
                throw new ClassCastException(context.toString() +
                        "must implement ExampleDialogListener");
            }
        }

        @Override
        public void onDestroy() {
            if (isTimerOn)
            {
                timer.cancel();
                timer = null;
            }
            super.onDestroy();
        }

    }
