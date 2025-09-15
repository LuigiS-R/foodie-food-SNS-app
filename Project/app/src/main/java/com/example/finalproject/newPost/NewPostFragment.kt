package com.example.finalproject.newPost

import com.example.finalproject.receiptHandle.ReceiptUploader
//import com.example.finalproject.HomeActivity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.finalproject.R
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.media.Image
import androidx.lifecycle.lifecycleScope
import com.example.finalproject.HomeActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.launch
import org.w3c.dom.Text

class NewPostFragment : Fragment() {
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.make_post_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //Initializing db
        FirebaseApp.initializeApp(view.context)
        val db = Firebase.firestore

        //Initializing widget objects
        val restaurantNameEntry :TextView = view.findViewById(R.id.restaurantNameInput)
        val dateEntry :TextView = view.findViewById(R.id.dateInput)
        val frameLayout :FrameLayout = view.findViewById(R.id.imageFrame);
        val selectedImageView :ImageView = view.findViewById(R.id.selectedImage)
        val shareBtn :TextView = view.findViewById(R.id.shareBtn)
        val contentInput: EditText = view.findViewById(R.id.postContent)

        //Initializing lateinit input objects
        lateinit var postContent: String
        var imageUri: Uri? = null
        var storeimageUri: String = ""



        //Accessing data from previous fragment (Results retrieved form Receipt Scan API)
        val restaurantName = arguments?.getString("restaurantName")
        val date = arguments?.getString("date")
        val address = arguments?.getString("address")
        val username = arguments?.getString("username")

        restaurantNameEntry.text = restaurantName
        dateEntry.text = date

        //Handling picture selection
        frameLayout.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            imagePickerLauncher.launch(intent)
        }


        imagePickerLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                imageUri = result.data?.data
                selectedImageView.setImageURI(imageUri)
                //Handling image upload to Firebase storage
                val storage = Firebase.storage
                val storageRef = storage.reference

                val fileName = "images/${System.currentTimeMillis()}.jpg"
                val imageRef = storageRef.child(fileName)

                imageUri?.let { uri ->
                    val uploadTask = imageRef.putFile(uri)
                    uploadTask.addOnSuccessListener {
                        imageRef.downloadUrl.addOnSuccessListener { uri ->
                            storeimageUri = uri.toString()
                        }
                    }.addOnFailureListener {

                    }
                }
            }
        }

        shareBtn.setOnClickListener{
            postContent = contentInput.text.toString()


            val post = hashMapOf(
                "username" to username,
                "restaurant" to restaurantName,
                "date" to date,
                "address" to address,
                "content" to postContent,
                "image" to storeimageUri
            )

            db.collection("posts")
                .add(post)
                .addOnSuccessListener { doc ->
                    val intent = Intent(requireContext(), HomeActivity::class.java)
                    val myHomeScreen = HomeActivity();
                    myHomeScreen.loadPostsFromFirebase()
                    startActivity(intent)
                }
        }
    }
}