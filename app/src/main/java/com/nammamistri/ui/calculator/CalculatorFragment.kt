package com.nammamistri.ui.calculator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.nammamistri.databinding.FragmentCalculatorBinding
import com.nammamistri.util.MaterialCalculator

class CalculatorFragment : Fragment() {
    private var _binding: FragmentCalculatorBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCalculatorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val thicknesses = arrayOf("4.5 inch (0.115m)", "9 inch (0.23m)", "13.5 inch (0.345m)")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, thicknesses)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerThickness.adapter = adapter
        binding.radioGroupCalcType.setOnCheckedChangeListener { _, _ -> clearResult() }
        binding.btnCalculate.setOnClickListener { calculate() }
        binding.btnClear.setOnClickListener { clearAll() }
    }

    private fun calculate() {
        val lengthStr = binding.etLength.text.toString()
        val heightStr = binding.etHeight.text.toString()
        val widthStr = binding.etWidth.text.toString()
        if (lengthStr.isEmpty() || heightStr.isEmpty()) { binding.etLength.error = "Required"; return }
        val length = lengthStr.toDoubleOrNull() ?: return
        val height = heightStr.toDoubleOrNull() ?: return
        when (binding.radioGroupCalcType.checkedRadioButtonId) {
            com.nammamistri.R.id.rbWall -> calculateWall(length, height)
            com.nammamistri.R.id.rbSlab -> {
                val width = widthStr.toDoubleOrNull()
                if (width == null) { binding.etWidth.error = "Required for slab"; return }
                calculateSlab(length, width, height)
            }
            com.nammamistri.R.id.rbPlaster -> calculatePlaster(length, height)
        }
    }

    private fun calculateWall(length: Double, height: Double) {
        val thicknessValues = arrayOf(0.115, 0.23, 0.345)
        val thickness = thicknessValues[binding.spinnerThickness.selectedItemPosition]
        val result = MaterialCalculator.calculateWall(length, height, thickness)
        binding.tvResult.text = "🧱 WALL MATERIAL ESTIMATE\n━━━━━━━━━━━━━━━━━━━━━━━━\n📐 Wall Volume   : %.2f m³\n\n🧱 Bricks        : ${result.bricks} nos\n🪣 Cement        : ${result.cementBags} bags (50kg)\n⛏️ Sand          : %.1f CFT\n\n💡 Tip: Add 10%% extra for wastage".format(result.volume, result.sandCFT)
        binding.cardResult.visibility = View.VISIBLE
    }

    private fun calculateSlab(length: Double, width: Double, thickness: Double) {
        val result = MaterialCalculator.calculateSlab(length, width, thickness)
        binding.tvResult.text = "🏗️ SLAB MATERIAL ESTIMATE (M20)\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n📐 Slab Volume   : %.2f m³\n\n🪣 Cement        : ${result.cementBags} bags (50kg)\n⛏️ Sand          : %.1f CFT\n🪨 Aggregate     : %.1f CFT\n🔩 Steel         : ~${result.steelKg} kg".format(result.volume, result.sandCFT, result.aggregateCFT)
        binding.cardResult.visibility = View.VISIBLE
    }

    private fun calculatePlaster(length: Double, height: Double) {
        val result = MaterialCalculator.calculatePlaster(length, height, 12.0)
        binding.tvResult.text = "🎨 PLASTER MATERIAL ESTIMATE\n━━━━━━━━━━━━━━━━━━━━━━━━━━━\n📐 Surface Area  : %.2f m²\n\n🪣 Cement        : ${result.cementBags} bags (50kg)\n⛏️ Sand          : %.1f CFT\n\n💡 Thickness: 12mm, Ratio 1:4".format(result.area, result.sandCFT)
        binding.cardResult.visibility = View.VISIBLE
    }

    private fun clearResult() { binding.cardResult.visibility = View.GONE; binding.tvResult.text = "" }
    private fun clearAll() { binding.etLength.text?.clear(); binding.etHeight.text?.clear(); binding.etWidth.text?.clear(); clearResult() }
    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
