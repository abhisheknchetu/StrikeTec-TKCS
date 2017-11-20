package com.striketec.mobile.util.sensormanager.bluetooth.connection;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.striketec.mobile.util.AppConst;

import java.io.IOException;
import java.util.UUID;

/**
 * Thread to manage the bluetooth connection attempt.
 *
 * @author Cogden
 */
public class ConnectionThread extends Thread {

    // Class log tag
    private static final String TAG = "ConnectionThread";

    // Handler to communicate with UI
    private final Handler uiHandler;

    // Bluetooth connection
    private final BluetoothSocket bluetoothSocket;

    public BluetoothSocket getBluetoothSocket() {
        return bluetoothSocket;
    }

    private final BluetoothDevice bluetoothDevice;
    private final UUID uuid;
    private final BluetoothAdapter bluetoothAdapter;
    private final ConnectionManager bluetoothConnectionManager;

    public ConnectionManager getBluetoothConnectionManager() {
        return bluetoothConnectionManager;
    }

    /**
     * Basic constructor.
     *
     * @param device  - Bluetooth device to attempt connecting to
     * @param handler - UI handler to notify with messages of
     * @param id      - UUID of device to attempt to connect to
     * @param adapter - System bluetooth adapter to use for communication
     * @param manager - Manger that controls the connection thread
     */
    public ConnectionThread(BluetoothDevice device, Handler handler, UUID id, BluetoothAdapter adapter, ConnectionManager manager) {
        uuid = id;
        bluetoothAdapter = adapter;
        bluetoothDevice = device;
        uiHandler = handler;
        bluetoothConnectionManager = manager;

        BluetoothSocket tmp = null;

        // Get a BluetoothSocket for a connection with the
        // given BluetoothDevice
        try {
            tmp = device.createInsecureRfcommSocketToServiceRecord(uuid);
        } catch (IOException e) {
            Log.d(TAG, "Bluetooth socket creation failed", e);
        }

        bluetoothSocket = tmp;
    }

    /**
     * Method to be run as a seperate thread, attempting to connect to a bluetooth device.
     */
    public void run() {
        Log.d(TAG, "Beginning connection thread");
        setName("ConnectionThread");

        // Canceling discovery to avoid slowing down thread
        boolean cancledDiscovery = bluetoothAdapter.cancelDiscovery();

        try {
            bluetoothSocket.connect();
            if (bluetoothSocket != null) {
                Log.i("ConnectionThread", AppConst.SENSOR_CONNECTION_ESTABLISHED_MESSAGE + "=" + bluetoothDevice.getAddress());
                String infoMsg = AppConst.SENSOR_CONNECTION_ESTABLISHED_MESSAGE + bluetoothDevice.getAddress();
                Message msg = uiHandler.obtainMessage(AppConst.MESSAGE_TOAST);
                Bundle bundle = new Bundle();
                bundle.putString("TOAST", infoMsg);
                bundle.putString(AppConst.CONNECTED_DEVICE_TEXT, bluetoothDevice.getAddress());
                bundle.putString("CONNECTION", "success");
                msg.setData(bundle);
                uiHandler.sendMessage(msg);
            }
            // Send the successful connection status to the UI
            //uiHandler.obtainMessage(TabletFightersActivity.MESSAGE_TOAST, "Connection established with device "+ bluetoothDevice.getAddress()).sendToTarget();

        } catch (IOException e) {

            String infoMsg = AppConst.SENSOR_CONNECTION_FAILED_MESSAGE + bluetoothDevice.getAddress();
            Message msg = uiHandler.obtainMessage(AppConst.MESSAGE_TOAST);
            Bundle bundle = new Bundle();
            bundle.putString("TOAST", infoMsg);
            bundle.putString("DeviceAddress", bluetoothDevice.getAddress());
            bundle.putString("CONNECTION", "unsuccess");
            msg.setData(bundle);
            uiHandler.sendMessage(msg);
            //uiHandler.obtainMessage(TabletFightersActivity.MESSAGE_TOAST, "Failed to connect with device = " + bluetoothDevice.getAddress() + ". Please check your device ID and Try again").sendToTarget();
            bluetoothConnectionManager.disconnect();

            return;
        } catch (Exception ex) {
            ex.printStackTrace();
            String infoMsg = AppConst.BLUETOOTH_SOCKET_CONNECTION_FAILED_MESSAGE;
            Message msg = uiHandler.obtainMessage(AppConst.MESSAGE_TOAST);
            Bundle bundle = new Bundle();
            bundle.putString("TOAST", infoMsg);
            bundle.putString("CONNECTION", "unsuccess");
            msg.setData(bundle);
            uiHandler.sendMessage(msg);
            bluetoothConnectionManager.disconnect();
            return;
        }

        // Otherwise, we are good to go and need to start reading data
        //bluetoothConnectionManager.connected(bluetoothSocket);
    }

    /**
     * Closes the connection made.
     */
    public void cancel() {
        Log.d(TAG, "Closing connection");

        try {
            bluetoothSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}