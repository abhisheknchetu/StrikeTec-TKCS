package com.striketec.mobile.restapi;

/**
 * Created by Qiang on 8/15/2017.
 */

public class RestUrl {
    public static final int ENV = 0; //0 : is for beta, 1 : is for production
    public static final String BASE_URL = "http://54.233.233.189/"; //Client url
//    public static final String BASE_URL = "http://172.16.10.73/"; //Local url new

    //sign up with credential
    public static final String SIGN_UP = "user/register";
    //sign in with credential
    public static final String SIGN_IN = "auth/login";
    //update profile
    public static final String UPDATE_USER = "users";
    //reset password
    public static final String PASSWORD = "password";
    //reset password
    public static final String RESET_PASSWORD = "password/reset";
    //verify code
    public static final String VERIFY_CODE = "password/verify_code";
    //upload training sessions/rounds/punches
    public static final String UPLOAD_SESSIONS = "user/training/sessions";
    public static final String UPLOAD_ROUNDS = "user/training/sessions/rounds";
    public static final String UPLOAD_PUNCHES = "user/training/sessions/rounds/punches";
    //retrieve training sessions
    public static final String RETRIEVE_SESSIONS = "user/training/sessions";
    //get rounds with sessionid
    public static final String RETRIEVE_ROUNDS_WITH_SESSIONID = "user/training/sessions/{session_id}";
    //get punches with roundid
    public static final String RETRIEVE_PUNCHES_WITH_ROUNDID = "user/training/sessions/rounds/{round_id}";
    //get rounds by training type
    public static final String RETRIEVE_ROUND_BY_TRAINING_TYPE = "user/training/sessions/rounds_by_training";
    //get video with category
    public static final String GET_VIDEOS = "videos";
    //search video with query
    public static final String SEARCH_VIDEOS = "videos/search";
    //Favorite video
    public static final String FAVORITE_VIDEO = "videos/favourite/{videoId}";
    //unfavorite video
    public static final String UNFAVORITE_VIDEO = "videos/unfavourite/{videoId}";
    //increase view count
    public static final String INCREASE_VIEWCOUNT = "videos/add_view/{videoId}";
    //get favorite videos
    public static final String GET_FAVORITE_VIDEOS = "user/fav_videos";
    //get countries
    public static final String GET_COUNTRY = "countries";
    //get state of country
    public static final String GET_STATES_OF_COUNTRY = "states_by_country/{country_id}";
    //get city of state
    public static final String GET_CITIES_OF_STATE = "cities_by_state/{state_id}";
    //update user's preference
    public static final String UPDATE_USER_PREFERENCES = "users/preferences";
    //change user password
    public static final String CHANGE_PASSWORD = "users/change_password";
    //get leaderboard
    public static final String GET_LEADERBOARD = "leaderboard";
    //get explore
    public static final String GET_EXPLORE = "explore";
    //follow other user
    public static final String FOLLOW_USER = "user/follow/{user_id}";
    //unfollow other user
    public static final String UNFOLLOW_USER = "user/unfollow/{user_id}";
    //facebook sign up
    public static final String FB_SIGNUP = "user/register/facebook";
    //facebook sign in
    public static final String FB_SIGNIN = "auth/facebook";
    //get user
    public static final String GET_USER = "users/{user_id}";
    //update app token for push
    public static final String UPDATE_TOKEN = "user/app_token";
    //get list of subscription plans.
    public static final String GET_SUBSCRIPTION_PLANS = "subscriptions";
    //create subscription.
    public static final String CREATE_SUBSCRIPTION = "usersubscription";
    //Cancel subscription.
    public static final String CANCEL_SUBSCRIPTION = "cancelsubscriptionapi";
    //get current subscription status.
    public static final String GET_SUBSCRIPTION_STATUS = "getusersubscriptionstatus";
    //get recording files from server.
    public static final String GET_RECORDING_FILES = "battles/combos/audio";
    //upload recording files to the server.
    public static final String UPLOAD_RECORDING_FILES = "combos/audio";

}
