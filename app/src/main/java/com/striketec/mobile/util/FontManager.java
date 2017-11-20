package com.striketec.mobile.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by Qiang on 8/7/2017.
 */
public class FontManager {

    private static Context mContext;
    public static AssetManager assetManager;

    public static Typeface blenderproBook;
    public static Typeface blenderproMedium;
    public static Typeface efdigits;

    public static void init(Context context){
        mContext = context;

        assetManager = mContext.getAssets();
        blenderproBook = Typeface.createFromAsset(assetManager, "fonts/BlenderPro-Book.otf");
        blenderproMedium = Typeface.createFromAsset(assetManager, "fonts/BlenderPro-Medium.otf");
        efdigits = Typeface.createFromAsset(assetManager, "fonts/efdigits-ExtraCondensed.ttf");
    }


}
