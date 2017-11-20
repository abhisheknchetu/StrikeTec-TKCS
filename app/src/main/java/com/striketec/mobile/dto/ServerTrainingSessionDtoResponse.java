package com.striketec.mobile.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ServerTrainingSessionDtoResponse extends DefaultResponseDto {
    @SerializedName("sessions")
    public List<TrainingSessionDto> mSessions;
}
