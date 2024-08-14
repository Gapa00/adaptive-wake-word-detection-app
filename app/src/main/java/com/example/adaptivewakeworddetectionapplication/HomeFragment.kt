package com.example.adaptivewakeworddetectionapplication

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adaptivewakeworddetectionapplication.databinding.FragmentHomeBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class HomeFragment : Fragment() {

    private var isServiceRunning: Boolean = false

    private lateinit var binding: FragmentHomeBinding
    private var sharedPreferences: SharedPreferences? = null

    private lateinit var detectionResultAdapter: DetectionResultAdapter
    private var detectionResults = mutableListOf<DetectionResult>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = activity?.let { PreferenceManager.getDefaultSharedPreferences(it) }
        detectionResultAdapter = DetectionResultAdapter(detectionResults)
        registerReceiver()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = detectionResultAdapter

        loadHistoricalData()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnRecord.setOnClickListener {
            if (isServiceRunning) {
                binding.btnRecord.setImageResource(R.drawable.baseline_mic_24)
                stopRecordingService()
            } else {
                binding.btnRecord.setImageResource(R.drawable.baseline_mic_off_24)
                startRecordingService()
            }
        }
    }

    private fun startRecordingService() {
        val serviceIntent = Intent(activity, WordDetectionService::class.java).apply {
            putExtra("ACTION", "START")
        }
        activity?.startService(serviceIntent)
        isServiceRunning = true
    }

    private fun stopRecordingService() {
        val serviceIntent = Intent(activity, WordDetectionService::class.java).apply {
            putExtra("ACTION", "STOP")
        }
        activity?.startService(serviceIntent)
        isServiceRunning = false
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun registerReceiver() {
        val filter = IntentFilter(WordDetectionService.BROADCAST_ACTION)
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(wakeWordReceiver, filter)
    }

    private fun unregisterReceiver() {
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(wakeWordReceiver)
    }

    private val wakeWordReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val detectionResult = intent?.getParcelableExtra<DetectionResult>(WordDetectionService.EXTRA_WAKE_WORD)
            detectionResult?.let {
                activity?.runOnUiThread {
                    detectionResultAdapter.addDetectionResult(it)
                    binding.recyclerView.scrollToPosition(0)
                    saveDetectionResults()
                }
            }
        }
    }

    private fun loadHistoricalData() {
        val json = sharedPreferences?.getString(R.string.detection_results.toString(), "")
        if (!json.isNullOrEmpty()) {
            val type = object : TypeToken<List<DetectionResult>>() {}.type
            val historicalData: List<DetectionResult> = Gson().fromJson(json, type)
            detectionResultAdapter.setDetectionResults(historicalData)
        }
    }

    private fun saveDetectionResults() {
        val editor = sharedPreferences?.edit()
        val json = Gson().toJson(detectionResults)
        editor?.putString(R.string.detection_results.toString(), json)
        editor?.apply()
    }

    companion object {
        private val TAG = HomeFragment::class.simpleName
    }
}