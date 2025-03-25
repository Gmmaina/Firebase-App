package com.example.firebaseapp

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import com.example.firebaseapp.databinding.ActivitySignUpBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat
import java.util.Locale

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    private lateinit var root: DatabaseReference

    private lateinit var username: String
    private lateinit var email : String
    private lateinit var password : String
    private lateinit var confirmPassword : String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()
        root = db.reference.child("Users")

        binding.loginNavTv.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.signupBtn.setOnClickListener {
            username = binding.usernameEt.getText().toString().trim()
            email = binding.emailEt.getText().toString().trim()
            password = binding.passwordEt.getText().toString().trim()
            confirmPassword = binding.confirmPasswordEt.getText().toString().trim()

            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                showErrorMessage("Please fill in all fields")
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                showErrorMessage("Invalid email address")
                return@setOnClickListener
            }
            if (password.length < 6) {
                showErrorMessage("Password must be at least 6 characters long")
                return@setOnClickListener
            }
            if (password != confirmPassword) {
                showErrorMessage("Passwords do not match")
                return@setOnClickListener
            }
            showProgress(true)
            createUser(username, email, password)
        }
    }

    private fun showProgress(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.loadingOverlay.visibility = if (show) View.VISIBLE else View.GONE
    }

    fun getFormattedDate(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(System.currentTimeMillis())
    }

    private fun createUser(username: String, email: String, password: String) {


        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                showProgress(false)

                if (task.isSuccessful) {
                    showSuccessMessage()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        saveData(userId, username, email)
                    }
                }else{
                    handleSignUpError(task.exception)
                }

            }.addOnFailureListener { exception ->
                showProgress(false)
                handleSignUpError(exception)
            }
    }

    private fun saveData(userId: String, username: String, email: String) {
        val userData = mapOf(
            "User ID" to userId,
            "Username" to username,
            "Email" to email,
            "Created Time" to getFormattedDate()
        )

        root.push().setValue(userData)
    }

    private fun handleSignUpError(exception: Exception?) {
        val errorMessage = when (exception) {
            is FirebaseAuthUserCollisionException -> "Account email already exists"
            is FirebaseAuthWeakPasswordException -> "Password is too weak"
            is FirebaseAuthInvalidCredentialsException -> "Invalid email format"
            else -> "Sign up failed. Please try again."
        }
        showErrorMessage(errorMessage)
    }

    private fun showErrorMessage(message: String) {
        binding.statusMessage.apply {
            visibility = View.VISIBLE
            text = message
            setTextColor("#D32F2F".toColorInt())
            postDelayed({
                visibility = View.GONE
            }, 2000)

        }
    }

    private fun showSuccessMessage() {
        binding.statusMessage.apply {
            visibility = View.VISIBLE
            text = context.getString(R.string.success_message)
            setTextColor("#388E3C".toColorInt())
            postDelayed({
                visibility = View.GONE
            }, 2000)
        }
    }


}


