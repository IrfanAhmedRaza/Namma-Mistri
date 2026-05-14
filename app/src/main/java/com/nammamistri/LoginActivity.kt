package com.nammamistri

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.nammamistri.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()

        // Already logged in and email verified
        val current = auth.currentUser
        if (current != null && current.isEmailVerified) {
            goToMain()
            return
        }

        binding.btnLogin.setOnClickListener { handleLogin() }
        binding.tvRegisterLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        binding.tvForgotPassword.setOnClickListener { handleForgotPassword() }
    }

    private fun handleLogin() {
        val email = binding.etUsername.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (email.isEmpty()) { binding.etUsername.error = "Enter email"; return }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etUsername.error = "Enter valid email"; return
        }
        if (password.isEmpty()) { binding.etPassword.error = "Enter password"; return }

        binding.progressBar.visibility = View.VISIBLE
        binding.btnLogin.isEnabled = false

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                binding.progressBar.visibility = View.GONE
                binding.btnLogin.isEnabled = true

                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null && user.isEmailVerified) {
                        Toast.makeText(this, "✅ Welcome back!", Toast.LENGTH_SHORT).show()
                        goToMain()
                    } else {
                        // Resend verification email
                        user?.sendEmailVerification()
                        Toast.makeText(this,
                            "⚠️ Please verify your email first. Check your inbox and click the verification link.",
                            Toast.LENGTH_LONG).show()
                        auth.signOut()
                    }
                } else {
                    val msg = task.exception?.message ?: "Login failed"
                    Toast.makeText(this, "❌ $msg", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun handleForgotPassword() {
        val email = binding.etUsername.text.toString().trim()
        if (email.isEmpty()) {
            binding.etUsername.error = "Enter your email first"
            return
        }
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "✅ Password reset email sent to $email", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "❌ ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun goToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
