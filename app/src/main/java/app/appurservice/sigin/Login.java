package app.appurservice.sigin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.Timer;
import java.util.TimerTask;

import app.appurservice.ActivityMain;
import app.appurservice.R;
import app.appurservice.data.DatabaseHandler;

/**
 * Created by RVN-TECH on 7/27/2016.
 */
public class Login extends AppCompatActivity {

    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;

    //Facebook login button
    private FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            Profile profile = Profile.getCurrentProfile();
            nextActivity(profile);
        }
        @Override
        public void onCancel() {        }

        @Override
        public void onError(FacebookException e) {      }
        };

    Button btlogin;
    TextView btregister;
    EditText EtLoginUsername, EtLoginPwd;
    DatabaseHandler db;
    final Context con = this;
    private ImageView user_image;
    LoginButton loginButton;

    UserSessionManager session;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
        // Initialize SDK before setContentView(Layout ID)
        FacebookSdk.sdkInitialize(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        btregister = (TextView) findViewById(R.id.loginRegisterBtn);
        btlogin = (Button) findViewById(R.id.loginSubmitBtn);
        EtLoginUsername = (EditText) findViewById(R.id.loginEmailEt);
        EtLoginPwd = (EditText) findViewById(R.id.loginPassEt);
        loginButton = (LoginButton) findViewById(R.id.login_button);
        user_image = (ImageView) findViewById(R.id.user_image);

            session = new UserSessionManager(getApplicationContext());
          //  Toast.makeText(getApplicationContext(),
          //           "User Login Status: " + session.isUserLoggedIn(),
          //          Toast.LENGTH_LONG).show();

            btlogin.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    // get The User name and Password
                    db = new DatabaseHandler(con);
                    String username = EtLoginUsername.getText().toString();
                    String password = EtLoginPwd.getText().toString();

                    // fetch the Password form database for respective user name
                    String StoredPassword = db.getregister(username);

                    // check if the Stored password matches with Password entered by user
                    if (password.equals(StoredPassword)) {

                        session.createUserLoginSession(username, password);

                        Toast.makeText(Login.this, "Congrats: Login Successfull", Toast.LENGTH_LONG).show();
                        Intent loginuser = new Intent(Login.this, ActivityMain.class);
                        loginuser.putExtra("username", username);
                        loginuser.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        loginuser.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(loginuser);
                    } else {
                        Toast.makeText(Login.this, "User Name or Password does not match", Toast.LENGTH_LONG).show();
                    }
                }
            });

                btregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    //Start Activity To Open CameraPage
                    Intent openRegister_intent = new Intent(Login.this, Register.class);
                    startActivity(openRegister_intent);
                    finish();
                    }
                    });

        callbackManager = CallbackManager.Factory.create();
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {    }
            };

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                nextActivity(newProfile);   }
                };
        accessTokenTracker.startTracking();
        profileTracker.startTracking();

        LoginButton loginButton = (LoginButton)findViewById(R.id.login_button);
        callback = new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken accessToken = loginResult.getAccessToken();
                //accessToken.getCurrentAccessToken();
                Profile profile = Profile.getCurrentProfile();
                nextActivity(profile);
                Toast.makeText(getApplicationContext(), "Logging in...", Toast.LENGTH_SHORT).show();    }

            @Override
            public void onCancel() {    }

            @Override
            public void onError(FacebookException e) {      }
                };
            loginButton.setReadPermissions("public_profile", "email","user_friends");
            loginButton.registerCallback(callbackManager, callback);

        }

            @Override
            public boolean onCreateOptionsMenu(Menu menu) {
                // Inflate the menu; this adds items to the action bar if it is present.
                getMenuInflater().inflate(R.menu.menu_login, menu);
            return true;
                }

            @Override
            public boolean onOptionsItemSelected(MenuItem item) {
                // Handle action bar item clicks here. The action bar will
                // automatically handle clicks on the Home/Up button, so long
                // as you specify a parent activity in AndroidManifest.xml.
                int id = item.getItemId();

                //noinspection SimplifiableIfStatement
                if (id == R.id.action_settings) {
                    return true;
                }

                return super.onOptionsItemSelected(item);
            }

            @Override
            protected void onResume() {
            super.onResume();
                //Facebook login
                Profile profile = Profile.getCurrentProfile();
                nextActivity(profile);

            }

            @Override
            protected void onPause() {

                super.onPause();
            }

            protected void onStop() {
            super.onStop();
                //Facebook login
                accessTokenTracker.stopTracking();
                profileTracker.stopTracking();
            }

            @Override
            protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
            super.onActivityResult(requestCode, responseCode, intent);
                //Facebook login
                callbackManager.onActivityResult(requestCode, responseCode, intent);
            }

            private void nextActivity(Profile profile){
                if(profile != null){

                    Intent main = new Intent(Login.this, ActivityMain.class);
                    main.putExtra("name", profile.getFirstName());
                    main.putExtra("surname", profile.getLastName());
                    main.putExtra("middlename", profile.getMiddleName());
                    main.putExtra("imageUrl", profile.getProfilePictureUri(150, 150).toString());
                    startActivity(main);

//                    Intent loginuser = new Intent(this, FormDetails.class);
//                    loginuser.putExtra("firstname", profile.getFirstName());
//                    loginuser.putExtra("lastname", profile.getLastName());
//                    loginuser.putExtra("middlename", profile.getMiddleName());
//                    startActivity(loginuser);
                    }
            }

}