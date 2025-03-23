package com.example.firebaseapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.firebaseapp.databinding.ActivityLoginBinding
import com.example.firebaseapp.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import java.util.regex.Pattern
import androidx.core.graphics.toColorInt
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db : FirebaseDatabase
    private lateinit var root : DatabaseReference



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()
        root = db.reference.child("Users")

        binding.navToLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.signupBtn.setOnClickListener {
            createUser()
        }
    }

    fun getFormattedDate(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(System.currentTimeMillis())
    }
    private fun createUser() {
        val username = binding.username.getText().toString().trim()
        val email = binding.email.getText().toString().trim()
        val password = binding.password.getText().toString().trim()
        val confirmPassword = binding.confirmPassword.getText().toString().trim()

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showErrorMessage("Please fill in all fields")
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showErrorMessage("Invalid email address")
            return
        }
        if (password.length < 6) {
            showErrorMessage("Password must be at least 6 characters long")
            return
        }
        if (password != confirmPassword) {
            showErrorMessage("Passwords do not match")
            return
        }

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener (this) { task ->
            if (task.isSuccessful){
                val userId = auth.currentUser?.uid
                if (userId != null){
                    val userData = mapOf(
                        "User ID" to userId,
                        "Username" to username,
                        "Email" to email,
                        "Created Time" to getFormattedDate()
                    )

                    root.push().setValue(userData)
                        .addOnSuccessListener {
                            showSuccessMessage()
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        }.addOnFailureListener{
                            showErrorMessage("Failed to create account")
                        }
                }
            }

        }.addOnFailureListener {
            showErrorMessage("Failed to create account")
        }
    }

    private fun showErrorMessage(message: String) {
        binding.successErrorTv.apply {
            visibility = View.VISIBLE
            text = message
            setBackgroundColor("#FFCDD2".toColorInt())
            setTextColor("#D32F2F".toColorInt())
        }
    }
    private fun showSuccessMessage() {
        binding.successErrorTv.apply { 
            visibility = View.VISIBLE
            text = context.getString(R.string.success_message)
            setBackgroundColor("#C8E6C9".toColorInt())
            setTextColor("#388E3C".toColorInt())
        }
    }


}


