package com.striketec.mobile.util.sensormanager.bluetooth.readerBean;

public class SensorData {
    double ax;
    double ay;
    double az;
    double prevAx;
    double prevAy;
    double prevAz;
    short gx;
    short gy;
    short gz;
    short temperature;
    double msgTime;
    double prevTime;
    int punchStartTime;
    int punchFinishTime;
    int maxUpperCutValueTime;
    double summation;
    boolean testPunchForTimeInterval;
    boolean isIncreasePunchEndTime;
    int punchEndTime;
    String hand;


    public SensorData() {
        super();
        this.ax = 0;
        this.ay = 0;
        this.az = 0;
        this.prevAx = 0;
        this.prevAy = 0;
        this.prevAz = 0;
        this.gx = 0;
        this.gy = 0;
        this.gz = 0;
        this.temperature = 0;
        this.msgTime = 0;
        this.prevTime = 0;
        this.punchStartTime = 0;
        this.punchFinishTime = 0;
        this.maxUpperCutValueTime = 0;
        this.summation = 0;
        this.testPunchForTimeInterval = false;
        this.isIncreasePunchEndTime = true;
        this.punchEndTime = 0;
        this.hand = "";
    }


    public SensorData(double ax, double ay, double az,
                      double prevAx, double prevAy, double prevAz, short gx, short gy,
                      short gz, short temperature, int currentTime, int prevTime,
                      int punchStartTime, int punchFinishTime, int maxUpperCutValueTime,
                      double summation, boolean testPunchForTimeInterval,
                      boolean isIncreasePunchEndTime, int punchEndTime, String hand) {
        super();
        this.ax = ax;
        this.ay = ay;
        this.az = az;
        this.prevAx = prevAx;
        this.prevAy = prevAy;
        this.prevAz = prevAz;
        this.gx = gx;
        this.gy = gy;
        this.gz = gz;
        this.temperature = temperature;
        this.msgTime = currentTime;
        this.prevTime = prevTime;
        this.punchStartTime = punchStartTime;
        this.punchFinishTime = punchFinishTime;
        this.maxUpperCutValueTime = maxUpperCutValueTime;
        this.summation = summation;
        this.testPunchForTimeInterval = testPunchForTimeInterval;
        this.isIncreasePunchEndTime = isIncreasePunchEndTime;
        this.punchEndTime = punchEndTime;
        this.hand = hand;
    }


    /**
     * @return the ax
     */
    public double getAx() {
        return ax;
    }

    /**
     * @param ax the ax to set
     */
    public void setAx(double ax) {
        this.ax = ax;
    }

    /**
     * @return the ay
     */
    public double getAy() {
        return ay;
    }

    /**
     * @param ay the ay to set
     */
    public void setAy(double ay) {
        this.ay = ay;
    }

    /**
     * @return the az
     */
    public double getAz() {
        return az;
    }

    /**
     * @param az the az to set
     */
    public void setAz(double az) {
        this.az = az;
    }


    /**
     * @return the prevAx
     */
    public double getPrevAx() {
        return prevAx;
    }


    /**
     * @param prevAx the prevAx to set
     */
    public void setPrevAx(double prevAx) {
        this.prevAx = prevAx;
    }

    /**
     * @return the prevAy
     */
    public double getPrevAy() {
        return prevAy;
    }

    /**
     * @param prevAy the prevAy to set
     */
    public void setPrevAy(double prevAy) {
        this.prevAy = prevAy;
    }

    /**
     * @return the prevAz
     */
    public double getPrevAz() {
        return prevAz;
    }

    /**
     * @param prevAz the prevAz to set
     */
    public void setPrevAz(double prevAz) {
        this.prevAz = prevAz;
    }

    /**
     * @return the gx
     */
    public short getGx() {
        return gx;
    }

    /**
     * @param gx the gx to set
     */
    public void setGx(short gx) {
        this.gx = gx;
    }

    /**
     * @return the gy
     */
    public short getGy() {
        return gy;
    }

    /**
     * @param gy the gy to set
     */
    public void setGy(short gy) {
        this.gy = gy;
    }

    /**
     * @return the temperature
     */
    public short getTemperature() {
        return temperature;
    }

    /**
     * @param temperature the temperature to set
     */
    public void setTemperature(short temperature) {
        this.temperature = temperature;
    }

    /**
     * @return the gz
     */
    public short getGz() {
        return gz;
    }

    /**
     * @param gz the gz to set
     */
    public void setGz(short gz) {
        this.gz = gz;
    }

    /**
     * @return the currentTime
     */
    public double getMsgTime() {
        return msgTime;
    }

    /**
     * @param currentTime the currentTime to set
     */
    public void setMsgTime(double msgTime) {
        this.msgTime = msgTime;
    }

    /**
     * @return the prevTime
     */
    public double getPrevTime() {
        return prevTime;
    }

    /**
     * @param prevTime the prevTime to set
     */
    public void setPrevTime(double prevTime) {
        this.prevTime = prevTime;
    }

    /**
     * @return the punchStartTime
     */
    public int getPunchStartTime() {
        return punchStartTime;
    }

    /**
     * @param punchStartTime the punchStartTime to set
     */
    public void setPunchStartTime(int punchStartTime) {
        this.punchStartTime = punchStartTime;
    }

    /**
     * @return the punchFinishTime
     */
    public int getPunchFinishTime() {
        return punchFinishTime;
    }

    /**
     * @param punchFinishTime the punchFinishTime to set
     */
    public void setPunchFinishTime(int punchFinishTime) {
        this.punchFinishTime = punchFinishTime;
    }

    /**
     * @return the maxUpperCutValueTime
     */
    public int getMaxUpperCutValueTime() {
        return maxUpperCutValueTime;
    }

    /**
     * @param maxUpperCutValueTime the maxUpperCutValueTime to set
     */
    public void setMaxUpperCutValueTime(int maxUpperCutValueTime) {
        this.maxUpperCutValueTime = maxUpperCutValueTime;
    }

    /**
     * @return the summation
     */
    public double getSummation() {
        return summation;
    }

    /**
     * @param summation the summation to set
     */
    public void setSummation(double summation) {
        this.summation = summation;
    }

    /**
     * @return the testPunchForTimeInterval
     */
    public boolean isTestPunchForTimeInterval() {
        return testPunchForTimeInterval;
    }

    /**
     * @param testPunchForTimeInterval the testPunchForTimeInterval to set
     */
    public void setTestPunchForTimeInterval(boolean testPunchForTimeInterval) {
        this.testPunchForTimeInterval = testPunchForTimeInterval;
    }

    /**
     * @return the isIncreasePunchEndTime
     */
    public boolean isIncreasePunchEndTime() {
        return isIncreasePunchEndTime;
    }

    /**
     * @param isIncreasePunchEndTime the isIncreasePunchEndTime to set
     */
    public void setIncreasePunchEndTime(boolean isIncreasePunchEndTime) {
        this.isIncreasePunchEndTime = isIncreasePunchEndTime;
    }

    /**
     * @return the punchEndTime
     */
    public int getPunchEndTime() {
        return punchEndTime;
    }

    /**
     * @param punchEndTime the punchEndTime to set
     */
    public void setPunchEndTime(int punchEndTime) {
        this.punchEndTime = punchEndTime;
    }

    /**
     * @return the hand
     */
    public String getHand() {
        return hand;
    }

    /**
     * @param hand the hand to set
     */
    public void setHand(String hand) {
        this.hand = hand;
    }


    public void resetSensorData() {
        this.ax = 0;
        this.ay = 0;
        this.az = 0;
        this.prevAx = 0;
        this.prevAy = 0;
        this.prevAz = 0;
        this.gx = 0;
        this.gy = 0;
        this.gz = 0;
        this.temperature = 0;
        this.msgTime = 0;
        this.prevTime = 0;
        this.punchStartTime = 0;
        this.punchFinishTime = 0;
        this.maxUpperCutValueTime = 0;
        this.summation = 0;
        this.testPunchForTimeInterval = false;
        this.isIncreasePunchEndTime = true;
        this.punchEndTime = 0;
        this.hand = "";
    }


    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "SensorData [ax=" + ax + ", ay=" + ay + ", az=" + az
                + ", prevAx=" + prevAx + ", prevAy=" + prevAy + ", prevAz="
                + prevAz + ", gx=" + gx + ", gy=" + gy + ", gz=" + gz
                + ", temperature=" + temperature + ", currentTime="
                + msgTime + ", prevTime=" + prevTime + ", punchStartTime="
                + punchStartTime + ", punchFinishTime=" + punchFinishTime
                + ", maxUpperCutValueTime=" + maxUpperCutValueTime
                + ", summation=" + summation + ", testPunchForTimeInterval="
                + testPunchForTimeInterval + ", isIncreasePunchEndTime="
                + isIncreasePunchEndTime + ", punchEndTime=" + punchEndTime
                + ", hand=" + hand + "]";
    }


}
