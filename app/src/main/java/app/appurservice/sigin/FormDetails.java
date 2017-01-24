package app.appurservice.sigin;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import app.appurservice.ActivityMain;
import app.appurservice.ActivitySetting;
import app.appurservice.R;
import app.appurservice.data.DatabaseHandler;
import app.appurservice.utils.Tools;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Created by RVN-TECH on 8/3/2016.
 */
public class FormDetails extends Activity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    Spinner SMonths, SDays, Syears, ScvlStatus;
    Button FDbtsave, FDbtcancel;
    EditText etlname,etfname,etmname,etaddress, etMobileNo, etlandline, etEmail, etBirth;
    RadioButton rbMale, rbFemale;
    RadioGroup rgGen;
    DatabaseHandler db;
    final Context con = this;

    ImageButton ib;
    private Calendar cal;
    private int day;
    private int month;
    private int year;
    private DatePickerDialog birthDatePickerDialog;
    private SimpleDateFormat dateFormatter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form_layout);
       /* SMonths = (Spinner) findViewById(R.id.Months);
        SDays = (Spinner) findViewById(R.id.days);
        Syears = (Spinner) findViewById(R.id.years);*/
        rgGen = (RadioGroup) findViewById(R.id.rgGender);
        rbFemale = (RadioButton) findViewById(R.id.radioButton);
        rbMale = (RadioButton) findViewById(R.id.radioButton2);
        ScvlStatus = (Spinner) findViewById(R.id.civilstatus);
        FDbtsave = (Button) findViewById(R.id.formsave);
        FDbtcancel = (Button) findViewById(R.id.formcancel);
        etlname = (EditText) findViewById(R.id.formLname);
        etfname = (EditText) findViewById(R.id.formFname);
        etmname = (EditText) findViewById(R.id.formMname);
        etMobileNo = (EditText) findViewById(R.id.formMobile);
        etaddress = (EditText) findViewById(R.id.formaddress);
        etlandline = (EditText) findViewById(R.id.formLandline);
        etEmail = (EditText) findViewById(R.id.formEmailadd);
        /*[Start]DATE PICKER*/
        etBirth = (EditText) findViewById(R.id.formBirthDate);
        dateFormatter = new SimpleDateFormat("dd MMM yyyy", Locale.US);
        etBirth.setInputType(InputType.TYPE_NULL);

        ib = (ImageButton) findViewById(R.id.imageButton1);
        cal = Calendar.getInstance();
        day = cal.get(Calendar.DAY_OF_MONTH);
        month = cal.get(Calendar.MONTH);
        year = cal.get(Calendar.YEAR);
        ib.setOnClickListener(this);
     //   setDateTimeField();
        /*[End]DATE PICKER*/

        String mobileNumber = etMobileNo.getText().toString();


        String fname = getIntent().getStringExtra("firstname");
        String lname = getIntent().getStringExtra("lastname");
        // String mname = getIntent().getStringExtra("middlename");

        etlname.setText(fname);
        etfname.setText(lname);
        //etmname.setText(mname);


        FDbtcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent openForm_intent = new Intent(FormDetails.this, ActivityMain.class);
                // startActivity(openForm_intent);
                finish();
            }
        });

        FDbtsave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                String lname = etlname.getText().toString();
                String fname = etfname.getText().toString();
                String mname = etmname.getText().toString();
                String mobile = etMobileNo.getText().toString();
                String landline = etlandline.getText().toString();
                String email = etEmail.getText().toString();
                String address = etaddress.getText().toString();

                db = new DatabaseHandler(con);
                FormDetailData form = new FormDetailData();

                form.setlastName(lname);
                form.setfirstName(fname);
                form.setMiddle_i(mname);
                form.setMobNo(mobile);
                form.setLandline(landline);
                form.setEmailId(email);
                form.setAddress(address);
                //form.setBday_Month(item);


                db.editNewDetails(form);

                Toast.makeText(getApplicationContext(), "Edited", Toast.LENGTH_LONG).show();
            }
        });


        // Spinner Drop down elements CivilStatus
        List<String> CivilStatus = new ArrayList<String>();
        CivilStatus.add("Single");
        CivilStatus.add("Married");
        CivilStatus.add("Separated");
        CivilStatus.add("Widow");
        CivilStatus.add("Widower");
        CivilStatus.add("Common Law");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, CivilStatus);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        ScvlStatus.setAdapter(dataAdapter);
    }

    /*[Start]DATE PICKER*/
    protected Dialog onCreateDialog(int id) {
        return new DatePickerDialog(this, datePickerListener, year, month, day);
    }

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            etBirth.setText(selectedDay + " / " + (selectedMonth + 1) + " / "
                    + selectedYear);
        }
    };

    /*[End]DATE PICKER*/
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();

        // Showing selected spinner item
        // Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
    }
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-gen


        // TODO: 8/3/2016 create validations
        //erated method stub
    }

    /*DATE PICKER*/
    @Override
    public void onClick(View v) {
        showDialog(0);

        int checkRb = rgGen.getCheckedRadioButtonId();
        switch(checkRb) {
            case R.id.radioButton:
                if (rbMale.isChecked()) {
                }
                break;
            case R.id.radioButton2:
                if (rbFemale.isChecked()) {
                }
                break;
        }

    }
    /*
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            //start camera view again
            Intent i = new Intent(getApplicationContext(), ActivityMain.class);
            startActivity(i);

            return false;
        } else if(keyCode == KeyEvent.KEYCODE_MENU) {
            Toast.makeText(getApplicationContext(), " ", Toast.LENGTH_LONG).show();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
    */
    private static Boolean isExit = false;

    private void exitBy2Click() {
        Timer tExit = null;
        if (isExit == false) {
            isExit = true; // ready to exit
            Toast.makeText(this, "double tap to exit", Toast.LENGTH_SHORT).show();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                public void run() {
                    isExit = false; // exit canceled
                }
            }, 2000); // if user did't click again within 2 seconds, the process inside will be canceled
        } else {
            finish();
            System.exit(0);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            super.onBackPressed();
            return true;
        }
            return super.onOptionsItemSelected(item);
        }
    }


