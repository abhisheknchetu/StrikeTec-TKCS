package com.striketec.mobile.util.sensormanager.bluetooth.readerBean;

import com.striketec.mobile.util.AppConst;
import com.striketec.mobile.util.CommonUtils;
import com.striketec.mobile.util.FontManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * The Class ConstantPropertyValue.
 */
public class PunchDetectionConfig {

    private static PunchDetectionConfig punchDetectionConfig;

    private String WEB_SERVICE_DOMAIN;

    /**
     * The velocity est type.
     */
    private String velocityEstType;

    /**
     * The high g nback.
     */
    private int highGNback;

    /**
     * The high g ax thr.
     */
    private int highGAxThr;

    /**
     * The high g ay thr.
     */
    private int highGAyThr;

    /**
     * The high g az thr.
     */
    private int highGAzThr;

    /**
     * The low gx imp thr.
     */
    private int lowGXImpThr;

    /**
     * The low gz imp thr.
     */
    private int lowGZImpThr;

    /**
     * The low gn xback.
     */
    private int nXback;

    /**
     * The low gn zback.
     */
    private int nZback;

    private int nFwd;

    private int punchWaitTime;

    private double highGYyCoef;

    private double highGYzCoef;

    private int vfaBufferSize;

    private double punchMassEff;

    private double maxVelocity;
    private double minVelocity;
    private double maxForce;
    private double minForce;
    private int highG_Nfrwd;
    private double highG_YY_bounce_coeff;
    private double highG_YZ_bounce_coeff;
    private String forceFormula;
    private int highG_nonY_punches;
    private String WEB_SOCKET_DOMAIN;

    /** The Constant PROPERTIESFILEPATH. */

    /**
     * Instantiates a new constant property value.
     */
    private PunchDetectionConfig() {
        super();
        try {

            // Read from the /assets directory
            readConfigFile();

        } catch (Exception e) {
        }
    }

    /**
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void readConfigFile() {

        try {
            InputStream inputStream = FontManager.assetManager.open("config.properties");

            Properties prop = new Properties();
            prop.load(inputStream);
            inputStream.close();

            WEB_SERVICE_DOMAIN = prop.getProperty("WEB_SERVICE_DOMAIN");
            WEB_SOCKET_DOMAIN = prop.getProperty("WEB_SOCKET_DOMAIN");

            velocityEstType = prop.getProperty("Vel_est_type");
            highGNback = Integer.parseInt(prop.getProperty("HighG_Nback"));

            highGAxThr = Integer.parseInt(prop.getProperty("HighG_thr_AX"));
            highGAyThr = Integer.parseInt(prop.getProperty("HighG_thr_AY"));
            highGAzThr = Integer.parseInt(prop.getProperty("HighG_thr_AZ"));

            lowGXImpThr = Integer.parseInt(prop.getProperty("Ximpthr"));
            lowGZImpThr = Integer.parseInt(prop.getProperty("Zimpthr"));

            nXback = Integer.parseInt(prop.getProperty("Nxback"));
            nZback = Integer.parseInt(prop.getProperty("Nzback"));

            nFwd = Integer.parseInt(prop.getProperty("Nfrwd"));
            punchWaitTime = Integer.parseInt(prop.getProperty("punchWaitTime"));

            highGYyCoef = Double.parseDouble(prop.getProperty("HighG_YY_coef"));
            highGYzCoef = Double.parseDouble(prop.getProperty("HighG_YZ_coef"));

            vfaBufferSize = Integer.parseInt(prop.getProperty("VFABufferSize"));

            punchMassEff = Integer.parseInt(prop.getProperty("punch_mass_eff"));

            maxVelocity = Double.parseDouble(prop.getProperty("max_velocity"));
            minVelocity = Double.parseDouble(prop.getProperty("min_velocity"));
            maxForce = Double.parseDouble(prop.getProperty("max_force"));
            minForce = Double.parseDouble(prop.getProperty("min_force"));

            highG_Nfrwd = Integer.parseInt(prop.getProperty("HighG_Nfrwd"));
            highG_YY_bounce_coeff = Double.parseDouble(prop.getProperty("HighG_YY_bounce_coeff"));
            highG_YZ_bounce_coeff = Double.parseDouble(prop.getProperty("HighG_YZ_bounce_coeff"));

            forceFormula = prop.getProperty("force_formula").trim();

            highG_nonY_punches = Integer.parseInt(prop.getProperty("HighG_nonY_punches"));

        } catch (Exception e) {
            e.printStackTrace();
            punchDetectionConfig = null;
        }

    }

    public void saveBaseUrl(String url) {

        try {
            File configFile = new File(CommonUtils.getCommonDirectory() + File.separator + AppConst.CONFIG_DIRECTORY, AppConst.PROPERTIESFILEPATH);

            InputStream inputStream = new FileInputStream(configFile);
            Properties prop = new Properties();
            prop.load(inputStream);

            prop.setProperty("WEB_SERVICE_DOMAIN", url);


        } catch (Exception e) {
            e.printStackTrace();
        }

        readConfigFile();
    }

    public static PunchDetectionConfig getInstance() {

        if (punchDetectionConfig == null) {
            punchDetectionConfig = new PunchDetectionConfig();
        }
        return punchDetectionConfig;

    }

    /**
     * Gets the velocity est type.
     *
     * @return the velocity est type
     */
    public String getVelocityEstType() {
        return velocityEstType;
    }

    /**
     * Sets the velocity est type.
     *
     * @param velocityEstType the new velocity est type
     */
    public void setVelocityEstType(String velocityEstType) {
        this.velocityEstType = velocityEstType;
    }

    /**
     * Gets the high g nback.
     *
     * @return the high g nback
     */
    public int getHighGNback() {
        return highGNback;
    }

    /**
     * Sets the high g nback.
     *
     * @param highGNback the new high g nback
     */
    public void setHighGNback(int highGNback) {
        this.highGNback = highGNback;
    }

    /**
     * Gets the high g ax thr.
     *
     * @return the high g ax thr
     */
    public int getHighGAxThr() {
        return highGAxThr;
    }

    /**
     * Sets the high g ax thr.
     *
     * @param highGAxThr the new high g ax thr
     */
    public void setHighGAxThr(int highGAxThr) {
        this.highGAxThr = highGAxThr;
    }

    /**
     * Gets the high g ay thr.
     *
     * @return the high g ay thr
     */
    public int getHighGAyThr() {
        return highGAyThr;
    }

    /**
     * Sets the high g ay thr.
     *
     * @param highGAyThr the new high g ay thr
     */
    public void setHighGAyThr(int highGAyThr) {
        this.highGAyThr = highGAyThr;
    }

    /**
     * Gets the high g az thr.
     *
     * @return the high g az thr
     */
    public int getHighGAzThr() {
        return highGAzThr;
    }

    /**
     * Sets the high g az thr.
     *
     * @param highGAzThr the new high g az thr
     */
    public void setHighGAzThr(int highGAzThr) {
        this.highGAzThr = highGAzThr;
    }

    /**
     * Gets the low gx imp thr.
     *
     * @return the low gx imp thr
     */
    public int getLowGXImpThr() {
        return lowGXImpThr;
    }

    /**
     * Sets the low gx imp thr.
     *
     * @param lowGXImpThr the new low gx imp thr
     */
    public void setLowGXImpThr(int lowGXImpThr) {
        this.lowGXImpThr = lowGXImpThr;
    }

    /**
     * Gets the low gz imp thr.
     *
     * @return the low gz imp thr
     */
    public int getLowGZImpThr() {
        return lowGZImpThr;
    }

    /**
     * Sets the low gz imp thr.
     *
     * @param lowGZImpThr the new low gz imp thr
     */
    public void setLowGZImpThr(int lowGZImpThr) {
        this.lowGZImpThr = lowGZImpThr;
    }

    /**
     * Gets the low gn xback.
     *
     * @return the low gn xback
     */
    public int getNXback() {
        return nXback;
    }

    /**
     * Sets the low gn xback.
     *
     * @param lowGNXback the new low gn xback
     */
    public void setNXback(int nXback) {
        this.nXback = nXback;
    }

    /**
     * Gets the low gn zback.
     *
     * @return the low gn zback
     */
    public int getNZback() {
        return nZback;
    }

    /**
     * Sets the low gn zback.
     *
     * @param lowGNZback the new low gn zback
     */
    public void setNZback(int nZback) {
        this.nZback = nZback;
    }

    /**
     * Gets the low gn fwd.
     *
     * @return the low gn fwd
     */
    public int getNFwd() {
        return nFwd;
    }

    /**
     * Sets the low gn fwd.
     *
     * @param lowGNFwd the new low gn fwd
     */
    public void setNFwd(int nFwd) {
        this.nFwd = nFwd;
    }

    /**
     * @return the punchWaitTime
     */
    public int getPunchWaitTime() {
        return punchWaitTime;
    }

    /**
     * @param punchWaitTime the punchWaitTime to set
     */
    public void setPunchWaitTime(int punchWaitTime) {
        this.punchWaitTime = punchWaitTime;
    }

    /**
     * Gets the propertiesfilepath.
     *
     * @return the propertiesfilepath
     */
    public static String getPropertiesfilepath() {
        return AppConst.PROPERTIESFILEPATH;
    }

    /**
     * @return the yyCoef
     */
    public double getHighGYyCoef() {
        return highGYyCoef;
    }

    /**
     * @param highGYyCoef the yyCoef to set
     */
    public void setHighGYyCoef(int highGYyCoef) {
        this.highGYyCoef = highGYyCoef;
    }

    /**
     * @return the yzCoef
     */
    public double getHighGYzCoef() {
        return highGYzCoef;
    }

    /**
     * @param highGYzCoef the yzCoef to set
     */
    public void setHighGYzCoef(int highGYzCoef) {
        this.highGYzCoef = highGYzCoef;
    }

    /**
     * @return the vfaBufferSize
     */
    public int getVfaBufferSize() {
        return vfaBufferSize;
    }

    /**
     * @param vfaBufferSize the vfaBufferSize to set
     */
    public void setVfaBufferSize(int vfaBufferSize) {
        this.vfaBufferSize = vfaBufferSize;
    }

    public String getWEB_SERVICE_DOMAIN() {
//        return "http://34.212.48.125:8090/EFD";
        return "http://54.213.226.127:8090/EFD";
//        String storedUrl = SharedPreferencesUtils.getBaseUrl(App.getContext());
//        return storedUrl != null ? storedUrl : WEB_SERVICE_DOMAIN;
    }

    public void setWEB_SERVICE_DOMAIN(String wEBSERVICEDOMAIN) {
        WEB_SERVICE_DOMAIN = wEBSERVICEDOMAIN;
    }

    public double getPunchMassEff() {
        return punchMassEff;
    }

    public void setPunchMassEff(double punchMassEff) {
        this.punchMassEff = punchMassEff;
    }

    public double getMaxVelocity() {
        return maxVelocity;
    }

    public void setMaxVelocity(double maxVelocity) {
        this.maxVelocity = maxVelocity;
    }

    public double getMinVelocity() {
        return minVelocity;
    }

    public void setMinVelocity(double minVelocity) {
        this.minVelocity = minVelocity;
    }

    public double getMaxForce() {
        return maxForce;
    }

    public void setMaxForce(double maxForce) {
        this.maxForce = maxForce;
    }

    public double getMinForce() {
        return minForce;
    }

    public void setMinForce(double minForce) {
        this.minForce = minForce;
    }

    public int getHighG_Nfrwd() {
        return highG_Nfrwd;
    }

    public void setHighG_Nfrwd(int highG_Nfrwd) {
        this.highG_Nfrwd = highG_Nfrwd;
    }

    public double getHighG_YY_bounce_coeff() {
        return highG_YY_bounce_coeff;
    }

    public void setHighG_YY_bounce_coeff(double highG_YY_bounce_coeff) {
        this.highG_YY_bounce_coeff = highG_YY_bounce_coeff;
    }

    public double getHighG_YZ_bounce_coeff() {
        return highG_YZ_bounce_coeff;
    }

    public void setHighG_YZ_bounce_coeff(double highG_YZ_bounce_coeff) {
        this.highG_YZ_bounce_coeff = highG_YZ_bounce_coeff;
    }

    public void setForceFormula(String forceFormula) {
        this.forceFormula = forceFormula;
    }

    public String getForceFormula() {
        return forceFormula;
    }

    public int getHighG_nonY_punches() {
        return highG_nonY_punches;
    }

    public void setHighG_nonY_punches(int highGNonYPunches) {
        highG_nonY_punches = highGNonYPunches;
    }

    public String getWEB_SOCKET_DOMAIN() {
        return WEB_SOCKET_DOMAIN;
    }

    public void setWEB_SOCKET_DOMAIN(String wEBSOCKETDOMAIN) {
        WEB_SOCKET_DOMAIN = wEBSOCKETDOMAIN;
    }
}
