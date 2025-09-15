package com.example.finalproject

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.view.View

class FoodRecommendationActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var progressBar: ProgressBar
    private val PICK_IMAGE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.food_recommendation_screen)

        imageView = findViewById(R.id.receiptImage)  // reuse ImageView from your old layout
        progressBar = findViewById(R.id.progressBar)
        val selectButton: Button = findViewById(R.id.selectButton) // reuse the same button

        selectButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri: Uri? = data.data
            imageUri?.let {
                imageView.setImageURI(it)

                // Show loading spinner
                progressBar.visibility = View.VISIBLE

                // Upload image to backend
                val uploader = FoodUploader(this)
                uploader.uploadFoodImage(it) { predictedLabel ->
                    runOnUiThread {
                        // Hide loading spinner
                        progressBar.visibility = View.GONE

                        if (predictedLabel != null) {
                            val resultText: TextView = findViewById(R.id.resultText)
                            resultText.text = "Predicted: $predictedLabel"
                            openGoogleMaps(predictedLabel)
                        } else {
                            Toast.makeText(this, "Prediction failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun openGoogleMaps(predictedLabel: String) {
        val encodedQuery = Uri.encode("$predictedLabel restaurants")
        val gmmIntentUri = Uri.parse("geo:0,0?q=$encodedQuery")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")

        if (mapIntent.resolveActivity(packageManager) != null) {
            startActivity(mapIntent)
        } else {
            Toast.makeText(this, "Google Maps is not installed", Toast.LENGTH_LONG).show()
        }
    }
}