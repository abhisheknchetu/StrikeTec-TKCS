package com.striketec.mobile.util.sensormanager.bluetooth.connection;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.striketec.mobile.util.AppConst;

import java.util.UUID;

/**
 * Class to manage the connection to and reading of a bluetooth device.
 *
 * @author Cogden
 */
public class ConnectionManager {

    // Class log tag
    private static final String TAG = "ConnectionManager";

    // Handler to communicate with UI
    private final Handler uiHandler;

    // Bluetooth settings
    private final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private final BluetoothAdapter adapter;
    private ConnectionThread connectionThread;
    public ReaderThread readerThread;
    private String boxerName;
    private String boxerStance;
    private String boxerHand;
    private String sessionStartTime;

    /**
     * Constructor
     *
     * @param handler - Handler for sending messages to the UI
     */
    public ConnectionManager(Handler handler) {
        adapter = BluetoothAdapter.getDefaultAdapter();
        uiHandler = handler;
    }

    /**
     * Attempts to establish a connection with a bluetooth device at the given MAC Address
     *
     * @param leftDeviceId - MAC address of our desired device
     * @param appContext
     */
    public synchronized void connect(String deviceId) {

        // If we aren't already trying to connect, start a connection attempt on its own thread
        if ((connectionThread == null) && (readerThread == null)) {
            try {
                BluetoothDevice bluetoothDevice = adapter.getRemoteDevice(deviceId);
                connectionThread = new ConnectionThread(bluetoothDevice, uiHandler, uuid, adapter, this);
                connectionThread.start();
            } catch (Exception e) {
                Log.i("ConnectionManager", "Device address is not correct ===" + connectionThread);
                String infoMsg;
                if (deviceId.equals("")) {
                    infoMsg = AppConst.EMPTY_FIELD_CONNECTION_ERROR_MESSAGE;
                } else {
                    infoMsg = AppConst.INCORRECT_SENSOR_ID_MESSAGE + deviceId;
                }
                Message msg = uiHandler.obtainMessage(AppConst.MESSAGE_TOAST);
                Bundle bundle = new Bundle();
                bundle.putString("TOAST", infoMsg);
                bundle.putString("DeviceAddress", deviceId);
                bundle.putString("CONNECTION", "unsuccess");
                msg.setData(bundle);
                uiHandler.sendMessage(msg);
            }
        } else if (readerThread != null) {
            Message msg = uiHandler.obtainMessage(AppConst.MESSAGE_TOAST);
            Bundle bundle = new Bundle();
            bundle.putString(AppConst.TOAST, AppConst.ALREADY_CONNECTED_MESSAGE_TEXT);
            msg.setData(bundle);
            uiHandler.sendMessage(msg);
        }
    }

    /**
     * Method to create a deviceDataProcessingThread thread for a successful connection to a bluetooth device.
     *
     * @param bluetoothSocket
     */
    public synchronized void connected(BluetoothSocket bluetoothSocket) {

        Log.d(TAG, "Connected to socket");

         /*super commented this, connectionthread should not be null until app is closed because sensor connection has to kept
        if (connectionThread != null) {
            connectionThread = null;
        }
        */

            /* super commented this, app will create readerthread only when first training after app starts,
        for next training app will update only training info like boxer name, boxer id, training id, ...
         */
        // Dispose of currently running thread for reading data
//        disposeReaderThread();

        if (readerThread == null) {
            readerThread = new ReaderThread(bluetoothSocket, uiHandler, this);
            readerThread.start();
        }else {
            readerThread.updateTrainingInfo();
        }
    }

    public void stopWriteCSV(){
        if (readerThread != null)
            readerThread.stopWriteCSV();
    }

    /**
     * Cancels all running threads for the open connection on disconnect.
     */
    public synchronized void disconnect() {
        Log.i(TAG, "Closing connections");
        disposeConnectionThread();
        disposeReaderThread();
    }

    private void disposeReaderThread() {
        if (readerThread != null) {
            readerThread.cancel();
            readerThread = null;
        }
    }

    private void disposeConnectionThread() {
        if (connectionThread != null) {
            connectionThread.cancel();
            connectionThread = null;
        }
    }

    /**
     * Identifies the UI of the connection attempt failure.
     */
    public void connectionFailed() {
        // Send a failure message back to the Activity
        Message msg = uiHandler.obtainMessage(AppConst.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(AppConst.TOAST, AppConst.UNABLE_TO_CONNECT_WITH_SENSOR_MESSAGE_TEXT);
        msg.setData(bundle);
        uiHandler.sendMessage(msg);
    }

    /**
     * Identifies the UI of the connection being lost.
     */
    public void connectionLost() {
        // Send a failure message back to the Activity
        Message msg = uiHandler.obtainMessage(AppConst.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(AppConst.TOAST, AppConst.SENSOR_CONNECTION_LOST_MESSAGE_TEXT);
        msg.setData(bundle);
        uiHandler.sendMessage(msg);
    }

    public String getBoxerName() {
        return boxerName;
    }

    public void setBoxerName(String boxerName) {
        this.boxerName = boxerName;
    }

    public String getBoxerStance() {
        return boxerStance;
    }

    public void setBoxerStance(String boxerStance) {
        this.boxerStance = boxerStance;
    }

    public String getBoxerHand() {
        return boxerHand;
    }

    public void setSessionStartTime(String sessionStartTime){
        this.sessionStartTime = sessionStartTime;
    }

    public String getSessionStartTime(){
        return sessionStartTime;
    }

    public void setBoxerHand(String boxerHand) {
        this.boxerHand = boxerHand;
    }

    public ConnectionThread getConnectionThread() {
        return connectionThread;
    }
}