package com.nammamistri.ui.workers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nammamistri.data.FirebaseRepository
import com.nammamistri.data.Worker
import com.nammamistri.databinding.ItemWorkerBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WorkerAdapter(
    private val repo: FirebaseRepository,
    private val onClick: (Worker) -> Unit
) : ListAdapter<Worker, WorkerAdapter.WorkerViewHolder>(DiffCallback) {

    inner class WorkerViewHolder(private val binding: ItemWorkerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(worker: Worker) {
            binding.tvWorkerName.text = "👷 ${worker.name}"
            binding.tvDailyWage.text = "Daily Wage: ₹%.0f".format(worker.dailyWage)
            binding.tvBalance.text = "Loading..."
            binding.tvDaysWorked.text = ""

            CoroutineScope(Dispatchers.IO).launch {
                val totalDays = repo.getTotalDaysWorked(worker.id)
                val totalAdvance = repo.getTotalAdvancePaid(worker.id)
                val earned = totalDays * worker.dailyWage
                val balance = earned - totalAdvance

                withContext(Dispatchers.Main) {
                    binding.tvBalance.text = "Balance Due: ₹%.0f".format(balance)
                    binding.tvBalance.setTextColor(
                        if (balance >= 0)
                            binding.root.context.getColor(android.R.color.holo_green_dark)
                        else
                            binding.root.context.getColor(android.R.color.holo_red_dark)
                    )
                    binding.tvDaysWorked.text = "Days: $totalDays | Advance: ₹%.0f".format(totalAdvance)
                }
            }

            binding.root.setOnClickListener { onClick(worker) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkerViewHolder {
        val binding = ItemWorkerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WorkerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WorkerViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Worker>() {
        override fun areItemsTheSame(a: Worker, b: Worker) = a.id == b.id
        override fun areContentsTheSame(a: Worker, b: Worker) = a == b
    }
}
