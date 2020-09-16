package com.adityarana.sangharsh.learning.sangharsh.Tools;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

import com.adityarana.sangharsh.learning.sangharsh.CategoryActivity;
import com.adityarana.sangharsh.learning.sangharsh.Model.OrderResponse;

public interface Purchase {

    @POST("createOrder")
    Call<OrderResponse> createOrder(@Body CategoryActivity.OrderParams params);

}
