package com.striketec.mobile.util.sensormanager.mmaGlove;

import com.striketec.mobile.util.sensormanager.bluetooth.readerBean.SensorAcclData;
import com.striketec.mobile.util.sensormanager.bluetooth.readerBean.SensorData;
import com.striketec.mobile.util.sensormanager.bluetooth.readerBean.VFA;

/**
 * It calculates velocity, force & acceleration On YYAxis.
 */
public class PunchVFACalculatorOnYYAxis extends PunchVFACalculator {


    public PunchVFACalculatorOnYYAxis() {
        this.VELOCITY_ESTIMATE_TYPE = "YY";

    }

    /**
     * It checks the threshold condition for HighG and stores data in buffer.
     *
     * @param sensorData
     * @param vfa
     * @param velocityEstimationType
     */
    public void estimateVelocity(SensorData sensorData, String commonLoggerPattern) {

        if (sensorData.getAy() < 0) {

            if (sensorData.getAy() < punchDetectionConfig.getHighGAyThr()) {
                storeDataInBuffer(sensorData); //store data between 0 and minimum
                isThresholdCrossed = true;
                if (currentMinAy > sensorData.getAy()) {
                    currentMinAy = sensorData.getAy();
                    currentMinAyTime = sensorData.getMsgTime();
                    nFrwdCount = 0;
                } else {
                    nFrwdCount++;
                }
            } else if (sensorData.getAy() > punchDetectionConfig.getHighGAyThr()) {
                storeDataInBuffer(sensorData);
                if (isThresholdCrossed) {
                    //prepareIntegratorBuffer(vfa,commonLoggerPattern);
                    nFrwdCount++;
                }
            }
            if (nFrwdCount >= punchDetectionConfig.getHighG_Nfrwd()) {
                prepareIntegratorBuffer(vfa, commonLoggerPattern);
            }
        } else if (sensorData.getAy() >= 0 && isThresholdCrossed) {
            // value jumps positive directly after crossing the threshold
            prepareIntegratorBuffer(vfa, commonLoggerPattern);
        }
        if (sensorData.getAy() >= 0 /*&& !isThresholdCrossed*/) {//Change by 1014 :- When ever AY went greater than 0 then we have to reset all (isThresholdCrossed,previousMinAy,currentMinAy) this fields.
            // sometimes ay goes back positive without crossing threshold, no
            // punch so do not calculate velocity
            // reset buffer
            //LOGGER.info("Inside remove all-----------------"+velocityDataCollectionStack);
            velocityDataCollectionStack.removeAll(velocityDataCollectionStack);
            isThresholdCrossed = false;    //Change by 1014:- reset isThresholdCrossed, when ay get greater than 0.
            previousMinAy = 0;            //Change by 1014:- reset previousMinAy, when ay get greater than 0.
            currentMinAy = 0;                //Change by 1014:- reset currentMinAy, when ay get greater than 0.
        }
    }

    /**
     * We process HighG data thats present in buffer and calculate Velocity, force, Acceleration for that punch on YY Axis.
     *
     * @param vfa
     * @param commonLoggerPattern
     */
    private void prepareIntegratorBuffer(VFA vfa, String commonLoggerPattern) {

        if (this.previousMinAy > this.currentMinAy) {
            this.previousMinAy = this.currentMinAy;

        } else {
            return;
        }


//		Log.i("PunchVFACalculatorOnYYAxis",commonLoggerPattern+"minAy found = " + this.currentMinAy+" currentMinAy="+this.currentMinAy);
        double iAy;

        short tempArray[] = new short[punchDetectionConfig.getHighGNback()];
        short tempFrwdArray[] = new short[punchDetectionConfig.getHighG_Nfrwd()];
        int frwdIndex = 0;
        int index = 0;
        int stackIndex = 0;
        if (!velocityDataCollectionStack.empty()) {
            stackIndex = velocityDataCollectionStack.size();
        }

        boolean isStartStoring = false;
        SensorAcclData acclDataPeak=new SensorAcclData();
        while (stackIndex > 0) {
            //we only need a certain number of data points before minimum
            if (index > punchDetectionConfig.getHighGNback() - 1) {
                break;
            }
            SensorAcclData acclData = velocityDataCollectionStack.get(--stackIndex);//velocityDataCollectionStack.pop();
            //Note:- This code is for testing the slap punches. So, maintaining maxAcclX & maxAcclZ for entire graph
            double acclX = Double.valueOf("7E-3") * Math.abs(acclData.getAx());
            double acclZ = Double.valueOf("7E-3") * Math.abs(acclData.getAz());
            if (maxAcclX < acclX) maxAcclX = acclX;
            if (maxAcclZ < acclZ) maxAcclZ = acclZ;

            if (!isStartStoring) {
                if (acclData.getAy() <= this.currentMinAy) {
                    isStartStoring = true;
                    tempArray[index] = acclData.getAy();
                    acclDataPeak = acclData;
                    index++;
                } else {
                    if (frwdIndex <= punchDetectionConfig.getHighG_Nfrwd() && (acclData.getAy() <= (1 - punchDetectionConfig.getHighG_YY_bounce_coeff()) * this.currentMinAy)) {
                        tempFrwdArray[frwdIndex] = acclData.getAy();
                        frwdIndex++;
                    }
                }
            } else {
                tempArray[index] = acclData.getAy();
                index++;
            }
        }


        short integratorArray[] = new short[index];
        short integratorFrwdArray[] = new short[frwdIndex];
        short finalIntegratorArray[] = new short[index + frwdIndex];

        System.arraycopy(tempArray, 0, integratorArray, 0, index);
        integratorArray = reverseArray(integratorArray);

        System.arraycopy(tempFrwdArray, 0, integratorFrwdArray, 0, frwdIndex);
        integratorFrwdArray = reverseArray(integratorFrwdArray);

        System.arraycopy(integratorArray, 0, finalIntegratorArray, 0, index);
        System.arraycopy(integratorFrwdArray, 0, finalIntegratorArray, index, frwdIndex);

//		Log.i("PunchVFACalculatorOnYYAxis",commonLoggerPattern+"integratorArray on " + this.VELOCITY_ESTIMATE_TYPE + " axis= " + Arrays.toString(integratorArray));
//		Log.i("PunchVFACalculatorOnYYAxis",commonLoggerPattern+"integratorFrwdArray on " + this.VELOCITY_ESTIMATE_TYPE + " axis= " + Arrays.toString(integratorFrwdArray));
//		Log.i("PunchVFACalculatorOnYYAxis",commonLoggerPattern+"finalIntegratorArray on " + this.VELOCITY_ESTIMATE_TYPE + " axis= " + Arrays.toString(finalIntegratorArray));


        // checking for Integrator class exception
        if (finalIntegratorArray.length >= 2) {
            Integrator integrator = new Integrator();
            iAy = integrator.computeIntegral(finalIntegratorArray, 0.001); // check
            // iAy
            // for
            // exception
//			Log.i("PunchVFACalculatorOnYYAxis",commonLoggerPattern+"OnYYAxis, currentMinAyTime = "+currentMinAyTime);
            this.computeVFA(vfa, this.currentMinAy, iAy, commonLoggerPattern, punchDetectionConfig.getHighGYyCoef(), punchDetectionConfig.getHighG_YY_bounce_coeff(), acclDataPeak);
            vfaMap.put(currentMinAyTime, new VFA(vfa.getAxyz(), vfa.getVxyzMPH(), vfa.getForceLSB()));
        } else {
//			Log.i("PunchVFACalculatorOnYYAxis",commonLoggerPattern+"OnYYAxis can't determine velocity for intgrator array length less than zero...");
        }
        isThresholdCrossed = false;

        this.currentMinAy = 0;
    }


}
