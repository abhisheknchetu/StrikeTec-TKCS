package com.striketec.mobile.dto;

import java.io.Serializable;
import java.util.ArrayList;

public class SetsDto implements Serializable{
    private int id;
    private String name;
    private ArrayList<Integer> comboIDLists;   //this is array of combination id

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

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

    public SetsDto(String name, ArrayList<Integer> comboIDLists, int id) {
        super();
        this.id = id;
        this.name = name;
        this.comboIDLists = comboIDLists;
    }

    public SetsDto(){

    }
}
