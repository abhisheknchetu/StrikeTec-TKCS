package com.striketec.mobile.util;


public class TrainingManager {

    private boolean isTrainingRunning = false;

    public TrainingManager() {
    }

    public boolean isTrainingRunning() {
        return isTrainingRunning;
    }

    private void setTrainingRunning(boolean isTrainingRunning) {
        this.isTrainingRunning = isTrainingRunning;
    }

    public boolean startTraining() {

        setTrainingRunning(true);
        return true;
    }

    public boolean stopTraining() {

        setTrainingRunning(false);
        return true;
    }


}
