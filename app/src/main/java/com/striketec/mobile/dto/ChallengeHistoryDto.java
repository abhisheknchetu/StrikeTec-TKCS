package com.striketec.mobile.dto;

import java.io.Serializable;

public class ChallengeHistoryDto implements Serializable{
    public String name1; //winner
    public String name2; //losser
    public boolean firstisCurrent;

    public ChallengeHistoryDto(String name1, String name2, boolean firstisCurrent){
        this.name1 = name1;
        this.name2 = name2;
        this.firstisCurrent = firstisCurrent;
    }

    public ChallengeHistoryDto(){

    }
}
