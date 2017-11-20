package com.striketec.mobile.dto;

import android.bluetooth.BluetoothDevice;

/**
 * Created by Qiang on 8/20/2017.
 */
public class SensorDTO {

    public BluetoothDevice bluetoothDevice;
    public boolean isSet;
    public boolean isHand;
    public boolean isLeft;

    public SensorDTO(BluetoothDevice bluetoothDevice, boolean isSet, boolean isHand, boolean isLeft) {
        this.bluetoothDevice = bluetoothDevice;
        this.isSet = isSet;
        this.isHand = isHand;
        this.isLeft = isLeft;
    }
}
