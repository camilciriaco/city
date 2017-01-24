package app.appurservice.sigin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import app.appurservice.ActivityMain;
import app.appurservice.R;
import app.appurservice.data.DatabaseHandler;

/**
 * Created by RVN-TECH on 8/1/2016.
 */
public class Register extends Activity {
    Button btRegister;
    TextView btCancel;
    EditText etUsername, etPwd, etConPwd, etMobileNo, etEmail;
    DatabaseHandler db;
    final Context con = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btRegister = (Button) findViewById(R.id.registerSubmitBtn);
        btCancel = (TextView) findViewById(R.id.registerCancelButton);
        etUsername = (EditText) findViewById(R.id.registrationUserName);
        etPwd = (EditText) findViewById(R.id.registrationPassword);
        etEmail = (EditText) findViewById(R.id.registrationEmail);
        etMobileNo = (EditText) findViewById(R.id.registrationPhnNumber);
        etConPwd = (EditText) findViewById(R.id.registrationConfirmPassword);

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent openRegister_intent = new Intent(Register.this, Login.class);
                startActivity(openRegister_intent);
            }
        });

        btRegister.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                String edusrneym = etUsername.getText().toString();

                String edemail = etEmail.getText().toString();
                String edmobile = etMobileNo.getText().toString();
                String edpass = etPwd.getText().toString();
                String edConf = etConPwd.getText().toString();

                if (edusrneym.equals("") || edemail.equals("") || edmobile.equals("") || edpass.equals("")) {
                    Toast.makeText(getApplicationContext(), "Please fill in the blank fields", Toast.LENGTH_LONG).show();
                    return;
                }

                if (edConf.equals(edpass)) {

                    db = new DatabaseHandler(con);
                    RegisterData reg = new RegisterData();

                    reg.setUsername(edusrneym);
                    reg.setEmailId(edemail);
                    reg.setMobNo(edmobile);
                    reg.setPassword(edpass);

                    db.addregister(reg);

                    Toast.makeText(getApplicationContext(), "Registered", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getApplicationContext(), Login.class));

                } else {

                    Toast.makeText(getApplicationContext(), "Password doesn't match", Toast.LENGTH_LONG).show();
                    etPwd.setText("");
                    etConPwd.setText("");
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

           Intent i = new Intent(this, Login.class);
            startActivity(i);

            return false;
        } else if (keyCode == KeyEvent.KEYCODE_MENU) {
            Toast.makeText(getApplicationContext(), " ", Toast.LENGTH_LONG).show();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}


