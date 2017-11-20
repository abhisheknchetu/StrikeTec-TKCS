package com.striketec.mobile.dto;

import java.io.Serializable;

public class MistakeDto implements Serializable{
    public TrainingPunchDto trainingPunchDto;
    public boolean speedisslow;

    public MistakeDto(TrainingPunchDto punchResultDto, boolean speedisslow){
        this.trainingPunchDto = trainingPunchDto;
        this.speedisslow = speedisslow;
    }

    public MistakeDto(){

    }
}
