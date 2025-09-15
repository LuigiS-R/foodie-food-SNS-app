package com.example.finalproject

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeActivity : ComponentActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var postList: ArrayList<Post>
    private lateinit var adapter: PostAdapter
    private val db = FirebaseFirestore.getInstance()
    var username:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.main_screen)

        //Getting username
        username = intent.getStringExtra("username")?: ""

        //Declaring buttons from navBar
        val makeNewPostBtn :LinearLayout = findViewById(R.id.makeNewPost)
        val getRecommendation :LinearLayout = findViewById(R.id.getRecommendation)

        //Assigning click event listeners to navBar buttons
        makeNewPostBtn.setOnClickListener{
            val intent = Intent(this, NewPostActivity::class.java)
            intent.putExtra("username", username)
            startActivity(intent)
        }

        getRecommendation.setOnClickListener{
            val intent = Intent(this, FoodRecommendationActivity::class.java)
            startActivity(intent)
        }

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        postList = arrayListOf()
        adapter = PostAdapter(this, postList)
        recyclerView.adapter = adapter

        loadPostsFromFirebase()
    }
    fun loadPostsFromFirebase() {
        db.collection("posts")
            .whereEqualTo("username", username)
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    Toast.makeText(this, username, Toast.LENGTH_SHORT).show()
                }
                Toast.makeText(this, "Username not found", Toast.LENGTH_SHORT).show()
                for (doc in result) {
                    val post = doc.toObject(Post::class.java)
                    postList.add(post)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load posts", Toast.LENGTH_SHORT).show()
            }
    }
}