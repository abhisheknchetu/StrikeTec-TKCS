package com.striketec.mobile.util;

/**
 * Created by Qiang on 8/7/2017.
 */

public class AppConst {

    //true : to see logs
    public static final boolean DEBUG = true;
    public static final boolean SENSOR_TEST = false;
    public static final boolean SENSOR_DEBUG = true;

    public static final String LEFT_MAC_ADDRESS = "0017E9254E0C";
    public static final String RIGHT_MAC_ADDRESS = "0017E9234014";

    public static boolean appendNotificationMessages = false;
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;
    public static final String PUSH_NOTIFICATION = "pushNotification";
    public static final String PUSH_MESSAGE = "pushmessage";

    public static final String PREFERENCE = "preference";

    public static final String PROPERTIESFILEPATH = "config.properties";
    public static final String TEMPLATEFILEPATH = "templates.csv";
    public static final String FORCE_TABLE_TEMPLATE_FILEPATH = "forceTable.csv";
    public static final String CONFIG_DIRECTORY = "config";
    public static final String LOGS_DIRECTORY = "logs";
    public static final String SENSOR_FOUND_MESSAGE = "Sensor :- ";
    public static final String DETECTED_TEXT = " detected.";

    public static String FB_PREFIX = "https://graph.facebook.com/";

    public static final String DATABASE_NAME = "StrikeTec_DB.db";
    public static final String DATABASE_DIRECTORY = "Database";

    public static final String TRAINING_TYPE_QUICKSTART = "1";
    public static final String TRAINING_TYPE_ROUND = "2";
    public static final String TRAINING_TYPE_COMBINATION = "3";
    public static final String TRAINING_TYPE_SETS = "4";
    public static final String TRAINING_TYPE_WORKOUT = "5";

    public static final String TRAINING_TYPE_QUICKSTART_STRING = "QUICK START TRAINING";
    public static final String TRAINING_TYPE_ROUND_STRING = "ROUND TRAINING";
    public static final String TRAINING_TYPE_COMBINATION_STRING = "COMBINATION";
    public static final String TRAINING_TYPE_SETS_STRING = "SET ROUTINE";
    public static final String TRAINING_TYPE_WORKOUT_STRING = "WORKOUT";

    public static final String[] MMAGLOVE_PREFIX = {"MMAGlove-", "STRIKTEC-"};    // "MMAGlove-";
    public static final String BLANK_TEXT = "";

    //-------------------------- Spinner kind  ------------------------
    public static final int SPINNER_DIGIT_ORANGE = 0;
    public static final int SPINNER_TEXT_ORANGE = 1;

    public static final String SKILL_LEVEL_NOVICE_TEXT = "Novice";
    public static final String SKILL_LEVEL_AMATEUR_TEXT = "Amateur";
    public static final String SKILL_LEVEL_PROFESSIONAL_TEXT = "Professional";

    public static final String SKILL_LEVEL_ONE = "Fat Loss";
    public static final String SKILL_LEVEL_TWO = "Cardio & Conditioning";
    public static final String SKILL_LEVEL_THREE = "Box like a Professional";

    //max, min value of preset
    public static final int ROUNDS_MIN = 1;
    public static final int ROUNDS_MAX = 12;
    public static final int ROUNDS_INTERVAL = 1;

    public static final int PRESET_MIN_TIME = 30;  //30 secs
    public static final int PRESET_MAX_TIME = 600; //10 mins
    public static final int PRESET_INTERVAL_TIME = 30;

    public static final int WARNING_MIN_TIME = 5;
    public static final int WARNING_MAX_TIME = 60;
    public static final int WARNING_INTERVAL_TIME = 5;

    public static final int REACH_MIN = 30;
    public static final int REACH_MAX = 50;
    public static final int REACH_INTERVAL = 1;

    public static final int GLOVE_MIN = 4;
    public static final int GLOVE_MAX = 16;
    public static final int GLOVE_INTERVAL = 2;

    public static final int WEIGHT_MIN = 100; //lbs
    public static final int WEIGHT_MAX = 300;
    public static final int WEIGHT_INTERVAL = 1;

    public static final int AGE_MAX = 80;
    public static final int AGE_MIN = 15;

    public static final int HEIGHT_MIN = 50; //inch
    public static final int HEIGHT_MAX = 100;
    public static final int HEIGHT_INTERVAL = 1;

    public static final String SESSIONID = "sessionid";
    public static final String SESSIONSTARTTIME = "starttime";

    public static final int MEASURE_DATE = 0;

    public static final String USER_INTENT = "userintent";



    public static final String EMPTY_FIELD_CONNECTION_ERROR_MESSAGE = "Unable to connect with empty field.";
    public static final String INCORRECT_SENSOR_ID_MESSAGE = "Sensor id is not correct = ";
    public static final String UNABLE_TO_CONNECT_WITH_SENSOR_MESSAGE_TEXT = "Unable to connect sensor.";
    public static final String SENSOR_CONNECTION_LOST_MESSAGE_TEXT = "Sensor connection was lost.";
    public static final String SENSOR_CONNECTION_ESTABLISHED_MESSAGE = "Connection established with sensor ";
    public static final String CONNECTED_DEVICE_TEXT = "ConnectedDevice";
    public static final String DISCONNECTED_DEVICE_TEXT = "DisconnectedDevice";
    public static final String SENSOR_CONNECTION_FAILED_MESSAGE = "Failed to connect with sensor = ";
    public static final String BLUETOOTH_SOCKET_CONNECTION_FAILED_MESSAGE = "Failed to connect with bluetooth socket.";

    public static final int REQUEST_ENABLE_BT = 1;
    public static final int MESSAGE_TOAST = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final String TOAST = "TOAST";
    public static final String ALREADY_CONNECTED_MESSAGE_TEXT = "Sensor already connected.";

    public static final String DATE_FORMAT = "yyyy_MMM_dd_HH_mm_ss_SSS";
    public static final String DEFAULT_START_TIME = "00:00:00";

    public static final double GUEST_TRAINING_EFFECTIVE_PUNCH_MASS = 3.0;

    public static final  int SENSOR_LIMIT = 2;

    public static final String TYPE = "type";
    public static final String COUNTRY = "country";
    public static final String STATE = "state";
    public static final String CITY = "city";

    //---------------------------- Device constants Start ----------------------
    public static final int START_BYTE = 170;
    public static final int LOW_G_MODE = 64;
    public static final int HIGH_G_MODE = 65;
    public static final int GYRO_MODE = 66;
    public static final int BATTERY_MODE = 67;
    public static final int MESSAGE_LENGTH_LSB = 104;
    public static final int MESSAGE_LENGTH_BATTERY = 6;
    public static final int MESSAGE_LENGTH_MSB = 0;
    public static final int SAMPLE_PACKET_SIZE = 16;

    //----------------------------- Stance Type --------------------------------
    public static final String NON_TRADITIONAL_TEXT = "Non-Traditional";
    public static final String TRADITIONAL_TEXT = "Traditional";
    public static final String NON_TRADITIONAL = "Non-Traditional (right foot front)";
    public static final String TRADITIONAL = "Traditional- Left foot front";

    //----------------------- Punch Type Abbreviation texts ----------------------
    public static final String JAB_ABBREVIATION_TEXT = "J";
    public static final String STRAIGHT_ABBREVIATION_TEXT = "S";
    public static final String HOOK_ABBREVIATION_TEXT = "H";
    public static final String UPPERCUT_ABBREVIATION_TEXT = "U";
    public static final String UNRECOGNIZED_ABBREVIATION_TEXT = "UR";

    //----------------------------- Punch Types --------------------------------
    public static final String LEFT_JAB = "LJ";
    public static final String RIGHT_JAB = "RJ";
    public static final String LEFT_STRAIGHT = "LS";
    public static final String RIGHT_STRAIGHT = "RS";
    public static final String LEFT_HOOK = "LH";
    public static final String RIGHT_HOOK = "RH";
    public static final String LEFT_UPPERCUT = "LU";
    public static final String RIGHT_UPPERCUT = "RU";
    public static final String LEFT_UNRECOGNIZED = "LR";
    public static final String RIGHT_UNRECOGNIZED = "RR";
    public static final String JAB = "JAB";
    public static final String STRAIGHT = "STRAIGHT";
    public static final String HOOK = "HOOK";
    public static final String UPPERCUT = "UPPERCUT";
    public static final String UNRECOGNIZED = "UNRECOGNIZED";
    public static final String UNIDENTIFIED = "UNIDENTIFIED";

    //-------------------------------- Boxer Hand ------------------------------
    public static final String LEFT_HAND = "left";
    public static final String RIGHT_HAND = "right";

    //------------------ Force Formula calculation type -------------------------
    public static final String OLD_FORCE_FORMULA = "0";
    public static final String CSV_FORCE_FORMULA = "1";

    public static final int PHONE_HISTORY_GRAPH_LENGTH = 15;
    public static final int TABLET_HISTORY_GRAPH_LENGTH = 24;

    public static final String DEVICE_ID_MUST_NOT_BE_BLANK_ERROR = "No sensors assigned for selected boxer.";
    public static final String DEVICE_ID_MUST_NOT_BE_BLANK = "Sensor id cannot be blank.";
    public static final String DEVICE_ID_MUST_NOT_BE_SAME = "Sensor ids cannot be same.";


    public static final String TYPE_COMBO = "combo";
    public static final String TYPE_SET = "set";
    public static final String TYPE_WORKOUT = "workout";

    public static final int SPEED_MAX = 50;
    public static final int POWER_MAX = 1250;
    public static final int GRID_COUNT = 15;

    public static final String ROUND_SETTINGS = "roundsettings";
    public static final String ROUNDTRAINING = "roundtraining";
    public static final String PRESET = "preset";

    public static final String TRAININGTYPE = "trainingtype";
    public static final String COMBINATION = "combination";
    public static final String SETS = "sets";
    public static final String WORKOUT = "workout";

    public static final String COMBO_ID = "comboid";
    public static final String SET_ID = "setid";
    public static final String WORKOUT_ID = "workoutid";

    public static final int MAX_NUM_FORPUNCH = 7;

    public static final String SESSIONS = "sessions";
    public static final String START_TIME = "starttime";
    public static final String SESSION_INTENT = "sessionintent";
    public static final String LAST_SESSION = "lastsession";
    public static final String ROUND_INTENT = "roundintent";
    public static final String COMBO_INTENT = "combointent";
    public static final String PLAN_ID = "planid";
    public static final String WORKOUT_ROUND_POSITION = "roundposition";
    public static final String FROM_ROUND = "fromround";
    public static final String FROM_SESSION = "fromsession";
    public static final String START_DATE = "startdate";
    public static final String END_DATE = "enddate";
    public static final String IS_GOAL = "is_goal";

    public static final String FEED_TYPE = "feedtype";
    public static final String GUIDANCE_TYPE_WORKOUTS = "workoutroutines";
    public static final String GUIDANCE_TYPE_TUTORIALS = "tutorials";
    public static final String GUIDANCE_TYPE_DRILLS = "drills";
    public static final String GUIDANCE_TYPE_ESSENTIALS = "essentials";

    public static final String ONBOARD_FROMMORE = "onboardfrommore";
    public static final String VIDEO_INTENT = "video";
    public static final String VIDEO_POSITION = "videoposition";

    public static final int ANIMATION_DURATION = 300;
    public static final int SHOW_TIME = 4000;

    public static final String FEED_EXPLORE = "explore";
    public static final String FEED_LEADERBOARD = "leaderboard";
    public static final String FEED_COMPARE_LEADERBOARD = "compareleaderboard";

    public static final int COUNTRY_REQUEST_CODE  = 123;
    public static final int STATE_REQUEST_CODE = 124;
    public static final int CITY_REQUEST_CODE = 125;
    public static final int FILTER_REQUEST_CODE = 126;

    public static final String MIN_WEIGHT = "minweight";
    public static final String MAX_WEIGHT = "maxweight";
    public static final String MIN_AGE = "minage";
    public static final String MAX_AGE = "maxage";
    public static final String GENDER = "gender";
    public static final String FILTER = "filter";
    public static final String TYPE_RECEIVED_BATTLES = "receivedbattles";
    public static final String TYPE_MY_BATTLES = "mybattles";
    public static final String TYPE_FINISHED_BATTLES = "finishedbattles";
    public static final int REQUEST_CODE_SPEECH_INPUT = 1001;
    public static final int RequestPermissionCode = 123;
}
