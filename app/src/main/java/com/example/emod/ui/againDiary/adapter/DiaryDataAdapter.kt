package com.example.emod.ui.againDiary.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.emod.R
import com.example.emod.ui.againDiary.data.DiaryDay

class DiaryDayAdapter(
    private val items: List<DiaryDay>,
    private val onDiaryClick: (DiaryDay) -> Unit
) : RecyclerView.Adapter<DiaryDayAdapter.DayViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_day_diary_item, parent, false)
        return DayViewHolder(view)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        holder.bind(items[position], onDiaryClick)
    }

    override fun getItemCount(): Int = items.size

    class DayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dateText: TextView = itemView.findViewById(R.id.tv_date)
        private val dayOfWeekText: TextView = itemView.findViewById(R.id.tv_date_of_week)
        private val emojiImage: ImageView = itemView.findViewById(R.id.iv_emotion)

        fun bind(item: DiaryDay, onDiaryClick: (DiaryDay) -> Unit) {
            dateText.text = item.date.drop(8)
            dayOfWeekText.text = item.dayOfWeek

            if (item.summary != null) {
                // 감정별 이미지 설정 (예시: summary 내부에 감정을 명시했다고 가정)
                val emotionRes = when {
                    item.summary.contains("기쁨") -> R.drawable.ic_lion_happy
                    item.summary.contains("슬픔") -> R.drawable.ic_lion_sad
                    item.summary.contains("화남") -> R.drawable.ic_lion_angry
                    else -> R.drawable.ic_lion_happy
                }

                emojiImage.setImageResource(emotionRes)
                itemView.isEnabled = true
                itemView.alpha = 1.0f
                itemView.setOnClickListener { onDiaryClick(item) }
            } else {
                emojiImage.setImageResource(R.drawable.ic_lion_yet)
                itemView.isEnabled = false
                itemView.setOnClickListener(null)
            }
        }

    }

    fun updateItems(newItems: List<DiaryDay>) {
        (items as MutableList).clear()
        (items as MutableList).addAll(newItems)
        notifyDataSetChanged()
    }
}
