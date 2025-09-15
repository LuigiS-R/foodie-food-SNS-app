package com.example.finalproject

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts

class EditPostActivity : AppCompatActivity() {

    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>
    private lateinit var selectedImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.make_post_screen)

        selectedImageView = findViewById(R.id.selectedImage)

        selectedImageView.setOnClickListener {
            openGallery()
        }

        imagePickerLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                val imageUri = result.data!!.data
                selectedImageView.setImageURI(imageUri)
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        imagePickerLauncher.launch(intent)
    }
}