package eviateam.evia;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;

import eviateam.evia.dialogs.WaitDialog;
import eviateam.evia.googlemaps.MapDriver;
import eviateam.evia.googlemaps.MapPassenger;
import eviateam.evia.utility.Communication;

/**
 * Main class , used to start application and run facebook log in procedure
 */
public class Main extends AppCompatActivity implements WaitDialog.WaitDialogListener
{
    private Communication communication = new Communication();
    private CallbackManager callbackManager;
    private Snackbar snackbar;


    String mId;
    String mFirstName;
    String mLastName;
    String mEmail;

    //driver - false, passenger - true
    boolean mRole = false;

    /**
     * Method called on creation. Sets contents for activity, and starts facebook log in procedure
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        callbackManager = CallbackManager.Factory.create();

        AppEventsLogger.activateApp(getApplication());

        setContentView(R.layout.activity_main);
//        Unless you need new PC to be able to build login-working app, leave these commented
//        System.out.println("Generating hashcode");
//        HashGen key = new HashGen(getPackageManager());
//        key.keyGenerate();

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

        if (isLoggedIn)
        {
            try
            {
                if (readDataFromFile(getApplicationContext()))
                {
                    readUserRole(getApplicationContext());
                    toLogged();
                }
                else
                {
                    LoginManager.getInstance().logOut();
                    facebookLogIn();
                }


            } catch (IOException | JSONException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            LoginManager.getInstance().logOut();
            facebookLogIn();
        }


    }

    /**
     * Method that must be here to handle results of a login
     * @param requestCode code send to facebook API
     * @param resultCode code received from facebook API
     * @param data data received from facebook API
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * Method that logs into the user's account with basic permissions
     * and gathers id, name and surname as JSON object
     */
    private void facebookLogIn()
    {
        LoginButton loginButton = findViewById(R.id.login_button);

        loginButton.setPermissions(Arrays.asList("public_profile", "email"));


        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>()
                {
                    @Override
                    public void onSuccess(LoginResult loginResult)
                    {


                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback()
                                {
                                    @Override
                                    public void onCompleted(JSONObject object, GraphResponse response) {

                                        try
                                        {
                                            JSONObject data = response.getJSONObject();

                                            mId = data.getString("id");
                                            mFirstName = data.getString("first_name");
                                            mLastName = data.getString("last_name");
                                            mEmail = data.getString("email");


                                        } catch (JSONException e)
                                        {
                                            e.printStackTrace();
                                        }

                                        try
                                        {
                                            putDataToFile(getApplicationContext());
                                            readUserRole(getApplicationContext());
                                        }
                                        catch (IOException | JSONException e)
                                        {
                                            e.printStackTrace();
                                        }

                                        toLogged();
                                    }
                                });

                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,first_name,last_name,email");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }
                    @Override
                    public void onCancel()
                    {
                        snackbar = Snackbar.make(findViewById(R.id.mainActivityView), R.string.canceled, Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                    @Override
                    public void onError(FacebookException exception)
                    {
                        snackbar = Snackbar.make(findViewById(R.id.mainActivityView), R.string.fail, Snackbar.LENGTH_LONG);
                        snackbar.show();
                        exception.getStackTrace();
                    }
                });

    }

    /**
     * Method called when facebook data has been successfully obtained. Creates bundle with user data and starts either
     * mapDriver or mapPassenger according to role saved in phone memory (passenger by default)
     */
    private void toLogged()
    {
        final WaitDialog waitDialog = new WaitDialog(15);
        waitDialog.show(getSupportFragmentManager(), "wait");

        new Thread(new Runnable() {
            @Override
            public void run() {
                final Bundle bundle = new Bundle();
                final Intent[] intent = new Intent[1];

                bundle.putString("id", mId);
                bundle.putString("fname", mFirstName);
                bundle.putString("lname", mLastName);
                bundle.putString("email", mEmail);
                bundle.putBoolean("role", mRole);


                AccessToken accessToken = AccessToken.getCurrentAccessToken();
                boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

                if (!isLoggedIn)
                {
                    LoginManager.getInstance().logInWithReadPermissions(getParent(), Arrays.asList("public_profile", "email"));
                }

                String xmpplogin = mEmail;
                xmpplogin = xmpplogin.replace("@","..");

                Communication.setUsername(xmpplogin);

                communication.serverConnect();

                while(true) {if (Communication.isConnected() != null) break;}
                Log.d("Info","connected");

                communication.loginToServer("pass", bundle);
                while(true) {if (communication.isLoggedIn()) break;}
                Log.d("Info","logged in");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mRole)
                        {
                            intent[0] = new Intent(getApplicationContext(), MapPassenger.class);
                        }
                        else
                        {
                            intent[0] = new Intent(getApplicationContext(), MapDriver.class);
                        }

                        intent[0].putExtras(bundle);
                        startActivity(intent[0]);
                        finish();
                    }
                });
            }
        }).start();

    }

    public void signIn(View v) {
        Toast.makeText(getApplicationContext(),"Not implemented yet", Toast.LENGTH_LONG).show();
    }

    public void logIn(View v) {
        Toast.makeText(getApplicationContext(),"Not implemented yet", Toast.LENGTH_LONG).show();
    }

    public void forgotPass(View v) {
        Toast.makeText(getApplicationContext(),"Not implemented yet", Toast.LENGTH_LONG).show();
    }

    /**
     * Method called during facebook log in method. If user data already exists in phone memory, it is obtained from
     * there instead of facebook to save time
     * @param context current context
     * @throws IOException
     * @throws JSONException
     */
    private void putDataToFile(Context context) throws IOException, JSONException
    {
        OutputStreamWriter writer = new OutputStreamWriter(context.openFileOutput("evia.txt", Context.MODE_PRIVATE));

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("id", mId);
        jsonObject.put("fname", mFirstName);
        jsonObject.put("lname", mLastName);
        jsonObject.put("email", mEmail);

        try
        {
            writer.write(jsonObject.toString());
            writer.close();
        }
        catch (IOException e)
        {
            Log.d("Exception", getString(R.string.log_exception01));
        }

    }

    /**
     * Method called during facebook log in method. Saves user data in phone memory, so that next time it can be read
     * directly from phone to save time
     * @param context current context
     * @return true if data read, false if any field is missing
     * @throws IOException
     * @throws JSONException
     */
    private boolean readDataFromFile(Context context) throws IOException, JSONException
    {
        try
        {
            InputStream stream = context.openFileInput("evia.txt");

            if (stream != null)
            {
                InputStreamReader inputStreamReader = new InputStreamReader(stream);
                BufferedReader reader = new BufferedReader(inputStreamReader);

                JSONObject jsonObject = new JSONObject(reader.readLine());
                reader.close();

                mId = jsonObject.getString("id");
                mFirstName = jsonObject.getString("fname");
                mLastName = jsonObject.getString("lname");
                mEmail = jsonObject.getString("email");
            }
        }
        catch (FileNotFoundException e)
        {
            Log.d("Exception", getString(R.string.log_exception02));
        }

        return mId != null && mFirstName != null && mLastName != null && mEmail != null;
    }

    /**
     * Method that reads phone memory for last used role. If none exists, writes passenger as default
     * @param context current context
     * @throws IOException
     */
    private void readUserRole(Context context) throws IOException
    {
        try
        {
            InputStream stream = context.openFileInput("evia_role.txt");

            int d = stream.read();

            mRole = d != 0;

            stream.close();


        }
        catch (FileNotFoundException e)
        {
            OutputStreamWriter writer = new OutputStreamWriter(context.openFileOutput("evia_role.txt", Context.MODE_PRIVATE));

            writer.write(1);
            writer.close();

            mRole = true;
        }
    }
    @Override
    public void onTimeout() {}
}
