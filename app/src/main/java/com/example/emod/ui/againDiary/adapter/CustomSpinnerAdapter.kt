package com.example.emod.ui.againDiary.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.emod.R

class CustomSpinnerAdapter(
    context: Context,
    private val items: List<String>
) : ArrayAdapter<String>(context, R.layout.spinner_dropdown_item, items) {

    // 현재 선택된 아이템 표시뷰(드롭다운 닫힘)
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.spinner_dropdown_item, parent, false)
        val text = view.findViewById<TextView>(R.id.spinner_item_text)
        text.text = items[position]
        return view
    }

    // 드롭다운 펼친 목록 옵션
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(context)
        val view = convertView ?: inflater.inflate(R.layout.spinner_dropdown_item, parent, false)
        val text = view.findViewById<TextView>(R.id.spinner_item_text)

        text.text = items[position]

        // 맨 위 또는 맨 아래면 구분선 보임, 그 외는 숨김 처리

        return view
    }
}
