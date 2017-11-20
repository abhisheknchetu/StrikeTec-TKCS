package com.striketec.mobile.dto;

import java.io.Serializable;
import java.util.ArrayList;

public class SubscriptionDto implements Serializable{

    public int mTutorialNum;
    public float mTutorialPrice;
    public int mTournamentNum;
    public float mTournamentPrice;
    public int mBattleNum;
    public float mBattlePrice;
    public int mPlanType;  //0 : free plan, //1 : month plan, //2: annual plan
    public float mTotalPrice;

    public SubscriptionDto(){

    }
}
