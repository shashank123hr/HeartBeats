package cs.uml.edu.heartbeats;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class mainActivity extends Activity {

    TextView mTextView;
    Cursor c;
    DBHelper dbConnector = new DBHelper(this);

    //shows the data logged by the application from wear
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView myList=(ListView)findViewById(android.R.id.list);
        ArrayList results = new ArrayList();

        //get the data from cursor and display in the activity
        try {
            c=dbConnector.gethrRecord();
            if (c != null) {

                c.moveToFirst();
                int hrate = c.getColumnIndex("HEART_RATE");
                int calorie = c.getColumnIndex("CALORIES_BURNED");
                int d = c.getColumnIndex("DATE");
                 /* Check if at least one Result was returned. */
                if (c.isFirst()) {
                    int i = 0;
                      /* Loop through all Results */
                    do {
                        i++;
                        String first = c.getString(hrate);
                        String cal = c.getString(calorie);
                        String date = c.getString(d);

                           /* Add current Entry to results. */
                        results.add(""+ date+ " Heart Rate" + first + " Calories " + cal);
                    } while (c.moveToNext());
                }
            }
            myList.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, results));
        }
        catch (Exception e)
        {

        }
        finally
        {
            if(c!= null)
            {
                c.close();
            }
        }

    }

    //menu for more options
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    protected void onResume(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
    //Handle the options menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        //to manage the signout
        if (id == R.id.action_signout) {
            Intent myIntent = new Intent(mainActivity.this, loginActivity.class);
            startActivity(myIntent);
            return true;
        }
        //to display the credits as a dialog
        else if (id == R.id.action_about) {
            AlertDialog alertDialog = new AlertDialog.Builder(mainActivity.this).create();
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
