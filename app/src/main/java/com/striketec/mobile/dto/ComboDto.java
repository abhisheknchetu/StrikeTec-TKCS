package com.striketec.mobile.dto;

import java.io.Serializable;
import java.util.ArrayList;

public class ComboDto implements Serializable{
    private int id;
    private String name;
    private String combos;
    private ArrayList<String> comboTypes;

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

    public String getCombos() {
        return combos;
    }

    public void setCombos(String combos) {
        this.combos = combos;
    }

    public ArrayList<String> getComboTypes(){
        return comboTypes;
    }

    public void setComboTypes(ArrayList<String> comboTypes){
        this.comboTypes = comboTypes;
        this.combos = getCombosFromComboTypes(comboTypes);
    }

//    public int getRange() {
//        return range;
//    }
//
//    public void setRange(int range) {
//        this.range = range;
//    }

    public ComboDto(String name, ArrayList<String> comboTypes, int id ){//}, int range) {
        super();
        this.id = id;
        this.name = name;
        this.comboTypes = comboTypes;
//        this.range = range;
        this.combos = getCombosFromComboTypes(comboTypes);
    }

    public ComboDto(){

    }

    private String getCombosFromComboTypes(ArrayList<String> comboTypes){
        String result = "";

        if (comboTypes != null && comboTypes.size() > 0){
            for (int i = 0; i < comboTypes.size(); i++){
                if (i == 0){
                    result += comboTypes.get(0);
                }else {
                    result += "-" + comboTypes.get(i);
                }
            }
        }

        return result;
    }
}
