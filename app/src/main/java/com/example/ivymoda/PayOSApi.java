package com.example.ivymoda;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface PayOSApi {
    @POST("https://api-merchant.payos.vn/v2/payment-requests")
    @Headers("Content-Type: application/json")
    Call<PayOSResponse> createPayment(@Body PayOSRequest request);

    @Headers("Content-Type: application/json")
    @POST(".")
    Call<Map<String, Object>> createPaymentRaw(
            @Header("x-client-id") String clientId,
            @Header("x-api-key") String apiKey,
            @Body Map<String, Object> body
    );
}