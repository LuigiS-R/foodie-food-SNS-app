package com.example.finalproject.newPost

import com.example.finalproject.receiptHandle.ReceiptUploader
import com.example.finalproject.newPost.NewPostFragment

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
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class ReceiptUploadFragment : Fragment() {
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.receipt_pre_verification_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //Retrieving username
        val username = arguments?.getString("username")

        //Initializing transition fragment
        val makePostFragment = NewPostFragment()

        //Initializing widget objects
        val uploadBtn :Button = view.findViewById(R.id.verifyBtn)

        //Handling Click event
        uploadBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            imagePickerLauncher.launch(intent)
        }



        imagePickerLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                val imageUri: Uri? = result.data?.data

                viewLifecycleOwner.lifecycleScope.launch{
                    //CALLING API
                    val uploader = ReceiptUploader(requireContext())

                    //Loading Screen
                    val progress = ProgressDialog(requireContext())
                    progress.setMessage("Uploading image...")
                    progress.setCancelable(false)
                    progress.show()

                    uploader.uploadReceipt(imageUri!!) { receiptInfo ->
                        if (receiptInfo != null) {

                            val bundle = Bundle()
                            bundle.putString("username", username)
                            bundle.putString("restaurantName", receiptInfo.restaurant_name)
                            bundle.putString("date", receiptInfo.date)
                            bundle.putString("address", receiptInfo.address)
                            makePostFragment.arguments = bundle

                            progress.dismiss()
                            requireActivity().supportFragmentManager.beginTransaction()
                                .replace(R.id.FragmentContainer, makePostFragment)
                                .commit()
                        } else {
                            // Error handling
                        }
                    }
                }

            }
        }
    }
}
