package com.example.finalproject.receiptHandle

import android.content.Context
import android.net.Uri
import android.util.Log
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class ReceiptUploader(private val context: Context) {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://receipt-parser-hsej.onrender.com")
        .client(OkHttpClient())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(ReceiptApi::class.java)

    suspend fun uploadReceipt(uri: Uri, onResult: (ReceiptInfo?) -> Unit) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return
            val tempFile = File.createTempFile("upload", ".jpg", context.cacheDir)
            tempFile.outputStream().use { output ->
                inputStream.copyTo(output)
            }

            val reqFile = RequestBody.create("image/*".toMediaTypeOrNull(), tempFile)
            val body = MultipartBody.Part.createFormData("file", tempFile.name, reqFile)

            val call = api.parseReceipt(body)
            call.enqueue(object : retrofit2.Callback<ReceiptInfo> {
                override fun onResponse(call: retrofit2.Call<ReceiptInfo>, response: retrofit2.Response<ReceiptInfo>) {
                    if (response.isSuccessful) {
                        onResult(response.body())
                    } else {
                        Log.e("ReceiptUploader", "Failed: ${response.code()} - ${response.errorBody()?.string()}")
                        onResult(null)
                    }
                }

                override fun onFailure(call: retrofit2.Call<ReceiptInfo>, t: Throwable) {
                    Log.e("ReceiptUploader", "Error: ${t.message}")
                    onResult(null)
                }
            })
        } catch (e: Exception) {
            Log.e("ReceiptUploader", "Exception: ${e.message}")
            onResult(null)
        }
    }
}
