package com.example.anurag.btapp;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.Set;

public class Mainscreen extends AppCompatActivity implements
        TextFragment.OnSendRequestListener,
        TextFragment.OnTextLogRequestListener,
        GraphFragment.OnSendRequestListener,
        GraphFragment.OnValueRequestListener, FragmentTabHost.OnTabChangeListener {

    public static final String TAG = "Device";
    public static final boolean D = true;
    private Toolbar toolbar;
    private FragmentTabHost tabhost;
    private FragmentManager manager;
    static long senddelay = 1;
    long sendDelay = senddelay;

    int maxCount = numValues;

    private LinkedList<String> Valuesecg;
    private LinkedList<String> Valuesppg;

    private BluetoothAdapter btadapter = null;

    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    private String ConnectedDevName = null;

    public static final String DEVICE_NAME = "device_name";
    public static final String DEVICE_ADDR = "device_addr";
    public static final String TOAST = "toast";

    private static final String PREF_DEVICE_ADDR = "pref_device_addr";
    private String lastConnectedDevAddr = null;

    static int numValues = 100;

    private LinkedList<String> values1;


    public BluetoothService btservice = null;
    private Toast Toast1;
    boolean sendtimefinish = true;
    CountDownTimer timer;
    private TextFragment textfrag;
    private GraphFragment graphfrag;
    private MGraphFragment mGraphFragment;

    private ArrayAdapter<String> convadapter;

    private InputMethodManager mgr;




    @Override
    public void onSendRequest(String s) {
        sendMessage(s);
        return;
    }


    @Override
    public ArrayAdapter<String> OnTextLogRequest() {
        return convadapter;
    }

    @Override
    public LinkedList<String> OnValueRequest() {
        return values1;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        if (D) Log.e(TAG, "+++ ON CREATE +++");
        setContentView(R.layout.activity_mainscreen);
        toolbar = (Toolbar) findViewById(R.id.xmltoolbar);
        //setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        btadapter = BluetoothAdapter.getDefaultAdapter();
        if (btadapter == null) {
            Toast.makeText(this, "Bluetooth not available la", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        values1 = new LinkedList<String>();
        //GraphFragment.initList(values1, maxCount);

        manager = this.getSupportFragmentManager();
        tabhost = (FragmentTabHost) findViewById(R.id.tabhost1);
        tabhost.setup(this, manager, R.id.tabFrameLayout);

        tabhost.addTab(tabhost.newTabSpec("Terminal").setIndicator("terminal"), TextFragment.class, null);
        tabhost.addTab(tabhost.newTabSpec("Graphs").setIndicator("Graphs"), MGraphFragment.class, null);
        tabhost.setOnTabChangedListener(this);

        timer = new CountDownTimer(sendDelay, sendDelay) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                sendtimefinish = true;

            }
        };

        mgr = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        Toast1.makeText(this, "", Toast.LENGTH_SHORT);
   }

    @Override
    public void onStart() {
        super.onStart();

        if (D) Log.e(TAG, "++ ON START ++");


        if (!btadapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);

        } else {
            if (btservice == null) setupBTService();
        }


    }


    @Override
    public synchronized void onResume() {
        super.onResume();
        if (D) Log.e(TAG, "+ ON RESUME +");


        if (btservice != null) {

            if (btservice.getState() == BluetoothService.STATE_NONE) {
                // Start the Bluetooth service
                btservice.start();
            }

            // Reconnect last connected device
            if (btservice.getState() != BluetoothService.STATE_CONNECTED) {
                SharedPreferences settings = getSharedPreferences(PREF_DEVICE_ADDR, 0);
                lastConnectedDevAddr = settings.getString("dev_addr", null);

                if (lastConnectedDevAddr != null) {
                    BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
                    // Get a set of currently paired devices
                    Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
                    for (BluetoothDevice device : pairedDevices) {
                        if (device.getAddress().equals(lastConnectedDevAddr)) {
                            btservice.connect(device, false);
                            break;
                        }
                    }
                }

            }
        }
    }

    @Override
    public synchronized void onPause() {
        super.onPause();
        if (D) Log.e(TAG, "- ON PAUSE -");
    }


    @Override
    public void onStop() {
        super.onStop();

        if (D) Log.e(TAG, "-- ON STOP --");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (btservice != null) btservice.stop();
        if (D) Log.e(TAG, "--- ON DESTROY ---");
    }

    private void setupBTService() {

        convadapter = new ArrayAdapter<String>(this, R.layout.message);


        btservice = new BluetoothService(this, handler1);
    }

    private void sendMessage(String msg) {
        if (btservice.getState() != btservice.connected_state) {
            Toast1.setText("Not connected to a device");
            Toast1.setDuration(Toast.LENGTH_SHORT);
            Toast1.show();
            return;
        }

        if (!sendtimefinish) {
            return;
        }

        if (msg.length() > 0) {
            byte[] send = msg.getBytes();
            btservice.write(send);
            timer.start();
            sendtimefinish = false;

            try {
                textfrag.resetbuffer();
            } catch (NullPointerException e) {
            }
        }
    }

    private final void setStatus(int resId) {
//        actionBar.setSubtitle(resId);
        toolbar.setSubtitle(resId);
    }

    private final void setStatus(CharSequence subTitle) {
        toolbar.setSubtitle(subTitle);
    }


    private final Handler handler1 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    if (D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            setStatus(getString(R.string.title_connected_to, ConnectedDevName));
                            try {
                                convadapter.clear();
                            } catch (NullPointerException e) {
                            }
                            break;
                        case BluetoothService.STATE_CONNECTING:
                            setStatus("connecting");
                            break;
                        case BluetoothService.STATE_LISTEN:
                        case BluetoothService.STATE_NONE:
                            setStatus(R.string.title_not_connected);
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;

                    String writeMessage = new String(writeBuf);
                    try {
                        convadapter.add("TX: " + writeMessage);
                    } catch (NullPointerException e) {
                    }
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;

                    String readMessage = new String(readBuf, 0, msg.arg1);
                    try {
                        String a[] = TextFragment.parsevalues(readMessage);
                        for (int i = 0; i < a.length; i++) {
                            convadapter.add("RX:  " + a[i]);
                            //Log.i("the signal", " " + a[i]);
                        }

                        // Log.i("the signal", " "+ readMessage);
                    } catch (NullPointerException e) {
                    }

                    try {
                        //Log.i("the signal11", " " + readMessage + "new message");
                        readMessage = readMessage.replace("\n", "").replace("\r", "");
                        //PPGFragment.parseValues(readMessage, Valuesecg);
                        //Log.d("the signal after parse"," "+ Valuesecg.getFirst());
                        //ppgFrag.updateGraph(Valuesecg);
                        //Log.v("value in list"," "+ Valuesecg.get(1));
                        //for(int i=0;i<Valuesecg.size();i++){
                        //  String x = Valuesecg.get(i);
                        GraphFragment.parsevalues(readMessage, values1);
                        graphfrag.updateGraph(values1);
                        //graphfrag.updateGraph1(values1);
                        //Log.i("values"," "+x);
                        //}
                    } catch (NullPointerException e) {
                    }
                    try {
                        Log.i("the signal", " " + readMessage);
                        readMessage = readMessage.replace("\n", "").replace("\r", "");

                        //ECGFragment.parseValues(readMessage, Valuesppg);
                        //Log.d("the signal after parse"," "+ Valuesppg.size());
                        //ecgFrag.updateGraph(Valuesppg);
                        //Log.v("value in list"," "+ Valuesppg.get(1));}
                    } catch (NullPointerException e) {
                    }

                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    ConnectedDevName = msg.getData().getString(DEVICE_NAME);
                    lastConnectedDevAddr = msg.getData().getString(DEVICE_ADDR);

                    if (D) Log.e(TAG, "connect to " + lastConnectedDevAddr);

                    SharedPreferences settings = getSharedPreferences(PREF_DEVICE_ADDR, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("device address", lastConnectedDevAddr);
                    editor.commit();

                    Toast.makeText(getApplicationContext(), "Connected to "
                            + ConnectedDevName, Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (D) Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:

                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, false);
                }
                break;
            case REQUEST_ENABLE_BT:

                if (resultCode == Activity.RESULT_OK) {

                    setupBTService();
                } else {

                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    private void connectDevice(Intent data, boolean secure) {

        String address = data.getExtras()
                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);

        BluetoothDevice device = btadapter.getRemoteDevice(address);

        btservice.connect(device, secure);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent serverIntent = null;
        //Toast1.cancel();
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            openOptionsMenu();
            return true;
        } else if (itemId == R.id.connect_scan) {

            serverIntent = new Intent(this, DeviceListActivity.class);
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
            return true;
        }
        return false;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        return;
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        if (fragment.getClass().equals(TextFragment.class)) {
            textfrag = (TextFragment) fragment;
        }
        if (fragment.getClass().equals(MGraphFragment.class)) {
            //graphfrag = (GraphFragment) fragment;
            mGraphFragment=(MGraphFragment)fragment;
        }
        /*else if (fragment.getClass().equals(PPGFragment.class)) {
            ppgFrag = (PPGFragment)fragment;
        }
        else if (fragment.getClass().equals(ECGFragment.class)) {
            ecgFrag = (ECGFragment)fragment;
        }*/
    }

    @Override
    public void onTabChanged(String tabId) {
        try {

            textfrag.hideKeyboard(mgr);

        } catch (NullPointerException e) {}
    }
}

