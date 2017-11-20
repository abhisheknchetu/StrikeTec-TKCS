package com.striketec.mobile.dto;

import java.io.Serializable;
import java.util.ArrayList;

public class WorkoutRoundDto implements Serializable{
    private String name;
    private ArrayList<Integer> comboIDLists;   //this is array of combination id

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public  ArrayList<Integer> getComboIDLists() {
        return comboIDLists;
    }

    public void setComboIDLists( ArrayList<Integer> comboIDLists) {
        this.comboIDLists = comboIDLists;
    }

    public WorkoutRoundDto(String name, ArrayList<Integer> comboIDLists) {
        super();
        this.name = name;
        this.comboIDLists = comboIDLists;
    }

    public WorkoutRoundDto(){
        this.name = "";
        this.comboIDLists = new ArrayList<>();
    }
}
