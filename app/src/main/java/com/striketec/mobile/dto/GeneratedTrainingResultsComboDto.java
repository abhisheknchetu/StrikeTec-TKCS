package com.striketec.mobile.dto;

import java.io.Serializable;
import java.util.ArrayList;

public class GeneratedTrainingResultsComboDto implements Serializable{
    public int mComboId;
    public ArrayList<PunchwithDesiredDto> mComboPunches;

    public GeneratedTrainingResultsComboDto(ArrayList<PunchwithDesiredDto> mComboPunches, int mComboId){
        super();
        this.mComboPunches = mComboPunches;
        this.mComboId = mComboId;
    }

    public GeneratedTrainingResultsComboDto(){

    }
}
