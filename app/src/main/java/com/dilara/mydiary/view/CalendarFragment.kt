package com.dilara.mydiary.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import com.dilara.mydiary.MONTH
import com.dilara.mydiary.databinding.FragmentCalendarBinding
import java.util.Calendar

class CalendarFragment : Fragment() {
    private lateinit var binding : FragmentCalendarBinding
    private lateinit var datePicker : DatePicker
    private lateinit var today : Calendar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCalendarBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        datePicker = binding.datePicker
        today = Calendar.getInstance()

        val day : Int = today.get(Calendar.DAY_OF_MONTH)
        val month = MONTH.values().firstOrNull { it.value == today.get(Calendar.MONTH) }
        val year : Int = today.get(Calendar.YEAR)

        binding.todayDateText.setText("$day $month $year")
    }

}