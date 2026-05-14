package com.nammamistri

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.nammamistri.data.FirebaseRepository
import com.nammamistri.data.Site
import com.nammamistri.databinding.ActivityAddSiteBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class AddSiteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddSiteBinding
    private val repo = FirebaseRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddSiteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Add New Site"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.btnSaveSite.setOnClickListener { saveSite() }
    }

    private fun saveSite() {
        val name = binding.etSiteName.text.toString().trim()
        val location = binding.etLocation.text.toString().trim()
        val client = binding.etClientName.text.toString().trim()

        if (name.isEmpty()) { binding.etSiteName.error = "Site name required"; return }
        if (location.isEmpty()) { binding.etLocation.error = "Location required"; return }
        if (client.isEmpty()) { binding.etClientName.error = "Client name required"; return }

        binding.btnSaveSite.isEnabled = false

        val today = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
        val site = Site(name = name, location = location, clientName = client, startDate = today)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                repo.addSite(site)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AddSiteActivity, "✅ Site '$name' added!", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK)
                    finish()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.btnSaveSite.isEnabled = true
                    Toast.makeText(this@AddSiteActivity, "❌ Failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean { finish(); return true }
}
