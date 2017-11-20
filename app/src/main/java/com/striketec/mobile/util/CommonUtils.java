package com.striketec.mobile.util;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.striketec.mobile.R;
import com.striketec.mobile.dto.DefaultResponseDto;
import com.striketec.mobile.dto.TrainingPunchDto;
import com.striketec.mobile.dto.TrainingSessionDto;
import com.striketec.mobile.restapi.IndicatorCallback;
import com.striketec.mobile.restapi.RetrofitSingleton;

import org.apache.commons.collections.map.HashedMap;

import java.io.File;
import java.lang.reflect.Type;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Qiang on 8/09/2017.
 */

public class CommonUtils {

    private static Context mContext;

    public static void init(Context context) {
        mContext = context;
    }

    public static String getJsonFromHashMap(HashMap<String, Object> hashMap) {
        Gson gsonTrainingSession = new GsonBuilder().create();
        String result = gsonTrainingSession.toJson(hashMap);

        return result;
    }

    public static void updateRegistrationToken() {
        if (TextUtils.isEmpty(SharedUtils.getStringvalue(SharedUtils.TOKEN, "")))
            return;

        if (CommonUtils.isOnline()) {
            Map<String, Object> params = new HashedMap();
            params.put("os", "ANDROID");
            params.put("token", SharedUtils.getStringvalue(SharedUtils.TOKEN, ""));

            RetrofitSingleton.USER_REST.updateDeviceToken(SharedUtils.getHeader(), params).enqueue(new IndicatorCallback<DefaultResponseDto>(mContext.getApplicationContext(), false) {
                @Override
                public void onResponse(Call<DefaultResponseDto> call, Response<DefaultResponseDto> response) {
                    super.onResponse(call, response);
                }

                @Override
                public void onFailure(Call<DefaultResponseDto> call, Throwable t) {
                    super.onFailure(call, t);
                }
            });
        } else {
        }
    }

    public static int getRandomNum(int high, int low) {
        Random random = new Random();
        return random.nextInt(high - low) + low;
    }

    public static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    public static boolean pwdValidation(String password) {
        Pattern hasUppercase = Pattern.compile("[A-Z]");
        Pattern hasSpecialChar = Pattern.compile("[^a-zA-Z0-9]");
        Pattern hasLowercase = Pattern.compile("[a-z]");
        Pattern hasnumber = Pattern.compile("[0-9]");

        if (!hasUppercase.matcher(password).find()) {
            CommonUtils.showToastMessage(mContext.getResources().getString(R.string.error_nonuppercase));
            return false;
        }

        if (!hasSpecialChar.matcher(password).find()) {
            CommonUtils.showToastMessage(mContext.getResources().getString(R.string.error_nonspecial));
            return false;
        }

        if (!hasLowercase.matcher(password).find()) {
            CommonUtils.showToastMessage(mContext.getResources().getString(R.string.error_nonlowercase));
            return false;
        }

        if (!hasnumber.matcher(password).find()) {
            CommonUtils.showToastMessage(mContext.getResources().getString(R.string.error_nonnumber));
            return false;
        }

        return true;
    }

    //show toast message in the center of screen
    public static void showToastMessage(String message) {
        Toast toast = Toast.makeText(mContext, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static int dpTopx(float dp) {
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, mContext.getResources().getDisplayMetrics());
        return px;
    }

    public static int spTopx(float sp) {
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, mContext.getResources().getDisplayMetrics());
        return px;
    }

    public static boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        return (networkInfo != null && networkInfo.isConnected());
    }

    public static void getKeyHash() {
        try {
            PackageInfo info = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());

                if (AppConst.DEBUG)
                    Log.e("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

    /**
     * calculatePastDate
     *
     * @param date    must have "yyyy-MM-dd" format
     * @param number  if measure = 0 then number is number of past days else if measure = 1 then number is number of past years
     * @param measure pass 0 to deduct from days and pass 1 for year
     * @return
     */
    public static String calculatePastDate(String date, int number, int measure) {

        Calendar calendar = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date parsedDate = dateFormat.parse(date);
            calendar = Calendar.getInstance();
            calendar.setTime(parsedDate);

            calendar.setTime(dateFormat.parse(date));
            calendar.add((measure == 1) ? Calendar.YEAR : Calendar.DATE, -number);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateFormat.format(calendar.getTime());

    }

    public static double average(double[] values) {
        double total = 0;
        for (int i = 0; i < values.length; i++) {
            total += values[i];
        }
        double average = total / values.length;
        return average;
    }

    public static File getCommonDirectory() {
        return mContext.getExternalFilesDir(null);
    }

    public static String getMacAddress(String address) {
        String macAddress = "";

        for (int i = 0; i < address.length(); i++) {
            char ch = address.charAt(i);
            if (i % 2 == 0 && i != 0) {

                macAddress = macAddress + ":";
                macAddress = macAddress + ch;
            } else {
                macAddress = macAddress + ch;
            }
        }
        return macAddress.toUpperCase();
    }

    public static void showAlert(Context context, String message) {
        final Dialog dialog = new Dialog(context);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        dialog.setCancelable(false);

        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_alert);

        TextView messageView, positiveView;

        messageView = (TextView) dialog.findViewById(R.id.message);
        messageView.setText(message);

        positiveView = (TextView) dialog.findViewById(R.id.positive);

        positiveView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public static String changeArrayListtoString(ArrayList<TrainingSessionDto> arrayList) {
        Gson gson = new Gson();
        final String jsonString = gson.toJson(arrayList);
        return jsonString;
    }

    public static ArrayList<TrainingSessionDto> changeStringToArrayList(String jsonString) {
        Gson gson = new Gson();
        ArrayList<TrainingSessionDto> results;
        Type type = new TypeToken<ArrayList<TrainingSessionDto>>() {
        }.getType();

        if (TextUtils.isEmpty(jsonString))
            results = new ArrayList<>();
        else
            results = gson.fromJson(jsonString, type);
        return results;
    }

    public static int compareCount(TrainingSessionDto sessionDto) {
        if (sessionDto.mPunchCount < 90) {
            return 0;
        } else if (sessionDto.mPunchCount > 110) {
            return 2;
        } else {
            return 1;
        }
    }

    public static int compareForce(TrainingPunchDto trainingPunchDto) {
        if (trainingPunchDto.mForce < 450) {
            return 0;
        } else if (trainingPunchDto.mForce > 550) {
            return 2;
        } else {
            return 1;
        }
    }

    public static String getHandfromChar(String hand) {
        return hand.equalsIgnoreCase("L") ? "LEFT" : "RIGHT";
    }

    public static String getTypefromvalue(String type) {
        if (type.equalsIgnoreCase(AppConst.JAB_ABBREVIATION_TEXT)) {
            return AppConst.JAB;
        } else if (type.equalsIgnoreCase(AppConst.HOOK_ABBREVIATION_TEXT)) {
            return AppConst.HOOK;
        } else if (type.equalsIgnoreCase(AppConst.STRAIGHT_ABBREVIATION_TEXT)) {
            return AppConst.STRAIGHT;
        } else if (type.equalsIgnoreCase(AppConst.UPPERCUT_ABBREVIATION_TEXT)) {
            return AppConst.UPPERCUT;
        } else if (type.equalsIgnoreCase(AppConst.UNRECOGNIZED_ABBREVIATION_TEXT)) {
            return AppConst.UNRECOGNIZED;
        }

        return "";
    }

    /**
     * Created by rakeshk2 on 7/21/2017.
     * description this method will create mp3 file inside file number folder with app name folder:
     */
    public static String createFile(String fileName) {
        String path = Environment.getExternalStorageDirectory() +
                File.separator + mContext.getString(R.string.strike_tec);
        File folder = new File(path);
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdirs();
        }
        return path;
    }

    /**
     * Created by rakeshk2 on 11/06/2017.
     * description : Create random file name with extension .3gp:
     */
    public static String createRandomAudioFileName(String fileName) {
        SimpleDateFormat formatter = new SimpleDateFormat(mContext.getString(R.string.time_format), Locale.getDefault());
        Date now = new Date();
        return formatter.format(now) + fileName + mContext.getString(R.string.audio_extention);
    }

    /**
     * Created by rakeshk2 on 11/06/2017.
     * description : Search file by matching a string(name):
     */
    public static String searchFile(String fileName) {
        String path = null;
        List<String> paths = new ArrayList<>();

        String folderPath = Environment.getExternalStorageDirectory() +
                File.separator + mContext.getString(R.string.strike_tec);
        File directory = new File(folderPath);

        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                paths.add(file.getAbsolutePath());
                if (file.getAbsolutePath().contains(fileName)) {
                    path = file.getAbsolutePath();
                }
            }
        }
        return path;
    }


}
