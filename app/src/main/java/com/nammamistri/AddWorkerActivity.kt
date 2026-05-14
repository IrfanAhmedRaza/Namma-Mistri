package com.nammamistri

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.nammamistri.data.FirebaseRepository
import com.nammamistri.data.Worker
import com.nammamistri.databinding.ActivityAddWorkerBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddWorkerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddWorkerBinding
    private val repo = FirebaseRepository()
    private var siteId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddWorkerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        siteId = intent.getStringExtra("SITE_ID") ?: ""
        supportActionBar?.title = "Add Worker"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.btnSaveWorker.setOnClickListener { saveWorker() }
    }

    private fun saveWorker() {
        val name = binding.etWorkerName.text.toString().trim()
        val wageStr = binding.etDailyWage.text.toString().trim()

        if (name.isEmpty()) { binding.etWorkerName.error = "Name required"; return }
        if (wageStr.isEmpty()) { binding.etDailyWage.error = "Daily wage required"; return }
        val wage = wageStr.toDoubleOrNull()
        if (wage == null || wage <= 0) { binding.etDailyWage.error = "Enter valid amount"; return }

        binding.btnSaveWorker.isEnabled = false
        val worker = Worker(siteId = siteId, name = name, dailyWage = wage)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                repo.addWorker(worker)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AddWorkerActivity, "✅ Worker '$name' added!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.btnSaveWorker.isEnabled = true
                    Toast.makeText(this@AddWorkerActivity, "❌ Failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean { finish(); return true }
}
