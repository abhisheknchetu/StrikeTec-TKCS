package com.striketec.mobile.interfaces;

import com.striketec.mobile.dto.TrainingSessionDto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Qiang on 8/7/2017.
 */

public interface UploadFinishCallback {
    void syncFinished();

    void getSessionsWithType(ArrayList<TrainingSessionDto> sessionDtos);

    void getSessionsError();
}
