package com.example.hackathon.SignUp

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.hackathon.BaseFragment
import com.example.hackathon.adapter.CharacterAdapter
import com.example.hackathon.databinding.FragmentSignup1Binding
import com.example.hackathon.model.AppCharacter

class Signup1Fragment
    : BaseFragment<FragmentSignup1Binding>(FragmentSignup1Binding::inflate) {

    private val data = AppCharacter.values().toList()

    // 어댑터는 lazy로 안전하게 초기화
    private val adapter: CharacterAdapter by lazy {
        CharacterAdapter(data) { pos -> selectedIndex = pos }
    }

    // 선택 인덱스 변경 시 중앙 full 이미지와 리스트 동기화
    private var selectedIndex = 0
        set(value) {
            field = ((value % data.size) + data.size) % data.size // 순환
            adapter.selected = field
            binding.characterFull.setImageResource(data[field].fullRes)
            binding.rvCharacters.smoothScrollToPosition(field)
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // RecyclerView
        binding.rvCharacters.apply {
            layoutManager = LinearLayoutManager(
                requireContext(), RecyclerView.HORIZONTAL, false
            )
            adapter = this@Signup1Fragment.adapter
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            addItemDecoration(SpacingDecoration(horizontal = dp(32)))
            LinearSnapHelper().attachToRecyclerView(this) // 중앙 스냅(선택)
        }

        // 초기 선택(기본: PARROT)
        selectedIndex = 0

        // 좌/우 버튼
        binding.chBtnLeft.setOnClickListener { selectedIndex -= 1 }
        binding.chBtnRight.setOnClickListener { selectedIndex += 1 }
    }

    /** 간단 스페이싱 데코레이터 */
    class SpacingDecoration(private val horizontal: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
        ) {
            val pos = parent.getChildAdapterPosition(view)
            val last = (parent.adapter?.itemCount ?: 0) - 1
            outRect.left = if (pos == 0) horizontal else horizontal / 2
            outRect.right = if (pos == last) horizontal else horizontal / 2
        }
    }

    private fun dp(v: Int): Int =
        (resources.displayMetrics.density * v).toInt()
}
