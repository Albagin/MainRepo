package eviateam.evia.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import java.util.Objects;
import eviateam.evia.entities.CarEntity;
import eviateam.evia.R;

/**
 * Dialog listing options for existing car
 */
public class CarOptionsDialog extends AppCompatDialogFragment
{
    private CarOptionListener listener;
    private CarEntity carEntity;
    private String carString;

    public CarOptionsDialog(CarEntity carEntity, String string)
    {
        this.carEntity = carEntity;
        this.carString = string;
    }

    /**
     * Sets contents for dialog
     * @param savedInstanceState
     * @return newly constructed Dialog
     */
    @SuppressLint("InflateParams")
    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));

        final LayoutInflater inflater = requireActivity().getLayoutInflater();

        final View view = inflater.inflate(R.layout.car_options_dialog, null);


        ImageView remove = view.findViewById(R.id.remove_car_dialog_button);
        ImageView edit = view.findViewById(R.id.edit_car_dialog_button);
        ImageView mark = view.findViewById(R.id.mark_as_default_dialog_button);

        TextView name = view.findViewById(R.id.car_options_dialog_name);
        //TextView color = view.findViewById(R.id.car_options_dialog_color);
        TextView nr = view.findViewById(R.id.car_options_dialog_number);

        LinearLayout linearColor = view.findViewById(R.id.car_options_dialog_color);

        name.setText(carEntity.getBrand());
        //color.setText(carEntity.getColor());
        linearColor.getBackground().setColorFilter(Integer.parseInt(carEntity.getColor()), PorterDuff.Mode.SRC_IN);
        //linearColor.setBackgroundColor(Integer.parseInt(carEntity.getColor()));
        nr.setText(carEntity.getRegistration());

        final ImageView carImage = view.findViewById(R.id.car_options_car_image);

        builder.setView(view);

        byte[] byteArray = null;

        if (carString != null)
            {
                byteArray = Base64.decode(carString, Base64.NO_WRAP);

//                System.out.println(Arrays.toString(byteArray));
            }

        if (byteArray != null)
            {
                Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                carImage.setImageBitmap(bitmap);
            }

        remove.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                listener.onCarRemoveClicked(carEntity);
                dismiss();
            }
        });

        edit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                listener.onCarEditClicked(carEntity, carString);
                dismiss();
            }
        });

        mark.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                carEntity.setIsDefault(true);
                listener.onMarkedAsDefault(carEntity);
                dismiss();
            }
        });

        return builder.create();
    }

    public interface CarOptionListener
    {
        void onCarRemoveClicked(CarEntity d);
        void onCarEditClicked(CarEntity d, String c);
        void onMarkedAsDefault(CarEntity d);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (CarOptionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement ExampleDialogListener");
        }
    }

}
