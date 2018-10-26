package cs.uml.edu.heartbeats;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;


public class loginActivity extends Activity {

    //creating a database object for database operatuions
    DBHelper dConnector = new DBHelper(this);

    String Fname;
    String emailid;
    String phone;
    String passwd;
    String ages;
    String weights;
    String selection;

    //here signup and login are in the same activity use the onclick listener and Views to make it look like two activites
    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final EditText firstName = (EditText) findViewById(R.id.firstName);
        final EditText email = (EditText) findViewById(R.id.email);
        final EditText phoneNumber = (EditText) findViewById(R.id.phone);
        final EditText age = (EditText) findViewById(R.id.age);
        final EditText weight = (EditText) findViewById(R.id.weight);
        final RadioGroup rg=(RadioGroup)findViewById(R.id.radioSex);
        final RadioButton b;
        final EditText password = (EditText) findViewById(R.id.password);
        final Button register = (Button)findViewById(R.id.register);
        final Button login = (Button) findViewById(R.id.login);
        final Button submit = (Button) findViewById(R.id.submit);

        firstName.setVisibility(View.GONE);
        email.setVisibility(View.VISIBLE);
        phoneNumber.setVisibility(View.GONE);
        submit.setVisibility(View.GONE);
        age.setVisibility(View.GONE);
        weight.setVisibility(View.GONE);
        rg.setVisibility(View.GONE);

        int selected = rg.getCheckedRadioButtonId();
        b= (RadioButton) findViewById(selected);


        //on click listener for register button
        register.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                firstName.setVisibility(View.VISIBLE);
                email.setVisibility(View.VISIBLE);
                phoneNumber.setVisibility(View.VISIBLE);
                age.setVisibility(View.VISIBLE);
                weight.setVisibility(View.VISIBLE);
                rg.setVisibility(View.VISIBLE);
                login.setVisibility(View.GONE);
                register.setVisibility(View.GONE);
                submit.setVisibility(View.VISIBLE);
            }
        });
        //on click listener for login button validating user credentials
        login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                try {
                    List<String> userRecord = dConnector.getRecord(email.getEditableText().toString(), password.getEditableText().toString());
                    Intent myIntent = new Intent(view.getContext(), Banner.class);
                    startActivityForResult(myIntent, 0);
                }
                catch (Exception e) {

                    Toast.makeText(getApplicationContext(), "Login Failed, Record Not Found", Toast.LENGTH_SHORT).show();

                    e.printStackTrace();

                }



            }



        });

        //on click listener for submit button to add the user data in the database
        submit.setOnClickListener(new View.OnClickListener(){




            @Override

            public void onClick(View view) {

                Fname=(firstName.getEditableText().toString());
                emailid=(email.getEditableText().toString());
                phone=(phoneNumber.getEditableText().toString());
                ages=(age.getEditableText().toString());
                weights=(weight.getEditableText().toString());
                phone=(phoneNumber.getEditableText().toString());
                passwd=(password.getEditableText().toString());
                selection= (String)b.getText();
                dConnector.addRecord(Fname,emailid,phone,passwd,ages,weights,selection);
                Toast.makeText(getApplicationContext(), "Record Added!", Toast.LENGTH_SHORT).show();
                firstName.setVisibility(View.GONE);
                email.setVisibility(View.VISIBLE);
                phoneNumber.setVisibility(View.GONE);
                age.setVisibility(View.GONE);
                weight.setVisibility(View.GONE);
                rg.setVisibility(View.GONE);
                submit.setVisibility(View.GONE);
                login.setVisibility(View.VISIBLE);
                register.setVisibility(View.VISIBLE);

            }



        });




    }

    //menu options for navigation
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    //Handling the settings option
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //dialog to show the credits
        if (id == R.id.action_about) {
            AlertDialog alertDialog = new AlertDialog.Builder(loginActivity.this).create();
            alertDialog.setTitle("About");
            alertDialog.setMessage("Developed for 91.580 guided by Prof.Guanling Chen in UMass Lowell Developed by Shashank HR");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
