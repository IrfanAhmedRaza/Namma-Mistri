package com.nammamistri

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.nammamistri.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()

        binding.btnRegister.setOnClickListener { handleRegister() }
        binding.tvLoginLink.setOnClickListener { finish() }
    }

    private fun handleRegister() {
        val name = binding.etFullName.text.toString().trim()
        val email = binding.etUsername.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()

        if (name.isEmpty()) { binding.etFullName.error = "Enter your name"; return }
        if (email.isEmpty()) { binding.etUsername.error = "Enter email"; return }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etUsername.error = "Enter valid email address"; return
        }
        if (password.isEmpty()) { binding.etPassword.error = "Enter password"; return }
        if (password.length < 6) { binding.etPassword.error = "Min 6 characters"; return }
        if (confirmPassword != password) {
            binding.etConfirmPassword.error = "Passwords do not match"; return
        }

        binding.progressBar.visibility = View.VISIBLE
        binding.btnRegister.isEnabled = false

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser!!

                    // Save user profile in Firestore
                    val userDoc = hashMapOf(
                        "fullName" to name,
                        "email" to email,
                        "createdAt" to System.currentTimeMillis()
                    )
                    db.collection("users").document(user.uid)
                        .set(userDoc)

                    // Send verification email
                    user.sendEmailVerification()
                        .addOnCompleteListener { verifyTask ->
                            binding.progressBar.visibility = View.GONE
                            binding.btnRegister.isEnabled = true

                            if (verifyTask.isSuccessful) {
                                Toast.makeText(this,
                                    "✅ Account created! A verification email has been sent to $email. Please verify and then login.",
                                    Toast.LENGTH_LONG).show()
                                auth.signOut()
                                startActivity(Intent(this, LoginActivity::class.java))
                                finishAffinity()
                            } else {
                                Toast.makeText(this,
                                    "Account created but failed to send verification email. Try logging in.",
                                    Toast.LENGTH_LONG).show()
                            }
                        }
                } else {
                    binding.progressBar.visibility = View.GONE
                    binding.btnRegister.isEnabled = true
                    val msg = task.exception?.message ?: "Registration failed"
                    Toast.makeText(this, "❌ $msg", Toast.LENGTH_LONG).show()
                }
            }
    }
}
