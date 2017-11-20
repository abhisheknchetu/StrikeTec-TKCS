package com.striketec.mobile.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.util.StringBuilderPrinter;

import com.striketec.mobile.dto.DBTrainingRoundDto;
import com.striketec.mobile.dto.DBTrainingSessionDto;
import com.striketec.mobile.dto.SyncResponseDto;
import com.striketec.mobile.dto.DBTrainingPunchDto;
import com.striketec.mobile.dto.UserDto;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by Qiang on 8/20/2017.
 */

public class DBAdapter {

    // A string tag to display message in log file.
    private static final String TAG = DBAdapter.class.getSimpleName();

    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    TimeZone utcTimeZone = TimeZone.getTimeZone("UTC");

    // A initialization for database version.
    private static final int DATABASE_VERSION = 1; //TODO:- Note- When ever you do changes/modification in your database. Please upgrade the version.

    // A String DATABASE_NAME contains database EFD_TrainerApp_DB
    private static final String DATABASE_NAME = AppConst.DATABASE_NAME;

    // ***************** training session **********************/
    private static final String TRAINING_SESSION_TABLE = "training_session";
    //start_time is unique value, and server will update this.

    private static final String KEY_TRAINING_SESSION_ID = "id";
    private static final String KEY_TRAINING_SESSION_SERVER_USER_ID = "user_id";
    private static final String KEY_TRAINING_SESSION_TRAINING_TYPE = "training_type"; // quick/round/workout/combo/set
    private static final String KEY_TRAINING_SESSION_SERVER_PLAN_ID = "plan_id";
    private static final String KEY_TRAINING_SESSION_START_TIME = "start_time"; //long value
    private static final String KEY_TRAINING_SESSION_END_TIME = "end_time";  //long value
    private static final String KEY_TRAINING_SESSION_AVG_SPEED = "avg_speed";
    private static final String KEY_TRAINING_SESSION_MAX_SPEED = "max_speed";
    private static final String KEY_TRAINING_SESSION_AVG_FORCE = "avg_force";
    private static final String KEY_TRAINING_SESSION_MAX_FORCE = "max_force";
    private static final String KEY_TRAINING_SESSION_PUNCHES_COUNT = "punches_count";
    private static final String KEY_TRAINING_SESSION_BEST_TIME = "best_time";
    private static final String KEY_TRAINING_SESSION_FINISHED = "finished"; //0: training, 1:finished
    private static final String KEY_TRAINING_SESSION_SYNC = "sync";  // o : unsynced

    // Query string for creating table TRAINING_SESSION_TABLE .
    private static final String DATABASE_CREATE_TRAINING_SESSION_TABLE = "create table "
            + TRAINING_SESSION_TABLE
            + " (id integer NOT NULL primary key autoincrement, "
            + " user_id integer(20) NOT NULL, "
            + " training_type text(50) DEFAULT NULL, "
            + " plan_id integer(20) NOT NULL, "
            + " start_time text NOT NULL, "
            + " end_time text DEFAULT NULL, "
            + " avg_speed double default 0, "
            + " max_speed double default 0, "
            + " avg_force double default 0, "
            + " max_force double default 0, "
            + " punches_count integer default 0, "
            + " best_time float default 0, "
            + " finished integer(1) default 0, "
            + " sync integer(1) default 0); ";

    public static String getTrainingSessionTable() {
        return TRAINING_SESSION_TABLE;
    }

    // ********** training round table *************************
    private static final String TRAINING_ROUND_TABLE = "training_round";

    private static final String KEY_TRAINING_ROUND_ID = "id";
    private static final String KEY_TRAINING_ROUND_SERVER_USER_ID = "user_id";
    private static final String KEY_TRAINING_ROUND_SESSION_START_TIME = "session_start_time"; // to identify with training session table
    private static final String KEY_TRAINING_ROUND_START_TIME = "start_time"; //long value
    private static final String KEY_TRAINING_ROUND_END_TIME = "end_time";  //long value
    private static final String KEY_TRAINING_ROUND_AVG_SPEED = "avg_speed";
    private static final String KEY_TRAINING_ROUND_MAX_SPEED = "max_speed";
    private static final String KEY_TRAINING_ROUND_AVG_FORCE = "avg_force";
    private static final String KEY_TRAINING_ROUND_MAX_FORCE = "max_force";
    private static final String KEY_TRAINING_ROUND_PUNCHES_COUNT = "punches_count";
    private static final String KEY_TRAINING_ROUND_BEST_TIME = "best_time";

    private static final String KEY_TRAINING_ROUND_FINISHED = "finished"; //0: training, 1:finished
    private static final String KEY_TRAINING_ROUND_SYNC = "sync";  // o : unsynced

    // Query string for creating table TRAINING_SESSION_TABLE .
    private static final String DATABASE_CREATE_TRAINING_ROUND_TABLE = "create table "
            + TRAINING_ROUND_TABLE
            + " (id integer NOT NULL primary key autoincrement, "
            + " user_id integer(20) NOT NULL, "
            + " session_start_time text NOT NULL, "
            + " start_time text NOT NULL, "
            + " end_time text DEFAULT NULL, "
            + " avg_speed double default 0, "
            + " max_speed double default 0, "
            + " avg_force double default 0, "
            + " max_force double default 0, "
            + " punches_count integer default 0, "
            + " best_time float default 0, "
            + " finished integer(1) default 0, "
            + " sync integer(1) default 0); ";

    public static String getTrainingRoundTable() {
        return TRAINING_ROUND_TABLE;
    }

    //************************** training punch detail, for graph for previous training **************//
    private static final String TRAINING_PUNCH_TABLE = "training_punch";

    private static final String KEY_TRAINING_PUNCH_DETAIL_ID = "id";
    private static final String KEY_TRAINING_PUNCH_SERVER_USER_ID = "user_id";
    private static final String KEY_TRAINING_PUNCH_ROUND_START_TIME = "round_start_time";
    private static final String KEY_TRAINING_PUNCH_PUNCHTIME = "punch_time";
    private static final String KEY_TRAINING_PUNCH_PUNCHTYPE = "punch_type";
    private static final String KEY_TRAINING_PUNCH_PUNCHHAND = "hand";
    private static final String KEY_TRAINING_PUNCH_DURATION = "punch_duration";
    private static final String KEY_TRAINING_PUNCH_FORCE = "force";
    private static final String KEY_TRAINING_PUNCH_SPEED = "speed";
    private static final String KEY_TRAINING_PUNCH_SYNC = "sync";

    public static String getTrainingPunchTable(){
        return TRAINING_PUNCH_TABLE;
    }

    private static final String DATABASE_CREATE_TRAINING_PUNCH_TABLE = "create table "
            + TRAINING_PUNCH_TABLE
            + " (id integer NOT NULL primary key autoincrement, "
            + " user_id interger(20) NOT NULL, "
            + " round_start_time text NOT NULL, "
            + " punch_time text NOT NULL, "
            + " punch_type text DEFAULT null, "
            + " hand text DEFAULT null, "
            + " punch_duration float default 0, "
            + " force integer default 0, "
            + " speed integer default 0, "
            + " sync integer(1) default 0 ); ";

    //*********************** user table ****************************
    private static final String USER_TABLE = "user";
    public static String getUserTable() {
        return USER_TABLE;
    }

    public static final String KEY_USER_ID = "id";
    public static final String KEY_USER_SERVER_ID = "user_server_id";
    public static final String KEY_USER_FACEBOOK_ID = "facebook_id";
    public static final String KEY_USER_FIRST_NAME = "first_name";
    public static final String KEY_USER_LAST_NAME = "last_name";
    public static final String KEY_USER_EMAIL = "email";
    public static final String KEY_USER_PASSWORD = "password";
    public static final String KEY_USER_PHOTO = "photo";
    public static final String KEY_USER_SHOWTIP = "showtip";
    public static final String KEY_USER_GENDER = "gender";
    public static final String KEY_USER_BIRTHDAY = "birthday";
    public static final String KEY_USER_WEIGHT = "weight";
    public static final String KEY_USER_HEIGHT = "height";
    public static final String KEY_USER_SKILL_LEVEL = "skilllevel";
    public static final String KEY_USER_LEFT_HAND_SENSOR = "lefthandsensor";
    public static final String KEY_USER_RIGHT_HAND_SENSOR = "righthandsensor";
    public static final String KEY_USER_LEFT_KICK_SENSOR = "leftkicksensor";
    public static final String KEY_USER_RIGHT_KICK_SENSOR = "rightkicksensor";
    public static final String KEY_USER_IS_SPECTATOR = "isspectator";
    public static final String KEY_USER_STANCE = "stance";
    public static final String KEY_USER_CREATED_AT = "createdat";
    public static final String KEY_USER_UPDATED_AT = "updatedat";

    // Query string for creating table DATABASE_CREATE_USER .
    private static final String DATABASE_CREATE_USER_TABLE = "create table "
            + USER_TABLE
            + " (id  integer NOT NULL primary key autoincrement, "
            + " user_server_id integer(20) NOT NULL,"
            + " facebook_id integer(20) DEFAULT NULL, "
            + " first_name text(50) DEFAULT NULL, "
            + " last_name text(50) DEFAULT NULL, "
            + " email text(50) DEFAULT NULL, "
            + " password text(255) DEFAULT NULL, "
            + " photo text(10) DEFAULT NULL, "
            + " showtip integer(1) default 1, "
            + " gender text(50) DEFAULT NULL, "
            + " birthday text(255) DEFAULT NULL, "
            + " weight integer DEFAULT NULL, "
            + " height integer DEFAULT NULL, "
            + " skilllevel text(255) DEFAULT NULL, "
            + " lefthandsensor text(255) DEFAULT NULL, "
            + " righthandsensor text(255) DEFAULT NULL, "
            + " leftkicksensor text(255) DEFAULT NULL, "
            + " rightkicksensor text(255) DEFAULT NULL, "
            + " isspectator integer(1) default 0, "
            + " stance text(50) DEFAULT NULL, "
            + " createdat text(255) DEFAULT NULL, "
            + " updatedat text(255) DEFAULT NULL ); ";

    // Object reference for context.
    private final Context context;

    // Object reference for DatabaseHelper.
    private static DBAdapter.DatabaseHelper DBHelper;

    // Object reference for SQLiteDatabase.
    private SQLiteDatabase db;

    private static DBAdapter instance = null;
    private static String dbDirectoryPath = null;

    private DBAdapter(Context ctx) {
        this.context = ctx;

        File myDirectory = new File(context.getExternalFilesDir(null), AppConst.DATABASE_DIRECTORY);

        if (!myDirectory.exists()) {
            myDirectory.mkdirs();
        }

        dbDirectoryPath = myDirectory.getAbsolutePath();

        DBHelper = new DBAdapter.DatabaseHelper(context);
    }

    public static DBAdapter getInstance(Context ctx) {
        if (instance == null) {
            instance = new DBAdapter(ctx);
            instance.open();


        }
        return instance;
    }

    public static void clearDBAdapter() {
        instance = null;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {

        /**
         * Constructor for database helper class.
         *
         * @param context
         */
        DatabaseHelper(Context context) {
            super(context, dbDirectoryPath + File.separator + DATABASE_NAME, null, DATABASE_VERSION);
            setWriteAheadLoggingEnabled(true);
        }

        /**
         * Called when the database is created for the first time. This is where
         * the creation of tables and the initial population of the tables
         * should happen. Parameters: db The database.
         */
        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(DATABASE_CREATE_TRAINING_SESSION_TABLE);
            Log.d(TAG, "created table " + DATABASE_CREATE_TRAINING_SESSION_TABLE);

            db.execSQL(DATABASE_CREATE_TRAINING_ROUND_TABLE);
            Log.d(TAG, "created table " + DATABASE_CREATE_TRAINING_ROUND_TABLE);

            db.execSQL(DATABASE_CREATE_TRAINING_PUNCH_TABLE);
            Log.d(TAG, "created table " + DATABASE_CREATE_TRAINING_PUNCH_TABLE);

            db.execSQL(DATABASE_CREATE_USER_TABLE);
            Log.d(TAG, "created table " + DATABASE_CREATE_USER_TABLE);
        }

        /**
         * Called when the database needs to be upgraded. The implementation
         * should use this method to drop tables, add tables, or do anything
         * else it needs to upgrade to the new schema version. The SQLite ALTER
         * TABLE documentation can be found here. If you add new columns you can
         * use ALTER TABLE to insert them into a live table. If you rename or
         * remove columns you can use ALTER TABLE to rename the old table, then
         * create the new table and then populate the new table with the
         * contents of the old table. Parameters: db The database. oldVersion
         * The old database version. newVersion: The new database version.
         */
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            // TODO: For production version we need to write alter table & data migration scripts here for according to version numbers instead of dropping table & recreating it.

            Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + TRAINING_SESSION_TABLE);

            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + TRAINING_SESSION_TABLE);

            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + TRAINING_ROUND_TABLE);

            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + TRAINING_PUNCH_TABLE);

            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "  + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);

            onCreate(db);
        }

    }

    /**
     * Opens the database connection.
     *
     * @return
     * @throws SQLException
     */
    public DBAdapter open() throws SQLException {
        try {

            db = DBHelper.getWritableDatabase();
            Log.i(TAG, db.getPath());

        } catch (Throwable e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * Closes the database connection.
     */
    public void close() {
        DBHelper.close();
    }


    //create training session
    public String insertTrainingSession(int userId, String trainingType, int planId) {

        ContentValues values = new ContentValues();
        String startTime = String.valueOf(System.currentTimeMillis());

        values.put(KEY_TRAINING_SESSION_SERVER_USER_ID, userId);
        values.put(KEY_TRAINING_SESSION_TRAINING_TYPE, trainingType);
        values.put(KEY_TRAINING_SESSION_SERVER_PLAN_ID, planId);
        values.put(KEY_TRAINING_SESSION_START_TIME, startTime);
        values.put(KEY_TRAINING_SESSION_FINISHED, 0);
        values.put(KEY_TRAINING_SESSION_SYNC, 0);

        db.insert(TRAINING_SESSION_TABLE, null, values);

        return startTime;
    }

    //end training session
    public void endTrainingSession (int userId, String trainingsessionStartTime){
        String selectQuery = "SELECT * FROM " + TRAINING_SESSION_TABLE + " WHERE " + KEY_TRAINING_SESSION_SERVER_USER_ID + "='"  + userId
                + "' AND " + KEY_TRAINING_SESSION_START_TIME + "='" + trainingsessionStartTime + "'";

        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            int count = cursor.getCount();

            if (count > 0) {
                cursor.moveToFirst();

                String endTime = String.valueOf(System.currentTimeMillis());

                ContentValues values = new ContentValues();

                //get end time from training punch table
                values.put(KEY_TRAINING_SESSION_FINISHED, 1);
                values.put(KEY_TRAINING_SESSION_END_TIME, endTime);
                db.update(TRAINING_SESSION_TABLE, values, KEY_TRAINING_SESSION_ID + " = " + cursor.getInt(cursor.getColumnIndex(KEY_TRAINING_SESSION_ID)), null);
                cursor.moveToNext();
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
    }

    public void syncedTrainingSession(int userId, String trainingsessionStartTime){
        String selectQuery = "SELECT * FROM " + TRAINING_SESSION_TABLE + " WHERE " + KEY_TRAINING_SESSION_SERVER_USER_ID + "='"  + userId
                + "' AND " + KEY_TRAINING_SESSION_START_TIME + "='" + trainingsessionStartTime + "'";

        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            int count = cursor.getCount();

            if (count > 0) {
                cursor.moveToFirst();

                ContentValues values = new ContentValues();

                //get end time from training punch table
                values.put(KEY_TRAINING_SESSION_SYNC, 1);
                db.update(TRAINING_SESSION_TABLE, values, KEY_TRAINING_SESSION_ID + " = " + cursor.getInt(cursor.getColumnIndex(KEY_TRAINING_SESSION_ID)), null);
                cursor.moveToNext();
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
    }

    public void syncedTrainingRound(int userId, String trainingroundStarttime){
        String selectQuery = "SELECT * FROM " + TRAINING_ROUND_TABLE + " WHERE " + KEY_TRAINING_ROUND_SERVER_USER_ID + "='"  + userId
                + "' AND " + KEY_TRAINING_ROUND_START_TIME + "='" + trainingroundStarttime + "'";

        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            int count = cursor.getCount();

            if (count > 0) {
                cursor.moveToFirst();

                ContentValues values = new ContentValues();

                //get end time from training punch table
                values.put(KEY_TRAINING_ROUND_SYNC, 1);
                db.update(TRAINING_ROUND_TABLE, values, KEY_TRAINING_ROUND_ID + " = " + cursor.getInt(cursor.getColumnIndex(KEY_TRAINING_ROUND_ID)), null);
                cursor.moveToNext();
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
    }


    public void syncedTrainingPunch(int userId, String trainingpunchTime){
        String selectQuery = "SELECT * FROM " + TRAINING_PUNCH_TABLE + " WHERE " + KEY_TRAINING_PUNCH_SERVER_USER_ID + "='"  + userId
                + "' AND " + KEY_TRAINING_PUNCH_PUNCHTIME + "='" + trainingpunchTime + "'";

        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            int count = cursor.getCount();

            if (count > 0) {
                cursor.moveToFirst();

                ContentValues values = new ContentValues();

                //get end time from training punch table
                values.put(KEY_TRAINING_PUNCH_SYNC, 1);
                db.update(TRAINING_PUNCH_TABLE, values, KEY_TRAINING_PUNCH_DETAIL_ID + " = " + cursor.getInt(cursor.getColumnIndex(KEY_TRAINING_PUNCH_DETAIL_ID)), null);
                cursor.moveToNext();
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
    }



    //create training round
    public String insertTrainingRound (int userId, String trainingSessionStartTime){
        ContentValues values = new ContentValues();
        String startTime = String.valueOf(System.currentTimeMillis());

        values.put(KEY_TRAINING_ROUND_SERVER_USER_ID, userId);
        values.put(KEY_TRAINING_ROUND_SESSION_START_TIME, trainingSessionStartTime);
        values.put(KEY_TRAINING_ROUND_START_TIME, startTime);
        values.put(KEY_TRAINING_ROUND_FINISHED, 0);
        values.put(KEY_TRAINING_ROUND_SYNC, 0);

        db.insert(TRAINING_ROUND_TABLE, null, values);

        return startTime;
    }

    //end training round
    public void endTrainingRound (int userId, String trainingsessionStartTime, String trainingroundStartTime){
        String selectQuery = "SELECT * FROM " + TRAINING_ROUND_TABLE + " WHERE " + KEY_TRAINING_ROUND_SERVER_USER_ID + "='"  + userId
                + "' AND " + KEY_TRAINING_ROUND_SESSION_START_TIME + "='" + trainingsessionStartTime
                + "' AND " + KEY_TRAINING_ROUND_START_TIME + "='" + trainingroundStartTime + "'";

        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            int count = cursor.getCount();

            if (count > 0) {
                cursor.moveToFirst();

                String endTime = String.valueOf(System.currentTimeMillis());
                ContentValues values = new ContentValues();

                //get end time from training punch table
                values.put(KEY_TRAINING_ROUND_FINISHED, 1);
                values.put(KEY_TRAINING_ROUND_END_TIME, endTime);
                db.update(TRAINING_ROUND_TABLE, values, KEY_TRAINING_ROUND_ID + " = " + cursor.getInt(cursor.getColumnIndex(KEY_TRAINING_ROUND_ID)), null);
                cursor.moveToNext();
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
    }

    //create training punch
    public void insertTrainingPunch (int userId, DBTrainingPunchDto punchDto){
        ContentValues values = new ContentValues();
        values.put(KEY_TRAINING_PUNCH_SERVER_USER_ID, userId);
        values.put(KEY_TRAINING_PUNCH_ROUND_START_TIME, punchDto.mRoundStartTime);
        values.put(KEY_TRAINING_PUNCH_PUNCHTIME, punchDto.mPunchTime);
        values.put(KEY_TRAINING_PUNCH_PUNCHTYPE, punchDto.mPunchType);
        values.put(KEY_TRAINING_PUNCH_PUNCHHAND, punchDto.mHand);
        values.put(KEY_TRAINING_PUNCH_DURATION, punchDto.mPunchDuration);
        values.put(KEY_TRAINING_PUNCH_FORCE, punchDto.mForce);
        values.put(KEY_TRAINING_PUNCH_SPEED, punchDto.mSpeed);
        values.put(KEY_TRAINING_PUNCH_SYNC, 0);

        db.insert(TRAINING_PUNCH_TABLE, null, values);

        //update training round, session table
        updateTrainingRound(userId, punchDto);
    }

    private void updateTrainingRound(int userId, DBTrainingPunchDto punchDto){
        String selectQuery = "SELECT * FROM " + TRAINING_ROUND_TABLE + " WHERE " + KEY_TRAINING_ROUND_SERVER_USER_ID + "='"  + userId
                + "' AND " + KEY_TRAINING_ROUND_START_TIME + "='" + punchDto.mRoundStartTime + "'";

        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            int count = cursor.getCount();

            if (count > 0) {
                cursor.moveToFirst();

                double avgSpeed = cursor.getDouble(cursor.getColumnIndex(KEY_TRAINING_ROUND_AVG_SPEED));
                double avgForce = cursor.getDouble(cursor.getColumnIndex(KEY_TRAINING_ROUND_AVG_FORCE));
                double maxSpeed = cursor.getDouble(cursor.getColumnIndex(KEY_TRAINING_ROUND_MAX_SPEED));
                double maxForce = cursor.getDouble(cursor.getColumnIndex(KEY_TRAINING_ROUND_MAX_FORCE));
                float bestTime = cursor.getFloat(cursor.getColumnIndex(KEY_TRAINING_ROUND_BEST_TIME));
                int punchesCount = cursor.getInt(cursor.getColumnIndex(KEY_TRAINING_ROUND_PUNCHES_COUNT));

                if (bestTime == 0)
                    bestTime = punchDto.mPunchDuration;
                else
                    bestTime = Math.min(bestTime, punchDto.mPunchDuration);

                avgSpeed = (avgSpeed * punchesCount + punchDto.mSpeed) / (punchesCount + 1);
                avgForce = (avgForce * punchesCount + punchDto.mForce) / (punchesCount + 1);
                maxSpeed = Math.max(maxSpeed, punchDto.mSpeed);
                maxForce = Math.max(maxForce, punchDto.mForce);
                punchesCount = punchesCount + 1;

                ContentValues values = new ContentValues();

                values.put(KEY_TRAINING_ROUND_AVG_SPEED, avgSpeed);
                values.put(KEY_TRAINING_ROUND_MAX_SPEED, maxSpeed);
                values.put(KEY_TRAINING_ROUND_AVG_FORCE, avgForce);
                values.put(KEY_TRAINING_ROUND_MAX_FORCE, maxForce);
                values.put(KEY_TRAINING_ROUND_BEST_TIME, bestTime);
                values.put(KEY_TRAINING_ROUND_PUNCHES_COUNT, punchesCount);

                db.update(TRAINING_ROUND_TABLE, values, KEY_TRAINING_ROUND_ID + " = " + cursor.getInt(cursor.getColumnIndex(KEY_TRAINING_ROUND_ID)), null);
                updateTrainingSession(userId, cursor.getString(cursor.getColumnIndex(KEY_TRAINING_ROUND_SESSION_START_TIME)), punchDto);

            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
    }

    private void updateTrainingSession(int userId, String trainingsessionStartTime, DBTrainingPunchDto punchDto){
        String selectQuery = "SELECT * FROM " + TRAINING_SESSION_TABLE + " WHERE " + KEY_TRAINING_SESSION_SERVER_USER_ID + "='"  + userId
                + "' AND " + KEY_TRAINING_SESSION_START_TIME + "='" + trainingsessionStartTime + "'";

        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            int count = cursor.getCount();

            if (count > 0) {
                cursor.moveToFirst();

                double avgSpeed = cursor.getDouble(cursor.getColumnIndex(KEY_TRAINING_SESSION_AVG_SPEED));
                double avgForce = cursor.getDouble(cursor.getColumnIndex(KEY_TRAINING_SESSION_AVG_FORCE));
                double maxSpeed = cursor.getDouble(cursor.getColumnIndex(KEY_TRAINING_SESSION_MAX_SPEED));
                double maxForce = cursor.getDouble(cursor.getColumnIndex(KEY_TRAINING_SESSION_MAX_FORCE));
                float bestTime = cursor.getFloat(cursor.getColumnIndex(KEY_TRAINING_SESSION_BEST_TIME));
                int punchesCount = cursor.getInt(cursor.getColumnIndex(KEY_TRAINING_SESSION_PUNCHES_COUNT));

                if (bestTime == 0)
                    bestTime = punchDto.mPunchDuration;
                else
                    bestTime = Math.min(bestTime, punchDto.mPunchDuration);

                avgSpeed = (avgSpeed * punchesCount + punchDto.mSpeed) / (punchesCount + 1);
                avgForce = (avgForce * punchesCount + punchDto.mForce) / (punchesCount + 1);
                maxSpeed = Math.max(maxSpeed, punchDto.mSpeed);
                maxForce = Math.max(maxForce, punchDto.mForce);
                punchesCount = punchesCount + 1;

                ContentValues values = new ContentValues();

                values.put(KEY_TRAINING_SESSION_AVG_SPEED, avgSpeed);
                values.put(KEY_TRAINING_SESSION_MAX_SPEED, maxSpeed);
                values.put(KEY_TRAINING_SESSION_AVG_FORCE, avgForce);
                values.put(KEY_TRAINING_SESSION_MAX_FORCE, maxForce);
                values.put(KEY_TRAINING_SESSION_BEST_TIME, bestTime);
                values.put(KEY_TRAINING_SESSION_PUNCHES_COUNT, punchesCount);

                db.update(TRAINING_SESSION_TABLE, values, KEY_TRAINING_SESSION_ID + " = " + cursor.getInt(cursor.getColumnIndex(KEY_TRAINING_SESSION_ID)), null);

            }
        } finally {
            if (cursor != null)
                cursor.close();
        }

    }

    public void endAllTrainingSession(){
        String selectQuery = "SELECT  * FROM " + TRAINING_SESSION_TABLE + " WHERE " + KEY_TRAINING_SESSION_FINISHED + "='" + 0 + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            int count = cursor.getCount();
            if (count > 0) {
                cursor.moveToFirst();

                String lastTime = getLastEndTimeFromRoundTable(cursor.getInt(cursor.getColumnIndex(KEY_TRAINING_SESSION_SERVER_USER_ID)),
                        cursor.getString(cursor.getColumnIndex(KEY_TRAINING_SESSION_START_TIME)));

                ContentValues values = new ContentValues();

                //get end time from training punch table
                values.put(KEY_TRAINING_SESSION_FINISHED, 1);
                values.put(KEY_TRAINING_SESSION_END_TIME, lastTime);
                db.update(TRAINING_SESSION_TABLE, values, KEY_TRAINING_SESSION_ID + " = " + cursor.getInt(cursor.getColumnIndex(KEY_TRAINING_SESSION_ID)), null);
                cursor.moveToNext();
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
    }

    public void endAllTrainingRounds(){
        String selectQuery = "SELECT  * FROM " + TRAINING_ROUND_TABLE + " WHERE " + KEY_TRAINING_ROUND_FINISHED + "='" + 0 + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            int count = cursor.getCount();
            if (count > 0) {
                cursor.moveToFirst();

                String lastTime = getLastPunchTimeFromPunchTable(cursor.getInt(cursor.getColumnIndex(KEY_TRAINING_ROUND_SERVER_USER_ID)),
                        cursor.getString(cursor.getColumnIndex(KEY_TRAINING_ROUND_START_TIME)));

                if (!lastTime.equalsIgnoreCase("0")) {

                    ContentValues values = new ContentValues();

                    //get end time from training punch table
                    values.put(KEY_TRAINING_ROUND_FINISHED, 1);
                    values.put(KEY_TRAINING_ROUND_END_TIME, lastTime);
                    db.update(TRAINING_ROUND_TABLE, values, KEY_TRAINING_ROUND_ID + " = " + cursor.getInt(cursor.getColumnIndex(KEY_TRAINING_ROUND_ID)), null);
                }
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
    }

    private String getLastPunchTimeFromPunchTable (int userId, String roundstarttime){
        String lastTime = "0";
        String query = "SELECT "
                + KEY_TRAINING_PUNCH_PUNCHTIME
                + " from " + TRAINING_PUNCH_TABLE
                + " where " + KEY_TRAINING_PUNCH_SERVER_USER_ID + " ='" + userId + "' and "
                + KEY_TRAINING_PUNCH_ROUND_START_TIME + " = ' " + roundstarttime + "'"
                + "ORDER BY " + TRAINING_PUNCH_TABLE + "." + KEY_TRAINING_PUNCH_PUNCHTIME + " DESC " +  ";";

        Cursor cursor = db.rawQuery(query, null);

        try {
            cursor.moveToFirst();

            if (cursor.moveToFirst()) {
                lastTime = cursor.getString(0);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }

        return lastTime;
    }

    private String getLastEndTimeFromRoundTable (int userId, String sessionstarttime){
        String lastTime = "0";

        String query = "SELECT "
                + KEY_TRAINING_ROUND_END_TIME
                + " from " + TRAINING_ROUND_TABLE
                + " where " + KEY_TRAINING_ROUND_SERVER_USER_ID + " ='" + userId + "' and "
                + KEY_TRAINING_ROUND_SESSION_START_TIME + " = ' " + sessionstarttime + "'"
                + "ORDER BY " + TRAINING_ROUND_TABLE + "." + KEY_TRAINING_ROUND_END_TIME + " DESC " +  ";";

        Cursor cursor = db.rawQuery(query, null);

        try {
            cursor.moveToFirst();

            if (cursor.moveToFirst()) {
                lastTime = cursor.getString(0);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }

        return lastTime;
    }

    //********************** super added get nonsynced records for each table ******************************//
    public ArrayList<DBTrainingSessionDto> getAllNonSynchronizedTrainingSessionRecords(int limit, int userid) {

        DBTrainingSessionDto dbTrainingSessionDTO = null;
        ArrayList<DBTrainingSessionDto> list = new ArrayList<DBTrainingSessionDto>();

        String selectQuery = "SELECT * FROM " + TRAINING_SESSION_TABLE + " WHERE " + KEY_TRAINING_SESSION_SERVER_USER_ID + "='"  + userid
                + "' AND " + KEY_TRAINING_SESSION_SYNC + "='" + 0
                + "' AND " + KEY_TRAINING_SESSION_FINISHED + "='" + 1 + "'"
                + " limit " + limit + ";";

        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            cursor.moveToFirst();

            if (cursor.moveToFirst()) {
                do {
                    if (checkSessionHasRounds(cursor.getString(cursor.getColumnIndex(KEY_TRAINING_SESSION_START_TIME)))){
                        dbTrainingSessionDTO = new DBTrainingSessionDto(
                                cursor.getString(cursor.getColumnIndex(KEY_TRAINING_SESSION_TRAINING_TYPE)),
                                cursor.getInt(cursor.getColumnIndex(KEY_TRAINING_SESSION_SERVER_PLAN_ID)),
                                cursor.getString(cursor.getColumnIndex(KEY_TRAINING_SESSION_START_TIME)),
                                cursor.getString(cursor.getColumnIndex(KEY_TRAINING_SESSION_END_TIME)),
                                (int)cursor.getDouble(cursor.getColumnIndex(KEY_TRAINING_SESSION_AVG_SPEED)),
                                (int)cursor.getDouble(cursor.getColumnIndex(KEY_TRAINING_SESSION_MAX_SPEED)),
                                (int)cursor.getDouble(cursor.getColumnIndex(KEY_TRAINING_SESSION_AVG_FORCE)),
                                (int)cursor.getDouble(cursor.getColumnIndex(KEY_TRAINING_SESSION_MAX_FORCE)),
                                cursor.getInt(cursor.getColumnIndex(KEY_TRAINING_SESSION_PUNCHES_COUNT)),
                                cursor.getFloat(cursor.getColumnIndex(KEY_TRAINING_SESSION_BEST_TIME)));

                        list.add(dbTrainingSessionDTO);
                    }else {
                        db.delete(TRAINING_SESSION_TABLE, KEY_TRAINING_SESSION_ID + "=?", new String[]{String.valueOf(cursor.getInt(cursor.getColumnIndex(KEY_TRAINING_SESSION_ID)))});
                    }
                } while (cursor.moveToNext());

            }
        } finally {
            if (cursor != null)
                cursor.close();
        }

        return list;
    }

    private boolean checkSessionHasRounds(String trainingstarttime){
        String selectQuery = "SELECT  * FROM " + TRAINING_ROUND_TABLE + " WHERE " + KEY_TRAINING_ROUND_SESSION_START_TIME + "='" + trainingstarttime + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);

        boolean result = false;

        if (cursor.getCount() > 0){
            result = true;
        }

        if (cursor != null)
            cursor.close();

        return result;
    }

    private boolean checkTrainingSessionisFinishedOfRound(String sessionstarttime){
        //check training session is finished of round

        String selectQuery = "SELECT  * FROM " + TRAINING_SESSION_TABLE + " WHERE " + KEY_TRAINING_SESSION_START_TIME + "='" + sessionstarttime + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            if (cursor.getCount() > 0) {

                cursor.moveToFirst();

                if (cursor.getInt(cursor.getColumnIndex(KEY_TRAINING_SESSION_FINISHED)) == 1)
                    return true;
                else
                    return false;
            } else {
                return false;
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
    }


    public ArrayList<DBTrainingRoundDto> getAllNonSynchronizedTrainingRoundRecords(int limit, int userid) {

        DBTrainingRoundDto dbTrainingRoundDto = null;
        ArrayList<DBTrainingRoundDto> list = new ArrayList<DBTrainingRoundDto>();

        String selectQuery = "SELECT * FROM " + TRAINING_ROUND_TABLE + " WHERE " + KEY_TRAINING_ROUND_SERVER_USER_ID + "='"  + userid
                + "' AND " + KEY_TRAINING_ROUND_SYNC + "='" + 0
                + "' AND " + KEY_TRAINING_ROUND_FINISHED + "='" + 1 + "'"
                + " limit " + limit + ";";

        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            cursor.moveToFirst();

            if (cursor.moveToFirst()) {
                do {
//                    if (checkTrainingSessionisFinishedOfRound(cursor.getString(cursor.getColumnIndex(KEY_TRAINING_ROUND_SESSION_START_TIME)))){
                    if (checkRoundhasPunches(cursor.getString(cursor.getColumnIndex(KEY_TRAINING_ROUND_START_TIME)))){
                        dbTrainingRoundDto = new DBTrainingRoundDto(
                                cursor.getString(cursor.getColumnIndex(KEY_TRAINING_ROUND_SESSION_START_TIME)),
                                cursor.getString(cursor.getColumnIndex(KEY_TRAINING_ROUND_START_TIME)),
                                cursor.getString(cursor.getColumnIndex(KEY_TRAINING_ROUND_END_TIME)),
                                (int)cursor.getDouble(cursor.getColumnIndex(KEY_TRAINING_ROUND_AVG_SPEED)),
                                (int)cursor.getDouble(cursor.getColumnIndex(KEY_TRAINING_ROUND_MAX_SPEED)),
                                (int)cursor.getDouble(cursor.getColumnIndex(KEY_TRAINING_ROUND_AVG_FORCE)),
                                (int)cursor.getDouble(cursor.getColumnIndex(KEY_TRAINING_ROUND_MAX_FORCE)),
                                cursor.getInt(cursor.getColumnIndex(KEY_TRAINING_ROUND_PUNCHES_COUNT)),
                                cursor.getFloat(cursor.getColumnIndex(KEY_TRAINING_ROUND_BEST_TIME)));

                        list.add(dbTrainingRoundDto);
                    }else {
                        //delete round
                        db.delete(TRAINING_ROUND_TABLE, KEY_TRAINING_ROUND_ID + "=?", new String[]{String.valueOf(cursor.getInt(cursor.getColumnIndex(KEY_TRAINING_ROUND_ID)))});
                    }
//                    }
                } while (cursor.moveToNext());

            }
        } finally {
            if (cursor != null)
                cursor.close();
        }

        return list;
    }

    private boolean checkTrainingRoundisFinishedOfPunch(String roundstarttime){
        //check training session is finished of round

        String selectQuery = "SELECT  * FROM " + TRAINING_ROUND_TABLE + " WHERE " + KEY_TRAINING_ROUND_START_TIME + "='" + roundstarttime + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();

                if (cursor.getInt(cursor.getColumnIndex(KEY_TRAINING_ROUND_FINISHED)) == 1)
                    return true;
                else
                    return false;
            } else {
                return false;
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
    }

    private boolean checkRoundhasPunches(String roundstarttime){
        String selectQuery = "SELECT  * FROM " + TRAINING_PUNCH_TABLE + " WHERE " + KEY_TRAINING_PUNCH_ROUND_START_TIME + "='" + roundstarttime + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);

        boolean result = false;

        if (cursor.getCount() > 0){
            result = true;
        }

        if (cursor != null)
            cursor.close();

        return result;
    }

    public ArrayList<DBTrainingPunchDto> getAllNonSynchronizedTrainingPunchRecords(int limit, int userid) {

        DBTrainingPunchDto dbTrainingPunchDto = null;
        ArrayList<DBTrainingPunchDto> list = new ArrayList<DBTrainingPunchDto>();

        String selectQuery = "SELECT * FROM " + TRAINING_PUNCH_TABLE + " WHERE " + KEY_TRAINING_PUNCH_SERVER_USER_ID + "='"  + userid +
                "' AND " + KEY_TRAINING_PUNCH_SYNC + "='" + 0 + "'"
                + " limit " + limit + ";";

        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            cursor.moveToFirst();

            if (cursor.moveToFirst()) {
                do {
                    if (checkTrainingRoundisFinishedOfPunch(cursor.getString(cursor.getColumnIndex(KEY_TRAINING_PUNCH_ROUND_START_TIME)))){
                        dbTrainingPunchDto = new DBTrainingPunchDto(
                                cursor.getString(cursor.getColumnIndex(KEY_TRAINING_PUNCH_ROUND_START_TIME)),
                                cursor.getString(cursor.getColumnIndex(KEY_TRAINING_PUNCH_PUNCHTIME)),
                                cursor.getString(cursor.getColumnIndex(KEY_TRAINING_PUNCH_PUNCHTYPE)),
                                cursor.getString(cursor.getColumnIndex(KEY_TRAINING_PUNCH_PUNCHHAND)),
                                cursor.getFloat(cursor.getColumnIndex(KEY_TRAINING_PUNCH_DURATION)),
                                cursor.getInt(cursor.getColumnIndex(KEY_TRAINING_PUNCH_FORCE)),
                                cursor.getInt(cursor.getColumnIndex(KEY_TRAINING_PUNCH_SPEED)));
                        list.add(dbTrainingPunchDto);
                    }
                } while (cursor.moveToNext());

            }
        } finally {
            if (cursor != null)
                cursor.close();
        }

        return list;
    }

    public int updateSynchronizedRecordsForTable(String tableName, List<SyncResponseDto> dtos) {

        ContentValues values = new ContentValues();
        int success = 0;

        for (int i = 0; i < dtos.size(); i++) {
            values.put("sync", "1");
            values.put("server_time", dtos.get(i).serverTime);

            if (AppConst.DEBUG)
                Log.e(TAG, "update db = " + tableName + "    " + dtos.get(i).id + "      " + dtos.get(i).serverTime);
            try {
                success = db.update(tableName, values, "id = ? ", new String[]{String.valueOf(dtos.get(i).id)});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        return success;
    }

    public void updateUser(UserDto userDto) {

        ContentValues values = new ContentValues();

        values.put(KEY_USER_SERVER_ID, userDto.mId);
        values.put(KEY_USER_FACEBOOK_ID, userDto.mFacebookId);
        values.put(KEY_USER_FIRST_NAME, userDto.mFirstName);
        values.put(KEY_USER_LAST_NAME, userDto.mlastName);
        values.put(KEY_USER_EMAIL, userDto.mEmail);
        values.put(KEY_USER_PASSWORD, userDto.mBirthday);
        values.put(KEY_USER_GENDER, userDto.mGender);
        values.put(KEY_USER_BIRTHDAY, userDto.mBirthday);
        values.put(KEY_USER_WEIGHT, userDto.mWeight);
        values.put(KEY_USER_HEIGHT, userDto.mHeight);
        values.put(KEY_USER_SHOWTIP, userDto.mshowTip);
        values.put(KEY_USER_SKILL_LEVEL, userDto.mSkillLevel);
        values.put(KEY_USER_PHOTO, userDto.mPhoto);
        values.put(KEY_USER_LEFT_HAND_SENSOR, userDto.mLeftHandSensor);
        values.put(KEY_USER_RIGHT_HAND_SENSOR, userDto.mRightHandSensor);
        values.put(KEY_USER_LEFT_KICK_SENSOR, userDto.mLeftKickSensor);
        values.put(KEY_USER_RIGHT_KICK_SENSOR, userDto.mRightKickSensor);
        values.put(KEY_USER_IS_SPECTATOR, userDto.mIsSpectator);
        values.put(KEY_USER_STANCE, userDto.mStance);
        values.put(KEY_USER_CREATED_AT, userDto.mCreatedAt);
        values.put(KEY_USER_UPDATED_AT, userDto.mUpdatedAt);

        String selectQuery = "SELECT * FROM " + USER_TABLE + " WHERE " + KEY_USER_SERVER_ID + "='"  + userDto.mId + "';";
        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            if (cursor.getCount() > 0) {
                db.update(USER_TABLE, values, KEY_USER_SERVER_ID + " = " + userDto.mId, null);
            } else {
                db.insert(USER_TABLE, null, values);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
    }

    public void trainingSessionEnd(String sessionStartTime) {
        ContentValues values = new ContentValues();
        values.put(KEY_TRAINING_SESSION_END_TIME, String.valueOf(System.currentTimeMillis()));
        values.put(KEY_TRAINING_SESSION_FINISHED, 1);

        db.update(TRAINING_SESSION_TABLE, values, KEY_TRAINING_SESSION_START_TIME + " = " + sessionStartTime, null);
    }

    public void trainingRoundEnd(String roundStartTime) {
        ContentValues values = new ContentValues();
        values.put(KEY_TRAINING_ROUND_END_TIME, String.valueOf(System.currentTimeMillis()));
        values.put(KEY_TRAINING_ROUND_FINISHED, 1);

        db.update(TRAINING_ROUND_TABLE, values, KEY_TRAINING_ROUND_START_TIME + " = " + roundStartTime, null);
    }

    public UserDto getUserInfoFromEmail(String email, String password) {
        UserDto userDto = null;

        String selectQuery = "SELECT * FROM " + USER_TABLE + " WHERE " + KEY_USER_EMAIL + "='"  + email
                + "' AND " + KEY_USER_PASSWORD + "='" + password + "';";

        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                userDto = new UserDto();
                userDto.mId = cursor.getInt(cursor.getColumnIndex(KEY_USER_SERVER_ID));
                userDto.mFacebookId = cursor.getLong(cursor.getColumnIndex(KEY_USER_FACEBOOK_ID));
                userDto.mFirstName = cursor.getString(cursor.getColumnIndex(KEY_USER_FIRST_NAME));
                userDto.mlastName = cursor.getString(cursor.getColumnIndex(KEY_USER_LAST_NAME));
                userDto.mEmail = cursor.getString(cursor.getColumnIndex(KEY_USER_EMAIL));
                userDto.mPassword = cursor.getString(cursor.getColumnIndex(KEY_USER_PASSWORD));
                userDto.mshowTip = cursor.getInt(cursor.getColumnIndex(KEY_USER_SHOWTIP));
                userDto.mGender = cursor.getString(cursor.getColumnIndex(KEY_USER_GENDER));
                userDto.mBirthday = cursor.getString(cursor.getColumnIndex(KEY_USER_BIRTHDAY));
                userDto.mWeight = cursor.getInt(cursor.getColumnIndex(KEY_USER_WEIGHT));
                userDto.mHeight = cursor.getInt(cursor.getColumnIndex(KEY_USER_HEIGHT));
                userDto.mPhoto = cursor.getString(cursor.getColumnIndex(KEY_USER_PHOTO));
                userDto.mLeftHandSensor = cursor.getString(cursor.getColumnIndex(KEY_USER_LEFT_HAND_SENSOR));
                userDto.mRightHandSensor = cursor.getString(cursor.getColumnIndex(KEY_USER_RIGHT_HAND_SENSOR));
                userDto.mLeftKickSensor = cursor.getString(cursor.getColumnIndex(KEY_USER_LEFT_KICK_SENSOR));
                userDto.mRightKickSensor = cursor.getString(cursor.getColumnIndex(KEY_USER_RIGHT_KICK_SENSOR));
                userDto.mIsSpectator = cursor.getInt(cursor.getColumnIndex(KEY_USER_IS_SPECTATOR));
                userDto.mStance = cursor.getString(cursor.getColumnIndex(KEY_USER_STANCE));
                userDto.mCreatedAt = cursor.getString(cursor.getColumnIndex(KEY_USER_CREATED_AT));
                userDto.mUpdatedAt = cursor.getString(cursor.getColumnIndex(KEY_USER_UPDATED_AT));
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }

        return userDto;
    }

    public UserDto getUserInfoFromFBId(String fbId) {
        UserDto userDto = null;

        String selectQuery = "SELECT * FROM " + USER_TABLE + " WHERE " + KEY_USER_FACEBOOK_ID + "='"  + fbId + "';";

        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                userDto = new UserDto();
                userDto.mId = cursor.getInt(cursor.getColumnIndex(KEY_USER_SERVER_ID));
                userDto.mFacebookId = cursor.getLong(cursor.getColumnIndex(KEY_USER_FACEBOOK_ID));
                userDto.mFirstName = cursor.getString(cursor.getColumnIndex(KEY_USER_FIRST_NAME));
                userDto.mlastName = cursor.getString(cursor.getColumnIndex(KEY_USER_LAST_NAME));
                userDto.mEmail = cursor.getString(cursor.getColumnIndex(KEY_USER_EMAIL));
                userDto.mPassword = cursor.getString(cursor.getColumnIndex(KEY_USER_PASSWORD));
                userDto.mGender = cursor.getString(cursor.getColumnIndex(KEY_USER_GENDER));
                userDto.mshowTip = cursor.getInt(cursor.getColumnIndex(KEY_USER_SHOWTIP));
                userDto.mBirthday = cursor.getString(cursor.getColumnIndex(KEY_USER_BIRTHDAY));
                userDto.mWeight = cursor.getInt(cursor.getColumnIndex(KEY_USER_WEIGHT));
                userDto.mHeight = cursor.getInt(cursor.getColumnIndex(KEY_USER_HEIGHT));
                userDto.mPhoto = cursor.getString(cursor.getColumnIndex(KEY_USER_PHOTO));
                userDto.mLeftHandSensor = cursor.getString(cursor.getColumnIndex(KEY_USER_LEFT_HAND_SENSOR));
                userDto.mRightHandSensor = cursor.getString(cursor.getColumnIndex(KEY_USER_RIGHT_HAND_SENSOR));
                userDto.mLeftKickSensor = cursor.getString(cursor.getColumnIndex(KEY_USER_LEFT_KICK_SENSOR));
                userDto.mRightKickSensor = cursor.getString(cursor.getColumnIndex(KEY_USER_RIGHT_KICK_SENSOR));
                userDto.mIsSpectator = cursor.getInt(cursor.getColumnIndex(KEY_USER_IS_SPECTATOR));
                userDto.mStance = cursor.getString(cursor.getColumnIndex(KEY_USER_STANCE));
                userDto.mCreatedAt = cursor.getString(cursor.getColumnIndex(KEY_USER_CREATED_AT));
                userDto.mUpdatedAt = cursor.getString(cursor.getColumnIndex(KEY_USER_UPDATED_AT));
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }

        return userDto;
    }
}


