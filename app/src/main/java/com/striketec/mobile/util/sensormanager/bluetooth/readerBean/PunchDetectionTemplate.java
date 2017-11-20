package com.striketec.mobile.util.sensormanager.bluetooth.readerBean;


import android.content.res.AssetManager;
import android.content.res.Resources;
import android.util.Log;

import com.striketec.mobile.util.AppConst;
import com.striketec.mobile.util.FontManager;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.StringTokenizer;

public class PunchDetectionTemplate {

    private int dx1;
    private int dx2;
    private int dy1Left;
    private int dy2Left;
    private int dy1Right;
    private int dy2Right;
    private int dz1;
    private int dz2;
    private int dx1Hook;
    private int dx2Hook;
    private int dy1LeftHook;
    private int dy2LeftHook;
    private int dy1RightHook;
    private int dy2RightHook;
    private int dz1Hook;
    private int dz2Hook;


    public PunchDetectionTemplate() {
        super();
        try {
            // Read from the /assets directory
            InputStream inputStream = FontManager.assetManager.open(AppConst.TEMPLATEFILEPATH);
            BufferedReader bufRdr = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            bufRdr.readLine();
            bufRdr.readLine();
            line = bufRdr.readLine();
            StringTokenizer stringTokenizer = new StringTokenizer(line, ",");
            stringTokenizer.nextToken();

            dx1 = Integer.parseInt(stringTokenizer.nextToken());
            dx2 = Integer.parseInt(stringTokenizer.nextToken());

            dy1Left = Integer.parseInt(stringTokenizer.nextToken());
            dy2Left = Integer.parseInt(stringTokenizer.nextToken());

            dy1Right = Integer.parseInt(stringTokenizer.nextToken());
            dy2Right = Integer.parseInt(stringTokenizer.nextToken());

            dz1 = Integer.parseInt(stringTokenizer.nextToken());
            dz2 = Integer.parseInt(stringTokenizer.nextToken());

            line = bufRdr.readLine();
            stringTokenizer = new StringTokenizer(line, ",");
            stringTokenizer.nextToken();

            dx1Hook = Integer.parseInt(stringTokenizer.nextToken());
            dx2Hook = Integer.parseInt(stringTokenizer.nextToken());

            dy1LeftHook = Integer.parseInt(stringTokenizer.nextToken());
            dy2LeftHook = Integer.parseInt(stringTokenizer.nextToken());

            dy1RightHook = Integer.parseInt(stringTokenizer.nextToken());
            dy2RightHook = Integer.parseInt(stringTokenizer.nextToken());

            dz1Hook = Integer.parseInt(stringTokenizer.nextToken());
            dz2Hook = Integer.parseInt(stringTokenizer.nextToken());


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public PunchDetectionTemplate(int dx1, int dx2, int dy1Left, int dy2Left,
                                  int dy1Right, int dy2Right, int dz1, int dz2, int dx1Hook,
                                  int dx2Hook, int dy1LeftHook, int dy2LeftHook, int dy1RightHook,
                                  int dy2RightHook, int dz1Hook, int dz2Hook) {
        super();
        this.dx1 = dx1;
        this.dx2 = dx2;
        this.dy1Left = dy1Left;
        this.dy2Left = dy2Left;
        this.dy1Right = dy1Right;
        this.dy2Right = dy2Right;
        this.dz1 = dz1;
        this.dz2 = dz2;
        this.dx1Hook = dx1Hook;
        this.dx2Hook = dx2Hook;
        this.dy1LeftHook = dy1LeftHook;
        this.dy2LeftHook = dy2LeftHook;
        this.dy1RightHook = dy1RightHook;
        this.dy2RightHook = dy2RightHook;
        this.dz1Hook = dz1Hook;
        this.dz2Hook = dz2Hook;
    }


    /**
     * @return the number of arrays that are equal to the corresponding templates.
     */
    public int getCorrelation(int[] dX, int[] dY, int[] dZ, String hand, String punchType, String stance, String commonLoggerPattern) {
        int[] templateX = this.getXTemplate(punchType);
        int[] templateY = this.getYTemplate(punchType, hand, stance);
        int[] templateZ = this.getZTemplate(punchType);
        Log.d("PunchDetectionTemplate", commonLoggerPattern + "Inside getCorrelation(), templateX = " + Arrays.toString(templateX) + " templateY = " + Arrays.toString(templateY) + " templateZ =" + Arrays.toString(templateZ));
        Log.d("PunchDetectionTemplate", commonLoggerPattern + "Inside getCorrelation(), dx = " + Arrays.toString(dX) + " dY = " + Arrays.toString(dY) + "dz =" + Arrays.toString(dZ));
        return areEqual(dX, templateX) +
                areEqual(dY, templateY) +
                areEqual(dZ, templateZ);
    }

    private int areEqual(int[] a, int[] b) {
        return Arrays.equals(a, b) ? 1 : 0;
    }


    //templateZ[0]=templatesValue.getDz1();templateZ[1]=templatesValue.getDz2();

    private int[] getXTemplate(String punchType) {
        int[] templateX = new int[2];

        if (punchType.equalsIgnoreCase(AppConst.HOOK)) {
            templateX[0] = this.getDx1Hook();
            templateX[1] = this.getDx2Hook();
        } else if (punchType.equalsIgnoreCase(AppConst.JAB) || punchType.equalsIgnoreCase(AppConst.STRAIGHT)) {
            templateX[0] = this.getDx1();
            templateX[1] = this.getDx2();
        }

        return templateX;

    }

    private int[] getYTemplate(String punchType, String hand, String stance) {
        int[] templateY = new int[2];
        if (punchType.equalsIgnoreCase(AppConst.HOOK)) {
            if (hand.equalsIgnoreCase(AppConst.LEFT_HAND)) {
                templateY[0] = this.getDy1LeftHook();
                templateY[1] = this.getDy2LeftHook();
            } else {
                templateY[0] = this.getDy1RightHook();
                templateY[1] = this.getDy2RightHook();
            }
        } else if (punchType.equalsIgnoreCase(AppConst.JAB) || punchType.equalsIgnoreCase(AppConst.STRAIGHT)) {
            if ((hand.equals(AppConst.LEFT_HAND)) /*stance.equals(EFDConstants.TRADITION) && hand.equals(EFDConstants.LEFT_HAND)) ||
                    (stance.equals(EFDConstants.NON_TRADITION) && hand.equals(EFDConstants.RIGHT_HAND))*/) { // Temporary condition for Left Hand

                templateY[0] = this.getDy1Left();
                templateY[1] = this.getDy2Left();
            } else {
                // Temporary condition for Right Hand
                templateY[0] = this.getDy1Right();
                templateY[1] = this.getDy2Right();
            }
        }
        return templateY;
    }

    private int[] getZTemplate(String punchType) {
        int[] templateZ = new int[2];
        if (punchType.equalsIgnoreCase(AppConst.HOOK)) {
            templateZ[0] = this.getDz1Hook();
            templateZ[1] = this.getDz2Hook();
        } else if (punchType.equalsIgnoreCase(AppConst.JAB) || punchType.equalsIgnoreCase(AppConst.STRAIGHT)) {
            templateZ[0] = this.getDz1();
            templateZ[1] = this.getDz2();
        }
        return templateZ;
    }


    /**
     * @return the dx1
     */
    public int getDx1() {
        return dx1;
    }


    /**
     * @param dx1 the dx1 to set
     */
    public void setDx1(int dx1) {
        this.dx1 = dx1;
    }


    /**
     * @return the dx2
     */
    public int getDx2() {
        return dx2;
    }


    /**
     * @param dx2 the dx2 to set
     */
    public void setDx2(int dx2) {
        this.dx2 = dx2;
    }


    /**
     * @return the dy1Left
     */
    public int getDy1Left() {
        return dy1Left;
    }


    /**
     * @param dy1Left the dy1Left to set
     */
    public void setDy1Left(int dy1Left) {
        this.dy1Left = dy1Left;
    }


    /**
     * @return the dy2Left
     */
    public int getDy2Left() {
        return dy2Left;
    }


    /**
     * @param dy2Left the dy2Left to set
     */
    public void setDy2Left(int dy2Left) {
        this.dy2Left = dy2Left;
    }


    /**
     * @return the dy1Right
     */
    public int getDy1Right() {
        return dy1Right;
    }


    /**
     * @param dy1Right the dy1Right to set
     */
    public void setDy1Right(int dy1Right) {
        this.dy1Right = dy1Right;
    }


    /**
     * @return the dy2Right
     */
    public int getDy2Right() {
        return dy2Right;
    }


    /**
     * @param dy2Right the dy2Right to set
     */
    public void setDy2Right(int dy2Right) {
        this.dy2Right = dy2Right;
    }


    /**
     * @return the dz1
     */
    public int getDz1() {
        return dz1;
    }


    /**
     * @param dz1 the dz1 to set
     */
    public void setDz1(int dz1) {
        this.dz1 = dz1;
    }


    /**
     * @return the dz2
     */
    public int getDz2() {
        return dz2;
    }


    /**
     * @param dz2 the dz2 to set
     */
    public void setDz2(int dz2) {
        this.dz2 = dz2;
    }


    /**
     * @return the propertiesfilepath
     */
    public static String getPropertiesfilepath() {
        return AppConst.TEMPLATEFILEPATH;
    }


    public int getDx1Hook() {
        return dx1Hook;
    }


    public void setDx1Hook(int dx1Hook) {
        this.dx1Hook = dx1Hook;
    }


    public int getDx2Hook() {
        return dx2Hook;
    }


    public void setDx2Hook(int dx2Hook) {
        this.dx2Hook = dx2Hook;
    }


    public int getDy1LeftHook() {
        return dy1LeftHook;
    }


    public void setDy1LeftHook(int dy1LeftHook) {
        this.dy1LeftHook = dy1LeftHook;
    }


    public int getDy2LeftHook() {
        return dy2LeftHook;
    }


    public void setDy2LeftHook(int dy2LeftHook) {
        this.dy2LeftHook = dy2LeftHook;
    }


    public int getDy1RightHook() {
        return dy1RightHook;
    }


    public void setDy1RightHook(int dy1RightHook) {
        this.dy1RightHook = dy1RightHook;
    }


    public int getDy2RightHook() {
        return dy2RightHook;
    }


    public void setDy2RightHook(int dy2RightHook) {
        this.dy2RightHook = dy2RightHook;
    }


    public int getDz1Hook() {
        return dz1Hook;
    }


    public void setDz1Hook(int dz1Hook) {
        this.dz1Hook = dz1Hook;
    }


    public int getDz2Hook() {
        return dz2Hook;
    }


    public void setDz2Hook(int dz2Hook) {
        this.dz2Hook = dz2Hook;
    }


}
