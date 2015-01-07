package cs.uml.edu.heartbeats;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.WearableListenerService;

import java.math.BigInteger;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;

public class WearListener extends WearableListenerService
{
    //creating a database object for using the database operations
    DBHelper dbConnector = new DBHelper(this);

    private static final String HELLO_WORLD_WEAR_PATH = "/heart-rate-wear";
    private static final String TAG = WearListener.class.getName();
    private int heartrate = 70;
    private int calorieSpent = 225;
    private String gender;
    private int age;
    private int weight;
    public int temp = 0;
    public String HRate="Loading";
    public String CRate="Loading";
    public boolean flag=false;

    private ArrayList<Integer> calorie = new ArrayList<Integer>();
    private ArrayList<Integer> hrate = new ArrayList<Integer>();
    private long startTime = System.currentTimeMillis();
    private long elapsedTime=0;
    private String mydate;
    private Banner b = new Banner();
    public LocalBroadcastManager broadcaster = LocalBroadcastManager.getInstance(this);
    public LocalBroadcastManager broadcaster2 = LocalBroadcastManager.getInstance(this);
    static final public String B_TAG= "cs.uml.edu.heartbeats.updateui";

    //usage of a constructor is advisable to implement a broadcast service
    public WearListener(){
        super();
    }
    //deafault method to get the data from google message API
    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        try {

            if (messageEvent.getPath().equals(HELLO_WORLD_WEAR_PATH)) {
                String gender = dbConnector.usr_gender;
                int age = Integer.parseInt(dbConnector.usr_age);
                int weight =Integer.parseInt(dbConnector.usr_weight);
                //A simple delay logic to check for add values in db
                elapsedTime = System.currentTimeMillis() - startTime;
                if(elapsedTime>10000) {
                    startTime=System.currentTimeMillis();
                    elapsedTime=0;
                    addDbValue();
                }
                HRate="Loading";
                String s = new String(messageEvent.getData(), "UTF-8");
                heartrate= (int)Double.parseDouble(s);
                //Send data to broadcast listener
                sendHR(String.valueOf(heartrate),String.valueOf(calorieSpent));
                //Buffer logic for detecting the data in a specific range
                if(temp==0)
                {
                    temp=heartrate;
                }
                else {
                    temp=calculateCalories(heartrate, temp);
                }
            }

        }
        catch(Exception e)
        {
            Log.d(TAG, "exception "+e);
        }

    }
    //Buffer Logic for calculating calories and maintaing consistency
    public int calculateCalories(final int heartrate,int temp) {
        if ((temp / 10) != (heartrate / 10)) {
            temp = heartrate;
            if (gender.equals("male")) {
                calorieSpent = (int) Math.round(((-55.0969 + (0.6309 * temp) + (0.1988 * (0.4536 * weight)) + (0.2017 * age)) / 4.184) * 60);
                calorie.add((int) calorieSpent);
                hrate.add(temp);
                sendHR(String.valueOf(heartrate),String.valueOf((int) calorieSpent));
            } else if (gender.equals("female")) {
                calorieSpent = (int) Math.round(((-20.4022 + (0.4472 * temp) - (0.1263 * (0.4536 * weight)) + (0.074 * age)) / 4.184) * 60);
                calorie.add((int) calorieSpent);
                hrate.add(temp);
                sendHR(String.valueOf(heartrate),String.valueOf((int) calorieSpent));
            }
        }
        return temp;
    }
    //add values in the database and averaging the values for optimisation
    public void addDbValue()
    {
        Integer sum = 0, average = 0, i = 0,hraverage = 0;
        if(!(hrate.isEmpty())||(calorie.isEmpty()))
        {
            if (calorie.size() != 0) {
                for (i = 0; i < calorie.size(); i++) {
                    sum = sum + calorie.get(i);
                }
                average = sum / i;
                calorie.clear();
            }
            if (hrate.size() != 0) {
                sum = 0;
                i = 0;
                for (i = 0; i < hrate.size(); i++) {
                    sum = sum + hrate.get(i);
                }
                hraverage = sum / i;
                hrate.clear();
            }
            mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
            if((hraverage!=0)||(average!=0)) {
                dbConnector.addhrRecord(hraverage.toString(), average.toString(), mydate);
            }
        }

    }
    //Send broadcast to Banner to update UI
    public void sendHR(String message,String message2) {
        Intent intent = new Intent(B_TAG);
        if(message != null)
            intent.putExtra("Hr", message);
            intent.putExtra("Cal", message2);
        broadcaster.sendBroadcast(intent);
    }
}

