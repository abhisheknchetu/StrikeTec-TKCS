package com.striketec.mobile.util.sensormanager.bluetooth.readerBean;

public class SensorAcclData {
    short ax;
    short ay;
    short az;
    double time;

    public SensorAcclData() {
        super();
    }

    public SensorAcclData(short ax, short ay, short az, double time) {
        super();
        this.ax = ax;
        this.ay = ay;
        this.az = az;
        this.time = time;
    }

    public short getAx() {
        return ax;
    }

    public void setAx(short ax) {
        this.ax = ax;
    }

    public short getAy() {
        return ay;
    }

    public void setAy(short ay) {
        this.ay = ay;
    }

    public short getAz() {
        return az;
    }

    public void setAz(short az) {
        this.az = az;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "SensorAcclData [ax=" + ax + ", ay=" + ay + ", az=" + az
                + ", time=" + time + "]";
    }


}
