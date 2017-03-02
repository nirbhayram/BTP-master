package com.example.anurag.btapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by ANURAG on 01-08-2016.
 */
public class BluetoothService {
    private final BluetoothAdapter adapter1;
    private final Handler handler1;
    private int state1;
    private static final String TAG = "BluetoothService";
    private ConnectThread conn_thread;
    private ConnectedThread conned_thread;
    private AcceptThread secacceptthread;
    private AcceptThread insecaccceptthread;


    public static final int no_state = 0;
    public static final int LISTEN_state = 1;
    public static final int connecting_state = 2;
    public static final int connected_state = 3;

    public static final int STATE_NONE = 0;
    public static final int STATE_LISTEN = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;

    private static final String SECURENAME = "PIC_Conn_Secure";
    private static final String INSECURENAME = "PIC_Conn_Insecure";
    private static final UUID SECUREUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final UUID INSECUREUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public BluetoothService(Context context, Handler handler) {
        adapter1 = BluetoothAdapter.getDefaultAdapter();
        state1 = 1;
        handler1 = handler;
    }

    public synchronized void setState(int state) {
        state1 = state;
    }

    public synchronized int getState() {
        return state1;
    }

    public synchronized void start()
    {
        if(conn_thread!=null){
            conn_thread.cancel();
            conn_thread=null;
        }
        if(conned_thread!=null){
            conned_thread.cancel();
            conned_thread=null;
        }
        setState(LISTEN_state);
        if(secacceptthread==null){
            secacceptthread=new AcceptThread(true);
            secacceptthread.start();
        }
        if(insecaccceptthread==null){
            insecaccceptthread=new AcceptThread(false);
            insecaccceptthread.start();
        }
    }

    public synchronized void connect(BluetoothDevice device,boolean sec){
        if(state1==connecting_state){
            if(conn_thread!=null){
                conn_thread.cancel();
                conn_thread=null;
            }


        }
        if(conned_thread!=null){
            conned_thread.cancel();
            conned_thread=null;
        }

        conn_thread=new ConnectThread(device,sec);
        conn_thread.start();
        setState(connecting_state);
    }

    public synchronized void connected(BluetoothSocket socket,BluetoothDevice device,final String sockettype){
        if(conn_thread!=null){
            conn_thread.cancel();
            conn_thread=null;
        }
        if(conned_thread!=null){
            conned_thread.cancel();
            conned_thread=null;
        }
        if(secacceptthread!=null){
            secacceptthread.cancel();
            secacceptthread=null;
        }
        if(insecaccceptthread!=null){
            insecaccceptthread.cancel();
            insecaccceptthread=null;
        }
        conned_thread=new ConnectedThread(socket,sockettype);
        conned_thread.start();
        Message msg=handler1.obtainMessage(4);
        Bundle bundle=new Bundle();
        bundle.putString("devicename",device.getName());
        bundle.putString("deviceadress",device.getAddress());
        msg.setData(bundle);
        setState(connected_state);
    }

    public synchronized void stop(){
        if(conn_thread!=null){
            conn_thread.cancel();
            conn_thread=null;
        }
        if(conned_thread!=null){
            conned_thread.cancel();
            conned_thread=null;
        }
        if(secacceptthread!=null){
            secacceptthread.cancel();
            secacceptthread=null;
        }
        if(insecaccceptthread!=null){
            insecaccceptthread.cancel();
            insecaccceptthread=null;
        }
        setState(no_state);
    }

    public void write(byte[] output){
        ConnectedThread tmp;
        synchronized(this){
            if(state1!=connected_state) return;
            tmp=conned_thread;
        }
        tmp.write(output);
    }

    private void connectionFailed(){
        Message msg=handler1.obtainMessage(5);
        Bundle bundle=new Bundle();
        bundle.putString("toast","Can't connect device");
        msg.setData(bundle);
        handler1.sendMessage(msg);
        BluetoothService.this.start();
    }

    private void connectionLost(){
        Message msg=handler1.obtainMessage(5);
        Bundle bundle=new Bundle();
        bundle.putString("toast","device connection lost");
        msg.setData(bundle);
        handler1.sendMessage(msg);
        BluetoothService.this.start();
    }


    private class AcceptThread extends Thread {
        private final BluetoothServerSocket ServerSocket2;
        private String sockettype;

        public AcceptThread(boolean a) {
            BluetoothServerSocket temp = null;
            sockettype = a ? "Secure" : "Insecure";
            try {
                if (a) {
                    temp = adapter1.listenUsingRfcommWithServiceRecord(SECURENAME, SECUREUUID);
                } else {
                    temp = adapter1.listenUsingInsecureRfcommWithServiceRecord(INSECURENAME, INSECUREUUID);
                }
            } catch (IOException e) {
                Log.e(TAG, "Socket Type: " + sockettype + "listen() failed", e);
                //Log.e(TAG + sockettype + "meoww", e);
            }
            ServerSocket2 = temp;
        }

        public void run() {
            if (true) {
                Log.d(TAG, sockettype + "acceptthread" + this);

            }
            setName("AcceptThread" + sockettype);
            BluetoothSocket socket = null;
            while (state1 != connected_state) {
                try {
                    socket = ServerSocket2.accept();
                } catch (IOException e) {
                    Log.e(TAG, "Socket Type: " + sockettype + "listen() failed", e);
                    break;
                }
                if (socket != null) {
                    synchronized (BluetoothService.this) {
                        switch (state1) {
                            case LISTEN_state:
                            case connecting_state:
                                connected(socket, socket.getRemoteDevice(),sockettype);
                                break;
                            case no_state:
                            case connected_state:
                                try{
                                    socket.close();
                                }catch(IOException e){
                                    Log.e(TAG,"cant close socket");
                                }
                                break;


                        }
                    }
                }
            }
            if(true){
                Log.d(TAG,"acceptthread socket"+sockettype);
            }
        }
        public void cancel(){
            try{
                ServerSocket2.close();
            }catch(IOException e){
                Log.e(TAG,"connection close failed for"+sockettype);
            }
        }
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket socket2;
        private final BluetoothDevice device1;
        private String socket_type;

        public ConnectThread(BluetoothDevice device, boolean secure) {
            device1 = device;
            BluetoothSocket tmp = null;
            socket_type = secure ? "Secure" : "Insecure";
            try {
                if (secure) {
                    tmp = device1.createRfcommSocketToServiceRecord(SECUREUUID);
                } else {
                    tmp = device1.createInsecureRfcommSocketToServiceRecord(INSECUREUUID);
                }
            } catch (IOException e) {
                Log.e(TAG, "Socket Type: " + socket_type + "listen() failed", e);
                //Log.e(TAG + sockettype + "meoww", e);
            }
            socket2 = tmp;
        }

        public void run(){
            setName("connected thread"+socket_type);
            adapter1.cancelDiscovery();

            try{
                socket2.connect();
            } catch (IOException e) {
                try{
                    socket2.close();
                }catch (IOException e1){
                    Log.e(TAG,"cant close"+socket_type+"failed la",e1);
                }
                connectionFailed();
                return;
            }
            synchronized (BluetoothService.this) {
                conn_thread = null;
            }
            connected(socket2,device1,socket_type);
        }

        public void cancel() {
            try {
                socket2.close();
            } catch (IOException e) {
                Log.e(TAG, " cant close" + socket_type + "failed", e);
            }
        }
    }

    private class ConnectedThread extends Thread{
        private final BluetoothSocket socket2;
        private final OutputStream outstream;
        private final InputStream instream;
        public ConnectedThread(BluetoothSocket socket,String sockettype){
            socket2=socket;
            InputStream tempin=null;
            OutputStream tempout=null;

            try{
                tempin= socket.getInputStream();
                tempout=socket.getOutputStream();
            }catch(IOException e){
                Log.e(TAG,"socket not there la",e);
            }
            instream=tempin;
            outstream=tempout;
        }
        public void run(){
            byte[] buffer=new byte[1024];
            int bytes;
            while(true){
                try{
                    bytes=instream.read(buffer);
                    handler1.obtainMessage(2,bytes,-1,buffer).sendToTarget();
                }catch(IOException e){
                    Log.e(TAG,"disconnect maybe",e);
                    connectionLost();
                    BluetoothService.this.start();
                    break;
                }
            }
        }

        public void write(byte[] buffer){
            try{
                outstream.write(buffer);
                handler1.obtainMessage(3,-1,-1,buffer).sendToTarget();
            }catch(IOException e){
                Log.e(TAG,"can't write",e);
            }
        }

        public void cancel(){
            try {
                socket2.close();
            }catch(IOException e){
                Log.e(TAG,"cangt close connected socket la",e);
            }
        }
    }
}