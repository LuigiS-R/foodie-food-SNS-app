package com.example.finalproject

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import android.widget.*
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        //Initializing db
        FirebaseApp.initializeApp(this)
        val db = Firebase.firestore

        //Finding views
        val usernameEditText :EditText = findViewById(R.id.usernameEditText)
        val pwEditText :EditText = findViewById(R.id.passwordEditText)
        val loginBtn: Button = findViewById(R.id.loginButton)
        val signupBtn :TextView= findViewById(R.id.signupLink)

        //SignUp Button
        signupBtn.setOnClickListener{
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        //LogIn Button
        loginBtn.setOnClickListener{
            val usernameInput = usernameEditText.text.toString()
            val pwInput = pwEditText.text.toString()

            if (usernameInput.isBlank() || pwInput.isBlank()){
                Toast.makeText(this, "Make sure to fill all the fields", Toast.LENGTH_SHORT).show()
            }

        db.collection("users")
            .whereEqualTo("username", usernameInput)
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    Toast.makeText(this, "Username not found", Toast.LENGTH_SHORT).show()
                }

                for (document in result) {
                    val pw = document.getString("password")
                    if (pw == pwInput){
                        //LOGIN
                        val intent = Intent(this, HomeActivity::class.java)
                        intent.putExtra("username", usernameInput)
                        startActivity(intent)
                    }
                    else{
                        Toast.makeText(this, "Password is incorrect", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}