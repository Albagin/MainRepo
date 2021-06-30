package eviateam.evia.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.thebluealliance.spectrum.SpectrumPalette;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

import eviateam.evia.R;
import eviateam.evia.entities.CarEntity;
import eviateam.evia.utility.CarImageFinder;

import static android.app.Activity.RESULT_OK;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Class supporting adding and editing carEntity objects
 */
public class CarChangeDialog extends AppCompatDialogFragment implements WaitDialog.WaitDialogListener
{
    private TextView lNr;
    private Button lColor;
    private Spinner lBrand;
    private ImageView lPicture;
    private LinearLayout colorLayout;

    private int color = 0;

    /**
     * Variable containing car photo as string
     */
    @Nullable
    private String carString;
    /**
     * Current car object
     */
    private CarEntity carEntity;

    /**
     * Variable informing if car is being added or edited
     */
    private boolean edit = false;
    private boolean isPhotoLoaded = false;
    private boolean isPhotoRemoved = false;

    /**
     * Variable containing car photo as file, initialized in CarImageFinder class
     */
    @Nullable
    private File carFile = null;

    private static final int CAR_IMAGE_REQUEST_CODE = 0;

    private CarChangeDialogListener listener;

    /**
     * Default constructor
     */
    public CarChangeDialog(){}

    /**
     * Constructor called when editing car
     * @param carEntity carEntity object to be edited
     * @param carString carEntity id used in XMPP
     */
    public CarChangeDialog(CarEntity carEntity, @Nullable String carString)
    {
        this.carEntity = carEntity;
        this.carString = carString;
        this.edit = true;
    }

    /**
     * Method constructing dialog. Initializes all listeners and XML objects
     * @param savedInstanceState fsfsdf
     * @return constructed Dialog
     */
    @SuppressLint("InflateParams")
    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));


        this.setCancelable(false);

        final LayoutInflater inflater = requireActivity().getLayoutInflater();

        final View view = inflater.inflate(R.layout.car_change_dialog, null);


        SpectrumPalette palette = view.findViewById(R.id.input_kolor);
        lNr = view.findViewById(R.id.input_nr);
        lBrand = view.findViewById(R.id.input_spinner);
        lPicture = view.findViewById(R.id.car_change_dialog_car_image);
        lColor = view.findViewById(R.id.input_kolor_button);
        colorLayout = view.findViewById(R.id.input_kolor_layout);

        ImageView lAdd = view.findViewById(R.id.car_change_dialog_add_car);
        ImageView lClear = view.findViewById(R.id.car_change_dialog_delete_car);
        Button save = view.findViewById(R.id.add_car_button);
        Button cancel = view.findViewById(R.id.cancel_car_button);

        if (edit)
            {
                setDialogToEdit();
            }

        builder.setView(view);
                                                                            // 4 pola z lColor do zmiany

        lColor.setOnClickListener(new View.OnClickListener()
        {
            /**
             * This method will check if fields are not empty and run listener to send data to server
             * @param v view with button
             */
            @Override
            public void onClick(View v)
            {
                colorLayout.setVisibility(View.VISIBLE);
            }
        });

        palette.setOnColorSelectedListener(new SpectrumPalette.OnColorSelectedListener() {
            @Override
            public void onColorSelected(int clr) {
                color = clr;
                lColor.setBackgroundColor(color);
                colorLayout.setVisibility(View.GONE);
            }
        });

        //Domyślny kolor
        //palette.setSelectedColor(getResources().getColor(R.color.white));
       // color = getResources().getColor(R.color.white);

        save.setOnClickListener(new View.OnClickListener()
        {
            /**
             * This method will check if fields are not empty and run listener to send data to server
             * @param v view with button
             */
            @Override
            public void onClick(View v)
            {
                WaitDialog waitDialog = new WaitDialog(15);
                waitDialog.show(getChildFragmentManager(), "wait");
                if (colorTest() && nrTest())
                    {
                        CarEntity d;

                        //Edytujemy istniejący samochód
                        if (edit)
                        {
                                d = new CarEntity(carEntity.getId(), lNr.getText().toString(), lBrand.getSelectedItem().toString(), String.valueOf(color), carEntity.getUserId(), carEntity.getIsDefault(), carEntity.getPhotoId());


                                String nr = lNr.getText().toString().replaceAll(" ", "");
                                nr = nr.toUpperCase();
                                d.setRegistration(nr);
                                lNr.setText(nr);
                                Log.d("Info", "Started editing car sequence");

                                if(nr.length() > 7 || nr.length() < 4) {
                                    Toast toast = Toast.makeText(getApplicationContext(), "zły nr rejestracyjny", Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                                else if (!isPhotoLoaded && !isPhotoRemoved) //nie naciśnięto dodania auta
                                    {
                                        Log.d("Info", "Photo was not loaded Sending carEdit request");
                                        listener.onCarEdit(d);
                                        dismiss();
                                    }
                                //naciśnięto przycisk usunięcia zdjęcia
                                else if (!isPhotoLoaded)
                                    {
                                        Log.d("Info", "Photo has been removed by user. Sending carEdit request with photoId as 0");
                                        d.setPhotoId(0);
                                        listener.onCarEdit(d);
                                        dismiss();

                                    }
                                //naciśnięto przycisk edycji (i udało się załadować zdjęcie)
                                else
                                    {
                                        //zdjęcie zostało załadowane i nie jest nullem
                                        if (carFile != null)
                                            {
                                                String dummy = fileToString(carFile);
                                                Log.d("Info: String", "" + dummy);

                                                Log.d("Info", "Photo is loaded successfully. Sending photoCarEdit request");
                                                listener.onCarEdit(d, dummy);
                                                dismiss();
                                            }
                                        else
                                            {
                                                Log.d("Error", "Tried to load photo, but carFile null. Cancelling editing car");
                                                dismiss();
                                            }
                                    }
                            }
                            //Wstawamy nowy samochód
                              else
                                 {
                                    d = new CarEntity(lNr.getText().toString(), lBrand.getSelectedItem().toString(),String.valueOf(color));

                                     String nr = lNr.getText().toString().replaceAll(" ", "");
                                     nr = nr.toUpperCase();
                                     lNr.setText(nr);

                                    Log.d("Info", "Started adding car sequence");

                                     if(nr.length() > 7 || nr.length() < 4) {
                                         Toast toast = Toast.makeText(getApplicationContext(), "zły nr rejestracyjny", Toast.LENGTH_SHORT);
                                         toast.show();
                                     }
                                    else if (!isPhotoLoaded)
                                        {
                                            Log.d("Info", "Photo was not loaded. Sending carAdd request");
                                            listener.onCarAdd(d);
                                         dismiss();
                                        }
                                     else
                                        {
                                            if (carFile != null)
                                                {
                                                    String dummy = fileToString(carFile);
                                                    Log.d("Info", "String loaded from carFile: " + dummy);

                                                    Log.d("Info", "Photo was successfully loaded. Sending photoCarAdd request");
                                                    listener.onCarAdd(d, dummy);
                                                    dismiss();
                                                }
                                            else
                                                {
                                                    Log.d("Error", "Photo was loaded, but carFile is null. Cancelling adding car");
                                                    dismiss();
                                                }

                                         }
                                }
                        }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dismiss();
            }
        });

        lAdd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getContext(), CarImageFinder.class);
                startActivityForResult(intent, CAR_IMAGE_REQUEST_CODE);

            }
        });

        lClear.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                isPhotoLoaded = false;
                isPhotoRemoved = true;
                lPicture.setImageBitmap(null);
            }
        });

        return builder.create();
    }

    /** Method to get results from ImagePicker class with Image Uri and File. Handle with care
     *
     * @param requestCode code send to CarImageView
     * @param resultCode code received from CarImageView
     * @param data data (Uri, File) in Intent received from CarImageFinder class
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAR_IMAGE_REQUEST_CODE)
        {
            if (resultCode == RESULT_OK && data != null)
            {
                Uri carUri =  data.getParcelableExtra("uri");
                carFile = (File) data.getSerializableExtra("file");
                if (carFile != null) Log.d("Info", "File is not null");
                lPicture.setImageURI(carUri);
                isPhotoLoaded = true;
            }
            else Log.d("Exception", "Uri has fallen along the way");
        }
    }

    /**
     * Method testing if color has been selected
     * @return true if color selected, false if not
     */
    private boolean colorTest()
    {
            return (color != 0);
    }

    /**
     * Method testing if phone number has been inserted
     * @return true if number inserted, false if not
     */
    private boolean nrTest()
    {
        return !lNr.getText().toString().isEmpty();
    }

    /**
     * Method that sets data from existing car for editing
     */
    private void setDialogToEdit()
    {
        String value = carEntity.getBrand();

        int index = getIndex(lBrand, value);


        lBrand.setSelection(index);
        lNr.setText(carEntity.getRegistration());
        lColor.setBackgroundColor(Integer.parseInt(carEntity.getColor()));  //kolor guzika
        color = Integer.parseInt(carEntity.getColor()); //kolor z palety kolorów

        byte[] byteArray = null;

        if (carString != null)
            {
                byteArray = Base64.decode(carString, Base64.NO_WRAP);

                Log.d("Info: byteArray", Arrays.toString(byteArray));
            }

        if (byteArray != null)
            {
                Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                lPicture.setImageBitmap(bitmap);
            }
    }

    /**
     * Method used to find specific car brand from spinner and return his index
     * @param spinner  spinner object from activity
     * @param myString  brand we are searching for
     * @return index of spinner with required string
     */
    private int getIndex(Spinner spinner, String myString)
    {
        for (int i=0;i<spinner.getCount();i++)
        {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString))
            {
                return i;
            }
        }
        return 0;
    }

    @Override
    public void onTimeout() {/*No additional action needs to be taken*/}

    public interface CarChangeDialogListener
    {
//        void onCarChange(CarEntity d, boolean c, boolean b);
//        void onCarChange(CarEntity d, boolean c, boolean b, String string);

        void onCarAdd(CarEntity carEntity);
        void onCarAdd(CarEntity carEntity, String string);
        void onCarEdit(CarEntity carEntity);
        void onCarEdit(CarEntity carEntity, String string);
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (CarChangeDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement ExampleDialogListener");
        }
    }

    /**
     * This method converts File variable acquired from CarImageFinder class to String
     * @param file File class variable containing Image
     * @return byteArray encoded to String containing Image
     */
    private static String fileToString(File file)
    {
        int size;

        try
            {
                 size = (int) file.length();
            }
            catch (NullPointerException e)
                {
                    return null;
                }

        byte[] data = new byte[size];

        try
            {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            int err = buf.read(data, 0, data.length);
            buf.close();

            Log.d("Info", "Read " + err + " bytes into string");
        }
        catch (IOException e)
        {
            Log.d("Exception", "IOException during encoding");
        }

        return Base64.encodeToString(data, Base64.NO_WRAP);
    }
}
