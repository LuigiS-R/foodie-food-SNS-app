package com.example.finalproject

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SignUpActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signup)

        //Initializing db
        FirebaseApp.initializeApp(this)
        val db = Firebase.firestore

        //Finding views
        val usernameEditText :EditText = findViewById(R.id.signupUsername)
        val emailEditText :EditText = findViewById(R.id.signupEmail)
        val pwEditText :EditText = findViewById(R.id.signupPassword)
        val confirmPwEditText :EditText = findViewById(R.id.signupConfirmPassword)
        val signUpBtn :Button = findViewById(R.id.signupButton)
        val goBackBtn :TextView = findViewById(R.id.goBackBtn)

        //SignUp Button
        signUpBtn.setOnClickListener{
            val usernameInput = usernameEditText.text.toString()
            val emailInput = emailEditText.text.toString()
            val passwordInput = pwEditText.text.toString()
            val confirmPwInput = confirmPwEditText.text.toString()

            if (usernameInput.isBlank() || emailInput.isBlank() || passwordInput.isBlank() || confirmPwInput.isBlank())
                Toast.makeText(this, "Please make sure to have entered all the fields", Toast.LENGTH_SHORT).show()
            else if(passwordInput != confirmPwInput)
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            else {
                db.collection("users")
                    .whereEqualTo("username", usernameInput)
                    .get()
                    .addOnSuccessListener { result ->
                        if (result.isEmpty) {
                            val user = hashMapOf(
                                "username" to usernameInput,
                                "email" to emailInput,
                                "password" to passwordInput
                            )

                            db.collection("users")
                                .add(user)
                                .addOnSuccessListener { doc ->
                                    val intent = Intent(this, HomeActivity::class.java)
                                    intent.putExtra("username", usernameInput)
                                    startActivity(intent)
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(this, "There was an error while creating the new account", Toast.LENGTH_SHORT).show()
                                }
                        }
                        else{
                            Toast.makeText(this, "The username is already taken", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

        //Go Back Button
        goBackBtn.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}