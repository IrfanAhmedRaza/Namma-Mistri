package com.nammamistri.ui.workers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.nammamistri.databinding.FragmentWorkersBinding

class WorkersFragment : Fragment() {
    private var _binding: FragmentWorkersBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentWorkersBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvInfo.text = "👷 Open a Site to manage its workers and daily wage log."
    }
    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
