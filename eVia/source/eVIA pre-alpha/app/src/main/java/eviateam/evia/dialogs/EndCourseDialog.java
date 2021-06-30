package eviateam.evia.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
 * Dialog allowing to accept or refuse current course ending
 */
public class EndCourseDialog extends AppCompatDialogFragment
{
    private EndCourseListener listener;

    /**
     * Variable informing if timer has been scheduled
     */
    private boolean isTimerOn = false;

    private Timer timer = new Timer();

    /**
     * Timertask supporting onEndCourseAgreed method method call
     */
    private TimerTask timerTask = new TimerTask()
    {
        @Override
        public void run()
        {
            listener.onEndCourseAgreed();
            dismiss();
        }
    };

    /**
     * Sets contents for dialog, starts 15 second timer, that will close dialog automatically to avoid hang-ups
     * @param savedInstanceState
     * @return newly constructed Dialog
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));

        this.setCancelable(false);


        try {
            builder
                    .setTitle(R.string.end_course_title)
                    .setMessage(R.string.end_course_msg)
                    .setPositiveButton(R.string.agree, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (isTimerOn)
                                {
                                    isTimerOn = false;
                                    timer.cancel();
                                }
                            listener.onEndCourseAgreed();
                        }
                    })
                    .setNegativeButton(R.string.decline, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (isTimerOn)
                                {
                                    isTimerOn = false;
                                    timer.cancel();
                                }
                            listener.onEndCourseDecline();
                        }
                    });


        } catch (Exception e){
            Log.d("Exception", "Exception in onCreateDialog");
        }


        timer.schedule(timerTask, (long)15 * 1000);
        isTimerOn = true;

        return builder.create();
    }

    public interface EndCourseListener {
        void onEndCourseAgreed();
        void onEndCourseDecline();
    }

    /**
     * this method will be called the moment it will get a parent (Activity)
     * @param context context of parent activity
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (EndCourseListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement ExampleDialogListener");
        }
    }
}
