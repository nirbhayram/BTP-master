package com.example.anurag.btapp;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Set;

public class DeviceListActivity extends AppCompatActivity {

    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    private Toolbar toolbar;
    private BluetoothAdapter btadapter;
    private ArrayAdapter<String> paireddevices;
    private ArrayAdapter<String> newdevices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);

        toolbar=(Toolbar)findViewById(R.id.xmltoolbar);
//        setSupportActionBar(toolbar);

        setResult(Activity.RESULT_CANCELED);

        Button scan=(Button)findViewById(R.id.button_scan);
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doDiscovery();
                view.setVisibility(View.GONE);
            }
        });

        paireddevices=new ArrayAdapter<String>(this,R.layout.device_name);
        newdevices=new ArrayAdapter<String>(this,R.layout.device_name);

        ListView pairedView=(ListView)findViewById(R.id.paired_devices);
        pairedView.setAdapter(paireddevices);
        pairedView.setOnItemClickListener(deviceclickListener);

        ListView newView=(ListView)findViewById(R.id.new_devices);
        newView.setAdapter(newdevices);
        newView.setOnItemClickListener(deviceclickListener);
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(Receiver1,filter);

        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(Receiver1,filter);

        btadapter=BluetoothAdapter.getDefaultAdapter();

        Set<BluetoothDevice> paireddev=btadapter.getBondedDevices();

        if(paireddev.size()>0){
            findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
            for(BluetoothDevice device : paireddev){
                paireddevices.add(device.getName()+"\n"+device.getAddress());
            }
        }else{
            paireddevices.add("No device paired la");
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

        if(btadapter!=null){
            btadapter.cancelDiscovery();
        }
        this.unregisterReceiver(Receiver1);
    }

    private void doDiscovery(){
        setProgressBarIndeterminateVisibility(true);
        setTitle("scanning for dev");

        findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);
        if(btadapter.isDiscovering()){
            btadapter.cancelDiscovery();
        }
        btadapter.startDiscovery();
    }

    private AdapterView.OnItemClickListener deviceclickListener=new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            btadapter.cancelDiscovery();

            String info=((TextView) view).getText().toString();
            String address=info.substring(info.length()-17);

            Intent intent=new Intent();
            intent.putExtra(EXTRA_DEVICE_ADDRESS,address);

            setResult(RESULT_OK,intent);
            finish();
        }
    };

    private final BroadcastReceiver Receiver1 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device=intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    newdevices.add(device.getName()+"\n"+device.getAddress());
                }
            }else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                setProgressBarIndeterminateVisibility(false);
                setTitle("select a device to connect");
                if(newdevices.getCount()==0){
                    String noDevices = "No devices found".toString();
                    newdevices.add(noDevices);
                }
            }
        }
    };
}
