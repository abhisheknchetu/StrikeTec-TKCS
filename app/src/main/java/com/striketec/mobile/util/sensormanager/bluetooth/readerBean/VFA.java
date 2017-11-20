package com.striketec.mobile.util.sensormanager.bluetooth.readerBean;

import java.util.Comparator;

public class VFA implements Comparable<VFA> {
    private double Axyz;
    private double VxyzMPH;
    private double forceLSB;

    public VFA() {
        this.Axyz = 0;
        this.VxyzMPH = 0;
        this.forceLSB = 0;
    }

    public void resetVFA() {
        this.Axyz = 0;
        this.VxyzMPH = 0;
        this.forceLSB = 0;
    }


    public VFA(double axyz, double vxyzMPH, double forceLSB) {
        super();
        Axyz = axyz;
        VxyzMPH = vxyzMPH;
        this.forceLSB = forceLSB;
    }

    /**
     * @return the axyz
     */
    public double getAxyz() {
        return Axyz;
    }

    /**
     * @param axyz the axyz to set
     */
    public void setAxyz(double axyz) {
        Axyz = axyz;
    }

    /**
     * @return the vxyzMPH
     */
    public double getVxyzMPH() {
        return VxyzMPH;
    }

    /**
     * @param vxyzMPH the vxyzMPH to set
     */
    public void setVxyzMPH(double vxyzMPH) {
        VxyzMPH = vxyzMPH;
    }

    /**
     * @return the forceLSB
     */
    public double getForceLSB() {
        return forceLSB;
    }

    /**
     * @param forceLSB the forceLSB to set
     */
    public void setForceLSB(double forceLSB) {
        this.forceLSB = forceLSB;
    }


    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "VFA [Axyz=" + Axyz + ", VxyzMPH=" + VxyzMPH + ", forceLSB=" + forceLSB + "]";
    }

    @Override
    public int compareTo(VFA compareVFA) {
        double compareVelocity = ((VFA) compareVFA).getVxyzMPH();
        Double obj1 = new Double(this.VxyzMPH);
        Double obj2 = new Double(compareVelocity);
        return obj1.compareTo(obj2);
    }

    public static Comparator<VFA> VFAVelocityComparator = new Comparator<VFA>() {

        public int compare(VFA vfa2, VFA vfa1) {
            return (int) vfa2.compareTo(vfa1);
        }

    };


}
