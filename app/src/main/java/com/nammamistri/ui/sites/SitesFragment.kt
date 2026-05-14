package com.nammamistri.ui.sites

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.nammamistri.SiteDetailActivity
import com.nammamistri.data.FirebaseRepository
import com.nammamistri.databinding.FragmentSitesBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SitesFragment : Fragment() {

    private var _binding: FragmentSitesBinding? = null
    private val binding get() = _binding!!
    private val repo = FirebaseRepository()
    private lateinit var adapter: SiteAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSitesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = SiteAdapter(
            onClick = { site ->
                val intent = Intent(requireContext(), SiteDetailActivity::class.java)
                intent.putExtra("SITE_ID", site.id)
                intent.putExtra("SITE_NAME", site.name)
                startActivity(intent)
            },
            onDelete = { site ->
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        repo.deleteSite(site.id)
                        withContext(Dispatchers.Main) {
                            Toast.makeText(requireContext(), "✅ Site deleted!", Toast.LENGTH_SHORT).show()
                            loadSites()
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(requireContext(), "❌ Failed to delete: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        )

        binding.rvSites.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSites.adapter = adapter
        loadSites()
    }

    override fun onResume() {
        super.onResume()
        loadSites()
    }

    private fun loadSites() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val sites = repo.getSites()
                withContext(Dispatchers.Main) {
                    adapter.submitList(sites)
                    binding.tvEmpty.visibility = if (sites.isEmpty()) View.VISIBLE else View.GONE
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.tvEmpty.visibility = View.VISIBLE
                    binding.tvEmpty.text = "⚠️ Could not load sites.\nCheck your internet connection."
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
