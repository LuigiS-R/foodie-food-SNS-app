package com.example.finalproject

import com.example.finalproject.newPost.ReceiptUploadFragment

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class NewPostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.new_post_screen)

        val username = intent.getStringExtra("username")

        val bundle = Bundle()
        bundle.putString("username", username)

        val fragment = ReceiptUploadFragment()
        fragment.arguments = bundle

        supportFragmentManager.beginTransaction()
            .replace(R.id.FragmentContainer, fragment)
            .commit()
    }
}