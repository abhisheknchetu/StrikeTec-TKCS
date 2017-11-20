package com.striketec.mobile.interfaces;

import com.striketec.mobile.dto.TrainingSessionDto;
import com.striketec.mobile.dto.VideoDto;

import java.util.ArrayList;

/**
 * Created by Qiang on 8/7/2017.
 */

public interface VideoCallback {
    void viewCountChanged(VideoDto videoDto);
    void favoriteChanged(VideoDto videoDto);
}
