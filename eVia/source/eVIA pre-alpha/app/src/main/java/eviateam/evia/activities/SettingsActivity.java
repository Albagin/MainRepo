package eviateam.evia.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import eviateam.evia.R;

/**
 * Class supporting user settings
 */
public class SettingsActivity extends AppCompatActivity
{
    /**
     * Sets contents for activity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_settings);

        Toast toast = Toast.makeText(this, "Not implemented yet", Toast.LENGTH_LONG);
        toast.show();
    }
}
