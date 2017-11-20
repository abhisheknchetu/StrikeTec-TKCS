package com.striketec.mobile.util;

import android.content.Context;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.striketec.mobile.R;

import java.util.Locale;

/**
 * user : rakeshk2
 * date : 10/26/2017
 * description : This class converts Text to Speech.
 **/
public class ConvertTextToSpeech {

    private static final String TAG = "TTS";
    public static TextToSpeech tts;

    /**
     * Description : To speak given text
     *
     * @param context activity context
     * @param text    text to speak
     */
    public static void SpeakText(final Context context, final String text) {
        try {
            TextToSpeech.OnInitListener listener = new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {

                    if (status == TextToSpeech.SUCCESS) {

                        int result = tts.setLanguage(Locale.getDefault());

                        if (result == TextToSpeech.LANG_MISSING_DATA
                                || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                            Log.e(TAG, context.getString(R.string.language_not_supported));
                        } else {
                            //Speak text (Deprecated in API 21)
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
                            } else {
                                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                            }
                        }
                    } else {
                        Log.e(TAG, context.getString(R.string.initialization_failed));
                    }
                }
            };
            tts = new TextToSpeech(context, listener/*, "en-us-x-sfg#male_1-local"*/);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
