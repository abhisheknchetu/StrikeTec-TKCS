/*
 * author : #1014
 * timestamp : Jan 23, 2014 12:48:13 PM
 * filename : - PunchVFACalculator.java
 */
package com.striketec.mobile.util.sensormanager.mmaGlove;

import com.striketec.mobile.util.AppConst;
import com.striketec.mobile.util.sensormanager.bluetooth.readerBean.PunchDetectionConfig;
import com.striketec.mobile.util.sensormanager.bluetooth.readerBean.SensorAcclData;
import com.striketec.mobile.util.sensormanager.bluetooth.readerBean.SensorData;
import com.striketec.mobile.util.sensormanager.bluetooth.readerBean.VFA;
import com.striketec.mobile.util.sensormanager.bluetooth.readerBean.VFAMap;

import java.util.Stack;

public abstract class PunchVFACalculator {

    protected String VELOCITY_ESTIMATE_TYPE = "";
    protected PunchDetectionConfig punchDetectionConfig;
    protected Stack<SensorAcclData> velocityDataCollectionStack;
    protected double currentMinAy = 0;
    protected double previousMinAy;
    protected double currentMinAyTime = 0;
    protected boolean isThresholdCrossed = false;
    protected VFA vfa;
    protected VFA previousVfa;
    protected VFAMap vfaMap;
    protected int sampleCount;
    protected int nFrwdCount;
    protected ForceTableTemplate forceTableTemplate;
    protected boolean isSlapPunch = false;
    double maxAcclX = 0;
    double maxAcclZ = 0;

    public PunchVFACalculator() {
        punchDetectionConfig = PunchDetectionConfig.getInstance();
        // Instantiate only if calculating with new csv based force formula
        if (AppConst.CSV_FORCE_FORMULA.equals(punchDetectionConfig.getForceFormula())) {
            forceTableTemplate = ForceTableTemplate.getInstance();
        }
        velocityDataCollectionStack = new Stack<SensorAcclData>();
        vfa = new VFA();
        previousVfa = new VFA();
        vfaMap = new VFAMap(punchDetectionConfig.getVfaBufferSize());
        previousMinAy = 0;
        currentMinAyTime = 0;
        sampleCount = 0;
        nFrwdCount = 0;
    }

    /**
     * @return the punchDetectionConfig
     */
    public PunchDetectionConfig getPunchDetectionConfig() {
        return punchDetectionConfig;
    }

    /**
     * @param punchDetectionConfig the punchDetectionConfig to set
     */
    public void setPunchDetectionConfig(PunchDetectionConfig punchDetectionConfig) {
        this.punchDetectionConfig = punchDetectionConfig;
    }

    /**
     * @return the velocityDataCollectionStack
     */
    public Stack<SensorAcclData> getVelocityDataCollectionStack() {
        return velocityDataCollectionStack;
    }

    /**
     * It checks the threshold condition for HighG and stores data in buffer.
     *
     * @param sensorData
     */
    public abstract void estimateVelocity(SensorData sensorData, String commonLoggerPattern);

    protected void storeDataInBuffer(SensorData sensorData) {
        SensorAcclData acclData = new SensorAcclData();

        acclData.setAx((short) sensorData.getAx());
        acclData.setAy((short) sensorData.getAy());
        acclData.setAz((short) sensorData.getAz());
        acclData.setTime((sensorData.getMsgTime()));

        //restric the stack size to some arbitrary number
        if (velocityDataCollectionStack.size() >= 500) {
            velocityDataCollectionStack.removeElementAt(0);
            //LOGGER.info(commonLoggerPattern+"No velocity calculated on "+VELOCITY_ESTIMATE_TYPE+" axis even after 500 samples.");
        }
        velocityDataCollectionStack.push(acclData); // add data to FIFO


    }

    protected short[] reverseArray(short[] integratorArray) {
        for (int i = 0; i < integratorArray.length / 2; i++) {
            short temp = integratorArray[i];
            integratorArray[i] = integratorArray[integratorArray.length - i - 1];
            integratorArray[integratorArray.length - i - 1] = temp;
        }
        return integratorArray;
    }

//    protected void computeVFA(VFA vfa, double changeOfYVelocityFromEngageToDisengage, double iAy, String commonLoggerPattern, double coef, double bounceCoeff, SensorAcclData acclDataPeak) {
//        double maxAcclY = Double.valueOf("7E-3") * Math.abs(changeOfYVelocityFromEngageToDisengage);
//
//        //code for checking slap punches (if HighG X accl OR HighG Z accl > HighG Y accl
//        if ((maxAcclX > maxAcclY || maxAcclZ > maxAcclY) && (punchDetectionConfig.getHighG_nonY_punches() == 0)) {
//            Log.i("PunchVFACalculator", commonLoggerPattern + " HighG X or Z acceleration is greater than HighG Y acceleration");
//            Log.i("PunchVFACalculator", commonLoggerPattern + " Slap punch detected. maxAcclX=" + maxAcclX + " maxAcclY = " + maxAcclY + " maxAcclZ = " + maxAcclZ);
//            isSlapPunch = true;
//            return;
//        }
//
//        double mph = Double.valueOf("154E-3") * Math.abs(iAy) * coef;
//        double usingiay = iAy;
//
//        if (mph > 35)
//        {
//            usingiay = (35 / (Double.valueOf("154E-3") * coef));
//            Log.i("mph", "*** over max " + mph + " so reducing to [35]");
//
//            mph = 35;
//        }
//        else if (mph < 8) {
//            usingiay = (8 / (Double.valueOf("154E-3") * coef));
//            Log.i("mph", "*** under min " + mph + " so increasing to [8]");
//
//            mph = 8;
//        }
//        vfa.setVxyzMPH(mph);
//        vfa.setAxyz(maxAcclY);
//
//        double calculatedForceValue = 0;
//
////        calculatedForceValue = punchDetectionConfig.getPunchMassEff() * Math.abs(changeOfYVelocityFromEngageToDisengage);
//
////        vfa.setForceLSB(calculatedForceValue);
//
//        Log.i("PunchVFACalculator", "****** PME = " + punchDetectionConfig.getPunchMassEff());
//        Log.i("PunchVFACalculator", "****** Math.abs(AyMax) = "	+ Math.abs(iAy));
//        Log.i("PunchVFACalculator", "****** PreAdjusted = "	+ Double.valueOf("69E-4") * Math.abs(iAy));
//        calculatedForceValue = Math.round(punchDetectionConfig.getPunchMassEff() * Double.valueOf("69E-4") * Math.abs(usingiay));
//
//        if (calculatedForceValue > 800)
//        {
//            Log.i("force", "*** over max " + calculatedForceValue + " so reducing to [800]");
//            calculatedForceValue = 800;
//        }
//
//        vfa.setForceLSB(calculatedForceValue);
//        Log.i("PunchVFACalculator", "calculatedForceValue = " + calculatedForceValue);
//
//        Log.i("PunchVFACalculator", "calculatedForceValue = " + calculatedForceValue);
//        Log.i("PunchVFACalculator", commonLoggerPattern + "VELOCITY_ESTIMATE_TYPE = " + VELOCITY_ESTIMATE_TYPE + "----------- Velocity = " + vfa.getVxyzMPH() + " Force = " + vfa.getForceLSB() + " acc = "
//                + vfa.getAxyz());
//        Log.i("PunchVFACalculator", commonLoggerPattern + " maxAcclX=" + maxAcclX + " maxAcclY = " + maxAcclY + " maxAcclZ = " + maxAcclZ);
//
//    }

    protected void computeVFA(VFA vfa, double AyMax, double iAy, String commonLoggerPattern, double coef, double bounceCoeff,SensorAcclData acclDataPeak) {
        //Double cv=1+(Math.pow(((0.007 * AyMax) / punchDetectionConfig.getHighG_dblA()),2));
        //Double cv=1+((Double.valueOf("49E-6") * Math.pow(AyMax, 2)) / Math.pow(punchDetectionConfig.getHighG_dblA(),2));
        //vfa.setVxyzMPH(punchDetectionConfig.getMaxVelocity()*(1-Math.exp(-cv*(Double.valueOf("154E-3") * Math.abs(iAy)*coef)/punchDetectionConfig.getMaxVelocity()))); //New formula
        //vfa.setVxyzMPH(cv*(Double.valueOf("154E-3") * Math.abs(iAy)*coef)); //Old formula that crossing max & min velocity

        double acclX=Double.valueOf("7E-3") * Math.abs(acclDataPeak.getAx());
        double acclZ=Double.valueOf("7E-3") * Math.abs(acclDataPeak.getAz());
        double acclY=Double.valueOf("7E-3") * Math.abs(acclDataPeak.getAy());

        //code for checking slap punches (if HighG X accl OR HighG Z accl > HighG Y accl
        if((acclX>acclY || acclZ>acclY)&& (punchDetectionConfig.getHighG_nonY_punches()==0) ){
            isSlapPunch=true;
            return;
        }

        vfa.setVxyzMPH((Double.valueOf("154E-3") * Math.abs(iAy)*coef));
        vfa.setAxyz(Double.valueOf("7E-3") * Math.abs(AyMax));

        double calculatedForceValue = 0;

        // For Old Formula
        if (AppConst.OLD_FORCE_FORMULA.equals(punchDetectionConfig.getForceFormula())) {
            calculatedForceValue = punchDetectionConfig.getPunchMassEff()*Math.round(Double.valueOf("69E-4") * Math.abs(AyMax));
        } else if (AppConst.CSV_FORCE_FORMULA.equals(punchDetectionConfig.getForceFormula())) {	// For New Formula
            double V0_1; // V0.1
            double Vf; // VxyzAvg
            double V0_1Half; // Value after multiplying 0.5 * V0.1

            V0_1 = Double.valueOf("154E-3") * Math.abs(iAy) * bounceCoeff;
            V0_1Half = 0.5 * V0_1;
            Vf = (vfa.getVxyzMPH() < V0_1) ? V0_1Half : vfa.getVxyzMPH() - V0_1Half; //Formula :- vf = vm - 0.5*V0_1

            // Calculate the corresponding value from the table
            int index_row_velocity,index_column_acceleration;

            index_row_velocity = computeIndex(Vf, forceTableTemplate.getRowMinValue(), forceTableTemplate.getRowStep());
            index_column_acceleration = computeIndex(vfa.getAxyz(), forceTableTemplate.getColumnMinValue(), forceTableTemplate.getColumnStep());

            calculatedForceValue = Double.parseDouble(String.valueOf(forceTableTemplate.getForceValue(index_row_velocity, index_column_acceleration)));
        }
//		vfa.setForceLSB(oldForceLSB);
        vfa.setForceLSB(calculatedForceValue);

    }


    private int computeIndex(double value, int min_value, int step) {
        // index=(value-min. value+step/2) div step +1
        return ((int) (((double) (value - min_value) + (double) (step / 2)) / step) + 1);
    }

    protected boolean isValueBeyondThreshold(short threshold, short value) {
        return (threshold >= 0) ? value > threshold : value < threshold;
    }

    public double getComputedVelocity() {
        return vfa.getVxyzMPH();
    }

    public double getComputedForce() {
        return vfa.getForceLSB();
    }

    public double getAxyz() {
        return vfa.getAxyz();
    }

    public void resetVFA() {
        vfa.resetVFA();
    }


    /**
     * @return the previousVfa
     */
    public VFA getPreviousVfa() {
        return previousVfa;
    }

    /**
     * @param previousVfa the previousVfa to set
     */
    public void setPreviousVfa(VFA previousVfa) {
        this.previousVfa = previousVfa;
    }

    /**
     * @return the sampleCount
     */
    public int getSampleCount() {
        return sampleCount;
    }

    /**
     * @param sampleCount the sampleCount to set
     */
    public void setSampleCount(int sampleCount) {
        this.sampleCount = sampleCount;
    }

    public void resetBufferAfterPunchFound(String commonLoggerPattern, double punchTime) {
        this.previousMinAy = 0;
        this.currentMinAyTime = 0;
        velocityDataCollectionStack.removeAllElements();
        sampleCount = 0;
        previousVfa.resetVFA();
        isSlapPunch = false;
        maxAcclX = 0;
        maxAcclZ = 0;
        this.currentMinAy = 0;            //Change by 1014:- reset currentMinAy.
        isThresholdCrossed = false;        //Change by 1014:- reset isThresholdCrossed.
        vfa.resetVFA();                    //Change by 1014:- reset vfa.
        vfaMap.removeContentBeforeTime(punchTime + punchDetectionConfig.getVfaBufferSize());
        nFrwdCount = 0;                    //Change by 1014:- reset nFrwdCount.
    }

    public boolean isSlapPunch() {
        return isSlapPunch;
    }

    public void setSlapPunch(boolean isSlapPunch) {
        this.isSlapPunch = isSlapPunch;
    }

    public VFAMap getVfaMap() {
        return vfaMap;
    }

    public void setVfaMap(VFAMap vfaMap) {
        this.vfaMap = vfaMap;
    }
}
