package com.nammamistri.ui.sites

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nammamistri.data.Site
import com.nammamistri.databinding.ItemSiteBinding

class SiteAdapter(
    private val onClick: (Site) -> Unit,
    private val onDelete: (Site) -> Unit
) : ListAdapter<Site, SiteAdapter.SiteViewHolder>(DiffCallback) {

    inner class SiteViewHolder(private val binding: ItemSiteBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(site: Site) {
            binding.tvSiteName.text = site.name
            binding.tvSiteLocation.text = "📍 ${site.location}"
            binding.tvClientName.text = "👤 ${site.clientName}"
            binding.tvStartDate.text = "📅 Started: ${site.startDate}"
            binding.tvStatus.text = if (site.isActive) "🟢 Active" else "🔴 Completed"
            binding.root.setOnClickListener { onClick(site) }
            binding.btnDeleteSite.setOnClickListener {
                AlertDialog.Builder(binding.root.context)
                    .setTitle("Delete Site")
                    .setMessage("Are you sure you want to delete '${site.name}'? All workers and logs will also be deleted.")
                    .setPositiveButton("Yes, Delete") { _, _ -> onDelete(site) }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SiteViewHolder {
        val binding = ItemSiteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SiteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SiteViewHolder, position: Int) = holder.bind(getItem(position))

    companion object DiffCallback : DiffUtil.ItemCallback<Site>() {
        override fun areItemsTheSame(a: Site, b: Site) = a.id == b.id
        override fun areContentsTheSame(a: Site, b: Site) = a == b
    }
}
