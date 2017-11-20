package com.striketec.mobile.util.sensormanager.bluetooth.readerBean;

import android.util.Log;

public class GloveDeviceData {
    private double deltaT;
    private double Axyz;
    private double angleGX;
    private double angleGY;
    private double angleGZ;
    private double aiXo;
    private double aiYo;
    private double aiZo;
    private double Vx0;
    private double Vy0;
    private double Vz0;
    private double Vxyz;
    private double force;
    private double VxyzMPH;
    private double forceLSB;
    private double headTrauma;

    public GloveDeviceData() {
    }

    public GloveDeviceData(double deltaT, double axyz, double angleGX,
                           double angleGY, double angleGZ, double aiXo, double aiYo,
                           double aiZo, double vx0, double vy0, double vz0, double vxyz,
                           double force, double vxyzMPH, double forceLSB, double headTrauma) {
        super();
        this.deltaT = deltaT;
        this.Axyz = axyz;
        this.angleGX = angleGX;
        this.angleGY = angleGY;
        this.angleGZ = angleGZ;
        this.aiXo = aiXo;
        this.aiYo = aiYo;
        this.aiZo = aiZo;
        this.Vx0 = vx0;
        this.Vy0 = vy0;
        this.Vz0 = vz0;
        this.Vxyz = vxyz;
        this.force = force;
        this.VxyzMPH = vxyzMPH;
        this.forceLSB = forceLSB;
        this.headTrauma = headTrauma;
    }

    /**
     * @return the deltaT
     */

    public double getDeltaT() {
        return deltaT;
    }

    /**
     * @param deltaT the deltaT to set
     */
    public void setDeltaT(double deltaT) {
        this.deltaT = deltaT;
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
     * @return the angleGX
     */
    public double getAngleGX() {
        return angleGX;
    }

    /**
     * @param angleGX the angleGX to set
     */
    public void setAngleGX(double angleGX) {
        this.angleGX = angleGX;
    }

    /**
     * @return the angleGY
     */
    public double getAngleGY() {
        return angleGY;
    }

    /**
     * @param angleGY the angleGY to set
     */
    public void setAngleGY(double angleGY) {
        this.angleGY = angleGY;
    }

    /**
     * @return the angleGZ
     */
    public double getAngleGZ() {
        return angleGZ;
    }

    /**
     * @param angleGZ the angleGZ to set
     */
    public void setAngleGZ(double angleGZ) {
        this.angleGZ = angleGZ;
    }

    /**
     * @return the aiXo
     */
    public double getAiXo() {
        return aiXo;
    }

    /**
     * @param aiXo the aiXo to set
     */
    public void setAiXo(double aiXo) {
        this.aiXo = aiXo;
    }

    /**
     * @return the aiYo
     */
    public double getAiYo() {
        return aiYo;
    }

    /**
     * @param aiYo the aiYo to set
     */
    public void setAiYo(double aiYo) {
        this.aiYo = aiYo;
    }

    /**
     * @return the aiZo
     */
    public double getAiZo() {
        return aiZo;
    }

    /**
     * @param aiZo the aiZo to set
     */
    public void setAiZo(double aiZo) {
        this.aiZo = aiZo;
    }

    /**
     * @return the vx0
     */
    public double getVx0() {
        return Vx0;
    }

    /**
     * @param vx0 the vx0 to set
     */
    public void setVx0(double vx0) {
        Vx0 = vx0;
    }

    /**
     * @return the vy0
     */
    public double getVy0() {
        return Vy0;
    }

    /**
     * @param vy0 the vy0 to set
     */
    public void setVy0(double vy0) {
        Vy0 = vy0;
    }

    /**
     * @return the vz0
     */
    public double getVz0() {
        return Vz0;
    }

    /**
     * @param vz0 the vz0 to set
     */
    public void setVz0(double vz0) {
        Vz0 = vz0;
    }

    /**
     * @return the vxyz
     */
    public double getVxyz() {
        return Vxyz;
    }

    /**
     * @param vxyz the vxyz to set
     */
    public void setVxyz(double vxyz) {
        Vxyz = vxyz;
    }

    /**
     * @return the force
     */
    public double getForce() {
        return force;
    }

    /**
     * @param force the force to set
     */
    public void setForce(double force) {
        this.force = force;
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

    /**
     * @return the headTrauma
     */
    public double getHeadTrauma() {
        return headTrauma;
    }

    /**
     * @param headTrauma the headTrauma to set
     */
    public void setHeadTrauma(double headTrauma) {
        this.headTrauma = headTrauma;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "GloveDeviceData [deltaT=" + deltaT + ", Axyz=" + Axyz
                + ", angleGX=" + angleGX + ", angleGY=" + angleGY
                + ", angleGZ=" + angleGZ + ", aiXo=" + aiXo + ", aiYo=" + aiYo
                + ", aiZo=" + aiZo + ", Vx0=" + Vx0 + ", Vy0=" + Vy0 + ", Vz0="
                + Vz0 + ", Vxyz=" + Vxyz + ", force=" + force + ", VxyzMPH="
                + VxyzMPH + ", forceLSB=" + forceLSB + ", headTrauma="
                + headTrauma + "]";
    }

    public static void main(String[] args) {
        Log.d("GloveDeviceData", "GloveDeviceData = " + new GloveDeviceData());
    }

    /**
     * Reset all data member of this class except HeadTrauma.
     */
    public void resetAllValues() {
        this.deltaT = 0;
        this.Axyz = 0;
        this.angleGX = 0;
        this.angleGY = 0;
        this.angleGZ = 0;
        this.aiXo = 0;
        this.aiYo = 0;
        this.aiZo = 0;
        this.Vx0 = 0;
        this.Vy0 = 0;
        this.Vz0 = 0;
        this.Vxyz = 0;
        this.force = 0;
        this.VxyzMPH = 0;
        this.forceLSB = 0;
        //this.headTrauma = 0;
    }

}
