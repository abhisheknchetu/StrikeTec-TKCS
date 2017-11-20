package com.striketec.mobile.util.sensormanager.mmaGlove;

/**
 * Created by erich on 8/22/2016.
 */

import android.text.TextUtils;
import android.util.Log;

import com.striketec.mobile.util.AppConst;

/**
 *
 *
 * Professional:    4.3% of body weight (3.87)
 * Amateur:    3% of body weight  (2.7)
 * Novice  (>1yr)    2.30% of body weight (2.07)
 * Beginner    1.7% of body weight (1.53)
 *
 * Female => .9 of percentage.
 *
 * @author erich
 *
 */
public class EffectivePunchMassCalculator {

    private final double genderAdjustment = .9;

    public double calculatePunchMassEffect(String weight, String skillLevel, String gender) {

        try {
            double percentageTweak = 1;
            SkillLevel lookUp = SkillLevel.lookUp(skillLevel);

            if (!TextUtils.isEmpty(gender) && gender.toLowerCase().startsWith("f"))
                percentageTweak = genderAdjustment;

            double parsedWeight = Double.parseDouble(weight);

            return lookUp.adjust(parsedWeight, percentageTweak);
        } catch (Exception e) {
            Log.d("EffPunchMassCalc", "Error calculating: " + e.getMessage());
            return AppConst.GUEST_TRAINING_EFFECTIVE_PUNCH_MASS;
        }
    }
}
