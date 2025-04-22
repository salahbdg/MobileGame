package com.bdg.mobilegame.utils;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.util.UUID;


public class BluetoothService {
    private final BluetoothAdapter bluetoothAdapter;
    private final UUID appUUID = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66"); // Use a fixed UUID
    private AcceptThread acceptThread;
    private ConnectThread connectThread;

    public interface ConnectionCallback {
        void onConnected(BluetoothSocket socket);
    }

    public BluetoothService(BluetoothAdapter adapter) {
        bluetoothAdapter = adapter;
    }

    public void startServer(Context context, ConnectionCallback callback) {
        acceptThread = new AcceptThread(context, callback);
        acceptThread.start();
    }

    public void connectToDevice(Context context, BluetoothDevice device, ConnectionCallback callback) {
        connectThread = new ConnectThread(context, device, callback);
        connectThread.start();
    }


    private class AcceptThread extends Thread {
        private BluetoothServerSocket serverSocket;
        private final ConnectionCallback callback;

        public AcceptThread(Context context, ConnectionCallback cb) {
            BluetoothServerSocket tmp = null;
            callback = cb;

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1002);
                Log.e("Bluetooth", "BLUETOOTH_CONNECT permission not granted");

                return;
            }

            try {
                tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord("BluetoothCounterApp", appUUID);
            } catch (IOException | SecurityException e) {
                e.printStackTrace();
            }

            serverSocket = tmp;
        }


        public void run() {
            BluetoothSocket socket;
            try {
                socket = serverSocket.accept(); // blocking call
                if (socket != null) {
                    callback.onConnected(socket);
                    serverSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ConnectThread extends Thread {
        private BluetoothSocket socket;
        private final ConnectionCallback callback;
        private final Context context; // Add this field


        public ConnectThread(Context context, BluetoothDevice device, ConnectionCallback cb) {
            this.context = context; // Save the context
            callback = cb;
            socket = null;

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1002);
                return;
            }

            try {
                socket = device.createRfcommSocketToServiceRecord(appUUID);
            } catch (IOException | SecurityException e) {
                e.printStackTrace();
            }
        }


        public void run() {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1002);
                return;
            }

            bluetoothAdapter.cancelDiscovery();
            try {
                socket.connect();
                callback.onConnected(socket);
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    socket.close();
                } catch (IOException closeException) {
                    closeException.printStackTrace();
                }
            }
        }

    }
}
