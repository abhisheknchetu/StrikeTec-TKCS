package com.striketec.mobile.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ServerTrainingRoundsDtoResponse extends DefaultResponseDto {

    @SerializedName("session")
    public TrainingSessionDto mSession;

    @SerializedName("rounds")
    public List<TrainingRoundDto> mRounds;
}
