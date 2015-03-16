package com.example.jack.droid_proc_man;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Camera;
import android.hardware.camera2.CameraManager;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends Activity {

    final static String TAG = "JAAAAAAAAAAAAAAAAAACK";
    ListView listView ;
    boolean mic, spk, msc, blt, cam, loc;
    AudioManager am;
    ArrayList<ResInfo> arrayOfResInfo;

    LocationManager locationManager;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get ListView object from xml
        listView = (ListView) findViewById(R.id.listview);

        // Construct the data source
        arrayOfResInfo = new ArrayList<>();
        updateStates();
        // Create the adapter to convert the array to views
        ResInfoAdapter adapter = new ResInfoAdapter(this, arrayOfResInfo);
        // Attach the adapter to a ListView
        listView.setAdapter(adapter);

        IntentFilter filter1 = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
        IntentFilter filter2 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        IntentFilter filter3 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        this.registerReceiver(mReceiver, filter1);
        this.registerReceiver(mReceiver, filter2);
        this.registerReceiver(mReceiver, filter3);

        // http://stackoverflow.com/questions/843675/how-do-i-find-out-if-the-gps-of-an-android-device-is-enabled
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // ListView Item Click Listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

//                // ListView Clicked item index
//                int itemPosition     = position;
//
//                // ListView Clicked item value
//                String  itemValue    = (String) listView.getItemAtPosition(position);
//
//                // Show Alert
//                Toast.makeText(getApplicationContext(),
//                        "Position :" + itemPosition + "  ListItem : " + itemValue, Toast.LENGTH_LONG)
//                        .show();
//
            }

        });
    }

    public void updateStates() {
        arrayOfResInfo.clear();
        am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mic = am.isMicrophoneMute();
        spk = am.isSpeakerphoneOn();
        msc = am.isMusicActive();
        cam = isCameraUsebyApp();
        loc = isGPSon();
        arrayOfResInfo.add(new ResInfo("Microphone", mic));
        arrayOfResInfo.add(new ResInfo("Speaker", spk));
        arrayOfResInfo.add(new ResInfo("Music", msc));
        arrayOfResInfo.add(new ResInfo("Camera", cam));
        arrayOfResInfo.add(new ResInfo("Bluetooth", blt));
        arrayOfResInfo.add(new ResInfo("GPS", loc));
    }

    public boolean isGPSon() {
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            return true;
        }else{
            return false;
        }
    }

    // http://stackoverflow.com/questions/15862621/how-to-check-if-camera-is-opened-by-any-application
    public boolean isCameraUsebyApp() {
        Camera camera = null;
        try {
            camera = Camera.open();
        } catch (RuntimeException e) {
            return true;
        } finally {
            if (camera != null) camera.release();
        }
        return false;
    }

    // The BroadcastReceiver that listens for bluetooth broadcasts
    // http://stackoverflow.com/questions/4715865/how-to-programmatically-tell-if-a-bluetooth-device-is-connected-android-2-2
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            }
            else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                blt = true;
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
            }
            else if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action)) {
            }
            else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                blt = false;
            }
        }
    };

    public class ResInfo {
        public String resName;
        public boolean onoff;

        ResInfo(String s, boolean b) {
            this.resName = s;
            this.onoff = b;
        }

    }

    public class ResInfoAdapter extends ArrayAdapter<ResInfo> {
        public ResInfoAdapter(Context context, ArrayList<ResInfo> users) {
            super(context, 0, users);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            ResInfo user = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.resource_list_item, parent, false);
            }
            // Lookup view for data population
            TextView tvName = (TextView) convertView.findViewById(R.id.label);
            TextView tvHome = (TextView) convertView.findViewById(R.id.onoff);
            // Populate the data into the template view using the data object
            tvName.setText(user.resName);
            tvHome.setText(user.onoff ? "On" : "Off");
            // Return the completed view to render on screen
            return convertView;
        }
    }
}