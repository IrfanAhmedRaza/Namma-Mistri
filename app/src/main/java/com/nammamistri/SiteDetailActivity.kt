package com.nammamistri

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.nammamistri.data.FirebaseRepository
import com.nammamistri.databinding.ActivitySiteDetailBinding
import com.nammamistri.ui.workers.WorkerAdapter
import com.nammamistri.ui.workers.WorkerDetailBottomSheet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SiteDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySiteDetailBinding
    private var siteId: String = ""
    private val repo = FirebaseRepository()
    private lateinit var adapter: WorkerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySiteDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        siteId = intent.getStringExtra("SITE_ID") ?: ""
        val siteName = intent.getStringExtra("SITE_NAME") ?: "Site"

        supportActionBar?.title = siteName
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        adapter = WorkerAdapter(repo) { worker ->
            val sheet = WorkerDetailBottomSheet(worker, repo) { loadWorkers() }
            sheet.show(supportFragmentManager, "WorkerDetail")
        }

        binding.rvWorkers.layoutManager = LinearLayoutManager(this)
        binding.rvWorkers.adapter = adapter

        loadWorkers()

        binding.btnAddWorker.setOnClickListener {
            val intent = Intent(this, AddWorkerActivity::class.java)
            intent.putExtra("SITE_ID", siteId)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        loadWorkers()
    }

    private fun loadWorkers() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val workers = repo.getWorkers(siteId)
                withContext(Dispatchers.Main) {
                    adapter.submitList(workers)
                    binding.tvNoWorkers.visibility = if (workers.isEmpty()) View.VISIBLE else View.GONE
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@SiteDetailActivity, "❌ ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean { finish(); return true }
}
