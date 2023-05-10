package com.test.apex;

import java.util.ArrayList;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

interface APIInterface {

    @POST("/apiProducts.php?apicall=readProduct")
    @Headers({"Content-Type: application/json","Accept:application/json"})
            Call<ArrayList<Product>>retrieveProducts();

    @FormUrlEncoded
    @POST("/apiUsers.php?apicall=loginusername")
    Call<Object> retrieveLoginUsername(@Field("username") String username,
                                       @Field("password") String password);

    @FormUrlEncoded
    @POST("/apiUsers.php?apicall=loginemail")
    Call<Object> retrieveLoginEmail(@Field("email") String email,
                                    @Field("password") String password);

    @FormUrlEncoded
    @POST("/apiUsers.php?apicall=signup")
    Call<Object> setSignup(@Field("username") String username,
                           @Field("email") String email,
                           @Field("password") String password);

    @FormUrlEncoded
    @POST("/apiTransactions.php?apicall=readTransaction")
    Call<Object> retrieveCount(@Field("userId") String id);

    @FormUrlEncoded
    @POST("/apiTransactions.php?apicall=createTransaction")
    Call<Object> setTransaction(@Field("invoiceNumber") String invoiceNumber,
                                @Field("transactionStatus") String transactionStatus,
                                @Field("transactionAmount") long transactionAmount,
                                @Field("transactionAddress") String transactionAddress,
                                @Field("invoiceDate") String invoiceDate,
                                @Field("paymentDate") String paymentDate,
                                @Field("userId") String userId,
                                @Field("products") String products);

}