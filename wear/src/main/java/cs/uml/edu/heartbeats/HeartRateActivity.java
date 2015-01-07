package cs.uml.edu.heartbeats;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.concurrent.CountDownLatch;

public class HeartRateActivity extends Activity implements SensorEventListener,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private static final String TAG = HeartRateActivity.class.getName();

    private TextView rate;
    private TextView accuracy;
    private TextView banner;
    private static final int SENSOR_TYPE_HEARTRATE = 65562;
    private Sensor mHeartRateSensor;
    private SensorManager mSensorManager;
    private CountDownLatch latch;
    private String hRate;
    private byte[] hrByte;

    Node mNode; // the connected device to send the message to phone
    GoogleApiClient mGoogleApiClient;
    private static final String HELLO_WORLD_WEAR_PATH = "/heart-rate-wear";
    private boolean mResolvingError=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_rate);
        latch = new CountDownLatch(1);

        //Connect the GoogleApiClient
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        //UI for Wear
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                banner = (TextView) stub.findViewById(R.id.sensor);
                banner.setText("Heart Rate:");

                rate = (TextView) stub.findViewById(R.id.rate);
                rate.setText("Reading...");

                accuracy = (TextView) stub.findViewById(R.id.accuracy);
                latch.countDown();


            }
        });
        // declaring the type of sensor we use
        mSensorManager = ((SensorManager)getSystemService(SENSOR_SERVICE));
        mHeartRateSensor = mSensorManager.getDefaultSensor(SENSOR_TYPE_HEARTRATE);

    }

    //Start Google message API and register the sensor
    @Override
    protected void onStart() {
        super.onStart();
        if (!mResolvingError) {
            mGoogleApiClient.connect();
        }

        mSensorManager.registerListener(this, this.mHeartRateSensor, 3);
    }

    //on senosor value chanege convert the sensor data into bytes as used by google message API
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        try {
            latch.await();
            if(sensorEvent.values[0] > 0){
                Log.d(TAG, "sensor event: " + sensorEvent.accuracy + " = " + sensorEvent.values[0]);
                rate.setText(String.valueOf(sensorEvent.values[0]));
                hRate=String.valueOf(sensorEvent.values[0]);
                hrByte = hRate.getBytes("UTF-8");
                accuracy.setText("Accuracy: "+sensorEvent.accuracy);
                sendMessage();
            }

        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

    }
    //send message to Android phone from Wear
    private void sendMessage() {

        if (mNode != null && mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            Wearable.MessageApi.sendMessage(
                    mGoogleApiClient, mNode.getId(), HELLO_WORLD_WEAR_PATH, hrByte).setResultCallback(

                    new ResultCallback<MessageApi.SendMessageResult>() {
                        @Override
                        public void onResult(MessageApi.SendMessageResult sendMessageResult) {

                            if (!sendMessageResult.getStatus().isSuccess()) {
                                Log.e("TAG", "Failed to send message with status code: "
                                        + sendMessageResult.getStatus().getStatusCode());
                            }
                        }
                    }
            );
        }
    }
    //Node resolution for the mobile client
    private void resolveNode() {

        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
            @Override
            public void onResult(NodeApi.GetConnectedNodesResult nodes) {
                for (Node node : nodes.getNodes()) {
                    mNode = node;
                }
            }
        });
    }
    //Change the accuracy value as the sensor accuracy changes
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        Log.d(TAG, "accuracy changed: " + i);
    }


    //unregister the sensor
    @Override
    protected void onStop() {
        super.onStop();

        mSensorManager.unregisterListener(this);
    }

     //on connection success update resolve node
    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "Connection Sucessful ");
        resolveNode();
    }

    //on connection suspended show in the log
    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Connection Suspeneded ");
    }

    //on connection failure show log connection failure
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Connection Failed ");
    }
}