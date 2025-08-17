package com.example.hackathon.ui.signUp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.example.hackathon.R
import com.example.hackathon.domain.model.AppCharacter

class CharacterAdapter(
    private val items: List<AppCharacter>,
    private val onClick: (position: Int) -> Unit
) : RecyclerView.Adapter<CharacterAdapter.VH>() {

    // 반드시 초기값을 둔다 (lateinit 불가)
    var selected: Int = 0
        set(value) {
            val old = field
            field = value
            if (old in items.indices) notifyItemChanged(old, PAYLOAD_SELECTION)
            if (field in items.indices) notifyItemChanged(field, PAYLOAD_SELECTION)
        }

    companion object { private const val PAYLOAD_SELECTION = "sel" }

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val btn: ImageButton = view.findViewById(R.id.btn_head)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_character, parent, false)
        return VH(v)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        bind(holder, position)
    }

    override fun onBindViewHolder(holder: VH, position: Int, payloads: MutableList<Any>) {
        if (payloads.contains(PAYLOAD_SELECTION)) {
            holder.btn.isSelected = (position == selected)
        } else {
            bind(holder, position)
        }
    }

    private fun bind(holder: VH, position: Int) {
        val item = items[position]
        holder.btn.setImageResource(item.headRes)
        holder.btn.isSelected = (position == selected)
        holder.btn.setOnClickListener { onClick(position) }
    }
}
