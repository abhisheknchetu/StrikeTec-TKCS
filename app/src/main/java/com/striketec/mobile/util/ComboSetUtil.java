package com.striketec.mobile.util;

import android.content.Context;
import com.striketec.mobile.dto.ComboDto;
import com.striketec.mobile.dto.SetsDto;
import com.striketec.mobile.dto.WorkoutDto;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by super qiang 8/27/2017.
 */
public class ComboSetUtil {

    public static HashMap<String, String> punchTypeMap;
    public static ArrayList<String> keyLists;
    public static Context mContext;

    public static ArrayList<ComboDto>  defaultCombos;
    public static ArrayList<SetsDto>   defaultSets;
    public static ArrayList<WorkoutDto>  defaultWorkouts;

    public static void init(Context context){
        mContext = context;
        punchTypeMap = new HashMap<>();
        keyLists = new ArrayList<>();
        defaultCombos = new ArrayList<>();
        defaultSets = new ArrayList<>();
        defaultWorkouts = new ArrayList<>();

        //punch type
        punchTypeMap.put("1", "Jab");
        punchTypeMap.put("2", "Straight");
        punchTypeMap.put("3", "Left Hook");
        punchTypeMap.put("4", "Right Hook");
        punchTypeMap.put("5", "Left Uppercut");
        punchTypeMap.put("6", "Right Uppercut");
        punchTypeMap.put("7", "Shovel Hook");

        //movement
        punchTypeMap.put("SR", "Slip Right");
        punchTypeMap.put("SL", "Slip Left");
        punchTypeMap.put("DL", "Duck Left");
        punchTypeMap.put("DR", "Duck Right");
        punchTypeMap.put("SF", "Step Forward");
        punchTypeMap.put("SB", "Step Back");

        keyLists.add("1");
        keyLists.add("2");
        keyLists.add("3");
        keyLists.add("4");
        keyLists.add("5");
        keyLists.add("6");
        keyLists.add("7");
        keyLists.add("SR");
        keyLists.add("SL");
        keyLists.add("DL");
        keyLists.add("DR");
        keyLists.add("SF");
        keyLists.add("SB");

        initDefaultCombos();
        initDefaultSets();
        initDefaultWorkouts();
    }

    private static void initDefaultCombos(){
        ArrayList<String>  punchKeyList = new ArrayList<>();
        punchKeyList.add("1");
        punchKeyList.add("2");
        punchKeyList.add("SR");
        punchKeyList.add("2");
        punchKeyList.add("3");
        punchKeyList.add("2");
        punchKeyList.add("5");
        punchKeyList.add("6");
        punchKeyList.add("3");
        punchKeyList.add("2");

        defaultCombos.add(new ComboDto("Attack", punchKeyList, 1));

        punchKeyList = new ArrayList<>();
        punchKeyList.add("1");
        punchKeyList.add("2");
        punchKeyList.add("5");
        punchKeyList.add("7");
        punchKeyList.add("3");
        punchKeyList.add("2");
        punchKeyList.add("SR");
        punchKeyList.add("5");
        punchKeyList.add("3");
        punchKeyList.add("1");

        defaultCombos.add(new ComboDto("Crafty", punchKeyList, 2));

        punchKeyList = new ArrayList<>();
        punchKeyList.add("1");
        punchKeyList.add("3");
        punchKeyList.add("5");
        punchKeyList.add("5");
        punchKeyList.add("3");
        punchKeyList.add("1");
        punchKeyList.add("5");
        punchKeyList.add("3");
        punchKeyList.add("3");
        punchKeyList.add("1");

        defaultCombos.add(new ComboDto("Left overs", punchKeyList, 3));

        punchKeyList = new ArrayList<>();
        punchKeyList.add("1");
        punchKeyList.add("2");
        punchKeyList.add("6");
        punchKeyList.add("7");
        punchKeyList.add("3");
        punchKeyList.add("2");
        punchKeyList.add("5");
        punchKeyList.add("1");
        punchKeyList.add("3");
        punchKeyList.add("2");

        defaultCombos.add(new ComboDto("Defensive", punchKeyList, 4));

        punchKeyList = new ArrayList<>();
        punchKeyList.add("3");
        punchKeyList.add("5");
        punchKeyList.add("4");
        punchKeyList.add("1");
        punchKeyList.add("5");
        punchKeyList.add("2");
        punchKeyList.add("1");
        punchKeyList.add("6");
        punchKeyList.add("3");
        punchKeyList.add("2");

        defaultCombos.add(new ComboDto("Super Banger", punchKeyList, 5));
    }

    private static void initDefaultSets(){
        ArrayList<Integer> comboIDList = new ArrayList<>();
        comboIDList.add(1);
        comboIDList.add(2);
        comboIDList.add(3);
        defaultSets.add(new SetsDto("Aggressor", comboIDList, 1));

        comboIDList = new ArrayList<>();
        comboIDList.add(1);
        comboIDList.add(4);
        comboIDList.add(5);
        defaultSets.add(new SetsDto("Defensive", comboIDList, 2));

    }

    private static void initDefaultWorkouts(){

        ArrayList<ArrayList<Integer>>  roundComboLists = new ArrayList<>();

        ArrayList<Integer> comboIDList = new ArrayList<>();
        comboIDList.add(1);
        comboIDList.add(2);
        comboIDList.add(3);
        roundComboLists.add(comboIDList);

        comboIDList = new ArrayList<>();
        comboIDList.add(1);
        comboIDList.add(4);
        comboIDList.add(5);
        roundComboLists.add(comboIDList);

        comboIDList = new ArrayList<>();
        comboIDList.add(2);
        comboIDList.add(3);
        comboIDList.add(1);
        roundComboLists.add(comboIDList);

        comboIDList = new ArrayList<>();
        comboIDList.add(3);
        comboIDList.add(4);
        comboIDList.add(2);
        roundComboLists.add(comboIDList);

        comboIDList = new ArrayList<>();
        comboIDList.add(3);
        comboIDList.add(1);
        comboIDList.add(5);
        roundComboLists.add(comboIDList);

        defaultWorkouts.add(new WorkoutDto(1, "Workout 1", 5, roundComboLists, PresetUtil.timeList.size() / 2, PresetUtil.timeList.size() /2,
                PresetUtil.timeList.size() / 2, PresetUtil.warningList.size() / 2, PresetUtil.gloveList.size() / 2));

        roundComboLists = new ArrayList<>();

        comboIDList = new ArrayList<>();
        comboIDList.add(1);
        comboIDList.add(5);
        comboIDList.add(3);
        roundComboLists.add(comboIDList);

        comboIDList = new ArrayList<>();
        comboIDList.add(2);
        comboIDList.add(4);
        comboIDList.add(3);
        roundComboLists.add(comboIDList);

        comboIDList = new ArrayList<>();
        comboIDList.add(5);
        comboIDList.add(3);
        comboIDList.add(4);
        roundComboLists.add(comboIDList);

        comboIDList = new ArrayList<>();
        comboIDList.add(1);
        comboIDList.add(4);
        comboIDList.add(2);
        roundComboLists.add(comboIDList);

        comboIDList = new ArrayList<>();
        comboIDList.add(3);
        comboIDList.add(1);
        comboIDList.add(5);
        roundComboLists.add(comboIDList);

        comboIDList = new ArrayList<>();
        comboIDList.add(2);
        comboIDList.add(1);
        comboIDList.add(5);
        roundComboLists.add(comboIDList);

        comboIDList = new ArrayList<>();
        comboIDList.add(3);
        comboIDList.add(2);
        comboIDList.add(5);
        roundComboLists.add(comboIDList);

        comboIDList = new ArrayList<>();
        comboIDList.add(3);
        comboIDList.add(4);
        comboIDList.add(1);
        roundComboLists.add(comboIDList);

        defaultWorkouts.add(new WorkoutDto(2, "Workout 2", 8, roundComboLists, PresetUtil.timeList.size() / 2, PresetUtil.timeList.size() /2,
                PresetUtil.timeList.size() / 2, PresetUtil.warningList.size() / 2, PresetUtil.gloveList.size() / 2));
    }

    public static ComboDto getComboDtowithID(int id){
        ArrayList<ComboDto> comboDTOs = SharedUtils.getSavedCombinationList();
        for (int i = 0; i < comboDTOs.size(); i++){
            if (id == comboDTOs.get(i).getId())
                return comboDTOs.get(i);
        }

        return null;
    }

    public static void updateComboDto(ComboDto comboDTO){
        ArrayList<ComboDto> comboDTOs = SharedUtils.getSavedCombinationList();
        for (int i = 0; i < comboDTOs.size(); i++){
            if (comboDTO.getId() == comboDTOs.get(i).getId()){
                comboDTOs.set(i, comboDTO);
                SharedUtils.saveCombinationList(comboDTOs);
                return;
            }
        }
    }

    public static void addComboDto(ComboDto comboDTO){
        ArrayList<ComboDto> comboDTOs = SharedUtils.getSavedCombinationList();
        comboDTOs.add(comboDTO);
        SharedUtils.saveCombinationList(comboDTOs);
    }

    public static void deleteComboDto(ComboDto comboDTO){
        ArrayList<ComboDto> comboDTOs = SharedUtils.getSavedCombinationList();

        for (int i = 0; i < comboDTOs.size(); i++){
            if (comboDTO.getId() == comboDTOs.get(i).getId()) {
                comboDTOs.remove(i);
                continue;
            }
        }

        SharedUtils.saveCombinationList(comboDTOs);
    }

    public static SetsDto getSetDtowithID(int id){
        ArrayList<SetsDto> setsDTOs = SharedUtils.getSavedSetList();
        for (int i = 0; i < setsDTOs.size(); i++){
            if (id == setsDTOs.get(i).getId())
                return setsDTOs.get(i);
        }

        return null;
    }

    public static void updateSetDto(SetsDto setsDTO){
        ArrayList<SetsDto> setDTOS = SharedUtils.getSavedSetList();
        for (int i = 0; i < setDTOS.size(); i++){
            if (setsDTO.getId() == setDTOS.get(i).getId()){
                setDTOS.set(i, setsDTO);
                SharedUtils.saveSetList(setDTOS);
                return;
            }
        }
    }

    public static void addSetDto(SetsDto setsDTO){
        ArrayList<SetsDto> setsDTOs = SharedUtils.getSavedSetList();
        setsDTOs.add(setsDTO);
        SharedUtils.saveSetList(setsDTOs);
    }

    public static void deleteSetDto(SetsDto setsDTO){
        ArrayList<SetsDto> setsDTOs = SharedUtils.getSavedSetList();
        for (int i = 0; i < setsDTOs.size(); i++){
            if (setsDTO.getId() == setsDTOs.get(i).getId()) {
                setsDTOs.remove(i);
                continue;
            }
        }

        SharedUtils.saveSetList(setsDTOs);
    }

    public static WorkoutDto getWorkoutDtoWithID(int id){
        ArrayList<WorkoutDto> workoutDTOs = SharedUtils.getSavedWorkouts();
        for (int i = 0; i < workoutDTOs.size(); i++){
            if (id == workoutDTOs.get(i).getId())
                return workoutDTOs.get(i);
        }

        return null;
    }

    public static void updateWorkoutDto(WorkoutDto workoutDTO){
        ArrayList<WorkoutDto> workoutDTOs = SharedUtils.getSavedWorkouts();
        for (int i = 0; i < workoutDTOs.size(); i++){
            if (workoutDTO.getId() == workoutDTOs.get(i).getId()){
                workoutDTOs.set(i, workoutDTO);
                SharedUtils.saveWorkoutList(workoutDTOs);
                return;
            }
        }
    }

    public static void addWorkoutDto(WorkoutDto workoutDTO){
        ArrayList<WorkoutDto> workoutDTOs = SharedUtils.getSavedWorkouts();
        workoutDTOs.add(workoutDTO);
        SharedUtils.saveWorkoutList(workoutDTOs);
    }

    public static void deleteWorkoutDto(WorkoutDto workoutDTO){
        ArrayList<WorkoutDto> workoutDTOs = SharedUtils.getSavedWorkouts();
        for (int i = 0; i < workoutDTOs.size(); i++){
            if (workoutDTO.getId() == workoutDTOs.get(i).getId()) {
                workoutDTOs.remove(i);
                continue;
            }
        }

        SharedUtils.saveWorkoutList(workoutDTOs);
    }

    public static void deleteComboFromAllSets(ComboDto comboDTO){
        ArrayList<SetsDto> setsDTOs = SharedUtils.getSavedSetList();

        ArrayList<SetsDto> newSetsDTOS = new ArrayList<>();

        for (int i = 0; i < setsDTOs.size(); i++){
            SetsDto setsDTO = setsDTOs.get(i);

            if (!setsDTO.getComboIDLists().contains(comboDTO.getId())) {
                newSetsDTOS.add(setsDTO);
                continue;
            }

            ArrayList<Integer> newComboIDList = new ArrayList<>();
            for (int j = 0; j < setsDTO.getComboIDLists().size(); j++){
                if (comboDTO.getId() != setsDTO.getComboIDLists().get(j)){
                    newComboIDList.add(setsDTO.getComboIDLists().get(j));
                }
            }

            SetsDto newSetDto = new SetsDto(setsDTO.getName(), newComboIDList, setsDTO.getId());
            newSetsDTOS.add(newSetDto);
        }

        SharedUtils.saveSetList( newSetsDTOS);
    }

    public static void deleteComboFromAllWorkout(ComboDto comboDTO){
        ArrayList<WorkoutDto> workoutDTOs = SharedUtils.getSavedWorkouts();

        ArrayList<WorkoutDto> newWorkoutDtos = new ArrayList<>();

        for (int i = 0; i < workoutDTOs.size(); i++){
            WorkoutDto workoutDTO = workoutDTOs.get(i);
            ArrayList<ArrayList<Integer>> newRoundComboLists = new ArrayList<>();

            for (int j = 0; j < workoutDTO.getRoundcount(); j++){
                ArrayList<Integer> comboLists = workoutDTO.getRoundsetIDs().get(j);

                if (!comboLists.contains(comboDTO.getId())){
                    newRoundComboLists.add(comboLists);
                    continue;
                }

                ArrayList<Integer> newComboLists = new ArrayList<>();

                for (int k = 0; k < comboLists.size(); k++){
                    if (comboLists.get(k) != comboDTO.getId()){
                        newComboLists.add(comboLists.get(k));
                    }
                }

                newRoundComboLists.add(newComboLists);
            }

            WorkoutDto newWorkoutDTO = new WorkoutDto(workoutDTO.getId(), workoutDTO.getName(), workoutDTO.getRoundcount(),
                    newRoundComboLists, workoutDTO.getRound(), workoutDTO.getRest(), workoutDTO.getPrepare(), workoutDTO.getWarning(), workoutDTO.getGlove());

            newWorkoutDtos.add(newWorkoutDTO);
        }

        SharedUtils.saveWorkoutList(newWorkoutDtos);
    }
}
