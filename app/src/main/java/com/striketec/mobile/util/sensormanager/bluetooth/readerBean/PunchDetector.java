package com.striketec.mobile.util.sensormanager.bluetooth.readerBean;

public class PunchDetector {
    boolean isUpperCut;
    boolean isHook;
    boolean isJab;
    boolean isStraight;
    boolean isUnrecognized;
    double punchDetectedTime;

    public PunchDetector() {
        super();
        this.isUpperCut = false;
        this.isHook = false;
        this.isJab = false;
        this.isStraight = false;
        this.isUnrecognized = false;
        this.punchDetectedTime = 0;
    }

    public void resetPunchDetector() {
        this.isUpperCut = false;
        this.isHook = false;
        this.isJab = false;
        this.isStraight = false;
        this.isUnrecognized = false;
        this.punchDetectedTime = 0;
    }

    public PunchDetector(boolean isUpperCut, boolean isHook, boolean isJab, boolean isStraight, boolean isUnrecognized, double punchDetectedTime) {
        super();
        this.isUpperCut = isUpperCut;
        this.isHook = isHook;
        this.isJab = isJab;
        this.isStraight = isStraight;
        this.isUnrecognized = isUnrecognized;
        this.punchDetectedTime = punchDetectedTime;
    }

    /**
     * @return the isUpperCut
     */
    public boolean isUpperCut() {
        return isUpperCut;
    }


    /**
     * @param isUpperCut the isUpperCut to set
     */
    public void setUpperCut(boolean isUpperCut) {
        this.isUpperCut = isUpperCut;
    }


    /**
     * @return the isHook
     */
    public boolean isHook() {
        return isHook;
    }


    /**
     * @param isHook the isHook to set
     */
    public void setHook(boolean isHook) {
        this.isHook = isHook;
    }


    /**
     * @return the isJab
     */
    public boolean isJab() {
        return isJab;
    }


    /**
     * @param isJab the isJab to set
     */
    public void setJab(boolean isJab) {
        this.isJab = isJab;
    }


    public boolean isStraight() {
        return isStraight;
    }


    public void setStraight(boolean isStraight) {
        this.isStraight = isStraight;
    }


    public boolean isUnrecognized() {
        return isUnrecognized;
    }

    public void setUnrecognized(boolean isUnrecognized) {
        this.isUnrecognized = isUnrecognized;
    }

    public double getPunchDetectedTime() {
        return punchDetectedTime;
    }

    public void setPunchDetectedTime(double punchDetectedTime) {
        this.punchDetectedTime = punchDetectedTime;
    }

    /**
     * (non-Javadoc)
     *
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return "PunchDetector [isUpperCut=" + isUpperCut + ", isHook=" + isHook + ", isJab=" + isJab + ", isStraight=" + isStraight
                + ", isUnrecognized=" + isUnrecognized + ", punchDetectedTime=" + punchDetectedTime + "]";
    }

}
