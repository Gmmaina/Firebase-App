package com.example.firebaseapp

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.firebaseapp.databinding.ActivityProfileBinding
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ProfileActivity : AppCompatActivity() {

    private lateinit var storageRef: StorageReference
    private val imageUri: Uri? = null
    private lateinit var binding: ActivityProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        super.onCreate(savedInstanceState)


        storageRef = FirebaseStorage.getInstance().getReference().child("Image Uploads")

        val pickImageLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                if (uri != null) {
                    binding.imageView.setImageURI(uri)
                }
            }
        binding.imageView.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.uploadImageButton.setOnClickListener {
            uploadImage()
        }
    }


    private fun uploadImage() {
        if (imageUri != null) {
            val fileName = storageRef.child(System.currentTimeMillis().toString())
            val fileRef = storageRef.child("$fileName.jpg")

            binding.progressBar.visibility = View.VISIBLE

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    fileRef.putFile(imageUri).await()
                    fileRef.downloadUrl.await()

                    runOnUiThread {
                        Toast.makeText(
                            this@ProfileActivity,
                            "Image Uploaded Successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.imageView.setImageURI(null)
                        binding.progressBar.visibility = View.GONE
                    }
                } catch (e: Exception) {
                    runOnUiThread {
                        Toast.makeText(
                            this@ProfileActivity,
                            "Image Upload Failed",
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.imageView.setImageURI(null)
                        binding.progressBar.visibility = View.GONE
                    }
                }
            }
        }
    }
}