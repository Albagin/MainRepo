package eviateam.evia.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import java.util.Objects;

import eviateam.evia.R;

/**
 * Dialog allowing to rate partner after course end
 */
public class RateDialog extends AppCompatDialogFragment
{
    /**
     * Value for rate. 5 by default
     */
    private int rate = 5;

    private RateDialogListener listener;


    /**
     * Sets contents for dialog
     * @param savedInstanceState
     * @return newly created dialog
     */
        @SuppressLint("InflateParams")
        @Override
        @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));

        this.setCancelable(false);

        final LayoutInflater inflater = requireActivity().getLayoutInflater();

        final View view = inflater.inflate(R.layout.rate_layout, null);


        builder.setTitle(R.string.partner_rate)
                .setView(view)
                .setPositiveButton("Rate",  new DialogInterface.OnClickListener()
    {
        @Override
        public void onClick(DialogInterface dialog, int which)
        {
            RatingBar seekBar = view.findViewById(R.id.rate_bar);

            try
                {
                    rate = (int)(seekBar.getRating());
                    System.out.println("OCENA = " + rate);
                }
                catch (NullPointerException e)
                    {
                        Log.d("Exception", "SeekBar is null, returning rate=5");
                    }



            listener.onRateClick(rate);
        }
    });



        return builder.create();
    }

    public interface RateDialogListener
    {
        void onRateClick(int rate);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (RateDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement ExampleDialogListener");
        }
    }
}
