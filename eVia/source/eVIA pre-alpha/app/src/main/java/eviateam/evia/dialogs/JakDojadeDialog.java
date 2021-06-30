package eviateam.evia.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import org.json.JSONException;

import java.util.Objects;

import eviateam.evia.R;

/**
 * Dialog used to propose opening JakDojade functionality
 */
public class JakDojadeDialog extends AppCompatDialogFragment
{
    private JakDojadeListener listener;

    /**
     * Sets contents for dialog
     * @param savedInstanceState
     * @return newly constructed Dialog
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));

        try
        {
            builder//.setView(view)
                    .setTitle(R.string.Cannot_find_driver)
                    .setMessage(R.string.public_transport)
                    .setPositiveButton("yes", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i)
                        {
                            try
                            {
                                listener.onJakDojadeAgreed();
                                dismiss();
                            } catch (JSONException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    })
                    .setNegativeButton("no", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            listener.onJakDojadeDeclined();

                        }
                    });


        } catch (Exception e)
        {
            Log.d("Exception", "Exception");
        }

        return builder.create();
    }

    public interface JakDojadeListener
    {
        void onJakDojadeAgreed() throws JSONException;
        void onJakDojadeDeclined();
    }

    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);

        try
        {
            listener = (JakDojadeListener) context;
        } catch (ClassCastException e)
        {
            throw new ClassCastException(context.toString() +
                    "must implement ExampleDialogListener");
        }
    }
}
