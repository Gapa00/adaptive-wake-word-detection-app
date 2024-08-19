package com.example.adaptivewakeworddetectionapplication

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.google.android.material.slider.Slider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels

class SettingsFragment : Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        sharedPreferences = activity?.let { PreferenceManager.getDefaultSharedPreferences(it) }!!

        val slider: Slider = view.findViewById(R.id.settingsSlider)
        val editText: EditText = view.findViewById(R.id.settingsEditText)
        val updateButton: Button = view.findViewById(R.id.updateButton)

        val savedSliderValue = sharedPreferences.getFloat(R.string.settings_slider.toString(), 0.01f).toDouble()
        val savedEditTextValue = sharedPreferences.getFloat(R.string.settings_input.toString(), 80.0f)

        slider.value = savedSliderValue.toFloat()
        editText.setText(savedEditTextValue.toString())

        slider.setLabelFormatter { value: Float ->
            String.format("%.3f", value)
        }

        updateButton.setOnClickListener {
            val sliderValue = slider.value.toDouble()
            val editTextValue = editText.text.toString().toFloat()

            // Save values to SharedPreferences
            with(sharedPreferences.edit()) {
                putFloat(R.string.settings_slider.toString(), sliderValue.toFloat())
                putFloat(R.string.settings_input.toString(), editTextValue)
                apply()
            }

            (requireActivity() as MainActivity).returnToHomeFragment()
        }

        return view
    }
}
