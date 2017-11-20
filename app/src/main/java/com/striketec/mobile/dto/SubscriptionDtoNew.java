package com.striketec.mobile.dto;

public class SubscriptionDtoNew {

    public int id;
    public String tutorials;
    public String tutorial_details;
    public String tournaments;
    public String tournament_details;
    public String battles;
    public String battle_details;
    public String name;
    public String duration;
    public float price;
    public String SKU;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTutorials() {
        return tutorials;
    }

    public void setTutorials(String tutorials) {
        this.tutorials = tutorials;
    }

    public String getTutorial_details() {
        return tutorial_details;
    }

    public void setTutorial_details(String tutorial_details) {
        this.tutorial_details = tutorial_details;
    }

    public String getTournaments() {
        return tournaments;
    }

    public void setTournaments(String tournaments) {
        this.tournaments = tournaments;
    }

    public String getTournament_details() {
        return tournament_details;
    }

    public void setTournament_details(String tournament_details) {
        this.tournament_details = tournament_details;
    }

    public String getBattles() {
        return battles;
    }

    public void setBattles(String battles) {
        this.battles = battles;
    }

    public String getBattle_details() {
        return battle_details;
    }

    public void setBattle_details(String battle_details) {
        this.battle_details = battle_details;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getSKU() {
        return SKU;
    }

    public void setSKU(String SKU) {
        this.SKU = SKU;
    }

    /*@SerializedName("id")
    public int mId;

    @SerializedName("tutorials")
    public String mTutorials;

    @SerializedName("tutorial_details")
    public String mTutorialDetails;

    @SerializedName("tournaments")
    public String mTournaments;

    @SerializedName("tournament_details")
    public String mTournamentDetails;

    @SerializedName("battles")
    public String mBattles;

    @SerializedName("battle_details")
    public String mBattleDetails;

    @SerializedName("name")
    public String mName;

    @SerializedName("duration")
    public String mDuration;

    @SerializedName("price")
    public float mPrice;

    @SerializedName("SKU")
    public String mSKU;*/

}
