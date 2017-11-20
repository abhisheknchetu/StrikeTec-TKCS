package com.striketec.mobile.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ServerTrainingPunchesDtoResponse extends DefaultResponseDto {

    @SerializedName("round")
    public TrainingRoundDto mRound;

    @SerializedName("punches")
    public List<TrainingPunchDto> mPunches;
}
