package cs.uml.edu.heartbeats;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class Banner extends Activity {
    public TextView hrTextView;
    public TextView calTextView;
    public String HeartRate="Loading!!";
    public String CalorieData="Loading!!";
    public boolean flag=false;
    private static final String TAG = "Banner";
    private BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner);

        hrTextView = (TextView)  findViewById(R.id.textView5);
        calTextView = (TextView) findViewById(R.id.textView6);
        //Braoadcast Reciever to get the data from the WearListener service to update UI used it because threads blocks the main UI which is not advisable
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                HeartRate = intent.getStringExtra("Hr");
                CalorieData = intent.getStringExtra("Cal");
                hrTextView.setText(HeartRate);
                calTextView.setText(CalorieData);
            }
        };
    }

    //To create menu option in the UI
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_banner, menu);
        return true;
    }

    //To get items from the options menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //To go to the Status Records
        if (id == R.id.action_status) {
            Intent myIntent = new Intent(Banner.this, mainActivity.class);
            startActivity(myIntent);
            return true;
        }
        //To go to login Activity
        else if (id == R.id.action_signout) {
            Intent myIntent = new Intent(Banner.this, loginActivity.class);
            startActivity(myIntent);
            return true;
        }
        //To display the credits
        else if (id == R.id.action_about) {
            AlertDialog alertDialog = new AlertDialog.Builder(Banner.this).create();
            alertDialog.setTitle("About");
            alertDialog.setMessage("Developed for 91.580 guided by Prof.Guanling Chen in UMass Lowell by Shashank HR");
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

    //to register broadcast receiver
    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((receiver), new IntentFilter(WearListener.B_TAG));
    }

    //to unregister broadcast receiver on exit
    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onStop();
    }
}
