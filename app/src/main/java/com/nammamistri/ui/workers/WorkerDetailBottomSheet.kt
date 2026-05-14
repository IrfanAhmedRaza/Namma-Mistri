package com.nammamistri.ui.workers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nammamistri.data.FirebaseRepository
import com.nammamistri.data.Worker
import com.nammamistri.data.WorkLog
import com.nammamistri.databinding.BottomSheetWorkerBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class WorkerDetailBottomSheet(
    private val worker: Worker,
    private val repo: FirebaseRepository,
    private val onLogAdded: () -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetWorkerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = BottomSheetWorkerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvWorkerTitle.text = "👷 ${worker.name}"
        binding.tvWageInfo.text = "Daily Wage: ₹%.0f".format(worker.dailyWage)

        loadSummary()

        val daysOptions = arrayOf("Full Day (1.0)", "Half Day (0.5)", "2 Days", "3 Days")
        val daysValues = arrayOf(1.0, 0.5, 2.0, 3.0)

        val adapter = android.widget.ArrayAdapter(requireContext(),
            android.R.layout.simple_spinner_item, daysOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerDays.adapter = adapter

        binding.btnLogWork.setOnClickListener {
            val daysWorked = daysValues[binding.spinnerDays.selectedItemPosition]
            val advanceStr = binding.etAdvance.text.toString()
            val advance = if (advanceStr.isEmpty()) 0.0 else advanceStr.toDoubleOrNull() ?: 0.0
            val note = binding.etNote.text.toString()
            val today = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())

            val log = WorkLog(
                workerId = worker.id,
                date = today,
                daysWorked = daysWorked,
                advancePaid = advance,
                note = note
            )

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    repo.addLog(log)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "✅ Work logged for ${worker.name}", Toast.LENGTH_SHORT).show()
                        binding.etAdvance.text?.clear()
                        binding.etNote.text?.clear()
                        loadSummary()
                        onLogAdded()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "❌ Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun loadSummary() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val totalDays = repo.getTotalDaysWorked(worker.id)
                val totalAdvance = repo.getTotalAdvancePaid(worker.id)
                val earned = totalDays * worker.dailyWage
                val balance = earned - totalAdvance

                withContext(Dispatchers.Main) {
                    binding.tvSummary.text = """
💼 WAGE SUMMARY
━━━━━━━━━━━━━━━━━━━━
📅 Days Worked   : $totalDays days
💰 Total Earned  : ₹%.0f
💸 Advance Paid  : ₹%.0f
━━━━━━━━━━━━━━━━━━━━
🏦 Balance Due   : ₹%.0f
                    """.trimIndent().format(earned, totalAdvance, balance)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.tvSummary.text = "Could not load summary."
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
