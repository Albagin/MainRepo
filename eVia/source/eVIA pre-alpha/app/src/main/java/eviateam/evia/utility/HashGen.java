package eviateam.evia.utility;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static android.content.pm.PackageManager.*;

/**
 * Class used to generate hashcode of a computer run on. Only for developer's use.
 */
@SuppressLint("Registered")
public class HashGen extends Application
{

    PackageManager manager;

    @SuppressLint("Registered")
    public HashGen(PackageManager manager)
    {
        this.manager = manager;
    }

    /**
     * Method that will generate hashcode of the user's PC. Should only be used, if need arises
     * to have another computer be able to build facebook login-working app.
     */
    public void keyGenerate()
    {

        try {
            PackageInfo info = manager.getPackageInfo("eviateam.evia", GET_SIGNATURES);

            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        }
        catch (NameNotFoundException | NoSuchAlgorithmException e)
        {
            Log.d("Exception", "As above");
        }
    }
}
