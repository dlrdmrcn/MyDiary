package com.dilara.mydiary.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.navigation.Navigation
import com.dilara.mydiary.MONTH
import com.dilara.mydiary.base.BaseFragment
import com.dilara.mydiary.databinding.FragmentCalendarBinding
import java.util.Calendar

class CalendarFragment : BaseFragment() {
    private lateinit var binding: FragmentCalendarBinding
    private lateinit var datePicker: DatePicker
    private lateinit var today: Calendar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        datePicker = binding.datePicker
        today = Calendar.getInstance()

        val day: Int = today.get(Calendar.DAY_OF_MONTH)
        val month = MONTH.values().firstOrNull { it.value == today.get(Calendar.MONTH) }
        val year: Int = today.get(Calendar.YEAR)

        binding.todayDateText.setText("$day $month $year")
        binding.showDiary.setOnClickListener {
            val selectedDay = datePicker.dayOfMonth
            val selectedMonth = MONTH.values().firstOrNull { it.value == datePicker.month }
            val selectedYear = datePicker.year
            val selectedDate = "$selectedDay $selectedMonth $selectedYear"
            val action =
                CalendarFragmentDirections.actionCalendarFragmentToShowDiaryFragment(selectedDate)
            Navigation.findNavController(it).navigate(action)
        }

        binding.addDiary.setOnClickListener {
            val selectedDay = datePicker.dayOfMonth
            val selectedMonth = MONTH.values().firstOrNull { it.value == datePicker.month }
            val selectedYear = datePicker.year
            val selectedDate = "$selectedDay $selectedMonth $selectedYear"
            val action = CalendarFragmentDirections.actionCalendarFragmentToAddDiaryFragment(
                selectedDate,
                null,
                null,
                null,
                -1,
                null,
                true
            )
            Navigation.findNavController(it).navigate(action)
        }
    }
}