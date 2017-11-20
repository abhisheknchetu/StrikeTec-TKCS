package com.striketec.mobile.restapi;

import com.striketec.mobile.StriketecApp;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Qiang on 8/15/2017.
 */

public class RetrofitSingleton {

    public static CredentialRest CREDENTIAL_REST;
    public static TrainingDataRest TRAINING_DATA_REST;
    public static UserRest USER_REST;
    public static VideoRest VIDEO_REST;
    public static DataRest DATA_REST;

    /**
     * user : rakeshk2
     * date : 10/23/2017
     * description :
     **/
    public static SubscriptionRest SUBSCRIPTION_REST;
    public static RecordingRest RECORDING_REST;

    private static StriketecApp application;
    private static OkHttpClient httpClient = new OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request.Builder ongoing = chain.request().newBuilder();
                    ongoing.addHeader("Accept", "text/plain,application/json;version=1");

                    return chain.proceed(ongoing.build());
                }
            })
            .build();


    private static Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl(RestUrl.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create());

    public static <S> S createService(Class<S> serviceClass) {
        Retrofit retrofit = builder.client(httpClient).build();
        return retrofit.create(serviceClass);
    }

    public static void setApplication(StriketecApp app) {
        RetrofitSingleton.application = app;
        CREDENTIAL_REST = createService(CredentialRest.class);
        USER_REST = createService(UserRest.class);
        TRAINING_DATA_REST = createService(TrainingDataRest.class);
        VIDEO_REST = createService(VideoRest.class);
        DATA_REST = createService(DataRest.class);

        /**
         user : rakeshk2
         date : 10/23/2017
         description : 
         **/
        SUBSCRIPTION_REST = createService(SubscriptionRest.class);
        RECORDING_REST = createService(RecordingRest.class);

    }

}
