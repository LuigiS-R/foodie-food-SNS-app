package com.example.finalproject

import android.content.Context
import android.net.Uri
import android.util.Log
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import org.json.JSONObject

class FoodUploader(private val context: Context) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(50, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(50, java.util.concurrent.TimeUnit.SECONDS)
        .writeTimeout(50, java.util.concurrent.TimeUnit.SECONDS)
        .build()

    private val backendUrl = "https://food-predictor-951b.onrender.com/predict_food"

    fun uploadFoodImage(imageUri: Uri, callback: (String?) -> Unit) {
        val inputStream = context.contentResolver.openInputStream(imageUri)
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("file", "food.jpg",
                inputStream!!.readBytes().toRequestBody("image/jpeg".toMediaTypeOrNull()))
            .build()

        val request = Request.Builder()
            .url(backendUrl)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                callback(null)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    val responseBody = it.body?.string()
                    Log.d("FoodUploader", "Raw response body: $responseBody")

                    if (!it.isSuccessful) {
                        Log.e("FoodUploader", "Server error: ${response.code}")
                        callback(null)
                        return
                    }

                    try {
                        val json = JSONObject(responseBody)
                        val label = json.getString("food_label")
                        Log.d("FoodUploader", "Parsed label: $label")
                        callback(label)
                    } catch (e: Exception) {
                        Log.e("FoodUploader", "Failed to parse JSON", e)
                        callback(null)
                    }
                }
            }

        })
    }
}