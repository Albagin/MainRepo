package eviateam.evia.utility;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.github.dhaval2404.imagepicker.ImagePicker;
import java.io.File;

/**
 * Class supporting image loading from phone data
 */
public class CarImageFinder extends AppCompatActivity
{

    /**
     * Sets configuration for ImagePicker class
     * @param savedInstances
     */
   @Override
    public void onCreate(Bundle savedInstances)
   {
       super.onCreate(savedInstances);

       ImagePicker.Companion.with(this)
               .crop()	    			//Crop image(Optional), Check Customization for more option
               .compress(1024)			//Final image size will be less than 1 MB(Optional)
               .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
               .galleryOnly()
               .start();
   }

    /**
     * Called when ImagePicker has been used. Transfers acquired Uri object to File objects and sends it back to CarsActivity
     * @param requestCode code from CarsActivty requesting callback
     * @param resultCode code send to CarsActivity as callback
     * @param data Intent to send data back to
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK)
        {

            Uri fileUri = data.getData();
            File file = ImagePicker.Companion.getFile(data);

            Toast.makeText(this, "We did it!", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent();
            intent.putExtra("uri", fileUri);
            intent.putExtra("file", file);
            setResult(RESULT_OK, intent);
            finish();
        }
        else if (resultCode == ImagePicker.RESULT_ERROR)
        {
            Toast.makeText(this, ImagePicker.Companion.getError(data), Toast.LENGTH_SHORT).show();
            Log.d("HEY LISTEN", "ImagePicker has failed us");
            finish();
        }
        else
        {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
            Log.d("HEY LISTEN", "ImagePicker has been cancelled!");
            finish();
        }
    }
}
