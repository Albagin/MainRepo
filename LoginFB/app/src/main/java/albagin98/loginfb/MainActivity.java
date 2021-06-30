package albagin98.loginfb;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.material.snackbar.Snackbar;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity
{

    private CallbackManager callbackManager;
    private Snackbar snackbar;
    private static final String EMAIL = "albag98@wp.pl";
    private View view;// = findViewById(R.id.view);


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        callbackManager = CallbackManager.Factory.create();

        view = findViewById(R.id.view);

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>()
                {
                    @Override
                    public void onSuccess(LoginResult loginResult)
                    {
                        // App code
                        snackbar = Snackbar.make(view, "Success", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }

                    @Override
                    public void onCancel()
                    {
                        // App code
                        snackbar = Snackbar.make(view, "Canceled", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }

                    @Override
                    public void onError(FacebookException exception)
                    {
                        // App code
                        snackbar = Snackbar.make(view, "Fail", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        exception.getStackTrace();

                    }
                });

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

        if (isLoggedIn)
        {
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", EMAIL));
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }


}
