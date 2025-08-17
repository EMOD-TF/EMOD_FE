package com.example.hackathon.SignUp

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
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

    private val vm: SignupViewModel by activityViewModels()
    private val data = AppCharacter.values().toList()

    // 어댑터는 lazy로 안전하게 초기화
    private val adapter: CharacterAdapter by lazy {
        CharacterAdapter(data) { pos -> selectedIndex = pos }
    }

    private var isInitialized = false

    private fun applySelectedIndex(newIndex: Int, animate: Boolean) {
        selectedIndex = ((newIndex % data.size) + data.size) % data.size
        adapter.selected = selectedIndex
        binding.characterFull.setImageResource(data[selectedIndex].fullRes)
        vm.character = data[selectedIndex]
        if (animate && isInitialized) {
            binding.rvCharacters.smoothScrollToPosition(selectedIndex)
        }
    }

    // 선택 인덱스 변경 시 중앙 full 이미지와 리스트 동기화
    private var selectedIndex = 0
        set(value) {
            field = ((value % data.size) + data.size) % data.size // 순환
            adapter.selected = field
            binding.characterFull.setImageResource(data[field].fullRes)
            vm.character = data[field]
            binding.rvCharacters.smoothScrollToPosition(field)
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val lm = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        val snap = LinearSnapHelper()

        binding.rvCharacters.apply {
            layoutManager = lm
            adapter = this@Signup1Fragment.adapter

            // 초기에는 animator 끔 (미세 튐 방지). 이후 isInitialized=true에서 다시 켜도 됨.
            itemAnimator = null

            clipToPadding = false
            overScrollMode = RecyclerView.OVER_SCROLL_NEVER

            // 아이템 간 간격
            val gap = dp(32)
            addItemDecoration(SpacingDecoration(space = gap, includeEdge = false))

            snap.attachToRecyclerView(this)
        }

        // ✅ 초기 선택 복원 (스크롤 애니메이션 금지)
        val initialIndex = vm.character?.let { data.indexOf(it) }?.takeIf { it >= 0 } ?: 0
        applySelectedIndex(initialIndex, animate = false)

        // ✅ RV, 아이템 실제 폭을 기반으로 "정확한" 사이드 패딩 계산
        binding.rvCharacters.post {
            // 1) 임시로 첫 아이템 inflate 해서 실제 폭 얻기 (어댑터가 정적 사이즈라면 하드코딩해도 OK)
            val vh = adapter.createViewHolder(binding.rvCharacters, adapter.getItemViewType(0))
            adapter.onBindViewHolder(vh, 0)
            vh.itemView.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            val itemWidth = vh.itemView.measuredWidth

            val rvWidth = binding.rvCharacters.width
            val side = ((rvWidth - itemWidth) / 2).coerceAtLeast(0)
            binding.rvCharacters.setPadding(side, 0, side, 0)

            // 2) 정확히 오프셋 0으로 위치 고정
            (binding.rvCharacters.layoutManager as? LinearLayoutManager)
                ?.scrollToPositionWithOffset(initialIndex, 0)

            // 3) 스냅이 계산한 잔여 오프셋 있으면 한 번 더 보정
            snap.findSnapView(lm)?.let { v ->
                snap.calculateDistanceToFinalSnap(lm, v)?.let { d ->
                    if (d[0] != 0 || d[1] != 0) binding.rvCharacters.scrollBy(d[0], d[1])
                }
            }

            // 이제부터는 사용자 조작 시 부드럽게 스크롤
            isInitialized = true
            // 필요하면 animator 복구
            binding.rvCharacters.itemAnimator = DefaultItemAnimator()
        }

        binding.chBtnLeft.setOnClickListener { applySelectedIndex(selectedIndex - 1, animate = true) }
        binding.chBtnRight.setOnClickListener { applySelectedIndex(selectedIndex + 1, animate = true) }
    }

    /** 간단 스페이싱 데코레이터 */
    class SpacingDecoration(
        private val space: Int,
        private val includeEdge: Boolean = false
    ) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            val pos = parent.getChildAdapterPosition(view)
            val last = (parent.adapter?.itemCount ?: 0) - 1
            val half = space / 2
            outRect.left = if (pos == 0 && includeEdge) space else half
            outRect.right = if (pos == last && includeEdge) space else half
        }
    }

    private fun dp(v: Int): Int =
        (resources.displayMetrics.density * v).toInt()
}
