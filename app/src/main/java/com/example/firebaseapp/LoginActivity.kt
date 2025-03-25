package com.example.firebaseapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.firebaseapp.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    private lateinit var email: String
    private lateinit var password: String

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
            email = binding.emailEt.getText().toString().trim()
            password = binding.passwordEt.getText().toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                showErrorMessage("Please fill in all fields")
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                showErrorMessage("Invalid email address format")
                return@setOnClickListener
            }
            showProgress(true)
            loginUser(email, password)
        }
        binding.forgotPassTv.setOnClickListener {

        }
    }

    private fun showProgress(show: Boolean) {
        binding.loadingOverlay.visibility = if (show) View.VISIBLE else View.GONE
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                showProgress(false)
                if (task.isSuccessful) {
                    showSuccessMessage()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    handleLoginError(task.exception)
                }
            }

    }

    private fun handleLoginError(exception: Exception?) {
        val errorMessage = when (exception) {
            is FirebaseAuthInvalidUserException -> "Account does not exist"
            is FirebaseAuthInvalidCredentialsException -> "Invalid email or password"
            else -> "Login failed. Please try again."
        }
        showErrorMessage(errorMessage)
    }

    private fun showSuccessMessage() {
        binding.statusMessage.apply {
            text = context.getString(R.string.login_successful)
            visibility = View.VISIBLE
            setTextColor(Color.GREEN)
            postDelayed({
                visibility = View.GONE
            }, 2000)
        }
    }

    private fun showErrorMessage(message: String) {
        binding.statusMessage.apply {
            text = message
            visibility = View.VISIBLE
            setTextColor(Color.RED)
            postDelayed({
                visibility = View.GONE
            }, 2000)
        }
    }
}