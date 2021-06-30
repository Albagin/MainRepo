package eviateam.evia.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.login.widget.ProfilePictureView;

import eviateam.evia.R;

/**
 * Class supporting user profile
 */
public class ProfileActivity extends AppCompatActivity
{


    /**
     * Set contents for activity and calls mapUserData
     * @param savedInstanceState
     */
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);
        mapUserData();
    }

    /**
     * Method that fills XML objects with user data from bundle acquired
     */
    private void mapUserData()
    {
        Bundle b = getIntent().getExtras();

        TextView idTextView = findViewById(R.id.idTextView);
        TextView fnameTextView = findViewById(R.id.fnameTextView);
        TextView lnameTextView = findViewById(R.id.lnameTextView);
        TextView emailTextView = findViewById(R.id.emailTextView);
        ProfilePictureView profilePictureView = findViewById(R.id.user_profile_picture);


        assert b != null;
        idTextView.setText(b.getString("id"));
        fnameTextView.setText(b.getString("fname"));
        lnameTextView.setText(b.getString("lname"));
        emailTextView.setText(b.getString("email"));

        profilePictureView.setProfileId(b.getString("id"));

    }
}
