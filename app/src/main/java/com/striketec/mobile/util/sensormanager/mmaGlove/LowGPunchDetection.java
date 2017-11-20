package com.striketec.mobile.util.sensormanager.mmaGlove;

import android.util.Log;

import com.striketec.mobile.util.AppConst;
import com.striketec.mobile.util.sensormanager.bluetooth.readerBean.PunchDetectedMap;
import com.striketec.mobile.util.sensormanager.bluetooth.readerBean.PunchDetectionConfig;
import com.striketec.mobile.util.sensormanager.bluetooth.readerBean.PunchDetectionTemplate;
import com.striketec.mobile.util.sensormanager.bluetooth.readerBean.PunchDetector;
import com.striketec.mobile.util.sensormanager.bluetooth.readerBean.SensorAcclData;
import com.striketec.mobile.util.sensormanager.bluetooth.readerBean.SensorData;

import org.apache.commons.collections.Buffer;
import org.apache.commons.collections.BufferOverflowException;
import org.apache.commons.collections.BufferUtils;
import org.apache.commons.collections.buffer.BoundedFifoBuffer;

import java.util.Arrays;

/**
 * It processes LowG data & detect punches according to Algorithm.
 */
public class LowGPunchDetection {

    private PunchDetectionTemplate punchDetectionTemplate;
    private PunchDetectionConfig punchDetectionConfig;
    private boolean isZeroCrossedForXAxis = false;
    private boolean isThresholdCrossedForXAxis = false;
    private boolean isZeroCrossedForZAxis = false;
    private boolean isThresholdCrossedForZAxis = false;
    private PunchDetectedMap punchDetectedMap;

    Buffer punchDataCollectionFifoForXAxis;
    Buffer punchDataCollectionFifoForZAxis;
    private double startDetectingNewPunchTime;

    private double upperCutCurrentMinAx;
    private double upperCutCurrentMinAz;
    private boolean isThresholdBackCrossed;
    private boolean isAzCrossedLowGZImpThr;
    private boolean isCheckUpperCut;


    private int counter = 0;

    public LowGPunchDetection() {

        punchDetectionConfig = PunchDetectionConfig.getInstance();

        this.startDetectingNewPunchTime = 0;

        punchDetectionTemplate = new PunchDetectionTemplate();
        punchDetectedMap = new PunchDetectedMap(punchDetectionConfig.getVfaBufferSize());
        punchDataCollectionFifoForXAxis = BufferUtils.synchronizedBuffer(new BoundedFifoBuffer(punchDetectionConfig.getNXback() + 1));
        punchDataCollectionFifoForZAxis = BufferUtils.synchronizedBuffer(new BoundedFifoBuffer(punchDetectionConfig.getNZback() + 1));

        this.upperCutCurrentMinAx = 0;
        this.upperCutCurrentMinAz = 0;
        this.isThresholdBackCrossed = false;
        this.isAzCrossedLowGZImpThr = false;
        this.isCheckUpperCut = false;
    }

    public void detectPunchType(SensorData sensorData, PunchDetector punchDetector, String stance, String commonLoggerPattern) {
        if (this.startDetectingNewPunchTime != 0 && sensorData.getMsgTime() < this.startDetectingNewPunchTime) {
            if (counter == 0) {
//				Log.i("LowGPunchDetection ",commonLoggerPattern+"returning <<-----  startDetectingNewPunchTime = "+startDetectingNewPunchTime);
                counter++;
            }
            return;
        }
        counter = 0;
        this.startDetectingNewPunchTime = 0;
        detectPunchOnXAxis(sensorData, punchDetector, stance, commonLoggerPattern);
        detectPunchOnZAxis(sensorData, punchDetector, stance, commonLoggerPattern);
    }

    /**
     * It detect Punch On XAxis.
     **/
    @SuppressWarnings("unchecked")
    public void detectPunchOnXAxis(SensorData sensorData, PunchDetector punchDetector, String stance, String commonLoggerPattern) {

        if (sensorData.getAx() < 0) {

            SensorAcclData sensorAcclData = new SensorAcclData();
            sensorAcclData.setAx((short) sensorData.getAx());
            sensorAcclData.setAy((short) sensorData.getAy());
            sensorAcclData.setAz((short) sensorData.getAz());

            try {
                punchDataCollectionFifoForXAxis.add(sensorAcclData); // add data to fifo
            } catch (BufferOverflowException e) {
                punchDataCollectionFifoForXAxis.remove();
                punchDataCollectionFifoForXAxis.add(sensorAcclData);
            }
            boolean isDetectJab = false;
            isZeroCrossedForXAxis = true;
            if (sensorData.getAx() <= punchDetectionConfig.getLowGXImpThr()) {
                Log.i("LowGPunchDetection ", commonLoggerPattern + "Fifo Length = " + punchDataCollectionFifoForXAxis.toArray().length + " content = " + Arrays.deepToString(punchDataCollectionFifoForXAxis.toArray()));
                int fifoBufferLength = punchDataCollectionFifoForXAxis.toArray().length;
//				Log.i("LowGPunchDetection ","isZeroCrossedForXAxis ="+isZeroCrossedForXAxis+" isThresholdCrossedForXAxis ="+isThresholdCrossedForXAxis);
                if (isZeroCrossedForXAxis && isThresholdCrossedForXAxis) {
//					Log.i("LowGPunchDetection ",commonLoggerPattern+"Ax is again less than lowGXImpThr. Ax ="+sensorData.getAx());
                    if (fifoBufferLength <= punchDetectionConfig.getNXback()) {
                        punchDataCollectionFifoForXAxis.removeAll(punchDataCollectionFifoForXAxis);
//						Log.i("LowGPunchDetection ",commonLoggerPattern+"Ax is again less than lowGXImpThr. punchDataCollectionFifoForXAxis removed...");
                        return;
                    }
                }
                if (!isThresholdCrossedForXAxis) {
                    punchDetector.setPunchDetectedTime(sensorData.getMsgTime());//set the punch time when threshold crossed at 1st time.
                }
                isThresholdCrossedForXAxis = true;
                //-------------------------------- Peak Base Detection for UpperCut Start----------------------------------------
                if (fifoBufferLength > punchDetectionConfig.getNXback()) {
                    this.isCheckUpperCut = true;
//					Log.i("LowGPunchDetection ",commonLoggerPattern+"isAzCrossedLowGZImpThr = "+isAzCrossedLowGZImpThr);
                    if (!isAzCrossedLowGZImpThr) {
                        checkUppercut(sensorData, punchDetector, commonLoggerPattern);
                    } else {
                        punchDataCollectionFifoForXAxis.removeAll(punchDataCollectionFifoForXAxis);
                    }
                    return;
                }
                //-------------------------------- Peak Base Detection for UpperCut End----------------------------------------


//				Log.i("LowGPunchDetection ",commonLoggerPattern+"current time = "+sensorData.getMsgTime()+"Before Inside Jab or Straight  startDetectingNewPunchTime = "+startDetectingNewPunchTime+" Ax = "+sensorData.getAx()+"Az = "+sensorData.getAz());
                this.startDetectingNewPunchTime = sensorData.getMsgTime() + punchDetectionConfig.getPunchWaitTime();//Note:-Detect next punch after next 50 MilliSec.
//				Log.i("LowGPunchDetection ",commonLoggerPattern+"current time = "+sensorData.getMsgTime()+"After Inside Jab or Straight  startDetectingNewPunchTime = "+startDetectingNewPunchTime);


                int[] dX = {punchDetectionTemplate.getDx1(), punchDetectionTemplate.getDx2()};
//				int[] dY = new int[2];
//				int[] dZ = new int[2];


                if ((stance.equals(AppConst.TRADITIONAL) && sensorData.getHand().equals(AppConst.LEFT_HAND)) ||
                        (stance.equals(AppConst.NON_TRADITIONAL) && sensorData.getHand().equals(AppConst.RIGHT_HAND))) {
                    isDetectJab = true;
                }

                int correlationCount = 0;
                int[][] dYdZArray = new int[2][2];
                String punchType = isDetectJab ? AppConst.JAB : AppConst.STRAIGHT;

//				Log.i("LowGPunchDetection ",commonLoggerPattern+"punchDataCollectionFifoForXAxis length = "+punchDataCollectionFifoForXAxis.toArray().length+" content = "+Arrays.deepToString(punchDataCollectionFifoForXAxis.toArray()));

                SensorAcclData acclData = new SensorAcclData();
                for (Object obj : punchDataCollectionFifoForXAxis) {
                    acclData = (SensorAcclData) obj;
                }
//				Log.i("LowGPunchDetection ",commonLoggerPattern+"Nfrwd sample  on X-Axis="+acclData);
                dYdZArray = getdYAnddZ(acclData, sensorData.getHand());
                correlationCount = punchDetectionTemplate.getCorrelation(dX, dYdZArray[0], dYdZArray[1], sensorData.getHand(), punchType, stance, commonLoggerPattern);
                if (correlationCount == 3 || correlationCount == 2) {
                    if (isDetectJab) {
                        punchDetector.setJab(true);
                        punchDetector.setUnrecognized(false);
                        Log.i("LowGPunchDetection ", commonLoggerPattern + "Jab Found- Punch recognized................................correlationCount = " + correlationCount);
                    } else {
                        punchDetector.setStraight(true);
                        punchDetector.setUnrecognized(false);
                        Log.i("LowGPunchDetection ", commonLoggerPattern + "Straight Found- Punch recognized................................correlationCount = " + correlationCount);
                    }
                } else {
                    Log.i("LowGPunchDetection ", commonLoggerPattern + "In Jab, Punch Unrecognized................................correlationCount = " + correlationCount);
                    punchDetector.setUnrecognized(true);
                }
                punchDataCollectionFifoForXAxis.removeAll(punchDataCollectionFifoForXAxis);
                //return;

            } else if (sensorData.getAx() > punchDetectionConfig.getLowGXImpThr()) {
                if (isZeroCrossedForXAxis && isThresholdCrossedForXAxis) {
//					Log.i("LowGPunchDetection ",commonLoggerPattern+"Ax is going up..Phase completed..Greater than 0 & Less than threshold.");
                    isThresholdCrossedForXAxis = false;
                    isZeroCrossedForXAxis = false;
                    if (!isAzCrossedLowGZImpThr && isCheckUpperCut) {
                        checkUppercut(sensorData, punchDetector, commonLoggerPattern);
                        isCheckUpperCut = false;
                    }
                    punchDataCollectionFifoForXAxis.removeAll(punchDataCollectionFifoForXAxis);
                }
            }


        } else if (sensorData.getAx() >= 0) {
            punchDataCollectionFifoForXAxis.removeAll(punchDataCollectionFifoForXAxis);
            if (isZeroCrossedForXAxis && isThresholdCrossedForXAxis) {
//				Log.i("LowGPunchDetection ",commonLoggerPattern+"Ax is up ..Phase completed.. Greater than 0 ");
                isThresholdCrossedForXAxis = false;
                isZeroCrossedForXAxis = false;
                if (!isAzCrossedLowGZImpThr && isCheckUpperCut) {
                    checkUppercut(sensorData, punchDetector, commonLoggerPattern);
                    isCheckUpperCut = false;
                }
            }
        }

    }

    /**
     * It detects Punch On YAxis.
     **/
    @SuppressWarnings("unchecked")
    public void detectPunchOnZAxis(SensorData sensorData, PunchDetector punchDetector, String stance, String commonLoggerPattern) {

        if (sensorData.getAz() > 0) {

            SensorAcclData detector = new SensorAcclData();
            detector.setAx((short) sensorData.getAx());
            detector.setAy((short) sensorData.getAy());
            detector.setAz((short) sensorData.getAz());

            try {
                punchDataCollectionFifoForZAxis.add(detector); // add data to fifo
            } catch (BufferOverflowException e) {
                punchDataCollectionFifoForZAxis.remove();
                punchDataCollectionFifoForZAxis.add(detector);
            }

            isZeroCrossedForZAxis = true;
            //LOGGER.info("checking for Hook.... Ax = "+sensorData.getAx()+"Az = "+sensorData.getAz()+" current time == "+sensorData.getCurrentTime()+" startDetectingNewPunchTime = "+this.startDetectingNewPunchTime);
            if (sensorData.getAz() >= punchDetectionConfig.getLowGZImpThr()) {

                if (isZeroCrossedForZAxis && isThresholdCrossedForZAxis) {
//					Log.i("LowGPunchDetection ",commonLoggerPattern+"Az is again greater than lowGZImpThr. Az ="+sensorData.getAz());
                    punchDataCollectionFifoForZAxis.removeAll(punchDataCollectionFifoForZAxis);
                    return;
                }
                isThresholdCrossedForZAxis = true;
                Log.i("LowGPunchDetection ", commonLoggerPattern + "punchDataCollectionFifoForZAxis length = = = =" + punchDataCollectionFifoForZAxis.toArray().length + " content = " + Arrays.deepToString(punchDataCollectionFifoForZAxis.toArray()));
                if (punchDataCollectionFifoForZAxis.toArray().length > punchDetectionConfig.getNZback()) {
//					Log.i("LowGPunchDetection ",commonLoggerPattern+"--------------------- Not an impact on Z Axis----------------------------------"+" punchDataCollectionFifoForZAxis = "+Arrays.deepToString(punchDataCollectionFifoForZAxis.toArray()));
//					Log.i("LowGPunchDetection ",commonLoggerPattern+"integratorList content on Z axis = "+punchDataCollectionFifoForZAxis.toArray());
                    punchDataCollectionFifoForZAxis.removeAll(punchDataCollectionFifoForZAxis);
                    punchDetector.setUnrecognized(true); //Change by 1014, Note:- Send Unrecognized if no punch found.
                    return;
                }
                isAzCrossedLowGZImpThr = false;
                isCheckUpperCut = false;
//				Log.i("LowGPunchDetection ",commonLoggerPattern+"Hook  startDetectingNewPunchTime = "+startDetectingNewPunchTime+" Ax = "+sensorData.getAx()+"Az = "+sensorData.getAz());
                this.startDetectingNewPunchTime = sensorData.getMsgTime() + punchDetectionConfig.getPunchWaitTime(); //Note:-Detect next punch after next 50 MilliSec.

//				int[] dX = new int[2];
//				int[] dY = new int[2];
                int[] dZ = {punchDetectionTemplate.getDz1Hook(), punchDetectionTemplate.getDz2Hook()};

                int correlationCount = 0;
                int[][] dXdYArray = new int[2][2];
                SensorAcclData acclData = new SensorAcclData();
                for (Object obj : punchDataCollectionFifoForZAxis) {
                    acclData = (SensorAcclData) obj;
                }
                punchDetector.setPunchDetectedTime(sensorData.getMsgTime());//set the punch time when threshold crossed at 1st time.
                Log.i("LowGPunchDetection ", commonLoggerPattern + "Nfrwd sample on Z-Axis=" + acclData);
                dXdYArray = getdXAnddY(acclData, sensorData.getHand());
//				dX = dXdYArray[0];	dY = dXdYArray[1];
//				Log.i("LowGPunchDetection ",commonLoggerPattern+"dX = "+ Arrays.toString(dXdYArray[0])+"dY = "+ Arrays.toString(dXdYArray[1])+"dZ = "+ Arrays.toString(dZ));
                correlationCount = punchDetectionTemplate.getCorrelation(dXdYArray[0], dXdYArray[1], dZ, sensorData.getHand(), "HOOK", stance, commonLoggerPattern);
                if (correlationCount == 3 || correlationCount == 2) {
                    Log.i("LowGPunchDetection ", commonLoggerPattern + "Hook Found- Punch recognized................................noOfMatch = " + correlationCount);
                    punchDetector.setHook(true);
                    punchDetector.setUnrecognized(false);
                } else {
                    punchDetector.setUnrecognized(true);
                    Log.i("LowGPunchDetection ", commonLoggerPattern + "In hook, Punch Unrecognized ................................noOfMatch = " + correlationCount);
                }
                punchDataCollectionFifoForZAxis.removeAll(punchDataCollectionFifoForZAxis);
            } else if (sensorData.getAz() < punchDetectionConfig.getLowGZImpThr()) {
                if (isZeroCrossedForZAxis && isThresholdCrossedForZAxis) {
//					Log.i("LowGPunchDetection ",commonLoggerPattern+"Az is going down..Phase completed..Greater than 0 & Less than threshold.");
                    isThresholdCrossedForZAxis = false;
                    isZeroCrossedForZAxis = false;
                    punchDataCollectionFifoForZAxis.removeAll(punchDataCollectionFifoForZAxis);
                }
            }


        } else if (sensorData.getAz() <= 0) {
            punchDataCollectionFifoForZAxis.removeAll(punchDataCollectionFifoForZAxis);
            if (isZeroCrossedForZAxis && isThresholdCrossedForZAxis) {
//				Log.i("LowGPunchDetection ",commonLoggerPattern+"Az is going down..Phase completed.. less than 0 ");
                isThresholdCrossedForZAxis = false;
                isZeroCrossedForZAxis = false;
            }
        }
        return;
    }

    @SuppressWarnings("unchecked")
    public void checkUppercut(SensorData sensorData, PunchDetector punchDetector, String commonLoggerPattern) {

//		Log.i("LowGPunchDetection ",commonLoggerPattern+"Inside checkUppercut().... az = "+sensorData.getAz()+" ax = "+sensorData.getAx());
        if (sensorData.getAz() > punchDetectionConfig.getLowGZImpThr()) {
            isAzCrossedLowGZImpThr = true;
//			Log.i("LowGPunchDetection ",commonLoggerPattern+"Inside checkUppercut() if() isAzCrossedLowGZImpThr = "+isAzCrossedLowGZImpThr);
            return;
        }
        if (sensorData.getAx() < punchDetectionConfig.getLowGXImpThr()) {
            if (upperCutCurrentMinAx > sensorData.getAx()) {
                upperCutCurrentMinAx = sensorData.getAx();
                upperCutCurrentMinAz = sensorData.getAz();
            }
        } else if (sensorData.getAx() > punchDetectionConfig.getLowGXImpThr()) {
            isThresholdBackCrossed = true;
        }
//		Log.i("LowGPunchDetection ","Inside checkUppercut() isThresholdBackCrossed = "+isThresholdBackCrossed+" upperCutCurrentMinAx = "+upperCutCurrentMinAx+ " upperCutCurrentMinAz = "+upperCutCurrentMinAz);
        if (this.isThresholdBackCrossed) {

            this.startDetectingNewPunchTime = sensorData.getMsgTime() + punchDetectionConfig.getPunchWaitTime();

            if (this.upperCutCurrentMinAz < punchDetectionConfig.getLowGXImpThr()) {
                Log.i("LowGPunchDetection ", commonLoggerPattern + "--------------------Uppercut found--------------------");
                punchDetector.setUpperCut(true);
                punchDetector.setUnrecognized(false);

            } else {
//				Log.i("LowGPunchDetection ",commonLoggerPattern+"sorry can't get uppercut uppercutPeakAzValue = "+this.upperCutCurrentMinAz+"lowGXImpThr ="+punchDetectionConfig.getLowGXImpThr());
                punchDetector.setUnrecognized(true);
            }
            upperCutCurrentMinAx = 0;
            upperCutCurrentMinAz = 0;
            punchDataCollectionFifoForXAxis.removeAll(punchDataCollectionFifoForXAxis);
            this.isThresholdBackCrossed = false;
        }
        return;
    }

    private int[][] getdYAnddZ(SensorAcclData acclData, String hand) {
        int[][] dYdZArray = new int[2][2];
        dYdZArray[0][1] = ((int) Math.signum(acclData.getAy())) == 0 ? 1 : ((int) Math.signum(acclData.getAy()));
        ;
        dYdZArray[0][0] = -dYdZArray[0][1];
        dYdZArray[1][1] = ((int) Math.signum(acclData.getAz())) == 0 ? 1 : ((int) Math.signum(acclData.getAz()));
        ;
        dYdZArray[1][0] = -dYdZArray[1][1];
        return dYdZArray;
    }

    private int[][] getdXAnddY(SensorAcclData acclData, String hand) {
        int[][] dXdYArray = new int[2][2];
        dXdYArray[0][1] = ((int) Math.signum(acclData.getAx())) == 0 ? 1 : ((int) Math.signum(acclData.getAx()));
        dXdYArray[0][0] = -dXdYArray[0][1];
        dXdYArray[1][1] = ((int) Math.signum(acclData.getAy())) == 0 ? 1 : ((int) Math.signum(acclData.getAy()));
        dXdYArray[1][0] = -dXdYArray[1][1];
        return dXdYArray;
    }

    public PunchDetectedMap getPunchDetectedMap() {
        return punchDetectedMap;
    }

    public void setPunchDetectedMap(PunchDetectedMap punchDetectedMap) {
        this.punchDetectedMap = punchDetectedMap;
    }
}
