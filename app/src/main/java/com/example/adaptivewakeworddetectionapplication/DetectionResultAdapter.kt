package com.example.adaptivewakeworddetectionapplication

import android.icu.text.SimpleDateFormat
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.sql.Date
import java.util.Locale


class DetectionResultAdapter(private val detectionResults: MutableList<DetectionResult>) :
    RecyclerView.Adapter<DetectionResultAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val classLabelTextView: TextView = view.findViewById(R.id.tvClassLabel)
        val likelihoodTextView: TextView = view.findViewById(R.id.tvLikelihood)
        val timestampTextView: TextView = view.findViewById(R.id.tvTimestamp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_detection_result, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val result = detectionResults[position]

        val bestClassLabel = result.topClassLabels.firstOrNull() ?: "Unknown"
        val bestLikelihood = (result.likelihoods.firstOrNull()) ?: 0f

        val formattedLikelihood = String.format("%.2f%%", bestLikelihood * 100)

        val detectedText = holder.itemView.context.getString(R.string.detected_text, bestClassLabel)

        val spannableDetectedText = SpannableString(detectedText)
        spannableDetectedText.setSpan(
            ForegroundColorSpan(holder.itemView.context.getColor(R.color.pewter)),
            0, 9, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableDetectedText.setSpan(
            RelativeSizeSpan(0.8f),
            0, 9, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableDetectedText.setSpan(
            StyleSpan(android.graphics.Typeface.BOLD),
            10, detectedText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableDetectedText.setSpan(
            ForegroundColorSpan(holder.itemView.context.getColor(R.color.white)),
            10, detectedText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        val likelihoodText = holder.itemView.context.getString(R.string.likelihood_text, formattedLikelihood)

        val spannableLikelihoodText = SpannableString(likelihoodText)
        spannableLikelihoodText.setSpan(
            ForegroundColorSpan(holder.itemView.context.getColor(R.color.pewter)),
            0, 11, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableLikelihoodText.setSpan(
            RelativeSizeSpan(0.8f),
            0, 11, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableLikelihoodText.setSpan(
            ForegroundColorSpan(holder.itemView.context.getColor(R.color.white)),
            12, likelihoodText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        holder.classLabelTextView.text = spannableDetectedText
        holder.likelihoodTextView.text = spannableLikelihoodText

        val dateFormat = SimpleDateFormat("yyyy-MM-dd, HH:mm", Locale.getDefault())
        val date = Date(result.timestamp)
        holder.timestampTextView.text = dateFormat.format(date)
    }

    override fun getItemCount(): Int {
        return detectionResults.size
    }

    fun setDetectionResults(newResults: List<DetectionResult>) {
        detectionResults.clear()
        detectionResults.addAll(newResults)
        notifyDataSetChanged()
    }

    fun addDetectionResult(result: DetectionResult) {
        detectionResults.add(0, result)
        notifyItemInserted(0)
    }
}
