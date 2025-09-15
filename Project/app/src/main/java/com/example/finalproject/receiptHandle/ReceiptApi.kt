package com.example.finalproject.receiptHandle

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

data class ReceiptInfo(
    val restaurant_name: String,
    val address: String,
    val date: String
)

interface ReceiptApi {
    @Multipart
    @POST("/parse_receipt")
    fun parseReceipt(
        @Part file: MultipartBody.Part
    ): Call<ReceiptInfo>
}
