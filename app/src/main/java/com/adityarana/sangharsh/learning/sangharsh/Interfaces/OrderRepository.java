package com.adityarana.sangharsh.learning.sangharsh.Interfaces;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class OrderRepository {

    private static OrderRepository instance;

    public Purchase getPurchaseService() {
        return purchaseService;
    }

    public static OrderRepository getInstance(){
        if (instance == null){
            instance = new OrderRepository();
        }
        return instance;
    }

    public OrderRepository() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://us-central1-sangharshx-dc6ff.cloudfunctions.net")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        purchaseService = retrofit.create(Purchase.class);
    }

    private Purchase purchaseService;
}
