package com.example.firebaseapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Patterns
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.HandlerCompat.postDelayed
import com.example.firebaseapp.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLoginBinding
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.navToSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.signInBtn.setOnClickListener {
            loginUser()
        }
        binding.forgotPass.setOnClickListener {
           TODO()
        }
    }

    private fun loginUser() {
        val email: String = binding.email.getText().toString().trim()
        val password: String = binding.password.getText().toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            showErrorMessage("Please fill in all fields")
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showErrorMessage("Invalid email address format")
            return
        }
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }, 2000)
            showSuccessMessage()
        }.addOnFailureListener {
            showErrorMessage("Failed to create account!")
        }

    }

    private fun showSuccessMessage() {
        binding.successErrorTv.apply {
            text = context.getString(R.string.login_successful)
            visibility = View.VISIBLE
            setBackgroundColor(Color.GREEN)
            setTextColor(Color.WHITE)
            postDelayed({
                visibility = View.GONE
            }, 2000)
        }
    }

    private fun showErrorMessage(message: String) {
        binding.successErrorTv.apply {
            text = message
            visibility = View.VISIBLE
            setBackgroundColor(Color.RED)
            setTextColor(Color.WHITE)
            postDelayed({
                visibility = View.GONE
            }, 2000)
        }
    }
}