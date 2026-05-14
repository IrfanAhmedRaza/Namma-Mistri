package com.nammamistri

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.nammamistri.databinding.ActivityMainBinding
import com.nammamistri.ui.MainPagerAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val addSiteLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { _ -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        // Load user name from Firestore
        val uid = auth.currentUser?.uid
        if (uid != null) {
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val doc = db.collection("users").document(uid).get().await()
                    val name = doc.getString("fullName") ?: "Mistri"
                    supportActionBar?.title = "👷 $name"
                } catch (e: Exception) {
                    supportActionBar?.title = "👷 Namma Mistri"
                }
            }
        } else {
            supportActionBar?.title = "👷 Namma Mistri"
        }

        val pagerAdapter = MainPagerAdapter(this)
        binding.viewPager.adapter = pagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "🧮 Calculator"
                1 -> "🏗️ Sites"
                2 -> "👷 Workers"
                else -> ""
            }
        }.attach()

        binding.fabAddSite.setOnClickListener {
            addSiteLauncher.launch(Intent(this, AddSiteActivity::class.java))
        }

        binding.fabAddSite.hide()

        binding.viewPager.registerOnPageChangeCallback(object :
            androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (position == 1) binding.fabAddSite.show() else binding.fabAddSite.hide()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_logout) {
            AlertDialog.Builder(this)
                .setTitle("🚪 Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes, Logout") { _, _ ->
                    auth.signOut()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finishAffinity()
                }
                .setNegativeButton("Cancel", null)
                .show()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
